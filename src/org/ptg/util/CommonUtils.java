package org.ptg.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.collections.Closure;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.ClassReader;
import org.ptg.analyzer.asm.ConstExtractor;
import org.ptg.analyzer.model.Setter;

public class CommonUtils {
	public static void forEachFileInDirFile(String name, Closure func, boolean withdir, boolean recursive) {
		File f = new File(name);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File fl : files) {
				if (fl.isFile()) {
					func.execute(fl);
				} else if (f.isDirectory()) {
					if (withdir) {
						func.execute(fl);
					}
					if (recursive) {
						forEachFileInDirFile(fl.getAbsolutePath(), func, withdir, recursive);
					}
				}
			}
		}
	}

	public static String jaxbMarshall(Object o, String pack) {
		JAXBContext context = null;
		Marshaller marshaller = null;
		String ret = null;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			context = JAXBContext.newInstance(pack);
			marshaller = context.createMarshaller();
			marshaller.marshal(o, bs);
			ret = new String(bs.toByteArray());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Object jaxbUnMarshall(InputStream data, String pack) {
		JAXBContext context = null;
		Unmarshaller unmarshaller = null;
		Object ret = null;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			context = JAXBContext.newInstance(pack);
			unmarshaller = context.createUnmarshaller();
			ret = unmarshaller.unmarshal(data);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return ret;
	}
	public static void generatePDF(String url){
		
	}
	public static String[] runCmd(String line,String wdir) throws ExecuteException, IOException{
		String[] ret = new String[3];		
		int exitValue =   -1;
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		String random = RandomStringUtils.randomAlphanumeric(8);
		String outfile = SpringHelper.get("systemtempdir")+"out_"+random+".tmp";
		String errfile = SpringHelper.get("systemtempdir")+"err_"+random+".tmp";
		FileOutputStream stdout = new FileOutputStream(new File(outfile));
		FileOutputStream stderr = new FileOutputStream(new File(errfile));
        PumpStreamHandler handler = new PumpStreamHandler(stdout,stderr);
		executor.setStreamHandler(handler);
		executor.setWorkingDirectory(new File(wdir));
		exitValue = executor.execute(cmdLine);
		stdout.flush();
		stdout.close();
		stderr.flush();
		stderr.close();
		ret[0] = ""+exitValue;
		ret[1] = outfile;
		ret[2] = errfile;
		return ret;
	}
	public static void captureScreen(String fileName) throws Exception {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		String format = StringUtils.substringAfterLast(fileName, ".");
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		ImageIO.write(image, format, new File(fileName));

	}
///////////image utils
	public static List<String> extractSVGPaths(String fname) throws IOException, FileNotFoundException {
		List<String>  paths = new LinkedList<String>();
	    Source source = new Source(IOUtils.toString(new FileInputStream(fname)));
	    List<Element> a = source.getAllElements("path");
	    for(Element e: a){
	    	String path = e.getAttributeValue("d");
	    	if(!path.contains("z")) {
	    	System.out.println(path);
	    	paths.add(path);
	    	}else {
	    		String[] subPaths = StringUtils.split(path,"z");
	    		for(String subPath : subPaths)
	    			paths.add(subPath);
	    	}
			
	    }
	    return paths;
	}
	
	//mkbitmap -f 2 -s 2 -t 0.48
	public static void mkbitmap(String fname) throws ExecuteException, IOException{
		String out = StringUtils.substringBeforeLast(fname, ".");
		String cmd = "C:/projects/CaerusonePlugins/ImageFilters/native/image/mkbitmap -f 2 -t 0.48 "+fname+" -o "+out+".bmp";
		String[] o = CommonUtils.runCmd(cmd, "c:\\temp");
	}
//	/C:\projects\CaerusonePlugins\ImageFilters\native\image\potrace.exe"
	public static void convertToBMP256(String fname) throws ExecuteException, IOException{
		String out = StringUtils.substringBeforeLast(fname, ".");
		String cmd = "c:/Progra~1/GraphicsMagick-1.3.18-Q8/gm.exe convert -colors 256 "+fname+" "+out+".bmp";
		String[] o = CommonUtils.runCmd(cmd, "c:\\temp");
	}
	public static void convertToSvg(String fname) throws ExecuteException, IOException{
		String out = StringUtils.substringBeforeLast(fname, ".");
		String cmd = "C:/projects/CaerusonePlugins/ImageFilters/native/image/potrace.exe -s "+out+".bmp -o "+out+".svg";
		String[] o = CommonUtils.runCmd(cmd, "c:\\temp");
	}
	
	public static List<String> extractSVG2(String fname) throws ExecuteException, IOException{
		mkbitmap(fname);
		convertToSvg(fname);
		String out = StringUtils.substringBeforeLast(fname, ".");
		String svg = out+".svg";
		return extractSVGPaths(svg);
}	
	public static List<String> extractSVG(String fname) throws ExecuteException, IOException{
		convertToBMP256(fname);
		convertToSvg(fname);
		String out = StringUtils.substringBeforeLast(fname, ".");
		String svg = out+".svg";
		return extractSVGPaths(svg);
		
}
/////////image utils end
	public static List<Setter> visitClassCode(java.util.jar.JarFile jar ,java.util.jar.JarEntry file ) throws Exception{
		java.io.InputStream is = jar.getInputStream(file); 
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		while (is.available() > 0) {  
			fos.write(is.read());
		}
		fos.flush();
		fos.close();
		byte[]data = fos.toByteArray();
		List<Setter> v = analyzeBytecode(data);
		System.out.println(v);
		return v;
	}

	public static List<Setter> analyzeBytecode(byte[] data) throws IOException {
		ClassReader  cr = new ClassReader(new ByteArrayInputStream(data));
		ConstExtractor v = new ConstExtractor(262144);
		cr.accept(v,0);
		return v.getReach();
	}

	public static List<Setter> analyzeBytecode(String f) throws IOException {
		ClassReader  cr = new ClassReader(new FileInputStream(f));
		ConstExtractor v = new ConstExtractor(262144);
		cr.accept(v,0);
		return v.getReach();
	}
	
}
