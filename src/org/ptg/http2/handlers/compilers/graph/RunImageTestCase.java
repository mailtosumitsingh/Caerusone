/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.compilers.graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.ptg.eventloop.AutomationHelper;
import org.ptg.models.ext.OpenCVModel;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.CommonUtils;
import org.ptg.util.HTTPClientUtil;
import org.ptg.util.awt.BBox;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;

import net.sf.json.JSONObject;

/*this class applies loopback automatically*/
public class RunImageTestCase extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String url = "http://localhost:8080/process";
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String targetImage = request.getParameter("targetImage");
		String instidStr = getUUIDStr(null);
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		String tagTrainImage = getTrainImage(o);
		tagTrainImage = StringUtils.replace(tagTrainImage, "\\","/");
		tagTrainImage = StringUtils.replace(tagTrainImage, "//","/");
		
		if(tagTrainImage==null) {
		System.out.println("Invalid graphd does not have an image tag.");
		}else {
		OpenCVModel model = new OpenCVModel();
		model.getConnections().addAll(o.getForward().values());
		model.getAnonDefs().addAll(o.getAnonDefs());
		model.getShapes().putAll(o.getShapes());
		model.setName(name);
		model.setTagTrainImage(tagTrainImage);
		model.setTargetImage(targetImage);
		try {
			String json  = CommonUtil.toJson(model);
			byte[] bytes = HTTPClientUtil.doPostWithBody(json, url);
			String s = new String(bytes);
			System.out.println(s);
			response.getWriter().print(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		((Request) request).setHandled(true);
	}
	private String getTrainImage(FPGraph2 o) {
		System.out.println("GEt image tag then get image ");
		for(JSONObject a: o.getOrphans().values()) {
			if(a.get("type").equals("ImageToTag")) {
				String path = a.getString("path");
				return  CommonUtil.base+path;
			}
		}
		return null;
	}
	private String getUUIDStr(Map config) {
		return CommonUtil.getUUIDStr(config);
	}

}
