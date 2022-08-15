package org.ptg.models;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Connector implements IPtContainer{
	private String id;
	private String connectortype;
	private String type;
	private String opacity;
	private String fill;
	private String stroke;
	private String visible;
	
	List<String> pts = Lists.newLinkedList();
	private Map<String,String>data=Maps.newHashMap();

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}


	@Override
	public boolean pointPartOf(String id) {
		for(String p: pts) {
			if(p.equals(id))return true;
		}
		return false;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getConnectortype() {
		return connectortype;
	}



	public void setConnectortype(String connectortype) {
		this.connectortype = connectortype;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getOpacity() {
		return opacity;
	}



	public void setOpacity(String opacity) {
		this.opacity = opacity;
	}



	public String getFill() {
		return fill;
	}



	public void setFill(String fill) {
		this.fill = fill;
	}



	public String getStroke() {
		return stroke;
	}



	public void setStroke(String stroke) {
		this.stroke = stroke;
	}



	public String getVisible() {
		return visible;
	}



	public void setVisible(String visible) {
		this.visible = visible;
	}



	public List<String> getPts() {
		return pts;
	}



	public void setPts(List<String> pts) {
		this.pts = pts;
	}


}
