/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */
var moveGroupElements = true;
var supportAnimation = false;
var animObjects = new Array();
var animObjectsToRemove = {};
var animHandlers = {};
var pCanvas = {};
var pData = {};
pData.data = null;
pData.data = new Array();
var screenWidth = 6000;
var screenHeight = 6000;
var connEdit = false;
var connectionNodeWidth = 15;
var connectionNodeHeight = 15;
var leftClick = false;
var screenFontSize = 14;
var showStreamToNode = true;
var topBarHeight = 35;
var cmdRowHeight = 150;

var undoGraph = new Array();
var redoGraph = new Array();
var COLOR_BLUE = "blue";
var COLOR_GREEN = "green";
var COLOR_BLACK = "black";
var COLOR_RED = "red";
var COLOR_YELLOW = "yellow";
var font;
var frameRate = 5;
// Used in data normalization
var maxX = screenWidth;
var maxY = screenHeight;
var isCurrentGraphChanged = false;
var currentGraphId = "";
var leftWidth = 6;// 195;
var topWidth = 37;
var drawElements = new Array();
var mouseX = 0;
var mouseY = 0;
var realMouseX = 0;
var realMouseY = 0;

var ie = true;
var nodeTypeName = null;
var nodeName = null;
var nodeNameValue = null;
var xCoordinateName = null;
var xCoordinateValue = null;
var yCoordinateName = null;
var yCoordinateValue = null;
var nodeWidthName = null;
var nodeWidthValue = null;
var nodeHeightName = null;
var nodeHeightValue = null;
var streamballhue = 185;
var lastSelectedNode = null;
var currentGraph = null;
var lastSelectedConn = null;
var mouseOverConn = null;
var lastSelectedNote = null;
var maxdummy = 1;
var propm = null;
var docm = null;
var processIcons = new Array();
var cmenu = null;
var cmenuBall = null;
var porteventstream = "/PortEventStream";
var ceq = "/CustomEventQue";
var geq = "/GraphEventQue";
var ptq = "/PointQue";
var vars = {};
var defHeight = 84
var defWidth = 84;
var maxConnLabelLength = 20;
var useqarrow = true;
var compDiag2 = new Array();
var currConnColor = "red";
var nodeeventhandlers = {};
var postUpdateFun = {};
var preUpdateFun = {};
// /////////////////////// for connect stream on click and move graph function
var p1, p2;
var nd1, nd2;
var clickcount = 1;
var x1, y1, x2, y2;
var genericnodes = {};// typename->imagesrc param
var lastSelectedPts = {};
var lastSelectedRegion = null;
var onLayers = {};
var smoothen = true
var layeringEnabled = false;
var sortedSteps = new Array();
var eraseMode=false;
nodeeventhandlers["default"] = function(jdata) {
	console.log(dojo.toJson(jdata));
}
var nodeDrawingHandler = {};
var connDrawingHandler = null;
var gclist = new Array();
var sliderMap = {};
var lastAdded = null;
// ///////////////////////
var lastDragged = null;
var autoId = 1;
////////////////////////
var drawModulePort = true;
var allSubs = {};


///////////////////
var dragger = function() {
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
}, move = function(dx, dy) {
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
		selectedNode.normalizedx = (mouseX);
		selectedNode.normalizedy = (mouseY);
		selectedNode.x = (mouseX);
		selectedNode.y = (mouseY);
		var nditem = findDrawEleById(myid);
		if (nditem != null) {
			var ndtext = nditem.textnode;
			var imgnode = nditem.img;
			var item = nditem.item;
			var xpos = selectedNode.normalizedx;
			var ypos = selectedNode.normalizedy;
			if (ndtext != null) {
				ndtext.attr({
					"x" : (xpos - selectedNode.r / 2),
					"y" : (ypos - selectedNode.b / 2)
				});
			}
			if (imgnode != null) {
				var ix = xpos - selectedNode.r / 2;
				var iy = ypos - selectedNode.b / 2;
				imgnode.attr({
					"x" : ix,
					"y" : (iy)
				});
			}
			if (item != null) {
				if (this.type == "text") {
					item.attr({
						"x" : (xpos),
						"y" : (ypos)
					});
				} else if (this.type == "circle") {
					item.attr({
						"cx" : (xpos - selectedNode.r / 2),
						"cy" : (ypos - selectedNode.b / 2)
					});
				} else {
					item.attr({
						"x" : (xpos - selectedNode.r / 2),
						"y" : (ypos - selectedNode.b / 2)
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
}, up = function() {
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

			if (obj["type"] == "node") {
				getProcDoc(obj.clz);
			} else if (obj["type"] == "group") {
				// removed the code to rearrange will do on server side
			} else if (obj["type"] == "connection") {
				var d = dojo.byId('docval');
				d.innerHTML = "<b>" + obj.connCond + "</b>";
			} else {
				var d = dojo.byId('docval');
				d.innerHTML = "<b>" + "" + "</b>";
			}
		}
		var myid = this.node.getAttribute("eleid");
		var evtdata = {};
		evtdata.id = myid;
		evtdata.type = "up";
		sendEvent(geq, [ evtdata ]);
		if (Math.abs(this.ox - this.attr("x")) > 5
				|| Math.abs(this.oy - this.attr("y")) > 5) {
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
var txtdragger = function() {
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, txtmove = function(dx, dy) {
	var att = {
		x : mouseX,
		y : mouseY
	}
	this.attr(att);
	pCanvas.safari();
}, txtup = function() {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var myid = this.node.getAttribute("eleid");
	var obj = findNodeById(myid)
	if (obj != null) {
		obj.normalizedx = mouseX;
		obj.normalizedy = mouseY;
	}
	var jtextarea = dojo.byId("jsonval");
	jtextarea.innerHTML = this.attr("text")
	lastSelectedNote = myid;
	pCanvas.safari();
	draw();
};

String.prototype.capitalize = function() {
	return this.replace(/(^|\s)([a-z])/g, function(m, p1, p2) {
		return p1 + p2.toUpperCase();
	});
};

function showUpdateNodeDialogAtMouse() {
	var obj = lastSelectedNode;
	if (obj.type == "node") {
		createUpdateNodeDialog(obj);
	}
}

function showAddNodeDialogAtMouse() {
	var x = mouseX;
	var y = mouseY;
	createNewNodeDialog(x, y, 40, 40);
}
function showUpdateConnDialogAtMouse() {
	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedConn;
	if (obj != null && obj.type == "connection") {
		updateConnectionDialogAtXYID(x, y, obj.id);
	}
}

function showAddConnDialogAtMouse() {

	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedNode;
	if (obj != null)
		createConnectionDialogAtXYID(x, y, obj.id);
	else
		createConnectionDialogAtXYID(x, y, "");

}
function showUpdateStreamDialogAtMouse() {
	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedNode;
	if (obj != null && obj.type == "stream")
		updateStreamDialogAtXYID(x, y, obj.id);
}

function showAddStreamDialogAtMouse() {
	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedNode;
	if (obj != null)
		createStreamDialogAtXYID(x, y, obj.id);
	else
		createStreamDialogAtXYID(x, y, "");
}

function showAddEventDialogAtMouse() {
	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedNode;
	if (obj != null)
		createEventDialogAtXYID(x, y, obj.id);
	else
		createEventDialogAtXYID(x, y, "");

}
function showAddRouteDialogAtMouse() {
	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedNode;
	if (obj != null)
		createRouteDialogAtXYID(x, y, obj.id);
	else
		createRouteDialogAtXYID(x, y, "");

}
function ShowAddEventPropertyAtMouse() {
	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedNode;
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null)
		addEventPropertyDialogAtXYID(x, y, obj.id);
	else
		addEventPropertyDialogAtXYID(x, y, "");
}

function SaveEvent() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Save Event: " + obj.id);
		if (a) {
			var objstr = dojo.toJson(obj);
			CallWebService("SaveEvent", objstr);
		}
	}

}
function SaveNode() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Save Node: " + obj.id);
		if (a) {
			var objstr = dojo.toJson(obj);
			CallWebService("SaveNode", objstr);
		}
	}

}
function SaveRoute() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Save Route: " + obj.id);
		if (a) {
			var objstr = dojo.toJson(obj);
			CallWebService("SaveRoute", objstr);
		}
	}
}
function saveConnectionItem(nodedata) {
	var sel = dojo.byId("saveconnid");
	var sidx = sel.selectedIndex;
	var savenode = sel.options[sidx].text;
	var obj = findConnById(savenode);
	if (obj != null) {
		var a = confirm("Save Connection: " + obj.id);
		if (a) {
			var objstr = dojo.toJson(obj);
			CallWebService("SaveConnection", objstr);
		}
	}
}
function SaveConnection() {
	var loc = dojo.byId('saveconnid');
	loc.innerHTML = "";
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj != null) {
			if (obj.type == 'connection') {
				dojo.create("option", {
					innerHTML : obj.id
				}, loc);
			}
		}
	}
	var saveConnDialog = dijit.byId('saveConnecDialog');
	saveConnDialog.show();
}

function SaveStream() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Save Stream: " + obj.id);
		if (a) {
			var objstr = dojo.toJson(obj);
			CallWebService("SaveStream", objstr);
		}
	}

}

function DeleteEvent() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Delete Event: " + obj.id);
		if (a) {
			removeNodeById(obj.id);
		}
	}
	draw();
}

function DeleteConnection() {
	var x = mouseX;
	var y = mouseY;
	var obj = lastSelectedConn;
	if (obj != null) {
		var a = confirm("Delete Connection: " + obj.id);
		if (a) {
			removeNodeById(obj.id);
		}
	}
	draw();
}

function DeleteRoute() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Delete Route: " + obj.id);
		if (a) {
			removeNodeById(obj.id);
		}
	}
	draw();
}

function DeleteNode() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Delete Node: " + obj.id);
		if (a) {
			removeNodeById(obj.id);
		}
	}
	draw();
}

function DeleteStream() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;
	if (obj != null) {
		var a = confirm("Delete Stream: " + obj.id);
		if (a) {
			removeNodeById(obj.id);
		}
	}
	draw();
}

function ShowAddStreamPropertyDialog() {
	var x = mouseX;
	var y = mouseY;
	var obj = findSelectedNode(x, y);
	if (obj == null)
		obj = lastSelectedNode;

	if (obj != null)
		addStreamPropertyDialogAtXYID(x, y, obj.id);
	else
		addStreamPropertyDialogAtXYID(x, y, "");

}

function createBackgroundGrid() {
	for ( var i = 0, j = 1; i < screenWidth; i += 50, j++) {
		var attr = {
		};
		if (j % 2) {
			attr = {
				"stroke-width" : .5,
			};
		} else {
			attr = {
				"strokey-width" : 1,
			};
		}

		var p = pCanvas.path("M" + i + " 0L" + i + " " + screenHeight);
		p.attr(attr);
		p = pCanvas.path("M" + 0 + " " + i + "L" + screenWidth + " " + i);
		p.attr(attr);
	}
}

