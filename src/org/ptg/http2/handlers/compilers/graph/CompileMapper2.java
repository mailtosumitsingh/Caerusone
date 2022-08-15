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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class CompileMapper2 extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String run = request.getParameter("run");
		String mappingtype = request.getParameter("mappingtype");
		String ineventtype = request.getParameter("eventtype");
		Map<String, String> params = new HashMap<String, String>();
		Object toret = null;
		params.put("eventtype", ineventtype);
		if (mappingtype == null)
			mappingtype = "FreeSpring";
		try {
			Object o = CommonUtil.instantiateMappingGraph2(name, graphjson, mappingtype, params);
			if (run != null) {
				if (o != null) {
					Method mtd = null;
					
					mtd = o.getClass().getMethod("execute", new Class[]{Object.class});
					if(mtd==null){
						mtd = o.getClass().getMethod("run", new Class[0]);
					}
					if (mtd!=null) {
						if(mtd.getName().equals("run")){
							toret = mtd.invoke(o, new Object[0]);
						}else if(mtd.getName().equals("execute")){
							toret = mtd.invoke(o, new Object[]{null});
						}
									
					}
				}
			}
			if(toret==null)
				response.getWriter().print("Compiled fine");
			else
				response.getWriter().print(toret.toString());
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

}