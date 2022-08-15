/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function GetName(){
return js_String("TruncateEvents");
}
function ExecuteTask(){
js_Print("Going to truncate Subscription related tables");
var stmt = new Array();
stmt[0] =  js_String("truncate table events");
stmt[1] = js_String("truncate table event_definition");
var conn  = DBHelper.getInstance().createConnection();
DBHelper.getInstance().executeInTrans(conn,stmt);
DBHelper.getInstance().closeConnection(conn);
return js_String("Successfull");
}