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

import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebStartProcess;
import org.ptg.util.db.DBHelper;

public class ExecuteStaticComponent extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");

		try {

			String ip = request.getRemoteAddr();
			String name = request.getParameter("name");

			String toSave = DBHelper.getInstance().getString("Select txt from staticcomponent where name='" + name + "'");
			JSONObject jo = JSONObject.fromObject(toSave);
			String s = jo.getString("txt");
			WebStartProcess.getInstance().getScriptEngine().runString(s);

			response.getOutputStream().print("Document Executed");
		} catch (Exception e) {
			response.getOutputStream().print("Document cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
