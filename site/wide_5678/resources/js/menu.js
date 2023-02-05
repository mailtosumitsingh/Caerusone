/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */
var siteWasDown = false;
var cfg = null;
var statusled = "0-#007700-#05ff05";
var failstatusled = "0-#f30000-#ff0005";
var cmenu = null;
var cmenuBall = null;
var currentGraphConfig = {};
var menuitems = {};
var handleTaskTraceEvent = null;
function postFormWithContent(urlto, jsonContent, fun) {
	dojo.xhrPost({
		url : urlto,
		content : jsonContent,
		load : function(response, ioArgs) {
			if (fun != null)
				fun(response);
		},
		error : function(response, ioArgs) {
			if (fun != null)
				fun(response);
		}
	});
}

function createNode(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);
	var sel = dojo.byId("processclz");
	var sidx = sel.selectedIndex;
	var clz = sel.options[sidx].text;
	var id = newDataObject.nodeId;
	var vx = dojo.byId("xCoordinate").value;
	var vy = dojo.byId("yCoordinate").value;
	var q = dojo.byId("processquery").value;
	var newNode = createNodeObj(id, vx, vy, clz, q);
	addObjectToGraph(newNode);
	normalizeData();
	draw();
}

function createNodeObj(id, vx, vy, clz, q) {
	var newNode = {};
	newNode["type"] = "node";
	newNode["id"] = id;
	newNode["x"] = vx;
	newNode["normalizedx"] = vx;
	newNode["y"] = vy;
	newNode["normalizedy"] = vy;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["name"] = id
	newNode["clz"] = clz;
	newNode["query"] = q;
	newNode["icon"] = "";
	return newNode
}

function updateNode(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);
	var newNode = findNodeById(newDataObject.u_nodeId);
	var sel = dojo.byId("u_processclz");
	var sidx = sel.selectedIndex;
	var pclztxt = sel.options[sidx].text;
	console.log("Created the newDataObject");
	newNode["type"] = "node";
	newNode["id"] = newDataObject.u_nodeId;
	newNode["x"] = dojo.byId("u_xCoordinate").value;
	newNode["normalizedx"] = dojo.byId("u_xCoordinate").value;
	newNode["y"] = dojo.byId("u_yCoordinate").value;
	newNode["normalizedy"] = dojo.byId("u_yCoordinate").value;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["name"] = newDataObject.u_nodeId;
	newNode["clz"] = pclztxt;
	newNode["query"] = dojo.byId("u_processquery").value;
	newNode["icon"] = "";
	console.log("Successfully updated newNode");
	console.log(pData.data[0].x);
	normalizeData();
	draw();
}

function createUpdateNodeDialog(obj) {
	if (obj != null && obj.type == 'node') {
		var fi = dojo.byId("u_nodeId");
		fi.value = obj.id;
		fi = dojo.byId("u_xCoordinate");
		fi.value = obj.normalizedx;
		fi = dojo.byId("u_yCoordinate");
		fi.value = obj.normalizedy;
		fi = dojo.byId("u_rWidth");
		fi.value = obj.r;
		fi = dojo.byId("u_bHeight");
		fi.value = obj.b;
		fi = dojo.byId("u_processclz");
		fi.value = obj.clz;
		fi = dojo.byId("u_processquery");
		fi.value = obj.query;
	}
	var createNodeDialog = dijit.byId('updateNodeDialog');
	createNodeDialog.show();
}

function createNewNodeDialog() {
	var createNodeDialog = dijit.byId('createNodeDialog');
	createNodeDialog.show();
}

function NewPage() {
	addStreamsToPageDlg();

	var createNodeDialog = dijit.byId('createPageDialog');
	createNodeDialog.show();
}

function addStreamsToPageDlg() {
	var sel = dojo.byId("p_streamname");
	var procs = new Array();
	sel.innerHTML = "";
	for (var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'node') {
			var obj = pData.data[i];
			if (obj.clz == "org.ptg.processors.ConditionalPageProcessor"
					|| obj.clz == "org.ptg.processors.PageProcessor"
					|| obj.clz == "org.ptg.processors.SimplePageProcessor") {
				procs[obj.id] = obj;
			}
		}
	}
	for (var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'stream') {
			var obj = pData.data[i];
			if (procs[obj.processor] != null) {
				var temp = dojo.create("option", {
					innerHTML : obj.id
				}, sel);
			}
		}
	}

}

function DeployPage() {
	var createDeployDialog = dijit.byId('createDeployDialog');
	createDeployDialog.show();
}

function createPage(pageData) {
	var d = dojo.fromJson(pageData);
	var sel = dojo.byId("p_streamname");
	var sidx = sel.selectedIndex;
	var pstreamname = sel.options[sidx].text;
	var np = {};
	np["name"] = d.pagename;
	np["dest"] = pstreamname;
	var pgct = dojo.byId('pagecontent');
	var a = pgct.value;
	np["content"] = a;
	np["eventType"] = 'org.ptg.util.events.PageEvent';
	var data = dojo.toJson(np);
	// alert(data)
	// newpage
	// CallWebService("SaveForm",data);
	saveFormPage(data);

}
function closePageDialog() {
	var dlg = dijit.byId("createPageDialog");
	dlg.hide();
}
function createNewNodeDialog(x, y, w, h) {
	var createNodeDialog = dijit.byId('createNodeDialog');
	var temp = dojo.byId("xCoordinate");
	temp.value = x;
	temp = dojo.byId("yCoordinate");
	temp.value = y;

	temp = dojo.byId("rWidth");
	temp.value = w;
	temp = dojo.byId("bHeight");
	temp.value = h;
	createNodeDialog.show();
}
function createConnectionNode(connectionData) {
	var newDataObject = dojo.fromJson(connectionData);
	var newConnection = {};
	var sel = dojo.byId("fromNode");
	var sidx = sel.selectedIndex;
	var fromnode = sel.options[sidx].text;
	sel = dojo.byId("toNode");
	sidx = sel.selectedIndex;
	var tonode = sel.options[sidx].text;

	sel = dojo.byId("ctype");
	sidx = sel.selectedIndex;
	var ctype = sel.options[sidx].text;
	var shapeid = dojo.byId("connshapeid").value;
	console.log("Shape id: " + shapeid)
	if (shapeid != null) {
		var shape = findComponentByName(shapeid);
		if (shape != null) {
			// shape=dojo.fromJson(shape);
			newConnection["shape"] = shape;

			console.log("found shape");
		}

	}

	console.log("Created connection object");
	newConnection["type"] = "connection";
	newConnection["nodes"] = new Array(fromnode, tonode);
	newConnection["from"] = fromnode;
	newConnection["to"] = tonode;
	newConnection["connCond"] = newDataObject.conncond;
	newConnection["id"] = newDataObject.connectionId;
	newConnection["ctype"] = ctype;
	newConnection["sequence"] = newDataObject.sequenceCC;
	newConnection["attrib"] = newDataObject.attribCC;
	console.log("Created the newConnection Object");
	console.log(newConnection.nodes[0] + "  " + newConnection.nodes[1]);
	addObjectToGraph(newConnection);
	normalizeData();
	dojo.byId("connshapeid").value = -1;
	draw();
}
function updateConnection(connectionData) {
	var newDataObject = (connectionData);
	var newConnection = findConnById(newDataObject.u_connectionId);
	var sel = dojo.byId("u_fromNode");
	var sidx = sel.selectedIndex;
	var u_fromnode = sel.options[sidx].text;
	sel = dojo.byId("u_toNode");
	sidx = sel.selectedIndex;
	var u_tonode = sel.options[sidx].text;

	sel = dojo.byId("u_ctype");
	sidx = sel.selectedIndex;
	var ctype = sel.options[sidx].text;

	newConnection["type"] = "connection";
	newConnection["nodes"] = new Array(u_fromnode, u_tonode);
	newConnection["from"] = u_fromnode;
	newConnection["to"] = u_tonode;
	var conncd = "";
	if (newDataObject.u_conncond != null && newDataObject.u_conncond != "")
		conncd = dojo.toJson(newDataObject.u_conncond);
	newConnection["connCond"] = conncd;
	newConnection["id"] = newDataObject.u_connectionId;
	newConnection["ctype"] = ctype;
	newConnection["sequence"] = newDataObject.sequenceEC;
	newConnection["attrib"] = newDataObject.attribEC;

	removeNodeById(newDataObject.u_connectionId);
	addObjectToGraph(newConnection);// addand remove to make sure we are the
	// draw after all our connecting nodes
	// !!!!!!!!!!!

	// normalizeData();
	draw();
}

function updateConnectionDialogAtXYID(x, y, id) {

	var conn = findConnById(id);

	var createConnectionDialog = dijit.byId('updateConnectionDialog');
	GetUpdateStreams(conn);
	if (conn != null) {
		var nd = dojo.byId("u_fromNode");
		dojo.create("option", {
			innerHTML : conn.from
		}, nd);

		nd.value = conn.from;
		nd = dojo.byId("u_toNode");
		dojo.create("option", {
			innerHTML : conn.to
		}, nd);
		nd.value = conn.to;
		nd = dojo.byId("u_conncond");
		nd.value = "";
		if (conn.connCond != null && conn.connCond != "")
			nd.value = dojo.fromJson(conn.connCond);
		nd = dojo.byId("sequenceEC");
		nd.value = conn.sequence;
		nd = dojo.byId("attribEC");
		nd.value = conn.attrib;

		nd = dojo.byId("u_connectionId");
		nd.value = conn.id;

	}
	createConnectionDialog.show();

}

function createConnectionDialogAtXYID(x, y, id) {
	var createConnectionDialog = dijit.byId('createConnectionDialog');
	GetStreams();
	var nd = dojo.byId("fromNode");
	nd.value = id;
	console.log("Connection from id is: " + id);
	createConnectionDialog.show();
}
function addEventPropertyDialogAtXYID(x, y, id) {
	var addEventDlg = dijit.byId('addPropertyDialog');
	var nd = dojo.byId("aeeventid");
	nd.value = id;
	console.log("AddEventProperty from id is: " + id);
	addEventDlg.show();
}
function updateStreamDialogAtXYID(x, y, id) {
	var obj = findNodeById(id);
	var createConnectionDialog = dijit.byId('updateStreamDialog');
	console.log("Connection from id is: " + id);
	if (obj != null) {
		temp = dojo.byId("u_streamextra");
		temp.value = obj.extra;
		temp = dojo.byId("u_streamname");
		temp.value = obj.id;
		temp = dojo.byId("u_exceptionstream");
		temp.value = obj.exceptionStream;
		GetUpdateProcessors(obj.processor);
		GetUpdateEventTypes(obj.eventType);

	}
	createConnectionDialog.show();
}

function createStreamDialogAtXYID(x, y, id) {
	GetProcessors();
	GetEventTypes();
	var createConnectionDialog = dijit.byId('createStreamDialog');
	console.log("Connection from id is: " + id);
	var temp = dojo.byId("streamx");
	temp.value = x;
	temp = dojo.byId("streamy");
	temp.value = y;
	createConnectionDialog.show();
}
function createEventDialogAtXYID(x, y, id) {
	var createEventDialog = dijit.byId('createEventDialog');
	var temp = dojo.byId("eventx");
	temp.value = x;
	temp = dojo.byId("eventy");
	temp.value = y;
	createEventDialog.show();
}
function addStreamPropertyDialogAtXYID(x, y, id) {
	var createEventDialog = dijit.byId('addStreamPropertyDialog');
	var nd = dojo.byId("streventid");
	nd.value = id;
	console.log("AddEventProperty from id is: " + id);
	createEventDialog.show();
}
function createRouteDialogAtXYID(x, y, id) {
	var createRouteDialog = dijit.byId('createRouteDialog');
	var temp = dojo.byId("routex");
	temp.value = x;
	temp = dojo.byId("routey");
	temp.value = y;
	createRouteDialog.show();
}

function addEventProperty(nodeData) {
	var prop = {};
	var dao = dojo.fromJson(nodeData);
	var sel = dojo.byId("aetype");
	var sidx = sel.selectedIndex;
	var aetype = sel.options[sidx].text;
	dao.type = aetype;
	var evt = findNodeById(dao.aeeventid);
	console.log("event id is " + dao.aeeventid + ", evt found " + evt.id);

	if (evt != null) {
		if (evt.props == null) {
			evt.props = {};
		}
		prop.searchable = 1;
		prop.name = dao.aename;
		prop.index = dao.aeindexid;
		prop.type = dao.type;
		evt.props[prop.index] = prop;
	}
	;

	console.log("Added property " + dojo.toJson(dao));
}

function addStreamProperty(nodeData) {
	var dao = dojo.fromJson(nodeData);
	var sel = dojo.byId("strtype");
	var prop = {};
	var sidx = sel.selectedIndex;
	var aetype = sel.options[sidx].text;
	dao.type = aetype;
	var sel2 = dojo.byId("strproptype");
	var sidxx = sel2.selectedIndex;
	var acc = sel2.options[sidxx].text;
	var index = dojo.byId("strpropindex").value;
	var evt = findNodeById(dao.streventid);
	console.log("event id is " + dao.streventid + ", evt found " + evt.id);
	if (evt != null) {
		if (evt.defs == null) {
			evt.defs = {};
		}
		prop.accessor = acc;

		prop.destProp = dao.strdestporp;
		prop.name = dao.strname;
		prop.extra = dao.strname;
		prop.index = index;
		prop.type = aetype;
		prop.xmlExpr = dao.strxmlxpr;
		evt.defs[prop.name] = prop;
	}
	console.log("Added property " + dojo.toJson(dao));
}

function updateStream(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);
	var newNode = findNodeById(newDataObject.u_streamname);
	if (newNode) {
		var sel = dojo.byId("u_streameventtype");
		var sidx = sel.selectedIndex;
		var u_streameventtype = sel.options[sidx].text;
		sel = dojo.byId("u_streamprocessor");
		sidx = sel.selectedIndex;
		var u_processortype = "";
		if (sidx >= 0)
			u_processortype = sel.options[sidx].text;

		console.log("Created the newDataObject");
		newNode["type"] = "stream";
		newNode["id"] = newDataObject.u_streamname;
		// newNode["x"]=dojo.byId("u_streamx").value;
		// newNode["normalizedx"]=dojo.byId("u_streamx").value;
		// newNode["y"]=dojo.byId("u_streamy").value;
		// newNode["normalizedy"]=dojo.byId("u_streamy").value;
		newNode["eventType"] = u_streameventtype;
		newNode["name"] = newDataObject.u_streamname;
		newNode["processor"] = u_processortype;
		newNode["seda"] = 0;
		newNode["r"] = 48;
		newNode["b"] = 48;
		newNode["extra"] = dojo.byId("u_streamextra").value;
		newNode["exceptionStream"] = dojo.byId("u_exceptionstream").value;
		console.log("Successfully updated stream");
		normalizeData();
		draw();
	}
}
function createStream(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);

	var sel = dojo.byId("streameventtype");
	var sidx = sel.selectedIndex;
	var streameventtype = sel.options[sidx].text;
	sel = dojo.byId("streamprocessor");
	sidx = sel.selectedIndex;
	var processortype = "";
	if (sidx >= 0) {
		processortype = sel.options[sidx].text;
	}
	var vx = dojo.byId("streamx").value;
	var vy = dojo.byId("streamy").value;
	var sextra = dojo.byId("streamextra").value;
	var expstr = dojo.byId("exceptionstream").value;

	var sname = newDataObject.streamname;
	console.log("Created the newDataObject");
	var newNode = createStreamItem(sname, vx, vy, streameventtype,
			processortype, sextra, expstr);
	console.log("Successfully created the new stream");
	addObjectToGraph(newNode);
	console.log(newDataObject.streamextra);
	normalizeData();
	draw();
}
function createStreamItem(sname, vx, vy, streameventtype, processortype,
		sextra, expstr) {
	if (sname == null)
		sname = getUniqId();
	var newNode = {};
	newNode["type"] = "stream";
	newNode["id"] = sname;
	newNode["x"] = vx;
	newNode["normalizedx"] = vx
	newNode["y"] = vy;
	newNode["normalizedy"] = vy;
	newNode["eventType"] = streameventtype;
	newNode["name"] = sname;
	newNode["processor"] = processortype;
	newNode["seda"] = 0;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["extra"] = sextra;
	newNode["exceptionStream"] = expstr;
	return newNode;
}
function createEvent(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);
	var newNode = {};

	console.log("Created the newDataObject");
	newNode["type"] = "event";
	newNode["id"] = newDataObject.eventname;
	newNode["x"] = dojo.byId("eventx").value;
	newNode["normalizedx"] = dojo.byId("eventx").value;
	newNode["y"] = dojo.byId("eventy").value;
	newNode["normalizedy"] = dojo.byId("eventy").value;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["eventStore"] = newDataObject.eventstore;
	console.log("Successfully created the new event");
	addObjectToGraph(newNode);
	console.log(pData.data[0].x);
	normalizeData();
	draw();
}
function createRouteItem(sname, vx, vy, desc) {
	if (sname == null)
		sname = getUniqId();
	var newNode = {};
	newNode["type"] = "route";
	newNode["id"] = sname;
	newNode["x"] = vx;
	newNode["normalizedx"] = vx
	newNode["y"] = vy;
	newNode["normalizedy"] = vy;
	newNode["eventType"] = "RouteEvent";
	newNode["routeName"] = sname;
	newNode["routeType"] = '1';
	newNode["seda"] = 0;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["routeDescription"] = desc;
	return newNode;
}
function createRoute(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);
	var newNode = {};
	console.log("Created the newDataObject");
	newNode["type"] = "route";
	newNode["routeName"] = newDataObject.routename;
	newNode["id"] = newDataObject.routename;
	newNode["x"] = dojo.byId("routex").value;
	newNode["normalizedx"] = dojo.byId("routex").value;
	newNode["y"] = dojo.byId("routey").value;
	newNode["normalizedy"] = dojo.byId("routey").value;
	newNode["eventType"] = 'RouteEvent';
	newNode["routeType"] = '1';
	newNode["r"] = 48;
	newNode["b"] = 48;
	console.log("routeDescription: " + newDataObject.processquery);
	newNode["routeDescription"] = newDataObject.routedesc;
	console.log("Successfully created the new route");
	addObjectToGraph(newNode);
	console.log(pData.data[0].x);
	normalizeData();
	draw();
}
function createConnectionDialog() {
	var createConnectionDialog = dijit.byId('createConnectionDialog');
	GetStreams();
	createConnectionDialog.show();
}

function saveGraph(gtype) {
	draw();
	if (gtype == null) {
		gtype = 'graph';
	}
	dojo.byId('savegraphtype').value = gtype;
	var gid = dojo.byId("savegraphid");
	gid.value = currentGraph;
	var createConnectionDialog = dijit.byId('saveGraphDialog');
	createConnectionDialog.show();
}

function saveGraphData(nodeData) {
	// saveGraphDialog
	var newDataObject = dojo.fromJson(nodeData);
	currentGraph = newDataObject.savegraphid;
	var cnt = {
		"tosave" : dojo.toJson(pData),
		"graphconfig" : dojo.toJson(currentGraphConfig),
		"name" : newDataObject.savegraphid,
		"graphtype" : newDataObject.savegraphtype,
		"graphicon" : newDataObject.savegraphicon
	};
	var fun1 = function(res) {
		alert(res);
	}
	var url = getURL("SERVICEBASE") + "SaveGraph";
	postFormWithContent(url, cnt, fun1);
}
function openGraph(gtype) {
	GetGraphs(gtype);
}
function getGraphFromServer(nodeData) {
	clearGraph();

	var sel = dojo.byId("choosegraphid");
	var sidx = sel.selectedIndex;
	var graphname = sel.options[sidx].text;
	getGraphFromServerByName(graphname);
}
function getGraphFromServerByName(graphname) {
	var urlStr = getURL("GETGRAPH") + "?graphid=" + graphname;
	dojo.xhrGet({
		url : urlStr,
		load : function(response, ioArgs) {
			if (response != null || respone != "") {
				var obj = dojo.fromJson(response)
				loadGraph(obj, graphname);
			}
			return response;

		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to Load Graph" + a.result);
			return response;
		}
	});
}

function clearGraph() {
	pData.data = new Array();
	var config = { };
	config.type="GraphConfig";
	config.graphType=wideconfig.graph_type;
	config.id="GraphConfig";
	pData.data.push(config);
	applyDrawConfig();
	currentGraphConfig = {};
	draw();
	displayGraphName("");
}
function  applyDrawConfig(){
	var cfg = findNodeById("GraphConfig");
	if(cfg!=null){
	for ( var i in wideconfig) {
		cfg["config-"+i]=wideconfig[i];
	}
	}
}
function loadGraph(dataobj, gname) {
	if (dataobj != null && dataobj.length > 0) {
		var g = dojo.fromJson(dataobj[0].graph).data
		_loadGraph(g, gname);
		currentGraphConfig = dojo.fromJson(dataobj[0].graphconfig);
	}
}
function displayGraphName(gname){
	var url = "/site/wide_5678/wide.html.jsp?designMode=true&ce=true&page="+gname;
	var gnameDisp = "<a href='"+url+"'>"+gname+"</a>";
	dojo.byId("graphNameTd").innerHTML = "" + gnameDisp;
}
function _loadGraph(g, gname) {
	currentGraph = gname;
	pData.data = g
	displayGraphName(gname);
	draw();
}
function loadGraphAsObj(dataobj, gname) {
	currentGraph = gname;
	pData.data = dataobj;
	draw();
}

function CallWebServiceSilent(a, b, s, f) {
	dojo.xhrPost({
		url : getURL("SERVICEBASE") + a,
		content : {
			"tosave" : b
		},
		load : function(response, ioArgs) {
			if (s != null) {
				s(response);
			}
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			if (f != null) {
				f(response);
			}
			return response;
		}
	});
}
function CallWebServiceSimple(a, b, s, f) {
	dojo.xhrPost({
		url : a,
		content : b,
		load : function(response, ioArgs) {
			if (s != null) {
				s(response);
			}
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			if (f != null) {
				f(response);
			}
			return response;
		}
	});
}

function CallWebService(a, b) {
	dojo.xhrPost({
		url : getURL("SERVICEBASE") + a,
		content : {
			"tosave" : b
		},
		load : function(response, ioArgs) {
			// alert(response);
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			addErrorToBox("Failed to Save" + a.result);
			return response;
		}
	});

}
function getProcDoc(name) {
	// this is dummy original is named as function getProcDoc2(name){
}
function getProcDoc2(name) {
	dojo.xhrGet({
		url : getURL("PROCDOC") + "?name=" + name,
		load : function(response, ioArgs) {
			var doc = dojo.fromJson(response);
			var d = dojo.byId('docval');
			d.innerHTML = "<b>" + "" + "</b>";
			if (doc != null) {
				d.innerHTML = "<b>" + doc[0].doc + "</b>";
			}
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			return response;
		}
	});

}
function CallWebServiceWithName(a, b, c) {
	dojo.xhrPost({
		url : getURL("SERVICEBASE") + a,
		content : {
			"tosave" : b,
			"name" : c
		},
		load : function(response, ioArgs) {
			alert(response);
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to Save" + a.result);
			return response;
		}
	});
}

function showFirstGraph() {
	// alert("Show first graph");
	if (designMode == true) {
		getGraphDesignByName(currentGraph);
	} else {
		var graphname = currentGraph;
		dojo.xhrGet({
			url : getURL("GETGRAPH") + "?graphid=" + graphname,
			load : function(response, ioArgs) {
				if (response != null || respone != "") {
					var obj = dojo.fromJson(response)
					loadGraph(obj, graphname);
				}
				return response;
			},
			error : function(response, ioArgs) {
				var a = dojo.fromJson(response);
				alert("Failed to Load Graph" + a.result);
				return response;
			}
		});
	}
}
function GetUpdateProcesses() {
	var loc = dojo.byId('u_processclz');
	loc.innerHTML = "";
	dojo.xhrGet({
		url : getURL("PROCESSJSON"),
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.name
				}, loc);
			}

			);
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to retrive processtypes" + a.result);
			return response;
		}
	});
}
function GetProcesses() {
	var loc = dojo.byId('processclz');
	loc.innerHTML = "";
	dojo.xhrGet({
		url : getURL("PROCESSJSON"),
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.name
				}, loc);
			}

			);
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to retrive processtypes" + a.result);
			return response;
		}
	});
}
function GetGraphs(gtype) {
	var loc = dojo.byId('choosegraphid');
	loc.innerHTML = "";
	if (gtype == null)
		gtype = "graph"
	dojo.xhrGet({
		url : getURL("GETGRAPHS") + "?graphtype=" + gtype,
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.name
				}, loc);
			});
			var createConnectionDialog = dijit.byId('openGraphDialog');
			createConnectionDialog.show();
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to retrive processtypes" + a.result);
			return response;
		}
	});
}

