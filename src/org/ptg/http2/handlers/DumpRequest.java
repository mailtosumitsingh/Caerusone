/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebStartProcess;
import org.ptg.events.MapWrapperEvent;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.events.ParseURLEventV2;

/**
 * 
 */
public class DumpRequest extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			String urls = request.getParameter("urls");
			String foundat = request.getParameter("foundat");
			String body = request.getParameter("bodycontent");
			
			final String path = base + File.separator + "uploaded" + File.separator + "in" + File.separator;
			String dest = CommonUtil.getRandomString(24);
			String fname = path + dest;
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(fname), body.getBytes());
			//SearchParseURLSaveStream
			String[] allurls = StringUtils.split(urls,',');
			for(String s : allurls){
				if(!s.contains("/"))//remove functions etc.
					continue;
				ParseURLEventV2 v2 = new ParseURLEventV2();
			v2.setParser("titancrawler");
			v2.setFoundat(foundat);
			v2.setUrl(s);
			CommonUtil.sendAndWait("SearchParseURLSaveStream", v2);
			
			//send to iterator
			Exchange ex = WebStartProcess.getInstance().getRoutingEngine().getDefaultExchange();
			ex.getIn().setHeader("toadd", v2);
			MapWrapperEvent evt = new MapWrapperEvent();
			evt.addProp("opcode", "add");
			ex.getIn().setBody(evt);
			CommonUtil.sendAndWait("IterateNextURLStream", ex);
			
			
			
			}
			org.ptg.events.PageDumpEvent e  = new org.ptg.events.PageDumpEvent();
			e.setFoundat(foundat);
			e.setFilename(fname);
			CommonUtil.sendAndWait("Random_717", e);
	
			Exchange ex = WebStartProcess.getInstance().getRoutingEngine().getDefaultExchange();
			ex.setProperty("query", foundat);
			ex.getIn().setBody(body);
			CommonUtil.sendAndWait("WordBreakProcessorStream", ex);
			
			
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().print("done.");
		} catch (Throwable e) {
			response.getOutputStream().print("Could not compile:\n" + e.toString());
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
