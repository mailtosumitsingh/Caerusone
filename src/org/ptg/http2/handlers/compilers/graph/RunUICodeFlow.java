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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class RunUICodeFlow extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String fpath = request.getParameter("filePath");
		String filePath = base + "/uploaded/in/" + fpath;
		CompileTaskPlanToCode comp = new CompileTaskPlanToCode();
		String uid = getUUIDStr();
		try {
			FPGraph2 o = buildGraph(name, graphjson);
			CodeBlock code = comp.createAndRunGraph(uid,name,o,null,comp.JAVA_SCRIPT_SRC,true);
			String codeRet = StringUtils.replace(code.build(), "boolean", "var");
			response.getWriter().print(codeRet);
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}


	public RunUICodeFlow() {
		init();
	}
	public FPGraph2 buildGraph(String name, String graphjson) {
		init();
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		List<TypeDefObj> types = o.getTypeDefs();
		Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		CommonUtil.getAnonFromUIModels(o, types, typeMap, anonCompMap);
		CommonUtil.updateGraphicProps(anonCompMap, o.getPorts());
		return o;
	}

	public void init() {
			
	}
	private String getUUIDStr() {
		return CommonUtil.getUUIDStr();
	}
}