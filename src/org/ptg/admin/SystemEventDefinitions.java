/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.admin;

import java.io.File;
import java.util.HashSet;

import org.apache.commons.collections.Closure;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.events.PropertyDefinition;
import org.ptg.events.TraceEvent;
import org.ptg.stream.StreamDefinition;
import org.ptg.util.CommonUtil;
import org.ptg.util.Constants;
import org.ptg.util.db.DBHelper;

import com.google.common.collect.Sets;

public class SystemEventDefinitions {
	static HashSet<String> notToDelete = Sets.newHashSet();
	static {
		notToDelete.add("graphs");
		notToDelete.add("graphs");
	}

	public static void main(String[] args) {
		// CommonUtil.generateEventDefinitionFromEvent(new PageEvent());
		reinit();
	}

	public static void reinit() {
		// cleanExtraBinDir();
		// cleanExtraTables();
		// truncateAllTables();

		recreateSystemObjects();
	}

	private static void truncateAllTables() {
		String[] tables = DBHelper.getInstance().getTableNames("caerusone", "caerusone", "");
		for (String table : tables) {
			if (!notToDelete.contains(table)) {
				System.out.println("Truncating table: " + table);
				DBHelper.getInstance().truncateTable(table);
			}
		}
	}

	public static void cleanExtraBinDir() {
		String base = "c:\\Projects\\caerusone";
		CommonUtil.forEachFileInDirFile(base + File.separator + "extrabin", new Closure() {
			@Override
			public void execute(Object arg0) {
				File f = (File) arg0;
				System.out.println("Deleting: " + f.getAbsolutePath());
				f.delete();
			}
		}, false);
	}

	private static void recreateSystemObjects() {
		buildCoreEventDefinitions();
		EventDefinitionManager.getInstance().generateEventClasses();
		EventDefinitionManager.getInstance().generateEventDBTransformerClasses();
		EventDefinitionManager.getInstance().ensureDBTables();
	}

	public static void cleanExtraTables() {
		for (EventDefinition def : EventDefinitionManager.getInstance().getEventDefinitions().values()) {
			DBHelper.getInstance().dropTable(def.getEventStore());
		}
	}

	public static void buildCoreEventDefinitions() {
		CommonUtil.generateEventDefinitionFromEvent(new TraceEvent());
		SystemEventDefinitions def = new SystemEventDefinitions();
		def.createRouteDefinition();
		def.createLogDefinition();
		def.createQuartzIDefinition();
		def.createQuartzDefinition();
		def.createEDUpdateEvent();
		def.createDeployEventDefinition();
		def.createBPEvent();
		def.createStepEvent();
		def.createWatcherEvent();
		def.createDebugControlEvent();
		def.heartBeatEventDefinition();
		def.createPageEventDefinition();
	}

