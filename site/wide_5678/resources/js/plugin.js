/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

var sampleData ={};
var patternData = {};
var anonDrawers = {};
var showPortName;
var stepAnotHandler = {};
function initPluginJS() {
	genericnodes["cd"] = "/images/apply_f2.png";
	genericnodes["classobj"] = "/images/apply_f2.png";
	genericnodes["paramobj"] = "/images/apply_f2.png";
	genericnodes["functionobj"] = "/images/apply_f2.png";
	genericnodes["dummy"] = "/images/apply_f2.png";
	genericnodes["ColDef"] = "/images/apply_f2.png";
	genericnodes["TableDef"] = "/images/apply_f2.png";
	genericnodes["sqlobj"] = "/images/apply_f2.png";
	genericnodes["module"] = "/images/apply_f2.png";	
	genericnodes["ToDoEvent"] = "/images/task.png";	
	genericnodes["Port"] = "/images/task.png";
	genericnodes["AnonDef"] = "/images/task.png";
	genericnodes["TypeDef"] = "/images/task.png";
	genericnodes["Layer"] = "/images/task.png";
	genericnodes["ArbitObject"] = "/images/task.png";
	genericnodes["SampleData"] = "/images/task.png";
	genericnodes["PatternData"] = "/images/task.png";
	genericnodes["portable"] = "/images/task.png";
	genericnodes["Step"] = "/images/task.png";
	genericnodes["elimPoint"] = "/images/task.png";
	genericnodes["org.ptg.util.events.ActivityEvent"]="/images/task.png";
	genericnodes["hole"] = "/images/hole.png";
	genericnodes["circletap"] = "/images/hole.png";	
	//for folder and notes api for searchr
	//genericnodes["notes"]="/images/task.png";
	//genericnodes["folder"]="/images/task.png";

	nodeDrawingHandler["ToDoEvent"]=drawToDoEvent;
	nodeDrawingHandler["SampleData"]=configSample;
	nodeDrawingHandler["PatternData"]=configPatternData;
	nodeDrawingHandler["Port"]=configPort;
	nodeDrawingHandler["AnonDef"]=drawAnonDef;
	nodeDrawingHandler["TypeDef"]=drawTypeDef;
	nodeDrawingHandler["Layer"]=noop;
	nodeDrawingHandler["ArbitObject"]=arbitShapeDraw;
	nodeDrawingHandler["portable"] = drawPortable;
	nodeDrawingHandler["Step"] = drawStep;
	nodeDrawingHandler["elimPoint"] = drawElemPoint;
	nodeDrawingHandler["notes"] = drawElemPoint;
	//for folder and notes api for searchr
	//done else where in code.
	//nodeDrawingHandler["notes"] = drawElemPoint;
	//nodeDrawingHandler["folder"] = drawElemPoint;
    nodeDrawingHandler["hole"] = drawHole;
    nodeDrawingHandler["circletap"] = drawCircleTap;
}

//////////////////////annotations handler /////////////////////////////
function ncharStepAnotHandler(anotname,mytagname, defval,objtype){
	this.anotname = anotname
	this.mytagname = mytagname;
	this.defval = defval;
	this.objtype = objtype;
}
ncharStepAnotHandler.prototype.getTagValue= function (pStep,obj,currTag) {
	var items = findItemsBetweenSteps(pStep,obj,this.objtype);
	var max = {};
	var mc = this.defval;
	for(var j=0;j<items.length;j++){
			mc = max[parseInt(items[j].y)];
			if(mc==null){
			mc =items[j].text.length; 
			max[parseInt(items[j].y)] = mc;
			}else{
					max[parseInt(items[j].y)] = mc +items[j].text.length;
			}
	}
	mc = this.defval;
	for(var k in max){
		if(mc<max[k]){
			mc = max[k]
		}
	}
	return mc
}
var anotHandler = new ncharStepAnotHandler("nchar","fixed-text-width",-1,"ceword");
stepAnotHandler[anotHandler.anotname] = anotHandler;
///////////////
anotHandler = new ncharStepAnotHandler("pattern","pattern-str","","ceword");
anotHandler.getTagValue = function (pStep,obj,currTag) {
	var items = findItemsBetweenSteps(pStep,obj,this.objtype);
	var max = {};
	var mc = this.defval;
	for(var j=0;j<items.length;j++){
			mc = max[parseInt(items[j].y)];
			if(mc==null){
			mc =items[j].text; 
			max[parseInt(items[j].y)] = mc;
			}else{
					max[parseInt(items[j].y)] = mc +" "+items[j].text;
			}
	}
	var arr = new Array();
	for(var j in max){
		arr.push(max[j]);
	}
	mc = this.defval;
	var content = { } ;
	content.items = dojo.toJson(arr);
	var html = doGetHtmlSyncWithContent(getURL("GetRegEx"),content);
	return html;
}
stepAnotHandler[anotHandler.anotname] = anotHandler;
///////////////////////
anotHandler = new ncharStepAnotHandler("default","","","ceword");
anotHandler.getTagValue = function (pStep,obj,tagname) {
	return tagname;
}
stepAnotHandler[anotHandler.anotname] = anotHandler;
////////////
anotHandler = new ncharStepAnotHandler("RecPos","RecPos-tag",0,"ceword");
anotHandler.getTagValue = function (pStep,obj,tagname) {
	var s = 0;
	var e = 0;
	var indx  = sortedStepIndex(obj);
	var pStep = {};
	pStep .x = 0;
	pStep .y = 0;
	for(var i=0;i<indx+1;i++){
		s = e;
		var step = sortedSteps[i]; 
		e  = findRecPos(pStep,step,this.objtype,this.defval);
		e = s+e;
		pStep = step;
	}
	return s+"/"+e;
}
stepAnotHandler[anotHandler.anotname] = anotHandler;


//////////////////////////////////////////////////////
function testPlugin() {
	var a = {};
	a.d = "dada";
	a.b = "baba";
	a.type = "cd";
	a.name = "uni1";
	a.cfg = [ "a", "b", "c" ];
	createGenericNodeItem(a, 300, 300);
}
function getCO(x, y, t, c,type,cn,dataType,id) {
	var a = {};
	a.c = c;
	a.text = t;
	a.textual = true;
	a.cn=cn;
	if(dataType==null)dataType="Object";
	a.dataType=dataType;
	if(id!=null)
		a.id=id;
	if(type==null)
		type="dummy";
	a.type = type;
	return createGenericNodeItem(a, x, y);
}
function getFO(x, y, t, c,type,cn,fn,dataType,classType,id) {
	var a = {};
	a.c = c;
	a.text = t;
	if(id!=null)
		a.id =id;
	a.cn=cn;
	a.fn=fn;
	a.classType=classType
	a.textual = true;
	if(dataType==null)dataType="Object";
	a.dataType=dataType;
	if(type==null)
		type="dummy";
	a.type = type;
	return createGenericNodeItem(a, x, y);
}
function getPO(x, y, t, c,type,cn,fn,pn,index,dataType,grp,classType) {
	var a = {};
	a.c = c;	
	a.text = t;
	a.cn=cn;
	a.fn=fn;
	a.pn=pn;
	a.classType=classType;
	a.grp=(grp==null?"":grp);
	a.index = index;
	a.textual = true;
	if(dataType==null)dataType="Object";
	a.dataType=dataType;
	if(type==null)
		type="dummy";
	a.type = type;
	return createGenericNodeItem(a, x, y);
}

function getGTNI(x, y, t, c,type) {
	var a = {};
	a.c = c;
	a.text = t;
	a.textual = true;
	if(type==null)
		type="dummy";
	a.type = type;
	return createGenericNodeItem(a, x, y);
}
var xx = 300, yy = 300;
var start = 0;

///////////////////////
function addMtdMapping(){
	cfg = new Array();
	old = new Array();
	cfg.push("classname");
	cfg.push("bean");
	cfg.push("mtdname");
	old.push("org.ptg.util.CommonUtil");
	old.push("org.ptg.util.CommonUtil");
	old.push("getGraphObjectsFromJsonData");
	dynaDlg(cfg,old,'getAndMethodMapping(getDlgValues(configdlg,cfg));');
}
function addFldMapping(){
	cfg = new Array();
	old = new Array();
	cfg.push("classname");
	cfg.push("bean");
	cfg.push("fldname");
	old.push("org.ptg.util.CommonUtil");
	old.push("org.ptg.util.CommonUtil");
	old.push("buildDir");
	dynaDlg(cfg,old,'getAndFieldMapping(getDlgValues(configdlg,cfg));');
}
function getAndEventMapping(obj){
	postFormWithContent(getURL("GetBeanDefinition"),obj,function (res){addFieldMap(res);pData.eventtype=obj.classname});
}
function getAndFieldMapping(obj){
	postFormWithContent(getURL("GetBeanDefinition"),obj,function (res){addFieldMap(res)});
}
function addFunctionMap(res){
	var a= dojo.fromJson(res);
	var x = 300,y= 300;
	var ca =null;
	var start=0;
	var fid=null;
	for(var i= 0;i<a.length;i++){//functionobj should always come before paramobj
		var obj = a[i];
		if(obj.type=="functionobj"){
			ca = getFO(x+start,y, obj.fn, "orange",obj.type,obj.cn,obj.fn,obj.dataType,obj.classType);
			fid=ca.id;
		}
		if(obj.type=="paramobj"){
			ca = getPO(x+start,y, obj.pn+":"+obj.index, "#0f0fff",obj.type,obj.cn,obj.fn,obj.pn,obj.index,obj.dataType,fid,obj.classType);
		}
		if(ca!=null){
			ca = findDrawEleById(ca.id).item;
			start += ca.getBBox().width + 10;
		}
	}
}
function getAndMethodMapping(obj){
	postFormWithContent(getURL("GetBeanDefinition"),obj,function (res){addFunctionMap(res)});
}

