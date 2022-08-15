/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.processors;

import java.util.Collection;

import org.apache.camel.Exchange;
import org.ptg.events.Event;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.v2.FPGraph2;

public class MicroProcessGraph extends AbstractIProcessor {
	IStreamTransformer cls;
	FPGraph2 g;

	public void childAttach() throws GenericException {
		try {
			g = CommonUtil.buildMappingGraph2(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void childProcess(Exchange msg) throws Exception {
		String action = ((Event)msg.getIn().getBody()).getEventStringProperty(extra);
		if (action != null) {
			Collection<FunctionPoint> fps = g.getMainGraph().getGraph().getSuccessors(g.getMainGraph().getFunctionPoints().get(action));
			if (fps.size() > 0) {
				FunctionPoint fpTarget = fps.iterator().next();
				String where = fpTarget.getId();
				IProcessor p = org.ptg.processors.ProcessorManager.getInstance().getProcessorFromRoutingTable(where);
				if (p != null) {
					p.process(msg);
				}
			}
		}
	}

	@Override
	public String getDoc() {
		return "This processor will take the \"query\" parameter.\n" + "where query is the name of mapping to load and execute on event";
	}
}