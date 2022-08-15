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

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class BFSTopologicalVisitor<T, C extends CompilePath> {
	DirectedSparseMultigraph<String, String> graph = null;// new
	Set<String> depsResolved = new LinkedHashSet<String>();
	Set<String> starts = new HashSet<String>();
	List<String> ends = new ArrayList<String>();

	public void init(DirectedSparseMultigraph<String, String> graph) {
		this.graph = graph;
	}


	public void prepareEnds() {
		for (String s: graph.getVertices()) {
			if (graph.getOutEdges(s).size() == 0) {
				ends.add(s);
			}
		}
	}

	public void prepareStarts() {
		for (String s: graph.getVertices()) {
			if (graph.getInEdges(s).size() == 0) {
				starts.add(s);
			} else {
				Collection<String> ins = graph.getInEdges(s);
				if (ins.size() == 1) {
					for (String in1 : ins) {
						in1 = graph.getIncidentVertices(in1).iterator().next();
						if (in1.equals(s)) {
							starts.add(s);
						}
					}
				}
			}
		}
	}

	public void prepare() {
		depsResolved.clear();
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
			visitor.visitStart(s, null, 0, null, graph, visited, code);
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
		if (visited.containsKey(curr))
			return;
		visited.put(curr, curr);
		visitor.visitNode(curr, null, depth, null, graph, visited, code);
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
			visitor.visitEnd(curr, null, depth, null, graph, visited, code);
			if (depsResolved) {
				visitor.visitEndNodeDependencyResolved(curr, null, depth, null, graph, visited, code, deps, depsR, depsU);
			} else {
				visitor.visitEndNodeDependendencyUnResolved(curr, null, depth, null, graph, visited, code, deps, depsR, depsU);
				if (curr != null) {
					visited.remove(curr);
				}
			}

		}
		for (String c : childs) {
			String s = graph.getOpposite(curr, c);
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
						visitor.visitDependentNodeResolved(curr, null, depth, null, graph, visited, code, deps, depsR, depsU);
						// System.out.println("Going for since dpendencies are resolved: "
						// + curr + " from " + from + " .");
						// System.out.println("Processing Non Leaf: " + curr);
					}else{
						System.out.println("Not Revisiting resolved node visitor not interested: "+curr);	
					}
				} else {
					this.depsResolved.add(curr);
					visitor.visitDependentNodeResolved(curr, null, depth, null, graph, visited, code, deps, depsR, depsU);
					// System.out.println("Going for since dpendencies are resolved: "
					// + curr + " from " + from + " .");
					// System.out.println("Processing Non Leaf: " + curr);
				}
				visit(s, curr, sb, depth + 1, visited, code, visitor);
			} else {
				visitor.visitDependentNodeUnResolved(curr, null, depth, null, graph, visited, code, deps, depsR, depsU);
				// System.out.println("dpendencies are not resolved: " + curr +
				// " from " + from + " .");
				if (curr != null) {
					visited.remove(curr);
				}
			}
		}
	}


}
