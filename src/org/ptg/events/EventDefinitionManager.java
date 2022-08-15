/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.admin.AppContext;
import org.ptg.admin.WebStartProcess;
import org.ptg.util.Constants;
import org.ptg.util.DynaObjectHelper;
import org.ptg.util.IEventDBTransformer;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.SpringHelper;
import org.ptg.util.db.DBHelper;
import org.ptg.util.db.DBTransformerHelper;
import org.ptg.velocity.VelocityHelper;

public class EventDefinitionManager {
	Map<String, EventDefinition> defs = new HashMap<String, EventDefinition>();
	String loadSQl = "select e.type as etype,e.eventstore as estore, e.id as eid,ed.type as edtype,ed.eventType as edeventType,ed.name as edname,ed.idx as edidx,ed.searchable as edsearchable,ed.id as edid,ed.ds as edds  from events e left outer join event_definition ed on e.id = ed.eventType order by ed.idx";
	String insertStream = "insert into events (type,eventstore) values (?,?)";
	String insertStreamDefinition = "insert into event_definition (name, type,eventtype,idx,searchable,ds) values (?,?,?,?,?,?)";

	public void loadEventDefinitions() {
		try {
			DBHelper inst = DBHelper.getInstance();
			EventDefinitionLoader l = new EventDefinitionLoader(defs);
			inst.forEach(loadSQl, l);
			AppContext.getInstance().setStat("EventTypeCount", defs.size());
			System.out.println("Loaded " + defs.size() + " Event Types");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateEventClasses() {
		for (EventDefinition e : defs.values()) {
			try {
				buildDefinition(e);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("Failed to generate class for : " + e.getType());
			}
		}
	}

	public void ensureDBTables() {
		for (EventDefinition e : defs.values()) {
			createEventTable(e);
		}
	}

	public void generateEventDBTransformerClasses() {
		for (EventDefinition e : defs.values()) {
			buildDBTransformerDefinition(e, false);
		}
	}

	public EventDefinition getEventDefinition(String type) {
		return defs.get(type);
	}

	public Map<String, EventDefinition> getEventDefinitions() {
		return defs;
	}

	public void saveEvent(EventDefinition ed) {
		if (defs.get(ed.getType()) == null) {
			saveToDB(ed);
			ed.setDirty(true);
			defs.put(ed.getType(), ed);
		}
		createEventTable(ed);

	}

	private void createEventTable(EventDefinition ed) {
		String store = ed.getEventStore();
		String[] tables = DBHelper.getInstance().getTableNames("", "PUBLIC", store);
		if (tables == null || tables.length == 0) {
			Map m = new HashMap();
			m.put("store", store);
			StringBuilder data = new StringBuilder();
			for (int i = 1; i <= ed.getProps().values().size() + 8; i++) {
				String dt = "`a" + i + "` varchar(256) default NULL,";
				data.append(dt);
			}
			m.put("coldata", data.toString());
			StringBuffer buf = VelocityHelper.burnTemplate(m, (String) SpringHelper.get("eventtableTemplate"));
			DBHelper.getInstance().executeUpdate(buf.toString());
		}

	}

	public void deleteEventDefinition(EventDefinition ed) {
		if (ed == null) {
			return;
		}
		DBHelper inst = DBHelper.getInstance();
		Connection conn = inst.createConnection();
		Statement stmt = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String sqlTemp = "delete from event_definition where eventType in (select id from events where type =\'" + ed.getType() + "\')";
			System.out.println("Now executing : " + sqlTemp);
			stmt.executeUpdate(sqlTemp);
			conn.commit();
			sqlTemp = "delete from events where type=\'" + ed.getType() + "\'";
			System.out.println("Now executing : " + sqlTemp);
			stmt.executeUpdate(sqlTemp);
			ed.setId(inst.getLastId(conn));
			conn.commit();
			defs.remove(ed.getType());
		} catch (SQLException e) {
			inst.rollback(conn);
			e.printStackTrace();
		} finally {
			inst.closeStmt(stmt);
			inst.closeConnection(conn);
		}
	}

	private void saveToDB(EventDefinition ed) {
		DBHelper inst = DBHelper.getInstance();
		Connection conn = inst.createConnection();
		PreparedStatement stmt = null;
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			stmt = conn.prepareStatement(insertStream);
			stmt.setString(1, ed.getType());
			stmt.setString(2, ed.getEventStore());
			stmt.executeUpdate();
			ed.setId(inst.getLastId(conn));
			for (PropertyDefinition sd : ed.getProps().values()) {
				saveToDB(conn, sd, ed);
			}
			inst.commit(conn);
		} catch (SQLException e) {
			inst.rollback(conn);
			e.printStackTrace();
		} finally {
			inst.closeStmt(stmt);
			inst.closeConnection(conn);
			WebStartProcess p = WebStartProcess.getInstance();
			if (p != null) {
				EventDefinitionUpdateEvent e = new EventDefinitionUpdateEvent();
				e.setName(ed.getType());
				
			}
		}
	}

