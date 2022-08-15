package org.ptg.http2.handlers.geom;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.parser.PathParser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.handlers.cnc.CNCPathHandler;
import org.ptg.tests.SVGextractTest.CubicCurve;
import org.ptg.tests.SVGextractTest.IShape;
import org.ptg.tests.SVGextractTest.Line;
import org.ptg.util.CommonUtil;
import org.ptg.util.CommonUtils;
import org.ptg.util.SpringHelper;

import com.google.common.collect.Lists;

public class SVGExtracter extends AbstractHandler {
	String base = (String) SpringHelper.get("basedir");

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String f = request.getParameter("f");
		String split = request.getParameter("s");
		String pts = request.getParameter("pts");
		String fileName = base + "uploaded/extraimages/" + f;
		List<String> paths = CommonUtils.extractSVGPaths(fileName);
		List<String> pathSplits = Lists.newLinkedList();
		for (String s : paths) {
			System.out.println("Now parsing:");
			System.out.println(s);
			PathParser p = new PathParser();
			CNCPathHandler cncPathHandler = new CNCPathHandler();
			p.setPathHandler(cncPathHandler);
			p.parse(s);
			if (split != null) {
				for (IShape shape : cncPathHandler.getShape().getShapes()) {
					if (shape.getType().equalsIgnoreCase("C")) {
						CubicCurve cc = (CubicCurve) shape;
						System.out.println("pCanvas.circle( " + cc.getS().getX() + ", " + cc.getS().getY() + " ,6);");
						System.out.println("var r = pCanvas.circle( " + cc.getC1().getX() + " , " + cc.getC1().getY() + " ,9);r.attr(\"stroke\",\"red\");");
						System.out.println("r = pCanvas.circle( " + cc.getC2().getX() + " , " + cc.getC2().getY() + " ,9);r.attr(\"stroke\",\"red\");");
						System.out.println("pCanvas.circle( " + cc.getE().getX() + " , " + cc.getE().getY() + " ,6 );");
						pathSplits.add(cc.toString());

					}
					if (shape.getType().equalsIgnoreCase("L")) {
						Line cc = (Line) shape;
						System.out.println("pCanvas.circle( " + cc.getS().getX() + ", " + cc.getS().getY() + " ,6);");
						System.out.println("pCanvas.circle( " + cc.getE().getX() + " , " + cc.getE().getY() + " ,6);");
						pathSplits.add(cc.toString());
					}
				}
			}else{
				if(pts!=null){
					//from pts;
				}
			}
		}
		if(split!=null){
			response.getOutputStream().print(CommonUtil.jsonFromArray(pathSplits));
		}else{
			response.getOutputStream().print(CommonUtil.jsonFromArray(paths));
		}
		response.getOutputStream().flush();
		((Request) request).setHandled(true);
	}
}
