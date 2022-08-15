package org.ptg.http2.handlers.geom;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.parser.PathParser;
import org.apache.commons.lang.RandomStringUtils;
import org.ptg.http2.handlers.cnc.CNCPathHandler;
import org.ptg.models.Connector;
import org.ptg.models.Facet;
import org.ptg.models.Point;
import org.ptg.models.Shape;
import org.ptg.models.Point.PointType;
import org.ptg.tests.SVGextractTest.CubicCurve;
import org.ptg.tests.SVGextractTest.IShape;
import org.ptg.tests.SVGextractTest.Line;
import org.ptg.tests.SVGextractTest.QCurve;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.algorithm.SimplePointInAreaLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import math.geom2d.Point2D;

public class ShapeHelper {
	
	
	public CNCPathHandler getCncPathHandler(String s, PathParser p) {
		CNCPathHandler cncPathHandler = new CNCPathHandler();
		p.setPathHandler(cncPathHandler);
		p.parse(s);
		return cncPathHandler;
	}



	public Point2D getIntersection(LineString ls, LineString ls2) {
		Coordinate c = ls2.intersection(ls).getCoordinates()[0];
		return new Point2D((float)c.x,(float)c.y);
	}
	

	
	public List<Coordinate> pathLengthToCoordinates(int numofpoints, PathLength pathLength) {
		List<Coordinate> cords = Lists.newLinkedList();
		for(int i=0;i<numofpoints;i++) {
			java.awt.geom.Point2D pt = pathLength.pointAtLength((pathLength.lengthOfPath()/numofpoints)*i);
			if(pt!=null) {
			System.out.println("var r = pCanvas.circle( " + pt.getX()+ " , " + pt.getY()+ " ,10);r.attr(\"stroke\",\"green\");");
			cords.add(new Coordinate(pt.getX(),pt.getY()));
			}	
		}
		return cords;
	}
	

	public List<java.awt.geom.Point2D> pathLengthToPoint2d(int numofpoints, PathLength pathLength) {
		List<java.awt.geom.Point2D> cords = Lists.newLinkedList();
		for(int i=0;i<numofpoints;i++) {
			java.awt.geom.Point2D pt = pathLength.pointAtLength((pathLength.lengthOfPath()/numofpoints)*i);
			if(pt!=null) {
			System.out.println("var r = pCanvas.circle( " + pt.getX()+ " , " + pt.getY()+ " ,10);r.attr(\"stroke\",\"green\");");
			cords.add(pt);
			}	
		}
		return cords;
	}
	public List<java.awt.geom.Point2D> pathLengthToCoordinatesAtDistance(float distance,float startingDistance, float endDistance,int numOfPoints, PathLength pathLength) {
		List<java.awt.geom.Point2D> cords = Lists.newLinkedList();
		float dist = startingDistance;
		float totalLength = pathLength.lengthOfPath();
		if(endDistance>0)
			totalLength=endDistance;
		int i = 0;
		while(dist<=totalLength) {
			java.awt.geom.Point2D pt = pathLength.pointAtLength(dist);
			if(pt!=null) {
			System.out.println("var r = pCanvas.circle( " + pt.getX()+ " , " + pt.getY()+ " ,10);r.attr(\"stroke\",\"green\");");
			cords.add(pt);
			}	
			i++;
			dist = dist + distance;
			if(numOfPoints>0 && i>=numOfPoints)
				break;
		}
		return cords;
	}


	private List<Coordinate> pointsToCoordinates(List<Point> points) {
		List<Coordinate> coordinates = points.stream().map(new Function<Point, Coordinate>() {
			@Override
			public Coordinate apply(Point t) {
				Coordinate c = new Coordinate();
				c.x = t.getX();
				c.y=t.getY();
				c.z = t.getZ();
				return c;
			}
		}).collect(Collectors.toList());
		return coordinates;
	}
	
	


