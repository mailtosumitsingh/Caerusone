package org.ptg.util.graph;

import java.util.List;
import java.util.Map;

import org.ptg.http2.handlers.compilers.graph.CompileTaskPlanV2;
import org.ptg.processors.ConnDef;
import org.ptg.util.ITaskFunction;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class SimpleNodeHandler {
	private Map<String, ITaskFunction> functions;

	public SimpleNodeHandler(Map<String, ITaskFunction> functions) {
		this.functions = functions;
	}

	public List<PNode> handle(PNode pnode, PNode parent, Map<String, WaitStruct> waitList, AnonDefObj curr, Map<String, Object> executionCtx, List<FunctionPortObj> inputs,
			List<FunctionPortObj> outputs, FPGraph2 o2, Map<String, AnonDefObj> anonCompMap, CompileTaskPlanV2 compileTaskPlanV2) {
		if (curr != null) {
			AnonDefObj parallelDependent = null;
			for (ConnDef cd : o2.getForward().values()) {
				String grp = cd.getFrom();
				AnonDefObj def = anonCompMap.get(grp);
				if (cd.getCtype().equals("dependency") && cd.getTo().equals(curr.getId())) {
					if (def != null && def.getTags() != null && def.getTags().contains("AllAtOnce")) {
						parallelDependent = def;
						break;
					}

				}
			}
			if (parallelDependent != null) {
				if (!parent.getName().equals(parallelDependent.getId())) {
					System.out.println("SH , Skipping: " + curr.getName() + " Waiting for parallel : " + parallelDependent.getName() + " to finish.");
					return null;
				}
			}

			System.out.println("SH , Executing: " + curr.getName());
			WaitStruct w = waitList.get(curr.getId());
			if (w != null) {
				if (!w.isReady()) {
					System.out.println("SH , Skipping: " + curr.getName());
					return null;
				} else {
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
			}
		}
		return pnode.getChilds();
	}

}
