/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.mapper.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ptg.models.Shape;
import org.ptg.processors.ConnDef;
import org.ptg.processors.ProcessorDef;
import org.ptg.stream.Stream;
import org.ptg.util.CommonUtil;
import org.ptg.util.Group;
import org.ptg.util.db.ColDef;
import org.ptg.util.db.SQLObj;
import org.ptg.util.db.TableDef;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.LayerObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.StepObj;
import org.ptg.util.mapper.TypeDefObj;

import net.sf.json.JSONObject;

public class FPGraph2 {

	String name;
	Map<String, ConnDef> forward = new HashMap<String, ConnDef>();
	Map<String, FunctionPoint> fps = new HashMap<String, FunctionPoint>();
	Map<String, Group> groups = new HashMap<String, Group>();
	Map<String, FPGraph> subgraphs = new HashMap<String, FPGraph>();
	Map<String, JSONObject> orphans = new HashMap<String, JSONObject>();
	FPGraph mainGraph = new FPGraph();
	Map<String, ColDef> colDefs = new HashMap<String, ColDef>();
	Map<String, TableDef> tableDefs = new HashMap<String, TableDef>();
	Map<String, FunctionPoint> aux = new HashMap<String, FunctionPoint>();
	List<FunctionPoint> starts = new ArrayList<FunctionPoint>();
	List<FunctionPoint> ends = new ArrayList<FunctionPoint>();
	Map<String,PortObj> ports = new LinkedHashMap<String,PortObj>();
	List<AnonDefObj> anonDefs = new ArrayList<AnonDefObj>();
	List<TypeDefObj> typeDefs = new ArrayList<TypeDefObj>();
	List<LayerObj> layers = new ArrayList<LayerObj>();
	Map<String,Stream> streams = new HashMap<String,Stream>();
	Map<String,ProcessorDef> procs = new HashMap<String,ProcessorDef>();
	Map<String,StepObj>steps = new HashMap<String,StepObj>();
	Map<String,Shape>shapes = new HashMap<String,Shape>();
	
	public FPGraph2() {
		init();
	}

