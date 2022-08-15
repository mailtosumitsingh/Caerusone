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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebSiteConstants;
import org.ptg.velocity.VelocityHelper;

public class SiteUpTest extends AbstractHandler {
	private static Map<String, String> pagemap = new HashMap<String, String>();
	private static Map<String, Object> TemplateParams = new HashMap<String, Object>();
	static {
		pagemap.put("mainpage", "html/welcome.vm");
		pagemap.put("activity", "html/activity.vm");
		pagemap.put("test", "html/test.vm");
		pagemap.put("simple", "html/simple.vm");
		pagemap.put("aedemo", "html/rform.vm");
		pagemap.put("stockdemo", "html/stockinfo.vm");
		TemplateParams.put("hostname", WebSiteConstants.HOST_IP_SITE);
		TemplateParams.put("hostport", "8090");

	}

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String pageid = request.getParameter("pageid");
		StringBuffer responseContent = null;
		try {
			if (pageid != null) {
				String fname = pagemap.get(pageid);
				if (fname != null) {
					TemplateParams.put("now", System.currentTimeMillis());
					responseContent = VelocityHelper.burnTemplate(TemplateParams, fname);
				} else {
					File tempfile = new File("extra/html/" + pageid + ".vm");
					if (tempfile.exists()) {
						TemplateParams.put("now", System.currentTimeMillis());
						responseContent = VelocityHelper.burnTemplate(TemplateParams, "html/" + pageid + ".vm");
					}
				}

			}
			if(responseContent!=null)
				response.getWriter().write(responseContent.toString());
			else
				response.getOutputStream().print("Could not return page:\n" +pageid);
			
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (Exception e) {
			response.getOutputStream().print("Could not return page:\n" + e.toString());
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
