/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import org.apache.camel.Exchange;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;


public class NullProcessor  implements IProcessor {
	public String getStreamName() {
		return "NullStream";
	}
	public IStreamTransformer getTransformer() {
		return null;
	}
	public void attach(String streamName) throws GenericException {
	
	}


	public void process(Exchange msg) throws Exception {
	   //i donot nothing.
		System.out.println("Null stream got a message");
	}
	@Override
	public void setQuery(String s) {
		// TODO Auto-generated method stub
		
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


