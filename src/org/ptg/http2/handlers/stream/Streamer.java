/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.stream;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.ContPublisherWriter;

public class Streamer extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ContPublisherWriter tempECWriter =null;
        try{
        	response.setContentLength(-1);
             tempECWriter = ContPublisherWriter.getInstance();
             while(tempECWriter.hasNextChunk()){
             String nextMsg = tempECWriter.nextMsg();
			response.getWriter().write(nextMsg);
             response.getWriter().flush();
             //break;
             }
             }finally{
            ContPublisherWriter.removeInstance(tempECWriter);
        }
		
		((Request) request).setHandled(true);
	}
}
