package org.ptg.util.db;

import java.util.List;

public class TableDef {
String name;
String type= "TableDef";
String id ;
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
}
