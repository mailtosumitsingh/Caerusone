package org.ptg.cnc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ptg.velocity.VelocityHelper;

import com.vividsolutions.jts.geom.Coordinate;

public class GCodeHelper {


 Map<String, SortStrategy> sortStrategies = new HashMap<String,SortStrategy>();
final  BigDecimal offsetx, offsety,modelheightmm ,modelheightinch ,iterstepmm,iterstepinch  ;
private BigDecimal iter;
private BigDecimal depth;
private BigDecimal modelheight;
private BigDecimal toolSize;
private String unit;

{
	sortStrategies.put("default",new XYSortStrategy());
}

public GCodeHelper(String unit,BigDecimal offsetx, BigDecimal offsety,BigDecimal modelheightmm ,BigDecimal modelheightinch ,
		BigDecimal iterstepmm,BigDecimal iterstepinch,BigDecimal depth, BigDecimal modelheight,BigDecimal iter, BigDecimal toolSize) {
	this.offsetx=offsetx;
	this.offsety=offsety;
	this.modelheightmm=modelheightmm;
	this.modelheightinch =modelheightinch;
	this.iterstepmm=iterstepmm;
	this.iterstepinch=iterstepinch;
	this.depth  = depth;
	this.iter=iter;
	this.modelheight = modelheight;
	this.toolSize = toolSize;
	this.unit = unit;
}


public  List<? extends CNCElement> sort(List<? extends CNCElement> holes,String strategy){
	SortStrategy st = sortStrategies.get(strategy);
	return st.sort(holes);
}

public String getDrillGcode(CNCElement d, String material,int pixelsPerUnit,double depth,int preDrillWait,int postDrillWait, int feedRate){
	Map<String,Object> params = new HashMap<String,Object>();
	BigDecimal x = new BigDecimal(d.getNormalizedx());
	x = x.setScale(2,RoundingMode.HALF_EVEN);
	x = x.divide(new BigDecimal(pixelsPerUnit));
	BigDecimal y = new BigDecimal(d.getNormalizedy());
	y = y.setScale(2,RoundingMode.HALF_EVEN);
	y = y.divide(new BigDecimal(pixelsPerUnit));
	x= x.subtract(offsetx);
	y= y.subtract(offsety);
	if(d.getData().get("overridexy")!=null && d.getData().get("overridexy").equals("true")) {
		x = new BigDecimal(d.getData().get("x"));
		y = new BigDecimal(d.getData().get("y"));
		x = x.setScale(2,RoundingMode.HALF_EVEN);
		y = y.setScale(2,RoundingMode.HALF_EVEN);
	}
	params.put("X", x);
	params.put("Y", y);
	params.put("Z", depth);
	params.put("PREE_DRILL_WAIT",preDrillWait);
	params.put("POST_DRILL_WAIT",postDrillWait);
	params.put("FEED_RATE",feedRate);
	params.put("COMMENT",d.getText());
	String val = d.getData().get("feedRate");
	if(val==null) val = "100";
	String rval = d.getData().get("feedRateRetract");
	if(rval==null) rval = "100";
	BigDecimal drillFeedRate = new BigDecimal(val);
	String val3 = d.getData().get("iter");
	if(val3==null)
		val3=""+depth;
	BigDecimal iter = new BigDecimal(val3);
	if(!unit.equals("mm")) {
		iter = iter.multiply(new BigDecimal("0.0393701"));
	}
	String modelTop = d.getData().containsKey("modeltop")?d.getData().get("modeltop"):"1";
	BigDecimal modelheight = new BigDecimal(modelTop);
	BigDecimal cd = new BigDecimal(modelTop);
	cd = cd.setScale(2,RoundingMode.HALF_EVEN);
	StringBuilder code = new StringBuilder();
	double lastDepth = 0;
	while(cd.doubleValue()<depth) {
	cd = cd.setScale(2,RoundingMode.HALF_EVEN);
	String format = "G01 F"+drillFeedRate.intValue()+" x"+x+" y-"+y+" z-"+cd.doubleValue()+"\n";
	lastDepth = cd.doubleValue();
	code.append(format);
	format = "G01 F"+d.getData().get("feedRateRetract")+" x"+x+" y-"+y+" z-"+modelheight+"\n";
	code.append(format);
	cd =cd.add(iter);
	}
	if(depth-lastDepth>0) {
		String format = "G01 F"+drillFeedRate.intValue()+" x"+x+" y-"+y+" z-"+depth;
		code.append(format);
	}
	params.put("code",code);
	StringBuffer res = VelocityHelper.burnTemplate(params, "/gcodes/drill.vm");

	return res.toString();
}
public  String getAbsDrillGcodeFile(String s,String units){
	Map<String,Object> params = new HashMap<String,Object>();
	params.put("gcode", s);
	StringBuffer res = VelocityHelper.burnTemplate(params, "/gcodes/gABStemplate-"+units+".vm");
	return res.toString();
}
public String getMoveToGcode(Coordinate coor,String material,int pixelsPerInch,int feedRate, double depth){
	BigDecimal x = new BigDecimal(coor.x/pixelsPerInch);
	x = x.setScale(2,RoundingMode.HALF_EVEN);
	BigDecimal y = new BigDecimal(coor.y/pixelsPerInch);
	y = y.setScale(2,RoundingMode.HALF_EVEN);
	x= x.subtract(offsetx);
	y= y.subtract(offsety);
	String s = "G01 F"+feedRate+" x"+" "+x.toString()+"  y -"+y.toString()+" z -"+depth+"\n";
	return s;
}
public  String cutCircle(int cxx, int cyy, CNCElement e,int pixelsPerInch){

	BigDecimal d = depth.divide(new BigDecimal(pixelsPerInch));
	d = d.setScale(2,RoundingMode.HALF_EVEN);
	BigDecimal r = new BigDecimal(e.getData().get("radius"));
	r = r.setScale(2,RoundingMode.HALF_EVEN);
	BigDecimal toolSize =  new BigDecimal(e.getData().get("toolsize"));
	r=r.subtract(toolSize.multiply(new BigDecimal(1)));
	
	//compute x, y
	BigDecimal cx = new BigDecimal(cxx/pixelsPerInch);
	BigDecimal cy = new BigDecimal(cyy/pixelsPerInch);

	if(e.getData().get("overridexy").equals("true")) {
		cx = new BigDecimal(e.getData().get("x"));
		cy = new BigDecimal(e.getData().get("y"));
		cx = cx.setScale(2,RoundingMode.HALF_EVEN);
		cx = cx.subtract(r);
		cy = cy.setScale(2,RoundingMode.HALF_EVEN);
		
	}else {
		cx = cx.setScale(2,RoundingMode.HALF_EVEN);
		cy = cy.setScale(2,RoundingMode.HALF_EVEN);
		cx = cx.subtract(offsetx);
		cx = cx.subtract(r);
		cy = cy.subtract(offsety);
	}
	BigDecimal height = new BigDecimal(e.getData().get("depth"));
		StringBuilder sb = new StringBuilder();
	BigDecimal modelheight = new BigDecimal(e.getData().get("modeltop"));

	sb.append("G90 "+"\n");
	sb.append("G01 F"+e.getData().get("feedRate")+"\n");
	sb.append("G00 X"+cx+" y-"+cy+" Z-0"+"\n");
	sb.append("G00 X"+cx+" y-"+cy+" Z-"+modelheight.setScale(2,RoundingMode.HALF_EVEN).toString()+"\n");
	BigDecimal currZ = modelheight;
	currZ = currZ.setScale(2, RoundingMode.HALF_EVEN);
	BigDecimal increment = new BigDecimal(e.getData().get("iter"));
	increment.setScale(2, RoundingMode.HALF_EVEN);
	while (currZ.compareTo(height) <= 0) {
		sb.append("G00 X"+cx+" y-"+cy+" Z-"+currZ.toString()+"\n");
		sb.append("G02 X"+cx+" y-"+cy+" I"+r+" "+"\n");
		currZ = currZ.add(increment);
	}
	sb.append("G00 X"+cx+" y-"+cy+" Z-0"+"\n");

	
	return sb.toString();
}
public  String cutCircle(CNCElement e, String matAlum14,int pixelsPerInch) {
	return cutCircle(e.getX(),e.getY(),e,pixelsPerInch);
}

}

