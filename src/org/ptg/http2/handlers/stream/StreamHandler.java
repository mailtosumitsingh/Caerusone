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
public class StreamHandler extends AbstractHandler{

    		public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
                response.setContentType("multipart/x-mixed-replace;boundary=XXoXoX");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.flushBuffer();
	        for(int i=0;i<5000000;i++){
				String temp ="";
				//temp ="--XXoXoX\r\n";
				temp = "Content-Type: text/plain \r\n\r\n";
				temp += "{\"data\":\""+i+"\"}";
				temp +="\r\n--XXoXoX\r\n";
				response.getWriter().write(temp.toString());
				response.flushBuffer();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
	        ((Request)request).setHandled(true);
	    }

}
