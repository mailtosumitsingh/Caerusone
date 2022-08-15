/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import org.apache.camel.Exchange;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.SpringHelper;

public class JavaProcessForName extends AbstractIProcessor{
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	IProcessor child = null;
	@Override
	public void attach(String streamName) throws GenericException {
		super.attach(streamName);
		String name = this.name;
			try {
				Class t = CommonUtil.forName(query);
				if(t!=null){
				child = (IProcessor) t.newInstance();
				child.setName(name);
				child.setQuery(query);
				child.setConfigItems(cfg);
				child.attach(streamName);				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	

	@Override
	public void detach() {
		if(child!=null)
		child.detach();
	}
	@Override
	public void process(Exchange msg) throws Exception {
		if(child !=null)
		child.process(msg);
	}
	

}
