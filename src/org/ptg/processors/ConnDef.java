/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.util.List;

public class ConnDef {
	String id;
	String from;
	String to;
	String connCond;
	String []nodes;
    String ctype;
    String shape;
    String x;
    String y;
    String normalizedx;
    String normalizedy;
    String sequence;
    String attrib;
    double maxFlow;
    List<String> layer;
    List<String> tags ;
	boolean visible;
	

 	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getLayer() {
		return layer;
	}
	public void setLayer(List<String> layer) {
		this.layer = layer;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String[] getNodes() {
		return nodes;
	}
	public void setNodes(String[] nodes) {
		this.nodes = nodes;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getConnCond() {
		return connCond;
	}
	public void setConnCond(String cond) {
		this.connCond = cond;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getNormalizedx() {
		return normalizedx;
	}
	public void setNormalizedx(String normalizedx) {
		this.normalizedx = normalizedx;
	}
	public String getNormalizedy() {
		return normalizedy;
	}
	public void setNormalizedy(String normalizedy) {
		this.normalizedy = normalizedy;
	}
	@Override
	public String toString() {
		return "ConnDef {id=" + id + ":{" + from + "-> " + to + "}}";
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getAttrib() {
		return attrib;
	}
	public void setAttrib(String attrib) {
		this.attrib = attrib;
	}
	public double getMaxFlow() {
		return maxFlow;
	}
	public void setMaxFlow(double maxFlow) {
		this.maxFlow = maxFlow;
	}
	public String getDirection(String fromOrTo){
		if(from.equals(fromOrTo)){
			return "out";
		}else if( to.equals(fromOrTo)){
			return "in";
		}else {
			return "unk";
		}
		}
}
