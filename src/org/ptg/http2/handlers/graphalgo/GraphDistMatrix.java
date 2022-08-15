/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.graphalgo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.mapper.FunctionPoint;

import edu.uci.ics.jung.graph.DirectedGraph;

public class GraphDistMatrix extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String mappingtype = request.getParameter("mappingtype");
		String ineventtype = request.getParameter("eventtype");
		Map<String, String> params = new HashMap<String, String>();
		params.put("eventtype", ineventtype);
		if (mappingtype == null)
			mappingtype = "ToDo";
		try {
			DirectedGraph<FunctionPoint, ConnDef>  o = CommonUtil.analyzeSpanningTree(name, graphjson, mappingtype, params);
			Collection<ConnDef>defs = o.getEdges();
			Map<String,String> treeMap = new HashMap<String,String>();
			for(ConnDef def: defs){
				treeMap.put(def.getFrom(), def.getTo());
			}
			response.getWriter().print(CommonUtil.toJson(treeMap));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

}