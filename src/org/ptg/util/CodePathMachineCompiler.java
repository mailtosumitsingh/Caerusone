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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class CodePathMachineCompiler {
	FPGraph fpgraph;
	FPGraph2 fpgraph2;
	Map<String, List<FunctionPoint>> grps = null;
	DirectedSparseMultigraph<String, String> graph = null;// new
	Map<String, CompilePath> cpaths = new HashMap<String, CompilePath>();
	Set<String> starts = new HashSet<String>();
	List<String> ends = new ArrayList<String>();

	public void init() {
		try {
			graph = new DirectedSparseMultigraph<String, String>();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void addCompilePath(CompilePath name) {
		String from = name.getStart().getId();
		String to = name.getEnd().getId();
		cpaths.put(from + "." + to, name);
	}

	public void prepareEnds() {
		for (Map.Entry<String, CompilePath> s : cpaths.entrySet()) {
			if (graph.getOutEdges(s.getValue().getEnd().getId()).size() == 0) {
				ends.add(s.getValue().getEnd().getId());
			}
		}
	}

	public void prepareStarts() {
		for (Map.Entry<String, CompilePath> s : cpaths.entrySet()) {
			if (graph.getInEdges(s.getValue().getStart().getId()).size() == 0) {
				starts.add(s.getValue().getStart().getId());
			}else{
				Collection<String>ins = graph.getInEdges(s.getValue().getStart().getId());
				if(ins.size()==1){
					for(String in1: ins){
						in1 = graph.getIncidentVertices(in1).iterator().next();
						if(in1.equals(s.getValue().getStart().getId())){
							starts.add(s.getValue().getStart().getId());
						}
					}
				}
			}
		}
	}

	public void prepare() {
		for (Map.Entry<String, CompilePath> s : cpaths.entrySet()) {
			String from = s.getValue().getStart().getId();
			String to = s.getValue().getEnd().getId();
			Collection<FunctionPoint> fps = s.getValue().getDeps();
			if (fps != null) {
				for (FunctionPoint fp : fps) {
					graph.addVertex(fp.getId());
					String st1  = fp.getId() ; 
					String st2 = s.getValue().getStart().getId();
					//if(!st1.equals(st2))
					graph.addEdge(st1+"."+st2,new edu.uci.ics.jung.graph.util.Pair(st1,st2));
				}
			}
			graph.addVertex(from);
			graph.addVertex(to);
			//if(!from.equals(to))
			graph.addEdge(from + "." + to, new edu.uci.ics.jung.graph.util.Pair(from, to));
		}
		// now prepare the start and the end nodtes
		prepareStarts();
		prepareEnds();
	}

	public String compile() {
		prepare();
		Map<String, String> visited = new HashMap<String, String>();
		StringBuilder ret = new StringBuilder();
		Map<String,String> code = new LinkedHashMap<String,String>();
		// //////////////////////////////////
		for (String s : starts) {
			visit(s, ret, 0, visited,code);
		}
		StringBuilder toret = new StringBuilder();
		for(Map.Entry<String,String>cd : code.entrySet()){
			toret.append(cd.getValue());
		}
		// /////////////////////////////////////////
		return toret.toString();
	}

	public void visit(String curr, StringBuilder sb, int depth, Map<String, String> visited,Map<String,String> code) {
		System.out.println("Currently at:\t" + curr);
		Collection<String> childs = graph.getOutEdges(curr);
		if(visited.containsKey(curr))
			return;
		visited.put(curr, curr);
		sb.append(curr+"-->"+"("+depth+")");
		
		for (String c : childs) {
			String s = graph.getOpposite(curr, c);
			CompilePath cp = cpaths.get(curr + "." + s);
			if (cp == null) {
				boolean found = false;
				for (CompilePath dpt : cpaths.values()) {
					Collection<FunctionPoint> deps1 = dpt.getDeps();
					if (deps1 != null) {
						for (FunctionPoint dfp : deps1) {
							if ((dfp.getId()+"."+dfp.getGrp()).equals( c)) {
								cp = dpt;
								found = true;
							}
						}
					}

					if (found) {
						Collection<FunctionPoint> deps = cp.getDeps();
						boolean depsResolved = true;
						if (deps != null) {
							for (FunctionPoint dfp : deps) {
								if (!visited.containsKey(dfp.getId()))
									depsResolved = false;
							}
						}
						if (depsResolved) {
							for(Map.Entry<String,String>cd : cp.getCode().entrySet()){
								if(!code.containsKey(cd.getKey())){
									//code.put(cd.getKey(), cd.getValue());
									String updatedCode = getUpdatedCode(cd.getKey(), cd.getValue(), cp);
									code.put(cd.getKey(), updatedCode);
								}
							}
							visit(s, sb, depth + 1, visited,code);
						}
					}
				}
			} else {
				for(Map.Entry<String,String>cd : cp.getCode().entrySet()){
					if(!code.containsKey(cd.getKey())){
						//code.put(cd.getKey(), cd.getValue());
						String updatedCode = getUpdatedCode(cd.getKey(), cd.getValue(), cp);
						code.put(cd.getKey(), updatedCode);
					}
				}
				visit(s, sb, depth + 1, visited,code);
			}
		}
		System.out.println("got here");
	}

	public FPGraph getFpgraph() {
		return fpgraph;
	}

	public void setFpgraph(FPGraph fpgraph) {
		this.fpgraph = fpgraph;
	}
	
	public String getUpdatedCode(String key,String value,CompilePath cp){
		Map<String, JSONObject> rc = fpgraph2.getOrphans();
		boolean added = false;
		Map<String, String> c = null;
		for (Map.Entry<String, JSONObject> en : rc.entrySet()) {
			String type = en.getValue().getString("type");
			String rid = en.getValue().getString("id");
			if (type != null && type.equals("region")) {
				if(en.getValue().getString("gtype").equals("excp")){
				JSONArray items = (JSONArray) en.getValue().get("items");
				for (Object o : items) {
					if (o.toString().equals(key)) {
						System.out.println("region " + rid + " contains " + o);
						Map<String, Object> p = new HashMap<String, Object>();
						p.put("cp", cp);
						p.put("c", key);
						p.put("grps", grps);
						Collection<FunctionPoint> lps  = fpgraph.getGraph().getPredecessors(fpgraph.getFunctionPoints().get(key));
						FunctionPoint lp = null;
						if(lps.size()>0){
							lp = lps.iterator().next();
						}
						p.put("lp",lp);
						p.put("fp",fpgraph.getFunctionPoints().get(key));
						p.put("r", en.getValue());
						c = CommonUtil.compileOrhpahDefinition(en.getValue(), p);
						added = c.size() > 0 ? true : false;
					}
				}
			}
		}
		}
		if (added) {
			return (c.values().iterator().next());
		} else {
			return value;
			}
	}

	public FPGraph2 getFpgraph2() {
		return fpgraph2;
	}

	public void setFpgraph2(FPGraph2 fpgraph2) {
		this.fpgraph2 = fpgraph2;
	}

	public Map<String, List<FunctionPoint>> getGrps() {
		return grps;
	}

	public void setGrps(Map<String, List<FunctionPoint>> grps) {
		this.grps = grps;
	}
}
