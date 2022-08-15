/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.ptg.velocity.VelocityHelper;


public class NativeFormatter {
	public static String getString(String s) {
		return s;
	}

	public static boolean getBoolean(String s) {
		s = StringUtils.trim(s);
		boolean ret = false;
		try {
			ret = s.equals("1")?true:false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static float getFloat(String s) {
		s = StringUtils.trim(s);
		float ret = Float.MIN_VALUE;
		try {
			ret = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static double getDouble(String s) {
		s = StringUtils.trim(s);
		double ret = 0D;//Double.MIN_VALUE;
		try {
			ret = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Date getDate(String s) {
		s = StringUtils.trim(s);
		Date ret = null;
		try {
			ret = new Date(getLong(s));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static int getInt(String s) {
		s = StringUtils.trim(s);
		Integer ret = null;
		try {
			ret = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret.intValue();
	}

	public static long getLong(String s) {
		s = StringUtils.trim(s);
		Long ret = null;
		try {
			ret = Long.parseLong(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret.longValue();
	}
	public static Boolean getBooleanObject(String s) {
		s = StringUtils.trim(s);
		Boolean ret = false;
		try {
			ret = s.equals("1")?true:false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Float getFloatObject(String s) {
		s = StringUtils.trim(s);
		Float ret = 0F;//Float.MIN_VALUE;
		try {
			ret = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Double getDoubleObject(String s) {
		s = StringUtils.trim(s);
		Double ret = Double.MIN_VALUE;
		try {
			ret = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Integer getIntObject(String s) {
		s = StringUtils.trim(s);
		Integer ret = Integer.MIN_VALUE;
		try {
			ret = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Long getLongObject(String s) {
		s = StringUtils.trim(s);
		Long ret = Long.MIN_VALUE;
		try {
			ret = Long.parseLong(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}	
public static String toString(Object s){
	if (s==null) return null;
	if(CommonUtil.isBoolean(s.getClass().getCanonicalName())){
		return ((Boolean)s).booleanValue()==true?"1":"0";
	}else if (CommonUtil.isDate(s.getClass().getCanonicalName())){
		Date d = (Date)s;
		long l = d.getTime();
		return String.valueOf(l);
	}else{
		return s.toString();
	}
}
public static Object fromString(String type,String s){
	if (s==null) return null;
	if(CommonUtil.isBoolean(type)){
		return s.equals("1")?Boolean.TRUE:Boolean.FALSE;
	}else if (CommonUtil.isDate(type)){
		long l = getLong(s);
		return new Date(l);
	}else if (CommonUtil.isDouble(type)){
		if(s==null||s.length()==0)return 0;
		return getDouble(s);
	}else if (CommonUtil.isFloat(type)){
		if(s==null||s.length()==0)return 0;
		return getFloat(s);
	}else if (CommonUtil.isLong(type)){
		if(s==null||s.length()==0)return 0;
		return getLong(s);
	}else if (CommonUtil.isInt(type)){
		if(s==null||s.length()==0)return 0;
		return getInt(s);
	}else{
		return s;
	}
}	
public static Object getParseGetString(String idxstr,String type,String name){
	return getParseGetString(idxstr,type,name,false);
}
public static Object getParseGetString(String idxstr,String type,String name,boolean userRealName){
	String g = "get";
	String n = WordUtils.capitalize(name);
	String mc = Constants.FormatterObject+"."+g+n+"()";
	if(CommonUtil.isBoolean(type)){
		if(CommonUtil.isNativeBoolean(type))
			//return mc+"==true?\"1\":\"0\"";
		    return burnBranchTemplate(mc, "true", idxstr, "\"1\"", "\"0\"");
		else
			//return mc+".booleanValue()==true?\"1\":\"0\"";
			return burnBranchTemplate(mc+".booleanValue()", "true", idxstr, "\"1\"", "\"0\"");
	}else if (CommonUtil.isDate(type)){
		//return mc+"==null?null:String.valueOf("+mc+".getTime())";
		return burnBranchTemplate(mc,"null",idxstr,"null","String.valueOf("+mc+".getTime())");
	}else if (CommonUtil.isDouble(type)){
		//return "String.valueOf("+mc+")";
		return burnSimpleTemplate(idxstr, "String.valueOf("+mc+")");
	}else if (CommonUtil.isFloat(type)){
		//return "String.valueOf("+mc+")";
		return burnSimpleTemplate(idxstr, "String.valueOf("+mc+")");
	}else if (CommonUtil.isLong(type)){
		//return "String.valueOf("+mc+")";
		return burnSimpleTemplate(idxstr, "String.valueOf("+mc+")");
	}else if (CommonUtil.isInt(type)){
		//return "String.valueOf("+mc+")";
		return burnSimpleTemplate(idxstr, "String.valueOf("+mc+")");
	}else{
		return burnSimpleTemplate(idxstr, mc);
	}
}
public static Object getParseSetString(String type,String name,int index){
	return getParseSetString(type,name,index,false);
}
public static Object getParseSetString(String type,String name,int index,boolean useRealName){
	String g = "set";
	String n = WordUtils.capitalize(name);
	String mc = Constants.FormatterObject+"."+g+n;
	String ob = "(";
	String cb = ")";
	String stmt = Constants.RSObject;
	stmt +=".getString";
	String A = Constants.EventA;
	String colname = null;
	if(useRealName){
		colname = A+index;
	}else{
		colname = name;		
	}
	if(CommonUtil.isBoolean(type)){
		if(CommonUtil.isNativeBoolean(type)){
		return mc+ob+"org.ptg.util.NativeFormatter.getBoolean"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}else{
			return mc+ob+"org.ptg.util.NativeFormatter.getBooleanObject"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}
	}else if (CommonUtil.isDate(type)){
		return mc+ob+"org.ptg.util.NativeFormatter.getDate"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
	}else if (CommonUtil.isDouble(type)){
		if(CommonUtil.isNativeDouble(type)){
		return mc+ob+"org.ptg.util.NativeFormatter.getDouble"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}else{
			return mc+ob+"org.ptg.util.NativeFormatter.getDoubleObject"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}
	}else if (CommonUtil.isFloat(type)){
		if(CommonUtil.isNativeFloat(type)){
		return mc+ob+"org.ptg.util.NativeFormatter.getFloat"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}else{
		return mc+ob+"org.ptg.util.NativeFormatter.getFloatObject"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;			
		}
	}else if (CommonUtil.isLong(type)){
		if(CommonUtil.isNativeLong(type)){
		return mc+ob+"org.ptg.util.NativeFormatter.getLong"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}else{
		return mc+ob+"org.ptg.util.NativeFormatter.getLongObject"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}
	}else if (CommonUtil.isInt(type)){
		if(CommonUtil.isNativeInt(type)){
		return mc+ob+"org.ptg.util.NativeFormatter.getInt"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
		}else{
			return mc+ob+"org.ptg.util.NativeFormatter.getIntObject"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;	
		}
	}else{
		return mc+ob+"org.ptg.util.NativeFormatter.getString"+ob+stmt+ob+"\""+colname+"\""+cb+cb+cb;
	}
}	
public static String burnBranchTemplate(String check,String equals,String idxstr,String firstval,String secondval){
    String inFile = "branchtemplate.vm";
    Map map = new HashMap();
    map.put("condexpr",check );
    map.put("cond", equals);
    map.put("idxstr", idxstr);
    map.put("val1", firstval);
    map.put("val2", secondval);
    StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
    return s.toString();
}
public static String burnSimpleTemplate(String idxstr,String val1){
    String inFile = "simpletemplate.vm";
    Map map = new HashMap();
    map.put("idxstr", idxstr);
    map.put("val1", val1);
    StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
    return s.toString();
}
}
