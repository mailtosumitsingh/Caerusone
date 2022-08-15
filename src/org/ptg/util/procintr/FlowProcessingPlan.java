/*
\
"Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.procintr;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ptg.util.CommonUtil;
import org.ptg.util.GraphProcessorSupport;
import org.ptg.util.PropInfo;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class FlowProcessingPlan extends GraphProcessorSupport {
	
	public FlowProcessingPlan (){
		
	}

	public void createAndRunGraph(String name, FPGraph2 o,String processingPlan) {
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		Map<String, PropInfo> props = new LinkedHashMap<String, PropInfo>();
	 	//get topologically sorted types
		List<AnonDefObj> types = CommonUtil.topologicallySortAnonDefs(o);
		//get anon def map/build prop info maps and the root tree
	}

}