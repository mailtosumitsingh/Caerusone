/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.compilers.code;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebStartProcess;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.db.DBHelper;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.v2.FPGraph2;
import org.ptg.velocity.VelocityHelper;

public class CompileTemplate extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		FPGraph2 g = null;
		try {
			if(graphjson==null){
				g = CommonUtil.buildMappingGraph2(name);
			}else{
				g = new FPGraph2();
				g.fromGraphJson(name, graphjson);
			}
			Map<String,Object> contextMap = new LinkedHashMap<String,Object>();
			String template = null;
			for(Map.Entry<String,FunctionPoint>en : g.getFunctionPoints().entrySet()){
				if(en.getKey().equals("main")){
					template = en.getValue().getVal();
				}else{
				if(en.getValue().getVal()!=null){
				contextMap.put(en.getKey(), en.getValue().getVal());
				}
				}
			}
			if(template!=null){
			contextMap.put("spring",SpringHelper.class);
			contextMap.put("ws",WebStartProcess.class);
			contextMap.put("CommonUtil",CommonUtil.class);
			contextMap.put("dbhelper",DBHelper.getInstance());
			StringBuffer sb = VelocityHelper.burnStringTemplate(contextMap,template);
			sb = VelocityHelper.burnStringTemplate(contextMap,sb.toString());
			response.getWriter().print(sb.toString());
			}else{
				response.getWriter().print("Please add a module with id main");
			}
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

}