	private void saveToDB(Connection conn, PropertyDefinition sd, EventDefinition s) throws SQLException {
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement(insertStreamDefinition);
		stmt.setString(1, sd.getName());
		stmt.setString(2, sd.getType());
		stmt.setInt(3, sd.getEventType() == 0 ? s.getId() : sd.getEventType());
		stmt.setInt(4, sd.getIndex());
		stmt.setInt(5, sd.getSearchable());
		stmt.setString(6, sd.getDataStructure());
		stmt.executeUpdate();
	}

	public Class buildDefinition(String s) {
		EventDefinition e = defs.get(s);
		if (e == null) {
			return null;
		}
		return buildDefinition(e);
	}

	public Class buildDefinition(EventDefinition e) {
		Map<String, String> def = new HashMap<String, String>();
		Map<String, Map<String, Object>> hints = new HashMap<String, Map<String, Object>>();
		for (PropertyDefinition pd : e.getProps().values()) {
			def.put(pd.getName(), pd.getType());
			Map<String, Object> anno = new HashMap<String, Object>();
			anno.put(Constants.Searchable, pd.getSearchable() == 1);
			anno.put(Constants.Index, pd.getIndex());
			hints.put(pd.getName(), anno);
		}
		Class eventClass = DynaObjectHelper.getClass(e.getType(), def, hints, e.isDirty());
		e.setDirty(false);
		String s = e.getType();
		String nm = StringUtils.substringAfterLast(s, ".");
		if (nm.isEmpty()) {
			nm = s;
		}
		return eventClass;
	}

	public Class buildDBTransformerDefinition(String s, boolean useRealname) {
		EventDefinition e = defs.get(s);
		if (e == null) {
			return null;
		}
		return buildDBTransformerDefinition(e, useRealname);
	}

	public Class buildDBTransformerDefinition(String s) {
		EventDefinition e = defs.get(s);
		if (e == null) {
			return null;
		}
		return buildDBTransformerDefinition(e, false);
	}

	public boolean defined(String s) {
		return defs.get(s) != null;

	}

	public Class buildDBTransformerDefinition(EventDefinition e) {
		return buildDBTransformerDefinition(e, false);
	}

	public Class buildDBTransformerDefinition(EventDefinition e, boolean userRealName) {
		Map<String, String> def = new LinkedHashMap<String, String>();
		Map<String, Map<String, Object>> hints = new LinkedHashMap<String, Map<String, Object>>();
		for (PropertyDefinition pd : e.getProps().values()) {
			def.put(pd.getName(), pd.getType());
			Map<String, Object> anno = new LinkedHashMap<String, Object>();
			anno.put(Constants.Searchable, pd.getSearchable() == 1);
			anno.put(Constants.Index, pd.getIndex());
			hints.put(pd.getName(), anno);
		}

		Class transformer = DBTransformerHelper.getTransformerClass(e.getType(), def, hints, e.getEventStore(), userRealName);
		if (transformer != null) {
			IEventDBTransformer xfer = (IEventDBTransformer) ReflectionUtils.createInstance(transformer.getName());
			xfer.setStore(e.getEventStore());
		}
		return transformer;
	}

	private static class SingletonHolder {
		private static final EventDefinitionManager INSTANCE = new EventDefinitionManager();
		static {
			INSTANCE.loadEventDefinitions();
		}
	}

	public static EventDefinitionManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

}
