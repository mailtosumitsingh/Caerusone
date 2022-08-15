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
import org.ptg.admin.AppContext;
import org.ptg.util.NativeFormatter;

public class SetVar extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		Object val = request.getParameter("val");
		String type = request.getParameter("type");
		try {
			if (type != null && type.length() > 0) { 
				val = NativeFormatter.fromString(type, val.toString()); 
				System.out.println("Native value of v is : "+val); 
			}
			AppContext.getInstance().setVar(name, val);
			System.out.println("Setting " + name + " : " + val);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().print("Variable set fine: " + name);
		} catch (Exception e) {
			response.getOutputStream().print("Variable could not be set\n" + e.toString());
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
