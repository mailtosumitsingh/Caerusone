package org.ptg.util.functions;

import java.util.List;

import org.apache.tools.ant.util.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class ExpressionParser extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output,FPGraph2 o2) {
		StringBuilder solution = new StringBuilder();
		solution.append("\t\t try{\n");
		String lhs = "";
		if(inputs.size()>0){
			FunctionPortObj s = inputs.get(0);
			if (s.getGrpName().equals("unk")){
				lhs = StringUtils.replace(s.getMyPort().getId(),".","_");
			}else{
				lhs = s.getGrpName() + ".get(currIndex).get ( " + s.getIndex()+" ) ";
			}
		}
		String expr = lhs;
		if(anon.getAux()!=null && anon.getAux().size()>0)
			expr = anon.getAux().get(0);

		StringBuilder preDefVars = new StringBuilder();
		for(FunctionPortObj sLon: output){
			if (sLon.getGrpName().equals("unk")){
				preDefVars.append("Object "+ StringUtils.replace(sLon.getPo().getId(),".","_")+" = null;\n");
				solution.append(StringUtils.replace(sLon.getPo().getId(),".","_") + " = " + expr +"  ; \n");
			}else{
				solution.append(sLon.getGrpName() + getRHSSetterFunction()+" ( " + sLon.getIndex() + " , " + expr + " ) ; \n");
			}
		}
		solution.append ( "\t}catch(Exception exp){\nlogError(currIndex,\""+anon.getId()+":"+anon.getAnonType()+"\",exp.getMessage());\n } \n");
		preDefVars.append(solution);
		return preDefVars.toString();
	}

}
