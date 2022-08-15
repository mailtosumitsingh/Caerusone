package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class ConstantTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
		for (FunctionPortObj fobj :output){
			Object val  = getMyPortVal(graph, executionCtx, fobj);
			setPortVal(graph,executionCtx,fobj,val);
		}
		finished(anon.getId());
		}
		return null;
	}

}
