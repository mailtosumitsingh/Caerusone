/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.velocity.VelocityHelper;

public class GetCanvas extends AbstractHandler {
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String uid = request.getParameter("uid");
		try{
			Pattern p = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9 _]*)\\}");
			Map m = new HashMap();
			m.put("uid",uid);
			StringBuffer responseContent = VelocityHelper.burnTemplate(m, "canvasModuleTemplate"+".vm");
			String toret = responseContent.toString();
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().print(toret);
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e.toString());
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
