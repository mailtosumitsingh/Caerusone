/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.proptest.Generate;
import org.ptg.proptest.PropGraph;
import org.ptg.util.PropInfo;

public class GetClassLayout extends AbstractHandler {
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = (String) arg1.getParameter("classname");
		String newname = (String) arg1.getParameter("newclassname");
		String id = (String) arg1.getParameter("id");
		String force = (String) arg1.getParameter("type");
		String isRemap = arg1.getParameter("isremap");
		boolean addRemapPort  = false;
		if(isRemap!=null && isRemap.equalsIgnoreCase("true")){
			addRemapPort = true;
		}
		Map<String,String> stack = new LinkedHashMap<String,String>();
		PropGraph pg = new PropGraph();
		PropInfo<PropInfo> prop = pg.analyze(name, null,stack,0);
		if(newname!=null&&newname.length()>0){
			prop.setName(newname);
			prop.setPropClass(newname);
		}
		Generate g = new Generate();
		String ret = null;
		if(newname!=null&&newname.length()>0){
			ret = g.getHtml(id,newname, prop,force,addRemapPort);
		}else{
			ret = g.getHtml(id,name, prop,force,addRemapPort);
		}
		response.getWriter().write(ret);
		((Request) request).setHandled(true);
	}


}