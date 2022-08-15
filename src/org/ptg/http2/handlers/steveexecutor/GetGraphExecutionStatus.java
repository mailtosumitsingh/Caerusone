/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.steveexecutor;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;

public class GetGraphExecutionStatus extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String eleid = request.getParameter("eleid");
		String exec = request.getParameter("exec");
		String uid = request.getParameter("uid");
		Map<String,String>s = CommonUtil.getExecStatus(uid);
		response.getOutputStream().print( CommonUtil.toJson(s));
		arg1.setHandled(true);
	}

}