	public void init() {
		try {
			forward = new HashMap<String, ConnDef>();
			fps = new HashMap<String, FunctionPoint>();
			starts = new ArrayList<FunctionPoint>();
			ends = new ArrayList<FunctionPoint>();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public Map<String, Shape> getShapes() {
		return shapes;
	}

	public void setShapes(Map<String, Shape> shapes) {
		this.shapes = shapes;
	}

	public void fromGraphJson(String name, String json) {
		json = json.replace("NaN", "\"0\"");
		json = json.replace("undefined", "\"\"");
		System.out.println(json);
		final Map<String, Object> l = CommonUtil.getGraphObjectsFromJsonData(name, json);
		setName(name);
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
				if (obj instanceof SQLObj) {
					SQLObj ed = (SQLObj) obj;

					FunctionPoint def = new FunctionPoint();
					def.setXref(ed);
					def.setId(ed.getId());
					def.setName(ed.getId());
					if (ed.getSqlType().equals("query")) {
						String fncode = "java.sql.Resultset rs" + ed.getId() + " = stmt" + ed.getId() + ".executeQuery(\"" + ed.getSql() + "\");";
						fncode = " java.sql.Statement stmt" + ed.getId() + " = " + "conn" + ".getStatement()\n" + fncode;
						def.setFn(fncode);
					} else if (ed.getSqlType().equals("insert") || ed.getSqlType().equals("update")) {
						def.setFn("${rs} = ${stmt}" + ".executeUpdate(\"" + ed.getSql() + "\");");
					}
					def.setDataType("java.sql.Statement");
					def.setType("sqlobj");
					aux.put(def.getName(), def);
				}
				if (obj instanceof ColDef) {
					ColDef ed = (ColDef) obj;
					colDefs.put(ed.getId(), ed);
					// now add a function point for this definition
					if (ed.getColOp() == null || ed.getColOp().equals("query")) {
						FunctionPoint def = new FunctionPoint();
						def.setId("Function_" + ed.getId());
						def.setName("Function_" + ed.getId());
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

						def2.setGrp("Function_" + ed.getId());
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
						cdef.setFrom("Function_" + ed.getId());
						cdef.setTo(ed.getId());
						cdef.setId("Conn_" + def.getId());
						cdef.setCtype("arbitconnection");
						cdef.setNodes(new String[] { ed.getId(), "Function_" + ed.getId() });
						forward.put("Conn_" + def.getId(), cdef);

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
						def2.setVal("" + ed.getOrder());
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
					ports.put(po.getId(),po);
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
				if(obj instanceof Stream){
					streams.put(((Stream) obj).getName(), (Stream)obj);
				}
				if(obj instanceof ProcessorDef){
					procs.put(((ProcessorDef) obj).getName(), (ProcessorDef)obj);
				}
				if(obj instanceof StepObj){
					steps.put(((StepObj) obj).getId(), (StepObj)obj);
				}
				if(obj instanceof Shape){
					shapes.put(((Shape) obj).getId(), (Shape)obj);
				}
			}
			for (JSONObject o : orphans.values()) {
				if (o.containsKey("type") && o.containsKey("gtype")) {
					String type = o.getString("type");
					String gtype = o.getString("gtype");
					int order = -1;
					if (o.has("order")) {
						order = Integer.valueOf(o.getString("order"));
					}

					if (type.equals("region") && (gtype.equals("order") || gtype.equals("foreach") || gtype.equals("while") || gtype.equals("for") || gtype.equals("loop") || gtype.equals("values") || gtype.equals("if"))) {
						String[] items = (String[]) o.getJSONArray("items").toArray(new String[0]);
						Set<String> it = CommonUtil.getSet(items);
						Collection<ConnDef> col = new ArrayList<ConnDef>();
						String name = o.getString("id");
						if (items != null) {
							for (ConnDef cd : forward.values()) {
								if (it.contains(cd.getFrom()) && it.contains(cd.getTo())) {
									col.add(cd);
								}
							}
						}
						Map<String, Object> gmap = new HashMap<String, Object>();
						for (String s : items) {
							Object fptemp = fps.get(s);
							if (fptemp != null) {
								gmap.put(s, fptemp);
							}
							fptemp = orphans.get(s);
							if (fptemp != null) {
								gmap.put(s, fptemp);
							}
							fptemp = groups.get(s);
							if (fptemp != null) {
								gmap.put(s, fptemp);
							}
						}
						for (ConnDef cd : col) {
							gmap.put(cd.getId(), cd);
						}
						ConnDef in = null, out = null;
						for (ConnDef cd : forward.values()) {
							if (cd.getTo().equals(name)) {
								in = cd;
							}
							if (cd.getFrom().equals(name)) {
								out = cd;
							}
						}
						FPGraph fp = new FPGraph();
						fp.setOut(out);
						fp.setIn(in);
						fp.init();
						fp.setName(name);
						fp.setType(type);
						fp.setGtype(gtype);
						fp.setOrder(order);
						fp.fromObjectMap(gmap, null);
						subgraphs.put(name, fp);
						gmap.clear();
					}

				}
			}
			Map<String, Object> gmap = new HashMap<String, Object>();
			for (FunctionPoint fpm : fps.values()) {
				boolean found = false;
				for (FPGraph ff : subgraphs.values()) {
					if (ff.getFunctionPoints().containsKey(fpm.getId())) {
						found = true;
						break;
					}
				}
				if (found == false) {
					gmap.put(fpm.getId(), fpm);
				}
			}
			for (ConnDef fpm : forward.values()) {
				boolean found = false;
				for (FPGraph ff : subgraphs.values()) {
					if (ff.getForward().containsKey(fpm.getId())) {
						found = true;
						break;
					}
				}
				if (found == false) {
					gmap.put(fpm.getId(), fpm);
				}
			}
			mainGraph = new FPGraph();
			mainGraph.setName("main" + name);
			mainGraph.init();
			mainGraph.fromObjectMap(gmap, null);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public Map<String, StepObj> getSteps() {
		return steps;
	}

	public void setSteps(Map<String, StepObj> steps) {
		this.steps = steps;
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

	public FPGraph getGraph(String s) {
		return subgraphs.get(s);
	}

	public void setGraph(String s, FPGraph graph) {
		this.subgraphs.put(s, graph);
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

	public Map<String, FPGraph> getSubgraphs() {
		return subgraphs;
	}

	public void setSubgraphs(Map<String, FPGraph> subgraphs) {
		this.subgraphs = subgraphs;
	}

	public FPGraph getMainGraph() {
		return mainGraph;
	}

	public void setMainGraph(FPGraph mainGraph) {
		this.mainGraph = mainGraph;
	}

	public Map<String, FunctionPoint> getAux() {
		return aux;
	}

	public void setAux(Map<String, FunctionPoint> aux) {
		this.aux = aux;
	}

	public Map<String, FunctionPoint> getFps() {
		return fps;
	}

	public void setFps(Map<String, FunctionPoint> fps) {
		this.fps = fps;
	}

	public Map<String, ColDef> getColDefs() {
		return colDefs;
	}

	public void setColDefs(Map<String, ColDef> colDefs) {
		this.colDefs = colDefs;
	}

	public Map<String, TableDef> getTableDefs() {
		return tableDefs;
	}

	public void setTableDefs(Map<String, TableDef> tableDefs) {
		this.tableDefs = tableDefs;
	}

	public Map<String, PortObj> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, PortObj> ports) {
		this.ports = ports;
	}

	public List<AnonDefObj> getAnonDefs() {
		return anonDefs;
	}

	public void setAnonDefs(List<AnonDefObj> anonDefs) {
		this.anonDefs = anonDefs;
	}

	public List<TypeDefObj> getTypeDefs() {
		return typeDefs;
	}

	public void setTypeDefs(List<TypeDefObj> typeDefs) {
		this.typeDefs = typeDefs;
	}

	public List<LayerObj> getLayers() {
		return layers;
	}

	public void setLayers(List<LayerObj> layers) {
		this.layers = layers;
	}

	public Map<String, Stream> getStreams() {
		return streams;
	}

	public void setStreams(Map<String, Stream> streams) {
		this.streams = streams;
	}

	public Map<String, ProcessorDef> getProcs() {
		return procs;
	}

	public void setProcs(Map<String, ProcessorDef> procs) {
		this.procs = procs;
	}

}
/**/