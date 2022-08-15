package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class LoopBackTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
		for (FunctionPortObj fobj :inputs){
			Object val = getPortVal(graph, executionCtx, fobj);
			for (FunctionPortObj out :output){
				 double val2 = 20+Double.parseDouble(val.toString());
				setPortVal(graph, executionCtx, out,val2);  
				 PortObj po = out.getPo();
				 if(po!=null){
					 po.setPortval(""+val2);
				 }
			}
		}
		finished(anon.getId());
		}
		return "";
	}

	

}
