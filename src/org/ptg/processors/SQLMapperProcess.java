/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.commons.collections.Closure;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.db.DBHelper;
import org.ptg.util.db.SQLObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class SQLMapperProcess extends AbstractIProcessor{
	boolean multiDB = false;
	Closure c;
	public void childAttach() throws GenericException {
		try {
			Map<String, String> params = new HashMap<String, String>();
	    	String graphjson= DBHelper.getInstance().getString("select graph from graphs  where name=\'"+query+"\'");
	    	Map<String, Object> l = CommonUtil.getGraphObjectsFromJson(query,graphjson);
			FPGraph2 g = new FPGraph2();
			g.setName(query);
			g.fromObjectMap(l,null);
	    	int sqlcount=0;
			String sql = "";
			String sqlin = "";
			String sqlout = "";
			String sqlType = "";
			for(Object o:l.values()){
				if(o instanceof SQLObj){
					SQLObj so = (SQLObj)o;
					sqlType = so.getSqlType();
					if(sqlType.equals("query")){
		            	sqlin = so.getSql();
		            	sql =so.getSql();
		            }
		            if(sqlType.equals("update")||sqlType.equals("insert")){
		            	sqlout = so.getSql();
		            	sql =so.getSql();
		            }
					sqlcount++;
				}
			}
			if(sqlcount> 1 ){
				if(multiDB)
					sqlType = "DBMap";
				else
					sqlType = "tablemap";
			}
			String ineventtype = stream.getEventType();
			String mappingtype = "SQLMapper1";
			if (sqlType!=null )
				mappingtype = "SQLMapper_"+sqlType;
			else 
				mappingtype = "SQLMapper1";

			params.put("eventtype", ineventtype);
			params.put("sql","\""+sql+"\"");
			params.put("sql","\""+sqlin+"\"");
			params.put("sqlin","\""+sqlin+"\"");
			params.put("sqlout","\""+sqlout+"\"");
			params.put("eventtype", stream.getEventType());

			Object  o =  CommonUtil.instantiateSQLMappingGraph(g, mappingtype, params);
			if (o != null) {
				if (o instanceof Closure) {
					c = (Closure) o;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}

	public void childProcess(Exchange msg) throws Exception {
		if(c!=null){
			c.execute(msg);
		}
	}

	@Override
	public String getDoc() {
		return "This processor will take the \"query\" parameter.\n"+
		"where query is the name of mapping to load and execute on event";
	}
}