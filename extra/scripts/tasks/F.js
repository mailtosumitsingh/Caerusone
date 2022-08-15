/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function GetName(){
return js_String("F");
}
function ExecuteTask(req,res){
js_Print("Listing Fields");
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
var  a = CommonUtil.getFields(sql);
var k = a.entrySet().iterator();
while(k.hasNext()){
	var en = k.next();
	ret.append("[ "+en.getKey() +": " +en.getValue()+" ]");
}
}
return ret;
}