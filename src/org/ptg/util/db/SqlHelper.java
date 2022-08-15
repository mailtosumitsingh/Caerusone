package org.ptg.util.db;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import Zql.ParseException;
import Zql.ZFromItem;
import Zql.ZInsert;
import Zql.ZQuery;
import Zql.ZSelectItem;
import Zql.ZStatement;
import Zql.ZUpdate;
import Zql.ZqlParser;

public class SqlHelper {
	public List<Object> parseSP(String str) throws ParseException {
		List<Object> c = new ArrayList<Object>();
		
		/*		for (String t : tables) {
					TableDef d = new TableDef();
					d.setName(t);
					c.add(d);
				}
				int i = 0;
				for (String t : cols) {
					ColDef d = new ColDef();
					i ++;
					d.setName(t);
					d.setColOp(colOp);
					d.setOrder(i);
					c.add(d);
					
				}*/
				return c;
	}
	public List<Object> parseDynaSql(String str) throws ParseException {
		List<Object> c = new ArrayList<Object>();
		Statement st = null;
		Connection conn = null;
		try {
			conn = DBHelper.getInstance().createConnection();
			st = conn.createStatement();
			st.executeQuery(str);
			ResultSetMetaData rmeta = st.getResultSet().getMetaData();
			int cols = rmeta.getColumnCount();
			
			for(int i=0;i<cols;i++){
				ColDef d = new ColDef();
				d.setName(rmeta.getColumnLabel(i+1));
				d.setColOp("unk");
				d.setOrder(i+1);
				String columnTypeName = DBTransformerHelper.types.get(rmeta.getColumnType(i+1));
			
				String dataType = DBTransformerHelper.dataTypeMap.get(columnTypeName);
				d.setDataType(dataType);
				c.add(d);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelper.getInstance().closeStmt(st);
			DBHelper.getInstance().closeConnection(conn);
		}
/*		for (String t : tables) {
			TableDef d = new TableDef();
			d.setName(t);
			c.add(d);
		}
		int i = 0;
		for (String t : cols) {
			ColDef d = new ColDef();
			i ++;
			d.setName(t);
			d.setColOp(colOp);
			d.setOrder(i);
			c.add(d);
			
		}*/
		return c;
	}
	public List<Object> parseSql(String str) throws ParseException {
		ZqlParser p = null;
		str = preprocess(str);
		// System.out.println(str);
		p = new ZqlParser(new ByteArrayInputStream(str.getBytes()));
		p = prepareParser(p);
		ZStatement st;
		st = p.readStatement();
		Object[] retstr = null;
		// System.out.println(st.toString());
		String colOp =null;
		SQLObj def = new SQLObj();
		def.setSql(str);
		if (st instanceof ZQuery) {
			retstr = buildQuery(str, (ZQuery) st);
			colOp = "query";
		} else if (st instanceof ZInsert) {
			retstr = buildInsert(str, (ZInsert) st);
			colOp = "insert";
			// i do nothing
		} else if (st instanceof ZUpdate) {
			// i do nothing
			retstr = buildUpdate(str, (ZUpdate) st);
			colOp = "update";
		}
		def.setSqlType(colOp);
		Object[] ret = retstr;
		List<String> tables = (List<String>) ret[0];
		List<String> cols = (List<String>) ret[1];
		List<Object> c = new ArrayList<Object>();
		for (String t : tables) {
			TableDef d = new TableDef();
			d.setName(t);
			c.add(d);
		}
		int i = 0;
		for (String t : cols) {
			ColDef d = new ColDef();
			i ++;
			d.setName(t);
			d.setColOp(colOp);
			d.setOrder(i);
			c.add(d);
		}
		c.add(def);
		return c;
	}

	public String preprocess(String s) {
		return s;
	}

	public ZqlParser prepareParser(ZqlParser s) {
		return s;
	}

	public Object[] buildQuery(String qorig, ZQuery q) {
		List<String> c = new ArrayList<String>();
		List<String> tables = new ArrayList<String>();
		Vector<ZFromItem> froms = q.getFrom();
		for (ZFromItem f : froms) {
			String temp = f.getAlias() == null ? f.getTable() : f.getAlias();
			tables.add(temp);
		}
		Vector<ZSelectItem> sels = q.getSelect();
		for (ZSelectItem i : sels) {
			String res = (i.getAlias() == null ? i.getColumn() : i.getAlias());
			c.add((i.getTable() == null ? "" : i.getTable() + ".") + res);
		}
		return new Object[] { tables, c };
	}
	public Object[] buildInsert(String qorig, ZInsert q) {
		List<String> c = new ArrayList<String>();
		List<String> tables = new ArrayList<String>();
		tables.add(q.getTable());
		Vector<String> sels = q.getColumns();
		for (String i : sels) {
			c.add(i);
		}
		return new Object[] { tables, c };
	}
	public Object[] buildUpdate(String qorig, ZUpdate q) {
		List<String> c = new ArrayList<String>();
		List<String> tables = new ArrayList<String>();
		tables.add(q.getTable());
		Hashtable sels = q.getSet();
		for (Object i : sels.keySet()) {
			c.add((String)i);
		}
		return new Object[] { tables, c };
	}

}

