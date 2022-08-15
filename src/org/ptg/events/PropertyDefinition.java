/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.events;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PropertyDefinition  implements Externalizable{
private String name;
private String type;
private String dataStructure="Object";//possible values Map,List,Collection,Object
private int eventType;
private int index;
public String getDataStructure() {
	return dataStructure;
}
public void setDataStructure(String dataStructure) {
	this.dataStructure = dataStructure;
}

private int searchable;
private  int id;
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
public int getEventType() {
	return eventType;
}
public void setEventType(int eventType) {
	this.eventType = eventType;
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
public int getSearchable() {
	return searchable;
}
public void setSearchable(int searchable) {
	this.searchable = searchable;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}

public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	name  = in.readUTF();
	type = in.readUTF();
	eventType = in.readInt();
	index = in.readInt();
	searchable = in.readInt();
	id = in.readInt();
	dataStructure = in.readUTF();
}

public void writeExternal(ObjectOutput out) throws IOException {
	out.writeUTF(name==null?"":name);
	out.writeUTF(type==null?"":type);
	out.writeInt(eventType);
	out.writeInt(index);
	out.writeInt(searchable);
	out.writeInt(id);
	out.writeUTF(dataStructure==null?"":dataStructure);
}

}
