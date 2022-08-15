/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.db.DBHelper;

public class GetGraphNamesWithIcon extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String graphtype = request.getParameter("graphtype");
			if (graphtype == null) {
				graphtype = "graph";
			}
			Map<String, String> json = DBHelper.getInstance().getStringMap("select name,icon from graphs where graphtype='" + graphtype + "' and icon is not null");
			response.getOutputStream().print(CommonUtil.toJson(json));
		} catch (Exception e) {
			response.getOutputStream().print("Node cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
