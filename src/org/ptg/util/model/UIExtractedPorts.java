package org.ptg.util.model;

public class UIExtractedPorts {
private String grpid;
private String name;
private int index;
private String dtype = "int21";
private String label;
private String defaultVal;


public String getDefaultVal() {
	return defaultVal;
}
public void setDefaultVal(String defaultVal) {
	this.defaultVal = defaultVal;
}
public String getLabel() {
	return label;
}
public void setLabel(String label) {
	this.label = label;
}
public String getGrpid() {
	return grpid;
}
public void setGrpid(String grpid) {
	this.grpid = grpid;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
public UIExtractedPorts(String grpid, String name, int index) {
	this.grpid = grpid;
	this.name = name;
	this.index = index;
}
public String getDtype() {
	return dtype;
}
public void setDtype(String dtype) {
	this.dtype = dtype;
}

}
