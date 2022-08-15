
package org.ptg.models;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

public class Point {
	public int x = 0;
	public int y = 0;
	public int z = 0;
	private String id;
	public String type="ShapePoint";
	private String[] tags = new String[0];

	private Map<String, String> data = Maps.newHashMap();

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public enum PointType {
		Position, Control, control, position
	};

	private PointType pointType = PointType.Position;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PointType getPointType() {
		return pointType;
	}

	public void setPointType(PointType pointType) {
		this.pointType = pointType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Point(int x, int y, int z, String id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	public Point(int x, int y, String id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}

	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point() {
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

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", z=" + z + ", id=" + id + ", type=" + type + ", tags=" + Arrays.toString(tags) + ", pointType=" + pointType + "]";
	}

}