	public ExtendedGeneralPath getExtendedGeneralPath(CNCPathHandler cncPathHandler) {
		ExtendedGeneralPath path = new ExtendedGeneralPath();
		for (IShape shape : cncPathHandler.getShape().getShapes()) {
			if (shape.getType().equalsIgnoreCase("C")) {
				CubicCurve cc = (CubicCurve) shape;
				path.moveTo((float)cc.getS().getX(),(float)cc.getS().getY());
				path.curveTo((float)cc.getC1().getX(),(float)cc.getC1().getY(),
						(float)cc.getC2().getX(),(float)cc.getC2().getY(),
						(float)cc.getE().getX(),(float)cc.getE().getY());
			}
			if (shape.getType().equalsIgnoreCase("Q")) {
				QCurve cc = (QCurve) shape;
				path.moveTo((float)cc.getS().getX(),(float)cc.getS().getY());
				path.quadTo((float)cc.getC1().getX(),(float)cc.getC1().getY(),
						(float)cc.getE().getX(),(float)cc.getE().getY());
			}
			if (shape.getType().equalsIgnoreCase("L")) {
				Line cc = (Line) shape;
				path.moveTo((float)cc.getS().getX(),(float)cc.getS().getY());
				path.lineTo((float)cc.getE().getX(),(float)cc.getE().getY());
			}
		}
		return path;
	}

	public Point2D mapValue(int inmin, double startVal, GeometryFactory factory, LineString curveLineString, double map, int val) {
		double mappedXValue = map*(val-inmin)+ startVal;
		LineString verticalLineString = factory.createLineString(new Coordinate[] {new Coordinate(mappedXValue,3000),new Coordinate(mappedXValue,0)});
		Point2D intersectionPoint = getIntersection(curveLineString,verticalLineString);
		System.out.println("var r = pCanvas.circle( " + intersectionPoint.getX()+ " , " + intersectionPoint.getY()+ " 	,5);r.attr(\"stroke\",\"yellow\");"+"r.attr(\"fill\",\"yellow\");");
		return intersectionPoint;
	}
	public Point2D mapValueOut(int inmin, double startVal, GeometryFactory factory, LineString curveLineString, double map, int val) {
		double mappedYValue =  startVal - map*(val-inmin);
		LineString verticalLineString = factory.createLineString(new Coordinate[] {new Coordinate(3000,mappedYValue),new Coordinate(0,mappedYValue)});
		Point2D intersectionPoint = getIntersection(curveLineString,verticalLineString);
		System.out.println("var r = pCanvas.circle( " + intersectionPoint.getX()+ " , " + intersectionPoint.getY()+ " 	,5);r.attr(\"stroke\",\"purple\");"+"r.attr(\"fill\",\"purple\");");
		return intersectionPoint;
	}

