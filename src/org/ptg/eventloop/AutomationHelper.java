package org.ptg.eventloop;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Image;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Region;

public abstract class AutomationHelper {
	Robot r = new Robot();

	public void click(int x, int y, int w, int h) {
		r.click(x, y, w, h);
	}

	public void click(Region r) {
		r.click();
	}

	public void rightClick() {
		int[] loc = r.getMouseLoc();
		rightClick(loc[0],loc[1]);
	}
	
	public void rightClick(Region r) {
		r.rightClick();
	}
	public void rightClick(int x, int y) {
		r.rightClick(x,y);
	}
	public void doubleClick(Region r) {
		r.doubleClick();
	}
	
	public boolean clickImage(String path) {
		Image img = StaticUtil.getImageFromFile(path);
		if (img == null)return false;
		
		Location loc = this.r.findFirstImage(img);
		if (loc != null) {
			loc.click();
			return true;
		}
		return false;
	}
	public boolean doubleClickImage(String path) {
		Image img = StaticUtil.getImageFromFile(path);
		if (img == null)return false;
		
		Location loc = this.r.findFirstImage(img);
		if (loc != null) {
			loc.doubleClick();
			return true;
		}
		return false;
	}
	public boolean moveToImage(String path) {
		Image img = StaticUtil.getImageFromFile(path);
		if (img == null)return false;
		
		Location loc = this.r.findFirstImage(img);
		if (loc != null) {
			r.moveMouse(loc.x, loc.y);
			return true;
		}
		return false;
	}
	public void mouseMove(Region r) {
		r.mouseMove();
	}

	public void sendKeys(Region r, String text) {
		r.type(text);
	}

	public String getText(Region r) {
		return r.text();
	}

	public Image getImage(Region r) {
		return r.getImage();
	}
	public boolean saveImage(Region r, String path) {
		Image img = getImage(r);
		if(img!=null) {
			saveImage(img, path);
			return true;
		}
		return false;
	}

	public Point findFirstText(Region r, String txt) {
		Location loc = this.r.findFirstText(r, txt);
		if (loc != null) {
			Point p = new Point();
			p.setX(loc.getX());
			p.setY(loc.getY());
			return p;
		}
		return null;
	}

	public boolean ifFindText(Region r, String txt) {
		if(findFirstText(r, txt)==null)
			return false;
		return true;
	}
	public boolean ifFindImage(Region r, String image) {
		if(findFirstImage(r, image)==null)
			return false;
		return true;
	}

	public Point findFirstImage(Region r, String path) {
		Image img = StaticUtil.getImageFromFile(path);
		if (img == null)
			return null;
		return findFirstImage(r, img);
	}

	public Point findFirstImage(Region r, Image target) {
		Location loc = this.r.findFirstImage(r, target);
		if (loc != null) {
			Point p = new Point();
			p.setX(loc.getX());
			p.setY(loc.getY());
			return p;
		}
		return null;
	}

	public void click(int x, int y) {
		r.click(x, y);
	}

	public void clickPoint(Point p) {
		r.click(p.x, p.y);
	}

	public void doubleClick(int x, int y) {
		r.doubleClick(x, y);
	}

	public void moveMouse(int x, int y) {
		r.moveMouse(x, y);
	}

	public void sendText(int x, int y, int w, int h, String text) {
		r.sendText(x, y, w, h, text);
	}

	public void sendKeys(String text) {
		r.type(text);
	}

	public void type(String text) {
		r.type(text);
	}

	public void backspace() {
		r.backspace();
	}

	public int[] getColor(int x, int y) {
		return r.getColor(x, y);
	}

	public int[] getMouseLoc() {
		return r.getMouseLoc();
	}

	public void sendText(int x, int y, String text) {
		r.sendText(x, y, text);
	}

	public String toText(int x, int y, int w, int h) {
		return r.toText(x, y, w, h);
	}

	public List<Match> findAllText(Image img, String txt) {
		return r.findAllText(img, txt);
	}

	public Iterator<Match> findAllImage(Region r, Image target) throws FindFailed {
		return this.r.findAllImage(r, target);
	}

	public Point findFirstText(String txt) {
		Location loc = this.r.findFirstText(txt);
		if (loc != null) {
			Point p = new Point();
			p.setX(loc.getX());
			p.setY(loc.getY());
			return p;
		}

		return null;
	}

	public String toText() {
		return r.toText();
	}

	public Point findFirstImage(String path) {
		Image img = StaticUtil.getImageFromFile(path);
		if (img == null)
			return null;
		return findFirstImage(img);
	}

	public Point findFirstImage(Image target) {
		Location loc = r.findFirstImage(target);
		if (loc != null) {
			Point p = new Point();
			p.setX(loc.getX());
			p.setY(loc.getY());
			return p;
		}

		return null;

	}

	public void moveMouseWheel(int amt) {
		r.moveMouseWheel(amt);
	}

	public int[] getMouseColor(int x, int y) {
		return r.getMouseColor(x, y);
	}

	public void sleepMS(int sleepTimeMs) {
		r.sleepMS(sleepTimeMs);
	}

	public String screenShot() {
		return r.screenShot();
	}
	public void saveImage(Image img, String path) {
		StaticUtil.saveImage(img, path);
	}
}
