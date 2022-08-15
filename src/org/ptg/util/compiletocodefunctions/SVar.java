package org.ptg.util.compiletocodefunctions;

import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class SVar  implements CodeGenHandler{

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
			varInitVal = o.getPorts().get(initValPort).getPortval();
			org.ptg.util.functions.Expression exp = null;
			if(curr!=null && curr.getAux()!=null && curr.getAux().size()>0){
				exp = CommonUtil.getMyInExpression(o,curr,curr.getAux().get(2),"java");
			}
			if(exp!=null){
				varInitVal = exp.getVal() ;
			}
		}
		String prefix = CommonUtil.getDataTypePrefix(varType);
		if(prefix !=null )
			code.addCodeLine( varType+" "+varName +" = ("+varType+") CommonUtil.getVar"+prefix+"(\""+ varName+ "\","+varInitVal+");",curr.getId());
		else
			code.addCodeLine( varType+" "+varName +" = ("+varType+") CommonUtil.getVar"+prefix+"(\""+ varName+ "\","+varInitVal+");",curr.getId());
	}

}
