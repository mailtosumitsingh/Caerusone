package org.ptg.util;

import java.util.List;

public class SchemaInfo {
	List<String>  inputs;
	List<String>  outputs;
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getInputs() {
		return inputs;
	}
	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}
	public List<String> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}
	
}
