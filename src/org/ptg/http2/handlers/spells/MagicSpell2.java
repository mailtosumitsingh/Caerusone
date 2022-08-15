/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.spells;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.stream.Stream;
import org.ptg.util.CommonUtil;
import org.ptg.util.FindSimpleGraphPattern;
import org.ptg.util.Graph;

public class MagicSpell2 extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		Graph graph = null;
		try {
			graph = CommonUtil.getGraphRepresentationFromJson(name, graphjson);
			System.out.println(graph.getEnds());
			System.out.println(graph.getStarts());
			List<List<Stream>> g2 = graph.getAllSubPaths();
			StringBuilder sb = new StringBuilder();
			// /////////////////////

			List<Graph> graphs = CommonUtil.buildGraphs();
			for (List<Stream> search : g2) {
				List<String> search2 = new ArrayList<String>();
				for (Stream ss : search) {
					search2.add(ss.getName());
				}
				List<Graph> filtered = CommonUtil.forEachGraphDo(graphs, new FindSimpleGraphPattern(search2));
				sb.append("{");
				sb.append("\"mc\":\"" + filtered.size() + "\",\"matches\":[");
				for (int i = 0; i < filtered.size(); i++) {
					Graph g = filtered.get(i);
					sb.append("\""+g.getName() +"\""+ "\n");
					if (i < filtered.size() && i < filtered.size() - 1) {
						sb.append(",");
					}
					List<List<Stream>> paths = g.getAllPathsv3(search2.get(0), g.getEnds());
					System.out.println("dada" + paths.toString());
				}
				sb.append("],\"inpaths\":[");
				for (int i = 0; i < filtered.size(); i++) {
					Graph g = filtered.get(i);
					sb.append("{\"graphname\":\"" + g.getName() + "\",\"paths\":[");
					List<List<Stream>> paths = new ArrayList<List<Stream>>();
					List<List<Stream>> pathsOrig = g.getAllPaths();
					for (List<Stream> pathStream : pathsOrig) {
						boolean contains = false;
						for (String searchStream : search2) {
							if (!pathStream.contains(g.getStreams().get(searchStream))) {
								contains = false;
								break;
							} else {
								contains = true;
							}
						}
						if(contains)
							paths.add(pathStream);
					}
					StringBuilder tempPathStr = new StringBuilder();
					for (List<Stream> stream : paths) {
						tempPathStr.append("[");
						for (int k=0;k<stream.size();k++) {
							Stream ss = stream.get(k);
							tempPathStr.append("\""+ss.getName()+"\"" );

							if(k<(stream.size()-1)){
							tempPathStr.append( ",");
							}
							}
						tempPathStr.append("]");
					}
					sb.append( tempPathStr);
					sb.append("]}");
					if(i<(filtered.size()-1)){
						sb.append( ",");
						}
					
				}
				sb.append("]");
				sb.append("}");
			}

			// /////////////////////
			response.getOutputStream().print(sb.toString());
		} catch (Exception e) {
			response.getOutputStream().print("Could not validate process:\n" + e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
