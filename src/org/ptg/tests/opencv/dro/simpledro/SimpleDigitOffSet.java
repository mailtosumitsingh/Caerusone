package org.ptg.tests.opencv.dro.simpledro;

public class SimpleDigitOffSet {
	private int xOffset;
	private int yOffset;
	
	public SimpleDigitOffSet(int xoff, int yoff) {
		this.xOffset	= xoff;
		this.yOffset = yoff;
	}
	public int getxOffset() {
		return xOffset;
	}
	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	public int getyOffset() {
		return yOffset;
	}
	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
}
