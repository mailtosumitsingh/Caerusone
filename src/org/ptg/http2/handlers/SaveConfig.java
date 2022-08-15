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

public class SaveConfig extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String config = request.getParameter("config");
		String type = request.getParameter("type");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			CommonUtil.saveVar("CONFIG_" + type + "_" + name, config);
			response.getOutputStream().print("Configuration " + name + " Saved");
		} catch (Exception e) {
			response.getOutputStream().print("Configuration " + name + "cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
