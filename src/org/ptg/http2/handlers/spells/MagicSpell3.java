/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.spells;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.stream.Stream;
import org.ptg.util.Graph;

public class MagicSpell3 extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		Graph graph = null; 
		try{
			graph  = new Graph();
			graph.fromJson(name, graphjson);
			System.out.println(graph.getEnds());
			System.out.println(graph.getStarts());
			List<List<Stream>>  g1 = graph.getAllPaths();
			List<List<Stream>>  g2 = graph.getAllSubPaths();
		
			response.getOutputStream().print("done");
		} catch (Exception e) {
			response.getOutputStream().print("Could not validate process:\n" + e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	
}
