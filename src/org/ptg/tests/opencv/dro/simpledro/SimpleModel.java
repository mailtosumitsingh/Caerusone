package org.ptg.tests.opencv.dro.simpledro;

import java.util.List;

import org.opencv.core.Scalar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SimpleModel {
	private double displayAngle = 2;
	private int x = 74, y = 98, w = 184, h = 28;
	@JsonIgnore
	private Scalar displayColor = new Scalar(65, 253, 242);
	@JsonIgnore
	private Scalar colorMatchTolerance = new Scalar(10, 10, 10);
	private List<SimpleDigit> digits = Lists.newArrayList();
	private int digitWidth = 25;
	private int digitHeight = 18;

	private int modelSegmentClusterTolerance = 60;
	private int intParts = 3;
	public int getModelSegmentClusterTolerance() {
		return modelSegmentClusterTolerance;
	}

	public void setModelSegmentClusterTolerance(int modelSegmentClusterTolerance) {
		this.modelSegmentClusterTolerance = modelSegmentClusterTolerance;
	}


	public double getDisplayAngle() {
		return displayAngle;
	}

	public void setDisplayAngle(double displayAngle) {
		this.displayAngle = displayAngle;
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

	public Scalar getDisplayColor() {
		return displayColor;
	}

	public void setDisplayColor(Scalar displayColor) {
		this.displayColor = displayColor;
	}

	public Scalar getColorMatchTolerance() {
		return colorMatchTolerance;
	}

	public void setColorMatchTolerance(Scalar colorMatchTolerance) {
		this.colorMatchTolerance = colorMatchTolerance;
	}

	public int getIntParts() {
		return intParts;
	}

	public void setIntParts(int intParts) {
		this.intParts = intParts;
	}

	public List<SimpleDigit> getDigits() {
		return digits;
	}

	public void setDigits(List<SimpleDigit> digits) {
		this.digits = digits;
	}

	public int getDigitWidth() {
		return digitWidth;
	}

	public void setDigitWidth(int digitWidth) {
		this.digitWidth = digitWidth;
	}

	public int getDigitHeight() {
		return digitHeight;
	}

	public void setDigitHeight(int digitHeight) {
		this.digitHeight = digitHeight;
	}
	


}
