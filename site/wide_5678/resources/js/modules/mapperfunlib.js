/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var mapperfunlibvars = {};
var proto  = {};
proto.classType="MYClass";
proto.retType="java.lang.String";
proto.bean="mybean";
proto.funName = "myFunction";
proto.param = new Array();
var p1 = {};
p1.index = 0;
p1.dataType="java.lang.String";
proto.param.push(p1);
//getFO(x, y, t, c,type,cn,fn,dataType,classType,id) 
//getPO(x, y, t, c,type,cn,fn,pn,index,dataType,grp,classType) 
function addFunProto(f, a,compname,isui,mx,my,configobj) {
	var uidRandom =	"Random_"+Math.ceil(Math.random()*1000);
	var ca = getFO(mouseX,mouseY,proto.funName,"orange","functionobj",proto.bean,proto.funName,proto.retType,proto.classType,uidRandom);
	ca = findDrawEleById(ca.id).item;
	var start = ca.getBBox().width + 5;
	getPO(mouseX+start,mouseY,p1.dataType+":"+p1.index,"yellow","paramobj",proto.bean,proto.funName,p1.dataType,p1.index,p1.dataType,uidRandom,"");
	
}

mapperfunlib_init = function(){
	var  src = new dojo.dnd.Source("mapperfunlib",{copyOnly:true});
    configs["BuildFun"] = addFunProto;
    comps["BuildFun"] = dummy;
}






