package org.ptg.util;

public class TaskSpec implements Comparable<TaskSpec>{
String [] inputs;
String [] outputs;
String [] aux;
String anonType;
String dispName;

public String getDispName() {
	return dispName;
}
public void setDispName(String dispName) {
	this.dispName = dispName;
}
boolean isCustom = false;

public boolean isCustom() {
	return isCustom;
}
public void setCustom(boolean isCustom) {
	this.isCustom = isCustom;
}

public TaskSpec(String[] inputs, String[] outputs, String[] aux, String anonType, boolean isCustom, String dispName) {
	this.inputs = inputs;
	this.outputs = outputs;
	this.aux = aux;
	this.anonType = anonType;
	this.dispName = dispName;
	this.isCustom = isCustom;
}
public String[] getInputs() {
	return inputs;
}
public void setInputs(String[] inputs) {
	this.inputs = inputs;
}
public String[] getOutputs() {
	return outputs;
}
public void setOutputs(String[] outputs) {
	this.outputs = outputs;
}
public String[] getAux() {
	return aux;
}
public void setAux(String[] aux) {
	this.aux = aux;
}
public String getAnonType() {
	return anonType;
}
public void setAnonType(String anonType) {
	this.anonType = anonType;
}
@Override
public int compareTo(TaskSpec o) {
	return this.getDispName().compareTo(o.getDispName());
}

}
