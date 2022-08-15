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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.models.Point;
import org.ptg.models.Shape;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.v2.FPGraph2;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferOp;

public class ShapeOffset extends AbstractHandler {
	//todo fix triangulation should triangulate based on points
	ShapeHelper shapeHelper = new ShapeHelper();
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String design = request.getParameter("design");
		double toolsizeinmm = Double.parseDouble(request.getParameter("toolsizeinmm"));
		int pixelsPerUnit= Integer.parseInt(request.getParameter("pixelsPerUnit"));
		String direction = request.getParameter("dir");
		int numofpoints= Integer.parseInt(request.getParameter("numofpoints"));
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		
		double bufferOffset = -10;
		bufferOffset = toolsizeinmm*pixelsPerUnit;
		//can be inside or outside
		if(direction.equals("inside")||direction.equals("cavity"))
			bufferOffset = 0-Math.abs(bufferOffset);
		else
			bufferOffset = Math.abs(bufferOffset);
		StringBuilder all= new StringBuilder();
		for(Shape gg :g.getShapes().values()) {
			if(!gg.isVisible())continue;
		StringBuilder sb = handleShape(bufferOffset, gg,direction,direction.equals("cavity"),numofpoints);
		all.append(sb);
		}
		response.getOutputStream().print(all.toString());
		((Request) request).setHandled(true);
	}

	private StringBuilder handleShape(double bufferOffset, Shape gg, String direction, boolean isCavity,int numofpoints) {
		String grpId=RandomStringUtils.randomAlphabetic(6);
		StringBuilder sb = new StringBuilder();
		GeometryFactory geometryFactory = new GeometryFactory();
		Geometry geom =null;
		do {
		
		List<Point> facetAsPoint = gg.getFacetAsPoint(0);
		String shapeStrSVG  = gg.getPathFromPointsArrayEx(facetAsPoint);
		System.out.println("//Path str: "+shapeStrSVG);
		
		List<Coordinate> cords = shapeHelper.toCoordinates(numofpoints, shapeStrSVG);
		cords.add(cords.get(0));
		
	
		Coordinate[] cordsArray = cords.toArray(new Coordinate[0]);
		LinearRing curveLineString = geometryFactory.createLinearRing(cordsArray);
		Polygon poly = geometryFactory.createPolygon(curveLineString,null);
		geom = poly.buffer(bufferOffset,10,BufferOp.CAP_SQUARE);
		for(Coordinate coor:geom.getCoordinates()) {
			sb.append("var r = pCanvas.circle( " + coor.x+ " , " + coor.y+ " 	,2);r.attr(\"stroke\",\"yellow\");"+"r.attr(\"fill\",\"yellow\");");
		}

		String id = RandomStringUtils.randomAlphabetic(6);
		Shape shapeObj = shapeHelper.toShape(cords, geometryFactory, Maps.newHashMap());
		shapeObj.getData().put("cutdir", direction);
		shapeObj.getData().put("cavity", ""+isCavity);
		shapeObj.getData().put("cavity", ""+isCavity);
		if(isCavity) {
			shapeObj.getData().put("cavitygrpid", grpId);
		}
		shapeObj.setId( id);
		
		sb.append(";var shapeobj="+(CommonUtil.toJson(shapeObj))+";\n");
		sb.append("addObjectToGraph(shapeobj);\n");
		bufferOffset += bufferOffset;
		geom = poly.buffer(bufferOffset,10,BufferOp.CAP_SQUARE);
		}while(isCavity==true&&geom!=null && geom.getCoordinates().length>0);
		return sb;	
	}



}

