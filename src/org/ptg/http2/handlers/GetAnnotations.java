/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.models.Shape;
import org.ptg.util.CommonUtil;
import org.ptg.util.awt.BBox;
import org.ptg.util.awt.Rectangle;
import org.ptg.util.mapper.v2.FPGraph2;

public class GetAnnotations extends AbstractHandler {
	boolean escape = CommonUtil.escapeJavaScriptInSql();

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			response.setHeader("Access-Control-Allow-Origin", "*");
		try {

			String ip = request.getRemoteAddr();
			String name = request.getParameter("name");
			String  bbox = request.getParameter("bbox");
			FPGraph2 fp2 = CommonUtil.buildDesignMappingGraph2(name);
			Map<String, Shape> shapes = fp2.getShapes();
			if(bbox ==null) {
			String ret  = CommonUtil.jsonFromArray(shapes.values());
			response.getWriter().write(ret);
			}else {
				List<BBox> rectangles = new ArrayList<>();
				for(Shape sh : shapes.values()) {
					BBox r = new BBox();
					r.x = sh.getX();
					r.y = sh.getY();
					r.b = sh.getB();
					r.r= sh.getR();
					r.id  = sh.getId();
					rectangles.add(r);
 				}
				String ret  = CommonUtil.jsonFromArray(rectangles);
				response.getWriter().write(ret);

			}

		} catch (Exception e) {
			response.getOutputStream().print("Document cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
