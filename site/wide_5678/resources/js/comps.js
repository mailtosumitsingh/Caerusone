/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

var globals = new Array();
var maps = new Array();
var grids = new Array();
var htmlcomps = new Array();
var canvasModules = {};
var comps = {};
comps["Chart"] = addChart;
comps["Chart 2 Dim"] = addChart2;
comps["Pie Chart"] = addPieChart;
comps["Dimmer"] = addDimmElementImage;
comps["Light"] = addLightElementImage;
comps["Green Bulb"] = addGreenbulb;
comps["Red Bulb"] = addRedbulb;
comps["Tag"] = addTag;
comps["Flat Red Led"] = addFlatRedLED;
comps["Flat Yellow Led"] = addFlatYellowLED;
comps["Flat Green Led"] = addFlatGreenLED;
comps["Blinker"] = addBlinkElementImage;
comps["Dialog"] = showDlg;
comps["Onetime Timer"] = dummy;
comps["Continous Timer"] = dummy;
comps["Google Map"] = handleLocation;
comps["Spread Sheet"] = handleSpreadSheet;
comps["InputPort"] = dummy;
comps["AuxPort"] = dummy;
comps["OutputPort"] = dummy;
comps["Port"] = dummy;
comps["PortMethod"] = dummy;
//complex ports
comps["ComplexInputPort"] = dummy;
comps["ComplexAuxPort"] = dummy;
comps["ComplexOutputPort"] = dummy;
comps["ComplexPort"] = dummy;
comps["ComplexPortMethod"] = dummy;
comps["CovertToPort"] = dummy;
var configs = {};
configs["Chart"] = defaultChartConfigurator;
configs["Chart 2 Dim"] = Char2dConfigurator;
configs["Pie Chart"] = defaultChartConfigurator;
configs["Dimmer"] = defaultConfigurator;
configs["Light"] = defaultConfigurator;
configs["Green Bulb"] = defaultConfigurator;
configs["Red Bulb"] = defaultConfigurator;
configs["Tag"] = defaultConfigurator;
configs["Flat Red Led"] = defaultConfigurator;
configs["Flat Yellow Led"] = defaultConfigurator;
configs["Flat Green Led"] = defaultConfigurator;
configs["Blinker"] = defaultConfigurator;
configs["Dialog"] = defaultConfigurator;
configs["Onetime Timer"] = createTimer;
configs["Continous Timer"] = createContTimer;
configs["Google Map"] = configGoogle;
configs["Spread Sheet"] = configSpreadSheet;
configs["InputPort"] = configInputPort;
configs["OutputPort"] = configOutputPort;
configs["AuxPort"] = configAuxPort;
configs["Port"] = configPort;
configs["PortMethod"] = configPortMethod;
configs["ComplexInputPort"] = configInputPort;
configs["ComplexOutputPort"] = configOutputPort;
configs["ComplexAuxPort"] = configAuxPort;
configs["ComplexPort"] = configPort;
configs["ComplexPortMethod"] = configPortMethod;
configs["CovertToPort"] = configCovertToPortMethod;


