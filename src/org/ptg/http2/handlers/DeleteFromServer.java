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
import org.ptg.util.CommonUtil;

public class DeleteFromServer extends AbstractHandler {
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			CommonUtil.deleteGraphItem(name, type);
			response.getOutputStream().print(name +" Deleted Successfully.");
		} catch (Exception e) {
			response.getOutputStream().print(name +" Could not be deleted");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}



}
