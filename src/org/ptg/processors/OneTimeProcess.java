/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import org.apache.camel.Exchange;
import org.apache.commons.collections.Closure;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.DynaObjectHelper;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;


public class OneTimeProcess implements IProcessor{
	String name;
	String streamName;
	Stream stream;
	String code ;
	Closure _closure;
	public void attach(String streamName) throws GenericException {
		this.streamName = streamName;
		try{
		Class c = StreamManager.getInstance().getStreamTransformer(streamName);
		stream = StreamManager.getInstance().getStream(streamName);
		}catch(Exception e){
			throw new GenericException("Cannot find stream transformer",e);
		}
		try {
			Class c = DynaObjectHelper.getClosureImpl("CustomCodeProcessor"+name+CommonUtil.getRandomString(6), code);
			_closure = (Closure)c.newInstance();
			_closure.execute(stream);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	public String getStreamName() {
		return streamName;
	}

	public IStreamTransformer getTransformer() {
		return null;
	}

	public void process(Exchange msg) throws Exception {
		System.out.println("Ontime process only executes during attachment.");
	}
	public void setQuery(String s) {
		code = s;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name= name;
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
