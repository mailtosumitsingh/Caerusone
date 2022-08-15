package org.ptg.http2.handlers.cnc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.parser.PathParser;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.cnc.CNCElement;
import org.ptg.cnc.DrillHole;
import org.ptg.cnc.GCodeHelper;
import org.ptg.models.Shape;
import org.ptg.tests.SVGextractTest.CubicCurve;
import org.ptg.tests.SVGextractTest.IShape;
import org.ptg.tests.SVGextractTest.Line;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.mapper.v2.FPGraph2;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GcodeGenerator extends AbstractHandler {
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;
	Map<String, Integer> feedRates = new HashMap<String, Integer>();
	Map<String, Integer> zfeedRates = new HashMap<String, Integer>();
	Map<String, Integer> retractfeedRates = new HashMap<String, Integer>();

	Map<String, Integer> dowellTimes = new HashMap<String, Integer>();
	public static final String HOLE = "drillhole";
	public static final String POST_DRILL_WAIT = "predrill";
	public static final String PREE_DRILL_WAIT = "postdrill";

	{
		feedRates.put("ALUM", 100);
		feedRates.put("ALUM_1/4", 100);
		feedRates.put("ALUM_1/5", 100);
		zfeedRates.put("ALUM", 80);
		zfeedRates.put("ALUM_1/4", 80);
		zfeedRates.put("ALUM_1/5", 80);
		retractfeedRates.put("ALUM", 100);
		retractfeedRates.put("ALUM_1/4", 100);
		retractfeedRates.put("ALUM_1/5", 100);

		dowellTimes.put(HOLE, 2);
		dowellTimes.put(PREE_DRILL_WAIT, 2);
		dowellTimes.put(POST_DRILL_WAIT, 2);

	}

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String design = request.getParameter("design");
		try {
			String str= generateGcode(name, graphjson, design);
			int count = StringUtils.split(str, "\n").length;
			response.getWriter().print("{\"status\":\"success\",\"msg\":\"Compiled successfully: Lines " + count + "\"}");
		} catch (Exception e) {
			response.getWriter().print("{\"status\":\"failure\",\"msg\":\"Could not compile\"}");
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	public String generateGcode(String name, String graphjson, String design) throws Exception {
		FPGraph2 obj = getObjectMapperInst(name, graphjson);
		Map<String, JSONObject> t = obj.getOrphans();
		JSONObject graphConfig = t.get("GraphConfig");
		String mat = graphConfig.getString("config-material");
		String units = graphConfig.getString("config-units");
		String pixelsPerUnitStr = graphConfig.getString("config-pixelsPerUnit");
		BigDecimal modelBottom = new BigDecimal(graphConfig.getString("config-depth"));
		modelBottom = modelBottom.setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal offsetx = new BigDecimal(graphConfig.getString("config-offsetx"));
		BigDecimal offsety = new BigDecimal(graphConfig.getString("config-offsety"));
		int pixelsPerUnit = Integer.parseInt(pixelsPerUnitStr);
		BigDecimal modelheightmm = new BigDecimal(graphConfig.getString("config-modelheightmm"));
		BigDecimal modelheightinch = new BigDecimal(graphConfig.getString("config-modelheightinch"));
		BigDecimal iterstepmm = new BigDecimal(graphConfig.getString("config-iterstepmm"));
		BigDecimal iterstepinch = new BigDecimal(graphConfig.getString("config-iterstepinch"));
		BigDecimal toolSize = new BigDecimal(graphConfig.getString("config-toolsizeinmm"));
		
		BigDecimal modelHeight = BigDecimal.ZERO;
		BigDecimal increment = BigDecimal.ZERO;
		if(units.equals("mm")) {
			modelHeight = modelheightmm;
			increment = iterstepmm;
		}else {
			modelHeight = modelheightinch;
			increment = iterstepinch;
		}
		GCodeHelper gcodeHelper = new GCodeHelper(units,offsetx, offsety,modelheightmm ,modelheightinch ,iterstepmm,iterstepinch,modelBottom,modelHeight,increment,toolSize);
		List<CNCElement> elements = new LinkedList<CNCElement>();
		for (JSONObject j : t.values()) {
			if (j.has("type") && j.getString("type").equals("hole")) {
				Map classMap = new HashMap();
				DrillHole bean = (DrillHole) JSONObject.toBean(j, DrillHole.class, classMap);
				elements.add(bean);
				System.out.println();
			} else if (j.has("type") && j.getString("type").equals("circletap")) {
				Map classMap = new HashMap();
				DrillHole bean = (DrillHole) JSONObject.toBean(j, DrillHole.class, classMap);
				elements.add(bean);
				System.out.println();
			}
		}
		elements = (List<CNCElement>) gcodeHelper.sort(elements, "default");
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int i = 0;
		for (CNCElement e : elements) {
			sb.append("pCanvas.text(" + e.getNormalizedx() + "," + e.getNormalizedy() + "," + "'" + i + "');");
			i++;
			if (e.getType().equals("hole")) {
				double drillheight = e.getDepth() ;
				if(drillheight==0) {
					String strDepth = e.getData().get("depth");
					if(strDepth!=null)
						drillheight = Double.parseDouble(strDepth);
				}
				if(drillheight <= 0 ) {
					drillheight = modelBottom.doubleValue() ;
				}
				String s = gcodeHelper.getDrillGcode(e, mat, pixelsPerUnit, drillheight, dowellTimes.get(PREE_DRILL_WAIT), dowellTimes.get(POST_DRILL_WAIT), feedRates.get(mat));
				sb2.append(s);
			} else if (e.getType().equals("circletap")) {
				String s = gcodeHelper.cutCircle(e, mat, pixelsPerUnit);
				sb2.append(s);
				sb2.append("\n");
			}
		}
		System.out.println(sb.toString());
		System.out.println();
		String shapeGcodes = processShapes(design, mat, pixelsPerUnit, modelBottom, gcodeHelper,modelHeight,increment);
		sb2.append(shapeGcodes);
		// 2>Process modern shape objects
		StringBuilder sbShapeObject = processShapeObjects(obj, mat, pixelsPerUnit, gcodeHelper);
		sb2.append(sbShapeObject);

		String absDrillGcodeFile = gcodeHelper.getAbsDrillGcodeFile(sb2.toString(), units);
		// System.out.println(absDrillGcodeFile);
		absDrillGcodeFile = StringUtils.replace(absDrillGcodeFile, "--", "");
		CncHandler.gcodes.put(name, absDrillGcodeFile);
		return absDrillGcodeFile;
	}

	private String processShapes(String json, String mat, int pixelsPerUnit, BigDecimal height, GCodeHelper gcodeHelper,BigDecimal modelHeight,BigDecimal  increment) {
		StringBuilder gcodes = new StringBuilder();
		json = json.replace("NaN", "\"0\"");
		json = json.replace("undefined", "\"\"");
		json = StringUtils.replace(json, "NaN", "null");
		json = json.replace("Infinity", "\"0\"");
		JSONArray objs = JSONArray.fromObject(json);
		for (int i = 0; i < objs.size(); i++) {
			JSONObject jsonObject = (JSONObject) objs.get(i);
			if (jsonObject.has("name") && !jsonObject.getString("name").equals("Shape")) {
				continue;
			} else {
				String shapeId = jsonObject.getString("id");
				gcodes.append("G04 P1000 ( " + shapeId + ")\n");
				if (jsonObject.has("path") && !jsonObject.has("pts")) {
					/* this is path based drawing probably imported */
					processShapeFromPath(gcodes, jsonObject, shapeId, mat, pixelsPerUnit, height, gcodeHelper,modelHeight,increment);
				} else {
					/* this is point based drawing use points */
					processShapeFromPoints(gcodes, jsonObject, shapeId, mat, pixelsPerUnit, height, gcodeHelper,modelHeight,increment);
				}
			}
		}
		return gcodes.toString();
	}

	//this is oly good one rest we will drop
	
	private StringBuilder processShapeObjects(FPGraph2 g, String mat, int pixelsPerUnit, GCodeHelper gcodeHelper) {
		StringBuilder gcodes = new StringBuilder();
		for (Entry<String, Shape> gg : g.getShapes().entrySet()) {
			Shape sh = gg.getValue();
			if(sh.isVisible()) {
				String iterSize = sh.getData().get("iter");
				if(iterSize==null)iterSize= "1";
				BigDecimal increment = new BigDecimal(iterSize);
				increment.setScale(2, RoundingMode.HALF_EVEN);
				//todo : rinky change o svg parse result
				CNCPathHandler result = sh.getSVGParseResult(0);
				List<Coordinate> cs = result.getCoordinates();
				String shapeDepth = sh.getData().get("depth");
				if(shapeDepth==null) shapeDepth =  "1";
				BigDecimal depth = new BigDecimal(shapeDepth);
				String modelTopHeight = sh.getData().get("modeltop");
				if(modelTopHeight ==null) modelTopHeight="1";
				BigDecimal modeltop = new BigDecimal(modelTopHeight); 
				modeltop = modeltop.setScale(2,RoundingMode.HALF_EVEN);
				if(sh.getData().get("close")!=null &&sh.getData().get("close").equals("true")) {
					cs.add(cs.get(0));
				}
				
				convertPointsToGcodes(gcodes, sh.getId(), cs, mat, pixelsPerUnit, depth, gcodeHelper,modeltop,increment);
				}else {
				System.out.println("Skipping shape: "+sh.getId());
			}
		}
		return gcodes;
	}

	private void processShapeFromPath(StringBuilder gcodes, JSONObject jsonObject, String shapeId, String mat, int pixelsPerUnit, BigDecimal height, GCodeHelper gcodeHelper,BigDecimal modelHeight,BigDecimal  increment) {
		String svgPath = jsonObject.getString("path");
		CNCPathHandler cncPathHandler = new CNCPathHandler();
		if (jsonObject.has("tr")) {
			JSONObject tr = jsonObject.getJSONObject("tr");
			cncPathHandler.setTx(tr.getDouble("x"));
			cncPathHandler.setTy(tr.getDouble("y"));
		}
		PathParser p = new PathParser();
		p.setPathHandler(cncPathHandler);
		p.parse(svgPath);
		List<Coordinate> cs = cncPathHandler.getCoordinates();
		convertPointsToGcodes(gcodes, shapeId, cs, mat, pixelsPerUnit, height, gcodeHelper, modelHeight,  increment);
	}

	public void processShapeFromPoints(StringBuilder gcodes, JSONObject obj, String shapeId, String mat, int pixelsPerUnit, BigDecimal height, GCodeHelper gcodeHelper,BigDecimal modelHeight,BigDecimal  increment) {
		JSONArray o = JSONArray.fromObject(obj.get("pts"));
		try {
			for (int j = 0; j < o.size(); j++) {
				JSONObject jo = (JSONObject) o.get(j);
				List<Coordinate> cs = new LinkedList<Coordinate>();
				Iterator<String> keys = jo.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					if (key.startsWith("uni") || key.startsWith("Random")) {
						JSONObject xy = (JSONObject) jo.get(key);
						double x = xy.getDouble("x");
						double y = xy.getDouble("y");
						Coordinate c = new Coordinate(x, y, 0);
						cs.add(c);

					}
				}
				convertPointsToGcodes(gcodes, shapeId, cs, mat, pixelsPerUnit, height, gcodeHelper,modelHeight, increment);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void convertPointsToGcodes(StringBuilder gcodes, String shapeId, List<Coordinate> cs, String mat, int pixelsPerUnit, BigDecimal height, GCodeHelper gcodeHelper,BigDecimal modelHeight, BigDecimal increment) {
		if (cs.size() == 2) {
			convertPointsToGcodesSlot(gcodes, shapeId, cs, mat, pixelsPerUnit, height, gcodeHelper,modelHeight, increment);
		} else {
			convertPointsToGcodesPath(gcodes, shapeId, cs, mat, pixelsPerUnit, height, gcodeHelper,modelHeight, increment);

		}
	}

	public void convertPointsToGcodesPath(StringBuilder gcodes, String shapeId, List<Coordinate> cs, String mat, int pixelsPerUnit, BigDecimal height, GCodeHelper gcodeHelper,BigDecimal modelHeight, BigDecimal increment) {
		GeometryFactory f = new GeometryFactory();
		Geometry mp = f.createMultiPoint(cs.toArray(new Coordinate[0]));
		Geometry g = mp.convexHull();
		Coordinate[] ch = mp.getCoordinates();

		StringBuilder sb2 = new StringBuilder();
		sb2.append("/*" + shapeId + "*/");
		gcodes.append(gcodeHelper.getMoveToGcode(ch[0], mat, pixelsPerUnit, feedRates.get(mat), 0d));
		BigDecimal currZ = modelHeight;
		currZ = currZ.setScale(2, RoundingMode.HALF_EVEN);
		increment = increment.setScale(2, RoundingMode.HALF_EVEN);
		ArrayList<Coordinate> chf = Lists.newArrayList(ch);
		ArrayList<Coordinate> chr = Lists.newArrayList(ch);
		Collections.reverse(chr);
		Coordinate last = null;
		while (currZ.compareTo(height) <= 0) {
			int k = 0;
			for (Coordinate c : chf) {
				sb2.append("pCanvas.circle(" + (c) + "," + (c) + "," + "'" + 2 + "');");
				if (k == 0) {
					gcodes.append(gcodeHelper.getMoveToGcode(c, mat, pixelsPerUnit, zfeedRates.get(mat), currZ.doubleValue()));
				} else {
					gcodes.append(gcodeHelper.getMoveToGcode(c, mat, pixelsPerUnit, feedRates.get(mat), currZ.doubleValue()));
				}
				k++;
				last = c;
			}
			currZ = currZ.add(increment);
			if(currZ.compareTo(height) <= 0) {
			k = 0;
			for (Coordinate c : chr) {
				sb2.append("pCanvas.circle(" + (c.x) + "," + (c.y) + "," + "'" + 2 + "');");
				if (k == 0) {
					gcodes.append(gcodeHelper.getMoveToGcode(c, mat, pixelsPerUnit, zfeedRates.get(mat), currZ.doubleValue()));
				} else {
					gcodes.append(gcodeHelper.getMoveToGcode(c, mat, pixelsPerUnit, feedRates.get(mat), currZ.doubleValue()));
				}
				k++;
				last = c;
			}
			}
			currZ = currZ.add(increment);

		}
		if(last!=null)
		gcodes.append(gcodeHelper.getMoveToGcode(last, mat, pixelsPerUnit, retractfeedRates.get(mat), 0));

		System.out.println(sb2.toString());
	}

	public void convertPointsToGcodesSlot(StringBuilder gcodes, String shapeId, List<Coordinate> cs, String mat, int pixelsPerUnit, BigDecimal height, GCodeHelper gcodeHelper, BigDecimal modelHeight, BigDecimal increment) {
		GeometryFactory f = new GeometryFactory();
		Geometry mp = f.createMultiPoint(cs.toArray(new Coordinate[0]));
		Geometry g = mp.convexHull();
		Coordinate[] ch = mp.getCoordinates();

		StringBuilder sb2 = new StringBuilder();
		sb2.append("/*" + shapeId + "*/");
		gcodes.append(gcodeHelper.getMoveToGcode(ch[0], mat, pixelsPerUnit, feedRates.get(mat), 0d));
		BigDecimal currZ = modelHeight;
		currZ = currZ.setScale(2, RoundingMode.HALF_EVEN);
		increment = increment.setScale(2, RoundingMode.HALF_EVEN);
		Coordinate last =null;
		while (currZ.compareTo(height) <= 0) {
			gcodes.append(gcodeHelper.getMoveToGcode(ch[0], mat, pixelsPerUnit, zfeedRates.get(mat), currZ.doubleValue()));
			gcodes.append(gcodeHelper.getMoveToGcode(ch[1], mat, pixelsPerUnit, feedRates.get(mat), currZ.doubleValue()));
			last=ch[1];
			currZ = currZ.add(increment);
			if(currZ.compareTo(height) <= 0) {
			gcodes.append(gcodeHelper.getMoveToGcode(ch[1], mat, pixelsPerUnit, zfeedRates.get(mat), currZ.doubleValue()));
			gcodes.append(gcodeHelper.getMoveToGcode(ch[0], mat, pixelsPerUnit, feedRates.get(mat), currZ.doubleValue()));
			currZ = currZ.add(increment);
			last=ch[0];
			}
		}
		if(last!=null)
		gcodes.append(gcodeHelper.getMoveToGcode(last, mat, pixelsPerUnit, retractfeedRates.get(mat), 0));

		System.out.println(sb2.toString());
	}

	public FPGraph2 getObjectMapperInst(String name, String graphjson) throws Exception {
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		return o;
	}
}