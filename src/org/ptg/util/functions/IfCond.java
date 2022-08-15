package org.ptg.util.functions;

import java.util.List;

import org.apache.tools.ant.util.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class IfCond extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output,FPGraph2 o2) {
		StringBuilder solution = new StringBuilder();
		
		String lhs = "";
		String ifExpr = "true";
		if (anon.getAux() != null && anon.getAux().size() > 0) {
			ifExpr = anon.getAux().get(0);
			PortObj p = o2.getPorts().get("aux_" + anon.getId() + "." + ifExpr);
			if (p != null) {
				ifExpr = p.getPortval();
			} else {
				p = o2.getPorts().get("aux_" + anon.getId() + "." + "\"" + ifExpr + "\"");
				if (p != null)
					ifExpr = p.getPortval();
				else
					ifExpr = "";
			}
		}
		solution.append("\t\t if("+ifExpr+"){\n");
		
		if(inputs.size()>0){
			FunctionPortObj s = inputs.get(0);
			if (s.getGrpName().equals("unk")){
				lhs = StringUtils.replace(s.getMyPort().getId(),".","_");
			}else{
				lhs = s.getGrpName() + ".get(currIndex).get ( " + s.getIndex()+" ) ";
			}
		}
		StringBuilder preDefVars = new StringBuilder();
		for(FunctionPortObj sLon: output){
			if (sLon.getGrpName().equals("unk")){
				preDefVars.append("Object "+ StringUtils.replace(sLon.getPo().getId(),".","_")+" = null;\n");
				solution.append(StringUtils.replace(sLon.getPo().getId(),".","_") + " = " + lhs +"  ; \n");
			}else{
				solution.append(sLon.getGrpName() + getRHSSetterFunction()+"  ( " + sLon.getIndex() + " , " + lhs + " ) ; \n");
				solution.append ( "\t\t} \n");
			}
		}
		preDefVars.append(solution);
		return preDefVars.toString();
	}

}
