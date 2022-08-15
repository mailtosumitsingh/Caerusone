package org.ptg.util.model;

import java.util.List;

import org.ptg.util.awt.Point;

public class PointRoute {
  Point from;
  Point to;
  List<Point> routePoints;
public Point getFrom() {
	return from;
}
public void setFrom(Point from) {
	this.from = from;
}
public Point getTo() {
	return to;
}
public void setTo(Point to) {
	this.to = to;
}
public List<Point> getRoutePoints() {
	return routePoints;
}
public void setRoutePoints(List<Point> routePoints) {
	this.routePoints = routePoints;
}
  
}
