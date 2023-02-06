/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.tagging;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.opencv.core.Scalar;
import org.ptg.tests.opencv.dro.simpledro.DynaModelSupport;
import org.ptg.tests.opencv.dro.simpledro.IDROReader;
import org.ptg.tests.opencv.dro.simpledro.SimpleDigit;
import org.ptg.tests.opencv.dro.simpledro.SimpleModel;
import org.ptg.tests.opencv.dro.simpledro.SimpleSegment;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.mapper.v2.FPGraph2;
import org.ptg.velocity.VelocityHelper;

import com.google.common.collect.Maps;

import net.sf.json.JSONObject;

public class CompileTagModel extends AbstractHandler {
	
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;

	DynaModelSupport support;
	
	public CompileTagModel(DynaModelSupport support) {
	this.support = support;	
	}
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String run = request.getParameter("run");
		String mappingtype = request.getParameter("mappingtype");
		String ineventtype = request.getParameter("eventtype");
		Map<String, String> params = new HashMap<String, String>();
		params.put("eventtype", ineventtype);
		if (mappingtype == null)
			mappingtype = "FreeSpring";
		try {
			FPGraph2 g = new FPGraph2();
			g.fromGraphJson(name, graphjson);
			Map<String, JSONObject> map = g.getOrphans();
			Map<String, SimpleDigit> digits = Maps.newTreeMap();
			// pass1 digits
			for (Map.Entry<String, JSONObject> jo : map.entrySet()) {
				if (jo.getValue().containsKey("type") && !jo.getValue().getString("type").equalsIgnoreCase("VisTagNode"))
					continue;
				String id = jo.getValue().getString("id");
				String tagType = jo.getValue().getString("tagtype");
				String group = jo.getValue().getString("group");
				String label = jo.getValue().getString("label");
				String xx = jo.getValue().getString("x");
				String yy = jo.getValue().getString("y");
				String rr = jo.getValue().getString("r");
				String bb = jo.getValue().getString("b");
				int x = (int) Double.parseDouble(xx);
				int y = (int) Double.parseDouble(yy);
				int r = (int) Double.parseDouble(rr);
				int b = (int) Double.parseDouble(bb);
				if (group.equalsIgnoreCase("self")) {
					SimpleDigit sd = new SimpleDigit(x, y, r, b, label);
					digits.put(label, sd);
				}
			}
			// pass2 segments
			for (Map.Entry<String, JSONObject> jo : map.entrySet()) {
				if (jo.getValue().containsKey("type") && !jo.getValue().getString("type").equalsIgnoreCase("VisTagNode"))
					continue;
				String id = jo.getValue().getString("id");
				String tagType = jo.getValue().getString("tagtype");
				String group = jo.getValue().getString("group");
				String label = jo.getValue().getString("label");
				String xx = jo.getValue().getString("x");
				String yy = jo.getValue().getString("y");
				String rr = jo.getValue().getString("r");
				String bb = jo.getValue().getString("b");
				int x = (int) Double.parseDouble(xx);
				int y = (int) Double.parseDouble(yy);
				int r = (int) Double.parseDouble(rr);
				int b = (int) Double.parseDouble(bb);
				int labelInt = (int) Double.parseDouble(label);
				if (!group.equalsIgnoreCase("self")&& !group.equalsIgnoreCase("onclr") &&!group.equalsIgnoreCase("offclr")) {
					SimpleDigit sd = digits.get(group);
					SimpleSegment sm = new SimpleSegment(x - sd.getX(), y - sd.getY(), r, b, label);
					sd.getSegments().set(labelInt, sm);
				}
				System.out.println(jo.getKey());
			}
			SimpleModel model = getModel(digits);
			this.support.setDynaModel(model);
			Map<String, Object> data = Maps.newHashMap();
			data.put("digits", digits.values());
			StringBuffer sb = VelocityHelper.burnTemplate(data, "digitmodel.vm");
			System.out.println(sb.toString());
			String code = CommonUtil.getMappingGraphCode(name, sb.toString(), "droreader", null);
			response.getWriter().print(code);
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
		
	}
	
	private  SimpleModel getModel(Map<String, SimpleDigit> digits) {
		SimpleModel model = new SimpleModel();
		int xoff = 5, yoff = 153;
		model.setX(398 + xoff);
		model.setY(173 + yoff);
		model.setW(413);
		model.setH(51);
		model.setDisplayAngle(0);
		model.setDigitWidth(43);
		model.setDigitHeight(56);
		model.setDisplayColor(new Scalar(251, 255, 249, 1));
		model.setColorMatchTolerance(new Scalar(50, 50, 30, 1));
		model.setModelSegmentClusterTolerance(4);
		for(SimpleDigit digit: digits.values()) {
		model.getDigits().add(digit);
		}
		return model;
	}


}