/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
function GetName(){
return js_String("FindGraph");
}
function ExecuteTask(req,res){
js_Print("Find Graph");
var sql = req.getParameter("p");
if (sql==null){
	sql = req.getParameter("path");
}
if (sql==null){
	sql = req.getParameter("jsonpath");
}

if(sql!=null)
js_Print(sql);
if(sql ){
	var buff = new StringBuilder();
	var a = DBHelper.getInstance().getStringList("select graph from graphs");
	var aa = a.toArray()
	for( i in aa ){
		var temp = CommonUtil.getJsonPathVal(aa[i],sql)
		if(temp!='false')
		buff.append(temp);
	}
	
return	buff;
}
return null;
}