// Initialize the canvas, set the size font, font size
function setup() {
	setupRaphael();
	pCanvas = Raphael("graph", screenWidth, screenHeight);
	ie = document.all ? true : false;
	document.onmousemove = captureMousePosition2;
	initGeqListener();
}
function setupRaphael() {
	Raphael.fn.line = function(x1, y1, x2, y2) {
		return this.path("M" + x1 + " " + y1 + " L" + x2 + " " + y2);
	};
	Raphael.fn.arrow = function(x1, y1, x2, y2, size) {
		var angle = Math.atan2(x1 - x2, y2 - y1);
		angle = (angle / (2 * Math.PI)) * 360;
		var arrowPath = this.path(
				"M" + x2 + " " + y2 + " L" + (x2 - size) + " " + (y2 - size)
						+ " L" + (x2 - size) + " " + (y2 + size) + " L" + x2
						+ " " + y2).attr("fill", "black").rotate((90 + angle),
				x2, y2);
		var linePath = this.path("M" + x1 + " " + y1 + " L" + x2 + " " + y2);
		return [ linePath, arrowPath ];
	}
	Raphael.fn.qarrow = function(x1, y1, x2, y2, size) {
		var angle = Math.atan2(x1 - x2, y2 - y1);
		angle = (angle / (2 * Math.PI)) * 360;
		var arrowPath = this.path(
				"M" + x2 + " " + y2 + " L" + (x2 - size) + " " + (y2 - size)
						+ " L" + (x2 - size) + " " + (y2 + size) + " L" + x2
						+ " " + y2).attr("fill", "black").rotate((90 + angle),
				x2, y2);
		var xm = (x1 + x2) / 2;
		var ym = (y1 + y2) / 2
		if ((y1 - y2) > 0) {
			ym -= 20;
		} else {

			ym += 20;
		}
		if ((x1 - x2) > 0) {
			xm -= 20;
		} else {
			xm += 20;
		}
		var linePath = this.path("M" + x1 + " " + y1 + " Q " + xm + " " + ym
				+ " " + x2 + " " + y2);
		return [ linePath, arrowPath ];
	}
	Raphael.fn.lightarrow = function(x1, y1, x2, y2, size) {
		var angle = Math.atan2(x1 - x2, y2 - y1);
		angle = (angle / (2 * Math.PI)) * 360;
		var arrowPath = this.path(
				"M" + x2 + " " + y2 + " L" + (x2 - size) + " " + (y2 - size)
						+ " L" + (x2 - size) + " " + (y2 + size) + " L" + x2
						+ " " + y2).attr("fill", "black").rotate((90 + angle),
				x2, y2);
		var xm = (x1 + x2) / 2;
		var ym = (y1 + y2) / 2
		if ((y1 - y2) > 0) {
			xm -= 20;
			ym -= 20;
		} else {
			xm += 20;
			ym += 20;
		}
		var linePath = this.path("M" + x1 + " " + y1 + " Q " + xm + " " + ym
				+ " " + x2 + " " + y2);
		return [ linePath, arrowPath ];
	}
	Raphael.fn.connectionarrow = function(bb1, bb2, size) {
		var linePath = this.connection(bb1, bb2);
		// var linePath2 = this.connectioWn(bb1,bb2);
		// linePath2.translate(2,2);
		// linePath2.attr("opacity",".5");
		var length = linePath.getTotalLength();
		var pt1 = linePath.getPointAtLength(length - 8);
		var pt2 = linePath.getPointAtLength(length);
		var x1 = pt1.x, y1 = pt1.y, x2 = pt2.x, y2 = pt2.y;
		var angle = Math.atan2(x1 - x2, y2 - y1);
		angle = (angle / (2 * Math.PI)) * 360;
		var arrowPath = this.path(
				"M" + x2 + " " + y2 + " L" + (x2 - size) + " " + (y2 - size)
						+ " L" + (x2 - size) + " " + (y2 + size) + " L" + x2
						+ " " + y2).attr("fill", "black").rotate((90 + angle),
				x2, y2);
		// var linePath = this.connection(bb1,bb2);
		return [ linePath, arrowPath ];
	}

	Raphael.fn.ball = function(x, y, r, hue) {
		hue = hue || 0;
		return this.set(this.ellipse(x, y + r - r / 5, r, r / 2).attr({
			fill : "rhsb(" + hue + ", 1, .25)-hsb(" + hue + ", 1, .25)",
			stroke : "none",
			opacity : 0
		}), this.ellipse(x, y, r, r).attr(
				{
					fill : "r(.5,.9)hsb(" + hue + ", 1, .75)-hsb(" + hue
							+ ", .5, .25)",
					stroke : "none"
				}), this.ellipse(x, y, r - r / 5, r - r / 20).attr({
			stroke : "none",
			fill : "r(.5,.1)#ccc-#ccc",
			opacity : 0
		}));
	}
	Raphael.fn.connection = function(bb1, bb2) {
		if (bb1.x != bb2.x || bb1.y != bb2.y) {
			var p = [ {
				x : bb1.x + bb1.width / 2,
				y : bb1.y - 1
			}, {
				x : bb1.x + bb1.width / 2,
				y : bb1.y + bb1.height + 1
			}, {
				x : bb1.x - 1,
				y : bb1.y + bb1.height / 2
			}, {
				x : bb1.x + bb1.width + 1,
				y : bb1.y + bb1.height / 2
			}, {
				x : bb2.x + bb2.width / 2,
				y : bb2.y - 1
			}, {
				x : bb2.x + bb2.width / 2,
				y : bb2.y + bb2.height + 1
			}, {
				x : bb2.x - 1,
				y : bb2.y + bb2.height / 2
			}, {
				x : bb2.x + bb2.width + 1,
				y : bb2.y + bb2.height / 2
			} ], d = {}, dis = [];
			for ( var i = 0; i < 4; i++) {
				for ( var j = 4; j < 8; j++) {
					var dx = Math.abs(p[i].x - p[j].x), dy = Math.abs(p[i].y
							- p[j].y);
					if ((i == j - 4)
							|| (((i != 3 && j != 6) || p[i].x < p[j].x)
									&& ((i != 2 && j != 7) || p[i].x > p[j].x)
									&& ((i != 0 && j != 5) || p[i].y > p[j].y) && ((i != 1 && j != 4) || p[i].y < p[j].y))) {
						dis.push(dx + dy);
						d[dis[dis.length - 1]] = [ i, j ];
					}
				}
			}
			if (dis.length == 0) {
				var res = [ 0, 4 ];
			} else {
				res = d[Math.min.apply(Math, dis)];
			}
			var x1 = p[res[0]].x, y1 = p[res[0]].y, x4 = p[res[1]].x, y4 = p[res[1]].y;
			dx = Math.max(Math.abs(x1 - x4) / 2, 10);
			dy = Math.max(Math.abs(y1 - y4) / 2, 10);
			var x2 = [ x1, x1, x1 - dx, x1 + dx ][res[0]].toFixed(3), y2 = [
					y1 - dy, y1 + dy, y1, y1 ][res[0]].toFixed(3), x3 = [ 0, 0,
					0, 0, x4, x4, x4 - dx, x4 + dx ][res[1]].toFixed(3), y3 = [
					0, 0, 0, 0, y1 + dy, y1 - dy, y4, y4 ][res[1]].toFixed(3);
			var path = [ "M", x1.toFixed(3), y1.toFixed(3), "C", x2, y2, x3,
					y3, x4.toFixed(3), y4.toFixed(3) ].join(",");
			return this.path(path);
		} else {
			var x = bb1.x + bb1.width / 2;
			var y = bb1.y;
			var x2 = bb1.x + bb1.width / 2;
			var y2 = bb1.y + bb1.height;

			var path = [ "M", x, y, "A", bb1.width / 1.4, bb1.height / 1.4, 0,
					1, 0, x2, y2 ];
			return this.path(path);
		}
	}
	var tokenRegex = /\{([^\}]+)\}/g, objNotationRegex = /(?:(?:^|\.)(.+?)(?=\[|\.|$|\()|\[('|")(.+?)\2\])(\(\))?/g, // matches
	// .xxxxx
	// or
	// ["xxxxx"]
	// to
	// run
	// over
	// object
	// properties
	replacer = function(all, key, obj) {
		var res = obj;
		key.replace(objNotationRegex, function(all, name, quote, quotedName,
				isFunc) {
			name = name || quotedName;
			if (res) {
				if (name in res) {
					res = res[name];
				}
				typeof res == "function" && isFunc && (res = res());
			}
		});
		res = (res == null || res == obj ? all : res) + "";
		return res;
	}, fill = function(str, obj) {
		return String(str).replace(tokenRegex, function(all, key) {
			return replacer(all, key, obj);
		});
	};
	/*
	 * usage: var labels = []; label.push(r.text(60, 12, "24
	 * hits").attr(txt)); label.push(r.text(60, 27, "22 September
	 * 2008").attr(txt1).attr({fill: color})); label.hide(); var frame =
	 * r.popup(100, 100, label, "right").attr({fill: "#000", stroke: "#666",
	 * "stroke-width": 2, "fill-opacity": .7}).hide();
	 * 
	 */
	Raphael.fn.popup = function(X, Y, set, pos, ret) {
		pos = String(pos || "top-middle").split("-");
		pos[1] = pos[1] || "middle";
		var r = 5, bb = set.getBBox(), w = Math.round(bb.width), h = Math
				.round(bb.height), x = Math.round(bb.x) - r, y = Math
				.round(bb.y)
				- r, gap = Math.min(h / 2, w / 2, 10), shapes = {
			top : "M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}l-{right},0-{gap},{gap}-{gap}-{gap}-{left},0a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z",
			bottom : "M{x},{y}l{left},0,{gap}-{gap},{gap},{gap},{right},0a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z",
			right : "M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}l0-{bottom}-{gap}-{gap},{gap}-{gap},0-{top}a{r},{r},0,0,1,{r}-{r}z",
			left : "M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}l0,{top},{gap},{gap}-{gap},{gap},0,{bottom}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z"
		}, offset = {
			hx0 : X - (x + r + w - gap * 2),
			hx1 : X - (x + r + w / 2 - gap),
			hx2 : X - (x + r + gap),
			vhy : Y - (y + r + h + r + gap),
			"^hy" : Y - (y - gap)

		}, mask = [ {
			x : x + r,
			y : y,
			w : w,
			w4 : w / 4,
			h4 : h / 4,
			right : 0,
			left : w - gap * 2,
			bottom : 0,
			top : h - gap * 2,
			r : r,
			h : h,
			gap : gap
		}, {
			x : x + r,
			y : y,
			w : w,
			w4 : w / 4,
			h4 : h / 4,
			left : w / 2 - gap,
			right : w / 2 - gap,
			top : h / 2 - gap,
			bottom : h / 2 - gap,
			r : r,
			h : h,
			gap : gap
		}, {
			x : x + r,
			y : y,
			w : w,
			w4 : w / 4,
			h4 : h / 4,
			left : 0,
			right : w - gap * 2,
			top : 0,
			bottom : h - gap * 2,
			r : r,
			h : h,
			gap : gap
		} ][pos[1] == "middle" ? 1 : (pos[1] == "top" || pos[1] == "left") * 2];
		var dx = 0, dy = 0, out = this.path(fill(shapes[pos[0]], mask))
				.insertBefore(set);
		switch (pos[0]) {
		case "top":
			dx = X - (x + r + mask.left + gap);
			dy = Y - (y + r + h + r + gap);
			break;
		case "bottom":
			dx = X - (x + r + mask.left + gap);
			dy = Y - (y - gap);
			break;
		case "left":
			dx = X - (x + r + w + r + gap);
			dy = Y - (y + r + mask.top + gap);
			break;
		case "right":
			dx = X - (x - gap);
			dy = Y - (y + r + mask.top + gap);
			break;
		}
		out.translate(dx, dy);
		if (ret) {
			ret = out.attr("path");
			out.remove();
			return {
				path : ret,
				dx : dx,
				dy : dy
			};
		}
		set.translate(dx, dy);
		return out;
	};
	
}
var mxpts = new Array();
var mypts = new Array();

mxpts[0] = -1;
mxpts[1] = -1;
mxpts[2] = -1;
mypts[0] = -1;
mypts[1] = -1;
mypts[2] = -1;

function captureMousePosition2(e) {
	mouseX = 0;
	mouseY = 0;
	if (!e)
		var e = window.event;
	if (e.pageX || e.pageY) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	} else if (e.clientX || e.clientY) {
		mouseX = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
		mouseY = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
	}
	realMouseX = mouseX;
	realMouseY = mouseY;
	var ele = getElementFromPos(mouseX, mouseY);
	if (wideconfig.trace_ele!=null && parseInt(wideconfig.trace_ele)>5) {
		console.log(ele);
	}
	checkSizes();
	mouseY -= topWidth;
	mouseY -= scrollTop();
	//shouldwe do it? be
	//mouseX -= scrollLeft();
	var up = false, down = false;
	var left = false, right = false;
	mxpts.shift();
	mypts.shift();
	mxpts[2] = mouseX;
	mypts[2] = mouseY;

	if ((mxpts[0] > mxpts[1]) && (mxpts[1] > mxpts[2]))
		left = true;
	if ((mxpts[0] < mxpts[1]) && (mxpts[1] < mxpts[2]))
		right = true;
	if ((mypts[0] > mypts[1]) && (mypts[1] > mypts[2]))
		up = true;
	if ((mypts[0] < mypts[1]) && (mypts[1] < mypts[2]))
		down = true;
	// console.log(" u:"+up+" r:"+right+" d:"+down+ " l:"+left);
	/*
	 * console.log(mxpts); console.log(mypts);
	 */
}

function removeNodeById(id,noundo) {
	var nodeRequired = null;
	for ( var i = 0; i < pData.data.length; i++) {
		// if(pData.data[i].type != 'connection')
		{
			var obj = pData.data[i];
			if (obj.id == id) {
				removeObjectFromGraph(i, 1,noundo);
				return obj;
			}
		}
	}
	return nodeRequired;
}
function removeDrawEleByItemId(id) {
	var nodeRequired = null;
	for ( var i = 0; i < drawElements.length; i++) {
		var obj = drawElements[i].id;
		if (obj != null) {
			if (obj == id) {
				drawElements.splice(i, 1);
				return obj;
			}
		}
	}
	return nodeRequired;
}

function findDrawEleByItemId(id) {
	var nodeRequired = null;
	for ( var i = 0; i < drawElements.length; i++) {
		var obj = drawElements[i].id;
		if (obj != null) {
			if (obj == id) {
				nodeRequired = drawElements[i];
				break;
			}
		}
	}
	return nodeRequired;
}

function findDrawEleById(id) {
	var nodeRequired = null;
	for ( var i = 0; i < drawElements.length; i++) {
		var obj = drawElements[i].item;
		if (obj != null) {
			var myid = obj.node.getAttribute("eleid");
			if (myid == id) {
				nodeRequired = drawElements[i];
				break;
			}
		}
	}
	return nodeRequired;
}
function findDrawEleByIdEx(id) {
	var nodeRequired = null;
	for ( var i = 0; i < drawElements.length; i++) {
		var obj = drawElements[i];
		if (obj != null) {
			if (id == obj.id) {
				nodeRequired = drawElements[i];
				break;
			} else {
				var obj = obj.item;
				if (obj != null) {
					var myid = obj.node.getAttribute("eleid");
					if (id == myid) {
						nodeRequired = drawElements[i];
						break;
					}
				}
			}

		}
	}
	return nodeRequired;
}

function findNodeById(id) {
	var nodeRequired = null;
	for ( var i = 0; i < pData.data.length; i++) {
		// if(pData.data[i].type != 'connection')
		{
			var obj = pData.data[i];
			if (obj.id == id)
				nodeRequired = obj;
		}
	}
	return nodeRequired;
}
function findNodeByPortName(id) {
	var nodeRequired = null;
	for ( var i = 0; i < pData.data.length; i++) {
		// if(pData.data[i].type != 'connection')
		{
			var obj = pData.data[i];
			if (obj.portname == id)
				nodeRequired = obj;
		}
	}
	return nodeRequired;
}
function findConnById(id) {
	var nodeRequired = null;
	for ( var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'connection') {
			var obj = pData.data[i];
			if (obj.id == id)
				nodeRequired = obj;
		}
	}
	return nodeRequired;
}
function findConnConnecting(id) {
	var ar = new Array();
	for ( var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'connection') {
			var obj = pData.data[i];
			if (obj.from == id || obj.to == id)
				ar.push(obj);
		}
	}
	return ar;
}
function findSelectedNode(x, y) {
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		/*
		 * if(obj.type == 'node'||obj.type == 'stream'||obj.type ==
		 * 'route'||obj.type == 'event')
		 */
		var test = pCanvas.circle(x, y, 3);
		test.attr("stroke", "orange");
		test.toFront();
		if (obj.type != "Port") {
			//console.log("Ispointer inside: "+dojo.toJson(obj)+"  , x:  "+x+" ,y: "+y);
			//var test = pCanvas.circle(obj.normalizedx,obj.normalizedy,3);
			//test.attr("stroke","pink");
			var isInside = null;
			if (obj.type == "ceword") {
				isInside = isPointerInsideNode(obj, x, y, true, true);
			} else {
				isInside = isPointerInsideNode(obj, x, y, false, false);
			}
			if (isInside == true) {
				return obj;
			}
		}
		if(obj.type=="ShapeShape"){
			for(var j=0;j<obj.pts.length;j++){
				var pt = obj.pts[j];
				if(isPointInRect(x,y,pt.x-3,pt.y-3,pt.x+3,pt.y+3)){
					return pt;
				}
			}
		}
	}
	return null;
}
function findSelectedConnection(x, y) {
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'connection') {
			var links = obj.nodes;
			var node1 = findNodeById(links[0]);
			var node2 = findNodeById(links[1]);
			if (isPointOnLine(node1.normalizedx, node1.normalizedy,
					node2.normalizedx, node2.normalizedy, x, y)) {
				console.log("Connection is : " + obj.id);
			}
		}

	}
}

function findMaxXY() {
	if (pData.data.length < 1) {
		maxX = 0;
		maxY = 0;
		return;
	}
	maxX = pData.data[0].x;
	maxY = pData.data[0].y;
	for ( var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].x > maxX) {
			maxX = pData.data[i].x;
		}
		if (pData.data[i].y > maxY) {
			maxY = pData.data[i].y;
		}
	}
}

function normalizeData() {
	findMaxXY();
	if (maxX > screenWidth) {
		for ( var i = 0; i < pData.data.length; i++) {
			var obj = pData.data[i];
			if (obj.x != null)
				obj.normalizedx = Math.floor((obj.x * screenWidth) / maxX);
		}
	}
	if (maxY > screenHeight) {
		for ( var i = 0; i < pData.data.length; i++) {
			var obj = pData.data[i];
			if (obj.y != null)
				obj.normalizedy = Math.floor((obj.y * screenHeight) / maxY);
		}
	}
}

function initNodePropsTable() {
	nodeTypeName = document.getElementById("nodeTypeName");
	nodeName = document.getElementById("nodeName");
	nodeNameValue = document.getElementById("nodeNameValue");
	xCoordinateName = document.getElementById("xCoordinateName");
	xCoordinateValue = document.getElementById("xCoordinateValue");
	yCoordinateName = document.getElementById("yCoordinateName");
	yCoordinateValue = document.getElementById("yCoordinateValue");
	nodeWidthName = document.getElementById("nodeWidthName");
	nodeWidthValue = document.getElementById("nodeWidthValue");
	nodeHeightName = document.getElementById("nodeHeightName");
	nodeHeightValue = document.getElementById("nodeHeightValue");
}
function removeEventProperty(id, prop) {
	var evt = findNodeById(id);
	for ( var i in evt.props) {
		if (evt.props[i].name == prop) {
			if (confirm("Do you want to remove Property : \"" + prop + "\" ? ")) {
				delete evt.props[i];
				displaySelectedNodeProps(evt);
				return;
			}
		}
	}

}
function removeStreamDefinition(id, prop) {
	var stream = findNodeById(id);
	for ( var i in stream.defs) {
		if (stream.defs[i].name == prop) {
			if (confirm("Do you want to remove Property : \"" + prop + "\" ? ")) {
				delete stream.defs[i];
				displaySelectedNodeProps(stream);
				return;
			}
		}
	}

}

function displaySelectedNodeProps(obj) {

	if (obj != null) {
		var jtextarea = dojo.byId("jsonval");
		var str = "";
		for ( var i in obj) {
			if (i !== "x" && i != "y" && i != "r" && i != "b"
					&& i != "normalizedy" && i != "normalizedx" && i != "seda") {
				if (i == "type") {
					var ele = dojo.byId("ndtype");
					ele.innerHTML = (obj[i].capitalize() + " Properties");
				} else {
					if (i != "defs" && i != "props")
						str += ("<b>" + i
								+ "&nbsp;&nbsp;:&nbsp;&nbsp;</b>&nbsp;&nbsp;"
								+ obj[i] + "<br/>");
				}
			}
		}
		if (obj.type == "stream") {
			str += ("<br/><u>Stream Definition</u><br/>");
			var defs = obj.defs;
			for ( var i in defs) {
				var objinner = defs[i];
				for ( var j in objinner) {
					str += ("<b>&nbsp;&nbsp;&nbsp;&nbsp;" + j
							+ "&nbsp;&nbsp;:&nbsp;&nbsp;</b>&nbsp;&nbsp;"
							+ objinner[j] + "<br/>");

				}
				str += ("<button type=\"button\" onclick=\"removeStreamDefinition('"
						+ obj.id + "','" + objinner.name + "');\" value=\"remove\">remove</button><br/>");
			}

		}
		if (obj.type == "event") {
			str += ("<br /><u>Event Properties</u><br/>");
			var props = obj.props;
			for ( var i in props) {
				var objinner = props[i];
				for ( var j in objinner) {
					if (j != "searchable") {
						str += ("<b>&nbsp;&nbsp;&nbsp;&nbsp;" + j
								+ "&nbsp;&nbsp;:&nbsp;&nbsp;</b>&nbsp;&nbsp;"
								+ objinner[j] + "<br/>");
					}
				}
				str += ("<button type=\"button\" onclick=\"removeEventProperty('"
						+ obj.id + "','" + objinner.name + "');\" value=\"remove\">remove</button><br/>");
			}
		}
		jtextarea.innerHTML = str;// dojo.toJson(obj);
		if (obj.type == "node") {
			updateProcConfig();
		} else if (obj.type == "AnonDef") {
			updateConfigDialog();
		}

	}
}

var isGraphClosed = false;

function isPointerInsideNode(node, x, y, isText, startAnchor) {

	var x1 = null;
	var x2 = null;
	var y1 = null;
	var y2 = null;
	if (isText && startAnchor) {
		x1 = node.normalizedx;
		y1 = node.normalizedy;
		x2 = node.normalizedx + node.r;
		y2 = node.normalizedy + node.b;
		//pCanvas.rect(x1,y1,node.r,node.b);
	} else {
		x1 = node.normalizedx - node.r / 2;
		y1 = node.normalizedy - node.b / 2;
		x2 = node.normalizedx + node.r / 2;
		y2 = node.normalizedy + node.b / 2;
		//pCanvas.rect(x1,y1,node.r,node.b);
	}
	return isPointInRect(x, y, x1, y1, x2, y2);
}
/*
 * polygon is parallel to x axis!!
 * 
 * */
function isPointInRect(mousePosX, mousePosY, x1, y1, x2, y2) {
	if (mousePosX >= x1 && mousePosX <= x2 && mousePosY >= y1
			&& mousePosY <= y2) {
		return true;
	}
	return false;
}
function removeDrawElement(drawElement) {
	if (drawElement.item != null)
		drawElement.item.remove();

	if (drawElement.textnode != null) {
		drawElement.textnode.remove();
	}

	if (drawElement.subs != null) {
		for ( var j = 0; j < drawElement.subs.length; j++) {
			drawElement.subs[j].remove();
		}
	}

	if (drawElement.img != null)
		drawElement.img.remove();

}

