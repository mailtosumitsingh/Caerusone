var started = false;
var pstr = "";
var ox=-10,oy=-10;
var pts = {};
var eleid= "uni"+Math.ceil(Math.random()*1000);
var i = 0;
document.onmousemove = function(e){
var mx =0,my=0;

if (e.pageX || e.pageY) {
		mx = e.pageX;
		my = e.pageY;
	} else if (e.clientX || e.clientY) {
		mx = e.clientX + document.body.scrollLeft+ document.documentElement.scrollLeft;
		my = e.clientY + document.body.scrollTop+ document.documentElement.scrollTop;
	}

if(started){
if(ox<0 && oy<0){
ox = mx;
oy = my-24;
var ptid = ""+eleid+i;
pstr += "M "+ox+" "+ oy +" ";
pts[ptid] = {"x":ox,"y":oy,"id":ptid};
i++;
}else{
pCanvas.line(ox,oy,mx,my-24);
ox = mx;
oy = my-24;
pstr += " L "+ox+" "+ oy;
var ptid = ""+eleid+i;
pts[ptid] = {"x":ox,"y":oy,"id":ptid};
i++;
}
}
}
document.onmousedown = function (e){

if(started){
started=false;
var p = pCanvas.path(pstr);

p.node.id = eleid;
p.attr("stroke","red");
p.attr("fill","green");
p.drag(mymove, mydrag, myup);
p.tx=0;
p.ty=0;
ox=-10,oy=-10;
document.onmousedown = function (e){};
document.onmousemove =captureMousePosition2 ;
var shapestr = "var shape=pCanvas.path(\""+pstr+"\");\n";
shapestr += "shape.attr(\"fill\",\"green\");";
shapestr += "shape.attr(\"stroke\",\"red\");";
var sh = getShape(p.node.id,shapestr,pstr);
sh.pts = dojo.toJson(pts);
pstr = "";
compDiag.push(sh);
globals[""+p.node.id]=p;
for(var j in pts){
var c =  pCanvas.circle(pts[j].x,pts[j].y,3,3);
c.node.id=pts[j].id;
c.node.eleid = eleid;
c.attr("fill","blue");
globals[pts[j].id]=c;
c.drag(ptmove,ptdrag, ptup);
c.mouseover(function (e){
this.attr("fill","red");
});
c.mouseout(function (e){
this.attr("fill","blue");
});
c.dblclick(function (e){
removePoint(this);
});
c.toFront();
}
}else{
pstr = "";
started = true;
ox=-10,oy=-10;

}
}