	public void createDebugControlEvent() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.debug.DebugControlEvent");
		e.setEventStore("dbgctrl_event");
		PropertyDefinition p1 = getPropertyDefinition("command", "java.lang.String", 1, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		PropertyDefinition p2 = getPropertyDefinition("params", "java.lang.String", 2, 1);
		e.getProps().put("" + p2.getIndex(), p2);

		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createBPEvent() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.debug.BPEvent");
		e.setEventStore("bpevent_store");
		PropertyDefinition p1 = getPropertyDefinition("line", "java.lang.String", 1, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		PropertyDefinition p2 = getPropertyDefinition("codebase", "java.lang.String", 2, 1);
		e.getProps().put("" + p2.getIndex(), p2);

		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createStepEvent() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.debug.StepEvent");
		e.setEventStore("StepEvent_store");
		PropertyDefinition p1 = getPropertyDefinition("line", "java.lang.String", 1, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		PropertyDefinition p2 = getPropertyDefinition("codebase", "java.lang.String", 2, 1);
		e.getProps().put("" + p2.getIndex(), p2);

		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createWatcherEvent() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.debug.WatcherEvent");
		e.setEventStore("WatcherEvent_store");
		PropertyDefinition p1 = getPropertyDefinition("line", "java.lang.String", 1, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		PropertyDefinition p2 = getPropertyDefinition("codebase", "java.lang.String", 2, 1);
		e.getProps().put("" + p2.getIndex(), p2);

		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createEDUpdateEvent() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.events.EventDefinitionUpdateEvent");
		e.setEventStore("eventdefUpdate_store");
		PropertyDefinition p1 = getPropertyDefinition("name", "java.lang.String", 1, 1);
		e.getProps().put("" + p1.getIndex(), p1);

		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void heartBeatEventDefinition() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.util.events.HeartBeatEvent");
		e.setEventStore("current_events");
		PropertyDefinition p1 = getPropertyDefinition("millis", "long", 1, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createDeployEventDefinition() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.util.events.DeployEvent");
		e.setEventStore("deploy_events");
		PropertyDefinition p1 = getPropertyDefinition("name", "java.lang.String", 1, 1);
		PropertyDefinition p2 = getPropertyDefinition("type", "java.lang.String", 2, 1);
		PropertyDefinition p3 = getPropertyDefinition("dest", "java.lang.String", 3, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		e.getProps().put("" + p2.getIndex(), p2);
		e.getProps().put("" + p3.getIndex(), p3);
		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createPageEventDefinition() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.util.events.PageEvent");
		e.setEventStore("page_events");
		PropertyDefinition p1 = getPropertyDefinition("name", "java.lang.String", 1, 1);
		PropertyDefinition p2 = getPropertyDefinition("content", "java.lang.String", 2, 1);
		PropertyDefinition p3 = getPropertyDefinition("dest", "java.lang.String", 3, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		e.getProps().put("" + p2.getIndex(), p2);
		e.getProps().put("" + p3.getIndex(), p3);
		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createRouteDefinition() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.util.events.RouteEvent");
		e.setEventStore("route_events");
		PropertyDefinition p1 = getPropertyDefinition(Constants.RouteName, "java.lang.String", 1, 1);
		PropertyDefinition p2 = getPropertyDefinition(Constants.RouteDescription, "java.lang.String", 2, 1);
		PropertyDefinition p3 = getPropertyDefinition(Constants.RouteType, "java.lang.Integer", 3, 1);
		PropertyDefinition p4 = getPropertyDefinition(Constants.Action, "java.lang.String", 4, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		e.getProps().put("" + p2.getIndex(), p2);
		e.getProps().put("" + p3.getIndex(), p3);
		e.getProps().put("" + p4.getIndex(), p4);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createLogDefinition() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.util.events.LogEvent");
		e.setEventStore("log_events");
		PropertyDefinition p1 = getPropertyDefinition(Constants.LogStmt, "java.lang.String", 1, 1);
		PropertyDefinition p2 = getPropertyDefinition(Constants.LogEventType, "java.lang.String", 2, 1);
		PropertyDefinition p3 = getPropertyDefinition(Constants.LogTime, "java.util.Date", 3, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		e.getProps().put("" + p2.getIndex(), p2);
		e.getProps().put("" + p3.getIndex(), p3);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public StreamDefinition getStreamPropertyDefinition(String destProp, String type, int index) {
		StreamDefinition def = new StreamDefinition();
		def.setAccessor(Constants.Property);
		def.setDestProp(destProp);
		def.setExtra(destProp);
		def.setName(destProp);
		def.setType(type);
		def.setXmlExpr(null);
		return def;
	}

	public PropertyDefinition getPropertyDefinition(String name, String type, int index, int searchable) {
		PropertyDefinition p1 = new PropertyDefinition();
		p1.setName(name);
		p1.setType(type);
		p1.setIndex(index);
		p1.setSearchable(searchable);
		return p1;

	}

	public void createQuartzDefinition() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.timer.QuartzEvent");
		e.setEventStore("quartz_events");
		PropertyDefinition p1 = getPropertyDefinition("start", "java.util.Date", 1, 1);
		PropertyDefinition p2 = getPropertyDefinition("end", "java.util.Date", 2, 1);
		PropertyDefinition p3 = getPropertyDefinition("count", "int", 3, 1);
		PropertyDefinition p4 = getPropertyDefinition("interval", "long", 4, 1);
		PropertyDefinition p5 = getPropertyDefinition("hint", "java.lang.String", 5, 1);
		PropertyDefinition p6 = getPropertyDefinition("clazz", "java.lang.String", 6, 1);
		PropertyDefinition p7 = getPropertyDefinition("mtd", "java.lang.String", 7, 1);
		PropertyDefinition p8 = getPropertyDefinition("action", "java.lang.String", 8, 1);
		PropertyDefinition p9 = getPropertyDefinition("name", "java.lang.String", 9, 1);
		PropertyDefinition p10 = getPropertyDefinition("group", "java.lang.String", 10, 1);
		e.getProps().put("" + p1.getIndex(), p1);
		e.getProps().put("" + p2.getIndex(), p2);
		e.getProps().put("" + p3.getIndex(), p3);
		e.getProps().put("" + p4.getIndex(), p4);
		e.getProps().put("" + p5.getIndex(), p5);
		e.getProps().put("" + p6.getIndex(), p6);
		e.getProps().put("" + p7.getIndex(), p7);
		e.getProps().put("" + p8.getIndex(), p8);
		e.getProps().put("" + p9.getIndex(), p9);
		e.getProps().put("" + p10.getIndex(), p10);
		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

	public void createQuartzIDefinition() {
		EventDefinition e = new EventDefinition();
		e.setType("org.ptg.timer.QuartzImmidiateEvent");
		e.setEventStore("quartzi_events");
		PropertyDefinition p1 = getPropertyDefinition("delay", "long", 1, 1);
		PropertyDefinition p2 = getPropertyDefinition("count", "int", 2, 1);
		PropertyDefinition p3 = getPropertyDefinition("interval", "long", 3, 1);
		PropertyDefinition p4 = getPropertyDefinition("hint", "java.lang.String", 4, 1);
		PropertyDefinition p5 = getPropertyDefinition("clazz", "java.lang.String", 5, 1);
		PropertyDefinition p6 = getPropertyDefinition("mtd", "java.lang.String", 6, 1);
		PropertyDefinition p7 = getPropertyDefinition("action", "java.lang.String", 7, 1);
		PropertyDefinition p8 = getPropertyDefinition("name", "java.lang.String", 8, 1);
		PropertyDefinition p9 = getPropertyDefinition("group", "java.lang.String", 9, 1);

		e.getProps().put("" + p1.getIndex(), p1);
		e.getProps().put("" + p2.getIndex(), p2);
		e.getProps().put("" + p3.getIndex(), p3);
		e.getProps().put("" + p4.getIndex(), p4);
		e.getProps().put("" + p5.getIndex(), p5);
		e.getProps().put("" + p6.getIndex(), p6);
		e.getProps().put("" + p7.getIndex(), p7);
		e.getProps().put("" + p8.getIndex(), p8);
		e.getProps().put("" + p9.getIndex(), p9);

		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
	}

}
