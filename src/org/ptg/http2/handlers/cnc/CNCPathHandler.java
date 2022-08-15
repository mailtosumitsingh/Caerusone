package org.ptg.http2.handlers.cnc;

import java.util.LinkedList;
import java.util.List;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.ptg.tests.SVGextractTest.CubicCurve;
import org.ptg.tests.SVGextractTest.Line;
import org.ptg.tests.SVGextractTest.QCurve;
import org.ptg.tests.SVGextractTest.ShapeEx;

import com.vividsolutions.jts.geom.Coordinate;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.spline.CubicBezierCurve2D;
import math.geom2d.spline.QuadBezierCurve2D;

public class CNCPathHandler implements PathHandler {
    float sx ,sy;
    float cx ,cy;
    int i = 0;
    ShapeEx shape = new ShapeEx();
    List<Coordinate> coordinates = new LinkedList<Coordinate>();
	private double tx;
	private double ty;
	private int pathPointCount = 20;
	
	
	public int getPathPointCount() {
		return pathPointCount;
	}

	public void setPathPointCount(int pathPointCount) {
		this.pathPointCount = pathPointCount;
	}

	@Override
	public void arcAbs(float arg0, float arg1, float arg2, boolean arg3, boolean arg4, float arg5, float arg6) throws ParseException {
		//System.out.println("//1CNCPathHandler.arcAbs()");
	}

	@Override
	public void arcRel(float arg0, float arg1, float arg2, boolean arg3, boolean arg4, float arg5, float arg6) throws ParseException {
		//System.out.println("//1CNCPathHandler.arcRel()");

	}

	@Override
	public void closePath() throws ParseException {
		//System.out.println("//1CNCPathHandler.closePath()");

	}

	@Override
	public void curvetoCubicAbs(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) throws ParseException {
		arg0 +=tx;
		arg1 +=ty;
		arg2 +=tx;
		arg3 +=ty;
		arg4 +=tx;
		arg5 +=ty;

		//System.out.println("//1CNCPathHandler.curvetoCubicAbs()");
		//System.out.println("var r= pCanvas.circle("+(cx)+","+(cy)+","+5+");r.attr(\"stroke\",\"blue\");");
		//System.out.println("r = pCanvas.circle("+(arg0)+","+(arg1)+","+6+");r.attr(\"stroke\",\"red\");");
		//System.out.println("r= pCanvas.circle("+(arg2)+","+(arg3)+","+7+");r.attr(\"stroke\",\"green\");");
		//System.out.println("pCanvas.circle("+(arg4)+","+(arg5)+","+4+");r.attr(\"stroke\",\"orange\");");
		//System.out.println("pCanvas.text("+(arg4)+","+(arg5)+",\""+i+"(ABS)\");r.attr(\"stroke\",\"orange\");");
		CubicBezierCurve2D cc = new CubicBezierCurve2D(cx,cy,arg0,arg1,arg2,arg3,arg4,arg5);
		CubicCurve sh = new CubicCurve(new org.ptg.tests.SVGextractTest.Point2D(cx,cy),new org.ptg.tests.SVGextractTest.Point2D(arg0,arg1),
				new org.ptg.tests.SVGextractTest.Point2D(arg2,arg3),
				new org.ptg.tests.SVGextractTest.Point2D(arg4,arg5));
		
		shape.addShape(sh);
		AffineTransform2D dd = null;
		Polyline2D a = cc.asPolyline(pathPointCount);
		
		//int i = 0;
		for(Point2D p: a.vertexArray()){
			//1//System.out.println("pCanvas.text("+(p.getX())+","+(p.getY())+",\""+i+"(ABS"+i+")\");r.attr(\"stroke\",\"orange\");");
			//System.out.println("r= pCanvas.circle("+(p.getX())+","+(p.getY())+",\""+2+"\");r.attr(\"stroke\",\"orange\");");
			Coordinate c = new Coordinate();
			c.x= p.getX();
			c.y = p.getY();
			coordinates.add(c);
			i++;
		}
		cx=arg4;
		cy=arg5;

		i++;
	}