function addFieldMap(res){
	var x = 200,y= 200;
	var ca =null;
	var start=0;
	var fid=null;
	var a= dojo.fromJson(res);
	for(var i= 0;i<a.length;i++){
		var obj = a[i];
		if(obj.type=="classobj"){
			 ca = getCO(x,y+start, obj.cn, "green",obj.type,obj.cn,obj.dataType);
			 fid=ca.id;
		}
		if(obj.type=="functionobj"){
			ca = getFO(x,y+start, obj.fn, "orange",obj.type,obj.cn,obj.fn,obj.dataType,obj.classType);
			fid=ca.id;
		}
		if(obj.type=="paramobj"){
			ca = getPO(x,y+start, obj.pn, "#0f0fff",obj.type,obj.cn,obj.fn,obj.pn,obj.index,obj.dataType,null,obj.classType);
			fid=ca.id;
		}
		if(ca!=null){
			ca = findDrawEleById(ca.id).item;
			start += ca.getBBox().height + 5;
		}
	}
}
function addGenericObj(obj){
	if(obj.type=="classobj"){
		 getCO(300,300, obj.cn, "green",obj.type,obj.cn,obj.dataType,obj.id);
	}
	if(obj.type=="functionobj"){
		 getFO(300,300, obj.fn, "orange",obj.type,obj.cn,obj.fn,obj.dataType,obj.classType);
	}
	if(obj.type=="paramobj"){
		getPO(300,300, obj.pn, "#0f0fff",obj.type,obj.cn,obj.fn,obj.pn,obj.index,obj.dataType,null,obj.classType);
	}
	
}
function realignFunctionObj(nd){
	var count=0;
	var items = {};
    var main = findDrawEleById(nd.id).item;
    var x = main.attr("x");
    var y = main.attr("y");
    var start = main.getBBox().width+10;
	for(var i=0; i<pData.data.length; i++){
	        var obj = pData.data[i];
	         if(obj.type!=null&&obj.type=="paramobj"){
	        	if(obj.cn==nd.cn&&obj.fn==nd.fn&&obj.grp==nd.id&&obj.pn!=null){
	        		items[count]=obj;
	        		count++;
	        	} 
	         }
	   }
		x = x + start;
	     for(var j=0;j<count;j++){
	       var it = items[j];
	       if(it!=null){
		   var ai = findDrawEleById(it.id);
		   if(ai!=null){
		   var a =ai.item;
		   a.attr({"x":x,"y":y });
		   var ndi = findNodeById(it.id);
		   ndi.x = x;
		   ndi.y = y;
		   ndi.normalizedx = x;
		   ndi.normalizedy = y;
		   start = (a.getBBox().width+10);
		   x = x + start;
	       }
	       }
	     }
}

function validateMapper(){
	var r = {};
	for(var i=0; i<pData.data.length; i++){
        var obj = pData.data[i];
         if(obj.type!=null&&obj.type=="connection"){
           var from = findNodeById(obj.from);
           var to = findNodeById(obj.to);
           if(from==null||to==null){
        	   r[obj.id] = "<a  onclick=\"highliteConn(\'"+obj.id+"\',\'red\')\">From or To is null</a>";
           }else{
        	   if(from.dtype!=null &&to.dtype != null){
        		   if(from.dtype !=to.dtype ){
                	   r[obj.id] = "<a onclick=\"highliteConn(\'"+obj.id+"\',\'red\')\">DataType <u>Miss Matches</u> "+from.dtype +" >> "+to.dtype +"</a>";
        		   }else{
        			   r[obj.id] = "<a onclick=\"highliteConn(\'"+obj.id+"\',\'green\')\">DataType <u>Matches</u> "+from.dtype +" >> "+to.dtype +"</a>";
        		   }
        	   }else{
            	   r[obj.id] = "<a onclick=\"highliteConn(\'"+obj.id+"\',\'red\')\">Cannot validate dataType is not specified"+"</a>";
        	   }
           }
         }
         }
	resultMsgDlgObjResultOnly(r);
}
var classchoiceid= null;
function updateClassesForFunctionObj(id){
	classchoiceid = id;
	var obj = {};
	var cl = prompt("Enter class name to search", "CommonUtil");
	obj.classname = cl;
	
	postFormWithContent(getURL("GetClasses"),obj,function (res){showClassChoices(res)});
}
function showClassChoices(res){
	var a = dojo.fromJson(res);
	var aa = "";
	for(var i  = 0;i<a.length;i++){
		aa +=a[i];
		if(i<a.length-1){
			aa+=":";
		}
	}
	cfg = new Array();
	old = new Array();
	cfg.push("ChooseClass");
	old.push(aa);
	dynaDlg(cfg,old,'setClassToId(getDlgValues(configdlg,cfg));');
}
function setClassToId(cfg){
	var node = findNodeById(classchoiceid);
	var dodraw = false;
	if(node!=null){
		if(node.type=="functionobj"){
			if(node.dataType!=null&&node.classType==node.dataType ){//ctor
				node.cn = cfg.ChooseClass;
				node.classType = cfg.ChooseClass;
				node.dataType = cfg.ChooseClass;
				var b = cfg.ChooseClass.split(".");
				b = (b[b.length-1]);
				node.text = b;
				node.fn = b;
				dodraw = true;
		}else {
			node.cn = cfg.ChooseClass;
			node.classType = cfg.ChooseClass;
			var b = cfg.ChooseClass.split(".");
			b = (b[b.length-1]);
			node.text = "ChooseFunction";
			node.fn = "ChooseFunction";
			dodraw = true;
		}
		}
		else if(node.type=="classobj")
			node.dataType = cfg.ChooseClass;
		else if(node.type=="paramobj" && (node.fn==null||node.fn.length<1)&&node.pn!=null){
			node.classType = cfg.ChooseClass;
			dodraw = true;
		}
	}
	displaySelectedNodeProps(node);
   if(dodraw)
	   draw();
}

function updateFuntionForFunctionObj(id){
	classchoiceid = id;
	var obj = {};
	obj.classname = findNodeById(id).classType;
	
	postFormWithContent(getURL("GetMethods"),obj,function (res){showMethodChoices(res)});
}
function showMethodChoices(res){
	var a = dojo.fromJson(res);
	var aa = "";
	for(var i  = 0;i<a.length;i++){
		aa +=a[i];
		if(i<a.length-1){
			aa+=":";
		}
	}
	cfg = new Array();
	old = new Array();
	cfg.push("ChooseMethod");
	old.push(aa);
	dynaDlg(cfg,old,'setMethodToId(getDlgValues(configdlg,cfg));');
}
function setMethodToId(cfg){
	var node = findNodeById(classchoiceid);
	if(node!=null){
		node.fn = cfg.ChooseMethod;
		var obj={};
		obj.classname = node.classType;
		obj.bean=node.cn;
		obj.mtdname=cfg.ChooseMethod;
		getAndReplaceMethodMapping(obj);
	}
	displaySelectedNodeProps(node);
}
function getAndReplaceMethodMapping(obj){
	postFormWithContent(getURL("GetBeanDefinition"),obj,function (res){replaceFunctionMap(res)});
}
function removeNodeAndGroup(id){
	var nod = findNodeById(id);
	var nds = new Array();

	if(nod!=null){
		var grp  = id;
		nds.push(id);
		for(var i = 0;i<pData.data.length;i++){
			if(pData.data[i].grp!=null&&pData.data[i].grp==id){
				nds.push(pData.data[i].id);
			}
		}
		for(var j in nds){
			for(var k in pData.data){
				if(pData.data[k].from == nds[j]||pData.data[k].to == nds[j]){
					nds.push(pData.data[k].id);
				}
			}
			
		}
		for(var j in nds)
		removeNodeById(nds[j]);
		//removeNodeById(id);
	}
	
}
function replaceFunctionMap(res){
	var nd = findNodeById(classchoiceid);
	var bbold = findDrawEleById(classchoiceid).item.getBBox();
	var x = bbold.x,y= bbold.y;
	removeNodeAndGroup(classchoiceid)
	var a= dojo.fromJson(res);
	var ca =null;
	var start=0;
	var fid=classchoiceid;
	for(var i= 0;i<a.length;i++){//functionobj should always come before paramobj
		var obj = a[i];
		if(obj.type=="functionobj"){
			ca = getFO(x+start,y, obj.fn, "orange",obj.type,obj.cn,obj.fn,obj.dataType,obj.classType,classchoiceid);
			fid=ca.id;
		}
		if(obj.type=="paramobj"){
			ca = getPO(x+start,y, obj.pn+":"+obj.index, "#0f0fff",obj.type,obj.cn,obj.fn,obj.pn,obj.index,obj.dataType,fid,obj.classType);
		}
		if(ca!=null){
			ca = findDrawEleById(ca.id).item;
			start += ca.getBBox().width + 10;
		}
	}
	draw();
}

function showVarChoices(res){
	var a = dojo.fromJson(res);
	var aa = "";
	for(var i  = 0;i<a.length;i++){
		aa +=a[i].pn;
		if(i<a.length-1){
			aa+=":";
		}
	}
	cfg = new Array();
	old = new Array();
	cfg.push("ChooseVariable");
	old.push(aa);
	dynaDlg(cfg,old,'setMethodToVar(getDlgValues(configdlg,cfg));');
}
function setMethodToVar(obj){
var a = findNodeById(classchoiceid)	;
a.pn = obj.ChooseVariable;
a.text = obj.ChooseVariable;
displaySelectedNodeProps(a);
draw();
}

function setVarFld(node){
	var obj ={};
	classchoiceid = node.id;
	obj.classname = node.classType;
	obj.bean=node.cn;
	obj.fldname="*";
	postFormWithContent(getURL("GetBeanDefinition"),obj,function (res){showVarChoices(res);});
}
function addEventTypePlugin(evtname,imgstr){
	if(evtname!=null){
		if(imgstr==null){
		imgstr = "/images/apply_f2.png";
		}
		genericnodes[evtname] = imgstr;
	}
}
function addTextualEventTypeObject(evtname,dispfld){
	if(evtname!=null){
		cfg= new Array();
		old = new Array();
		getEventProp(evtname,function (res){
			var a = dojo.fromJson(res);
			for(var i = 0;i<a.length;i++){
				var def = a[i];
				if(def.searchable==1){
				cfg.push(def.name);
				old.push("");
				}
				dynaDlg(cfg,old,'addTextualEventObjNode(\''+evtname+'\','+'(getDlgValues(configdlg,cfg)),\''+dispfld+'\');');
			}
			
		});
	}
}
function addEventTypeObject(evtname,dispfld,inputVal){
	if(evtname!=null){
		cfg= new Array();
		old = new Array();
		getEventProp(evtname,function (res){
			var a = dojo.fromJson(res);
			for(var i = 0;i<a.length;i++){
				var def = a[i];
				if(def.searchable==1){
				cfg.push(def.name);
				var val ="";
				if(inputVal!=null){
					val = inputVal[def.name];
					if(val==null){
						val="";
					}
				}
				old.push(val);
				}
				dynaDlg(cfg,old,'addEventObjNode(\''+evtname+'\','+'(getDlgValues(configdlg,cfg)),\''+dispfld+'\');');
			}
			
		});
	}
}
function addTextualEventObjNode(type,a,dispfld) {
	a.type = type;
	a.textual  = true; 
	if(dispfld!=null){
		a.text = a[dispfld];
	}
	createGenericNodeItem(a, 300, 300,true);
}
function addEventObjNode(type,a,dispfld) {
	a.type = type;
	if(dispfld!=null){
		a.text = a[dispfld];
	}
	createGenericNodeItem(a, 300, 300);
}
function updateObjectItem(a){
	if(a!=null){
		cfg= new Array();
		old = new Array();
			for(var i in a){
				if(typeof a[i]=="string"&&i!="id"){
				cfg.push(i);
				if(i=="icon"){
					old.push(getIcons());
				}else{
				old.push(a[i]);
					}
				}
			}
			dynaDlg(cfg,old,'applyUpdate(\''+a.id+'\','+'(getDlgValues(configdlg,cfg)));');
	}
}
function updateObject(){
	var a = lastSelectedNode;
	updateObjectItem(a);
}
function applyUpdate(id,a) {
var b = findNodeById(id);
if(b!=null){
	for(i in a ){
		b[i] = a[i];
	}
}
draw();
}

