/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors.sys;

import org.apache.camel.Exchange;
import org.ptg.admin.AppContext;
import org.ptg.util.Constants;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;


public class WireTapProcessor implements IProcessor{
	String sname;
	public void process(Exchange arg0) throws Exception {
		String name = (String) arg0.getIn().getHeader(Constants.STREAMIN);
		if(name!=null){
		System.out.println(name);
		AppContext.getInstance().incrStat(name+"_Messages");
		}
	}
	public void attach(String streamName) throws GenericException {
		sname = streamName;
	}

	public String getStreamName() {
		return sname;
	}

	public IStreamTransformer getTransformer() {
		return null;
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
