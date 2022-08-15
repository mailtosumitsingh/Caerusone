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

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.db.DBHelper;

public class GetGraph extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String downLoad = request.getParameter("d");
			String graphid = request.getParameter("graphid");
			if (downLoad != null && downLoad.equals("true")) {
				response.setHeader("content-disposition", "attachment; filename=" + graphid + "-Graph.json");
			}
			String json = DBHelper.getInstance().getResultJson("select graph,graphconfig from graphs where name='" + graphid + "'");
			response.getOutputStream().print(json);
			response.flushBuffer();
		} catch (Exception e) {
			response.getOutputStream().print("Node cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