function drawToDoEvent(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
		var id = obj.id;
		var ret = null;
		var temp = width;
		if(obj.textual==null||obj.textual==false){
		 if(obj.title!=null){
			 ret = myrect(xpos, ypos, width, height, fillColor, borderColor,obj.id,obj.title);
		 }else{
	     ret = myrect(xpos, ypos, width, height, fillColor, borderColor,id);
		 }
		 ret.id=id;
	    var sh = findNodeById(id);
	    if(sh!=null){
	    var mg = pCanvas.image(obj.icon, xpos-width/2, ypos-height/2, width, height);
	    	mg.toBack();
	    	ret.img= mg;
	    }        
	    drawElements.push(ret);
		}else{
			ret = drawGenericTextualNode(xpos, ypos,obj,imgsrc);
			ret.id=id;
			drawElements.push(ret);
		}
		ret.item.dblclick(function (evt){
			 var ele=this.node.getAttribute("eleid");
	    	 var nd = findNodeById(ele);
	    	 alert("Now Executing: "+nd.title);
	    	 execElementFromGraph();
		});
		
}
function getIcons(){
	
	return "/images/apply_f2.png:/images/archive_f2.png:/images/back_f2.png:/images/banners:/images/blank.png:/images/cal.gif:/images/calendar-64.png:/images/"+
	"camera.png:/images/cancel.png:/images/cancel_f2.png:/images/cd.png:/images/coding.png:/images/comp1.png:/images/copy.jpeg"+
	"cross.png:/images/css_f2.png:/images/dashboard.png:/images/db.jpeg:/images/desktop.png:/images/documents.png:/images/documents2.png";
	
}

////////////////////////////////PORT
function drawIconifyModule(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	return pCanvas.rectangle(xpos,ypos,width,height);
}

function noop(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	
}
function configPort(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	if((obj.porttype!=null && obj.porttype=="arbitport")||(obj.grp==null)){
		var port = drawArbitPort(obj.normalizedx,obj.normalizedy,3,3,"#B7C3D0", "#B7C3D0",obj.id,null);
		return port
	}
}

function arbitShapeDraw(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	var a = obj.shape;
	if(obj.color==null)
		a.color="purple";
	else 
		a.color = obj.color;
   var pts = dojo.fromJson(a.pts);
   var pathString = getPathFromPointsArray(pts);
   var shapestr = "{var shape=pCanvas.path(\""+pathString +"z"+"\");\n";
   shapestr += "shape.attr(\"stroke\",\""+a.color+"\");";
   shapestr += "shape.attr(\"fill\",\""+a.color+"\");";
   shapestr += "shape.attr(\"fill-opacity\","+.5+");";
   shapestr += "shape.toBack();";
   shapestr+="return shape;}"
   a.design=shapestr;
   var fun = (new Function(shapestr));
   var shape =  fun();
   shape.click(function (evt){
	   var nd = findNodeById(this.node.getAttribute("eleid"));
	   displaySelectedNodeProps(nd);
	   lastSelectedNode = nd;
   });
   if(shape!=null){
  shape.node.setAttribute("eleid",obj.id);
  shape.attr("stroke",a.color);
  shape.attr("stroke-width",2);
  var ret = {};
  ret.id=obj.id;
  ret.item = shape;
  drawElements.push(ret);
  }
}

function drawPort(xpos, ypos, width, height, fillColor, borderColor,id,imgsrc){
	var ret = null;
	var temp = width;
	if(showPortName){
		var n = id.lastIndexOf('.');
		var result = id.substring(n + 1);
		ret = mycircle(xpos, ypos, 6, id,fillColor, borderColor,result);
	}else
		ret = mycircle(xpos, ypos, 6, id,fillColor, borderColor,null);
	ret.item.toFront();
	ret.id=id;
    drawElements.push(ret);
	ret.item.click(function (evt){
		 var ele=this.node.getAttribute("eleid");
    	 var nd = findNodeById(ele);
    	 displaySelectedNodeProps(nd);
    	 if(nd.porttype=="aux" || (nd.porttype=="input")||(nd.porttype=="output")){
    		 var	 val  = "";
    		 if(undefined != nd.portval && nd.portval!=null){
    		    val  = dojo.fromJson(nd.portval);
    		 }
    		 val = prompt("Enter value: ",val);
    		 val   = dojo.toJson(val);
    		 nd.portval  = val;
    		 
    		 updatePortable(nd.id,val);
    	 }
	});
	ret.item.mouseover(function (evt){
			var ele=this.node.getAttribute("eleid");
			var nd = findNodeById(ele);
   	 		displaySelectedNodeProps(nd);   	 		
	});
	return ret;
}
function drawArbitPort(xpos, ypos, width, height, fillColor, borderColor,id,imgsrc){
	var ret = null;
	var temp = width;
/*	if(showPortName)
		ret = mycircle(xpos, ypos, width, id,fillColor, borderColor,id);
	else
*/		ret = mycircle(xpos, ypos, width, id,fillColor, borderColor,null);
	ret.item.toBack();
	ret.id=id;
    drawElements.push(ret);
	ret.item.click(function (evt){
		 var ele=this.node.getAttribute("eleid");
    	 var nd = findNodeById(ele);
    	 displaySelectedNodeProps(nd);
	});
	return ret;
}
function createPortObj(vx,vy,name,pname,type,grp,dtype,portIndex){
    var newNode = {};
	newNode["type"]="Port";
	newNode["id"]=name
	newNode["x"]=vx;
	newNode["normalizedx"]=vx;
	newNode["y"]=vy;
	newNode["normalizedy"]=vy;
	newNode["r"]=6;
	newNode["b"]=6;
    newNode["name"]=name;
    newNode["portname"]=pname
    newNode["porttype"]=type
    newNode["grp"]=grp
    if(portIndex!=null)
    	newNode["portindex"]=portIndex;
    else
    	newNode["portindex"]=0;	
    if(dtype==null)
    	newNode["dtype"]="";
    else
    	newNode["dtype"]=dtype;
	return newNode
}
function createLayerObj(name,layername,tags,itemjson){
    var newNode = {};
	newNode["type"]="Layer";
	newNode["id"]=name
	newNode["x"]=-1;
	newNode["normalizedx"]=-1;
	newNode["y"]=-1;
	newNode["normalizedy"]=-1;
	newNode["r"]=-1;
	newNode["b"]=-1;
    newNode["name"]=name;
    newNode["layername"]=layername
    newNode["tags"]=tags
    newNode["shape"]=null;
    newNode["items"]=itemjson;
	return newNode
}
function createAnonDefObj(x,y,w,h,name,inputs,outputs,aux,anontype,index){
    var newNode = {};
	newNode["type"]="AnonDef";
	newNode["id"]=getUniqId();
	newNode["x"]=x;
	newNode["normalizedx"]=x;
	newNode["y"]=y;
	newNode["normalizedy"]=y;
	newNode["r"]=w;
	newNode["b"]=h;
	newNode["name"] = name;
	newNode["anonType"] = anontype;
    newNode["inputs"]=inputs;//array of string
    newNode["aux"]=    aux;
    newNode["noofaux"]=    aux.length;
    newNode["outputs"]=outputs;
    newNode["noofin"]=inputs.length;
    newNode["noofout"]=outputs.length;
    newNode["index"]=index==null?"":index;
	var head = 20;

	var d = newNode.b/(newNode.inputs.length+1);
    for(var ii=0;ii<newNode.inputs.length;ii++){
    	var vx = x,vy= y+head+d*ii;
    	var port = createPortObj(vx,vy,"inp_"+newNode["id"]+"."+newNode.inputs[ii],newNode.inputs[ii],"input",newNode["id"]);
    	pData.data.unshift(port);
    	}
	d = newNode.b/(newNode.outputs.length+1);
	for(var ii=0;ii<newNode.outputs.length;ii++){
	var vx = x+w,vy = y+head+d*ii;
	var port = createPortObj(vx,vy,"out_"+newNode["id"]+"."+newNode.outputs[ii],newNode.outputs[ii],"output",newNode["id"]);
	pData.data.unshift(port);
	}

	//auxillary
	d = newNode.b/(newNode.aux.length+1);
	for(var ii=1;ii<newNode.aux.length+1;ii++){
	var vx = x+5+d*ii,vy = y+h;
	var port = createPortObj(vx,vy,"aux_"+newNode["id"]+"."+newNode.aux[ii-1],newNode.aux[ii-1],"aux",newNode["id"]);
	pData.data.unshift(port);
	}

    return newNode
}
function realignPortsById(nd){
	var count=0;
	var items = {};
    
for(var i=0; i<pData.data.length; i++){
	        var obj = pData.data[i];
	         if(obj.type!=null&&obj.type=="Port"){
	        	if(obj.grp==nd){
	        		items[count]=obj;
	        		count++;
	        	} 
	         }
	   }

	     for(var j=0;j<count;j++){
	       var it = items[j];
	       if(it!=null){
		   var ai = findDrawEleById(it.id);
              if(ai!=null){
            	  removeDrawEleByItemId(ai.id)
            	  removeDrawElement(ai);
              }
	       }
	      }
}
var mouseOverShape = null;
var mouseOverShapeTrigger  = null;
function showAnonHints(){
	hideCMenu();
	ShowSelectedAdvancedMenu(mouseOverShape);
}
function myAnonRect(xpos, ypos, width, height, fillColor, borderColor,obj,title){
	var id = obj.id;
    var x1 = xpos - width/2;
    var y1 = ypos - height/2;
    var shape= pCanvas.rect(x1,y1,width,height,6);
        shape.attr({"fill": fillColor, "stroke": borderColor, "fill-opacity": .1, "stroke-width": 3, cursor: "move"});
        shape.node.setAttribute("eleid",id);
        shape.drag(move, dragger, up);
        var txt = null;
        if(title!=null){
        	txt = drawText(x1,y1,width, height,title);
        }else{
        	txt = drawText(x1,y1,width, height,id);
        }
        shape.mouseover(function (e){
        if(shift){	
        	var myid = this.node.getAttribute("eleid");
       	  		mouseOverShape = findNodeById(myid);
       	  		console.log("setting mouseover timeout");
       	  		mouseOverShapeTrigger = setTimeout(showAnonHints,500);
        	}
       	  });
        
        shape.mouseout(function (e){
        	if(mouseOverShapeTrigger!=null){
        		console.log("clearing mouseover timeout");
 		   		clearTimeout(mouseOverShapeTrigger );
 		   		mouseOverShape = null;
        	}
 	  });
        
        txt.node.setAttribute("eleid","nodetxt_"+id);
        var ret = {"item":shape,"textnode":txt,"subs":new Array(),img:null};
        return ret;
}

