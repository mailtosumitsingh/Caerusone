/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.db;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ptg.cluster.AppContext;
import org.ptg.events.Event;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.util.Constants;
import org.ptg.util.IEventDBTransformer;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.SpringHelper;
import org.ptg.util.closures.IStateAwareClosure;

import au.com.bytecode.opencsv.CSVWriter;

public class DBHelper {
	AppContext ctx = null;
	private DataSource ds;
	private String tag;
	private String lastRowIdFunc;
	private ThreadLocal<Connection> threadTxLocal;

	public DBHelper() {
		this((DataSource) null);
	}

	public DBHelper(String tag) {
		this((DataSource) null);
		this.tag = tag;
	}

	public DBHelper(DataSource ds) {
		this.ds = ds;
	}

	public DBHelper(DataSource ds, String tag) {
		this.ds = ds;
		this.tag = tag;
	}

	public void init() {
		if (ds == null) {
			ds = (DataSource) SpringHelper.get(Constants.DataSource);
		}
		threadTxLocal = new ThreadLocal<Connection>();
		ctx = (AppContext) SpringHelper.get("appContext");
		Map<String, String> values = (Map<String, String>) SpringHelper.get("sqlServerLastIdMap");
		ResultSet res = null;
		Connection conn = null;
		lastRowIdFunc = values.get(ctx.getSqlServer());
	}