function myClearCanvas() {
	try{
		for ( var i = 0; i < drawElements.length; i++) {
	
		if (drawElements[i].item != null) {
			drawElements[i].item.remove();
		}

		if (drawElements[i].textnode != null)
			drawElements[i].textnode.remove();
		if (drawElements[i].subs != null) {
			for ( var j = 0; j < drawElements[i].subs.length; j++) {
				drawElements[i].subs[j].remove();
			}
		}
		if (drawElements[i].img != null)
			drawElements[i].img.remove();
	}
	}catch (e) {
		console.log("failed to remove");
	}
	drawElements = new Array();
	// pCanvas.clear();
}
function lightOff(opa, acceptFun, attr) {
	if (opa == null)
		opa = .1

	if (attr == null)
		attr = {
			"opacity" : opa
		};

	for ( var i = 0; i < drawElements.length; i++) {
		if ((acceptFun == null)
				|| (acceptFun != null && (attr = acceptFun(drawElements[i])) != null)) {

			if (drawElements[i].item != null)
				drawElements[i].item.attr(attr);

			if (drawElements[i].textnode != null)
				drawElements[i].textnode.attr(attr);

			if (drawElements[i].subs != null) {
				for ( var j = 0; j < drawElements[i].subs.length; j++) {
					drawElements[i].subs[j].attr(attr);
				}
			}
			if (drawElements[i].img != null)
				drawElements[i].img.attr(attr);
		}
	}
}
function lightOn() {
	for ( var i = 0; i < drawElements.length; i++) {
		if (drawElements[i].item != null)
			drawElements[i].item.attr("opacity", 1);

		if (drawElements[i].textnode != null)
			drawElements[i].textnode.attr("opacity", 1);

		if (drawElements[i].subs != null) {
			for ( var j = 0; j < drawElements[i].subs.length; j++) {
				drawElements[i].subs[j].attr("opacity", 1);
			}
		}
		if (drawElements[i].img != null)
			drawElements[i].img.attr("opacity", 1);
	}

}
var filter = {};
function issubgroup(id) {
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'group') {
			for ( var j = 0; j < obj.items.length; j++) {
				var item = obj.items[j];
				if (id == item)
					return obj;
			}
		}
	}
	return null;
}
function cleangclist() {
	for ( var i in gclist) {
		var item = gclist[i];
		if (sliderMap[item.id] != null) {
			sliderMap[item.id] = null;
		}
		dojo.destroy(item);
	}
	gclist = {};

}
function draw(){
	applyDrawConfig();
	shapeElementsDrawn = {};
	console.log("Draw");
	myClearCanvas();
	pCanvas.clear();
	//draw custom shapes that are part of this page
	//shapes are stored in compdiag.
	applyShapes();
	if(wideconfig.global_canvas==null ||wideconfig.global_canvas!="true"){
		return;
	}
	cleangclist();
	for ( var postfun in preUpdateFun) {
		preUpdateFun[postfun]();
	}
	applyWideConfig(null);
	compDiag2 = new Array();
	filter = {};
	prepareSequence();
	var w = 40;
	var h = 40
	sortSteps();
	for ( var i = 0; i < pData.data.length; i++) {/* fist draw group items */
		var obj = pData.data[i];
		if (obj.type == 'group') {
			if (obj.closed != true) {
				continue;
			}
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_YELLOW;
			var fillColor = COLOR_YELLOW;
			var objp = issubgroup(obj.id);
			if (objp == null || objp.closed == false) {
				if (layeringEnabled && !layerEnabled(obj.layer)) {
					continue;
				} else {
					var fun = nodeDrawingHandler[obj.type];
					if (fun != null) {
						fun(xpos, ypos, 48, 48, fillColor, borderColor, obj,
								null);
					} else {
						drawGroupRect(xpos, ypos, 48, 48, fillColor,
								borderColor, obj);
					}
				}
			}
			for ( var j = 0; j < obj.items.length; j++) {
				var item = obj.items[j];
				filter[item] = obj;
			}

		}
	}
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (filter[obj.id] != null)
			continue;
		if (layeringEnabled && !layerEnabled(obj.layer)) {
			continue;
		}
		if (obj.type == 'node') {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_BLUE;
			var fillColor = COLOR_BLUE;
			w = obj.r;
			h = obj.b;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, w, h, fillColor, borderColor, obj, null);
			} else {
				drawNodeRect(xpos, ypos, w, h, fillColor, borderColor, obj);
			}
		} else if (obj.type == 'stream') {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_GREEN;
			var fillColor = COLOR_GREEN;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, width, height, fillColor, borderColor, obj,
						null);
			} else {
				drawStreamRect(xpos, ypos, 48, 48, fillColor, borderColor,
						obj.id);
			}
		} else if (obj.type == 'event') {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_YELLOW;
			var fillColor = COLOR_YELLOW;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, 48, 48, fillColor, borderColor, obj, null);
			} else {
				drawEventRect(xpos, ypos, 48, 48, fillColor, borderColor,
						obj.id);
			}
		} else if (obj.type == 'route') {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_RED;
			var fillColor = COLOR_RED;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, 48, 48, fillColor, borderColor, obj, null);
			} else {
				drawRouteRect(xpos, ypos, 48, 48, fillColor, borderColor,
						obj.id);
			}
		} else if (obj.type == 'textnode') {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_RED;
			var fillColor = COLOR_RED;
			if (maxdummy <= obj.id) {
				maxdummy = (obj.id + 1);
			}
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, 48, 48, fillColor, borderColor, obj, null);
			} else {
				drawTextNode(xpos, ypos, fillColor, borderColor, obj);
			}
		} else if (obj.type == 'region') {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_BLUE;
			var fillColor = COLOR_BLUE;
			w = obj.r;
			h = obj.b;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, w, h, fillColor, borderColor, obj, null);
			} else {
				drawRegionRect(xpos, ypos, w, h, fillColor, borderColor, obj);
			}
		} else if (obj.type == 'module') {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_BLUE;
			var fillColor = COLOR_BLUE;
			w = obj.r;
			h = obj.b;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, w, h, fillColor, borderColor, obj, null);
			} else {
				drawModuleNode(xpos, ypos, w, h, fillColor, borderColor, obj);
			}
		}else if(obj.type=="ShapeShape"){
			currShapeRenderer.renderShape(obj);	
		}else if(obj.type=="myanim"){
			renderMyAnim(obj);
		} else if (genericnodes[obj.type] != null) {
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_RED;
			var fillColor = COLOR_RED;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, 48, 48, fillColor, borderColor, obj, obj.icon);
			} else {
				drawGenericNode(xpos, ypos, 48, 48, fillColor, borderColor,
						obj, genericnodes[obj.type]);
			}
		} else if (obj.icon != null && obj.type != "group") {
			genericnodes[obj.type] = obj.icon;
			var xpos = obj.normalizedx;
			var ypos = obj.normalizedy;
			var borderColor = COLOR_RED;
			var fillColor = COLOR_RED;
			var fun = nodeDrawingHandler[obj.type];
			if (fun != null) {
				fun(xpos, ypos, 48, 48, fillColor, borderColor, obj, obj.icon);
			} else {
				drawGroupNode(xpos, ypos, 48, 48, fillColor, borderColor, obj,
						genericnodes[obj.type]);
			}
		}
	}
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (filter[obj.id] != null)
			continue;
		if (layeringEnabled && !layerEnabled(obj.layer)) {
			continue;
		}
		if (obj.type == 'connection') {
			if ((filter[obj.from] == null) && (filter[obj.to] == null)) {
				var fun = nodeDrawingHandler[obj.type];
				if (fun != null) {
					fun(0, 0, w, h, null, null, obj, null);
				} else {
					connectLinks(obj);
				}
			}
		}
	}
	if (showStreamToNode == true) {
		var fun = nodeDrawingHandler["streamtonodeconn"];
		if (fun != null) {
			fun(0, 0, 0, 0, null, null, null);
		} else {
			drawStreamToNodeConn();
		}
	}
	var fun = nodeDrawingHandler["streamconn"];
	if (fun != null) {
		fun(0, 0, 0, 0, null, null, null);
	} else {
		drawStreamConnections();
	}
	var fun = nodeDrawingHandler["groupconn"];
	if (fun != null) {
		fun(0, 0, 0, 0, null, null, null);
	} else {
		drawGroupConnectors();
	}
	for ( var postfun in postUpdateFun) {
		postUpdateFun[postfun]();
	}
	if (doLayout) {
		getLayout();
	}
	
}

function drawGroupConnectors() {
}
function redrawConn() {
	for ( var i = 0; i < drawElements.length; i++) {
		for ( var j = 0; j < drawElements[i].subs.length; j++)
			drawElements[i].subs[j].remove();
	}

	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'connection') {
			connectLinks(obj);
		}
		drawStreamToNodeConn();
		drawStreamConnections();
	}
}
function drawStreamToNodeConn() {
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'stream') {
			var node1 = obj;
			if (filter[obj.id] == null)
				drawStreamToNode(obj);
		}
	}
}
function drawStreamConnections() {
	for ( var i = 0; i < pData.data.length; i++) {
		var obj = pData.data[i];
		if (obj.type == 'stream' || obj.type == 'flownode') {
			var node1 = obj;
			if (filter[obj.id] == null) {
				drawStreamConnect(obj);
				drawStreamExceptionConnect(obj);
			}
		}
	}
}
function drawStreamExceptionConnect(obj) {
	var node1 = obj;
	var nodes = new Array();
	var drnode1 = findDrawEleById(obj.id);
	var comps = drnode1.subs;
	if (obj.exceptionStream != null) {
		if (obj.exceptionStream.indexOf(":") > -1) {
			nodes = obj.exceptionStream.split(":");
		} else {
			nodes.push(obj.exceptionStream);
		}
	}
	for ( var i = 0; i < nodes.length; i++) {
		var node2 = findNodeById(nodes[i]);
		if (node2 != null) {
			if (node1.normalizedx < node2.normalizedx) {
				var txtnd = drawText(
						(node1.normalizedx + node2.normalizedx) / 2,
						(node1.normalizedy + node2.normalizedy) / 2, 0, 0, "");
				arrowExp(node1.normalizedx + node1.r / 2, node1.normalizedy,
						node2.normalizedx - node2.r / 2, node2.normalizedy,
						comps);
				comps.push(txtnd);
			} else {
				var txtnd = drawText(
						(node1.normalizedx + node2.normalizedx) / 2,
						(node1.normalizedy + node2.normalizedy) / 2, 0, 0, "");
				arrowExp(node1.normalizedx - node1.r / 2, node1.normalizedy,
						node2.normalizedx + node2.r / 2, node2.normalizedy,
						comps);
				comps.push(txtnd);
			}

		}
	}

}
function drawStreamConnect(obj) {
	var node1 = obj;
	var nodes = new Array();
	var drnode1 = findDrawEleById(obj.id);
	var comps = drnode1.subs;
	if (obj.extra != null) {
		if (obj.extra.indexOf(":") > -1) {
			nodes = obj.extra.split(":");
		} else {
			nodes.push(obj.extra);
		}
	}
	for ( var i = 0; i < nodes.length; i++) {
		var node2 = findNodeById(nodes[i]);
		if (node2 != null) {
			if (node1.normalizedx < node2.normalizedx) {
				var txtnd = drawText(
						(node1.normalizedx + node2.normalizedx) / 2,
						(node1.normalizedy + node2.normalizedy) / 2, 0, 0, "");
				arrow(node1.normalizedx, node1.normalizedy, node2.normalizedx,
						node2.normalizedy, comps);
				comps.push(txtnd);
			} else {
				var txtnd = drawText(
						(node1.normalizedx + node2.normalizedx) / 2,
						(node1.normalizedy + node2.normalizedy) / 2, 0, 0, "");
				arrow(node1.normalizedx, node1.normalizedy, node2.normalizedx,
						node2.normalizedy, comps);
				comps.push(txtnd);
			}

		}
	}

}
function drawStreamToNode(obj) {
	var node1 = obj;
	var node2 = findNodeById(obj.processor);
	if (node2 != null) {
		if (filter[node2.id] != null) {
			node2 = filter[node2.id];
		}
		var nditem = findDrawEleById(obj.id);

		var ln = null;
		if (node1.normalizedy < node2.normalizedy)
			ln = pCanvas.line(node1.normalizedx, node1.normalizedy + node1.b
					/ 2, node2.normalizedx, node2.normalizedy - node2.b / 2);
		else
			ln = pCanvas.line(node2.normalizedx, node2.normalizedy + node2.b
					/ 2, node1.normalizedx, node1.normalizedy - node1.b / 2);

		ln.attr({
			"stroke" : "red",
			"fill-opacity" : .4,
			"opacity" : .4,
			"stroke-width" : 1,
			"stroke-dasharray" : "."
		});

		nditem.subs.push(ln);

		if (node1.normalizedy < node2.normalizedy) {
			var el1 = pCanvas.ball(node1.normalizedx, node1.normalizedy
					+ node1.b / 2, 4, streamballhue);// pCanvas.ellipse(node1.normalizedx+node1.r/2,node1.normalizedy,4,4);
			nditem.subs.push(el1);
			// el1.attr({"fill-opacity": .2,"fill":"red","stroke":
			// "red","opacity":.2});
			el1 = pCanvas.ball(node2.normalizedx, node2.normalizedy - node2.r
					/ 2, 4, streamballhue);// pCanvas.ellipse(node2.normalizedx-node2.r/2,node2.normalizedy,4,4);
			nditem.subs.push(el1);
			// el1.attr({"fill-opacity": .2,"fill":"red","stroke":
			// "red","opacity":.2});

		} else {
			var el1 = pCanvas.ball(node1.normalizedx, node1.normalizedy
					- node1.b / 2, 4, streamballhue);// pCanvas.ellipse(node1.normalizedx-node1.r/2,node1.normalizedy,4,4);
			nditem.subs.push(el1);
			// el1.attr({"fill-opacity": .2,"fill":"red","stroke":
			// "red","opacity":.2});

			var el1 = pCanvas.ball(node2.normalizedx, node2.normalizedy
					+ node2.b / 2, 4, streamballhue);// pCanvas.ellipse(node2.normalizedx+node2.r/2,node2.normalizedy,4,4);
			nditem.subs.push(el1);
			// el1.attr({"fill-opacity": .2,"fill":"red","stroke":
			// "red","opacity":.2});

		}
	}
}

function connectLinks(connection) {
	if(connection.visible !=null && connection.visible==false){
		return;
	}
	var links = connection.nodes;
	var node1 = findNodeById(links[0]);
	var node2 = findNodeById(links[1]);
	var pt1 = null;
	var pt2 = null;

	var txtnd = null;
	var connline = null;
	var comps = new Array();
	var connLabel = getConnectionLabel(connection.connCond);
	if (connection.shape != null) {
		if (!smoothen) {
			var a = connection.shape;

			if (a.color == null)
				a.color = "purple";
			var pts = dojo.fromJson(a.pts);

			var pathString = getPathFromPointsArray(pts);
			var shapestr = "{var shape=pCanvas.path(\"" + pathString + "\");\n";
			shapestr += "shape.attr(\"stroke\",\"" + a.color + "\");";
			shapestr += "return shape;}"
			if (connEdit) {
				for ( var pt in pts) {
					var c = drawPoint(pts[pt], a.id);
					globals[c.node.eleid] = c;
				}
			}
			a.design = shapestr;
			var fun = (new Function(shapestr));
			var shape = fun();
			if (shape != null) {
				shape.node.setAttribute("eleid", connection.id);
				shape.attr("stroke", a.color);
				shape.attr("stroke-width", 2);
				var len = shape.getTotalLength();
				var point = shape.getPointAtLength(len / 2);
				var x = point.x;
				var y = point.y;
				// console.log("lengeth :" +len+" pt : "+dojo.toJson(point));
				var mytext = pCanvas.text(x, y - 10, connection.id);
				comps.push(mytext);
				shape.toBack();
				shape.mouseover(function(e) {
					var myid = shape.node.getAttribute("eleid");
					this.attr({
						"stroke-width" : 4
					});
					lastSelectedConn = findConnById(myid);
					mouseOverConn = findConnById(myid);
				});
				shape.mouseout(function(e) {
					this.attr({
						"stroke-width" : 2
					});
					mouseOverConn = null;
					// lastSelectedConn = null;
				});
				shape.node.onclick = function() {
					var myid = shape.node.getAttribute("eleid");
					var conn = findConnById(myid);
					displaySelectedNodeProps(conn);
					var evtdata = {};
					evtdata.id = myid;
					evtdata.type = "up";
					sendEvent(geq, [ evtdata ]);

				};
				comps.push(shape);
			}
		} else {/*smoothen*/
			var a = connection.shape;
			var pts = dojo.fromJson(a.pts);
			var count = 0;
			var lastel = null;
			var ndd1 = null;
			for ( var el in pts) {
				if (count == 0)
					pt1 = pts[el];
				count++;
				lastel = el;
			}
			pt2 = pts[lastel];

			var links = connection.nodes;
			if (links != null && links.length > 0) {
				var lnode1 = findNodeById(links[0]);
				var lnode2 = findNodeById(links[1]);
				if (lnode1 != null) {
					pt1.x = lnode1.normalizedx;
					pt1.y = lnode1.normalizedy;
				}
				if (lnode2 != null) {
					pt2.x = lnode2.normalizedx;
					pt2.y = lnode2.normalizedy;
				}
			}
			var bb1 = {};
			var bb2 = {};
			bb1.x = pt1.x;
			bb1.y = pt1.y;
			bb1.width = 5;
			bb1.height = 5;
			bb2.x = pt2.x;
			bb2.y = pt2.y;
			bb2.width = 5;
			bb2.height = 5;
			//override for step
			if (node1.type == "Step" && node2.type != "Step" && pt2 != null) {
				bb1.y = pt2.y;
			}

			//override for step
			if (node2.type == "Step" && pt1 != null) {
				bb2.y = pt1.y;
			}

			arrowLink2(bb1, bb2, comps, connection.id);
			if (debug) {//debug start
				var pathString = getPathFromPointsArray(pts);
				var shapestr = "{var shape=pCanvas.path(\"" + pathString
						+ "\");\n";
				shapestr += "shape.attr(\"stroke\",\"" + a.color + "\");";
				shapestr += "return shape;}"
				if (connEdit) {
					for ( var pt in pts) {
						var c = drawPoint(pts[pt], a.id);
						globals[c.node.eleid] = c;
					}
				}
				a.design = shapestr;
				var fun = (new Function(shapestr));
				var shape = fun();

			}//debug end

		}

	} else {
		if (node1 != null && node2 != null) {
			/*
			 * if(node1.normalizedx < node2.normalizedx){ var ndd1 =
			 * findDrawEleById(node1.id); var ndd2 =
			 * findDrawEleById(node2.id); if(ndd1!=null&&ndd2!=null){ txtnd =
			 * drawText((node1.normalizedx+node2.normalizedx)/2,(node1.normalizedy+node2.normalizedy)/2,0,0,connLabel);
			 * arrowLink2(ndd1.item.getBBox(),ndd2.item.getBBox(),comps,connection.id); }
			 * }else
			 */{
				var ndd1 = findDrawEleById(node1.id);
				var ndd2 = findDrawEleById(node2.id);
				var sx = null;
				var sy = null;
				var sh = null;
				var sw = null;
				var sx2 = null;
				var sy2 = null;
				var sh2 = null;
				var sw2 = null;
				var poi1 = null;
				var poi2 = null;
				if (ndd1 == null) {
					if(node1.type=="module"){
						var m = getRealPosFunc(node1.id);
				        if(m!=null/* && m.rotation>0*/){
					        var k = m.rotation;
					        var cx = m.x, cy=m.y;
	 			            var pts = getPoi(m);
				            poi1 = new Array();
				            for(var l=0;l<pts.length;l++){
				              var p = rotatePointDeg(pts[l],cx,cy,k);
				              if(!(l%2==0)){
				                poi1.push(p)
				              }
				            }
				        }else{
							sx = node1.normalizedx;
							sy = node1.normalizedy;
							sh = node1.b;
							sw = node1.r;
							sx = sx - node1.r/2;
							sy = sy - node1.b/2;
				        }
					}else{
						sx = node1.normalizedx;
						sy = node1.normalizedy;
						sh = node1.b;
						sw = node1.r;
						sx = sx - node1.r/2;
						sy = sy - node1.b/2;
			        }
				} else {
					if(node1.type=="Port"){
						var pos = dojo.position(ndd1.item.node);
						pos.x+=scrollLeft();
						if(pos.x>5 &&pos.y>5){
						sx = pos.x+2;
						sy= pos.y -topWidth+2;
						sh = ndd1.item.getBBox().height-2;
						sw =ndd1.item.getBBox().width-2;
						}else{
							sx = ndd1.item.getBBox().x;
							sy = ndd1.item.getBBox().y;
							sh = ndd1.item.getBBox().height;
							sw = ndd1.item.getBBox().width;
						}
					}else{
					sx = ndd1.item.getBBox().x;
					sy = ndd1.item.getBBox().y;
					sh = ndd1.item.getBBox().height;
					sw = ndd1.item.getBBox().width;
					}
				}

				txtnd = drawText((node1.normalizedx + node2.normalizedx) / 2,
						(node1.normalizedy + node2.normalizedy) / 2, 0, 0,
						connLabel);
				if (ndd2 == null) {
					if(node2.type=="module"){
						var m = getRealPosFunc(node2.id);
				        if(m!=null /*&& m.rotation>0*/){
					        var k = m.rotation;
					        var cx = m.x, cy=m.y;
	 			            var pts = getPoi(m);
				            poi2 = new Array();
				            for(var l=0;l<pts.length;l++){
				              var p = rotatePointDeg(pts[l],cx,cy,k);
				              if(!(l%2==0)){
					                poi2.push(p)
					              }
				            }
				        }else{
				        	sx2 = node2.normalizedx;
							sy2 = node2.normalizedy;
							sh2 = node2.b;
							sw2 = node2.r;
							sx2 = sx2 - node2.r/2;
							sy2 = sy2 - node2.b/2;
				        }							
					}else{
			        	sx2 = node2.normalizedx;
						sy2 = node2.normalizedy;
						sh2 = node2.b;
						sw2 = node2.r;
						sx2 = sx2 - node2.r/2;
						sy2 = sy2 - node2.b/2;
			        }
				} else {
					if(node2.type=="Port"){
						var pos = dojo.position(ndd2.item.node);
						pos.x+=scrollLeft();
						if(pos.x>5 &&pos.y>5){
						sx2 = pos.x+2;
						sy2= pos.y -topWidth+2;
						sh2 = ndd2.item.getBBox().height-2;
						sw2 =ndd2.item.getBBox().width-2;
						}else{
							sx2 = ndd2.item.getBBox().x;
							sy2 = ndd2.item.getBBox().y;
							sh2 = ndd2.item.getBBox().height;
							sw2 = ndd2.item.getBBox().width;
						}
					}else{
					sx2 = ndd2.item.getBBox().x;
					sy2 = ndd2.item.getBBox().y;
					sh2 = ndd2.item.getBBox().height;
					sw2 = ndd2.item.getBBox().width;
					}
				}

				var bb1 = {};
				var bb2 = {};
				if(poi1!=null && poi2==null){
					var pt = findnearItemColl(sx2,sy2,poi1);
					bb1.x = pt.x;
					bb1.y = pt.y;
					bb1.width = 1;
					bb1.height = 1;
					bb2.x = sx2;
					bb2.y = sy2;
					bb2.width = sw2;
					bb2.height = sh2;
				}else if (poi1==null && poi2!=null){
					var pt = findnearItemColl(sx,sy,poi2);
					bb1.x = sx;
					bb1.y = sy;
					bb1.width = sw;
					bb1.height = sh;
					bb2.x = pt.x;
					bb2.y = pt.y;
					bb2.width = 1;
					bb2.height = 1;
				}else if (poi1!=null && poi2!=null){
					var pt = findnearItemCollColl(poi1,poi2);
					bb1.x = pt.first.x;
					bb1.y = pt.first.y;
					bb1.width = 1;
					bb1.height = 1;
					bb2.x = pt.second.x;
					bb2.y = pt.second.y;
					bb2.width = 1;
					bb2.height = 1;
				}else{
					bb1.x = sx;
					bb1.y = sy;
					bb1.width = sw;
					bb1.height = sh;
					bb2.x = sx2;
					bb2.y = sy2;
					bb2.width = sw2;
					bb2.height = sh2;
				}
				var isport1 = false,isport2=false;
				if((node1!=null&&node1.type=="Port"))
					isport1=true;
				if((node2!=null&&node2.type=="Port"))
					isport2 =  true;
				if(node1!=null){
					bb1.nd = node1;
				}
				if(node2!=null){
					bb2.nd = node2;
				}

				/*if(isport1||isport2){
					arrowLinkPort(bb1, bb2, comps, connection.id,isport1,isport2);
				}else*/
				{
					arrowLink2(bb1, bb2, comps, connection.id);
				}
				
			}
		}
	}
	var connection = {
		"item" : null,
		"textnode" : txtnd,
		"subs" : comps,
		"id" : connection.id
	};
	drawElements.push(connection);
	if (connDrawingHandler != null) {/* for any customization of the connection */
		connDrawingHandler(connection);
	}
}
function arrowLine(x1, y1, x2, y2) {
	return pCanvas.line(x1, y1, x2, y2);
}
function lightarrow(x1, y1, x2, y2, subs, id) {
	var a = pCanvas.line(x1, y1, x2, y2);
	a.node.setAttribute("eleid", id);
	a.attr({
		"opacity" : .3,
		"stroke-dasharray" : "-"
	});
	a.toBack();
	subs.push(a);
}
function arrowLink(x1, y1, x2, y2, subs, id) {
	var a = pCanvas.lightarrow(x1, y1, x2, y2, 8);
	a[0].node.setAttribute("eleid", id);
	a[0].attr("stroke-width", "2");
	a[0].attr("stroke", "orange");
	a[1].attr("stroke", "orange");
	a[1].attr("stroke-dasharray", "-");

	a[0].mouseover(function(e) {
		var myid = a[0].node.getAttribute("eleid");
		this.attr({
			"stroke-width" : 3
		});
		lastSelectedConn = findConnById(myid);
	});
	a[0].mouseout(function(e) {
		this.attr({
			"stroke-width" : 2
		});
		// lastSelectedConn = null;
	});
	a[0].node.onclick = function() {
		var myid = a[0].node.getAttribute("eleid");
		var conn = findConnById(myid);
		displaySelectedNodeProps(conn);
		var evtdata = {};
		evtdata.id = myid;
		evtdata.type = "click";
		sendEvent(geq, [ evtdata ]);
		lastSelectedConn = conn;
	};
	a[0].node.ondblclick = function() {
		var myid = a[0].node.getAttribute("eleid");
		var conn = findConnById(myid);
		displaySelectedNodeProps(conn);
		var evtdata = {};
		evtdata.id = myid;
		evtdata.type = "dblclick";
		sendEvent(geq, [ evtdata ]);
		lastSelectedConn = conn;
		showUpdateConnDialogAtMouse();

	};
	subs.push(a[0], a[1]);
}
/*
 * this uses quadratic curve
 */
