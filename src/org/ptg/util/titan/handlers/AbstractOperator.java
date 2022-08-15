/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.titan.handlers;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.titan.IMacroHandler;
import org.ptg.util.titan.TitanCompiler;

public abstract class AbstractOperator implements IMacroHandler{

	@Override
	public String handle(String m,String para, String line, String all,int nestcount,TitanCompiler comp) {
		String currMacro = StringUtils.substringAfter(line,"/*{");
		currMacro = StringUtils.trim(currMacro);
		String []temp  = currMacro.split(" ");
		if(temp!=null&&temp.length!=0){
			currMacro = temp[0];
		}//parameters starts from temp[1];
		String []lines = para.split("\n");
		StringBuilder code = new StringBuilder();
		code.append("/*>*/\n");
		for(int j=1;j<lines.length-1;j++){
			code.append(lines[j]);
			code.append("\n");
		}
		code.append("/*<*/");
		return handle(currMacro,temp,code,nestcount,comp);
	}
	public abstract String handle(String macro, String[] paramsWithMacroAtZero,StringBuilder filteredCode,int nestcount,TitanCompiler comp);

}
