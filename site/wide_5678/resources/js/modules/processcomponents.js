/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var proccomptypes = {};
proccomptypes["connection"] ="";
proccomptypes["stream"] ="";
proccomptypes["node"] ="";
proccomptypes["node"] ="";
proccomptypes["group"] ="";
proccomptypes["event"] ="";
proccomptypes["route"] ="";
proccomptypes["textnode"] ="";
proccomptypes["region"] ="";
defaultRotAngle=45;
function proccomps_init(){
	console.log("proccomps Loaded");
	reload(null);
	addFileMenuFunc("Reload Components", reload);
//	addFileMenuFunc("Show Graph Elements", showGraphElementsProcComp);
	postUpdateFun["reloadGraphComps"] = reloadPostDraw;
}  
function reloadPostDraw(name){
	reload(null);
}
function showGraphElementsProcComp(){
	for(var i = 0;i<pData.data.length;i++){
		var item = pData.data[i];
		addProcessItem(item.type,item);
	}
}

function reload(id){
	dojo.byId("procmoduldiv").innerHTML="";
	for (var i in proccomptypes){
		addCompCategory("procmoduldiv",i);
		
	}
	for (var i in genericnodes){
		addCompCategory("procmoduldiv",i);
		
	}
	showGraphElementsProcComp();
	ddtreemenu.createTree("procCompDiv");
	
}
/*function addProcessItemLabel(type,lbl){
	var ul = dojo.byId("comp"+type+"ProcCompDiv");
	if(ul==null){
		addCompCategory("procmoduldiv",type);	
	}
	ul = dojo.byId("comp"+type+"ProcCompDiv");
	var divinnernode = dojo.create("li",{"innerHTML":lbl},ul);	
}
*/
function addShapeItem(type,item){
	var ul = dojo.byId("comp"+item.id+"ProcCompDiv");
	if(ul==null){
		addCompCategory("procmoduldiv",item.id);	
		ul = dojo.byId("comp"+item.id+"ProcCompDiv");
		var infoBtn = dojo.create("input",{"type":"button","value":"v"},ul);
		var infocode = "hideShape('"+item.id+"');";
		var infofun = (new Function(infocode));
		dojo.connect(infoBtn,"onclick",infofun);
		
		var infoBtn2 = dojo.create("input",{"type":"button","value":"e"},ul);
		var infocode2 = "selectShape('"+item.id+"');";
		var infofun2 = (new Function(infocode2));
		dojo.connect(infoBtn2,"onclick",infofun2);
		
		var infoBtn3 = dojo.create("input",{"type":"button","value":"p"},ul);
		var infocode3 = "beginShapePoints('"+item.id+"');";
		var infofun3 = (new Function(infocode3));
		dojo.connect(infoBtn3,"onclick",infofun3);

		var infoBtn3 = dojo.create("input",{"type":"button","value":"f"},ul);
		var infocode3 = "selectShape('"+item.id+"');"+"createFacet('"+item.id+"_"+getUniqId()+"');"+"saveShape('"+item.id+"');draw();";
		var infofun3 = (new Function(infocode3));
		dojo.connect(infoBtn3,"onclick",infofun3);
		
		var infoBtn4 = dojo.create("input",{"type":"button","value":"d"},ul);
		var infocode4 = "deleteShape('"+item.id+"');draw();";
		var infofun4 = (new Function(infocode4));
		dojo.connect(infoBtn4,"onclick",infofun4);
		
		var infoBtn5 = dojo.create("input",{"type":"button","value":"m"},ul);
		var infocode5 = "{if(moveShapeMode==true){moveShapeMode=false;}else {moveShapeMode=true}}";
		var infofun5 = (new Function(infocode5));
		dojo.connect(infoBtn5,"onclick",infofun5);
		
		var infoBtn6 = dojo.create("input",{"type":"button","value":"r"},ul);
		var infocode6 = "{var angle=prompt(\"angle\",45);rotateShape(\""+item.id+"\",angle)}";
		var infofun6 = (new Function(infocode6));
		dojo.connect(infoBtn6,"onclick",infofun6);
		
		var infoBtn7 = dojo.create("input",{"type":"button","value":"R"},ul);
		var infocode7 = "{rotateShape(\""+item.id+"\",defaultRotAngle)}";
		var infofun7 = (new Function(infocode7));
		dojo.connect(infoBtn7,"onclick",infofun7);

		var infoBtn7 = dojo.create("input",{type:"range"  , "max":360,"min":-360,"value":0,"step":1},ul);
		var infocode7 = "{rotateShape(\""+item.id+"\",this.value);draw();}";
		var infofun7 = (new Function(infocode7));
		dojo.connect(infoBtn7,"onchange",infofun7);
		
		var infoBtn8 = dojo.create("input",{"type":"button","value":"S"},ul);
		var infocode8 = "{scaleShape(\""+item.id+"\",defaultScaleX,defaultScaleY);}";
		var infofun8 = (new Function(infocode8));
		dojo.connect(infoBtn8,"onclick",infofun8);
		
		
		var infoBtn11 = dojo.create("input",{type:"range"  , "max":2,"min":.1,"value":1,"step":.1},ul);
		var infocode11 = "{scaleShape(\""+item.id+"\",this.value,this.value);draw();}";
		var infofun11 = (new Function(infocode11));
		dojo.connect(infoBtn11,"onchange",infofun11);
		
		var infoBtn9 = dojo.create("input",{"type":"button","value":"O"},ul);
		var infocode9 = "{offsetShape(\""+item.id+"\",defaultOffsetX,defaultOffsetY);}";
		var infofun9 = (new Function(infocode9));
		dojo.connect(infoBtn9,"onclick",infofun9);

		var infoBtn10 = dojo.create("input",{"type":"button","value":"MD"},ul);
		var infocode10 = "{modifyObjectDataMap(\""+item.id+"\");}";
		var infofun10 = (new Function(infocode10));
		dojo.connect(infoBtn10,"onclick",infofun10);

		

		
		
		
	}
	//console.log("cc>>"+JSON.stringify(item));
	for(var i=0;i<item.facets.length;i++){
		var f = item.facets[i];
		addCompCategory("comp"+item.id+"ProcCompDiv",item.id+"_"+f.id);
		ul = dojo.byId("comp"+item.id+"_"+f.id+"ProcCompDiv");
		var infoBtn = dojo.create("input",{"type":"button","value":"x"},ul);
		var infocode = "deleteShapeFacet('"+item.id+"','"+f.id+"');";
		var infofun = (new Function(infocode));
		dojo.connect(infoBtn,"onclick",infofun);
		
		var infoBtn2 = dojo.create("input",{"type":"button","value":"xx"},ul);
		var infocode2 = "deleteShapeFacetEx('"+item.id+"','"+f.id+"');";
		var infofun2 = (new Function(infocode2));
		dojo.connect(infoBtn2,"onclick",infofun2);
		
		var infoBtn2 = dojo.create("input",{"type":"button","value":"+"},ul);
		var infocode2 = "createConnectorShapeId('"+item.id+"','"+f.id+"','"+item.id+"_"+getUniqId()+"');draw();";
		var infofun2 = (new Function(infocode2));
		dojo.connect(infoBtn2,"onclick",infofun2);
		
		for(var j=0;j<f.connectors.length;j++){
			var c = f.connectors[j];
			var divinnernode = dojo.create("li",{"id":"proccompitem"+c.id,"innerHTML":c.id},ul);	
			var infoBtn3 = dojo.create("input",{"type":"button","value":"?"},divinnernode);
			var infocode = "alert(dojo.toJson(getShapeConn(\'"+item.id+"\'"+",\'"+f.id+"\'"+",\'"+c.id+"\')));";
			var infofun = (new Function(infocode));
			dojo.connect(infoBtn3,"onclick",infofun);
			
			var infoBtn4 = dojo.create("input",{"type":"button","value":"s"},divinnernode);
			var infocode2 = "selectShape('"+item.id+"');bringShapeConnFront(\'"+item.id+"\'"+",\'"+f.id+"\'"+",\'"+c.id+"\');";
			var infofun2 = (new Function(infocode2));
			dojo.connect(infoBtn4,"onclick",infofun2);
			
			
			var infoBtn5 = dojo.create("input",{"type":"button","value":"e"},divinnernode);
			var tmp = "selectShape('"+item.id+"');"+"selectFacetConnector('"+f.id+"','"+c.id+"');beginShapePoints();";
			
			var infocode5 = tmp;
			var infofun5 = (new Function(infocode5));
			dojo.connect(infoBtn5,"onclick",infofun5);
			
			

			
		}	
	}
}
function addDrillItem(type,item,ul){
	console.log("adding Drill menu");
	var infoBtn10 = dojo.create("input",{"type":"button","value":"MD"},ul);
	var infocode10 = "{modifyObjectDataMap(\""+item.id+"\");}";
	var infofun10 = (new Function(infocode10));
	dojo.connect(infoBtn10,"onclick",infofun10);
}

