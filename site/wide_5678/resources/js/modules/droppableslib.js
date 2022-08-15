/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/


var lastDropped = null;
function execDroppable(f, a,compname,isui,mx,my,configobj) {
	lastDropped = a;
	 dojo.byId("addstaticcomponentid").value = compname;

	postFormWithContent(urlMap.GetStaticComponent,{"name":compname},function (data){
		var id  = dojo.byId("addstaticcomponentid").value;
		var obj = dojo.fromJson(data);
		obj.id=id;
		if(obj.name=="Animation"||obj.name=="Graphics"||obj.name=="Code"){
			try {
				var fun = (new Function("event",obj.txt));
				var event = {};
				event.a = lastDropped;
				fun(event);
			} catch (e) {
				alert(e.message || e);
			}		
		}else{
		compDiag.push(obj);
		var f = comps[obj.name];
		var configFun = configs[obj.name];
		  if(configFun!=null){
			configFun(f,lastDropped,obj.name,false,obj.mx,obj.my,obj);
			if(obj.zindex!=null){
		 	   if(obj.zindex>zindex){
					zindex=obj.zindex;
					zindex+=getZIndex();
				}//if
			 }
		}	
		}
	}
	);
}


droppableslibs_init = function(){
	var loc = dojo.byId('droppableslibs');
	loc.innerHTML = "";
	dojo.xhrGet( {
		url : urlMap.GetStaticComponents,
		load : function(response, ioArgs) {
			var a = dojo.fromJson(response);
			dojo.forEach(a, function(oneEntry, index, array) {
				var temp = dojo.create("div", {
					innerHTML : oneEntry.name
				}, loc);
				dojo.attr(temp,"class","dojoDndItem");
				dojo.attr(temp,"ondblclick","getAndShowStaticComp(\""+oneEntry.name+"\");")
				configs[oneEntry.name] = execDroppable;
			    comps[oneEntry.name] = dummy;	
			});
			var  src = new dojo.dnd.Source("droppableslibs",{copyOnly:true});
		}
	});
	
    

    
}