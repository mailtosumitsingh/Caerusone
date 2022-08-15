package org.ptg.http2.handlers.geom;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.models.Point;
import org.ptg.models.Shape;
import org.ptg.util.CommonUtil;
import org.ptg.util.CommonUtils;
import org.ptg.util.SpringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class SVGExtracterToShape extends AbstractHandler {
	String base = (String) SpringHelper.get("basedir");
	ShapeHelper shapeHelper = new ShapeHelper();
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String f = request.getParameter("f");
		int numofpoints= Integer.parseInt(request.getParameter("numofpoints"));
		String splitParam = request.getParameter("split");
		boolean removeLast = false;
		String inkscape = request.getParameter("inkscape");
		if(inkscape.equalsIgnoreCase("true")) removeLast = true;
		if(splitParam==null) splitParam = "true";
		boolean split = Boolean.parseBoolean(splitParam);
		GeometryFactory geometryFactory = new GeometryFactory();
		StringBuilder sb = new StringBuilder();
		String fileName = base + "uploaded/extraimages/" + f;
		List<String> paths = CommonUtils.extractSVGPaths(fileName);
		System.out.println("//number of paths: "+paths.size());
		if(split) {
			Shape[] shapes = new Shape[paths.size()];
			int i = 0;
		for (String shapeStrSVG : paths) {
			System.out.println("Now parsing:");
			System.out.println(shapeStrSVG);
			List<Point> cords = shapeHelper.toShapePoint( shapeStrSVG);
			if(removeLast )cords.remove(cords.size()-1);
			if(cords.size()>0) {
			Shape obj = shapeHelper.toShape(cords,  Maps.newHashMap());
			shapes[i]=obj;
			i++;
			}
		}
		response.getOutputStream().print(CommonUtil.jsonFromArray(shapes));
		}else {
			List<Point> cordsAll = Lists.newLinkedList();
			for (String shapeStrSVG : paths) {
				System.out.println("Now parsing:");
				System.out.println(shapeStrSVG);
				List<Point> cords = shapeHelper.toShapePoint( shapeStrSVG);
				if(removeLast )cords.remove(cords.size()-1);
				cordsAll.addAll(cords);
			}
			Shape big = shapeHelper.toShape(cordsAll,  Maps.newHashMap());
			response.getOutputStream().print(CommonUtil.toJson(big));
		}
		response.getOutputStream().flush();
		((Request) request).setHandled(true);
	}
}
