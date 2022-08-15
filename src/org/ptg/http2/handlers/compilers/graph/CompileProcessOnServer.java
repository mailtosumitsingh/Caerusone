/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.compilers.graph;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.events.EventDefinition;
import org.ptg.processors.ConnDef;
import org.ptg.processors.ProcessorDef;
import org.ptg.processors.ProcessorManager;
import org.ptg.stream.Stream;
import org.ptg.util.CommonUtil;
import org.ptg.util.Graph;
import org.ptg.util.Group;

public class CompileProcessOnServer extends AbstractHandler {

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


			for (ProcessorDef def : graph.getProcs().values()) {
				boolean b = CommonUtil.compileProcessorDefinition(def);
				results.put(def.getName(), b==true?"Compiled fine.":"Failed to compile.");
			}
			for (Stream def : graph.getStreams().values()) {
				String processor = def.getProcessor();
				if (processor != null) {
					Map<String, Object> proc = ProcessorManager.getInstance().getProcessors().get(processor);
					if (proc == null) {
						ProcessorDef pdef = CommonUtil.getDefaultProcessor(def.getName() + "_DEFAULTProc");
						boolean b = CommonUtil.compileProcessorDefinition(pdef);
						if (b) {
							def.setProcessor(def.getName() + "_DEFAULTProc");
							b = CommonUtil.compileStreamDefinition(def);
							results.put(def.getName(), b==true?"Proc was mising (added) ,Compiled fine.":"Proc was mising (attempted),Failed to compile.");
						}else{
							results.put(def.getName(), "Failed to compile, default added but failed to compile.");
						}
					} else {
						boolean b = CommonUtil.compileStreamDefinition(def);
						results.put(def.getName(), b==true?"Compiled fine.":"Failed to compile.");
					}
				} else {
					ProcessorDef pdef = CommonUtil.getDefaultProcessor(def.getName() + "_DEFAULTProc");
					boolean b = CommonUtil.compileProcessorDefinition(pdef);
					if (b) {
						def.setProcessor(def.getName() + "_DEFAULTProc");
						b = CommonUtil.compileStreamDefinition(def);
						results.put(def.getName(), b==true?"Proc was mising (added) ,Compiled fine.":"Proc was mising (attempted),Failed to compile.");
					}else{
						results.put(def.getName(), "Failed to compile, default added but failed to compile.");
					}
				}

			}
			for (ConnDef def : graph.getForward().values()) {
				// "org.ptg.processors.connection.ConditionalConnection"
				if("synthetic".equals(def.getCtype()))continue;
				if (graph.getStreams().containsKey(def.getFrom()) && graph.getStreams().containsKey(def.getTo())) {
					if (def.getCtype()!=null&&def.getCtype().equals("arbitconnection")) {
						// is stream connection
						def.setCtype("org.ptg.processors.connection.ConditionalConnection");
						boolean b = CommonUtil.compileConnDefinition(def);
						results.put(def.getId(), b==true?"Compiled fine.":"Failed to compile.");
					} else {
						boolean b = CommonUtil.compileConnDefinition(def);
						results.put(def.getId(), b==true?"Compiled fine.":"Failed to compile.");
					}
				} else {
					results.put(def.getId(), "Failed to compile.");
				}
			}
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
