/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.geom;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.models.Point;
import org.ptg.models.Shape;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.v2.FPGraph2;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import math.geom2d.Point2D;

public class ShapePixalate extends AbstractHandler {
	// todo fix triangulation should triangulate based on points
	ShapeHelper shapeHelper = new ShapeHelper();
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String design = request.getParameter("design");
		double toolsizeinmm = Double.parseDouble(request.getParameter("toolsizeinmm"));
		int pixelsPerUnit = Integer.parseInt(request.getParameter("pixelsPerUnit"));
		int numofpoints = Integer.parseInt(request.getParameter("numofpoints"));
		String id = request.getParameter("id");

		double pixalRadius = -10;
		pixalRadius = toolsizeinmm * pixelsPerUnit;
		GeometryFactory geometryFactory = new GeometryFactory();
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		Shape gg = null;
		List<Point2D> all = Lists.newLinkedList();
		if (id.equals("*")) {
			for (Entry<String, Shape> shapeEn : g.getShapes().entrySet()) {
				gg = shapeEn.getValue();
				List<Point> facetAsPoint = gg.getFacetAsPoint(0);
				facetAsPoint.add(facetAsPoint.get(0));

				// covert to svg path handling
				String shapeStrSVG = gg.getPathFromPointsArrayEx(facetAsPoint);
				System.out.println("//Path str: " + shapeStrSVG);

				List<Coordinate> cords = shapeHelper.toCoordinates(numofpoints, shapeStrSVG);
				List<Point2D> ret = shapeHelper.pixalate(geometryFactory, cords, pixalRadius, pixalRadius);
				all.addAll(ret);
			}
			
		} else {
			for (Entry<String, Shape> shapeEn : g.getShapes().entrySet()) {
				if (shapeEn.getValue().getId().equals(id)) {
					gg = shapeEn.getValue();
					break;
				}
			}

			List<Point> facetAsPoint = gg.getFacetAsPoint(0);
			facetAsPoint.add(facetAsPoint.get(0));

			// covert to svg path handling
			String shapeStrSVG = gg.getPathFromPointsArrayEx(facetAsPoint);
			System.out.println("//Path str: " + shapeStrSVG);

			List<Coordinate> cords = shapeHelper.toCoordinates(numofpoints, shapeStrSVG);
			List<Point2D> ret = shapeHelper.pixalate(geometryFactory, cords, pixalRadius, pixalRadius);
			all.addAll(ret);
		}
	    List<Point> pts = all.stream().map(pt->{
	    	 Point pt2 = new Point();
	    	 pt2.x = (int)pt.getX();
	    	 pt2.y=  (int)pt.getY();
	    	 return pt2;
	     }).collect(Collectors.toList());
		response.getWriter().write(CommonUtil.jsonFromCollection(pts));
		((Request) request).setHandled(true);
	}

	

	



}
