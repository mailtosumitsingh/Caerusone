/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
var cfg =null;
var old =null;


var configdlg = null;
function dynaDlg(cfg/* array of required values */,oldval,okfun,dlgTitle, width,okstr){
	if(dlgTitle==null)
		dlgTitle="Please enter values";
	if(okstr==null)
		okstr="Ok";

	if(configdlg ==null){
	configdlg =new dijit.Dialog({
        title: dlgTitle,
        style: width
    });
}	
 if(cfg==null || cfg.length<1)
         configdlg.attr("content", "No Extra Configuration.");
 else{
	 var cntn  ="";
	 var temp = "<div id='dlgcnt' dojoType='dijit.layout.ContentPane' class='cpane' >"
		 temp +=cntn;
	 	 cntn = temp;
         cntn  +="<form > <table>";
             if(cfg!=null){
             for(var i=0; i<cfg.length; i++){
            	 var a = cfg[i];
            	 var val = oldval==null?"":oldval[i];
            	 if(val==null) val= "";
            	 val = ""+val;
            	 var items = val.split(":");
            	 var sel = false;
            	 if(items!=null&&items.length>1)
            		 sel =true;
            	 if(sel){
            		 cntn  +="<tr>";
            		 cntn+="<td><label for=\"id"+a+"\">"+a+"</label></td>";
            		 cntn+="<td><select value=\"\" name=\"id"+a+"\" id=\"id"+a+"\" >";
            		 for(var k in items){
            			 cntn  +=("<option>"+items[k]+"</option>");
            		 }
            		 cntn+="</td></select>";
            		 cntn  +="<tr>";
            	 }else{
            		 cntn  +="<tr>";
            		 cntn+="<td><label for=\"id"+a+"\">"+a+"</label></td>";
            		 cntn +="<td><input type=text id=\"id"+a+"\" name=\""+a+"\" value=\""+val+"\"></input></td>";
            		 cntn  +="<tr>";
            	 }
             }
        
        	 cntn +="<td><input type=\"button\" name=\""+"SaveExtraConfigBtn"+"\" onclick=\""+okfun+"configdlg.hide();\"  value=\""+okstr+"\"></input></td>"
        	 cntn +="<td><input type=\"button\" name=\""+"CloseCfgDlg"+"\" onclick=\"configdlg.hide();\"  value=\""+"Cancel"+"\"></input></td>"
        	 cntn  +="</tr>";
        	 cntn  +="</form></table>";
        	 cntn+="</div><div id='hand1' dojoType='dojox.layout.ResizeHandle' targetId='dlgcnt'></div> ";
        	 configdlg.attr("content", cntn);
         }
	 }
         configdlg.show();
         
  
}
function getDlgValues(configdlg,cfg){
	var a = configdlg.getValues();
	console.log("dlgvalues: "+dojo.toJson(a));
	var mycfg = {};
    if(cfg!=null){
    	 for(var i=0; i<cfg.length; i++){
            var valh = dojo.byId("id"+cfg[i])
            if(valh!=null){
            	var val =null;
            if(valh.type=="text"){
        	val = valh.value;
        	mycfg[cfg[i]] = val;
            }else if(valh.type=="select-one"){
                var sidx = valh .selectedIndex;
                val = valh .options[sidx].text;
             }
            if(val!=null){
        	mycfg[cfg[i]] = val;
        	}else{
        		mycfg[cfg[i]] = null;
        	}
       
        	}
    	 }
    }
    if(cfg!=null){
    return mycfg
    }else{
    	return a;
    }
}

