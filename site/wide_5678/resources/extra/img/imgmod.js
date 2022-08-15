/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var imgs = new Array();
var img1 = {};
img1.title="Buy Tomatoes";
img1.id = img1.title;
img1.path = "/images/apply_f2.png";
img1.content= "Please buy some tomoto";
imgs.push(img1);	

var lastDroppedimg = null;
function execImgDrop(f, a,compname,isui,mx,my,configobj) {
	lastDroppedimg = a;
	configobj = {};
	var l=mx,t = my,w="60px",h="40px";
	var code ;
	var thiscomp = null;
	dojo.forEach(imgs, function(oneEntry, index) {
		if(oneEntry.id == compname){
			thiscomp=oneEntry;
		}
	});
		if(thiscomp!=null){
			code = prompt('Enter Code', 'alert(\"OnTimer\");');
			configobj = {"id":getUniqId(),"name":"Image","aid":a==null?0:a.id,"mouseX":mx,"mouseY":my,"t":0,"code":dojo.toJson(code),"src":thiscomp.path,"left":l,"top":t,"width":w,"height":h,"zindex":zindex };
			//createImage(f, a,"Image",false,mx,my,configobj)
			//compDiag.push(configobj);
			var inputVal = {};
			inputVal.title = thiscomp.title;
			inputVal.content = thiscomp.content;
			inputVal.icon = thiscomp.path;
			inputVal.compStatus = "10";
			addEventTypeObject ( "ToDoEvent", "title",inputVal);
		}
}


function imgmod_init(){
	var loc = dojo.byId('dropimg');
	loc.innerHTML = "";
	dojo.forEach(imgs, function(oneEntry, index, array) {
	var temp = dojo.create("div", {
				innerHTML :oneEntry.id+"<img src=\'"+oneEntry.path +"\'></img>"
			}, loc);
	dojo.attr(temp,"class","dojoDndItem");
	dojo.attr(temp,"id",oneEntry.id);
	configs[oneEntry.id] = execImgDrop;
	comps[oneEntry.id] = dummy;	
	});
	var  src = new dojo.dnd.Source("dropimg",{copyOnly:true});
}