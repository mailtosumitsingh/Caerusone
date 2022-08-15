package org.ptg.util;

import java.util.Collection;
import java.util.Map;

import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.MultiGraph;

public abstract class AbstractGraphVisitor<C extends CompilePath> implements IGraphVisitor<C > {

	@Override
	public void visitNode(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code) {
		System.out.println("visitNode: "+t);
	}

	@Override
	public void visitStart(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code) {
		System.out.println("visitStart: "+t);		
	}

	@Override
	public void visitEnd(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code) {
		System.out.println("visitEnd: "+t);
		
	}

	@Override
	public void visitDependentNodeResolved(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code, Collection<String > deps, Collection<String > depsR, Collection<String > depsU) {
		System.out.println("visitDependentNodeResolved: "+t);
		
	}

	@Override
	public void visitDependentNodeUnResolved(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code, Collection<String > deps, Collection<String > depsR, Collection<String > depsU) {
		System.out.println("visitDependentNodeUnResolved: "+t);		
	}

	@Override
	public void visitEndNodeDependencyResolved(String t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEndNodeDependendencyUnResolved(String t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StringBuilder getCode(Map<String, String> code) {
		return new StringBuilder();
	}

	@Override
	public void setFpgraph2(FPGraph2 pg) {
		// TODO Auto-generated method stub
		
	}

}
