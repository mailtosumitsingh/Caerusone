package org.ptg.util.awt;

public class DistVector {
int distance;
Point point;
Point from;

public DistVector() {
}
public DistVector(int distance, Point point, Point from) {
	super();
	this.distance = distance;
	this.point = point;
	this.from = from;
}
public Point getFrom() {
	return from;
}
public void setFrom(Point from) {
	this.from = from;
}
public Point getPoint() {
	return point;
}
public void setPoint(Point point) {
	this.point = point;
}
public int getDistance() {
	return distance;
}
public void setDistance(int distance) {
	this.distance = distance;
}
public DistVector(int distance, Point point) {
	super();
	this.distance = distance;
	this.point = point;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((point == null) ? 0 : point.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	DistVector other = (DistVector) obj;
	if (point == null) {
		if (other.point != null)
			return false;
	} else if (!point.equals(other.point))
		return false;
	return true;
}
@Override
public String toString() {
	return point .toString();
}

}
