/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.executors;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebStartProcess;
public class SriptCommandHandler extends AbstractHandler{

    		public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    			String cmd  = request.getParameter("script");
    			response.setHeader("Access-Control-Allow-Origin", "*");
	         try {
				 WebStartProcess.getInstance().getScriptEngine().runString(cmd);
		         response.getOutputStream().print("/*Result */\n"+"Success");
			} catch (Exception e) {
				response.getOutputStream().print("/*Result */ \n"+e);
				e.printStackTrace();
			}
	         ((Request)request).setHandled(true);
	    }

}
