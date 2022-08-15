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

public class SourceMap {
 Map<String,Object> sources = new HashMap<String,Object>();

public Map<String, Object> getSources() {
	return sources;
}

public void setSources(Map<String, Object> sources) {
	this.sources = sources;
}
public Object getSource(String store){
	return sources.get(store);
} 

} 
