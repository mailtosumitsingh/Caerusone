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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.parser.PathParser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.handlers.cnc.CNCPathHandler;
import org.ptg.models.Shape;
import org.ptg.util.mapper.v2.FPGraph2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import math.geom2d.Point2D;

public class ShapeMapper extends AbstractHandler {
	//todo fix triangulation should triangulate based on points
	ShapeHelper shapeHelper = new ShapeHelper();

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String design = request.getParameter("design");
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		Shape gg = g.getShapes().entrySet().iterator().next().getValue();
		
		int inmin=10;
		int inmax=60;
		int outmin=-10;
		int outmax=10;
		int numofpoints = 200;
		
		String s  = gg.getPathFromPointsArrayEx(gg.getFacetAsPoint(0));
		System.out.println("//Path str: "+s);
		PathParser p = new PathParser();
		CNCPathHandler cncPathHandler = shapeHelper.getCncPathHandler(s, p);
		ExtendedGeneralPath path = shapeHelper.getExtendedGeneralPath(cncPathHandler);
	
		PathLength pathLength = new PathLength(path);
		GeometryFactory geometryFactory = new GeometryFactory();
		List<Coordinate> cords = shapeHelper.pathLengthToCoordinates(numofpoints, pathLength);
		Coordinate[] cordsArray = cords.toArray(new Coordinate[0]);
		LineString curveLineString = geometryFactory.createLineString(cordsArray);
		Geometry h = curveLineString.getEnvelope();
		Envelope en = h.getEnvelopeInternal();
		System.out.println("//"+pathLength.lengthOfPath());
		System.out.println("//"+pathLength.getNumberOfSegments());
		int range = inmax-inmin;
		double map = en.getWidth()/range;
		
		int outrange = outmax - outmin;
		double outmap = en.getHeight()/outrange;
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
	
		//map by input so we can get an output based on input
		java.awt.geom.Point2D startPt = pathLength.pointAtLength(0);
		shapeHelper.mapValue(inmin, startPt.getX(), geometryFactory, curveLineString, map, 10	);
		shapeHelper.mapValue(inmin, startPt.getX(), geometryFactory, curveLineString, map, 35);
		shapeHelper.mapValue(inmin, startPt.getX(), geometryFactory, curveLineString, map, 60);
		//test input
		Point2D intersectionPoint = shapeHelper.mapValueOut(outmin, startPt.getY(), geometryFactory, curveLineString, outmap, 0);
		double inmap = range/en.getWidth();
		double inputValueWidth  = intersectionPoint.getX()-startPt.getX();
		double inmappedval = inmap*inputValueWidth+inmin;
		System.out.println("//in: "+0+" out: "+df.format(inmappedval));
		
		//map by output so we can get an input value for a given output
		shapeHelper.mapValueOut(outmin, startPt.getY(), geometryFactory, curveLineString, outmap, -10);
		shapeHelper.mapValueOut(outmin, startPt.getY(), geometryFactory, curveLineString, outmap, 0);
		shapeHelper.mapValueOut(outmin, startPt.getY(), geometryFactory, curveLineString, outmap, 10);
		
		//test outvalue
		Point2D intersectionPoint2 = shapeHelper.mapValue(inmin, startPt.getX(), geometryFactory, curveLineString, map, 35);
		double outInverseMap = outrange/en.getHeight();
		double outValHeight  = startPt.getY()- intersectionPoint2.getY()		;
		double outmappedval = outInverseMap*outValHeight+outmin;
		System.out.println("//in: "+35+" out: "+df.format(outmappedval));		
		
		
		((Request) request).setHandled(true);
	}





	
}

