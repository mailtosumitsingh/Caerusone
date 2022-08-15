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

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.ComponentComileException;
import org.ptg.util.Constants;
import org.ptg.util.DiagnosticException;
import org.ptg.util.IObjectMapper;
import org.ptg.util.SpringHelper;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class CompileObjectMapping extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;
	Map<String, String> ctxVarNames = new HashMap<String, String>();
	CompileTaskPlanToCode compileTaskPlanToCode = new CompileTaskPlanToCode();

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");

		try {
			IObjectMapper obj = getObjectMapperInst(name, graphjson);
			obj.init();
			response.getWriter().print("{\"status\":\"success\",\"msg\":\"Compiled successfully\"}");
		} catch (Exception e) {
			if(e instanceof ComponentComileException){
				ComponentComileException compExcep = (ComponentComileException)e;
				if(compExcep.getCompName()!=null && compExcep.getCompName().length()>0){
				response.getOutputStream().print("{\"status\":\"fail\",\"msg\":\"Couldn not Compile "+StringEscapeUtils.escapeJavaScript(e.toString())+"\",\"component\":\""+compExcep.getCompName()+"\"}");
				}else{
					response.getOutputStream().print("{\"status\":\"fail\",\"msg\":\"Couldn not Compile "+StringEscapeUtils.escapeJavaScript(e.toString())+"\"}");
				}
			}else{
			response.getOutputStream().print("{\"status\":\"fail\",\"msg\":\"Couldn not Compile "+StringEscapeUtils.escapeJavaScript(e.toString())+"\"}");
			}
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	public IObjectMapper getObjectMapperInst(String name, String graphjson) throws Exception {
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		return objectMapperFromGraph(name, o);
	}
	public CodeBlock getSolution(String name, FPGraph2 o, FPGraph2 parent,boolean genCond) throws Exception{
		String instidStr = CommonUtil.getUUIDStr();
		CodeBlock ret = compileTaskPlanToCode.runApp(instidStr, name, o, parent, compileTaskPlanToCode.JAVA_SRC,genCond);
		return ret;
	}

	public CodeBlock getSolution(String name, FPGraph2 o, FPGraph2 parent) throws Exception{
		String instidStr = CommonUtil.getUUIDStr();
		CodeBlock ret = compileTaskPlanToCode.runApp(instidStr, name, o, parent, compileTaskPlanToCode.JAVA_SRC,true);
		return ret;
	}

	public IObjectMapper objectMapperFromGraph(String name, FPGraph2 o) throws Exception{
		CodeBlock solution = getSolution(name, o, null);
		// /////////////////////
		AnonDefObj in = null;
		AnonDefObj out = null;
		for (AnonDefObj ad : o.getAnonDefs()) {
			if (ad.getInputs().size() == 0 && ad.getAux().size() == 0 && ad.getOutputs().size() > 0) {
				in = ad;
			} else if (ad.getInputs().size() > 0&& ad.getAux().size() == 0  && ad.getOutputs().size() == 0) {
				out = ad;
			}
		}

		Map<String, String> params = new HashMap<String, String>();
		CommonUtil.getMainPortObject(o,out);
		CommonUtil.getMainPortObject(o,in);
		params.put("outtype", out.getMainPort().getDtype());
		params.put("intype", in.getMainPort().getDtype());

		IObjectMapper obj = null;
		try {
			obj = (IObjectMapper) CommonUtil.compileMappingGraph2(name, solution.build(), "objmapper", params);
		}catch (RuntimeException e) {
			e.printStackTrace();
			if(e.getCause() instanceof DiagnosticException ){
				DiagnosticException dex = (DiagnosticException) e;
			}else if(e.getCause() instanceof DiagnosticException){
				DiagnosticException d = (DiagnosticException) e.getCause();
				long line = ((DiagnosticException) d).getDiagnostic().getLineNumber();
				String comp = solution.getLineToComp().get(line-68/*base line code that is used by the template*/);
				String ln = solution.build().split("\n")[(int) (line-68-1)];
				if(comp!=null){
					System.out.println("Problem with Component: "+comp);
					ComponentComileException excep = new ComponentComileException("Problem with Component: "+comp);
					excep.setCompName(comp);
					throw excep;
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public CompileObjectMapping() {
		init();
	}

	public void init() {
		ctxVarNames.put(Constants.InstanceVarName, Constants.InstanceVarValue);
		ctxVarNames.put(Constants.OutInstanceVarName, Constants.OutInstanceVarValue);
	}

	public void prepareFunctions(boolean isObject) {
	}

}