function addCircleTapItem(type,item,ul){
	console.log("adding circletap menu");
	var infoBtn10 = dojo.create("input",{"type":"button","value":"MD"},ul);
	var infocode10 = "{modifyObjectDataMap(\""+item.id+"\");}";
	var infofun10 = (new Function(infocode10));
	dojo.connect(infoBtn10,"onclick",infofun10);
}
function addProcessItem(type,item){
	if(type=="ShapeShape"){
		addShapeItem(type,item);
		return;
	}
	var ul = dojo.byId("comp"+type+"ProcCompDiv");
	if(ul==null){
		addCompCategory("procmoduldiv",type);	
	}
	ul = dojo.byId("comp"+type+"ProcCompDiv");
	var divinnernode = dojo.create("li",{"id":"proccompitem"+item.id,"innerHTML":item.id},ul);	
	if(type=="Layer"){
	var layerBtn = dojo.create("input",{"type":"button","value":"s"},divinnernode);
	var layercode = "getLayerCH(\'"+item.id+"\');";
	var layerfun = (new Function(layercode));
	dojo.connect(layerBtn,"onclick",layerfun);
	
	var layerBtn2 = dojo.create("input",{"type":"button","value":"D"},divinnernode);
	var layercode2 = "hideLayer(\'"+item.id+"\');layeringEnabled=true;draw();";
	var layerfun2 = (new Function(layercode2));
	dojo.connect(layerBtn2,"onclick",layerfun2);
	
	var layerBtn3 = dojo.create("input",{"type":"button","value":"E"},divinnernode);
	var layercode3 = "showLayer(\'"+item.id+"\');layeringEnabled=true;draw();";
	var layerfun3 = (new Function(layercode3));
	dojo.connect(layerBtn3,"onclick",layerfun3);
	}
	if(type=="Pattern"){
		var layerBtn = dojo.create("input",{"type":"button","value":"s"},divinnernode);
		var layercode = "showPattern(\'"+item.id+"\');";
		var layerfun = (new Function(layercode));
		dojo.connect(layerBtn,"onclick",layerfun);
		
	}
	var infoBtn = dojo.create("input",{"type":"button","value":"?"},divinnernode);
	var infocode = "alert(dojo.toJson(findNodeById(\'"+item.id+"\')));";
	var infofun = (new Function(infocode));
	dojo.connect(infoBtn,"onclick",infofun);
	
	var deleteBtn = dojo.create("input",{"type":"button","value":"X"},divinnernode);
//	var bcode = "dojo.destroy(\'"+"proccompitem"+item.id+"\');console.log(\'"+item.id+"\');removeNodeById(\'"+item.id+"\');draw();";
	var bcode = "dojo.destroy(\'"+"proccompitem"+item.id+"\');removeNodeById(\'"+item.id+"\');draw();";
	var fun = (new Function(bcode));
	dojo.connect(deleteBtn,"onclick",fun);

	if(type=="hole"){
		addDrillItem(type,item,divinnernode);
	}
	console.log("Item: "+item.id);
	if(type=="circletap"){
		console.log("found circle tap");
		addCircleTapItem(type,item,divinnernode);
	}

}
function addCompCategory(divid,i){
	var uldivnode = dojo.byId(divid);
var mnode = dojo.create("li", {"class":"submenu","style":"background-image: url(\"open.gif\");"}, uldivnode);
var linode = dojo.create("div",{"id":i+"ModelDiv","innerHTML":i},mnode);	
var ulinnernode = dojo.create("ul",{"id":"comp"+i+"ProcCompDiv","rel":"open","style":"display: block;"},mnode);	
//var divinnernode = dojo.create("li",{"innerHTML":"test"+i},ulinnernode);	
//addProcessItemLabel(i,"test-"+i);
return mnode;
}
function proccomps_update(){
	
	
}
function showPattern(patternId){
	var itm  = {};
	var pattern = findNodeById(patternId);
    var items = dojo.fromJson(pattern.pattern);   
	for(var i=0;i<items.length;i++){
		itm[items[i]]=i;
	}
	var cnt = 0;
	 for(var i=0; i<drawElements.length; i++){
			var obj = drawElements[i];
			var item = obj.item;
			if(item!=null && itm[obj.id]!=null){
			var nd = item.getBBox();
			var pt = {};
			pt.x = nd.x;
			pt.y=nd.y;
			pCanvas.circle(pt.x,pt.y,10);
			pCanvas.text(pt.x,pt.y,""+(itm[obj.id]));
			}
	 }
}
function getLayerCH(layerid){
//	var layerid = "layer2";

	var p = new Array();
	var layer = findNodeById(layerid);
	var itm  = {};
        var items = dojo.fromJson(layer.items);   
	for(var i=0;i<items.length;i++){
		//alert((items[i]));
               itm[items[i]]="";
	}
	 for(var i=0; i<drawElements.length; i++){
	var obj = drawElements[i];
	var item = obj.item;
	if(item!=null && itm[obj.id]!=null){

	var nd = item.getBBox();
	var pt = {};
	pt.x = nd.x;
	pt.y=nd.y;
	p.push(pt);
	 pt = {};
	pt.x = nd.x+nd.width
	pt.y=nd.y;
	p.push(pt);
	 pt = {};
	pt.x = nd.x+nd.width;
	pt.y=nd.y+nd.height;
	p.push(pt);
	 pt = {};
	pt.x = nd.x
	pt.y=nd.y+nd.height
	p.push(pt);
	}
	}

	var url = "/site/FindConvexHull";
	var req = {};
	req.pts = dojo.toJson(p);
	postFormWithContent(url,req,function (data){
	console.log("DAta"+data);
	var a = dojo.fromJson(data);
if(debug){
	for(var i=0;i<a.length;i++){
	var pt = a[i];
	pCanvas.circle(pt.x,pt.y,10);
	pCanvas.text(pt.x,pt.y,""+i);
	}
}
         var path = getPathFromPointsArray(a);
         var pd = pCanvas.path(path);  
         pd.attr({"stroke": Raphael.getColor(),"fill":Raphael.getColor(), "fill-opacity": .4,"opacity":.4, "stroke-width": 1,"stroke-dasharray":"."});
         pd.toBack();
         });


         }