/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.events;

import java.util.HashMap;
import java.util.Map;
/*
 * this is transient event donot use it beyond the vm boundry
 * since it has no serialization and other knowledge that 
 * an event should have
 * */
public class MapWrapperEvent extends Event {
	private java.util.Map _wrapped;
	protected Class getMyClass() {
		return _wrapped.getClass();
	}

	protected Object getThis() {
		return _wrapped;
	}
	public void  setWrapped(Object wrapped) {
		_wrapped = (Map) wrapped;
	}
	public MapWrapperEvent(Object wrapped) {
		_wrapped = (Map) wrapped;
	}
	public MapWrapperEvent() {
		_wrapped = new HashMap();
	}
	public void addProp(String key,Object val) {
		_wrapped.put(key,val);
	}
	public void setEventProperty(String prop, Object val) {
	 _wrapped.put(prop,val);	
	}

	public Object getEventProperty(String prop) {
		return _wrapped.get(prop);
	}

}
