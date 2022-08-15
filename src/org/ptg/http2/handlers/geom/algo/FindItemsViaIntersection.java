/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.geom.algo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;

import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 */
public class FindItemsViaIntersection extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String json = request.getParameter("process");
		String region = request.getParameter("region");

		json = json.replace("NaN", "\"0\"");
		json = json.replace("undefined", "\"\"");
		System.out.println(json);
		GeometryFactory f = new GeometryFactory();
		List<String> ids = new LinkedList<String>();
		List<LineString> polys = new LinkedList<LineString>();
		List<Coordinate[]> cutPoint = new LinkedList<Coordinate[]>();
		List<String> cuts = new LinkedList<String>();

		
		List<Coordinate> reg = getRegionLineCoordinates(region);
		StringBuilder sb   = new StringBuilder();
		
		final Map<String, Object> elements = CommonUtil.getRawGraphObjectsFromJsonData(name, json);
		for (Map.Entry<String, Object> en : elements.entrySet()) {
			if (en.getValue().getClass().equals(JSONObject.class)) {
				String type = ((JSONObject) en.getValue()).getString("type");
				if (type.equals("ceword")||type.equals("AnonDef")||type.equals("group")||type.equals("stream")||type.equals("node")||type.equals("module")) {
					JSONObject jo = ((JSONObject) en.getValue());
					String xstrVal = jo.getString("x").toString();
					if(xstrVal.endsWith("px"))xstrVal = xstrVal.substring(0,xstrVal.length()-2);
					
					String ystrVal = jo.getString("y").toString();
					if(ystrVal.endsWith("px"))ystrVal = ystrVal.substring(0,ystrVal.length()-2);
					
					String rstrVal = jo.getString("r").toString();
					if(rstrVal.endsWith("px"))rstrVal = rstrVal.substring(0,rstrVal.length()-2);
					
					String bstrVal = jo.getString("b").toString();
					if(bstrVal.endsWith("px"))bstrVal = bstrVal.substring(0,bstrVal.length()-2);

					double x = Double.parseDouble(xstrVal);
					double y = Double.parseDouble(ystrVal);
					double w = Double.parseDouble(rstrVal);
					double h = Double.parseDouble(bstrVal);
					double sx= 0;
					double ex= 0;
					if(type.equals("ceword")){
					sx = x;
					ex = x+w;
					}else if(type.equals("AnonDef")){
						sx = x-w/2;
						ex = x+w/2;
					}else if(type.equals("group")){
						sx = x-w/2;
						ex = x+w/2;
					}else if(type.equals("node")){
						sx = x-w/2;
						ex = x+w/2;
					}else if(type.equals("stream")){
						sx = x-w/2;
						ex = x+w/2;
					}else if(type.equals("module")){
						sx = x-w/2;
						ex = x+w/2;
					}else{
						sx = x;
						ex = x+w;
					}
					Coordinate lt = new Coordinate(sx, y);
					Coordinate rt = new Coordinate(ex, y);
					LineString ls = f.createLineString(new Coordinate[]{lt, rt});
					sb.append("pCanvas.path(\"M");
					sb.append(lt.x);
					sb.append(" , ");
					sb.append(lt.y);
					sb.append(" L ");
					sb.append(rt.x);
					sb.append(" , ");
					sb.append(rt.y);
					sb.append("\");");
					polys.add(ls);
					ids.add(jo.getString("id"));
				}
			}

		}
		
		LineString ls = f.createLineString(reg.toArray(new Coordinate[0]));
		LengthIndexedLine lengthIndex = new LengthIndexedLine(ls);
		RobustLineIntersector intersector = new RobustLineIntersector();
		int j = 0;
		for (LineString poly : polys) {
			try {
				
				Geometry gm = poly.intersection(ls);
				boolean added = false;
				if (gm != null) {
					Coordinate[] ints = gm.getCoordinates();
					if (ints != null && ints.length > 0) {
						cuts.add(ids.get(j));
						cutPoint.add(ints);
						added = true;
					}
				}
				if (!added) {
					cuts.add(ids.get(j));
					cutPoint.add(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			j += 1;
		}
		
		
		Map<Double, String> sortedCuts = new TreeMap<Double, String>();
		int i = 0;
		for (Coordinate[] c : cutPoint) {
			// for(Coordinate cc: c)
			if (c != null) {
				Coordinate cc = c[0];
				{
					double index = lengthIndex.indexOf(cc);
					sortedCuts.put(index, ids.get(i));
				}
			}
			i++;
		}

		response.getOutputStream().print(CommonUtil.jsonFromCollection(sortedCuts.values()));
		((Request) request).setHandled(true);
	}

	public List<Coordinate> getRegionLineCoordinates(String region) {
		JSONArray ja = JSONArray.fromObject(region);
		List<Coordinate> reg = new ArrayList<Coordinate>();
		for (int i = 0; i < ja.size() ; i++) {
			JSONObject j = (JSONObject) ja.get(i);
			double x = j.getDouble("x");
			double y = j.getDouble("y");
			Coordinate lt = new Coordinate(x, y);
			reg.add(lt);

		}
		JSONObject jo = (JSONObject) ja.get(0);
		double x = jo.getDouble("x");
		double y = jo.getDouble("y");
		Coordinate lt = new Coordinate(x, y);
		reg.add(lt);
		return reg;
	}

}