function GetAndShowEventTypes(where, current) {
	var loc = dojo.byId(where);
	dojo.xhrGet({
		url : getURL("GETEVENTS"),
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.type
				}, loc);
			});
			if (current != null)
				loc.value = current;
			return response;
		},
		error : function(response, ioArgs) {
			return response;
		}
	});
}
function GetUpdateEventTypes(val) {
	var loc = dojo.byId('u_streameventtype');
	loc.innerHTML = "";
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'event') {
			temp = dojo.create("option", {
				innerHTML : obj.id
			}, loc);
		}
	}
	GetAndShowEventTypes('u_streameventtype', val);

}
function GetUpdateProcessors(val) {
	var loc = dojo.byId('u_streamprocessor');
	loc.innerHTML = "";
	var found = false;
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'node') {
			temp = dojo.create("option", {
				innerHTML : obj.id
			}, loc);
			if (val != null && val == obj.id) {
				found == true;
			}
		}
	}
	if (!found) {
		temp = dojo.create("option", {
			innerHTML : val
		}, loc);
	}
	loc.value = val;
}

function GetProcessors() {
	var loc = dojo.byId('streamprocessor');
	loc.innerHTML = "";
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'node') {
			temp = dojo.create("option", {
				innerHTML : obj.id
			}, loc);
		}
	}
}
function GetUpdateStreams(conn) {
	var loc = dojo.byId('u_fromNode');
	var loc2 = dojo.byId('u_toNode');
	loc2.innerHTML = "";
	loc.innerHTML = "";
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (conn != null && conn.ctype == "arbitconnection") {
			dojo.create("option", {
				innerHTML : obj.id
			}, loc);
			dojo.create("option", {
				innerHTML : obj.id
			}, loc2);
		} else {
			if (obj.type == 'stream') {
				dojo.create("option", {
					innerHTML : obj.id
				}, loc);
				dojo.create("option", {
					innerHTML : obj.id
				}, loc2);
			}
		}
	}
}

function GetStreams() {
	var loc = dojo.byId('fromNode');
	var loc2 = dojo.byId('toNode');
	loc2.innerHTML = "";
	loc.innerHTML = "";
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		//if (obj.type == 'stream')
		 {
			dojo.create("option", {
				innerHTML : obj.id
			}, loc);
			dojo.create("option", {
				innerHTML : obj.id
			}, loc2);
		}
	}
}
function GetEventTypes() {
	var loc = dojo.byId('streameventtype');
	loc.innerHTML = "";
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'event') {
			temp = dojo.create("option", {
				innerHTML : obj.id
			}, loc);
		}
	}
	GetAndShowEventTypes('streameventtype');
}
function showNoteDlg() {
	var loc = dojo.byId('noteforid');
	loc.innerHTML = "";
	dojo.create("option", {
		innerHTML : "null"
	}, loc);
	var nt = dijit.byId("addNodeDialog");
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		{
			dojo.create("option", {
				innerHTML : obj.id
			}, loc);
		}
	}
	nt.show();
}
function addNoteItem(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);
	var newNode = {};
	var sel = dojo.byId("noteforid");
	var sidx = sel.selectedIndex;
	var ntfor = sel.options[sidx].text;
	newNode["type"] = "textnode";
	newNode["normalizedx"] = 400;
	newNode["normalizedy"] = 400;
	if (ntfor == "null") {
		newNode["notefor"] = null;
	} else {
		newNode["notefor"] = ntfor;
	}
	newNode["icon"] = "";
	newNode["id"] = (maxdummy++);
	var pgct = dojo.byId('savenodeid');
	var a = pgct.value;
	newNode["txt"] = a;

	console.log("Successfully created the newNode");
	addObjectToGraph(newNode);
	console.log(pData.data[0].x);
	normalizeData();
	draw();
}
function removeNote() {
	if (lastSelectedNote != null) {
		var myid = lastSelectedNote;
		for (var i = 0; i < pData.data.length; i++) {
			var obj = pData.data[i];
			if (obj.type == 'textnode') {
				if (myid == obj.id) {
					removeObjectFromGraph(i, 1);
					lastSelectedNote = null;
					draw();
					break;
				}
			}
		}
	}
}
function doGetHtmlSync(url) {
	var ret = null;
	dojo.xhrGet({
		sync : true,
		url : url,
		load : function(data) {
			ret = data;
		}
	});
	return ret;
}
function doGetHtmlSyncWithContent(url, content) {
	var ret = null;
	dojo.xhrGet({
		sync : true,
		url : url,
		content : content,
		load : function(data) {
			console.log("sync get:" + data);
			ret = data;
		}
	});
	return ret;
}
function doGetHtml(url, fun1) {
	dojo.xhrGet({
		url : url,
		load : function(response, ioArgs) {
			if (fun1 != null) {
				fun1(response);
			}
			return response;
		},
		error : function(response, ioArgs) {
			if (fun1 != null) {
				fun1(response);
			}
			return response;
		}
	});
}
function doPostHtml(url, cnt, fun1) {
	dojo.xhrPost({
		url : url,
		content : {
			"tosave" : cnt
		},
		load : function(response, ioArgs) {
			alert(response);
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to Save" + a.result);
			return response;
		}
	});
}
function saveFormPage(cnt) {
	var url = getURL("NEWPAGE");
	doPostHtml(url, cnt, handleResults);
}
function runCmd() {
	var cmd = dojo.byId("cmdname").value;
	var url = getURL("RUNCMD") + cmd;
	doGetHtml(url, handleResults);
}
function handleResults(a) {
	var val = dojo.byId("cmdresult");
	val.value = a;
}
function addPropertyToEvent(evt, name, type, index) {
	var evt = findNodeById(evt);
	var prop = {};
	if (evt != null) {
		if (evt.props == null) {
			evt.props = {};
		}
		prop.searchable = 1;
		prop.name = name;
		prop.index = index;
		prop.type = type;
		evt.props[prop.index] = prop;
		console.log("Added property " + dojo.toJson(prop));
	}
}

function compile(name, code, update, run) {
	var url = getURL("CompileClosure");
	var req = {};
	req.code = code;
	req.name = name;
	if (update != null && update == true) {
		req.update = "true";
	}
	if (run != null && run == true) {
		req.run = "true";
	}

	postFormWithContent(url, req, function(res) {
		alert(res);
	});

}

function runScript(local) {
	var cmd = dojo.byId("cmdresult");
	var cd = cmd.value;
	if (local) {
		var fun = (new Function(cd));
		fun();
	} else {
		dojo.eval(cd);
	}
}
function runCompile() {
	var code = dojo.byId("cmdresult");
	var name = dojo.byId("cmdname");

	var cd = code.value;
	var nm = name.value;
	compile(nm, cd, true, true);
}
function runServerScript() {
	var code = dojo.byId("cmdresult");
	var name = dojo.byId("cmdname");
	var url = getURL("ServerScript");
	var req = {};
	req.script = code.value;
	req.name = name.value;
	postFormWithContent(url, req, function(res) {
		alert(res);
	});
}
function applyGraph() {
	var code = dojo.byId("cmdresult");
	var name = dojo.byId("cmdname");
	var req = {};
	req.script = code.value;
	req.name = name.value;
	_loadGraph(dojo.fromJson(req.script), req.name);

}

function saveCache() {
	var val = dojo.byId("cmdresult");
	var name = dojo.byId("cmdname");
	var cd = val.value;
	var nm = name.value;
	var url = getURL("SETCACHE");
	var req = {};
	req.val = cd;
	req.key = nm;

	postFormWithContent(url, req, function(res) {
		alert(res);
	});
}
function getCache() {
	var val = dojo.byId("cmdresult");
	var name = dojo.byId("cmdname");
	var cd = val.value;
	var nm = name.value;
	var url = getURL("GETCACHE");
	var req = {};
	req.key = nm;
	postFormWithContent(url, req, function(res) {
		val.value = res;

	});
}
function createDeploy() {
	postFormWithIframe("deployForm", getURL("deployURL"), function(rsp) {
		addMessageToBox(rsp);
		var createDeployDialog = dijit.byId('createDeployDialog');
		createDeployDialog.hide();
	}, function(rsp) {
		addErrorToBox("Error:" + rsp);
	});
}

function postFormWithIframe(formid, posturl, fun, fune) {
	dojo.io.iframe.send({
		method : "post",
		contentType : "multipart/form-data",
		handleAs : "text",
		url : posturl,
		load : function(response, ioArgs) {
			if (fun != null)
				fun(response);
		},
		error : function(response, ioArgs) {
			if (fune != null)
				fune(response);

		},
		form : dojo.byId(formid)
	});
}

function showHistorical() {
	var url = getURL("HistoricalGraphs") + currentGraph;
	doGetHtml(url, function(rs) {
		var s = dojo.fromJson(rs);
		var txt = "";
		var cnt = 0;
		for (var i = 0; i < s.length; i++) {
			txt += ("<br><u><b onclick=loadHistGraph(" + s[i].id + ")>"
					+ s[i].id + "&nbsp;&nbsp;&nbsp;&nbsp;" + s[i].name
					+ " (Ver." + cnt + ")" + "</u></b></br>");
			cnt++;
		}
		dojo.byId("histdata").innerHTML = txt;
	});
}
function loadHistGraph(id) {
	clearDesign();
	clearGraph();
	var url = getURL("HistoricalGraph") + id;
	doGetHtml(url, function(response) {
		try {
			var obj = dojo.fromJson(response)
			if (wideconfig.load_graph_on_design_open == "true") {
				compDiag = dojo.fromJson(obj[0].config);
				applyDesign();
				var name = (obj[0].name);
				loadGraph(obj, name)
			} else {
				compDiag = dojo.fromJson(obj[0].config);
				applyDesign();
			}
		} catch (exp) {
			getGraphFromServerByName(name);
		}
	});
}
function CallWebServiceWithNameAndConfig(a, b, c, d) {
	try {
		dojo.xhrPost({
			url : getURL("SERVICEBASE") + a,
			content : {
				"tosave" : c,
				"name" : b,
				"configsave" : d
			},
			load : function(response, ioArgs) {
				addInfoToBox(response);
				return response;
			},
			error : function(response, ioArgs) {
				addErrorToBox("Failed to Save: " + response);
				return response;
			}
		});
	} catch (error) {
		addErrorToBox("Failed to load resource: " + getURL("SERVICEBASE") + a);
	}
}

function enableBam() {
	var gid = currentGraph
	CallWebServiceWithNameAndConfig("SaveDesign", gid, dojo.toJson(pData), null)
}
function updateProcConfig(name) {
	dojo
			.xhrGet({
				url : getURL("PROCDOC") + "?name=" + lastSelectedNode.clz,
				load : function(response, ioArgs) {
					var configdlg = dojo.byId("ComponentConfigDialog");
					configdlg.innerHTML = "No Extra Configuration.";
					if(response!="[]"){
					var doc = dojo.fromJson(response);
					var cntn = "";
					cntn += "<form > <table>";
					var oldval = lastSelectedNode.configItems;
					if (oldval != null)
						oldval = dojo.fromJson(oldval);
					if (doc != null) {
						cfg = dojo.fromJson(doc[0].configoptions);
						if (cfg != null) {
							for (var i = 0; i < cfg.length; i++) {
								cntn += "<tr>";
								var a = cfg[i];
								var val = oldval == null ? "" : oldval[a];
								cntn += "<td><label for=\"id" + a + "\">" + a
										+ "</label></td>";
								cntn += "<td><input type=text id=\"id" + a
										+ "\" name=\"" + a + "\" value=\""
										+ val + "\"></input></td>"
							}
							cntn += "<tr>";
							cntn += "<td><input type=\"button\" name=\""
									+ "SaveExtraConfigBtn"
									+ "\" onclick=\"getprocargs();\"  value=\"Save\"></input></td>"
							cntn += "</tr>";
							cntn += "</form></table>";
							configdlg.innerHTML = cntn;
						}
					}
					return response;
					}
				},
				error : function(response, ioArgs) {
					var configdlg = dojo.byId("ComponentConfigDialog");
					configdlg.innerHTML = "No Extra Configuration.";
					var a = dojo.fromJson(response);
					return response;
				}
			});
}
function setNodeConfigDlg() {
	getProcConfig();
}

function getProcConfig(name) {
	if (configdlg == null) {
		configdlg = new dijit.Dialog({
			title : "Node Extra Configuration",
			style : "width: 300px"
		});
	}

	dojo
			.xhrGet({
				url : getURL("PROCDOC") + "?name=" + lastSelectedNode.clz,
				load : function(response, ioArgs) {
					var doc = dojo.fromJson(response);
					configdlg.attr("content", "No Extra Configuration.");
					var cntn = "";
					cntn += "<form > <table>";
					var oldval = lastSelectedNode.configItems;
					if (oldval != null)
						oldval = dojo.fromJson(oldval);
					if (doc != null) {
						cfg = dojo.fromJson(doc[0].configoptions);
						if (cfg != null) {
							for (var i = 0; i < cfg.length; i++) {
								cntn += "<tr>";
								var a = cfg[i];
								var val = oldval == null ? "" : oldval[a];
								cntn += "<td><label for=\"id" + a + "\">" + a
										+ "</label></td>";
								cntn += "<td><input type=text id=\"id" + a
										+ "\" name=\"" + a + "\" value=\""
										+ val + "\"></input></td>"
							}
							cntn += "<tr>";
							cntn += "<td><input type=\"button\" name=\""
									+ "SaveExtraConfigBtn"
									+ "\" onclick=\"logargs();\"  value=\"Save\"></input></td>"
							cntn += "<td><input type=\"button\" name=\""
									+ "CloseCfgDlg"
									+ "\" onclick=\"configdlg.hide();\"  value=\"Close\"></input></td>"
							cntn += "</tr>";
							cntn += "</form></table>";
							configdlg.attr("content", cntn);
						}

					}
					configdlg.show();
					return response;
				},
				error : function(response, ioArgs) {
					var a = dojo.fromJson(response);
					return response;
				}
			});
}
function getprocargs() {
	var mycfg = {};
	if (cfg != null) {
		for (var i = 0; i < cfg.length; i++) {
			var val = dojo.byId("id" + cfg[i]).value;
			mycfg[cfg[i]] = val;
		}
	}
	lastSelectedNode.configItems = dojo.toJson(mycfg);
}
function logargs() {
	var a = configdlg.getValues();
	// alert(dojo.toJson(a));
	var mycfg = {};
	configdlg.hide();
	if (cfg != null) {
		for (var i = 0; i < cfg.length; i++) {
			var val = dojo.byId("id" + cfg[i]).value;
			mycfg[cfg[i]] = val;
		}
	}
	lastSelectedNode.configItems = dojo.toJson(mycfg);
}
function hidediv(id) {
	// safe function to hide an element with a specified id
	if (document.getElementById) { // DOM3 = IE5, NS6
		document.getElementById(id).style.display = 'none';
	} else {
		if (document.layers) { // Netscape 4
			document.id.display = 'none';
		} else { // IE 4
			document.all.id.style.display = 'none';
		}
	}
}

function showdiv(id) {
	// safe function to show an element with a specified id

	if (document.getElementById) { // DOM3 = IE5, NS6
		document.getElementById(id).style.display = 'block';
	} else {
		if (document.layers) { // Netscape 4
			document.id.display = 'block';
		} else { // IE 4
			document.all.id.style.display = 'block';
		}
	}
}
function compileAndRunMapperOnServer() {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.run = "true";
	var url = getURL("CompileMapper2");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function compileAndRunMapperOnServerUserTemplate(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	mtype = prompt
	mtype = prompt('Enter Template to use for compilation', "FreeSpring");
	if (mtype == null || mtype == "") {
		mtype = null;
	}
	if (eventtype != null)
		req.eventtype = eventtype;
	req.run = "true";
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileMapper2");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function compileMapperOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileMapper2");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileTagModelOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("compileTagModel");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileEv3Path(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("GetEv3Path");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function compileTodoMapperOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileTodoMapper");
	postFormWithContent(url, req, function(res) {
		var a = dojo.fromJson(res);
		var b = {};
		b["<U><B>Title</U></B>"] = "<B><U>Completion Status</U></B>";
		for (i in pData.data) {
			var obj = pData.data[i];
			if (obj.type == "ToDoEvent") {
				var id = obj.id;
				b[obj.title] = a[id];
				b[obj.title + "-Total"] = a[id + "Total"];
				b[obj.title + "-Perc"] = (a[id] / a[id + "Total"]) * 100
				if (a[id] == a[id + "Total"]) {
					flatGreenLEDGraph(obj.id, 20000);
				} else {
					flatRedLEDGraph(obj.id, 20000);
				}
			}
		}

		resultMsgDlgObj(b);
	});
}
function compileTodoDistances(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("GetTodoDistances");
	postFormWithContent(url, req, function(res) {
		var a = dojo.fromJson(res);
		for ( var item in a) {
			var from = item;
			var to = a[from];
			var selectedNode = findNodeById(from);
			var bb1 = {};
			var bb2 = {};
			if (selectedNode != null) {
				bb1.x = selectedNode.normalizedx;
				bb1.y = selectedNode.normalizedy;
				bb1.width = selectedNode.b / 2;
				bb1.height = selectedNode.r / 2;
				var led = pCanvas.circle(selectedNode.normalizedx,
						selectedNode.normalizedy, selectedNode.b);
				led.attr("fill", "green");
				led.attr("stroke", "green");
				led.attr({
					"fill-opacity" : .2
				});
				led.toBack();
			}
			selectedNode = findNodeById(to);
			if (selectedNode != null) {
				bb2.x = selectedNode.normalizedx;
				bb2.y = selectedNode.normalizedy;
				bb2.width = selectedNode.b / 2;
				bb2.height = selectedNode.r / 2;
				var led = pCanvas.circle(selectedNode.normalizedx,
						selectedNode.normalizedy, selectedNode.b);
				led.attr("fill", "green");
				led.attr("stroke", "green");
				led.attr({
					"fill-opacity" : .2
				});
				led.toBack();
			}
			var ar = pCanvas.connectionarrow(bb1, bb2, 8);
			ar[0].attr("stroke-width", "5");
			ar[0].attr({
				"fill-opacity" : .2
			});
			ar[0].attr({
				"opacity" : .2
			});
			ar[0].attr("stroke", "green");
			ar[1].attr("stroke", "green");
			ar[1].attr("fill", "orange");
			ar[1].attr("stroke-dasharray", "-");
			ar[1].attr({
				"fill-opacity" : .2
			});
			ar[1].attr({
				"opacity" : .2
			});

		}
	});
}
function compileFastCamelOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileFastCamel");
	postFormWithContent(url, req, function(res) {
		resultMsgDlgObj(res);
	});
}
function compileFastLocalOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileLocalGraph");
	postFormWithContent(url, req, function(res) {
		resultMsgDlgObj(res);
	});
}
function compileMapperOnServer2(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileMapper2");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileMapperOnServer3(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	mtype = prompt
	mtype = prompt('Enter Template to use for compilation', "FreeSpring");
	if (mtype == null || mtype == "") {
		mtype = null;
	}
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileMapper2");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileMapperOnServer5(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var filePath = prompt('Enter FilePath to Process', "");
	req.filePath = filePath;
	var mainTable = prompt('Enter mainTable to Process', "");
	req.mainTable = mainTable;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileTypeMapping");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function compileImageCollage(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("ImageCollage");
	postFormWithContent(url, req, function(res) {
		var a = dojo.fromJson(res);
		for (var i = 0; i < a.length; i++) {
			var n = a[i];
			pCanvas.rect(n.bounds.x, n.bounds.y, n.bounds.w, n.bounds.h);
		}

	});
}