function getAnonShapeDrawable(obj){
	return anonDrawers[obj.anonType];
}

function drawAnonDef(x, y, w,h, fillColor, borderColor,obj,imgsrc){
	borderColor="gray";
	fillColor="gray";
		
	realignPortsById(obj.id);
	var anonRectDrawer = getAnonShapeDrawable(obj);
	if(anonRectDrawer!=null){
		anonRectDrawer (x, y, w,h, fillColor, borderColor,obj,imgsrc);
	}else{
	var id = obj.id;
	var ret = myAnonRect(x,y, obj.r, obj.b, fillColor, borderColor,obj);
	ret.id=id;
	var a = ret.item;
	drawElements.push(ret);
	var head = 20;
	if(ret.textnode!=null&& obj.name!=null){//override
		ret.textnode.attr("text",obj.name+"("+obj.anonType+")");
	}
	//inputs
	var b = a.getBBox();
	var d = b.height/(obj.inputs.length+1);
	for(var ii=0;ii<obj.inputs.length;ii++){
	var vx = x,vy= y+head-obj.b/2+d*ii;
	drawPort(vx-obj.r/2+3, vy, 5,5, "magenta",  "magenta","inp_"+obj.id+"."+obj.inputs[ii],null);
	var port  = findNodeById("inp_"+obj.id+"."+obj.inputs[ii]);
	port.x = vx-obj.r/2;
	port.y=vy;
	port.normalizedx = vx-obj.r/2;
	port.normalizedy = vy;
	}
	//outputs
	d = b.height/(obj.outputs.length+1);
	for(var ii=0;ii<obj.outputs.length;ii++){
	var vx = x+obj.r/2,vy = y-obj.b/2+head+d*ii;
	drawPort(vx-3, vy, 5,5, "purple",  "purple","out_"+obj.id+"."+obj.outputs[ii],null);
	var port  = findNodeById("out_"+obj.id+"."+obj.outputs[ii]);
	port.x = vx;
	port.y=vy;
	port.normalizedx = vx;
	port.normalizedy = vy;
	
	}

	//auxillary
	d = b.width/(obj.aux.length+1);
	for(var ii=1;ii<obj.aux.length+1;ii++){
		var vx = x+5+d*ii,vy = y;
		drawPort(vx-obj.r/2, vy+obj.b/2, 5,5, "blue",  "blue","aux_"+obj.id+"."+obj.aux[ii-1],null);
		var port  = findNodeById("aux_"+obj.id+"."+obj.aux[ii-1]);
		port.x = vx-obj.r/2;
		port.y=vy+obj.b/2;
		port.normalizedx = vx-obj.r/2;
		port.normalizedy = vy+obj.b/2;
		}
	}
}



function createTypeDefObj(x,y,w,h,name,inputs,anontype,extra,types){
    var newNode = {};
	newNode["type"]="TypeDef";
	newNode["id"]=name;
	newNode["x"]=x;
	newNode["normalizedx"]=x;
	newNode["y"]=y;
	newNode["normalizedy"]=y;
	newNode["r"]=w;
	newNode["b"]=h;
	newNode["anonType"] = anontype;
	newNode["name"] = name;
    newNode["inputs"]=inputs;//array of string
    newNode["noofin"]=inputs.length;
    newNode["extra"]=extra;
    newNode["dtypes"]=types;
	var head = 20;

	var d = newNode.b/(newNode.inputs.length+1);
    for(var ii=0;ii<newNode.inputs.length;ii++){
    	var vx = x,vy= y+head+d*ii;
    	var dtype= (newNode.dtypes==null?"":newNode.dtypes[ii]);
    	var port = createPortObj(vx,vy-5,"r_"+newNode["id"]+"."+newNode.inputs[ii],newNode.inputs[ii],"poutput",newNode["id"],dtype);
    	pData.data.unshift(port);
    	port = createPortObj(vx+newNode.r/2,vy-5,"l_"+newNode["id"]+"."+newNode.inputs[ii],newNode.inputs[ii],"pinput",newNode["id"],dtype);
    	pData.data.unshift(port);
    		
    }
    return newNode
}

function drawTypeDef(x, y, w,h, fillColor, borderColor,obj,imgsrc){
	obj.b = obj.inputs.length*18+20;
	realignPortsById(obj.id);
	var id = obj.id;
	var ret = myrect(x,y, obj.r, obj.b, fillColor, borderColor,obj.id);
	
	ret.item.attr({"fill-opacity":.2,"stroke":"gray","opacity":.8,"fill":"gray","stroke-width":3});
	
	ret.id=id;
	if(ret.textnode!=null&&obj.extra!=null){//override
		ret.textnode.attr("text",obj.extra);
	}
	var a = ret.item;
	ret.img= null;
	drawElements.push(ret);
	var head = 20;

	//inputs
	var b = a.getBBox();
	var d = b.height/(obj.inputs.length+1);
	for(var ii=0;ii<obj.inputs.length;ii++){
	var vx = x,vy= y+head-obj.b/2+d*ii;
	drawPortEx(vx-obj.r/2, vy, obj.r,obj.b, "red",  "red",obj.inputs[ii],null,obj.id);
	var port  = findNodeById("r_"+obj.id+"."+obj.inputs[ii]);
	port.x = vx+obj.r/2+3;
	port.y=vy+3;
	port.normalizedx = vx+obj.r/2;
	port.normalizedy = vy+3;
	var port  = findNodeById("l_"+obj.id+"."+obj.inputs[ii]);
	port.x = vx-obj.r/2-3;
	port.y=vy+3;
	port.normalizedx = vx-obj.r/2;
	port.normalizedy = vy+3;

	}

}
function drawPortEx(xpos, ypos, width, height, fillColor, borderColor,id,imgsrc,grpid,drawSeparator){
	var ret = {"item":null,"textnode":null,"subs":null,"img":null};
	var temp = width;
	var subs = new Array();

	var shapeL= pCanvas.circle(xpos-3,ypos,6);
	
	shapeL.toBack();
    shapeL.attr({"fill": fillColor, "stroke": borderColor,"opacity":.6, "fill-opacity": .6, "stroke-width": 1, cursor: "move"});
    shapeL.node.setAttribute("eleid",id);
    shapeL.node.setAttribute("grpid",grpid);
    shapeL.toBack();   
    ret.item=shapeL;
	ret.id=id;
	shapeL.click(function (evt){
		var ele=this.node.getAttribute("eleid");
	    var grp=this.node.getAttribute("grpid");
		 var nd = findNodeById("l_"+grp+"."+ele);
		 displaySelectedNodeProps(nd);
		 if(nd!=null){
			 console.log("Now Updating: "+nd.portname);
			 var olddtype=nd.dtype;
		 	 var val = prompt("Entry type:",olddtype);
		 	 if(val!=null||val!=""){
		 		var nd2 = findNodeById("r_"+grp+"."+ele);
		 		if(nd2!=null)
		 			nd2.dtype=val;
		 		nd.dtype=val;
		 		var parent = findNodeById(grp);
		 		if(parent!=null){
		 			for(var bb in parent.inputs){
		 				if(parent.inputs[bb]==ele){
		 					parent.dtypes[bb]=val;
		 				}
		 				
		 			}
		 		}
		 	 }
		 }
		});

	shapeL.mouseover(function (evt){
		if(sampleData!=null){	
			var ele=this.node.getAttribute("eleid");
	       var grp=this.node.getAttribute("grpid");
	 	   var nd = findNodeById("l_"+grp+"."+ele);
	 	  displaySelectedNodeProps(nd);
	 	   var res = "";
	 	   if(sampleData!=null){
	 	   var samples  = sampleData[grp+ele];
			for(var j in samples){
				if(typeof samples[j] !="function"){
				res+=samples[j];
				res+="\n";
				}
			}
	 	   }
			res+="\n\n";
			if(patternData!=null){
			for(j in patternData[grp+ele]){
				if(typeof patternData[grp+ele][j] !="function"){
					var p =patternData[grp+ele][j];
					var stars =" [";
					var perc = p.matches/p.total*100 ;
					for(var pi =0;pi< perc/10;pi++){
						stars+="*"
					}
					perc+=" %";
					stars+="] ";
					res+= "   "+p.type+" matches "+ perc+stars+" \n";
				}
			}
			}
			res+="\n------------------------------------------------------\n";
			dojo.byId("cmdresult").value = res;
		}
		});
	
	if(drawSeparator==null||drawSeparator==true){
	var shape = pCanvas.line(xpos,ypos+3,xpos+width,ypos+3);
	shape.attr("stroke","gray");
	shape.attr("opacity",.8);
	shape.attr("stroke-dasharray","-");
	subs.push(shape);
	}
	
	var shapeR= pCanvas.circle(xpos+width+3,ypos,6);
    shapeR.attr({"fill": fillColor, "stroke": borderColor,"opacity":.6, "fill-opacity": .6, "stroke-width": 1, cursor: "move"});
    shapeR.node.setAttribute("eleid",id);
    shapeR.node.setAttribute("grpid",grpid);
	subs.push(shapeR);
	shapeR.toBack();
	shapeR.dblclick(function (evt){
	var ele=this.node.getAttribute("eleid");
    var grp=this.node.getAttribute("grpid");
	 var nd = findNodeById("r_"+grp+"."+ele);
	 if(nd!=null){
		 console.log("Now Updating: "+nd.portname);
	 	 updateObjectItem(nd);
	 }
	});
	shapeR.click(function (evt){
		var ele=this.node.getAttribute("eleid");
	    var grp=this.node.getAttribute("grpid");
		 var nd = findNodeById("r_"+grp+"."+ele);
		 displaySelectedNodeProps(nd);
		 if(nd!=null){
			 console.log("Now Updating: "+nd.portname);
			 var olddtype=nd.dtype;
		 	 var val = prompt("Entry type:",olddtype);
		 	 if(val!=null||val!=""){
		 		var nd2 = findNodeById("l_"+grp+"."+ele);
		 		if(nd2!=null)
		 			nd2.dtype=val;
		 		nd.dtype=val;
		 		var parent = findNodeById(grp);
		 		if(parent!=null){
		 			for(var bb in parent.inputs){
		 				if(parent.inputs[bb]==ele){
		 					parent.dtypes[bb]=val;
		 				}
		 				
		 			}
		 		}
		 	 }
		 }
		});
	shapeR.mouseover(function (evt){
	if(sampleData!=null){	
		var ele=this.node.getAttribute("eleid");
       var grp=this.node.getAttribute("grpid");
 	   var nd = findNodeById("r_"+grp+"."+ele);
 	  displaySelectedNodeProps(nd);
 	   var res = "";
 	   if(sampleData!=null){
 	   var samples  = sampleData[grp+ele];
		for(var j in samples){
			if(typeof samples[j] !="function"){
			res+=samples[j];
			res+="\n";
			}
		}
 	   }
		res+="\n\n";
		if(patternData!=null){
		for(j in patternData[grp+ele]){
			if(typeof patternData[grp+ele][j] !="function"){
				var p =patternData[grp+ele][j];
				var stars =" [";
				var perc = p.matches/p.total*100 ;
				for(var pi =0;pi< perc/10;pi++){
					stars+="*"
				}
				stars+="] ";
				perc +=" %"
				res+= "   "+p.type+" matches "+ perc+stars+" \n";
			}
		}
		}
		res+="\n------------------------------------------------------\n";
		dojo.byId("cmdresult").value = res;
	}
	});
    drawElements.push(ret);
	var text = pCanvas.text(xpos,ypos,id);
	var bb2 = text.getBBox();
	text.translate(width/2-bb2.width/2,-bb2.height/2);
	text.toFront();
	text.node.setAttribute("eleid",id);
	text.node.setAttribute("grpid",grpid);
	text.click(function (evt){
		var del = prompt("Delete?: "+ this.attr("text"));
		if(del!=null && del.length>0){
			var id = this.node.getAttribute("eleid");
			var grpid = this.node.getAttribute("grpid");
			var rid = "r_"+grpid+"."+id;
			var lid = "l_"+grpid+"."+id;
			console.log("Removing: "+rid)
			console.log("Removing: "+lid)
			removeNodeById("r_"+grpid+"."+id);
			removeNodeById("l_"+grpid+"."+id);
			var prop = removeAnonTypeProp(grpid,id);
			console.log("Removed: "+prop);
			removeNodeConnections(lid);
			removeNodeConnections(rid);
			draw();
		}
	});

	ret["textnode"]=text;
	ret["subs"]=subs;
	drawElements.push(ret);
	return ret;
}

