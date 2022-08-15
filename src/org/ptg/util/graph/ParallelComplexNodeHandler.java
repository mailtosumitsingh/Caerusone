package org.ptg.util.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.ptg.http2.handlers.compilers.graph.CompileTaskPlanV2;
import org.ptg.processors.ConnDef;
import org.ptg.util.ITaskFunction;
import org.ptg.util.ScatterGather2;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class ParallelComplexNodeHandler {
	private Map<String, ITaskFunction> functions;

	public ParallelComplexNodeHandler(Map<String, ITaskFunction> functions) {
		this.functions = functions;
	}

	public List<PNode> handle(AnonDefObj curr,List<FunctionPortObj> inputs, List<FunctionPortObj> outputs, String uidStr, SimpleNodeHandler sh,
			ComplexNodeHandler ch, ParallelComplexNodeHandler psh,
			Map<String, Object> executionCtx, PNode pnode, PNode parent,
			Map<String, WaitStruct> waitList, FPGraph2 o2,
			Map<String, AnonDefObj> anonCompMap, Map<String, PNode> allPnodes,CompileTaskPlanV2 v2) {
		Map<String, String> skipList = new HashMap<String, String>();
		if (curr != null) {
			System.out.println("CH , Executing: " + curr.getName());
			WaitStruct w = waitList.get(curr.getId());
			if (w != null) {
				if (!w.isReady()) {
					System.out.println("PSH , Skipping: " + curr.getName());
					return null;
				} else {
					waitList.remove(curr.getId());
				}
			}
			System.out.println("PSH , RealExecution: " + curr.getName());
			ITaskFunction f = functions.get(curr.getAnonType());
			if (f != null) {
				executionCtx.put(curr.getId() + "_result", "Failed");
				f.setConfigItems(curr.getConfigItems());
				Object o = f.execute(curr, inputs, outputs, o2, executionCtx,
						null, pnode, parent);
				executionCtx.put(curr.getId() + "_result",
						(f.wasSuccess() ? "Successful" : "Failed"));
			}
		}
		List<Callable<Object>> calls = new LinkedList<Callable<Object>>();
		NextCallable:
		for (PNode c : pnode.getChilds()) {
			for(ConnDef cd : o2.getForward().values()){
				if(cd.getCtype().equals("dependency")){
					if(cd.getFrom().equals(pnode.getName())&&cd.getTo().equals(c.getName())){
						continue NextCallable;
					}
				}
			}
			if (!skipList.containsKey(c.getName())) {
				WaitStruct w = waitList.get(curr.getId());
				if (w != null) {
					if (!w.isReady()) {
						System.out.println("SH , Skipping: " + curr.getName());
						continue;
					} else {
						waitList.remove(curr.getId());
					}
				}
				AnonDefObj childCurr = anonCompMap.get(c.getName());
				if (childCurr != null) {
					ParallelTaskCallable pCall = new ParallelTaskCallable(uidStr, sh, ch, psh, executionCtx, c, c, waitList, o2, anonCompMap, allPnodes, v2, childCurr);
					calls.add(pCall);
				}
			}
		}

		if(calls.size()>0){
			ScatterGather2 sc = new ScatterGather2();
			Object[] results = sc.scatterGather(calls.toArray(new Callable[0]));
		}
		List<PNode> ret = new LinkedList<PNode>();
			for (PNode c : pnode.getChilds()) {
				for(ConnDef cd : o2.getForward().values()){
					if(cd.getCtype().equals("dependency")){
						if(cd.getFrom().equals(pnode.getName())&&cd.getTo().equals(c.getName())){
							if (!skipList.containsKey(c.getName())) {
								ret.add(c);
							}
						}
					}
				}
			}
		return ret;
	}


}
