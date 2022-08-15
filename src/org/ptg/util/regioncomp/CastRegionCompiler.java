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
import org.ptg.util.mapper.FunctionPointFunctionParamCompiler;
import org.ptg.util.mapper.FunctionPointVariableCompiler;

public class CastRegionCompiler implements GraphObjectCompiler {

	@Override
	public String getName() {
		return "CastRegionCompiler_1.0";
	}

	@Override
	public String compile(Map m) {
		Map<String, List<FunctionPoint>> grps =(Map<String, List<FunctionPoint>>) m.get("grps");
		CompilePath cp = (CompilePath) m.get("cp");
		FunctionPoint fp = (FunctionPoint) m.get("fp");
		FunctionPoint lp = (FunctionPoint) m.get("lp");
		JSONObject reg = (JSONObject) m.get("r");
		final String dt = fp.getDataType();
		String temp  = dt;
		if(reg.containsKey("code")){
			temp = reg.getString("code");
			if(temp!=null){
				temp = StringEscapeUtils.unescapeJavaScript(temp);
				temp = CommonUtil.extractTextFromHtmlTitan(temp);
			}else{
				temp =dt;
			}
		}
		
		fp.setDataType(temp);
		String ret = null;
		if ((fp.getPn() == null || fp.getPn().length() < 1) && fp.getFn() != null) {
		String []lhsrhs =  FunctionPointFunctionCompiler.compileFunctionPointFunction(grps, cp, fp,null);
		ret =(lhsrhs[0]+" = "+lhsrhs[1]+";\n"); 
		}else if (fp.getPn() != null && (fp.getFn() == null || fp.getFn().length() < 1)) {
		ret  = FunctionPointVariableCompiler.compileFunctionPointVariable(null, fp,null);
		}else if ((fp.getPn() != null || fp.getPn().length() > 0) && (fp.getFn() != null || fp.getFn().length() > 0)) {
			ret = FunctionPointFunctionParamCompiler.compileFunctionPointFunctionParam(lp, fp,null);
		}
		fp.setDataType(dt);
		return ret;
	}

}
