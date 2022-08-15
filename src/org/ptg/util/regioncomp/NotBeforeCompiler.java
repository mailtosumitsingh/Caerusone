/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.regioncomp;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.SystemUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.GraphObjectCompiler;
import org.ptg.util.mapper.CompilePath;

public class NotBeforeCompiler implements GraphObjectCompiler {

	@Override
	public String getName() {
		return "CastRegionCompiler_1.0";
	}

	@Override
	public String compile(Map m) {
		String ret = null;
		CompilePath cp = (CompilePath) m.get("cp");
		String c = (String) m.get("c");
		String v = (String) m.get("v");
		JSONObject reg = (JSONObject) m.get("r");
		String temp ="";
		if(reg.containsKey("code")){
			temp = reg.getString("code");
			if(temp!=null){
				temp = StringEscapeUtils.unescapeJavaScript(temp);
				temp = CommonUtil.extractTextFromHtmlTitan(temp);
			}
		}
		ret = v + SystemUtils.LINE_SEPARATOR+temp+SystemUtils.LINE_SEPARATOR;
		return ret;
	}

}
