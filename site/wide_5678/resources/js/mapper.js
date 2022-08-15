/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

var node= lastSelectedNode;
dojo.xhrGet( {
				url : getURL("GETEventDefinition")+ node.id,
				load : function(response, ioArgs) {
					alert(response);
                                         addprop(node,dojo.fromJson(response));
					console.log(response)
					return response;
				},
				error : function(response, ioArgs) {
					var a = dojo.fromJson(response);
					console.log("Failed to retrive processtypes" + a.result);
					return response;
				}
			});
var x,y;
function addprop(node,toDraw){
var cnt = toDraw.length;
var d = 2*Math.PI/cnt
var angle = 0;
var distance = 140;
for (i in toDraw){
	y = Math.round( 300 + distance * Math.sin( angle ) );
        x = Math.round( 300 + distance * Math.cos( angle ) );
	var subs = new Array();
	
var temp2 = pCanvas.text(x,y,toDraw[i].name)
angle = angle +d;				
}
}