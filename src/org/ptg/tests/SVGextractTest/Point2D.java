package org.ptg.tests.SVGextractTest;

public class Point2D implements IShape {
double x;
double y;
public double getX() {
	return x;
}
public void setX(double x) {
	this.x = x;
}
public double getY() {
	return y;
}
public void setY(double y) {
	this.y = y;
}
@Override
public String getType() {
	return "P";
}
@Override
public int getN() {
	return 1;
}
public Point2D(double x, double y) {
	super();
	this.x = x;
	this.y = y;
}
@Override
public String toSVG() {
	return "r = pCanvas.circle("+x+","+y+","+"5);";
}

}
