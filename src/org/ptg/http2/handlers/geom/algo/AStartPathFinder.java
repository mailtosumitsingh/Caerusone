/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.geom.algo;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.awt.DistVector;
import org.ptg.util.awt.Point;
import org.ptg.util.awt.Polygon;
import org.ptg.util.awt.Rectangle;
import org.ptg.util.model.PointRoute;

import cern.colt.matrix.impl.DenseObjectMatrix2D;

/**
 * 
 */
public class AStartPathFinder extends AbstractHandler {
	ServletOutputStream stream;

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		this.stream = response.getOutputStream();
		/****************************************************************************
		 * real point to location in matrix
		 ****************************************************************************/
		List<Point> toRem = new LinkedList<Point>();
		Map<Point, Point> pointToLoc = new HashMap<Point, Point>();
		List<Polygon> ps = new LinkedList<Polygon>();
		List<Polygon> bbRect = new LinkedList<Polygon>();
		List<PointRoute> ptrt = new LinkedList<PointRoute>();
		List<Integer> xx = new LinkedList<Integer>();
		List<Integer> yy = new LinkedList<Integer>();
		
		/**Process polygons first and extract the points**/
		String remStr = request.getParameter("rem");
		getPointsFromString(toRem,remStr);
		
		String polyStr = request.getParameter("polys");
		getPolygonsFromString(ps, polyStr);
		
