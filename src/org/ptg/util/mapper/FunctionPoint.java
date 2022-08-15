/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
package org.ptg.util.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionPoint {
 String text;
 String cn;
 String fn;
 String pn;
 String type;//classobj,functionobj,paramobj
 String dataType;
 int index=-10;
 String grp;//group of nodes it belongs to
 String val;
 String classType;
 Object xref;
 List<String> layer  ;
 int x;
 int y;
 int r;
 int b;

	List<String> tags ;
	public List<String> getLayer() {
		return layer;
	}
	public void setLayer(List<String> layer) {
		this.layer = layer;
	}

 Map<String,Object> symbolTable = new HashMap<String,Object>();
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
public String getDataType() {
	return dataType;
}
public void setDataType(String dataType) {
	this.dataType = dataType;
}
@Override
public String toString() {
	return name;
}
String name;
public String getId() {
	return name;
}
public void setId(String id) {
	this.name = id;
}
public String getText() {
	return text;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public void setText(String text) {
	this.text = text;
}
public String getCn() {
	return cn;
}
public void setCn(String cn) {
	this.cn = cn;
}
public String getFn() {
	return fn;
}
public void setFn(String fn) {
	this.fn = fn;
}
public String getPn() {
	return pn;
}
public void setPn(String pn) {
	this.pn = pn;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getGrp() {
	return grp;
}
public void setGrp(String grp) {
	this.grp = grp;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((cn == null) ? 0 : cn.hashCode());
	result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
	result = prime * result + ((fn == null) ? 0 : fn.hashCode());
	result = prime * result + ((grp == null) ? 0 : grp.hashCode());
	result = prime * result + index;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((pn == null) ? 0 : pn.hashCode());
	result = prime * result + ((text == null) ? 0 : text.hashCode());
	result = prime * result + ((type == null) ? 0 : type.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	FunctionPoint other = (FunctionPoint) obj;
	if (cn == null) {
		if (other.cn != null)
			return false;
	} else if (!cn.equals(other.cn))
		return false;
	if (dataType == null) {
		if (other.dataType != null)
			return false;
	} else if (!dataType.equals(other.dataType))
		return false;
	if (fn == null) {
		if (other.fn != null)
			return false;
	} else if (!fn.equals(other.fn))
		return false;
	if (grp == null) {
		if (other.grp != null)
			return false;
	} else if (!grp.equals(other.grp))
		return false;
	if (index != other.index)
		return false;
	if (name == null) {
		if (other.name != null)
			return false;
	} else if (!name.equals(other.name))
		return false;
	if (pn == null) {
		if (other.pn != null)
			return false;
	} else if (!pn.equals(other.pn))
		return false;
	if (text == null) {
		if (other.text != null)
			return false;
	} else if (!text.equals(other.text))
		return false;
	if (type == null) {
		if (other.type != null)
			return false;
	} else if (!type.equals(other.type))
		return false;
	return true;
}
public String getVal() {
	return val;
}
public void setVal(String val) {
	this.val = StringEscapeUtils.unescapeJavaScript(val);
}
public String getClassType() {
	return classType;
}
public void setClassType(String classType) {
	this.classType = classType;
}
public Object getXref() {
	return xref;
}
public void setXref(Object xref) {
	this.xref = xref;
}
public Map<String, Object> getSymbolTable() {
	return symbolTable;
}
public void setSymbolTable(Map<String, Object> symbolTable) {
	this.symbolTable = symbolTable;
}
public int getX() {
	return x;
}
public void setX(int x) {
	this.x = x;
}
public int getY() {
	return y;
}
public void setY(int y) {
	this.y = y;
}
public int getR() {
	return r;
}
public void setR(int r) {
	this.r = r;
}
public int getB() {
	return b;
}
public void setB(int b) {
	this.b = b;
}
public List<String> getTags() {
	return tags;
}
public void setTags(List<String> tags) {
	this.tags = tags;
}
}