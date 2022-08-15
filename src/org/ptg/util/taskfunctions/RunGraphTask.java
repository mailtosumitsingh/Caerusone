package org.ptg.util.taskfunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.http2.handlers.compilers.graph.CompileTaskPlanV2;
import org.ptg.util.CommonUtil;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class RunGraphTask extends AbstractICompileFunction {
	String opt1 = "GraphName";
	String opt2 = "Mapping";
	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList,PNode p, PNode parent ) {
		if(canExec(anon.getId())){
			started(anon.getId());
			String name = (String) c.get(opt1);
			if(name!=null){
				String uuid = CommonUtil.getRandomString(8);
				CompileTaskPlanV2 ctp = new CompileTaskPlanV2();
				ctp.init(uuid);
				Map<String,String> replace = new HashMap<String,String>();
				String m = (String) c.get("Mapping");
				if (m != null && m.length() > 0) {
					String[] pairs = StringUtils.split(m, ";");
					int i = 0;
					for (String s : pairs) {
						if (s != null && s.length() > 0) {
							String[] keyval = StringUtils.split(s, ",");
							if (keyval != null && keyval.length == 2) {
								Object val = null;
								if(i<inputs.size())
								val = getMyPortVal(graph, executionCtx, inputs.get(i));
								
								if(val==null)
									replace.put(keyval[0], keyval[0]);
								else
									replace.put(keyval[0], val.toString());
							}
						}
						i++;
					}
				}
				FPGraph2 g = CommonUtil.buildMappingGraph2(name,replace);
				Map<String, Object> map = ctp.runApp(uuid,name, g,1,executionCtx,null);
			}
			finished(anon.getId());
		}
		return null;
	}
	public String getConfigOptions() {
		return "[\""+opt1+"\","+"\""+opt2+"\"]";
	}

}
