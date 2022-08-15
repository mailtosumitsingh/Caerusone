package org.ptg.util.compiletocodefunctions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
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

public class JavaMethodCall implements CodeGenHandler{
    Pattern exprPart = Pattern.compile("[a-zA-Z0-9_]+\\.expr(\\(([a-zA-Z0-9_\\,\\[\\]\\(\\)\\.]+)\\))");
    
	@Override
	public void generateCode(CodeBlock codeBldr, String currName, AnonDefObj curr, FPGraph2 o) {
		boolean useVarSubstitution  = true;
		JSONObject jo = (JSONObject) o.getFps().get(curr.getId()).getXref();
		String codeStr = jo.getString("script");
		Map<String,String>portNames = new LinkedHashMap<String,String>();
		for(String s: curr.getAux()){
			portNames.put(StringUtils.substringAfterLast(s, curr.getId()), s);
		}
		for(Map.Entry<String, String>en:portNames.entrySet()){
			PortObj po = o.getPorts().get("aux_"+curr.getId()+"."+en.getValue());
			String portName = en.getKey();
			if(portName.endsWith(".in.value")){
			    for(ConnDef cd :o.getForward().values()){
			    	if(cd.getTo().equals(po.getId())){
			    		PortObj from  = o.getPorts().get(cd.getFrom());
			    		if(from==null){
                            FunctionPoint obj = o.getFunctionPoints().get(from);
                            if(obj!=null){
                            	codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",obj.getVal() );
                            }else{
                            	codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",po.getPortval() );
                            }
			    		}else{
			    			codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",from.getPortval() );	
			    		}
			    		break;
			    	}
			    }
			}else if(portName.endsWith(".in.name")){
			    for(ConnDef cd :o.getForward().values()){
			    	if(cd.getTo().equals(po.getId())){
			    		PortObj from  = o.getPorts().get(cd.getFrom());
			    		if(from==null){
                            FunctionPoint obj = o.getFunctionPoints().get(cd.getFrom());
                            if(obj!=null){
                            	codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",obj.getId() );
                            }else{
                            	codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",po.getPortval() );
                            }
			    		}else{
			    			codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",from.getPortname() );	
			    		}
			    		break;
			    	}
			    }
			}else{
				org.ptg.util.functions.Expression exp = null;
				String val = po.getPortval();
				if(curr!=null && curr.getAux()!=null && curr.getAux().size()>0){
					exp = CommonUtil.getMyInExpression(o,curr,curr.getAux().get(po.getPortindex()),"java");
					if(exp!=null){
						val = exp.getVal() ;
					}else{
						for(ConnDef cd : o.getForward().values()){
							if(cd.getTo().equals(po.getId())){
								FunctionPoint fpp = o.getFunctionPoints().get(cd.getFrom());
								if(fpp!=null){
									val =  fpp.getVal();
									break;
								}else{
									PortObj from  = o.getPorts().get(cd.getFrom());
									if(from!=null){
										codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",from.getPortval() );	
									}
								}
							}
					}
					}
				}
				codeStr = StringUtils.replace(codeStr,"{"+ portName+"}",val );
			}
		}
		portNames.clear();
		for(String s: curr.getOutputs()){
			portNames.put(StringUtils.substringAfterLast(s, curr.getId()), s);
		}
		codeStr = cleanOuts(curr, useVarSubstitution, codeStr, portNames);
		/*write the output*/
		/*was there a outputport if true then set the value
		 * other wise it can be used for the processing of the vallues
		 * */
		boolean found = false;
		for(Map.Entry<String, String>en:portNames.entrySet()){
			PortObj po = o.getPorts().get("out_"+curr.getId()+"."+en.getValue());
			String portName = en.getKey();
			org.ptg.util.functions.Expression exp = null;
			String val = po.getPortval();
			if(val!=null && val.length()>0){
				codeStr = po.getPortval();
			}else{
				if(useVarSubstitution){
					String safeId = CommonUtil.getSafeIdentifier(curr.getId());
					safeId = safeId + "_" + portName;
					po.setPortval(safeId);
				}
			}
			if(curr!=null && curr.getOutputs()!=null && curr.getOutputs().size()>0){
				String pName = curr.getOutputs().get(po.getPortindex());
				exp = CommonUtil.getMyOutExpression(o,curr,pName,"java");
				if(exp!=null){
					val = exp.getVal() ;
					codeBldr.addCodeLine((StringUtils.replace(val,"{1}",codeStr )+";"),curr.getId());
					found =true;
				}else{
					if(po.getPortname().endsWith(".myid")){
						po.setPortval(CommonUtil.getSafeIdentifier(curr.getId()));
					}else{ 
						String expr = (po.getPortname());
						if(expr!=null && expr.length()>0 ){
							expr= StringUtils.substringAfterLast(po.getPortname(),po.getGrp());
							Matcher m = exprPart.matcher(expr);
							if(m.matches()){
								String exprVal = m.group(2);
								po.setPortval(exprVal);
							}
					}else{
						po.setPortval(codeStr);
					}
					}
				}
			}
		}
		if(!found){
		codeBldr.addCodeLine(codeStr,curr.getId());
		}		
	}

	public String cleanOuts(AnonDefObj curr, boolean useVarSubstitution, String codeStr, Map<String, String> portNames) {
		/*
		 * Clean codestr replace with the variable substitution.
		 * */
		for(Map.Entry<String, String>en:portNames.entrySet()){
			String portName = en.getKey();
			if(useVarSubstitution){
				String safeId = CommonUtil.getSafeIdentifier(curr.getId());
				safeId = safeId + "_" + portName;
				codeStr = StringUtils.replace(codeStr,"<"+ portName+">",safeId );	
			}else{
				codeStr = StringUtils.replace(codeStr,"<"+ portName+">","" );
			}
		}
		return codeStr;
	}

}
