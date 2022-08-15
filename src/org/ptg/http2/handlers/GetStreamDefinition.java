/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.Constants;

public class GetStreamDefinition extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = (String) request.getParameter(Constants.StreamName);
		if(name!=null){
		Stream defs = StreamManager.getInstance().getStream(name);
		Collection<String[]> s= new ArrayList<String[]>();
		String ret  = CommonUtil.jsonFromArray(defs.getDefs().values().toArray());
		response.getWriter().write(ret);
	}
		((Request) request).setHandled(true);
		
	}
	
}
