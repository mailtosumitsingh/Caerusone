
package org.ptg.components;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.camel.Component;
import org.ptg.events.Event;
import org.ptg.events.EventDefinitionManager;
import org.ptg.router.RoutingEngine;
import org.ptg.util.Constants;
import org.ptg.util.GenericException;
import org.ptg.util.IEventDBTransformer;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.closures.IStateAwareClosure;


public class ComponentsLoader implements IStateAwareClosure{
	private RoutingEngine eng;
	IEventDBTransformer dbtransformer   = null;
	
	public ComponentsLoader(RoutingEngine eng){
		this.eng = eng;
	}
	public void execute(ResultSet rs) throws SQLException {
		Event e = dbtransformer.loadFromDb(rs);
		String name = (String)e.getEventProperty(Constants.ComponentName);
		String url = (String)e.getEventProperty(Constants.ComponentURL);
		String type = (String) e.getEventProperty(Constants.ComponentClass);
		Component component  =null;
		if(url==null)
		component = (Component)ReflectionUtils.createInstance(name);
		else
			component = (Component)ReflectionUtils.createInstance(name, new Object[]{url});
			
		try {
			eng.addComponent(name, component);
		} catch (GenericException e1) {
			e1.printStackTrace();
		}
		}

	public void finish() {
		
	}
	public void init() {
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(Constants.RouteEvent);
		dbtransformer  = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
	}
}
