/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.mapper;

import org.ptg.processors.ConnDef;

public class FunctionPointFunctionParamCompiler {
	public static String compileFunctionPointFunctionParam( FunctionPoint lp, FunctionPoint fp,ConnDef currConn) {
		if (lp == null) {
			// this is error //todo :do something
			System.out.println("Error how can this happen!!!!!!!!!!!!!");
			if (fp.getVal() == null || fp.getVal().length() < 1) {
				return (fp.getDataType() + " " + fp.getName() + " = ("+fp.getDataType()+")" + "null" + ";\n");
			} else {
				return (fp.getDataType() + " " + fp.getName() + " = ("+fp.getDataType()+")" + fp.getVal() + ";\n");
			}
		} else {// calling a method directly wthout a parameter
				// call
			if (fp.getVal() == null || fp.getVal().length() < 1) {
				if(currConn !=null && currConn.getConnCond()!=null && currConn.getConnCond().length()>0)
					return (fp.getDataType() + " " + fp.getName() + " = ("+fp.getDataType()+")" + lp.getName()+currConn.getConnCond() + ";\n");
				else
					return (fp.getDataType() + " " + fp.getName() + " = ("+fp.getDataType()+")" + lp.getName() + ";\n");
			} else {
				return (fp.getDataType() + " " + fp.getName() + " = ("+fp.getDataType()+")" + fp.getVal() + ";\n");
			}
		}
	}
}
