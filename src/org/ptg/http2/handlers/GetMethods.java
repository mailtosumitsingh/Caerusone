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

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;


public class GetMethods extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = (String) arg1.getParameter("classname");
		// later someday//String mtdname = (String)
		// arg1.getParameter("mtdname");
		Collection<String> fps = new ArrayList<String>();
		ClassPool c = CommonUtil.getClassPool();
		CtClass cl;
		try {
			cl = c.get(name);
			CtMethod[] cms = cl.getDeclaredMethods();
			for (CtMethod cm : cms) {
				String mm=cm.getName()+" ( ";
				CtClass[]cc= cm.getParameterTypes();
				for(int i=0;i<cc.length;i++){
					mm+=cc[i].getName();
					//mm+=" param"+i;
					if(i<cc.length-1){
						mm+=(" ,");
					}
				}
				mm+=" )";
				fps.add(mm);
			}
			CtConstructor[] cts = cl.getConstructors();
			for (CtConstructor ct : cts) {
				String mm=ct.getName()+" ( ";
				CtClass[]cc= ct.getParameterTypes();
				for(int i=0;i<cc.length;i++){
					mm+=cc[i].getName();
				//	mm+=" param"+i;
					if(i<cc.length-1){
						mm+=(" ,");
					}
				}
				mm+=" )";
				fps.add(mm);
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		String ret = CommonUtil.jsonFromArray(fps);
		response.getWriter().write(ret);
		((Request) request).setHandled(true);
	}
}
