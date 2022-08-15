/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.compilers.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.ContPublisherWriter;
import org.ptg.util.camelcomps.DefaultCamelComp;
import org.ptg.util.camelcomps.ICamelComp;
import org.ptg.util.events.TaskTraceEvent;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class CompileCamelPlan extends AbstractHandler {
	Map<String, ICamelComp> functions = new LinkedHashMap<String, ICamelComp>();
	DirectedSparseMultigraph<AnonDefObj, String> graph = new DirectedSparseMultigraph<AnonDefObj, String>();
	List<AnonDefObj> starts = new ArrayList<AnonDefObj>();
	List<AnonDefObj> ends = new ArrayList<AnonDefObj>();
	private boolean multiFanoutAllowed = false;
	private Set<AnonDefObj> depsResolved;
	boolean persist = false;
	boolean trace = false;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String trace = request.getParameter("trace");
		this.trace = trace != null ? true : false;
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String loop = request.getParameter("loopcount");
		String graphconfig = request.getParameter("graphconfig");
		if (graphconfig == null)
			graphconfig = "{}";
		Map<String, Object> executionCtx = null;
		Map config = CommonUtil.getConfigFromJsonData(graphconfig);
		String instidStr = getUUIDStr(config);
		int loopCount = 1;
		if (loop != null)
			loopCount = Integer.parseInt(loop);
		try {
			Map<String, Object> ctx = runApp(instidStr, name, graphjson, loopCount, executionCtx);
			response.getWriter().print(CommonUtil.toJson(ctx));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	public Map<String, Object> runApp(String uidStr, String name, String graphjson, int loopCount, Map<String, Object> executionCtx) {
		init(uidStr);
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		return runApp(uidStr, name, o, loopCount, executionCtx);
	}

	public Map<String, Object> runApp(String uidStr, String name, FPGraph2 o, int loopCount, Map<String, Object> executionCtx) {
		List<TypeDefObj> types = o.getTypeDefs();
		Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (TypeDefObj obj : types) {
			typeMap.put(obj.getId(), obj);
		}
		for (AnonDefObj def : o.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}

		Pattern anonPat = Pattern.compile("([a-z_A-Z0-9]+)\\(([a-z_A-Z0-9]+)\\)");

		Map<String, JSONObject> orphans = o.getOrphans();
		for (Map.Entry<String, JSONObject> en : orphans.entrySet()) {
			JSONObject jo = en.getValue();
			if (jo.getString("type").equals("portable")) {
				String portType = jo.getString("portType");
				if ("prop".equals(portType)) {
					String id = jo.getString("grpid");
					TypeDefObj tobj = typeMap.get(id);
					if (tobj == null) {
						tobj = new TypeDefObj();
						tobj.setId(id);
					}
					int indx = jo.getInt("index");
					while (tobj.getDtypes().size() < indx + 1)
						tobj.getDtypes().add(null);
					while (tobj.getInputs().size() < indx + 1)
						tobj.getInputs().add(null);
					tobj.getDtypes().set(indx, jo.getString("dtype"));
					tobj.getInputs().set(indx, jo.getString("id"));
					//System.out.println(jo);
					typeMap.put(tobj.getId(), tobj);
				} else { /* anondef */
					String id = jo.getString("grpid");
					Matcher mat = anonPat.matcher(id);
					String uid = null;
					String ttype = null;
					if (mat.find()) {
						uid = mat.group(1);
						ttype = mat.group(2);
					}
					if (uid == null || ttype == null)
						continue;
					AnonDefObj tobj = anonCompMap.get(id);
					if (tobj == null) {
						tobj = new AnonDefObj();
						tobj.setId(id);
						/* tobj.setId(uid); */
						tobj.setAnonType(ttype);
						tobj.setName(uid);
					}
					String portId = jo.getString("id");
					if ("input".equals(portType))
						tobj.getInputs().add(portId);
					else if ("output".equals(portType))
						tobj.getOutputs().add(portId);
					else if ("aux".equals(portType))
						tobj.getAux().add(portId);

					o.getAnonDefs().add(tobj);
					anonCompMap.put(tobj.getId(), tobj);
				}

			}
		}
		types = new ArrayList<TypeDefObj>();
		for (TypeDefObj t : typeMap.values()) {
			types.add(t);
		}
		o.setTypeDefs(types);
		// fix loopback
		o = applyLoopBacks(o, typeMap, anonCompMap);

		return createAndRunGraph(uidStr, name, o, loopCount, executionCtx);
	}

	public Map<String, Object> createAndRunGraph(String uidStr, String name, FPGraph2 o, int loopCount, Map<String, Object> executionCtx) {
		if (persist) {
			executionCtx = (Map<String, Object>) CommonUtil.getVar("exec-" + uidStr);
		}
		if (executionCtx == null)
			executionCtx = new LinkedHashMap<String, Object>();
		for (int i = 0; i < loopCount; i++) {
			List<AnonDefObj> toRem = browseGraph(uidStr, o, executionCtx);// CommonUtil.topologicalSortAnonTypes(o);
			if (persist) {
				CommonUtil.saveVar("exec-" + uidStr, executionCtx);
			}
		}
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : o.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, Object> en : executionCtx.entrySet()) {
			String k = en.getKey();
			Object v = en.getValue();
			PortObj po = o.getPorts().get(k);
			if (po != null) {
				ret.put(anonCompMap.get(po.getGrp()).getName() + "." + po.getPortname(), v);
			} else {
				ret.put(en.getKey(), en.getValue());
			}
		}
		return ret;
	}

	public List<AnonDefObj> browseGraph(String uidStr, FPGraph2 g, Map<String, Object> executionCtx) {
		Map<String, PortObj> ports = g.getPorts();
		List<AnonDefObj> toRet = new LinkedList<AnonDefObj>();
		graph = new DirectedSparseMultigraph<AnonDefObj, String>();
		depsResolved = new LinkedHashSet<AnonDefObj>();
		starts.clear();
		ends.clear();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : g.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		Map<String, String> filter = new HashMap<String, String>();
		for (ConnDef cd : g.getForward().values()) {
			{
				PortObj from = g.getPorts().get(cd.getFrom());
				PortObj to = g.getPorts().get(cd.getTo());
				if (from != null && to != null && from.getPorttype().equals("aux") && to.getPorttype().equals("aux")) {
					String f = from.getGrp();
					AnonDefObj fa = anonCompMap.get(f);
					String t = to.getGrp();
					AnonDefObj ta = anonCompMap.get(t);
					String filterKey = f + "->" + t;
					if (!filter.containsKey(filterKey)) {
						if (!fa.equals(ta)) {
							graph.addVertex(fa);
							graph.addVertex(ta);
							graph.addEdge(filterKey, fa, ta);
						}
						filter.put(filterKey, filterKey);
					}
				}
			}
		}
		prepareStarts();
		prepareEnds();
		Map<AnonDefObj, AnonDefObj> visited = new LinkedHashMap<AnonDefObj, AnonDefObj>();
		/*
		 * for simulations we need to add one more param that controls the no of
		 * iteratinos and another as a step probable?
		 */
		for (AnonDefObj anon : starts) {
			visit(uidStr, anon, null, 0, visited, toRet, ports, executionCtx, g);
		}
		//System.out.println(graph);
		return toRet;
	}

	public void prepareEnds() {
		Collection<AnonDefObj> coll = graph.getVertices();
		for (AnonDefObj anonDefObj : coll) {
			if (graph.getOutEdges(anonDefObj).size() == 0) {
				ends.add(anonDefObj);
			}
		}
	}

	public void prepareStarts() {
		Collection<AnonDefObj> coll = graph.getVertices();
		for (AnonDefObj anonDefObj : coll) {
			if (graph.getInEdges(anonDefObj).size() == 0) {
				starts.add(anonDefObj);
			}
		}
	}

	public void visit(String uidStr, final AnonDefObj curr, AnonDefObj from, int depth, Map<AnonDefObj, AnonDefObj> visited, List<AnonDefObj> toRet, Map<String, PortObj> ports, Map<String, Object> executionCtx, FPGraph2 g) {
		if (visited.containsKey(curr))
			return;
		visited.put(curr, curr);
		Collection<String> childs = graph.getOutEdges(curr);// get the out edges
		if (childs.size() < 1) {
			Collection<AnonDefObj> deps = graph.getPredecessors(curr);
			Collection<AnonDefObj> depsR = new LinkedList<AnonDefObj>();
			Collection<AnonDefObj> depsU = new LinkedList<AnonDefObj>();
			boolean depsResolved = true;
			if (deps != null) {
				for (AnonDefObj dfp : deps) {
					if (!visited.containsKey(dfp)) {
						depsU.add(dfp);
						depsResolved = false;
					} else {
						depsR.add(dfp);
					}
				}
			}
			//System.out.println("End: " + curr.getName());
			if (depsResolved) {
				//System.out.println("End Resolved: " + curr.getName());
				toRet.add(curr);
				List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
				List<FunctionPortObj> outputs = new ArrayList<FunctionPortObj>();
				for (String str : curr.getInputs()) {
					processInputs(g, ports, curr, inputs, str);
				}
				for (String str : curr.getOutputs()) {
					processOutputs(g, ports, curr, outputs, str);
				}
				ICamelComp f = functions.get(curr.getAnonType());
				if (f != null) {
					f.setConfigItems(curr.getConfigItems());
					Object o = f.execute(curr, inputs, outputs, g, executionCtx,null);
					CommonUtil.saveVar("exec-" + uidStr, executionCtx);
					executionCtx.put(curr.getId() + "_result", (f.wasSuccess() ? "Successful" : "Failed"));
					if (trace) {
						ContPublisherWriter.getInstance().loadEvent(new TaskTraceEvent(executionCtx, curr.getId()));
					}
					if (!f.wasSuccess()) {
						throw new RuntimeException("Failed to execute : " + curr);
					}
				}
			} else {
				//System.out.println("End Not Resolved: " + curr.getName());
				if (curr != null) {
					visited.remove(curr);
				}
			}

		}
		Map<String, String> skipList = new HashMap<String, String>();
		for (String c : childs) {
			AnonDefObj s = graph.getOpposite(curr, c);
			Collection<AnonDefObj> deps = graph.getPredecessors(curr);
			Collection<AnonDefObj> depsR = new LinkedList<AnonDefObj>();
			Collection<AnonDefObj> depsU = new LinkedList<AnonDefObj>();
			boolean depsResolved = true;
			if (deps != null) {
				for (AnonDefObj dfp : deps) {
					if (!visited.containsKey(dfp)) {
						depsU.add(dfp);
						depsResolved = false;
					} else {
						depsR.add(dfp);
					}
				}
			}
			if (depsResolved) {
				if ((this.depsResolved.contains(curr))) {
					if (multiFanoutAllowed) {
						//System.out.println("Revisiting resolved node because of multiple fanout " + curr);
						this.depsResolved.add(curr);
					} else {
						//System.out.println("Not Revisiting resolved node visitor not interested: " + curr);
					}
				} else {
					//System.out.println("Resolved: " + curr.getName());
					toRet.add(curr);
					this.depsResolved.add(curr);
					List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
					List<FunctionPortObj> outputs = new ArrayList<FunctionPortObj>();
					for (String str : curr.getInputs()) {
						processInputs(g, ports, curr, inputs, str);
					}
					for (String str : curr.getOutputs()) {
						processOutputs(g, ports, curr, outputs, str);
					}
					ICamelComp f = functions.get(curr.getAnonType());
					if (f != null) {
						f.setConfigItems(curr.getConfigItems());
						Object o = f.execute(curr, inputs, outputs, g, executionCtx,skipList);
						CommonUtil.saveVar("exec-" + uidStr, executionCtx);
						executionCtx.put(curr.getId() + "_result", (f.wasSuccess() ? "Successful" : "Failed"));
						if (trace) {
							ContPublisherWriter.getInstance().loadEvent(new TaskTraceEvent(executionCtx, curr.getId()));
						}
						if (!f.wasSuccess()) {
							throw new RuntimeException("Failed to execute : " + curr);
						}
						if (o != null) {
							if (o instanceof Boolean || o.getClass().equals(boolean.class)) {
								if (curr.getAnonType().equals("if")) {
									if (((Boolean) o) == false) {
										if (outputs != null && outputs.size() > 0) {
											String key = outputs.get(0).getPo().getGrp();
											skipList.put(key, key);
										}
									} else {
										if (outputs != null && outputs.size() > 1) {
											String key = outputs.get(1).getPo().getGrp();
											skipList.put(key, key);
										}
									}
								}
							} else {
								if (o instanceof PortObj) {
									PortObj next = (PortObj) o;
									for (FunctionPortObj fo : outputs) {
										if (fo.getPo() != null) {
											String key = fo.getPo().getGrp();
											if (!key.equals(next.getGrp())) {
												skipList.put(key, key);
											}
										}
									}
								}
							}
						}
					}
				}
				if (!skipList.containsKey(s.getId())) {
					visit(uidStr, s, curr, depth + 1, visited, toRet, ports, executionCtx, g);
				} else {
					//System.out.println("Skipping: " + s);
				}
			} else {
				//System.out.println("Not Resolved: " + curr.getName());
				if (curr != null) {
					visited.remove(curr);
				}
			}
		}
	}

	public void processOutputs(FPGraph2 o, Map<String, PortObj> ports, AnonDefObj anon, List<FunctionPortObj> outputs, String s) {
		PortObj po = ports.get("out_" + anon.getId() + "." + s);
		//System.out.println("Found: " + po.getId());
		for (ConnDef cd : o.getForward().values()) {

			if (cd.getFrom().equals(po.getId())) {
				//System.out.println("Found output conn: " + cd.getId());
				PortObj opp = ports.get(cd.getTo());
				PortObj myPort = ports.get(cd.getFrom());
				FunctionPortObj inp = null;
				inp = new FunctionPortObj(opp, -1);
				inp.setGrpName("unk");
				inp.setMyPort(myPort);
				if (opp != null) {
					if (opp.getPorttype().equals("pinput")) {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {// this is
																// it
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										inp = new FunctionPortObj(opp, i);
										String mvar = opp.getGrp();
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(opp.getGrp());
										inp.setMyPort(myPort);
									}
								}
							}
						}
						//System.out.println("Opposite to: " + inp);
					} else {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {// this is
																// it
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										String mvar = opp.getGrp();
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(mvar);
										inp.setMyPort(myPort);
									}
								}
							}
						}
						//System.out.println("Opposite to: " + inp);
					}
				}
				outputs.add(inp);
			}
		}
	}

	public void processInputs(FPGraph2 o, Map<String, PortObj> ports, AnonDefObj anon, List<FunctionPortObj> inputs, String s) {
		//System.out.println("ProcessInputs: " + "inp_" + anon.getId() + "." + s);
		PortObj po = ports.get("inp_" + anon.getId() + "." + s);
		//System.out.println("Found: " + po.getId());
		for (ConnDef cd : o.getForward().values()) {
			if (cd.getTo().equals(po.getId())) {
				//System.out.println("Found input conn: " + cd.getId());
				PortObj opp = ports.get(cd.getFrom());
				PortObj myPort = ports.get(cd.getTo());
				FunctionPortObj inp = null;
				inp = new FunctionPortObj(opp, -1);
				inp.setGrpName("unk");
				inp.setMyPort(myPort);
				if (opp != null) {
					if (opp.getPorttype().equals("poutput")) {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {// this is
																// it
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(opp.getGrp());
										inp.setMyPort(myPort);
									}
								}
							}
						}
						//System.out.println("Opposite From: " + inp);
					} else {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										String mvar = opp.getGrp();
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(mvar);
										inp.setMyPort(myPort);
										//System.out.println("Bad mapping mapping from input to output ");
									}
								}
							}
						}

						//System.out.println("Opposite From: " + inp);
					}
				}
				inputs.add(inp);
			}
		}
	}

	public CompileCamelPlan() {

	}

	protected FPGraph2 applyLoopBacks(FPGraph2 o, Map<String, TypeDefObj> typeMap, Map<String, AnonDefObj> anonCompMap) {
		Map<String, ConnDef> m = o.getForward();
		List<String> toRem = new ArrayList<String>();
		for (Map.Entry<String, ConnDef> en : m.entrySet()) {
			ConnDef cd = en.getValue();
			String id = en.getKey();
			if (cd.getCtype() != null && cd.getCtype().equalsIgnoreCase("loopback")) {
				//System.out.println("There is a loopback: " + cd.getId());
				toRem.add(id);
			}
		}
		Map<String, PortObj> ports = o.getPorts();
		for (String s : toRem) {
			ConnDef cd = o.getForward().get(s);
			PortObj f = ports.get(cd.getFrom());
			PortObj t = ports.get(cd.getTo());
			o.getForward().remove(s);
			o.getMainGraph().getForward().remove(s);
			o.getMainGraph().getGraph().removeEdge(cd);
			// add anon
			AnonDefObj tobj = new AnonDefObj();
			tobj.setId(cd.getId() + "loopback");
			tobj.setAnonType("loopback");
			tobj.setName(CommonUtil.getRandomString(4) + "_loopback");
			tobj.getInputs().add("inp1");
			tobj.getOutputs().add("out1");
			tobj.getAux().add("aux1");
			// input
			PortObj pi = new PortObj();
			pi.setId("inp_" + tobj.getId() + "." + "inp1");
			pi.setGrp(tobj.getId());
			pi.setPorttype("input");
			pi.setPortname("inp1");
			// output
			PortObj po = new PortObj();
			po.setId("out_" + tobj.getId() + "." + "out1");
			po.setGrp(tobj.getId());
			po.setPorttype("output");
			po.setPortname("out1");
			// output
			PortObj pa = new PortObj();
			pa.setId("aux_" + tobj.getId() + "." + "aux1");
			pa.setGrp(tobj.getId());
			pa.setPorttype("aux");
			pa.setPortname("aux1");

			ConnDef ci = new ConnDef();
			ci.setId(pi.getId() + "conn");
			ci.setFrom(cd.getFrom());
			ci.setTo(pi.getId());

			ConnDef co = new ConnDef();
			co.setId(po.getId() + "conn");
			co.setFrom(po.getId());
			co.setTo(cd.getTo());

			AnonDefObj a1 = anonCompMap.get(f.getGrp());
			String aux1 = a1.getAux().get(0);
			AnonDefObj a2 = anonCompMap.get(t.getGrp());
			String aux2 = a2.getAux().get(0);

			ConnDef ca1 = new ConnDef();
			ca1.setId(pa.getId() + "conn1");
			ca1.setFrom("aux_" + a1.getId() + "." + aux1);
			ca1.setTo(pa.getId());
			ca1.setNodes(new String[] { "aux_" + a1.getId() + "." + aux1, pa.getId() });

			/*
			 * We will not finish the short circuit but keep it open loop so
			 * what we are essentially doing is we take a loopback and then
			 * convert into anon def but that is not setting values anywhre.
			 * ConnDef ca2 = new ConnDef(); ca2.setId(pa.getId()+"conn2");
			 * ca2.setFrom("aux_"+tobj.getId()+"."+"aux1");
			 * ca2.setTo("aux_"+a2.getId()+"."+aux2);
			 * o.getForward().put(ca2.getId(), ca2);
			 */

			anonCompMap.put(tobj.getId(), tobj);
			o.getAnonDefs().add(tobj);
			o.getPorts().put(pi.getId(), pi);
			o.getPorts().put(po.getId(), po);
			o.getPorts().put(pa.getId(), pa);
			o.getForward().put(ci.getId(), ci);
			o.getForward().put(co.getId(), co);
			o.getForward().put(ca1.getId(), ca1);

		}
		return o;
	}

	private String getUUIDStr(Map config) {
		return CommonUtil.getUUIDStr(config);
	}

	public void init(String instid) {
		functions.put("from", new DefaultCamelComp());
	}


	public boolean isPersist() {
		return persist;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}
}

/*
 * todo : run taskplan upto task run individual task step forward plan step
 * backward plan checkpoint task conditional task based on query
 */
