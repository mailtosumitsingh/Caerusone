package org.ptg.util.taskfunctions.sping;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;
import org.ptg.util.taskfunctions.AbstractICompileFunction;

public class LogTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		for (FunctionPortObj fobj :inputs){
			Object val = null;
			PortObj tobj = fobj.getPo();
			String beanName = tobj.getPortname();
			if(beanName.contains(".")&&!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())){
					beanName = StringUtils.substringAfterLast(beanName, ".");
			}
			if (!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())) {
				Object ref = CommonUtil.getDynamicSpringConfig().getBean(tobj.getGrp() + "_" + beanName);
				val = (ref);
				} else {
					Object ref = CommonUtil.getDynamicSpringConfig().getBean(tobj.getGrp() );
					val = (ref);					
				}
			System.out.println("In "+anon.getName()+", val: "+val);
			for (FunctionPortObj out :output){
				 setPortVal(graph, executionCtx, out,val);  
			}
		}
		return "";
	}

	

}
