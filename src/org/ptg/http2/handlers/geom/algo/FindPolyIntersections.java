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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 */
public class FindPolyIntersections extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		GeometryFactory f = new GeometryFactory();
		String pts = request.getParameter("pts");
		int sx = (int) Double.parseDouble(request.getParameter("sx"));
		int sy = (int) Double.parseDouble(request.getParameter("sy"));
		int ex = (int) Double.parseDouble(request.getParameter("ex"));
		int ey = (int) Double.parseDouble(request.getParameter("ey"));
		int mx = (int) Double.parseDouble(request.getParameter("mx"));
		int my = (int) Double.parseDouble(request.getParameter("my"));
		int nx = (int) Double.parseDouble(request.getParameter("nx"));
		int ny = (int) Double.parseDouble(request.getParameter("ny"));
		String self = request.getParameter("self");
        int pw = 32/2;//is devided by 2
        int ph = 32/2;//is devided by 2 
		StringBuilder sb = new StringBuilder();
		List<Polygon> polys = new LinkedList<Polygon>();
		List<String> ids = new LinkedList<String>();
		JSONArray o = JSONArray.fromObject(pts);
		try {
			for(int i = 0;i<o.size();i++){
				List<Coordinate> cs = new ArrayList<Coordinate>();
				JSONObject jo = (JSONObject) o.get(i);
				double x = jo.getDouble("x");
				double y = jo.getDouble("y");
				String id = jo.getString("id");
				Coordinate c = new Coordinate(x-pw,y-ph,0);cs.add(c);
				c = new Coordinate(x+pw,y-ph,0);cs.add(c);
				c = new Coordinate(x+pw,y+ph,0);cs.add(c);
				c = new Coordinate(x-pw,y+ph,0);cs.add(c);
				c = new Coordinate(x-pw,y-ph,0);cs.add(c);
				LinearRing lr = f.createLinearRing(cs.toArray(new Coordinate[0]));
				Polygon poly = f.createPolygon(lr, null);
                polys.add(poly);
                ids.add(id);
			}
			List<LinearRing> lines = new ArrayList<LinearRing>();
				Coordinate c1 = new Coordinate(sx,sy);
				Coordinate c2 = new Coordinate(ex,ey);
				LineString ls = f.createLineString(new Coordinate[]{c1,c2});
				
			int j=0;
			List<Integer> cuts = new LinkedList<Integer>();
			for(Polygon poly: polys){
				try {
					Geometry gm = poly.intersection(ls);
					if(gm!=null){
					Coordinate[] ints = gm.getCoordinates();
					if(ints!=null && ints.length>0){
						cuts.add(j);
					}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				j+=1;
			}
			System.out.println(sb.toString());
			List<String> rets = new LinkedList<String>();
			for(int id : cuts){
				String c  = ids.get(id);
				if(!c.equals(self)){
				JSONObject jo = (JSONObject) o.get(id);
				int x =(int) jo.getDouble("x");
				int y =(int) jo.getDouble("y");
				if(nx<mx){
					if((x>=mx)){
						rets.add(c);					
					}else{
						System.out.println(c + " is at another end of "+self);
						rets.add("-"+c);			
					}
				}else{
					if((x<=mx)){
						rets.add(c);					
					}else{
						System.out.println(c + " is at another end of "+self);
						rets.add("-"+c);			
					}
					
				}
				}

			}
			
			response.getOutputStream().print(CommonUtil.jsonFromCollection(rets));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}


