/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.IEventDBTransformer;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.events.PageEvent;
public class PageHandler extends AbstractHandler{
	private IEventDBTransformer dbtransformer ;
	
    		public PageHandler() {
    		super();
    		try{
    		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(new PageEvent().getEventType());
			if(ed!=null){
				Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(ed.getType());
			dbtransformer  = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
			dbtransformer.setStore(ed.getEventStore());
			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		}

			public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    		String toSave = request.getParameter("tosave");
    		PageEvent evt = (PageEvent)CommonUtil.getEventFromJsonData(toSave);
    		evt.setXref(evt.getDest()+"_"+evt.getName());
    		response.getWriter().write("Success");
    		dbtransformer.deleteFromDbByXref(evt);
    		dbtransformer.saveToDb(evt);
    		CommonUtil.sendAndWait(evt.getDest(), evt);
	        ((Request)request).setHandled(true);
	    }

}
