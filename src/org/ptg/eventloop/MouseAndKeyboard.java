package org.ptg.eventloop;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;

public class MouseAndKeyboard {
	private static Robot r;
	static AsciiKeyTyper t;
	static {
		try {
			r = new Robot();
			t = new AsciiKeyTyper();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static void sendText(String text, int pace) throws Exception {
		for (char c : text.toCharArray()) {
			t.typeKey(c);
			Thread.sleep(pace + 20);
		}
	}

	public static void sendText(String text) throws Exception {
		System.out.println("Send text: " + text);
		sendText(text, 20);
	}

	public static void sendBackSpace() {
		r.keyPress(KeyEvent.VK_BACK_SPACE);
		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
		r.keyRelease(KeyEvent.VK_BACK_SPACE);
		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
	}

	public static void moveMouseApprox(int x, int y) {
		r.mouseMove(x, y);
	}

	public static void moveMouse(int x, int y) {
		for (int i = 0; i < 10; i++) {
			r.mouseMove(x, y);
			int[] loc = MouseAndKeyboard.getMouseLoc();
			int xoff = loc[0] - x;
			int yoff = loc[1] - y;
			System.out.println(xoff + " : " + yoff);
			if (xoff == 0 && yoff == 0)
				return;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void moveLClicked() throws Exception {
		r.mousePress(MouseEvent.BUTTON1_MASK);
		Thread.currentThread().sleep(40);
		r.mouseRelease(MouseEvent.BUTTON1_MASK);
	}

	public static void moveRClicked() throws Exception {
		r.mousePress(MouseEvent.BUTTON3_MASK);
		Thread.currentThread().sleep(40);
		r.mouseRelease(MouseEvent.BUTTON3_MASK);
	}

	public static void moveMClicked() throws Exception {
		r.mousePress(MouseEvent.BUTTON2_MASK);
		Thread.currentThread().sleep(40);
		r.mouseRelease(MouseEvent.BUTTON2_MASK);
	}

	public static void moveLDown() throws Exception {
		r.mousePress(MouseEvent.BUTTON1_MASK);
	}

	public static void moveRDown() throws Exception {
		r.mousePress(MouseEvent.BUTTON3_MASK);
	}

	public static void moveMDown() throws Exception {
		r.mousePress(MouseEvent.BUTTON2_MASK);
	}

	public static void moveLUp() throws Exception {
		r.mouseRelease(MouseEvent.BUTTON1_MASK);
	}

	public static void moveRUp() throws Exception {
		r.mouseRelease(MouseEvent.BUTTON3_MASK);
	}

	public static void moveMUp() throws Exception {
		r.mouseRelease(MouseEvent.BUTTON2_MASK);
	}

	public static void movePress(int buttons) throws Throwable {
		r.mousePress(buttons);
		Thread.currentThread().sleep(40);
		r.mouseRelease(buttons);
	}

	public static void moveMouseWheel(int amt) throws Throwable {
		r.mouseWheel(amt);
	}

	public static int[] getMouseLoc() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		return new int[] { (int) p.getX(), (int) p.getY() };
	}

	public static int[] getMouseColor(int x, int y) {
		return new int[] { r.getPixelColor(x, y).getRed(), r.getPixelColor(x, y).getGreen(),
				r.getPixelColor(x, y).getBlue() };
	}

}
