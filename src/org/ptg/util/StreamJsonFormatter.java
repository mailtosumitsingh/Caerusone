/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import org.apache.commons.lang.WordUtils;

public class StreamJsonFormatter {
	private static String fparam = "jsonObject";
public static String transformExpression(String prop, String xmlextra,String destProp,String srcType,String destType){
	return getPropExpr(prop, xmlextra,destProp,srcType,destType);
}
public static String getPropExpr(String prop, String xmlextra,String destProp,String srcType,String destType){
	String dest =WordUtils.capitalize(destProp);
	if(!CommonUtil.isNative(destType))
		return "e.set"+dest+"(("+(destType)+")"+fparam+".get("+"\""+prop+"\""+")"+");\n";
	else{
		String toNative = CommonUtil.getNonPrimitiveDestType(destType);
		return "e.set"+dest+"("+"new "+toNative+"("+fparam+".get("+"\""+prop+"\""+").toString()"+")"+"."+destType+"Value()"+");\n";
	}
}
}