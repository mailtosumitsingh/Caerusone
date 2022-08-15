package org.ptg.util.functions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class ConcatWithComma extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		String ret = " (";
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			PortObj po = s.getPo();
			if (po == null || s.getIndex() == -1) {
				ret += "(String)" + handleUnk(s, graph);
			} else {
				String castExpr = StringUtils.split(po.getDtype(), "/")[0];
				if (castExpr != null || castExpr.length() > 0) {
					ret += "(" + castExpr + ")";
				}
				ret += s.getGrpName();
				ret += ".get(currIndex). get( " + s.getIndex() + " )";
			}
			if (i < inputs.size() - 1) {
				ret += "+" + "\",\"" + "+";
			}
		}
		ret += ") ";
		StringBuilder preDefVars = new StringBuilder();

		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj sLat = output.get(i);
			if (sLat.getGrpName().equals("unk")) {
				preDefVars.append("Object " + StringUtils.replace(sLat.getPo().getId(), ".", "_") + " = null;\n");
				solution.append(StringUtils.replace(sLat.getPo().getId(), ".", "_") + " = " + ret + "  ; \n");
			} else {
				solution.append(sLat.getGrpName() + getRHSSetterFunction() + " ( " + sLat.getIndex() + " , " + ret + " ) ; \n");
			}

		}
		preDefVars.append(solution);
		return preDefVars.toString();
	}

}
