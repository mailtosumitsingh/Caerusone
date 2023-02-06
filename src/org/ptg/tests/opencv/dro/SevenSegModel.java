package org.ptg.tests.opencv.dro;

import java.util.List;

import org.opencv.core.Scalar;

import com.google.common.collect.Lists;

public class SevenSegModel {
	// display angle it is hard to place camera and display at same angle so we
	// compensate
	// in software what will take minutes each time and this will be automated in
	// next excercise
	private int displayAngle = 2;
	// display area we are interested in that contains the 7 segment display
	// characters.
	private int x = 74, y = 98, w = 184, h = 28;
	// Segment info generally it is 7 segment display.
	// segment width horizontal generally it is 2wice the size or 1.5 or 3 in some
	// good proportion
	private int segmentWidth = 8;
	// height of individual segment generally it is in proportion with width
	private int segmentHeight = 4;
	// segments are placed at and angle sometimes (acctually mostly ;-) not sure why
	// anymore since we have been trying to digitize from years)
	private int segmentDriftHorizontal = 4;
	// segment space - generally whensegments placed they are placed with a min
	// distance between them
	// since by design they are tried to be put toghether as close possible
	// sometimes itis
	// not possible to have a close fit and results in space. it could be more for
	// bigger displays and for non leds - 0.
	private int segmentSpace = 1;
	// drift vertical as segments are placed at an angle cause a drift for our
	// recognizer rectangle area of interest.
	private int segmentDriftVertical = 1;
	// colors
	// display color that we are going to recognize.
	private Scalar displayColor = new Scalar(65, 253, 242);
	// display color tolerenaces - if the color falls in the tolereance range it
	// will be considered same
	// so if we have tolereance of 10 for each RGB we will consider a color green if
	// it is within 10 off in RGB range space.
	private Scalar colorMatchTolerance = new Scalar(10, 10, 10);
	// no of digits in our charachter stream on display.
	private int noOfDigits = 8;
	// how many of total noOfDigits counts towards integer part
	// this saves us form trying to recognize dot
	// it can be dynamic on some advanced display
	private int intParts = 3;
	private List<Digit> digits = Lists.newArrayList();
	private List<DigitOffSet> digitOffsets = Lists.newLinkedList();
	private int digitWidth = 25;
	private int digitHeight = 18;

	private int modelSegmentClusterTolerance = 60;

	public int getModelSegmentClusterTolerance() {
		return modelSegmentClusterTolerance;
	}

	public void setModelSegmentClusterTolerance(int modelSegmentClusterTolerance) {
		this.modelSegmentClusterTolerance = modelSegmentClusterTolerance;
	}

	public void initDigits() {
		for (int i = 0; i < noOfDigits; i++) {
			Digit d = new Digit(x + i * digitWidth + digitOffsets.get(i).getxOffset(), y, digitHeight + digitOffsets.get(i).getyOffset(), h, "" + i);
			d.setSgw(segmentWidth);
			d.setSegmentDrift(segmentDriftHorizontal);
			d.setSgh(segmentHeight);
			d.setSegSpace(segmentSpace);
			d.setSegDriftV(segmentDriftVertical);
			digits.add(d);
		}
	}

	public void initDigitsOffsets() {
		digitOffsets.clear();
		for (int i = 0; i < noOfDigits; i++) {
			DigitOffSet d = new DigitOffSet(0, 0);
			digitOffsets.add(d);
		}
		digitOffsets.get(0).setxOffset(-1);
		digitOffsets.get(0).setyOffset(2);

		digitOffsets.get(1).setxOffset(-2);
		digitOffsets.get(1).setyOffset(2);

		digitOffsets.get(2).setxOffset(-3);
		digitOffsets.get(2).setyOffset(2);

		digitOffsets.get(3).setxOffset(-5);
		digitOffsets.get(3).setyOffset(2);

		digitOffsets.get(4).setxOffset(-6);
		digitOffsets.get(4).setyOffset(2);

		digitOffsets.get(5).setxOffset(-8);
		digitOffsets.get(5).setyOffset(2);

		digitOffsets.get(6).setxOffset(-9);
		digitOffsets.get(6).setyOffset(2);

		digitOffsets.get(7).setxOffset(-9);
		digitOffsets.get(7).setyOffset(2);
	}

	public int getDisplayAngle() {
		return displayAngle;
	}

	public void setDisplayAngle(int displayAngle) {
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

	public int getSegmentWidth() {
		return segmentWidth;
	}

	public void setSegmentWidth(int segmentWidth) {
		this.segmentWidth = segmentWidth;
	}

	public int getSegmentHeight() {
		return segmentHeight;
	}

	public void setSegmentHeight(int segmentHeight) {
		this.segmentHeight = segmentHeight;
	}

	public int getSegmentDriftHorizontal() {
		return segmentDriftHorizontal;
	}

	public void setSegmentDriftHorizontal(int segmentDriftHorizontal) {
		this.segmentDriftHorizontal = segmentDriftHorizontal;
	}

	public int getSegmentSpace() {
		return segmentSpace;
	}

	public void setSegmentSpace(int segmentSpace) {
		this.segmentSpace = segmentSpace;
	}

	public int getSegmentDriftVertical() {
		return segmentDriftVertical;
	}

	public void setSegmentDriftVertical(int segmentDriftVertical) {
		this.segmentDriftVertical = segmentDriftVertical;
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

	public int getNoOfDigits() {
		return noOfDigits;
	}

	public void setNoOfDigits(int noOfDigits) {
		this.noOfDigits = noOfDigits;
	}

	public int getIntParts() {
		return intParts;
	}

	public void setIntParts(int intParts) {
		this.intParts = intParts;
	}

	public List<Digit> getDigits() {
		return digits;
	}

	public void setDigits(List<Digit> digits) {
		this.digits = digits;
	}

	public List<DigitOffSet> getDigitOffsets() {
		return digitOffsets;
	}

	public void setDigitOffsets(List<DigitOffSet> digitOffsets) {
		this.digitOffsets = digitOffsets;
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
