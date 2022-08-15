/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.geom.algo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.script.ScriptEngine;

/**
 * 
 */
public class FindItemsInRegion extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String proc = request.getParameter("process");
		String region = request.getParameter("region");
		try {
			ScriptEngine  s = new ScriptEngine();
			s.init();
			List l = new ArrayList();
			l.add(proc);
			l.add(region);
			Object r = s.runFuntionRaw("findGraphItemsInRegion", l);
			//String ret =  (String) ((org.mozilla.javascript.NativeJavaObject) r).unwrap();
			response.getOutputStream().print(r.toString());
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
