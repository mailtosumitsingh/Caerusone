/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.events;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DeployEvent  extends org.ptg.events.Event{
String name;
String type;
String dest;
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
public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	super.readExternal(in);
	name = in.readUTF();
	type = in.readUTF();
	dest = in.readUTF();
}

public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeUTF(name);
	out.writeUTF(type);
	out.writeUTF(dest);
}
public String getDest() {
	return dest;
}
public void setDest(String dest) {
	this.dest = dest;
}
public DeployEvent(String name, String type, String dest) {
	super();
	this.name = name;
	this.type = type;
	this.dest = dest;
}
public DeployEvent() {
	super();
}


 
}