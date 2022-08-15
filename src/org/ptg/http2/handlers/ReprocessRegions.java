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

import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.Graph;
import org.ptg.util.Group;

public class ReprocessRegions extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		Map<String, String> results = new HashMap<String, String>();

		Graph graph = null;
		try {
			graph = new Graph();
			graph.fromGraphJson(name, graphjson);
			System.out.println(graph.getEnds());
			System.out.println(graph.getStarts());

			for (Group def : graph.getGroups().values()) {
				boolean b = CommonUtil.compileGroupDefinition(def);
				results.put(def.getId(), b==true?"Compiled fine.":"Failed to compile.");
			}
			for (JSONObject def : graph.getOrphans().values()) {
				boolean b = CommonUtil.compileOrhpahDefinition(def,new HashMap()).size()!=0;
				results.put(def.getString("id"), b==true?"Compiled fine.":"Failed to compile.");
			}
			(response).getOutputStream().print(CommonUtil.toJson(results));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}
	
		((Request) request).setHandled(true);
	}

}
