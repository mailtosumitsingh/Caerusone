/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.ptg.events.Event;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.Constants;
import org.ptg.util.GenericException;
import org.ptg.util.IEventDBTransformer;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;
import org.ptg.util.ReflectionUtils;


public class DBUpdateProcessor implements IProcessor {
	private String streamName;
	private IEventDBTransformer dbtransformer ;
	String table = "";
	Stream stream; 
	String ufield;
	public String getStreamName() {
		return streamName;
	}
	public IStreamTransformer getTransformer() {
		return null;
	}
	public void attach(String streamName) throws GenericException {
		this.streamName = streamName;
		try{
		Class c = StreamManager.getInstance().getStreamTransformer(streamName);
		stream = StreamManager.getInstance().getStream(streamName);
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(stream.getEventType());
		dbtransformer  = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
		table = stream.getExtra();
		if(table==null||table.length()==0){
			EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(stream.getEventType());
			if(ed!=null)
			dbtransformer.setStore(ed.getEventStore());
		}else{
			dbtransformer.setStore(table);
		}
		}catch(Exception e){
			throw new GenericException("Cannot find stream transformer",e);
		}
	}


	public void process(Exchange msg) throws Exception {
		Message m = msg.getIn();
		m.setHeader(Constants.EM_STREAM, streamName);
		Event e = (Event)m.getBody();
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(e.getEventType());
		dbtransformer.update(e);
	}
	public void setQuery(String s) {
		ufield = s;
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
		// TODO Auto-generated method stub
		return null;
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



