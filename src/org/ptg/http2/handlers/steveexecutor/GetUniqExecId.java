/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.steveexecutor;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;


public class GetUniqExecId extends AbstractHandler{
    boolean escape = CommonUtil.escapeJavaScriptInSql();

    		public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    			String name = request.getParameter("name");
				String toSave = request.getParameter("tosave");
    			String graphjson = request.getParameter("process");
				String graphconfig = request.getParameter("graphconfig");
				String graphtype = request.getParameter("graphtype");
				String uuid= request.getParameter("uid");
    			try {
    				String ip = request.getRemoteAddr();
    				if(graphconfig==null) graphconfig = "";
    				if(graphtype==null)graphtype = "graph";
    				if(escape){
    				toSave = StringEscapeUtils.escapeJavaScript(toSave);
    				}
    				Map config = CommonUtil.getConfigFromJsonData(graphconfig);
    				String instid = null;
    				if(uuid==null || uuid.length()==0){
    					instid = CommonUtil.createGraphInstance(name);
    				}else{
    					instid = CommonUtil.createGraphInstance(name,uuid);
    				}
    				//String instid = CommonUtil.createGraphInstance(name, toSave, graphconfig, graphtype, ip,instidStr);
    				response.getOutputStream().print(instid);
    			} catch (Exception e) {
    				response.getOutputStream().print("Graph cannot be saved");
    				e.printStackTrace();
    			}
    			
    			((Request) request).setHandled(true);
    		}

			

			private String getUUIDStr(Map config) {
				return CommonUtil.getUUIDStr(config) ;
			}

}
