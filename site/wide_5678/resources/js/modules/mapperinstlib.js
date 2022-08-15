/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var mapperfunlibvars = {};
var instproto  = {};
instproto.dataType="java.lang.String";
instproto.bean="mystring";
instproto.ctor="String";
//getCO(x, y, t, c,type,cn,dataType,id)
//getFO(x, y, t, c,type,cn,fn,dataType,classType,id) 
//getPO(x, y, t, c,type,cn,fn,pn,index,dataType,grp,classType) 
function addNewInst(f, a,compname,isui,mx,my,configobj) {
	var uidRandom =	"Random_"+Math.ceil(Math.random()*1000);
	instproto.bean = uidRandom;
	var ca = getFO(mouseX,mouseY,instproto.bean,"green","functionobj",
			instproto.bean,instproto.ctor,instproto.dataType,instproto.dataType,uidRandom) ;
}

mapperinstlib_init = function(){
	var  src = new dojo.dnd.Source("mapperinstlib",{copyOnly:true});
    configs["NewString"] = addNewInst;
    comps["NewString"] = dummy;
}






