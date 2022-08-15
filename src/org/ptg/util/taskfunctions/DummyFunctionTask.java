package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class DummyFunctionTask extends AbstractICompileFunction {
	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
		StringBuilder temp  = new StringBuilder();
		for (FunctionPortObj fobj :inputs){
			String id = fobj.getMyPort().getId();
			temp.append(executionCtx.get(id));
		}
		
		for (FunctionPortObj fobj :output){
			String id = fobj.getPo().getId();
			executionCtx.put(id, temp.toString());
		}
		finished(anon.getId());
		}
		
		return null;
	}

}