var oldConnRubber = null;
var useStraight1 = false;
var useQuad1 = true;
var mouseOverTrigger = null;
var mousePopup = null;
function removeItemTag(from, what) {
	console.log("removing " + from + "/" + what);
	var node = findNodeById(from);
	if (node != null) {
		var tags = node.tags;
		if (tags != null) {
			var rem = removeItemFromArray(tags, what);
			if (rem != null) {
				console.log("removed tag: " + what + " from " + from);
				mousePopup.setContent(getPopupContent());
				return;
			}
		}
	}
	console.log("could not tag: " + what + " from " + from);
}

function getPopupContent() {
	var images = "";
	var lastConn = mouseOverConn;
	if (lastConn != null) {
		if (lastConn.tags != null) {
			for ( var i = 0; i < lastConn.tags.length; i++) {
				var str = lastConn.tags[i];
				var aspec = annotSpecs[str];
				if (aspec != null) {
					images += ("<image src='"
							+ aspec.icon
							+ "'  style='width: 16px; height: 16px;' onclick='removeItemTag(\""
							+ lastConn.id + "\",\"" + str + "\"" + ")'/>");
				}
			}
		}
	}
	return images
			+ "&nbsp;&nbsp;&nbsp;<button onclick='dijit.popup.close(mousePopup);' style='width: 16px; height: 16px;' value='x'></button>";
}
function showConnHints() {
	console.log("show menu");
	clearTimeout(mouseOverTrigger);
	if (mousePopup == null) {
		mousePopup = new dijit.TooltipDialog({
			content : getPopupContent()
		});
		//		var cls = "popupmenucls";
		//	mousePopup = getSimpleStaticModule("mouseOverHints", cls, mouseX, mouseY, 100, 40, "this is hint window", zindex++);
	}
	//default is {BL:'TL', TL:'BL'}
	mousePopup.setContent(getPopupContent());
	dijit.popup.open({
		popup : mousePopup,
		x : mouseX - 10,
		y : mouseY + 36,
		orient : {
			BR : 'BL',
			BL : 'BR'
		}
	});

	//dojo.style(mousePopup,{"left":mouseX+"px","top":mouseY+"px"});
	//showdiv("mouseOverHints");	
}

function arrowLinkPort(bb1, bb2, subs, id,isport1,isport2) {
	var x1 = bb1.x, y1 = bb1.y+bb1.height/2, x2 = bb2.x, y2 = bb2.y+bb2.height/2;
	var a = null;
		var mid1 = {
				x:x1,
				y:y1
		};
		if(isport1){
		if(bb1.x<bb2.x){
			mid1.x = x1-40;
		}else{
			mid1.x = x1+40;
			x1=bb1.x+bb1.width;
		}
		}
		if(isport1){
		if(bb1.y<bb2.y){
			mid1.y = y1-80;
		}else{
			mid1.y = y1+80;
		}
		}
		var mid2 = {
			x:x2,
			y:y2
		};
		if(isport2){
		if(bb2.x<bb1.x){
			mid2.x = x2-40;
		}else{
			if(isport2)
			mid2.x = x2+40;
			x2=bb2.x+bb2.width;
		}
		}
		if(isport2){
		if(bb2.y<bb1.y){
			mid2.y = y2-80;
		}else{
			mid2.y = y2+80;
		}
		}
		a = new Array();
		var size = 8;
		var angle = Math.atan2(mid2.x - x2, y2 - mid2.y);
		angle = (angle / (2 * Math.PI)) * 360;
		var pathStr = "M " + x1 + " " + y1 + " C " + mid1.x + " "
		+ mid1.y + " " + mid2.x + " " + mid2.y + " " + x2 + " "
		+ y2;
		console.log("Path: "+pathStr);
		a[0] = pCanvas.path(pathStr);
		a[1] = pCanvas.path(
				"M" + x2 + " " + y2 + " L" + (x2 - size) + " " + (y2 - size)
						+ " L" + (x2 - size) + " " + (y2 + size) + " L" + x2
						+ " " + y2).attr("fill", "black").rotate((90 + angle),
				x2, y2);
	a[0].node.setAttribute("eleid", id);
	a[0].attr("stroke-width", "4");
	a[0].attr("stroke", "orange");
	a[1].attr("stroke", "orange");
	a[1].attr("fill", "orange");
	a[1].attr("stroke-dasharray", "-");

	a[0].mouseover(function(e) {
		a[0].node.setAttribute("clr",this.attr("stroke"));
		Raphael.getColor();
		this.attr("stroke",Raphael.getColor());
		var myid = a[0].node.getAttribute("eleid");
		var wi = this.attr("stroke-width");
		wi = parseInt(wi)
		this.attr({
			"stroke-width" : (wi + 2) 
		});
		lastSelectedConn = findConnById(myid);
		mouseOverConn = findConnById(myid);
		if (shift) {
			console.log("setting mouseover timeout");
			mouseOverTrigger = setTimeout(showConnHints, 500);
		}
	});
	a[0].mouseout(function(e) {
		var clr = a[0].node.getAttribute("clr");
		this.attr("stroke",clr);
		console.log("clearing mouseover timeout");
		if (mouseOverTrigger != null) {
			clearTimeout(mouseOverTrigger);
		}
		var wi = this.attr("stroke-width");
		wi = parseInt(wi)
		this.attr({
			"stroke-width" : (wi - 2)
		});
		wi = this.attr("stroke-width");
		mouseOverConn = null;
		// lastSelectedConn = null;
	});
	a[0].click(function(evt) {
		handleConnClick(this)
	});
	a[0].dblclick(function() {
		var myid = this.node.getAttribute("eleid");
		var conn = findConnById(myid);
		displaySelectedNodeProps(conn);
		var evtdata = {};
		evtdata.id = myid;
		evtdata.type = "dblclick";
		sendEvent(geq, [ evtdata ]);
		lastSelectedConn = conn;

		showUpdateConnDialogAtMouse();

	});
	subs.push(a[0]);
	subs.push(a[1]);
}
function arrowLink2(bb1, bb2, subs, id) {
	var x1 = bb1.x, y1 = bb1.y, x2 = bb2.x, y2 = bb2.y;
	var a = null;
	if (useStraight1) {
		var d = Math.floor((Math.random()*20)+1)+Math.floor((Math.random()*20)+1);
		x1 = bb1.x+bb1.width/2, y1 = bb1.y+bb1.height/2, x2 = bb2.x+bb2.width/2, y2 = bb2.y+bb2.height/2;
		var mid1 = {};
		var mid2 = {};
		var mid3 = {};
		var mid4 = {};
		mid1.x = ((x1 + x2) / 2) + d;
		mid1.y = y1;
		mid2.x = ((x1 + x2) / 2) + d;
		mid2.y = y2;
		mid3.x = ((x1 + x2) / 2) + d;
		mid3.y = y2;
		mid4.x = ((x1 + x2) / 2) + d;
		mid4.y = y2;

		if(bb1.nd!=null){
		if(bb1.nd.id==bb1.nd.grp+".0"||bb1.nd.id==bb1.nd.grp+".1"||bb1.nd.id==bb1.nd.grp+".2"){
				mid1.x = x1 ;
				mid1.y = y1-40;
			}else if(bb1.nd.id==bb1.nd.grp+".4"||bb1.nd.id==bb1.nd.grp+".5"||bb1.nd.id==bb1.nd.grp+".6"){
				mid1.x = x1 ;
				mid1.y = y1+40;
			}
		}
		if(bb2.nd!=null){
			if(bb2.nd.id==bb2.nd.grp+".0"||bb2.nd.id==bb2.nd.grp+".1"||bb2.nd.id==bb2.nd.grp+".2"){
					mid2.x = x2 ;
					mid2.y = y2-40;
				}else if(bb2.nd.id==bb2.nd.grp+".4"||bb2.nd.id==bb2.nd.grp+".5"||bb2.nd.id==bb2.nd.grp+".6"){
					mid2.x = x2 ;
					mid2.y = mid1.y;
				}
			}
		var angle = Math.atan2(mid2.x - x2, y2 - mid2.y);
		angle = (angle / (2 * Math.PI)) * 360;
		a = new Array();
		var size = 8;
		a[0] = pCanvas.path("M " + x1 + " " + y1 + " L " + mid1.x + " "
				+ mid1.y + " L " + mid2.x + " " + mid2.y + " L " + x2 + " "
				+ y2);
		a[1] = pCanvas.path(
				"M" + x2 + " " + y2 + " L" + (x2 - size) + " " + (y2 - size)
						+ " L" + (x2 - size) + " " + (y2 + size) + " L" + x2
						+ " " + y2).attr("fill", "black").rotate((90 + angle),
				x2, y2);
	} else if (useQuad1) {
		a = pCanvas.connectionarrow(bb1, bb2, 8);
	}
	a[0].node.setAttribute("eleid", id);
	a[0].attr("stroke-width", "4");
	a[0].attr("stroke", "orange");
	a[1].attr("stroke", "orange");
	a[1].attr("fill", "orange");
	a[1].attr("stroke-dasharray", "-");

	a[0].mouseover(function(e) {
		a[0].node.setAttribute("clr",this.attr("stroke"));
		var clr = Raphael.getColor();
		a[0].attr("stroke",clr);
		a[1].attr("stroke",clr);
		var myid = a[0].node.getAttribute("eleid");
		var wi = this.attr("stroke-width");
		wi = parseInt(wi)
		this.attr({
			"stroke-width" : (wi + 2)
		});
		wi = this.attr("stroke-width");
		lastSelectedConn = findConnById(myid);
		mouseOverConn = findConnById(myid);
		if (shift) {
			console.log("setting mouseover timeout");
			mouseOverTrigger = setTimeout(showConnHints, 500);
		}
	});
	a[0].mouseout(function(e) {
		var clr = a[0].node.getAttribute("clr");
		this.attr("stroke",clr);
		a[1].attr("stroke",clr);
		console.log("clearing mouseover timeout");
		if (mouseOverTrigger != null) {
			clearTimeout(mouseOverTrigger);
		}
		var wi = this.attr("stroke-width");
		wi = parseInt(wi)
		this.attr({
			"stroke-width" : (wi - 2)
		});
		wi = this.attr("stroke-width");
		mouseOverConn = null;
		// lastSelectedConn = null;
	});
	a[0].click(function(evt) {
		handleConnClick(this)
	});
	a[0].dblclick(function() {
		var myid = this.node.getAttribute("eleid");
		var conn = findConnById(myid);
		displaySelectedNodeProps(conn);
		var evtdata = {};
		evtdata.id = myid;
		evtdata.type = "dblclick";
		sendEvent(geq, [ evtdata ]);
		lastSelectedConn = conn;

		showUpdateConnDialogAtMouse();

	});
	subs.push(a[0]);
	subs.push(a[1]);
}
function arrowExp(x1, y1, x2, y2, subs, id) {
	var a = pCanvas.qarrow(x1, y1, x2, y2, 8);
	a[0].node.setAttribute("eleid", id);
	a[0].attr("stroke", "red");
	a[1].attr("stroke", "red");
	a[0].attr("stroke-dasharray", ".");
	a[1].attr("stroke-dasharray", ".");
	a[0].mouseover(function(e) {
		var myid = a[0].node.getAttribute("eleid");
		this.attr({
			"stroke-width" : 3
		});
		lastSelectedConn = findConnById(myid);
	});
	a[0].mouseout(function(e) {
		this.attr({
			"stroke-width" : 1
		});
		// lastSelectedConn = null;
	});
	a[0].node.onclick = function() {
		var myid = a[0].node.getAttribute("eleid");
		var conn = findConnById(myid);
		displaySelectedNodeProps(conn);

	};

	subs.push(a[0], a[1]);
}
function arrow(x1, y1, x2, y2, subs, id) {
	var bb1 = {};
	var bb2 = {};
	bb1.x = x1 - 24;
	bb1.y = y1 - 24;
	bb2.x = x2 - 24;
	bb2.y = y2 - 24;
	bb1.width = 48;
	bb1.height = 48;
	bb2.width = 48;
	bb2.height = 48;
	var a = pCanvas.connectionarrow(bb1, bb2, 8);
	// var a = pCanvas.qarrow(x1, y1, x2, y2,8);
	a[0].node.setAttribute("eleid", id);
	a[0].attr("stroke-width", "2");
	a[0].attr("stroke", "blue");
	a[1].attr("stroke", "blue");
	a[0].mouseover(function(e) {
		var myid = a[0].node.getAttribute("eleid");
		this.attr({
			"stroke-width" : 3
		});
		lastSelectedConn = findConnById(myid);
	});
	a[0].mouseout(function(e) {
		this.attr({
			"stroke-width" : 2
		});
		// lastSelectedConn = null;
	});
	a[0].node.onclick = function() {
		var myid = a[0].node.getAttribute("eleid");
		var conn = findConnById(myid);
		displaySelectedNodeProps(conn);

	};

	subs.push(a[0], a[1]);
}

function drawText(xpos, ypos, w, h, txt) {
	// console.log("drawing text: "+txt);
	return pCanvas.text(xpos, ypos - 5, txt);

}
function drawText2(xpos, ypos, txt) {
	return pCanvas.text(xpos, ypos - 5, txt);

}

