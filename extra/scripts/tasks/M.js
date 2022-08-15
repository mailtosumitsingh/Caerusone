/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function GetName(){
return js_String("M");
}
function ExecuteTask(req,res){
js_Print("Listing Methods");
var sql = req.getParameter("c");
var ret = new StringBuilder();
if (sql!=null){
	sql = req.getParameter("class");
}
if (sql==null){
	sql = req.getParameter("classname");
}
if (sql==null){
	sql = req.getParameter("name");
}
js_Print(sql);
if(sql ){
var  a = CommonUtil.getMethods(sql);
for(var  j =0; j<a.size();j++){
	var temp  = a.get(j);
	ret.append("[ "+temp+"] \n");
}
}
return ret;
}