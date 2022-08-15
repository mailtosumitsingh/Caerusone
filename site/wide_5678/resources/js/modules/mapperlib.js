/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var str1 = '{"c":"yellow","text":"buildDir","cn":"bean","fn":"","pn":"buildDir","classType":"org.ptg.util.CommonUtil",'+
	'"grp":"","index":0,"textual":true,"dataType":"java.lang.String","type":"paramobj",'+
	'"id":"';
var str2 =		'","r":48,"b":48,"x":';
var str3=',"y":';
var str4 = ',"normalizedx":';
var str5 = ',"normalizedy":'
var str6 =	',"icon":"/images/apply_f2.png"}';
var mapperlibvars = {};
////////////////////////////
var strv1 = '{"c":"yellow","text":"';
var strv2 = '","cn":"';
var strv3 = '","fn":"","pn":"';
var strv4 = '","classType":"';
var strv5 = '","grp":"","index":0,"textual":true,"dataType":"';
var strv6 = '","type":"paramobj","id":"';
////////////////////////////

function addBuildDirVar(f, a,compname,isui,mx,my,configobj) {
	var uid = "";
	if(isui==true){
		var uidRandom =	"Random_"+Math.ceil(Math.random()*1000);
		uid = prompt("Please enter the uid of variable",uidRandom);
		if(uid==null&&uid.length()==0){
			uid = uidRandom;
		}
		var strmv1 = mapperlibvars["bean"+compname];
		var str = strmv1+uid+ str2+mouseX+str3+mouseY+str4+mouseX+str5+mouseY+str6;
		var obj = dojo.fromJson(str);
		addObjectToGraph(obj);
		draw();
	}
}

mapperlib_init = function(){
	var  src = new dojo.dnd.Source("mapperlib",{copyOnly:true});
    configs["buildDir"] = addBuildDirVar;
    comps["buildDir"] = dummy;
    addVarToMapperLib('buildDir','org.ptg.util.CommonUtil','bean','java.lang.String');
}

function addVarToMapperLib(name,classType,instname,dataType){
	var strvar = strv1 +name+ strv2+instname +strv3+name +strv4 +classType+strv5+dataType +strv6;
	mapperlibvars[instname+name]	 = strvar;
}