function compileMapperOnServerToJavaScriptCode(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.handler = "javascriptsrc";
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileTaskPlanToCode");
	postFormWithContent(url, req, function(res) {
		res = res.replace(/boolean/g, "var");
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileMapperOnServerToVizPipeCode(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.handler = "javasrc";
	req.trace = true;
	req.mappingtype = "vizpipe";
	if (eventtype != null)
		req.eventtype = eventtype;
	var url = getURL("CompileTaskPlanToCode");
	postFormWithContent(url, req, function(res) {
		console.log("inside: compileMapperOnServerToVizPipeCode  " + res);
		var jsonRes = dojo.fromJson(res);
		if (jsonRes.component != null && jsonRes.component.length > 0) {
			showErrorModule(jsonRes.component);
			dynaHtmlDlg(jsonRes.msg, "Error!", null, "", "ok");
		} else {
			dynaHtmlDlg(jsonRes.msg, "Message", null, "", "ok");
		}
	});
}
function compileMapperOnServerToOpenCVCode(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.handler = "javasrc";
	req.trace = true;
	req.mappingtype = "opencv";
	if (eventtype != null)
		req.eventtype = eventtype;
	var url = getURL("CompileTaskPlanToCode");
	postFormWithContent(url, req, function(res) {
		console.log("inside: CompileTaskPlanToCode_JAVA  " + res);
		var jsonRes = dojo.fromJson(res);
		if (jsonRes.component != null && jsonRes.component.length > 0) {
			showErrorModule(jsonRes.component);
			dynaHtmlDlg(jsonRes.msg, "Error!", null, "", "ok");
		} else {
			dynaHtmlDlg(jsonRes.msg, "Message", null, "", "ok");
		}
	});
}

function compileMapperOnServerToJavaCode(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.handler = "javasrc";
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileTaskPlanToCode");
	postFormWithContent(url, req, function(res) {
		console.log("inside: CompileTaskPlanToCode_JAVA  " + res);
		var jsonRes = dojo.fromJson(res);
		if (jsonRes.component != null && jsonRes.component.length > 0) {
			showErrorModule(jsonRes.component);
			dynaHtmlDlg(jsonRes.msg, "Error!", null, "", "ok");
		} else {
			dynaHtmlDlg(jsonRes.msg, "Message", null, "", "ok");
		}
	});
}

function compilePatternOnServerToCode(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompilePattern");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileMapperOnServerToCode(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileTaskPlanToCode");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function runImageTestCase1(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	var targetImage = prompt("Enter target Image");
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.targetImage=targetImage;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("RunImageTestCase");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function runAutomation1(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("RunAutomationProcess");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileMapperOnServer6(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileTaskPlanV2");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileSpringConfigOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	req.processingPlan = "CompileSpringConfig";
	var url = getURL("ProcessingPlan");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileClassOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	req.processingPlan = "CompileObjectClasses";
	var url = getURL("ProcessingPlan");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileCompileCamelPlan(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileCamelPlan");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function getLayout(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.mainTable = null;
	req.trace = true;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("GetLayout");
	postFormWithContent(url, req, function(res) {
		var m = dojo.fromJson(res);
		for ( var i in m) {
			var p = m[i];
			var ln = pCanvas.line(p);
			ln.attr("stroke", "pink");
			ln.attr("stroke-dasharray", "-");
			ln.attr("stroke-width", "2");
		}
	});
}

function compileSubGraphOnServer(name) {
	if (name == null || name == "") {
		name = prompt("Please enter graph name");
	}
	if (name == null || name == "")
		name = currentGraph;
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = name;
	req.process = objstr;
	req.isobject = "true";
	var mx = mouseX, my = mouseY;
	var url = getURL("CompileSubGraph");
	postFormWithContent(url, req, function(res) {
		var r = dojo.fromJson(res);
		var ins = r.inputPorts;
		var outs = r.outputPorts;
		var auxs = r.auxPorts;
		var name = r.name;
		var a = createAnonDefObj(mx, my, 100, 5 * 15, "Sub-" + name, ins, outs,
				auxs, "SubGraph");
		a.configItems = "graph:" + name + ";";
		pData.data.push(a);
		drawAnonDef(mx, my, 200, 100, "orange", "orange", a, null);
		console.log(a);

	});
}

function compileGcodeMapperOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(filterLayer(pData.data));
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.design = dojo.toJson(compDiag);
	req.isobject = "true";
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileGcode");
	postFormWithContent(url, req, function(res) {
		console.log("inside: CompileGcode  " + res);
		var jsonRes = dojo.fromJson(res);
		if (jsonRes.component != null && jsonRes.component.length > 0) {
			addErrorToBox(jsonRes.msg);
		} else {
			addInfoToBox(jsonRes.msg);
			downloadGcode();
		}
	});
}
function runViaGrblControllerOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(filterLayer(pData.data));
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.design = dojo.toJson(compDiag);
	req.isobject = "true";
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("GrblController");
	postFormWithContent(url, req, function(res) {
		console.log("inside: GrblController  " + res);
		var jsonRes = dojo.fromJson(res);
		if (jsonRes.component != null && jsonRes.component.length > 0) {
			showErrorModule(jsonRes.component);
			dynaHtmlDlg(jsonRes.msg, "Error!", null, "", "ok");
		} else {
			dynaHtmlDlg(jsonRes.msg, "Message", null, "", "ok");
		}
	});
}

function triangulateOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(filterLayer(pData.data));
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.design = dojo.toJson(compDiag);
	req.isobject = "true";
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("triangulate");
	postFormWithContent(url, req, function(res) {
		console.log("inside: triangulate  " + res);
		console.log(res);
		eval(res);
	});
}

function shapeOffsetOnServerInside(mtype, eventtype) {
	shapeOffsetOnServerInternal(mtype, eventtype,"inside"); 
}
function shapeCavityOnServer(mtype, eventtype) {
	shapeOffsetOnServerInternal(mtype, eventtype,"cavity"); 
}
function shapeOffsetOnServerOutside(mtype, eventtype) {
	shapeOffsetOnServerInternal(mtype, eventtype,"outside"); 
}
function shapeOffsetOnServer(mtype, eventtype,dir) {
	shapeOffsetOnServerInternal(mtype, eventtype,"inside"); 
}
function shapeOffsetOnServerInternal(mtype, eventtype,dir) {
	var objstr = dojo.toJson(filterLayer(pData.data));
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.design = dojo.toJson(compDiag);
	req.isobject = "true";
	req.toolsizeinmm = wideconfig.toolsizeinmm;
	req.pixelsPerUnit=wideconfig.pixelsPerUnit
	req.numofpoints=wideconfig.numofpoints
	
	req.dir=dir;
	
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("ShapeOffset");
	postFormWithContent(url, req, function(res) {
		console.log("inside: ShapeOffset  " + res);
		console.log(res);
		eval(res);
	});
}

function shapePixalateOnServer(mtype, eventtype) {
	shapePixalateOnServerInternal("*",wideconfig.toolsizeinmm,wideconfig.pixelsPerUnit,wideconfig.numofpoints,mtype,eventtype,null);
}
function shapePixalateOnServerInternal(id,toolsizeinmm,pixelsPerUnit,numofpoints,mtype, eventtype,f) {
	var objstr = dojo.toJson(filterLayer(pData.data));
	var req = {};
	req.name = currentGraph;
	req.id = id;
	req.process = objstr;
	req.design = dojo.toJson(compDiag);
	req.isobject = "true";
	req.toolsizeinmm = toolsizeinmm;
	req.pixelsPerUnit=pixelsPerUnit
	req.numofpoints=numofpoints;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("ShapePixalate");
	postFormWithContent(url, req, function(res) {
		console.log("inside: shapePixalateOnServer  " + res);
		console.log(res);
		if(f!=null)
			f(res);
		else	
			var pts  =dojo.fromJson(res);
		for(var i=0;i<pts.length;i++){
			pCanvas.circle(pts[i].x,pts[i].y,2).attr("stroke","green");
		}
	});
}
function shapeMapperOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(filterLayer(pData.data));
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.design = dojo.toJson(compDiag);
	req.isobject = "true";
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("ShapeMapper");
	postFormWithContent(url, req, function(res) {
		console.log("inside: shapeMapperOnServer  " + res);
		console.log(res);
		eval(res);
	});
}
function pathToPointsOnServer(id,mtype, eventtype,pointDistance,startingDistance,endDistance,numOfPoints,f) {
	var objstr = dojo.toJson(filterLayer(pData.data));
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.design = dojo.toJson(compDiag);
	req.id = id;
	req.isobject = "true";
	req.numOfPoints = numOfPoints;
	req.startingDistance = startingDistance;
	req.endDistance = endDistance;
	req.pointDistance=pointDistance;
	req.toolsizeinmm = wideconfig.toolsizeinmm;
	req.pixelsPerUnit=wideconfig.pixelsPerUnit
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("PathToPoints");
	postFormWithContent(url, req, function(res) {
		console.log("inside: pathToPointsOnServer  ");
		console.log(res);
		f(res);
	});
}
function compileObjectMapperOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.isobject = "true";
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileObjectMapping");
	postFormWithContent(url, req, function(res) {
		console.log("inside: CompileObjectMapping  " + res);
		var jsonRes = dojo.fromJson(res);
		if (jsonRes.component != null && jsonRes.component.length > 0) {
			showErrorModule(jsonRes.component);
			dynaHtmlDlg(jsonRes.msg, "Error!", null, "", "ok");
		} else {
			dynaHtmlDlg(jsonRes.msg, "Message", null, "", "ok");
		}
	});
}
function showErrorModule(nodeid) {
	var rotate = 0;
	if (filter[nodeid] == null) {
		var ret = findComponentByName(nodeid);
		if (ret != null) {
			rotate = ret.rotation;
			var realpos = null;
			if (rotate != null) {
				if (rotate != "0deg") {
					setCSSRotation(nodeid, 0);
					realpos = dojo.position(nodeid)
					setCSSRotation(nodeid, rotate);
				} else {
					realpos = dojo.position(nodeid)
				}
			} else {
				realpos = dojo.position(nodeid)
			}
			var exists = moduleExists(nodeid);
			if (!exists) {
				var f = comps[ret.name];
				var configFun = configs[ret.name];
				if (configFun != null) {
					configFun(f, null, ret.name, false, null, null, ret);
				}
			}
			realpos.x -= leftWidth;
			realpos.y -= topWidth;
			realpos.r = realpos.w;
			realpos.b = realpos.h;
			var rect2 = null;
			rect2 = pCanvas.rect(realpos.x - 5, realpos.y - 5, realpos.w + 10,
					realpos.h + 10);
			rect2.attr("stroke", "red").attr("stroke-width", 5);
			rotate = getDegRotation(rotate);
			if (rotate > 0) {
				rect2.rotate(rotate);
			}
		}
	}
}

function compileMapperOnServer4(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var mainTable = prompt('Enter mainTable to Process', "");
	req.mainTable = mainTable;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileTypeMapping");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function compileWebUIFlow(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var url = getURL("CompileWebUIFlow");
	postFormWithContent(url, req, function(res) {
		dojo.eval(res);
	});
}
function runUICodeFlow(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var url = getURL("RunUICodeFlow");
	postFormWithContent(url, req, function(res) {
		console.log(res);
		dojo.eval(res);
	});
}
function compileDFStateMachineOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	var url = getURL("CompileDFStateMachine");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function compileGraphOnServer() {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var url = getURL("CompileGraphOnServer");
	postFormWithContent(url, req, function(res) {
		resultMsgDlg(res);
	});
}
function CompileProcess(list) {
	/* Result */
	var data = null;
	if (list == null)
		data = pData.data;
	else {
		data = new Array();
		for ( var item in list) {
			data.push(findNodeById(list[item]));
		}
	}
	var str = "";
	for (i in data) {
		if (data[i] != null) {
			if (data[i].type == "node") {
				str += data[i].id + "\n";
			} else if (data[i].type == "stream") {
				str += data[i].id + "\n";
			}
			saveGraphEle(data[i]);
		}
	}

	// alert(str);
	function saveGraphEle(obj) {

		if (obj.type == "stream") {
			var objstr = dojo.toJson(obj);
			CallWebServiceSilent("SaveStream", objstr);
		}

		if (obj.type == "node") {
			var objstr = dojo.toJson(obj);
			CallWebServiceSilent("SaveNode", objstr);
		}

		if (obj.type == "event") {
			var objstr = dojo.toJson(obj);
			CallWebServiceSilent("SaveEvent", objstr);
		}

		if (obj.type == "connection") {
			var objstr = dojo.toJson(obj);
			CallWebServiceSilent("SaveConnection", objstr);
		}

		if (obj.type == "route") {
			var objstr = dojo.toJson(obj);
			CallWebServiceSilent("SaveRoute", objstr);
		}
	}
}
function setSystemVar(name, val) {
	var req = {};
	req.name = name;
	req.val = val;
	postFormWithContent(getURL("SetVar"), req, function(res) {
		addInfoToBox(res);
	});
}

function receiveMessage(event) {
	console.log("Received message: "+event.data);
	// event.origin
	// event.source
	// event.data is "hi there yourself! the secret response is: rheeeeet!"
	if (event.data != null) {
		var msg = dojo.fromJson(event.data);
		if (msg.from == "showAceEditor" ||msg.from == "showEditor") {
			var a =  msg.data;
			var uid = msg.id;
		
			globals[uid + "code"] = globals["popupreturn"];
			var ret = findComponentByName(uid);
			if (ret != null) {
				ret.code = globals[uid + "code"];
			}
				if(msg.target=="getAnonScriptStaticModule"){
					var a =  msg.data;
					var uid = msg.id;
					findComponentByName(uid).script = a;
					var gnode = findNodeById(uid)
					if(gnode!=null){
						gnode.script = a;
					}
					var content = {};
					content.grpid = uid;
					content.code = a;
					content.template="Script";
					var html = doGetHtmlSyncWithContent(getURL("CodeToPortJava"),content);
					dojo.byId("bid_"+uid).innerHTML=html;
					preparePortable();
				    var pos = dojo.position(uid);
					var evtdata = {};
					evtdata.id = uid;
					evtdata.type="ssmmovestop";
					evtdata.pos = pos;
					sendEvent(geq, [evtdata]) ;
					syncModuleCode(uid);
				} 
				if(msg.target=="getStaticModule"){
					var a =  msg.data;
					var uid = msg.id;
					findComponentByName(uid).cntn = a;
					dojo.byId("bid_"+uid).innerHTML="<span>"+a+"</span>";
					loadDynaHTMLBodyScripts(dojo.byId("bid_"+uid));
				}
				if(msg.target=="editStreamPage"){
					var a =  msg.data;
					var uid = msg.id;
					dojo.byId("pagecontent").value = a;
				}
				if(msg.target=="editJavaProcess"){
					var a =  msg.data;
					var uid = msg.id;
				var selectedNode = findNodeById(uid);
				if (lastSelectedNode.type == "node") {
					selectedNode.query = a;
					selectedNode.clz = "org.ptg.processors.JavaProcess";
				} else if (lastSelectedNode.type == "region") {
					selectedNode.code = a;
				}
				}
				if (lastSelectedNode.type == "myregion") {
				var a =  msg.data;
				var uid = msg.id;
				var mynode = findNodeById(uid);
				mynode.code = (a);
				displaySelectedNodeProps(nd);
				}
				if (lastSelectedNode.type == "createHeaderLable"||lastSelectedNode.type == "createLable"){
				var a =  msg.data;
				var uid = msg.id;
				findComponentByName(uid).label = a;
				dojo.byId("bid_" + uid).innerHTML = "<div>" + a + "</div>";
				}
			}
		}
}
window.addEventListener("message", receiveMessage, false);

function showAceEditor(cd, uid, postdoneMsg) {
	console.log("showAceEditor");
	globals["popupreturn"] = cd;
	globals["popupreturn_name"] = uid;
	window.showModalDialog("/site/bam_5678/liteeditor.html.jsp?id="+ uid+ "&f=showAceEditor&msg=" + postdoneMsg, "Code Editor","dialogWidth:900px;dialogHeight:900px");
	// showModalDialog("/site/bam_5678/liteeditor.html.jsp","Code
	// Editor","Width:900px;Height:900px")
}
function showModalDialog(url, titlestr, style) {
	window.open(url, titlestr, style);
}
function showEditor(cd, uid, postdoneMsg) {
	console.log("showEditor");
	globals["popupreturn"] = cd;
	globals["popupreturn_name"] = uid;
	window.showModalDialog("/site/bam_5678/javaeditor.html.jsp?id=" + uid + "&f=showEditor&msg=" + postdoneMsg, "Function Editor","dialogWidth:900px;dialogHeight:900px");
	// showModalDialog("/site/bam_5678/javaeditor.html.jsp","Function
	// Editor","Width:900px;Height:900px")
}
function editJavaProcess(uid) {
	if (uid == null)
		uid = lastSelectedNode.id;
	if (lastSelectedNode.type != "node") {
		alert("Please select a node to edit");
	} else {
		var code = lastSelectedNode.query == null ? "" : lastSelectedNode.query;
		showEditor(code, uid,"editJavaProcess");
	}
}
function editStreamPage() {
	var cd = dojo.byId("pagecontent").value;
	var uid = "NewPageDialog"
	showEditor(cd, uid,"editStreamPage");
}
function fixNode(name) {
	var a = findNodeById(name);
	if (a != null) {
		var b = {};
		for ( var i in a) {
			if (i != "defs")
				b[i] = a[i];
		}
		removeNodeById(name);
		addObjectToGraph(b);
		draw();

	}
}
function fixStream(name) {
	var a = findNodeById(name);
	if (a != null) {
		var b = {};
		for ( var i in a) {
			if (i != "props")
				b[i] = a[i];
		}
		removeNodeById(name);
		addObjectToGraph(b);
		draw();
	}
}
function exportGraph() {
	var a = dojo.toJson(pData.data)
	dojo.byId("cmdresult").value = (a);
}
function exportSelection() {
	var a = dojo.toJson(cpBuffer)
	dojo.byId("cmdresult").value = (a);
}

function cloneItem(a) {
	var b = {};
	if (a != null) {
		for ( var i in a) {
			if (typeof (a[i]) == "object") {
				b[i] = cloneItem(a[i]);
			} else {
				b[i] = a[i];
			}

		}
		return b;
	} else {
		return null;
	}

}
function validateProcess() {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var url = getURL("ValidateProcess");
	postFormWithContent(url, req, showGraphStatus);
}

function showGraphStatus(str) {
	var a = dojo.fromJson(str);
	/*
	 * console.log(a); for(var i in a){ var s = i.split(".");
	 * console.log(s[s.length-1]); }
	 */
	matchGraphStatus(a);
	resultMsgDlg(str);
}
function statustag(name, text, t) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var mytext = pCanvas.text(selectedNode.normalizedx - selectedNode.r / 2
				- 20, selectedNode.normalizedy - selectedNode.b / 2 - 20, text);
		var ledcomp = {
			"item" : mytext,
			"id" : name + "ValidationTag"
		};
		drawElements.push(ledcomp);
	}
}
function streamStatustag(name, text, t) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var mytext = pCanvas.text(selectedNode.normalizedx + selectedNode.r / 2
				+ 20, selectedNode.normalizedy + selectedNode.b / 2 + 20, text);
		var ledcomp = {
			"item" : mytext,
			"id" : name + "ValidationTag"
		};
		drawElements.push(ledcomp);
	}
}
function flatLED(name, color, t) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var led = pCanvas.rect(selectedNode.normalizedx - selectedNode.r / 2
				- 20, selectedNode.normalizedy - selectedNode.b / 2 - 20, 40,
				4, 2);
		if (color == "red") {
			led.attr("fill", failstatusled);
			led.attr("stroke", failstatusled);
		}
		if (color == "green") {
			led.attr("fill", statusled);
			led.attr("stroke", statusled);
		}

		led.animate({
			"fill-opacity" : .2
		}, t, "bounce", function() {
			// led.remove();
		});
		var ledcomp = {
			"item" : led,
			"id" : name + "ValidationLED"
		};
		drawElements.push(ledcomp);
	}
}
/*
 * Processor.Instance. Processor.Route. Connection.Route. Connection.Instance.
 * Connection.Processor. Stream.Route. Stream.Instance.
 * EventDefinition.Instance. stream node event connection
 */
function matchGraphStatus(st) {
	for ( var i in pData.data) {
		var ele = pData.data[i];
		var id = ele.id;
		var text = "";
		if (ele.type == "node") {
			if (st["Processor.Instance." + ele.id] == true) {
				console.log(id + " " + "Processer instance is fine.")
			} else {
				console.log(id + " " + "Processer instance is not fine.")
			}

			if (st["Processor.Route." + ele.id] == true) {
				console.log(id + " " + "Processer route is fine.")
			} else {
				console.log(id + " " + "Processer route is not fine.")
			}
			if ((st["Processor.Instance." + ele.id] == true)
					&& (st["Processor.Route." + ele.id] == true)) {
				flatLED(id, "green", 5000);
			} else {
				flatLED(id, "red", 5000);
			}

			if (st["Processor.Calls." + ele.id] != null) {
				console.log("Processor calls made: "
						+ st["Processor.Calls." + ele.id]);
				text += "[s: " + st["Processor.Calls." + ele.id] + " ]"
			} else {
				console.log("Cannot find no of processor callss.")
			}
			if (st["Processor.ExpCalls." + ele.id] != null) {
				console.log("Processor calls made: "
						+ st["Processor.ExpCalls" + ele.id])
				text += "[f: " + st["Processor.ExpCalls." + ele.id] + " ]"
			} else {
				console.log("Cannot find no of processor exception calls.")
			}

			if (st["Processor.DBRecord." + ele.id] == true) {
				console.log(id + " " + "Processer dbrecord is fine.")
			} else {
				console.log(id + " " + "Processer dbrecord is not fine.")
			}
			text += "{p.db: " + st["Processor.DBRecord." + ele.id] + " }";
			statustag(ele.id, text, 5000);
		}
		if (ele.type == "stream") {
			if (st["Stream.Instance." + ele.id] == true) {
				console.log(id + " " + "Stream instance is fine.")
			} else {
				console.log(id + " " + "Stream instance is not fine.")
			}
			if (st["Stream.Route." + ele.id] == true) {
				console.log(id + " " + "Stream route is fine.")
			} else {
				console.log(id + " " + "Stream route not is fine.")
			}
			if ((st["Stream.Instance." + ele.id] == true)
					&& (st["Stream.Route." + ele.id] == true)) {
				console.log("Stream : " + id + "will paint " + "green");
				flatLED(id, "green", 5000);
				var ele = findDrawEleByItemId(id);
				if (ele != null) {
					if (ele.subs != null) {
						for (var j = 0; j < ele.subs.length; j++) {
							ele.subs[j].attr("stroke", "green");
							ele.subs[j].attr("stroke-width", 2);
							ele.subs[j].attr("stroke-dasharray", "-.-");
						}
					}
				}
			} else {
				console.log("Stream : " + id + "will paint " + "red");
				flatLED(id, "red", 5000);
				var ele = findDrawEleByItemId(id);
				if (ele != null) {
					if (ele.subs != null) {
						for (var j = 0; j < ele.subs.length; j++) {
							ele.subs[j].attr("stroke", "red");
							ele.subs[j].attr("stroke-width", 2);
							ele.subs[j].attr("stroke-dasharray", "-.-");
						}
					}
				}
			}
			if (st["Stream.DBRecord." + ele.id] == true) {
				console.log(id + " " + "Stream dbrecord is fine.")
			} else {
				console.log(id + " " + "Stream dbrecord is not fine.")
			}
			if (st["Stream.PageCount." + ele.id] != null) {
				console.log(id + " " + "Stream has following pages: "
						+ st["Stream.PageCount." + ele.id])
			} else {
				console.log(id + " " + "Stream does not have pages.")
			}
			if (st["Stream.PageNames." + ele.id] != null) {
				console.log(id + " " + "Stream has pages: "
						+ st["Stream.PageNames." + ele.id])
			} else {
				console.log(id + " " + "Stream does not have pages.")
			}
			text += "{s.db: " + st["Stream.DBRecord." + ele.id] + " }";
			text += "{s.pc: " + st["Stream.PageCount." + ele.id] + " }";
			text += "{s.pn: " + st["Stream.PageNames." + ele.id] + " }";

			streamStatustag(ele.id, text, 5000);

		}

		if (ele.type == "event") {

			if (st["EventDefinition.Instance." + ele.id] == true) {
				flatLED(id, "green", 5000);
				console.log(id + " " + "Event definition is fine.")
			} else {
				flatLED(id, "red", 5000);
				console.log(id + " " + "Event Definition  is not fine.")
			}

		}
		if (ele.type == "connection") {
			var ele = findDrawEleByItemId(id);

			if (st["Connection.Route." + ele.id] == true) {
				console.log(id + " " + "Conn route is fine.")
			} else {
				console.log(id + " " + "conn route is not fine.")
			}
			text += "{c.r: " + st["Connection.Route." + ele.id] + " }\n";

			if (st["Connection.Instance." + ele.id] == true) {
				console.log(id + " " + "conn instance is fine.")
			} else {
				console.log(id + " " + "conn instance is not fine.")
			}
			text += "{c.i: " + st["Connection.Instance." + ele.id] + " }\n";

			if (st["Connection.Processor." + ele.id] == true) {
				console.log(id + " " + "connection processor is fine.")
			} else {
				console.log(id + " " + "conn processor is not fine.")
			}
			text += "{c.p: " + st["Connection.Processor." + ele.id] + " }\n";

			if ((st["Connection.Route." + ele.id] == true)
					&& (st["Connection.Instance." + ele.id] == true)
					&& (st["Connection.Processor." + ele.id] == true)) {
				if (ele != null) {
					if (ele.subs != null) {
						for (var j = 0; j < ele.subs.length; j++) {
							ele.subs[j].attr("stroke", "green");
							ele.subs[j].attr("stroke-width", 2);
							ele.subs[j].attr("stroke-dasharray", "---");
						}
					}
				}
			} else {
				if (ele != null) {
					if (ele.subs != null) {
						for (var j = 0; j < ele.subs.length; j++) {
							ele.subs[j].attr("stroke", "red");
							ele.subs[j].attr("stroke-width", 2);
							ele.subs[j].attr("stroke-dasharray", "---");
						}
					}
				}
			}
			if (st["Connection.ProcDBRecord." + ele.id] == true) {
				console
						.log(id + " "
								+ "Connection Processor dbrecord is fine.")
			} else {
				console.log(id + " "
						+ "Connection Processor dbrecord is not fine.")
			}
			if (st["Connection.StreamDBRecord." + ele.id] == true) {
				console.log(id + " " + "Connection Stream dbrecord is fine.")
			} else {
				console.log(id + " "
						+ "Connection Stream dbrecord is not fine.")
			}
			text += "{c.pdb: " + st["Connection.ProcDBRecord." + ele.id]
					+ " }\n";
			text += "{c.sdb: " + st["Connection.StreamDBRecord." + ele.id]
					+ " }";
			var myline = ele.subs[0];
			var a = myline.getTotalLength();
			var point = myline.getPointAtLength(a / 2);
			var x = point.x;
			var y = point.y;
			var mytext = pCanvas.text(x, y, text);
			var ledcomp = {
				"item" : mytext,
				"id" : name + "ConnValidTag"
			};
			// mytext.rotate(-90);
			drawElements.push(ledcomp);
		}
	}

}

/** ******************************* */
function stopRE() {
	var req = {};
	req.name = "StopRE";
	var url = getURL("StopRoutingEngine");
	postFormWithContent(url, req, function(res) {
		alert(res);
	});
}
function startRE() {
	var req = {};
	req.name = "StopRE";
	var url = getURL("StartRoutingEngine");
	postFormWithContent(url, req, function(res) {
		alert(res);
	});
}
function restartRE() {
	var req = {};
	req.name = "StopRE";
	var url = getURL("RestartRoutingEngine");
	postFormWithContent(url, req, function(res) {
		alert(res);
	});
}
function addRoutesRE() {
	var req = {};
	req.name = "StopRE";
	var url = getURL("AddRoutesToEngine");
	postFormWithContent(url, req, function(res) {
		alert(res);
	});
}

/* =================================================================================== */
/*******************************************************************************
 * 
 ******************************************************************************/
