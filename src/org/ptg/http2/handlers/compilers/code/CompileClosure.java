/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.compilers.code;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.Closure;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;

public class CompileClosure extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String code = "{\n"+request.getParameter("code")+"}\n";
		String update = request.getParameter("update");
		String run = request.getParameter("run");
		try {
			Class c = CommonUtil.compileClosure(name,code,update!=null?(update.equalsIgnoreCase("true")?true:false):false);
			if(run.startsWith("true")){
				Closure clo = (Closure) c.newInstance();
				clo.execute(null);
			}
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().print("Compiled fine: "+name);
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
