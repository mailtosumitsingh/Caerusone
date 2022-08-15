var impPorts = {};
var polys = new Array();
var removePointFromLayoutArr = new Array();
function drawRoutes() {
	impPorts = {};
	polys = new Array();
	for ( var i = 0; i < pData.data.length; i++) {
		var d = pData.data[i];
		if (d.type == "connection") {
			impPorts[d.from] = d.from;
			impPorts[d.to] = d.to;
		}
	}
	for ( var i = 0; i < pData.data.length; i++) {
		var m = pData.data[i];
		if (m.type == "AnonDef" || m.type == "module") {
			if (m.type == "module") {
				m = getRealPos(m.id);
				var k = m.rotation;
				var cx = m.x, cy = m.y;
				var pts = getPoi(m);
				if (m.rotation > 0) {
					var arr = new Array();
					for ( var l = 0; l < pts.length; l += 2) {
						var p = rotatePointDeg(pts[l], cx, cy, k);
						arr.push(p)
					}
					polys.push(getPolyFromPts(arr[0], arr[1], arr[2], arr[3]));
				} else {
					var temp = new Array();
					for ( var l = 0; l < pts.length; l += 2) {
						temp.push(pts[l]);
					}
					pts = temp;
					polys.push(getPolyFromPts(pts[0], pts[1], pts[2], pts[3]));
				}
			} else {
				var pts = getPoi(m);
				polys.push(getPoly(m));
			}
		}else if (m.type == "Port" && impPorts[m.id] != null) {
			impPorts[m.id] = m;
		}
	}
	getRoute();
}
function getRoute() {
	var req = {};
	var routesAll = new Array();
	for ( var i = 0; i < pData.data.length; i++) {
		var d = pData.data[i];
		if (d.type == "connection") {
			var f = impPorts[d.from];
			var t = impPorts[d.to];
			var rt = {};
			if(f==null || t ==null){
			rt.from = {
				"x" : f.x,
				"y" : f.y,
				"id" : f.id
			};
			rt.to = {
				"x" : t.x,
				"y" : t.y,
				"id" : t.id
			};
			}else{
				var crt = getGraphConnPoints(d);
				rt.from = {
					"x" : crt.f.x,
					"y" : crt.f.y,
					"id" : crt.f.id
				};
				rt.to = {
					"x" : crt.t.x,
					"y" : crt.t.y,
					"id" : crt.t.id
				};				
			}
			routesAll.push(rt);
		}
	}
	for(var i=0;i<routesAll.length;i++){
		var routes = new Array();
		routes.push(routesAll[i]);
	var avoidPts = new Array();
	for(var k=0;k<removePointFromLayoutArr.length;k++){
		avoidPts.push(removePointFromLayoutArr[k]);
	}
	var index = {};
	var avoidPoints = new Array();
	for(var k=0;k<pData.data.length;k++){
		var obj = pData.data[k];
		index[obj.id] = obj;
		if(obj.type=="Layer"){
			if(obj.tags!=null && contains(obj.tags,"avoid")){
				var items = dojo.fromJson(obj.items);
				for(var l=0;l<items.length;l++){
					var toAdd = index[items[l]];
					if(toAdd!=null&& toAdd.x!=null && toAdd.y!=null){
						var pt = {};
						pt.x = toAdd.x;
						pt.y=toAdd.y;
						avoidPoints.push(pt);
					}
				}
			}
		}
	}
	var url = "/site/FindConvexHull";
	var req = {};
	req.pts = dojo.toJson(avoidPoints);
	var data = doGetHtmlSyncWithContent(url, req);
	console.log("DAta" + data);
	var a = dojo.fromJson(data);
	polys.push(a);
	req.rem = dojo.toJson(avoidPts);
	req.routes = dojo.toJson(routes);
	req.polys = dojo.toJson(polys);
	postFormWithContent("/site/RoutingHelper", req, function(res) {
		eval(res);
	});
	}
}


function getGraphConnPoints(connection) {
	var links = connection.nodes;
	var node1 = findNodeById(links[0]);
	var node2 = findNodeById(links[1]);
	var pt1 = null;
	var pt2 = null;

	var txtnd = null;
	var connline = null;
	var comps = new Array();
	if (node1 != null && node2 != null) {
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
				var m = getRealPos(node1.id);
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
		if (ndd2 == null) {
			if(node2.type=="module"){
				var m = getRealPos(node2.id);
		        if(m!=null && m.rotation>0){
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
		bb1.id = node1.id;
		bb2.id = node2.id;
		if(poi1!=null && poi2==null){
			var pt = findnearItemColl(sx2,sy2,poi1);
			bb1.x = pt.x;
			bb1.y = pt.y;
			bb1.id = pt.id;
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
			bb2.id = pt.id;
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
			bb1.id = pt.first.id;
			bb2.id = pt.second.id;
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
		if(debug){
		pCanvas.circle(bb1.x,bb1.y,10).attr("fill","red");
		pCanvas.circle(bb2.x,bb2.y,10).attr("fill","red");
		}
		return {"f":bb1,"t": bb2};
	}
}
function removePointFromLayout(x,y){
	pCanvas.circle(x,y,10).attr("stroke","pink");
	pCanvas.circle(x,y,11).attr("stroke","pink");
	var pt = {
			"x" : x,
			"y" : y,
			"r" : 10,
			"b" : 10
		};
	pt.id = getUniqId();
	pt.type="elimPoint";
	pt.userData=new Array();
	removePointFromLayoutArr.push(pt)
	addObjectToGraph(pt)
	
}