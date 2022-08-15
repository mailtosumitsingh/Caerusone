package org.ptg.plugins;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class OpencvDroppableGenerator {
	public static void main(String[] args) throws IOException {
		String pluginId = "OpenCV";
		String[] items = new String[] { "${id}prepareToStop(MethodCall)", "${id}prepareToRun(MethodCall)", "${id}isContinueRunning(MethodCall)", "${id}deployImage(MethodCall)",
				"${id}saveImage(MethodCall)", "${id}uploadImages(MethodCall)", "${id}stop(MethodCall)", "${id}start(MethodCall)", "${id}getImage(MethodCall)", "${id}gaussSmooth(MethodCall)",
				"${id}setText(MethodCall)", "${id}getDateString(MethodCall)", "${id}sleep(MethodCall)", "${id}calcWhitePixel(MethodCall)", "${id}clone(MethodCall)",
				"${id}createGrayImage(MethodCall)", "${id}toGrayImage(MethodCall)", "${id}absDiff(MethodCall)", "${id}calculateWhitePixelCount(MethodCall)", "${id}calculatePercChange(MethodCall)",
				"${id}createCanvas(MethodCall)", "${id}showImage(MethodCall)", "${id}convertToBinImage(MethodCall)", "${id}getFont(MethodCall)" };
		java.util.List<DefaultDroppable> drops = new LinkedList<DefaultDroppable>();
		for (String str : items) {
			String c = CommonUtil.getStaticComponent(str);
			String base =  (String) SpringHelper.get("basedir");
			String str2 = CommonUtil.getSafeIdentifier(str);
			FileUtils.writeStringToFile(new File(base+"\\plugins\\"+pluginId+"\\drops\\"+str2+".js"), c);
			DefaultDroppable d = new DefaultDroppable();
			d.setCode(str2+".js");
			d.setId(str);
			drops.add(d);
		}
		StringBuilder sb = new StringBuilder();
		for(DefaultDroppable d: drops){
		sb.append(d+"\n");
		}
		System.out.println(sb.toString());
	}
}
