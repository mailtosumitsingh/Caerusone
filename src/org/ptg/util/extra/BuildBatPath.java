package org.ptg.util.extra;

import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.SystemUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class BuildBatPath {
	public static String buildPath() {
		StringBuilder sb = new StringBuilder();
		String baseDir = (String) SpringHelper.get("basedir");
		String content = CommonUtil.loadFileContent(baseDir + ".classpath");
		Source c = new Source(content);
		List<Element> elements = c.getAllElements("classpathentry");
		sb.append("."+SystemUtils.FILE_SEPARATOR+SystemUtils.PATH_SEPARATOR);
		for (Element ele : elements) {
			String kind = ele.getAttributeValue("kind");
			String cp = ele.getAttributeValue("path");
			if (!kind.equals("src")) {
				String pathStr = "."+SystemUtils.FILE_SEPARATOR + cp+SystemUtils.PATH_SEPARATOR;
				pathStr = pathStr.replace("/", SystemUtils.FILE_SEPARATOR);
				sb.append( pathStr);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String sptr = SystemUtils.FILE_SEPARATOR;
		String mainClass = "org.ptg.cluster.ClusterMem";//"org.ptg.tests.RunStaticGraph";
		StringBuilder sb = new StringBuilder();
		sb.append("java ");
		sb.append("-cp ");
		sb.append(buildPath() + " ");
		sb.append(mainClass );
		String baseDir = (String) SpringHelper.get("basedir");
		CommonUtil.writeFile(baseDir + sptr + "Run.bat" , sb.toString());
	}
}
