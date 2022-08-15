package org.ptg.util.mapper;

import java.util.ArrayList;
import java.util.List;

public class PortObj {
	String type;
	String id;
	String name;
	String portname;
	String porttype;
	String grp;
	String dtype;
	String portval;
	/*single,list,map,enum*/
	String occurance;
	List<String> tags ;
	int portindex;
	public enum Location{
		N,S,E,W
	}
	Location l;
   
 	public Location getL() {
		return l;
	}
	public void setL(Location l) {
		this.l = l;
	}
	public int getPortindex() {
		return portindex;
	}
	public void setPortindex(int portindex) {
		this.portindex = portindex;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	Object xref;
     List<String> layer  = new ArrayList<String>();
 	double x;
	double y;
	double r;
	double b;
	private String codeToReadValue;
	private String codetoWriteValue;

	
     public String getCodeToReadValue() {
		return codeToReadValue;
	}
	public void setCodeToReadValue(String codeToReadValue) {
		this.codeToReadValue = codeToReadValue;
	}
	public String getCodetoWriteValue() {
		return codetoWriteValue;
	}
	public void setCodetoWriteValue(String codetoWriteValue) {
		this.codetoWriteValue = codetoWriteValue;
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
	public String getPortname() {
		return portname;
	}
	public void setPortname(String portname) {
		this.portname = portname;
	}
	public String getPorttype() {
		return porttype;
	}
	public void setPorttype(String porttype) {
		this.porttype = porttype;
	}
	public String getGrp() {
		return grp;
	}
	public void setGrp(String grp) {
		this.grp = grp;
	}
	public Object getXref() {
		return xref;
	}
	public void setXref(Object xref) {
		this.xref = xref;
	}
	public List<String> getLayer() {
		return layer;
	}
	public void setLayer(List<String> layer) {
		this.layer = layer;
	}
	public String getDtype() {
		return dtype;
	}
	public void setDtype(String dtype) {
		this.dtype = dtype;
	}
	public String getPortval() {
		return portval;
	}
	public void setPortval(String portval) {
		this.portval = portval;
	}
	public String getOccurance() {
		return occurance;
	}
	public void setOccurance(String occurance) {
		this.occurance = occurance;
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
	@Override
	public String toString() {
		return "PortObj [id=" + id + "]";
	}

	
 
}
