/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.AnnotSpec;
import org.ptg.util.CommonUtil;

public class GetAnnotSpecs extends AbstractHandler {
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Map<String,AnnotSpec> AnnotSpecs = new TreeMap<String,AnnotSpec>(); 
		addAnnotSpecs(AnnotSpecs);
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String json = CommonUtil.jsonFromCollection(AnnotSpecs.values());
			response.getOutputStream().print(json);
		} catch (Exception e) {
			response.getOutputStream().print("Node cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	private void addAnnotSpecs(Map<String, AnnotSpec> AnnotSpecs) {
		AnnotSpec breakSpec = new AnnotSpec("break","break","/site/images/cross.png");
		AnnotSpecs.put(breakSpec.getName(), breakSpec);
	}
	
	public Collection<AnnotSpec> getAnnotSpecs(){
		Map<String,AnnotSpec> AnnotSpecs = new TreeMap<String,AnnotSpec>();
		addAnnotSpecs(AnnotSpecs);
		return AnnotSpecs.values(); 
	}

}


