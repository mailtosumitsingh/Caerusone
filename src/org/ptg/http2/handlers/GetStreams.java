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
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;

public class GetStreams extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Collection<Stream>defs = StreamManager.getInstance().getStreams().values();
		Collection<WebStream> s= new ArrayList<WebStream>();
		for(Stream def:defs){
			WebStream  ws = new WebStream ();
			ws.name = def.getName();
			ws.eventType =def.getEventType();
			ws.processor = def.getProcessor();
			ws.seda = def.getSeda();
			ws.id =def.getId();
			s.add(ws);
		}
		String ret  = CommonUtil.jsonFromArray(s.toArray());
		response.getWriter().write(ret);
		((Request) request).setHandled(true);
	}
	public class WebStream {
		String name;
		String eventType;
		String processor;
		int seda ;
		int id;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEventType() {
			return eventType;
		}
		public void setEventType(String eventType) {
			this.eventType = eventType;
		}
		public String getProcessor() {
			return processor;
		}
		public void setProcessor(String processor) {
			this.processor = processor;
		}
		public int getSeda() {
			return seda;
		}
		public void setSeda(int seda) {
			this.seda = seda;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		
		}
}
