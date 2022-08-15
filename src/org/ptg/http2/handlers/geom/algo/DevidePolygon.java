/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.geom.algo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.awt.Rectangle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 */
public class DevidePolygon extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String region = request.getParameter("poly");
		String box = request.getParameter("box");
		int parts = 10;
		double [] ret = new double[parts*4+4];
		List<Coordinate> cs = new ArrayList<Coordinate>();
		StringBuilder sb = new StringBuilder();
		try {
			JSONObject bx = JSONObject.fromObject(box);
			JSONArray o = JSONArray.fromObject(region);
			for(int i = 0;i<o.size();i++){
				JSONObject jo = (JSONObject) o.get(i);
				double x = jo.getDouble("x");
				double y = jo.getDouble("y");
				Coordinate c = new Coordinate(x,y,0);
				cs.add(c);
			}
			
			Collections.sort(cs,new Comparator<Coordinate>(){
				@Override
				public int compare(Coordinate o1, Coordinate o2) {
					return 1;
				}});
		/*	Collections.sort(cs,new Comparator<Coordinate>(){
				@Override
				public int compare(Coordinate o1, Coordinate o2) {
					if(o1.x==o2.x)return 0;
					else
					return (o1.x-o2.x)>0?1:-1;
				}});
		*/
			/*JSONObject jo = (JSONObject) o.get(0);
			double x = jo.getDouble("x");
			double y = jo.getDouble("y");
			Coordinate c = new Coordinate(x,y,0);
			cs.add(c);*/
			cs.add(cs.get(0));
			GeometryFactory f = new GeometryFactory();
			LinearRing lr = f.createLinearRing(cs.toArray(new Coordinate[0]));
			Polygon poly = f.createPolygon(lr, null);
			Rectangle rect = new Rectangle();
			Coordinate []cos  = lr.getCoordinates();
			for(int j=0;j<cos.length;j++){
				if(j==0){
					sb.append("pCanvas.path(\"M");
					sb.append(cos[j].x);
					sb.append(",");
					sb.append(cos[j].y);
				}else{
					sb.append("L");
					sb.append(cos[j].x);
					sb.append(",");
					sb.append(cos[j].y);
				}
			}{
				sb.append("z\");\n");
			
			}
			rect.setBounds((int)bx.getDouble("x"),(int) bx.getDouble("y"),(int) bx.getDouble("width"),(int)bx.getDouble("height"));
			List<LinearRing> lines = new ArrayList<LinearRing>(parts);
			Envelope env = poly.getEnvelopeInternal();
			double bbx = env.getMinX();
			double bby = env.getMinY();
			double w = env.getWidth();
			double delta = w/parts;
			double h = env.getHeight();
			Coordinate clt = new Coordinate(bbx+w+delta,bby-10);
			Coordinate clb = new Coordinate(bbx+w+delta,bby+h+10);
			for(int i=0;i<=parts;i++){
				Coordinate c1 = new Coordinate(bbx+i*delta,bby-10);
				Coordinate c2 = new Coordinate(bbx+i*delta,bby+h+10);
				LinearRing ls = f.createLinearRing(new Coordinate[]{c1,clt,clb,c2,c1});
				Polygon pg = f.createPolygon(ls, null);
				sb.append("pCanvas.path(\"M"+c1.x+","+c1.y+"L"+clt.x+","+clt.y+"L"+clb.x+","+clb.y+"L"+c2.x+","+c2.y+"z\").attr(\"stroke\",Raphael.getColor());\n");
				lines.add(ls);
			}
			int j=0;
			for(LinearRing ls: lines){
				try {
					Geometry gm = poly.intersection(ls);
					Coordinate[] ints = gm.getCoordinates();
					if(ints!=null && ints.length>0){
						Arrays.sort(ints,new Comparator<Coordinate>(){
							@Override
							public int compare(Coordinate o1, Coordinate o2) {
								return (int)(o1.y-o2.y);
							}});
						ret[j] = ints[0].x;
						ret[j+1] = ints[0].y;
						ret[j+2] = ints[ints.length-1].x;
						ret[j+3] = ints[ints.length-1].y;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				j+=4;
			}
			System.out.println(sb.toString());
			response.getOutputStream().print(CommonUtil.jsonFromArray(ret));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
