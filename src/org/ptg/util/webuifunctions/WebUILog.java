package org.ptg.util.webuifunctions;

import java.util.List;

import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class WebUILog extends AbstractWebUIICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		String ret = " (";
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			PortObj po = s.getMyPort();
			if (po != null ) {
				ret += getVarId(po);
			}
			if (i < inputs.size() - 1) {
				ret += "+";
			}
		}
		ret += ") ";
		StringBuilder preDefVars = new StringBuilder();
		solution.append("console.log("+ret+");");
		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj sLat = output.get(i);
			if (sLat.getGrpName().equals("unk")){
				PortObj po = sLat.getPo();
				if(po==null){
					String tempid = CommonUtil.getRandomString(6);
					FunctionPoint fpp =null;
					for(ConnDef cd : graph.getForward().values()){
			    		if(cd.getFrom().equals(sLat.getMyPort().getId())){
			    			fpp = graph.getFunctionPoints().get(cd.getTo());
			    			if(fpp!=null){
			    				break;	
			    			}
			    			
			    		}
			    	}
					if(fpp!=null){
					solution.append("var "+ tempid+" = "+ret+";\n");
					solution.append("dojo.byId(\"bid_"+fpp.getId()+"\").innerHTML="+ret+";\n" );
					}
				}else{
				String varId = getVarId(po);
				preDefVars.append("var "+ varId+" = null;\n");
				solution.append(varId + " = " + ret +"  ; \n");
				}
			}
			
		}
		preDefVars.append(solution);
		return preDefVars.toString();
	}

}
