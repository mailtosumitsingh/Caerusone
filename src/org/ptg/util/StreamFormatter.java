/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.ptg.util.db.DBTransformerHelper;

public class StreamFormatter {
	private static String fparam = "$1";
	private static String fparam2 = "b";
	private static DBTransformerHelper dbutil = new DBTransformerHelper();

	public static String transformExpression(String atype, String prop, String xmlextra, String destProp, String srcType, String destType, int index) {
		if (atype.equals("property")) {
			return getPropExpr(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("body")) {
			return getBodyExpr(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("propertyXmlExpr")) {
			return getPropExprWithXmlEasy(prop, xmlextra, destProp, srcType, destType);
		}else if (atype.equals("propjexl")) {
			return getPropExprWithJexlEasy(prop, xmlextra, destProp, srcType, destType);
		}else if (atype.equals("bodyjexl")) {
			return getBodyExprWithJexlEasy(prop, xmlextra, destProp, srcType, destType);
		}else if (atype.equals("propjxpath")) {
			return getPropExprWithJXPathEasy(prop, xmlextra, destProp, srcType, destType);
		}else if (atype.equals("bodyjxpath")) {
			return getBodyExprWithJXPathEasy(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("bodyXmlExpr")) {
			return getBodyExprWithXmlEasy(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("objectprop")) {
			return getObjectExpr(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("sqlprop")) {
			return getResultsetExpr(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("mapprop")) {
			return getMapExpr(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("rowprop")) {
			return getRowExpr(prop, xmlextra, destProp, srcType, destType,index);
		} else if (atype.equals("rowlistprop")) {
			return getRowListExpr(prop, xmlextra, destProp, srcType, destType,index);
		} else if (atype.equals("propertyJsonPathExpr")) {
			return getPropExprWithJsonPathEasy(prop, xmlextra, destProp, srcType, destType);
		} else if (atype.equals("bodyJsonPathExpr")) {
			return getBodyExprWithJsonPathEasy(prop, xmlextra, destProp, srcType, destType);
		}else if (atype.equals("customcode")) {
			return xmlextra;
		} else if (atype.equals("httpprop")) {
			return getHTTPExpr(prop, xmlextra, destProp, srcType, destType);
		}
		
		
		
		return null;
	}

	public static String getPropExpr(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = WordUtils.capitalize(destProp);
		if (!CommonUtil.isNative(destType)) {
			return "System.out.println(\"now setting" + destProp + "\");" + "e.set" + dest + "((" + (destType) + ")" + fparam + ".getHeader(" + "\"" + prop + "\"" + ")" + ");\n";
		} else {
			dest = WordUtils.capitalize(destProp);
			String toNative = CommonUtil.getNonPrimitiveDestType(destType);
			return "e.set" + dest + "(" + "new " + toNative + "(" + fparam + ".getHeader(" + "\"" + prop + "\"" + ").toString()" + ")" + "." + destType + "Value()" + ");\n";
		}
	}

	public static String getBodyExpr(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = WordUtils.capitalize(destProp);
		return "e.set" + dest + "((" + destType + ")" + fparam + ".getBody(" + ")" + ");\n";
	}

	/* forseting object to object */
	public static String getObjectExpr(String prop, String xmlextra, String destProp, String srcType, String destType) {
		 String dest = WordUtils.uncapitalize(destProp);
		String set= "e.setEventProperty(\"" + dest+  "\",($w)" + fparam + ".getEventProperty" + "(\"" + prop + "\")" + ");";
		StringBuilder ret = new StringBuilder("{\n");
		ret.append("if("+fparam + ".getEventProperty(\"" + prop + "\")==null){}\n");
		ret.append("else { "+set+"}\n");
		ret.append("}\n");
		return ret.toString();
	}
	public static String getMapExpr(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String p = WordUtils.capitalize(destProp);
		if (CommonUtil.isNative(destType)) {
			String toNative = CommonUtil.getNonPrimitiveDestType(destType);
			String set= "e.set" + p + "(((" + toNative + ")" + fparam + ".get(\"" + prop + "\"))" + "." + destType + "Value()" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam + ".get(\"" + prop + "\")==null){}\n");
			ret.append("else {\n "+set+"\n}\n");
			ret.append("}\n");
			return ret.toString();
			
		} else {
			String set= "e.set" + p + "((" + destType + ")($w)" + fparam + ".get(\"" + prop + "\")" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam + ".get(\"" + prop + "\")==null){}\n");
			ret.append("else { "+set+"}\n");
			ret.append("}\n");
			return ret.toString();
	
		}
	}
	public static String getRowListExpr(String prop, String xmlextra, String destProp, String srcType, String destType,int index) {
		String p = WordUtils.capitalize(destProp);
		if (CommonUtil.isNative(destType)) {
			String toNative = CommonUtil.getNonPrimitiveDestType(destType);
			String set= "e.set" + p + "(((" + toNative + ")" + fparam + ".get(" + index + "))" + "." + destType + "Value()" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam + ".get(" + index + ")==null){}\n");
			ret.append("else {\n "+set+"\n}\n");
			ret.append("}\n");
			return ret.toString();
			
		} else {
			String set= "e.set" + p + "((" + destType + ")($w)" + fparam + ".get(" + index + ")" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam + ".get(" + index + ")==null){}\n");
			ret.append("else { "+set+"}\n");
			ret.append("}\n");
			return ret.toString();
	
		}
	}

	/* forseting object to object */
	public static String getRowExpr(String prop, String xmlextra, String destProp, String srcType, String destType,int index) {
		String p = WordUtils.capitalize(destProp);
		if (CommonUtil.isNative(destType)) {
			String toNative = CommonUtil.getNonPrimitiveDestType(destType);
			String set= "e.set" + p + "(((" + toNative + ")" + fparam + "[" + index + "])" + "." + destType + "Value()" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam + "[" + index + "]==null){}\n");
			ret.append("else {\n "+set+"\n}\n");
			ret.append("}\n");
			return ret.toString();
			
		} else {
			String set= "e.set" + p + "((" + destType + ")($w)" + fparam + "[" + index + "]" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam + "[" + index + "]==null){}\n");
			ret.append("else { "+set+"}\n");
			ret.append("}\n");
			return ret.toString();
	
		}
	}
	/* forseting object to object */
	public static String getHTTPExpr(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String p = WordUtils.capitalize(destProp);
		if (CommonUtil.isNative(destType)) {
			String toNative = CommonUtil.getNonPrimitiveDestType(destType);
			String set= "e.set" + p + "(("+toNative+".valueOf(((" + "String" + "[])" + fparam2 + ".get(\"" + prop + "\"))[0]))" + "." + destType + "Value()" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam2 + ".get(\"" + prop + "\")==null){}\n");
			ret.append("else {\n "+set+"\n}\n");
			ret.append("}\n");
			return ret.toString();
			
		} else {
			String set= "e.set" + p + "(((" + destType + "[])($w)" + fparam2 + ".get(\"" + prop + "\"))[0]" + ");";
			StringBuilder ret = new StringBuilder("{\n");
			ret.append("if("+fparam2 + ".get(\"" + prop + "\")==null){}\n");
			ret.append("else { "+set+"}\n");
			ret.append("}\n");
			return ret.toString();
	
		}
	}
	/* forseting resultset to object */
	public static String getResultsetExpr(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String p = WordUtils.capitalize(destProp);
		String dest = StringUtils.lowerCase(prop);
		String mtd = dbutil.DbToJavaMtdMap.get(srcType);
		if (CommonUtil.isNative(destType)) {
			return "e.set" + p + "(" + fparam + "." + mtd + "(" + "\"" + dest + "\"" + ")" + ");\n";
		} else {
			return "e.set" + p + "(("+destType+")($w)" + fparam + "." + mtd + "(" + "\"" + dest + "\"" + ")" + ");\n";

		}
	}

