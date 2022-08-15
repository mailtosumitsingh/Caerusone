package org.ptg.util.functions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class Concat extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		if (super.isObjectMapper()) {
			return compileObject(anon, inputs, output, graph);
		} else {
			return compileMultiRowRecord(inputs, output, graph);
		}
	}

	private Object compileMultiRowRecord(List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		String ret = " (";
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			PortObj po = s.getPo();
			if (po == null || s.getIndex() == -1) {
				ret += ("(String)" + handleUnk(s, graph));
			} else {
				String castExpr = StringUtils.split(po.getDtype(), "/")[0];
				if (castExpr != null || castExpr.length() > 0)
					ret += ("(" + castExpr + ")");
				ret += s.getGrpName();
				ret += (".get(currIndex). get( " + s.getIndex() + " )");
			}
			if (i < inputs.size() - 1) {
				ret += "+";
			}
		}
		ret += ") ";
		StringBuilder preDefVars = new StringBuilder();

		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj sLat = output.get(i);
			if (sLat.getGrpName().equals("unk")) {
				preDefVars.append("Object " + getSafeIdentifier(sLat.getPo().getId()) + " = null;\n");
				solution.append(getSafeIdentifier(sLat.getPo().getId()) + " = " + ret + "  ; \n");
			} else {
				solution.append(sLat.getGrpName() + getRHSSetterFunction() + " ( " + sLat.getIndex() + " , " + ret + " ) ; \n");
			}

		}
		preDefVars.append(solution);
		return preDefVars.toString();
	}

	public Object compileObject(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> outputs, FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		List<Expression> inputExpr = getInputExpressionList(inputs, graph);
		for (int i = 0; i < inputExpr.size(); i++) {
			Expression exp = inputExpr.get(i);//should return a variable name, variabletype, and code to read value;
			if(exp!=null){
			compileInputExpr(solution, exp,false);
			}
		}
		 
		String compiledExpr = getExpressionStrWithOperator(inputExpr,"+");
		for (int i = 0; i < outputs.size(); i++) {
			FunctionPortObj s = outputs.get(i);
			//should take a list of variables and output expression then will use base class to compiel the while expr 
			Expression outExp = processOutput(graph,  s);
			if(outExp!=null){
				compileOutputExpr(solution, compiledExpr, outExp); 
			}
		}
		System.out.println(solution.toString());
		return solution.toString();
	}

}
