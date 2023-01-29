package org.ptg.eventloop;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Point {
	public int x;
	public int y;
	public String id;
	Map<String,Object> data = new HashMap<String,Object>();
	
	
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public Point() {
		super();
	}
	public Point(int x, int y, String id) {
		super();
		this.x = x;
		this.y = y;
		this.id = id;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	} 
	public Point clone() {
		Point ret = new Point();
		ret.x = this.x;
		ret.y = this.y;
		ret.id = this.id;
		return ret;
	}
	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", id=" + id + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, x, y);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		return Objects.equals(id, other.id) && x == other.x && y == other.y;
	}
	
}
