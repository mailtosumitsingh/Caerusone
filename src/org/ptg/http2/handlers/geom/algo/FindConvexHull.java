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
import com.vividsolutions.jts.geom.MultiPoint;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 */
public class FindConvexHull extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		GeometryFactory f = new GeometryFactory();
		String pts = request.getParameter("pts");
        int pw = 2;//is devided by 2
        int ph = 2;//is devided by 2 
		StringBuilder sb = new StringBuilder();
		List<Coordinate> cs = new ArrayList<Coordinate>();
		JSONArray o = JSONArray.fromObject(pts);
		try {
			for(int i = 0;i<o.size();i++){
				JSONObject jo = (JSONObject) o.get(i);
				double x = jo.getDouble("x");
				double y = jo.getDouble("y");
				Coordinate c = new Coordinate(x-pw,y-ph,0);cs.add(c);
				c = new Coordinate(x+pw,y-ph,0);cs.add(c);
				c = new Coordinate(x+pw,y+ph,0);cs.add(c);
				c = new Coordinate(x-pw,y+ph,0);cs.add(c);
				c = new Coordinate(x-pw,y-ph,0);cs.add(c);
			}
			MultiPoint mp = f.createMultiPoint(cs.toArray(new Coordinate[0]));
			
			Geometry g = mp.convexHull();
			Coordinate [] ch = g.getCoordinates();
			JSONObject [] points = new JSONObject[ch.length];
			int i = 0;
			for(Coordinate c: ch){
				JSONObject jo = new JSONObject();
			jo.put("x", (int)ch[i].x);
			jo.put("y",(int)ch[i].y);
			points[i] = jo;
			i++;
			}
			response.getOutputStream().print(CommonUtil.jsonFromArray(points ));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}


