package org.ptg.tests.SVGextractTest;

public class CubicCurve implements IShape {
	Point2D s;
	Point2D c1;
	Point2D c2;
	Point2D e;

	public CubicCurve(Point2D s, Point2D c1, Point2D c2, Point2D e) {
		this.s = s;
		this.c1 = c1;
		this.c2 = c2;
		this.e = e;
	}

	public Point2D getS() {
		return s;
	}

	public void setS(Point2D s) {
		this.s = s;
	}

	public Point2D getC1() {
		return c1;
	}

	public void setC1(Point2D c1) {
		this.c1 = c1;
	}

	public Point2D getC2() {
		return c2;
	}

	public void setC2(Point2D c2) {
		this.c2 = c2;
	}

	public Point2D getE() {
		return e;
	}

	public void setE(Point2D e) {
		this.e = e;
	}

	@Override
	public String getType() {
		return "C";
	}

	@Override
	public int getN() {
		return 4;
	}

	@Override
	public String toSVG() {
		String ret = "r = pCanvas.path(\"M"+s.x+", "+s.y+" C " +c1.x+" ,"+c1.y+" " +c2.x+" ,"+c2.y+" " +e.x+" ,"+e.y+"\");";	
		ret +="r = pCanvas.circle("+s.x+","+s.y+","+"5);";
		ret +="r = pCanvas.circle("+c1.x+","+c1.y+","+"5);";
		ret +="r = pCanvas.circle("+c2.x+","+c2.y+","+"5);";

		ret +="r = pCanvas.circle("+e.x+","+e.y+","+"5);";
	return ret;
}

	@Override
	public String toString() {
		return "\"M"+s.x+", "+s.y+" C " +c1.x+" ,"+c1.y+" " +c2.x+" ,"+c2.y+" " +e.x+" ,"+e.y+"\"";
	}

}
