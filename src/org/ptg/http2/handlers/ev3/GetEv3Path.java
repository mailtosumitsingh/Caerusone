/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.ev3;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.HTTPClientUtil;
import org.ptg.util.mapper.v2.FPGraph2;

import com.google.common.collect.Maps;

public class GetEv3Path extends AbstractHandler {
	

	
	public GetEv3Path() {
	}
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
			String sstr= CommonUtil.jsonFromCollection(g.getShapes().values());
		Map<String,String>m = Maps.newHashMap();
		m.put("graph",sstr);
		try {
			byte[] model  = HTTPClientUtil.doPost(m, "http://localhost:8080/getPath");
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		((Request) request).setHandled(true);
		
	}
	
}