/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.router;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Service;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.UnitOfWork;
import org.apache.commons.lang.RandomStringUtils;
import org.ptg.admin.AppContext;
import org.ptg.processors.ConnDef;
import org.ptg.processors.ProcessorDef;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.Constants;
import org.ptg.util.DynaRouteBuilder;
import org.ptg.util.GenericException;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.db.DBHelper;

public class RoutingEngine {
	private DefaultCamelContext ctx = null;
	private ProducerTemplate tmpl;
	private JndiRegistry reg = new JndiRegistry();

	public RoutingEngine() {

	}

	public void init() {
		try {
			ctx = new DefaultCamelContext();
			ctx.setRegistry(reg);
			tmpl = ctx.createProducerTemplate();
			ctx.start();
			ctx.setTrace(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ProducerTemplate getTemplate() {
		return tmpl;
	}

	public CamelContext getContext() {
		return ctx;
	}

	public void reinit() {
		try {
			ctx.stop();
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addRoutes(RouteBuilder r) throws GenericException {
		try {
			ctx.addRoutes(r);
			AppContext.getInstance().incrStat("RoutingEngineRouteBuilderCount");
			RoutesDefinition routes = r.getRouteCollection();
			for (RouteDefinition rd : routes.getRoutes()) {
				ctx.startRoute(rd);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericException("Problem adding routes", e);
		}
	}

	public void removeRoutes(RouteBuilder r) throws GenericException {
		try {

			RoutesDefinition routes = r.getRouteCollection();
			for (RouteDefinition rd : routes.getRoutes()) {
				ServiceStatus status = rd.getStatus();
				if (status.isStoppable()) {
					ctx.stopRoute(rd);
				}
				ctx.removeRouteDefinition(rd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericException("Problem adding routes", e);
		}
	}

	public void addComponent(String name, Component component) throws GenericException {
		try {
			ctx.addComponent(name, component);
			AppContext.getInstance().incrStat("RoutingEngineComponentCount");

		} catch (Exception e) {
			throw new GenericException("Problem adding component", e);
		}
	}

	public void addRouteFromString(String rt) {
		String name = RandomStringUtils.randomAlphabetic(31);
		addRouteFromString(name, rt);
	}

	public void removeRouteFromString(String name, String desc) {
		Class c = DynaRouteBuilder.getClass(RandomStringUtils.randomAlphabetic(31), desc);
		RouteBuilder o = (RouteBuilder) ReflectionUtils.createInstance(c.getName());
		try {
			removeRoutes(o);
		} catch (GenericException e) {
			e.printStackTrace();
		}

	}

	public void addRouteFromString(String name, String rt) {
		{
			Class c = DynaRouteBuilder.getClass(RandomStringUtils.randomAlphabetic(31), rt);
			RouteBuilder o = (RouteBuilder) ReflectionUtils.createInstance(c.getName());
			try {
				addRoutes(o);
			} catch (GenericException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadStaticRoutes() {
		String sql = "select * from " + Constants.RouteStore;
		RouteLoader l = new RouteLoader(this);
		DBHelper.getInstance().forEach(sql, l);

	}

	public void sendExchange(String url, Exchange e) {
		if (tmpl != null) {
			tmpl.send(url, e);
		}
	}

	public Exchange getDefaultExchange() {
		Exchange e = new DefaultExchange(ctx);
		return e;
	}

	public Exchange sendMessage(String url, Exchange e) {
		if (tmpl != null) {
			tmpl.send(url, e);
		}
		return e;
	}

	public Exchange sendMessage(String url, Message m) {
		Exchange e = new DefaultExchange(ctx);
		e.setIn(m);
		if (tmpl != null) {
			tmpl.send(url, e);
		}
		return e;
	}

	public void printOutputs(StringBuilder sb, int depth, Collection<ProcessorDefinition> pds) {
		depth = depth + 1;
		for (ProcessorDefinition pd : pds) {
			for (int i = 0; i <= depth; i++) {
				sb.append("\t");
			}
			sb.append("ProcessorOutput: " + pd.getLabel() + "\n");
			printOutputs(sb, ++depth, pd.getOutputs());
		}

	}

	public void printInputs(StringBuilder sb, int depth, Collection<FromDefinition> pds) {
		depth = depth + 1;
		for (FromDefinition pd : pds) {
			for (int i = 0; i <= depth; i++) {
				sb.append("\t");
			}
			sb.append("ProcessorInput: " + pd.getLabel() + "\n");
		}

	}

	public void printServices(StringBuilder sb, Collection<Service> pds) {
		for (Service pd : pds) {
			// sb.append("\tService: "+pd.toString()+"\n");
			if (pd instanceof UnitOfWork) {
				UnitOfWork u = (UnitOfWork) pd;
				System.out.println("\tID: " + u.getId());
			} else if (pd instanceof Consumer) {
				Consumer u = (Consumer) pd;
				System.out.println("\tConsumer: " + u);
			} else {
				sb.append("\tService: " + pd.toString() + "\n");
			}

		}
	}

	public void printRoutes() {
		StringBuilder sb = new StringBuilder();
		for (RouteDefinition rd : ctx.getRouteDefinitions()) {
			sb.append("RouteDefinition: " + rd.getId() + "\n");
			printOutputs(sb, 0, rd.getOutputs());
			printInputs(sb, 0, rd.getInputs());
		}

		/*
		 * for(Route rr: ctx.getRoutes()){
		 * sb.append("Route: "+rr.getEndpoint().getEndpointUri());
		 * System.out.println(rr.getEndpoint().getEndpointUri()); try {
		 * printServices(sb,rr.getServicesForRoute()); } catch (Exception e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } }
		 */
		System.out.println(sb.toString());
	}

	public void createMessage(String to, byte[] body) {
		tmpl.sendBody(to, body);
	}

	public void stop() throws Exception {
		ctx.stop();
	}

	public void start() throws Exception {
		ctx = new DefaultCamelContext();
		tmpl = ctx.createProducerTemplate();
		ctx.start();
		ctx.setTrace(false);
	}

	public void pause() {
		// ctx.pause();
	}

	public void resume() {
		// ctx.resume();
	}

	public Map<String, RouteDefinition> getRoutes() {
		Map<String, RouteDefinition> routes = new HashMap<String, RouteDefinition>();
		for (RouteDefinition rd : ctx.getRouteDefinitions()) {
			routes.put(rd.getId(), rd);
		}
		return routes;
	}

	public boolean checkStreamRoutes(Stream s) {
		if (s != null) {
			return checkStreamRoutes(s.getProcessor() == null ? s.getName() + "_DEFAULTProc" : s.getProcessor(), s.getName());
		}
		return false;
	}

	public boolean checkStreamRoutes(String proc, String stream) {
		boolean m1 = false, m2 = false, m3 = false;
		Map<String, RouteDefinition> routes = getRoutes();
		RouteDefinition rt = routes.get(proc);
		String s1 = "direct:" + stream;
		String s2 = "direct:" + stream + "-out";
		if (rt != null) {
			m1 = true;
			for (ProcessorDefinition pd : rt.getOutputs()) {
				if (pd != null && pd.getLabel() != null) {
					if (pd.getLabel().equals(s2)) {
						m2 = true;
						break;
					}
				}
			}
			for (FromDefinition pd : rt.getInputs()) {
				if (pd != null && pd.getLabel() != null) {
					if (pd.getLabel().equals(s1)) {
						m3 = true;
						break;
					}
				}
			}
		}

		return m1 & m2 & m3;
	}

	public boolean checkRoute(String proc, String in, String out) {
		Map<String, RouteDefinition> routes = getRoutes();
		RouteDefinition rt = routes.get(proc);
		String s1 = "direct:" + in;
		String s2 = "direct:" + out;
		if (rt != null) {
			return checkRoute(rt, s1, s2);
		}

		return false;
	}

	public boolean checkRoute(RouteDefinition rt, String in, String out) {
		boolean m1 = false, m2 = false;
		for (ProcessorDefinition pd : rt.getOutputs()) {
			for (Object o : pd.getOutputs()) {
				ProcessorDefinition pd2 = (ProcessorDefinition) o;
				m1 = true;
				break;
			}
		}
		if (m1 == true) {
			for (FromDefinition pd : rt.getInputs()) {
				if (pd.getLabel().equals(in)) {
					m2 = true;
					break;
				}
			}
		}
		return m1 & m2;
	}

	public boolean checkConnRoute(String in, String out) {
		boolean m1 = false, m2 = false;

		for (RouteDefinition rt : getRoutes().values()) {
			for (ProcessorDefinition pd : rt.getOutputs()) {
				if (pd != null && pd.getLabel() != null) {
					if (pd.getLabel().equals(out)) {
						m1 = true;
						break;
					}
				}
			}
			if (m1 == true) {
				for (FromDefinition pd : rt.getInputs()) {
					if (pd != null && pd.getLabel() != null) {
						if (pd.getLabel().equals(in)) {
							m2 = true;
							break;
						}
					}
				}
			}
			if (m1 & m2) {
				break;
			} else {
				m1 = false;
				m2 = false;
			}
		}
		return m1 & m2;
	}

	public boolean checkProcessorRoute(ProcessorDef s) {
		Map<String, RouteDefinition> routes = getRoutes();
		RouteDefinition rt = routes.get(s.getName());
		return rt != null;
	}

	public boolean checkConnectionRoutes(ConnDef s) {
		Stream self = StreamManager.getInstance().getStream("CondStream" + s.getId());
		boolean m1 = checkStreamRoutes(self);
		String s1 = "direct:CondStream" + s.getId();
		String s2 = "direct:CondStream" + s.getId() + "-out";
		String s3 = "direct:" + s.getFrom() + "-out";
		String s4 = "direct:" + s.getTo();
		// System.out.println(s3 +" "+s1);
		boolean m2 = checkConnRoute(s3, s1);
		// System.out.println(s2 +" "+s4);
		boolean m3 = checkConnRoute(s2, s4);
		return m1 & m2 & m3;
	}

	public void register(String name, Object toreg) {
		reg.bind(name, toreg);
	}
}
