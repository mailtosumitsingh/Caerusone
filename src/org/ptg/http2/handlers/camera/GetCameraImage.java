/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.camera;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.HTTPClientUtil;

public class GetCameraImage extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String index=request.getParameter("index");
		String width=request.getParameter("width");
		String height=request.getParameter("height");
		if(index==null) {
			index = "0";
		}
		if(width==null) {
			width = "1280";
		}
		if(height==null) {
			height = "720";
		}
		int idx = Integer.parseInt(index);
		int w = Integer.parseInt(width);
		int h = Integer.parseInt(height);
		String url = "http://localhost:8080/getCameraImage";
		String json = "";
		try {
			byte[] bytes = HTTPClientUtil.doPostWithBody(json, url);
			//do something with image
		} catch (Exception e) {
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}