function myrect(xpos, ypos, width, height, fillColor, borderColor, id, title) {
	var x1 = xpos - width / 2;
	var y1 = ypos - height / 2;
	var shape = pCanvas.rect(x1, y1, width, height, 4);
	// var color = Raphael.getColor();
	shape.attr({
		"fill" : fillColor,
		"stroke" : borderColor,
		"opacity" : .1,
		"fill-opacity" : 0,
		"stroke-width" : 2,
		cursor : "move"
	});
	shape.node.setAttribute("eleid", id);
	shape.drag(move, dragger, up);
	var txt = null;
	if (title != null) {
		txt = drawText(x1, y1, width, height, title);
	} else {
		txt = drawText(x1, y1, width, height, id);
	}
	txt.node.setAttribute("eleid", "nodetxt_" + id);
	var ret = {
		"item" : shape,
		"textnode" : txt,
		"subs" : new Array()
	};
	return ret;
}
function mycircle(xpos, ypos, r, id, fillColor, borderColor, title) {
	var x1 = xpos;
	var y1 = ypos;
	var shape = pCanvas.circle(x1, y1, r);
	shape.attr({
		"fill" : fillColor,
		"stroke" : borderColor,
		"opacity" : 1,
		"fill-opacity" : .5,
		"stroke-width" : r / 2,
		cursor : "move"
	});
	shape.node.setAttribute("eleid", id);
	shape.drag(move, dragger, up);
	var txt = null;
	if (title != null) {
		txt = drawText2(x1 - 3, y1 - 3, title);
		txt.node.setAttribute("eleid", "nodetxt_" + id);
	}
	var ret = {
		"item" : shape,
		"textnode" : txt,
		"subs" : new Array(),
		"img" : null
	};
	return ret;
}
function myregion(xpos, ypos, width, height, fillColor, borderColor, node) {
	var id = node.id
	var x1 = xpos - width / 2;
	var y1 = ypos - height / 2;
	var pts = dojo.fromJson(node.shape.pts);
	var pathString = getPathFromPointsArray(pts);
	var shapestr = "{var shape=pCanvas.path(\"" + pathString + "\");\n";
	shapestr += "shape.attr(\"fill\",\"" + "lightblue" + "\");";
	shapestr += "shape.attr(\"stroke\",\"" + node.color + "\");";
	shapestr += "shape.toBack();";
	shapestr += "return shape;}"
	var fun = (new Function(shapestr));
	var shape = fun();
	// shape.drag(regdrag,regmove,regup);
	shape.dblclick(function(evt) {
		var ele = this.node.getAttribute("eleid");
		var nd = findNodeById(ele);
		// var code = prompt("Enter Code","System.out.println(\"dada\");");
		// nd.code=code;

		var code = nd.code;
		if (code == null)
			code = "System.out.println(\\\"dada\\\");";
		showEditor(code, nd.id,"myregion");
	});
	var a = shape.getBBox();
	console.log(dojo.toJson(a));
	shape.node.setAttribute("eleid", id);
	shape.drag(move, dragger, up);
	var txt = null;
	txt = drawText(a.x, a.y, 20, 20, node.id);
	txt.node.setAttribute("eleid", "nodetxt_" + id);
	var ret = {
		"item" : shape,
		"textnode" : txt,
		"subs" : new Array()
	};
	return ret;

}
function drawTextNode(xpos, ypos, fillColor, borderColor, obj) {
	var ret = pCanvas.text(xpos, ypos, obj.txt);
	ret.toFront();
	ret.attr({
		font : '18px Fontin-Sans, Arial'
	});
	ret.drag(txtmove, txtdragger, txtup);
	ret.node.setAttribute("eleid", obj.id);
	ret.attr("text-anchor", "start");
	var a = obj.txt.split("\n");
	var height = a.length * (12 + 4);
	var ts = null;
	var maxlength = 0;
	for ( var i = 0; i < a.length; i++) {
		maxlength = maxlength < a[i].length ? a[i].length : maxlength;
	}
	var width = maxlength * (12 * .5) + 30;
	var bb = ret.getBBox();
	// var cir3 = pCanvas.rect(bb.x+bb.width-26,bb.y-2,5,5);
	// a.attr("text-anchor","start");

	var textrect = pCanvas.rect(bb.x, bb.y, bb.width, bb.height, 2);
	textrect.attr({
		"fill" : "0-#FFFF00-#FFD700",
		"stroke" : "orange",
		"opacity" : .9,
		"fill-opacity" : .9,
		"stroke-width" : 1
	});
	textrect.toBack();
	var textrectshadow = pCanvas.rect(bb.x + 6, bb.y + 6, bb.width, bb.height,
			2);
	textrectshadow.attr({
		"fill" : "0-#F0f0f0-#F0D000",
		"stroke" : "white",
		"opacity" : .4,
		"fill-opacity" : .4,
		"stroke-width" : 0
	});
	textrectshadow.toBack();

	var retobj = {
		"item" : null,
		"textnode" : ret,
		"subs" : new Array(),
		"id" : obj.id
	};
	var fid = obj.notefor;
	if (fid != null) {
		var nd = findNodeById(fid);
		if (nd != null) {
			lightarrow(xpos, ypos, nd.normalizedx, nd.normalizedy, retobj.subs,
					"tndarrow_" + (obj.id));
		}
	}

	retobj.subs.push(textrect);
	retobj.subs.push(textrectshadow);

	drawElements.push(retobj);
}
function loadImageIcons() {
	doGetHtml(getURL("iconURL"), initIcons);
}
function initIcons(response) {

	var pdiv = dojo.byId("dynamicicons");
	var a = dojo.fromJson(response);
	dojo.forEach(a, function(oneEntry, index, array) {
		var imgadd = dojo.create("img", null, pdiv);
		dojo.attr(imgadd, "src", oneEntry.src);
		dojo.attr(imgadd, "id", oneEntry.id);
		processIcons[oneEntry.process] = oneEntry.id
	});
}
function drawNodeRect(xpos, ypos, width, height, fillColor, borderColor, obj) {
	var id = obj.id;
	var ret = myrect(xpos, ypos, width, height, fillColor, borderColor, id);
	ret.id = id;
	ret.item.dblclick(function(evt) {
		showUpdateNodeDialogAtMouse();

	});
	var sh = findNodeById(id);
	var img = null;
	if (sh != null) {
		if (obj.icon == null || obj.icon == "") {
			img = document.getElementById(getIconForProcess(sh.clz));
		} else {
			img = {};
			img.src = obj.icon;
		}
		var mg = pCanvas.image(img.src, xpos - width / 2, ypos - height / 2,
				width, height);
		mg.toBack();
		ret.img = mg;
	}
	drawElements.push(ret);
}
function drawGenericNode(xpos, ypos, width, height, fillColor, borderColor,
		obj, imgsrc) {
	{
		var id = obj.id;
		var ret = null;
		if (obj.textual == null || obj.textual == false) {
			if (obj.title != null) {
				ret = myrect(xpos, ypos, width, height, fillColor, borderColor,
						obj.id, obj.title);
			} else {
				ret = myrect(xpos, ypos, width, height, fillColor, borderColor,
						id);
			}
			ret.id = id;
			var sh = findNodeById(id);
			if (sh != null) {
				if (obj.icon != null)
					imgsrc = obj.icon;
				var mg = pCanvas.image(imgsrc, xpos - width / 2, ypos - height
						/ 2, width, height);
				mg.toBack();
				ret.img = mg;
			}
			drawElements.push(ret);
		} else {
			ret = drawGenericTextualNode(xpos, ypos, obj, imgsrc);
			ret.id = id;
			drawElements.push(ret);
		}
	}
}
function drawGenericTextualNode(xpos, ypos, obj, imgsrc) {
	var id = obj.id;
	var a = pCanvas.text(xpos, ypos, obj.text);
	a.node.setAttribute("eleid", id);

	a.attr("font-size", 20);
	a.attr("text-anchor", "start");
	//sumit changed this to explicit
	if (obj.c == "yellow" || obj.c == "YELLOW") {
		a.attr("stroke", "#0f0fff");
		a.attr("fill", "#0f0fff");
	} else {
		a.attr("stroke", obj.c);
		a.attr("fill", obj.c);
	}

	var ret = {
		"item" : a,
		"textnode" : null,
		"subs" : null
	};
	a.drag(move, dragger, up);
	if (obj.type == "paramobj") {
		a.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var selectedNode = findNodeById(myid);
			var val = prompt("Enter val{including \" for strings}",
					selectedNode.val == null ? "" : selectedNode.val);
			if (val != null && val.length > 0) {
				selectedNode.val = val;
				selectedNode.text = val;
			} else {// empty the value
				if (selectedNode.val != null)
					selectedNode.val = null;
				selectedNode.text = selectedNode.c;
			}
			var node = findDrawEleByIdEx(selectedNode.id);
			if (node != null) {
				node.item.attr("text", selectedNode.text);
			}
			displaySelectedNodeProps(selectedNode);
		});
		var bb = a.getBBox();
		var cir3 = pCanvas.circle(bb.x + bb.width - 26, bb.y - 2, 5, 5);
		cir3.node.setAttribute("eleid", id);
		cir3.attr("fill", "pink");
		cir3.attr("stroke", "pink");
		cir3.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var sn = findNodeById(myid);
			var bn = prompt("Enter bean name", sn.cn);
			if (bn != null && bn.length > 0) {
				sn.cn = bn;
				displaySelectedNodeProps(sn);
			}
		});
		if ((obj.fn == null || obj.fn.length < 1) && obj.pn != null) {
			var cir4 = pCanvas.circle(bb.x + bb.width - 38, bb.y - 2, 5, 5);
			cir4.node.setAttribute("eleid", id);
			cir4.attr("fill", "orange");
			cir4.attr("stroke", "yellow");
			cir4.click(function(evt) {
				var myid = this.node.getAttribute("eleid");
				var sn = findNodeById(myid);
				setVarFld(sn);
			});
		}
		// /
		var cir = pCanvas.circle(bb.x + bb.width, bb.y - 2, 5, 5);
		cir.attr("fill", "red");
		cir.attr("stroke", "orange");
		cir.node.setAttribute("eleid", id);
		cir.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var sn = findNodeById(myid);
			updateClassesForFunctionObj(myid);
		});

	} else if (obj.type == "classobj") {
		a.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var selectedNode = findNodeById(myid);
			var val = prompt("Enter val{including \" for strings}",
					selectedNode.val == null ? "" : selectedNode.val);
			if (val != null && val.length > 0) {
				selectedNode.val = val;
				selectedNode.text = val;
			} else {// empty the value
				if (selectedNode.val != null)
					selectedNode.val = null;
				selectedNode.text = selectedNode.c;
			}
			var node = findDrawEleByIdEx(selectedNode.id);
			if (node != null) {
				node.item.attr("text", selectedNode.text);
			}
			displaySelectedNodeProps(selectedNode);

		});
		var bb = a.getBBox();
		var cir2 = pCanvas.circle(bb.x + bb.width - 14, bb.y - 2, 5, 5);
		cir2.node.setAttribute("eleid", id);
		cir2.attr("fill", "green");
		cir2.attr("stroke", "blue");
		cir2.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var sn = findNodeById(myid);
			updateClassesForFunctionObj(myid);
		});
		var cir3 = pCanvas.circle(bb.x + bb.width - 26, bb.y - 2, 5, 5);
		cir3.node.setAttribute("eleid", id);
		cir3.attr("fill", "pink");
		cir3.attr("stroke", "pink");
		cir3.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var sn = findNodeById(myid);
			var bn = prompt("Enter bean name", sn.cn);
			if (bn != null && bn.length > 0) {
				sn.cn = bn;
				displaySelectedNodeProps(sn);
			}
		});

	} else if (obj.type == "functionobj") {
		var bb = a.getBBox();
		var cir = pCanvas.circle(bb.x + bb.width, bb.y - 2, 5, 5);
		cir.attr("fill", "red");
		cir.attr("stroke", "orange");
		cir.node.setAttribute("eleid", id);
		var cir2 = pCanvas.circle(bb.x + bb.width - 14, bb.y - 2, 5, 5);
		cir2.node.setAttribute("eleid", id);
		cir2.attr("fill", "green");
		cir2.attr("stroke", "blue");
		cir.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var sn = findNodeById(myid);
			updateClassesForFunctionObj(myid);
		});
		cir2.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var sn = findNodeById(myid);
			updateFuntionForFunctionObj(myid);
		});
		var cir3 = pCanvas.circle(bb.x + bb.width - 26, bb.y - 2, 5, 5);
		cir3.node.setAttribute("eleid", id);
		cir3.attr("fill", "pink");
		cir3.attr("stroke", "pink");
		cir3.click(function(evt) {
			var myid = this.node.getAttribute("eleid");
			var sn = findNodeById(myid);
			var bn = prompt("Enter bean name", sn.cn);
			if (bn != null && bn.length > 0) {
				sn.cn = bn;
				displaySelectedNodeProps(sn);
				for ( var i = 0; i < pData.data.length; i++) {
					var ott = pData.data[i];
					if (ott.grp != null && ott.grp == sn.id) {
						ott.cn = bn;
					}
				}
				displaySelectedNodeProps(sn);
			}
		});
	}
	return ret;
}
function getIconForProcess(name) {
	var ret = processIcons[name];
	if (ret != null) {
		return ret;
	} else {
		return "process.jpg";
	}
}
function getIconForStream(node) {
	return "/site/wide_5678/images/stream.jpg";
}
function drawStreamRect(xpos, ypos, width, height, fillColor, borderColor, id) {
	var node = findNodeById(id);
	var ret = myrect(xpos, ypos, width, height, fillColor, borderColor, id);
	ret.id = id;
	/*         ret.item.dblclick(function (evt){
	 setTimeout(hideCMenu,100);
	 showUpdateStreamDialogAtMouse();
	 });
	 */ret.item.click(function(evt) {
		if (ctrl) {
			if (showingExtendedAdvancedMenu) {
				HideSelectedAdvancedMenu();
			} else {
				ShowSelectedAdvancedMenu();
			}
		}
	});
	var img = getIconForStream(node);
	var mg = pCanvas.image(img, xpos - width / 2, ypos - height / 2, width,
			height);
	mg.toBack();
	ret.img = mg;
	drawElements.push(ret);
}

function drawEventRect(xpos, ypos, width, height, fillColor, borderColor, id) {
	var ret = myrect(xpos, ypos, width, height, fillColor, borderColor, id);
	ret.id = id;
	ret.item.dblclick(function(evt) {
	});

	var img = document.getElementById("event.jpg");
	var mg = pCanvas.image(img.src, xpos - width / 2, ypos - height / 2, width,
			height);
	mg.toBack();
	ret.img = mg;
	drawElements.push(ret);

}
function drawGroupRect(xpos, ypos, width, height, fillColor, borderColor, obj) {
	var id = obj.id;
	var icon = obj.icon;
	if (icon == null || icon == "/images/folder.png") {
		if (obj.closed == true) {
			obj.icon = "/images/folder.png";
		} else {
			obj.icon = "/images/unpublish_f2.png";
		}
	}
	if (filter[id] == null) {
		var ret = myrect(xpos, ypos, width, height, fillColor, borderColor, id);
		ret.id = id;
		var mg = pCanvas.image(obj.icon, xpos - width / 2, ypos - height / 2,
				width, height);
		mg.toBack();
		ret.img = mg;
		drawElements.push(ret);
	}
}
function drawModuleNode(xpos, ypos, width, height, fillColor, borderColor, node) {
	var override = nodeDrawingHandler["module-"+node.id];
	if(override!=null){
		override(xpos, ypos, width, height, fillColor, borderColor, node)
	}else{
		drawModuleNodeOrig(xpos, ypos, width, height, fillColor, borderColor, node);
	}
}
function drawModuleNodeOrig(xpos, ypos, width, height, fillColor, borderColor, node) {
	if (filter[node.id] == null) {
		var ret = findComponentByName(node.id);
		if (ret != null && dojo.byId(node.id)!=null/*check if elements really exists in ui*/) {
			node.val = getModuleCode(ret);
			var rotate = ret.rotation;
			var realpos = null;
			if(rotate!=null){
				if(rotate!="0deg"){
					setCSSRotation(node.id,0);
					realpos = dojo.position(node.id)
					setCSSRotation(node.id,rotate);
				}else{
					realpos = dojo.position(node.id)
				}
			}else{
				realpos = dojo.position(node.id)
			}
			var exists = moduleExists(node.id);
			if (!exists) {
				var f = comps[ret.name];
				var configFun = configs[ret.name];
				if (configFun != null) {
					configFun(f, null, ret.name, false, null, null, ret);
				}
			}
			realpos.x-=leftWidth;
			realpos.y-=topWidth;
			realpos.r = realpos.w;
			realpos.b = realpos.h;
			var rect2 = null;
			if(debug){
			rect2=pCanvas.rect(realpos.x-5,realpos.y-5,realpos.w+10,realpos.h+10);
			rect2.attr("stroke", "green").attr("stroke-width",2);
			}
			
			var dot = pCanvas.circle(realpos.x+realpos.w-10, realpos.y-7,10);
			dot.attr("stroke","white");
			//dot.attr("fill","#4F94CD");
			dot.node.setAttribute("eleid", node.id);
			dot.attr({"fill":"url(/site/images/refresh.png)"})
			
			var rangle = 0;
			if(rotate!=null &&rotate!="0deg"){
			var rangle=rotate.substr(0,rotate.length-3);
			if(debug){
			rect2.rotate(rangle,realpos.x+realpos.w/2,realpos.y+realpos.h/2);
			console.log(rect2.attr("rotate"));
			}
			dot.rotate(rangle,realpos.x+realpos.w/2,realpos.y+realpos.h/2);
			}
			var realpos2 = cloneItem(realpos);
			realpos2.x = realpos2.x+realpos2.w/2;
			realpos2.y = realpos2.y+realpos2.h/2;
			if(drawModulePort){
			if (rangle != null) {
				var pts = getPoi(realpos2);
				var poi1 = new Array();
				var cx = realpos2.x 
				var cy = realpos2.y ;
				for ( var l = 0; l < pts.length; l++) {
					var p = rotatePointDeg(pts[l], cx, cy, rangle);
					/*if (!(l % 2 == 0))*/
					{
						if(debug){
						pCanvas.circle(p.x, p.y, 5).attr("stroke", "green");
						}
						var port = null;
						port =findNodeById(node.id+"."+l);
						if(port==null){
							port = createPortObj(p.x,p.y,node.id+"."+l,l,"arbitport",node.id);
							addObjectToGraphNoUndo(port);
							configPort(port.x, port.y, 3, 3, "#B7C3D0", "#B7C3D0",port,null);
						}else{
							port.x = p.x;
							port.y = p.y;
							port.normalizedx = p.x;
							port.normalizedy = p.y;
							var d = findDrawEleByIdEx(port.id);
							if(d!=null){
							d.item.attr({"cx":p.x,"cy":p.y});
							}else{
								if(debug)
									console.log("inside drawModuleNode")
							}
						}
						
					}
				}
			}
			}
			
			var dotDragger = function() {
				this.ox = this.attr("cx") ;
				this.oy = this.attr("cy") ;
				this.animate({
					"fill-opacity" : .2
				}, 500);
			}, dotmove = function(dx, dy) {
				var att = {
					cx : mouseX,
					cy : mouseY
				} ;
				this.attr(att);
				var myid = this.node.getAttribute("eleid");
				if (myid != null) {
					var node = findNodeById(myid);
					var nditem = findDrawEleById(myid);
					var comp = findComponentByName(myid);
					if (node != null) {
						var cx = this.attr("cx"),cy = this.attr("cy")
						var angle = Math.atan2(node.x - cx,node.y-cy);
						angle = (angle / (2 * Math.PI)) * 360+90;
						if(angle<0){
							angle+=360;
						}
						//pCanvas.line(node.x,node.y,cx,cy);
						angle = angle.toFixed(0);
						var step = 5;
						if(wideconfig.rotation_step!=null){
							step = parseInt(wideconfig.rotation_step);
						}
						var rem = angle%step;
						angle = angle-rem;
						angle = 360-angle;
						setHintMessage(angle);
						setCSSRotation(myid,angle);
						if(comp!=null)
						comp.rotation=(angle+"deg");
					}
				}
				
				
			}, dotup = function() {
				draw();
			};
			dot.drag(dotmove,dotDragger,dotup);
		
			
		}
	}
}
function drawRegionRect(xpos, ypos, width, height, fillColor, borderColor, node) {
	if (filter[node.id] == null) {
		var ret = myregion(xpos, ypos, width, height, fillColor, borderColor,
				node);
		ret.id = node.id;
		drawElements.push(ret);
	}
}