	public static String getPropExprWithXml(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = WordUtils.capitalize(destProp);
		return "e.set" + dest + "((" + destType + ")" + "CommonUtil.getXpathValue(" + fparam + ".getHeader(" + "\"" + prop + "\"" + ")," + xmlextra + ");\n";

	}

	public static String getBodyExprWithXml(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = WordUtils.capitalize(destProp);
		return "e.set" + dest + "((" + destType + ")" + "CommonUtil.getXpathValue(" + fparam + ".getBody(" + ")," + xmlextra + ");\n";

	}

	public static String getPropExprEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;// WordUtils.capitalize(destProp);
		if (!CommonUtil.isNative(destType)) {
			// return
			// " e.set"+dest+"(("+(destType)+")"+fparam+".getHeader("+"\""+prop+"\""+")"+");\n";
			return " e.setEventProperty(\"" + dest + "\"," + fparam + ".getHeader(" + "\"" + prop + "\"" + ")" + ");\n";

		} else {
			dest = WordUtils.capitalize(destProp);
			String toNative = CommonUtil.getNonPrimitiveDestType(destType);
			return "e.set" + dest + "(" + "new " + toNative + "(" + fparam + ".getHeader(" + "\"" + prop + "\"" + ").toString()" + ")" + "." + destType + "Value()" + ");\n";
		}
	}

	public static String getBodyExprEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;// WordUtils.capitalize(destProp);
		// return "e.set"+dest+"(("+destType+")"+fparam+".getBody("+")"+");\n";
		return " e.setEventProperty(\"" + dest + "\"," + fparam + ".getBody()" + ");\n";
	}

	public static String getPropExprWithXmlEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.getXpathValue(" + fparam + ".getHeader(" + "\"" + prop + "\"" + ")," + xmlextra + ");\n";
	}
	public static String getPropExprWithJexlEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.evalJexl(" + fparam + ".getHeader(" + "\"" + prop + "\"" + ")," + xmlextra + ");\n";
	}
	public static String getBodyExprWithJexlEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.evalJexl(" + fparam + ".getBody(" + ""+ ")," + xmlextra + ");\n";
	}
	public static String getPropExprWithJXPathEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.getJXPath(" + fparam + ".getHeader(" + "\"" + prop + "\"" + ")," + xmlextra + ");\n";
	}
	public static String getBodyExprWithJXPathEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.getJXPath(" + fparam + ".getBody(" + ""+ ")," + xmlextra + ");\n";
	}
	public static String getBodyExprWithXmlEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;// WordUtils.capitalize(destProp);
		// return
		// "e.set"+dest+"(("+destType+")"+"CommonUtil.getXpathValue("+fparam+".getBody("+"),"+xmlextra+");\n";
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.getXpathValue(" + fparam + ".getBody(" + ")," + xmlextra + ");\n";
	}
	public static String getPropExprWithJsonPathEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;// WordUtils.capitalize(destProp);
		// return
		// "e.set"+dest+"(("+destType+")"+"CommonUtil.getXpathValue("+fparam+".getHeader("+"\""+prop+"\""+"),"+xmlextra+");\n";
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.getJsonPathVal(" + fparam + ".getHeader(" + "\"" + prop + "\"" + ")," + xmlextra + ");\n";
	}

	public static String getBodyExprWithJsonPathEasy(String prop, String xmlextra, String destProp, String srcType, String destType) {
		String dest = destProp;// WordUtils.capitalize(destProp);
		// return
		// "e.set"+dest+"(("+destType+")"+"CommonUtil.getXpathValue("+fparam+".getBody("+"),"+xmlextra+");\n";
		return " e.setEventProperty(\"" + dest + "\"," + "CommonUtil.getJsonPathVal(" + fparam + ".getBody(" + ")," + xmlextra + ");\n";
	}
}
