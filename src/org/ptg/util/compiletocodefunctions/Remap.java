package org.ptg.util.compiletocodefunctions;

import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class Remap implements CodeGenHandler{

	@Override
	public void generateCode(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		org.ptg.util.functions.Expression exp = CommonUtil.getMyInExpression(o,curr,curr.getAux().get(0),"java");
		System.out.println("Got a remap");
		code.addCodeLine(exp.getDtype()+" "+CommonUtil.getSafeIdentifier(curr.getId())+" = "+exp.getVal()+";",currName);
		
	}

}
