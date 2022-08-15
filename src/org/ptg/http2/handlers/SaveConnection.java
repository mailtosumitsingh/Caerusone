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

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class SaveConnection extends AbstractHandler {
	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String toSave = request.getParameter("tosave");
			toSave = StringEscapeUtils.unescapeJavaScript(toSave);
			JSONObject obj = JSONObject.fromObject(toSave);
			String id = "";
			if (obj != null) {
				id = obj.getString("id");
			}
			response.getOutputStream().print("Connection Saved");
		} catch (Exception e) {
			response.getOutputStream().print("Connection cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
