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
				    opener.focus();
				  }
				  window.close();
				}
			function SaveProcessInfo(){
					var editor_data = CKEDITOR.instances.editor1.getData();
					var req ={};
					req.code = editor_data;
					req.name=dojo.byId("procname").value;
					req.shortname=dojo.byId("procshortname").value;
					req.icon=dojo.byId("procicon").value;
					postFormWithContent("/SaveProcInfos",req,function (res){alert(res);});
				}
			function documentReady(){
				//dojo.byId("editor1").value = 	
			}
			</script>
			</head> 
			<body >
			<p>
	&nbsp;</p>
<form method="get" name="NewProcessorInfo">
	<p>
		Name:&nbsp; <input id="procname" name="procname" type="text" value="org.ptg.processors.JavaProcess"/></p>
	<p>
		ShortName: <input id="procshortname" name="procshortname" type="text" /></p>
	<p>
		Icon: <input id="procicon" name="procicon" type="text" value="/site/images/java.png" ></p>
	<p> 
		Code:<textarea class="ckeditor" cols="80" id="editor1" name="editor1" rows="180"><p>
	<span style="color:#008000;">import <span data-scayt_word="org.ptg.util.AbstractIProcessor" data-scaytid="72">org.ptg.util.AbstractIProcessor</span> ;</span></p>
<p>
	<span style="color:#008000;">import java.util.Map;<br />
	import org.apache.camel.Exchange;<br />
	import org.ptg.events.EventDefinition;<br />
	import org.ptg.events.EventDefinitionManager;<br />
	import org.ptg.stream.Stream;<br />
	import org.ptg.stream.StreamManager;</span></p>
<p>
	<span style="font-size:18px;">public&nbsp; class&nbsp; <span style="color:#ff0000;"><span style="background-color: rgb(255, 255, 0);">CLASS&nbsp;</span></span>&nbsp; extends <span style="color:#ffa07a;">AbstractIProcessor</span>{</span></p>
<p>
	&nbsp;</p>
<p>
	<span style="font-size:18px;">&nbsp;&nbsp;&nbsp; public void childAttach() throws GenericException {<span _fck_bookmark="1" style="display: none;">&nbsp;</span></span></p>
<p>
	<span style="font-size: 18px;"><span style="color:#ee82ee;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; /*Please add child attach code here*/</span><br />
	<span _fck_bookmark="1" style="display: none;">&nbsp;</span>&nbsp;&nbsp; &nbsp;}</span></p>
<p>
	<span style="font-size: 18px;">&nbsp;&nbsp;&nbsp; public void childProcess(Exchange msg)&nbsp; throws Exception{</span><br />
	<span style="font-size: 18px;"><span style="color:#ee82ee;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; /*Please add child process code here*/</span></span><span style="font-size:18px;">&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<br />
	&nbsp;&nbsp; &nbsp;}</span></p>
<p>
	<span style="font-size:18px;">&nbsp;&nbsp;&nbsp; public String getConfigOptions() {</span></p>
<p>
	<span style="font-size: 18px;"><span style="color:#ee82ee;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; /*configuration goes&nbsp; here*/</span></span><br />
	<span style="font-size: 18px;"> &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; return &quot;[\&quot;p1\&quot;,\&quot;p2\&quot;,\&quot;p3\&quot;]&quot;;<br />
	&nbsp;&nbsp; &nbsp;}</span></p>
<p>
	<span style="font-size:18px;">}</span></p>
</textarea></p>
	<p>
		&nbsp;</p>
</form>
			
			
			<input type="hidden" id="name" ></input>
			<input type="button" onclick="done()" value="Done"></input>
			<input  type="button" onclick="SaveProcessInfo()" value="Save Processor Info"></input>
			</body>
			</html> 