var mydrag = function() {
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, mymove = function(dx, dy) {

	var pt = this.getPointAtLength(1);
	var xamt = mouseX - pt.x;
	var yamt = mouseY - pt.y;
	if (this.tx) {
		this.tx += xamt;
	} else {
		this.tx = xamt;
	}

	if (this.ty) {
		this.ty += yamt;
	} else {
		this.ty = yamt;
	}

	this.translate(xamt, yamt);
	pCanvas.safari();
	var tr = this.attr("translation");
	var myid = this.node.id;
	var shape = findComponentByName(this.node.id);
	var pts = dojo.fromJson(shape.pts);
	for ( var j in pts) {
		globals[pts[j].id].translate(xamt, yamt);
	}
	this.toBack();
}, myup = function() {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var tr = this.attr("translation");
	var shape = findComponentByName(this.node.id);
	if (shape) {
		{

			if (shape.pts) {
				var pts = dojo.fromJson(shape.pts);
				for ( var k in pts) {
					pts[k].x += this.tx;
					pts[k].y += this.ty;
					globals[pts[k].id].attr("cx", pts[k].x);
					globals[pts[k].id].attr("cy", pts[k].y);
					globals[pts[k].id].toFront();
				}
				shape.pts = dojo.toJson(pts);
				this.tx = 0;
				this.ty = 0;
				var shapestr = "var shape=pCanvas.path(\""
						+ getPathFromPoints(shape.pts) + "\");\n";
				shapestr += "shape.attr(\"fill\",\"green\");";
				shapestr += "shape.attr(\"stroke\",\"red\");";
				shapestr += "return shape;"
				shape.design = shapestr;
			}
		}

	}
};
var started = false;
var pstr = "";
var ox = -10, oy = -10;
var s1 = null
var s2 = null;
var stream1 = null;
var stream2 = null;
var pts = {};
var eleid = "uni" + Math.ceil(Math.random() * 1000);
var i = 0;
var streamflow = false;
var imax = -1;
/* FREE FLOW*********************** */
var intype, outtype;

function drawFreeFlow(isstreamconn, create, max, convert, intypeele, outtypeele) {
	started = false;
	intype = intypeele;
	outtype = outtypeele;
	pstr = "";
	ox = -10, oy = -10;
	pts = {};
	imax = max;
	eleid = "uni" + Math.ceil(Math.random() * 1000);
	i = 0;
	s1 = null
	s2 = null;
	stream1 = null;
	stream2 = null;
	streamflow = isstreamconn;
	document.onmousemove = function(e) {
		var mx = 0, my = 0;

		if (e.pageX || e.pageY) {
			mx = e.pageX;
			my = e.pageY;
		} else if (e.clientX || e.clientY) {
			mx = e.clientX + document.body.scrollLeft
					+ document.documentElement.scrollLeft;
			my = e.clientY + document.body.scrollTop
					+ document.documentElement.scrollTop;
		}

		if (started) {

			if (ox < 0 && oy < 0) {
				ox = mx;
				oy = my - 24;
				var ptid = "" + eleid + i;
				pstr += "M " + ox + " " + oy + " ";
				pts[ptid] = {
					"x" : ox,
					"y" : oy,
					"id" : ptid
				};
				i++;
				pCanvas.circle(ox, oy, 20).attr("stroke", "green");
				var startItemPort = findnearItemObj(ox, oy, 20, "Port");
				highLightValidPorts(startItemPort);
			} else {
				var oldid = i - 1;
				var oldpt = pts["" + eleid + oldid]
				if (oldpt) {
					var d = Math.ceil(dist(oldpt.x, oldpt.y, mx, my - 24));
					if (d < 8) {
						console.log("text", "Less" + i + " " + d);
						return;
					} else {
						console.log("text", "dist" + d);
					}
				} else {
					console.log("text", "dist pt " + i);
				}
				pCanvas.line(ox, oy, mx, my - 24);
				pstr += " L " + ox + " " + oy;
				var ptid = "" + eleid + i;
				pts[ptid] = {
					"x" : ox,
					"y" : oy,
					"id" : ptid
				};
				i++;
				ox = mx;
				oy = my - 24;
			}

			if (create) {
				if (s1 == null) {
					if (stream2 != null) {
						if (distance(stream2.x, stream2.y, ox, oy) > imax) {
							stream1 = createStreamItem(getAndResetKeyed(), ox,
									oy, null, null, null, null);
							addObjectToGraph(stream1);
							s1 = stream1.id;
							var borderColor = COLOR_GREEN;
							var fillColor = COLOR_GREEN;
							drawStreamRect(stream1.x, stream1.y, 48, 48,
									fillColor, borderColor, stream1.id);
							var conn = createConnItem(s2, s1);
							lastSelectedNode = stream1;
							lastSelectedConn = conn;

							if (stream2 != null) {
								s2 = null;
								stream2 = null;
							}
						}
					} else {
						stream1 = createStreamItem(getAndResetKeyed(), ox, oy,
								null, null, null, null);
						addObjectToGraph(stream1);
						s1 = stream1.id;
						var borderColor = COLOR_GREEN;
						var fillColor = COLOR_GREEN;
						drawStreamRect(stream1.x, stream1.y, 48, 48, fillColor,
								borderColor, stream1.id);
						lastSelectedNode = stream1;
					}
				}
				if (s1 != null && s2 == null) {
					if (distance(stream1.x, stream1.y, ox, oy) > imax) {
						stream2 = createStreamItem(getAndResetKeyed(), ox, oy,
								null, null, null, null);
						addObjectToGraph(stream2);
						s2 = stream2.id;
						var borderColor = COLOR_GREEN;
						var fillColor = COLOR_GREEN;
						drawStreamRect(stream2.x, stream2.y, 48, 48, fillColor,
								borderColor, stream2.id);
						var conn = createConnItem(s1, s2);
						lastSelectedNode = stream2;
						lastSelectedConn = conn;

						s1 = null;
						stream1 = null;
					}
				}
			}
		}
	}
	document.onmousedown = function(e) {

		if (started) {
			artistMode = false;
			started = false;
			var p = pCanvas.path(pstr);

			p.node.id = eleid;
			p.attr("stroke", "red");
			// p.attr("fill","green");
			p.drag(mymove, mydrag, myup);
			p.tx = 0;
			p.ty = 0;
			p.toBack();
			ox = -10, oy = -10;
			document.onmousedown = function(e) {
			};
			document.onmousemove = captureMousePosition2;
			var shapestr = "var shape=pCanvas.path(\"" + pstr + "\");\n";
			// shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shapestr += "return shape;"

			var sh = getShape(p.node.id, shapestr);
			sh.color = currConnColor;
			sh.pts = dojo.toJson(pts);
			// construction
			console.log(pts);
			var pt1 = pts["" + eleid + "0"];
			console.log("" + eleid + ("" + i - 1));
			var pt2 = pts["" + eleid + ("" + i - 1)];
			// *********************this is where to handle the pts and shape
			// post
			console.log("Sreamflow:" + streamflow);
			if (streamflow) {

				var s1 = findnearStream(pt1.x, pt1.y);
				var s2 = findnearStream(pt2.x, pt2.y);
				console.log(s1 + "  :> " + s2);
				createConnectionDialogAtXYID(pt2.x, pt2.y, "");
				dojo.byId("fromNode").value = s1;
				dojo.byId("toNode").value = s2;
				dojo.byId("connshapeid").value = eleid;
			} else {

				if (!create) {
					if (intype == "module")
						s1 = findnearCompDiag(pt1.x, pt1.y);
					else
						s1 = findnearItem(pt1.x, pt1.y, 500, intype);

					if (outtype == "module")
						s2 = findnearCompDiag(pt2.x, pt2.y);
					else
						s2 = findnearItem(pt2.x, pt2.y, 500, outtype);

					console.log(s1 + "  :> " + s2);
					var newConnection = {};
					var cid = prompt("Enter connection Name:", getUniqId());
					newConnection["type"] = "connection";
					newConnection["nodes"] = new Array(s1, s2);
					newConnection["from"] = s1;
					newConnection["to"] = s2;
					newConnection["connCond"] = "";
					newConnection["id"] = cid;
					newConnection["ctype"] = "arbitconnection";
					newConnection["sequence"] = 0;
					newConnection["attrib"] = 0;

					if (convert == null || convert == false)
						newConnection["shape"] = sh;
					else
						newConnection["shape"] = null;

					console.log("Created the newConnection Object");
					console.log(newConnection.nodes[0] + "  "
							+ newConnection.nodes[1]);
					addObjectToGraph(newConnection);
					var evtdata = {};
					evtdata.id = newConnection.id;
					evtdata.type = "conncreated";
					sendEvent(geq, [ evtdata ]);
					// normalizeData();
					dojo.byId("connshapeid").value = -1;
					draw();
				}
			}
			// *********************END END ENDthis is where to handle the pts
			// and shape
			// post construction
			pstr = "";
			compDiag2.push(sh);
			globals["" + p.node.id] = p;
			// sumit uncomment below to draw points
			// for(var j in pts){
			// drawPoint(pts[j],eleid);
			// }

		} else {
			pstr = "";
			started = true;
			ox = -10, oy = -10;
			lastSelectedNode = null;
			lastSelectedConn = null;

			artistMode = true
		}
	}
}
function createConnObject(s1, s2, id) {
	var newConnection = {};
	newConnection["type"] = "connection";
	newConnection["nodes"] = new Array(s1, s2);
	newConnection["from"] = s1;
	newConnection["to"] = s2;
	newConnection["sequence"] = 0;
	newConnection["attrib"] = 0;

	newConnection["connCond"] = "";
	if (id == null)
		id = "Random_" + Math.ceil(Math.random() * 1000);
	newConnection["id"] = id;
	newConnection["ctype"] = "arbitconnection";
	console.log("Created the newConnection Object");
	console.log(newConnection.nodes[0] + "  " + newConnection.nodes[1]);
	dojo.byId("connshapeid").value = -1;
	return newConnection;
}
function createConnItem(s1, s2, id, donotadd) {
	var newConnection = createConnObject(s1, s2, id);
	if (donotadd != null && donoadd == true) {

	} else {
		addObjectToGraph(newConnection);
	}
	connectLinks(newConnection);
	return newConnection;
}
function drawPoint(pt, eleid) {
	var c = pCanvas.circle(pt.x, pt.y, 4, 4);
	c.node.id = pt.id;
	c.node.eleid = eleid;
	c.attr("fill", "blue");
	globals[pt.id] = c;
	c.drag(ptmove, ptdrag, ptup);
	c.mouseover(function(e) {
		this.attr("fill", "red");
	});
	c.mouseout(function(e) {
		this.attr("fill", "blue");
	});
	c.dblclick(function(e) {
		removePoint(this);
	});
	c.toFront();
	return c;
}
/*******************************************************************************
 * 
 ******************************************************************************/
var ptdrag = function() {
	this.animate({
		"fill-opacity" : .2
	}, 500);
	this.ox = this.attr("cx");
	this.oy = this.attr("cy");
}, ptmove = function(dx, dy) {
	var ox = this.ox + dx;
	var oy = this.oy + dy;
	this.attr("cx", ox);
	this.attr("cy", oy);
}, ptup = function() {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var par = findComponentByName(this.node.eleid);
	var pts = dojo.fromJson(par.pts);
	var x = this.attr("cx");
	var y = this.attr("cy");
	pts[this.node.id] = {
		"x" : x,
		"y" : y,
		"id" : this.node.id
	};
	var str = dojo.toJson(pts);
	par.pts = str;
	var pathstr = getPathFromPoints(str);
	var path = pCanvas.path(pathstr);
	path.attr("stroke", "yellow");
	// path.attr("fill","orange");
	path.tx = 0;
	path.ty = 0;
	path.toBack();
	path.node.id = this.node.eleid
	path.drag(mymove, mydrag, myup);
	var shapestr = "var shape=pCanvas.path(\"" + pathstr + "\");\n";
	// shapestr += "shape.attr(\"fill\",\"green\");";
	shapestr += "shape.attr(\"stroke\",\"red\");";
	shapestr += "return shape;"

	par.design = shapestr;
	globals[this.node.eleid].remove();
	globals[this.node.eleid] = path;
	var shape = findComponentByName(this.node.eleid);
	var pts = dojo.fromJson(shape.pts);
	for ( var k in pts) {
		globals[pts[k].id].toFront();
	}
}
/*******************************************************************************
 * 
 ******************************************************************************/
function removePoint(nd) {
	var name = nd.node.eleid;
	var ptname = nd.node.id;
	var pout = {};
	var shape = findComponentByName(name);
	if (shape) {

		if (shape.pts) {
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {
				if ([ pts[k].id ] == ptname) {
				} else {
					pout[pts[k].id] = pts[k];
				}
			}

			shape.pts = dojo.toJson(pout);
			var shapestr = "var shape=pCanvas.path(\""
					+ getPathFromPoints(shape.pts) + "\");\n";
			// shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shapestr += "return shape;"

			shape.design = shapestr;

			var pathstr = getPathFromPoints(shape.pts);
			var path = pCanvas.path(pathstr);
			path.attr("stroke", "pink");
			// path.attr("fill","silver");
			path.tx = 0;
			path.ty = 0;
			path.toBack();
			globals[name].remove();
			globals[name] = path;
			path.node.id = name;
			path.drag(mymove, mydrag, myup);
			globals[ptname].remove();
			globals[ptname] = null;
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {
				globals[pts[k].id].toFront();
			}
		}
	}
}
/*******************************************************************************
 * function txShape(name,x,y)
 ******************************************************************************/
function txShape(name, x, y) {
	var shape = findComponentByName(name);
	if (shape) {

		if (shape.pts) {
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {

				pts[k].x += x;
				pts[k].y += y;
				globals[pts[k].id].attr("cx", pts[k].x);
				globals[pts[k].id].attr("cy", pts[k].y);
				globals[pts[k].id].toFront();

			}
			shape.pts = dojo.toJson(pts);
			var shapestr = "var shape=pCanvas.path(\""
					+ getPathFromPoints(shape.pts) + "\");\n";
			// shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shapestr += "return shape;"

			shape.design = shapestr;
			var path = pCanvas.path(getPathFromPoints(shape.pts));
			path.attr("stroke", "yellow");
			// path.attr("fill","orange");
			path.node.id = name;
			path.tx = 0;
			path.ty = 0;
			path.toBack();
			path.drag(mymove, mydrag, myup);
			globals[name].remove();
			globals[name] = path;
		}
	}
}

/*******************************************************************************
 * create path from points
 ******************************************************************************/
function getPathFromPoints(ptsstr) {
	var pstr = "";
	var pts = dojo.fromJson(ptsstr);
	return getPathFromPointsArray(pts);
}
function getPathFromPointsArray(pts) {
	var pstr = "";
	var started = false;
	if(pts.length==null||pts.length==undefined){
		var props = Object.keys(pts);
		for (var i = 0; i < props.length; i++) {
			var key = props[i]
			var pt = pts[key];
			var ox = pt.x;
			var oy = pt.y;
			if (!started) {
				pstr += "M " + ox + " " + oy + " ";
				started = true;
			} else {
				pstr += " L " + ox + " " + oy;
			}
		}	
	}else{
	for (var i = 0; i < pts.length; i++) {
		var pt = pts[i];
		var ox = pt.x;
		var oy = pt.y;
		if (!started) {
			pstr += "M " + ox + " " + oy + " ";
			started = true;
		} else {
			pstr += " L " + ox + " " + oy;
		}
	}
	}
	return pstr;
}
/*******************************************************************************
 * 
 ******************************************************************************/

function txShape(name, x, y) {
	var shape = findComponentByName(name);
	if (shape) {

		if (shape.pts) {
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {

				pts[k].x += x;
				pts[k].y += y;
				globals[pts[k].id].attr("cx", pts[k].x);
				globals[pts[k].id].attr("cy", pts[k].y);
				globals[pts[k].id].toFront();

			}
			shape.pts = dojo.toJson(pts);
			var shapestr = "var shape=pCanvas.path(\""
					+ getPathFromPoints(shape.pts) + "\");\n";
			// shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shapestr += "return shape;"

			shape.design = shapestr;
			var path = pCanvas.path(getPathFromPoints(shape.pts));
			path.attr("stroke", "yellow");
			// path.attr("fill","orange");
			path.node.id = name;
			path.tx = 0;
			path.ty = 0;
			path.drag(mymove, mydrag, myup);
			globals[name].remove();
			globals[name] = path;
		}
	}
}
/*******************************************************************************
 * 
 ******************************************************************************/
function removePoint(nd) {
	var name = nd.node.eleid;
	var ptname = nd.node.id;
	var pout = {};
	var shape = findComponentByName(name);
	if (shape) {

		if (shape.pts) {
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {
				if ([ pts[k].id ] == ptname) {
				} else {
					pout[pts[k].id] = pts[k];
				}
			}

			shape.pts = dojo.toJson(pout);
			var shapestr = "var shape=pCanvas.path(\""
					+ getPathFromPoints(shape.pts) + "\");\n";
			// shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shapestr += "return shape;"

			shape.design = shapestr;
			var pathstr = getPathFromPoints(shape.pts);
			var path = pCanvas.path(pathstr);
			path.attr("stroke", "yellow");
			// path.attr("fill","orange");
			path.tx = 0;
			path.ty = 0;
			path.toBack();
			globals[name].remove();
			globals[name] = path;
			path.node.id = name;
			path.drag(mymove, mydrag, myup);
			globals[ptname].remove();
			globals[ptname] = null;
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {
				globals[pts[k].id].toFront();
			}
		}
	}
}
/*******************************************************************************
 * 
 ******************************************************************************/
function dist(x1, y1, x2, y2) {
	return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}
/*******************************************************************************
 * findComponentByName
 * 
 * @param str
 * @returns
 */
function findComponentByName2(str) {
	for (var i = 0; i < compDiag2.length; i++) {
		console.log("Cd: " + compDiag2[i].id)
		if (compDiag2[i].id == str) {
			return compDiag2[i];
		}
	}
	return findConnForShape(str);
}

function findComponentByName(str) {
	for (var i = 0; i < compDiag.length; i++) {
		console.log("Cd: " + compDiag[i].id)
		if (compDiag[i].id == str) {
			return compDiag[i];
		}
	}
	return findConnForShape(str);
}

//defunct
function getShapeOld(id, design) {
	return {
		"id" : id,
		"name" : "Shape",
		"design" : design
	};
}

function drawConnOnClicks() {
	var clickcount = 1;
	document.onclick = function(evt) {
		clickcount++;
		if (clickcount == 2) {
			x1 = mouseX;
			y1 = mouseY - 24;
			p1 = pCanvas.circle(x1, y1, 5, 5);
			nd1 = findnearStream(x1, y1);
		}
		if (clickcount == 3) {
			x2 = mouseX;
			y2 = mouseY - 24;
			document.onclick = function(evt) {
			}
			var p2 = pCanvas.arrow(x1, y1, x2, y2, 8);
			nd2 = findnearStream(x2, y2);

			// console.log("ND1: "+nd1);
			// console.log("ND2: "+nd2);
			createConnectionDialogAtXYID(x2, y2, "");
			dojo.byId("fromNode").value = nd1;
			dojo.byId("toNode").value = nd2;
			p1.remove();
			p2[0].remove();
			p2[1].remove();

		}

	}
}
function drawArbitConnOnClicks() {
	var clickcount = 1;
	var nd1, nd2;
	document.onclick = function(evt) {
		clickcount++;
		if (clickcount == 2) {
			x1 = mouseX;
			y1 = mouseY - 24;
			p1 = pCanvas.circle(x1, y1, 5, 5);
			nd1 = findnearItem(x1, y1, 100);
		}
		if (clickcount == 3) {
			x2 = mouseX;
			y2 = mouseY - 24;
			document.onclick = function(evt) {
			}
			var p2 = pCanvas.arrow(x1, y1, x2, y2, 8);
			nd2 = findnearItem(x2, y2, 100);
			var id = prompt("Please enter conn name: ", getUniqId());
			var conn = createConnItem(nd1, nd2, id);
			lastSelectedNode = nd1;
			lastSelectedConn = conn;

			p1.remove();
			p2[0].remove();
			p2[1].remove();

		}

	}
}

function moveGraphOnClick(selective) {
	var clickcount = 1;
	document.onclick = function(evt) {
		clickcount++;
		if (clickcount == 2) {
			x1 = mouseX;
			y1 = mouseY - 24;
			p1 = pCanvas.circle(x1, y1, 5, 5);
			document.onmousemove = function(e) {
				var mx = 0, my = 0;
				if (e.pageX || e.pageY) {
					mx = e.pageX;
					my = e.pageY;
				} else if (e.clientX || e.clientY) {
					mx = e.clientX + document.body.scrollLeft
							+ document.documentElement.scrollLeft;
					my = e.clientY + document.body.scrollTop
							+ document.documentElement.scrollTop;
				}
				var dx = mx - x1;
				var dy = my - y1;
				if (dx > 10 || dy > 10 || dx < -10 || dy < -10) {
					x1 = mx;
					y1 = my;
					var stepSize = wideconfig.stepMoverSize;
					dx = parseInt(dx / stepSize) * stepSize;
					dy = parseInt(dy / stepSize) * stepSize;
					translateGraph(dx, dy, mx, my, selective);
				}

			}

		}
		if (clickcount == 3) {
			document.onmousemove = captureMousePosition2;
			document.onclick = function(evt) {
			}
			p1.remove();
		}
	}
}

function translateGraph(dx, dy, mx, my, sel) {
	// now translate
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (layeringEnabled && !layerEnabled(obj.layer)) {
			continue;
		}

		var xpos = obj.normalizedx;
		var ypos = obj.normalizedy;
		var x = obj.x;
		var y = obj.y;
		console.log("mx: " + mx + "my : " + my + "o.x : " + obj.x + " obj.y: "
				+ obj.y)
		if (sel) {
			if ((obj.normalizedx > mx) && (obj.normalizedy > my)) {
				obj.x = parseInt(x) + dx;
				obj.y = parseInt(y) + dy;
				obj.normalizedx = xpos + dx;
				obj.normalizedy = ypos + dy;
			}
		} else {
			obj.x = parseInt(x) + dx;
			obj.y = parseInt(y) + dy;
			obj.normalizedx = xpos + dx;
			obj.normalizedy = ypos + dy;
		}
	}
	draw();
}

/** ************************* FREE FLOW*********************** */
function freeHandRegion(func, resfunc) {
	started = false;
	pstr = "";
	ox = -10, oy = -10;
	pts = {};
	eleid = "uni" + Math.ceil(Math.random() * 1000);
	i = 0;
	document.onmousemove = function(e) {
		var mx = 0, my = 0;

		if (e.pageX || e.pageY) {
			mx = e.pageX;
			my = e.pageY;
		} else if (e.clientX || e.clientY) {
			mx = e.clientX + document.body.scrollLeft
					+ document.documentElement.scrollLeft;
			my = e.clientY + document.body.scrollTop
					+ document.documentElement.scrollTop;
		}

		if (started) {
			if (ox < 0 && oy < 0) {
				ox = mx;
				oy = my - 24;
				var ptid = "" + eleid + i;
				pstr += "M " + ox + " " + oy + " ";
				pts[ptid] = {
					"x" : ox,
					"y" : oy,
					"id" : ptid
				};
				i++;
			} else {
				var oldid = i - 1;
				var oldpt = pts["" + eleid + oldid]
				if (oldpt) {
					var d = Math.ceil(dist(oldpt.x, oldpt.y, mx, my - 24));
					if (d < 8) {
						// console.log("text","Less"+i +" "+d);
						return;
					} else {
						// console.log("text","dist"+d);
					}
				} else {
					// console.log("text","dist pt "+i);
				}
				pCanvas.line(ox, oy, mx, my - 24);
				pstr += " L " + ox + " " + oy;
				var ptid = "" + eleid + i;
				pts[ptid] = {
					"x" : ox,
					"y" : oy,
					"id" : ptid
				};
				i++;
				ox = mx;
				oy = my - 24;
			}
		}
	}
	document.onmousedown = function(e) {

		if (started) {
			started = false;
			var p = pCanvas.path(pstr);

			p.node.id = eleid;
			p.attr("stroke", "red");
			// p.attr("fill","green");
			p.drag(mymove, mydrag, myup);
			p.tx = 0;
			p.ty = 0;
			p.toBack();
			ox = -10, oy = -10;
			document.onmousedown = function(e) {
			};
			document.onmousemove = captureMousePosition2;
			var shapestr = "var shape=pCanvas.path(\"" + pstr + "\");\n";
			// shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shapestr += "return shape;"

			var sh = getShape(p.node.id, shapestr);

			sh.pts = dojo.toJson(pts);
			if (func != null) {
				lastSelectedPts = pts;
				func(pts, resfunc);
			}
			pstr = "";
			compDiag2.push(sh);
			globals["" + p.node.id] = p;
			//
			// for(var j in pts){
			// drawPoint(pts[j],eleid);
			// }

		} else {
			pstr = "";
			started = true;
			ox = -10, oy = -10;

		}
	}
}
function finditemsinintersection(pts, fun) {
	var p = new Array();
	for ( var i in pts) {
		p.push(pts[i]);
	}

	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.region = dojo.toJson(p);
	var url = getURL("FindItemsViaIntersection");
	postFormWithContent(url, req, fun);
}
function finditemsinregion(pts, fun) {
	var p = new Array();
	for ( var i in pts) {
		p.push(pts[i]);
	}
	var itemsIn = new Array();
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.x != null && obj.y != null && isPointInPoly(p, obj)) {
			console.log(obj.id + " is in region.");
			itemsIn.push(obj.id);
		} else {
			console.log(obj.id + " is not in region.");
		}
	}
	fun(dojo.toJson(itemsIn));
}
function findBoundingRect(pts, fun) {
	var p = new Array();
	for ( var i in pts) {
		p.push(pts[i]);
	}

	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	req.region = dojo.toJson(p);
	var url = getURL("FindBoundryRect");
	postFormWithContent(url, req, fun);
}
function createBoundRect(res) {
	var a = dojo.fromJson(res);
	pCanvas.rect(a.x - 48, a.y - 48, a.w + 96, a.h + 96, 10);
}
function createGroupProcNode(res, nd) {
	var a = dojo.fromJson(res);
	var b = new Array();
	for ( var nd in a) {
		var node = findNodeById(a[nd]);
		if (node != null && node.type == "stream") {
			b.push(a[nd]);
		}
	}
	a = b;// swap
	var str = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
	}
	alert(str);
	createNewNodeDialog(300, 300, 40, 40);
	for (var j = 0; j < 100; j++) {
		if (nd == null) {
			nd = findNodeById("RandomGroupNode" + j);
		}
		if (nd == null)
			break;
	}
	dojo.byId("nodeId").value = "RandomGroupNode" + j;
	dojo.byId("processclz").value = "org.ptg.processors.FastProcessor";
	dojo.byId("processquery").value = str;

}

