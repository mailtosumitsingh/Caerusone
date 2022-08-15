/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.IStreamTransformer;

public class MapperProcess extends AbstractIProcessor{
	IStreamTransformer cls ;
	public void childAttach() throws GenericException {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("eventtype", stream.getEventType());
		
			Object  o = CommonUtil.instantiateMappingGraph2(query,"EventProcessor",params);
			if(o!=null){
				if(o instanceof IStreamTransformer){
					cls = (IStreamTransformer)o;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}

	public void childProcess(Exchange msg) throws Exception {
		if(cls!=null){
			cls.transformExchange(msg);
		}
	}

	@Override
	public String getDoc() {
		return "This processor will take the \"query\" parameter.\n"+
		"where query is the name of mapping to load and execute on event";
	}
}