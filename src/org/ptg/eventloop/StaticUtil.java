package org.ptg.eventloop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Uninterruptibles;

import org.sikuli.script.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class StaticUtil {
	public static String toJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}



	public static BufferedImage cloneImage(BufferedImage in) {
		BufferedImage o2 = new BufferedImage(in.getWidth(), in.getHeight(), in.getType());
		return o2;
	}

	public static void save(BufferedImage o2, String fileName, String fileTypeExt) throws Exception {
		ImageIO.write(o2, fileTypeExt, new FileOutputStream(fileName));
	}

	public static BufferedImage  getImage(String path) throws IOException, FileNotFoundException {
		BufferedImage o = ImageIO.read(new FileInputStream(path));
		return o;
	}
	public static Image takeScreenShot(int x, int y, int w, int h) {
		Region r = new Region(x,y,w,h);
		Image img = r.getImage();
		return img;
	}
	public static void saveImage(Image img, String path) {
		img.save(path);
	}
	public static Match imgmatch(Image img, Image img2) throws FindFailed {
		return img.find(img2);
	}
	public static  boolean has(Image img, Image img2) throws FindFailed {
		Match a = img.find(img2);
		if(a.isValid())return true;
		return false;
	}
	public static List<Match> getImage(Image img1,Image img2) {
		Finder f = new Finder(img1);
		f.find(img2);
		return f.getList();
	  }
	public static List<Match> getText(Image img1,String text) {
		Finder f = new Finder(img1);
		f.findText(text);
		return f.getList();
				
	  }
	public static Image getImageFromFile(String s) {
		if(!s.startsWith("c:")) {
			s = "C:\\projects\\images\\"+s;
		}
		if(!s.endsWith(".jpg")||!s.endsWith(".png"))
			s= s + ".jpg";
		
		System.out.println("Loading image from file: "+s);
		try {
			BufferedImage img = ImageIO.read(new File(s));
			return new Image(img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static  BufferedImage getBufferedImageFromFile(String s) throws IOException {
		BufferedImage img = ImageIO.read(new File(s));
		return img;
	}
	
	public static String getTempFile(String type) {
		return UUID.randomUUID().toString()+type;
	}

	public static String getTempFile() {
		return UUID.randomUUID().toString()+".png";
	}
	public static String getTempPath() {
		return getTempDir()+getTempFile();
	}

	public static String getTempDir() {
		return "c:\\temp\\";
	}
	public static void sleepMS(int sleepTimeMs) {
		Uninterruptibles.sleepUninterruptibly(sleepTimeMs, TimeUnit.MILLISECONDS);
	}
}
