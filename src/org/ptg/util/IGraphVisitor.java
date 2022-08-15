package org.ptg.util;

import java.util.Collection;
import java.util.Map;

import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.MultiGraph;

public interface IGraphVisitor<C extends CompilePath> {
	void visitNode(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code);

	void visitStart(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code);

	void visitEnd(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code);

	void visitDependentNodeResolved(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU);

	void visitDependentNodeUnResolved(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU);

	void visitEndNodeDependencyResolved(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR,
			Collection<String> depsU);

	void visitEndNodeDependendencyUnResolved(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR,
			Collection<String> depsU);

	StringBuilder getCode(Map<String, String> code);

	void setFpgraph2(FPGraph2 pg);

	boolean multiFanoutAllowed();

	public Map getConfig();

	public void setConfig(Map config);
}
