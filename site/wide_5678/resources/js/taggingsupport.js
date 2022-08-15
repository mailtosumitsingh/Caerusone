var mode='na';
var mode2='na'
var di=0;
var mxdi=2;
var si=0;
var currdi=0;

class ImageToTag{
    constructor(x,y,x2,y2,lbl,path){
    	this.x  = x;
		this.y  = y;
        this.x2 = x2;
        this.y2 = y2;
        this.label=""+lbl;
        this.path = path;
        this.tagtype = "DocImage";
        this.group = this.label;
    }
	draw(){
	    var node= createImageToTagNode(this.x,this.y,this.x2,this.y2,this.label,this.label,this.tagtype,this.group,this.path); 
	    addObjectToGraph(node);
		var d = pCanvas.image(node.path,node.x,node.y,node.r,node.b);
		d.toBack();
		d.node.setAttribute("eleid", node.id);
		d.click(handleTagClick);
		var dnode = {
				"item" : d,
				"id" : node.id
			};
		drawElements.push(dnode);
	}
}
genericnodes["ImageToTag"] = "/images/null.png";	
nodeDrawingHandler["ImageToTag"]=drawImageToTagNode;
function drawImageToTagNode(xpos, ypos, width, height, fillColor, borderColor,node,imgsrc){
	var d = pCanvas.image(node.path,node.x,node.y,node.r,node.b);
	d.toBack();
	d.node.setAttribute("eleid", node.id);
	d.click(handleTagClick);
	var dnode = {
			"item" : d,
			"id" : node.id
		};
	drawElements.push(dnode);

}
	function createImageToTagNode(x,y,w,h,id,name,tagtype,group,path){
	    var newNode = {};
		newNode["type"]="ImageToTag";
		newNode["id"]="ImageToTag_"+group+"_"+id;
		newNode["x"]=""+x;
		newNode["normalizedx"]=""+x;
		newNode["y"]=""+y;
		newNode["normalizedy"]=""+y;
		newNode["r"]=""+w;
		newNode["b"]=""+h;
		newNode["name"] = ""+name;
		newNode["tagtype"] = ""+tagtype;
		newNode["group"] = ""+group;
		newNode["label"] = ""+name;
		newNode["path"] = ""+path;
	    return newNode
	}
