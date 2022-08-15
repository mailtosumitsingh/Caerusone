/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.titan.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.titan.IMacroHandler;
import org.ptg.util.titan.TitanCompiler;
import org.ptg.velocity.VelocityHelper;

public class oloop implements IMacroHandler{

	@Override
	public String handle(String m,String para, String line, String all,int nestcount,TitanCompiler comp) {
		String currMacro = StringUtils.substringAfter(line,"/*{");
		currMacro = StringUtils.trim(currMacro);
		String []temp  = StringUtils.split(currMacro," ");
		if(temp!=null&&temp.length!=0){
			currMacro = temp[0];
		}//parameters starts from temp[1];
		System.out.println(para);
		StringBuilder code = new StringBuilder();
		code.append("/*>*/\n");
		Map <String,Object> prop =new HashMap<String,Object>();
	    code.append("java.util.List<"+temp[2]+"> l = new ArrayList<"+temp[2]+">();\n");	
	    String stemp = "";
		String varname = temp[1];
		String vartype=temp[2];
		stemp = StringUtils.substringAfter(line,"/*{");
		stemp=StringUtils.substringAfter(stemp,":");
	
		temp = StringUtils.split(stemp,",");
	    for(int i=0;i<temp.length;i++){
			code.append("l.add("+temp[i]+");\n");
		}
		code.append("for( "+vartype+" "+varname+" : l ) {\n");
		String []lines = para.split("\n");
		for(int j=1;j<lines.length-1;j++){
			code.append(lines[j]);
			if(j!=lines.length-2){
				code.append("\n");
			}
		}
		code.append("}\n");
		code.append("/*<*/");
		prop.put("code", code.toString());
		return VelocityHelper.burnTemplate(prop,"titanoloop.vm").toString();
	}

}
