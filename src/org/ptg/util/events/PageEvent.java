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

public class PageEvent  extends org.ptg.events.Event{
String name;
String content;
String dest;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	super.readExternal(in);
	name = in.readUTF();
	content = in.readUTF();
	dest = in.readUTF();
}

public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeUTF(name);
	out.writeUTF(content);
	out.writeUTF(dest);
}
public String getDest() {
	return dest;
}
public void setDest(String dest) {
	this.dest = dest;
}


 
}