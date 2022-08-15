/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ptg.admin.AppContext;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.db.DBHelper;

public class ProcessorManager {
	Map<String, IProcessor> processorRoutingTable = new HashMap<String, IProcessor>();
	Map<String, Map<String, Object>> processors = new HashMap<String, Map<String, Object>>();

	private static class SingletonHolder {
		private static final ProcessorManager INSTANCE = new ProcessorManager();
		static {
		}
	}

	public static ProcessorManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void init() {
		loadProcessors();
	}

	public void registerProcessorType(String name, String handlerClass, String query, String pconfig) throws GenericException {
		if (processors.get(name) == null) {
			Map<String, Object> an = new HashMap<String, Object>();
			an.put("handlerClass", handlerClass);
			an.put("query", query);
			an.put("configItems", pconfig);
			processors.put(name, an);
		} else {
			throw new GenericException("Process is already registered " + processors.get(name).getClass().getName());
		}

	}

	public Map<String, Object> getProcessor(String type) {
		return processors.get(type);
	}

	public IProcessor attach(String stream, String processor) throws GenericException {
		System.out.println("Now attaching: " + processor);
		ProxyProcessor pp = (ProxyProcessor) processorRoutingTable.get(stream);
		if (pp == null) {
			IProcessor o = null;
			Map<String, Object> an = processors.get(processor);
			if (an != null) {
				String pname = (String) an.get("handlerClass");
				String query = (String) an.get("query");
				String pconfig = (String) an.get("configItems");
				if (pname != null) {
					o = (IProcessor) ReflectionUtils.createInstance(pname, new Object[0]);
					if (o != null) {
						an.put("streamname", stream);
						pp = new ProxyProcessor(o);
						pp.setName(processor);
						pp.setQuery(query);
						pp.setConfigItems(pconfig);
						pp.setStreamName(stream);
						pp.attach(stream);

						processors.put(processor, an);// update the processors
														// with stream name
						processorRoutingTable.put(stream, pp);
					}
				}
			}
		} else {
			Map<String, Object> an = processors.get(processor);
			if (an != null) {
				an.put("streamname", stream);
				reattach(processor);
			}
		}
		return pp;
	}

	public IProcessor reattach(String processor) throws GenericException {
		System.out.println("Now re attaching: " + processor);
		ProxyProcessor pp = null;
		IProcessor o = null;
		Map<String, Object> an = processors.get(processor);
		if (an != null) {
			String pname = (String) an.get("handlerClass");
			String query = (String) an.get("query");
			String streamname = (String) an.get("streamname");
			String pconfig = (String) an.get("configItems");
			if (pname != null && streamname != null) {
				o = (IProcessor) ReflectionUtils.createInstance(pname, new Object[0]);
				if (o != null) {
					pp = (ProxyProcessor) processorRoutingTable.get(streamname);
					if(pp!=null){
					try {
						pp.lock();
						pp.detach();
						pp.setInnerProcessor(o);
						pp.setName(processor);
						pp.setQuery(query);
						pp.setConfigItems(pconfig);
						pp.attach(streamname);
					} finally {
						if(pp!=null)
						pp.unlock();
					}
					}
				}
			}
		}
		return pp;
	}

	public IProcessor getProcessorFromRoutingTable(String key) {
		for (IProcessor p : processorRoutingTable.values()) {
			if (p instanceof ProxyProcessor) {
				ProxyProcessor pp = (ProxyProcessor) p;
				if (pp.getInnerProcessor() != null && pp.getInnerProcessor().getName()!=null&& pp.getInnerProcessor().getName().equals(key)) {
					return p;
				}
			} else {
				if (p.getName().equals(key)) {
					return p;
				}

			}
		}
		return null;
	}

	public Map<String, IProcessor> getProcessorRoutingTable() {
		return processorRoutingTable;
	}

	public Map<String, Map<String, Object>> getProcessors() {
		return processors;
	}

	public void loadProcessors() {
		DBHelper helper = DBHelper.getInstance();
		ProcessorsLoader loader = new ProcessorsLoader(this);
		helper.forEach("select * from processors", loader);
		AppContext.getInstance().setStat("ProcessorsCount", processors.size());
		System.out.println("Loaded " + processors.size() + " Processors");
	}

	public void registerProcessorType(ProcessorDef def) throws GenericException {
		if (processors.get(def.getName()) == null) {
			Map<String, Object> an = new HashMap<String, Object>();
			an.put("handlerClass", def.getClz());
			an.put("query", def.getQuery());
			an.put("configItems", def.getConfigItems());
			processors.put(def.getName(), an);
		} else {
			System.out.println("Process is already registered reregistering" + processors.get(def.getName()).getClass().getName());
			Map<String, Object> an = processors.get(def.getName());
			an.put("handlerClass", def.getClz());
			an.put("query", def.getQuery());
			an.put("configItems", def.getConfigItems());
			processors.put(def.getName(), an);
			reattach(def.getName());
		}
	}

	public void saveProcessorDef(ProcessorDef def) {
		DBHelper helper = DBHelper.getInstance();
		Connection conn = null;
		try {
			try {
				conn = helper.getInstance().createConnection();
				conn.setAutoCommit(false);
				java.sql.PreparedStatement stmt = conn.prepareStatement("insert into processors (name,clz,query,configItems) values (?,?,?,?)");
				stmt.setString(1, def.getName());
				stmt.setString(2, def.getClz());
				stmt.setString(3, def.getQuery());
				stmt.setString(4, def.getConfigItems());
				stmt.execute();
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} finally {
			helper.getInstance().closeConnection(conn);
		}
	}

	public void deleteProcessorDef(String name) {
		DBHelper helper = DBHelper.getInstance();
		Connection conn = null;
		try {
			try {
				conn = helper.getInstance().createConnection();
				conn.setAutoCommit(false);
				java.sql.PreparedStatement stmt = conn.prepareStatement("delete from processors where name = ?");
				stmt.setString(1, name);
				stmt.execute();
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} finally {
			helper.getInstance().closeConnection(conn);
		}
	}

	public void deleteProcessorDef(ProcessorDef def) {
		DBHelper helper = DBHelper.getInstance();
		Connection conn = null;
		try {
			try {
				conn = helper.getInstance().createConnection();
				conn.setAutoCommit(false);
				java.sql.PreparedStatement stmt = conn.prepareStatement("delete from processors where name = ?");
				stmt.setString(1, def.getName());
				stmt.execute();
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} finally {
			helper.getInstance().closeConnection(conn);
		}
	}

}
