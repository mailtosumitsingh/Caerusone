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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class TodoPathMachineCompiler<T, C extends CompilePath> {
	FPGraph fpgraph;
	FPGraph2 fpgraph2;
	DirectedSparseMultigraph<String, String> graph = null;// new
	Map<String, CompilePath> cpaths = new HashMap<String, CompilePath>();
	Set<String> depsResolved = new LinkedHashSet<String>();
	Set<String> starts = new HashSet<String>();
	List<String> ends = new ArrayList<String>();

	public void init() {
		try {
			graph = new DirectedSparseMultigraph<String, String>();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public <T extends CompilePath> void addCompilePath(T name) {
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
			} else {
				Collection<String> ins = graph.getInEdges(s.getValue().getStart().getId());
				if (ins.size() == 1) {
					for (String in1 : ins) {
						in1 = graph.getIncidentVertices(in1).iterator().next();
						if (in1.equals(s.getValue().getStart().getId())) {
							starts.add(s.getValue().getStart().getId());
						}
					}
				}
			}
		}
	}

	public void prepare() {
		depsResolved.clear();
		for (Map.Entry<String, CompilePath> s : cpaths.entrySet()) {
			String from = s.getValue().getStart().getId();
			String to = s.getValue().getEnd().getId();
			Collection<FunctionPoint> fps = s.getValue().getDeps();
			if (fps != null) {
				for (FunctionPoint fp : fps) {
					graph.addVertex(fp.getId());
					String st1 = fp.getId();
					String st2 = s.getValue().getStart().getId();
					// if(!st1.equals(st2))
					graph.addEdge(st1 + "." + st2, new edu.uci.ics.jung.graph.util.Pair(st1, st2));
				}
			}
			graph.addVertex(from);
			graph.addVertex(to);
			// if(!from.equals(to))
			graph.addEdge(from + "." + to, new edu.uci.ics.jung.graph.util.Pair(from, to));
		}
		// now prepare the start and the end nodtes
		prepareStarts();
		prepareEnds();
	}

	public String compile(IGraphVisitor<CompilePath> visitor) {
		prepare();
		Map<String, String> visited = new HashMap<String, String>();
		StringBuilder ret = new StringBuilder();
		Map<String, String> code = new LinkedHashMap<String, String>();
		// //////////////////////////////////
		for (String s : starts) {
			visitor.visitStart(s, null, 0, fpgraph, graph, visited, code);
			visit(s, null, ret, 0, visited, code, visitor);
		}

		// /////////////////////////////////////////
		StringBuilder sb = visitor.getCode(code);
		System.out.println(sb.toString());
		if (sb == null)
			return "";
		else
			return sb.toString();
	}

	public void visit(final String curr, String from, StringBuilder sb, int depth, Map<String, String> visited, Map<String, String> code, IGraphVisitor<CompilePath> visitor) {
		CompilePath cp = cpaths.get(from + "." + curr);
		if (visited.containsKey(curr))
			return;
		visited.put(curr, curr);
		visitor.visitNode(curr, cp, depth, fpgraph, graph, visited, code);
		Collection<String> childs = graph.getOutEdges(curr);// get the out edges
		if (childs.size() < 1) {
			// System.out.println("Reached leaf out node: " + curr + " from " +
			// from + " .");
			// System.out.println("Processing Leaf: " + curr);
			/* first see if all childs are resolved */
			Collection<String> deps = graph.getPredecessors(curr);
			Collection<String> depsR = new LinkedList<String>();
			Collection<String> depsU = new LinkedList<String>();
			boolean depsResolved = true;
			if (deps != null) {
				for (String dfp : deps) {
					if (!visited.containsKey(dfp)) {
						depsU.add(dfp);
						depsResolved = false;
						// System.out.println("Dependency: " + dfp +
						// " is not resolved for  " + cp.getStart().getId());
					} else {
						depsR.add(dfp);
						// System.out.println("Dependency: " + dfp +
						// " is Rfor  " + cp.getStart().getId());
					}
				}
			}
			/* now fire end node trigger */
			visitor.visitEnd(curr, cp, depth, fpgraph, graph, visited, code);
			if (depsResolved) {
				visitor.visitEndNodeDependencyResolved(curr, cp, depth, fpgraph, graph, visited, code, deps, depsR, depsU);
			} else {
				visitor.visitEndNodeDependendencyUnResolved(curr, cp, depth, fpgraph, graph, visited, code, deps, depsR, depsU);
				if (curr != null) {
					visited.remove(curr);
				}
			}

		}
		for (String c : childs) {
			String s = graph.getOpposite(curr, c);
			cp = cpaths.get(curr + "." + s);
			Collection<String> deps = graph.getPredecessors(curr);
			Collection<String> depsR = new LinkedList<String>();
			Collection<String> depsU = new LinkedList<String>();
			boolean depsResolved = true;
			if (deps != null) {
				for (String dfp : deps) {
					if (!visited.containsKey(dfp)) {
						depsU.add(dfp);
						depsResolved = false;
						// System.out.println("Dependency: " + dfp +
						// " is not resolved for  " + cp.getStart().getId());
					} else {
						depsR.add(dfp);
						// System.out.println("Dependency: " + dfp +
						// " is Rfor  " + cp.getStart().getId());
					}
				}
			}
			if (depsResolved) {
				if ((this.depsResolved.contains(curr))) {
					if (visitor.multiFanoutAllowed()) {
						System.out.println("Revisiting resolved node because of multiple fanout "+curr);
						this.depsResolved.add(curr); 
						visitor.visitDependentNodeResolved(curr, cp, depth, fpgraph, graph, visited, code, deps, depsR, depsU);
						// System.out.println("Going for since dpendencies are resolved: "
						// + curr + " from " + from + " .");
						// System.out.println("Processing Non Leaf: " + curr);
					}else{
						System.out.println("Not Revisiting resolved node visitor not interested: "+curr);	
					}
				} else {
					this.depsResolved.add(curr);
					visitor.visitDependentNodeResolved(curr, cp, depth, fpgraph, graph, visited, code, deps, depsR, depsU);
					// System.out.println("Going for since dpendencies are resolved: "
					// + curr + " from " + from + " .");
					// System.out.println("Processing Non Leaf: " + curr);
				}
				visit(s, curr, sb, depth + 1, visited, code, visitor);
			} else {
				visitor.visitDependentNodeUnResolved(curr, cp, depth, fpgraph, graph, visited, code, deps, depsR, depsU);
				// System.out.println("dpendencies are not resolved: " + curr +
				// " from " + from + " .");
				if (curr != null) {
					visited.remove(curr);
				}
			}
		}
	}

	public FPGraph getFpgraph() {
		return fpgraph;
	}

	public void setFpgraph(FPGraph fpgraph) {
		this.fpgraph = fpgraph;
	}

	public FPGraph2 getFpgraph2() {
		return fpgraph2;
	}

	public void setFpgraph2(FPGraph2 fpgraph2) {
		this.fpgraph2 = fpgraph2;
	}

}
