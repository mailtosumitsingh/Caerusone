/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.steveexecutor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.mozilla.javascript.Context;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.db.DBHelper;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ExecuteGraphElement extends AbstractHandler {
	Map<String, PItem> progress = new LinkedHashMap<String, PItem>();

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String eleid = request.getParameter("eleid");
		String exec = request.getParameter("exec");
		String uid = request.getParameter("uid");
		PItem p = new PItem(eleid, exec, uid);
		System.out.println("now: "+eleid);
		processPItem(request, response, p);
		arg1.setHandled(true);
	}

	public void processPItem(HttpServletRequest request, HttpServletResponse response, PItem pitem) throws IOException {
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
				executeThisNode(response, eleid, exec, uid, ikey,config);
				response.getOutputStream().print("{\"result\":\"success\",\"result\":\"" + "Success" + "\"}");
			}else if( (cstatus!=null && cstatus.equalsIgnoreCase("Failed") ) && childReady){
				executeThisNode(response, eleid, exec, uid, ikey,config);
				response.getOutputStream().print("{\"result\":\"success\",\"result\":\"" + "Success" + "\"}");
			}else if( (cstatus==null||cstatus.equalsIgnoreCase("Failed"))&&childReady ==false ){
				CommonUtil.updateExecStatus(eleid, uid, "Failed", new String("All child nodes are not executed"));
				response.getOutputStream().print("{\"result\":\"Failed\",\"result\":\"" + "Waiting" + "\",\"child\":"+CommonUtil.jsonFromCollection(childWaiting)+"}");
			}else {
				response.getOutputStream().print("{\"result\":\"Ignored\",\"result\":\"" + "Already Executed" + "\"}");
			}
		} catch (Exception e) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			PrintStream w = new PrintStream(b);
			e.printStackTrace(w);
			response.getOutputStream().print("Failure");
			e.printStackTrace();
			CommonUtil.updateExecStatus(eleid, uid, "Failed", new String(b.toByteArray()));
		}

	}
	private void executeThisNode(HttpServletResponse response, String eleid, String exec, String uid, int id,Map config) throws IOException {
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
}
