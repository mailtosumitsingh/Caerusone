package org.ptg.tests.opencv.dro.simpledro;

import java.util.List;

import com.google.common.collect.Lists;

public class SimpleDigit {
	int x;
	int y;
	int w;
	int h;
	String label;
	int nsegments = 7;
	
	public int getNsegments() {
		return nsegments;
	}

	public void setNsegments(int nsegments) {
		this.nsegments = nsegments;
	}

	public List<SimpleSegment> getSegments() {
		return segments;
	}

	public void setSegments(List<SimpleSegment> segments) {
		this.segments = segments;
	}
	private List<SimpleSegment> segments  = Lists.newArrayList();
	{
		for(int i=0;i<7;i++) {
			segments.add(new SimpleSegment(0,0,0,0,""));
		}
		
	}
	
	private SimpleDigit() {
	}

	public SimpleDigit(int x, int y, int w, int h, int label) {
		this(x,y,w,h,"" +label);
	}

	public SimpleDigit(int x, int y, int w, int h, String label) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
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
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public SimpleDigit clone() {
		SimpleDigit  d = new SimpleDigit (this.x,this.y,this.w,this.h,this.label);
		List<SimpleSegment> segs = Lists.newArrayList();
		for(int i=0;i<nsegments;i++) {
			SimpleSegment seg = this.segments.get(i);
			SimpleSegment sg = new SimpleSegment(seg.getX(), seg.getY(), seg.getW(), seg.getH(), seg.getLabel());
			segs.add(sg);
		}
		d.segments = segs;
		return d;
		
	}

}
