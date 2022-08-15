package org.ptg.processors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.mozilla.javascript.Context;
import org.ptg.events.Event;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.db.DBHelper;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class RunProcessTask extends AbstractIProcessor {
	public void childAttach() throws GenericException {
	System.out.println("child attached");
	}

	public void childProcess(Exchange msg) throws Exception {
		Event evt = (Event) msg.getIn().getBody();
		String uid = evt.getEventStringProperty("uid");
		String eleid = (String) c.get("eleid");
		String gname = (String) c.get("gname");
		if(uid==null||uid.length()==0){
		if(query==null||query.length()==0||query.equals("PICKLAST")){
			uid=DBHelper.getInstance().getString("SELECT instid FROM graphinstances where iid= (select max(iid) from graphinstances where  name='"+gname+"')") ;
			if(uid==null)
				uid=CommonUtil.getUUIDStr(null);
		}else if(query.equals("PICKVAR")){
			uid=(String) CommonUtil.getVar(extra);
		}else if(query.equals("EXECSCRIPT")){
			uid=(String) CommonUtil.executeScript(extra);
		}else if(query.equals("EXECCODE")){
			uid=(String) CommonUtil.executeScript(extra);
		}
		}
		if(DBHelper.getInstance().getString("SELECT name FROM graphinstances where instid= '"+uid+"'")==null){
			CommonUtil.createGraphInstance(gname,null,null,null,null,uid);
		}
		String graphjson = DBHelper.getInstance().getString("select graph from "+"graphinstances"+" where instid='" + uid + "'");
		FPGraph2 g = new FPGraph2();
		 Map<String, Object> list = CommonUtil.getGraphObjectsFromJson(gname, graphjson);
			g.setName(name);
			g.fromObjectMap(list, null);
		 processEleid(msg, uid, eleid, g);
		
		
	}

	private void processEleid(Exchange msg, String uid, String eleid, FPGraph2 g) throws IOException {
		FunctionPoint fp = g.getFunctionPoints().get(eleid);
		JSONObject j = (JSONObject) fp.getXref();
		String exec = "sv=js_String(10);";//j.getString("content");
		PItem p = new PItem(eleid, exec, uid);
		System.out.println("now: "+eleid);
		processPItem(msg,p);
		Collection<FunctionPoint>fps = g.getMainGraph().getGraph().getSuccessors(fp);
		for(FunctionPoint fpp:fps){
			processEleid(msg, uid, fpp.getId(), g);	
		}
	}
	public void processPItem(Exchange msg,PItem pitem) throws IOException {
		System.out.println("now Executing: "+pitem.getEleid());
		String eleid = pitem.eleid, exec = pitem.exec, uid = pitem.uid;
		String query = ("select iid,graph,graphconfig from "+"graphinstances"+" where instid=\"" + uid + "\"");
		Statement st = null;
		Map config = null;
		String configStr  = null;
		Connection conn = null;
		ResultSet rs = null;
		int  ikey = -1;
		String json = null;
		try {
			conn = DBHelper.getInstance().createConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()){
				ikey = rs.getInt(1);
				json  = rs.getString(2);
				configStr = rs.getString(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelper.getInstance().closeStmt(st);
			DBHelper.getInstance().closeConnection(conn);
		}
	//	json = 
		try {
			//Thread.currentThread().sleep(1000);
			config = CommonUtil.getConfigFromJsonData(configStr);
			String cstatus = CommonUtil.getExecStatus(eleid, uid);
			List<String> childWaiting = allChildExecuted(eleid,uid,json);
			boolean childReady = (childWaiting.size()==0);
			if ( cstatus == null && (childReady)) {
				executeThisNode(msg,eleid, exec, uid, ikey,config);
				msg.getIn().setHeader("result","{\"result\":\"success\",\"result\":\"" + "Success" + "\"}");
			}else if( (cstatus!=null && cstatus.equalsIgnoreCase("Failed") ) && childReady){
				executeThisNode(msg,eleid, exec, uid, ikey,config);
				msg.getIn().setHeader("result","{\"result\":\"success\",\"result\":\"" + "Success" + "\"}");
			}else if( (cstatus==null||cstatus.equalsIgnoreCase("Failed"))&&childReady ==false ){
				CommonUtil.updateExecStatus(eleid, uid, "Failed", new String("All child nodes are not executed"));
				msg.getIn().setHeader("result","{\"result\":\"Failed\",\"result\":\"" + "Waiting" + "\",\"child\":"+CommonUtil.jsonFromCollection(childWaiting)+"}");
			}else {
				msg.getIn().setHeader("result","{\"result\":\"Ignored\",\"result\":\"" + "Already Executed" + "\"}");
			}
		} catch (Exception e) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			PrintStream w = new PrintStream(b);
			e.printStackTrace(w);
			msg.getIn().setHeader("result","Failure");
			e.printStackTrace();
			CommonUtil.updateExecStatus(eleid, uid, "Failed", new String(b.toByteArray()));
		}

	}
	private void executeThisNode(Exchange msg, String eleid, String exec, String uid, int id,Map config) throws IOException {
		CommonUtil.updateExecStatus(eleid, uid, "Started", "");
		Context ctx = CommonUtil.beginScriptContext();
		CommonUtil.addObject("graphConfig", config);
		System.out.println("now executing : " + exec);
		CommonUtil.executeScript(exec);
		Object sv = (String) CommonUtil.getObject("sv");
		//add code to update config sometime
		CommonUtil.removeObject("graphConfig");
		CommonUtil.endScriptContext(ctx);
		String gname = "" +id;
		CommonUtil.updateGraphItemsOfflineInTable(uid, new String[] { eleid }, new String[] { "compStatus" }, new String[] { "100" }, "graphinstances");
		CommonUtil.updateExecStatus(eleid, uid, "Finished", "");
	}
	public void getChildStatus(String eleid, String uid, String json) {
		final Map<String, Object> l = CommonUtil.getGraphObjectsFromJson("tempgraph", json);
		FPGraph2 g = new FPGraph2();
		g.setName("tempgraph");
		g.fromObjectMap(l, null);
		DirectedSparseMultigraph<FunctionPoint, ConnDef>  graph = g.getMainGraph().getGraph();
		FunctionPoint vertex = g.getMainGraph().getFunctionPoints().get(eleid);
		Collection<FunctionPoint> pre = graph.getPredecessors(vertex);
		for(FunctionPoint fpp : pre){
			String cst = CommonUtil.getExecStatus(fpp.getId(), uid);
			if(cst!=null){
				if(cst.equalsIgnoreCase("Started")){
					System.out.println(fpp.getId()+" is child and is Still running.");	
				}else if(cst.equalsIgnoreCase("Failed")){
					System.out.println(fpp.getId()+" is child and Has Failed.");
				}else if(cst.equalsIgnoreCase("Finished")){
					System.out.println(fpp.getId()+" is child and is Finished.");
				}else {
					System.out.println(fpp.getId()+" is child and its status is unknown.");
				}
				
			}else{
				System.out.println(fpp.getId()+" is child and is not done.");
			}
		}
	}
	public List<String> allChildExecuted(String eleid, String uid, String json) {
		final Map<String, Object> l = CommonUtil.getGraphObjectsFromJson("tempgraph", json);
		FPGraph2 g = new FPGraph2();
		List<String> ret = new ArrayList<String>();
		g.setName("tempgraph");
		g.fromObjectMap(l, null);
		DirectedSparseMultigraph<FunctionPoint, ConnDef>  graph = g.getMainGraph().getGraph();
		FunctionPoint vertex = g.getMainGraph().getFunctionPoints().get(eleid);
		Collection<FunctionPoint> pre = graph.getPredecessors(vertex);
		for(FunctionPoint fpp : pre){
			String cst = CommonUtil.getExecStatus(fpp.getId(), uid);
			if(cst!=null){
				if(cst.equalsIgnoreCase("Started")){
					System.out.println(fpp.getId()+" is child and is Still running.");	
						ret.add(fpp.getId());
				}else if(cst.equalsIgnoreCase("Failed")){
					System.out.println(fpp.getId()+" is child and Has Failed.");
					ret.add(fpp.getId());
				}else if(cst.equalsIgnoreCase("Finished")){
					System.out.println(fpp.getId()+" is child and is Finished.");
				}else {
					System.out.println(fpp.getId()+" is child and its status is unknown.");
					ret.add(fpp.getId());
				}
			}else{
				System.out.println(fpp.getId()+" is child and is not done.");
				ret.add(fpp.getId());
			}
		}
		if(pre==null||pre.size()==0){
			System.out.println(eleid+" has no child.");
		}
		return ret;
	}
	private static class PItem {
		String eleid;
		String exec;
		String uid;

		public String getEleid() {
			return eleid;
		}

		public void setEleid(String eleid) {
			this.eleid = eleid;
		}

		public String getExec() {
			return exec;
		}

		public void setExec(String exec) {
			this.exec = exec;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public PItem(String eleid, String exec, String uid) {
			super();
			this.eleid = eleid;
			this.exec = exec;
			this.uid = uid;
		}

	}
	public String getConfigOptions() {
		return "[\"eleid\",\"gname\"]";
	}
	
}

