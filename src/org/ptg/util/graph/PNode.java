package org.ptg.util.graph;

import java.util.HashMap;
import java.util.Map;

import org.ptg.util.AbstractHierStore;

public class PNode extends AbstractHierStore<PNode>{
    int level;
    Map<String, Object> userData = new HashMap<String,Object>();
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "PNode [level=" + level + ", name=" + name + "]";
	}
	
public void setUserData(String id, Object data){
	userData.put(id,data);
}
public Object getUserData(String id){
	return userData.get(id);
}
public Object removeUserData(String id){
	return userData.remove(id);
}

}
