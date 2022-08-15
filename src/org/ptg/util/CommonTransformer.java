/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class CommonTransformer {
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
		double ret = Double.MIN_VALUE;
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
		int ret = Integer.MIN_VALUE;
		try {
			ret = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static long getLong(String s) {
		s = StringUtils.trim(s);
		long ret = Long.MIN_VALUE;
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
		return getDouble(s);
	}else if (CommonUtil.isFloat(type)){
		return getFloat(s);
	}else if (CommonUtil.isLong(type)){
		return getLong(s);
	}else if (CommonUtil.isInt(type)){
		return getInt(s);
	}else{
		return null;
	}
}
public static Object castSafe(Object s, String type){
	if (s==null) return null;
	if(s.getClass().getCanonicalName().equals(type))
			return s;
	if(CommonUtil.isBoolean(s.getClass().getCanonicalName())){//boolean object
		if(CommonUtil.isBoolean(type))
			return s;
		if(CommonUtil.isInt(type)||CommonUtil.isLong(type))
			return ((Boolean)s).booleanValue()==true?1:0;
		if(CommonUtil.isFloat(type)||CommonUtil.isDouble(type))
			return ((Boolean)s).booleanValue()==true?1.0f:0.0f;
		if(CommonUtil.isString(type))
			return ((Boolean)s).booleanValue()==true?"1":"0";
	}else if (CommonUtil.isDate(s.getClass().getCanonicalName())){
		Date d = (Date)s;
		long l = d.getTime();
		if(CommonUtil.isInt(type)||CommonUtil.isLong(type))
			return l;
		if(CommonUtil.isFloat(type)||CommonUtil.isDouble(type))
			return l;
		if(CommonUtil.isString(type))
			return String.valueOf(l);
	}else if (CommonUtil.isInt(s.getClass().getCanonicalName())||CommonUtil.isLong(s.getClass().getCanonicalName())){
		if(CommonUtil.isBoolean(type))
			return ((Integer)s)==1?Boolean.TRUE:Boolean.FALSE;
		if(CommonUtil.isInt(type)||CommonUtil.isLong(type))
			return s;
		if(CommonUtil.isFloat(type)||CommonUtil.isDouble(type))
			return Float.valueOf(s.toString());
		if(CommonUtil.isString(type))
			return s.toString();
		
	}else if (CommonUtil.isFloat(s.getClass().getCanonicalName())||CommonUtil.isDouble(s.getClass().getCanonicalName())){
		if(CommonUtil.isBoolean(type))
			return ((Double)Double.valueOf(s.toString()))==1.0?Boolean.TRUE:Boolean.FALSE;
		if(CommonUtil.isInt(type)||CommonUtil.isLong(type))
			return Double.valueOf(s.toString()).intValue();
		if(CommonUtil.isFloat(type)||CommonUtil.isDouble(type))
			return Float.valueOf(s.toString());
		if(CommonUtil.isString(type))
			return s.toString();
		
	}else{
		return s.toString();
	}
	return null;
}
}
