/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public interface IProcessor extends Processor {
	public void setQuery(String s);
	public void attach(String streamName)throws GenericException;
	public void process(Exchange msg) throws Exception;
	public String getStreamName() ;
	public IStreamTransformer getTransformer() ;
	public String getName();
	public void setName(String name);
	public String getDoc();
	public void detach();
	public String getConfigItems();
	public void setConfigItems(String s);
	public String getConfigOptions();
}
