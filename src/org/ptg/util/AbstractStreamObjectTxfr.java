/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.ptg.events.Event;

public class AbstractStreamObjectTxfr implements IStreamTransformer {

	public Object transformStream(Message msg) {
		return null;
	}

	public Object transformStream(DefaultMessage msg) {
		return null;
	}

	public Object transformJsonStream(Message msg) {
		return null;
	}

	public Object transformJsonStream(DefaultMessage msg) {
		return null;
	}

	public Object transformObject(Event msg) {
		return null;
	}

	public Object transformResultSet(ResultSet msg) {
		return null;
	}

	public Object transformMap(Map msg) {
		return null;
	}

	public Object transformHTTP(HttpServletRequest request) {
		return null;
	}

	@Override
	public Object transformExchange(Exchange msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object transformRow(Object[] row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object transformRow(List<Object> row) {
		// TODO Auto-generated method stub
		return null;
	}

}
