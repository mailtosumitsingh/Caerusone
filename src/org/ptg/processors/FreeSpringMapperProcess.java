/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;

public class FreeSpringMapperProcess extends AbstractIProcessor{
	Object o ;
	public void childAttach() throws GenericException {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("eventtype", stream.getEventType());
		
			o = CommonUtil.instantiateMappingGraph2(query,"FreeSpring",params);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		}

	public void childProcess(Exchange msg) throws Exception {
		if(o!=null){
			if (o != null) {
				Method mtd = null;
				
				mtd = o.getClass().getMethod("execute", new Class[]{Object.class});
				if(mtd==null){
					mtd = o.getClass().getMethod("run", new Class[0]);
				}
				if (mtd!=null) {
					if(mtd.getName().equals("run")){
						 mtd.invoke(o, new Object[0]);
					}else if(mtd.getName().equals("execute")){
						mtd.invoke(o, new Object[]{msg});
					}
								
				}
			}
		}
	}

	@Override
	public String getDoc() {
		return "This processor will take the \"query\" parameter.\n"+
		"where query is the name of mapping to load and execute on event.\n" +
		"This is same as MapperProcess except that is more arbit in executing the \n" +
		"mapping code which uses apache commons closure";
	}
}