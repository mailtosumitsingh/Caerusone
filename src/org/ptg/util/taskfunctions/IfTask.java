package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class IfTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph, Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
			boolean retVal = false;
			if(canExec(anon.getId())){
				started(anon.getId());
			for (FunctionPortObj fobj : inputs) {
				String id = fobj.getMyPort().getId();
				Object val = executionCtx.get(id);
				for (FunctionPortObj out : output) {
					String idOut = out.getPo().getId();
					executionCtx.put(idOut, val);
				}
				if (val != null && Boolean.valueOf(val.toString())) {
					retVal = true;
				}

			}
			}
			finished(anon.getId());
			return retVal;

	}

	public boolean isBlockStart() {
		return true;
	}

	public boolean isConditional() {
		return true;
	}
}
