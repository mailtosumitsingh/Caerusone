package org.ptg.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.mapper.v2.FPGraph2;

public abstract class GraphProcessorSupport extends AbstractHandler {
	protected Map<String, Closure<PropInfo<PropInfo>>> closureMap = new HashMap<String, Closure<PropInfo<PropInfo>>>();

	



	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String graphconfig = request.getParameter("graphconfig");
		String procPlan = request.getParameter("processingPlan");
		if (graphconfig == null)
			graphconfig = "{}";
		Map config = CommonUtil.getConfigFromJsonData(graphconfig);
		try {
			processGraph(name, graphjson, procPlan);
			response.getWriter().print("Done");
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	protected void processGraph(String name, String graphjson, String procPlan) {
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		CommonUtil.extractAnonFromOrphans( o);
		createAndRunGraph(name, o, procPlan);

	}

	protected abstract void createAndRunGraph(String name, FPGraph2 o, String procPlan);

	public final class PropInfoTransformer implements Transformer<Map.Entry<String, PropInfo>, String> {
		public String transform(Map.Entry<String, PropInfo> en) {
			String parent = StringUtils.substringBeforeLast(en.getValue().getGroup()+"_"+en.getKey(), ".");
			return parent;
		}
	}
	public final class PropInfoTransformer2 implements Transformer<Map.Entry<String, PropInfo>, String> {
		public String transform(Map.Entry<String, PropInfo> en) {
			String parent = StringUtils.substringBeforeLast(en.getKey(), ".");
			return parent;
		}
	}
	}