	public Connection createConnection() {
		try {
			{
				Connection temp = ds.getConnection();

				temp.setAutoCommit(true);
				return temp;

			}
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		return null;
	}

	public Statement createStatement() {
		Connection conn = null;
		try {
			conn = createConnection();
			Statement ret = conn.createStatement();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} finally {
			closeConnection(conn);
		}
		return null;
	}

	public PreparedStatement createPreparedStatement(String sql) {
		Connection conn = null;
		try {
			conn = createConnection();
			PreparedStatement ret = conn.prepareStatement(sql);
			return ret;
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} finally {
			closeConnection(conn);
		}
		return null;
	}

	public String[] getTableNames(String catalog, String schema, String table) {
		List<String> l = null;
		Connection conn = null;
		try {
			conn = createConnection();
			DatabaseMetaData dm = conn.getMetaData();
			ResultSet s = dm.getTables(catalog, schema, table, new String[] { "TABLE" });
			l = new LinkedList<String>();
			while (s.next()) {
				String temp = s.getString("TABLE_NAME");
				l.add(temp);
			}

		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} finally {
			closeConnection(conn);
		}

		return l.toArray(new String[] {});
	}

	public String[] getColumnNames(String catalog, String schema, String table) {
		List<String> l = null;
		Connection conn = null;
		try {
			conn = createConnection();
			DatabaseMetaData dm = conn.getMetaData();
			ResultSet s = dm.getColumns(catalog, schema, table, "");
			l = new LinkedList<String>();
			while (s.next()) {
				String temp = s.getString("COLUMN_NAME");

				l.add(temp);
			}

		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} finally {
			closeConnection(conn);
		}

		return l.toArray(new String[] {});
	}

	public List getColumnDefinitions(String catalog, String schema, String table) {
		List<String[]> l = null;
		Connection conn = null;
		try {
			conn = createConnection();
			DatabaseMetaData dm = conn.getMetaData();
			ResultSet s = dm.getColumns(catalog, schema, table, "");
			l = new LinkedList<String[]>();
			while (s.next()) {
				String temp = s.getString("COLUMN_NAME");
				String datatype = s.getString("DATA_TYPE");
				l.add(new String[] { temp, datatype });
			}

		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} finally {
			closeConnection(conn);
		}

		return l;
	}

	public void executeUpdate(String sqlStr) {
		Statement st = null;
		Connection conn = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			st.executeUpdate(sqlStr);
			// it is in autocommit by default conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
	}

	public void executePreparedUpdate(String sqlStr, Object... param) {
		PreparedStatement st = null;
		Connection conn = null;
		try {
			conn = createConnection();
			st = conn.prepareStatement(sqlStr);
			st.setEscapeProcessing(false);
			int i = 1;
			for (Object o : param) {
				st.setObject(i, o);
				i++;
			}
			st.executeUpdate();
			// it is in autocommit by default conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
	}

	public int executePreparedInsert(String sqlStr, Object... param) {
		PreparedStatement st = null;
		Connection conn = null;
		try {
			conn = createConnection();
			st = conn.prepareStatement(sqlStr);
			st.setEscapeProcessing(false);
			int i = 1;
			for (Object o : param) {
				st.setObject(i, o);
				i++;
			}
			st.executeUpdate();
			int id = getLastId(conn);
			return id;
			// it is in autocommit by default conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
		return -1;
	}

	public int executeInsert(String sqlStr) {
		Statement st = null;
		Connection conn = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			st.executeUpdate(sqlStr);
			return getLastId(conn);
			// it is in autocommit by default conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
		return -1;
	}

	public void forEach(String sqlStr, IStateAwareClosure c) {
		c.init();
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);

			set = st.executeQuery(sqlStr);
			while (set.next()) {
				c.execute(set);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			c.finish();
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
	}

	public void forEach(Connection conn, String sqlStr, IStateAwareClosure c) {
		c.init();
		Statement st = null;

		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);

			set = st.executeQuery(sqlStr);
			while (set.next()) {
				c.execute(set);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			c.finish();
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
	}

	public void execute(String sqlStr) {
		Statement st = null;
		Connection conn = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			st.execute(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
	}

	public ResultSet executeQuery(String sqlStr) {
		Statement st = null;
		Connection conn = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			return st.executeQuery(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
		return null;
	}

	public boolean closeStmt(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;

	}

	public boolean closeResult(ResultSet stmt) {
		if (stmt != null) {
			try {
				stmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;

	}

	public boolean closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;

	}

	public boolean executeInTrans(String[] trans) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = createConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			stmt.setEscapeProcessing(false);
			for (String tran : trans) {
				stmt.execute(tran);
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn);
			closeStmt(stmt);
		}
		return true;
	}

	public boolean executeInTrans(Connection conn, String[] trans) throws SQLException {
		Statement stmt = null;
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		stmt.setEscapeProcessing(false);
		for (String tran : trans) {
			stmt.executeUpdate(tran);
		}
		conn.commit();
		conn.setAutoCommit(true);
		return true;
	}

	public void commit(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static class SingletonHolder {
		private static final DBHelper INSTANCE = new DBHelper((DataSource) null);
		static {
			INSTANCE.init();
		}
	}

	public void dropTable(String name) {
		Statement st = null;
		Connection conn = null;
		String sqlStr = "drop table " + name;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			st.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
	}

	public void truncateTable(String name) {
		Statement st = null;
		Connection conn = null;

		String sqlStr = null;
		if (ctx.getSqlServer().equals("sqllite")) {
			sqlStr = "delete from  \"" + name + "\"";
		} else {
			sqlStr = "truncate table  " + name + "";
		}

		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			st.executeUpdate(sqlStr);
		} catch (Exception e) {
			// System.out.println("Failed to execute: "+sqlStr);
			e.printStackTrace();
		} finally {
			closeStmt(st);
			closeConnection(conn);
		}
	}

	public static DBHelper getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public static DBHelper getInstance(String tag, String dataSourceName) {
		DataSource temp = (DataSource) SpringHelper.get(dataSourceName);
		DBHelper db = new DBHelper(temp, tag);
		db.init();
		return db;
	}

	public static DBHelper getInstance(String tag) {
		DataSource temp = (DataSource) SpringHelper.get(tag);
		DBHelper db = new DBHelper(temp, tag);
		db.init();
		return db;
	}

	public int getLastId(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		st.setEscapeProcessing(false);
		ResultSet rs = st.executeQuery(lastRowIdFunc);
		while (rs.next()) {
			return rs.getInt(1);
		}
		return -1;
	}

	public List getLongList(String sqlStr) {
		List<Long> l = new ArrayList<Long>();
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);

			set = st.executeQuery(sqlStr);
			while (set.next()) {
				l.add(set.getLong(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return l;
	}

	public List getDoubleList(String sqlStr) {
		List<Double> l = new ArrayList<Double>();
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);

			set = st.executeQuery(sqlStr);
			while (set.next()) {
				l.add(set.getDouble(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return l;
	}

	public List<String> getStringList(String sqlStr) {
		List<String> l = new ArrayList<String>();
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);

			set = st.executeQuery(sqlStr);
			while (set.next()) {
				l.add(set.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return l;
	}

	public Map<String, String> getStringMap(String sqlStr) {
		Map<String, String> l = new HashMap<String, String>();
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();

			set = st.executeQuery(sqlStr);
			while (set.next()) {
				l.put(set.getString(1), set.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return l;
	}

	public List<Integer> getIntList(String sqlStr) {
		List<Integer> l = new ArrayList<Integer>();
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();

			set = st.executeQuery(sqlStr);
			while (set.next()) {
				l.add(set.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return l;
	}

	public String getString(String sqlStr) {
		String ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			set = st.executeQuery(sqlStr);
			if (set.next()) {
				ret = set.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return ret;
	}

	public Integer getInt(String sqlStr) {
		Integer ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			set = st.executeQuery(sqlStr);
			if (set.next()) {
				ret = set.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return ret;
	}

	public Double getDouble(String sqlStr) {
		Double ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			set = st.executeQuery(sqlStr);
			if (set.next()) {
				ret = set.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return ret;
	}

	public List<Event> getEventsFromDb(EventDefinition def) {
		String sqlStr = "select * from " + def.getEventStore();
		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			set = st.executeQuery(sqlStr);
			Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(def.getType());
			IEventDBTransformer xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
			while (set.next()) {
				ret = xfer.loadFromDb(set);
				// //System.out.println("Executed Successfully");
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public List<Event> getEventsFromDb(EventDefinition def, int id) {
		String sqlStr = "select * from " + def.getEventStore() + " where id = " + id;
		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			set = st.executeQuery(sqlStr);
			Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(def.getType());
			IEventDBTransformer xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
			while (set.next()) {
				ret = xfer.loadFromDb(set);
				// System.out.println("Executed Successfully");
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public List<Event> getEventsFromDbGFromSQL(EventDefinition def, String sqlStr) {
		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			set = st.executeQuery(sqlStr);
			Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(def.getType());
			IEventDBTransformer xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
			while (set.next()) {
				ret = xfer.loadFromDb(set);
				// System.out.println("Executed Successfully");
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public List<Event> getEventsFromTable(String sqlstr, IEventDBTransformer xfer) {
		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			set = st.executeQuery(sqlstr);
			while (set.next()) {
				ret = xfer.loadFromDb(set);
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public List<Event> getEventsFromTableByType(String et) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(et);
		String table = ed.getEventStore();
		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		Map<String, IEventDBTransformer> xformers = new HashMap<String, IEventDBTransformer>();
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			String sqlStr = "select * from " + table;
			set = st.executeQuery(sqlStr);
			while (set.next()) {
				String evtType = set.getString("eventtype");
				IEventDBTransformer xfer = xformers.get(evtType);
				if (xfer == null) {
					Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(evtType);
					xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
				}
				ret = xfer.loadFromDb(set);
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public List<Event> getEventsFromTable(EventDefinition ed) {
		String table = ed.getEventStore();
		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		Map<String, IEventDBTransformer> xformers = new HashMap<String, IEventDBTransformer>();
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			String sqlStr = "select * from " + table;
			set = st.executeQuery(sqlStr);
			while (set.next()) {
				String evtType = set.getString("eventtype");
				IEventDBTransformer xfer = xformers.get(evtType);
				if (xfer == null) {
					Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(evtType);
					xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
				}
				ret = xfer.loadFromDb(set);
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public List<Event> getEventsFromTable(String table) {

		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		Map<String, IEventDBTransformer> xformers = new HashMap<String, IEventDBTransformer>();
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			String sqlStr = "select * from " + table;
			set = st.executeQuery(sqlStr);
			while (set.next()) {
				String evtType = set.getString("eventtype");
				IEventDBTransformer xfer = xformers.get(evtType);
				if (xfer == null) {
					Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(evtType);
					xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
				}
				ret = xfer.loadFromDb(set);
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public List<Event> getEventsFromTableWhere(String table, String where) {

		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		Map<String, IEventDBTransformer> xformers = new HashMap<String, IEventDBTransformer>();
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			String sqlStr = "select * from " + table + " " + (where == null ? "" : where);
			set = st.executeQuery(sqlStr);
			while (set.next()) {
				String evtType = set.getString("eventtype");
				IEventDBTransformer xfer = xformers.get(evtType);
				if (xfer == null) {
					Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(evtType);
					xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
				}
				ret = xfer.loadFromDb(set);
				if (ret != null) {
					rets.add(ret);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public boolean exists(String sqlStr) {
		boolean ret = false;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			set = st.executeQuery(sqlStr);
			if (set.next()) {
				ret = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return ret;
	}

	public static String getResultCSV(String query) {
		Connection cn = DBHelper.getInstance().createConnection();
		Statement stmt = null;
		try {
			stmt = cn.createStatement();
			stmt.setEscapeProcessing(false);
			ResultSet rs = stmt.executeQuery(query);
			StringWriter sw = new StringWriter();
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			CSVWriter writer = new CSVWriter(new PrintWriter(bs));
			writer.writeAll(rs, true);
			writer.flush();
			bs.flush();
			return bs.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelper.getInstance().closeConnection(cn);
		}
		return null;
	}

	public static String getResultJson(String query) {
		Connection cn = DBHelper.getInstance().createConnection();
		Statement stmt = null;
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		try {
			stmt = cn.createStatement();
			stmt.setEscapeProcessing(false);
			ResultSet res = stmt.executeQuery(query);
			ResultSetMetaData rmeta = res.getMetaData();

			int totalColumns = rmeta.getColumnCount();
			boolean ifrecord = false;
			while (res.next()) {
				ifrecord = true;
				sb.append("{");
				for (int i = 1; i <= totalColumns; i++) {
					String key = rmeta.getColumnName(i);
					Object o = res.getObject(key);
					if (o != null) {
						sb.append("\"" + StringUtils.lowerCase(key) + "\":");
						String temp = getVal(o);
						temp = StringEscapeUtils.escapeJavaScript(temp);
						sb.append("\"" + temp + "\"");
						if (i != totalColumns) {
							sb.append(",");
						}
					}
				}
				sb.append("}");
				// if (!res.isLast()) {
				sb.append(",");
				// }
			}
			if(ifrecord) {
			String stemp = sb.toString();
			sb = new StringBuilder();
			stemp = stemp.substring(0, stemp.length() - 1);
			sb.append(stemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelper.getInstance().closeConnection(cn);
		}
		sb.append("]");
		return sb.toString();
	}

	private static String getVal(Object o) {
		if (o instanceof Clob) {
			StringBuilder sb = new StringBuilder();
			try {
				char[] cbuf = new char[4098];
				Clob c = (Clob) o;
				Reader stream = c.getCharacterStream();
				int read;
				while ((read = stream.read(cbuf)) > 0) {
					sb.append(cbuf, 0, read);
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			return sb.toString();
		} else {
			return o.toString();
		}
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Connection createTxConn() throws Exception {
		Connection conn = threadTxLocal.get();
		if (conn == null) {
			conn = createConnection();
			threadTxLocal.set(conn);
		}
		if (conn != null) {
			conn.setAutoCommit(false);
		}
		return conn;
	}

	public void closeTxConn(Connection txConn) throws Exception {
		Connection conn = threadTxLocal.get();
		if (conn == null) {
			throw new Exception("There is no txConn in current thread context!");
		} else {
			closeConnection(conn);
			threadTxLocal.remove();
		}
	}

	public List<Event> getEventsFromDbGFromSQLNative(EventDefinition def, String sqlStr) {
		List<Event> rets = new ArrayList<Event>();
		Event ret = null;
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			st.setEscapeProcessing(false);
			set = st.executeQuery(sqlStr);

			Class dbc2 = EventDefinitionManager.getInstance().buildDBTransformerDefinition(def.getType());
			IEventDBTransformer xfer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc2.getName());
			while (set.next()) {
				ret = xfer.loadFromDbNative(set);
				// System.out.println("Executed Successfully");
				if (ret != null) {
					rets.add(ret);
				}
				ret = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return rets;
	}

	public Object[] getObjects(String sqlStr, int retCount) {
		List<Object> ret = new ArrayList<Object>();
		Statement st = null;
		Connection conn = null;
		ResultSet set = null;
		try {
			conn = createConnection();
			st = conn.createStatement();
			set = st.executeQuery(sqlStr);
			if (set.next()) {
				for (int i = 1; i < retCount + 1; i++) {
					Object r = set.getObject(i);
					ret.add(r);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(set);
			closeStmt(st);
			closeConnection(conn);
		}
		return ret.toArray();
	}
}
