package org.ptg.tests.opencv.dro;

import java.util.List;

import org.ptg.util.awt.Rectangle;

import com.google.common.collect.Lists;

public class Digit {
	int x;
	int y;
	int w;
	int h;
	int sgw = 8;
	int segmentDrift = 4;
	int sgh = 4;
	int segSpace = 1;
	int segDriftV = 1;

	String label;

	public int getSgw() {
		return sgw;
	}

	public void setSgw(int sgw) {
		this.sgw = sgw;
	}

	public int getSegDriftV() {
		return segDriftV;
	}

	public void setSegDriftV(int segDriftV) {
		this.segDriftV = segDriftV;
	}

	public int getSegSpace() {
		return segSpace;
	}

	public void setSegSpace(int segSpace) {
		this.segSpace = segSpace;
	}

	public int getSgh() {
		return sgh;
	}

	public void setSgh(int sgh) {
		this.sgh = sgh;
	}

	public int getSegmentDrift() {
		return segmentDrift;
	}

	public void setSegmentDrift(int segmentDrift) {
		this.segmentDrift = segmentDrift;
	}

	List<Rectangle> segments = Lists.newArrayList();
	{
		for (int i = 0; i < 8; i++) {
			segments.add(new Rectangle());
		}
	}

	public List<Rectangle> getSegments() {
		return segments;
	}

	public void setSegments(List<Rectangle> segments) {
		this.segments = segments;
	}



	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public Digit(int x, int y, int w, int h, String lbl) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.label = lbl;
	}

	public Digit() {

	}

}
