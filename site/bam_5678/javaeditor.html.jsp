<!--
/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
-->
			<html>
			</html>
			<head><title>Editor</title>
			<script type="text/javascript" src="/site/includes/js/ckeditor/ckeditor.js"></script>
			<script type="text/javascript" src="/site/dojo/dojo/dojo.js" djConfig="parseOnLoad: true"></script>
			<script type="text/javascript" >
			
			<%
			String id  = request.getParameter("id") ;
			String  f  = request.getParameter("f") ;
			String msg = request.getParameter("msg") ;
			out.print("var id  = \""+ id + "\" ; " );
			out.print("var  f  = \""+ f + "\" ; " );
			out.print("var msg = \""+ msg +"\" ; " );
			%>
			
			dojo.addOnLoad(documentReady);
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
			function done(){
				if (opener && !opener.closed){
					var editor_data = CKEDITOR.instances.editor1.getData();
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
					var editor_data = CKEDITOR.instances.editor1.getData();
					var req ={};
					req.code = editor_data;
					req.name=dojo.byId("name").value;
					postFormWithContent("/CompileJava",req,function (res){alert(res);});
				}
		function compileProcess(){
					var editor_data = CKEDITOR.instances.editor1.getData();
					var req ={};
					req.code = editor_data;
					req.name=dojo.byId("name").value;
					postFormWithContent("/CompileProcess",req,function (res){alert(res);});
				}
				function compileTitan(){
					var editor_data = CKEDITOR.instances.editor1.getData();
					var req ={};
					req.code = editor_data;
					req.name=dojo.byId("name").value;
					postFormWithContent("/TitanCompile",req,function (res){CKEDITOR.instances.editor1.setData(res)});
				} 
				function compileJob(){
					var editor_data = CKEDITOR.instances.editor1.getData();
					var req ={};
					req.code = editor_data;
					req.name=dojo.byId("name").value;
					postFormWithContent("/CompileJob",req,function (res){alert(res);});
				}


			function documentReady(){
				dojo.byId("editor1").value = 	opener.globals["popupreturn"];
				dojo.byId("name").value = opener.globals["popupreturn_name"];
			}
			</script>
			</head> 
			<body >
			<textarea class="ckeditor" cols="500" id="editor1" name="editor1" rows="400">&lt;p&gt;This is some &lt;strong&gt;sample text&lt;/strong&gt;. You are using &lt;a href="http://ckeditor.com/"&gt;CKEditor&lt;/a&gt;.&lt;/p&gt;</textarea>
			<input type="hidden" id="name" ></input>
			<input type="button" onclick="done()" value="Done"></input>
			<input  type="button" onclick="compile()" value="Compile"></input>
			<input  type="button" onclick="compileProcess()" value="Compile Process"></input>
			<input  type="button" onclick="compileJob()" value="Compile Job"></input>
			<input  type="button" onclick="compileTitan()" value="Compile Titan"></input>
			</body>
			</html> 
