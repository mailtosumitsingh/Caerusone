/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var mapperfunlibvars = {};
var varproto  = {};
varproto.dataType="java.lang.String";
//getCO(x, y, t, c,type,cn,dataType,id)
//getFO(x, y, t, c,type,cn,fn,dataType,classType,id) 
//getPO(x, y, t, c,type,cn,fn,pn,index,dataType,grp,classType) 
function addNewVar(f, a,compname,isui,mx,my,configobj) {
	var uidRandom =	"Random_"+Math.ceil(Math.random()*1000);
	var ca = getCO(mouseX,mouseY,uidRandom,"green","classobj",uidRandom,varproto.dataType,uidRandom);
}

mappervarlib_init = function(){
	var  src = new dojo.dnd.Source("mappervarlib",{copyOnly:true});
    configs["newvar"] = addNewVar;
    comps["newvar"] = dummy;
}






