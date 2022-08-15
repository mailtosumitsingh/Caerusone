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

public class GetProcDoc extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String graphid = request.getParameter("name");
			String json = DBHelper.getInstance().getResultJson("select doc,configoptions from procdocs where name='"+graphid+"'");
			if(json==null)
				response.getOutputStream().print("{}");
			else
				
			response.getOutputStream().print(json);
			
		} catch (Exception e) {
			response.getOutputStream().print("Node cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