function createGroup(res) {
	var a = dojo.fromJson(res);
	var str = "";
	var newnd = {};
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == "connection") {
			if (a.contains(obj.from) && a.contains(obj.to)) {
				a.push(obj.id);
			}
		}
	}
	for (var j = 0; j < 100; j++) {
		var nd = findNodeById("RandomGroupNode" + j);
		if (nd == null)
			break;
	}
	newnd.id = prompt('Enter Id', "RandomGroupNode" + j);
	if (newnd.id == null || newnd.id == "") {
		draw();
		return;
	}
	newnd.type = "group";
	newnd.gtype = "generic";
	newnd.normalizedx = 300;
	newnd.normalizedy = 300;
	newnd.x = 300;
	newnd.y = 300;
	newnd.r = 48;
	newnd.b = 48;
	newnd.items = a;
	newnd.closed = true;
	newnd.icon = "/images/folder.png";
	var shape = {};
	shape.pts = dojo.toJson(lastSelectedPts);
	var shapestr = "var shape=pCanvas.path(\""
			+ getPathFromPointsArray(lastSelectedPts) + "\");\n";
	shapestr += "shape.attr(\"fill\",\"green\");";
	shapestr += "shape.attr(\"stroke\",\"red\");";
	shapestr += "shape.toBack();";
	shapestr += "return shape;"
	shape.design = shapestr;
	newnd.shape = shape;
	addObjectToGraph(newnd);
	draw();
	var evtdata = {};
	evtdata.id = newnd.id;
	evtdata.type = "grouped";
	sendEvent(geq, [ evtdata ]);

}

function yarrange(res) {
	var a = dojo.fromJson(res);
	var str = "";
	var nds = new Array();
	var ytotal = 0;
	var n = 0;
	for (var i = 0; i < a.length; i++) {
		var nd = (findNodeById(a[i]));
		if (nd != null) {
			nds.push(nd);
			ytotal += nd.normalizedy;
			n += 1;
		}
	}
	var avgy = ytotal / n;
	for (var i = 0; i < nds.length; i++) {
		nds[i].normalizedy = avgy;
		nds[i].y = avgy;
	}
	draw();

}
function xarrange(res) {
	var a = dojo.fromJson(res);
	var str = "";
	var nds = new Array();
	var xtotal = 0;
	var n = 0;
	for (var i = 0; i < a.length; i++) {
		var nd = (findNodeById(a[i]));
		if (nd != null) {
			nds.push(nd);
			xtotal += nd.normalizedx;
			n += 1;
		}
	}
	var avgx = xtotal / n;
	for (var i = 0; i < nds.length; i++) {
		nds[i].normalizedx = avgx;
		nds[i].x = avgx;
	}
	draw();

}
function createSelection(res) {
	var a = dojo.fromJson(res);
	var str = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
	}
	cpBuffer = new Array();
	for (var i = 0; i < a.length; i++) {
		cpBuffer.push(findNodeById(a[i]));
	}
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == "connection") {
			if (a.contains(obj.from) && a.contains(obj.to)) {
				cpBuffer.push(obj);
			}
		}

	}
	var evtdata = {};
	evtdata.id = Math.ceil(Math.random() * 10000);
	evtdata.type = "selection";
	evtdata.sel = a;
	sendEvent(geq, [ evtdata ]);
}
function createPatternObj(id, vx, vy, pattern) {
	var newNode = {};
	newNode["type"] = "Pattern";
	newNode["id"] = id;
	newNode["x"] = vx;
	newNode["normalizedx"] = vx;
	newNode["y"] = vy;
	newNode["normalizedy"] = vy;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["name"] = id
	newNode["icon"] = null;
	newNode["pattern"] = pattern;
	console.log("created pattern: " + newNode.id + " , " + newNode["pattern"]);
	return newNode
}
function createPattern(res) {
	var obj = createPatternObj(getUniqId(), 100, 100, res);
	addObjectToGraph(obj);
	annotateComp(res);
}

function annotateComp(res) {
	console.log("anotatecomp: " + res);
	var a = dojo.fromJson(res);

	cpBuffer = new Array();
	for (var i = 0; i < a.length; i++) {
		var nd = findNodeById(a[i]);
		cpBuffer.push(nd);
		var delem = findDrawEleById(a[i]);
		var bb = null;
		if (delem != null) {
			var m = null;
			m = delem.item;
			bb = m.getBBox();
		} else {
			bb = {};
			bb.x = nd.x - nd.r / 2;
			bb.y = nd.y - nd.b / 2;
		}
		var c = pCanvas.text(bb.x, bb.y, "" + i);
		c.attr("text-anchor", "start");
		c.attr({
			font : '18px Fontin-Sans, Arial'
		});
		var cc = pCanvas.circle(bb.x, bb.y, 20);
	}

	/*
	 * sumit : this makes no sense 25th nov 2012 for ( var i = 0; i <
	 * pData.data.length; i++) { var obj = pData.data[i]; if (obj.type ==
	 * "connection") { if (a.contains(obj.from) && a.contains(obj.to)) {
	 * cpBuffer.push(obj); } } }
	 */var last = null;
	for (var i = 0; i < cpBuffer.length; i++) {
		var itm = cpBuffer[i];
		if (last != null) {
			var conn = createConnObject(last.id, itm.id, getUniqId());
			addObjectToGraph(conn);
		}
		last = itm;
	}
	var evtdata = {};
	evtdata.id = Math.ceil(Math.random() * 10000);
	evtdata.type = "selection";
	evtdata.sel = a;
	sendEvent(geq, [ evtdata ]);
	setTimeout(function() {
		draw()
	}, 500);
}

function createSelectGroup(res) {
	var a = dojo.fromJson(res);
	var str = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
	}
	cpBuffer = new Array();
	for (var i = 0; i < a.length; i++) {
		cpBuffer.push(findNodeById(a[i]));
	}
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == "connection") {
			if (a.contains(obj.from) && a.contains(obj.to)) {
				cpBuffer.push(obj);
			}
		}

	}
	var guid = prompt("Pleaes enter name", getUniqId());
	var arr = new Array();
	for (var i = 0; i < cpBuffer.length; i++) {
		arr.push(cpBuffer[i].id);
	}
	var gnode = getNewGroupNode(guid, arr);
	pData.data.push(gnode);
	var evtdata = {};
	evtdata.id = Math.ceil(Math.random() * 10000);
	evtdata.type = "creategroup";
	evtdata.sel = a;
	sendEvent(geq, [ evtdata ]);
}
function setModWeight(res) {
	var a = dojo.fromJson(res);
	var str = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
	}
	var weight = prompt("Please enter mod weight", 1);
	cpBuffer = new Array();
	for (var i = 0; i < a.length; i++) {
		var nd = findNodeById(a[i]);
		if (nd != null) {
			if (nd.type == "module") {
				nd.weight = weight;
				cpBuffer.push(nd);

			}
		}
	}
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == "connection") {
			if (a.contains(obj.from) && a.contains(obj.to)) {
				cpBuffer.push(obj);
			}
		}

	}
	var evtdata = {};
	evtdata.id = Math.ceil(Math.random() * 10000);
	evtdata.type = "setWeight";
	evtdata.sel = a;
	evtdata.weight = weight;
	sendEvent(geq, [ evtdata ]);
}
function createEventFromTypeSel(res) {
	var a = dojo.fromJson(res);
	var event = {};
	var name = prompt("Event name", getUniqId());
	event.id = name
	event.props = {};
	for (var i = 0; i < a.length; i++) {
		var nd = findNodeById(a[i]);
		if (nd.type == "TypeDef") {
			for ( var k in nd.inputs) {
				if (typeof nd.inputs[k] != "function") {
					var prop = {};
					prop.searchable = 1;
					prop.name = nd.inputs[k].replace(/ /g, "_");
					prop.index = k;
					prop.type = nd.dtypes[k];
					if (prop.type.indexOf("/") >= 0) {
						prop.type = prop.type.substring(0, prop.type
								.indexOf("/"));
					}
					event.props[prop.index] = prop;
				}
			}
			event.eventStore = "";
			event.type = "event";
			event.x = mouseX;
			event.y = mouseY;
			event.normalizedx = mouseX;
			event.normalizedy = mouseY;
			event.r = 48;
			event.b = 48;

			addObjectToGraph(event);
			draw();
		}
	}

}

function testSelection(res) {
	var a = dojo.fromJson(res);
	var graph = new Array();
	var str = "";
	var filePath = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
		var nd = findNodeById(a[i]);
		if (nd.type == "TypeDef" || nd.type == "Port") {
			graph.push(nd);
			if (nd.type == "TypeDef")
				filePath = nd.extra;
		}
	}
	var req = {};
	req.url = "/TestCompileTypeMapping";
	req.requesttype = "infos";
	req.name = "DummyMapTest";
	req.process = dojo.toJson(graph);
	req.filePath = filePath;
	postFormWithContent(req.url, req, function(res) {
		var result = dojo.fromJson(res);
		console.log(result);
		var colm = {};
		var rows = 0;
		for ( var i in result) {
			for ( var j in result[i]) {
				if (result[i][j] == true) {
					var m = colm[j];
					if (m == null) {
						m = 0;
					}
					colm[j] = (++m);
				}
			}
			rows++;
		}
		alert("Total Rows: " + (rows - 1) + ",Match Columns  :"
				+ dojo.toJson(colm));
	});
	var evtdata = {};
	evtdata.id = Math.ceil(Math.random() * 10000);
	evtdata.type = "selection";
	evtdata.sel = a;
	sendEvent(geq, [ evtdata ]);
}
function deleteSelection(res) {
	var a = dojo.fromJson(res);
	var str = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
	}
	for (var i = 0; i < a.length; i++) {
		removeNodeById(a[i]);
	}
	var evtdata = {};
	evtdata.id = Math.ceil(Math.random() * 10000);
	evtdata.type = "selectiondeleted";
	evtdata.sel = a;
	sendEvent(geq, [ evtdata ]);
	draw();
}

function setGroupProcessor(res) {
	setGroupProperty(res, "processor");
}
function setGroupExtra(res) {
	setGroupProperty(res, "extra");
}
function convertGroup(res) {
	setGroupProperty(res, "type");
}
function setGroupProperty(res, prop) {
	if (prop == null)
		prop = prompt("Please enter prop to set: ", "extra");
	var a = dojo.fromJson(res);
	var text = pCanvas.text(300, 300, "" + a.length);
	text.attr("font-size", 64);
	text.attr("opacity", .5);
	for (var i = 0; i < a.length; i++) {
		text.attr("text", a.length - i);
		var nd = findNodeById(a[i]);

		var ret = myrect(nd.x, nd.y, 60, 60, "green", "green", "Enter Text")
		ret.item.attr({
			"fill" : "red",
			"stroke" : "red",
			"opacity" : .1,
			"fill-opacity" : 0,
			"stroke-width" : 2,
			cursor : "move"
		});

		var val = prompt(prop + ": ", "");
		if (nd != null)
			nd[prop] = val;
		ret.textnode.attr("text", getConnectionLabel(nd[prop]));
		ret.item.toBack();
		drawElements.push(ret);
	}
	text.remove();
}
function magicSpell1(res) {
	var a = dojo.fromJson(res);
	var tosend = new Array();
	for (var i = 0; i < a.length; i++) {
		var nd = findNodeById(a[i]);
		if (nd != null) {
			tosend.push(nd);
			var ar = findConnConnecting(nd.id);
			if (ar != null) {
				for ( var v in ar) {
					tosend.push(ar[v]);
				}
			}
		}

	}
	var req = {};
	req.name = currentGraph;
	req.process = dojo.toJson(tosend);
	var url = getURL("MagicSpell1");
	postFormWithContent(url, req, function(res) {
		var clonedids = {};
		var clitems = {};
		alert(res);
		var a = dojo.fromJson(res);
		for ( var i in a) {
			console.log(dojo.toJson(a[i]));
			rename(a[i].name, a[i].newname);
			clonedids[a[i].name] = a[i].newname;
			var snode = findNodeById(a[i].newname);
			clitems[a[i].newname] = snode;
			var pid = a[i].proc.name;// getUniqId();
			var pcode = a[i].proc;
			var pnode = createNodeObj(pid, snode.x + 50, snode.y + 50,
					pcode.clz, pcode.query);
			snode.processor = pid;
			snode.extra = null;
			addObjectToGraph(pnode);
		}
		reapplyCloneIdsAll(clonedids)

		draw();
	});

}
function reapplyCloneIdsAll(cloneids) {
	var b = {};
	for ( var j in pData.data) {
		var a = pData.data[j];
		reapplyCloneIdsObj(cloneids, a)
	}
}
function reapplyCloneIdsObj(cloneids, a) {
	if (a != null) {
		for ( var i in a) {
			if (i == "id")
				continue;
			if (typeof (a[i]) == "object") {
				reapplyCloneIdsObj(cloneids, a[i])
			} else {
				var clid = cloneids[a[i]];
				if (clid != null) {
					a[i] = clid;
				}
			}

		}
	}
}
// cloneids : map of oldid:newid
// clitems: mapof newid:newidobj
function reapplyCloneIds(clitems, cloneids) {
	var b = {};
	for ( var j in clitems) {
		var a = clitems[j];
		if (a != null) {
			for ( var i in a) {
				if (i == "id")
					continue;
				if (typeof (a[i]) == "object") {
					// to do later
				} else {
					var clid = cloneids[a[i]];
					if (clid != null) {
						a[i] = clid;
					}
				}

			}
		}
	}
}
function cloneSelection(res) {
	var clonedids = {};
	var clitems = {};
	var a = dojo.fromJson(res);

	for ( var item in a) {
		var node = findNodeById(a[item]);
		if (node != null) {
			var nd2 = cloneItem(node);
			nd2.id = getUniqId();
			addObjectToGraph(nd2);
			clonedids[node.id] = nd2.id;
			clitems[nd2.id] = nd2;
		}
	}
	reapplyCloneIds(clitems, clonedids);
	var evtdata = {};
	evtdata.id = Math.ceil(Math.random() * 10000);
	evtdata.type = "cloneselection";
	evtdata.sel = a;
	sendEvent(geq, [ evtdata ]);
	var ar = new Array();
	for ( var n in clitems) {
		ar.push(clitems[n].id);
	}
	// createGroup(dojo.toJson(ar));
}
function compileSelected(res) {
	var a = dojo.fromJson(res);
	alert("Going to compile: " + a);
	if (a != null || a != "") {
		CompileProcess(a);
		draw();
	}

}
function rename(id, newid) {

	var newnode = removeNodeById(id);
	if (newnode != null) {
		if (newnode.id == id) {
			newnode.id = newid;
		}
		if (newnode.name == id) {
			newnode.name = newid;
		}
		var clids = {};
		clids[id] = newid;
		reapplyCloneIdsAll(clids);
		addObjectToGraph(newNode);

	}

}
function renameSelected(res) {
	var a = dojo.fromJson(res);
	if (a != null || a != "") {
		var newname = prompt("Please enter a name:", a[0]);
		if (newname != a[0] || newname != null || newname != "") {
			rename(a[0], newname);
		}

	}

}

function getNewGroupNode(name, itemsarray) {
	var newnd = {};
	if (name == null) {
		for (var j = 0; j < 100; j++) {
			var nd = findNodeById("RandomGroupNode" + j);
			if (nd == null) {
				name = "RandomGroupNode" + j;
				break;
			}
		}
	}
	newnd.id = name;
	newnd.type = "group";
	newnd.normalizedx = 300;
	newnd.normalizedy = 300;
	newnd.x = 300;
	newnd.y = 300;
	newnd.r = 48;
	newnd.b = 48;
	newnd.icon = "/images/folder.png";
	newnd.items = itemsarray;
	newnd.closed = true;
	return newnd;

}
function renameGroup() {
	var a = lastSelectedNode;
	for (var j = 0; j < 100; j++) {
		var nd = findNodeById("RandomGroupNode" + j);
		if (nd == null)
			break;
	}
	var nid = prompt('Enter new Id', "RandomGroupNode" + j);
	if (nid == null || nid == "") {
		return;
	}
	a.id = nid;
	draw();
}

function openGroup() {
	var a = lastSelectedNode;

	if (a != null && a.type == "group") {
		a.closed = false;
		var evtdata = {};
		evtdata.id = a.id;
		evtdata.type = "ungrouped";
		sendEvent(geq, [ evtdata ]);
	}

	draw();
}

function openGroups() {
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'group') {
			obj.closed = false;
			var evtdata = {};
			evtdata.id = obj.id;
			evtdata.type = "ungrouped";
			sendEvent(geq, [ evtdata ]);

		}
	}
	draw();
}
function deleteGroup() {
	var a = lastSelectedNode;
	if (a.type == "group")
		removeNodeById(a.id);
	draw();
}

function closeGroups() {
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'group') {
			obj.closed = true;
		}
	}
	draw();
}
function setConnColor() {
	var nid = prompt('Enter color value', "red");
	currConnColor = nid;
}
function findConnForShape(id) {
	var nodeRequired = null;
	for (var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'connection') {
			var obj = pData.data[i];
			if (obj.shape != null) {
				if (obj.shape.id == id)
					return obj.shape;
			}
		}
	}
	return nodeRequired;
}
function setConnShape(conn, shape) {
	var objconn = findConnForShape(shape);
	if (objconn != null) {
		objconn.shape = shape;
		draw();
	}
}
function importExistingGraph(gtype) {
	var loc = dojo.byId('chooseimportgraphid');
	loc.innerHTML = "";
	if (gtype == null)
		gtype = "graph";
	dojo.xhrGet({
		url : getURL("GETGRAPHS") + "?graphtype=" + gtype,
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.name
				}, loc);
			});
			var importdlg = dijit.byId('importGraphDialog');
			importdlg.show();
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			addInfoToBox("Failed to retrive graphs" + a.result);
			return response;
		}
	});
}
function importGraphFromServer() {
	var sel = dojo.byId("chooseimportgraphid");
	var sidx = sel.selectedIndex;
	var graphname = sel.options[sidx].text;
	importGraphFromServerByName(graphname);
}
function importGraphFromServerByName(graphname) {
	dojo.xhrGet({
		url : getURL("GETGRAPH") + "?graphid=" + graphname,
		load : function(response, ioArgs) {
			var obj = dojo.fromJson(response)

			importGraph(obj, graphname);
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			addInfoToBox("Failed to Load Graph" + a.result);
			return response;
		}
	});
}
function importGraph(dataobj, gname) {
	var g = dojo.fromJson(dataobj[0].graph).data;
	_importGraph(g, gname);
}
function _importGraph(g, gname) {
	var ar = new Array();
	for (var i = 0; i < g.length; i++) {
		var obj = g[i];
		addObjectToGraph(obj);
		ar.push(obj.id);
	}
	var grpnode = getNewGroupNode(gname, ar);
	addObjectToGraph(grpnode);
	draw();
}

// //////////////////////////
function hideCMenu() {
	if (cmenu != null) {
		for ( var mi in cmenu) {
			var tempmenu = cmenu[mi];
			if (tempmenu.lbl != null)
				tempmenu.lbl.remove();
			if (tempmenu.path != null)
				tempmenu.path.remove();
		}
	}
	hideCMenuBall();
	cmenu = new Array();
}
function hideCMenuBall() {
	if (cmenuBall != null) {
		for ( var i in cmenuBall) {
			cmenuBall.remove();
		}
	}
	cmenuBall = null;
}

function showCMenu(sx, sy, lbl, hint) {
	if (cmenu == null) {
		cmenu = new Array();
	} else {
		hideCMenu();
	}
	var cnt = lbl.length;
	var d = 2 * Math.PI / cnt
	var angle = 0;
	var distance = 20;

	for (var i = 0; i < cnt; i++) {
		var menuitem = {};
		var y1 = Math.round(sy + distance * Math.sin(angle));
		var x1 = Math.round(sx + distance * Math.cos(angle));
		var y3 = Math.round(sy + distance * 5 * Math.sin(angle));
		var x3 = Math.round(sx + distance * 5 * Math.cos(angle));
		var y2 = Math.round(sy + distance * 6 * Math.sin(angle + d / 2));
		var x2 = Math.round(sx + distance * 6 * Math.cos(angle + d / 2));

		var y7 = Math.round(sy + distance * 4.5 * Math.sin(angle + d / 2));
		var x7 = Math.round(sx + distance * 4.5 * Math.cos(angle + d / 2));

		angle += d;
		var y4 = Math.round(sy + distance * Math.sin(angle));
		var x4 = Math.round(sx + distance * Math.cos(angle));
		var y6 = Math.round(sy + distance * 5 * Math.sin(angle));
		var x6 = Math.round(sx + distance * 5 * Math.cos(angle));
		var y5 = Math.round(sy + distance * Math.sin(angle - d / 2));
		var x5 = Math.round(sx + distance * Math.cos(angle - d / 2));

		var pth = pCanvas.path("M " + x1 + " " + y1 + " L" + x3 + " " + y3
				+ " Q " + x2 + " " + y2 + " " + x6 + " " + y6 + " L" + x4 + " "
				+ y4 + " Q " + x5 + " " + y5 + " " + x5 + " " + y5 + " Z");
		menuitem.path = pth;
		pth.attr("stroke", "#6699FF")
		pth.attr("fill", "#6699FF")
		pth.attr("fill-opacity", ".4")
		var ax = Math.random() * 300 * Math.random() * 10;
		var ay = Math.random() * 300 * Math.random() * 10;
		pth.translate(-ax, -ay);

		pth.animate({
			"translation" : ax + "," + ay
		}, 200, "bounce");
		pth.toFront();
		var lb = lbl.shift();
		var hnt = hint.shift();
		pth.node.lbl = lb;
		pth.node.hint = hnt;

		pth.mouseout(function(event) {
			this.attr({
				"fill-opacity" : ".4"
			});
		});
		pth.mouseover(function(event) {
			this.attr({
				"fill-opacity" : ".1"
			});
			var lbt = this.node.lbl;
			var hnt = this.node.hint;
			var sx = mouseX;
			var sy = mouseY;
			console.log(lbt);
		});
		pth.click(function(event) {
			var lbt = this.node.lbl;
			var hnt = this.node.hint;
			var evtdata = {};
			evtdata.label = lbt;
			evtdata.hint = hnt;
			sendEvent(ceq, [ evtdata ]);
		});

		pth = pCanvas.text(x7, y7, lb);
		ax = Math.random() * 300 * Math.random() * 10;
		ay = Math.random() * 300 * Math.random() * 10;
		pth.translate(-ax, -ay);
		pth.animate({
			"translation" : ax + "," + ay
		}, 200, "bounce");

		pth.toFront();
		menuitem.lbl = pth;
		pth.node.lbl = lb;
		pth.node.hint = hnt;
		pth.click(function(event) {
			var lbt = this.node.lbl;
			var hnt = this.node.hint;
			var evtdata = {};
			evtdata.label = lbt;
			evtdata.hint = hnt;
			sendEvent(ceq, [ evtdata ]);
		});
		cmenu.push(menuitem);
	}
	cmenuBall = pCanvas.ball(sx, sy, 20, 60);
	ax = Math.random() * 300 * Math.random() * 10;
	ay = Math.random() * 300 * Math.random() * 10;
	cmenuBall.translate(-ax, -ay);
	cmenuBall.animate({
		"translation" : ax + "," + ay
	}, 200, "bounce");
	cmenuBall.toFront();
	cmenuBall.click(function(event) {
		hideCMenu();

	});

}
function registerMenu(str, func) {
	menuitems[str] = func;
}
function mycustommenu(lbl, hint) {
	// pCanvas.clear();
	var syy = mouseY;
	var sxx = mouseX;
	if (lbl == null) {
		lbl = new Array();
		lbl.push("Connect Ports");
		lbl.push("Connect Words");
		lbl.push("Create Connection");
		lbl.push("Create Stream");
		lbl.push("Create Event");
		lbl.push("Create Node");
		lbl.push("Advanced Menu");
		lbl.push("Redraw");
		lbl.push("Intersector");
	}

	if (hint == null) {
		hint = new Array();
		hint.push("Connect Ports");
		hint.push("Connect Words");
		hint.push("Create Connection");
		hint.push("Create Stream");
		hint.push("Create Event");
		hint.push("Create Node");
		hint.push("Advanced Menu");
		hint.push("Redraw");
		hint.push("Intersector");
	}
	registerMenu("Connect Ports", function() {
		hideCMenu();
		dijit.byId('connectPorts').onClick();
	});
	registerMenu("Create Connection", function() {
		hideCMenu();
		dijit.byId('addconnectionmenuitem').onClick();
	});
	registerMenu("Create Stream", function() {
		hideCMenu();
		dijit.byId('addstreammenuitem').onClick();
	});
	registerMenu("Create Event", function() {
		hideCMenu();
		dijit.byId('addeventmenuitem').onClick();
	});
	registerMenu("Create Node", function() {
		hideCMenu();
		dijit.byId('addnodemenuitem').onClick();
	});
	registerMenu("Advanced Menu", function() {
		hideCMenu();
		ShowSelectedAdvancedMenu();
	});
	registerMenu("Redraw", function() {
		hideCMenu();
		draw();
	});
	registerMenu("Connect Words", function() {
		hideCMenu();
		dijit.byId('connectWordsMenuItem').onClick();
	});
	registerMenu("Intersector", function() {
		hideCMenu();
		dijit.byId('connectWithIntersectorMenuId').onClick();
	});
	showCMenu(sxx, syy, lbl, hint);
}

