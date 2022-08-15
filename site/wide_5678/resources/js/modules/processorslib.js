/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var processorsList= {};

function addProcProto(f, a,compname,isui,mx,my,configobj) {
	var uidRandom =	prompt("Enter Process Name : ","Random_"+Math.ceil(Math.random()*1000));
	var clz =processorsList[compname].clz; 
	var q=processorsList[compname].q;
	var newNode = createNodeObj("Process"+uidRandom,mx,my,clz,q);
	newNode.icon = processorsList[compname].icon;
	addObjectToGraph(newNode);
	var sNode = createStreamItem("Stream"+uidRandom,mx-20,my-80,"","Process"+uidRandom,"","");
	addObjectToGraph(sNode);
	normalizeData();
	draw();
	
}

processorslib_init = function(){
    var loc = dojo.byId('processorslib');
	loc.innerHTML = "";
	dojo.xhrGet( {
		url : "/site/GetProcInfos",
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				var temp = dojo.create("div", {
					innerHTML :oneEntry.shortName+"<img src=\'"+oneEntry.icon+"\'></img>"
				}, loc);
				dojo.attr(temp,"class","dojoDndItem");
				dojo.attr(temp,"id",oneEntry.shortName);
				dojo.attr(temp,"class","dojoDndItem");
				configs[oneEntry.shortName] = addProcProto;
			    comps[oneEntry.shortName] = dummy;	
			    var dada ={};
			    dada.clz = oneEntry.name;
			    dada.q = oneEntry.code==null?"":oneEntry.code.value;
			    dada.icon = oneEntry.icon;
			    processorsList[oneEntry.shortName] = dada;
			});
			var  src = new dojo.dnd.Source("processorslib",{copyOnly:true});
		}
	});
}






