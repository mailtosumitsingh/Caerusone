package org.ptg.util.extra;

import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.SystemUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class BuildClassPath {
public static String buildPath() {
	StringBuilder sb = new StringBuilder();
	String baseDir = (String)SpringHelper.get("basedir");
	String content = CommonUtil.loadFileContent(baseDir+".classpath");
	Source c = new Source(content);
	List<Element> elements = c.getAllElements("classpathentry");
	sb.append(" ./ ");
	for(Element ele : elements){
	String kind = ele.getAttributeValue("kind");
	String cp = ele.getAttributeValue("path");
	if(!kind.equals("src")){
	sb.append(" "+"./" +cp+SystemUtils.LINE_SEPARATOR);
	}
	}
	return sb.toString();
}
public static void main(String [] args){
	String sptr = SystemUtils.FILE_SEPARATOR;
	String mainClass = "org.ptg.tests.RunStaticGraph";
	StringBuilder sb = new StringBuilder();
	sb.append("Manifest-Version: 1.0"+SystemUtils.LINE_SEPARATOR);
	sb.append("Class-Path: ");
	sb.append(buildPath());
	sb.append("Main-Class: "+mainClass+SystemUtils.LINE_SEPARATOR);
	String baseDir = (String)SpringHelper.get("basedir");
	CommonUtil.writeFile(baseDir+sptr+"extra"+sptr+"META-INF"+sptr+"MANIFEST.MF", sb.toString());
}
}
