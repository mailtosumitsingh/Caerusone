package org.ptg.util.extra;

import java.util.List;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.SystemUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class BuildIds {
	public String buildPath() {
		StringBuilder sb = new StringBuilder();
		String baseDir = (String) SpringHelper.get("basedir");
		String content = CommonUtil.loadFileContent(baseDir + "/site/wide_5678/wide.html.jsp");
		Source c = new Source(content);
		Pattern p = Pattern.compile("[a-zA-Z0-9]+[ ]*\\([ ]*\\)[ ]*[;]*");
		List<Element> elements = c.getAllElements("div");
		sb.append("."+SystemUtils.FILE_SEPARATOR+SystemUtils.PATH_SEPARATOR);
		for (Element ele : elements) {
			String type=ele.getAttributeValue("dojoType");
			if(type!=null && type.equals("dijit.MenuItem")){
			String id = ele.getAttributeValue("id");
			String onclick = ele.getAttributeValue("onClick");
			System.out.println("ID: "+id +" onclick : "+onclick);
			//creatematcher here and match
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		BuildIds b = new BuildIds();
		b.buildPath();
	}
}
