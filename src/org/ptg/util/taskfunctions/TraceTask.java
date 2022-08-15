package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class TraceTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){	
			started(anon.getId());
		Object val = getAuxPortVal(anon, graph, executionCtx, anon.getAux().get(0));
			for (FunctionPortObj out :output){
					String idOut = out.getPo().getId();
					executionCtx.put(idOut,val );
					System.out.println("Trace: "+val);
			}
			finished(anon.getId());
		}
		return "";
	}

}
