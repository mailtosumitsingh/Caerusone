/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.ptg.util.closures.IStateAwareClosure;

public class EventDefinitionLoader implements IStateAwareClosure {
	Map<String, EventDefinition >defs ;
	public EventDefinitionLoader(Map<String,EventDefinition> m){
		defs = m;
		
	}
	public void execute(ResultSet rs) throws SQLException {
		EventDefinition e = null; 
		String type = rs.getString("etype");
		String eventStore = rs.getString("estore");
		int id = rs.getInt("eid");
		e = defs.get(type);
		if(e==null){
		e = new EventDefinition();
		e.setId(id);
		e.setType(type);
		e.setEventStore(eventStore);
		defs.put(type, e);
		}
		loadEventProperty(rs, e);
		defs.put(e.getType(), e);

	}
	private void loadEventProperty(ResultSet rs, EventDefinition e) throws SQLException{
		PropertyDefinition def = new PropertyDefinition();
		String name = rs.getString("edname");
		if (name==null){
			return;
		}else{
		String type = rs.getString("edtype");
		int eventType = rs.getInt("edeventType");
		int index = rs.getInt("edidx");
		int searchable = rs.getInt("edsearchable");
		int id = rs.getInt("edid");
		String ds = rs.getString("edds");
		def.setEventType(eventType);
		def.setId(id);
		def.setIndex(index);
		def.setSearchable(searchable);
		def.setName(name);
		def.setType(type);
		def.setDataStructure(ds);
		e.getProps().put(""+index,def);
		}
		
	}
	public void finish() {
		// TODO Auto-generated method stub

	}

	public void init() {
		// TODO Auto-generated method stub

	}

}
