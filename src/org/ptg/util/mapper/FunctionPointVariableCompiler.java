/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.mapper;

import org.apache.commons.lang.WordUtils;
import org.ptg.processors.ConnDef;

public class FunctionPointVariableCompiler {
	public static String compileFunctionPointVariable(FunctionPoint lp, FunctionPoint fp,ConnDef currConn) {
		if (lp == null) {
			if (fp.getVal() == null || fp.getVal().length() < 1) {
				return ( fp.getDataType() + " " + fp.getName() + " = " + fp.getCn() + "." + "get" + WordUtils.capitalize(fp.getPn()) + "();\n");
			} else {
				String temp = fp.getCn() + "." + "set" + WordUtils.capitalize(fp.getPn()) + "(" +"("+fp.getDataType()+") " + fp.getVal() + ");\n";
				temp += (fp.getDataType() + " " + fp.getName() + " = "+"("+fp.getDataType()+") " + fp.getCn() + "." + "get" + WordUtils.capitalize(fp.getPn()) + "();\n");
				return ( temp);
			}
		} else {// calling a method directly wthout a parameter
			// call
			String temp = null;
			if (fp.getVal() == null || fp.getVal().length() < 1) {
				if(currConn !=null && currConn.getConnCond()!=null && currConn.getConnCond().length()>0)
					temp = fp.getCn() + "." + "set" + WordUtils.capitalize(fp.getPn()) + "(" +"("+fp.getDataType()+") "+ lp.getName()+currConn.getConnCond() + ");\n";
				else
					temp = fp.getCn() + "." + "set" + WordUtils.capitalize(fp.getPn()) + "(" +"("+fp.getDataType()+") "+ lp.getName() + ");\n";
			temp += fp.getDataType() + " " + fp.getName() + " = "+"("+fp.getDataType()+") " + fp.getCn() + "." + "get" + WordUtils.capitalize(fp.getPn()) + "();\n";
			}else{
				temp = fp.getCn() + "." + "set" + WordUtils.capitalize(fp.getPn()) + "(" +"("+fp.getDataType()+") "+ fp.getVal() + ");\n";
				temp += fp.getDataType() + " " + fp.getName() + " = "+"("+fp.getDataType()+") " + fp.getCn() + "." + "get" + WordUtils.capitalize(fp.getPn()) + "();\n";
					
			}
			return (temp);
		}
	}

}
