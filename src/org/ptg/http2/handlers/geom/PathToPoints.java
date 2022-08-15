/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.geom;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.parser.PathParser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.handlers.cnc.CNCPathHandler;
import org.ptg.models.Point;
import org.ptg.models.Shape;
import org.ptg.tests.SVGextractTest.CubicCurve;
import org.ptg.tests.SVGextractTest.IShape;
import org.ptg.tests.SVGextractTest.Line;
import org.ptg.tests.SVGextractTest.QCurve;
import org.ptg.util.CommonUtil;
import org.ptg.util.CommonUtils;
import org.ptg.util.mapper.v2.FPGraph2;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import math.geom2d.Point2D;

public class PathToPoints extends AbstractHandler {
	//todo fix triangulation should triangulate based on points
	ShapeHelper shapeHelper = new ShapeHelper();
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String design = request.getParameter("design");
		String id =  request.getParameter("id");
		double toolsizeinmm = Double.parseDouble(request.getParameter("toolsizeinmm"));
		int pixelsPerUnit= Integer.parseInt(request.getParameter("pixelsPerUnit"));
		int numofpoints= Integer.parseInt(request.getParameter("numOfPoints"));
		String pointDistanceStr = request.getParameter("pointDistance");
		int pointDistance= Integer.parseInt(pointDistanceStr)*pixelsPerUnit;
		float startingDistance   =Float.parseFloat(request.getParameter("startingDistance"))*pixelsPerUnit;
		float endDistance   =Float.parseFloat(request.getParameter("endDistance"))*pixelsPerUnit;
				
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		Shape gg = null;
		for(Entry<String, Shape> shapeEn: g.getShapes().entrySet()) {
			if(shapeEn.getValue().getId().equals(id)) {
				gg = shapeEn.getValue();
				break;
			}
		}
		if(gg!=null) {
		
		List<Point> facetAsPoint = gg.getFacetAsPoint(0);
		if(gg.getData().containsKey("close")&& gg.getData().get("close").equals("true")) {
			facetAsPoint.add(facetAsPoint.get(0));
		}
		String s  = gg.getPathFromPointsArrayEx(facetAsPoint);
		System.out.println("//Path str: "+s);
		PathParser p = new PathParser();
		CNCPathHandler cncPathHandler = shapeHelper.getCncPathHandler(s, p);
		ExtendedGeneralPath path = shapeHelper.getExtendedGeneralPath(cncPathHandler);
		PathLength pathLength = new PathLength(path);
		List<java.awt.geom.Point2D> cords = null;
		if(pointDistanceStr!=null && pointDistanceStr.length()>0) {
			cords = shapeHelper.pathLengthToCoordinatesAtDistance(pointDistance, startingDistance, endDistance,numofpoints, pathLength);
		}else {
			cords = shapeHelper.pathLengthToPoint2d(numofpoints, pathLength);			
		}
		response.getWriter().write(CommonUtil.jsonFromCollection(cords));
		}else{
			System.out.println("could not find shape with id: "+id);		
		}
		
		((Request) request).setHandled(true);
	}

	
	

}