var zindex = 0;
var compDiag = new Array();
var windows={};
function addChart(nd, evt, prop, t,chrt,mx,my,configobj) {
	
	var a = createDynamicModuleHolder(chrt);
	if(configobj!=null){
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex= configobj.zindex;
	}else{
			a.style.left = mx;
			a.style.top = my;
			a.style.zIndex= getZIndex();
		}
	var idstr = "bid_"+chrt;
	var chart3 = new dojox.charting.Chart2D(idstr);
	chart3.addPlot("default", {
		type : "Lines"
	});
	chart3.addAxis("x");
	chart3.addAxis("y", {
		vertical : true
	});
	var sid=chrt.substring(0,chrt.indexOf("("));
	var inpid=sid+"values";
	var inpid2 = sid+"aux";
	var d = dojo.create("div",{'ptype':"input",'class':"portable dojoDndItem",'isvisual':true,'index':0,'grpid': chrt, id:inpid,innerHTML:"values"},idstr);
	d = dojo.create("div",{'ptype':"aux",'class':"portable dojoDndItem",'isvisual':false,'index':1,'grpid': chrt, id:inpid2,innerHTML:"aux"},idstr);
	
	if(chrt.indexOf("(")==-1 )sid=chrt;
	globals["" + sid + ""] = chart3;
	var chart3_var = new Array();
	globals["" + sid + "_var"] = chart3_var;
	
	if(chrt.indexOf("(")==-1){//if name contains ( means it is non streaming version and is being used in task plan
	var code = "var " + sid + " = globals[\"" + sid + "\"];";
	code += "if(globals[\"" + sid+ "_var\"].length>100)globals[\"" + sid+ "_var\"].splice(0,1);";
	code += "globals[\"" + sid + "_var\"].push(" + prop + ");"
	code+=("console.log(globals[\"" + sid + "_var\"]);\n");
	code += "" + sid + ".addSeries(\"Series 4\", globals[\"" + sid	+ "_var\"]);";
	code += "" + sid + ".render();";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
	}else{
		var code="";
		code += "console.log(jdata);\n";
		code+= "var tid = jdata.targetId.substring(0,jdata.targetId.indexOf(\"(\"));\n";
		code+=("if ( \""+sid+"\"=="+"tid ){\n")
		code += "var myid = \""+sid+"."+inpid+"\";\n";
		code += "if(jdata.ctx[myid]!=null){\n";
		code += "console.log(\"My val\"+ parseInt(jdata.ctx[myid]));\n";
		code += "if(globals[\"" + sid + "_var\"].length>100)globals[\"" + sid 		+ "_var\"].splice(0,1);\n";
		code += "globals[\"" + sid + "_var\"].push(parseInt(jdata.ctx[myid])); \n";
		code += "var my = globals[\""+ sid + "\"];\n";
		code += ("my" + ".addSeries(\"Series 1\", globals[\"" + sid + "_var\"]);\n");
		code+=("console.log(globals[\"" + sid + "_var\"]);\n");
		code += ("my" + ".render();\n");
		code +="\t}\n";
		code +="}\n";
		console.log(code);
		try {
			var fun = (new Function("jdata", code));
			dojo.subscribe("ExecCtxEvent", null, fun);
		} catch (e) {
			alert(e.message || e);
		}

	}
}
function addPieChart(nd, evt, prop, t,chrt,mx,my,configobj) {

	var a = createDynamicModuleHolder(chrt);
	if(configobj!=null){
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex= configobj.zindex;
	}else{
		a.style.left = mx;
		a.style.top = my;
		a.style.zIndex= getZIndex();
	}
	var idstr = "bid_"+chrt;
	var chart3 = new dojox.charting.Chart2D(idstr);
	chart3.addPlot("default", {
		type : "Pie",
		fontColor : "white",
		labelOffset : 40
	});
	var sid=chrt.substring(0,chrt.indexOf("("));
	var inpid=sid+"values";
	var inpid2 = sid+"aux";
	var d = dojo.create("div",{'ptype':"input",'class':"portable dojoDndItem",'isvisual':true,'index':0,'grpid': chrt, id:inpid,innerHTML:"values"},idstr);
	d = dojo.create("div",{'ptype':"aux",'class':"portable dojoDndItem",'isvisual':false,'index':1,'grpid': chrt, id:inpid2,innerHTML:"aux"},idstr);
	if(chrt.indexOf("(")==-1 )sid=chrt;
	globals["" + sid + ""] = chart3;
	var chart3_var = new Array();
	globals["" + sid + "_var"] = chart3_var;
	
	if(chrt.indexOf("(")==-1){//if name contains ( means it is non streaming version and is being used in task plan
	var code = "var " + sid + " = globals[\"" + sid + "\"];";
	code += "if(globals[\"" + sid+ "_var\"].length>100)globals[\"" + sid			+ "_var\"].splice(0,1);";
	code += "globals[\"" + sid + "_var\"].push(" + prop + ");"
	code += "" + sid + ".addSeries(\"Series 4\", globals[\"" + sid+ "_var\"]);";
	code+="console.log(globals[\"" + sid+ "_var\"]);";
	code += "" + sid + ".render();";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
	}else{
		var code="";
		code += "console.log(jdata);\n";
		code+= "var tid = jdata.targetId.substring(0,jdata.targetId.indexOf(\"(\"));\n";
		code+=("if ( \""+sid+"\"=="+"tid ){\n")
		code += "var myid = \""+sid+"."+inpid+"\";\n";
		code += "if(jdata.ctx[myid]!=null){\n";
		code += "console.log(\"My val\"+parseInt( jdata.ctx[myid]));\n";
		code += "if(globals[\"" + sid + "_var\"].length>100)globals[\"" + sid 		+ "_var\"].splice(0,1);\n";
		code += "globals[\"" + sid + "_var\"].push(parseInt(jdata.ctx[myid])); \n";
		code += "var my = globals[\""+ sid + "\"];\n";
		code += ("my" + ".addSeries(\"Series 4\", globals[\"" + sid + "_var\"]);\n");
		code += ("my" + ".render();\n");
		code +="\t}\n";
		code +="}\n";
		console.log(code);
		try {
			var fun = (new Function("jdata", code));
			dojo.subscribe("ExecCtxEvent", null, fun);
		} catch (e) {
			alert(e.message || e);
		}

	}
}
function addChart2(nd, evt, prop, t,chrt,mx,my,configobj) {
	
	var a = createDynamicModuleHolder(chrt);
	if(configobj!=null){
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex= configobj.zindex;
	}else{
		a.style.left = mx;
		a.style.top = my;
		a.style.zIndex= getZIndex();
	}
	var idstr = "bid_"+chrt;
	var chart1 = new dojox.charting.Chart2D(idstr);
	chart1.addPlot("default", {
		type : "Lines"
	});
	chart1.addAxis("x");
	chart1.addAxis("y", {
		vertical : true
	});
	globals["" + chrt + ""] = chart1;
	var chart2x_var = new Array();
	globals["" + chrt + "x_var"] = chart2x_var;
	var chart2y_var = new Array();
	globals["" + chrt + "y_var"] = chart2y_var;
	2
	var code = "var " + chrt + " = globals[\"" + chrt + "\"];";
	code += "if(globals[\"" + chrt + "x_var\"].length>100)globals[\"" + chrt
			+ "x_var\"].splice(0,1);";
	code += "if(globals[\"" + chrt + "y_var\"].length>100)globals[\"" + chrt
			+ "y_var\"].splice(0,1);";
	code += "globals[\"" + chrt + "x_var\"].push(" + prop.x1 + ");"
	code += "globals[\"" + chrt + "y_var\"].push(" + prop.x2 + ");"
	code += "" + chrt + ".addSeries(\"Series 2\", globals[\"" + chrt
			+ "x_var\"],{stroke: {color: \"red\", width: 2}});";
	code += "" + chrt + ".addSeries(\"Series 3\", globals[\"" + chrt
			+ "y_var\"],{stroke: {color: \"green\", width: 2}});";
	code += "" + chrt + ".render();";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addTag(nd, evt, prop, t,mx,my,configobj) {
	var code = "tag(\"" + nd + "\"," + prop + "," + t + ");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addFlatRedLED(nd, evt, prop, t,mx,my,configobj) {
	
	var code = "flatRedLED(\"" + nd + "\"," + t + ");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addFlatYellowLED(nd, evt, prop, t,mx,my,configobj) {
	
	var code = "flatYellowLED(\"" + nd + "\"," + t + ");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addFlatGreenLED(nd, evt, prop, t,mx,my,configobj) {
	
	var code = "flatGreenLED(\"" + nd + "\"," + t + ");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addBlinkElementImage(nd, evt, prop, t,mx,my,configobj) {
	
	var code = "blinkElementImage(\"" + nd + "\"," + t + ");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addDimmElementImage(nd, evt, prop, t,mx,my,configobj) {
	
	var code = "dimmElementImage(\"" + nd + "\"," + t + ");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addLightElementImage(nd, evt, prop, t,mx,my,configobj) {
	
	var code = "lightElementImage(\"" + nd + "\"," + t + ");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addGreenbulb(nd, evt, prop, t,mx,my,configobj) {
	var code = "greenbulb(\"" + nd + "\");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function addRedbulb(nd, evt, prop, t,mx,my,configobj) {
	var code = "redbulb(\"" + nd + "\");";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function showDlg(nd, evt, prop, t,mx,my,configobj) {
	var code = "dynaDlg(jdata);";
	try {
		var fun = (new Function("jdata", code));
		dojo.subscribe(evt, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function dynaDlg(jdata) {
}
	
function defaultChartConfigurator(f, a,compname,isui,mx,my,configobj) {
    dojo.require("dojox.charting.Chart2D");

		if(isui==true){
		var evt = prompt('Enter Event', 'HeartBeatEvent');
		var prop = prompt('Enter Property', 'jdata.millis');
		var t = prompt('Enter Time', '1000');
		var chrt = prompt('Enter Chart Name', 'Chart');
		compDiag.push( {"name":compname,"aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"event":evt,"prop":prop,"t":t,"chrt":chrt,"id":chrt});
		f(a==null?0:a.id, evt, prop, t,chrt,null,null,null);
		}else{
			var evt = configobj.event;
			var prop = configobj.prop;
			var t = configobj.t;
			var chrt = configobj.chrt;
			if(f!=null){
				f(a, evt, prop, t,chrt,mx,my,configobj);// in case of non ui a
														// is aid
			}
		}
	}

function defaultConfigurator(f, a,compname,isui,mx,my,configobj) {
	if(isui==true){
	var evt = prompt('Enter Event', 'HeartBeatEvent');
	var prop = prompt('Enter Property', 'jdata.millis');
	var t = prompt('Enter Time', '1000');
	var uid = prompt('Enter UID', 'unique1000');
	compDiag.push({"name":compname,"aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"event":evt,"prop":prop,"t":t,"id":uid});
	f(a.id, evt, prop, t,null,null,null);
	}else{
		var evt = configobj.event;
		var prop = configobj.prop;
		var t = configobj.t;
		if(f!=null){
			f(a, evt, prop, t,mx,my,configobj);// in case of non ui a is aid
		}
	}
}
function Char2dConfigurator(f, a,compname,isui,mx,my,configobj) {
    dojo.require("dojox.charting.Chart2D");

	if(isui==true){
	var evt = prompt('Enter Event', 'HeartBeatEvent');
	var prop1 = prompt('Enter Property for Dim1', 'jdata.millis');
	var prop2 = prompt('Enter Property for Dim2', 'jdata.millis');
	var t = prompt('Enter Time', '1000');
	var chrt = prompt('Enter Chart Name', 'Chart');
	compDiag.push({"id":chrt,"name":compname,"aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"event":evt,"prop1":prop1,"prop2":prop2,"t":t,"chrt":chrt});
	f(a==null?0:a.id, evt, {
		"x1" : prop1,
		"x2" : prop2
	}, t,null,null,null);
	}else{
	var evt = configobj.evt;
	var prop = configobj.prop;
	var t = configobj.t;
	var chrt = configobj.chrt;
	if(f!=null){
		f(a, configobj.event, {
			"x1" : configobj.prop1,
			"x2" : configobj.prop2
		}, configobj.t,chrt,mx,my,configobj);
	}
	}
}
function handleLocation(jdata) {
	var myLatlng = new google.maps.LatLng(jdata.lat, jdata.lon);
	var map = maps["gmapsnode"];
	var lmarker = null;
	if (lmarker == null && map != null) {
		lmarker = new google.maps.Marker( {
			position : location,
			map : map,
			title : "New Search"
		});
	} else {
		lmarker.set_title("New Search");
		lmarker.set_position(location);
	}
}
function configSpreadSheet(f,a,compname,isui,mx,my,configobj){
	var evt =null;
	var dataurl = null;
	var layouturl = null;
	var storeType = null;
	if(isui==true){
	evt = prompt('Please give a unique name', 'Grid1');
	storeType = prompt('Choose:CSV or RW', 'CSV');
	dataurl = prompt('Data URL', '/wide_5678/data/movies.csv');
	layouturl = prompt('Layout URL', '/wide_5678/data/movies.layout.json');
	compDiag.push( {"id":evt,"name":compname,"aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"ss":evt,"dataurl":dataurl,"layouturl":layouturl,"storeType":storeType});
	}else{
		evt = configobj.id;
		dataurl = configobj.dataurl;
		layouturl = configobj.layouturl;
		storeType = configobj.storeType;
		if(storeType==null){
			storeType='CSV';
		}
	}

	var a = createDynamicModuleHolder(evt);
	if(isui==false){
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex= configobj.zindex;
	}else{
		a.style.left = mx;
		a.style.top = my;
		a.style.zIndex= getZIndex();
	}
	var idstr = "bid_"+evt;
    var store = null;
    
    if(storeType=='RW'){
        store = new dojo.data.ItemFileWriteStore({
           url: dataurl 
            });
        }else {
        store = new dojox.data.CsvStore({
           url: dataurl 
    });}
    	
    var layout = null;
    doGetHtml(layouturl,function (data){
    	layout = dojo.fromJson(data);
    var grid = new dojox.grid.EnhancedGrid({
        store: store,
        structure: layout,
        plugins: {
            nestedSorting: true,
            dnd: true,
            indirectSelection: {
                name: "Selection",
                width: "70px",
                styles: "text-align: center;"
            }
        }
    });
    dojo.byId(idstr).appendChild(grid.domNode);
    grid.startup();
    grids[evt] = grid;
    globals[evt+"_store"]=store;
    globals[evt+"_layout"]=layout;
    });
}
function handleSpreadSheet(jdata){
	
}
function configGoogle(f, a,compname,isui,mx,my,configobj) {
	var name = null;
	if(isui==true){
	name = prompt('Enter Map Name', 'Map1');
	compDiag.push({"name":compname,"id":name,"aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"event":"","mapname":name});
	}else{
		name = configobj.id;
	}
	var myLatlng = new google.maps.LatLng(39.7071188, -99.3451118);
	var geocoder = new google.maps.Geocoder();
	var myOptions = {
		zoom : 3,
		center : myLatlng,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	}
	var a = createDynamicMapModuleHolder(name);
	if(isui==false){
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex= configobj.zindex;
	}else{
		a.style.left = mx;
		a.style.top = my;
		a.style.zIndex= getZIndex();
	}

	var map = new google.maps.Map(document.getElementById("bid_"+name), myOptions);
	maps[name] = map;
}
function dummy(jdata) {
}
function createTimer(f, a,compname,isui,mx,my,configobj) {
	var code = null;
	var t = null;
	if(isui==true){
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		t = prompt('Enter Time', '1000');
		var uid =prompt('Enter Uid', 'unique1');  
		compDiag.push({"id":uid,"name":compname,"aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"t":t,"code":dojo.toJson(code)});
	}else{
		code = dojo.fromJson(configobj.code); 
		t = configobj.t;
	}
	try {
		var fun = (new Function(code));
		setTimeout(fun, t);
	} catch (e) {
		alert(e.message || e);
	}
}

function createContTimer(f,a,compname,isui,mx,my,configobj) {
	var code = null;
	var t = null;
	var hint = null; 
	if(isui==true){
	code = prompt('Enter Code', 'alert(\"OnTimer\");');
	t = prompt('Enter Time', '1000');
	var uid =prompt('Enter Uid', 'unique1');
	hint = prompt('Enter UniqueHint', 'unique_' + Math.ceil((Math.random() * 10)));
	code += "setTimeout(globals[\"" + hint + "\"]," + t + ");";
	compDiag.push({"id":uid,"name":compname,"aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"t":t,"hint":hint,"code":dojo.toJson(code)});
	}else{
		code = dojo.fromJson(configobj.code); 
		t = configobj.t;
		hint = configobj.hint;
	}
	try {
		
		var fun = (new Function(code));
		globals[hint] = fun;
		setTimeout(fun, t);
	} catch (e) {
		alert(e.message || e);
	}
}
function getModulesFor(givenPageId,func) {
	getModulesForPage(givenPageId, function(a) {
		for(var i=0;i<a.length;i++){ 
			var oneEntry = a[i];
			if(i+1==a.length){
			addModulesToPage(oneEntry.contid, oneEntry.instanceid,
					oneEntry.modulepath, oneEntry.modulename,
					oneEntry.moduleparams, oneEntry.jscomp,func,oneEntry);
			}else{
				addModulesToPage(oneEntry.contid, oneEntry.instanceid,
						oneEntry.modulepath, oneEntry.modulename,
						oneEntry.moduleparams, oneEntry.jscomp,null,oneEntry);

			}
		}
	});
}
function addModulesToPage(cid, instid, path, modulename, moduleparams, jscomp,func,oneEntry) {
	var cnode = dojo.byId(cid);
	if (cnode != null) {
		getModule(cnode, instid, path, modulename, moduleparams, jscomp,func);
		if(oneEntry!=null){
			if(oneEntry.disp==false){
					hideWindow(instid);
			}
		}
	}
}
function getModule(cnode, instid, path, modulename, moduleparams, jscomp,func) {
	registerWindow(instid);
	var mnode = dojo.create("div", null, cnode);
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", "movablenode");
	dojo.attr(mnode, "style", moduleparams);
	doGetHtml(path, function(response) {
		var dv = dojo.create("div", {
			innerHTML : "<div class=\"fixmoduleheading\" id=\"heading_"+instid+"\">"+instid+"<img src='/site/images/sort1.png' width='16' height='16' style='position: relative;float:right;' onclick='hideWindow(\""+instid+"\")'/></div>"+response,
			style : "width:\"100%\";height:\"100%\"",
			id : "bid" + instid
		}, mnode);
		// dojo.connect(dojo.byId("heading_"+instid),"onClick",function
		// (evt){alert("dada");});
		doGetHtml("/site/wide_5678/resources/js/modules/" + jscomp, function(a) {
			dojo.eval(a);
			dojo.eval(modulename + "_init();")
			var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
			dojo.connect(nd,"onMoveStop",function (mover){
				dojo.style(mover.host.node,"zIndex",getZIndex());

				var pos = dojo.position(mover.host.node);
				var evtdata = {};
	        	evtdata.id = mover.host.node.id;
	        	evtdata.type="ssmmovestop";
	        	evtdata.pos = pos;
	        	sendEvent(geq, [evtdata]) ;

				var item = findComponentByName(mover.host.node.id)
				if(item!=null){
					item.zindex = (getZIndex());
					item.width = mover.node.style.width;
					item.height = mover.node.style.height;
					item.left = mover.node.style.left;
					item.top = mover.node.style.top;

				}
				
			});
			var handle = new dojox.layout.ResizeHandle( {
				targetId : instid,
				minWidth:10,
				minHeight:10,
			}).placeAt("bid" + instid);
			if(func!=null)
				func();
		});
	});
}

function createDynamicModuleHolder(str) {
	var cnode = dojo.byId("dynamicnodes");
	var mnode = dojo.create("div", null, cnode);
	var instid = "" + str;
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", "movablenode");
	dojo.attr(mnode, "style", "left: 400px;top: 400px;overflow:hide");
	var dv = dojo.create("div", {
		innerHTML : "<div class=\"heading\" id=\"heading_"+instid+"\" ondblclick=\"var temp = dojo.byId('"+str+"');dojo.attr(temp,'min','true');" +
				"temp.style.width='200px';temp.style.height='50px';temp.style.top=(getWindowSize().h-50)+'px';\">" + str + "</div><div id=\"bid_"+str+"\" style=\"width:100%;height:100%;overflow:auto;\"></div>",
		style : "width:100%;height:100%",
		id :"cont_"+instid
	}, mnode);
	
	var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
	dojo.connect(nd,"onMoveStop",function (mover){
		var ided = mover.host.node.id;
		dojo.style(mover.host.node,"zIndex",getZIndex());

		var pos = dojo.position(mover.host.node);
		var evtdata = {};
    	evtdata.id = mover.host.node.id;
    	evtdata.type="ssmmovestop";
    	evtdata.pos = pos;
    	sendEvent(geq, [evtdata]) ;

		var comp = findComponentByName(ided);
		if(comp!=null){
			comp.left = mover.node.style.left;
			comp.top = mover.node.style.top;
			comp.width = mover.node.style.width;
			comp.height = mover.node.style.height;
			comp.zindex = (getZIndex());
		}
		preparePortable();
	});
	var handle = new dojox.layout.ResizeHandle( {
		targetId : instid,id:"place"+instid,
		minWidth:10,
		minHeight:10
	}).placeAt("cont_"+instid);
	return mnode;
}
function createHeadlessDynamicModuleHolder(str) {
	var cnode = dojo.byId("dynamicnodes");
	var mnode = dojo.create("div", null, cnode);
	var instid = "" + str;
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", "movablenode");
	dojo.attr(mnode, "style", "left: 400px;top: 400px;width:60px;height:40px;overflow:hide;border: 1px solid silver");
	var dv = dojo.create("div", {
		innerHTML : "<div class=\"heading\" id=\"heading_"+instid+"\">"+instid+"</div>"+"<div id=\"bid_"+str+"\" style=\"width:100%;height:100%;overflow:auto;\"></div>",
		style : "width:100%;height:100%",
		id :"cont_"+instid
	}, mnode);
	var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
	dojo.connect(nd,"onMoveStop",function (mover){
		var ided = mover.host.node.id;
		dojo.style(mover.host.node,"zIndex",getZIndex());

		var pos = dojo.position(mover.host.node);
		var evtdata = {};
    	evtdata.id = mover.host.node.id;
    	evtdata.type="ssmmovestop";
    	evtdata.pos = pos;
    	sendEvent(geq, [evtdata]) ;

		var comp = findComponentByName(ided);
		if(comp!=null){
			comp.left = mover.node.style.left;
			comp.top = mover.node.style.top;
			comp.width = mover.node.style.width;
			comp.height = mover.node.style.height;
			comp.zindex = (getZIndex());
		}
	});
	var handle = new dojox.layout.ResizeHandle( {
		targetId : instid,id:"place"+instid,
		minWidth:10,
		minHeight:10
	}).placeAt("cont_"+instid);
	return mnode;
}
function createDynamicMapModuleHolder(str) {
	var cnode = dojo.byId("dynamicnodes");
	var mnode = dojo.create("div", null,cnode);
	var instid = "" + str;
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", "movablenode");
	dojo.attr(mnode, "style", "left: 400px;top: 400px");
	mnode.innerHTML=	"<div class=\"heading\"  id=\"heading_"+instid+"\"  ondblclick=\"var temp = dojo.byId('"+str+"');temp.style.height='200px';\">" + str + "</div><div id=\"bid_"+str+"\" style=\"width:100%;height:95%\"></div>";
	
	var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
	dojo.connect(nd,"onMoveStop",function (mover){
		var ided = mover.host.node.id;
		dojo.style(mover.host.node,"zIndex",getZIndex());
		
		var pos = dojo.position(mover.host.node);
		var evtdata = {};
    	evtdata.id = mover.host.node.id;
    	evtdata.type="ssmmovestop";
    	evtdata.pos = pos;
    	sendEvent(geq, [evtdata]) ;

		
		var comp = findComponentByName(ided);
		if(comp!=null){
			comp.left = mover.node.style.left;
			comp.top = mover.node.style.top;
			comp.width = mover.node.style.width;
			comp.height = mover.node.style.height;
			comp.zindex = (getZIndex());
		}
	});

	var handle = new dojox.layout.ResizeHandle( {
		targetId : instid,id:"place"+instid,
		minWidth:10,
		minHeight:10
	}).placeAt(instid);
	return mnode;
}
function wipein(inode,idur) {
	if (inode != null) {
		var wipeIn = dojo.fx.wipeIn( {
			node : inode,
			duration : idur
		});
		wipeIn.play();
	}
}
function wipeout(inode,idur) {
	if (inode != null) {
		var wipeOut = dojo.fx.wipeOut( {
			node : inode,
			duration : idur
		});
		wipeOut.play();
	}
}
function getModulesForPage(givenPageId, func1) {
	var url = "/site/wide_5678/resources/js/config/" + givenPageId + "pageinfo.json";
	dojo.xhrGet( {
		url : url,
		handleAs : "json",
		load : function(response, ioArgs) {
			if (func1 != null) {
				func1(response);
			} else {
				alert(response);
			}
			return response;
		},
		error : function(response, ioArgs) {
			if (func1 != null) {
				func1(response);
			} else {
				alert(response);
			}
			return response;
		}
	});
}
function applyShapes(){
	console.log("applyShapes");
	// pass one create all elements
	for(var i=0;i<compDiag.length;i++){
		var obj = compDiag[i];
		if(obj.name=="Shape"){
		   var f = comps[obj.name];
	        var configFun = configs[obj.name];
	    	  if(configFun!=null){
					configFun(f,obj.aid,obj.name,false,obj.mx,obj.my,obj);
					if(obj.zindex!=null){
						if(obj.zindex>zindex)zindex=obj.zindex;
					}
	       	  }
	}
 }
}
function applyDesign(){
	console.log("applyDesign");
	// pass one create all elements
	for(var i=0;i<compDiag.length;i++){
		var obj = compDiag[i];
		if(obj.name=="Code"){
		   var f = comps[obj.name];
	        var configFun = configs[obj.name];
	    	  if(configFun!=null){
					configFun(f,obj.aid,obj.name,false,obj.mx,obj.my,obj);
					if(obj.zindex!=null){
						if(obj.zindex>zindex)zindex=obj.zindex;
					}
	       	  }
	}
 }
	// pass 2 create all elements
	for(var i=0;i<compDiag.length;i++){
		var obj = compDiag[i];
		if(obj.name=="Action"||obj.name=="Code"|| obj.name=="Last"){
			
		}else{
		   var f = comps[obj.name];
	        var configFun = configs[obj.name];
	    	  if(configFun!=null){
					configFun(f,obj.aid,obj.name,false,obj.mx,obj.my,obj);
					if(obj.zindex!=null){
						if(obj.zindex>zindex)zindex=obj.zindex;
					}
	       	  }
	}
 }
	// pass 3 only handle animations
	for(var i=0;i<compDiag.length;i++){
		var obj = compDiag[i];
		if(obj.name=="Action"){
		   var f = comps[obj.name];
	        var configFun = configs[obj.name];
	    	  if(configFun!=null){
					configFun(f,obj.aid,obj.name,false,obj.mx,obj.my,obj);
					if(obj.zindex!=null){
						if(obj.zindex>zindex)zindex=obj.zindex;
					}
	       	  }
	}
 }
	// pass 4 only the last component
	for(var i=0;i<compDiag.length;i++){
		var obj = compDiag[i];
		if(obj.name=="Last"){
		   var f = comps[obj.name];
	        var configFun = configs[obj.name];
	    	  if(configFun!=null){
					configFun(f,obj.aid,obj.name,false,obj.mx,obj.my,obj);
					if(obj.zindex!=null){
						if(obj.zindex>zindex)zindex=obj.zindex;
					}
	       	  }
	}
 }
	zindex+=5;
	preparePortable();
}
function findComponentByName(str){
	for(var i = 0;i<compDiag.length;i++){
	 if(compDiag[i].id==str){
		 return compDiag[i];
	 }	
	}
	return null;
}
function getDynaModule(instid, path, modulename, jscomp,l,t,w,h,zIndex) {
	var cnode = dojo.byId("dynamicnodes");
	var mnode = dojo.create("div", null, cnode);
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", "movablenode");
	dojo.attr(mnode, "style","width:"+w+";height:"+h+";left:"+l+";top:"+t+";"+"z-index:"+zIndex+";");
	doGetHtml(path, function(response) {
		var dv = dojo.create("div", {
			innerHTML : "<div class=\"heading\" id=\"heading_"+instid+"\">"+instid+"</div>"+response,
			style : "width:\"100%\";height:\"100%\"",
			id : "bid" + instid
		}, mnode);
		if(jscomp!=null &&jscomp!=""){
		doGetHtml( jscomp, function(a) {
			dojo.eval(a);
			dojo.eval(modulename + "_init();")
			});
		}
			var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
			dojo.connect(nd,"onMoveStop",function (mover){
			dojo.style(mover.host.node,"zIndex",getZIndex());
			
			var pos = dojo.position(mover.host.node);
			var evtdata = {};
        	evtdata.id = mover.host.node.id;
        	evtdata.type="ssmmovestop";
        	evtdata.pos = pos;
        	sendEvent(geq, [evtdata]) ;

			
			var item = findComponentByName(mover.host.node.id)
			if(item!=null){
				item.left = mover.node.style.left;
				item.top = mover.node.style.top;
				item.zindex = getZIndex();
				item.width = mover.node.style.width;
				item.height = mover.node.style.height;
			}
			var handle = new dojox.layout.ResizeHandle( {
				targetId : instid,
				minWidth:10,
				minHeight:10
			}).placeAt("bid" + instid);
		});
	});
}
function getDynaHeaderModule(instid, path, modulename, jscomp,l,t,w,h,zIndex) {
	registerWindow(instid);
	var cnode = dojo.byId("dynamicnodes");
	var mnode = dojo.create("div", null, cnode);
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", "movablenode");
	dojo.attr(mnode, "style","width:"+w+";height:"+h+";left:"+l+";top:"+t+";"+"z-index:"+zIndex+";overflow:scroll;");
	doGetHtml(path, function(response) {
		var dv = dojo.create("div", {
			innerHTML : "<div class=\"heading\" id=\"heading_"+instid+"\">"+instid+"</div>"+response,
			style : "width:\"100%\";height:\"100%\";",
			id : "bid" + instid
		}, mnode);
		if(jscomp!=null &&jscomp!=""){
		doGetHtml( jscomp, function(a) {
			dojo.eval(a);
			dojo.eval(modulename + "_init();")
			});
		}
			var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
			dojo.connect(nd,"onMoveStop",function (mover){
			dojo.style(mover.host.node,"zIndex",getZIndex());
			
			var pos = dojo.position(mover.host.node);
			var evtdata = {};
        	evtdata.id = mover.host.node.id;
        	evtdata.type="ssmmovestop";
        	evtdata.pos = pos;
        	sendEvent(geq, [evtdata]) ;

			
			var item = findComponentByName(mover.host.node.id)
			if(item!=null){
				item.left = mover.node.style.left;
				item.top = mover.node.style.top;
				item.zindex = (getZIndex());
				item.width = mover.node.style.width;
				item.height = mover.node.style.height;
			}
			var handle = new dojox.layout.ResizeHandle( {
				targetId : instid,
				minWidth:10,
				minHeight:10
			}).placeAt("bid" + instid);
		});
	});
}
function deleteElementsDiag(){
	var loc = dojo.byId('chooseelem');
	loc.innerHTML = "";
	for(var i = 0;i<compDiag.length;i++){
	dojo.create("option", {innerHTML : compDiag[i].id }, loc);
	}
	dojo.create("option", {innerHTML : "document" }, loc);
	
	dijit.byId("deleteElementDialog").show();
}
function showModNodeDlg(){
	var loc = dojo.byId('modnodeEle');
	loc.innerHTML = "";
	for(var i = 0;i<compDiag.length;i++){
	dojo.create("option", {innerHTML : compDiag[i].id }, loc);
	}
	dijit.byId("addModuleNodeDialog").show();
}
function addAllModuleNodes(){
	for(var i = 0;i<compDiag.length;i++){
		var obj  =  null;
		obj = findNodeById(compDiag[i].id);
		if(obj==null){
		obj = {};
		obj.id = compDiag[i].id	;
		obj.type = "module";
		obj.val = getModuleCode(compDiag[i]);
		obj.normalizedx = compDiag[i].left;
		obj.normalizedy = compDiag[i].top;
		obj.x = compDiag[i].left;
		obj.y = compDiag[i].top;

		obj.r = compDiag[i].width;
		obj.b = compDiag[i].height;
		if(compDiag[i].tags!=null){
			obj.tags = compDiag[i].tags;
		}
		addObjectToGraph(obj);
		}else{
			obj.val = getModuleCode(compDiag[i]);
		}
	}
}
function addModuleNode(node){
	var sel = dojo.byId("modnodeEle");
	var sidx = sel.selectedIndex;
	var elename = sel.options[sidx].text;
	addModuleNodeObj(elename);
}
function addModuleNodeObj(elename){
	var obj  =  {};
	if(elename!=null && elename.length>0){
		obj.id = elename	;
		obj.type = "module";
		addObjectToGraph(obj);
	}
	return obj;
}
function updateElementsDiag(){
	var loc = dojo.byId('chooseuelem');
	loc.innerHTML = "";
	for(var i = 0;i<compDiag.length;i++){
	dojo.create("option", {innerHTML : compDiag[i].id }, loc);
	}
	dojo.create("option", {innerHTML : "document" }, loc);
	updatePropName();
	
	dijit.byId("updateElementDialog").show();
}

function updatePropName(){
	var sel = dojo.byId("chooseuelem");
	var sidx = sel.selectedIndex;
	var elename = sel.options[sidx].text;
	var loc2 = dojo.byId('eleprop');
	loc2.innerHTML = "";
	for(var i = 0;i<compDiag.length;i++){
		if(compDiag[i].id==elename){
			for(var j in compDiag[i]){
				dojo.create("option", {innerHTML : j}, loc2);
			}
		}
	}
	updatePropValue();
}
function updatePropValue(){
	var sel = dojo.byId("chooseuelem");
	var sidx = sel.selectedIndex;
	var elename = sel.options[sidx].text;
	var sel2 = dojo.byId("eleprop");
	var sidx2 = sel2.selectedIndex;
	var elename2 = sel2.options[sidx2].text;
	for(var i = 0;i<compDiag.length;i++){
		if(compDiag[i].id==elename){
			var a = dojo.byId("propval");
			a.value = compDiag[i][elename2];
		}
	}
// /propval
}
function deleteElement(jdata){
	var sel = dojo.byId("chooseelem");
	var sidx = sel.selectedIndex;
	var elename = sel.options[sidx].text;
	deleteModuleItemByName(elename);
}
function deleteModuleItemByName(elename){
	for(var i = 0;i<compDiag.length;i++){
		if(compDiag[i].id==elename){
			removeCompFromGui(elename);
			compDiag.splice(i,1);
			globals[elename] = null;
			maps[elename] = null;
			grids[elename] = null;
			htmlcomps[elename] = null;
			removeFromGraphWithRelatives(elename);
		}
	}
}
function updateElement(jdata){
	var sel = dojo.byId("chooseuelem");
	var p = dojo.byId("eleprop").value
	var pv = dojo.byId("propval").value
	var sidx = sel.selectedIndex;
	var elename = sel.options[sidx].text;
	for(var i = 0;i<compDiag.length;i++){
		if(compDiag[i].id==elename){
			if(p=="code")
				compDiag[i][p]=dojo.toJson(pv);
			else
				compDiag[i][p]=pv;
		}
	}
}

function animElementsDiag(){
	var loc = dojo.byId('animelem');
	var loca = dojo.byId('animObject');
	var locb = dojo.byId('animAction');
	loc.innerHTML = "";
	loca.innerHTML = "";
	locb.innerHTML = "";
	for(var i = 0;i<compDiag.length;i++){
		if(compDiag[i].name!="Animation")
			dojo.create("option", {innerHTML : compDiag[i].id }, loc);
		else
			dojo.create("option", {innerHTML : compDiag[i].id }, loca);
	}
	
	dojo.create("option", {innerHTML : "document" }, loc);
	
	
	
	dojo.create("option",{innerHTML :  "onload"}, locb);
	dojo.create("option", {innerHTML : "onmouseup"},  locb);
	dojo.create("option", {innerHTML : "onmousedown"},  locb);
	dojo.create("option",{innerHTML :  "onmouseover"},  locb);
	dojo.create("option",{innerHTML :  "onmouseout"},  locb);
	dojo.create("option",{innerHTML :  "onmousemove"},  locb);
	dojo.create("option", {innerHTML : "onclick"},  locb);
	dojo.create("option",{innerHTML :  "ondblclick"},  locb);
	
	dojo.create("option",{innerHTML :  "ontouchstart"},  locb);
	dojo.create("option",{innerHTML :  "ontouchmove"},  locb);
	dojo.create("option",{innerHTML :  "ongesturechange"},  locb);
	dojo.create("option",{innerHTML :  "ongestureend"},  locb);
	dojo.create("option",{innerHTML :  "ontouchend"},  locb);
	dojo.create("option",{innerHTML :  "onRowClick"},  locb);
	
	
	
	
	
	
	
	dijit.byId("animElementDialog").show();
}

function animElement(jdata){
	var sel = dojo.byId("animelem");
	var sidx = sel.selectedIndex;
	var elename = sel.options[sidx].text;
	
	sel = dojo.byId("animAction");
	sidx = sel.selectedIndex;
	var eleaction = sel.options[sidx].text;
	
	sel = dojo.byId("animObject");
	sidx = sel.selectedIndex;
	var eleanim = sel.options[sidx].text;
	var configobj = {"id":elename+"_"+eleaction+"_"+eleanim,"name":"Action","aid":0,"mouseX":0,"mouseY":0,"t":0,"left":0,"top":0,"width":0,"height":0,"zindex":0,"action":eleanim ,"onevent":eleaction,"onelem":elename };
	compDiag.push(configobj);
	
}
function findNodeByComponentId(idstr){
	var obj = findComponentByName(idstr);
	return dojo.byId(getPrepend(obj.name)+obj.id)
}


function gotoGoogleCharts(){
	window.open("http://code.google.com/apis/chart/docs/chart_playground.html");
}

function saveCompStatic(){
	var loc = dojo.byId('saveelem');
	loc.innerHTML = "";
	for(var i = 0;i<compDiag.length;i++){
	dojo.create("option", {innerHTML : compDiag[i].id }, loc);
	}
	saveStaticCompChange();
	dijit.byId("savestaticElementDialog").show();
}

function saveStaticElement(jdata){
	var sel = dojo.byId("saveelem");
	var sidx = sel.selectedIndex;
	var elename = sel.options[sidx].text;
	var doc = dojo.byId("saveelemdoc").value;	
	var myname = dojo.byId("saveelemname").value;	
	saveStaticComp(elename,doc,myname);
}
function saveStaticComp(elename,doc,myname){
	for(var i = 0;i<compDiag.length;i++){
		if(compDiag[i].id==elename){
			var disp = dojo.toJson(compDiag[i]);
			postFormWithContent("/SaveStaticComponent",{"doc":doc,"tosave":disp,"name":myname},function (res){addInfoToBox(res);});
			
		}
	}
}
function addCompStatic(){
var loc = dojo.byId('addstaticcomponent');
	loc.innerHTML = "";
	dojo.xhrGet( {
		url : urlMap.GetStaticComponents,
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.name
				}, loc);
				addStaticCompChange();
			});
			
			var createConnectionDialog = dijit.byId('addStaticComponentDialog');
			createConnectionDialog.show();
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to retrive static components..." + a.result);
			return response;
		}
	});
}
		 
function addStaticComponentFromServer(){
var name  = dojo.byId("addstaticcomponentname").value;
getAndShowStaticComp(name);
}
function getAndShowStaticComp(name){
	//todo: need to fix it here this line below should not be ther
	dojo.byId("addstaticcomponentid").value = name;
	postFormWithContent(urlMap.GetStaticComponent,{"name":name},function (a){
		var id  = dojo.byId("addstaticcomponentid").value;
		var obj = dojo.fromJson(a);
		obj.id=id;
		compDiag.push(obj);
		var f = comps[obj.name];
		var configFun = configs[obj.name];
		  if(configFun!=null){
			configFun(f,obj.aid,obj.name,false,obj.mx,obj.my,obj);
			if(obj.zindex!=null){
		 	   if(obj.zindex>zindex){
					zindex=obj.zindex;
					zindex=getZIndex();
				}// if
			 }
		}
		});
}
function addCompStatic(){
var loc = dojo.byId('addstaticcomponentname');
	loc.innerHTML = "";
	dojo.xhrGet( {
		url : urlMap.GetStaticComponents,
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.name
				}, loc);
			});
			var createConnectionDialog = dijit.byId('addStaticComponentDialog');
			createConnectionDialog.show();
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to retrive static components..." + a.result);
			return response;
		}
	});
}
function addStaticComponent(){
var name  = dojo.byId("addstaticcomponentname").value;
postFormWithContent(urlMap.GetStaticComponent,{"name":name},function (a){
var id  = dojo.byId("addstaticcomponentid").value;
a = dojo.fromJson(a);
dojo.forEach(a, function(oneEntry, index, array) {
var obj = dojo.fromJson(oneEntry.txt);
obj.id=id;
compDiag.push(obj);
var f = comps[obj.name];
var configFun = configs[obj.name];
if(configFun!=null){
	configFun(f,obj.aid,obj.name,false,obj.mx,obj.my,obj);
	if(obj.zindex!=null){
	if(obj.zindex>zindex){
		zindex=obj.zindex;
		zindex=getZIndex();
	}else{
		obj.zindex=zindex;
		zindex=getZIndex();
	}
	}
	       	  }
});			
});
}

function getShape(id,design,pstr){
return  {"id":id,"name":"Shape","design":design,"type":"Shape","path":pstr};
}

function addHiddenImage(txt,uid){
	var mnode = dojo.create("img", {"src":txt,"style":"width:0px;height:0px;hidden:true"}, dojo.byId("dynamicnodes"));
	dojo.attr(mnode, "id",uid);
	
}
function playAnim(id){
	var actionToTake = findComponentByName(id);
	if(actionToTake){
	var fun = (new Function("cevent",actionToTake.txt));
	fun();
	}
}
function getAnim(id){
	var actionToTake = findComponentByName(id);
	if(actionToTake){
	var fun = (new Function("cevent",actionToTake.txt));
	return fun;
	}
	return null;
}

function getModuleCode(item){
	var code = null;
	if(item!=null){
		code =item.label;
		if(code==null){
			/*
			 * if(item.type=="Input"||item.type=="Graphics"||item.type=="Animation"||item.type=="Code"||item.type=="EventAnimation"||item.type=="Last")
			 * code = dojo.toJson(item.txt); else
			 */	code = item.txt;	
			if(code==null){
				code = item.lbl;
				if(code==null){
		/*
		 * if(item.type=="Graphics"||item.type=="Animation"||item.type=="Code"||item.type=="EventAnimation"||item.type=="Last")
		 * code = dojo.toJson(item.code); else
		 */			code = item.code;
				}

			}
		}
	}
	return code;
}

function moduleExists(id){
	var m = globals [id];
	if(m==null)
		m = maps [id];
	if(m==null)
		m = grids [id];
	if(m==null)
		m = htmlcomps [id];
	if(m==null)
		m = dojo.byId(id);
	return m==null?false:true;
}
function registerWindow(id){
	// alert("window: "+id);
	windows[id] =  {};
	windows[id].id = id;
	windows[id].visible = true;
	addWindowMenu(id);
}
function showWindow(id){
	windows[id].visible = true;
	showdiv(id);
}
function hideWindow(id){
	windows[id].visible = false;
	hidediv(id);
}
function addFileMenuFunc(id,func){
	try{
	var wm = dijit.byId("fileMenu");
	var m = new dijit.MenuItem({id:"wm"+id,label:id});
	m.onClick= function (){
		var id = this.id.substring(2);
		func(id);
	}
	wm.addChild(m);	
	}catch(e){
		console.log("error creating File menu: "+id);
	}
}

function hideAllWindows(){
	hidediv('cmdnode');hidediv('docnode');hidediv('propnode');hidediv('histnode');dw=false;hw=false;pw=false;cw=false;
	for(var w in windows){
		var win = windows[w];
		win.visible=false;
		hidediv(win.id);
	}
}
function showAllWindows(){
	showdiv('cmdnode');showdiv('docnode');showdiv('propnode');showdiv('histnode');dw=true;hw=true;pw=true;cw=true;
	for(var w in windows){
		var win = windows[w];
		win.visible=true;
		showdiv(win.id);
	}
}
function getCodeFromId(id){
	var obj = findNodeById(id);
	return getCode(obj);
}
function getCode(obj){
	return "js_Print(\""+obj.id+"\");sv=\""+obj.id+"\";";
}

function configInputPort(f, a,compname,isui,mx,my,configobj) {
	if(a==null){
		alert("Please drop on a Entity");
		}else{
	var pname = prompt("Enter Input Port Name","");
	var port = createPortObj(mx,my,"inp_"+a.id+"."+pname,pname,"input",a.id,"java.lang.String");
	pData.data.unshift(port);
	a.inputs.push(pname);
	a.noofin++;
		}
	draw();
}
function configOutputPort(f, a,compname,isui,mx,my,configobj) {
	if(a==null){
		alert("Please drop on a Entity");
		}else{
	var pname = prompt("Enter Output Port Name","");
	var port = createPortObj(mx,my,"out_"+a.id+"."+pname,pname,"output",a.id,"java.lang.String");
	pData.data.unshift(port);
	a.outputs.push(pname);
	a.noofout++;
	}
	draw();
}
function configAuxPort(f, a,compname,isui,mx,my,configobj) {
	if(a==null){
		alert("Please drop on a Entity");
		}else{
			var pname = prompt("Enter Aux Port Name","");
	var port = createPortObj(mx,my,"aux_"+a.id+"."+pname,pname,"aux",a.id,"java.lang.String");
	pData.data.unshift(port);
	a.aux.push(pname);
	a.noofaux++;
	}
	draw();
}
function configPort(f, a,compname,isui,mx,my,configobj) {
	if(a==null){
		var a = getNearDrawEle(mx,my,null);
		if(a==null){
		alert("Please drop on a Entity");
		}
	}
		if(a!=null){
			var pname = prompt("Enter Port Name","");
			if(a.inputs!=null){
				var portr = createPortObj(mx,my,"r_"+a.id+"."+pname,pname,"poutput",a.id,"java.lang.String");
				pData.data.unshift(portr);
				var portl = createPortObj(mx,my,"l_"+a.id+"."+pname,pname,"pinput",a.id,"java.lang.String");
				pData.data.unshift(portl);
				a.inputs.push(pname);
				a.dtypes.push("java.lang.String");
				a.noofin++;
			}else if(typeof a.items!=undefined){
				var myid = "a_"+a.id+"."+pname;
				var portr = createPortObj(mx,my,myid,pname,"arbitport",a.id,"java.lang.String");
				pData.data.unshift(portr);
				if(a.items ==null)
					a.items = new Array();
					a.items.push(pname);
				}
			draw();
		}

}
function configPortMethod(f, a,compname,isui,mx,my,configobj) {
	if(a==null){
		alert("Please drop on a Entity");
		}else{
			var pname = prompt("Enter Port Name","");
			var portr = createPortObj(mx,my,"r_"+a.id+"."+pname,pname,"pmtdout",a.id,"java.lang.String");
			pData.data.unshift(portr);
			var portl = createPortObj(mx,my,"l_"+a.id+"."+pname,pname,"pmtdin",a.id,"java.lang.String");
			pData.data.unshift(portl);
			a.inputs.push(pname);
			a.dtypes.push("java.lang.String");
			a.noofin++;
		}
		draw();
	}

//////////////////////////
function getStaticModule(instid, l,t,w,h,cntn,zi) {
	var zIndex=zi;
	if(zi==null){
		zIndex=( getZIndex() );
	}
	var cnode = dojo.byId("dynamicnodes");
	var mnode = dojo.create("div", null, cnode);
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", "staticnode");
	dojo.attr(mnode, "style","width:"+w+";height:"+h+";left:"+l+";top:"+t+";"+"z-index:"+zIndex+";");
	if(cntn == null||cntn.length<1)
		cntn="<div>&nbsp;</div>";
	
		var dv = dojo.create("div", {
			innerHTML : "<div class=\"heading\" id=\"heading_"+instid+"\">"+instid+"</div><div id='bid_"+instid+"' width='100%' height='100%' ></div>",
			width:"100%",
			height:'100%',
			id : "bid" + instid
		}, mnode);
		dojo.byId("bid_" + instid).innerHTML=cntn;
		
		    loadDynaHTMLBodyScripts(dv);
			var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
			dojo.connect(nd,"onMoveStop",function (mover){
			dojo.style(mover.host.node,"zIndex",getZIndex());
			
			var pos = dojo.position(mover.host.node);
			var evtdata = {};
        	evtdata.id = mover.host.node.id;
        	evtdata.type="ssmmovestop";
        	evtdata.pos = pos;
        	sendEvent(geq, [evtdata]) ;

			
			var item = findComponentByName(mover.host.node.id)
			if(item!=null){
				item.left = mover.node.style.left;
				item.top = mover.node.style.top;
				item.zindex = getZIndex();
				item.width = mover.node.style.width;
				item.height = mover.node.style.height;
			}
			preparePortable();
		});
			dojo.connect(dojo.byId("heading_"+instid),"ondblclick",function(e){
				var nd = e.target;
				e.preventDefault(); 
				var uid = dojo.attr(nd,"id");
				uid=uid.substring(8);
				var comp = findComponentByName(uid);
				var cd = comp.cntn;
				comp.cntn = dojo.byId("bid_"+uid).innerHTML;
				showEditor(cd,uid,"getStaticModule");
			});
			var handle = new dojox.layout.ResizeHandle( {
				targetId : instid,
				minWidth:10,
				minHeight:10
			}).placeAt("bid" + instid);
			return mnode;
}

function saveStaticCompChange(){
	var val = getSelVal("saveelem");
	dojo.byId("saveelemdoc").value=val;
	dojo.byId("saveelemname").value=val;
}
function addStaticCompChange(){
	var val = getSelVal("addstaticcomponentname");
	dojo.byId("addstaticcomponentid").value=val;
	
}
function getSimpleStaticModule(instid,clsName, l,t,w,h,cntn,zi) {
	var zIndex=zi;
	if(zi==null){
		zIndex=( getZIndex() );
	}
	var cnode = dojo.byId("dynamicnodes");
	var mnode = dojo.create("div", null, cnode);
	dojo.attr(mnode, "id", instid);
	dojo.attr(mnode, "class", clsName);
	dojo.attr(mnode, "style","width:"+w+"px"+";height:"+h+"px"+";left:"+l+"px"+";top:"+t+"px"+";"+"z-index:"+zIndex+";");
	if(cntn == null||cntn.length<1){
		cntn="<div>&nbsp;</div>";
	}
		var dv = dojo.create("div", {
			innerHTML : "<div id='bid_"+instid+"' width='100%' height='100%' >"+cntn+"</div>",
			width:"100%",
			height:'100%',
			id : "bid" + instid
		}, mnode);
			var nd = new dojo.dnd.Moveable(instid,{mover: dojo.dnd.StepMover});
			dojo.connect(nd,"onMove",function (mover){
				if(ctrl){
					var pos = dojo.position(mover.host.node);
					var evtdata = {};
		        	evtdata.id = mover.host.node.id;
		        	evtdata.type="ssmmove";
		        	evtdata.pos = pos;
		        	sendEvent(geq, [evtdata]) ;
				}else{
					var pos = dojo.position(mover.host.node);
					var evtdata = {};
		        	evtdata.id = mover.host.node.id;
		        	evtdata.type="ssmove";
		        	evtdata.pos = pos;
		        	sendEvent(geq, [evtdata]) ;
				}
			});
			dojo.connect(nd,"onMoveStop",function (mover){
				
			var pos = dojo.position(mover.host.node);
			console.log("Moving: "+mover.host.node.id+"   ,  "+dojo.toJson(pos));			
			
			
			var pos = dojo.position(mover.host.node);
			var evtdata = {};
        	evtdata.id = mover.host.node.id;
        	evtdata.type="ssmmovestop";
        	evtdata.pos = pos;
        	sendEvent(geq, [evtdata]) ;

			
			var pos = dojo.position(mover.host.node);
			var evtdata = {};
        	evtdata.id = mover.host.node.id;
        	evtdata.type="ssmmovestop";
        	evtdata.pos = pos;
        	sendEvent(geq, [evtdata]) ;
			
			dojo.style(mover.host.node,"zIndex",getZIndex());
			var item = findComponentByName(mover.host.node.id)
			if(item!=null){
				item.left = mover.node.style.left;
				item.top = mover.node.style.top;
				item.zindex = getZIndex();
				item.width = mover.node.style.width;
				item.height = mover.node.style.height;
			}
			
			preparePortable();
		});
			
			return mnode;
}

function configCovertToPortMethod(f, a,compname,isui,mx,my,configobj){
	var a = getElementFromPos(mx,my);
	var p = dojo.position(a);
	var rect = pCanvas.rect(p.x-p.w/2,a.y-p.h/2,p.w,p.h);
	rect.attr({"stroke": "red", "fill-opacity": .4,"opacity":.4, "stroke-width": 1,"stroke-dasharray":"."});

	
}
/*
 * 
 */
function getAnonScriptStaticModule(instid, l,t,w,h,cntn,zi,script) {
		var zIndex=zi;
		if(zi==null){
			zIndex=( getZIndex() );
		}
		var cnode = dojo.byId("dynamicnodes");
		var mnode = dojo.create("div", null, cnode);
		dojo.attr(mnode, "id", instid);
		dojo.attr(mnode, "class", "anondefstaticnode");
		dojo.attr(mnode, "style","width:"+w+";height:"+h+";left:"+l+";top:"+t+";"+"z-index:"+zIndex+";");
		if(cntn == null||cntn.length<1)
			cntn="<div>&nbsp;</div>";
		
			var dv = dojo.create("div", {
				innerHTML : "<div class=\"anondefheading\" id=\"heading_"+instid+"\">"+instid+"</div><div id='bid_"+instid+"' width='100%' height='100%' >"+cntn+"</div>",
				width:"100%",
				height:'100%',
				id : "bid" + instid
			}, mnode);
				var nd = new dojo.dnd.Moveable(instid,{handle:"heading_" + instid,mover: dojo.dnd.StepMover});
				dojo.connect(nd,"onMoveStop",function (mover){
				dojo.style(mover.host.node,"zIndex",getZIndex());
				
				var pos = dojo.position(mover.host.node);
				var evtdata = {};
	        	evtdata.id = mover.host.node.id;
	        	evtdata.type="ssmmovestop";
	        	evtdata.pos = pos;
	        	sendEvent(geq, [evtdata]) ;

				
				var item = findComponentByName(mover.host.node.id)
				if(item!=null){
					item.left = mover.node.style.left;
					item.top = mover.node.style.top;
					item.zindex = getZIndex();
					item.width = mover.node.style.width;
					item.height = mover.node.style.height;
				}
				preparePortable();
			});
				dojo.connect(dojo.byId("heading_"+instid),"ondblclick",function(e){
					var nd = e.target;
					e.preventDefault(); 
					var uid = dojo.attr(nd,"id");
					uid=uid.substring(8);
					var comp = findComponentByName(uid);
					//comp.cntn = dojo.byId("bid_"+uid).innerHTML;
					var cd = comp.script;
					showAceEditor(cd,uid,"getAnonScriptStaticModule");
				});
				var handle = new dojox.layout.ResizeHandle( {
					targetId : instid,
					minWidth:10,
					minHeight:10
				}).placeAt("bid" + instid);
				return mnode;
	}
function getZIndex(){
	if(wideconfig!=null && wideconfig.incr_zindex=="true"){
		return ++zindex;	
	}else{
		return 0;
	}
	
}
//-------------------------------------------------------------------------
function createStaticModule(f, a, compname, isui, mx, my, configobj) {
	var droppedon = a;
	var uid = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	var rotation = "0deg"; 
	var uidchanged = false;	
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		cntn = prompt('Enter Content', '');
		rotation = prompt('Enter rotation', '0');
		rotation= rotation+"deg";
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : droppedon == null ? 0 : droppedon.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : (getZIndex()),
			"rotation":rotation
		};
		compDiag.push(configobj);

	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		if(uid.indexOf("${id}")>=0){
			var tuid = prompt("Please enter a name");
			if(tuid!=null && tuid.length>0){
			uid = uid.replace(/\$\{id\}/g,tuid);
			cntn = cntn.replace(/\$\{id\}/g,tuid);
			configobj.id = uid;
			configobj.cntn = cntn;
			uidchanged = true;
			}
			
		}
		
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		if(configobj.rotation==null || configobj.rotation.length<1)
			rotation = "0deg";
		else
			rotation = configobj.rotation;
	}
	try {
		var a = getStaticModule(uid, l, t, w, h, cntn, zi)
		var target = new dojo.dnd.Source("bid_" + uid, {
			copyOnly : true,
			accept : [ "text", "treeNode" ],
			creator : widgetSourceCreator
		});
		setCSSRotation(uid,rotation)
		dojo.connect(target, "onDrop",
				function(source, nodes, copy) {
					if (lastDropModule != null) {
						var mod = lastDropModule.substring(4);
						findComponentByName(mod).cntn = dojo
								.byId(lastDropModule).innerHTML;
					}
				});
		htmlcomps[uid] = a;
		if(uidchanged){
		removeNodeById(uid);
		var obj = addModuleNodeObj(uid);
		obj.x = l-topWidth;
		obj.y = t;
		obj.r = w;
		obj.b =h
		obj.normalizedx = obj.x;
		obj.normalizedy = obj.y;
		obj.script = configobj.script;
		obj.val = configobj.cntn;
	    var pos = dojo.position(uid);
		var evtdata = {};
		evtdata.id = uid;
		evtdata.type="ssmmovestop";
		evtdata.pos = pos;
		sendEvent(geq, [evtdata]) ;
		preparePortable();
		syncModuleCode(uid);
		if(droppedon!=null && droppedon.type!=null && droppedon.type=="connection"){
			var origto = droppedon.to;
			droppedon.to=obj.id;
			droppedon.nodes[1]=obj.id
	        var drawing= findDrawEleByIdEx(droppedon.id);
	        if(drawing.item!=null)
	        drawing.item.remove();
	        if(drawing.textnode!=null)
	        drawing.textnode.remove();
	        for(var i=0;i<drawing.subs.length;i++){
	            drawing.subs[i].remove();
	        }
	        
	        createConnItem(obj.id,origto,getUniqId());
	        createConnItem(droppedon.from,obj.id,getUniqId());
      }else if(droppedon!=null && droppedon.type!=null && droppedon.type=="AnonDef"){
			createConnItem(droppedon.id,obj.id,getUniqId());
		}
		}else{
			removeNodeById(uid);
			var obj = addModuleNodeObj(uid);
			obj.script = configobj.script;
			draw();
		}
	} catch (e) {
		alert(e.message || e);
	}
}