function compileCamelProcess() {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var url = getURL("CompileCamelGraph");
	postFormWithContent(url, req, function(res) {
		alert((res));
		var r = findNodeById("Route" + currentGraph);
		if (r == null) {
			r = createRouteItem("Route" + currentGraph, 300, 300, res);
			addObjectToGraph(r);
		} else {
			r["routeDescription"] = res;
		}
		draw();
	});

}
function modifyGroup() {
	var a = lastSelectedNode;
	if (a != null && a.type == "group") {
		var nid = prompt('Enter group type', "generic");
		a.gtype = nid;
	}
}
function modifyConnectionColor() {
	var a = lastSelectedConn;
	if (a != null && a.type == "connection") {
		var nid = prompt('Enter group type', a.shape.color);
		a.shape.color = nid;
	}
}
function createGenericNodeItem(obj, x, y, textual) {
	var newNode = cloneItem(obj);
	if (newNode.id == null)
		newNode.id = getUniqId();
	else
		newNode["id"] = obj.id;

	newNode["type"] = obj.type;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["x"] = x;
	newNode["y"] = y;
	if (obj.cfg != null) {
		newNode["cfg"] = obj.cfg;
	}
	if (textual) {
		newNode.textual = true;
	}
	newNode["normalizedx"] = x;
	newNode["normalizedy"] = y;
	if (obj.icon == null || obj.icon.length < 1)
		newNode["icon"] = genericnodes[obj.type];
	else
		newNode["icon"] = obj.icon;
	// normalizeData();
	addObjectToGraph(newNode);
	var xpos = newNode.normalizedx;
	var ypos = newNode.normalizedy;
	var borderColor = COLOR_RED;
	var fillColor = COLOR_RED;
	drawGenericNode(xpos, ypos, 48, 48, fillColor, borderColor, newNode,
			newNode.icon);
	return newNode;
}
function getUniqId() {
	var str = "Random_" + Math.ceil(Math.random() * 1000);
	var ret = findNodeById(str);
	var i = 0;
	while (ret != null) {
		str = str + "_" + i;
		ret = findNodeById(str);
		i++;
	}
	return str;

}
function fixGraph() {
	for ( var k in pData.data) {
		var a = pData.data[k];
		if (a != null) {
			if (a.type == "node") {
				var b = {};
				var foundbad = false
				for ( var i in a) {
					if (i != "defs") {
						b[i] = a[i];
					} else {
						foundbad = true;
					}
				}
				if (foundbad) {
					removeNodeById(a.id);
					addObjectToGraph(b);
				}
			}

			if (a.type == "stream") {
				var b = {};
				var foundbad = false

				for ( var i in a) {
					if (i != "props") {
						b[i] = a[i];
					} else {
						foundbad = true;
					}
				}
				if (foundbad) {
					removeNodeById(a.id);
					addObjectToGraph(b);
				}

			}
		}
	}
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == null || obj.id == null) {
			removeObjectFromGraph(i, 1);
			return obj;
		}
	}
	draw();
}
function magicSpell2(res) {
	var a = dojo.fromJson(res);
	var tosend = new Array();
	for (var i = 0; i < a.length; i++) {
		var nd = findNodeById(a[i]);
		if (nd != null) {
			tosend.push(nd);
			var ar = findConnConnecting(nd.id);
			if (ar != null) {
				for ( var v in ar) {
					tosend.push(ar[v]);
				}
			}
		}

	}
	var req = {};
	req.name = currentGraph;
	req.process = dojo.toJson(tosend);
	var url = getURL("MagicSpell2");
	postFormWithContent(url, req, function(res) {

		var data = dojo.fromJson(res);
		/*
		 * {"mc":"1","matches":["SearchBingAndGoogle"
		 * ],"inpaths":[{"graphname":"SearchBingAndGoogle","paths":[["SearchPageStream",
		 * "SearchEventCopierStream","BingProcStream","BingResultIteratorStream","GoogleEventParserStream"]]}]}
		 */var msg = "";
		msg += "Results:" + data.mc + "<br/>";
		if (data.mc > 0) {
			for ( var j in data.matches) {
				msg += "<b>Graphs:&nbsp;&nbsp;</b><i>" + data.matches[j]
						+ "</i><br/>";
			}
			for ( var j in data.inpaths) {
				msg += ("<b>Paths in Graph: &nbsp;&nbsp;&nbsp;</b><i>"
						+ data.inpaths[j].graphname + "</i><br/> ");
				for ( var k in data.inpaths[j].paths) {
					msg += data.inpaths[j].paths[k];
				}
				msg += "<br/>";
			}
		}
		dynaHtmlDlg(msg, "Search results.", null, "", "ok");
	});

}

function addExistingEvent() {
	GetAndShowEventTypes('chooseExistEvent');
	var evtDlg = dijit.byId('addExistEventDlg');
	evtDlg.show();
}
function addExistingEventToPage(nodeData) {
	var newDataObject = dojo.fromJson(nodeData);
	var sel = dojo.byId("chooseExistEvent");
	var sidx = sel.selectedIndex;
	var evtname = sel.options[sidx].text;
	var newNode = {};
	newNode["type"] = "event";
	newNode["id"] = evtname;
	newNode["x"] = mouseX;
	newNode["normalizedx"] = mouseX;
	newNode["y"] = mouseY;
	newNode["normalizedy"] = mouseY;
	newNode["r"] = 48;
	newNode["b"] = 48;
	newNode["eventStore"] = "...";
	addObjectToGraph(newNode);
	var borderColor = COLOR_BLUE;
	var fillColor = COLOR_BLUE;

	drawEventRect(newNode.x, newNode.y, newNode.r, newNode.b, fillColor,
			borderColor, newNode.id);

}

function getEventProp(myname, func) {
	if (myname == null) {
		if (lastSelectedNode == null)
			return;
		else
			myname = lastSelectedNode.eventType;
	}

	dojo.xhrGet({
		url : getURL("GETEventDefinition") + myname,
		load : function(response, ioArgs) {
			if (func != null)
				func(response);
			console.log(response)
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			console.log("Failed to retrive processtypes" + a.result);
			return response;
		}
	});
}
function setStreamConfigDlg() {
	cfg = new Array();
	old = new Array();
	{
		var a = lastSelectedNode.cfg;
		var ov = lastSelectedNode.cfgval;
		if (ov != null) {
			for ( var j in ov) {
				old.push(ov[j]);
			}
		} else {
			old.push("");
		}
		old.push("");
		if (a != null) {
			for (var i = 0; i < a.length; i++) {
				var def = a[i];
				cfg.push(def);

			}
			dynaDlg(cfg, old,
					'setStreamConfiguration(getDlgValues(configdlg,cfg));');
		}
	}
}
function setStreamConfiguration(evt) {
	var stream = lastSelectedNode;
	if (stream != null)
		stream.cfgval = evt;
}
function covertToNonFreeHand() {
	var a = lastSelectedConn;
	a.shape = null;
}

function magicSpell3(res) {
	var a = dojo.fromJson(res);
	var tosend = new Array();
	for (var i = 0; i < a.length; i++) {
		var nd = findNodeById(a[i]);
		if (nd != null) {
			tosend.push(nd);
			var ar = findConnConnecting(nd.id);
			if (ar != null) {
				for ( var v in ar) {
					tosend.push(ar[v]);
				}
			}
		}

	}
	var req = {};
	req.name = currentGraph;
	req.process = dojo.toJson(tosend);
	var url = getURL("MagicSpell3");
	postFormWithContent(url, req, function(res) {
		alert(res);
		var a = dojo.fromJson(res);

		reapplyCloneIdsAll(clonedids)

		draw();
	});

}
function createRegion(res) {
	var a = dojo.fromJson(res);
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == "connection") {
			if (a.contains(obj.from) && a.contains(obj.to)) {
				a.push(obj.id);
			}
		}

	}
	var str = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
	}
	// alert(str);
	var newnd = {};
	for (var j = 0; j < 100; j++) {
		var nd = findNodeById("RandomGroupNode" + j);
		if (nd == null)
			break;
	}
	newnd.id = prompt('Enter Id', "RandomGroupNode" + j);
	if (newnd.id == null || newnd.id == "") {
		draw();
		return;
	}
	newnd.type = "region";
	newnd.gtype = "generic";
	newnd.order = "0";
	newnd.normalizedx = 300;
	newnd.normalizedy = 300;
	newnd.x = 300;
	newnd.y = 300;
	newnd.r = 48;
	newnd.b = 48;
	newnd.items = a;
	newnd.closed = true;
	newnd.color = currConnColor;
	var shape = {};
	shape.pts = dojo.toJson(lastSelectedPts);
	var shapestr = "var shape=pCanvas.path(\""
			+ getPathFromPointsArray(lastSelectedPts) + "\");\n";
	shapestr += "shape.attr(\"fill\",\"green\");";
	shapestr += "shape.attr(\"stroke\",\"red\");";
	shapestr += "shape.toBack();";
	shapestr += "return shape;"
	shape.design = shapestr;
	newnd.shape = shape;
	addObjectToGraph(newnd);
	draw();
	var evtdata = {};
	evtdata.id = newnd.id;
	evtdata.type = "shapecreated";
	sendEvent(geq, [ evtdata ]);

}
function createAvoidanceLayer(res) {
	createLayer(res, [ "avoid" ])
}
function createLayer(res, tags) {
	var a = dojo.fromJson(res);
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == "connection") {
			if (a.contains(obj.from) && a.contains(obj.to)) {
				a.push(obj.id);
			}
		}

	}
	var newnd = {};
	for (var j = 0; j < 100; j++) {
		var nd = findNodeById("RandomGroupNode" + j);
		if (nd == null)
			break;
	}
	newnd.id = prompt('Enter Id', "RandomGroupNode" + j);
	if (newnd.id == null || newnd.id == "") {
		draw();
		return;
	}
	var attrs = new Array();
	attrs.push("simple");
	if (tags != null) {
		for (var k = 0; k < tags.length; k++) {
			attrs.push(tags[k]);
		}
	}
	newnd = createLayerObj(newnd.id, newnd.id, attrs, dojo.toJson(a));

	addObjectToGraph(newnd);

	for (var i = 0; i < a.length; i++) {
		var obj = findNodeById(a[i]);
		if (obj != null) {
			if (obj.layer == undefined || obj.layer == null) {
				obj.layer = new Array();
			}
			obj.layer.push(newnd.id);
		}
	}
	draw();
	var evtdata = {};
	evtdata.id = newnd.id;
	evtdata.type = "layercreated";
	sendEvent(geq, [ evtdata ]);

}
function exportPageStaticUrl() {
	var sel = dojo.byId("p_streamname");
	var sidx = sel.selectedIndex;
	var streamname = sel.options[sidx].text;

	var pname = dojo.byId("pagename").value;
	var url = prompt("Please enter url:", getURL("ServerUrl") + streamname
			+ "?page=" + pname);

}
function exportPageDynamicUrl() {
	var sel = dojo.byId("p_streamname");
	var sidx = sel.selectedIndex;
	var streamname = sel.options[sidx].text;

	var pname = dojo.byId("pagename").value;
	var url = getURL("ServerUrl") + streamname + "?page=" + pname;

	getStreamPageDynaURL(url);
}
function deleteRegion() {
	if (lastSelectedRegion != null)
		removeNodeById(lastSelectedRegion.id);
	lastSelectedRegion = null;
	draw();
}
function updateRegion() {
	if (lastSelectedRegion == null)
		return;
	var pts = dojo.fromJson(lastSelectedRegion.shape.pts);
	finditemsinregion(pts, updateLastSelectedRegion);
}
function updateLastSelectedRegion(res) {
	var a = dojo.fromJson(res);
	lastSelectedRegion.items = a;
}
function setRegionType() {
	if (lastSelectedRegion == null) {
		alert("please select the region that you whant to update");
		return;
	}
	lastSelectedRegion.gtype = prompt("Please enter region's type:",
			lastSelectedRegion.gtype);
}

function setRegionOrder() {
	if (lastSelectedRegion == null) {
		alert("please select the region that you whant to update");
		return;
	}
	lastSelectedRegion.order = prompt("Please enter region's order:",
			lastSelectedRegion.order);
}
function recompileRegions() {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var url = getURL("ReprocessRegions");
	postFormWithContent(url, req, function(res) {
		resultMsgDlg(res);
	});
}

function removeSelfConn(nullsonly) {
	for (var i = 0; i < pData.data.length; i++) {
		var o = pData.data[i];
		if (o.type == "connection")
			if (o.from == o.to)
				if (nullsonly)
					if (o.id == null)
						removeObjectFromGraph(i, 1);
	}
}

function showGraphItemStatus() {
	var r = {};
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type != null) {
			r[obj.id] = "<a onclick=\"removeNodeById(\'" + obj.id
					+ "\');draw();\"><u>" + obj.type + "</u></a>";
		} else {
			r[obj.id] = "<a onclick=\"removeNodeById(\'" + obj.id
					+ "\');draw();\"><u>" + "null" + "</u></a>";
		}
	}
	resultMsgDlgObj(r);
}
function groupOnInstance(res) {
	var a = dojo.fromJson(res);
	var grpnode = null;
	for (var i = 0; i < a.length; i++) {
		var obj = findNodeById(a[i]);
		if (obj.type == "functionobj") {
			grpnode = obj// find first group node
			break;
		}
	}
	if (grpnode != null) {
		for (var i = 0; i < a.length; i++) {
			var obj = findNodeById(a[i]);
			if (obj.id != grpnode.id) {
				obj.cn = grpnode.id;
			}
		}
	} else {
		alert("Please select a function node to group on.")
	}

}
function createEventMappingProcessor() {
	cfg = new Array();
	old = new Array();
	cfg.push("classname");
	cfg.push("bean");
	cfg.push("fldname");
	old.push("org.ptg.util.events.ExcelReadEvent");
	old.push("eventIn");
	old.push("*");
	dynaDlg(cfg, old, 'getAndEventMapping(getDlgValues(configdlg,cfg));');
}
function createBeanMappingProcessor() {
	cfg = new Array();
	old = new Array();
	var bean = prompt("Enter bean name", "org.ptg.util.events.ExcelReadEvent")
	var b = bean.split(".");
	b = (b[b.length - 1]);
	cfg.push("classname");
	cfg.push("bean");
	cfg.push("mtdname");
	old.push(bean);
	old.push("eventIn");
	old.push(b);
	dynaDlg(cfg, old, 'getAndMethodMapping(getDlgValues(configdlg,cfg));');
}
function createVarOnlyMappingProcessor() {
	cfg = new Array();
	old = new Array();
	// / getCO(300,300, obj.cn, "green",obj.type,obj.cn,obj.dataType);
	cfg.push("varname");
	cfg.push("datatype");
	old.push(getUniqId());
	old.push("java.lang.Object");
	dynaDlg(cfg, old, 'addClassObjVar(getDlgValues(configdlg,cfg));');
}
function addClassObjVar(obj) {
	var o = {};
	o.type = "classobj";
	o.cn = obj.varname;
	o.dataType = obj.datatype;
	o.id = obj.varname;
	addGenericObj(o);
}

function createVarMappingProcessor() {
	cfg = new Array();
	old = new Array();
	// / getCO(300,300, obj.cn, "green",obj.type,obj.cn,obj.dataType);
	cfg.push("varname");
	cfg.push("vardatatype");
	cfg.push("varvalue");
	cfg.push("valdatatype");
	old.push(getUniqId());
	old.push("java.lang.Object");
	old.push(getUniqId());
	old.push("java.lang.Object");

	dynaDlg(cfg, old, 'addClassObjVarPair(getDlgValues(configdlg,cfg));');
}

function addClassObjVarPair(obj) {
	var o = {};
	o.type = "classobj";
	o.cn = obj.varname;
	o.dataType = obj.vardatatype;
	o.id = obj.varname;
	addGenericObj(o);
	var o2 = {};
	o2.type = "classobj";
	o2.cn = obj.varvalue;
	o2.dataType = obj.valdatatype;
	o2.id = obj.varvalue;
	addGenericObj(o2);
}
function deleteGraphItemFromServer(id, type) {
	var url = getURL("DeleteFromServer");
	var req = {};
	req.name = id;
	req.type = type;
	postFormWithContent(url, req, function(res) {
		alert("Deleted " + id + " Successfully. ");
	});

}
// //////////////////////////////////////////////
function DeleteEventFromServer() {
	var nd = lastSelectedNode;
	if (nd != null && nd.type == "event") {
		deleteGraphItemFromServer(nd.id, nd.type);
	}
}
function DeleteNodeFromServer() {
	var nd = lastSelectedNode;
	if (nd != null && nd.type == "node") {
		deleteGraphItemFromServer(nd.id, "processor");
	}
}
function DeleteStreamFromServer() {
	var nd = lastSelectedNode;
	if (nd != null && nd.type == "stream") {
		deleteGraphItemFromServer(nd.id, nd.type);
	}
}
function DeleteConnFromServer() {
	var nd = lastSelectedConn;
	if (nd != null && nd.type == "connection") {
		deleteGraphItemFromServer(nd.id, nd.type);
	}
}

// ------------------- save configuration
function saveWideConfig(name) {
	if (name == null)
		name = prompt("Enter name for this configuration", wideconfigtoUse);
	if (wideconfig.mapping_level == null) {
		wideconfig.mapping_level = "10";
	}
	if (wideconfig.rotation_step == null) {
		wideconfig.rotation_step = "10";
	}
	if (wideconfig.trace_ele == null) {
		wideconfig.traceele = "1";
	}
	if (wideconfig.stepMoverSize == null) {
		wideconfig.stepMoverSize = "5";
	}
	if (wideconfig.ieversion == null) {
		wideconfig.ieversion = "9";
	}
	if (wideconfig.cross_hair == null) {
		wideconfig.cross_hair = "true";
	}
	if (wideconfig.global_canvas == null) {
		wideconfig.global_canvas = "true";
	}
	if (wideconfig.incr_zindex == null) {
		wideconfig.incr_zindex = "false";
	}
	if (wideconfig.highlite_ports == null) {
		wideconfig.highlite_ports = "false";
	}
	if (wideconfig.graph_type == null) {
		wideconfig.graph_type = "JavaCode";
	}
	if (wideconfig.units == null) {
		wideconfig.units = "mm";
	}
	if (wideconfig.material == null) {
		wideconfig.material = "ALUM";
	}
	if (wideconfig.pixelsPerUnit == null) {
		wideconfig.pixelsPerUnit = "20";
	}
	if (wideconfig.depth == null) {
		wideconfig.depth= ".3";
	}
	if (wideconfig.numofpoints == null) {
		wideconfig.numofpoints= "200";
	}
	if (wideconfig.offsetx== null) {
		wideconfig.offsetx= "0";
	}
	if (wideconfig.offsety== null) {
		wideconfig.offsety= "0";
	}
	if (wideconfig.toolsizeinmm== null) {
		wideconfig.toolsizeinmm= "5";
	}
	if (wideconfig.modelheightmm== null) {
		wideconfig.modelheightmm= "5";
	}
	if (wideconfig.modelheightinch== null) {
		wideconfig.modelheightinch= ".5";
	}
	if (wideconfig.iterstepmm== null) {
		wideconfig.iterstepmm= ".1";
	}
	if (wideconfig.iterstepinch== null) {
		wideconfig.iterstepinch= ".0";
	}
		
	var b = {
		"name" : name,
		"type" : "wide",
		"config" : dojo.toJson(wideconfig)
	};
	CallWebServiceSimple(getURL("SaveConfig"), b, function fun(s) {
		addInfoToBox(s);
	}, function fun(s) {
		addErrorToBox(s);
	});

}
function applyWideConfig(a) {
	if (a != null)
		wideconfig = a;
	/* now apply image */
	if (wideconfig.bkimage != null && wideconfig.bkimage != "") {
		/* alert(wideconfig.bkimage); */
		var img = pCanvas.image(wideconfig.bkimage, 0, 0, screenWidth,
				screenHeight);
		img.toBack();
	}
	if (wideconfig.showgrid == "true") {
		for (var i = 0; i < screenWidth; i += 50) {
			var ln = pCanvas.line(0, i, screenHeight, i);
			ln.attr({
				"stroke" : "grey",
				"stroke-width" : .2
			});
			ln.toBack();
		}
		for (var i = 0; i < screenHeight; i += 50) {
			var ln = pCanvas.line(i, 0, i, screenWidth);
			ln.attr({
				"stroke" : "grey",
				"stroke-width" : .2
			});
			ln.toBack();
		}
	}
	applyDrawConfig();
}
function modifyWideConfiguration() {
	cfg = new Array();
	old = new Array();
	for ( var i in wideconfig) {
		cfg.push(i);
		old.push(wideconfig[i]);
	}
	dynaDlg(cfg, old, 'applyWideConfig(getDlgValues(configdlg,cfg));');
}
function getWideConfig(name) {
	if (name == null)
		name = prompt("Enter configuration to load", "Default");
	var b = {
		"name" : name,
		"type" : "wide"
	};
	CallWebServiceSimple(getURL("GetConfig"), b, function fun(s) {/* alert(s); */
		if (s != null && s.length > 0 && s != "null") {
			wideconfig = dojo.fromJson(s);
		}
	}, function fun(s) {/* alert(s); */
	});
}

function saveLibraryFunction() {
	saveGraph('lib');
}

function openLibraryFunction() {
	openGraph('lib');
}
function addLibraryFunction() {
	importExistingGraph('lib');
}

