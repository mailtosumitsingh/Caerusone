package org.ptg.util.mapper;

import java.util.List;

public class LayerObj {
	String type;
	String id;
	String name;
	String grp;
	 Object xref;
	 List<String> tags;
	String layername;	
	List<String> items;
	boolean visible;
	
	public List<String> getItems() {
	return items;
}
public void setItems(List<String> items) {
	this.items = items;
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
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getLayername() {
		return layername;
	}
	public void setLayername(String layername) {
		this.layername = layername;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
