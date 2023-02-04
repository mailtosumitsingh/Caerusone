var ptSelector = false;
var pullupmode = false;
var clonemode = false;
var currShapeFacet = null;
var currShapeConnector = null;
var bndryMoveMode=true;
var moveShapeMode = false;
var controlMode = false;
var defaultScaleX = .9;
var defaultScaleY = .9;
var defaultOffsetX = 5;
var defaultOffsetY = 5;


/*
 * pull up is implemented by drawing a shape with a facet
 * then when we pull up that facet we create a facet clone at dx
 * and then add n facets more where n is number of segments
 * then add pooints to that facet based on the original segments from increasing x then reverse virtual points!
 * the id of virtual points are created by id as original id + v_
 * 
 * */
class Point{
	constructor(x,y,z)
	{
		this.x= x;
		this.y = y;
		if(z==null)
			this.z=0;
		else
			this.z=z;
		this.id =getUniqId();
		this.pointType="Position";//Control
		this.type="ShapePoint";
	}
	static clonePoint(pt){
		var ret = new Point(pt.x,pt.y);
		ret.z = pt.z;
		ret.pointType = pt.pointType;
		ret.type = pt.type;
		ret.id = pt.id+"_v";
		return ret;
	}
	convertToPosition(){
		this.pointType = "Position";
	}
	convertToControl(){
		this.pointType = "Control";
	}
} 
class Connector{
   constructor(id)
   {
		this.id=id;
		this.pts = new Array();
		//StraightLine,Bezier,Quadratic,Arc,Manhattan,
		//StraightLineClose,BezierClose,QuadraticClose,ArcClose,ManhattanClose
		this.connectortype="StraightLineClose";
		this.type="ShapeConnector"
		this.opacity=.7;
		this.fill="90-#00c6ff-#0072ff"
	    this.stroke="black";
		this.visible = true;
	
   }
   static cloneConnector(c){
	   var cc = new Connector(c.id+"_v");
	   for(var i=0;i<c.pts.length;i++){
		   cc.addPoint(c.pts[i]+"_v");
	   }
	   return cc;
   }
	addPoint(ptid){
		var exists  = false;
		for ( var i = 0; i < this.pts.length; i++) {
			var obj = this.pts[i];
			if(obj.id==ptid)
				exists = true;
		}
		if(exists == false ){
			this.pts.push(ptid);
		}else{
			//if last pt == first pt!
			if(pts[0].id==ptid){
				this.pts.push(ptid);
			}
		}
	}
	addPoints(arr){
		for ( var i = 0; i < arr.length; i++) {
			var obj = arr[i];
			this.pts.push(obj);
		}
	}
	getPoints(){
		return this.pts;
	}
	addPointAfter(previd,prev2id,ptid){
	var pts2 = new Array();
	var f1 = false;
	var f2 = false;
	for ( var i = 0; i < this.pts.length; i++) {
		var obj = this.pts[i];
		if(obj==previd){
			f1 =true;
		}
		if(obj==prev2id){
			f2 =true;
		}
	}
	if(f1==false || f2 ==false) return; //only add if on same connector
		for ( var i = 0; i < this.pts.length; i++) {
			var obj = this.pts[i];
			if(obj==previd){
				if(i==this.pts.length-1){//reached last add after since we are doing point parts now!
					pts2.push(obj);
					pts2.push(ptid);
				}else{//not last
					if(this.pts[i+1]==prev2id){
						pts2.push(obj);
						pts2.push(ptid);
					}else{
						pts2.push(ptid);
						pts2.push(obj);
					}
				}
			}else{
				pts2.push(obj);
			}
		}
		this.pts = pts2;
	}
}
class Facet{
    constructor(id)
	{
		this.id=id;
	    this.connectors = new Array();
	    this.type="ShapeFacet";
		this.opacity=.7;
		this.fill="90-#00c6ff-#0072ff"
	    this.stroke="black";
		this.visible = true;
	}
    addConnector(conn){
    	this.connectors.push(conn);
    }
    deleteConnector(id){
		return removeItemFromArrayById(this.connectors,id);
		
	}
	createConnector(id){
		var c = new Connector(id);
		this.connectors.push(c);
		return c;
	}
	getPoints(){
		var ar = new Array();
		for(var i=0;i<this.connectors.length;i++){
			var c = this.connectors[i];
			Object.setPrototypeOf(c, Connector.prototype)
			var ar2  = c.getPoints();
			for(var j=0;j<ar2.length;j++){
				ar.push(ar2[j]);
			}
		}
		return ar;
	}
	addPointAfter(previd,prev2id,pid){
		for(var i=0;i<this.connectors.length;i++){
			var c = this.connectors[i];
			Object.setPrototypeOf(c, Connector.prototype)

			c.addPointAfter(previd,prev2id,pid);
		}
	}
	getConnector(id){
		for(var i=0;i<this.connectors.length;i++){
			var c = this.connectors[i];
			if(c.id==id)
				return c;
		}
		return null;
	}
}
class Shape{
	constructor(id)
	{
		this.id= id;
		this.pts = new Array();
		this.facets = new Array();
		this.type="ShapeShape"
		this.visible=true;
		this.compName="arbit"
		this.data={};
		this.data["close"]=true;
		this.data["tag"]="ImageMatch";
		
		this.shapeType="simple";//2d extruded
	}
	addPointToFacet(fid,prev,prev2,p){
		this.addPoint(p);
		var addtovolume = true;
		if(addtovolume){
			for(var i=0;i<this.facets.length;i++){
				var f = this.facets[i];
				Object.setPrototypeOf(f, Facet.prototype)
				f.addPointAfter(prev,prev2,p.id);
			}
		}else{
		var f = this.getFacet(fid);
		Object.setPrototypeOf(f, Facet.prototype)
		f.addPointAfter(prev,prev2,p.id);
		}
	}
	addPoint(p){
		this.pts.push(p);
	}
	addFacet(f){
		this.facets.push(f);
	}
	deleteFacet(id){
		return removeItemFromArrayById(this.facets,id);
		
	}
	deleteFacetEx(id){
		var fct = this.getFacet(id);
		Object.setPrototypeOf(fct, Facet.prototype)
		var ptstemp = fct.getPoints();
		removeItemFromArrayById(this.facets,id);
		var pts2 = new Array();
		
		for(var i=0;i<this.pts.length;i++){
			var id  = this.pts[i].id;
			var found = false;
			for(var j=0;j<ptstemp.length;j++){
				if(ptstemp[j]==id){
					found =true;
					break;
				}
			}
			if(!found)
			pts2.push(this.pts[i]);
		}
		this.pts =pts2;
	}
	createFacet(id){
		var f = new Facet(id);
		this.facets.push(f);
		return f;
	}
	movePoints(arr,dx,dy,dz){
		var m  = this.getPointMap();
		for ( var i = 0; i < arr.length; i++) {
			var point = m[arr[i]];
			if(point!=null){
				point.x = point.x+dx;
				point.y = point.y+dy;
				point.z = point.z+dz;
			}
		}
	}
	moveFacet(fid,dx,dy,dz){
		if(this.data["tdx"]!=null){
			this.data["tdx"] = parseInt(this.data["tdx"]) +parseInt(dx);
			this.data["tdy"] = parseInt(this.data["tdy"]) +parseInt(dy);
			this.data["tdz"] = parseInt(this.data["tdz"]) +parseInt(dz);
				}
		else{
			this.data["tdx"] = parseInt(dx);
			this.data["tdy"] = parseInt(dy);
			this.data["tdz"] = parseInt(dz);
		}
		var m  = this.getPointMap();
		for ( var i = 0; i < this.facets.length; i++) {
			var f = this.facets[i];
			if(f.id==fid){
			for(var j=0;j<f.connectors.length;j++){
				var c = f.connectors[j];
				for(var k=0;k<c.pts.length;k++){
					var pt = c.pts[k];
					var point = m[pt];
					if(point!=null){
						point.x = point.x+dx;
						point.y = point.y+dy;
						point.z = point.z+dz;
					}
				}
			}
		}
		}
	}
	moveAllPoints(myid,dx,dy,dz){
		if(this.data["tdx"]!=null){
			this.data["tdx"] = parseInt(this.data["tdx"]) +parseInt(dx);
			this.data["tdy"] = parseInt(this.data["tdy"]) +parseInt(dy);
			this.data["tdz"] = parseInt(this.data["tdz"]) +parseInt(dz);
				}
		else{
			this.data["tdx"] = parseInt(dx);
			this.data["tdy"] = parseInt(dy);
			this.data["tdz"] = parseInt(dz);
		}
		for ( var i = 0; i < this.pts.length; i++) {
			var point = this.pts[i];
			if(point!=null){
				point.x = point.x+dx;
				point.y = point.y+dy;
				point.z = point.z+dz;
			}
		}
		
	}
	cloneFacet(fid,dx,dy,dz){
		var cf = null;
		var m  = this.getPointMap();
		for ( var i = 0; i < this.facets.length; i++) {
			var f = this.facets[i];
			if(f.id==fid){
				cf = this.createFacet(f.id+"_"+getUniqId()+"_v");
				Object.setPrototypeOf(f, Facet.prototype)
				var ptoc = f.getPoints();
				for(var j=0;j<ptoc.length;j++){
					var ptt = m[ptoc[j]];
					var ptt2 = Point.clonePoint(ptt);
					ptt2.x=ptt2.x+dx;
					ptt2.y=ptt2.y+dy;
					ptt2.z=ptt2.z+dz;
					this.addPoint(ptt2);
				}
				for (var j=0;j<f.connectors.length;j++){
					var c = f.connectors[j];
					Object.setPrototypeOf(c, Connector.prototype)
					var cc = Connector.cloneConnector(c);
					cf.addConnector(cc);
				}
			}
		}
		return cf;
	}
	pullUpFacet(fid,dx,dy,dz){
		var cf = this.cloneFacet(fid,dx,dy,dz);
		var ptmap = this.getPointMap();
		for(var i=0;i<cf.connectors.length;i++){
			var c  = cf.connectors[i];
			var arr = c.getPoints();
			for(var j=0;j<arr.length;){ 
				var ar2 = new Array();
				if(j<arr.length-1){
					var found = false;
					ar2.push(arr[j]);
					while(found==false && j <arr.length-1){
					j++;
					ar2.push(arr[j]);
					found = isControlPoint(ptmap[arr[j]])==false?true:false;
					}
				}else{
					ar2.push(arr[j]);
					ar2.push(arr[0]);
					j++
					}
			
			var cf2 = this.createFacet(getUniqId());
			var c = cf2.createConnector(cf2.id+"_"+getUniqId());
			var ar3 = new Array();
			for(var k=0;k<ar2.length;k++){
				var a1 = ar2[k].substring(0,ar2[k].length-2);
				ar3.push(a1);
				//var cir = pCanvas.circle(ptmap[a1].x,ptmap[a1].y,3);
				//cir.attr("fill","purple");	
			}
			for(var k=ar2.length-1;k>=0;k--){
				ar3.push(ar2[k]);	
				//var cir = pCanvas.circle(ptmap[ar2[k]].x,ptmap[ar2[k]].y,3);
				//cir.attr("fill","pink");	

			}
			c.addPoints(ar3);

			

		}
		}
	}
	pullUpFacetOld(fid,dx,dy,dz){
		var cf = this.cloneFacet(fid,dx,dy,dz);
		for(var i=0;i<cf.connectors.length;i++){
			var c  = cf.connectors[i];
			var arr = c.getPoints();
			for(var j=0;j<arr.length;j++){
				var ar2 = new Array();
				if(j<arr.length-1){
					ar2.push(arr[j]);
					ar2.push(arr[j+1]);
				}else{
					ar2.push(arr[j]);
					ar2.push(arr[0]);
					}
			
			var cf2 = this.createFacet(getUniqId());
			var c = cf2.createConnector(cf2.id+"_"+getUniqId());
			var a1 = ar2[0].substring(0,ar2[0].length-2);
			var a2 = ar2[1].substring(0,ar2[1].length-2);
			c.addPoints([a1,a2,ar2[1],ar2[0]]);
		}
		}
	}
	updatePoint(id,newx,newy,newz){
		for ( var i = 0; i < this.pts.length; i++) {
			var obj = this.pts[i];
			if(obj.id==id){
				obj.x=newx;
				obj.y=newy;
				obj.z=newz;
			}
		}
	}
	getPointMap(){
		var m = {};
		for ( var i = 0; i < this.pts.length; i++) {
			var obj = this.pts[i];
			m[obj.id] = obj;
		}
		return m;
	}
	getPoint(id){
		for ( var i = 0; i < this.pts.length; i++) {
			var obj = this.pts[i];
			if(obj.id==id)return obj;
		}
		return null;
	}
	getFacet(id){
		for(var i=0;i<this.facets.length;i++){
			if(this.facets[i].id==id){
				return this.facets[i];
			}
		}
		return null;
	}
	getFacetPoints(id){
		var ret = new Array();
		var pm = this.getPointMap();
		for(var i=0;i<this.facets.length;i++){
			if(this.facets[i].id==id){
				Object.setPrototypeOf(this.facets[i], Facet.prototype)
				var points= this.facets[i].getPoints();
				for(var j=0;j<points.length;j++){
					var ptt = points[j];
					if(pm[ptt]!=null){
						ret.push(pm[ptt]);
					}
				}
			}
		}
		return ret;
	}
}

