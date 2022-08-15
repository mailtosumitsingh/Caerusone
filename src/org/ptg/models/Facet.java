package org.ptg.models;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class Facet implements IPtContainer{

	private String id;
	private List<Connector> connectors =  Lists.newLinkedList();
	private String type;
	private String opacity;
	private String fill;
	private String stroke;
	private String visible;
	private Map<String,String>data=Maps.newHashMap();

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public List<Connector> getConnectors() {
		return connectors;
	}

	public void addConnector(Connector connector) {
		this.connectors.add(connector);
	}
	public void setConnectors(List<Connector> connectors) {
		this.connectors = connectors;
	}

	@Override
	public boolean pointPartOf(String id) {
		for(Connector c : connectors) {
			if(c.pointPartOf(id))return true;
		}
		return false;
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


}
