package org.ptg.util.taskfunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class MethodCallTask extends AbstractICompileFunction {

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		Map<String ,String > methodToClassMap = new HashMap<String,String>();
		methodToClassMap.put("calculateWhitePixelCount","AbstractOpencvProcessor");
		if(canExec(anon.getId())){
			started(anon.getId());
			FunctionPoint fo = graph.getFps().get(anon.getId());
			JSONObject jo = (JSONObject) fo.getXref();
			String script = jo.getString("script");
			String method = StringUtils.substringBetween(script, "=",";");
			String methodName =  StringUtils.substringBefore(method,"(");
			methodName = methodName.trim();
			String className = methodToClassMap.get(methodName);
			Object []params = new Object[anon.getAux().size()];
			int i=0;
			for (String  id :anon.getAux()){
				Object val = executionCtx.get("aux_"+anon.getId()+"."+id);
				System.out.println(val);
				params[i++]=val;
			}
			Object ret = ReflectionUtils.invokeStatic(className, methodName, null);
			for (FunctionPortObj out :output){
				String idOut = out.getPo().getId();
				executionCtx.put(idOut,ret);
			}

		finished(anon.getId());
		}
		return null;
	}

}
