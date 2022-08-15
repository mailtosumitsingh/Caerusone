package org.ptg.http2.handlers.cnc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class CncHandler  extends AbstractHandler {
	static Map<String, String> gcodes= new HashMap<String,String>();

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		String name = request.getParameter("name");
		String op = request.getParameter("op");
		if(op.equals("up")){
			String gcode = request.getParameter("gcodes");
			gcodes.put(name,gcode);
			response.getWriter().write("done");
		}else if(op.equals("down")){
			response.setHeader("content-disposition", "attachment; filename=" + name + "-cnc."+"txt");
			response.getOutputStream().write(gcodes.get(name).getBytes());
			response.getOutputStream().flush();
			response.flushBuffer();
		}else{
			
		}
		
		((Request) request).setHandled(true);
		
	}
	

}
