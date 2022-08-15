package org.ptg.util.compiletocodefunctions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class JavaVizCall implements CodeGenHandler{
    Pattern exprPart = Pattern.compile("[a-zA-Z0-9_]+\\.expr(\\(([a-zA-Z0-9_\\,\\[\\]\\(\\)\\.]+)\\))");
    
	@Override
	public void generateCode(CodeBlock codeBldr, String currName, AnonDefObj curr, FPGraph2 o) {
		boolean useVarSubstitution  = true;
		JSONObject jo = (JSONObject) o.getFps().get(curr.getId()).getXref();
		StringBuilder codeStr = new StringBuilder();
		Map<String,String>portNames = new LinkedHashMap<String,String>();
		for(String s: curr.getAux()){
			portNames.put(StringUtils.substringAfterLast(s, curr.getId()), s);
		}
		for(Map.Entry<String, String>en:portNames.entrySet()){
			PortObj po = o.getPorts().get("aux_"+curr.getId()+"."+en.getValue());
			String portName = en.getKey();
				String val = null;
				if(curr!=null && curr.getAux()!=null && curr.getAux().size()>0){
						for(ConnDef cd : o.getForward().values()){
							if(cd.getTo().equals(po.getId())){
								FunctionPoint fpp = o.getFunctionPoints().get(cd.getFrom());
								if(fpp!=null){
									val =  fpp.getVal();
									break;
								}else{
									PortObj from  = o.getPorts().get(cd.getFrom());
									if(from!=null){
										if(codeStr.length()>0){
										codeStr .append(",");
										}
										codeStr.append(from.getPortval() );
									}
								}
							}
					}
				}
		}
		String safeIdentifier = CommonUtil.getSafeIdentifier(curr.getId());
		String code = "VizDataEvent "+safeIdentifier +" = new VizDataEvent(\""+curr.getId()+"\","+codeStr.toString()+");";
		codeBldr.addCodeLine(code,curr.getId());
		code = "ContPublisherWriter.getInstance().loadEvent("+safeIdentifier+");";
		codeBldr.addCodeLine(code,curr.getId());
	}

}
