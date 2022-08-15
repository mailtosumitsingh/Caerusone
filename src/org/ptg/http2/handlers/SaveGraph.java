/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.db.DBHelper;

public class SaveGraph extends AbstractHandler {
	boolean escape = CommonUtil.escapeJavaScriptInSql();

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String name = request.getParameter("name");
			String toSave = request.getParameter("tosave");
			String graphconfig = request.getParameter("graphconfig");
			String graphtype = request.getParameter("graphtype");
			String graphicon = request.getParameter("graphicon");
			if (graphconfig == null) {
				graphconfig = "";
			}
			if (graphtype == null) {
				graphtype = "graph";
			}
			if (escape) {
				toSave = StringEscapeUtils.escapeJavaScript(toSave);
			}
			String move = "insert into deletedgraphs select * from graphs where name='" + name + "'";
			String delete = "delete from graphs where name='" + name + "'";
			String insert = "insert into graphs(name,graph,userid,userip,graphconfig,graphtype,icon) values (?,?,?,?,?,?,?)";
			DBHelper.getInstance().executeUpdate(move);
			DBHelper.getInstance().executeUpdate(delete);
			DBHelper.getInstance().executePreparedInsert(insert, name, toSave, "0", ip, graphconfig, graphtype, graphicon);
			response.getOutputStream().print("Graph Saved");
		} catch (Exception e) {
			response.getOutputStream().print("Graph cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}
}
