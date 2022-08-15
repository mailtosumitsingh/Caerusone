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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.stream.Stream;
import org.ptg.util.CommonUtil;
import org.ptg.util.Graph;

public class CompileCamelGraph extends AbstractHandler {

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
			sb.append("");
			for(Collection<Stream> path: g2 ){
				int i=0;
				for(Stream st: path){
					sb.append(st.getProcessor()+"(\""+st.getExtra()+"\")");
					if(i!=path.size()-1)
						sb.append(".");
				 i++;
				}
				paths[j]=sb.toString();
				j++;
				sb.append(";");
			}
			
			response.getOutputStream().print(sb.toString());
		} catch (Exception e) {
			response.getOutputStream().print("Could not validate process:\n" + e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	
}
