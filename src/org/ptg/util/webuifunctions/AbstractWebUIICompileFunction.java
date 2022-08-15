package org.ptg.util.webuifunctions;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public abstract class AbstractWebUIICompileFunction implements IWebUICompileFunction{
    
    public String handleUnk(FunctionPortObj fp,FPGraph2 o){
    	{
    	for(ConnDef cd : o.getForward().values()){
    		if(cd.getFrom().equals(fp.getMyPort().getId())){
    			FunctionPoint fpp = o.getFunctionPoints().get(cd.getTo());
    			if(fpp!=null)
    			return fpp.getVal();
    		}
    	}
    	return "";
    	}
    }
    public String getAuxPortVal(AnonDefObj anon,FPGraph2 graph,Map<String, Object> executionCtx,String auxPort){
    	PortObj f = graph.getPorts().get("aux_"+anon.getId()+"."+auxPort);
		return f.getPortval();
    }
    public Object getPortVal(FPGraph2 graph, Map<String, Object> executionCtx, FunctionPortObj fobj) {
    	String id = fobj.getMyPort().getId();
		PortObj po = fobj.getPo();
		Object val = null;
		if(po==null){
			for(ConnDef cd : graph.getForward().values()){
				if(cd.getTo().equals(fobj.getMyPort().getId())){
					FunctionPoint fpp = graph.getFunctionPoints().get(cd.getFrom());
					if(fpp!=null){
						val =  fpp.getVal();
					}
				}
			}
		}else{
			if(po.getPortval()!=null){
				val  = po.getPortval();
			}else{
				if(executionCtx!=null)
				val = executionCtx.get(id);
			}
		
		}
		return val;
	}
    public String getVarId(PortObj po){
    	return StringUtils.replaceChars(po.getId(),"(),.","_");
    }
}
