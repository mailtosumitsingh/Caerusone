package org.ptg.analyzer.model;

import java.util.LinkedHashMap;
import java.util.Map;

import cern.colt.Arrays;

public class Setter {
Map<String,String> params = new LinkedHashMap<String,String>();
String name;
boolean isStatic = true;
String returnType;
public Map<String, String> getParams() {
	return params;
}

public void setParams(Map<String, String> params) {
	this.params = params;
}

@Override
public String toString() {
	return "Setter [params=" + Arrays.toString(params.keySet().toArray(new String[0]))+",name= "+name + "]";
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public boolean isStatic() {
	return isStatic;
}

public void setStatic(boolean isStatic) {
	this.isStatic = isStatic;
}

public String getReturnType() {
	return returnType;
}

public void setReturnType(String returnType) {
	this.returnType = returnType;
}

}
