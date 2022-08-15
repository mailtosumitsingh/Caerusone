/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CompilePath {
FunctionPoint start;
FunctionPoint end;
List<String> vars;
Map<String,String> code;
Collection<FunctionPoint>deps;
String id;
public CompilePath(){
	//id = CommonUtil.getRandomString(16);
}
public FunctionPoint getStart() {
	return start;
}
public void setStart(FunctionPoint start) {
	this.start = start;
}
public FunctionPoint getEnd() {
	return end;
}
public void setEnd(FunctionPoint end) {
	this.end = end;
}
public List<String> getVars() {
	return vars;
}
public void setVars(List<String> vars) {
	this.vars = vars;
}
public Map<String, String> getCode() {
	return code;
}
public void setCode(Map<String, String> code) {
	this.code = code;
}
public Collection<FunctionPoint> getDeps() {
	return deps;
}
public void setDeps(Collection<FunctionPoint> deps) {
	this.deps = deps;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}

}