function testEventFromUser(){
	cfg= new Array();
	old = new Array();
	getEventProp(null,function (res){
		var a = dojo.fromJson(res);
		for(var i = 0;i<a.length;i++){
			var def = a[i];
			if(def.searchable==1){
			cfg.push(def.name);
			old.push("");
			}
			dynaDlg(cfg,old,'sendTestEvent(getDlgValues(configdlg,cfg));');
		}
		
	});
}
function sendTestEvent(evt){
	var stream = lastSelectedNode;
	var url ="/"+stream.name;
	postFormWithContent(url,evt,function (res){alert(res);});
}
function testEventAutomated(){
	cfg= new Array();
	old = new Array();
	getEventProp(null,function (res){
		var a = dojo.fromJson(res);
		for(var i = 0;i<a.length;i++){
			var def = a[i];
			if(def.searchable==1){
			cfg.push(def.name);
			old.push("");
			}
			dynaDlg(cfg,old,'sendTestEventAutomated(getDlgValues(configdlg,cfg));');
		}
		
	});
}
function sendTestEventAutomated(evt){
	var stream = lastSelectedNode;
	evt.eventType=stream.eventType;
	var url = getURL("SaveEventObject");
	var tosave = {"tosave":dojo.toJson(evt),"stream":stream.name};
	postFormWithContent(url,tosave,function (res){alert(res);});
}
function dynaHtmlDlg(cntn,dlgTitle, width,okfun,okstr){
	if(dlgTitle==null)
		dlgTitle="Please enter values";
	if(okstr==null)
		okstr="Ok";


	if(configdlg ==null){
	configdlg =new dijit.Dialog({
        title: dlgTitle,
    });
}	
 if(cntn==null || cntn.length<1){
         configdlg.attr("content", "Blank Page!.");
 }else{
	 var temp = "<div id='dlgcnt' dojoType='dijit.layout.ContentPane' class='cpane' >"
		 temp +=cntn;
	 cntn = temp;
	 cntn  +="<form > <table>";
	 cntn  +="<tr>";
	 cntn +="<td><input type=\"button\" name=\""+"SaveExtraConfigBtn"+"\" onclick=\""+okfun+"configdlg.hide();\"  value=\""+okstr+"\"></input></td>"
	 cntn +="<td><input type=\"button\" name=\""+"CloseCfgDlg"+"\" onclick=\"configdlg.hide();\"  value=\""+"Cancel"+"\"></input></td>"
	 cntn  +="</tr>";
	 cntn  +="</form></table>";
	 cntn+="</div><div id='hand1' dojoType='dojox.layout.ResizeHandle' targetId='dlgcnt'></div> ";
         configdlg.attr("content", cntn);
	 }
 		configdlg.resizable = true;
         configdlg.show();
}
function msgDlg(msg){
	 dynaHtmlDlg(msg,"Message", null,"","ok");
}
function resultMsgDlgObj(m){
	var cm = "";
	cm+=("<table>")
	for(var i in m){
		cm+=("<tr>")
		cm+=("<td>")
		cm+=("<b>"+i+"</b>");
		cm+=("</td>")
		cm+=("<td>")
		cm+=("&nbsp;&nbsp;&nbsp;<i>"+m[i]+"</i>");
		cm+=("</td>")
		cm+=("</tr>")
	}
	cm+=("</table>")
	 dynaHtmlDlg(cm,"Message", null,"","ok");
	
}
function resultMsgDlgObjResultOnly(m){
	var cm = "";
	cm+=("<table>")
	for(var i in m){
		cm+=("<tr>")
		cm+=("<td>")
		cm+=("&nbsp;&nbsp;&nbsp;<i>"+m[i]+"</i>");
		cm+=("</td>")
		cm+=("</tr>")
	}
	cm+=("</table>")
	 dynaHtmlDlg(cm,"Message", null,"","ok");
}
function resultMsgDlg(msg){
	var m = dojo.fromJson(msg);
	resultMsgDlgObj(m);
}