	@Override
	public void curvetoCubicRel(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) throws ParseException {
		//System.out.println("//1CNCPathHandler.curvetoCubicRel()");
		//System.out.println("var r= pCanvas.circle("+(cx)+","+(cy)+","+9+");r.attr(\"stroke\",\"blue\");");
		//System.out.println("r = pCanvas.circle("+(cx+arg0)+","+(cy+arg1)+","+6+");r.attr(\"stroke\",\"red\");");
		//System.out.println("r= pCanvas.circle("+(cx+arg2)+","+(cy+arg3)+","+9+");r.attr(\"stroke\",\"green\");");
		//System.out.println("r=pCanvas.circle("+(cx+arg4)+","+(cy+arg5)+","+6+");r.attr(\"stroke\",\"orange\");");
		//System.out.println("pCanvas.text("+(cx+arg4)+","+(cy+arg5)+",\""+i+" (REL)\");r.attr(\"stroke\",\"orange\");");
		CubicBezierCurve2D cc = new CubicBezierCurve2D(cx,cy,cx+arg0,cy+arg1,cx+arg2,cy+arg3,cx+arg4,cy+arg5);
		CubicCurve sh = new CubicCurve(new org.ptg.tests.SVGextractTest.Point2D(cx,cy),
				new org.ptg.tests.SVGextractTest.Point2D(cx+arg0,cy+arg1),
				new org.ptg.tests.SVGextractTest.Point2D(cx+arg2,cy+arg3),
				new org.ptg.tests.SVGextractTest.Point2D(cx+arg4,cy+arg5));
		shape.addShape(sh);
		Polyline2D a = cc.asPolyline(pathPointCount);
		int i = 0;
		for(Point2D p: a.vertexArray()){
			//System.out.println("r=pCanvas.circle("+(p.getX())+","+(p.getY())+",\""+2+"\");r.attr(\"stroke\",\"orange\");");
			//11//System.out.println("pCanvas.text("+(p.getX())+","+(p.getY())+",\""+i+"(ABS"+i+")\");r.attr(\"stroke\",\"orange\");");
			Coordinate c = new Coordinate();
			c.x= p.getX();
			c.y = p.getY();
			coordinates.add(c);
			i++;
		}
		cx+=arg4;
		cy+=arg5;
		i++;
		
	}

	@Override
	public void curvetoCubicSmoothAbs(float arg0, float arg1, float arg2, float arg3) throws ParseException {
		//System.out.println("//1CNCPathHandler.curvetoCubicSmoothAbs()");

	}

	@Override
	public void curvetoCubicSmoothRel(float arg0, float arg1, float arg2, float arg3) throws ParseException {
		//System.out.println("//1CNCPathHandler.curvetoCubicSmoothRel()");
	}

	@Override
	public void curvetoQuadraticAbs(float arg0, float arg1, float arg2, float arg3) throws ParseException {
		System.out.println("//1CNCPathHandler.curvetoQuadraticAbs()");
		arg0 +=tx;
		arg1 +=ty;
		arg2 +=tx;
		arg3 +=ty;
		QuadBezierCurve2D cc = new QuadBezierCurve2D(cx,cy,arg0,arg1,arg2,arg3);
		QCurve sh = new QCurve(new org.ptg.tests.SVGextractTest.Point2D(cx,cy),new org.ptg.tests.SVGextractTest.Point2D(arg0,arg1),
				new org.ptg.tests.SVGextractTest.Point2D(arg2,arg3));
		
		shape.addShape(sh);
		AffineTransform2D dd = null;
		Polyline2D a = cc.asPolyline(pathPointCount);
		for(Point2D p: a.vertexArray()){
			Coordinate c = new Coordinate();
			c.x= p.getX();
			c.y = p.getY();
			coordinates.add(c);
			i++;
		}
		cx=arg2;
		cy=arg3;

		i++;
	}

	@Override
	public void curvetoQuadraticRel(float arg0, float arg1, float arg2, float arg3) throws ParseException {
		System.out.println("//1CNCPathHandler.curvetoQuadraticRel()");
		QuadBezierCurve2D cc = new QuadBezierCurve2D(cx,cy,cx+arg0,cy+arg1,cx+arg2,cy+arg3);
		QCurve sh = new QCurve(new org.ptg.tests.SVGextractTest.Point2D(cx,cy),
				new org.ptg.tests.SVGextractTest.Point2D(cx+arg0,cy+arg1),
				new org.ptg.tests.SVGextractTest.Point2D(cx+arg2,cy+arg3));
		shape.addShape(sh);
		Polyline2D a = cc.asPolyline(pathPointCount);
		int i = 0;
		for(Point2D p: a.vertexArray()){
			Coordinate c = new Coordinate();
			c.x= p.getX();
			c.y = p.getY();
			coordinates.add(c);
			i++;
		}
		cx+=arg2;
		cy+=arg3;
		i++;
	}