function bringShapeConnFront(sid,fid,cid){
	selectFacetConnector(fid,cid);
	if(shapeElementsDrawn[sid+fid+cid]!=null){
	shapeElementsDrawn[sid+fid+cid].toFront();
	shapeElementsDrawn[sid+fid+cid].attr("fill","white");
	}
	if(shapeElementsDrawn[sid+fid+cid+"bndry"]!=null){
	var ar = shapeElementsDrawn[sid+fid+cid+"bndry"];
	for(var i=0;i<ar.length;i++){
		var a = ar[i];
		a.toFront();
		a.attr("stroke","black");
	}
	}
}
function hideShape(sid){
	var sh = findNodeById(sid);
		if(sh.visible==false)
			sh.visible=true;
		else
			sh.visible=false;
	draw();
}
function deleteShapeFacet(sid,fid){
	console.log(sid+"->>"+fid);
	var sh = findNodeById(sid);
	Object.setPrototypeOf(sh, Shape.prototype)
	sh.deleteFacet(fid);
	draw();
}
function deleteShapeFacetEx(sid,fid){
	console.log(sid+"->>"+fid);
	var sh = findNodeById(sid);
	Object.setPrototypeOf(sh, Shape.prototype)
	sh.deleteFacetEx(fid);
	draw();
}