		/**Process routes**/
		String routes = request.getParameter("routes");
		getRoutesFromString(ptrt, routes);
		List<Point> ignoreHoriz = new LinkedList<Point>();
		List<Point> ignoreVert = new LinkedList<Point>();
		for(PointRoute pr : ptrt){
			Point []pts = new Point[]{pr.getFrom(),pr.getTo()};
			for(Point f :pts ){
			if(f.getId()!=null ){
				String idval  = StringUtils.substringAfterLast(f.getId(), ".");
				int id = -1;
				try {
					id = Integer.parseInt(idval);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if(id>-1){
					if(id==1||id==5){
						ignoreVert.add(f);	
					}else if(id==3||id==7){
						ignoreHoriz.add(f);
					}
				}
			}
			}
		}
		
		/*get bounding polygons**/
		getBoundingPolys(ps, bbRect,10,10);
		addPolygonPointsToPoiMap(pointToLoc, bbRect);
		/** Draw bounding polygons*/
		drawPolygons(bbRect);
		
		/*Process pois**/
		addPolygonPts(pointToLoc, xx, yy);
		addPointsFromRoutes(ptrt, xx, yy);
		addSubPoints(xx, yy);
		sortAndUniq(xx);
		sortAndUniq(yy);
		drawLines(xx, yy);
	
		DenseObjectMatrix2D pts = new DenseObjectMatrix2D(yy.size(), xx.size());
		DenseObjectMatrix2D dist = new DenseObjectMatrix2D(yy.size(), xx.size());
		 
		try {
			boolean showPois = false;
			for (int i = 0; i < yy.size(); i++) {
				for (int j = 0; j < xx.size(); j++) {
					int y = yy.get(i);
					int x = xx.get(j);
					Point pt = new Point(x, y);
					boolean found = false;
					boolean showPointsOnPoly = false;
					findInPoly: 
				for (Polygon p : bbRect) {
						if (p.contains(pt)) {
							for (int k = 0; k < p.xpoints.length; k++) {
								int px = p.xpoints[k];
								int py = p.ypoints[k];
								if (pt.y == py && pt.x == px) {
									if(showPois){
										drawDoubleCircle(pt,2, "red");
									}
									break findInPoly;
								}else if(pointOnPoly(p,pt)){
									if(showPointsOnPoly){
										drawDoubleCircle(pt,10, "orange");
									}
									break findInPoly;
								}
								for (PointRoute rt : ptrt) {
									Point ix1 = (rt.getFrom());
									Point ix2 = (rt.getTo());
									if ((ix1 != null && pt.equals(ix1)) || (ix2 != null && pt.equals(ix2))) {
										if(showPois){
											drawDoubleCircle(pt,14, "red");
										}
										break findInPoly;
									}
								}
							}
							if(showPois){
							drawDoubleCircle(pt);
							}
							
							found = true;
							break;
						}
					}
					if(ignorePoint(pt,ignoreHoriz,ignoreVert)){
						int fcount = 0;
						for (Polygon p : bbRect) {
							if(pointOnPoly(p,pt)){
								fcount++;	
							}
						}
						if(fcount==1){
							if(showPois)
							drawDoubleCircle(pt,14, "magenta");
							found = true;
						}else{
							if(showPois)
							drawDoubleCircle(pt,14, "green");
							found = false;
						}

					}
					for(Point rp: toRem){
						if(rp.x == pt.x && rp.y ==pt.y){
							found = true;
							drawDoubleCircle(pt,10, "yellow");
						}
					}
					if (found == false) {
						if(showPois){
						drawDoubleCircle(pt,"blue");
						}
						pts.setQuick(i, j, pt);
						pointToLoc.put(pt, new Point(j, i));
					}
				}
			}

			drawRoutes(pointToLoc, ptrt, pts, dist);

		} catch (Exception e) {
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}



	private boolean ignorePoint(Point pt, List<Point> ignoreHoriz, List<Point> ignoreVert) {
		for(Point p: ignoreVert){
			if(Math.abs(pt.y - p.y)<1){
				if(Math.abs(pt.x - p.x)>1){
					return true;
				}
			}
		}
		for(Point p: ignoreHoriz){
			if(Math.abs(pt.x - p.x)<1){
				if(Math.abs(pt.y - p.y)>1){
					return true;
				}

			}
		}
		return false;
	}



	private boolean pointOnPoly(Polygon r, Point pt) { 
		for(int i=1;i<r.npoints;i++){
			Line2D.Double l = new Line2D.Double(r.xpoints[i-1],r.ypoints[i-1],r.xpoints[i],r.ypoints[i]);
			 double dist = l.ptLineDist(pt.x,pt.y);
			if(dist<4){
				 return true;
			 }
			}
		Line2D.Double l = new Line2D.Double(r.xpoints[3],r.ypoints[3],r.xpoints[0],r.ypoints[0]);
		 double dist = l.ptLineDist(pt.x,pt.y);
		if(dist<4){
			 return true;
		 }
		
		return false;
	}



	private void addPolygonPointsToPoiMap(Map<Point, Point> pointToLoc, List<Polygon> bbRect) {
		for(Polygon r: bbRect){
		for(int i=0;i<r.npoints;i++){
		pointToLoc.put(new Point(r.xpoints[i], r.ypoints[i]), null);
		}
		}
	}



	private void drawPolygons(List<Polygon> bbRect) {
		boolean drawPolys = false;
		if(drawPolys){
		for(Polygon poly:bbRect){
			drawPoly(poly);
		}
		}
	}



	private void addPolygonPts(Map<Point, Point> pointToLoc, List<Integer> xx, List<Integer> yy) {
		for (Point p : pointToLoc.keySet()) {
			xx.add(p.x);
			yy.add(p.y);
		}
	}



	private void addPointsFromRoutes(List<PointRoute> ptrt, List<Integer> xx, List<Integer> yy) {
		for (PointRoute rt : ptrt) {
			Point ix1 = (rt.getFrom());
			Point ix2 = (rt.getTo());
			xx.add(ix1.x);
			xx.add(ix2.x);
			yy.add(ix1.y);
			yy.add(ix2.y);

		}
	}



	private void drawLines(List<Integer> xx, List<Integer> yy) {
		boolean showLines  = false;
		if(showLines){
		String xs = xx.toString();
		String ys = yy.toString();
		sendToUI("hlines(" + ys + ");");
		sendToUI("vlines(" + xs + ");");
		}
	}



	private Set<Integer> sortAndUniq(List<Integer> ints) {
		Set<Integer> uniq = new HashSet<Integer>();
		uniq.clear();
		uniq.addAll(ints);
		ints.clear();
		ints.addAll(uniq);
		Collections.sort(ints);
		return uniq;
	}



	private void addSubPoints(List<Integer> xx, List<Integer> yy) {
		boolean addSubPoints = false;
		if(addSubPoints){
		for (int i = 0; i < 2000; i += 100) {
			xx.add(i);
			yy.add(i);
		}
		}
	}



	private void getBoundingPolys(List<Polygon> ps, List<Polygon> bbRect,int xw,int yw) {
		for(Polygon p:ps){
			Polygon poly = getBoundingPoly(xw, yw, p);
			bbRect.add(poly);
		}
	}



	private Polygon getBoundingPoly(int xw, int yw, Polygon p) {
		Rectangle r = p.getBoundingBox();
		Polygon poly = rectToPoly(r,xw,yw);
		return poly;
	}



	private void getRoutesFromString(List<PointRoute> ptrt, String routes) {
		JSONArray rtArray = JSONArray.fromObject(routes);
		for (int i = 0; i < rtArray.size(); i++) {
			JSONObject jo = (JSONObject) rtArray.get(i);
			JSONObject from = jo.getJSONObject("from");
			JSONObject to = jo.getJSONObject("to");
			PointRoute rt = new PointRoute();
			Point f = new Point((int) from.getDouble("x"), (int) from.getDouble("y"), from.getString("id"));
			Point t = new Point((int) to.getDouble("x"), (int) to.getDouble("y"), to.getString("id"));
			rt.setFrom(f);
			rt.setTo(t);
			ptrt.add(rt);
			System.out.println("Found one routes" + f + ">>" + t);
		}
	}
	private void getPointsFromString(List<Point> pt, String routes) {
		JSONArray rtArray = JSONArray.fromObject(routes);
		for (int i = 0; i < rtArray.size(); i++) {
			JSONObject jo = (JSONObject) rtArray.get(i);
			Point f = new Point((int) jo.getDouble("x"), (int) jo.getDouble("y"));
			pt.add(f);
		}
	}


	private void getPolygonsFromString(List<Polygon> ps, String polyStr) {
		JSONArray polys = JSONArray.fromObject(polyStr);
		for (int i = 0; i < polys.size(); i++) {
			Polygon poly = new Polygon();
			JSONArray points = polys.getJSONArray(i);
			for (int j = 0; j < points.size(); j++) {
				JSONObject pt = (JSONObject) points.get(j);
				int y = pt.getInt("y");
				int x = pt.getInt("x");
				poly.addPoint(x, y);
			}
			ps.add(poly);
		}
	}



	private void drawRoutes(Map<Point, Point> pointToLoc, List<PointRoute> ptrt, DenseObjectMatrix2D pts, DenseObjectMatrix2D dist) {
		int i = 0;
		for (PointRoute rt : ptrt) {
			Point ix1 = pointToLoc.get(rt.getFrom());
			Point ix2 = pointToLoc.get(rt.getTo());
			boolean showPathVector = false;
			if (showPathVector) {
				sendToUI("pCanvas.line(" + rt.getFrom().x + "," + rt.getFrom().y + "," + rt.getTo().x + "," + rt.getTo().y + ");");
			}
			// dist = new DenseObjectMatrix2D(yy.size(), xx.size());
			calcuateDistMatrixV2(ix1, ix2, pts, dist);
			List<Point> path = getPath(ix1, ix2, pts, dist);
			StringBuilder sb = new StringBuilder();
			StringBuilder pstr = new StringBuilder();

			int count = 0;
			if (path.size() > 0) {
				pstr.append("var c =Raphael.getColor();pCanvas.path('");
				for (Point p : path) {
					if (count == 0) {
						pstr.append("M " + p.x + "," + p.y);
					} else if (count != path.size() - 1) {
						pstr.append(" L " + p.x + "," + p.y);
					} else {
						pstr.append(" L " + p.x + "," + p.y + "').attr('stroke-width',4).attr('stroke',Raphael.getColor());Raphael.getColor();Raphael.getColor();");
					}
					count++;
				}
				boolean debugPathpoints = false;
				if (debugPathpoints) {
					for (Point p : path) {
						pstr.append("pCanvas.circle(" + p.x + "," + p.y + ",5).attr('stroke',c);");
						count++;
					}
				}
			}
			i++;
			sendToUI(pstr.toString());
			//System.out.println(pts);
			//System.out.println(dist);
			boolean removeCommonPoints = false;
			if (removeCommonPoints) {
				for (int k = 1; k < path.size() - 1; k+=4) {
					Point p = path.get(k);
					p = pointToLoc.get(p);
					if (p != null) {
						for (PointRoute rt2 : ptrt) {
							Point ixs = (rt2.getFrom());
							Point ixe = (rt2.getTo());
							if (!((ixs != null && p.equals(ixs)) || (ixe != null && p.equals(ixe)))) {
								pts.setQuick(p.y, p.x, null);
							}
						}

					}
				}
			}
		}
	}

	

	private Polygon rectToPoly(Rectangle p, int wincr,int hincr) {
		p.x-=wincr/2;
		p.y-=hincr/2;
		p.width+=wincr;
		p.height+=hincr;
		
		Polygon ret = new Polygon();
		int [] xx = new int[4];
		 int [] yy = new int[4];
		 xx[0] = p.x;
		 xx[1] = p.x +p.width;
		 xx[2] = p.x +p.width;
		 xx[3] = p.x;
		 
		 yy[0] = p.y;
		 yy[1] = p.y ;
		 yy[2] = p.y +p.height;
		 yy[3] = p.y +p.height; 
		 for(int i=0;i<4;i++){
			 ret.addPoint(xx[i], yy[i]);
		 }
		 
		return ret;
	}



	private List<Point> getPath(Point ix1, Point ix2, DenseObjectMatrix2D pts, DenseObjectMatrix2D dist) {
		List<Point> path = new LinkedList<Point>();
		int count = 0;
		Point curr = ix2;
		int i = 0;
		while (count < pts.size() && !curr.equals(ix1)) {
			count++;
			DistVector dv = (DistVector) dist.get(curr.y, curr.x);
			if (dv != null) {
				Point pt = dv.getFrom();
				curr = pt;
				pt = (Point) pts.get(pt.y, pt.x);
				if (i == 0) {
					path.add((Point) pts.get(ix2.y, ix2.x));
				}
				path.add(pt);

			} else {
				// oops something wrong!!!!
				break;
			}
			i++;
		}

		return path;
	}

	private Point[] getNeighbours(Point ptrc, DenseObjectMatrix2D pts) {
		Point[] n = new Point[4];
		if (ptrc.y > 0) {
			Point pt4 = new Point(ptrc.x, ptrc.y - 1);
			n[0] = pt4;
		}
		if (ptrc.x < pts.columns() - 1) {
			Point pt2 = new Point(ptrc.x + 1, ptrc.y);
			n[1] = pt2;
		}
		if (ptrc.y < pts.rows() - 1) {
			Point pt3 = new Point(ptrc.x, ptrc.y + 1);
			n[2] = pt3;
		}
		if (ptrc.x > 0) {
			Point pt1 = new Point(ptrc.x - 1, ptrc.y);
			n[3] = pt1;
		}
		return n;
	}

	private boolean calcuateDistMatrixV2(Point ixrc1, Point ixrc2, DenseObjectMatrix2D pts, DenseObjectMatrix2D dist) {
		PriorityQueue<DistVector> pq = new PriorityQueue<DistVector>(pts.size(), new Comparator<DistVector>() {

			@Override
			public int compare(DistVector o1, DistVector o2) {
				return o1.getDistance() - o2.getDistance();
			}

		});
		for (int i = 0; i < pts.rows(); i++) {
			for (int j = 0; j < pts.columns(); j++) {
				if (pts.getQuick(i, j) != null) {
					Point p = new Point(j, i);
					DistVector dv = new DistVector(Integer.MAX_VALUE, p);
					pq.add(dv);
					dist.setQuick(p.y, p.x, dv);
				} else {
					//System.out.println("Point is blank at " + "(" + i + "," + j + ")");
				}
			}
		}
		DistVector dvs = new DistVector(0, ixrc1, ixrc1);
		dist.set(ixrc1.y, ixrc1.x, dvs);

		/*for (int i = 0; i < pts.rows(); i++) {
			for (int j = 0; j < pts.columns(); j++) {
				Point pt = (Point) pts.getQuick(i, j);
				if (pt != null) {
					sendToUI("pCanvas.circle(" + pt.x + "," + pt.y + "," +"8).attr('stroke','red');" + "pCanvas.circle(" + pt.x + "," + pt.y + "," + "6).attr('stroke','red');");
				}
			}
		}*/

		printDistMatrix(pts, dist);
		if (pq.contains(dvs)) {
			updatePriorityQueue(pq, dvs);
		} else {
			pq.add(dvs);
		}
		while (pq.size() > 0) {
			DistVector dv = pq.remove();// will be start first
			if (dv == null) {
				break;
			}
			Point curr = (Point) pts.get(dv.getPoint().y, dv.getPoint().x);
			Point[] nbrs = getNeighbours(dv.getPoint(), pts);
			for (Point pn : nbrs) {
				if (pn != null) {
					Point neigh = (Point) pts.getQuick(pn.y, pn.x);
					if (neigh != null) {
						DistVector dn = (DistVector) dist.getQuick(pn.y, pn.x);
						if (pq.contains(dn)) {/* if already removed donot do anything*/
							int d = (int) Math.sqrt((curr.x - neigh.x) * (curr.x - neigh.x) + (curr.y - neigh.y) * (curr.y - neigh.y));
							d = dv.getDistance() + d;
							if (d < dn.getDistance()) {
								dn.setDistance(d);
								dn.setFrom(dv.getPoint());
								updatePriorityQueue(pq, dn);
							}
						}
					} else {
						//System.out.println("Neighbour is null:" + pn.x + ":" + pn.y);
					}
				}
			}
			printDistMatrix(pts, dist);
		}
		printDistMatrix(pts, dist);
		System.out.println("Done computing distance");
		return false;
	}

	private void printDistMatrix(DenseObjectMatrix2D pts, DenseObjectMatrix2D dist) {
		/*
		 * DenseObjectMatrix2D res = new DenseObjectMatrix2D(dist.rows(),
		 * dist.columns()); for (int i = 0; i < pts.rows(); i++) { for (int j =
		 * 0; j < pts.columns(); j++) { DistVector d = (DistVector)
		 * dist.getQuick(i, j); if (d != null) { res.setQuick(i, j,
		 * d.getDistance()); } } } System.out.println(res);
		 */
	}

	private void updatePriorityQueue(PriorityQueue<DistVector> pq, DistVector dvs) {
		pq.remove(dvs);
		pq.add(dvs);
	}
	private void drawDoubleCircle(Point pt,int rad,String clr) {
		drawCircle(pt, rad,clr);
		String remPointStr = "removePointFromLayout("+pt.x+","+pt.y+");";
		sendToUI("c.click(function(evt) {console.log('( x:"+pt.x+" , y:"+pt.y+" )');"+remPointStr+"});");
		drawCircle(pt, rad+1,clr);
		sendToUI("c.click(function(evt) {console.log('( x:"+pt.x+" , y:"+pt.y+" )');"+remPointStr+"});");
	
		
	}
	private void drawDoubleCircle(Point pt,String clr) {
		drawDoubleCircle(pt,5,clr);
	}
	private void drawDoubleCircle(Point pt) {
		drawDoubleCircle(pt,"black");
	}

	private void drawCircle(Point pt,int radius,String clr) {
		String s = "var c = pCanvas.circle(" + pt.x + "," + pt.y +"," +radius+ ").attr('stroke','"+clr+"');";
		sendToUI(s);
	}
	private void sendToUI(String s) {
		try {
			stream.print(s);
			stream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void drawPoly(Polygon p){
		 String xs2 = Arrays.toString(p.xpoints);
		 String ys2 = Arrays.toString(p.ypoints);
		 sendToUI("var xx = " + xs2 + ";");
		 sendToUI("var yy = " + ys2 + ";");
		sendToUI("drawPolyGonXYArrays(xx,yy);");
	}
	private void drawRect(Rectangle p){
		 int [] xx = new int[4];
		 int [] yy = new int[4];
		 xx[0] = p.x;
		 xx[1] = p.x +p.width;
		 xx[2] = p.x +p.width;
		 xx[3] = p.x;
		 
		 yy[0] = p.y;
		 yy[1] = p.y ;
		 yy[2] = p.y +p.height;
		 yy[3] = p.y +p.height;
		 String xs2 = Arrays.toString(xx);
		 String ys2 = Arrays.toString(yy);
		 sendToUI("var xx = " + xs2 + ";");
		 sendToUI("var yy = " + ys2 + ";");
		sendToUI("drawPolyGonXYArrays(xx,yy);");
	}
}
