/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.stream;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class StreamDefinition  implements Externalizable{
	private String name;
	private String type;
	private int streamType;
	private  int id;
	String accessor;
	String extra;
	String xmlExpr;
	String destProp;
	int index;
	public String getXmlExpr() {
		return xmlExpr;
	}
	public void setXmlExpr(String xmlExpr) {
		this.xmlExpr = xmlExpr;
	}
	public String getDestProp() {
		return destProp;
	}
	public void setDestProp(String destProp) {
		this.destProp = destProp;
	}
	public String getAccessor() {
		return accessor;
	}
	public void setAccessor(String accessor) {
		this.accessor = accessor;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
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
	public int getStreamType() {
		return streamType;
	}
	public void setStreamType(int eventType) {
		this.streamType = eventType;
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
		streamType = in.readInt();
		id = in.readInt();
		accessor = in.readUTF();
		extra = in.readUTF();
		xmlExpr = in.readUTF();
		destProp = in.readUTF();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(name==null?"":name);
		out.writeUTF(type==null?"":type);
		out.writeInt(streamType);
		out.writeInt(id);
		out.writeUTF(accessor==null?"":accessor);
		out.writeUTF(extra==null?"":extra);
		out.writeUTF(xmlExpr==null?"":xmlExpr);
		out.writeUTF(destProp==null?"":destProp);
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

}