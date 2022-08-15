package org.ptg.util.mapper;

import java.util.List;

public class StepObj {
	 int x;
	 int y;
	 int r;
	 int b;
	
	 List<String> layer  ;
	 String text;
	 String type;
	 Object xref;
	 String steptype;//horiz, vert
	 String id;
	 int index;
	 List<String> tags;
	 String cont;//container type: explicit, implicit;
	 public enum ContainmentType{
		 explicit,implicit;
	 }
	 
	 public String getCont() {
		return cont;
	}
	public void setCont(String cont) {
		this.cont = cont;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		StepObj other = (StepObj) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public enum StepType{
		 horiz,vert;
	 }
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSteptype() {
		return steptype;
	}
	public void setSteptype(String steptype) {
		this.steptype = steptype;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}	
	/*"type" : "Step",
	"text" : txt,
	"x" : bb.x,
	"y" : bb.y,
	"r" : bb.width,
	"b" : bb.height,
	"normalizedx" : bb.x,
	"normalizedy" : bb.y,
	"id" : "word_" + myid,
    "index":index,
    "steptype":"horiz"*/
}