function removeAnonTypeProp(id,pid){
    var nd = findNodeById(id);
    for(var i=0; i<nd.inputs.length; i++){
        // if(pData.data[i].type != 'connection')
    	{
            var obj = nd.inputs[i];
            if(obj==pid){
                nd.inputs.splice(i,1);
                nd.dtypes.splice(i,1);
                return obj;
            }
        }
    }
    return null;
}
function showLayer(name){
	onLayers[name] = true;
}
function hideLayer(name){
	onLayers[name] = false;
}
function getOnLayerItems(){
	var ret = new Array();
	for(var i=0; i<pData.data.length; i++){
        var obj = pData.data[i];
        if(layerEnabled(obj.layer)){
           	ret.push(obj);
        }
	}	
	return ret;
}
function layerEnabled(layerNameArr){
	if(layerNameArr == undefined || layerNameArr==null)return true;
	for(var i =0;i<layerNameArr.length;i++){
	var layerName=layerNameArr[i];
	if(onLayers[layerName]!=undefined &&onLayers[layerName]!=null ){
		if( (onLayers[layerName]==false)){
			return false;
	}
	}
	}
	return true;
}
function layerDisabled(layerNameArr){
		if(layerNameArr == undefined || layerNameArr==null)return true;
		for(var i =0;i<layerNameArr.length;i++){
		var layerName=layerNameArr[i];
		if(onLayers[layerName]!=undefined &&onLayers[layerName]!=null ){
			if( (onLayers[layerName]==false)){
				return true;
		}
		}
		}
		return false;
}
function getOffLayerItems(){
	var ret = new Array();
	for(var i=0; i<pData.data.length; i++){
        var obj = pData.data[i];
       	if(layerDisabled(obj.layer)){
           	ret.push(obj);
        }
	}	
	return ret;
}
function getLayers(){
	var ret = new Array();
	for(var i=0; i<pData.data.length; i++){
		  var obj = pData.data[i];
		  if(obj.type=="Layer"){
			  ret.push(obj.layername);
		  }
	}
	return ret;
}
function deleteLayer(name){
	var layers = getLayers();
	for(var i=0; i<layers.length; i++){
		  var obj = layers[i];
	if(obj.layername==name){
	removeNodeById(obj.id);
	draw();
	return;
	}
	}
}
function defFileMapping(name ,props,extra,ptypes){
	var a = createTypeDefObj(200,200,200,400,name,props,"FileMapping",extra,ptypes);
	pData.data.unshift(a);
	console.log(a);
	 drawTypeDef(200,200,200,80, "orange","orange",a,null);
	 return a;
}
function defTableMapping(name ,props,extra,ptypes){
	var a = createTypeDefObj(200,200,200,400,name,props,"TableMapping",extra,ptypes);
	pData.data.unshift(a);
	console.log(a);
	 drawTypeDef(200,200,200,80, "orange","orange",a,null);
	 return a;
}
function defObjectMapping(name ,props,extra,ptypes){
	var a = createTypeDefObj(200,200,200,400,name,props,"ObjectMapping",extra,ptypes);
	pData.data.unshift(a);
	console.log(a);
	 drawTypeDef(200,200,200,80, "orange","orange",a,null);
	 return a;
}
function configSample(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	sampleData = obj.data;
}
function configPatternData(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	patternData = obj.data;
}
//layeringEnabled
function fileMapBlock(cfg){
	console.log(cfg);
	defFileMapping(cfg.name,new Array(),cfg.filename,new Array())
	}

var preparePortTrigger = null;
function preparePortable(){
	console.log("Called: "+(new Date()).getMilliseconds());
	if (preparePortTrigger != null) {
		console.log("Resetting timer: "+(new Date()).getMilliseconds());
		clearTimeout(preparePortTrigger);
	}
	preparePortTrigger = setTimeout(preparePortableInner, 200);
}
function preparePortableInner(){
	if(preparePortTrigger!=null){
	clearTimeout(preparePortTrigger);
	preparePortTrigger = null;
	}
	var toRem = {};
	var nodes  = dojo.query(".portable");
	nodes.forEach(function (node,items, vals){
		var grpid = node.getAttribute("grpid");
		toRem[grpid] = grpid;
	});
	var arr = new Array();
	for(var i = 0;i<pData.data.length;i++){
		if(pData.data[i].type!="portable"){
			if(pData.data[i].grp==null ||toRem[pData.data[i].grp]==null ){
				arr.push(pData.data[i]);
			}
		}
	}
	pData.data = arr;
	nodes.forEach(function (node,items, vals){
		var pos = dojo.position(node);
		
		var grpid = node.getAttribute("grpid");
		var grpNode = dojo.byId(grpid);
		var grpPos  = null;
		if(grpNode!=null){
			var ret = findComponentByName(grpid);
			var rotate = ret.rotation;
				if(rotate!=null){
					if(rotate!="0deg"){
						setCSSRotation(grpid,0)
						grpPos = dojo.position(grpid)
						pos = dojo.position(node);
						setCSSRotation(grpid,rotate)
					}else{
						grpPos = dojo.position(grpid)
					}
				}else{
					grpPos = dojo.position(grpid)
				}

		}
//		var prect = pCanvas.rect(pos.x-leftWidth,pos.y-topWidth,pos.w,pos.h);
	//	prect.attr({"fill":"orange","stroke":"orange","fill-color":"red"});
		var cfg = {};
		cfg.id = node.id;
		cfg.grpid = node.getAttribute("grpid");
		cfg.dtype = node.getAttribute("dtype");
		cfg.name = node.getAttribute("name");
		if(cfg.dtype==cfg.id){
			cfg.id=cfg.grpid+"_"+cfg.id;
		}
		var portType = node.getAttribute("ptype");
		var portIndex = node.getAttribute("index");
		var portVal = node.getAttribute("pval");
		if(portType==null )
			portType="prop";
		cfg.dtype=cfg.dtype==null?"java.lang.String":cfg.dtype;
		cfg.y = pos.y-topWidth;//-pos.h/2+6;
		
		cfg.normalizedx = pos.x-leftWidth;
		cfg.normalizedy = pos.y-topWidth;
		cfg.b = pos.h;

		if(grpPos!=null ){/*only wdepends on the parent*/
			cfg.r = grpPos.w;
			cfg.x = grpPos.x-leftWidth;
		}else{
			cfg.r = pos.w;
			cfg.x = pos.x-leftWidth;
		}
		cfg.index = portIndex;
		cfg.type = "portable";
		cfg.portType=portType;
		cfg.portVal = portVal;
		pData.data.unshift(cfg);
		if(portType=="prop"){
		var pid = "l_"+cfg.grpid+"."+cfg.id;
		if(findNodeById(pid)==null){
    	var port = createPortObj(cfg.x-3,cfg.y+cfg.b/2,pid,cfg.id,"pinput",cfg.grpid,cfg.dtype,portIndex);
    	port.portval = cfg.portVal;
    	pData.data.unshift(port);
		}
		pid = "r_"+cfg.grpid+"."+cfg.id;
		if(findNodeById(pid)==null){
    	var port = createPortObj(cfg.x+cfg.r+3,cfg.y+cfg.b/2,pid,cfg.id,"poutput",cfg.grpid,cfg.dtype,portIndex);
    	port.portval = cfg.portVal;
    	pData.data.unshift(port);
		}
		}else if(portType=="output"){
			var pid = "out_"+cfg.grpid+"."+cfg.id;
			if(findNodeById(pid)==null){
	    	var port = createPortObj(cfg.x+cfg.r+3,cfg.y+cfg.b/2,pid,cfg.id,"output",cfg.grpid,cfg.dtype,portIndex);
	    	port.portval = cfg.portVal;
	    	pData.data.unshift(port);
			}
		}else if(portType=="input"){
			var pid = "inp_"+cfg.grpid+"."+cfg.id;
			if(findNodeById(pid)==null){
	    	var port = createPortObj(cfg.x,cfg.y+cfg.b/2,pid,cfg.id,"input",cfg.grpid,cfg.dtype,portIndex);
	    	port.portval = cfg.portVal;
	    	pData.data.unshift(port);
			}
		}else if(portType=="aux"){
			var pid = "aux_"+cfg.grpid+"."+cfg.id;
			if(findNodeById(pid)==null){
	    	var port = createPortObj(cfg.x,cfg.y+cfg.b/2,pid,cfg.id,"aux",cfg.grpid,cfg.dtype,portIndex);
	    	port.portval = cfg.portVal;
	    	pData.data.unshift(port);
			}
			
		}
	});
	draw();
}
///////////////////////////////////////cnc hole

