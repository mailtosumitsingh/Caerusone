package org.ptg.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.batik.parser.PathParser;
import org.ptg.http2.handlers.cnc.CNCPathHandler;
import org.ptg.models.Point.PointType;
import org.ptg.tests.SVGextractTest.IShape;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;

public class Shape {
	private String id;
	private String type;
	private Object xref;
	private boolean visible;
	private String compName;
	private String shapeType ;
	private List<Facet> facets = Lists.newLinkedList();
	private List<Point> points = Lists.newLinkedList();
	List<String> layer  = new ArrayList<String>();
	private Map<String,String>data=Maps.newHashMap();
	private List<Point>pts = Lists.newLinkedList();
	
	public List<String> getLayer() {
		return layer;
	}

	public void setLayer(List<String> layer) {
		this.layer = layer;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getShapeType() {
		return shapeType;
	}

	public void setShapeType(String shapeType) {
		this.shapeType = shapeType;
	}

	public List<Point> getPts() {
		return pts;
	}

	public void setPts(List<Point> pts) {
		this.pts = pts;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getXref() {
		return xref;
	}

	public void setXref(Object xref) {
		this.xref = xref;
	}
		
	public List<Facet> getFacets() {
		return facets;
	}

	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}

	public void addFace(Facet face) {
		this.facets.add(face);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public List<Point> getPoints() {
		return points;
	}

	public void addPoint(Point point) {
		this.points.add(point);
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public List<Point> getFacetAsPoint(int facetIndex){
		Map<String, Point> pmap = getPointMap();
		List<Point> cp = Lists.newLinkedList();
		Facet f = getFacets().get(0);
		Connector cc = f.getConnectors().get(0);
		for(String s: cc.getPts()) {
			cp.add(pmap.get(s));
		}
		return cp;
	}

	public Map<String, Point> getPointMap() {
		Map<String, Point> pmap = Maps.newHashMap();
		for (Point p : getPoints()) {
			pmap.put(p.getId(), p);
		}
		return pmap;
	}
	public String getPathFromPointsArrayEx(List<Point > pts) {
		String pstr = "";
		boolean started = false;
		boolean isQuadCurve = false;
		boolean isCubicCurve = false;
		for (int i = 0; i < pts.size(); i++) {
			Point pt = pts.get(i);
			int ox = pt.getX();
			int oy = pt.getY();
			if (!started) {
				pstr += "M " + floatVal(ox) + " " + floatVal(oy) + " ";
				started = true;
			} else {
				if(isControlPoint(pts.get(i))){
					if(pts.size()-i>1){
						if(isControlPoint(pts.get(i+1))){
							//bezier curve
							if(isCubicCurve||isQuadCurve){
								pstr += " " +floatVal( ox) + " " + floatVal( oy ) + " ";
							}else{
								pstr += "C " + floatVal( ox )+ " " + floatVal( oy )+ " ";
							}
							isCubicCurve = true;	
						}else{
							if(isCubicCurve || isQuadCurve){
								pstr += "  " + floatVal( ox )+ " " + floatVal( oy )+ " ";
							}else{
								//quadtratic curve	
								pstr += "Q " + floatVal( ox )+ " " + floatVal( oy )+ " ";
								isQuadCurve = true;
							}
						}
					}else {
							if(isQuadCurve||isCubicCurve){
								pstr += "  " + floatVal( ox )+ " " + floatVal( oy );
								pstr += "  " + floatVal( pts.get(0).getX() )+ " " + floatVal( pts.get(0).getY() );
							}else{
								pstr += " Q " + floatVal( ox ) + " " + floatVal( oy );
								pstr += "  " + floatVal( pts.get(0).getX() )+ " " + floatVal( pts.get(0).getY() );
							}
					}
				}else {
					if(isQuadCurve||isCubicCurve){
						pstr += "  " + floatVal( ox ) + " " + floatVal( oy );
						isQuadCurve = false;
						isCubicCurve = false;
					}else{
					pstr += " L " + floatVal( ox )+ " " + floatVal( oy );
					}
				}
			}
		}
		return pstr;
	}

	public String getSVGString(int facet) {
		String s  = getPathFromPointsArrayEx(getFacetAsPoint(facet));
		return s;
	}
	private int floatVal(int ox) {
		return ox;
	}
	public boolean isControlPoint(Point pt){
		if(pt.getPointType()!=null &&(pt.getPointType().equals(PointType.Control)||pt.getPointType().equals(PointType.control))){
			return true;
		}
		return false;
	}
	public CNCPathHandler getSVGParseResult(int facet) {
		PathParser p = new PathParser();
		CNCPathHandler cncPathHandler = new CNCPathHandler();
		p.setPathHandler(cncPathHandler);
		p.parse(getSVGString(facet));
		return cncPathHandler;
	}
	public List<String> getPathSplits(CNCPathHandler cncPathHandler) {
		List<String> pathSplits = Lists.newLinkedList();
		for (IShape shape : cncPathHandler.getShape().getShapes()) {
				pathSplits.add(shape.toString());
		}
		return pathSplits;
	}
	public List<Coordinate> getCoordinates(CNCPathHandler cncPathHandler) {
		return cncPathHandler.getCoordinates();
	}
	public long map(long x, long in_min, long in_max, long out_min, long out_max)
	{
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	//todo add a value amp function so we can use the shape as mapp
}