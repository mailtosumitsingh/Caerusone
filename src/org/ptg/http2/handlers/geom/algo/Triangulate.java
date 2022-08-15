/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.geom.algo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.parser.PathParser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.handlers.cnc.CNCPathHandler;
import org.ptg.models.Connector;
import org.ptg.models.Facet;
import org.ptg.models.Point;
import org.ptg.models.Shape;
import org.ptg.tests.SVGextractTest.CubicCurve;
import org.ptg.tests.SVGextractTest.IShape;
import org.ptg.tests.SVGextractTest.Line;
import org.ptg.tests.triangulation.Earcut;
import org.ptg.util.mapper.v2.FPGraph2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;

public class Triangulate extends AbstractHandler {
	//todo fix triangulation should triangulate based on points
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String design = request.getParameter("design");
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		Shape gg = g.getShapes().entrySet().iterator().next().getValue();
		Map<String, Point> pmap = Maps.newHashMap();
		for (Point p : gg.getPoints()) {
			pmap.put(p.getId(), p);
		}
		List<Point> cp = Lists.newLinkedList();
		
		///
		CNCPathHandler result = gg.getSVGParseResult(0);
		for(Coordinate coord: result.getCoordinates()) {
			cp.add(new Point((int)coord.x,(int)coord.y,0));
		}
		
		
		double []dd = new double[cp.size()*2];
		for(int i=0;i<cp.size();i++) {
			dd[i*2]=cp.get(i).getX();
			dd[i*2+1]=cp.get(i).getY();
		}
		List<Integer> triangles = Earcut.earcut(dd, null, 2);
		StringBuilder sb = new StringBuilder();

		for(int i=0;i<(triangles.size()/3);i++) {
			Point p1 = cp.get(triangles.get(i*3));
			Point p2 = cp.get(triangles.get(i*3+1));
			Point p3 = cp.get(triangles.get(i*3+2));
			sb.append("pCanvas.line("+p1.getX()+","+p1.getY()+","+p2.getX()+","+p2.getY()+");");
			sb.append("pCanvas.line("+p2.getX()+","+p2.getY()+","+p3.getX()+","+p3.getY()+");");
			sb.append("pCanvas.line("+p3.getX()+","+p3.getY()+","+p1.getX()+","+p1.getY()+");");

		}
		System.out.println(sb.toString());
		String s  = gg.getPathFromPointsArrayEx(gg.getFacetAsPoint(0));
		System.out.println("Path str: "+s);
		PathParser p = new PathParser();
		CNCPathHandler cncPathHandler = new CNCPathHandler();
		p.setPathHandler(cncPathHandler);
		p.parse(s);
		List<String> pathSplits = Lists.newLinkedList();
		for (IShape shape : cncPathHandler.getShape().getShapes()) {
			if (shape.getType().equalsIgnoreCase("C")) {
				CubicCurve cc = (CubicCurve) shape;
				System.out.println("pCanvas.circle( " + cc.getS().getX() + ", " + cc.getS().getY() + " ,6);");
				System.out.println("var r = pCanvas.circle( " + cc.getC1().getX() + " , " + cc.getC1().getY() + " ,9);r.attr(\"stroke\",\"red\");");
				System.out.println("r = pCanvas.circle( " + cc.getC2().getX() + " , " + cc.getC2().getY() + " ,9);r.attr(\"stroke\",\"red\");");
				System.out.println("pCanvas.circle( " + cc.getE().getX() + " , " + cc.getE().getY() + " ,6 );");
				pathSplits.add(cc.toString());

			}
			if (shape.getType().equalsIgnoreCase("L")) {
				Line lc = (Line) shape;
				System.out.println("pCanvas.circle( " + lc.getS().getX() + ", " + lc.getS().getY() + " ,6);");
				System.out.println("pCanvas.circle( " + lc.getE().getX() + " , " + lc.getE().getY() + " ,6);");
				pathSplits.add(lc.toString());
			}
		}
		System.out.println("Now print int points -----------------------------");
		for(Coordinate coord:cncPathHandler.getCoordinates()) {
			System.out.println("var c = pCanvas.circle( " + coord.x+ ", " + coord.y + " ,3);c.attr(\"stroke\",\"red\");");
			
		}
		response.getOutputStream().println(sb.toString());
		((Request) request).setHandled(true);
	}

}