var holedragger = function() {
	this.ox = this.type == "circle" ? this.attr("cx") : this.attr("x");
	this.oy = this.type == "circle" ? this.attr("cy") : this.attr("y");
	this.animate({
		"fill-opacity" : .2
	}, 500);
	if (lastDragged != null) {
		lastDragged.remove();
		lastDragged = null;
	}
	var myid = this.node.getAttribute("eleid");
	if (myid != null) {
		var selectedNode = findNodeById(myid);
		console.log(selectedNode);
		if (selectedNode.type != "Port") {
			lastDragged = this.clone();
			lastDragged.toBack();
			this.attr({
				"stroke-dasharray" : "."
			});
		}
	}
}, holemove = function(dx, dy) {
	var stepSize = parseInt(wideconfig.stepMoverSize);
	var att = this.type == "circle" ? {
		cx : this.ox + dx,
		cy : this.oy + dy
	} : {
		x : this.ox + dx,
		y : this.oy + dy
	};
	// this.attr(att);
	var myid = this.node.getAttribute("eleid");
	if (myid != null) {
		var selectedNode = findNodeById(myid);
		selectedNode.normalizedx = parseInt((mouseX)/stepSize)*stepSize;
		selectedNode.normalizedy = parseInt((mouseY)/stepSize)*stepSize;
		selectedNode.x = selectedNode.normalizedx;
		selectedNode.y =selectedNode.normalizedy;
		
		var nditem = findDrawEleById(myid);
		if (nditem != null) {
			var ndtext = nditem.textnode;
			var item = nditem.item;
			var xpos = selectedNode.normalizedx;
			var ypos = selectedNode.normalizedy;
			selectedNode.text="("+(xpos/wideconfig.pixelsPerUnit).toFixed(2)+wideconfig.units+" , "+(ypos/wideconfig.pixelsPerUnit).toFixed(2)+wideconfig.units+")";
			var ndx = xpos + selectedNode.r ;
			var ndy = ypos - selectedNode.b -10 ;
			var tx = xpos;
			var ty = ypos;
			if (ndtext != null) {
				ndtext.attr({
					"x" : ndx,
					"y" : ndy
				});
				ndtext.attr("text","( "+(xpos/wideconfig.pixelsPerUnit).toFixed(2)+wideconfig.units+" , "+(ypos/wideconfig.pixelsPerUnit).toFixed(2)+wideconfig.units+")")
			}
			if (item != null) {
				if (this.type == "text") {
					item.attr({
						"x" : xpos,
						"y" : ypos
					});
				} else if (this.type == "circle") {
					item.attr({
						"cx" : tx,
						"cy" : ty
					});
				} else {
					item.attr({
						"x" : tx,
						"y" : ty
					});
					// item.attr({"x":(xpos),"y":(ypos)});
				}
			}
			if (selectedNode.type == "functionobj") {
				realignFunctionObj(selectedNode);
			} else if (selectedNode.type == "sqlobj") {
				realignSQLObj(selectedNode);
			}
		}
	}
	pCanvas.safari();
}, holeup = function() {
	if (lastDragged != null) {
		lastDragged.remove();
		lastDragged = null;
	}
	this.animate({
		"fill-opacity" : 0
	}, 500);
	var myid = this.node.getAttribute("eleid");
	if (myid != null) {
		var obj = findNodeById(myid);
		if (obj != null) {
			if (obj.type == "region") {
				lastSelectedRegion = obj;
			} else if (obj.type == "connection") {
				lastSelectedConn = obj;
			} else {
				lastSelectedNode = obj;
			}
			displaySelectedNodeProps(obj);
				var d = dojo.byId('docval');
				d.innerHTML = "<b>" + "" + "</b>";
		}
		var myid = this.node.getAttribute("eleid");
		var evtdata = {};
		evtdata.id = myid;
		evtdata.type = "up";
		sendEvent(geq, [ evtdata ]);
		if (Math.abs(this.ox - this.attr("cx")) > 5
				|| Math.abs(this.oy - this.attr("cy")) > 5) {
			draw();
		} else {
			this.animate({
				"fill-opacity" : .2
			}, 500);
			this.click(null);
		}
	} else {
		this.animate({
			"fill-opacity" : .2
		}, 500);
		this.click(null);
	}
};
function drawHole(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
    var id = obj.id;
    var ret = {"item":null,"textnode":null,"subs":null,"img":null};
    var temp = width;
    var subs = new Array();
    var shape= pCanvas.circle(xpos,ypos,6);
    shape.drag(holemove, holedragger, holeup);
    shape.toBack();
    shape.attr({"fill": fillColor, "stroke": borderColor,"opacity":.6, "fill-opacity": .6, "stroke-width": 1, cursor: "move"});
    shape.node.setAttribute("eleid",id);
    if(obj.grpid!=null){
    shape.node.setAttribute("grpid",grpid);
    }
    shape.toFront();
    var mytext = pCanvas.text(obj.normalizedx+obj.r, obj.normalizedy-obj.b- 10, "( "+(obj.normalizedx/wideconfig.pixelsPerUnit).toFixed(2)+" "+wideconfig.units+" , "+(obj.normalizedy/wideconfig.pixelsPerUnit).toFixed(2)+" "+wideconfig.units+")");
    obj.text= "( "+(obj.normalizedx/wideconfig.pixelsPerUnit).toFixed(2)+" "+wideconfig.units+" , "+(obj.normalizedy/wideconfig.pixelsPerUnit).toFixed(2)+" "+wideconfig.units+")";
    ret.item=shape;
    ret.id=id;
    ret["textnode"]=mytext ;
    ret["subs"]=subs;
    drawElements.push(ret);
    return ret;
    }

function drawCircleTap(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	console.log("Drawing circletap");
    var id = obj.id;
    var ret = {"item":null,"textnode":null,"subs":null,"img":null};
    var temp = width;
    var subs = new Array();
    var shape= pCanvas.circle(xpos,ypos,obj.rad);
    shape.drag(holemove, holedragger, holeup);
    shape.toBack();
    shape.attr({"fill": fillColor, "stroke": borderColor,"opacity":.6, "fill-opacity": .6, "stroke-width": 1, cursor: "move"});
    shape.node.setAttribute("eleid",id);
    if(obj.grpid!=null){
    shape.node.setAttribute("grpid",grpid);
    }
    shape.toFront();
    var text="("+(obj.normalizedx/wideconfig.pixelsPerUnit).toFixed(2)+wideconfig.units+" , "+(obj.normalizedy/wideconfig.pixelsPerUnit).toFixed(2)+wideconfig.units+")";
    var mytext = pCanvas.text(obj.normalizedx+obj.r, obj.normalizedy-obj.b- 10, text);
    
    ret.item=shape;
    ret.id=id;
    ret["textnode"]=mytext ;
    ret["subs"]=subs;
    drawElements.push(ret);
    return ret;
    }

function drawPortable(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	if(obj.portType=="prop"){
	drawPortEx(obj.x, obj.y+obj.b/2, obj.r,obj.b, "red",  "red",obj.id,null,obj.grpid,false);
	var port  = findNodeById("l_"+obj.grpid+"."+obj.id);
	port.x = obj.x-3;
	port.y=obj.y+obj.b/2;
	port.normalizedx = obj.x-3;
	port.normalizedy = obj.y+obj.b/2;
	var port  = findNodeById("r_"+obj.grpid+"."+obj.id);
	port.x = obj.x+obj.r+3;
	port.y=obj.y+obj.b/2;
	port.normalizedx = obj.x+obj.r+3;
	port.normalizedy = obj.y+obj.b/2;
}else if(obj.portType=="output"){
	var port  = findNodeById("out_"+obj.grpid+"."+obj.id);
	var ret = findComponentByName(obj.grpid);
	var grpnode = findNodeById(obj.grpid);
		var rotate = ret.rotation;
		var realpos = null;
		if(rotate!=null){
			if(rotate!="0deg"){
				setCSSRotation(obj.grpid,0);
				realpos = dojo.position(obj.grpid)
				setCSSRotation(obj.grpid,rotate);
				}else{
				realpos = dojo.position(obj.grpid)
			}
		}else{
			realpos = dojo.position(obj.grpid)
		}
		var pd = null;
		
		if(rotate!=null &&rotate!="0deg"){
			pd = drawPort(obj.x+obj.r+3, obj.y+obj.b/2, 5,5, "purple",  "purple","out_"+obj.grpid+"."+obj.id,null);
			if(debug){
			pCanvas.circle(obj.x-3, obj.y+obj.b/2, 5);
			pCanvas.circle(obj.x-3, obj.y+obj.b/2, 7);
			}	
			var rangle=rotate.substr(0,rotate.length-3);
			if(grpnode!=null){
				pd.item.rotate(rangle,realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth);
				if(debug){
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2, 10);
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2, dist(realpos.x+realpos.w/2,realpos.y+realpos.h/2,obj.x-3, obj.y+obj.b/2));
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth, 10).attr("stroke","green");
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth, dist(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth,obj.x-3, obj.y+obj.b/2)).attr("stroke","green");
				}
			}
			}else{
				pd = drawPort(obj.x+obj.r+3, obj.y+obj.b/2, 5,5, "purple",  "purple","out_"+obj.grpid+"."+obj.id,null);
			}
	var pos = dojo.position(pd.item.node);
	port.x = pos.x;
	port.y= pos.y + pos.h/2-topWidth;
	port.normalizedx = port.x;
	port.normalizedy = port.y;
}else if(obj.portType=="input"){
	var port  = findNodeById("inp_"+obj.grpid+"."+obj.id);
	var ret = findComponentByName(obj.grpid);
	var grpnode = findNodeById(obj.grpid);
		var rotate = ret.rotation;
		var realpos = null;
		if(rotate!=null){
			if(rotate!="0deg"){
				setCSSRotation(obj.grpid,0);
				realpos = dojo.position(obj.grpid)
				setCSSRotation(obj.grpid,rotate);
			}else{
				realpos = dojo.position(obj.grpid)
			}
		}else{
			realpos = dojo.position(obj.grpid)
		}
		var pd = null;
		
		if(rotate!=null &&rotate!="0deg"){
			pd = drawPort(obj.x-3, obj.y+obj.b/2, 5,5, "magenta",  "magenta","inp_"+obj.grpid+"."+obj.id,null);
			if(debug){
			pCanvas.circle(obj.x-3, obj.y+obj.b/2, 5);
			pCanvas.circle(obj.x-3, obj.y+obj.b/2, 7);
			}	
			var rangle=rotate.substr(0,rotate.length-3);
			if(grpnode!=null){
				pd.item.rotate(rangle,realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth);
				if(debug){
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2, 10);
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2, dist(realpos.x+realpos.w/2,realpos.y+realpos.h/2,obj.x-3, obj.y+obj.b/2));
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth, 10).attr("stroke","green");
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth, dist(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth,obj.x-3, obj.y+obj.b/2)).attr("stroke","green");
				}
			}
			}else{
				pd = drawPort(obj.x-3, obj.y+obj.b/2, 5,5, "magenta",  "magenta","inp_"+obj.grpid+"."+obj.id,null);
			}
	var pos = dojo.position(pd.item.node);
	port.x = pos.x;
	port.y= pos.y + pos.h/2-topWidth;
	port.normalizedx = port.x;
	port.normalizedy = port.y;
}else if(obj.portType=="aux"){
	var port  = findNodeById("aux_"+obj.grpid+"."+obj.id);
	var ret = findComponentByName(obj.grpid);
	var grpnode = findNodeById(obj.grpid);
		var rotate = ret.rotation;
		var realpos = null;
		if(rotate!=null){
			if(rotate!="0deg"){
				setCSSRotation(obj.grpid,0);
				realpos = dojo.position(obj.grpid)
				setCSSRotation(obj.grpid,rotate);
			}else{
				realpos = dojo.position(obj.grpid)
			}
		}else{
			realpos = dojo.position(obj.grpid)
		}
		var pd = null;
		
		if(rotate!=null &&rotate!="0deg"){
			pd = drawPort(obj.x-3, obj.y+obj.b/2, 5,5, "blue",  "blue","aux_"+obj.grpid+"."+obj.id,null);
			if(debug){
			pCanvas.circle(obj.x-3, obj.y+obj.b/2, 5);
			pCanvas.circle(obj.x-3, obj.y+obj.b/2, 7);
			}	
			var rangle=rotate.substr(0,rotate.length-3);
			if(grpnode!=null){
				pd.item.rotate(rangle,realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth);
				if(debug){
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2, 10);
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2, dist(realpos.x+realpos.w/2,realpos.y+realpos.h/2,obj.x-3, obj.y+obj.b/2));
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth, 10).attr("stroke","green");
				pCanvas.circle(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth, dist(realpos.x+realpos.w/2,realpos.y+realpos.h/2-topWidth,obj.x-3, obj.y+obj.b/2)).attr("stroke","green");
				}
			}
			}else{
				pd = drawPort(obj.x-3, obj.y+obj.b/2, 5,5, "blue",  "blue","aux_"+obj.grpid+"."+obj.id,null);
			}
	var pos = dojo.position(pd.item.node);
	port.x = pos.x+pos.w/2;
	port.y= pos.y + pos.h/2-topWidth;
	port.normalizedx = port.x;
	port.normalizedy = port.y;
}
	}

