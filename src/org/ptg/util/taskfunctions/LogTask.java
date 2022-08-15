package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class LogTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
			System.out.println(Thread.currentThread().getId()+" : "+"Log : "+anon.getName()+".");
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		for (FunctionPortObj fobj :inputs){
			Object val = getPortVal(graph, executionCtx, fobj);
			System.out.println("In "+anon.getName()+", val: "+val);
			for (FunctionPortObj out :output){
				 setPortVal(graph, executionCtx, out,val);  
			}
		}
		//failed(anon.getId());
		finished(anon.getId());
		}
		return "";
	}

	

}
