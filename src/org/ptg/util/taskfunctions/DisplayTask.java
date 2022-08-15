package org.ptg.util.taskfunctions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ptg.util.ContPublisherWriter;
import org.ptg.util.events.ExecCtxEvent;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class DisplayTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> ctx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
		try {
        	 Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
     		for (AnonDefObj def : graph.getAnonDefs()) {
     			anonCompMap.put(def.getId(), def);
     		}
     		Map<String, Object> ret = new LinkedHashMap<String, Object>();
     		for(Map.Entry<String, Object> en:ctx.entrySet()){
     			String k = en.getKey();
     			Object v = en.getValue();
     			PortObj po=graph.getPorts().get(k);
     			ret.put(anonCompMap.get(po.getGrp()).getName()+"."+po.getPortname(),v);
     		}
     		
        	 ContPublisherWriter.getInstance().loadEvent(new ExecCtxEvent(ret,anon.getId()));
        	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finished(anon.getId());
		}
		return "";
	}

	

}
