package org.ptg.util.graph;

import java.util.Map;
import java.util.concurrent.Callable;

import org.ptg.http2.handlers.compilers.graph.CompileTaskPlanV2;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class ParallelTaskCallable implements Callable{
	String uidStr;
	SimpleNodeHandler sh;
	ComplexNodeHandler ch;
	ParallelComplexNodeHandler psh;
	Map<String, Object> ctx;
	PNode pnode;
	PNode parent;
	Map<String, WaitStruct> waitList;
	FPGraph2 o;
	Map<String, AnonDefObj> anonCompMap;
	Map<String, PNode> allPnodes;
	CompileTaskPlanV2 v2;
	AnonDefObj curr;
	@Override
	public Object call() throws Exception {
		v2.analyze(uidStr, sh, ch, psh, ctx, pnode, parent, waitList, o, anonCompMap, allPnodes, v2);
		return curr.getId();
	}
	public ParallelTaskCallable(String uidStr, SimpleNodeHandler sh, ComplexNodeHandler ch, ParallelComplexNodeHandler psh, Map<String, Object> ctx, PNode pnode, PNode parent,
			Map<String, WaitStruct> waitList, FPGraph2 o, Map<String, AnonDefObj> anonCompMap, Map<String, PNode> allPnodes, CompileTaskPlanV2 v2, AnonDefObj curr) {
		this.uidStr = uidStr;
		this.sh = sh;
		this.ch = ch;
		this.psh = psh;
		this.ctx = ctx;
		this.pnode = pnode;
		this.parent = parent;
		this.waitList = waitList;
		this.o = o;
		this.anonCompMap = anonCompMap;
		this.allPnodes = allPnodes;
		this.v2 = v2;
		this.curr = curr;
	}
	
}
