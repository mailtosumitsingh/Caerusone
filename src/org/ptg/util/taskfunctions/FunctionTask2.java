package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.util.ReflectionUtils;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class FunctionTask2 extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
		String cls = anon.getAux().get(0);
		String mtd = anon.getAux().get(1);
		Object []params = new Object[inputs.size()];
		int i=0;
		for (FunctionPortObj fobj :inputs){
			String id = fobj.getMyPort().getId();
			PortObj opp = fobj.getPo();
			Object val = executionCtx.get(id);
			System.out.println(val);
			params[i++]=val;
		}
		Object ret = ReflectionUtils.invokeStatic(cls,mtd, params);
		for (FunctionPortObj out :output){
			String idOut = out.getPo().getId();
			executionCtx.put(idOut,ret);
		}
		finished(anon.getId());
		}
		return null;
	}

}
