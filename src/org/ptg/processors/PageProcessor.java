/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.processors;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.AppContext;
import org.ptg.admin.WebSiteConstants;
import org.ptg.admin.WebStartProcess;
import org.ptg.events.Event;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.events.ObjectWrapperEvent;
import org.ptg.events.PropertyDefinition;
import org.ptg.events.StringEvent;
import org.ptg.events.TraceEvent;
import org.ptg.http2.HTTPHandler;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamDefinition;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.ContPublisherWriter;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.SpringHelper;
import org.ptg.util.db.DBHelper;
import org.ptg.util.events.PageEvent;
import org.ptg.velocity.VelocityHelper;

public class PageProcessor extends AbstractHandler implements IProcessor {
	private String name;
	private String streamName;
	private String query = "";
	private Stream stream;
	IStreamTransformer sxfr;
	String output;
	String namecalls;
	String namecallsExp;

	private Map<String, String> pagemap = new HashMap<String, String>();
	private Map<String, Object> TemplateParams = new HashMap<String, Object>();
	static EventDefinition pageEventDef = EventDefinitionManager.getInstance().getEventDefinition(new PageEvent().getEventType());

	public PageProcessor() {
		pagemap.put("mainpage", "Hello from $hostname ");
		TemplateParams.put("hostname", WebSiteConstants.HOST_IP);
		TemplateParams.put("hostport", "8095");

	}

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) {

		try {
			String v = (String) AppContext.getInstance().getVar("processtrace_" + name);
			if (v != null && "true".equalsIgnoreCase(v)) {
				Event evt = new TraceEvent("Recieved Event", name, "T");
				ContPublisherWriter.getInstance().loadEvent(evt);
			}
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			String pageid = request.getParameter("pageid");
			if (pageid == null) {
				pageid = "mainpage";
			}
			if (pageid != null) {
				String fname = pagemap.get(pageid);
				if (fname != null) {
					String param = arg1.getParameter("tosave");
					Event e = null;
					if (param != null) {
						e = CommonUtil.getEventFromJsonData(param);
					} else {
						e = (Event) sxfr.transformHTTP(request);
					}
					processEventInternal(request, response, fname, e);
				}
			}
		} catch (IOException e) {
			AppContext.getInstance().incrStat("TotalExceptionCalls");
			AppContext.getInstance().incrStat(namecallsExp);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	private StringBuffer processEventInternal(HttpServletRequest request, HttpServletResponse response, String fname, Event e) throws IOException {
		Map m = new HashMap();
		if (output != null && output.length() > 0) {
			Exchange exh = WebStartProcess.getInstance().getRoutingEngine().getDefaultExchange();
			DefaultMessage msgtemp = new DefaultMessage();
			msgtemp.setBody(e);
			exh.setIn(msgtemp);
			WebStartProcess.getInstance().getRoutingEngine().sendExchange("direct:" + output, exh);
			m.put("mout", exh.getOut().getBody());
			m.put("exh", exh);
		}

		m.put("hostname", WebSiteConstants.HOST_IP);
		m.put("hostport", "8095");
		m.put("request", request);
		m.put("response", response);
		m.put("ein", e);
		m.put("spring", SpringHelper.class);
		m.put("ws", WebStartProcess.class);
		m.put("CommonUtil", CommonUtil.class);
		m.put("dbhelper", DBHelper.getInstance());
		StringBuffer responseContent = VelocityHelper.burnStringTemplate(m, fname);
		if (response != null) {
			response.getWriter().write(responseContent.toString());
			response.flushBuffer();
		}
		AppContext.getInstance().incrStat("TotalProcessorCalls");
		AppContext.getInstance().incrStat(namecalls);
		return responseContent;
	}

	@Override
	public void attach(String streamName) throws GenericException {
		namecalls = "Processor_" + getName() + "_Calls";
		namecallsExp = "Processor_" + getName() + "_CallsExp";
		pagemap.put("mainpage", "Hello from $hostname " + streamName + "," + name);

		this.streamName = streamName;
		try {
			stream = StreamManager.getInstance().getStream(streamName);
			EventDefinition edef = EventDefinitionManager.getInstance().getEventDefinition(stream.getEventType());
			if (stream.getDefs().size() == 0) {
				for (PropertyDefinition p : edef.getProps().values()) {
					StreamDefinition def = CommonUtil.getStreamPropertyDefinition(p.getName(), p.getType(), p.getIndex());
					stream.getDefs().put(def.getName(), def);
				}
			}
			Class c = StreamManager.getInstance().getStreamTransformer(streamName, true);

			sxfr = (IStreamTransformer) ReflectionUtils.createInstance(c.getName());

			output = stream.getExtra();
		} catch (Exception e) {
			throw new GenericException("Cannot find stream transformer", e);
		}
		HTTPHandler handler = WebStartProcess.getInstance().getTemplateServer().getHandler();
		handler.addHandler("/" + this.getName(), this);
		handler.addHandler("/" + this.streamName, this);
		String where = " where " + pageEventDef.getColMap("dest") + " = \'" + this.getStreamName() + "\'";
		List<Event> events = DBHelper.getInstance().getEventsFromTableWhere(pageEventDef.getEventStore(), where);
		for (Event e : events) {
			PageEvent pe = (PageEvent) e;
			pagemap.put(pe.getName(), pe.getContent());
		}
	}

	/*
	 * todo: the page event should tell what is the process id for that event
	 * and that we will trigger instead of using output.
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getStreamName() {
		return streamName;
	}

	@Override
	public IStreamTransformer getTransformer() {
		return null;
	}

	@Override
	public void process(Exchange msg) throws Exception {
		Object e = msg.getIn().getBody();
		if (e instanceof PageEvent) {
			PageEvent pe = (PageEvent) e;
			if (pe.getDest() == null || pe.getDest().length() < 1) {
				pagemap.put(pe.getName(), pe.getContent());
			} else {
				if (pe.getDest().equals(this.streamName)) {
					pagemap.put(pe.getName(), pe.getContent());
				}
			}
		} else {
			Event evt = null;
			String pageid = "mainpage";
			if (e instanceof Event) {
				evt = (Event) e;
				pageid = evt.getEventStringProperty("pageid");
			} else {
				evt = new ObjectWrapperEvent(e);
				pageid = evt.getEventStringProperty("pageid");
			}
			if (pageid == null) {
				pageid = "mainpage";
			}
			String fname = null;
			if (pageid != null) {
				fname = pagemap.get(pageid);
				StringBuffer content = processEventInternal(null, null, fname, evt);
				msg.getOut().setBody(new StringEvent(content.toString()));
			}
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public void setQuery(String s) {
		query = s;

	}

	@Override
	public String getDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getConfigItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfigItems(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getConfigOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}