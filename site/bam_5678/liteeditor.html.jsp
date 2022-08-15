<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title>Editor</title>
  <style type="text/css" media="screen">
    body {
        overflow: hidden;
    }
    
    #editor2 { 
        margin: 0;
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }
    #editor { 
   		position: relative;
        width: 100%;
        height: 670px;
       }


.ace_gutter-cell.ace_breakpoint { 
    border-radius: 20px 0px 0px 20px; 
    box-shadow: inset 0px 0px 2px 2px red;
    background: #f0d0d0;
}

    
  </style>
</head>
<body>

<div id="editor">function foo(items) {
    var i;
    for (i = 0; i &lt; items.length; i++) {
        alert("Ace Rocks " + items[i]);
    }
}</div>
        <script type="text/javascript" src="/site/dojo/dojo/dojo.js" djConfig="parseOnLoad: true"></script>
    
<script src="/site/includes/js/ace/ace-uncompressed.js" type="text/javascript" ></script>
<script src="/site/includes/js/ace/theme-eclipse.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/includes/js/ace/mode-javascript.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/includes/js/ace/mode-java.js" type="text/javascript" charset="utf-8"></script>

	<!--  <script type="text/javascript" src="/site/dojo/dojo/dojo.js" ></script>-->
	
			<script type="text/javascript" >
			 <%
            String id  = request.getParameter("id") ;
            String  f  = request.getParameter("f") ;
            String msg = request.getParameter("msg") ;
            out.print("var id  = \""+ id + "\" ; " );
            out.print("var  f  = \""+ f + "\" ; " );
            out.print("var msg = \""+ msg +"\" ; " );
            %>
			 var editor = null;
			function postFormWithContent(urlto,jsonContent,fun) {
				dojo.xhrPost( {
					url : urlto,
					content: jsonContent,
					load : function(response, ioArgs) {
						if(fun!=null)
							fun(response);
					},
					error : function(response, ioArgs) {
						if(fun!=null)
							fun(response);
					}
				});
			}
			function getValue(){
				return editor.getSession().getDocument().getValue();
			}
			function done(){
				if (opener && !opener.closed){
					var editor_data = getValue();
					opener.globals["popupreturn"]=editor_data;
					var msgData = {};
                    msgData.from=f;
                    msgData.target=msg;
                    msgData.id=id;
                    msgData.data=editor_data;
                    window.opener.postMessage(dojo.toJson(msgData), "/");
				    opener.focus();
				  }
				  window.close();
				}
			function compile(){
				var editor_data = getValue();
				var req ={};
				req.code = editor_data;
				req.name=dojo.byId("name").value;
				postFormWithContent("/CompileJava",req,function (res){alert(res);});
			}
	function compileProcess(){
				var editor_data = getValue();
				var req ={};
				req.code = editor_data;
				req.name=dojo.byId("name").value;
				postFormWithContent("/CompileProcess",req,function (res){alert(res);});
			}
			function compileTitan(){
				var editor_data = getValue();
				var req ={};
				req.code = editor_data;
				req.name=dojo.byId("name").value;
				postFormWithContent("/TitanCompile",req,function (res){CKEDITOR.instances.editor1.setData(res)});
			}
			function compileJob(){
				var editor_data = getValue();
				var req ={};
				req.code = editor_data;
				req.name=dojo.byId("name").value;
				postFormWithContent("/CompileJob",req,function (res){alert(res);});
			}
			</script>
<script>
window.onload = function() {
    editor = ace.edit("editor");
    editor.setTheme("ace/theme/eclipse");
    var JavaMode = require("ace/mode/java").Mode;
    editor.getSession().setMode(new JavaMode());
    editor.resize();
   // editor.focus();
    editor.getSession().setUseSoftTabs(true);
	editor.getSession().on('change',function(evt) {
		documentChanged(evt, editor);
	});
		editor.addEventListener("gutterclick", function(e){ 
		    var target = e.htmlEvent.target;
		    if (target.className.indexOf("ace_gutter-cell") == -1) 
		        return; 
		    if (!editor.isFocused()) 
		        return; 
		    if (e.clientX > 25 + target.getBoundingClientRect().left) 
		        return; 
		    var row = e.row;
		    if( e.editor.session.getBreakpoints()[row] ) {
		    	e.editor.session.clearBreakpoint(row);
		    	//breakpointInSourceFile(index, row, false);
		    }
		    else {
		    	e.editor.session.setBreakpoint(row);
		    	//breakpointInSourceFile(index, row, true);
		    }
	});
	
	editor.getSession().on("changeBreakpoint", function(){
		console.log("Breakpoint changes");
	});
	if(opener!=null){
	    editor.getSession().getDocument().setValue(opener.globals["popupreturn"]);
		dojo.byId("name").value = opener.globals["popupreturn_name"];
	    }
	function documentChanged(event, editor) {
    	var delta = event.data;
		var range = delta.range;
		var len, firstRow, f1;

		if (delta.action == "insertText") {
			len = range.end.row - range.start.row;
			firstRow = range.start.column == 0? range.start.row: range.start.row + 1;
		} else if (delta.action == "insertLines") {
			len = range.end.row - range.start.row;
			firstRow = range.start.row;
		} else if (delta.action == "removeText") {
            len = range.start.row - range.end.row;
			firstRow = range.start.row;
		} else if (delta.action == "removeLines") {
			len = range.start.row - range.end.row;
			firstRow = range.start.row;
		}
		
		var breakpoints = editor.session.getBreakpoints();
		var newBreakpoints = [];
		
		var changed = false;
		if (len > 0) {
			for( var index in breakpoints ) {
				var idx = parseInt(index);
				if( idx < firstRow ) {
					newBreakpoints.push(idx);
				}
				else {
					changed = true;
					newBreakpoints.push(idx+len);
				}
			}
        } else if (len < 0) {
        	for( var index in breakpoints ) {
				var idx = parseInt(index);
				
            	if( idx < firstRow ) {
            		newBreakpoints.push(idx);
            	}
            	else if( (index < firstRow-len) && !newBreakpoints[firstRow]) {
            		newBreakpoints.push(firstRow);
            		changed = true;
            	}
            	else {
            		newBreakpoints.push(len+idx);
            		changed = true;
            	}
            }
		}
		
		if( changed ) editor.session.setBreakpoints(newBreakpoints);
    }
}
</script>
			<input type="hidden" id="name" ></input>
			<input type="button" onclick="done()" value="Done"></input>
			<input  type="button" onclick="compile()" value="Compile"></input>
			<input  type="button" onclick="compileProcess()" value="Compile Process"></input>
			<input  type="button" onclick="compileJob()" value="Compile Job"></input>
			<input  type="button" onclick="compileTitan()" value="Compile Titan"></input>
</body>
</html>