// -----------------------
function parseSQL(name) {
	if (name == null)
		name = prompt("Please Enter SQL", "Default");
	var b = {
		"sql" : name
	};
	CallWebServiceSimple(getURL("ParseSQL"), b, function fun(s) {
		var i = dojo.fromJson(s);
		var y = 0;
		var b = {};
		for ( var ii in i) {
			if (i[ii].type == "sqlobj") {
				b.type = "sqlobj";
				b.name = getUniqId();
				b.id = b.name;
				b.text = getConnectionLabel(i[ii].sql);
				b.sql = i[ii].sql;
				b.sqlType = i[ii].sqlType;
				b.textual = true;
				b = createGenericNodeItem(b, 300, 300 + y);
			}
		}
		y += 18;
		for ( var ii in i) {
			if (i[ii].type == "ColDef") {
				var a = {};
				a.type = i[ii].type;
				a.name = i[ii].name;
				a.text = i[ii].name;
				a.colOp = i[ii].colOp;
				a.order = i[ii].order;
				a.textual = true;
				a.grp = b.id;
				a.c = "green";
				createGenericNodeItem(a, 300, 300 + y);
				y += 18;
			}
		}
	}, function fun(s) {/* alert(s); */
	});
}
function parseDynaSQL(name) {
	if (name == null)
		name = prompt("Please Enter SQL", "Default");
	var b = {
		"sql" : name,
		"type" : "dynasql"
	};
	CallWebServiceSimple(getURL("ParseSQL"), b, function fun(s) {
		var i = dojo.fromJson(s);
		var y = 0;
		var b = {};
		for ( var ii in i) {
			if (i[ii].type == "sqlobj") {
				b.type = "sqlobj";
				b.name = getUniqId();
				b.id = b.name;
				b.text = getConnectionLabel(i[ii].sql);
				b.sql = i[ii].sql;
				b.sqlType = i[ii].sqlType;
				b.textual = true;
				b = createGenericNodeItem(b, 300, 300 + y);
			}
		}
		y += 18;
		for ( var ii in i) {
			if (i[ii].type == "ColDef") {
				var a = {};
				a.type = i[ii].type;
				a.name = i[ii].name;
				a.text = i[ii].name;
				a.colOp = i[ii].colOp;
				a.order = i[ii].order;
				a.dataType = i[ii].dataType;
				a.textual = true;
				a.grp = b.id;
				a.c = "green";
				createGenericNodeItem(a, 300, 300 + y);
				y += 18;
			}
		}
	}, function fun(s) {/* alert(s); */
	});
}
function realignSQLObj(nd) {
	var count = 0;
	var items = {};
	var main = findDrawEleById(nd.id).item;
	var x = main.attr("x");
	var y = main.attr("y");
	var start = main.getBBox().width + 10;
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if ((obj.type != null && obj.type == "ColDef")
				|| (obj.type != null && obj.type == "TableDef")) {
			if (obj.grp != null && obj.grp == nd.id) {
				items[count] = obj;
				count++;
			}
		}
	}
	x = x + start;
	var maxx = 5;
	var ox = x;
	for (var j = 0; j < count; j++) {
		var it = items[j];
		if (it != null) {
			var ai = findDrawEleById(it.id);
			if (ai != null) {
				var a = ai.item;
				a.attr({
					"x" : x,
					"y" : y
				});
				var ndi = findNodeById(it.id);
				ndi.x = x;
				ndi.y = y;
				ndi.normalizedx = x;
				ndi.normalizedy = y;
				start = (a.getBBox().width + 10);
				x = x + start;
				if (j > 1 && j % maxx == 0) {
					y += 18;
					x = ox;
				}
			}
		}
	}
}
function compileSQLMapOnServer(mtype, eventtype, exec) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	/* get current sql */
	var sqlout = null;
	var sqlin = null;
	var countsql = 0;
	for (var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'sqlobj') {
			var obj = pData.data[i];
			req.sqlType = obj.sqlType;
			countsql++;
			if (req.sqlType == "query") {
				sqlin = obj.sql;
				req.sql = obj.sql;
			}
			if (req.sqlType == "update" || req.sqlType == "insert") {
				sqlout = obj.sql;
				req.sql = obj.sql;
			}
		}
	}

	if (countsql > 1) {
		req.sqlout = sqlout;
		req.sqlin = sqlin;
		req.sqlType = "tablemap";
	}
	if (exec) {
		req.run = "true";
	}
	var url = getURL("CompileSQLMapper");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function compileSQLDBMapOnServer(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	if (eventtype != null)
		req.eventtype = eventtype;
	if (mtype != null)
		req.mappingtype = mtype;
	/* get current sql */
	var sqlout = null;
	var sqlin = null;
	var countsql = 0;
	for (var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'sqlobj') {
			var obj = pData.data[i];
			req.sqlType = obj.sqlType;
			countsql++;
			if (req.sqlType == "query") {
				sqlin = obj.sql;
				req.sql = obj.sql;
			}
			if (req.sqlType == "update" || req.sqlType == "insert") {
				sqlout = obj.sql;
				req.sql = obj.sql;
			}
		}
	}

	if (countsql > 1) {
		req.sqlout = sqlout;
		req.sqlin = sqlin;
		req.sqlType = "DBMap";
	}

	var url = getURL("CompileSQLMapper");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}

function clearDesign() {
	clearDijit();
	compDiag = new Array();
	globals = new Array();
	maps = new Array();
	grids = new Array();
	htmlcomps = new Array();
	dojo.destroy("dynamicnodes");
	dojo.create("div", {
		"id" : "dynamicnodes"
	}, "bodyelem");

}
function saveDesign() {
	var name = prompt('Enter Design name', currentGraph);
	currentGraph = name;
	displayGraphName(name);
	CallWebServiceWithNameAndConfig("SaveDesign", name, dojo.toJson(pData),
			dojo.toJson(compDiag))
}
function openGraphDesignDialog() {
	var loc = dojo.byId('choosegraphid2');
	loc.innerHTML = "";
	dojo.xhrGet({
		url : urlMap.GetDesigns,
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			var indexToSet = -1;
			dojo.forEach(a, function(oneEntry, index, array) {
				temp = dojo.create("option", {
					innerHTML : oneEntry.name
				}, loc);
				if (oneEntry.name == currentGraph) {
					indexToSet = index;
				}
			});
			loc.selectedIndex = indexToSet;
			var createConnectionDialog = dijit.byId('openGraphDesignDialog');
			createConnectionDialog.show();
			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to retrive processtypes" + a.result);
			return response;
		}
	});
}
function getGraphDesignFromServer(nodeData) {
	var sel = dojo.byId("choosegraphid2");
	var sidx = sel.selectedIndex;
	var graphname = sel.options[sidx].text;
	getGraphDesignByName(graphname);
}
function getGraphDesignByName(graphname, optionalCallBack) {
	// alert("Getgraphdesignbyname");
	clearDesign();
	clearGraph();
	dojo.xhrGet({
		url : urlMap.GetDesign + "?graphid=" + graphname,
		load : function(response, ioArgs) {
			try {
				var obj = dojo.fromJson(response)
				if (wideconfig.load_graph_on_design_open == "true") {
					compDiag = dojo.fromJson(obj[0].config);
					applyDesign();
					var name = (obj[0].name);
					loadGraph(obj, name)
					if (optionalCallBack != null)
						optionalCallBack();
				} else {
					compDiag = dojo.fromJson(obj[0].config);
					applyDesign();
					if (optionalCallBack != null)
						optionalCallBack();
				}
			} catch (exp) {
				alert(exp);
				console.log(exp);
			}

			return response;
		},
		error : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			alert("Failed to Load Graph" + a.result);
			return response;
		}
	});
}
function gotoEditor() {
	window.open("/site/bam_5678/editor.html");
}

function findnearCompDiag(x1, y1) {
	var dist = 10000;
	var id = "";
	for (i in compDiag) {
		var dx = compDiag[i].left;
		var dy = compDiag[i].top;
		var wd = compDiag[i].width;
		var ht = compDiag[i].height;
		if (dx == null || dy == null) {
			dx = compDiag[i].mx;
			dy = compDiag[i].my;
			if (wd == null)
				wd = 48;
			if (ht == null)
				ht = 48

			if (dx == null || dy == null) {
				continue;
			}
		}
		dx = fixCord(dx);
		dy = fixCord(dy);
		wd = fixCord(wd);
		ht = fixCord(ht);
		dx = dx + wd / 2;
		dy = dy + ht / 2;
		dx = dx - x1;
		dy = dy - y1;
		var vdist = Math.sqrt(dx * dx + dy * dy);
		console.log("dx: " + dx + ",dy: " + dy + ",x: " + x1 + ",y1: " + y1
				+ ",vdist:" + vdist + ",id:" + compDiag[i].id);
		console.log(dojo.toJson(compDiag[i]));
		if (vdist < dist) {
			dist = vdist;
			id = compDiag[i].id;
		}
	}
	return id;
}
function fixCord(dy) {
	if (dy != null && typeof (dy) == "string") {
		if (dy.indexOf("px") > -1) {
			dy = dy.substring(0, dy.length - 2);
			dy = parseInt(dy);
		} else {
			dy = parseInt(dy);
		}
	}
	return dy;
}

function updateToDoStatus() {
	var a = lastSelectedNode;
	if (a.type == "ToDoEvent") {
		var p = prompt("Enter completion status between[0-100]",
				a.compStatus == null ? 10 : a.compStatus);
		a.compStatus = parseInt(p);
	}
}
function updateToDoAction() {
	var a = lastSelectedNode;
	if (a.type == "ToDoEvent") {
		var p = prompt("Enter action (server jsscript)",
				a.action == null ? "js_Print('" + a.id + "');" : a.action)
		a.action = (p);
	}
}
function startModuleConnListener() {
	dojo.subscribe(geq, null, function(evtdata) {
		if (evtdata.type == "conncreated") {
			console.log("new connection created now processing...>  "
					+ evtdata.id)
			var conn = findNodeById(evtdata.id);
			var from = findNodeById(conn.from);
			var to = findNodeById(conn.to);
		}
	});
}
function CompileGraphTemplate(mtype, eventtype) {
	var objstr = dojo.toJson(pData.data);
	var req = {};
	req.name = currentGraph;
	req.process = objstr;
	var url = getURL("CompileTemplate");
	postFormWithContent(url, req, function(res) {
		dynaHtmlDlg(res, "Message", null, "", "ok");
	});
}
function execElementFromGraph() {
	if (globals[currentGraph + "_currentExec"] == null) {
		prepareGraphToExec(dojo.toJson(currentGraphConfig),
				execElementFromGraph);
	} else {
		var cd = getCode(lastSelectedNode);
		exec(cd, lastSelectedNode.id, globals[currentGraph + "_currentExec"]);
	}
}

function prepareGraphToExec(hint, f) {
	getExecTran(hint, f);
}
function getExecTran(config, f) {
	postFormWithContent("/site/GetExecTran", {
		"name" : currentGraph,
		"tosave" : dojo.toJson(pData),
		"graphconfig" : config
	}, function(data) {
		globals[currentGraph + "_currentExec"] = data;
		if (f != null)
			f();
	});
}

function exec(exec, eleid, uid) {
	postFormWithContent("/site/ExecuteGraphElement", {
		"exec" : exec,
		"uid" : uid,
		"eleid" : eleid
	}, function(data) {
		alert(data);
	});
}

function createArbitObject(res) {
	var a = dojo.fromJson(res);
	for (var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == "connection") {
			if (a.contains(obj.from) && a.contains(obj.to)) {
				a.push(obj.id);
			}
		}

	}
	var str = "";
	for (var i = 0; i < a.length; i++) {
		str += a[i];
		if (i < a.length - 1) {
			str += ":"
		}
	}
	// alert(str);
	var newnd = {};
	for (var j = 0; j < 100; j++) {
		var nd = findNodeById("RandomGroupNode" + j);
		if (nd == null)
			break;
	}
	newnd.id = prompt('Enter Id', "RandomGroupNode" + j);
	if (newnd.id == null || newnd.id == "") {
		draw();
		return;
	}
	newnd.type = "ArbitObject";
	newnd.gtype = "generic";
	newnd.order = "0";
	newnd.normalizedx = 300;
	newnd.normalizedy = 300;
	newnd.x = 300;
	newnd.y = 300;
	newnd.r = 48;
	newnd.b = 48;
	newnd.items = a;
	newnd.closed = true;
	newnd.color = currConnColor;
	newnd.props = [];
	newnd.propsvals = [];
	newnd.linkto = null;
	var shape = {};
	shape.pts = dojo.toJson(lastSelectedPts);
	var shapestr = "var shape=pCanvas.path(\""
			+ getPathFromPointsArray(lastSelectedPts) + "\");\n";
	shapestr += "shape.attr(\"fill\",\"green\");";
	shapestr += "shape.attr(\"stroke\",\"red\");";
	shapestr += "shape.toBack();";
	shapestr += "return shape;"
	shape.design = shapestr;
	newnd.shape = shape;
	addObjectToGraph(newnd);
	draw();
	var evtdata = {};
	evtdata.id = newnd.id;
	evtdata.type = "shapecreated";
	sendEvent(geq, [ evtdata ]);

}

function pasteFreeHand() {
	if (cpBuffer != null) {
		for (var i = 0; i < cpBuffer.length; i++) {
			addObjectToGraph(cpBuffer[i]);
		}
	}
	draw();
}

function ModifySchemaForStream() {
	window
			.open("/site/wide_5678/wide.html.jsp?page=" + lastSelectedNode.id
					+ "Schema", "Schema Editor",
					"dialogWidth:900px;dialogHeight:900px");
}

function setAnonConfigDlg() {
	getAnonConfig(null);
}
function updateConfigDialog() {
	dojo
			.xhrGet({
				url : getURL("ANONCONFIG") + "?name="
						+ lastSelectedNode.anonType,
				load : function(response, ioArgs) {
					var doc = dojo.fromJson(response);
					var configdlg = dojo.byId("ComponentConfigDialog");
					configdlg.innerHTML = "No Extra Configuration.";

					var cntn = "";
					cntn += "<form > <table>";
					var oldval = lastSelectedNode.configItems;
					if (oldval != null)
						oldval = dojo.fromJson(oldval);
					if (doc != null) {
						cfg = dojo.fromJson(doc[0].configoptions);
						if (cfg != null) {
							for (var i = 0; i < cfg.length; i++) {
								cntn += "<tr>";
								var a = cfg[i];
								var val = oldval == null ? "" : oldval[a];
								cntn += "<td><label for=\"id" + a + "\">" + a
										+ "</label></td>";
								cntn += "<td><input type=text id=\"id" + a
										+ "\" name=\"" + a + "\" value=\""
										+ val + "\"></input></td>"
							}
							cntn += "<tr>";
							cntn += "<td><input type=\"button\" name=\""
									+ "SaveExtraConfigBtn"
									+ "\" onclick=\"setAnonConfig();\"  value=\"Save\"></input></td>"
							cntn += "</tr>";
							cntn += "</form></table>";
							configdlg.innerHTML = cntn;
						}

					}
					// configdlg.show();
					return response;
				},
				error : function(response, ioArgs) {
					var configdlg = dojo.byId("ComponentConfigDialog");
					configdlg.innerHTML = "No Extra Configuration.";
					var a = dojo.fromJson(response);
					return response;
				}
			});
}
function getAnonConfig(name) {
	if (configdlg == null) {
		configdlg = new dijit.Dialog({
			title : "Anon Extra Configuration",
			style : "width: 300px"
		});
	}
	dojo
			.xhrGet({
				url : getURL("ANONCONFIG") + "?name="
						+ lastSelectedNode.anonType,
				load : function(response, ioArgs) {
					var doc = dojo.fromJson(response);
					configdlg.attr("content", "No Extra Configuration.");
					var cntn = "";
					cntn += "<form > <table>";
					var oldval = lastSelectedNode.configItems;
					if (oldval != null)
						oldval = dojo.fromJson(oldval);
					if (doc != null) {
						cfg = dojo.fromJson(doc[0].configoptions);
						if (cfg != null) {
							for (var i = 0; i < cfg.length; i++) {
								cntn += "<tr>";
								var a = cfg[i];
								var val = oldval == null ? "" : oldval[a];
								cntn += "<td><label for=\"id" + a + "\">" + a
										+ "</label></td>";
								cntn += "<td><input type=text id=\"id" + a
										+ "\" name=\"" + a + "\" value=\""
										+ val + "\"></input></td>"
							}
							cntn += "<tr>";
							cntn += "<td><input type=\"button\" name=\""
									+ "SaveExtraConfigBtn"
									+ "\" onclick=\"setAnonConfig();\"  value=\"Save\"></input></td>"
							cntn += "<td><input type=\"button\" name=\""
									+ "CloseCfgDlg"
									+ "\" onclick=\"configdlg.hide();\"  value=\"Close\"></input></td>"
							cntn += "</tr>";
							cntn += "</form></table>";
							configdlg.attr("content", cntn);
						}

					}
					configdlg.show();
					return response;
				},
				error : function(response, ioArgs) {
					var a = dojo.fromJson(response);
					return response;
				}
			});
}

function setAnonConfig() {
	var mycfg = {};
	if (configdlg != null) {
		configdlg.hide();
	}
	if (cfg != null) {
		for (var i = 0; i < cfg.length; i++) {
			var val = dojo.byId("id" + cfg[i]).value;
			mycfg[cfg[i]] = val;
		}
	}
	lastSelectedNode.configItems = dojo.toJson(mycfg);
}
var extendedMenu = {};
extendedMenu[0] = {};
extendedMenu[0].icon = "/site/images/dashboard.png";
extendedMenu[0].name = "Dashboard";
extendedMenu[0].mtd = function(aroundItem) {
	HideSelectedAdvancedMenu();
};
extendedMenu[1] = {};
extendedMenu[1].icon = "/site/images/desktop.png";
extendedMenu[1].name = "Edit";
extendedMenu[1].mtd = function(aroundItem) {
	setTimeout(hideCMenu, 100);
	showUpdateStreamDialogAtMouse();
};
extendedMenu[2] = {};
extendedMenu[2].icon = "/site/images/ical.png";
extendedMenu[2].name = "connect";
extendedMenu[2].mtd = function(aroundItem) {
	// drawFreeFlow(true);
	// started=true;
	if (aroundItem == null || (typeof aroundItem == "undefined")) {
		aroundItem = lastSelectedNode;
	}
	var sub = new Array();
	var bb1 = {};
	var bb2 = {};
	bb1.x = aroundItem.normalizedx;
	bb1.y = aroundItem.normalizedy;
	bb1.width = 16;
	bb1.height = 16;
	bb2.x = aroundItem.normalizedx + 80;
	bb2.y = aroundItem.normalizedy;
	bb2.width = 16;
	bb2.height = 16;

	var item = createConnObject(aroundItem.id, getUniqId());
	addObjectToGraph(item);
	var id = item.id;
	arrowLink2(bb1, bb2, sub, id);
	sub[0].attr("stroke", "red");
	sub[1].attr("stroke", "red");
	sub[0].attr("stroke-width", "4");
	sub[1].attr("stroke-width", "4");
	handleConnClick(sub[0]);

}

extendedMenu.total = 3;
var showingExtendedAdvancedMenu = false;
function HideSelectedAdvancedMenu() {
	showingExtendedAdvancedMenu = false;
	var cnt = extendedMenu.total;
	for (var i = 0; i < cnt; i++) {
		var p = extendedMenu[i].drawEle;
		if (p != null && (typeof p != "undefined")) {
			try {
				var p = extendedMenu[i].drawEle;
				extendedMenu[i].drawEle = null;
				p.remove();
			} catch (err) {
				console.log(err);
			}
		}
	}
}
function ShowSelectedAdvancedMenu(aroundItem) {
	if (showingExtendedAdvancedMenu == true) {
		HideSelectedAdvancedMenu();
	}
	showingExtendedAdvancedMenu = true;
	var item = null;
	if (aroundItem != null) {
		item = aroundItem;
	} else {
		item = lastSelectedNode;
	}
	if (item != null) {
		var cnt = extendedMenu.total;

		var sx = item.normalizedx;
		var sy = item.normalizedy;

		var d = 2 * Math.PI / cnt
		var angle = 0;
		var distance = 70;
		for (var i = 0; i < cnt; i++) {
			var x1 = Math.round(sx + distance * Math.cos(angle));
			var y1 = Math.round(sy + distance * Math.sin(angle));
			var p = pCanvas.image(extendedMenu[i].icon, sx, sy, 32, 32)
					.animate({
						"x" : x1 - 16,
						"y" : y1 - 16
					}, 400, "<>");
			p.node.id = getUniqId();
			p.node.setAttribute("index", i);
			extendedMenu[i].drawEle = p;
			p.click(function(event) {
				var idx = this.node.getAttribute("index");
				var id = this.node.id;
				console.log("Clicked: " + extendedMenu[idx].name);
				if (extendedMenu[idx].mtd != null) {
					extendedMenu[idx].mtd(aroundItem);
				}
			});
			angle += d;
		}
	}
}
function traceExec() {
	dojo.subscribe("TaskTraceEvent", null, function(jdata) {
		if (jdata.ctx[jdata.targetId + "_result"] == "Successful") {
			flatGreenLEDGraph(jdata.targetId, 10000);
		} else if (jdata.ctx[jdata.targetId + "_result"] == "Failed") {
			flatRedLEDGraph(jdata.targetId, 10000);
		} else {
			if (handleTaskTraceEvent != null) {
				handleTaskTraceEvent(jdata);
			}
		}

	});
}
traceExec();