function getShapeConn(sid,fid,cid){
	console.log(sid+"->"+fid+"->"+cid);
	var sh = findNodeById(sid);
	Object.setPrototypeOf(sh, Shape.prototype)
	var f = sh.getFacet(fid);
	Object.setPrototypeOf(f, Facet.prototype)
	var c = f.getConnector(cid);
	Object.setPrototypeOf(c, Connector.prototype)
	return c;
}
function logFacets(){
	for ( var i = 0; i < globalShape.facets.length; i++) {
		var obj  = facets[i];
		console.log("Facet: "+obj.id);
	}		
}
function logConnectors(){
	for ( var i = 0; i < globalShape.facets.length; i++) {
		var obj  = facets[i];
		for ( var j = 0; j < obj.connectors.length; j++) {
		console.log("Facet: "+obj.id+" > "+obj.connectors[j].id);
		}
	}		
}
function saveShape(){
	removeNodeById(globalShape.id);
	addObjectToGraph(globalShape);
}
function selectShape(id){
	if(id==null)id=prompt("Enter id");
	 gs = findNodeById(id);
	Object.setPrototypeOf(gs, Shape.prototype)
	globalShape = gs;
	currShapeConnector=null;
	currShapeFacet = null;
}
function newShape(id){
	if(id==null)id=prompt("Enter id");
	globalShape = new Shape(id);
}
function deleteShape(id){
	if(id==null)id=prompt("Enter id");
	removeNodeById(id);
}
function createFacet(id){
	if(id==null)id=prompt("Enter id");
	var f = globalShape.createFacet(id);
	selectShapeFacet(id);
	return id;
}
function createCurrFacetSelector(id){
	if(id==null)id=prompt("Enter id");
	if(currShapeFacet!=null){
		var c = currShapeFacet.createConnector(id);
		var cc = selectShapeConnector(id);
		return cc;
	}
}
function createConnector(fid,id){
	var f = 	selectShapeFacet(fid);
	if(f!=null){
		var c = f.createConnector(id);
		var cc = selectShapeConnector(id);
		return cc;
	}
}
function pullupOn(){
	pullupmode=true;
}
function pullupOff(){
	pullupmode=false;
}
function clonemodeOn(){
	clonemode =true;
}
function clonemodeOff(){
	clonemode =false;
}

