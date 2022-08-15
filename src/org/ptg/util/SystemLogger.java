/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util;

import org.ptg.events.Event;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.events.TraceEvent;
import org.ptg.util.db.DBHelper;

public class SystemLogger {
	private IEventDBTransformer dbtransformer;
	private EventDefinition ed;
	private static SystemLogger INSTANCE;
	private IEventDBTransformer xfer;

	public SystemLogger() {
		ed = EventDefinitionManager.getInstance().getEventDefinition(new TraceEvent().getEventType());
		if (ed != null) {
			Class c = EventDefinitionManager.getInstance().buildDBTransformerDefinition(new TraceEvent().getEventType());
			if (c != null) {
				xfer = (IEventDBTransformer) ReflectionUtils.createInstance(c.getName());
				xfer.setStore(ed.getEventStore());
			}
			DBHelper.getInstance().truncateTable(ed.getEventStore());
		}

	}

	public void sendTrace(String stmt, String origin) {
		Event evt = new TraceEvent(stmt, origin, "T");
		CommonUtil.publishEvent(evt);
	}

	public void sendException(String stmt, String origin) {
		Event evt = new TraceEvent(stmt, origin, "E");
		CommonUtil.publishEvent(evt);
	}

	public void sendLog(String stmt, String origin) {
		Event evt = new TraceEvent(stmt, origin, "L");
		CommonUtil.publishEvent(evt);
	}

	public void saveLog(String stmt, String origin) {
		Event evt = new TraceEvent(stmt, origin, "L");
		if (xfer != null) {
			xfer.saveToDb(evt);
			CommonUtil.publishEvent(evt);
		}
	}

	public void saveException(String stmt, String origin) {
		Event evt = new TraceEvent(stmt, origin, "E");
		if (xfer != null) {
			xfer.saveToDb(evt);
			CommonUtil.publishEvent(evt);
		}
	}

	public void saveLog(String stmt) {
		Event evt = new TraceEvent(stmt, "SystemEvent", "L");
		if (xfer != null) {
			xfer.saveToDb(evt);
			CommonUtil.publishEvent(evt);
		}
	}

	public void saveException(String stmt) {
		Event evt = new TraceEvent(stmt, "SystemEvent", "E");
		if (xfer != null) {
			xfer.saveToDb(evt);
			CommonUtil.publishEvent(evt);
		}
	}
}