function drawRouteRect(xpos, ypos, width, height, fillColor, borderColor, id) {
	var ret = myrect(xpos, ypos, width, height, fillColor, borderColor, id);
	var img = document.getElementById("router.jpg");
	ret.id = id;
	var mg = pCanvas.image(img.src, xpos - width / 2, ypos - height / 2, width,
			height);
	mg.toBack();
	ret.img = mg;
	drawElements.push(ret);
}

function setHeights() {
	// document.getElementById("topBarTD").style.height = (topBarHeight+"px");
	// document.getElementById("cmdRowTD").style.height = (cmdRowHeight+"px");
	var node = dojo.byId("graph");
	var pos = dojo.position(node);
	topWidth = pos.y;
	leftWidth = pos.x;
}

function displayGraphSelectionDialog() {
	if (xmlHttp.readyState == 4) {
		var graphIds = eval(xmlHttp.responseText);
		var content = "<table>";
		for ( var i = 0; i < graphIds.length; i++) {
			// console.log("graph Id : "+graphIds[i]);
			content += "<tr><td><input type='radio' name='graphid' id='graphid' value="
					+ graphIds[i]
					+ "> Graph "
					+ graphIds[i]
					+ "</input></td></tr>";
		}
		content += "<tr><td><button dojoType='dijit.form.Button' type='submit'>OK</button></td></tr>";
		content += "</table>";
		content += "";
		var openDialog = dijit.byId('openDialog');
		openDialog.attr("content", content);
		openDialog.show();
	}
}

function prepareSequence() {
dojo.byId("rightbottombar").innerHTML="draw";
	
}
function setHintMessage(msg){
	dojo.byId("rightbottombar").innerHTML=msg;
}
function prepareGraph() {
	//alert("preparegraph");
	setup();
	prepareSequence();
	setHeights();
	initNodePropsTable();
	showFirstGraph();
	dojo.connect(dojo.byId("graph"), "ondblclick", function(evt) {
		if (ctrl) {
			mycustommenu();
		}
	});
}

function showAssociatedNodes(nodes, mainid) {
	addStreamsToPageDlg();

	var a = null;
	var toDraw = new Array();
	for (i in nodes) {
		if (nodes[i].a3 == mainid) {
			a = findNodeById(nodes[i].a3);
			if (a != null) {
				toDraw.push(nodes[i]);
			}
		}
	}
	if (a != null) {
		var aDraw = findDrawEleById(a.id);
		lightOff();
		aDraw.item.attr("opacity", ".8");
		aDraw.item.scale(2, 2);
		aDraw.img.attr("opacity", ".8");
		aDraw.textnode.scale(2, 2);
		aDraw.textnode.attr("opacity", "1");
		aDraw.img.scale(1.6, 1.6);
		aDraw.img.toFront();
		aDraw.textnode.toFront();
		// aDraw.attr("opacity",1);
		var sy = a.normalizedy;
		var sx = a.normalizedx;
		var cnt = toDraw.length;
		var d = 2 * Math.PI / cnt
		var angle = 0;
		var distance = 140;
		for (i in toDraw) {
			y = Math.round(sy + distance * Math.sin(angle));
			x = Math.round(sx + distance * Math.cos(angle));
			var subs = new Array();
			// /////////////////////
			var temp = pCanvas.image(getURL("HTMLF2"), sx, sy, 60, 60);
			temp.node.name = toDraw[i].a1;
			temp.node.stream = toDraw[i].a3;
			temp.node.cntn = toDraw[i].a2;
			temp.dblclick(function(event) {
				dojo.byId("p_streamname").value = this.node.stream;
				dojo.byId("pagename").value = this.node.name;
				dojo.byId("pagecontent").value = this.node.cntn;
				var dlg = dijit.byId("createPageDialog");
				dlg.show();
			});
			temp.animate({
				"x" : x - 30,
				"y" : y - 30
			}, 1000, "bounce");
			subs.push(temp);
			// ///////////////////////
			var temp2 = pCanvas.text(x, y, toDraw[i].a1)
			temp2.animateWith(temp, {
				"y" : y - 34
			}, 1000, "<>");
			subs.push(temp2);
			// ////////////////
			var temp3 = pCanvas.line(x, y, sx, sy);
			temp3.toBack();
			temp3.attr({
				"stroke-dasharray" : ".",
				"stroke" : "blue",
				"stroke-width" : 2,
				"opacity" : 0
			});
			temp3.animateWith(temp, {
				"opacity" : 1
			}, 1000, "<>");
			subs.push(temp3);
			// ////////////////
			angle = angle + d;
			var retobj = {
				"item" : null,
				"textnode" : null,
				"subs" : subs
			};
			drawElements.push(retobj);
		}
	}
}
function showRelatedNodes() {
	if (lastSelectedNode != null) {
		var str = getURL("SHOWPAGES") + lastSelectedNode.id;
		dojo.xhrGet({
			url : str,
			load : function(response, ioArgs) {
				var fun = (new Function(response));
				fun();
			},
			error : function(response, ioArgs) {
				alert("Failed to get related nodes" + response);
			}
		});
	}
}
function sendEvent(eventname, eventdata) {
	dojo.publish(eventname, eventdata);
}

function getConnectionLabel(str) {
	var as = "";
	if (str != null && str != "") {
		console.log("Now returning conlabel: " + str);
		try {
			as = dojo.fromJson(str);
		} catch (e) {
			console.log("conn label is not json " + str)
		}
		as = str;
		console.log("Now returning conlabel done: " + as);
	}
	var i = as.length
	if (as.length > maxConnLabelLength) {
		i = maxConnLabelLength;
		var a = as.substring(0, i);
		a += "..."
		return a;
	}
	return as;
}

function findnearStream(x1, y1) {
	var dist = 10000;
	var id = "";
	for (i in pData.data) {
		if (pData.data[i].type != "stream") {
			continue;
		}
		// console.log( pData.data[i].normalizedx+" : "+pData.data[i].normalizedy);
		var dx = pData.data[i].normalizedx - x1;
		var dy = pData.data[i].normalizedy - y1;
		var vdist = Math.sqrt(dx * dx + dy * dy);

		// console.log("vDist : "+vdist);
		if (vdist < dist) {
			dist = vdist;
			id = pData.data[i].id;

		}

	}
	// console.log("Min Dist : "+dist);
	// console.log("id : "+id);
	return id;
}
function distance(x1, y1, x2, y2) {
	var dx = x2 - x1;
	var dy = y2 - y1;
	var vdist = Math.sqrt(dx * dx + dy * dy);
	return vdist;
}
function editConnection() {
	connEdit = true;
	draw();
}
function findnearItem(x1, y1, max, intype) {
	var dist = 10000;
	var id = "";
	for ( var i = 0; i < pData.data.length; i++) {
		if (intype != null) {// filterbyintype
			if (pData.data[i].type != intype)
				continue;
		}
		var dx = pData.data[i].normalizedx - x1;
		var dy = pData.data[i].normalizedy - y1;
		var vdist = Math.sqrt(dx * dx + dy * dy);
		if (filter[pData.data[i].id] == null) {
			if (vdist < dist) {
				dist = vdist;
				id = pData.data[i].id;
			}
		}
	}
	if (max != null && max < dist)
		return null;
	return id;
}
function findnearItemObj(x1, y1, max, intype) {
	var dist = 10000;
	var iditem = "";
	for ( var i = 0; i < pData.data.length; i++) {
		if (intype != null) {// filterbyintype
			if (pData.data[i].type != intype)
				continue;
		}
		var dx = pData.data[i].normalizedx - x1;
		var dy = pData.data[i].normalizedy - y1;
		var vdist = Math.sqrt(dx * dx + dy * dy);
		if (filter[pData.data[i].id] == null) {
			if (vdist < dist) {
				dist = vdist;
				iditem = pData.data[i];
			}
		}
	}
	if (max != null && max < dist)
		return null;
	return iditem;
}
function findnearItemColl(x1, y1, coll) {
	var ldist = 10000;
	var pt = null;
	for (var i =0;i<coll.length;i++) {
		var vdist = dist(coll[i].x,coll[i].y,x1,y1)
			if (vdist < ldist) {
				ldist = vdist;
				pt = {};
				pt.x = coll[i].x;
				pt.y = coll[i].y;
				pt.id = coll[i].id;
				pt.dist = ldist
		}
	}
	return pt;
}
function findnearItemCollColl(coll1, coll2) {
	var arr = new Array();
	for (var i =0;i<coll1.length;i++) {
		var t = coll1[i];
		var pt = findnearItemColl(t.x,t.y,coll2);
		var l = {};
		l.first = t;
		l.second = pt;
		l.dist = pt.dist;
		arr.push(l);
	}
	arr.sort(function(a,b){return a.dist-b.dist});
	return arr[0];
}
function initGeqListener() {
	dojo.subscribe(geq, null, function handle(jdata) {
		var str = jdata.id;
		if (str != null && nodeeventhandlers[str] != null) {
			nodeeventhandlers[str](jdata);
		} else {
			nodeeventhandlers["default"](jdata);
		}

	});
}

function isGenericNode(nd) {
	if (genericnodes[nd.icon] != null)
		return true;
	return false;
}
function isGenericNodeId(id) {
	var nd = findNodeById(id);
	if (nd != null && genericnodes[nd.icon] != null)
		return true;
	return false;

}

function drawGroupShape(nd) {
	if (nd.shape != null) {
		var fun = (new Function(nd.shape.design));
		var shape = fun();
	}
}
function highliteConn(id, color) {
	if (color == null)
		color = 'red';
	var item = findDrawEleByIdEx(id);
	if (item.subs != null) {
		for ( var i =0;i<item.subs.length;i++) {
			item.subs[i].attr("stroke", color);
			item.subs[i].attr("stroke-width", "3");
			item.subs[i].toBack();

		}
	}
}
// ==========================
var regdrag = function() {
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, regmove = function(dx, dy) {
	var pt = this.getBBox();
	var xamt = mouseX - pt.x - leftWidth;
	var yamt = mouseY - pt.y - topWidth;
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
	console.log("tx: " + xamt + "," + yamt)
	pCanvas.safari();
	// var tr = this.attr("translation");
	// var myid = this.node.getAttribute("eleid");
	// var node= findNodeById(myid);
}, regup = function() {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var tr = this.attr("translation");
	var myid = this.node.getAttribute("eleid");
	var node = findNodeById(myid);
	var shape = node.shape;
	if (shape) {
		{
			if (shape.pts) {
				var pts = dojo.fromJson(shape.pts);
				for ( var k in pts) {
					pts[k].x += this.tx;
					pts[k].y += this.ty;
				}
				shape.pts = dojo.toJson(pts);
				this.tx = 0;
				this.ty = 0;
				var shapestr = "var shape=pCanvas.path(\""
						+ getPathFromPoints(pts) + "\");\n";
				shapestr += "shape.attr(\"stroke\",\"red\");";
				shape.design = shapestr;
			}
		}
	}
};
// /////tagigng routines

function blinkElementImage(name, t) {
	var nditem = findDrawEleById(name);
	var ndtext = nditem.textnode;
	var imgnode = nditem.img;
	imgnode.animate({
		"opacity" : .1
	}, t / 2, "bounce", function() {
		imgnode.animate({
			"opacity" : 1
		}, t / 2, "bounce");
	});
}
function dimmElementImage(name, t) {
	var nditem = findDrawEleById(name);
	var ndtext = nditem.textnode;
	var imgnode = nditem.img;
	imgnode.animate({
		"opacity" : .1
	}, t / 2, "bounce");
}
function lightElementImage(name, t) {
	var nditem = findDrawEleById(name);
	var ndtext = nditem.textnode;
	var imgnode = nditem.img;
	imgnode.animate({
		"opacity" : 1
	}, t / 2, "bounce");
}
function greenbulbGraph(name) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var ball = pCanvas.ball(selectedNode.normalizedx - selectedNode.r / 2
				- 10, selectedNode.normalizedy - selectedNode.b / 2 - 10, 10,
				.5);
		ball.animate({
			"fill-opacity" : 0
		}, 300, "bounce", function() {
			ball.remove();
		});
	}
}
function redbulbGraph(name) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var ball = pCanvas.ball(selectedNode.normalizedx - selectedNode.r / 2
				- 10, selectedNode.normalizedy - selectedNode.b / 2 - 10, 10,
				.9);
		ball.animate({
			"fill-opacity" : 0
		}, 300, "bounce", function() {
			ball.remove();
		});
	}
}

function bulbGraph(name, hue, t) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var ball = pCanvas.ball(selectedNode.normalizedx - selectedNode.r / 2
				- 10, selectedNode.normalizedy - selectedNode.b / 2 - 10, 10,
				hue);
		ball.animate({
			"fill-opacity" : 0
		}, t, "bounce", function() {
			ball.remove();
		});
	}
}
function flatLEDGraph(name, color, t) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var led = pCanvas.rect(selectedNode.normalizedx - selectedNode.r / 2
				- 20, selectedNode.normalizedy - selectedNode.b / 2 - 20, 40,
				4, 8);
		led.attr("fill", color);
		led.attr("stroke", color);
		led.animate({
			"fill-opacity" : .2
		}, t, "bounce", function() {
			led.remove();
		});
	}
	return led;
}
function flatGreenLEDGraph(name, t) {
	flatLEDGraph(name, "green", t);
}
function flatYellowLEDGraph(name, t) {
	flatLEDGraph(name, "yellow", t);
}
function flatRedLEDGraph(name, t) {
	flatLEDGraph(name, "Red", t);
}
function tagGraph(name, text, t) {
	var selectedNode = findNodeById(name);
	if (selectedNode != null) {
		var mytext = pCanvas.text(selectedNode.normalizedx - selectedNode.r / 2
				- 20, selectedNode.normalizedy - selectedNode.b / 2 - 20, text);
		mytext.animate({
			"opacity" : .1
		}, t, "bounce", function() {
			mytext.remove();
		});
	}
}

Array.prototype.contains = function(element) {
	for ( var i = 0; i < this.length; i++) {
		if (this[i] == element) {
			return true;
		}
	}
	return false;
}

function getNearDrawEle(x, y, intype) {
	var id = "";
	for ( var i = 0; i < drawElements.length; i++) {
		var de = drawElements[i];
		if (de.item != null) {
			var bbox = de.item.getBBox();
			var x1 = bbox.x;
			var y1 = bbox.y;
			var w = bbox.width;
			var h = bbox.height;
			var x2 = x1 + w;
			var y2 = y1 + h;
			if (x >= x1 && x <= x2) {
				if (y >= y1 && y <= y2) {
					var tid = de.id;
					var nd = findNodeById(tid);
					if (intype != null && nd.type != intype) {
						continue;
					} else {
						return nd;
					}
				}
			}
		}
	}
	return null;
}

function removeNodeConnections(id) {
	var objs = new Array();
	var nodeRequired = null;
	for ( var i = 0; i < pData.data.length; i++) {
		if (pData.data[i].type == 'connection') {
			var obj = pData.data[i];
			if (obj.from == id || obj.to == id) {
				removeObjectFromGraph(i, 1);
				objs.push(obj);
			}
		}
	}
	return objs;
}

function clientWidth() {
	return filterResults(
			window.innerWidth ? window.innerWidth : 0,
			document.documentElement ? document.documentElement.clientWidth : 0,
			document.body ? document.body.clientWidth : 0);
}
function clientHeight() {
	return filterResults(window.innerHeight ? window.innerHeight : 0,
			document.documentElement ? document.documentElement.clientHeight
					: 0, document.body ? document.body.clientHeight : 0);
}
function scrollLeft() {
	return filterResults(window.pageXOffset ? window.pageXOffset : 0,
			document.documentElement ? document.documentElement.scrollLeft : 0,
			document.body ? document.body.scrollLeft : 0);
}
function scrollTop() {
	return filterResults(window.pageYOffset ? window.pageYOffset : 0,
			document.documentElement ? document.documentElement.scrollTop : 0,
			document.body ? document.body.scrollTop : 0);
}
function filterResults(n_win, n_docel, n_body) {
	var n_result = n_win ? n_win : 0;
	if (n_docel && (!n_result || (n_result > n_docel)))
		n_result = n_docel;
	return n_body && (!n_result || (n_result > n_body)) ? n_body : n_result;
}

function getViewPort() {
	var viewportwidth;
	var viewportheight;
	if (typeof window.innerWidth != 'undefined') {
		viewportwidth = window.innerWidth, viewportheight = window.innerHeight
	} else if (typeof document.documentElement != 'undefined'
			&& typeof document.documentElement.clientWidth != 'undefined'
			&& document.documentElement.clientWidth != 0) {
		viewportwidth = document.documentElement.clientWidth,
				viewportheight = document.documentElement.clientHeight
	} else {
				viewportwidth = document.getElementsByTagName('body')[0].clientWidth,
				viewportheight = document.getElementsByTagName('body')[0].clientHeight
	}
	return {
		"width" : viewportwidth,
		"height" : viewportheight
	};
}

function checkSizes() {
	var node = dojo.byId("graph");
	var canvasPostion = dojo.position(node);
	leftWidth = canvasPostion.x;
	topWidth = canvasPostion.y;
}
var lastpos = null;
function nextCircString(currval, sliderpos, config, delta) {
	var cv = (currval);
	var off = parseInt(delta) > 0 ? 1 : -1;
	var a = config.extra;//array of strings
	var val = cv;
	for ( var i = 0; i < a.length; i++) {
		if (a[i] == currval) {
			if (off > 0) {
				if (i < (a.length - 1)) {
					val = a[i + 1];
				} else {
					val = a[0];
				}
			} else {
				if (i > 0) {
					val = a[i - 1];
				} else {
					val = a[a.length - 1];
				}
			}
		}

	}
	return val;
}
function nextString(currval, sliderpos, config, delta) {
	var cv = (currval);
	var off = parseInt(delta) > 0 ? 1 : -1;
	var a = config.extra;//array of strings
	var val = cv;
	for ( var i = 0; i < a.length; i++) {
		if (a[i] == currval) {
			if (off > 0) {
				if (i < (a.length - 1)) {
					val = a[i + 1];
				} else {
					//do nothing
				}
			} else {
				if (i > 0) {
					val = a[i - 1];
				} else {
					//do nothing
				}
			}
		}

	}
	return val;
}
function nextint(currval, sliderpos, config, delta) {
	var cv = parseInt(currval);
	var off = parseInt(delta) > 0 ? 1 : -1;
	return cv + off;
}
//drawSliderFor(this,nextint,true);
//drawSliderFor(this,nextCircString,true,['a','b','c','d','e','f','g']);

