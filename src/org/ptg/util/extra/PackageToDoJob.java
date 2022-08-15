package org.ptg.util.extra;

import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;

public class PackageToDoJob {
public static void main(String[] args) throws Exception {
	String base = (String) SpringHelper.get("basedir");
	String path = base + "tests/org/ptg/tests/";
	String jobfile = "RunToDo1";
	String name = "ToDo1";
	CommonUtil.compileToDoJobPackage(path, jobfile, name);
}

}
