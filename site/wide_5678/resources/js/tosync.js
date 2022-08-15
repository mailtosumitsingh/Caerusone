function triangulate(){
	
}
function toDegrees (angle) {
	  return angle * (180 / Math.PI);
	}

function toRadians (angle) {
	  return angle * (Math.PI / 180);
	}
//////////////delete this
//printAngle(pData.data[0]S.facets[0].connectors[0].pts);
//printShapeAngle(pData.data[2]);
//printShapeAngle(pData.data[2]);
function getPointFromGraphShape(id,b){
  var c = b.pts;
  for(var i=0;i<c.length;i++){
      if(c[i].id==id){
      return c[i];
      }
  }
  return null;
}

function printAllShapeAngles(){
	for(var i=0;i<pData.data.length;i++){
		var item = pData.data[i];
		if(item.type=="ShapeShape"){
			printShapeAngle(item);
		}
	}
	
}
function printShapeAngle(b){
	var c = b.facets[0].connectors[0].pts;
	var pts = new Array();
	for(var i=0;i<c.length;i++){
	    var pid = c[i];
	    var pt = getPointFromGraphShape(pid,b);
	    if(pt!=null){
	    pts.push(pt);
	    }
	}
	var lines = new Array();
	for(var i=0;i<=pts.length-2;i++){
	var p1 = new jsts.geom.Coordinate(floatVal(pts[i].x),floatVal(pts[i].y))
	var p2 = new jsts.geom.Coordinate(floatVal(pts[i+1].x),floatVal(pts[i+1].y))
	var ls = new jsts.geom.LineSegment(p1,p2)
	lines.push(ls);
	}
	for(var i=0;i<lines.length;i++){
	var a1 = lines[i].angle()
	var d1 = toDegrees(a1);
	console.log("Angle: "+d1+" len: "+lines[i].getLength());
	var a = pCanvas.text(parseInt(lines[i].p1.x),parseInt(lines[i].p1.y-10),""+parseInt(d1));
	a.attr({ "font-size": 16, "font-family": "fontName, sans-serif" ,"fill":"yellow","stroke-width":3});
	 }
	}

function getShapePathForSimu(shape){

	Object.setPrototypeOf(shape, Shape.prototype)
	var facet = Object.setPrototypeOf(shape.facets[0], Facet.prototype)
	var connector= Object.setPrototypeOf(facet.connectors[0], Connector.prototype)

				var m = shape.getPointMap();
				var arr = new Array();
			    if(connector.pts.length<1)return;
			    for(var i=0;i<connector.pts.length;i++){
	                var pt = m[connector.pts[i]];
			    	arr.push(pt); 
	            }
	            var path = getPathFromPointsArrayEx(arr,true);
			    path =  path + "  Z ";
			    var pp = pCanvas.path(path);
			    var clr = Raphael.getColor();
			    pp.attr("stroke",clr);
			    pp.attr("fill","90-#00c6ff-#00ffff")
			    pp.attr("opacity",1);
	            return pp;
	 }

function syncWait(ms) {
    var start = Date.now(),
        now = start;
    while (now - start < ms) {
      now = Date.now();
    }
}
//movePath(pData.data[2],pData.data[3],10);
function movePath(s1,s2,nsim){
    var p1 = getShapePathForSimu(s1);
    var p2 =  getShapePathForSimu(s2);
   var l = p1.getTotalLength();
   console.log(l);
   var bb = p2.getBBox();
   var otx=bb.x+bb.w/2,oty=bb.y+bb.h/2;
  for(var i=0;i<nsim;i++){

   var pt = p1.getPointAtLength((l/nsim)*i);
   var mvx = pt.x-otx;
   var mvy = pt.y-oty;
   console.log(pt);

   pCanvas.circle(pt.x,pt.y,5);
   console.log(mvx+":"+mvy);
        p2.translate(mvx,mvy);
   p2.clone();
   otx=pt.x;
   oty=pt.y;
   }
}


function shapeToPoly(shape){
  var coordinates = new Array();
  Object.setPrototypeOf(shape, Shape.prototype)
    var geometryFactory = new jsts.geom.GeometryFactory()
	var facet = Object.setPrototypeOf(shape.facets[0], Facet.prototype)
	var connector= Object.setPrototypeOf(facet.connectors[0], Connector.prototype)
	var m = shape.getPointMap();
  var arr = new Array();
	if(connector.pts.length<1)return;
	for(var i=0;i<connector.pts.length;i++){
      var pt = m[connector.pts[i]];
		arr.push(pt); 
      coordinates.push(new jsts.geom.Coordinate(pt.x, pt.y))
  }
   coordinates.push(new jsts.geom.Coordinate( m[connector.pts[0]].x, m[connector.pts[0]].y))
  var shell = geometryFactory.createLinearRing(coordinates)
  var searchPolygon = geometryFactory.createPolygon(shell)
  return searchPolygon;
}

function testRobotCollision(){
	var models = new Array();
	var shapes = new Array();
	var robot = null;
	var robotPoly  =null;
	for(var i =0;i<pData.data.length;i++){
		var s = pData.data[i];
		if(s.type=="ShapeShape"){
			if(s.id.startsWith("model")){
				models.push(shapeToPoly(s));
				shapes.push(s);
			}
			if(s.id.startsWith("robot")){
				robot=s;
				robotPoly = shapeToPoly(s);
				
			}
		}		
	}
	var inter  = new Array();
	for(var i =0;i<models.length;i++){
	if(robotPoly!=null){
		if(robotPoly.intersects(models[i])){
			inter.push(shapes[i]);
		}
	}
	}
	return inter;	
}