var cncPort= 'com3';
var url = '/site/SerialCmdHandler?port='+cncPort+'&cmd=';
var reinitUrl = '/site/SerialCmdHandler?port='+cncPort+'&op=reinit';
var svgToImg = "/site/imgtosvg"
var scale = 20;// .1mm per pixel point
var factor = 1/scale; // used in reverse interpolation(mm in pixels 20 pixels
						// = 1 mm )

var defaultFeedRate = 600;
var feedRate = "F "+defaultFeedRate;
var G01 = "G01";
var G00 = "G00";
var cncPostMsg = function (evt){
	console.log(evt);
}

function getCncCmdUrl(cmd,x,y){
	return url +getCncCmdStr(cmd,x,y);
}

function getCncCmdStr(cmd,x,y){
	return cmd+","+x+"," +y;
}
function onCncEvent(f){
	if(cncPostMsg!=null) 
		cncPostMsg(f);
    var s = f.msg;
    var ss = s.split("\n");
    var str =0;
    for( str = 0;str< ss.length;str++){
    if(ss[str].length>1)
    {
    	
        console.log(">>"+ss[str]);
        var x = ss[str].split(",")[0];
        var y = ss[str].split(",")[1];
        x = parseInt(x);
        y = parseInt(y)
        console.log("x: "+x+" ,y: "+ y );
        pCanvas.circle(x/scale,y/scale,3)
    }
    }
    
}
function getSvg(name,func){
	var url = svgToImg+"?f="+name;
	var ret = doGetHtmlSync(url);
	var a = dojo.fromJson(ret);
	for(var i=0;i<a.length;i++){
		console.log(a[i]);
		func(a[i]);
	}
}
function getSvgEx(name,func){
	var url = svgToImg+"?f="+name+"&s=true";
	var ret = doGetHtmlSync(url);
	var a = dojo.fromJson(ret);
	for(var i=0;i<a.length;i++){
		console.log(a[i]);
		func(a[i]);
	}
}
function mr(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("mr",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function reinitCnc(){
	var ret = doGetHtmlSync(reinitUrl);
	console.log(ret);
}
function ml(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("ml",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function mu(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("mu",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function md(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("md",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function zu(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("zu",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function xplunge(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("xplunge",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}function yplunge(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("yplunge",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}function zplunge(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("zplunge",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function zd(steps, rate){
	steps = steps * scale;
	var cmd = getCncCmdUrl("zd",steps,rate);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}

function spos(x, y){
	steps = x * scale;
	var cmd = getCncCmdUrl("spos",x,y);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function waitfor(x,y){
	var cmd = getCncCmdUrl("waitfor",x * scale,y * scale);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function sleepcnc(x,y){
	var cmd = getCncCmdUrl("sleepcnc",x * scale,y * scale);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}

function moveto(x,y){
	var cmd = getCncCmdUrl("moveto",x * scale,y * scale);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function movetofast(x,y){
	var cmd = getCncCmdUrl("movetofast",x*scale,y*scale);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function getpos(x,y){
	var cmd = getCncCmdUrl("getpos",x,y);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}

function setcc(x,y){
	var cmd = getCncCmdUrl("setcc",x,y);
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}

function circle(x,y,r){
	steps = x * scale;
	var cmd = url+ cmd+","+x+"," +y+","+r;
	var ret = doGetHtmlSync(cmd);
	console.log(ret);
}
function setFeedRate(mmpermin){
	feedRate = "F "+mmpermin;
}
function gcodify(x1,y1,x2,y2,z1,z2,collector){
	console.log("Gcodify: "+x1)
	gcodifyInternal(x1,y1,x2,y2,z1,z2,G01,collector);
}
function gcodifyInternal(x1,y1,x2,y2,z1,z2,gcode,collector){
	if(gcode==null)gcode = G01;
	
	var fixedWidth=2;
	var x11 = (x1*factor).toFixed(fixedWidth);
	var y11 = (y1*factor).toFixed(fixedWidth);
	var z11 = (z1*factor).toFixed(fixedWidth);
	
	var x12 = (x2*factor).toFixed(fixedWidth);
	var y12 = (y2*factor).toFixed(fixedWidth);
	var z12 = (z2*factor).toFixed(fixedWidth);

	if(x11!=x12 || y11 !=y12 || z11!=z12 ){
		var str = gcode +" X"+x12 +" Y" +y12 +" Z" +z12;
		if(gcode==G01){
			str = str +" " + feedRate + " ";
		}
		var oldCorrd = " ( " + x11 +" " +y11 +" "+z11 + " ) ";
		str  +=oldCorrd;
		console.log(str);
		if(collector!=null){
			collector(str);
		}
	}
}
function drawCncPath(a/* path raphael */,offsetx,offsety){
	var lastCncPoint = null;
	var len = a.getTotalLength();
	for(var i = 0;i<len+1;){
	  var b = a.getPointAtLength(i);
	  var c =   pCanvas.circle(b.x,b.y,10);
	  c.attr("stroke","red");
	    i=i+5;
	    if(lastCncPoint!=null){
	        var g = pCanvas.line(lastCncPoint.x+offsetx,lastCncPoint.y+offsety,b.x+offsetx,b.y+offsety);
	         g.attr("stroke","green");        
	    }
	    lastCncPoint = b;
	    
	}
}
var gcodes = null;
var subcription = dojo.subscribe("CNCInfoEvent", null, onCncEvent);
/***************************************
 * 
****************************************/
var cncdrag= function() {
    this.animate( {
		"fill-opacity" : .2
	}, 500);
this.ox = this.attr("x");
this.oy = this.attr("x");
}, cncmove = function(dx, dy) {
 var pt = this.getPointAtLength(1);
var xamt = mouseX-pt.x;
var yamt = mouseY-pt.y;
if(this.tx){
this.tx+=xamt;
}else{
this.tx=xamt;
}
    
if(this.ty){
this.ty+=yamt;
}else{
this.ty=yamt;
}

this.translate(xamt,yamt);
	pCanvas.safari();
},cncup = function() {
	this.animate( {
		"fill-opacity" : 1
}, 500);
}
/***************************************
 * pathPartsToPoints
****************************************/
function pathPartsToPoints(pathparts,str){
	var cornerList = [];

	for ( var i = 0; i < pathparts.length; i++ )
	{
	    switch( pathparts[i][0] )
	    {
	        case "M" :
	        case "L" :
	            var pt =  { x: pathparts[i][1], y: pathparts[i][2] } ;
	            cornerList.push(pt);
	           // console.log(pt);
	            break;
	        case "m" :
	            if(i>0 ){
	                pt =  { x: pathparts[i][1]+cornerList[i-1].x, y: pathparts[i][2] +cornerList[i-1].y} ;
	             
	            }else{
	                pt =  { x: pathparts[i][1], y: pathparts[i][2] } ;
	            }
	            cornerList.push(pt);
	           // console.log(pt);
	            break;
	        case "l" :
	            var pt = null;
	            if(i>0 ){
	            pt =  { x: pathparts[i][1]+cornerList[i-1].x, y: pathparts[i][2] +cornerList[i-1].y} ;
	           
	            }else{
	                pt =  { x: pathparts[i][1], y: pathparts[i][2] } ;
	            }
	            cornerList.push(pt);
	           // console.log(pt);
	            break;
	        default :
	            console.log("Skipping irrelevant path directive '" + pathparts[i][0] + "'" );
	            break;
	    }
	}
	if(str.indexOf("z", str.length - "z".length) !== -1){
	      cornerList.push(cornerList[0]);
	}
	return cornerList;
}
/***************************************
 * 
****************************************/
function getGcodeProgInterpolateDist(a,distance,offsetx,offsety){
	var gcodes = "";
	var lastCncPoint = null;	
	gcodes += "G90 ";
	gcodes +="\n";
	var len = a.getTotalLength();
	for(var i = 0;i<len+1;){
	  var b = a.getPointAtLength(i);
	  var c =   pCanvas.circle(b.x,b.y,10);
	  c.attr("stroke","red");
	    if(lastCncPoint!=null){
	        var g = pCanvas.line(lastCncPoint.x+offsetx,lastCncPoint.y+offsety,b.x+offsetx,b.y+offsety);
	         g.attr("stroke","green");
	         gcodifyInternal(lastCncPoint.x+offsetx,lastCncPoint.y+offsety,b.x+offsetx,b.y+offsety,0,0,G01,function (str){
                  console.log("gcode: "+str);
            	 gcodes += str;
	        	 gcodes +="\n";
	         });
	    }else{
	    	 gcodifyInternal(0,0,b.x+offsetx,b.y+offsety,0,0,G00,function (str){
                 console.log("gcode: "+str);
           	 gcodes += str;
	        	 gcodes +="\n";
	         });
	    }
	    lastCncPoint = b;
	    i=i+distance;
	}
	return gcodes;	
}
function getGcodeProgInterpolate(a){
	return getGcodeProgInterpolateDist(a,5,0,0);
}
/***************************************
 * getGcodeProgNoInterpolate
****************************************/
function getGcodeProgNoInterpolate(pathparts,cornerList){
	var gcodes = "";
	var gcodeTravelDir = null;
	for ( var i = 0; i < cornerList.length; i++ ){
	 var pt = cornerList[i];
	 if(i>0){
	    var ptold = cornerList[i-1];
	    var cncTravelModeTemp = null;
	    if(i>=pathparts.length-1 ){
	        cncTravelModeTemp =  "G90 ("+i+" " +pathparts.length+" )";
	    }else{
	         cncTravelModeTemp = getCncRelMode(pathparts[i][0]);    
	    }
	    
	     if(gcodeTravelDir==null){
	             gcodes+=cncTravelModeTemp;
	             gcodes +="\n";
	            gcodeTravelDir  =cncTravelModeTemp;
	     }else{
	         if(cncTravelModeTemp!=gcodeTravelDir){
	             gcodes+=cncTravelModeTemp;
	             gcodes +="\n";
	        }
	     }
	     gcodifyInternal(ptold.x/2,ptold.y/2,pt.x/2,pt.y/2,0,0,G01,function (str){
	                  console.log("gcode: "+str);
	            	 gcodes += str;
		        	 gcodes +="\n";
		         });
	 }else{
	     gcodifyInternal(0,0,pt.x/2,pt.y/2,0,0,G00,function (str){
	                console.log("gcode: "+str);
	            	 gcodes += str;
		        	 gcodes +="\n";
		         });
	 }
	 }
	return gcodes;
}
/***************************************
 * getCncRelMode
****************************************/
function getCncRelMode(str){
    switch( str )
    {
        case "M" :
        case "L" :
            return "G90";
        case "m" :
        case "l" :
            return "G91";
        default:
            console.log("unknown "+ str + " mode");
    }
    return "G91";
}
/***************************************
 * drawSVGFromServerForPath
****************************************/
function drawSVGFromServerForPath(apath){
gcodes = "";
gcodes +="\n";
gcodes +="G04 P100 " 
gcodes +="\n";
var gcodesTemp = getGcodeProgInterpolate(apath,0,0);
gcodes +=gcodesTemp;
putAndGetGCode(gcodes);
}
/***************************************
 * drawSVGFromServer
****************************************/
function drawSVGFromServer(stringSvgFile){
gcodes = "";
var s = getSvg(stringSvgFile+".svg",function (str){
str  = str.trim();
gcodes +="\n";
gcodes +="G04 P100" +"( "+str+" ) "
gcodes +="\n";
var a = pCanvas.path(str);
a.attr("stroke",Raphael.getColor());
a.attr("stroke-width","3");
a.drag(cncmove,cncdrag, cncup);

if(str.indexOf("c")>-1||str.indexOf("c")>-1){
	var gcodesTemp = getGcodeProgInterpolate(a,0,0);
	gcodes +=gcodesTemp;
}else{
	var pathparts = Raphael.parsePathString( str );
	console.log(pathparts);
	var cornerList  = pathPartsToPoints(pathparts,str);
	var gcodesTemp = getGcodeProgNoInterpolate(pathparts,cornerList);
	gcodes+=gcodesTemp;
}
});
putAndGetGCode(gcodes);
}
function showSVGFromServerEx(stringSvgFile){
	gcodes = "";
	var s = getSvgEx(stringSvgFile+".svg",function (str){
	var eleid= "uni"+Math.ceil(Math.random()*1000);
	var shapestr = "var shape=pCanvas.path(\""+str+"\");\n";
	shapestr += "shape.attr(\"fill\",\"green\");";
	shapestr += "shape.attr(\"stroke\",\"red\");";
	var sh = getShape(eleid,shapestr,str);
	compDiag.push(sh);
	draw();
	});
}

function showSVGFromServer(stringSvgFile){
	gcodes = "";
	var s = getSvg(stringSvgFile+".svg",function (str){
	var eleid= "uni"+Math.ceil(Math.random()*1000);
	var shapestr = "var shape=pCanvas.path(\""+str+"\");\n";
	shapestr += "shape.attr(\"fill\",\"green\");";
	shapestr += "shape.attr(\"stroke\",\"red\");";
	var sh = getShape(eleid,shapestr,str);
	compDiag.push(sh);
	draw();
	});
}
function downloadGcode(){
	var urlStr = getURL("DownloadGcode") + "?name=" + currentGraph+"&op=down";
	window.open(urlStr);
}
function uploadGcode(gcodes,fun){
	var url = getURL("UploadGcode") ;
	var cnt = {
			"name" : currentGraph,
			"gcodes": gcodes,
			"op":"up"
		};
	postFormWithContent(url, cnt, fun);
}
/***************************************
 * putAndGetGCode
****************************************/
function putAndGetGCode(gcodes){
	 uploadGcode(gcodes,function (data){downloadGcode();});
}

function addHoleAbs(xm,ym){
	var obj = {};
	obj.normalizedx = xm;
	obj.normalizedy=ym;
	obj.text="";
	obj.id=getUniqId();
	obj.type="hole";
	obj.r=12;
	obj.b=12;
	obj.data={};
	obj.data["iter"]="4";
	obj.data["feedRate"]="40";
	obj.data["feedRateRetract"]="100";
	obj["x"]=parseInt(obj.normalizedx)/parseInt(wideconfig.pixelsPerUnit);
	obj["y"]=parseInt(obj.normalizedy)/parseInt(wideconfig.pixelsPerUnit);
	obj["overridexy"]="false";
	obj["depth"]=wideconfig.depth;
	obj["modeltop"]=wideconfig.modelheightmm;
	addObjectToGraphNoUndo(obj);
	return obj;
}
function addHole(xm,ym){
	var scale = 20;
	var obj = {};
	obj.normalizedx = xm*scale;
	obj.normalizedy=ym*scale;
	obj.text="";
	obj.id=getUniqId();
	obj.type="hole";
	obj.r=12;
	obj.b=12;
	obj.data={};
	obj.data["iter"]="4";
	obj.data["feedRate"]="40";
	obj.data["feedRateRetract"]="100";
	obj["x"]=parseInt(obj.normalizedx)/parseInt(wideconfig.pixelsPerUnit);
	obj["y"]=parseInt(obj.normalizedy)/parseInt(wideconfig.pixelsPerUnit);
	obj["overridexy"]="false";
	obj["depth"]=wideconfig.depth;
	obj["modeltop"]=wideconfig.modelheightmm;
	addObjectToGraphNoUndo(obj);
	return obj;
}

