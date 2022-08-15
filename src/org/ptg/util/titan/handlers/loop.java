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

public class loop implements IMacroHandler{

	@Override
	public String handle(String m,String para, String line, String all,int nestcount,TitanCompiler comp) {
		String currMacro = StringUtils.substringAfter(line,"/*{");
		currMacro = StringUtils.trim(currMacro);
		String []temp  = currMacro.split(" ");
		if(temp!=null&&temp.length!=0){
			currMacro = temp[0];
		}//parameters starts from temp[1];
		System.out.println(para);
		Map <String,Object> prop =new HashMap<String,Object>();
		int i = Integer.parseInt(StringUtils.trim(temp[1]));
		int i2 = Integer.parseInt(StringUtils.trim(temp[2]));
		prop.put("start", temp[1]);
		prop.put("end", temp[2]);
		if(temp.length==4){
			if(i<i2){
				prop.put("counter", " +  "+temp[3]);
				}else{
					prop.put("counter", " -  "+temp[3]);
					
				}
		
		}else{
			if(i<i2){
				prop.put("counter", " + 1 ");
				}else{
					prop.put("counter", " - 1 ");
				}
		}
		
		String []lines = para.split("\n");
		StringBuilder code = new StringBuilder();
		code.append("/*>*/\n");
		for(int j=1;j<lines.length-1;j++){
			code.append(lines[j]);
			code.append("\n");
		}
		prop.put("loopvar", "i"+nestcount);
		code.append("/*<*/");
		prop.put("code", code.toString());
		return VelocityHelper.burnTemplate(prop,"titanloop.vm").toString();
	}

}
