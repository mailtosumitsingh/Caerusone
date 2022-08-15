package org.ptg.util.mapper;

import java.util.ArrayList;
import java.util.List;

public class TypeDefObj {
	String type;
	String id;
	String name;
	String extra;
	double x;
	double y;
	double r;
	double b;
List<String> tags ;
	

 	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	List<String> dtypes = new 	ArrayList<String> ();
	List<String> inputs = new 	ArrayList<String> ();
	 Object xref;
	 String anonType;
	 List<String> layer  ;
	 String configItems; 
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
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public List<String> getDtypes() {
		return dtypes;
	}
	public void setDtypes(List<String> dtypes) {
		this.dtypes = dtypes;
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
