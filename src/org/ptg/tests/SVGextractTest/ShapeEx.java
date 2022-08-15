package org.ptg.tests.SVGextractTest;

import java.util.LinkedList;
import java.util.List;

public class ShapeEx implements IShape{
	List<IShape> shapes = new LinkedList<IShape>();

	public List<IShape> getShapes() {
		return shapes;
	}

	public void setShapes(List<IShape> shapes) {
		this.shapes = shapes;
	}

	public void addShape(IShape shape) {
		shapes.add(shape);
	}

	@Override
	public String getType() {
		return "S";
	}

	@Override
	public int getN() {
		int ret = 0;
		for(IShape s: shapes)
			ret +=s.getN();
		return ret;
	}

	@Override
	public String toSVG() {
		StringBuilder sb = new StringBuilder();
		for(IShape s: shapes){
			sb.append(s.toSVG());
			sb.append("\n");
		}
		return sb.toString();
	}
}
