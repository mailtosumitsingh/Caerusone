package org.ptg.eventloop;

import org.sikuli.script.*;
import org.sikuli.script.Image;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@Component
public class Robot {
    OCR ocr = new OCR();

    public void click(int x, int y, int w, int h) {
        Region r = new Region(x, y, w, h);
        r.getCenter().click();

    }
    


    public void rightClick(int x, int y) {
        Location l = new Location(x, y);
        l.rightClick();
    }
    

    public void click(int x, int y) {
        Location l = new Location(x, y);
        l.click();
    }

    public void doubleClick(int x, int y) {
        Location l = new Location(x, y);
        l.doubleClick();
    }
	public void moveMouse(int x, int y){
		MouseAndKeyboard.moveMouse(x,y);
	}
    public void sendText(int x, int y, int w, int h, String text) {
        Region r = new Region(x, y, w, h);
        r.type(text);

    }
    public void type(String text){
        try {
            MouseAndKeyboard.sendText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void backspace(){
        MouseAndKeyboard.sendBackSpace();
    }
    public int[] getColor(int x, int y){
        return MouseAndKeyboard.getMouseColor(x,y);
    }
    public int[] getMouseLoc(){
        return MouseAndKeyboard.getMouseLoc();
    }
    public void sendText(int x, int y, String text) {
        Region r = new Region(x, y);
        r.type(text);
    }

    public String toText(int x, int y, int w, int h) {
        Region r = new Region(x, y, w, h);
		return r.text();
    }

    public List<Match> findAllText(Image img, String txt) {
        return ocr.findAllText(img, txt);
    }

    public Iterator<Match> findAllImage(Region r, Image target) throws FindFailed {
        return ocr.findAllImage(r, target);
    }
    public Location findFirstText(Region r,String txt) {
        try {
            return r.findText(txt).getTarget();
        } catch (FindFailed e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Location findFirstText(String txt) {
        try {
            Region r = new Region(Screen.getPrimaryScreen());
            System.out.println(r.text());
            return r.findText(txt).getTarget();
        } catch (FindFailed e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String toText() {
        try {
            Region r = new Region(Screen.getPrimaryScreen());
            return r.text();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Location findFirstImage(Region r, Image target) {
        try {
            return r.find(target).getTarget();
        } catch (FindFailed e) {
            e.printStackTrace();
        }
        return null;
    }
    public Location findFirstImage(Image target) {
        try {
            Region r = new Region(Screen.getPrimaryScreen());
            return r.find(target).getTarget();
        } catch (FindFailed e) {
            e.printStackTrace();
        }
        return null;
    }

	public void moveMouseWheel(int amt) {
        try {
            MouseAndKeyboard.moveMouseWheel(amt);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


	public  int[] getMouseColor(int x , int y) {
		return MouseAndKeyboard.getMouseColor(x,y);
	}
    public  void sleepMS(int sleepTimeMs) {
         StaticUtil.sleepMS(sleepTimeMs);
    }

    public String screenShot(){
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        Region r = new Region(0,0,width,height);
        Image img = r.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img.get(), "png", baos);
            byte[] bytes = baos.toByteArray();
            String data = Base64.getEncoder().encodeToString(bytes);
           return data;
            //StaticUtil.saveImage(img2, StaticUtil.getTempDir()+p.getId()+".jpg");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
