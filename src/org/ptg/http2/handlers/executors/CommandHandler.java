/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.executors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebStartProcess;
import org.ptg.script.ScriptEngine;

public class CommandHandler extends AbstractHandler{

    		public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    			String cmd  = request.getParameter("cmd");
    			response.setHeader("Access-Control-Allow-Origin", "*");
	         try {
	        	 List l = new ArrayList();
	        	 l.add(request);
	        	 l.add(response);
	        	 Object ret = null;
	        	 if(WebStartProcess.getInstance()!=null &&WebStartProcess.getInstance().getScriptEngine()!=null){
				  ret = WebStartProcess.getInstance().getScriptEngine().runTask(cmd, l);
	        	 }else{
	        		 ScriptEngine sc = new  ScriptEngine();
	        		 sc.init();
					  ret =  sc.runTask(cmd, l);
	        	 }
		         response.getOutputStream().print("/*Result */\n"+ret);
			} catch (Exception e) {
				response.getOutputStream().print("/*Result */ \n"+e);
				e.printStackTrace();
			}
	         ((Request)request).setHandled(true);
	    }

}
