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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.titan.TitanCompiler;

public class TitanCompile extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		try {
			final String path =base+File.separator+"uploaded"+File.separator+"titan"+File.separator ;
			
			String dest = name+".titan";
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), code.getBytes());
			
			//code = StringEscapeUtils.unescapeJavaScript(code);
			code = CommonUtil.extractTextFromHtmlTitan(code);
			code = TitanCompiler.compile(code);
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + name +".java"), code.getBytes());
			code = StringEscapeUtils.escapeHtml(code);
			code = StringUtils.replace(code,"\n","<br/>");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().print(code);
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e.toString());
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}
}