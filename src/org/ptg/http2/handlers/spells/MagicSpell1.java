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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.processors.ProcessorDef;
import org.ptg.processors.ProcessorManager;
import org.ptg.stream.Stream;
import org.ptg.util.CommonUtil;
import org.ptg.util.Graph;

public class MagicSpell1 extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		Graph graph = null; 
		try{
			graph  = CommonUtil.getGraphRepresentationFromJson(name, graphjson);
			System.out.println(graph.getEnds());
			System.out.println(graph.getStarts());
			List<List<Stream>>  g1 = graph.getAllPaths();
			List<List<Stream>>  g2 = graph.getAllSubPaths();
			String [] paths = new String[g2.size()];
			int j =0;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			
			Map<String,String> tree = new java.util.HashMap<String,String> ();
				tree.put("fr", "FileReaderProcess");
				tree.put("fw", "FileWriterProcess");
				tree.put("ftpout", "FTPProcess");
				tree.put("gl", "GetLockProcessor");
				tree.put("sm", "SendMailProcessor");
				tree.put(null, "DummyProcessor");
				tree.put("", "DummyProcessor");

				
				int total = graph.getStreams().values().size();
				
			for(Stream s: graph.getStreams().values()){
				sb.append("{\"name\":\"");
				sb.append(StringEscapeUtils.escapeJavaScript(s.getName()));
				sb.append("\",\"newname\":\"");
						sb.append(StringEscapeUtils.escapeJavaScript(tree.get(s.getName())==null?"DummyProcessor"+total:tree.get(s.getName())));
				String pname = s.getExtra();
						Map<String, Object> an = ProcessorManager.getInstance().getProcessor(pname);
				ProcessorDef def = null;
				if(an!=null){
					def =new ProcessorDef();
				String clz = (String) an.get("handlerClass");
				String query = (String) an.get("query");
				String pconfig = (String) an.get("configItems");
				def.setClz(clz);
				def.setQuery(query);
				def.setConfigItems(pconfig);
				def.setName(pname);
				}
				sb.append("\"");
				if(def!=null){
				String pjson = CommonUtil.toJson(def);
				sb.append(",\"proc\":");
				sb.append(pjson);
				}
				sb.append("}");
				if(total>1){
					sb.append(",");
				}
				total --;
			}
			sb.append("]");
			response.getOutputStream().print(sb.toString());
		} catch (Exception e) {
			response.getOutputStream().print("Could not validate process:\n" + e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	
}