//////////////////////////
function getStreamPageDynaURL(urlorig){
	cfg= new Array();
	old = new Array();
	getEventProp(null,function (res){
		var a = dojo.fromJson(res);
		for(var i = 0;i<a.length;i++){
			var def = a[i];
			if(def.searchable==1){
			cfg.push(def.name);
			old.push("");
			}
			var cmd = 'getEventURL(getDlgValues(configdlg,cfg),\''+urlorig+'\');';
			dynaDlg(cfg,old,cmd);
		}
		
	});
}
function getEventURL(evt,url){
	var stream = lastSelectedNode;
	evt.eventType=stream.eventType;
 	var url  = prompt("Dynamic url :",url+"&tosave="+dojo.toJson(evt));
}
function getFormContent(name,fun){
	if (name==null)
		name= lastSelectedNode.id;
	var mcfg= new Array();
	var mold = new Array();
	var stream = findNodeById(name);
	if(stream.type!="stream")
		return;
	else 
		name = stream.eventType;
	getEventProp(name,function (res){
		var params = "";
		var a = dojo.fromJson(res);
		for(var i = 0;i<a.length;i++){
			var def = a[i];
			if(def.searchable==1){
			mcfg.push(def.name);
			mold.push("");
			params+=def.name;
			params+=";";
			}
			
		}
		var cntn  ="";
		var frmid = "dlgcnt"+Math.ceil(Math.random()*3000);
		 var temp = "<div id='cntpane"+frmid+"' dojoType='dijit.layout.ContentPane' class='cpane' >"
			 temp +=cntn;
		 	 cntn = temp;
	         cntn  +='<form  id="'+frmid+'"> <table>';
	             if(mcfg!=null){
	             for(var i=0; i<mcfg.length; i++){
	            	 cntn  +="<tr>";
	            	 var a = mcfg[i];
	            	 var val = mold==null?"":mold[i];
	            	 cntn+="<td><label for=\"id"+a+"\">"+a+"</label></td>";
	            	 cntn +="<td><input type='text' id=\""+frmid+"_id"+a+"\" name=\""+a+"\" value=\""+val+"\"></input></td>" 
	             }
	        	 cntn  +="<tr>";
	        	 cntn +="<td><input type=\"button\" name=\""+"SaveExtraConfigBtn"+"\" onclick=\""+"sendFormToStream(\'"+frmid+"\',\'"+name+"\',\'"+stream.id+"\');"+"\"  value=\""+"Send"+"\"></input></td>"
	        	  cntn+='<input type=\"hidden\" id="params'+frmid+'" value="'+params+'" ></input>';
	        	 cntn  +="</tr>";
	        	 cntn  +="</table></form>";
	        	 cntn+="</div><div id='handle"+frmid+"' dojoType='dojox.layout.ResizeHandle' targetId='cntpane"+frmid+"'></div> ";
		fun (cntn);
	             }
	});

	
}
function getFormContentWithPage(name,fun,page){
	if (name==null)
		name= lastSelectedNode.id;
	var mcfg= new Array();
	var mold = new Array();
	var stream = findNodeById(name);
	if(stream.type!="stream")
		return;
	else 
		name = stream.eventType;
	getEventProp(name,function (res){
		var params = "";
		var a = dojo.fromJson(res);
		for(var i = 0;i<a.length;i++){
			var def = a[i];
			if(def.searchable==1){
			mcfg.push(def.name);
			mold.push("");
			params+=def.name;
			params+=";";
			}
			
		}
		var cntn  ="";
		var frmid = "dlgcnt"+Math.ceil(Math.random()*3000);
		 var temp = "<div id='cntpane"+frmid+"' dojoType='dijit.layout.ContentPane' class='cpane' >"
			 temp +=cntn;
		 	 cntn = temp;
	         cntn  +='<form  id="'+frmid+'"> <table>';
	             if(mcfg!=null){
	             for(var i=0; i<mcfg.length; i++){
	            	 cntn  +="<tr>";
	            	 var a = mcfg[i];
	            	 var val = mold==null?"":mold[i];
	            	 cntn+="<td><label for=\"id"+a+"\">"+a+"</label></td>";
	            	 cntn +="<td><input type='text' id=\""+frmid+"_id"+a+"\" name=\""+a+"\" value=\""+val+"\"></input></td>" 
	             }
	        	 cntn  +="<tr>";
	        	 cntn +="<td><input type=\"button\" name=\""+"SaveExtraConfigBtn"+"\" onclick=\""+"sendFormToStreamPage(\'"+frmid+"\',\'"+name+"\',\'"+stream.id+"\',\'"+page+"\');"+"\"  value=\""+"Send"+"\"></input></td>"
	        	  cntn+='<input type=\"hidden\" id="params'+frmid+'" value="'+params+'" ></input>';
	        	 cntn  +="</tr>";
	        	 cntn  +="</table></form>";
	        	 cntn+="</div><div id='handle"+frmid+"' dojoType='dojox.layout.ResizeHandle' targetId='cntpane"+frmid+"'></div> ";
		fun (cntn);
	             }
	});

	
}
function sendEventToStream(name,evt){
	var url = getURL("SaveEventObject");
	var tosave = {"tosave":dojo.toJson(evt),"stream":name};
	postFormWithContent(url,tosave,function (res){alert(res);});
}
function sendFormToStream(frmid,eventtype,stream){
	var evt={};
	var params  = dojo.byId('params'+frmid).value;
	var para = params.split(";");
	for(var temp in para){
		if(temp!=null&&para[temp].length>0){
		var val = dojo.byId(frmid+"_id"+para[temp]).value;
		if(val.length>0)
			evt[para[temp]]=val;
		}
	}
	evt.eventType=eventtype;
	var url = getURL("SaveEventObject");
	var tosave = {"tosave":dojo.toJson(evt),"stream":stream};
	postFormWithContent(url,tosave,function (res){alert(res);});
}

function sendFormToStreamPage(frmid,eventtype,stream,page){
	var evt={};
	var params  = dojo.byId('params'+frmid).value;
	var para = params.split(";");
	for(var temp in para){
		if(temp!=null&&para[temp].length>0){
		var val = dojo.byId(frmid+"_id"+para[temp]).value;
		if(val.length>0)
			evt[para[temp]]=val;
		}
	}
	evt.eventType=eventtype;
	var url = getURL("ServerUrl")+stream+"?page="+page;
	var tosave = {"tosave":dojo.toJson(evt)};
	postFormWithContent(url,tosave,function (res){alert(res);});
}