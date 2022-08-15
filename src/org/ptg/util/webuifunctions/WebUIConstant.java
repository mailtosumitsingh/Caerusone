package org.ptg.util.webuifunctions;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class WebUIConstant extends AbstractWebUIICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		String ret = " (";
		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj s = output.get(i);
			PortObj po = s.getMyPort();
			String v =  po.getPortval();
			v =StringEscapeUtils.unescapeJavaScript(v);
			ret +=v;
			if (i < inputs.size() - 1) {
				ret += "+";
			}
			break;//wedonotwant to preocess multiple outputs
		}
		ret += ") ";
		StringBuilder preDefVars = new StringBuilder();
		
		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj sLat = output.get(i);
			{
				String varId = getVarId(sLat.getPo());
				preDefVars.append("var "+ varId+" = null;\n");
				solution.append(varId + " = " + ret +"  ; \n");
			}
			
		}
		preDefVars.append(solution);
		return preDefVars.toString();
	}

}
