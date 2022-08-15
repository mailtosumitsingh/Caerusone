package org.ptg.util.webuifunctions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class WebUIWebService extends AbstractWebUIICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		String ret = " (";
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			PortObj po = s.getMyPort();
			if (po != null ) {
				ret += getVarId(po);
			}
			if (i < inputs.size() - 1) {
				ret += "+";
			}
		}
		ret += ") ";
		StringBuilder preDefVars = new StringBuilder();
		String myVarId = StringUtils.replaceChars(anon.getId(),"(),.","_");
		solution.append("var "+myVarId+"  =doGetHtmlSync("+ret+").results[0];");
		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj sLat = output.get(i);
			if (sLat.getGrpName().equals("unk")){
				String varId = getVarId(sLat.getPo());
				preDefVars.append("var "+ varId+" = null;\n");
				solution.append(varId + " = " + myVarId +"  ; \n");
			}
			
		}
		preDefVars.append(solution);
		return preDefVars.toString();
	}


}
