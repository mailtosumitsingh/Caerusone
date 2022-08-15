/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;

/*fixed send to*/
public class FastProcessor implements IProcessor {
	private Stream stream;
	private String sql;
	private String []to;
	public String getStreamName() {
		return stream.getName();
	}			

	public IStreamTransformer getTransformer() {
		return null;
	}

	public void process(Exchange msg) throws Exception {
		for(String output:to){
			IProcessor p = org.ptg.processors.ProcessorManager.getInstance().getProcessorFromRoutingTable(output);
			p.process(msg);
		}
	}

	public void attach(String streamName) throws GenericException {
		try {
			Class c = StreamManager.getInstance().getStreamTransformer(streamName);
			stream = StreamManager.getInstance().getStream(streamName);
			to = StringUtils.split(sql,":");
		} catch (Exception e) {
			throw new GenericException("Cannot find stream transformer", e);
		}
	}
	public void setQuery(String s) {
		this.sql=s;
	}
	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getDoc() {
		String str = "";
		str+=" Fast processor allows you to execute any arbit process of graph<br/>" +
				"in any arbit fashion , you can choose any arbit process via free hand<br/>" +
				"drawing.<br/>";
			
		return str;
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConfigItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfigItems(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConfigOptions() {
		// TODO Auto-generated method stub
		return null;
	}
}
