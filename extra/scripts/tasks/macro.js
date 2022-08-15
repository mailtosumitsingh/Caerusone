/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function GetName(){
return js_String("macro");
}
function ExecuteTask(req,res){
js_Print("Find macro");
var t = req.getParameter("m");
var f = "macro_"+t+".vm";
var m  = new HashMap();
m.put("req",req);
m.put("res",res);
m.put("spring",new SpringHelper().getClass());
m.put("CommonUtil",new CommonUtil().getClass());
m.put("DBHelper",DBHelper().getInstance().getClass());
var str =  VelocityHelper.burnTemplate(m,f);
return str.toString();
}