function drawSliderFor(item, algo, updatetext, opt) {
	var itemid = item.node.getAttribute("eleid");
	var bb = item.getBBox();
	var x = item.attr("x");
	var y = item.attr("y");
	x += bb.width + 5;
	y += bb.height / 2;
	var instid = getUniqId();
	var c = getSimpleStaticModule(instid, 'simplestaticnode2', x + "px", y
			+ "px", "50px", "10px", "   ", 200);
	dojo.byId('bid_' + instid).style.opacity = .1
	c.height = 20;
	var nd = new dojo.dnd.move.boxConstrainedMoveable(instid, {
		handle : "heading_" + instid,
		box : {
			l : x,
			t : (y - 200),
			w : 0,
			h : (y + 200)
		}
	});
	gclist[itemid] = instid;
	sliderMap[instid] = {
		'id' : itemid,
		'nextval' : algo,
		'base' : y,
		'updatetext' : updatetext,
		'extra' : opt
	};
}
dojo.subscribe(geq, null, handleSliderMove);
function handleSliderMove(jdata) {
	if (jdata.type == "ssmmove") {
		var obj = sliderMap[jdata.id];
		if (obj != null) {
			var i = obj.id;
			if (i != null) {
				var nd = findDrawEleByIdEx(i);
				if (nd != null)
					console.log("found nd: " + nd.id)
				if (nd != null) {
					var nd2 = findNodeById(i);
					if (nd2 != null)
						console.log("found nd2: " + nd2.id)
					var delta = 0;
					if (lastpos == null)
						delta = jdata.pos.y;
					else
						delta = lastpos.y - jdata.pos.y;
					var val = nd2.val;
					if (val == null) {
						val = nd.item.attr("text");
					}
					val = obj.nextval(val, jdata.pos, obj, delta);
					lastpos = jdata.pos;
					nd.item.attr("text", val);
					nd2.val = val;
					if (obj.updatetext) {
						nd2.text = val;
					}
				}
			}
		}
	} else if (jdata.type == 'ssmmovestop') {
		lastpos = null;
	}
}

var inspectorOn = false;
var inspector = null;
var inspectorSubs = null;
function showInspector() {
	if (inspectorOn == false) {
		inspectorOn = true;
	} else {
		inspectorOn = false;
	}
	if (inspectorOn) {
		inspector = getUniqId();
		var c = getSimpleStaticModule(inspector, 'simplestaticnode2',
				100 + "px", 100 + "px", "150px", "100px", "   ", 200);
		dojo.byId('bid_' + inspector).style.opacity = .9
		c.height = 20;
		var nd = new dojo.dnd.move.boxConstrainedMoveable(inspector, {
			handle : "heading_" + inspector,
			box : {
				l : 10,
				t : 50,
				w : 2000,
				h : 1200
			}
		});
		inspectorSubs = dojo.subscribe(geq, null, handleInspectorMove);
	} else {
		dojo.destroy(inspector);
		inspector = null;
		if (inspectorSubs != null) {
			dojo.unsubscribe(inspectorSubs);
			inspectorSubs = null;
		}

	}
}

function handleInspectorMove(jdata) {
	if (jdata.type == 'ssmmovestop') {
		var pos = jdata.pos;
		var item = findnearItem(pos.x + pos.w / 2,
				pos.y + pos.h / 2 - topWidth, 50, "AnonDef");
		console.log("near : " + item);
		dojo.byId('bid_' + inspector).innerHTML = item;
		dojo.byId('bid_' + inspector).style.opacity = .8
	}
}
function removeObjectFromGraph(i, j,noundo) {
	if(noundo==null ||noundo==false)
	undoGraph.push(dojo.toJson(pData.data));
	pData.data.splice(i, j);

}
function addObjectToGraph(obj) {
	lastAdded = obj;
	undoGraph.push(dojo.toJson(pData.data));
	pData.data.push(obj);
}
function addObjectToGraphNoUndo(obj) {
	//undoGraph.push(dojo.toJson(pData.data));
	pData.data.push(obj);
}
function removeObjectFromGraphNoUndo(i, j) {
	//undoGraph.push(dojo.toJson(pData.data));
	pData.data.splice(i, j);

}
function checkPoint() {
	undoGraph.push(dojo.toJson(pData.data));
}
function undo() {
	var a = undoGraph.pop();
	if (a != null) {
		redoGraph.push(a);
		var b = dojo.fromJson(a);
		pData.data = b;
		draw();
	}
}
function redo() {
	var a = redoGraph.pop();
	if (a != null) {
		undoGraph.push(a);
		var b = dojo.fromJson(a);
		pData.data = b;
		draw();
	}
}
function handleConnClick(pthis) {

	var myid = pthis.node.getAttribute("eleid");
	console.log("Connection clicked: " + myid)
	var conn = findConnById(myid);
	if (conn != null) {
		displaySelectedNodeProps(conn);
		lastSelectedConn = conn;
	}
	var evtdata = {};
	evtdata.id = myid;
	evtdata.type = "click";
	sendEvent(geq, [ evtdata ]);

	var pt1 = pthis.getPointAtLength(0);
	var pt2 = pthis.getPointAtLength(pthis.getTotalLength());
	var point1 = pCanvas.circle(pt1.x, pt1.y, 12).attr("fill", "#45f").attr(
			"stroke", "blue").attr("fill-opacity", .5);
	point1.node.setAttribute("eleid", myid);
	point1.node.setAttribute("pttype", "start");

	var point2 = pCanvas.circle(pt2.x, pt2.y, 12).attr("fill", "#45f").attr(
			"stroke", "blue").attr("fill-opacity", .5);
	point2.node.setAttribute("eleid", myid);
	point2.node.setAttribute("pttype", "end");

	point2.node.setAttribute("sx", pt1.x);
	point2.node.setAttribute("sy", pt1.y);
	point1.node.setAttribute("ex", pt2.x);
	point1.node.setAttribute("ey", pt2.y);

	var cptsdragger = function() {
		this.animate({
			"fill-opacity" : .2
		}, 500);
		var myid = this.node.getAttribute("eleid");
		var pttype = this.node.getAttribute("pttype");
		var item = findDrawEleByIdEx(myid);
		if (item != null) {
			item.subs[0].attr("stroke-dasharray", ".");
			item.subs[0].attr("stroke-width", "3");
			item.subs[0].attr("stroke", "green");
			item.subs[1].attr("stroke-dasharray", ".");
			item.subs[1].attr("stroke-width", "3");
			item.subs[1].attr("stroke", "green");
		}
		var me= findNodeById(myid);
		var thisItem = null ; 
		var circleColor ="green";
			if(me!=null){
				if (pttype == "start"){
					thisItem = findNodeById(me.to);
				}else if (pttype == "end") {
					thisItem = findNodeById(me.from);
				}
			
		if(thisItem!=null && thisItem.type=="Port"){
			highLightValidPorts(thisItem);
		}
			}
	}, cptsmove = function(dx, dy) {
		var att = {
			cx : mouseX - leftWidth,
			cy : mouseY - topWidth
		}
		this.attr(att);
		var pttype = this.node.getAttribute("pttype");
		var myid = this.node.getAttribute("eleid");
		var circleColor = "green";
		if (pttype == "end") {
			var sx = point2.node.getAttribute("sx");
			var sy = point2.node.getAttribute("sy");
			var bb1 = {};
			bb1.x = parseInt(sx);
			bb1.y = parseInt(sy);
			bb1.width = 4;
			bb1.height = 4;
			var bb2 = {};
			bb2.x = att.cx;
			bb2.y = att.cy;
			bb2.width = 4;
			bb2.height = 4;
			console.log("bb1: " + dojo.toJson(bb1));
			console.log("bb2: " + dojo.toJson(bb2));

			if (oldConnRubber != null) {
				oldConnRubber[0].remove();
				oldConnRubber[1].remove();
				if (oldConnRubber[2] != null)
					oldConnRubber[2].remove();
			}
			oldConnRubber = pCanvas.connectionarrow(bb1, bb2, 4);
			oldConnRubber[0].attr("stroke-dasharray", "-");
			oldConnRubber[0].attr("stroke-width", "3");
			oldConnRubber[0].attr("stroke", "orange");
			oldConnRubber[1].attr("stroke", "orange");
		} else if (pttype == "start") {
			var ex = point1.node.getAttribute("ex");
			var ey = point1.node.getAttribute("ey");
			var bb2 = {};
			bb2.x = parseInt(ex);
			bb2.y = parseInt(ey);
			bb2.width = 4;
			bb2.height = 4;
			var bb1 = {};
			bb1.x = att.cx;
			bb1.y = att.cy;
			bb1.width = 4;
			bb1.height = 4;
			console.log("bb1: " + dojo.toJson(bb1));
			console.log("bb2: " + dojo.toJson(bb2));

			if (oldConnRubber != null) {
				oldConnRubber[0].remove();
				oldConnRubber[1].remove();
				if (oldConnRubber[2] != null)
					oldConnRubber[2].remove();
			}
			oldConnRubber = pCanvas.connectionarrow(bb1, bb2, 4);
			oldConnRubber[0].attr("stroke-dasharray", "-");
			oldConnRubber[0].attr("stroke-width", "3");
			oldConnRubber[0].attr("stroke", "orange");
			oldConnRubber[1].attr("stroke", "orange");

		}
		var port = findnearItem(att.cx, att.cy, "20", "Port");
		if (port != null) {
			console.log("near: " + port)
			var item = findNodeById(port);
			var me= findNodeById(myid);
			var otherItem = null ; 
			var circleColor ="green";
				if(me!=null){
					if (pttype == "start"){
						otherItem = findNodeById(me.to);
					}else if (pttype == "end") {
						otherItem = findNodeById(me.from);
					}
					if(otherItem!=null && otherItem.type=="Port"){
						if(otherItem.dtype!=null && otherItem.dtype!=item.dtype){
							circleColor="red";
						}
						
					}
			}
			if (item != null) {
				if (oldConnRubber[2] != null) {
					oldConnRubber[2].remove();
				}
				var it = pCanvas.circle(item.normalizedx, item.normalizedy, 10).attr("stroke", circleColor);
				oldConnRubber[2] = it;
			}
		} else {
			port = findnearItem(att.cx, att.cy, "20", "stream");
			if (port == null) {
				port = findnearItem(att.cx, att.cy, "20", "AnonDef");
			}
			if (port == null) {
				port = findnearItem(att.cx, att.cy, "40", "ceword");
			}
			if (port == null) {
				port = findnearItem(att.cx, att.cy, "40", "Step");
			}
			if (port == null) {
				port = findnearItem(att.cx, att.cy, "100", "module");
			}
			if (port != null) {
				console.log("near: " + port)
				var item = findNodeById(port);
				if (item != null) {
					if (oldConnRubber != null) {
						if (oldConnRubber[2] != null) {
							oldConnRubber[2].remove();
						}
					}
					var it = pCanvas.rect(item.normalizedx - item.r / 2 - 10,item.normalizedy - item.b / 2 - 10, item.r + 20,item.b + 20, 5).attr("stroke", "green");
					oldConnRubber[2] = it;
				}
			}
		}
		var evtdata = {};
		evtdata.id = myid;
		evtdata.pttype = pttype;
		evtdata.type = "ptmoved";
		sendEvent(ptq, [ evtdata ]);

		pCanvas.safari();
	}, cptsup = function() {
		this.animate({
			"fill-opacity" : 1
		}, 500);
		var myid = this.node.getAttribute("eleid");
		var pttype = this.node.getAttribute("pttype");
		var obj = findNodeById(myid)
		console.log("moved:" + pttype + " " + obj.id);
		if (obj.type == "connection") {
			var port = findnearItem(this.attr("cx"), this.attr("cy"), "20",
					"Port");
			console.log("moved near:" + port);
			if (port != null) {
				if (pttype == "end") {
					obj.to = port;
					obj.nodes[1] = port;
				} else if (pttype == "start") {
					obj.from = port;
					obj.nodes[0] = port;
				}
				console.log("Changed connection");
			} else {
				port = findnearItem(this.attr("cx"), this.attr("cy"), "20",
						"stream");
				if (port == null) {
					port = findnearItem(this.attr("cx"), this.attr("cy"), "20",
							"AnonDef");
				}
				if (port == null) {
					port = findnearItem(this.attr("cx"), this.attr("cy"), "40",
							"ceword");
				}
				if (port == null) {
					port = findnearItem(this.attr("cx"), this.attr("cy"), "40",
							"Step");
				}
				if (port == null) {
					port = findnearItem(this.attr("cx"), this.attr("cy"), "100",
							"module");
				}
				if (port != null) {
					if (pttype == "end") {
						obj.to = port;
						obj.nodes[1] = port;
					} else if (pttype == "start") {
						obj.from = port;
						obj.nodes[0] = port;
					}
					console.log("Changed connection");
				} else {
					var p = prompt("Remove connection?");
					if (p != null && p.length > 0) {
						removeNodeById(myid);
					}
				}
			}
			if (oldConnRubber != null) {
				oldConnRubber[0].remove();
				oldConnRubber[1].remove();
				if (oldConnRubber[2] != null)
					oldConnRubber[2].remove();
				oldConnRubber = null
			}

			pCanvas.safari();
			draw();
		}
	};
	point1.drag(cptsmove, cptsdragger, cptsup);
	point2.drag(cptsmove, cptsdragger, cptsup);

}
function addAnimation(obj, effect) {
	var wrap = {};
	wrap["wrapped"] = obj;
	wrap["anim"] = effect;
	animObjects.push(wrap);
}
function removeAnimation(obj) {
	animObjectsToRemove[obj.id] = obj;
}
function cleanAnimation() {
	var temp = new Array();
	for ( var i = 0; i < animObjects.length; i++) {
		var a = animObjects[i];
		if (animObjectsToRemove[a.wrapped.id] == null) {
			temp.push(a);
		} else {
			animObjectsToRemove[a.wrapped.id] = null;
		}
	}
	animObjects = temp;
}
var animThreadPointer = null;
function startAnimationThread(interval) {
	animThreadPointer = setInterval(animThread, interval)
}
function stopAnimationThread() {
	clearInterval(animThreadPointer);
}
function animThread() {
	for ( var i = 0; i < animObjects.length; i++) {
		var a = animObjects[i];
		var anim = a.anim;
		animHandlers[anim](a);

	}
	cleanAnimation();
}
if(supportAnimation){
startAnimationThread(100);
}
animHandlers["blink"] = function(obj) {
	var item = findDrawEleByIdEx(obj.wrapped.id).item;
	var op = item.attr("fill-opacity");
	if (obj.dir == null) {
		obj.dir = -1;
	}
	if (obj.dir == 1) {
		item.attr("fill-opacity", op + .1);
		if (op >= 1) {
			obj.dir = -1;
		}
		console.log(obj.dir + " :: " + op);
	} else {
		obj.dir = -1;
		item.attr("fill-opacity", op - .1);
		if (op <= .1) {
			obj.dir = 1;
		}
		console.log(obj.dir + " :: " + op);
	}
	console.log("Blink: " + obj.wrapped.id);
};

function getElementFromPos(x, y) {
	return (document.elementFromPoint(x, y));
}

dojo.subscribe(geq, null, handleModuleMove);
function handleModuleMove(jdata) {
	var node = findNodeById(jdata.id);
	if (node != null) {
		lastSelectedNode  = node;
		if (jdata.type == 'ssmmovestop') {
			if (jdata.pos != null) {
				console.log("Updating node from module");
				var xx = jdata.pos.x - leftWidth;
				var yy = jdata.pos.y - topWidth;
				if(Math.abs(node.x-node.r/2-xx)<2&&Math.abs(node.y-node.b/2-yy)<2){
				console.log("Module is clicked");
				}else{
				node.x = xx;
				node.y = yy;
				node.r = jdata.pos.w;
				node.b = jdata.pos.h;
				node.x += node.r/2;
				node.y += node.b/2;
				node.normalizedx = node.x;
				node.normalizedy = node.y;
				}
			}
		}
	}
}
function vline(x, y, color) {
	var ln1 = pCanvas.line(x, y, x, 3000);
	ln1.attr({
		"stroke" : color,
		"stroke-width" :.5
	});
	return ln1;
}
function hline(x, y, color) {
	var ln1 = pCanvas.line(x, y, 3000, y);
	ln1.attr({
		"stroke" : color,
		"stroke-width" :.5
	});
	return ln1;
}
function uline(x, y, x2, y2, color) {
	var ln1 = pCanvas.line(x, y, x2, y2);
	ln1.attr({
		"stroke" : color,
		"stroke-width" : .5
	});
	return ln1;
}
function removeItemFromArrayById(arr, id) {
	for ( var i = 0; i < arr.length; i++) {
		var obj = arr[i];
		if (obj.id == id) {
			arr.splice(i, 1);
			return obj;
		}
	}
	return null;
}
function removeItemFromArray(arr, item) {
	for ( var i = 0; i < arr.length; i++) {
		var obj = arr[i];
		if (obj == item) {
			arr.splice(i, 1);
			return obj;
		}
	}
	return null;
}

function angleToRad(angle){
	return  (angle*Math.PI)/180;
}
function rotatePointDeg(pt,cx,cy,angle){
    angle = (angle*Math.PI)/180;
    return rotatePointRad(pt,cx,cy,angle);
    
}
function rotatePointRad(pt,cx,cy,angle){
var p = cloneItem(pt);

  var s = Math.sin(angle);
  var c = Math.cos(angle);

  // translate point back to origin:
  p.x -= cx;
  p.y -= cy;

  // rotate point
  var xnew = p.x * c - p.y * s;
  var ynew = p.x * s + p.y * c;

  // translate point back:
  p.x = xnew + cx;
  p.y = ynew + cy;
  return p;
  }
function getRotDeg(rotate){
	if(rotate!=null &&rotate!="0deg"){
		var rangle=rotate.substr(0,rotate.length-3);
		rangle = parseInt(rangle);
		return rangle;
	}
	return 0;
}