function widgetSourceCreator(item, hint) {
	var isportable = false;
	if (item == "Port") {
		propType = 'prop';
		isportable = true;
	} else if (item == "InputPort") {
		propType = 'input';
		isportable = true;
	} else if (item == "OutputPort") {
		propType = 'output';
		isportable = true;
	} else if (item == "AuxPort") {
		propType = 'aux';
		isportable = true;
	} else if (item == "PortMethod") {
		propType = 'prop';
		isportable = true;
	} else if (item == "Slider") {
		propType = 'output';
		isportable = true;
	} else {
		var dropId = item;
		if (typeof (item) != "string") {
			dropId = item.id;
			if (item.id.indexOf("dijit__TreeNode") == 0) {
				var nd = dijit.byId(dropId);
				dropId = nd.item.name;
				if (dropId == "Port" || dropId == "InputPort"
						|| dropId == "OutputPort" || dropId == "AuxPort"
						|| dropId == "PortMethod" || dropId == "Slider") {
					item = dropId;
					console.log("Dropped non portable item " + dropId);
					if (item == "Port") {
						propType = 'prop';
						isportable = true;
					} else if (item == "InputPort") {
						propType = 'input';
						isportable = true;
					} else if (item == "OutputPort") {
						propType = 'output';
						isportable = true;
					} else if (item == "AuxPort") {
						propType = 'aux';
						isportable = true;
					} else if (item == "PortMethod") {
						propType = 'prop';
						isportable = true;
					} else if (item == "Slider") {
						propType = 'output';
						isportable = true;
					}
				} else {
					isportable = false;
				}
			} else {
				isportable = false;
			}
			console.log("Dropped non portable " + dropId);
		}
	}
	var myDiv = null;
	if (isportable == true) {
		if (hint == 'avatar') {
			myDiv = dojo.create('div', {
				ptype : propType,
				'class' : "portable dojoDndItem",
				id : dojo.dnd.getUniqueId(),
				innerHTML : item
			});
			myDiv.innerHTML = item;
		} else {
			if (item == "Slider") {
				var mod = lastDropModule.substring(4);
				var itemid = dojo.dnd.getUniqueId();
				myDiv = dojo.create('div', {
					ptype : propType,
					'class' : "portable dojoDndItem",
					grpid : mod,
					id : itemid,
					innerHTML : item
				});
				var nodes = dojo.query("#" + lastDropModule + " .portable");
				console.log(nodes.length);
				myDiv.setAttribute("index", nodes.length);
				var id = prompt("label");
				myDiv.innerHTML = "<div id='" + itemid + "_slider" + "'>" + id
						+ "</div>";
				setTimeout(function() {
					var slider = new dijit.form.HorizontalSlider({}, itemid
							+ "_slider");
					hookDijitToPort(itemid + "_slider", null, "out_" + mod
							+ "." + itemid);
				}, 200);

			} else {
				var mod = lastDropModule.substring(4);
				myDiv = dojo.create('div', {
					ptype : propType,
					'class' : "portable dojoDndItem",
					grpid : mod,
					id : dojo.dnd.getUniqueId(),
					innerHTML : item
				});
				var nodes = dojo.query("#" + lastDropModule + " .portable");
				console.log(nodes.length);
				myDiv.setAttribute("index", nodes.length);
				var id = prompt("label");
				myDiv.innerHTML = id;
			}
		}
	} else {
		if (hint == 'avatar') {
			myDiv = dojo.create('div', {
				'class' : "dojoDndItem",
				id : dojo.dnd.getUniqueId(),
				innerHTML : dropId
			});
		} else {
			myDiv = dojo.create('div', {
				'class' : "dojoDndItem",
				id : dojo.dnd.getUniqueId(),
				innerHTML : dropId
			});
		}
	}
	return {
		node : myDiv,
		data : item
	};
}
function hierSourceCreator(item, hint) {
	var isportable = false;
	if (item == "Port" || item == "ComplexPort") {
		propType = 'prop';
		isportable = true;
	} else if (item == "InputPort" || item == "ComplexInputPort") {
		propType = 'input';
		isportable = true;
	} else if (item == "OutputPort" || item == "ComplexOutputPort") {
		propType = 'output';
		isportable = true;
	} else if (item == "AuxPort" || item == "ComplexAuxPort") {
		propType = 'aux';
		isportable = true;
	} else if (item == "PortMethod" || item == "ComplexPortMethod") {
		propType = 'prop';
		isportable = true;
	} else {
		var dropId = item.id;
		if (item.id.indexOf("dijit__TreeNode") == 0) {
			var nd = dijit.byId(dropId);
			dropId = nd.item.name;
			if (dropId == "Port" || dropId == "InputPort"
					|| dropId == "OutputPort" || dropId == "AuxPort"
					|| dropId == "PortMethod" || dropId == "ComplexPort"
					|| dropId == "ComplexInputPort"
					|| dropId == "ComplexOutputPort"
					|| dropId == "ComplexAuxPort"
					|| dropId == "ComplexPortMethod") {
				item = dropId;
				console.log("Dropped non portable item " + dropId);
				if (item == "Port" || item == "ComplexPort") {
					propType = 'prop';
					isportable = true;
				} else if (item == "InputPort" || item == "ComplexInputPort") {
					propType = 'input';
					isportable = true;
				} else if (item == "OutputPort" || item == "ComplexOutputPort") {
					propType = 'output';
					isportable = true;
				} else if (item == "AuxPort" || item == "ComplexAuxPort") {
					propType = 'aux';
					isportable = true;
				} else if (item == "PortMethod" || item == "ComplexPortMethod") {
					propType = 'prop';
					isportable = true;
				}
			} else {
				isportable = false;
			}
		} else {
			isportable = false;
		}
		console.log("Dropped non portable " + dropId);

	}
	var myDiv = null;
	if (isportable == true) {
		if (hint == 'avatar') {
			myDiv = dojo.create('div', {
				ptype : propType,
				'class' : "portable dojoDndItem",
				id : dojo.dnd.getUniqueId(),
				innerHTML : item
			});
			myDiv.innerHTML = item;
			return {
				node : mydiv,
				data : item
			};
		} else {
			var mod = lastDropModule;/* .substring(4); */
			var id = prompt("label");
			var dtype = prompt("data type", "int");
			var realid = mod + "." + id;
			var realgrp = lastDropModuleGrp;
			var liid = "li_" + realid;
			var mydivouter = null;
			var evtdata = {};
			if (item.toString().indexOf("Complex") != 0) {
				/* this is simple node */
				mydivouter = dojo.create('li', {
					"id" : liid
				});
				myDiv = dojo.create('div', {
					ptype : propType,
					'class' : "portable dojoDndItem",
					'dtype' : dtype,
					grpid : realgrp,
					id : realid,
					innerHTML : item
				}, mydivouter);
				var nodes = dojo.query("#" + lastDropModule + " .portable");
				var myindex = nodes.length;
				console.log(myindex);
				myDiv.setAttribute("index", myindex);
				myDiv.innerHTML = id;
				evtdata.iscomplex = false;
			} else {
				/* this is Complex node */
				mydivouter = dojo.create('li', {
					"id" : liid,
					"class" : "submenu",
					"style" : "background-image: url('open.gif');"
				});
				myDiv = dojo.create('div', {
					ptype : propType,
					'class' : "portable",
					'dtype' : dtype,
					index : myindex,
					grpid : realgrp,
					"style" : "display: block;",
					"rel" : "open",
					id : realid,
					innerHTML : item
				}, mydivouter);
				var myuldiv = dojo.create('ul', {
					"style" : "display: block;",
					"rel" : "open",
					ulid : realid,
					id : realid
				}, mydivouter);
				var nodes = dojo.query("#" + lastDropModule + " .portable");
				console.log(nodes.length);
				myDiv.setAttribute("index", nodes.length);
				myDiv.innerHTML = id;
				evtdata.iscomplex = true;
			}
			getDelButton(realid, myDiv, realgrp);
			evtdata.id = liid;
			evtdata.portid = realid;
			evtdata.pid = mod;
			evtdata.type = "portadded";
			sendEvent(porteventstream, [ evtdata ]);
			/* if(item.toString().indexOf("Complex")!=0) */
			{
				setTimeout(function() {
					moveHeirItemPort(evtdata)
				}, 300);
			}
			return {
				node : mydivouter,
				data : item
			};

		}

	}
	return {
		node : mydiv,
		data : item
	};
}
function getDelButton(eleid, where, realgrpid) {
	var onclickFun = "dojo.destroy(dojo.byId('" + eleid + "'));"
			+ "updateModuleContent('" + realgrpid + "');";
	var mnode = dojo.create("input", {
		"id" : "button_" + eleid,
		"type" : "button",
		"value" : "x",
		"style" : "width:20px;height:20px",
		"onclick" : onclickFun
	}, where);
}
function moveHeirItemPort(e) {
	var nd = dojo.byId(e.id);
	var gf = dojo.byId(e.id).parentNode.parentNode;
	var fromrem = dojo.byId(e.id).parentNode;
	var toadd = null;
	for (var i = 0; i < gf.childNodes.length; i++) {
		var ulnode = gf.childNodes[i];
		if (ulnode.nodeName == "UL" || ulnode.nodeName == "ul") {
			if (ulnode.id == e.pid) {
				toadd = ulnode;
			}
		}
	}
	if (ulnode != null) {
		fromrem.removeChild(nd);
		toadd.appendChild(nd);
	}
	if (lastDropModuleGrp != null) {
		updateModuleContent(lastDropModuleGrp);
	}
	convertNestedItemsToContainers();
	preparePortable();

}
function updateModuleContent(mod) {
	// alert(mod);
	findComponentByName(mod).cntn = dojo.byId("bid_" + mod).innerHTML;
	// alert(findComponentByName(mod).cntn);
}
initporteventstreamListener();
function initporteventstreamListener() {
	dojo.subscribe(porteventstream, null, function(jdata) {
		var str = jdata.id;
		var pid = jdata.portid;
		if (str != null) {
			console.log("New port added: " + str + " : " + pid);
		}

	});
}

function repoTreeOnDblClick(item) {
	if (!item.root) {
		var lbl = repostore.getLabel(item);
		console.log("DBlClicked:" + lbl);
		if (lbl.indexOf(":") > -1) {
			var parts = lbl.split(":");
			lbl = parts[1];
		}
		getAndShowStaticComp(lbl);
	}
}

function repoTreeOnClick(item) {
	if (!item.root) {
		var lbl = repostore.getLabel(item);
		console.log("Clicked: " + lbl);
	}
}

function convertNestedItemsToContainers() {
	console.log("mod: " + lastDropModuleGrp);
	var nodes = dojo.query("div");
	nodes.forEach(function(node, items, vals) {
		if (node != null) {
			var grpid = node.getAttribute("rel");
			if (grpid != null) {
				console.log(node.getAttribute("grpid"));
				var id = node.getAttribute("id");
				console.log(id);

				var target = new dojo.dnd.Source(node, {
					copyOnly : true,
					accept : [ "text", "treeNode" ],
					creator : hierSourceCreator
				});
				dojo.connect(target, "onDrop", function(source, nodes, copy) {
				});

			}
		}
	});
}

function fixEditor(id) {
	var a = editors[id]
	if (a != null) {
		var nd = dojo.byId(id);
		var pos = dojo.position(nd);
		var b = dojo.byId("textarea_" + id);
		b.style.width = (pos.w - 30) + "px";
		b.style.height = (pos.h - 86) + "px";
		a.resize();
	}
}

function getWindowSize() {
	var myWidth = 0, myHeight = 0;
	if (typeof (window.innerWidth) == 'number') {
		// Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if (document.documentElement
			&& (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
		// IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if (document.body
			&& (document.body.clientWidth || document.body.clientHeight)) {
		// IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	return {
		"w" : myWidth,
		"h" : myHeight
	};
}

function getScrollXY() {
	var scrOfX = 0, scrOfY = 0;
	if (typeof (window.pageYOffset) == 'number') {
		// Netscape compliant
		scrOfY = window.pageYOffset;
		scrOfX = window.pageXOffset;
	} else if (document.body
			&& (document.body.scrollLeft || document.body.scrollTop)) {
		// DOM compliant
		scrOfY = document.body.scrollTop;
		scrOfX = document.body.scrollLeft;
	} else if (document.documentElement
			&& (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
		// IE6 standards compliant mode
		scrOfY = document.documentElement.scrollTop;
		scrOfX = document.documentElement.scrollLeft;
	}
	return [ scrOfX, scrOfY ];
}

function portifyModuleInput(res) {
	portifyModuleAs(res, "input");
}

function portifyModuleOutput(res) {
	portifyModuleAs(res, "output");
}
function portifyModuleAux(res) {
	portifyModuleAs(res, "aux");
}
function portifyModuleAs(res, astype) {
	var a = dojo.fromJson(res);
	var cpBuffer = new Array();
	for (var i = 0; i < a.length; i++) {
		var nd = findNodeById(a[i]);
		if (nd != null) {
			if (nd.type == "module") {
				cpBuffer.push(nd.id);
			}
		}
	}

	for (var i = 0; i < cpBuffer.length; i++) {
		var mod = cpBuffer[i];
		var nodes = dojo.query("#" + mod + " input");
		var iindex = 0;
		var shortType = "aux";
		if (astype == "aux")
			shortType = "aux";
		if (astype == "input")
			shortType = "inp";
		if (astype == "output")
			shortType = "out";
		nodes
				.forEach(function(node, items, vals) {
					var name = node.getAttribute("name");
					var idgen = mod + name;
					dojo.attr(node, "id", idgen);
					dojo.attr(node, "grpid", mod);
					dojo.attr(node, "index", "" + iindex);
					dojo.attr(node, "ptype", astype);
					dojo.addClass(node, "portable");
					dojo.attr(node, "pval", node.value);

					dojo
							.attr(
									node,
									"onchange",
									"var tn =dojo.byId('"
											+ idgen
											+ "'); findNodeById('"
											+ shortType
											+ "_"
											+ mod
											+ "."
											+ idgen
											+ "').portval = tn.value; dojo.attr(tn,'pval', tn.value);tn.setAttribute('value',tn.value);syncModuleCode(tn.getAttribute('grpid'));");
					iindex++;
				});
		var nodes = dojo.query("#" + mod + " select");
		nodes
				.forEach(function(node, items, vals) {
					var name = node.getAttribute("name");
					var idgen = mod + name;
					dojo.attr(node, "id", idgen);
					dojo.attr(node, "grpid", mod);
					dojo.attr(node, "index", "" + iindex);
					dojo.attr(node, "ptype", astype);
					dojo.addClass(node, "portable");
					dojo.attr(node, "pval", getSelVal(idgen));
					dojo
							.attr(
									node,
									"onchange",
									"var tn =dojo.byId('"
											+ idgen
											+ "');var tnv=getSelVal('"
											+ idgen
											+ "'); findNodeById('"
											+ shortType
											+ "_"
											+ mod
											+ "."
											+ idgen
											+ "').portval = tnv;dojo.attr(tn,'pval', tnv);dojo.query('#'+ '"
											+ mod
											+ " select[name='+'"
											+ name
											+ "'+']>option').forEach(function (nd){if(nd.value==tnv){nd.setAttribute('selected','selected')}else{nd.removeAttribute('selected')}});syncModuleCode(tn.getAttribute('grpid'));");
					iindex++;
				});
	}
	preparePortable();
	var pos = dojo.position(mod);
	var evtdata = {};
	evtdata.id = mod;
	evtdata.type = "ssmmovestop";
	evtdata.pos = pos;
	sendEvent(geq, [ evtdata ]);
	syncModuleCode(mod);
	draw();
}

function setCssPropertt(target, prop, value) {
	if (typeof prop != "undefined")
		target.style[prop] = value;
}
function getCssProperty(target, prop) {
	if (typeof prop != "undefined")
		return target.style[prop];
	return null;
}
function reverseSelectedConnection() {
	if (lastSelectedConn != null) {
		var from = lastSelectedConn.from;
		var to = lastSelectedConn.to;
		lastSelectedConn.from = to;
		lastSelectedConn.to = from;
		lastSelectedConn.nodes = [ to, from ];
		var ai = findDrawEleById(lastSelectedConn.id);
		if (ai != null) {
			removeDrawEleByItemId(ai.id);
			removeDrawElement(ai);
			connectLinks(lastSelectedConn);
		} else {
			draw();
		}
	}
}
function syncModuleCode(modName) {
	var nd = findNodeById(modName);
	var comp = findComponentByName(modName);
	comp.cntn = dojo.byId("bid_" + modName).innerHTML;
	comp.code = comp.cntn;
	nd.val = comp.cntn;

}
function replaceAll(str, key, what) {
	/*
	 * var tuid = what; if(tuid!=null && tuid.length>0){ str =
	 * str.replace(/\$\{id\}/g,tuid); cntn = cntn.replace(/\$\{id\}/g,uid);
	 * configobj.id = uid; configobj.cntn = cntn;
	 */
}
function resizeCanvas(id) {
	var pos = getRealPos(id);
	var canvas = canvasModules[id];
	var w = parseInt(pos.w);
	var h = parseInt(pos.h);
	canvas.clear();
	canvas.setSize(w - 10, h - 20);
	canvas.rect(0, 0, w - 12, h - 30, 10);
}
function setCSSRotation(uid, rotation) {
	if (rotation.indexOf && rotation.indexOf("deg") > 0) {
		if (wideconfig.ieversion != null && parseInt(wideconfig.ieversion) < 9) {
			setRotationCSSForIE(dojo.byId(uid), rotation);
		}
		setCssPropertt(dojo.byId(uid), "-webkit-transform", "rotate("
				+ rotation + ")");
		setCssPropertt(dojo.byId(uid), "transform", "rotate(" + rotation + ")");
		setCssPropertt(dojo.byId(uid), "-ms-transform", "rotate(" + rotation
				+ ")");
	} else {
		if (wideconfig.ieversion != null && parseInt(wideconfig.ieversion) < 9) {
			setRotationCSSForIE(dojo.byId(uid), rotation);
		}
		rotation = rotation + "deg";
		setCssPropertt(dojo.byId(uid), "-webkit-transform", "rotate("
				+ rotation + ")");
		setCssPropertt(dojo.byId(uid), "transform", "rotate(" + rotation + ")");
		setCssPropertt(dojo.byId(uid), "-ms-transform", "rotate(" + rotation
				+ ")");

	}
}
function setRotationCSSForIE(uid, angle) {
	if (angle.indexOf && angle.indexOf("deg") > 0) {
		angle = angle.substring(0, angle.indexOf("deg"));
	}
	angle = parseInt(angle);
	var rad = angleToRad(angle);
	var SIN_THETA = Math.sin(rad);
	var COS_THETA = Math.cos(rad);
	var fltr1 = "progid:DXImageTransform.Microsoft.Matrix(" + "M11 = "
			+ COS_THETA + "," + "M12 = " + -SIN_THETA + "," + "M21 = "
			+ SIN_THETA + "," + "M22 = " + COS_THETA + ","
			+ "sizingMethod = 'auto expand'		);";
	var fltr2 = "progid:DXImageTransform.Microsoft.Matrix(" + "M11 = "
			+ COS_THETA + "," + "M12 = " + -SIN_THETA + "," + "M21 = "
			+ SIN_THETA + "," + "M22 = " + COS_THETA + ","
			+ "sizingMethod = 'auto expand'		);";
	setCssPropertt(dojo.byId(uid), "filter", fltr1);
	setCssPropertt(dojo.byId(uid), "-ms-filter", fltr2);

}

function contains(a, obj) {
	for (var i = 0; i < a.length; i++) {
		if (a[i] === obj) {
			return true;
		}
	}
	return false;
}
function clearDijit() {
	if(compDiag!=null){
	for (var i = 0; i < compDiag.length; i++) {
		console.log("Clearing id: " + compDiag[i].id)
		var widget = dijit.registry.byId(compDiag[i].id);
		if (widget) {
			widget.destroyRecursive();
		}
		widget = dijit.registry.byId("place" + compDiag[i].id);
		if (widget) {
			widget.destroyRecursive();
		}

	}
	}
}
var recentActions = {};
function menuItemClicked(id) {
	console.log("menuItemClicked: " + id);
	doGetHtml(getURL("SaveRecentMenu") + "?m=" + id);
	menuItemClickedInternal(id);
}
function menuItemClickedInternal(id) {
	if (recentActions[id] == null || undefined == recentActions[id]) {
		var widget = dijit.registry.byId(id);
		var lbl = null;
		if (widget != null && undefined != widget) {
			lbl = widget.label;
		}
		recentActions[id] = lbl
		addRecentUsedMenu(id, lbl);
		recentActions[id]
	}

}
function clickMenuById(id) {
	var widget = dijit.registry.byId(id);
	if (widget != null && undefined != widget) {
		widget.onClick();
	}
}
function addRecentUsedMenu(id, lbl) {
	try {
		var wm = dijit.byId("RecentActionsMenu");
		var m = new dijit.MenuItem({
			id : "rc" + id,
			label : lbl
		});
		m.onClick = function() {
			var id = this.id.substring(2);
			var widget = dijit.registry.byId(id);
			if (widget != null && undefined != widget) {
				widget.onClick();
			}
		}
		wm.addChild(m);
	} catch (e) {
		console.log("error creating Window menu: " + id + e);
	}
}
function addWindowMenu(id) {
	try {
		var wm = dijit.byId("WindowMenu");
		var m = new dijit.MenuItem({
			id : "wm" + id,
			label : id + " Window"
		});
		m.onClick = function() {
			var id = this.id.substring(2);
			// alert(id);
			if (windows[id].visible) {
				windows[id].visible = false;
				hidediv(id);
			} else {
				windows[id].visible = true;
				showdiv(id);
			}
		}
		wm.addChild(m);
	} catch (e) {
		console.log("error creating Window menu: " + id);

	}
}
function getDegRotation(inangle/* string angle example: 320deg */) {
	var angle = inangle;
	if (angle.indexOf && angle.indexOf("deg") > 0) {
		angle = angle.substring(0, angle.indexOf("deg"));
	}
	angle = parseInt(angle);
	return angle;
}
function highLightValidPorts(startItemPort) {

	if (startItemPort != null) {
		console.log("Starting connection from : " + startItemPort.id);
		if (wideconfig.highlite_ports == "true") {
			var dtype = startItemPort.dtype;
			if (dtype != null) {
				var portsGreen = new Array();
				var portsRed = new Array();
				for (var j = 0; j < pData.data.length; j++) {
					var obj = pData.data[j];
					if (obj.type == "Port" && obj.id != startItemPort.id
							&& obj.porttype != "arbitport") {
						if (obj.dtype != null && obj.dtype == dtype) {
							portsGreen.push(obj);
						} else {
							portsRed.push(obj);
						}
					}
				}
				// now handle red and green ports
				for (var j = 0; j < portsGreen.length; j++) {
					var item = portsGreen[j];
					pCanvas.circle(item.normalizedx, item.normalizedy, 10)
							.attr("stroke", "green");
				}
				for (var j = 0; j < portsRed.length; j++) {
					var item = portsRed[j];
					pCanvas.circle(item.normalizedx, item.normalizedy, 10)
							.attr("stroke", "red");
				}
			}
		}
	}
}

function downloadCurrentGraph() {
	var urlStr = getURL("GETGRAPH") + "?graphid=" + currentGraph + "&d=true";
	window.open(urlStr);
}

function downloadCurrentDesign() {
	var urlStr = getURL("GetDesign") + "?graphid=" + currentGraph + "&d=true";
	window.open(urlStr);
}
function downloadCurrentDesignAsPdf() {
	var urlStr = getURL("GetDesign") + "?graphid=" + currentGraph
			+ "&d=true&f=pdf&w=400&h=400";
	window.open(urlStr);
}
function downloadCurrentDesignAsImage() {
	var urlStr = getURL("GetDesign") + "?graphid=" + currentGraph
			+ "&d=true&f=png&w=400&h=400";
	window.open(urlStr);
}
function genericFunctionModuleSelectionChange(id,modName){
	var tn =dojo.byId(modName); 
	var tnv=getSelVal(modName); 
	findNodeById('inp_'+id+'(MethodCall).'+modName).portval = tnv; 
	dojo.attr(tn,'pval', tnv);
	var qstr = '#'+ id+'(MethodCall)'+' select[name=\''+modName+'\']>option';
	console.log(qstr);
	dojo.query(qstr).forEach(function (nd){ 
		if(nd.value==tnv){
			nd.setAttribute('selected','selected')
		}else{
			nd.removeAttribute('selected');
			}
		}
	); 
	var fun = new Function ("tnvParam", id+'Code(tnvParam);');
	fun(tnv);
	
	syncModuleCode(tn.getAttribute('grpid'));
	preparePortable();
	removeRelativeConnFromGraph(id+"(MethodCall)");
}

function printAngle(x1,y1,x2,y2,x3,y3,x4,y4){
	var p1 = new jsts.geom.Coordinate(floatVal(x1),floatVal(y1))
	var p2 = new jsts.geom.Coordinate(floatVal(x2),floatVal(y2))
	var p3 = new jsts.geom.Coordinate(floatVal(x3),floatVal(y3))
	var p4 = new jsts.geom.Coordinate(floatVal(x4),floatVal(y4))
	var ls = new jsts.geom.LineSegment(p1,p2)
	var ls2 = new jsts.geom.LineSegment(p2,p3)
	var a1 = ls.angle()
	var a2 = ls2.angle()
	var d1 = toDegrees(a1);
	var d2 = toDegrees(a2);
	console.log(d1);
	// console.log(d2);
	}

var shapeObjCurrent = null;
var shapedatacfg = new Array();
var shapedataold = new Array();
function modifyObjectDataMap(shapeid){
	shapeObjCurrent=null;
		for(var i=0;i<pData.data.length;i++){
		    var d = pData.data[i];
		    if (  d.id==shapeid){
		    	shapeObjCurrent=d;
		    }
		}
		if(shapeObjCurrent!=null){
		shapedatacfg = new Array();
		shapedataold = new Array();
		var defaultShapeDataItems = {};
		if(shapeObjCurrent.type=="ShapeShape")
		{
		defaultShapeDataItems["depth"]="0";
		defaultShapeDataItems["close"]="false";
		defaultShapeDataItems["modeltop"]=wideconfig.modelheightmm;
		defaultShapeDataItems["feedRate"]="40";
		defaultShapeDataItems["feedRateRetract"]="100";
		defaultShapeDataItems["tollerance"]=".01";
		defaultShapeDataItems["toolsize"]=wideconfig.toolsizeinmm;
		defaultShapeDataItems["iter"]="1";
		defaultShapeDataItems["tag"]="match";
		}
		if(shapeObjCurrent.type=="hole"){
		defaultShapeDataItems["iter"]=".2";
		defaultShapeDataItems["depth"]=wideconfig.depth;

		defaultShapeDataItems["feedRate"]="40";
		defaultShapeDataItems["feedRateRetract"]="100";
		defaultShapeDataItems["x"]=parseInt(shapeObjCurrent.normalizedx)/parseInt(wideconfig.pixelsPerUnit);
		defaultShapeDataItems["y"]=parseInt(shapeObjCurrent.normalizedy)/parseInt(wideconfig.pixelsPerUnit);
		defaultShapeDataItems["overridexy"]="false";
		defaultShapeDataItems["modeltop"]=wideconfig.modelheightmm;
		
		}
		if(shapeObjCurrent.type=="circletap"){
		defaultShapeDataItems["iter"]=".2";
		defaultShapeDataItems["depth"]=wideconfig.depth;
		defaultShapeDataItems["feedRate"]="40";
		defaultShapeDataItems["feedRateRetract"]="100";
		defaultShapeDataItems["tollerance"]=".01";
		defaultShapeDataItems["toolsize"]=wideconfig.toolsizeinmm;
		defaultShapeDataItems["radius"]=parseInt(shapeObjCurrent.rad)/parseInt(wideconfig.pixelsPerUnit);
		defaultShapeDataItems["x"]=parseInt(shapeObjCurrent.normalizedx)/parseInt(wideconfig.pixelsPerUnit);
		defaultShapeDataItems["y"]=parseInt(shapeObjCurrent.normalizedy)/parseInt(wideconfig.pixelsPerUnit);
		defaultShapeDataItems["overridexy"]="false";
		defaultShapeDataItems["modeltop"]=wideconfig.modelheightmm;
		}

		for ( var i in shapeObjCurrent.data) {
			defaultShapeDataItems[i]=shapeObjCurrent.data[i];
		}
		for ( var i in defaultShapeDataItems) {
			shapedatacfg.push(i);
			shapedataold.push(defaultShapeDataItems[i]);
		}
		dynaDlg(shapedatacfg, shapedataold, 'applyObjectDataMap(getDlgValues(configdlg,shapedatacfg));');
		}
}
function applyObjectDataMap(a){
		shapeObjCurrent .data = a;
}