/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.router;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ptg.events.Event;
import org.ptg.events.EventDefinitionManager;
import org.ptg.util.Constants;
import org.ptg.util.IEventDBTransformer;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.closures.IStateAwareClosure;


public class RouteLoader implements IStateAwareClosure{
	private RoutingEngine eng;
	IEventDBTransformer dbtransformer   = null;
	
	public RouteLoader(RoutingEngine eng){
		this.eng = eng;
	}
	public void execute(ResultSet rs) throws SQLException {
		Event e = dbtransformer.loadFromDb(rs);
		String name = (String)e.getEventProperty(Constants.RouteName);
		String desc = (String)e.getEventProperty(Constants.RouteDescription);
		int type = (Integer) e.getEventProperty(Constants.RouteType);
		if(type ==Constants.Persistant){
			eng.addRouteFromString(name, desc);
		}
		}
	public void finish() {
		
	}
	public void init() {
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(Constants.RouteEvent);
		dbtransformer  = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
	}
}
