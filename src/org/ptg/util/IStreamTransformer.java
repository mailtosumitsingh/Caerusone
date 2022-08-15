/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;

public interface IStreamTransformer {
	public Object transformStream (org.apache.camel.Message msg);
	public Object transformStream (org.apache.camel.impl.DefaultMessage msg);
	public Object transformJsonStream (org.apache.camel.Message msg);
	public Object transformJsonStream (org.apache.camel.impl.DefaultMessage msg);
	public Object transformObject (org.ptg.events.Event msg);
	public Object transformResultSet (java.sql.ResultSet msg);
	public Object transformMap (java.util.Map msg);
	public Object transformHTTP (HttpServletRequest request);
	public Object transformExchange (Exchange msg);
	public Object transformRow (Object[] row);
	public Object transformRow (List<Object> row);
	
}
