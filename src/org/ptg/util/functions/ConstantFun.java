package org.ptg.util.functions;

import java.util.List;

import org.apache.tools.ant.util.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class ConstantFun extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 o2) {
		StringBuilder solution = new StringBuilder();
		solution.append("\t\t try{\n");
		String expr = "";
		StringBuilder preDefVars = new StringBuilder();
		if (anon.getAux() != null && anon.getAux().size() > 0) {
			expr = anon.getAux().get(0);
			PortObj p = o2.getPorts().get("aux_" + anon.getId() + "." + expr);
			if (p != null) {
				expr = p.getPortval();
			} else {
				p = o2.getPorts().get("aux_" + anon.getId() + "." + "\"" + expr + "\"");
				if (p != null)
					expr = p.getPortval();
				else
					expr = "";
			}
		}
		boolean isNumber = true;
		for(char c: expr.toCharArray()){
			if(!(Character.isDigit(c)||c=='.')){
				isNumber=false;
				break;
			}
		}
		if(!isNumber||(expr.length()==1 &&expr.equals("."))){
			expr = "\"" + expr + "\"";
		}
		for (FunctionPortObj sLon : output) {
			String tempexpr = expr;
			if (sLon.getPo().getDtype().contains("String")) {
				tempexpr =  expr ;
			}
			if (sLon.getGrpName().equals("unk")){
				preDefVars.append("Object "+ StringUtils.replace(sLon.getPo().getId(),".","_")+" = null;\n");
				solution.append(StringUtils.replace(sLon.getPo().getId(),".","_") + " = " + tempexpr+"   ; \n");
			}else{
				solution.append(sLon.getGrpName() + getRHSSetterFunction()+" ( " + sLon.getIndex() + " ,"+tempexpr+" ) ; \n");
			}
		}
		solution.append("\t}catch(Exception exp){\nlogError(currIndex,\"" + anon.getId() + ":" + anon.getAnonType() + "\",exp.getMessage());\n } \n");
		preDefVars.append(solution);
		return preDefVars.toString();

	}

}