	public Shape toShape(List<Coordinate> cords,GeometryFactory geometryFactory, Map<String, Object>data) {
		Shape shapeObj = new Shape();
		if(data!=null) {
			for(Map.Entry<String, Object> en : data.entrySet())
			shapeObj.getData().put(en.getKey(),"" + en.getValue());
		}
		String id = RandomStringUtils.randomAlphabetic(6);
		shapeObj.setId( id);
		int index = 0;
		List<String> pids = Lists.newLinkedList();
		for(Coordinate coor:cords) {
			String pid = id+"_"+(index++);
			shapeObj.getPts().add(new Point((int)coor.x,(int)coor.y,pid));
			pids.add(pid);
		}
		Facet e = new Facet();
		e.setId(RandomStringUtils.randomAlphabetic(6));
		Connector cc = new Connector();
		cc.setId(RandomStringUtils.randomAlphabetic(6));
		cc.setPts(pids);
		e.addConnector(cc);
		e.setFill("90-#00c6ff-#0072ff");
		e.setOpacity(".7");
		e.setStroke("black");
		e.setType("ShapeFacet");
		cc.setFill("90-#00c6ff-#0072ff");
		cc.setOpacity(".7");
		cc.setStroke("black");
		cc.setType("ShapeConnector");
		shapeObj.setVisible(true);
		shapeObj.getFacets().add(e);
		shapeObj.setCompName("arbit");
		shapeObj.setShapeType("simple");
		shapeObj.setType("ShapeShape");
		return shapeObj;
	}
	public Shape toShape(List<Point> cords,Map<String, Object>data) {
		Shape shapeObj = new Shape();
		if(data!=null) {
			for(Map.Entry<String, Object> en : data.entrySet())
			shapeObj.getData().put(en.getKey(),"" + en.getValue());
		}
		String id = RandomStringUtils.randomAlphabetic(3);
		shapeObj.setId( id);
		int index = 0;
		List<String> pids = Lists.newLinkedList();
		for(Point coor:cords) {
			String pid = id+"_"+(index++);
			Point pt = new Point((int)coor.x,(int)coor.y,pid);
			pt.setPointType(coor.getPointType());
			pt.setData(coor.getData());
			pt.setTags(coor.getTags());
			pt.setType(coor.getType());
			shapeObj.getPts().add(pt);
			
			pids.add(pid);
		}
		Facet e = new Facet();
		e.setId(RandomStringUtils.randomAlphabetic(6));
		Connector cc = new Connector();
		cc.setId(RandomStringUtils.randomAlphabetic(6));
		cc.setPts(pids);
		e.addConnector(cc);
		e.setFill("90-#00c6ff-#0072ff");
		e.setOpacity(".7");
		e.setStroke("black");
		e.setType("ShapeFacet");
		cc.setFill("90-#00c6ff-#0072ff");
		cc.setOpacity(".7");
		cc.setStroke("black");
		cc.setType("ShapeConnector");
		shapeObj.setVisible(true);
		shapeObj.getFacets().add(e);
		shapeObj.setCompName("arbit");
		shapeObj.setShapeType("simple");
		shapeObj.setType("ShapeShape");
		return shapeObj;
	}
	public List<Point2D> pixalate(GeometryFactory geometryFactory, List<Coordinate> cords, double pixalW, double pixalH) {
		List<Point2D> ret = Lists.newLinkedList();
		Coordinate[] cordsArray = cords.toArray(new Coordinate[0]);
		LinearRing curveLineString = geometryFactory.createLinearRing(cordsArray);
		Polygon polygon = geometryFactory.createPolygon(curveLineString, null);
		Geometry envelopGeom = curveLineString.getEnvelope();
		SimplePointInAreaLocator ptLocator = new SimplePointInAreaLocator();

		Envelope envelop = envelopGeom.getEnvelopeInternal();
		double x = envelop.getMinX();
		double y = envelop.getMinY();
		double h = envelop.getHeight();
		double w = envelop.getWidth();
		for (double startX = x; startX <= x + w; startX = startX + pixalW) {
			for (double startY = y; startY <= y + h; startY = startY + pixalH) {
				Coordinate pt = new Coordinate(startX, startY);
				Coordinate tl = new Coordinate(pt.x - pixalW / 2, pt.y - pixalH / 2);
				Coordinate tr = new Coordinate(pt.x + pixalW / 2, pt.y - pixalH / 2);
				Coordinate bl = new Coordinate(pt.x - pixalW / 2, pt.y + pixalH / 2);
				Coordinate br = new Coordinate(pt.x + pixalW / 2, pt.y + pixalH / 2);

				Geometry mpg = geometryFactory.createMultiPoint(new Coordinate[] { tl, tr, bl, br });
				if (polygon.contains(mpg)) {
					System.out.println("var r = pCanvas.circle( " + pt.x + " , " + pt.y + " ,3);r.attr(\"stroke\",\"green\");r.attr(\"fill\",\"green\");");
					Point2D point = new Point2D(pt.x, pt.y);
					ret.add(point);
				} else {
					System.out.println("var r = pCanvas.circle( " + pt.x + " , " + pt.y + " ,3);r.attr(\"stroke\",\"orange\");r.attr(\"fill\",\"red\");");

				}
			}
		}
		return ret;

	}
	