connDrawingHandler = function (connEle){
	if(connEle!=null){
		var conn  = findNodeById(connEle.id);
		if(conn!=null){
		var from = findNodeById(conn.from);
		var to = findNodeById(conn.to);
		
		    if(from!=null && to!=null && from.type!=null && to.type!=null){
			    	if(from.type=="Port"&&from.porttype=="aux" &&to.type=="Port"&&to.porttype=="aux"){
			    		connEle.subs[0].attr({"opacity":.4,"stroke":"gray"});
			    		connEle.subs[1].attr({"opacity":.4,"stroke":"gray","fill":"gray"});
			    	}else if(((from.type=="Port"&&from.porttype=="arbitport")||from.type=="module" )&&((to.type=="Port"&&to.porttype=="arbitport")||from.type=="module")){
			    		connEle.subs[0].attr({"opacity":.4,"stroke":"blue"});
			    		connEle.subs[1].attr({"opacity":.4,"stroke":"blue","fill":"blue"});
			    	}else if(((from.type=="Port")||to.type=="port")){
			    		connEle.subs[0].attr({"opacity":.2,"stroke":"gray"});
			    		connEle.subs[1].attr({"opacity":.2,"stroke":"gray","fill":"gray"});
			    	}else{
			    		if(wideconfig.mapping_level!=null && parseInt(wideconfig.mapping_level)<5){
			    			connEle.subs[0].attr({"opacity":.4,"stroke":"blue"});
			        		connEle.subs[1].attr({"opacity":.4,"stroke":"blue","fill":"blue"});
			    		}
			    	}
		    }
		}
	}	
}
	
/*************************************
 * id,fun,toport
 *************************************/
function hookDijitToPort(id,fun,toport){
	(function (id,fun,toport){
		var a = dijit.byId(id);
		dojo.connect(a,"onChange",
		function (b){
			console.log(b)
				if(fun!=null){
					fun(b,id,toport) //value from to
				}
			 var nd = findNodeById(toport);
			 if(nd!=null){
				 nd.portval = b;
			 }
			 updatePortable(toport,b);
		});
	})(id,fun,toport);
}	
/*************************************
 * id,fun,toport
 *************************************/
function hookSelToPort(id,fun,toport){
	(function (id,fun,toport){
		var a = dojo.byId(id);
		dojo.connect(a,"onchange",
		function (){
			var b =getSelVal(id);
			console.log(b)
				if(fun!=null){
					fun(b,id,toport) //value from to
				}
			 var nd = findNodeById(toport);
			 if(nd!=null){
				 nd.portval = b;
			 }
			 updatePortable(toport,b);
		});
	})(id,fun,toport);
}	
/*************************************
 * id,fun,toport
 *************************************/
function hookTextToPort(id,fun,toport){
	(function (id,fun,toport){
		var a = dojo.byId(id);
		dojo.connect(a,"onchange",
				function (){
					var b =dojo.byId(id).value;
					console.log(b)
						if(fun!=null){
							fun(b,id,toport) //value from to
						}
					 var nd = findNodeById(toport);
					 if(nd!=null){
						 nd.portval = b;
					 }
					 updatePortable(toport,b);		 
				});
	})(id,fun,toport);
}
/*************************************
 * id
 *************************************/
function clearSelection(id){
	var sel = dojo.byId(id);
	sel.innerHTML="";
}
function setSelectValue (id, val) {
    dojo.byId(id).value = val;
}
/*************************************
 * id,itemArray,clear
 *************************************/
function loadSelection(id,itemArray,clear){
	var sel = dojo.byId(id);
	if(clear)
		sel.innerHTML="";
	for(var i = 0;i<itemArray.length;i++){
        var temp = dojo.create("option", { innerHTML: itemArray[i]}, sel);
	}
}
/*************************************
 * name
 *************************************/
function getSelVal(name){
    var sel = dojo.byId(name);
    if(sel!=null){
    var sidx = sel .selectedIndex;
    var ret = sel .options[sidx].text;
    	return ret;
    }else{
    	return null;
    }
}
/*************************************
 * id,fun
 *************************************/
function connectSel(id,fun){
	(function (id,fun,toport){
		var a = dojo.byId(id);
		a.onchange=function (){
			var b =dojo.byId(id).value;
			console.log(b)
				if(fun!=null){
					fun(b,id,toport) //value from to
				}
		}
	})(id,fun);
}
/*************************************
 * @param where
 *************************************/
function showAllSelect(where){
var ar = new Array();
var nodes  = dojo.query("select");
nodes.forEach(function (node,items, vals){
           console.log(node.id);
        ar.push(node.id);
});
loadSelection(where,ar,true);
}
/*************************************
 * @param where
 *************************************/
function showAllInput(where){
	var ar = new Array();
	var nodes  = dojo.query("input");
	nodes.forEach(function (node,items, vals){
	           console.log(node.id);
	           if(node.type=="text"){
	        	   ar.push(node.id);
	           }
	});
	loadSelection(where,ar,true);
}
/*************************************
 * @param where
 *************************************/
function showAllButton(where){
	var ar = new Array();
	var nodes  = dojo.query("button");
	nodes.forEach(function (node,items, vals){
	           console.log(node.id);
	        	   ar.push(node.id);
	});
	loadSelection(where,ar,true);
}
/***********************
*************************/
function hookButton(id,func){
		(function (id,fun){
			var a = dojo.byId(id);
			dojo.connect(a,"onchange",func);
		})(id,func);


}
/*******************************************
******************************************/
function updatePortable(toport,b){
var nodes  = dojo.query(".portable");
var uniqGrp = {};
nodes.forEach(function (node,items, vals){
	 	var id  = node.getAttribute("id");
		var grpid = node.getAttribute("grpid");
		uniqGrp[grpid] = grpid;
		var portType = node.getAttribute("ptype");
		if(portType=="prop"){
			var pidl = "l_"+grpid+"."+id;
			var pidr = "r_"+grpid+"."+id;
			if(toport ==pidl ||toport ==pidr ){
				dojo.attr(node,"pval",b);
				node.setAttribute("value",b);
			}
		}else if(portType=="output"){
			var pid = "out_"+grpid+"."+id;
			if(toport ==pid){
				dojo.attr(node,"pval",b);
				node.setAttribute("value",b);
			}
		}else if(portType=="input"){
			var pid = "inp_"+grpid+"."+id;
			if(toport ==pid){
				dojo.attr(node,"pval",b);
				node.setAttribute("value",b);
			}
		}else if(portType=="aux"){
			var pid = "aux_"+grpid+"."+id;
			if(toport ==pid){
				dojo.attr(node,"pval",b);
				node.setAttribute("value",b);
			}
		}
		
});
for(var grp in uniqGrp){
	syncModuleCode(grp);
}
}