//---------------------------------------------
class Digit{
    constructor(x,y,x2,y2,lbl,type)
	{
		this.x  = x;
		this.y  = y;
        this.x2 = x2;
        this.y2 = y2;
        this.label=""+lbl;
        this.segments=new Array();
        this.tagtype = type;
        this.group = "";
    }
    init(){
         for(var i=0;i<8;i++){
          this.segments.push(new Digit(0,0,0,0,""+i,"Seg"));
        }
    }
    draw(){
        console.log("drawing");
   
       var node= createVisTagNode(this.x,this.y,this.x2-this.x,this.y2-this.y,this.label,this.label,this.tagtype,this.group); 
       addObjectToGraph(node);
       var r = pCanvas.rect(this.x,this.y,this.x2-this.x,this.y2-this.y,1);
       r.toFront();
       r.attr({
   		"stroke" : "red",
   		"opacity" : .4,
   		"stroke-width" : 3,
   		"cursor" : "move"
   		});
       r.node.setAttribute("eleid", node.id);
       var t = pCanvas.text(this.x-3,this.y-10,""+this.label);
       t.toFront();
       var dragger = function() {
    		this.ox = this.type == "circle" ? this.attr("cx") : this.attr("x");
    		this.oy = this.type == "circle" ? this.attr("cy") : this.attr("y");
       };
       var move = function(dx, dy) {
    		var att = this.type == "circle" ? {
    			cx : this.ox + dx,
    			cy : this.oy + dy
    		} : {
    			x : this.ox + dx,
    			y : this.oy + dy
    		};
    	this.attr(att);
       };
       var up = function() {
    		var myid = this.node.getAttribute("eleid");
    		var item = findNodeById(myid);
    		item.x  = this.getBBox().x;
    		item.y = this.getBBox().y;
    		item.r  = this.getBBox().width;
    		item.b  = this.getBBox().height;
			
       };
       var r2= pCanvas.set(t,r)
       r2.drag(move, dragger, up);
       var connection = {
    			"item" : r,
    			"textnode" : t,
    			"id" : node.id
    		};
       drawElements.push(connection);
    }
}
genericnodes["VisTagNode"] = "/images/hole.png";	
nodeDrawingHandler["VisTagNode"]=drawVisTagNode;
function drawVisTagNode(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	   var r = pCanvas.rect(obj.x,obj.y,obj.r,obj.b,1);
       r.toFront();
       r.attr({
   		"stroke" : "red",
   		"opacity" : .4,
   		"stroke-width" : 3,
   		"cursor" : "move"
   		});
       r.node.setAttribute("eleid", obj.id);
       var t = pCanvas.text(obj.x-3,obj.y-10,""+obj.label);
       t.toFront();
       var dragger = function() {
   		this.ox = this.type == "circle" ? this.attr("cx") : this.attr("x");
   		this.oy = this.type == "circle" ? this.attr("cy") : this.attr("y");
      };
      var move = function(dx, dy) {
   		var att = this.type == "circle" ? {
   			cx : this.ox + dx,
   			cy : this.oy + dy
   		} : {
   			x : this.ox + dx,
   			y : this.oy + dy
   		};
   		var myid = this.node.getAttribute("eleid");
   		var item = findNodeById(myid);
   		var segEle = findDrawEleByIdEx(myid);
		   	if(segEle!=null){
		   		segEle.textnode.attr("x",this.ox+dx);
		   		segEle.textnode.attr("y",this.oy+dy)
		   	}
			if(item.tagtype=='Digit'){
			if(moveGroupElements){
	   			for(var i=0;i<7;i++){
	   				var segid = "VisTagNode_"+item.label+"_"+i;
	   		   		var seg = findNodeById(segid);
	   		   		if(seg!=null){
	   		   		xmove=parseInt(seg.x) + parseInt(dx);
	   		   		ymove =parseInt(seg.y)+ parseInt(dy);
	   		   		}
	   		  	var segEle = findDrawEleByIdEx(segid);
	   		   	if(segEle!=null){
	   		   		segEle.item.attr("x",xmove);
	   		   		segEle.item.attr("y",ymove);
	   		   		segEle.textnode.attr("x",xmove);
	   		   		segEle.textnode.attr("y",ymove);

	   		   	}
	   			}
	   			}
	   			}
			this.attr(att);			
      };
      var up = function() {
   		var myid = this.node.getAttribute("eleid");
   		var item = findNodeById(myid);
   		var dx = parseInt(this.getBBox().x)-parseInt(item.x);
   		var dy = parseInt(this.getBBox().y)-parseInt(item.y);
   		if(item.tagtype=='Digit'){
   			if(moveGroupElements){
   			for(var i=0;i<7;i++){
   				var segid = "VisTagNode_"+item.label+"_"+i;
   		   		var seg = findNodeById(segid);
   		   		if(seg!=null){
   		   		seg.x =parseInt(seg.x) + parseInt(dx);
   		   		seg.y =parseInt(seg.y)+ parseInt(dy);
   		   		}
   		  	var segEle = findDrawEleByIdEx(segid);
   		   	if(segEle!=null){
   		   		segEle.item.attr("x",seg.x);
   		   		segEle.item.attr("y",seg.y);
   		   	}
   			}
   			}
   		}
   		item.x  = this.getBBox().x;
   		item.y = this.getBBox().y;
   		item.r  = this.getBBox().width;
   		item.b  = this.getBBox().height;
      };
     r.drag(move, dragger, up);
     var connection = {
 			"item" : r,
 			"textnode" : t,
 			"id" : obj.id
 		};
    drawElements.push(connection);

       
}
function createVisTagNode(x,y,w,h,id,name,tagtype,group){
    var newNode = {};
	newNode["type"]="VisTagNode";
	newNode["id"]="VisTagNode_"+group+"_"+id;
	newNode["x"]=""+x;
	newNode["normalizedx"]=""+x;
	newNode["y"]=""+y;
	newNode["normalizedy"]=""+y;
	newNode["r"]=""+w;
	newNode["b"]=""+h;
	newNode["name"] = ""+name;
	newNode["tagtype"] = ""+tagtype;
	newNode["group"] = ""+group;
	newNode["label"] = ""+name;
	
    return newNode
}
var digits = new Array();
for(var i=0;i<8;i++){
    var dd = new Digit(0,0,0,0,""+i,"Digit");
    dd.init();
    digits.push(dd);
}
function tagDigit (ii){
    currdigit=digits[ii];
    currdi=ii;
    mode="seg";
    mode2="seg1"
    si=0;
 }
