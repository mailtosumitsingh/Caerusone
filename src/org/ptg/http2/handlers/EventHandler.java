/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.events.Event;
import org.ptg.util.CommonUtil;

/**
 * 
 */
public class EventHandler extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String toSave = request.getParameter("tosave");
			String stream = request.getParameter("stream");
			toSave = StringEscapeUtils.unescapeJavaScript(toSave);
			JSONObject obj = JSONObject.fromObject(toSave);
			String id = "";
			if (obj != null && obj.has("id")) {
				id = obj.getString("id");
			} else {
				id = CommonUtil.getRandomString(18);
			}
			Event evt = CommonUtil.getEventFromJsonObject(obj);
			Exchange ex = CommonUtil.sendAndWait(stream, evt);
			response.getOutputStream().print(StringEscapeUtils.escapeJavaScript(ex.getIn().getBody().toString()));

		} catch (Exception e) {
			response.getOutputStream().print("Event cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
