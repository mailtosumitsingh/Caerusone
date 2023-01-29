/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.builder.RouteBuilder;
import org.ptg.cluster.AppContext;
import org.ptg.components.ComponentsManager;
import org.ptg.events.EventDefinitionManager;
import org.ptg.http2.HTTPServer;
import org.ptg.http2.InitCustomHandlers;
import org.ptg.plugins.IPluginManager;
import org.ptg.processors.ProcessorManager;
import org.ptg.router.RoutingEngine;
import org.ptg.router.SystemRouter;
import org.ptg.script.ScriptEngine;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.SpringHelper;
import org.eclipse.jetty.webapp.Configuration;
public class WebStartProcess {
	private ScriptEngine s;
	private RoutingEngine r;
	private AtomicBoolean b = new AtomicBoolean(false);
	private HTTPServer templateServer;
	private SystemRouter systemRouter;
    private static IPluginManager pluginsManager;
    private AutomationServer automationServer;
    private static AppContext ctx; 
	private WebStartProcess() {
		if (b.getAndSet(true)) {
			throw new RuntimeException("StartProcess invoked more then once in same vm...");
		}
	}

	public static void main(String[] args) {
	initMain();
	}

	public static void initMain() {
		ctx = (AppContext) SpringHelper.get("appContext");
    	
		CommonUtil.saveLog("Initing webstart process", "SystemEvent");
		WebStartProcess p = WebStartProcess.getInstance();
		CommonUtil.saveLog("Starting spread Daemon ", "SystemEvent");
		p.startSpreadDaemon();
		CommonUtil.saveLog("Starting spread Daemon -Done", "SystemEvent");

		CommonUtil.saveLog("Starting http servers..", "SystemEvent");
		p.startHTTPServer();
		CommonUtil.saveLog("Starting Scripting Engine", "SystemEvent");
		p.startScriptEngine();
		p.s.addObject("se", p.s);
		CommonUtil.saveLog("Starting Routing Engine", "SystemEvent");
		p.startRoutingEngine();
		p.s.addObject("re", p.r);
		CommonUtil.saveLog("Starting Automation Server", "SystemEvent");
		p.startAutomationServer();
		
		CommonUtil.saveLog("initiating Sytem ", "SystemEvent");
		p.initSystem();
		  initPlugins();
		// TestSendSubscriptionEvent.execute();

	}

	private void startAutomationServer() {
			this.automationServer = new AutomationServer();
			this.automationServer.start();
	}

	public static void initPlugins() {
		pluginsManager = (IPluginManager) SpringHelper.get("pluginsManager"); 
	        if(ctx.isStartPlugins()){
	        	try {
					pluginsManager.loadPlugins((String) SpringHelper.get("pluginsDir"));
				} catch (Exception e) {
					e.printStackTrace();
				} 
	        }
	}

	public void startScriptEngine() {

		s = (ScriptEngine) SpringHelper.get("scriptEngine");
		if (s == null) {
			s = new ScriptEngine();
			SpringHelper.set("scriptEngine", s);
			s.init();
		}

		String str = null;
		try {
			str = (String) s.runTask("TestTask", new ArrayList());

		} catch (GenericException e) {
			e.printStackTrace();
		}
		System.out.println("Engine loadded :" + str == null ? "Failed" : str);
	}


	public void startRoutingEngine() {
		r = new RoutingEngine();
		r.init();
	}

	public ScriptEngine getScriptEngine() {
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		return s;
	}


	public RoutingEngine getRoutingEngine() {
		return r;
	}

	private static class SingletonHolder {
		private static final WebStartProcess INSTANCE = new WebStartProcess();
	}

	public static WebStartProcess getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void initSystem() {
		try {
			deployDynamicLoadedItems();
			// load event definitions
			CommonUtil.saveLog("Starting Event Definition Manager", "SystemEvent");
			EventDefinitionManager mgr = EventDefinitionManager.getInstance();
			mgr.loadEventDefinitions();
			CommonUtil.saveLog("Generating Event Definitions...", "SystemEvent");
			mgr.generateEventClasses();
			CommonUtil.saveLog("Generating Event DB Transformer classes...", "SystemEvent");
			mgr.generateEventDBTransformerClasses();
			CommonUtil.saveLog("Loading Stream Definitions..", "SystemEvent");
			// load Stream definitions
			StreamManager m = StreamManager.getInstance();
			m.loadStreams();
			CommonUtil.saveLog("Loading Stream Transformers...", "SystemEvent");
			m.generateStreamTransformers();
			CommonUtil.saveLog("Initializing Rule Engine...", "SystemEvent");
			// init rule engine
			initRuleEngine();
			CommonUtil.saveLog("Initializing Proces Engine...", "SystemEvent");
			// Init ProcesseManager
			CommonUtil.saveLog("Router comming up...", "SystemEvent");
			attachNodes();
			// initialize processor routes

			addRoutes();

			CommonUtil.saveLog("Adding Components..", "SystemEvent");
			// initialise subscription subssystem
			// initialize component system
			ComponentsManager.getInstance().init();

			CommonUtil.saveLog("Running first init script..", "SystemEvent");
			s.run("init.js");
			// CommonUtil.saveLog("Testing Json Engine..", "SystemEvent");
			// testJsonPath();
			// r.printRoutes();
			CommonUtil.saveLog("Starting ftp server ", "SystemEvent");
			startFTPService();

			CommonUtil.saveLog("Starting sshd server ", "SystemEvent");
			startSSHDService();

			CommonUtil.saveLog("Initializing cusom handlers .", "SystemEvent");
			org.ptg.http2.InitCustomHandlers handlers = new InitCustomHandlers();

			CommonUtil.saveLog("Houston we have a go.", "SystemEvent");

		} catch (GenericException e) {
			e.printStackTrace();
		}

	}

