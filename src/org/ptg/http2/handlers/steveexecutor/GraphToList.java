/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.steveexecutor;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;


public class GraphToList extends AbstractHandler{

    		public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    			String name = request.getParameter("name");
    			String graphjson = request.getParameter("process");
    			try {
    				String o = CommonUtil.graphToList(name, graphjson);
    				String [] arr = StringUtils.split(o,";");
    				response.getWriter().print(CommonUtil.jsonFromArray(arr));
    			} catch (Exception e) {
    				response.getOutputStream().print("Could not compile:\n" + e);
    				e.printStackTrace();
    			}

    			((Request) request).setHandled(true);
    		}

}
