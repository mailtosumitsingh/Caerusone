/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function execute(machinfo,taskinfo,varinfo,tasktempl,wrap,exe){
CommonUtil.dump(machinfo);
CommonUtil.dump(taskinfo);
CommonUtil.dump(varinfo);
CommonUtil.dump(tasktempl);

CommonUtil.dump(wrap);

CommonUtil.dump(exe);


return true;
}

function name(){

return "StringTask";
}