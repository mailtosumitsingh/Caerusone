package org.ptg.tests.SVGextractTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.batik.parser.PathParser;
import org.ptg.http2.handlers.cnc.CNCPathHandler;
import org.ptg.util.CommonUtils;

public class SVGExtract1 {
public static void main(String[] args) throws Exception {
	
	List<String> paths = CommonUtils.extractSVGPaths("C:/projects/CaerusOne/uploaded/extraimages/svg6.svg");
	for(String s: paths){
		System.out.println("Now parsing:");
		System.out.println(s);
		PathParser p = new PathParser();
		CNCPathHandler cncPathHandler = new CNCPathHandler();
		p.setPathHandler(cncPathHandler);
		p.parse(s);
	}
}
}
