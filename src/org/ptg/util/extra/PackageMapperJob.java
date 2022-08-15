package org.ptg.util.extra;

import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class PackageMapperJob {
public static void main(String[] args) throws Exception {
	String base = (String) SpringHelper.get("basedir");
	String path = base + "tests/org/ptg/tests/";
	String jobfile = "MapperEmbed";
	String name = "MapperEmbed";
	String eventType = "ToDoEvent";

	CommonUtil.compileMapperJobPackage(path, jobfile, name, eventType);
}


}
