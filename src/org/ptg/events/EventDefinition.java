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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventDefinition  implements Externalizable{
	Map<String, PropertyDefinition> props = new LinkedHashMap<String,PropertyDefinition>();
	Map<String, PropertyDefinition> propsReverse = new LinkedHashMap<String,PropertyDefinition>();
  private int id;
  private String type;
  private String eventStore;
  private boolean dirty;
  List<String> tags ;

	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

public boolean isDirty() {
	return dirty;
}
public void setDirty(boolean dirty) {
	this.dirty = dirty;
}
public String getEventStore() {
	return eventStore;
}
public void setEventStore(String eventStore) {
	this.eventStore = eventStore;
}
public Map<String, PropertyDefinition> getProps() {
	return props;
}
public void setProps(Map<String, PropertyDefinition> props) {
	this.props = props;
}
public void setProps(java.util.List<PropertyDefinition> props) {
	for(PropertyDefinition p: props){
	this.props.put(""+p.getIndex(), p);
	}
}
public void buildReverseMap(){
	this.propsReverse = new HashMap<String,PropertyDefinition>();
	for(Map.Entry<String, PropertyDefinition> en: props.entrySet()){
		propsReverse.put(en.getValue().getName(),en.getValue());
	}
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	eventStore  = in.readUTF();
	type = in.readUTF();
	id = in.readInt();
	int propsCount  = in.readInt();
	for(int i = 0 ; i< propsCount; i++){
		String key = in.readUTF();
		PropertyDefinition prop = new PropertyDefinition();
		prop.readExternal(in);
		props.put(key, prop);
	}
	buildReverseMap();
	
}

public void writeExternal(ObjectOutput out) throws IOException {
	
	out.writeUTF(eventStore==null?"":eventStore);
	out.writeUTF(type==null?"":type);
	out.writeInt(id);
	out.writeInt(props.size());
	for(Map.Entry<String, PropertyDefinition>en: props.entrySet()){
		out.writeUTF(en.getKey());
		en.getValue().writeExternal(out);
	}
}

public String getColMap(String property){
	if(propsReverse.size()==0){
		buildReverseMap();	
	}
	PropertyDefinition prop = propsReverse.get(property);
	if(prop!=null){
	return "a"+prop.getIndex();
	}
	return null;
	
}
public Map<Integer,String> getIndexMap() {
	Map<Integer,String> m = new HashMap<Integer,String>();
	for (PropertyDefinition pdef:props.values()){
		m.put(pdef.getIndex(), pdef.getName());
	}
	return m;
}
}
