/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.util.List;

public class ProcessorDef {
private String name;
private String clz;
private String query;
private String configItems;
int x;
int y;
int r;
int b;
List<String> tags ;
public List<String> getTags() {
	return tags;
}
public void setTags(List<String> tags) {
	this.tags = tags;
}

List<String> layer;
public List<String> getLayer() {
	return layer;
}
public void setLayer(List<String> layer) {
	this.layer = layer;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getClz() {
	return clz;
}
public void setClz(String clz) {
	this.clz = clz;
}
public String getQuery() {
	return query;
}
public void setQuery(String query) {
	this.query = query;
}
public String getConfigItems() {
	return configItems;
}
public void setConfigItems(String configItems) {
	this.configItems = configItems;
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

}
