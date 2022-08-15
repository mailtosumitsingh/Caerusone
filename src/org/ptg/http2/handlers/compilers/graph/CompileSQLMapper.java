/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.compilers.graph;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.Closure;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class CompileSQLMapper extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String sql = request.getParameter("sql");
		String sqlin = request.getParameter("sqlin");
		String sqlout = request.getParameter("sqlout");

		
		String run = request.getParameter("run");
		String sqlType = request.getParameter("sqlType");
		
		String ineventtype = request.getParameter("eventtype");
		Map<String, String> params = new HashMap<String, String>();
		params.put("eventtype", ineventtype);
		params.put("sql","\""+sql+"\"");
		params.put("sqlin","\""+sqlin+"\"");
		params.put("sqlout","\""+sqlout+"\"");
		
		String mappingtype = "SQLMapper1";
		if (sqlType!=null )
			mappingtype = "SQLMapper_"+sqlType;
		else 
			mappingtype = "SQLMapper1";
		try {
			Object o = CommonUtil.instantiateSQLMappingGraph(name, graphjson, mappingtype, params);
			if (run != null) {
				if (o != null) {
					if (o instanceof Closure) {
						Closure c = (Closure) o;

						c.execute(null);
					}
					
				}
			}
			response.getWriter().print("Compiled fine");
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

}