/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebSiteConstants;
import org.ptg.admin.WebStartProcess;
import org.ptg.events.Event;
import org.ptg.util.CommonUtil;
import org.ptg.velocity.VelocityHelper;
public class StaticHandler extends AbstractHandler{
		private static Map<String , String> pagemap = new HashMap<String ,String>();
        private static Map<String,Object>TemplateParams = new HashMap<String,Object>();

		static{
						pagemap.put("mainpage", "Hello from $hostname ");
                        TemplateParams.put("hostname",WebSiteConstants.HOST_IP);
                        TemplateParams.put("hostport","8095");
		}
    		public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    			response.setHeader("Access-Control-Allow-Origin", "*");
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                String pageid = request.getParameter("pageid");
                if(pageid!=null){
					String fname = pagemap.get(pageid);
					if(fname!=null){
					StringBuffer responseContent = VelocityHelper.burnStringTemplate(TemplateParams,fname );
					Event	e = (Event) CommonUtil.getEventFromJsonData(arg1.getParameter("tosave"));
					Exchange exh = WebStartProcess.getInstance().getRoutingEngine().getDefaultExchange();
					DefaultMessage msgtemp = new DefaultMessage();
					msgtemp.setBody(e);
					exh.setIn(msgtemp);
					WebStartProcess.getInstance().getRoutingEngine().sendExchange("direct:"+"TestStream", exh);
					 response.getWriter().write(responseContent.toString()+exh.getOut().getBody() );
					}

				}
                response.flushBuffer();
                ((Request)request).setHandled(true);
	    }

}
