/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function GetName(){
return js_String("Columns");
}
function ExecuteTask(req,res){
js_Print("Find Columns");
var t = req.getParameter("t");
if (t==null){
	t = req.getParameter("table");
	if (t==null){
	return "Please Specify Table a s t param";
	}
}
var a = DBHelper.getInstance().getColumnNames("house","house",t);
return CommonUtil.jsonFromArray(a);

}