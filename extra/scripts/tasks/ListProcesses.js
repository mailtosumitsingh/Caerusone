/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function GetName(){
return js_String("ListProcesses");
}
function ExecuteTask(req,res){
js_Print("Listing processes");
var sql = req.getParameter("q");
if (sql==null){
	sql = req.getParameter("query");
}
if (sql==null){
	sql = req.getParameter("sql");
}
js_Print(sql);
if(sql ){
return	CommonUtil.getResultJson(sql);
}
return null;
}