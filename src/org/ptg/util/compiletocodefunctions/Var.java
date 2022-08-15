package org.ptg.util.compiletocodefunctions;

import org.ptg.processors.ConnDef;
import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class Var  implements CodeGenHandler{

	@Override
	public void generateCode(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		String varName = "i";
		String varType = "int";
		String varInitVal = "0";
		{
			String vtypePort = "aux_" + curr.getId() + "." + curr.getAux().get(0);
			String vnamePort = "aux_" + curr.getId() + "." + curr.getAux().get(1);
			String initValPort = "aux_" + curr.getId() + "." + curr.getAux().get(2);
			varType = o.getPorts().get(vtypePort).getPortval();
			varName = o.getPorts().get(vnamePort).getPortval();
			PortObj varInitPortObj = o.getPorts().get(initValPort);
			varInitVal = varInitPortObj.getPortval();
			org.ptg.util.functions.Expression exp = null;
			if(curr!=null && curr.getAux()!=null && curr.getAux().size()>0){
				exp = CommonUtil.getMyInExpression(o,curr,curr.getAux().get(2),"java");
			}
			if(exp!=null){
				varInitVal = exp.getVal() ;
			}else{
				String val = varInitPortObj.getPortval();
						for(ConnDef cd : o.getForward().values()){
							if(cd.getTo().equals(varInitPortObj.getId())){
								FunctionPoint fpp = o.getFunctionPoints().get(cd.getFrom());
								if(fpp!=null){
									val =  fpp.getVal();
									break;
								}else{
									PortObj from  = o.getPorts().get(cd.getFrom());
									if(from!=null){
										varInitVal = from.getPortval();	
									}
								}
					}
				}
			}
		}
		code.addCodeLine( varType+" "+varName +" = "+varInitVal+ ";",curr.getId());
	}

}
