/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.mapper.FunctionPoint;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class DFStateMachineCompiler {
	String name;
	Map<String, List<FunctionPoint>> grps = null;

	DirectedSparseMultigraph<FunctionPoint, ConnDef> graph = null;// new
	Map<String, ConnDef> forward = new HashMap<String, ConnDef>();
	Map<String, FunctionPoint> fps = new HashMap<String, FunctionPoint>();
	Map<String, Group> groups = new HashMap<String, Group>();
	Map<String, JSONObject> orphans = new HashMap<String, JSONObject>();
	Map<String, String> stateVars = new HashMap<String, String>();

	public void init() {
		try {
			graph = new DirectedSparseMultigraph<FunctionPoint, ConnDef>();
			grps = new HashMap<String, List<FunctionPoint>>();
			forward = new HashMap<String, ConnDef>();
			fps = new HashMap<String, FunctionPoint>();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void fromGraphJson(String name, String json) {
		final Map<String, Object> l = CommonUtil.getGraphObjectsFromJsonData(name, json);
		this.name = (name);
		fromObjectMap(l, null);
	}

	public void fromObjectMap(Map<String, Object> map, String[] filters) {
		Set<String> filter = CommonUtil.getSet(filters);
		try {
			init();
			Collection ret = map.values();
			for (Object obj : ret) {
				if (obj instanceof ConnDef) {
					ConnDef def = (ConnDef) obj;
					forward.put(def.getId(), def);
				}
				if (obj instanceof FunctionPoint) {
					FunctionPoint def = (FunctionPoint) obj;
					fps.put(def.getName(), def);
				}
				if (obj instanceof Group) {
					Group ed = (Group) obj;
					groups.put(ed.getId(), ed);
				}
				if (obj instanceof JSONObject) {
					JSONObject jo = (JSONObject) obj;
					String id = jo.getString("id");
					orphans.put(id, jo);
				}
			}
			// first add vertexes
			for (FunctionPoint s : fps.values()) {
				if (filter.contains(s.getName()) == false)
					graph.addVertex(s);
			}
			// then add nodes
			for (ConnDef def : forward.values()) {
				FunctionPoint from = fps.get(def.getFrom());
				FunctionPoint to = fps.get(def.getTo());
				if (from != null && to != null) {
					graph.addEdge(def, new edu.uci.ics.jung.graph.util.Pair(from, to));
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	public String compile() {
		// create needed vars
		Set<FunctionPoint> visited = new HashSet<FunctionPoint>();
		List<String> codes = new LinkedList<String>();
		Collection<FunctionPoint> fps = graph.getVertices();

		// create starts

		// loose hell
		int i = 0;
		Collection<ConnDef> defs = graph.getEdges();

		List<FunctionPoint> start = new ArrayList<FunctionPoint>();

		// create starts
		for (FunctionPoint fp : fps) {
			if (graph.getPredecessorCount(fp) == 0) {
				start.add(fp);
			}
		}
		for (FunctionPoint fp : start) {
			StringBuilder strb = new StringBuilder();
			visit(fp, strb, 0);
			codes.add(strb.toString());
		}
		StringBuilder sb = new StringBuilder();
		for (String en : codes) {
			sb.append(en);
		}
		return sb.toString();
	}

	public void visit(FunctionPoint fp, StringBuilder sb, int depth) {
		Collection<ConnDef> childs = graph.getOutEdges(fp);
		// sort childs
		List<ConnDef> sorted = new ArrayList<ConnDef>();
		for (ConnDef c : childs) {
			sorted.add(null);
		}
		int ir = 0;
		for (ConnDef c : childs) {
			String cd = c.getConnCond();
			if (cd.contains(":")) {
				String[] it = cd.split(":");
				int i = Integer.parseInt(it[0]);
				String cond = it[1];
				cond = StringUtils.trim(cond);
				c.setConnCond(cond);// remember we have to fix the cond
				if (i > sorted.size()) {
					Object o = null;
					do {
						o = sorted.get(ir);
						ir++;
					} while (o != null);
					sorted.set(ir - 1, c);
				} else {
					sorted.set(i, c);
				}
			} else {
				Object o = null;
				do {
					o = sorted.get(ir);
					ir++;
				} while (o != null);
				sorted.set(ir - 1, c);

			}
		}
		for (Iterator<ConnDef> iter = sorted.iterator(); iter.hasNext();) {
			ConnDef d = iter.next();
			String v = d.getConnCond();
			for (int j = 0; j < depth; j++)
				sb.append("\t");
			sb.append("if (" + v + "){\n");
			visit(fps.get(d.getTo()), sb, depth + 1);
			for (int j = 0; j < depth ; j++)
				sb.append("\t");
			String val = "";
			FunctionPoint to  =fps.get(d.getTo());
			if(to!=null&&to.getVal()!=null&&to.getVal().length()>0){
				val  = to.getVal();
			}
			if(val!=null&&val.length()>0){
				for (int j = 0; j < depth ; j++)
					sb.append("\t");
				sb.append(val+" ; \n");
			}
			sb.append("}\n");
		}

	}

	String getVars() {
		StringBuilder ret = new StringBuilder();
		int i = 0;
		ret.append("/*******************State machine variables*******************/" + SystemUtils.LINE_SEPARATOR);
		ret.append("/*current state holder*/" + SystemUtils.LINE_SEPARATOR);
		ret.append("protected int state = -1;" + SystemUtils.LINE_SEPARATOR);
		for (String s : stateVars.values()) {
			ret.append("protected int " + s + " = " + i + " ; " + SystemUtils.LINE_SEPARATOR);
			i++;
		}
		ret.append("/*******************END*******************/" + SystemUtils.LINE_SEPARATOR);
		return ret.toString();
	}

}