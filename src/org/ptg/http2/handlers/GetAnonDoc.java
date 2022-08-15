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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.db.DBHelper;

public class GetAnonDoc extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		Map<String,String> functions = new HashMap<String,String>();
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String graphid = request.getParameter("name");
			functions.put("concat", "ConcatTask");
			functions.put("constant", "ConstantTask");
			functions.put("log", "LogTask");
			functions.put("functionCall", "FunctionTask");
			functions.put("functionCall2", "FunctionTask2");
			functions.put("trace", "TraceTask");
			functions.put("if", "IfTask");
			functions.put("end", "EndTask");
			functions.put("UIChart", "DisplayTask");
			functions.put("loopback","loopback"); 
			functions.put("rungraph","RunGraphTask"); 
			functions.put("command","command"); 
			functions.put("longCmd","longCmd"); 
			functions.put("SchemaMapperTask", "SchemaMapperTask");
			graphid = functions.get(graphid);
			String json = DBHelper.getInstance().getResultJson("select doc,configoptions from procdocs where name='"+graphid+"'");
			response.getOutputStream().print(json);
		} catch (Exception e) {
			response.getOutputStream().print("Node cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