	public void attachNodes() throws GenericException {
		StreamManager m = StreamManager.getInstance();
		ProcessorManager p = ProcessorManager.getInstance();
		p.init();
		CommonUtil.saveLog("Setting Streams and process...", "SystemEvent");
		// Init basic routing table.
		Iterator<Stream> iter = m.getStreams().values().iterator();
		while (iter.hasNext()) {
			Stream stream = iter.next();
			String name = stream.getName();
			String processor = stream.getProcessor();
			if (name != null && processor != null) {
				CommonUtil.saveLog("Now initializing process: " + name + " with processor " + processor);
				try {
					IProcessor iprocessor = p.attach(name, processor);
				} catch (Exception e) {
					CommonUtil.saveLog("Failed to initialize process[moving ahead]: " + name + " with processor " + processor);
					e.printStackTrace();
				}
			}
		}
	}

	public void detachNodes() {
		Map<String, IProcessor> routes = ProcessorManager.getInstance().getProcessorRoutingTable();
		for (Map.Entry<String, IProcessor> en : routes.entrySet()) {
			IProcessor p = en.getValue();
			p.detach();
		}
	}

	private void testJsonPath() {
		List l = new ArrayList();
		l.add("{ \"store\": {" + "\"book\": [" + "{ \"category\": \"reference\"," + "\"author\": \"Nigel Rees\"," + "\"title\": \"Sayings of the Century\"," + "\"price\": 8.95" + "}" + "]" + "}"
				+ "};");

		l.add("$..book[-1:]");
		String ret = (String) s.runFuntionRaw("js_jsonPath", l);
		System.out.println("Result from jsonpath test: " + ret);
	}

	private void startHTTPServer() {
		templateServer = new HTTPServer();
		templateServer.start();

	}

	public HTTPServer getTemplateServer() {
		return templateServer;
	}

	public void setTemplateServer(HTTPServer templateServer) {
		this.templateServer = templateServer;
	}

	public void initRuleEngine() {
	}

	public void deployDynamicLoadedItems() {
		org.ptg.cluster.AppContext a = (org.ptg.cluster.AppContext) SpringHelper.get("appContext");
		if (a.isDynaDeploy()) {
			CommonUtil.loadDeployements();
		}

	}

	public void startFTPService() {
		org.ptg.cluster.AppContext a = (org.ptg.cluster.AppContext) SpringHelper.get("appContext");
		if (a.isStartFTPServer()) {

		}
	}

	public void startSSHDService() {
		org.ptg.cluster.AppContext a = (org.ptg.cluster.AppContext) SpringHelper.get("appContext");
		if (a.isStartSSHD()) {

		}
	}

	public void startSpreadDaemon() {
		org.ptg.cluster.AppContext a = (org.ptg.cluster.AppContext) SpringHelper.get("appContext");
		if (a.isStartSpreadDaemon()) {
			try {
				Runtime.getRuntime().exec(".\\extra\\exec\\spread -c .\\extra\\exec\\spread.conf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addRoutes() throws GenericException {
		systemRouter = new SystemRouter();
		addRoutes(systemRouter);
		CommonUtil.saveLog("Adding Static Routes..", "SystemEvent");
		r.loadStaticRoutes();

	}

	public void addRoutes(RouteBuilder rb) throws GenericException {
		CommonUtil.saveLog("Adding routes..", "SystemEvent");

		r.addRoutes(rb);

	}

	public SystemRouter getSystemRouter() {
		return systemRouter;
	}

	public void setSystemRouter(SystemRouter systemRouter) {
		this.systemRouter = systemRouter;
	}

	public AutomationServer getAutomationServer() {
		return automationServer;
	}

	public void setAutomationServer(AutomationServer automationServer) {
		this.automationServer = automationServer;
	}
	
}
