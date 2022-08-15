package org.ptg.tests.triangulation;

import java.util.List;

import org.ptg.util.CommonUtils;

public class GenerateSVG {
	public static void main(String[] args) throws Exception{
		String fname = "C:\\Users\\singh\\Desktop\\diagrams\\arrow1.png";
		List<String> svg = CommonUtils.extractSVG(fname);
		System.out.println(svg);
	}
}
