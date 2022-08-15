package org.ptg.util.mapper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author maverick
 *
 */
public class AnonDefObj {
	String type;
	String id;
	String name;
	List<String> inputs = new LinkedList<String>();
	List<String> outputs= new LinkedList<String>();
	List<String> aux= new LinkedList<String>();
	 Object xref;
    String anonType;
    List<String> layer ;
    String configItems;
    int index;
	double x;
	double y;
	double r;
	double b;
	PortObj mainPort;
	List<String> tags ;
	

 	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public PortObj getMainPort() {
		return mainPort;
	}
	public void setMainPort(PortObj mainPort) {
		this.mainPort = mainPort;
	}
	public List<String> getLayer() {
		return layer;
	}
	public void setLayer(List<String> layer) {
		this.layer = layer;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public List<String> getAux() {
		return aux;
	}
	public void setAux(List<String> aux) {
		this.aux = aux;
	}
	public Object getXref() {
		return xref;
	}
	public void setXref(Object xref) {
		this.xref = xref;
	}
	public String getAnonType() {
		return anonType;
	}
	public void setAnonType(String anonType) {
		this.anonType = anonType;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public String toString() {
		return "AnonDefObj [type=" + type + ", id=" + id + ", name=" + name + "]";
	}
	public String getConfigItems() {
		return configItems;
	}
	public void setConfigItems(String configItems) {
		this.configItems = configItems;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getR() {
		return r;
	}
	public void setR(double r) {
		this.r = r;
	}
	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	}
	
}
