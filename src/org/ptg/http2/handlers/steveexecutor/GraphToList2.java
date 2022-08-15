/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.steveexecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.handlers.compilers.graph.CopyOfCompileTaskPlan;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class GraphToList2 extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String useCTP = request.getParameter("useCTP");
		try {
			//String o = CommonUtil.graphToList(name, graphjson);
			FPGraph2 g = new FPGraph2();
			g.fromGraphJson(name, graphjson);
			List<AnonDefObj> toRem = null;
			if (useCTP != null) {/*inhouse algorithm works best*/
				CopyOfCompileTaskPlan tp = new CopyOfCompileTaskPlan();
				toRem = tp.browseGraph(g);
			} else {
				toRem = CommonUtil.topologicalSortAnonTypes(g);
			}
			List<String> toRet2 = new ArrayList<String>();
			for (AnonDefObj obj : toRem) {
				toRet2.add(obj.getId());
			}
			//alternate way of doing via portsbut not using
			//Collection<String> toRet = CommonUtil.topologicalSortPorts(g);
			response.getOutputStream().print(CommonUtil.jsonFromCollection(toRet2));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

}
