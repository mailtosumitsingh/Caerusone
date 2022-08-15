package org.ptg.tests.SVGextractTest;

public class QCurve implements IShape{
Point2D s;
Point2D c1;
Point2D e;

public QCurve(Point2D s, Point2D c1,Point2D e) {
	this.s = s;
	this.c1 = c1;
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
public Point2D getE() {
	return e;
}
public void setE(Point2D e) {
	this.e = e;
}
@Override
public String getType() {
	return "Q";
}
@Override
public int getN() {
	return 3;
}
@Override
public String toSVG() {
	return "null";
}

}
