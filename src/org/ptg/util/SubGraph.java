package org.ptg.util;

import java.util.List;

public class SubGraph {
private String name;
List<String> inputPorts;
List<String> outputPorts;
List<String> auxPorts;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public List<String> getInputPorts() {
	return inputPorts;
}
public void setInputPorts(List<String> inputPorts) {
	this.inputPorts = inputPorts;
}
public List<String> getOutputPorts() {
	return outputPorts;
}
public void setOutputPorts(List<String> outputPorts) {
	this.outputPorts = outputPorts;
}
public List<String> getAuxPorts() {
	return auxPorts;
}
public void setAuxPorts(List<String> auxPorts) {
	this.auxPorts = auxPorts;
}

}
