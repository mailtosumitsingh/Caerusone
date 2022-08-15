package org.ptg.util.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ptg.http2.handlers.compilers.graph.CompileTaskPlanV2;
import org.ptg.util.ITaskFunction;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class ComplexNodeHandler {
	private Map<String, ITaskFunction> functions;

	public ComplexNodeHandler(Map<String, ITaskFunction> functions) {
		this.functions = functions;
	}

	public List<PNode> handle(PNode pnode, PNode parent, Map<String, WaitStruct> waitList, AnonDefObj curr, Map<String, Object> executionCtx, List<FunctionPortObj> inputs, List<FunctionPortObj> outputs, FPGraph2 o2, Map<String, AnonDefObj> anonCompMap, CompileTaskPlanV2 compileTaskPlanV2) {
		Map<String, String> skipList = new HashMap<String, String>();
		if (curr != null) {
			System.out.println("CH , Executing: " + curr.getName());
			WaitStruct w = waitList.get(curr.getId());
			if(w!=null){
				if(!w.isReady()){
					System.out.println("SH , Skipping: " + curr.getName());
					return null;
				}else{
					waitList.remove(curr.getId());
				}
			}
			System.out.println("SH , RealExecution: " + curr.getName());
			ITaskFunction f = functions.get(curr.getAnonType());
			if (f != null) {
				executionCtx.put(curr.getId() + "_result", "Failed");
				f.setConfigItems(curr.getConfigItems());
				Object o = f.execute(curr, inputs, outputs, o2, executionCtx, null, pnode, parent);
				executionCtx.put(curr.getId() + "_result", (f.wasSuccess() ? "Successful" : "Failed"));
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
						// add all other to skip list , to be used for switch
						// and fanout cases
						// all the other items that
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
		List<PNode> ret = new LinkedList<PNode>();
		for(PNode c : pnode.getChilds()){
			if(!skipList.containsKey(c.getName())) ret.add(c);
		}
		return ret;
	}
}
