/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.processors.ConnDef;
import org.ptg.processors.ProcessorDef;
import org.ptg.processors.ProcessorManager;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.Graph;

public class SaveProcessOnServer extends AbstractHandler {

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
			List<List<Stream>> g1 = graph.getAllPaths();
			List<List<Stream>> g2 = graph.getAllSubPaths();
			Collection<EventDefinition> events = graph.getEventDefs().values();

			for (EventDefinition def : events) {
				boolean b = true;
				EventDefinitionManager.getInstance().deleteEventDefinition(def);
				EventDefinitionManager.getInstance().saveEvent(def);
				results.put(def.getType(), b == true ? "Compiled fine." : "Failed to save.");
			}
			for (ProcessorDef def : graph.getProcs().values()) {
				boolean b = true;
				ProcessorManager.getInstance().deleteProcessorDef(def);
				ProcessorManager.getInstance().saveProcessorDef(def);
				results.put(def.getName(), b == true ? "Compiled fine." : "Failed to save.");
			}
			for (Stream def : graph.getStreams().values()) {
				boolean b = true;
				StreamManager.getInstance().deleteStream(def);
				StreamManager.getInstance().saveStream(def);
				results.put(def.getName(), "Failed to save, default added but failed to save.");

			}
			for (ConnDef def : graph.getForward().values()) {
				//we donot have to do anything specificigcally for saving the connection 
				//the connection is saved as usual even before the code reaches here.
			}

			(response).getOutputStream().print(CommonUtil.toJson(results));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

}
