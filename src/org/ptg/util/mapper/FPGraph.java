/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.Group;
import org.ptg.util.db.ColDef;
import org.ptg.util.db.TableDef;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class FPGraph {
	String name;
	String gtype;
	String type;
	int order;
	Map<String, ConnDef> forward = new HashMap<String, ConnDef>();
	Map<String, FunctionPoint> fps = new HashMap<String, FunctionPoint>();
	Map<String, Group> groups = new HashMap<String, Group>();
	Map<String, ColDef> colDefs = new HashMap<String, ColDef>();
	Map<String, TableDef> tableDefs = new HashMap<String, TableDef>();
	List<PortObj> ports = new ArrayList<PortObj>();
	List<AnonDefObj> anonDefs = new ArrayList<AnonDefObj>();
	List<TypeDefObj> typeDefs = new ArrayList<TypeDefObj>();
	List<LayerObj> layers = new ArrayList<LayerObj>();
	DirectedSparseMultigraph<FunctionPoint, ConnDef> graph = null;// new
														// DirectedSparseGraph<FunctionPoint,ConnDef>();
	Map<String, JSONObject> orphans = new HashMap<String, JSONObject>();
    ConnDef in;
    ConnDef out;
	List<FunctionPoint> starts = new ArrayList<FunctionPoint>();
	List<FunctionPoint> ends = new ArrayList<FunctionPoint>();

	public FPGraph() {
		init();
	}

	public void init() {
		try {
			graph = null;// new DirectedSparseGraph<FunctionPoint,ConnDef>();
			forward = new HashMap<String, ConnDef>();
			fps = new HashMap<String, FunctionPoint>();
			starts = new ArrayList<FunctionPoint>();
			ends = new ArrayList<FunctionPoint>();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void fromGraphJson(String name, String json) {
		final Map<String, Object> l = CommonUtil.getGraphObjectsFromJsonData(name, json);
		setName(name);
		fromObjectMap(l, null);
	}

	public void fromObjectMap(Map<String, Object> map, String[] filters) {
		Set<String> filter = CommonUtil.getSet(filters);
		try {
			init();
			graph = new DirectedSparseMultigraph<FunctionPoint, ConnDef>();
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
				if (obj instanceof ColDef) {
					ColDef ed = (ColDef) obj;
					colDefs.put(ed.getId(), ed);
					// now add a function point for this definition
					if (ed.getColOp() == null || ed.getColOp().equals("query")) {
						FunctionPoint def = new FunctionPoint();
						def.setId("Function_"+ed.getId());
						def.setName("Function_"+ed.getId());
						def.setCn("rsin");
						def.setType("functionobj");
						def.setXref(ed);
						def.setDataType(ed.getDataType() == null ? "Object" : ed.getDataType());
						// def.setName("${rs}");
						def.setFn("getObject");
						FunctionPoint def2 = new FunctionPoint();
						def2.setId("FP_" + ed.getId());
						def2.setName("FP_" + ed.getId());
						def2.setCn("rsin");
						def2.setFn("getObject");

						def2.setGrp("Function_"+ed.getId());
						def2.setPn(ed.getDataType() == null ? "Object" : ed.getDataType());
						def2.setDataType("java.lang.String");
						def2.setIndex(0);
						def2.setVal("\"" + ed.getName() + "\"");
						def2.setType("paramobj");
						
						FunctionPoint def3 = new FunctionPoint();
						def3.setId(ed.getId());
						def3.setName(ed.getId());
						def3.setCn(ed.getId());
						def3.setFn(null);
						def3.setDataType(ed.getDataType() == null ? "Object" : ed.getDataType());
						def3.setType("classobj");
						fps.put(def.getId(), def);
						fps.put(def2.getId(), def2);
						fps.put(def3.getId(), def3);
						ConnDef cdef = new ConnDef();
						cdef.setFrom("Function_"+ed.getId());
						cdef.setTo(ed.getId());
						cdef.setId("Conn_"+def.getId());
						cdef.setCtype("arbitconnection");
						cdef.setNodes(new String[]{ed.getId(),"Function_"+ed.getId()});
						forward.put("Conn_"+def.getId(), cdef);

					} else {
						FunctionPoint def = new FunctionPoint();
						def.setId("Function_" + ed.getId());
						def.setName("Function_" + ed.getId());
						def.setCn("rsout");
						def.setDataType("void");
						// def.setName("${rs}");
						def.setFn("setObject");
						def.setType("colsetobj");
						def.setXref(ed);
						FunctionPoint def2 = new FunctionPoint();
						def2.setId("FP_1" + ed.getId());
						def2.setName("FP_1" + ed.getId());
						def2.setCn("rsout");
						def2.setFn("setObject");
						def2.setGrp("Function_" + ed.getId());
						def2.setPn(ed.getDataType() == null ? "int" : ed.getDataType());
						def2.setDataType(ed.getDataType() == null ? "int" : ed.getDataType());
						def2.setIndex(0);
						def2.setVal(""+ed.getOrder());
						def2.setType("paramobj");

						FunctionPoint def3 = new FunctionPoint();
						def3.setId(ed.getId());
						def3.setName(ed.getId());
						def3.setCn("rsout");
						def3.setFn("setObject");
						def3.setGrp("Function_" + ed.getId());
						def3.setPn(ed.getDataType() == null ? "Object" : ed.getDataType());
						def3.setDataType(ed.getDataType() == null ? "Object" : ed.getDataType());
						def3.setIndex(1);
						def3.setType("paramobj");

						fps.put(def.getId(), def);
						fps.put(def2.getId(), def2);
						fps.put(def3.getId(), def3);
					}
				}
 
				if (obj instanceof TableDef) {
					TableDef ed = (TableDef) obj;
					tableDefs.put(ed.getId(), ed);
				}
				if (obj instanceof PortObj) {
					PortObj po = (PortObj) obj;
					ports.add(po);
				}
				if (obj instanceof LayerObj) {
					LayerObj po = (LayerObj) obj;
					layers.add(po);
				}

				if (obj instanceof TypeDefObj) {
					TypeDefObj po = (TypeDefObj) obj;
					typeDefs.add(po);
				}
				if (obj instanceof AnonDefObj) {
					AnonDefObj po = (AnonDefObj) obj;
					anonDefs.add(po);
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
			prepareStarts();
			prepareEnds();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void prepareEnds() {
		for (FunctionPoint s : fps.values()) {
			if (graph.getOutEdges(s).size() == 0) {
				ends.add(s);
			}
		}
	}

	public void prepareStarts() {
		for (FunctionPoint s : fps.values()) {
			if (graph.getPredecessorCount(s) == 0) {
				starts.add(s);
			}
		}
	}

	public Map<String, ConnDef> getForward() {
		return forward;
	}

	public void setForward(Map<String, ConnDef> forward) {
		this.forward = forward;
	}

	public Map<String, FunctionPoint> getFunctionPoints() {
		return fps;
	}

	public void setFunctionPoints(Map<String, FunctionPoint> FunctionPoints) {
		this.fps = FunctionPoints;
	}

	public DirectedSparseMultigraph<FunctionPoint, ConnDef> getGraph() {
		return graph;
	}

	public void setGraph(DirectedSparseMultigraph<FunctionPoint, ConnDef> graph) {
		this.graph = graph;
	}

	public List<FunctionPoint> getStarts() {
		return starts;
	}

	public void setStarts(List<FunctionPoint> starts) {
		this.starts = starts;
	}

	public Collection<FunctionPoint> getEnds() {
		return ends;
	}

	public void setEnds(List<FunctionPoint> ends) {
		this.ends = ends;
	}

	public Collection<FunctionPoint> getLongestPath() {
		Collection<FunctionPoint> longest = null;
		List<Collection<FunctionPoint>> rets = getAllPaths();
		for (FunctionPoint child : ends) {
			Collection<FunctionPoint> ret = graph.getNeighbors(child);
			rets.add(ret);
		}
		int max = Integer.MIN_VALUE;

		for (Collection<FunctionPoint> ret : rets) {
			if (max < ret.size()) {
				max = ret.size();
				longest = ret;
			}
		}
		return longest;
	}

	public Collection<FunctionPoint> getShortestPath() {
		Collection<FunctionPoint> shortest = null;
		List<Collection<FunctionPoint>> rets = getAllPaths();
		for (FunctionPoint child : ends) {
			Collection<FunctionPoint> ret = graph.getNeighbors(child);
			rets.add(ret);
		}
		int max = Integer.MAX_VALUE;

		for (Collection<FunctionPoint> ret : rets) {
			if (max > ret.size()) {
				max = ret.size();
				shortest = ret;
			}
		}
		return shortest;
	}

	public List<List<FunctionPoint>> getAllConverging(FunctionPoint fp) {
		List<List<FunctionPoint>> rets = new ArrayList<List<FunctionPoint>>();
		Set<FunctionPoint> endnodes = new HashSet<FunctionPoint>();
		for (FunctionPoint child : graph.getVertices()) {
			if (graph.getPredecessorCount(child) > 1 || (graph.getPredecessorCount(child) > 0 && graph.getOutEdges(child).size() == 0)) {
				endnodes.add(child);
			}
		}
		for (FunctionPoint s : endnodes) {
			List<List<FunctionPoint>> subs = getAllPaths(s, endnodes);
			for (List<FunctionPoint> subpath : subs) {
				if (subpath.get(subpath.size() - 1).equals(fp))
					rets.add(subpath);
			}
		}

		return rets;
	}

	/*
	 * later this should be done via strategy located via loopup /configuration/
	 * choosen by user
	 */
	public List<List<FunctionPoint>> getAllSubPaths() {
		List<List<FunctionPoint>> rets = new ArrayList<List<FunctionPoint>>();
		Set<FunctionPoint> endnodes = new HashSet<FunctionPoint>();
		for (FunctionPoint child : graph.getVertices()) {
			if (graph.getOutEdges(child).size() > 1 || graph.getPredecessorCount(child) > 1 || graph.getPredecessorCount(child) == 0) {
				endnodes.add(child);
			}
		}
		for (FunctionPoint s : endnodes) {
			List<List<FunctionPoint>> subs = getAllPaths(s, endnodes);
			for (List<FunctionPoint> subpath : subs) {
				rets.add(subpath);
			}
		}

		return rets;
	}

	public Collection<FunctionPoint>  getRoots(){
		Collection<FunctionPoint>  ret = new ArrayList<FunctionPoint>();
		Collection<FunctionPoint>vert = graph.getVertices();
		for(FunctionPoint fp: vert){
			if(graph.getInEdges(fp).size()==0){
				ret.add(fp);
			}
		}
		return ret;
		
	}
	public List<Collection<FunctionPoint>> getAllPaths() {
		List<Collection<FunctionPoint>> rets = new ArrayList<Collection<FunctionPoint>>();
		Collection<FunctionPoint> rootNodes = getRoots();
		for (FunctionPoint s : rootNodes) {
			List<List<FunctionPoint>> subs = getAllPaths(s, null);
			for (List<FunctionPoint> subpath : subs) {
				rets.add(subpath);
			}
		}

		return rets;
	}

	public List<List<FunctionPoint>> getAllPathsv3(String FunctionPoint, Collection<FunctionPoint> terminals) {
		FunctionPoint s = fps.get(FunctionPoint);
		return getAllPaths(s, terminals);
	}

	public List<List<FunctionPoint>> getAllPathsv2(String FunctionPoint, Collection<String> terminals) {
		Collection<FunctionPoint> t = new ArrayList<FunctionPoint>();
		FunctionPoint s = fps.get(FunctionPoint);
		for (String st : terminals) {
			FunctionPoint temp = fps.get(st);
			t.add(temp);
		}
		return getAllPaths(s, t);
	}

	public List<List<FunctionPoint>> getAllPaths(FunctionPoint s, Collection<FunctionPoint> terminals) {
		List<List<FunctionPoint>> rets = new ArrayList<List<FunctionPoint>>();
		List<FunctionPoint> path = new ArrayList<FunctionPoint>();
		Collection<FunctionPoint> ss = graph.getSuccessors(s);
		path.add(s);
		while (ss.size() > 0) {

			if (ss.size() > 1) {
				for (FunctionPoint sub : ss) {
					if (terminals != null && terminals.contains(sub))
						break;
					List<List<FunctionPoint>> subpaths = getAllPaths(sub, terminals);
					for (List<FunctionPoint> subsub : subpaths) {
						List<FunctionPoint> subsubpath = new ArrayList<FunctionPoint>();
						for (FunctionPoint pathFunctionPoints : path) {
							subsubpath.add(pathFunctionPoints);
						}
						for (FunctionPoint subsubFunctionPoint : subsub) {
							subsubpath.add(subsubFunctionPoint);
							if (terminals != null && terminals.contains(subsubFunctionPoint))
								break;
						}
						rets.add(subsubpath);
					}
				}
				return rets;
			} else {
				FunctionPoint current = ss.iterator().next();
				path.add(current);
				/*
				 * if we want to include the next FunctionPoint then do after
				 * adding otherwise before adding
				 */
				if (terminals != null && terminals.contains(current))
					break;
				ss = graph.getSuccessors(current);
			}
		}
		rets.add(path);
		return rets;
	}

	public boolean pathHasDeteor(Collection<FunctionPoint> path) {
		for (FunctionPoint s : path) {
			if (graph.getSuccessorCount(s) > 1 || graph.getPredecessorCount(s) > 1) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Group> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}

	public List<Boolean> pathStatus(List<ConnDef> sp) {
		List<Boolean> finding = new ArrayList<Boolean>();
		for (ConnDef s : sp) {
			boolean b = false;
			for (ConnDef def : graph.getEdges()) {
				if (def.getFrom().equals(s.getFrom()) && def.getTo().equals(s.getTo())) {
					b = true;
				}
			}
			finding.add(b);
		}

		return finding;
	}

	public boolean fullPathExists(List<String> sp) {
		List<ConnDef> cdefs = new ArrayList<ConnDef>();
		for (int i = 0; i < sp.size() - 1; i++) {
			ConnDef def = new ConnDef();
			String s1 = sp.get(i);
			String s2 = sp.get(i + 1);
			def.setFrom(s1);
			def.setTo(s2);
			def.setNodes(new String[] { s1, s2 });
			cdefs.add(def);
		}
		List<Boolean> finding = pathStatus(cdefs);
		return CommonUtil.allTrue(finding);
	}

	public boolean findConnPathExists(List<ConnDef> sp) {
		List<Boolean> finding = pathStatus(sp);
		return CommonUtil.allTrue(finding);
	}

	public ConnDef createConnection(String s1, String s2) {
		ConnDef def = new ConnDef();
		def.setId("Random_" + CommonUtil.getRandomString(6));
		def.setFrom(s1);
		def.setTo(s2);
		def.setNodes(new String[] { s1, s2 });
		return def;
	}

	@Override
	public String toString() {
		return "Graph [name=" + name + "]";
	}

	public Map<String, JSONObject> getOrphans() {
		return orphans;
	}

	public void setOrphans(Map<String, JSONObject> orphans) {
		this.orphans = orphans;
	}

	public ConnDef getIn() {
		return in;
	}

	public void setIn(ConnDef in) {
		this.in = in;
	}

	public ConnDef getOut() {
		return out;
	}

	public void setOut(ConnDef out) {
		this.out = out;
	}

	public String getGtype() {
		return gtype;
	}

	public void setGtype(String gtype) {
		this.gtype = gtype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<AnonDefObj> getAnonDefs() {
		return anonDefs;
	}

	public void setAnonDefs(List<AnonDefObj> anonDefs) {
		this.anonDefs = anonDefs;
	}
	
}
/**/