package org.ptg.tests.SVGextractTest;

public class Line implements IShape{
	Point2D s;
	Point2D e;
	public Point2D getS() {
		return s;
	}
	public void setS(Point2D s) {
		this.s = s;
	}
	public Point2D getE() {
		return e;
	}
	public void setE(Point2D e) {
		this.e = e;
	}
	@Override
	public String getType() {
		return "L";
	}
	@Override
	public int getN() {
		return 2;
	}
	public Line(Point2D s, Point2D e) {
		super();
		this.s = s;
		this.e = e;
	}
	@Override
	public String toSVG() {
		return "r = pCanvas.line("+s.x+","+s.y+","+e.x+","+e.y+");";
	}
	@Override
	public String toString() {
		return  "\"M"+s.x+", "+s.y+" L " +e.x+" ,"+e.y+"\"";
	}
	
}
