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
import org.ptg.events.PropertyDefinition;
import org.ptg.util.CommonUtil;
import org.ptg.util.Constants;


public class GetEventDefinition extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = (String) arg1.getParameter(Constants.EventType);
		if(name!=null){
			EventDefinition def = EventDefinitionManager.getInstance().getEventDefinition(name);
			if(def!=null){
		Collection<PropertyDefinition> s= new ArrayList<PropertyDefinition>();
		String ret =  CommonUtil.jsonFromArray(def.getProps().values().toArray());
		response.getWriter().write(ret);
			}else{
				response.getWriter().write("{}");
			}
		}
		((Request) request).setHandled(true);
	}
}