function tagDigit2 (ii){
    currdigit=digits[ii];
    currdi=ii;
    mode="ts";
    si=0;
 }


function handleTagClick(e){
console.log(parseInt(mouseX),parseInt(mouseY));
if(mode=='dc' && mode2=='dc1'){
    digits[di]=new Digit(parseInt(mouseX),parseInt(mouseY));
    digits[di].init();
    mode2='dc2';

}else if(mode=='dc' && mode2=='dc2'){
    digits[di].x2=parseInt(mouseX);
    digits[di].y2=parseInt(mouseY);
    digits[di].label=""+di;
    digits[di].tagtype="Digit";
    digits[di].group="self";
    digits[di].draw();
    di++;
    if(di<mxdi){
    mode2='dc1'
    }else{
    mode2='na';
    mode='na'
    }
}
if(mode=='seg' && mode2=='seg1'){
    currdigit.segments[si]=new Digit(parseInt(mouseX),parseInt(mouseY));
    mode2='seg2';
}else if(mode=='seg' && mode2=='seg2'){
    currdigit.segments[si].x2=parseInt(mouseX);
    currdigit.segments[si].y2=parseInt(mouseY);
    currdigit.segments[si].label=''+si
    currdigit.segments[si].group=''+currdi;
    currdigit.segments[si].tagtype='Seg';
    currdigit.segments[si].draw();
    si++;
    if(si<7){
    mode2='seg1'
    }else{
    mode2='na';
    mode='na'
    }    
}
if(mode=='ts' ){
    currdigit.segments[si]=new Digit(parseInt(mouseX),parseInt(mouseY),parseInt(mouseX+6),parseInt(mouseY+6));
    currdigit.segments[si].label=""+si
    currdigit.segments[si].group=''+currdi;
    currdigit.segments[si].tagtype='Seg';
    currdigit.segments[si].draw();
    si++;
    if(si<7){
    	console.log("continue tagging")
    }else{
    mode2='na';
    mode='na'
    } 
}
if(mode=='onclr'){
	var clr = new Digit(parseInt(mouseX),parseInt(mouseY),parseInt(mouseX+6),parseInt(mouseY+6));
	clr.label='onclr';
	clr.group='onclr';
	clr.tagtype='onclr';
	clr.draw();
	mode2='na';
    mode='na'
}

}
function initDigits(maxDigits){
	digits = new Array();
	for(var i=0;i<maxDigits;i++){
	    var dd = new Digit(0,0,0,0,""+i);
	    dd.init();
	    digits.push(dd);
	}
}
function initImageTagDigit(numOfDigitsToTag,indexOfStartDigit){
	mode='dc';
	mode2='dc1'
	di=indexOfStartDigit;
	mxdi=numOfDigitsToTag;
	si=0;
	currdi=indexOfStartDigit;
}
function cloneDigit(myid,cloneId){
	var item = findNodeById("VisTagNode_"+"self"+"_"+myid);
	var itemClone = cloneItem(item);
	itemClone.id="VisTagNode_"+"self"+"_"+cloneId;
	itemClone.label = cloneId;
	itemClone.name = cloneId;
	
	addObjectToGraph(itemClone);
	for(var i=0;i<7;i++){
		var segid = "VisTagNode_"+myid+"_"+i;
		var seg = findNodeById(segid);
		if(seg!=null){
			var segClone = cloneItem(seg);
			segClone.id = "VisTagNode_"+cloneId+"_"+i
			segClone.group=cloneId;
			addObjectToGraph(segClone );
		}
	}
}
function deleteDigit(myid){
	removeNodeById("VisTagNode_"+"self"+"_"+myid)
	for(var i=0;i<7;i++){
		var segid = "VisTagNode_"+myid+"_"+i;
		removeNodeById(segid)
	}
}
