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

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.Constants;
import org.ptg.util.ICompileFunction;
import org.ptg.util.SpringHelper;
import org.ptg.util.SubGraph;
import org.ptg.util.functions.Concat;
import org.ptg.util.functions.ConcatWithComma;
import org.ptg.util.functions.ConstantFun;
import org.ptg.util.functions.EndCond;
import org.ptg.util.functions.ExpressionParser;
import org.ptg.util.functions.IfCond;
import org.ptg.util.functions.LatLonParser;
import org.ptg.util.functions.LatLonToGeoHash;
import org.ptg.util.functions.LookUp;
import org.ptg.util.functions.Remap;
import org.ptg.util.functions.ShapeToLatLon;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class CompileSubGraph extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;
	Map<String, ICompileFunction> functions = new LinkedHashMap<String, ICompileFunction>();
    Map<String,String> ctxVarNames =  new HashMap<String,String>();
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String run = request.getParameter("run");
		String mappingtype = "FreeSpring";
		String mainId = request.getParameter("mainTable");
		boolean isObject = request.getParameter("isobject")==null?false:true;
		prepareFunctions(isObject);
		if(mainId!=null && mainId.length()==0)
			mainId=null;
		Map<String, String> params = new HashMap<String, String>();

		try {
			SubGraph obj = getRowMapperInst(name,mainId, mappingtype, params,isObject);
			obj.setName(name);
			response.getWriter().print(CommonUtil.toJson(obj));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	public SubGraph getRowMapperInst(String name,String mainId, String mappingtype, Map<String, String> params,boolean isObject) {
		FPGraph2 o = CommonUtil.buildDesignMappingGraph2(name);
		//prepare teh type def
		List<TypeDefObj> types = o.getTypeDefs();
		Map<String,TypeDefObj> typeMap = new LinkedHashMap<String,TypeDefObj>();  
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		CommonUtil.getAnonFromUIModels(o, types, typeMap, anonCompMap);
		return rowMapperFromGraph(name,mainId, params, o,isObject);
	}
	public SubGraph rowMapperFromGraph(String name,String mainId, Map<String, String> params, FPGraph2 o,boolean isObject) {
		SubGraph g = CommonUtil.subGraphFromGraph(o);
		return g;
	}


	public CompileSubGraph() {
		init();
	}

	public void init() {
		functions.put("concat", new Concat());
		functions.put("latlon", new LatLonParser());
		functions.put("expression", new ExpressionParser());
		functions.put("geohash", new LatLonToGeoHash());
		functions.put("shapetolatlon", new ShapeToLatLon());
		functions.put("constant", new ConstantFun());
		functions.put("lookup", new LookUp());
		functions.put("if", new IfCond());
		functions.put("end", new EndCond());
		functions.put("AddressAppender", new ConcatWithComma());
		functions.put("concatWithComm", new ConcatWithComma());
		functions.put("Remap", new Remap());
		initCtxVars();
	}
	private void initCtxVars() {
		ctxVarNames.put(Constants.InstanceVarName,Constants.InstanceVarValue);
		ctxVarNames.put(Constants.OutInstanceVarName,Constants.OutInstanceVarValue);
	}

	public void prepareFunctions(boolean isObject) {
		for(Map.Entry<String,ICompileFunction>en: functions.entrySet()){
			ICompileFunction functionObj = en.getValue();
			functionObj.setContext(ctxVarNames);
			functionObj.isObjectMapper(isObject);
		}
	}

}