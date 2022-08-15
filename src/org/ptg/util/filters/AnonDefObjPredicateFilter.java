package org.ptg.util.filters;

import java.util.Map;

import org.apache.commons.collections15.Predicate;
import org.ptg.util.mapper.AnonDefObj;

public class AnonDefObjPredicateFilter implements Predicate<Object>{
	 Map<String, AnonDefObj> anonCompMap;
	
	public AnonDefObjPredicateFilter(Map<String, AnonDefObj> anonCompMap) {
		super();
		this.anonCompMap = anonCompMap;
	}
	public Map<String, AnonDefObj> getAnonCompMap() {
		return anonCompMap;
	}
	public void setAnonCompMap(Map<String, AnonDefObj> anonCompMap) {
		this.anonCompMap = anonCompMap;
	}
	@Override
	public boolean evaluate(Object arg0) {
		return anonCompMap.containsKey(arg0.toString());
	}

}
