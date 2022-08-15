/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.compilers.graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.ITaskFunction;
import org.ptg.util.SpringHelper;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;
import org.ptg.util.taskfunctions.ConcatTask;
import org.ptg.util.taskfunctions.ConstantTask;
import org.ptg.util.taskfunctions.FunctionTask;
import org.ptg.util.taskfunctions.IfTask;
import org.ptg.util.taskfunctions.LogTask;
import org.ptg.util.taskfunctions.TraceTask;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class CopyOfCompileTaskPlan extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;
	Map<String, ITaskFunction> functions = new LinkedHashMap<String, ITaskFunction>();
	DirectedSparseMultigraph<AnonDefObj, String> graph = new DirectedSparseMultigraph<AnonDefObj, String>();
	List<AnonDefObj> starts = new ArrayList<AnonDefObj>();
	List<AnonDefObj> ends = new ArrayList<AnonDefObj>();
	private boolean multiFanoutAllowed = false;
	private Set<AnonDefObj> depsResolved;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		init();
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String run = request.getParameter("run");
		String mappingtype = request.getParameter("mappingtype");
		String ineventtype = request.getParameter("eventtype");
		String mainId = request.getParameter("mainTable");
		String fpath = request.getParameter("filePath");
		String filePath = base + "/uploaded/in/" + fpath;
		if (mainId != null && mainId.length() == 0)
			mainId = null;
		Map<String, String> params = new HashMap<String, String>();

		Object toret = null;
		params.put("eventtype", ineventtype);
		if (mappingtype == null)
			mappingtype = "FreeSpring";
		try {
			Map<String, Object> ctx = runApp(name, mainId, graphjson, mappingtype, params);
			response.getWriter().print(CommonUtil.toJson(ctx));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	public Map<String, Object> runApp(String name, String mainId, String graphjson, String mappingtype, Map<String, String> params) {
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);

		List<TypeDefObj> types = o.getTypeDefs();
		Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
		for (TypeDefObj obj : types) {
			typeMap.put(obj.getId(), obj);
		}
		Map<String, JSONObject> orphans = o.getOrphans();
		for (Map.Entry<String, JSONObject> en : orphans.entrySet()) {
			JSONObject jo = en.getValue();
			if (jo.getString("type").equals("portable")) {
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
			}
		}
		types = new ArrayList<TypeDefObj>();
		for (TypeDefObj t : typeMap.values()) {
			types.add(t);
		}
		o.setTypeDefs(types);
		return createAndRunGraph(name, mainId, params, o);
	}

	public Map<String, Object> createAndRunGraph(String name, String mainId, Map<String, String> params, FPGraph2 o) {
		Map<String, PortObj> ports = o.getPorts();
		Map<String, Object> executionCtx = new LinkedHashMap<String, Object>();
		Collections.sort(o.getAnonDefs(), new Comparator<AnonDefObj>() {
			@Override
			public int compare(AnonDefObj o1, AnonDefObj o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});

		Map<String, Integer> mappedVars = new LinkedHashMap<String, Integer>();
		Set<String> declInputs = new HashSet<String>();
		List<AnonDefObj> toRem = browseGraph(o);// CommonUtil.topologicalSortAnonTypes(o);
		Map<String, AnonDefObj> processed = new HashMap<String, AnonDefObj>();
		for (AnonDefObj anon : toRem) {
			if (processed.containsKey(anon.getId()))
				continue;
			else
				processed.put(anon.getId(), anon);

			List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
			List<FunctionPortObj> outputs = new ArrayList<FunctionPortObj>();
			// input processing
			for (String s : anon.getInputs()) {
				processInputs(o, ports, anon, inputs, s);
			}
			// output processing
			for (String s : anon.getOutputs()) {
				processOutputs(o, ports, mappedVars, anon, outputs, s);
			}
			ITaskFunction f = functions.get(anon.getAnonType());
			if (f != null) {
				f.execute(anon, inputs, outputs, o, executionCtx,null, null, null);

			}
			for (FunctionPortObj fo : inputs) {
				if (!declInputs.contains(fo.getPo().getGrp())) {
					boolean isAnonType = false;
					for (TypeDefObj obj : o.getTypeDefs()) {
						if (obj.getId().equals(fo.getPo().getGrp())) {
							isAnonType = true;
							break;
						}
					}

				}
				declInputs.add(fo.getPo().getGrp());
			}
		}// anon
		return executionCtx;
	}

	public List<AnonDefObj> browseGraph(FPGraph2 g) {
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
				if (from != null && to != null)// &&
												// from.getPorttype().equals("aux")&&
												// to.getPorttype().equals("aux"))*/
				{
					String f = from.getGrp();
					AnonDefObj fa = anonCompMap.get(f);
					String t = to.getGrp();
					AnonDefObj ta = anonCompMap.get(t);
					String filterKey = f + "->" + t;
					if (!filter.containsKey(filterKey)) {
						if (fa != null) {
							if (!fa.equals(ta)) {
								graph.addVertex(fa);
								if (ta != null) {
									graph.addVertex(ta);
									graph.addEdge(filterKey, fa, ta);
								}
							}
						}
						filter.put(filterKey, filterKey);
					}
				}
			}
		}
		prepareStarts();
		prepareEnds();
		Map<AnonDefObj, AnonDefObj> visited = new LinkedHashMap<AnonDefObj, AnonDefObj>();
		for (AnonDefObj anon : starts) {
			visit(anon, null, 0, visited, toRet);
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

	public void visit(final AnonDefObj curr, AnonDefObj from, int depth, Map<AnonDefObj, AnonDefObj> visited, List<AnonDefObj> toRet) {
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
			} else {
				//System.out.println("End Not Resolved: " + curr.getName());
				if (curr != null) {
					visited.remove(curr);
				}
			}

		}
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
				}
				visit(s, curr, depth + 1, visited, toRet);
			} else {
				//System.out.println("Not Resolved: " + curr.getName());
				if (curr != null) {
					visited.remove(curr);
				}
			}
		}
	}

	public void processOutputs(FPGraph2 o, Map<String, PortObj> ports, Map<String, Integer> mappedVars, AnonDefObj anon, List<FunctionPortObj> outputs, String s) {
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
					if (opp.getPorttype().equals("poutput")) {
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
										Integer val = mappedVars.get(mvar);
										if (val == null) {
											mappedVars.put(mvar, i);
										} else {
											if (i > val) {
												mappedVars.put(mvar, i);
											}
										}
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
										Integer val = mappedVars.get(mvar);
										if (val == null) {
											mappedVars.put(mvar, i);
										} else {
											if (i > val) {
												mappedVars.put(mvar, i);
											}
										}
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

	public CopyOfCompileTaskPlan() {

	}

	public void init() {
		functions.put("concat", new ConcatTask());
		functions.put("constant", new ConstantTask());
		functions.put("log", new LogTask());
		functions.put("functionCall", new FunctionTask());
		functions.put("trace", new TraceTask());
		functions.put("if", new IfTask());

	}
}