package org.ptg.util.db;

import java.util.List;

public class ColDef {
String name;
String type = "ColDef";
String id ;
String dataType;
String colOp; //insert/query/update
int order;
String grp;
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

public String getDataType() {
	return dataType;
}

public void setDataType(String dataType) {
	this.dataType = dataType;
}

public String getColOp() {
	return colOp;
}

public void setColOp(String colOp) {
	this.colOp = colOp;
}

public int getOrder() {
	return order;
}

public void setOrder(int order) {
	this.order = order;
}

public String getGrp() {
	return grp;
}

public void setGrp(String grp) {
	this.grp = grp;
}

}
