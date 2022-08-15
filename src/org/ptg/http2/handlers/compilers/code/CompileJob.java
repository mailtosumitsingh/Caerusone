/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.compilers.code;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.titan.TitanCompiler;

public class CompileJob extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		try {
			final String path =base+File.separator+"uploaded"+File.separator+"extrajob"+File.separator ;
			String dest = name+".java";
			//code = StringEscapeUtils.unescapeJavaScript(code);
			String origCode = code;
			code = CommonUtil.extractTextFromHtmlTitan(code);
			code = TitanCompiler.compile(code);
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + name+".titan"), origCode.getBytes());
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), code.getBytes());
			Class t = CommonUtil.getUploadedJobClass(path, name,name);
			//Object o =t.newInstance();
			//Runnable r = (Runnable)o;
			//r.run();
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().print("Compiled job File : "+name);
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile Job:\n"+e.toString());
			String s = e.getCause().getMessage();
			int line = Integer.parseInt(s.split(":")[2]);
			String ln = code.split("\n")[line-1];
			response.getOutputStream().print("BadCode at \n"+ln);
			
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}
}