	public List<Coordinate> toCoordinates(int numofpoints, String s) {
		PathParser pathParser = new PathParser();
		CNCPathHandler cncPathHandler = getCncPathHandler(s, pathParser);
		IShape last = null;
		List<Coordinate> cordsAll = Lists.newLinkedList();
		IShape first = cncPathHandler.getShape().getShapes().get(0);
		if (first.getType().equalsIgnoreCase("C")) {
			CubicCurve cc = (CubicCurve) first;
			Coordinate a = new Coordinate();
			a.x = cc.getS().getX();
			a.y = cc.getS().getY();
			cordsAll.add(a);
		}
		if (first.getType().equalsIgnoreCase("Q")) {
			QCurve cc = (QCurve) first;
			Coordinate a = new Coordinate();
			a.x = cc.getS().getX();
			a.y = cc.getS().getY();
			cordsAll.add(a);

		}
		if (first.getType().equalsIgnoreCase("L")) {
			Line cc = (Line) first;
			Coordinate a = new Coordinate();
			a.x = cc.getS().getX();
			a.y = cc.getS().getY();
			cordsAll.add(a);

		}
		for (IShape shape : cncPathHandler.getShape().getShapes()) {
			if (shape.getType().equalsIgnoreCase("C")) {
				CubicCurve cc = (CubicCurve) shape;
				ExtendedGeneralPath path = new ExtendedGeneralPath();
				path.moveTo((float)cc.getS().getX(),(float)cc.getS().getY());
				path.curveTo((float)cc.getC1().getX(),(float)cc.getC1().getY(),
						(float)cc.getC2().getX(),(float)cc.getC2().getY(),
						(float)cc.getE().getX(),(float)cc.getE().getY());
				last = shape;
				PathLength pathLength = new PathLength(path);
				List<Coordinate> cords = pathLengthToCoordinates(numofpoints, pathLength);
				cordsAll.addAll(cords);
				Coordinate b = new Coordinate();
				b.x = cc.getE().getX();
				b.y = cc.getE().getY();
				cordsAll.add(b);
				
			}
			if (shape.getType().equalsIgnoreCase("Q")) {
				QCurve cc = (QCurve) shape;
				ExtendedGeneralPath path = new ExtendedGeneralPath();
				path.moveTo((float)cc.getS().getX(),(float)cc.getS().getY());
				path.quadTo((float)cc.getC1().getX(),(float)cc.getC1().getY(),
						(float)cc.getE().getX(),(float)cc.getE().getY());
				last =shape ;
				PathLength pathLength = new PathLength(path);
				List<Coordinate> cords = pathLengthToCoordinates(numofpoints, pathLength);
				cordsAll.addAll(cords);
				Coordinate b = new Coordinate();
				b.x = cc.getE().getX();
				b.y = cc.getE().getY();
				cordsAll.add(b);
			}
			if (shape.getType().equalsIgnoreCase("L")) {
				Line cc = (Line) shape;
				last = shape;
				Coordinate b = new Coordinate();
				b.x = cc.getE().getX();
				b.y = cc.getE().getY();
				cordsAll.add(b);
			}
		}
		if (first.getType().equalsIgnoreCase("C")) {
			CubicCurve cc = (CubicCurve) first;
			Coordinate a = new Coordinate();
			a.x = cc.getS().getX();
			a.y = cc.getS().getY();
			cordsAll.add(a);
		}
		if (first.getType().equalsIgnoreCase("Q")) {
			QCurve cc = (QCurve) first;
			Coordinate a = new Coordinate();
			a.x = cc.getS().getX();
			a.y = cc.getS().getY();
			cordsAll.add(a);
		}
		if (first.getType().equalsIgnoreCase("L")) {
			Line cc = (Line) first;
			Coordinate a = new Coordinate();
			a.x = cc.getS().getX();
			a.y = cc.getS().getY();
			cordsAll.add(a);

		}
		
		return cordsAll;
	}
	public List<Point> toShapePoint( String s) {
		PathParser pathParser = new PathParser();
		CNCPathHandler cncPathHandler = getCncPathHandler(s, pathParser);
		IShape last = null;
		List<Point> cordsAll = Lists.newLinkedList();
		if(cncPathHandler.getShape().getShapes().size()>0) {
		IShape first = cncPathHandler.getShape().getShapes().get(0);
		if (first.getType().equalsIgnoreCase("C")) {
			CubicCurve cc = (CubicCurve) first;
			Point a = new Point();
			a.x = (int) cc.getS().getX();
			a.y = (int) cc.getS().getY();
			cordsAll.add(a);
		}
		if (first.getType().equalsIgnoreCase("Q")) {
			QCurve cc = (QCurve) first;
			Point a = new Point();
			a.x = (int) cc.getS().getX();
			a.y = (int) cc.getS().getY();
			cordsAll.add(a);

		}
		if (first.getType().equalsIgnoreCase("L")) {
			Line cc = (Line) first;
			Point a = new Point();
			a.x = (int) cc.getS().getX();
			a.y = (int) cc.getS().getY();
			cordsAll.add(a);

		}
		for (IShape shape : cncPathHandler.getShape().getShapes()) {
			if (shape.getType().equalsIgnoreCase("C")) {
				CubicCurve cc = (CubicCurve) shape;
				last = shape;
				Point a = new Point();
				a.x = (int) cc.getS().getX();
				a.y = (int) cc.getS().getY();
				Point c1 = new Point();
				c1.x = (int) cc.getC1().getX();
				c1.y = (int) cc.getC1().getY();
				c1.setPointType(PointType.Control);
				Point c2 = new Point();
				c2.x = (int) cc.getC2().getX();
				c2.y = (int) cc.getC2().getY();
				c2.setPointType(PointType.Control);
				Point e = new Point();
				e.x = (int) cc.getE().getX();
				e.y = (int) cc.getE().getY();
				cordsAll.add(a);
				cordsAll.add(c1);
				cordsAll.add(c2);
				cordsAll.add(e);
				
			}
			if (shape.getType().equalsIgnoreCase("Q")) {
				QCurve cc = (QCurve) shape;
				last = shape;
				Point a = new Point();
				a.x = (int) cc.getS().getX();
				a.y = (int) cc.getS().getY();
				Point c1 = new Point();
				c1.x = (int) cc.getC1().getX();
				c1.y = (int) cc.getC1().getY();
				c1.setPointType(PointType.Control);
				Point e = new Point();
				e.x = (int) cc.getE().getX();
				e.y = (int) cc.getE().getY();
				cordsAll.add(a);
				cordsAll.add(c1);
				cordsAll.add(e);
			}
			if (shape.getType().equalsIgnoreCase("L")) {
				Line cc = (Line) shape;
				last = shape;
				Point a = new Point();
				a.x = (int) cc.getS().getX();
				a.y = (int) cc.getS().getY();
				cordsAll.add(a);
			}
		}
		if (first.getType().equalsIgnoreCase("C")) {
			CubicCurve cc = (CubicCurve) first;
			Point a = new Point();
			a.x = (int) cc.getS().getX();
			a.y = (int) cc.getS().getY();
			cordsAll.add(a);
		}
		if (first.getType().equalsIgnoreCase("Q")) {
			QCurve cc = (QCurve) first;
			Point a = new Point();
			a.x = (int) cc.getS().getX();
			a.y = (int) cc.getS().getY();
			cordsAll.add(a);
		}
		if (first.getType().equalsIgnoreCase("L")) {
			Line cc = (Line) first;
			Point a = new Point();
			a.x = (int) cc.getS().getX();
			a.y = (int) cc.getS().getY();
			cordsAll.add(a);
		}
		}
		return cordsAll;
	}
}
