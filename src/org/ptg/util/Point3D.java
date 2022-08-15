package org.ptg.util;

public class Point3D {
	 public Point3D() {
		}
	 public Point3D(Point3D other) {
			this.x = other.x;
			this.y = other.y;
			this.z = other.z;
		}
	 public Point3D(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	 public Point3D(double[] p) {
			this.x = p[0];
			this.y = p[1];
			this.z = p[2];
		}
double x = 0;public double getX() {
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
public double getZ() {
	return z;
}
public void setZ(double z) {
	this.z = z;
}
double y=0;double z=0;
public double[] get() {
	return new double[] {x,y,z};
}
public void set(Point3D other) {
	this.x = other.x;
	this.y = other.y;
	this.z = other.z;
}
public void set(double[] p) {
	this.x = p[0];
	this.y = p[1];
	this.z = p[2];
}
}