function pointSelectorOn(){
	ptSelector = true;
}
function pointSelectorOff(){
	ptSelector = false;
}
function selectShapeFacet(id){
	for ( var i = 0; i < globalShape.facets.length; i++) {
	var o = globalShape.facets[i];
	if(o.id==id){
		currShapeFacet = o;
	}
	}
}
function selectShapeConnector(id){
	for ( var i = 0; i < currShapeFacet .connectors.length; i++) {
	var o = currShapeFacet .connectors[i];
	if(o.id==id){
		currShapeConnector = o;
	}
	}
}
function selectFacetConnector(fid,cid){
	selectShapeFacet(fid);
	selectShapeConnector(cid);
	
}
var ShapePointDragger = function() {
	this.ox = this.attr("cx") ;
	this.oy = this.attr("cy") ;
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, ShapePointMove = function(dx, dy) {
	var stepSize = parseInt(wideconfig.stepMoverSize);
	if(ptSelector==true ||controlMode==true){
		return;
	}
	var normalizedx = parseInt((mouseX)/stepSize)*stepSize;
	var normalizedy = parseInt((mouseY)/stepSize)*stepSize;
		var att = {
		cx : normalizedx  ,
		cy : normalizedy 
};
	this.attr(att);
	var myid = this.node.getAttribute("eleid");
	var mysid = this.node.getAttribute("shapeid");

	if (myid != null) {
		var gs = findNodeById(mysid);
		Object.setPrototypeOf(gs, Shape.prototype)
		if(gs!=null){
		var node = gs.getPoint(myid);
		if(node!=null){
			gs.updatePoint(myid,att.cx,att.cy,node.z);
		}
		}
	}
}, ShapePointUp = function() {
	if(ptSelector==true){
		if(currShapeConnector!=null){
			var ptStr = this.node.getAttribute("eleid");
			var mysid = this.node.getAttribute("shapeid");
			console.log("adding point to current connector: "+ptStr)
			Object.setPrototypeOf(currShapeConnector, Connector.prototype)
			currShapeConnector.addPoint(ptStr);
		}
	}else if(controlMode==true){
		var eleid = this.node.getAttribute("eleid");
		var mysid = this.node.getAttribute("shapeid");
		var gs = findNodeById(mysid);
		Object.setPrototypeOf(gs, Shape.prototype)
		if(gs!=null){
			var node = gs.getPoint(eleid);
			Object.setPrototypeOf(node, Point.prototype)
			var node2 = globalShape.getPoint(eleid);
			if(node2!=null)
			Object.setPrototypeOf(node2, Point.prototype)
			if(node!=null){
				if(isControlPoint(node)){
					node.convertToPosition();
					if(node2!=null)
					node2.convertToPosition();
					
				}else{
					node.convertToControl();
					if(node2!=null)
					node2.convertToControl();
				}
			}
		}
	}else{
		draw();
	}
	
};

function beginShapePoints(notusedid){
	var a = dojo.byId("graph");
	//console.log(a);
	a.onmousedown = function (e){
	var mxx = 0;
	var myy = 0;
	    if (!e)
			var e = window.event;
		if (e.pageX || e.pageY) {
			mxx = e.pageX;
			myy = e.pageY;
		} else if (e.clientX || e.clientY) {
			mxx = e.clientX + document.body.scrollLeft
					+ document.documentElement.scrollLeft;
			myy = e.clientY + document.body.scrollTop
					+ document.documentElement.scrollTop;
		}
	var hgt = 10;
	c = pCanvas.circle(mxx,myy-topWidth,hgt);
	c.attr("fill","orange");
	var p = new Point(mxx,myy-topWidth);
	if(currShapeConnector!=null)
		p.id = globalShape.id+"_"+currShapeConnector.id+"_"+p.id
		else{
			p.id = globalShape.id+"_"+getUniqId()+"_"+p.id
		}
	c.node.setAttribute("eleid",p.id);
	globalShape.addPoint(p);
	c.drag(ShapePointMove,ShapePointDragger,ShapePointUp);
	if(currShapeConnector!=null){
		currShapeConnector.addPoint(p.id);
	}
	}
}
function stopShapePoints(){
	var a = dojo.byId("graph");
	a.onmousedown = function (e){}
	saveShape();
	draw();
}
function createDefaultFacet(fid,cid){
	globalShape.createFacet(fid).createConnector(cid);
	selectFacetConnector(fid,cid);
}
function createFacetShapeId(sid,fid,cid){
	var gs = findNodeById(sid);
	Object.setPrototypeOf(gs, Shape.prototype);
	gs.createFacet(fid).createConnector(cid);
}
function createConnectorShapeId(sid,fid,cid){
	var gs = findNodeById(sid);
	Object.setPrototypeOf(gs, Shape.prototype);
	var f = gs.getFacet(fid);
	Object.setPrototypeOf(f, Facet.prototype);
	f.createConnector(cid);
}
function createTopFacet(){
	createDefaultFacet("top", "1");	
}
function createBottomFacet(){
	createDefaultFacet("bottom", "1");	
}
function createFrontFacet(){
	createDefaultFacet("front", "1");	
}
function createBackFacet(){
	createDefaultFacet("back", "1");	
}
function createLeftFacet(){
	createDefaultFacet("left", "1");	
}
function createRightFacet(){
	createDefaultFacet("right", "1");	
}

var FacetDragger = function() {
	this.odx = 0;
	this.ody = 0;
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, FacetMove = function(dx, dy) {
	this.translate(dx - this.odx, dy - this.ody);
	this.odx = dx;
	this.ody = dy;
	console.log("moved: "+this.odx+" , "+this.ody);
	var mysid = this.node.getAttribute("shapeid");
	var myid = this.node.getAttribute("facetid");
	if (myid != null) {
		var gs = findNodeById(mysid);
		Object.setPrototypeOf(gs, Shape.prototype)
		if(gs!=null){
		 //gs.moveFacet(myid,this.odx,this.ody,0);
		}
	}
}, FacetUp = function() {
	if(this.odx==0 && this.ody==0)return;
	 this.animate({"fill-opacity": 1}, 500);
	 var mysid = this.node.getAttribute("shapeid");
	 var myid = this.node.getAttribute("facetid");
		if (myid != null) {
			var gs = findNodeById(mysid);
			Object.setPrototypeOf(gs, Shape.prototype)
			if(gs!=null){
			if(clonemode==true){
				gs.cloneFacet(myid,this.odx,this.ody,0);
			}else if(pullupmode ==true){
				gs.pullUpFacet(myid,this.odx,this.ody,0);
			}else{
				if(moveShapeMode==false){
				gs.moveFacet(myid,this.odx,this.ody,0);
				}else{
				gs.moveAllPoints(myid,this.odx,this.ody,0);
				}
			}
			}
		}
		draw();
};

var BndryDragger = function() {
	this.odx = 0;
	this.ody = 0;
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, BndryMove = function(dx, dy) {
	if(!bndryMoveMode)return;
	this.translate(dx - this.odx, dy - this.ody);
	this.odx = dx;
	this.ody = dy;
	console.log("moved: "+this.odx+" , "+this.ody);
	var mysid = this.node.getAttribute("shapeid");
	var myid = this.node.getAttribute("facetid");
	if (myid != null) {
		var gs = findNodeById(mysid);
		Object.setPrototypeOf(gs, Shape.prototype)
		if(gs!=null){
		 //gs.moveFacet(myid,this.odx,this.ody,0);
		}
	}
}, BndryUp = function() {
	if(bndryMoveMode){
	if(this.odx==0 && this.ody==0) return;
	 this.animate({"fill-opacity": 1}, 500);
	 var mysid = this.node.getAttribute("shapeid");
	 var myid = this.node.getAttribute("facetid");
 	var afterid = this.node.getAttribute("se");
	var beforeid = this.node.getAttribute("ee");
	 if (myid != null) {
			var gs = findNodeById(mysid);
			Object.setPrototypeOf(gs, Shape.prototype)
			if(gs!=null){
				gs.movePoints([afterid,beforeid],this.odx,this.ody,0);
			}
		}
		draw();
	}else{
        var x = mouseX, y = mouseY;
	       var c = pCanvas.circle(x,y,3);
	    	// now add point to path
	    	var myid = this.node.getAttribute("facetid");
	    	var mysid = this.node.getAttribute("shapeid");
	    	var afterid = this.node.getAttribute("se");
	    	var beforeid = this.node.getAttribute("ee");
	    	if (myid != null) {
	    		var gs = findNodeById(mysid);
	    		Object.setPrototypeOf(gs, Shape.prototype)
	    		if(gs!=null){
                 if(afterid!=null && beforeid!=null){
			    	var p = new Point(x,y);
			    	c.node.setAttribute("eleid",p.id);
			    	gs.addPointToFacet(myid,afterid,beforeid,p);
                 }
	    		}
	    	}
	    	draw();

	}
};
function isControlPoint(pt){
	if(pt.pointType=="Control"||pt.pointType=="control"){
		return true;
	}
	return false;
}

function rotateShape(sid,angle){
for(var i=0;i<pData.data.length;i++){
    var d = pData.data[i];
    if (  d.type=="ShapeShape"  && d.id==sid){
        var so = d;//found a shape object;
        var mx = 0,my = 0 , mz = 0;
        d.data["trot"]=angle;
        for(var k = 0; k < so.pts.length ; k++){
           var pt = so.pts[k];
            mx += pt.x;
            my += pt.y;
        }
        mx = mx / so.pts.length;
        my = my / so.pts.length;
        for(var k = 0; k < so.pts.length ; k++){
           var pt = so.pts[k];
            var c = shapeElementsDrawn["pt_"+pt.id]
            if(c!=null){
                console.log(pt.id)
                var tt = "rotate("+angle+" "+mx+","+my +")";
                console.log(tt)
            c.node.setAttribute("transform",tt);
            console.log(c.node.getBoundingClientRect());
            console.log(dojo.toJson(pt))
            var br = c.node.getBoundingClientRect();
            pt.x = br.x+br.width/2;
            pt.y = br.y+br.height/2;
            pt.y -= topWidth;
	        pt.y -= scrollTop();
            console.log(dojo.toJson(pt))
            var cc = pCanvas.circle(pt.x,pt.y,5);
            cc.attr("fill","red")
            
            }
        }	
        
        
    }
}
}
function scaleShape(sid,skewx,skewy){
	console.log("Skew: "+skewx+","+skewy)
	for(var i=0;i<pData.data.length;i++){
	    var d = pData.data[i];
	    if (  d.type=="ShapeShape"  && d.id==sid){
	    		d.data["tsx"]=skewx;
	    		d.data["tsy"]=skewy;
	    	var so = d;//found a shape object;
	        var mx = 0,my = 0 , mz = 0;
	        for(var k = 0; k < so.pts.length ; k++){
	           var pt = so.pts[k];
	            mx += pt.x;
	            my += pt.y;
	        }
	        mx = mx / so.pts.length;
	        my = my / so.pts.length;
	        for(var k = 0; k < so.pts.length ; k++){
	           var pt = so.pts[k];
	           pt.x = pt.x - mx;
	           pt.y = pt.y - my;
	           pt.x *=skewx;
	           pt.y *=skewy;
	           pt.x +=mx;
	           pt.y +=my;
	           
	        }
	        
	        
	    }
	}
}
function offsetShape(sid,offx,offy){
	console.log("Skew: "+offx+","+offy)
	for(var i=0;i<pData.data.length;i++){
	    var d = pData.data[i];
	    if (  d.type=="ShapeShape"  && d.id==sid){
	        var so = d;//found a shape object;
	        var mx = 0,my = 0 , mz = 0;
	        for(var k = 0; k < so.pts.length ; k++){
	           var pt = so.pts[k];
	            mx += pt.x;
	            my += pt.y;
	        }
	        mx = mx / so.pts.length;
	        my = my / so.pts.length;
	        
	        for(var k = 0; k < so.pts.length ; k++){
	           var pt = so.pts[k];
	           pt.x = pt.x - mx;
	           pt.y = pt.y - my;
	           if(pt.x>=0)
	        	   pt.x -=offx;
	           else
	        	   pt.x +=offx;
	           
	           if(pt.y>=0)
	        	   pt.y -=offy;
	           else
	        	   pt.y +=offy;
           
	           pt.x +=mx;
	           pt.y +=my;
	           
	        }
	        
	        
	    }
	}
}




function cloneShapeDeep(id,myid){
    var item = findNodeById(id);
	var item2 = JSON.stringify(item);
	var conn = id+"_conn"
	var newconn = myid+conn;

	re = new RegExp(conn, 'g');
	console.log(item2)
	item3 = item2.replace(re,newconn);
	console.log("r: "+item3);
	var item4 = JSON.parse(item3);
	item4.id=myid;
	console.log(item4)
	item4.data["generated"] = "true";
	item4.data["generatedfrom"] = id;
	var item5 = findNodeById(myid);
	if(item5!=null){
	item4.data=item5.data;
	item4.data["generated"] = "true";
	item4.data["generatedfrom"] = id;
	}
	removeNodeById(item4.id);
	addObjectToGraph(item4);
	if(item4.data["tdx"]!=null){
		moveShape(item4, parseInt(item4.data["tdx"]),parseInt(item4.data["tdy"]),parseInt(item4.data["tdz"]),true)
	}
/*	if(item4.data["trot"]!=null){
		rotateShape(item4.id,item4.data["trot"])
	}
	if(item4.data["tsx"]!=null){
		scaleShape(item4.id,item4.data["tsx"],item4.data["tsy"])
	}*/
	return item4;
}
function moveShape(shape, dx,dy,dz,update){
    for(var i=0;i<shape.pts.length;i++){
        shape.pts[i].x = shape.pts[i].x +dx;
        shape.pts[i].y = shape.pts[i].y +dy;
        shape.pts[i].z = shape.pts[i].z +dz;
    }
    if(update!=true){
	if(shape.data["tdx"]!=null){
		shape.data["tdx"] = parseInt(shape.data["tdx"]) +dx;
		shape.data["tdy"] = parseInt(shape.data["tdy"]) +dy;
		shape.data["tdz"] = parseInt(shape.data["tdz"]) +dz; 	 
			}
	else{
		shape.data["tdx"] = dx;
		shape.data["tdy"] = dy;
		shape.data["tdz"] = dz;
	}
    }
 }

