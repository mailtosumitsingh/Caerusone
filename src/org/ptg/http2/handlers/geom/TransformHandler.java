/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.geom;

import java.io.IOException;
import java.math.BigDecimal;

import javax.media.j3d.Transform3D;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.Point3D;
import org.ptg.util.Transform3DMine;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TransformHandler extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String pts = request.getParameter("toSave");
		JSONObject oo = JSONObject.fromObject(pts);
		StringBuilder sb  = new StringBuilder();
		JSONArray o = JSONArray.fromObject(oo.get("data"));
		try {
			for(int i = 0;i<o.size();i++){
				JSONObject jo = (JSONObject) o.get(i);
				if(jo.getString("type").equals("ShapeShape")) {
					JSONArray points = JSONArray.fromObject(jo.get("pts"));
					int mx =0,my=0,mz=0;
					for(int k = 0;k<points.size();k++){
						JSONObject point = (JSONObject) points.get(k);
						int x = new BigDecimal(point.getString("x")).intValue();
						int y = new BigDecimal(point.getString("y")).intValue();
						int z = new BigDecimal(point.getString("z")).intValue();
						mx+=x;
						my+=y;
						mz+=z;
					}
					mx = mx/points.size();
					my = my/points.size();
					mz = mz/points.size();
					//tf.rotX(Math.toRadians(90));
					for(int k = 0;k<points.size();k++){
						JSONObject point = (JSONObject) points.get(k);
						int x = new BigDecimal(point.getString("x")).intValue();
						int y = new BigDecimal(point.getString("y")).intValue();
						int z = new BigDecimal(point.getString("z")).intValue();
						Point3d p3 = new Point3d(x-mx,y-my,z-mz);
						Transform3D tf = new Transform3D();
						//tf.setTranslation(new Vector3d(mx,my,mz));
						tf.setScale(.5);
						tf.transform(p3);
						Transform3DMine tf2 = new Transform3DMine();
						p3.setX(p3.getX()+mx);
						p3.setY(p3.getY()+my);
						p3.setZ(p3.getZ()+mz);

						org.ptg.util.Point3D pp = tf2.rotateAroundVector(new org.ptg.util.Point3D(p3.getX(),p3.getY(),p3.getZ()),new Point3D(mx,my,mz), (90));
						pp.setX(pp.getX());
						pp.setY(pp.getY());
						pp.setZ(pp.getZ());
						sb.append("pCanvas.circle(\""+(p3.getX())+"\","+"\""+(new BigDecimal(p3.getY()).intValue()+20)+"\",5);\n");
						sb.append("pCanvas.circle(\""+pp.getX()+"\","+"\""+(new BigDecimal(pp.getY()).intValue()+20)+"\",3);\n");
					}
					
				}
			}
		}catch(Exception exp) {
			exp.printStackTrace();
		}
		System.out.println(sb.toString());
		response.getOutputStream().print(sb.toString());
		((Request) request).setHandled(true);
	}

}