function drawStep(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	var lw = getStepWord(obj.text, xpos, ypos);
	var shadow = getStepShadow(obj.text, xpos+2, ypos-2);
	var bb2 = lw.getBBox();
	var xxx = bb2.x;
	var yyy = bb2.y+bb2.height+2;
	var xxx2 = bb2.x+bb2.width;
	var vl  = null;
	if(obj.steptype=="horiz")
		vl = hline(xxx2,ypos,"#0f0fff");
	else
		vl = vline(bb2.x+bb2.width/2,yyy,"#0f0fff");
	
	var uln = uline(xxx,yyy,xxx2,yyy,"#0f0fff");
	lw.node.setAttribute("eleid", obj.id);
	lw.node.setAttribute("lineid", obj.line);
	lw.node.setAttribute("wordid", obj.word);
	lw.dblclick(function(evt) {
		var mid = this.node.getAttribute("eleid");
		var lid = lw.node.getAttribute("lineid");
		var wid = lw.node.getAttribute("wordid");
		if (lid > lines.length)
			console
					.log("clicked: " + mid + " " + lid + " "
							+ this.attr("text"));
		else
			console.log("clicked: " + mid + " " + lid + " " + wid);
		var inp = prompt("Enter", this.attr("text"));
		this.attr("text", inp);
		var nd = findNodeById(mid)
		var bbox = this.getBBox();
		nd.r = bbox.width;
		nd.b = bbox.height;
		nd.text = inp;

	});
	var item = {
		"item" : lw,
		"textnode" : null,
		"subs" : new Array(),
		"id" : (obj.id)
	};
	item.subs.push(shadow);
	item.subs.push(vl);
	item.subs.push(uln);
	
	drawElements.push(item);
	var tags = obj.tags;
	var notfirst  = false;
	var sortedIndex = sortedStepIndex(obj);
	var anotarrowx = 600;
	if(sortedIndex>-1){
		anotarrowx+=(sortedIndex*30);
	}

	if(tags!=null){
		for(var i = 0;i<tags.length;i++){
			var currTag = tags[i];
			var anotHandler = stepAnotHandler[currTag];
			if(anotHandler == null && (currTag.indexOf("-")==-1)){
				anotHandler  = stepAnotHandler["default"];
			}
			if(anotHandler!=null){
				if(notfirst==false){
					if(anotHandler.mytagname.length>0){
					setStepAnnotAttribute(obj,anotHandler.mytagname,anotHandler.defval);
					}
					notfirst = true;
				}
				var pStep = prevStep(obj.id);
				if(pStep==null){
					pStep={};
					pStep.x = 0;
					pStep.y = obj.y;
				}
				if(pStep!=null){
				var bb1 = {};
				var bb2 = {};
				var bb3 = {};
				var bb4 = {};
				bb1.x = pStep.x;
				bb1.y = anotarrowx;
				bb1.width = 2;
				bb1.height = 2;
				
				bb2.x = (pStep.x+obj.x)/2-5;
				bb2.y = anotarrowx;
				bb2.width = 2;
				bb2.height = 2;

				bb3.x = (pStep.x+obj.x)/2+5;
				bb3.y = anotarrowx;
				bb3.width = 2;
				bb3.height = 2;

				bb4.x = obj.x;
				bb4.y = anotarrowx;
				bb4.width = 2;
				bb4.height = 2;
				var a = pCanvas.connectionarrow(bb2,bb1,8);
				var b = pCanvas.connectionarrow(bb3,bb4,8);
				a[0].node.setAttribute("eleid", obj.id);
				a[0].attr("stroke-width", "4");
				a[0].attr("stroke", "orange");
				a[1].attr("stroke", "orange");
				a[1].attr("fill", "orange");
				a[1].attr("stroke-dasharray", "-");
				b[0].node.setAttribute("eleid", obj.id);
				b[0].attr("stroke-width", "4");
				b[0].attr("stroke", "orange");
				b[1].attr("stroke", "orange");
				b[1].attr("fill", "orange");
				b[1].attr("stroke-dasharray", "-");

				var mc = anotHandler.getTagValue(pStep,obj,currTag);
				
				var text = pCanvas.text(bb2.x,bb3.y-12,currTag+"("+mc+")");
				text.attr("text-anchor","start");
				text.attr({
					font : '14px Fontin-Sans, Arial'
				});
				var text = pCanvas.text(bb2.x+2,bb3.y+2,"^");
				text.attr("text-anchor","start");
				text.attr({
					font : '14px Fontin-Sans, Arial'
				});
				if(anotHandler.mytagname.length>0){
				setStepAnnotAttribute(obj,anotHandler.mytagname,mc);
				}
				}
				anotarrowx+=28;
			}
		}
	}
}

function setStepAnnotAttribute(stepObj,attr,n){
	var tags = stepObj.tags;
	if(tags!=null){
	var found = false;
	for(var j = 0;j<tags.length;j++){
		if(tags[j].indexOf(attr)==0){
			tags[j] = attr+":"+n;
			found = true;
			break;
		}
	}
	if(!found){
		tags.push(attr+":"+n);
	}
	}else{
		stepObj.tags==new Array();
		tags.push(attr+":"+n);
	}
}
function findItemsBetweenSteps(pStep,step,type){
	var items = new Array();
	for(var i=0;i<pData.data.length;i++){
		var a = pData.data[i];
		if(a.type==type){
			if(a.x>pStep.x && a.x<step.x){
				if(a.y>pStep.y && a.y>step.y){
					items.push(a);
				}
			}
		}
	}
	return items;
}
function getStepWord(lastword, x, y) {
	if (x == null)
		x = curx;
	if (y == null)
		y = cury + lines.length * 20;
	var ret = pCanvas.text(x, y, lastword);
	ret.attr({
		font : '20px Fontin-Sans, Arial'
	});
	ret.attr("font-size", 20);
	ret.attr("text-anchor", "center");
	ret.attr("stroke","gray");
	ret.drag(stepMove, stepDragger, stepUp);
	return ret;
}
function getStepShadow(lastword, x, y) {
	if (x == null)
		x = curx;
	if (y == null)
		y = cury + lines.length * 20;
	var ret = pCanvas.text(x, y, lastword);
	ret.attr({
		font : '20px Fontin-Sans, Arial'
	});
	ret.attr("font-size", 20);
	ret.attr("text-anchor", "center");
	ret.attr("stroke","gray");
	ret.attr("opacity",".3");
	ret.drag(stepMove, stepDragger, stepUp);
	ret.toBack();
	return ret;
}

var stepDragger = function() {
    this.ox = this.type == "circle" ? this.attr("cx") : this.attr("x");
    this.oy = this.type == "circle" ? this.attr("cy") : this.attr("y");
	this.animate({
		"fill-opacity" : .2
	}, 500);
	var myid = this.node.getAttribute("eleid");
	var aDraw = findDrawEleById(myid);
	var obj = findNodeById(myid);
	if(aDraw!=null){
		var bb2 = this.node.getBBox();
		var xxx = bb2.x;
		var yyy = bb2.y+bb2.height+2;
		var vl  = vline(bb2.x+bb2.width/2,0,"orange");
		var lw = getStepWord(this.attr("text"),bb2.x+bb2.width/2,bb2.y);
		var shadow = getStepShadow(this.attr("text"), bb2.x+bb2.width/2,bb2.y);
		
	}
	
}, stepMove = function(dx, dy) {
	var xpos = mouseX;
	var ypos = mouseY;
	var att = {
		x : xpos,
		y : ypos
	}
	this.attr(att);
	var myid = this.node.getAttribute("eleid");
	var aDraw = findDrawEleById(myid);
	var obj = findNodeById(myid);
	if(aDraw!=null){
		if(aDraw.subs!=null){
			{
				aDraw.subs[0].attr("x",mouseX);
				aDraw.subs[0].attr("y",mouseY); 
			}
			{
				aDraw.subs[2].hide();
				aDraw.subs[2].remove();
				
			}

			{
				aDraw.subs[1].hide();
				aDraw.subs[1].remove();
			}
			var old = aDraw.subs;
			aDraw.subs = new Array();
			aDraw.subs.push(old[0]);
			old = null;
			var bb2 = this.node.getBBox();
			var xxx = bb2.x;
			var yyy = bb2.y+bb2.height+2;
			var xxx2 = xxx+bb2.width;
			var vl  = null;
			if(obj.steptype=="horiz")
				vl = hline(xxx2,ypos,"#0f0fff");
			else
				vl = vline(xpos,yyy,"#0f0fff");
			
			var uln = uline(xxx,yyy,xxx2,yyy,"#0f0fff");

			aDraw.subs.push(vl);
			aDraw.subs.push(uln);
		}
	}
	
}, stepUp = function() {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var myid = this.node.getAttribute("eleid");
	var obj = findNodeById(myid)
	if (obj != null) {
		obj.normalizedx = mouseX;
		obj.normalizedy = mouseY;
		obj.x = mouseX;
		obj.y = mouseY;
		var bbox = this.getBBox();
		obj.r = bbox.width;
		obj.b = bbox.height;

		console.log("updated step");
	}
	if (Math.abs(this.ox - this.attr("x")) > 5
			|| Math.abs(this.oy - this.attr("y")) > 5) {
		// this is moved
	} else {
		// this is clicked
	}
	if(Math.abs(this.ox-this.attr("x"))>5||Math.abs(this.oy-this.attr("y"))>5){
		draw();
	}
	if(obj!=null)
		displaySelectedNodeProps(obj);
	pCanvas.safari();
};

function getRealPos(grpid){
	var grpPos  = null;
		var ret = findComponentByName(grpid);
	if(ret!=null){	
		var rotate = ret.rotation;
			if(rotate!=null){
				if(rotate!="0deg"){
					setCSSRotation(grpid,0);
					grpPos = dojo.position(grpid)
					setCSSRotation(grpid,rotate);
				}else{
					grpPos = dojo.position(grpid)
				}
			}else{
				grpPos = dojo.position(grpid)
			}
			grpPos.rotation = getRotDeg(rotate);
			grpPos.x = grpPos.x-leftWidth+grpPos.w/2;
			grpPos.y = grpPos.y-topWidth +grpPos.h/2;
			grpPos.r = grpPos.w;
			grpPos.b = grpPos.h;
			grpPos.id = grpid;
	}
	return grpPos;
}

function findRecPos(zeroStep,obj,objtype,defval){
	var items = findItemsBetweenSteps(zeroStep,obj,objtype);
	var max = {};
	var mc = defval;
	for(var j=0;j<items.length;j++){
			mc = max[parseInt(items[j].y)];
			if(mc==null){
			mc =items[j].text.length; 
			max[parseInt(items[j].y)] = mc;
			}else{
					max[parseInt(items[j].y)] = mc +items[j].text.length;
			}
	}
	mc = defval;
	for(var k in max){
		if(mc<max[k]){
			mc = max[k]
		}
	}
	return mc

}
function drawElemPoint(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	var lw = pCanvas.circle(obj.x,obj.y,10).attr("stroke","pink");
	var lw2 = pCanvas.circle(obj.x,obj.y,11).attr("stroke","pink");
	var item = {
			"item" : lw,
			"textnode" : null,
			"subs" : new Array(),
			"id" : (obj.id)
		};
		item.subs.push(lw2);
		drawElements.push(item);
		var found = false;
		if(removePointFromLayoutArr!=null){
		for(var i=0;i<removePointFromLayoutArr.length;i++){
			if(removePointFromLayoutArr[i].id==obj.id){
				found=true;
				break;
			}
		}
		if(!found){
			removePointFromLayoutArr.push(obj);
		}
	}
}

function validateMapper2(){
	var r = {};
	for(var i=0; i<pData.data.length; i++){
        var obj = pData.data[i];
         if(obj.type!=null&&obj.type=="connection"){
           var from = findNodeById(obj.from);
           var to = findNodeById(obj.to);
           if(from==null||to==null){
        	   highliteConn(obj.id,'red');
           }else{
        	   if(from.dtype!=null &&to.dtype != null){
        		   if(from.dtype !=to.dtype ){
        			   highliteConn(obj.id,'red');
        		   }else{
        			   highliteConn(obj.id,'green');
        		   }
        	   }else{
        		   highliteConn(obj.id,'blue');
        	   }
           }
         }
         }
}
var getRealPosFunc = getRealPos;