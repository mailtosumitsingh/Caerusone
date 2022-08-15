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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Stream implements Externalizable{
	Map<String,StreamDefinition> defs = new LinkedHashMap<String,StreamDefinition>();
	String name;
	String eventType;
	String processor;
	int seda ;
	int id;
	String extra;
	String exceptionStream;
    private transient int sdMaxIndex=0;
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

	/*version is transient in nature maintaied during runtime only*/
	int version;
	List<String> layer;
	
	
	public List<String> getLayer() {
		return layer;
	}

	public void setLayer(List<String> layer) {
		this.layer = layer;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeda() {
		return seda;
	}

	public void setSeda(int seda) {
		this.seda = seda;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	
	public Map<String, StreamDefinition> getDefs() {
		return defs;
	}

	public void setDefs(Map<String, StreamDefinition> defs) {
		this.defs = defs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		name  = in.readUTF();
		eventType = in.readUTF();
		processor = in.readUTF();
		seda = in.readInt();
		id = in.readInt();
		int propsCount  = in.readInt();
		for(int i = 0 ; i< propsCount; i++){
			String key = in.readUTF();
			StreamDefinition prop = new StreamDefinition();
			prop.readExternal(in);
			defs.put(key, prop);
		}
		extra = in.readUTF();
		exceptionStream = in.readUTF();
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(name==null?"":name);
		out.writeUTF(eventType==null?"eventType":eventType);
		out.writeUTF(processor==null?"":processor);
		out.writeInt(seda);
		out.writeInt(id);
		out.writeInt(defs.size());
		for(Map.Entry<String, StreamDefinition>en: defs.entrySet()){
			out.writeUTF(en.getKey()==null?"":en.getKey());
			en.getValue().writeExternal(out);
		}
		out.writeUTF(extra==null?"":extra);
		out.writeUTF(exceptionStream==null?"":exceptionStream);
	}

	public String getExceptionStream() {
		return exceptionStream;
	}

	public void setExceptionStream(String exceptionStream) {
		this.exceptionStream = exceptionStream;
	}

	@Override
	public String toString() {
		return "Stream [name=" + name + "]";
	}

	public void setSdMaxIndex(int sdMaxIndex) {
		this.sdMaxIndex = sdMaxIndex;
	}

	public int getSdMaxIndex() {
		return sdMaxIndex;
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
