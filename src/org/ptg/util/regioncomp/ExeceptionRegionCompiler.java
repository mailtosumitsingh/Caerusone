/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.regioncomp;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.GraphObjectCompiler;
import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.FunctionPointFunctionCompiler;

public class ExeceptionRegionCompiler implements GraphObjectCompiler {

	@Override
	public String getName() {
		return "ExeceptionRegionCompiler_1.0";
	}

	@Override
	public String compile(Map m) {
		Map<String, List<FunctionPoint>> grps =(Map<String, List<FunctionPoint>>) m.get("grps");
		CompilePath cp = (CompilePath) m.get("cp");
		FunctionPoint fp = (FunctionPoint) m.get("fp");
		JSONObject reg = (JSONObject) m.get("r");
		String []lhsrhs =  FunctionPointFunctionCompiler.compileFunctionPointFunction(grps, cp, fp,null);
		
		String temp  = null;
		if(reg.containsKey("code"))
			temp = reg.getString("code");
		if(temp!=null){
			temp = CommonUtil.extractTextFromHtmlTitan(temp);
			temp = StringEscapeUtils.unescapeJavaScript(temp);
		}
		String ret = (lhsrhs[0]+" = null;\n"); 
		ret +="try{\n";
		ret+=("\t "+fp.getName()+" = "+lhsrhs[1]+"\n");
		ret+="}\ncatch(java.lang.Throwable t){\n t.printStackTrace();\n";
		if(temp!=null)
			ret +=temp;
		ret +="\n}\n";
		return ret;
	}

}
