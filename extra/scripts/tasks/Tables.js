/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function GetName(){
return js_String("Tables");
}
function ExecuteTask(req,res){
js_Print("Find Tables");
var a = DBHelper.getInstance().getTableNames("house","house","");
return CommonUtil.jsonFromArray(a);

}