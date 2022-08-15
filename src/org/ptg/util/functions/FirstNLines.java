package org.ptg.util.functions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class FirstNLines extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		solution.append("{\n");
		String ret = " (";
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			PortObj po = s.getPo();
			if (po == null || s.getIndex() == -1) {
				ret += "(String)" + handleUnk(s, graph);
			} else {
				ret += "(" + StringUtils.split(po.getDtype(), "/")[0] + ")";
				ret += s.getGrpName();
				ret += ".get(currIndex).get ( " + s.getIndex() + " )";
			}
			if (i < inputs.size() - 1) {
				ret += "+";
			}
		}
		ret += ") ";

		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj s = output.get(i);
			solution.append(s.getGrpName() + getRHSSetterFunction() + " ( " + s.getIndex() + " , firstn(" + ret + "," + anon.getAux().get(0) + " )) ; \n");
		}
		solution.append("}\n");
		return solution.toString();
	}

}