///////////////////////////////////
function getPoi(m){
    var pt1 = {"x":m.x-m.r/2,"y":m.y-m.b/2,id:m.id+"."+0};
    var pt2 = {"x":m.x,"y":m.y-m.b/2,id:m.id+"."+1};
    var pt3 = {"x":m.x+m.r/2,"y":m.y-m.b/2,id:m.id+"."+2};
    var pt4 = {"x":m.x+m.r/2,"y":m.y,id:m.id+"."+3};
    var pt5 = {"x":m.x+m.r/2,"y":m.y+m.b/2,id:m.id+"."+4};
    var pt6 = {"x":m.x,"y":m.y+m.b/2,id:m.id+"."+5};
    var pt7 = {"x":m.x-m.r/2,"y":m.y+m.b/2,id:m.id+"."+6};
    var pt8 = {"x":m.x-m.r/2,"y":m.y,id:m.id+"."+7};
    return [pt1,pt2,pt3,pt4,pt5,pt6,pt7,pt8];
    
}
function getPoly(m){
  var poly = new Array();
    poly.push(
    getPoint(m.x-m.r/2,m.y-m.b/2),
    getPoint(m.x+m.r/2,m.y-m.b/2),
    getPoint(m.x+m.r/2,m.y+m.b/2),
    getPoint(m.x-m.r/2,m.y+m.b/2)
    );
    return poly;
}
function getPolyFromPts(pt1,pt2,pt3,pt4){
  return [pt1,pt2,pt3,pt4];
}
function getPoint(x,y){ return {"x":x,"y":y};};

//////////////////////////////

function sortSteps(){
sortedSteps = new Array();
for(var i=0;i<pData.data.length;i++){
    var g = pData.data[i];
    if(g.type=="Step"){
        sortedSteps.push(g);
    }
}
sortedSteps.sort(function (a,b){
return a.normalizedx - b.normalizedx;
});
for(var i=0;i<sortedSteps.length;i++){
console.log(sortedSteps[i].id);
}
}
function prevStep(id){
var ret = null;
 for(var i=0;i<sortedSteps.length;i++){
  console.log(sortedSteps[i].id);
  if(sortedSteps[i].id==id){
      if(i>0){
          return sortedSteps[i-1];
      }else{
          return ret;
      }
  }
} 
return ret;
}
function nextStep(id){ 
var ret = null;
 for(var i=0;i<sortedSteps.length;i++){
  console.log(sortedSteps[i].id);
  if(sortedSteps[i].id==id){
      if(i<sortedSteps.length-1){ 
          return sortedSteps[i+1];
      }else{
          return ret;
      }
  }
} 
return ret;
}

function sortedStepIndex(obj){ 
	 for(var i=0;i<sortedSteps.length;i++){
	  if(sortedSteps[i].id==obj.id){
	      return i;
	  }
	} 
	return -1;
}

function isPointInPoly(poly, pt){
    for(var c = false, i = -1, l = poly.length, j = l - 1; ++i < l; j = i)
		((poly[i].y <= pt.y && pt.y < poly[j].y) || (poly[j].y <= pt.y && pt.y < poly[i].y))
		&& (pt.x < (poly[j].x - poly[i].x) * (pt.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x)
		&& (c = !c);
	return c;
}

function drawPolyGon(p) {
	var pstr = ("M ");
	for ( var j = 0; j < p.length; j++) {
		pstr += (" " + p[j].x + " " + p[j].y);
		if (j < (p.length - 1)) {
			pstr += ",";
		}
	}
	pstr += "Z";
	console.log(pstr);
	pCanvas.path(pstr).attr("stroke", "orange").attr("stroke-width", 2)
			.toBack();

}


function drawPolyGonXYArrays(xx,yy) {
	var pstr = ("M ");
	for ( var j = 0; j < xx.length; j++) {
		pstr += (" " + xx[j]+ " " + yy[j]);
		if (j < (xx.length - 1)) {
			pstr += ",";	
		}
	}
	pstr =(pstr+ "Z");
	pCanvas.path(pstr).attr("stroke", "orange").attr("stroke-width", 2).toBack();

}

function hlines(xx){
	for ( var j = 0; j < xx.length; j++) {
		hline(0,xx[j],"green");
	}
}
function vlines(yy){
	for ( var j = 0; j < yy.length; j++) {
		vline(yy[j],0,"green");
	}
}

function removeFromGraphWithRelatives(elename){
var grpItems = {};
for(var i=0;i<pData.data.length;i++){
	var obj =pData.data[i];
	if(obj!=null){
		if(obj.id==elename){
			grpItems[obj.id]=obj;
		}
		if((undefined!=obj.grp && obj.grp!=null)||(undefined!=obj.grpid && obj.grpid !=null )){
			if(obj.grp ==elename||obj.id == elename ||(obj.grpid !=null && obj.grpid ==elename)){
				grpItems[obj.id]=obj;
			}
		}
	}
}
var connToRem = {};
for(var i=0;i<pData.data.length;i++){
	var obj =pData.data[i];
	if(obj!=null){
		if(undefined!=obj.type&& obj.type!=null){
			if(obj.type =="connection"){
				if(grpItems[obj.from]!=null||grpItems[obj.to]!=null){
					connToRem[obj.id]=obj;		
				}
			}
		}
	}
}
var clone=new Array();
for(var i=0;i<pData.data.length;i++){
	var obj =pData.data[i];
	if(obj!=null){
		if(grpItems[obj.id]!=null || connToRem[obj.id]!=null){
			console.log("Find an item to remove: "+obj.type+":"+obj.id);
		}else{
			console.log("Find an item not to remove: "+obj.type+":"+obj.id);
			clone.push(obj);
		}
	}
}
pData.data = clone;
draw();
}
function removeRelativeConnFromGraph(elename){
	var grpItems = {};
	for(var i=0;i<pData.data.length;i++){
		var obj =pData.data[i];
		if(obj!=null){
			if(obj.id==elename){
				grpItems[obj.id]=obj;
			}
			if((undefined!=obj.grp && obj.grp!=null)||(undefined!=obj.grpid && obj.grpid !=null )){
				if(obj.grp ==elename||obj.id == elename ||(obj.grpid !=null && obj.grpid ==elename)){
					grpItems[obj.id]=obj;
				}
			}
		}
	}
	var connToRem = {};
	for(var i=0;i<pData.data.length;i++){
		var obj =pData.data[i];
		if(obj!=null){
			if(undefined!=obj.type&& obj.type!=null){
				if(obj.type =="connection"){
					if(grpItems[obj.from]!=null||grpItems[obj.to]!=null){
						connToRem[obj.id]=obj;		
					}
				}
			}
		}
	}
	var clone=new Array();
	for(var i=0;i<pData.data.length;i++){
		var obj =pData.data[i];
		if(obj!=null){
			if(connToRem[obj.id]!=null){
				console.log("Find an item to remove: "+obj.type+":"+obj.id);
			}else{
				console.log("Find an item not to remove: "+obj.type+":"+obj.id);
				clone.push(obj);
			}
		}
	}
	pData.data = clone;
	draw();
	}
function arc(value, total, cx,cy, R) {
    var alpha = 360 / total * value,
        a = (90 - alpha) * Math.PI / 180,
        x = cx + R * Math.cos(a),
        y = cy - R * Math.sin(a),
        color = "hsb(".concat(Math.round(R) / 200, ",", value / total, ", .75)"),
        path;
    if (total == value) {
        path = [["M", cx, cy - R], ["A", cx, cy, 0, 1, 1, 299.99, cy - R]];
    } else {
        path = [["M", cx, cy - R], ["A", R, R, 0, +(alpha > 180), 1, x, y]];
    }
    return {path: path, stroke: color};
}

function pieItem(value, total, cx,cy, R) {
    var alpha = 360 / total * value,
        a = (90 - alpha) * Math.PI / 180,
        x = cx + R * Math.cos(a),
        y = cy - R * Math.sin(a),
        color = "hsb(".concat(Math.round(R) / 200, ",", value / total, ", .75)"),
        path;
    if (total == value) {
        path = "M" +" " +cx+" "+ cy+" "+" L "+" "+cx+" "+(cy-R)+" "+" A"+" "+ cx+" "+cy+" "+0+" "+ 1+" "+1+" "+299.99+" "+(cy - R)+" Z ";
    } else {
        path = "M"+" "+cx+" "+cy+" "+" L "+" "+cx+" "+(cy-R)+" "+" A"+" "+R+" "+R+" "+0+" "+(0+(alpha > 180))+" "+1+" "+x+" "+y+" Z ";
    }
    return {path: path, stroke: color};
}

function getPieAngle(value, total) {
    var alpha = (value/total)*360;
    return alpha;
}

function octagon(x,y,wt,ht, w, h){
	var ps  = "M " +x +" " + y + " L " + ( x + wt ) +" "+ ( y - ht ) + " L " + ( x + wt + w ) +" "+ ( y - ht ) ;
	 ps  += ( " L " +( x + wt + wt + w ) +" " + y + " L " + ( x + wt + wt + w )+ " " + ( y + h)  ) ; 
	 ps += ( " L " + ( x + wt + w) + " " + ( y + h + ht ) +" L " + ( x + wt ) + " " + ( y + h + ht ) + " L " + ( x) +" " + ( y + h ) +" Z ") ;
	var path = pCanvas.path(ps); 
	path.attr("fill", Raphael.getColor());
	 path.attr("stroke", "white");
	 return path;
}

function hexagon(x,y,wt,ht, w, h){
    var ps  = "M " +x +" " + y + " L " + ( x + w ) +" "+ ( y - h ) + " L " + ( x + 2*w ) +" "+ ( y  ) ;
	 ps  += ( " L " +( x + w + w ) +" " + ( y + h)+ " L " + ( x + w )+ " " + ( y + h + h )  ) ; 
	 ps += ( " L " + ( x ) + " " + ( y + h ) +" Z ") ;
	var path = pCanvas.path(ps); 
	path.attr("fill", Raphael.getColor());
	 path.attr("stroke", "white");
	 return path;
}

        

function Hexagon(x,y,wt,ht, w, h){
	this.pathStr = "";
	this.x = x;
	this.y = y;
	this.w = w;
	this.h = h;
	this.wt = wt;
	this.ht = ht;
	this.path  =null;
	this.draw  = function (){
		var x = this.x,y=this.y,wt=this.wt,ht=this.ht, w=this.w, h=this.h;
		var ps  = "M " +x +" " + y + " L " + ( x + w ) +" "+ ( y - h ) + " L " + ( x + 2*w ) +" "+ ( y  ) ;
		 ps  += ( " L " +( x + w + w ) +" " + ( y + h)+ " L " + ( x + w )+ " " + ( y + h + h )  ) ; 
		 ps += ( " L " + ( x ) + " " + ( y + h ) +" Z ") ;
		 this.pathStr  = ps;
		 var path = pCanvas.path(ps); 
		path.attr("fill", Raphael.getColor());
		 path.attr("stroke", "white");
		this.path = path;
		return this;
	}
	this.hexagon1 = function (){
	    var x = this.x - this.w;
	    var y = this.y - this.h -this.h  ;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();
	}
	this.hexagon2 = function (){
		var x = this.x ;
		var y = this.y - this.h - this.h - this.h;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();
	}
	this.hexagon3 = function (){
		var x = this.x + this.w ;
		var y = this.y - this.h  - this.h ;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();
	}
	this.hexagon4 = function (){
		var x = this.x + this.w +this.w ;
		var y = this.y ;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();

	}
	this.hexagon5 = function (){
		var x = this.x + this.w  ;
		var y = this.y + this.h + this.h ;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();

	}
	this.hexagon6 = function (){
		var x = this.x ;
		var y = this.y + this.h + +this.h + this.h ;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();

	}
	this.hexagon7 = function (){
	    var x = this.x - this.w  ;
		var y = this.y + this.h + this.h ;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();

	}
	this.hexagon8 = function (){
		var x = this.x - this.w - this.w ;
		var y = this.y ;
		var hex = new Hexagon(x,y,this.wt,this.ht, this.w, this.h);
		return hex.draw();

	}
}

function Octagon(x,y,wt,ht, w, h){
	this.pathStr = "";
	this.x = x;
	this.y = y;
	this.w = w;
	this.h = h;
	this.wt = wt;
	this.ht = ht;
	this.path  =null;
	this.draw  = function (x,y,wt,ht, w, h){
		var x = this.x,y=this.y,wt=this.wt,ht=this.ht, w=this.w, h=this.h;
		var ps  = "M " +x +" " + y + " L " + ( x + wt ) +" "+ ( y - ht ) + " L " + ( x + wt + w ) +" "+ ( y - ht ) ;
		 ps  += ( " L " +( x + wt + wt + w ) +" " + y + " L " + ( x + wt + wt + w )+ " " + ( y + h)  ) ; 
		 ps += ( " L " + ( x + wt + w) + " " + ( y + h + ht ) +" L " + ( x + wt ) + " " + ( y + h + ht ) + " L " + ( x) +" " + ( y + h ) +" Z ") ;
		var path = pCanvas.path(ps); 
		path.attr("fill", Raphael.getColor());
		 path.attr("stroke", "white");
		this.path = path;
		this.pathStr = ps;
		return this;
	}
	this.octagon1 = function(x,y,wt,ht, w, h){
		var x = this.x - this.w - this.wt;
		var y = this.y - this.ht - this.h ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();
	}

	this.octagon2 = function(x,y,wt,ht, w, h){
		var x = this.x ;
		var y = this.y - this.ht - this.ht - this.h ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();

	}
	this.octagon3 = function(x,y,wt,ht, w, h){
		var x = this.x + this.w + this.wt ;
		var y = this.y - this.ht  - this.h ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();
	}
	this.octagon4 = function(x,y,wt,ht, w, h){
		var x = this.x + this.w + this.wt + this.wt ;
		var y = this.y ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();
	}
	this.octagon5 = function(x,y,wt,ht, w, h){
		var x = this.x + this.w + this.wt ;
		var y = this.y + this.h + this.ht ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();
	}
	this.octagon6 = function(x,y,wt,ht, w, h){
		var x = this.x ;
		var y = this.y + this.h + this.ht + this.ht ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();
	}
	this.octagon7 = function(x,y,wt,ht, w, h){
		var x = this.x - this.w - this.wt ;
		var y = this.y + this.h + this.ht ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();
	}
	this.octagon8 = function(x,y,wt,ht, w, h){
		var x = this.x - this.w - this.wt - this.wt ;
		var y = this.y ;
		var oct = new Octagon(x,y,this.wt,this.ht, this.w, this.h);
		return oct.draw();
	}
	    
}

function removeLastSelectedNode(){
	checkPoint();
	if ( lastSelectedNode.id != null ) {
	  if ( lastSelectedNode.type != "module" ) {
		removeFromGraphWithRelatives ( lastSelectedNode.id ) ;
	}else{
		deleteModuleItemByName ( lastSelectedNode.id ) ;
	}
	   lastSelectedNode = null;
	}
	
}


var stremerXOBReq = null;
function xob () {
    var ua = navigator.userAgent.toLowerCase();
    if (!window.ActiveXObject) {
        return new XMLHttpRequest();
    }
    if (ua.indexOf('msie 5') == -1) {
        return new ActiveXObject("Msxml2.XMLHTTP");
    } else {
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}
function startXobStreamer(){
	stremerXOBReq = xob();
	stremerXOBReq.open("POST", "/contevents", true);
	var old_state = 0;
    var index = 0;
	stremerXOBReq.onreadystatechange = function () {
      //  console.log("State change: " + old_state + " => " + stremerXOBReq.readyState + "\n");
        old_state = stremerXOBReq.readyState;
        var rtlen = stremerXOBReq.responseText.length;
        if (index < rtlen) {
            onRead(stremerXOBReq.responseText.substring(index));
            index = rtlen;
        }
    }
	 stremerXOBReq.send("foo=bar");
}
function onRead (data) {
    console.log("Input: " + data);
    dojo.eval(data);
}


function startSyncing() {
	/*var iFrm = dojo.byId("estreamer");
	dojo.attr(iFrm, "src", getURL("eventURL"));*/
	startXobStreamer();
}
function stopSyncing() {
	/*var iFrm = dojo.byId("estreamer");
	dojo.attr(iFrm, "src", "");*/
	if(stremerXOBReq!=null){
		stremerXOBReq.abort();
	}
	stremerXOBReq = null;
}

function handleDebug(stream, jdata) {
	console.log("Got an streaming event: "+jdata[0].eventType);
	dojo.publish(stream, jdata);
}

function subscribeVizulization(str,fun){
	if(allSubs[str]!=null){
		dojo.unsubscribe(allSubs[str]);
		allSubs[str] = null;
	}
var subcription = dojo.subscribe("VizDataEvent", null, fun);
allSubs[str] = subcription;
}
function loadDynaHTMLBodyScripts(body_el) {
	   dojo.query("script", body_el).forEach(function(node, index, arr){
		    console.debug("Found one script tag: "+node.innerHTML);
		    try{
		    dojo.eval(node.innerHTML);
		    }catch(exp){
		    	console.log("Error execuyting the function :"+exp.message);
		    }
	   });
	   
	}
function filterLayer(ar){
	var ret = new Array();
	for(var i = 0;i<ar.length;i++){
		var obj = ar[i];
		if (layeringEnabled && !layerEnabled(obj.layer)) {
			continue;
		}
		ret.push(obj);
	}
	return ret;
}
function fixConnections(){
	
}

//steps--------------
var sortedSteps = new Array();
function addHorizStep(bb,myid,index,txt){
    addObjectToGraphNoUndo({
		"type" : "Step",
		"text" : txt,
		"x" : bb.x,
		"y" : bb.y,
		"r" : bb.width,
		"b" : bb.height,
		"normalizedx" : bb.x,
		"normalizedy" : bb.y,
		"id" : "word_" + myid,
        "index":index,
        "steptype":"horiz"
	});
}


function addVertStep(bb,myid,index,txt){
    addObjectToGraphNoUndo({
    	"type" : "Step",
		"text" : txt,
		"x" : bb.x,
		"y" : bb.y,
		"r" : bb.width,
		"b" : bb.height,
		"normalizedx" : bb.x,
		"normalizedy" : bb.y,
		"id" : "word_" + myid,
        "index":index,
        "steptype":"vert"
	});

}
function sortSteps(){
	sortedSteps = new Array();
	for(var i=0;i<pData.data.length;i++){
	    var g = pData.data[i];
	    if(g.type=="Step"){
	        sortedSteps.push(g);
	    }
	}
	sortedSteps.sort(function (a,b){
	return a.normalizedx - b.normalizedx;
	});
	for(var i=0;i<sortedSteps.length;i++){
	console.log(sortedSteps[i].id);
	}
	}
function prevStep(id){
	var ret = null;
	 for(var i=0;i<sortedSteps.length;i++){
	  console.log(sortedSteps[i].id);
	  if(sortedSteps[i].id==id){
	      if(i>0){
	          return sortedSteps[i-1];
	      }else{
	          return ret;
	      }
	  }
	} 
	return ret;
	}
	function nextStep(id){ 
	var ret = null;
	 for(var i=0;i<sortedSteps.length;i++){
	  console.log(sortedSteps[i].id);
	  if(sortedSteps[i].id==id){
	      if(i<sortedSteps.length-1){ 
	          return sortedSteps[i+1];
	      }else{
	          return ret;
	      }
	  }
	} 
	return ret;
	}