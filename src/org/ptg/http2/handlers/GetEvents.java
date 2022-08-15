/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.util.CommonUtil;

public class GetEvents extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Collection<EventDefinition>defs = EventDefinitionManager.getInstance().getEventDefinitions().values();
		Collection<WebEventDefinition> s= new ArrayList<WebEventDefinition>();
		for(EventDefinition def:defs){
			WebEventDefinition d = new WebEventDefinition();
			d.setId(def.getId());
			d.type = def.getType();
			d.eventStore = def.getEventStore();
		s.add(d);
		}
		String ret =  CommonUtil.jsonFromArray(s.toArray());
		response.getWriter().write(ret);
		((Request) request).setHandled(true);
	}
	public class WebEventDefinition {
		  private int id;
		  private String type;
		  private String eventStore;
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
		public String getEventStore() {
			return eventStore;
		}
		public void setEventStore(String eventStore) {
			this.eventStore = eventStore;
		}
		  
		}

}
