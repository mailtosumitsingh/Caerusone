package org.ptg.util.functions;

import java.util.List;

import org.apache.tools.ant.util.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class LookUp extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output,FPGraph2 o2) {
		StringBuilder solution = new StringBuilder();
		solution.append("\t\t try{\n");
		String expr = " ";
		if(anon.getAux()!=null && anon.getAux().size()>0){
			expr = anon.getAux().get(0);
			PortObj p = o2.getPorts().get("aux_"+anon.getId()+"."+expr);
			if(p!=null){
					expr = p.getPortval();
			}else{
				p = o2.getPorts().get("aux_"+anon.getId()+"."+"\""+expr+"\"");
				if(p!=null)
					expr = p.getPortval();
				else
					expr="";
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
		FunctionPortObj s = inputs.get(0);
		String lhs = "";
		if (s.getGrpName().equals("unk")){
			lhs = StringUtils.replace(s.getMyPort().getId(),".","_")+";\n";
		}else{
			lhs = s.getGrpName() + ".get(currIndex).get ( " + s.getIndex()+" );\n ";
		}
		solution.append("String "+" invar "+" = "+lhs);
		StringBuilder preDefVars = new StringBuilder();
		
		for(FunctionPortObj outp: output){
			if (outp.getGrpName().equals("unk")){
				preDefVars.append("Object "+ StringUtils.replace(outp.getPo().getId(),".","_")+" = null;\n");
				solution.append(StringUtils.replace(outp.getPo().getId(),".","_") + " = " + " , lookup(" + expr + ",invar) )  ; \n");
			}else{
				solution.append(outp.getGrpName() + getRHSSetterFunction()+"  ( " + outp.getIndex() + " , lookup(" + expr + ",invar) ) ; \n");
			}
		}
		solution.append ( "\t}catch(Exception exp){\nlogError(currIndex,\""+anon.getId()+":"+anon.getAnonType()+"\",exp.getMessage());\n } \n");
		preDefVars.append(solution);
		return preDefVars.toString();
	}

}