	@Override
	public void curvetoQuadraticSmoothAbs(float arg0, float arg1) throws ParseException {
		//System.out.println("//1CNCPathHandler.curvetoQuadraticSmoothAbs()");
	}

	@Override
	public void curvetoQuadraticSmoothRel(float arg0, float arg1) throws ParseException {
		//System.out.println("//1CNCPathHandler.curvetoQuadraticSmoothRel()");
	}

	@Override
	public void endPath() throws ParseException {
		//System.out.println("//1CNCPathHandler.endPath()");
	}

	@Override
	public void linetoAbs(float arg0, float arg1) throws ParseException {
		arg0 +=tx;
		arg1 +=ty;

		//System.out.println("//1CNCPathHandler.linetoAbs()");
		//System.out.println("pCanvas.text("+(arg0+3)+","+(arg1+3)+",\""+i+"(abs)\");r.attr(\"stroke\",\"orange\");");
		//System.out.println("var r= pCanvas.line("+(cx+3)+","+(cy+3)+","+(arg0+3)+","+(arg1+3)+");r.attr(\"stroke\",\"blue\");");

		Coordinate c0 = new Coordinate();
		c0.x= cx;
		c0.y = cy;
		coordinates.add(c0);
		
		Coordinate c = new Coordinate();
		c.x= arg0;
		c.y = arg1;
		coordinates.add(c);
		Line l = new Line(new org.ptg.tests.SVGextractTest.Point2D(cx,cy), new org.ptg.tests.SVGextractTest.Point2D(arg0,arg1));
		shape.addShape(l);
		cx = arg0;
		cy = arg1;
		i++;
		
	}

	@Override
	public void linetoHorizontalAbs(float arg0) throws ParseException {
		//System.out.println("//1CNCPathHandler.linetoHorizontalAbs()");
	}

	@Override
	public void linetoHorizontalRel(float arg0) throws ParseException {
		//System.out.println("//1CNCPathHandler.linetoHorizontalRel()");
	}

	@Override
	public void linetoRel(float arg0, float arg1) throws ParseException {
		//System.out.println("//1CNCPathHandler.linetoRel()");
		//System.out.println("pCanvas.text("+(cx+arg0+3)+","+(cy+arg1+3)+",\""+i+"(REL)\");r.attr(\"stroke\",\"orange\");");
		//System.out.println("var r= pCanvas.line("+(cx+3)+","+(cy+3)+","+(cx+3+arg0)+","+(cy+3+arg1)+");r.attr(\"stroke\",\"blue\");");

		Coordinate c0 = new Coordinate();
		c0.x= cx;
		c0.y = cy;
		coordinates.add(c0);
		
		Coordinate c = new Coordinate();
		c.x= cx+arg0;
		c.y =cy+arg1;
		coordinates.add(c);

		Line l = new Line(new org.ptg.tests.SVGextractTest.Point2D(cx,cy), 
				new org.ptg.tests.SVGextractTest.Point2D(cx+arg0,cy+arg1));
		shape.addShape(l);

		cx+=arg0;
		cy+=arg1;
		i++;
	}

	@Override
	public void linetoVerticalAbs(float arg0) throws ParseException {
		//System.out.println("//1CNCPathHandler.linetoVerticalAbs()");
	}

	@Override
	public void linetoVerticalRel(float arg0) throws ParseException {
		//System.out.println("//1CNCPathHandler.linetoVerticalRel()");
	}

	@Override
	public void movetoAbs(float arg0, float arg1) throws ParseException {
		//System.out.println("//1CNCPathHandler.movetoAbs()");
		sx = arg0;
		sy = arg1;
		sx +=tx;
		sy +=ty;
		cx = sx;
		cy = sy;
	}

	@Override
	public void movetoRel(float arg0, float arg1) throws ParseException {
		sx +=tx;
		sy +=ty;
		sx += arg0;
		sy += arg1;
		cx += sx;
		cy += sy;
		//System.out.println("//1CNCPathHandler.movetoRel()");

	}

	@Override
	public void startPath() throws ParseException {
		//System.out.println("//1CNCPathHandler.startPath()");
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public void setTy(double ty) {
		this.ty= ty;		
	}

	public void setTx(double tx) {
		this.tx = 		tx;
	}

	public ShapeEx getShape() {
		return shape;
	}

	public void setShape(ShapeEx shape) {
		this.shape = shape;
	}

}
