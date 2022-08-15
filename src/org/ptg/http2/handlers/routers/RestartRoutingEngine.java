/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.routers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebStartProcess;
import org.ptg.router.RoutingEngine;

public class RestartRoutingEngine extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		try {
			
					RoutingEngine re = WebStartProcess.getInstance().getRoutingEngine();
					//WebStartProcess.getInstance().detachNodes();
					//WebStartProcess.getInstance().attachNodes();
					re.reinit();/*will start stop and initialze again*/
					/*reload routes here*/
					WebStartProcess.getInstance().addRoutes();
					response.getOutputStream().print("Engine restared fine");
		} catch (Exception e) {
			response.getOutputStream().print("Could not RestartEngine:\n" + e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
