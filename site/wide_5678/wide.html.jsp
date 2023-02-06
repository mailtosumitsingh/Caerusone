	<%@page import="org.ptg.plugins.IPluginMenu"%>
<%@page import="org.ptg.plugins.IPlugin"%>
<%@page import="org.ptg.plugins.IPluginManager"%>
<%@ page
	import="org.ptg.util.*,org.ptg.util.db.*,java.util.*,org.ptg.http2.handlers.*,org.ptg.util.TaskSpec,java.util.*	,org.ptg.http2.handlers.GetMenuGroup"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

-webkit-transform:rotate(30deg);
-->
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html;  charset=windows-1250">
<meta name="apple-mobile-web-app-capable" content="yes">
<title>WIDE - Web IDE</title>
<script src="/site/includes/js/ace/ace.js" type="text/javascript"
	charset="utf-8"></script>
<script src="/site/includes/js/ace/theme-eclipse.js"
	type="text/javascript" charset="utf-8"></script>
<script src="/site/includes/js/ace/mode-javascript.js"
	type="text/javascript" charset="utf-8"></script>
<script src="/site/includes/js/ace/mode-java.js" type="text/javascript"
	charset="utf-8"></script>
<script src="/site/d3js/d3.v3.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/d3.csv.js" type="text/javascript" charset="utf-8"></script>	
<script src="/site/d3js/contour.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/hexbin.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/sankey.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/d3box.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/gauge.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/bullet.js" type="text/javascript" charset="utf-8"></script>

    
<script type="text/javascript" src="/site/dojo/dojo/dojo.js"
	djConfig="parseOnLoad: true"></script>
<script type="text/javascript">

		var debug  = false;
		var wideconfig = {
			"bkimage":	"",
			"showgrid": "",
			"load_design_on_open":"false",
			"load_design_with_graph":"false",//<--to remove this
			"load_graph_on_design_open":"false", //<--to keep this
			"mapping_level":"10", //<--to keep this
			"rotation_step":"10",
			"trace_ele":"1",
			"stepMoverSize":"10",
			"ieversion":"9",
			"cross_hair":"true",
			"global_canvas":"true",
			"incr_zindex":"false",
			"highlite_ports":"true",
			"graph_type" : "JavaCode",
			"units":"mm",
			"material":"ALUM",
			"pixelsPerUnit":"20",
			"depth":".6",
			"offsetx":"0",
			"offsety":"0",
			"toolsizeinmm":"5",
			"modelheightmm":"5",
			"modelheightinch":"5",
			"iterstepmm":".1",
			"iterstepinch":".1",
					
			
		};
		var wideconfigtoUse="Default";

		var ctrl = false;
		var shift = false;
		var alt = false;
		var cpBuffer = null;
		var cw = true,pw=true,hw=true,dw=true;
		var lastDropModule =null;
		var lastDropModuleGrp = null;
				  
		var urlMap = {};
		var dynaMod = true;
		urlMap.eventURL = "/contevents";
		urlMap.SiteUpTestUrl="/SiteUpTest?pageid=test";
		urlMap.ServerUrl = "http://localhost:8095/";
		urlMap.deployURL = "/site/SaveDeploy";
		urlMap.iconURL = "/site/icons.json";
		urlMap.HTMLF2 = "/site/images/all/html_f2.png";
		urlMap.SHOWPAGES = "/cmd?cmd=macro&m=showpages&c=";
		urlMap.ARIALSVG="/site/bam_5678/arial.svg";
		urlMap.GETGRAPH = "/site/GetGraph";
		urlMap.SERVICEBASE = "/";/*originally itwas: /myhtml/*/
		urlMap.PROCDOC = "/site/GetProcDoc";
		urlMap.ANONCONFIG = "/site/GetAnonConfig";
		urlMap.PROCESSJSON = "/site/getprocess.json";				
		urlMap.GETGRAPHS ="/site/GetGraphs";
		urlMap.GETEVENTS ="/site/GetEvents";
		urlMap.GETSTEAMS ="/site/GetStreams";
		urlMap.GETSTEAMDefinition ="/site/GetStreamDefinition";
		urlMap.GETEventDefinition ="/site/GetEventDefinition?EventType=";
		urlMap.ReprocessRegions = "/site/ReprocessRegions";
	
		urlMap.NEWPAGE ="/site/newpage";
		urlMap.RUNCMD ="/site/cmd?cmd=";
		urlMap.CompileClosure = "/site/CompileClosure";
		urlMap.SETCACHE = "/site/SetCache";
		urlMap.GETCACHE = "/site/GetCache";
		urlMap.HistoricalGraphs="/site/GetHistoricalGraphs?name=";
		urlMap.HistoricalGraph="/site/GetHistoricalGraph?graphid=";
		urlMap.ServerScript="/site/scriptcmd";
		urlMap.CompileProcess="/site/CompileProcess";
		urlMap.CompileJava="/site/CompileJava";
		urlMap.ValidateProcess = "/site/ValidateProcess";
		urlMap.CompileGraphOnServer = "/site/CompileGraphOnServer";
		urlMap.SetVar = "/site/SetVar";
		urlMap.CompileCamelGraph = "/site/CompileCamelGraph";
		urlMap.AddRoutesToEngine = "/site/AddRoutesToEngine";
		urlMap.RestartRoutingEngine = "/site/RestartRoutingEngine";
		urlMap.StartRoutingEngine = "/site/StartRoutingEngine";
		urlMap.StopRoutingEngine = "/site/StopRoutingEngine";
		urlMap.FindItemsInRegion="/site/FindItemsInRegion";
		urlMap.FindItemsViaIntersection="/site/FindItemsViaIntersection";
		urlMap.MagicSpell1="/site/MagicSpell1";
		urlMap.MagicSpell2="/site/MagicSpell2";
		urlMap.MagicSpell3="/site/MagicSpell3";
		urlMap.SaveEventObject="/site/SaveEventObject";
		urlMap.CompileMapper="/site/CompileMapper";
		urlMap.CompileMapper2="/site/CompileMapper2";
		urlMap.GetBeanDefinition="/site/GetBeanDefinition";
		urlMap.GetClasses = "/site/GetClasses";
		urlMap.GetMethods= "/site/GetMethods";
		urlMap.CompileDFStateMachine = "/site/CompileDFStateMachine";
		urlMap.DeleteFromServer = "/site/DeleteFromServer";
		urlMap.SaveConfig = "/site/SaveConfig";
		urlMap.GetConfig = "/site/GetConfig";
		urlMap.ParseSQL = "/site/ParseSQL";
		urlMap.CompileSQLMapper = "/site/CompileSQLMapper";
		urlMap.CompileTodoMapper = "/site/CompileTodoMapper";
		urlMap.CompileTemplate = "/site/CompileTemplate";
		urlMap.CompileFastCamel = "/site/CompileFastCamel";
		urlMap.CompileLocalGraph = "/site/CompileLocalGraph";
		urlMap.GetStaticComponents = "/site/GetStaticComponents";
		urlMap.GetStaticComponent = "/site/GetStaticComponent";
		urlMap.GetDesign = "/site/GetDesign";
		urlMap.GetDesigns = "/site/GetDesigns";
		urlMap.GetTodoDistances = "/GetTodoDistances";
		urlMap.FindBoundryRect="/FindBoundryRect";
		urlMap.DevidePolygon="/DevidePolygon";
		urlMap.CompileTypeMapping="/CompileTypeMapping";
		urlMap.CompileTaskPlan="/CompileTaskPlan";
		urlMap.CompileWebUIFlow="/CompileWebUIFlow";
		urlMap.RunUICodeFlow="/site/RunUICodeFlow";
		urlMap.CompileCamelPlan="/CompileCamelPlan";
		urlMap.GetLayout="/GetLayout";
		urlMap.CompileSubGraph="/CompileSubGraph";
		urlMap.CompileObjectMapping="/site/CompileObjectMapping";
		urlMap.ProcessingPlan = "/ProcessingPlan";
		urlMap.CompileTaskPlanV2="/CompileTaskPlanV2";
		urlMap.RunAutomationProcess="/RunAutomationProcess";
		urlMap.RunImageTestCase="/RunImageTestCase";
		urlMap.GetProtoLayout = "/site/GetProtoLayout";
		urlMap.GetTaskSpecs = "/site/GetTaskSpecs";
		urlMap.GetTaskSpecs = "/site/GetAnnotSpecs";
		urlMap.CompileTaskPlanToCode="/site/CompileTaskPlanToCode";
		urlMap.CodeToPortJava="/site/CodeToPortJava";
		urlMap.GetCanvas="/site/GetCanvas";
		urlMap.GetRegEx = "/site/GetRegEx";
		urlMap.GetMenuGroup = "/site/GetMenuGroup";
		urlMap.GetSQLLayout= "/site/GetSQLLayout";
		urlMap.DownloadGcode= "/site/DownloadGcode";
		urlMap.UploadGcode= "/site/UploadGcode";
		urlMap.CompileGcode="/site/compileGcode";
		urlMap.SaveRecentMenu="/site/SaveRecentMenu";
		urlMap.CompilePattern="/site/CompilePattern";
		urlMap.triangulate="/site/triangulate1";
		urlMap.compileTagModel="/site/compileTagModel";
		urlMap.GetEv3Path="/site/getEv3Path";
		urlMap.ShapeMapper = "/site/shapeMapper";
		urlMap.ReadDro = "/site/readDROOverHTTP"; 
		urlMap.CameraImage = "/site/getCameraImage"; 
		urlMap.Vfd = "/site/vfd"; 
		urlMap.MC1= "/site/mc1" ;
		urlMap.VaccumControl = "/site/vaccumControl" ;
		urlMap.GrblController = "/site/GrblController";
		urlMap.ShapeOffset = "/site/ShapeOffset";
		urlMap.ShapePixalate="/site/ShapePixalate";
		urlMap.PathToPoints="/site/PathToPoints";

		
		function getURL(str){
			return urlMap[str];
		}
		var doLayout = false;
		</script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/raphael-min.js"
	type="text/javascript" charset="utf-8"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/timeline.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/graph2.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/taggingsupport.js"></script>

<script type="text/javascript"
	src="/site/includes/jsts/javascript.util.js"></script>
<script type="text/javascript"
	src="/site/includes/jsts/jsts.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/groupNode.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/menu.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/comps.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/modules/custommodules.js"></script>
<script type="text/javascript" src="/site/wide_5678/resources/js/dlg.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/plugin.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/codeEditor.js"></script>
<script type="text/javascript"
	src="/site/wide_5678/resources/js/maps.js"></script>
<script type="text/javascript"
	src="/site/includes/js/tree/simpletreemenu.js"></script>
<link rel="stylesheet" type="text/css"
	href="/site/includes/js/tree/simpletree.css" />
	<script type="text/javascript"
	src="/site/wide_5678/resources/js/router.js"></script>
		<script type="text/javascript"
	src="/site/wide_5678/resources/js/shapeeditor.js"></script>
	<script type="text/javascript"
	src="/site/wide_5678/resources/js/anim_editor.js"></script>
		<script type="text/javascript"
	src="/site/wide_5678/resources/js/shaperenderer.js"></script>
		<script type="text/javascript"
	src="/site/wide_5678/resources/js/cnc.js"></script>
	<script type="text/javascript" src="/site/wide_5678/resources/js/tosync.js"></script>
	<script type="text/javascript" src="/site/wide_5678/resources/js/ev3.js"></script>


	<%
	String d3vis = request.getParameter("d3vis"); 
		if(d3vis!=null&&d3vis.equalsIgnoreCase("true")){
%>
<script type="text/javascript" src="/site/vis/d3.v2.js"></script>
<%
	}
%>
	

<script type="text/javascript">
	var taskSpecs = {};
	var annotSpecs = {};
	var globalShape = new Shape("globalShape");
	
<%GetTaskSpecs getTaskSpecs = new GetTaskSpecs();
List<TaskSpec> taskList = getTaskSpecs.getTaskSpecs();
for(TaskSpec ts: taskList){
out.write("taskSpecs[\""+ts.getDispName()+"\"] = "+CommonUtil.toJson(ts)+" ;");
}%>
<%GetAnnotSpecs getAnnotSpecs = new GetAnnotSpecs();
Collection<AnnotSpec> annotSpecsList = getAnnotSpecs.getAnnotSpecs();
for(AnnotSpec ts: annotSpecsList){
out.write("annotSpecs[\""+ts.getDisplayName()+"\"] = "+CommonUtil.toJson(ts)+" ;");
}%>
<%

boolean filterDroppables = true;
String fdStr= request.getParameter("filterdroppable") ;
if(fdStr==null || fdStr.length()<1) {
	filterDroppables = true;
}else {
	filterDroppables = false;
}

Set<String> dropableFilter = new java.util.HashSet<String>();
GetMenuGroup menuGroup = new GetMenuGroup();
List<Group> groupMenuList = menuGroup.getGroups();
for(Group g : groupMenuList){
	for(String str: g.getItems()){
		dropableFilter.add(str);
		}
}
%>
</script>
<%
	List<String> mapperTaskList = new LinkedList<String>();
        	 			 mapperTaskList.add("ConcatMTask");
        	 			 mapperTaskList.add("LatLonMTask");
        	 			 mapperTaskList.add("ExpressionMTask");
        	 			 mapperTaskList.add("GeoHashMTask");
        	 			 mapperTaskList.add("ShapeToLatLonMTask");
        	 			 mapperTaskList.add("ConstantMTask");
        	 			 mapperTaskList.add("LookupMTask");
        	 			 mapperTaskList.add("AddressAppenderMTask");
        	 			 mapperTaskList.add("ConcatWithCommaMTask");
        	 			 mapperTaskList.add("IfMTask");
        	 			 mapperTaskList.add("EndMTask");
                         mapperTaskList.add("RemapMTask");
  Collections.sort(mapperTaskList);
%>
<%
	List<String>webUITaskList = new LinkedList<String>();
	        	 webUITaskList.add("GethttpUITask");
        				 webUITaskList.add("LogUITask");
            	 		 webUITaskList.add("ConstantUITask");
    Collections.sort(webUITaskList);
%>
<%
	String gm = request.getParameter("gm"); 
		if(gm!=null&&gm.equalsIgnoreCase("true")){
%>
<script type="text/javascript"
	src="http://maps.google.com/maps/api/js?sensor=true"></script>
<%
	}
%>
<link rel="stylesheet" type="text/css"
	href="/site/wide_5678/resources/css/index.css" />
	
<style type="text/css">
@import "/site/dojo/dojox/layout/resources/ResizeHandle.css";

@import "/site/dojo/dijit/themes/tundra/tundra.css";

@import
"/site/dojo/dojo/resources/dojo.css"
@import "/site/dojo/dojo/resources/dnd.css";
	path.chord {
  fill-opacity: .67;
}

#footerbar { 
position: fixed; 
bottom: 0px; 
width: 100%; 
float: bottom; 
height:25px; 
background: #000; 
color: #FFF; 
margin: 0; 
padding: 10px 0px 10px 0px; 
} 
.buttonOn {background-color: #4CAF50;} /* Green */
.buttonOff {background-color: #f44336;} /* Blue */

#bottomCanvas{
width: 80px;
height:25px;
border-color: white;
background-color: #000000 ;
}
#messages{
width: 500px;
height:25px;
background-color: #000066;
}
#messageBox{
width: 500px;
height:25px;
background-color: #000000;
border:none;
color: #ffffff; 
font-family: Verdana; 
font-weight: normal; 
font-size: 18px;
border: 2px solid white;
border-radius: 7px;
}
.greenText{ color:#00FF33; }

.blueText{ color:blue; }

.infoText{ color:white; }

.redText{ color:red; }

.orangeText{ color:orange; }

#messageBox:focus { 
    outline: none;
    border-color: #white;
    box-shadow: 0 0 20px #9ecaed;
}
#rightbottombar{
height:25px;
border-color: white;
background-color: #000000;
}
#top{    
width: 100%;   
 height: 1000px;    
 } 
#dropbox {
	width: 400px;
	height: 200px;
	border: 2px solid #DDD;
	-moz-border-radius: 8px;
	-webkit-border-radius: 8px;
	background-color: #FEFFEC;
	text-align: center;
	color: #BBB;
	font-size: 2em;
	font-family: Arial, sans-serif;
}
#Widgets {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 100px;
	top: 200px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#propnode {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* scroll; */
	position: absolute;
	width: 250px;
	height: 300px;
	left: 940px;
	top: 50px;
	filter: alpha(opacity =   80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#docnode {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* scroll; */
	position: absolute;
	width: 400px;
	height: 200px;
	left: 1200px;
	top: 330px;
	filter: alpha(opacity =   80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#histnode {
	background: #fff;
	border: 3px solid silver;
	overflow: scroll; 
	position: absolute;
	width: 400px;
	height: 200px;
	left: 1200px;
	top: 100px;
	filter: alpha(opacity =   80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#cmdnode {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* scroll; */
	position: absolute;
	width: 660px;
	height: 350px;
	left: 500px;
	top: 330px;
	filter: alpha(opacity =   80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#chartnode1 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 600px;
	top: 100px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#chartnode2 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 100px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#chartnode3 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#chartnode3 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 600px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}
.fixmoduleheading{
	height: 20px;
	width:   100%;
	background: orange;
	text-align: center;
background: #c5deea; /* Old browsers */
background: -moz-linear-gradient(left,  #969696 0%, #9696f0 31%, #9696f0 100%); /* FF3.6+ */
background: -webkit-gradient(linear, left top, right top, color-stop(0%,#969696), color-stop(31%,#9696f0), color-stop(100%,#9696f0)); /* Chrome,Safari4+ */
background: -webkit-linear-gradient(left,  #969696 0%, #9696f0 31%, #9696f0 100%); /* Chrome10+,Safari5.1+ */
background: -o-linear-gradient(left,  #969696 0%, #9696f0 31%, #9696f0 100%); /* Opera 11.10+ */
background: -ms-linear-gradient(left,  #969696 0%, #9696f0 31%, #9696f0 100%); /* IE10+ */
background: linear-gradient(to right,  #969696 0%, #9696f0 31%, #9696f0 100%); /* W3C */
filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#969696', endColorstr='#9696f0',GradientType=1 ); /* IE6-9 */
	 border-top-left-radius: 10px;
	 border-top-right-radius: 10px;
	 -webkit-border-top-left-radius: 10px;
	 -webkit-border-top-right-radius: 10px;
	 

}
.anondefheading,#ndtype,.heading,#ndtype2{
	height: 20px;
	width:   100%;
	background: orange;
	text-align: center;
background: #c5deea; /* Old browsers */
background: -moz-linear-gradient(left,  #c5deea 0%, #8abbd7 31%, #066dab 100%); /* FF3.6+ */
background: -webkit-gradient(linear, left top, right top, color-stop(0%,#c5deea), color-stop(31%,#8abbd7), color-stop(100%,#066dab)); /* Chrome,Safari4+ */
background: -webkit-linear-gradient(left,  #c5deea 0%,#8abbd7 31%,#066dab 100%); /* Chrome10+,Safari5.1+ */
background: -o-linear-gradient(left,  #c5deea 0%,#8abbd7 31%,#066dab 100%); /* Opera 11.10+ */
background: -ms-linear-gradient(left,  #c5deea 0%,#8abbd7 31%,#066dab 100%); /* IE10+ */
background: linear-gradient(to right,  #c5deea 0%,#8abbd7 31%,#066dab 100%); /* W3C */
filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#c5deea', endColorstr='#066dab',GradientType=1 ); /* IE6-9 */
	 border-top-left-radius: 10px;
	 border-top-right-radius: 10px;
	 -webkit-border-top-left-radius: 10px;
	 -webkit-border-top-right-radius: 10px;
	 
}

/* #ndtype,.heading,#ndtype2 {
	height: 30px width:   100%;
	border: 1px solid #c0c0c0;
	border-bottom: 1px solid #9b9b9b;
	background: #fff
		url(/site/dojo/dijit/themes/tundra/images/buttonEnabled.png) repeat-x
		bottom left;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
	text-align: center;
} */

#chart1,#chart2,#chart3 {
	width: 100%;
	height: 100%;
	border: 1px solid #c0c0c0;
	border-bottom: 1px solid #9b9b9b;
	background: #fff
		url(/site/dojo/dijit/themes/tundra/images/buttonEnabled.png) repeat-x
		bottom left;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
	text-align: center;
}

#gmapsnode {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;/* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

/* .movablenode {
	background: #fff;
	border: 3px solid silver;
	overflow: scroll;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
} */

.popupmenucls {
	background: #fff;
	border: 2px solid black;
	overflow: hidden;/* auto; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}
.anondefstaticnode,.staticnode,.movablenode{
	background: #fff;
	overflow: hidden;/* auto; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-box-shadow: 0px 3px 4px #000;
	-webkit-box-shadow: 0px 3px 4px #000;
	box-shadow: 0px 3px 4px #000;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	 border-radius: 10px;
}
/* .staticnode {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}
 */
.simplestaticnode {
	background: white;
	border: 1px solid gray;
	overflow: hidden;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     100);
	opacity: 1;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

.simplestaticnode2 {
	background: white;
	border: 1px solid black;
	overflow: hidden;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =     90);
	opacity: 0.9;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}
</style>
<script type="text/javascript"><!--
		var artistMode=false;
        var lastkeyed="";		
        
        var wdgtsrc ;
        var lastkeyedAll = new Array();
		var currentGraph=null;
		var designMode=false;
		<%String graph= request.getParameter("page") ;
		if(graph==null|| graph.length()<=0) {
		out.print("currentGraph=\"MyDesign1\";");
		}
		else {
			out.print( "currentGraph=\""+graph+"\";");
		}
		String designMode= request.getParameter("designMode") ;
		if(designMode==null|| designMode.length()<=0) {
		out.print("designMode=false;");
		}
		else {
			out.print( "designMode="+true+";");
		}%>
  	  		//dojo.require("dojo.query");
  	  		//dojo.require("dojo.position");
            dojo.require("dojox.layout.ResizeHandle");
	        dojo.require("dijit.MenuBar");
	        dojo.require("dijit.MenuBarItem");
	        dojo.require("dijit.PopupMenuBarItem");
	        dojo.require("dijit.Menu");
	        dojo.require("dijit.MenuItem");
	        dojo.require("dijit.PopupMenuItem");
	        dojo.require("dijit.layout.SplitContainer");
	        dojo.require("dijit.layout.ContentPane");
	        dojo.require("dijit.layout.AccordionContainer");
	        dojo.require("dijit.ColorPalette");
	        dojo.require("dijit.Dialog");
	        dojo.require("dijit.form.RadioButton");
	        dojo.require("dijit.form.ValidationTextBox");
	        dojo.require("dijit.form.NumberTextBox");
	        dojo.require("dijit.InlineEditBox");
	        dojo.require("dijit.form.HorizontalSlider");
	        dojo.require("dijit.form.HorizontalRule");
	        dojo.require("dijit.form.HorizontalRuleLabels");
            dojo.require("dijit.Menu");
            dojo.require("dojo.parser");
            dojo.require("dojo.dnd.Mover");
            dojo.require("dojo.dnd.Moveable");
            dojo.require("dojo.dnd.move");
            dojo.require("dojo.io.iframe");
            dojo.require("dojo.dnd.Container");
            dojo.require("dojo.dnd.Manager");
            dojo.require("dojo.dnd.Source");
            dojo.require("dojox.av.FLAudio");
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dojo.data.ItemFileReadStore");
            dojo.require("dijit.tree.ForestStoreModel");
        	dojo.require("dijit.Tree");
        	dojo.require("dijit.TooltipDialog");
        	  dojo.require("dijit.tree.dndSource");
        	  dojo.require("dojox.charting.Chart2D");
        	  dojo.require("dojox.charting.themes.Tom");
/*         	  dojo.require("dojox.charting.axis2d.Default");
        	  dojo.require("dojox.charting.plot2d.Default");
        	  dojo.require("dojox.charting.plot2d.Spider");
        	  dojo.require("dojox.charting.axis2d.Base");
 */            
 
  
 dojo.require("dojox.widget.AnalogGauge");
dojo.require("dojox.widget.gauge.AnalogArcIndicator");
dojo.require("dojox.widget.gauge.AnalogNeedleIndicator");
dojo.require("dojox.widget.gauge.AnalogArrowIndicator");

 dojo.addOnLoad(documentReady);
            var mySound;
            var repostore=null;
            var repomodel=null;
            var repotree=null;
            var response = [{
             module:"Droppables",
             name  :"Droppables",
             items:  [
             <%List<String> lst = DBHelper.getInstance().getStringList("select name from staticcomponent order by id desc");
             int i = 0;
             Set<String> processed = new HashSet<String>();
            for(String s: lst){
            	if(processed.contains(s))continue;
            	 processed.add(s);
            	if(filterDroppables && !dropableFilter.contains(s)){
            	out.print("{ module: 'Droppables' ,name:\""+s+"\"}");
            	if(i<(lst.size()-1)){
            		out.println(",");
            	}
            	i++;
            	}
            }%>
              ]
            },
            {
                module:"Processors Library",
                name  :"Processors Library",
                items:  [
                <%List<String> lst2 = DBHelper.getInstance().getStringList("select shortname from procinfos order by id desc");
                int i2 = 0;
               for(String s: lst2){
               	out.print("{ module: 'Processors Library' ,name:\""+s+"\"}");
               	if(i2<(lst2.size()-1))
               		out.println(",");
               	i2++;
               }%>
                 ]
               },
               {
                   module:"Graphs Library",
                   name  :"Graphs Library",
                   items:  [
                   <%List<String> lst3 = DBHelper.getInstance().getStringList("select name from graphs");
                   int i3 = 0;
                  for(String s: lst3){
                  	out.print("{ module: 'Graphs Library' ,name:\"Graph:"+s+"\"}");
                  	if(i3<(lst3.size()-1))
                  		out.println(",");
                  	i3++;
                  }%>
                    ]
                  },{
                      module:"Design Library",
                      name  :"Design Library",
                      items:  [
                      <%List<String> lstDesign = DBHelper.getInstance().getStringList("select name from pageconfig");
                      int lstDesignCount = 0;
                     for(String s: lstDesign){
                     	out.print("{ module: 'Design Library' ,name:\"Design:"+s+"\"}");
                     	if(lstDesignCount<(lstDesign.size()-1))
                     		out.println(",");
                     	lstDesignCount++;
                     }%>
                       ]
                     },{
                      module:"Annotations",
                      name  :"Annotations",
                      items:  [ 
                      { module: 'Annotations' ,name:"Annot:ScatterGather"}/*shedule  parallel*/,
                      { module: 'Annotations' ,name:"Annot:FinishFirst"}/*deapth first*/,
                      { module: 'Annotations' ,name:"Annot:JoinAll"}/*Breadth first*/,
                      { module: 'Annotations' ,name:"Annot:break"}/*break statement*/,
                      { module: 'Annotations' ,name:"Annot:AllAtOnce"}/*Breadth first*/
                      ]
                     },{
                         module:"TextAnnotations",
                         name  :"TextAnnotations",
                         items:  [ 
                         { module: 'TextAnnotations' ,name:"Annot:int"},
                         { module: 'TextAnnotations' ,name:"Annot:date"},
                         { module: 'TextAnnotations' ,name:"Annot:decimal"},
                         { module: 'TextAnnotations' ,name:"Annot:alphaNum"},
                         { module: 'TextAnnotations' ,name:"Annot:phone"},
                         { module: 'TextAnnotations' ,name:"Annot:month"},
                         { module: 'TextAnnotations' ,name:"Annot:zipcode"},
                         { module: 'TextAnnotations' ,name:"Annot:alphaNumwithspace"},
                         { module: 'TextAnnotations' ,name:"Annot:vin"},
                         { module: 'TextAnnotations' ,name:"Annot:email"},
                         { module: 'TextAnnotations' ,name:"Annot:loc"},
                         { module: 'TextAnnotations' ,name:"Annot:url"},
                         { module: 'TextAnnotations' ,name:"Annot:poly"},
                         { module: 'TextAnnotations' ,name:"Annot:street"},
                         { module: 'TextAnnotations' ,name:"Annot:RecPos"},
                         { module: 'TextAnnotations' ,name:"Annot:IPAddress"},
                         { module: 'TextAnnotations' ,name:"Annot:HTTPCode"},
                         { module: 'TextAnnotations' ,name:"Annot:URI"},
                         { module: 'TextAnnotations' ,name:"Annot:HTTPVerb"},
                         { module: 'TextAnnotations' ,name:"Annot:streetloc"},
                         { module: 'TextAnnotations' ,name:"Annot:nchar"},
                         { module: 'TextAnnotations' ,name:"Annot:pattern"},
                         { module: 'TextAnnotations' ,name:"Annot:borough"}
                         ]
                        },{
                            module:"LangAnnotations",
                            name  :"LangAnnotations",
                            items:  [
									{ module: 'LangAnnotations' ,name:"Annot:name"},
                                     { module: 'LangAnnotations' ,name:"Annot:verb"},
                                     { module: 'LangAnnotations' ,name:"Annot:noun"},
                                     { module: 'LangAnnotations' ,name:"Annot:adjective"},
                                     { module: 'LangAnnotations' ,name:"Annot:subjacctive"},
                                     { module: 'LangAnnotations' ,name:"Annot:pronoun"}
                            ]
                           },{
                               module:"PointAnnotations",
                               name  :"PointAnnotations",
                               items:  [
   									{ module: 'LangAnnotations' ,name:"Annot:action1"},
   									{ module: 'LangAnnotations' ,name:"Annot:action2"},
   									{ module: 'LangAnnotations' ,name:"Annot:action3"},
   									{ module: 'LangAnnotations' ,name:"Annot:action4"}
                               ]
                              },{
                module:"Widgets",
                name  :"Widgets",
                items:  [ 
                { module: 'Widgets' ,name:"Blinker"},
                { module: 'Widgets' ,name:"Dimmer"},
                { module: 'Widgets' ,name:"Light"},
                { module: 'Widgets' ,name:"Green Bulb"},
                { module: 'Widgets' ,name:"Red Bulb"},
                { module: 'Widgets' ,name:"Flat Green Led"},
                { module: 'Widgets' ,name:"Flat Red Led"},
                { module: 'Widgets' ,name:"Flat Yellow Led"},
                { module: 'Widgets' ,name:"Tag"},
                { module: 'Widgets' ,name:"Continous Timer"},
                { module: 'Widgets' ,name:"Onetime Timer"},
                { module: 'Widgets' ,name:"InputPort"},
                { module: 'Widgets' ,name:"OutputPort"},
                { module: 'Widgets' ,name:"AuxPort"},
                { module: 'Widgets' ,name:"Port"},
                { module: 'Widgets' ,name:"PortMethod"},
                { module: 'Widgets' ,name:"ComplexPort"},
                { module: 'Widgets' ,name:"ComplexPortMethod"},
                { module: 'Widgets' ,name:"ComplexInputPort"},
                { module: 'Widgets' ,name:"ComplexOutputPort"},
                { module: 'Widgets' ,name:"ComplexAuxPort"},
                { module: 'Widgets' ,name:"ConvertToInputPort"},
                { module: 'Widgets' ,name:"ConvertToOutputPort"},
                { module: 'Widgets' ,name:"ConvertToAuxPort"},
                { module: 'Widgets' ,name:"Slider"},
                ]
               },{
                   module:"Code Blocks",
                   name  :"Code Blocks",
                   items:  [ { module: 'Code Blocks' ,name:"TitanClass"},
                             { module: 'Code Blocks' ,name:"TitanJob"},
                             { module: 'Code Blocks' ,name:"TitanAnalyzer"},
                             { module: 'Code Blocks' ,name:"TitanBlock"},
                             { module: 'Code Blocks' ,name:"AnonDefScript"}
                            ]
               
                  },{
                      module:"Custom Modules",
                      name  :"Custom Modules",
                      items:  [ { module: 'Custom Modules' ,name:"FetchUrlButton"},
                                { module: 'Custom Modules' ,name:"Button"},
                                { module: 'Custom Modules' ,name:"Link"},
                                { module: 'Custom Modules' ,name:"Module"},
                                { module: 'Custom Modules' ,name:"SQLOutputMapping"},
                                { module: 'Custom Modules' ,name:"SQLInputMapping"},
                                { module: 'Custom Modules' ,name:"Remap"},
                                { module: 'Custom Modules' ,name:"ClassOutputMapping"},
                                { module: 'Custom Modules' ,name:"ClassInputMapping"},
                                { module: 'Custom Modules' ,name:"NewClassOutputMapping"},
                                { module: 'Custom Modules' ,name:"NewClassInputMapping"},
                                { module: 'Custom Modules' ,name:"NewClassPropMapping"},
                                { module: 'Custom Modules' ,name:"StaticModule"},
                                { module: 'Custom Modules' ,name:"AnonScriptModule"},
                                { module: 'Custom Modules' ,name:"CanvasModule"},
                                { module: 'Custom Modules' ,name:"SimpleStaticModule"},
                                { module: 'Custom Modules' ,name:"Image"},
                                { module: 'Custom Modules' ,name:"TextArea"},
                                { module: 'Custom Modules' ,name:"Graphics"},
                                { module: 'Custom Modules' ,name:"Label"},
                                { module: 'Custom Modules' ,name:"Animation"},
                                { module: 'Custom Modules' ,name:"Input"},
                                { module: 'Custom Modules' ,name:"Action"},
                                { module: 'Custom Modules' ,name:"EventAnimation"},
                                { module: 'Custom Modules' ,name:"RawImage"},
                                { module: 'Custom Modules' ,name:"Code"},
                                { module: 'Custom Modules' ,name:"HeaderModule"},
                                { module: 'Custom Modules' ,name:"HeaderLabel"},
                                { module: 'Custom Modules' ,name:"Last"},
                                { module: 'Custom Modules' ,name:"Shape"},
                                { module: 'Custom Modules' ,name:"CanvasImage"},
                                { module: 'Custom Modules' ,name:"Google Map"},
                                { module: 'Custom Modules' ,name:"Chart"},
                                { module: 'Custom Modules' ,name:"Chart 2 Dim"},
                                { module: 'Custom Modules' ,name:"ProtoBuff"},
                                { module: 'Custom Modules' ,name:"Pie Chart"}]
                  
                     },{
                    	
                         module:"Tasks",
                         name  :"Tasks",
                         items:  [ 
                                   <%for(TaskSpec s: taskList){
									out.println("{ module: 'Tasks' ,name:'Task:"+s.getDispName()+"'},");
                                   }%>
                                  ]
                     
                        },{
                            module:"Mapper Tasks",
                            name  :"Mapper Tasks",
                            items:  [ 
                                      <%for(String s: mapperTaskList){
   									out.println("{ module: 'Mapper Tasks' ,name:'MTask:"+s+"'},");
                                      }%>
                                     ]
                        
                           }
                           ,{
                               module:"WebUI Tasks",
                               name  :"WebUI Tasks",
                               items:  [ 
                                         <%for(String s: webUITaskList){
      									out.println("{ module: 'WebUI Tasks' ,name:'WebUITask:"+s+"'},");
                                         }%>
                                        ]
                           
                              }<%for(Group grp:groupMenuList){
                        		out.println(",{");
                        		out.println("module:'"+grp.getId()+"',");
                        		out.println("name:'"+grp.getId()+"',");
                        		out.println("items:  [ ");
                        		for(String s: grp.getItems()){
   									out.println("{ module: '"+grp.getId()+"' ,name:'"+grp.getId()+":"+s+"'},");
                                     }
                        		out.println("] ");
                        		out.println("}");
                           }%>                           
                           ];
           
            var data = {
                    "identifier": "name",
                    "label": "name",
                    "items": response
                };

            var enableSound = false;
            var stepMoverHline = null;
            var stepMoverVline = null;
            var stepMoverText = null;
            function recalcPos(id){
            	var comp = findComponentByName(id);
            	var gnode = findNodeById(id);
            	var pos = dojo.position(id);
            	var evtdata = {};
            	evtdata.id = id;
            	evtdata.type="ssmmovestop";
            	evtdata.pos = pos;
            	sendEvent(geq, [evtdata]) ;
            	if(comp!=null){
            		comp.width = pos.w;
            		comp.height = pos.h;
            	}
            	if(gnode!=null){
            	draw();
            	}
            }
            dojo.declare("dojo.dnd.StepMover", dojo.dnd.Mover, {
            	  onMouseMove: function(e) {
            		  if(stepMoverHline!=null){
            			  try{
            			  stepMoverHline.remove();
            			  }catch(e){}
            			  stepMoverHline = null;
            		  }
            		  if(stepMoverVline!=null){
            			  try{
            				  stepMoverVline.remove();
            			  }catch(e){}
            			  stepMoverVline = null;
            		  }
            		  if(stepMoverText!=null){
            			  try{
            				  stepMoverText.remove();
            			  }catch(e){}
            			  stepMoverText = null;
            		  }            		  
            	    dojo.dnd.autoScroll(e);
            	    var srcEle = e.srcElement==null ?e.target:e.srcElement;
            	    var id = null;
            	    if(srcEle!=null && (undefined != srcEle)){
            	    	id = srcEle.id;
            	  }
            	    var m = this.marginBox;
            	    var stepSize = parseInt(wideconfig.stepMoverSize);
            	    var shp = null;
            	    if(e.ctrlKey) {
            	    	shp =  {l: parseInt((m.l + e.pageX) /stepSize) *stepSize, t: parseInt((m.t + e.pageY) / stepSize) * stepSize};
            	      this.host.onMove(this,shp );
            	    } else {
            	    	shp = {l: m.l + e.pageX, t: m.t + e.pageY};
            	      this.host.onMove(this, shp);
            	    }
            	    if(wideconfig.cross_hair!=null && wideconfig.cross_hair=="true"){
            	    if(id!=null){
            	    	var mid =id;
            	    	var comp = null;
        	    		if(id.length>"heading_".length)
        	    			mid = id.substring("heading_".length);
            	    		comp = findComponentByName(mid);
            	    	if(comp==null){
            	    		comp = findComponentByName(id);
            	    	}
            	    	if(comp!=null && undefined!=comp){
            	    	var grpPos = dojo.position(mid);
            	    	var smx = grpPos.x-leftWidth;
            	    	var smy  = grpPos.y-topWidth ;
            	    	smx = parseInt(smx);
            	    	smy = parseInt(smy);
            	    	stepMoverHline = hline(0,smy,"black");
            	    	stepMoverVline = vline(smx,0,"black");
            	    	stepMoverText = pCanvas.text(smx,smy-10,"("+smx+","+smy+")")
            	    	}
            	    }
            	    }
            	    dojo.stopEvent(e);
            	    
            	  }
            	});
function documentReady(){
	<%
	String wconfig = request.getParameter("wconfig"); 
		if(wconfig!=null){
			out.print("wideconfigtoUse=\""+wconfig+"\";");
		}
	%>
    getWideConfig(wideconfigtoUse);
    repostore = new dojo.data.ItemFileWriteStore({data: data});
    console.log(repostore);
	if(enableSound){
	mySound = new dojox.av.FLAudio({
        initialVolume: .5,
        autoPlay: false,
        isDebug: false,
        statusInterval: 500
    });
	}
	propm = new dojo.dnd.Moveable("propnode",{handle:"propnodeheader" });
    docm = new dojo.dnd.Moveable("docnode",{handle:"docnodeheader" });
    var cmdm = new dojo.dnd.Moveable("cmdnode",{handle:"cmdnodeheader"});
    var histm = new dojo.dnd.Moveable("histnode",{handle:"histnodeheader"});
	
    loadImageIcons();
    
    
//prepareGraph();
startModuleConnListener();
GetProcesses();
GetUpdateProcesses();
/*added to support dynamic modules
 * 
 */
 if(dynaMod){
	getModulesFor("wide",prepareGraph);
 }
hidediv('cmdnode');hidediv('docnode');hidediv('propnode');hidediv('histnode');dw=false;hw=false;pw=false;cw=false;
dojo.subscribe(ceq, null, function handle(jdata){
  var str = jdata.label;
  	console.log(str);
	if(menuitems[str]!=null){
		menuitems[str](jdata);
	}
	
});

dojo.subscribe("/dojo/resize/stop", function(inst){
	console.log("resized: "+inst.targetDomNode.id);
		var a = editors[inst.targetDomNode.id]
			if(a!=null){
				/*var pos = dojo.position(inst.targetDomNode);
				var b = dojo.byId("textarea_"+inst.targetDomNode.id);
				b.style.width = (pos.w-40)+"px";
				b.style.height = (pos.h-44)+"px";
				a.resize();*/
				var fun = (new Function("fixEditor('"+inst.targetDomNode.id+"')"));
				setTimeout(fun,300);
			}
	   var a=maps[inst.targetDomNode.id];
	   if(a!=null){
		   google.maps.event.trigger(a, "resize");
	   }
	  
	   var a=globals[inst.targetDomNode.id];
	   if(a!=null){
		   a.resize(inst.targetDomNode.offsetWidth,inst.targetDomNode.offsetHeight,inst.targetDomNode.id);
		   }
	   
	   var a=grids[inst.targetDomNode.id];
	   if(a!=null){
		   a.resize();
		   a.update();
		   }
		   var fun = (new Function("recalcPos(\'"+inst.targetDomNode.id+"\');"));
			setTimeout(fun,200);

		var a = canvasModules[inst.targetDomNode.id];
		if(a!=null){
			  var fun = (new Function("resizeCanvas(\'"+inst.targetDomNode.id+"\');"));
			  setTimeout(fun,300);
		}
		
});

dojo.connect(document, "onkeyup", function(e){
	switch(e.keyCode){
		case dojo.keys.CTRL:
   			ctrl = false;
			break;
		case dojo.keys.ALT:
   			alt = false;
			break;
		case dojo.keys.SHIFT:
   			shift = false;
			break;
		case dojo.keys.DELETE:
   			removeLastSelectedNode();
			break;
			
		}
});
dojo.connect(document, "onkeydown", function(e){
	switch(e.keyCode){
		case dojo.keys.CTRL:
   			ctrl = true;
			break;
		case dojo.keys.ALT:
   			alt = true;
			break;
		case dojo.keys.SHIFT:
   			shift = true;
			break;
		}
});
dojo.connect(document, "onkeypress", function(e){
    switch(e.charOrCode){
       case 
       dojo.keys.ENTER :
       	console.log(lastkeyed);
    	lastkeyedAll.push(lastkeyed);
    	handleCommand();
    	lastkeyed="";   
        break;
   
       default: 
       	//console.log('you typed: ', e.charOrCode);
    	lastkeyed+=e.charOrCode;
    	lastkeyedAll.push(lastkeyed);
    }
});
initPluginJS();
var propm = new dojo.dnd.Moveable("Widgets",{handle:'ndtype2'});
registerWindow("Widgets");
hideWindow("Widgets");
var startDropId = null;
wdgtsrc = new dojo.dnd.Source("wdgts",{copyOnly:true,handle:'ndtype'});
dojo.subscribe("/dnd/cancel", function(source){
    console.debug("cancled the drop", source);
	var ele = getElementFromPos(realMouseX,realMouseY);
	console.log(ele);
	var dropId=startDropId;
	 var isTree = false;
     if(dropId.indexOf("dijit__TreeNode")==0){
   	  isTree = true;
   	  dropId=dropId.substring(0,dropId.length-4);
   	  var nd = dijit.byId(dropId);
   	  dropId=nd.item.name;
   	  console.log("dropped tree: "+nd.item.name+"   ,drop id:"+dropId);
     }
     var cx = mouseX,cy = mouseY;
     var a = findSelectedNode(cx,cy);
     console.log("Dropped on :");
     console.log(a);
     if(undefined == dropId || dropId ==null) 
    	 return;
     if(!( typeof dropId === 'string' )) {
         dropId = dropId[ 0];
 	 }
     var compname = dropId;
     var component = findComponentByName(a.id);
     if(a.type=="module"){
  		  if(dropId.indexOf(":")>-1){
     			var parts = dropId.split(":");
     			compname = parts[1].trim();
  		  }
    	 if(a.tags==null){
				a.tags = new Array();
			}
    	 a.tags.push(compname);
    	 if(component!=null){
    		 if(component.tags==null){
    			 component.tags = new Array();
 			}
    		 component.tags.push(compname);
    	 }
    	 
     }

  });

  dojo.subscribe("/dnd/start", function(source, nodes, copy){
    console.debug("Starting the drop", source);
    startDropId = nodes[0].id==null?nodes[0].innerHTML:nodes[0].id;
    console.log("dnd  start: "+  startDropId);
    
  });
  dojo.subscribe("/dnd/drop/before", function(source, nodes, copy, target){
	  startDropId = null;//donot forget to nullify!!
	  var dropId = nodes[0].id==null?nodes[0].innerHTML:nodes[0].id;
	 console.log("dnd  before: "+  dropId);
    if(target == c1){
      console.debug(copy ? "Copying from" : "Moving from", source, "to", target, "before", target.before);
    }
  });
  dojo.subscribe("/dnd/drop", function(source, nodes, copy, target){
    if(target == c1)
    {
      console.debug(copy ? "Copying from" : "Moving from", source, "to", target, "before", target.before);
      var dropId = nodes[0].id==null?nodes[0].innerHTML:nodes[0].id;
      console.info("Dropped: "+dropId);
      var isTree = false;
      if(dropId.indexOf("dijit__TreeNode")==0){
    	  isTree = true;
    	  dropId=dropId.substring(0,dropId.length-4);
    	  var nd = dijit.byId(dropId);
    	  dropId=nd.item.name;
    	  console.log("dropped tree: "+nd.item.name+"   ,drop id:"+dropId);
      }
      var cx = mouseX,cy = mouseY;
      var a = findSelectedNode(cx,cy);
      console.log("Dropped on :"+a);
      if(a==null){
    	  if(mouseOverConn!=null){
    		  a = mouseOverConn;
    	  }
      }
      if(a!=null){
      console.info("DroppedOntopon: "+a.id);
      }
      var f = comps[dropId];
      if(f==null && isTree==false){
    	  dropId = nodes[0].innerHTML
    	  f = comps[dropId];
      }
      if(undefined == dropId || dropId ==null) 
          return;

     if(!( typeof dropId === 'string' )) {
        dropId = dropId[ 0];
	 }
 
     if(a!=null && a.type!=null&&a.type=="group"){
    	 if(a.items==null){
    		 a.items = new Array();
    	 }
    	 if(dropId.indexOf("noteitem")>0){
    	 var regex = new RegExp(/<([^\s]+).*?id="([^"]*?)".*?>(.+?)<\/\1>/gi);
    	var  matches = dropId.match(regex);
    	 var results = new Array();
    	 for (var i=0;i<matches.length;i++) {
    	     parts = regex.exec(matches[i]);
    	     results.push(parts[2]);
    	 }
    	 for(var i=0;i<results.length;i++){
    		 a.items.push(results[i]);
    		  var nf = "/site/notesapi?t=antf&f="+a.folderId+"&n="+results[i];
    		     var str = doGetHtmlSync(nf);
    		     addInfoToBox(str);
    	 }
    	 }
    	 else{
    	 a.items.push(dropId);
        }
    	 
     }else{
   	    var configFun = configs[dropId];
    	  if(configFun!=null){
			configFun(f,a,dropId,true,cx,cy,null);//true mean commin via ui
       	  }else{
       		//sumit://adding a short way of adding drop handlers originial method now works bad with tree and autogenerated items!!
       		var func = null;
       		var partIDX = 0 ;
       		var nameIDX = 1 ;
       		  if(dropId.indexOf(":")>-1){
       			var parts = dropId.split(":");
       			partIDX = parts.length-2;
       			nameIDX = partIDX+1;
       			var fcode  = "handle"+parts[partIDX]+"Drop"
       			func = comps[fcode];
       		  }else{
       			func = comps[dropId];
       		  }
       			if(func!=null){
       				func(f,a,parts[nameIDX],true,cx,cy,null);//true mean commin via ui that case last parm is null(no exisiting config)'
       			}
       			
       		  
       	  }
	   }
  }else {
    	 console.debug(copy ? "Copying from" : "Moving from", source.node.id, "to", target.node.id);
         console.info("Dropped: "+nodes[0]);
         var dropId = nodes[0].id==null?nodes[0].innerHTML:nodes[0].id;
         console.info("Dropid: "+dropId);
         if(dropId.indexOf("dijit__TreeNode")==0){
       	  dropId=dropId.substring(0,dropId.length-4);
       	  var nd = dijit.byId(dropId);
   	  	  dropId=nd.item.name;
       	  console.log("dropped tree: "+dropId);
         }
         lastDropModule = target.node.id;
         lastDropModuleGrp = target.node.getAttribute("grpid");
	   }
  });
}
function getKeyed(){
return lastkeyed;
}
function getKeyedAll(){
return lastkeyedAll;
}
function resetKeyed(){
lastkeyed = "";
}
function getAndResetKeyed(){
var temp= lastkeyed;
lastkeyed="";
return temp.length>0?temp:null;
}
function resetKeyedAll(){
lastkeyedAll = new Array();
}
function getAndResetKeyedAll(){
lastkeyedAll.push(lastkeyed);
lastkeyed="";
var temp = lastkeyedAll;
lastkeyedAll = new Array();
return temp;
}
 function handleCommand(){
	 var cmd = dojo.trim(lastkeyed);
        {
				
				var parts = cmd.split(" ");
				cmd = parts[0];
				console.log(parts);
				console.log(cmd);
				if(cmd=="!cn"){
					createNewNodeDialog(300,300,40,40);
						dojo.byId("nodeId").value = parts[1];
			
				}
				if(cmd=="!cs"){
				createStreamDialogAtXYID(300,300,"");
				dojo.byId("streamname").value = parts[1];
				dojo.byId("streamprocessor").value = parts[2];
				
				}
				if(cmd=="!cc"){
				createConnectionDialogAtXYID(300,300,"");
				dojo.byId("connectionId").value = parts[1];
				dojo.byId("fromNode").value = parts[2];
				dojo.byId("toNode").value = parts[3];
				}
				if(cmd=="!cnote"){
				showNoteDlg();
				}
				if(cmd=="!ce"){
				createEventDialogAtXYID(300,300,"");
					dojo.byId("eventname").value = parts[1];
				dojo.byId("eventstore").value = parts[2];
			
				}
				if(cmd=="!sg"){
				saveGraph();
				}
				if(cmd=="!cg"){
				CompileProcess();
				}
				if(cmd=="!ng"){
				clearGraph();
				}
				if(cmd=="!og"){
				openGraph();
				}
				if(cmd=="!redraw"){
				draw();
				}
				if(cmd=="!sh"){
				showHistorical();
				}
				if(cmd=="!cps"){
				validateProcess();
				}
				if(cmd =="!mv"){
					var vid = parts[1];
					var dx = parts[2];
					var dy = parts[3];
					var nd = findNodeById(vid);
					nd.normalizedy = parseInt(nd.normalizedy) +parseInt(dy);
					nd.normalizedx = parseInt(nd.normalizedx) +parseInt(dx);
 					nd.x= parseInt(nd.x)+parseInt(dx);
					nd.y=parseInt(nd.y)+parseInt(dy);
					draw();
       		}
       		if(cmd=="!ep"){
       		var vid = parts[1];
       		 createEventDialogAtXYID(300,300,vid);
       		}
       		if(cmd=="!sp"){
       		var vid = parts[1];
       		 addStreamPropertyDialogAtXYID(300,300,vid);
       		}
       		if(cmd=="!cmenu"){
       		mycustommenu();
       		}
       		if(cmd=="!ss"){
       		var vid = parts[1];
       		var obj = findNodeById(vid);
					
       		var objstr = dojo.toJson(obj);
            CallWebService("SaveStream",objstr);
       		}
       		 if(cmd=="!sn"){
       		 var vid = parts[1];
       		 var obj = findNodeById(vid);
       		 var objstr = dojo.toJson(obj);
            CallWebService("SaveNode",objstr);
       		}
       		if(cmd=="!dn"){
       		 var vid = parts[1];
       		removeNodeById(vid);
       		draw();
       		}
       		if(cmd=="!ds"){
       		 var vid = parts[1];
       		removeNodeById(vid);
       		draw();
       		}
       		
        }

    }
 /*addRepoLibItem([{'name':'abc','module':'module'}]);*/
 function addRepoLibItem(data){
	 var addedItems = new Array();
	 var module = "";
	 for(var i=0;i<data.length;i++){
		 var item = repostore.newItem({"name":data[i].name,"module":data[i].module});
		 addedItems.push(item);
		 repostore.save();
		 module= data[i].module
	 }
	 var item = repostore.newItem({"name":module,"module":module,items:addedItems});
	 repostore.save();
	 
 }
	 
 		--></script>

</head>
<body class="tundra body" id="bodyelem">
<div id="top">
	<div dojoType="dijit.Dialog" id="createNodeDialog"
		title="Create New Node"
		execute="createNode(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="nodeId">Id: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="nodeId" id="nodeId" required="true" /></td>
				<td><label>Example : node1</label></td>
			</tr>
			<tr>
				<td><input type="hidden" name="xCoordinate" id="xCoordinate"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="yCoordinate" id="yCoordinate"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="rWidth" id="rWidth"
					constraints="{min:0}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="bHeight" id="bHeight"
					constraints="{min:0}" /></td>
			</tr>
			<tr>
				<td><label for="processclz">ProcessClass</label></td>
				<td><select value="org.ptg.processors.EventProcessor"
					name="processclz" id="processclz">
				</select></td>
			</tr>
			<tr>
				<td><label for="processquery">Query:</label></td>
				<td><textarea
						style="border-color: yellow; width: 100%; height: 100%;"
						name="processquery" id="processquery">select e as evt from Event e;</textarea>
				</td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Create</button></td>
			</tr>
		</table>
	</div>
	<!-- Update Node Dialog -->
	<div dojoType="dijit.Dialog" id="updateNodeDialog" title="Update Node"
		execute="updateNode(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="u_nodeId">Id: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="u_nodeId" id="u_nodeId" required="true" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="u_xCoordinate"
					id="u_xCoordinate" required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="u_yCoordinate"
					id="u_yCoordinate" required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="u_rWidth" id="u_rWidth"
					constraints="{min:0}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="u_bHeight" id="u_bHeight"
					constraints="{min:0}" /></td>
			</tr>
			<tr>
				<td><label for="u_processclz">ProcessClass</label></td>
				<td><select value="org.ptg.processors.EventProcessor"
					name="u_processclz" id="u_processclz">
				</select></td>
			</tr>
			<tr>
				<td><label for="u_processQuery">Query:</label></td>
				<td><textarea
						style="border-color: yellow; width: 100%; height: 100%;"
						name="u_processquery" id="u_processquery">select e as evt from Event e;</textarea>
				</td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Update Node</button></td>
			</tr>
		</table>
	</div>

	<!-- End Update Node Dialog -->
	<div dojoType="dijit.Dialog" id="createStreamDialog"
		title="Create New Stream"
		execute="createStream(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="streamname">Name:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="streamname" id="streamname" required="true" /></td>
				<td><label>Example : TestStream</label></td>
			</tr>
			<tr>
				<td><label for="streameventtype">EventType:</label></td>
				<td><select value="TestEvent" name="streameventtype"
					id="streameventtype">
				</select></td>
			</tr>
			<tr>
				<td><label for="streamprocessor">Processor:</label></td>
				<td><select value="TestEventProcessor" name="streamprocessor"
					id="streamprocessor">
				</select></td>
			</tr>
			<tr>
				<td><label for="streamextra">Extra: </label></td>
				<td><textarea
						style="border-color: yellow; width: 100%; height: 100%;"
						name="streamextra" id="streamextra"></textarea></td>
			</tr>
			<tr>
				<td><label for="exceptionstream">Exception Stream: </label></td>
				<td><textarea
						style="border-color: yellow; width: 100%; height: 100%;"
						name="exceptionstream" id="exceptionstream"></textarea></td>
			</tr>
			<tr>
				<td><input type="hidden" name="streamx" id="streamx"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="streamy" id="streamy"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Create</button></td>
			</tr>
		</table>
	</div>
	<!-- Update Stream Dialog -->
	<div dojoType="dijit.Dialog" id="updateStreamDialog"
		title="Modify Stream"
		execute="updateStream(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="u_streamname">Name:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="u_streamname" id="u_streamname" required="true" /></td>
			</tr>
			<tr>
				<td><label for="u_streameventtype">EventType:</label></td>
				<td><select value="TestEvent" name="u_streameventtype"
					id="u_streameventtype">
				</select></td>
			</tr>
			<tr>
				<td><label for="u_streamprocessor">Processor:</label></td>
				<td><select value="TestEventProcessor" name="u_streamprocessor"
					id="u_streamprocessor">
				</select></td>
			</tr>
			<tr>
				<td><label for="u_streamextra">Extra: </label></td>
				<td><textarea
						style="border-color: yellow; width: 100%; height: 100%;"
						name="u_streamextra" id="u_streamextra"></textarea></td>
			</tr>
			<tr>
				<td><label for="u_exceptionstream">Exception Stream: </label></td>
				<td><textarea
						style="border-color: yellow; width: 100%; height: 100%;"
						name="u_exceptionstream" id="u_exceptionstream"></textarea></td>
			</tr>

			<tr>
				<td><input type="hidden" name="u_streamx" id="u_streamx"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="u_streamy" id="u_streamy"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Update Stream</button></td>
			</tr>
		</table>
	</div>
	<!-- End update Stream Dialog -->
	<div dojoType="dijit.Dialog" id="createEventDialog"
		title="Create New Event"
		execute="createEvent(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="eventname">Name:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="eventname" id="eventname" required="true" /></td>
				<td><label>Example : TestEvent</label></td>
			</tr>
			<tr>
				<td><label for="eventstore">EventStore: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="eventstore" id="eventstore" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="eventx" id="eventx"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="eventy" id="eventy"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Create</button></td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="createRouteDialog"
		title="Create New Route"
		execute="createRoute(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="routename">Name:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="routename" id="routename" required="true" /></td>
				<td><label>Example : TestRoute</label></td>
			</tr>
			<tr>
				<td><label for="routedesc">Description: </label></td>
				<td><textarea
						style="border-color: yellow; width: 100%; height: 100%;"
						name="routedesc" id="routedesc"></textarea></td>
				<td><label>from(a).to(b)</label></td>
			</tr>
			<tr>
				<td><input type="hidden" name="routex" id="routex"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td><input type="hidden" name="routey" id="routey"
					required="true" constraints="{min:0, max:900}" /></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Create</button></td>
			</tr>
		</table>
	</div>
	<!-- Deployement dialog Start-->
	<div dojoType="dijit.Dialog" resizable="true" id="createDeployDialog"
		title="Deploy Resource">
		<form name="deployForm" id="deployForm" method="POST"
			enctype="multipart/form-data">
			<table>
				<tr>
					<td><label for="deployName">Deploy Name</label></td>
					<td><input dojoType="dijit.form.ValidationTextBox" type="text"
						name="deployName" id="deployName" required="true"
						constraints="{min:0, max:900}" /></td>
				</tr>

				<tr>
					<td><label for="deployType">Type:</label></td>
					<td><select value="" name="deployType" id="deployType">
							<option>Java</option>
							<option>Class</option>
							<option>Jar</option>
							<option>Script</option>
							<option>Template</option>
							<option>Jsp</option>
							<option>ClassService</option>
							<option>ServiceArchive</option>
							<option>ProtocolBuf</option>
							<option>xml</option>
							<option>yaml</option>
							<option>process</option>
							<option>in</option>
							<option>out</option>
							<option>rule</option>
							<option>jaxb</option>
							<option>jaxbEvents</option>
							<option>javarule</option>
							<option>cacheitem</option>
							<option>titan</option>
						    <option>plugin</option>
						    <option>image</option>
					</select></td>
				</tr>
				<tr>
					<td><label for="uploadedResource">Resource</label></td>
					<td><input type="file" name="uploadedResource"
						id="uploadedResource"></input></td>
				</tr>
				<tr>
					<td colspan="3"><button dojoType="dijit.form.Button"
							onclick='createDeploy()'>Deploy</button></td>
				</tr>
			</table>
		</form>
	</div>

	<!-- Deployement dialog end -->

	<div dojoType="dijit.Dialog" resizable="true" id="createPageDialog"
		title="Create/Update Web Page"
		execute="createPage(dojo.toJson(arguments[0], true))">

		<table>
			<tr>
				<td><label for="p_streamname">Page Processor:</label></td>
				<td><select name="p_streamname" id="p_streamname">
				</select></td>
			</tr>
			<tr>
				<td><label for="pagename">Name:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="pagename" id="pagename" required="true" /></td>
			</tr>
			<tr>

				<td><label for="pagecontent">Content </label></td>
				<td><div dojoType="dijit.layout.ContentPane"
						title="Page Content" id="pagecontentwnd">
						<textarea style="border-color: yellow; width: 100%; height: 100%;"
							name="pagecontent" id="pagecontent"></textarea></td>
				</div>
			</tr>
			<tr>
				<td colspan="1"><button dojoType="dijit.form.Button"
						type="submit">Create/Update</button></td>
				<td colspan="1"><button dojoType="dijit.form.Button"
						type="button" onclick="exportPageDynamicUrl()">Export
						Dynamic Url</button></td>
				<td colspan="1"><button dojoType="dijit.form.Button"
						type="button" onclick="exportPageStaticUrl()">Export
						Static Url</button></td>
			</tr>
			<tr>
				<td colspan="1"><button dojoType="dijit.form.Button"
						type="button" onclick="editStreamPage()">Edit Content</button></td>
				<td colspan="1"><button dojoType="dijit.form.Button"
						onclick='closePageDialog()'>Close</button></td>
			</tr>
		</table>
		<div dojoType="dojox.layout.ResizeHandle" targetId="pagecontentwnd"></div>
	</div>
	<div id="Widgets">
		<div id="ndtype2">Widgets</div>
		<div>
			<div id="wdgts">
				<div class="dojoDndItem">Blinker</div>
				<div class="dojoDndItem">Dimmer</div>
				<div class="dojoDndItem">Light</div>
				<div class="dojoDndItem">Green Bulb</div>
				<div class="dojoDndItem">Red Bulb</div>
				<div class="dojoDndItem">Flat Green Led</div>
				<div class="dojoDndItem">Flat Red Led</div>
				<div class="dojoDndItem">Flat Yellow Led</div>
				<div class="dojoDndItem">Tag</div>
				<div class="dojoDndItem">Continous Timer</div>
				<div class="dojoDndItem">Onetime Timer</div>
				<div class="dojoDndItem">InputPort</div>
				<div class="dojoDndItem">OutputPort</div>
				<div class="dojoDndItem">AuxPort</div>
				<div class="dojoDndItem" id="Port">Port</div>
				<div class="dojoDndItem">PortMethod</div>
				<div class="dojoDndItem">ComplexInputPort</div>
				<div class="dojoDndItem">ComplexOutputPort</div>
				<div class="dojoDndItem">ComplexAuxPort</div>
				<div class="dojoDndItem">ComplexPort</div>
				<div class="dojoDndItem">ComplexPortMethod</div>
			</div>
			<div dojoType="dojox.layout.ResizeHandle" targetId="Widgets"></div>
		</div>
	</div>
	<div id="dynamicnodes"></div>
	<div id="pagemodules"></div>
	<div dojoType="dijit.Menu" id="popup1campus"
		contextMenuForWindow="false" style="display: none;"
		targetNodeIds="graph">
		<div dojoType="dijit.MenuItem" disabled="false" onclick="draw();">Redraw</div>
		<div dojoType="dijit.MenuItem" disabled="false"
			onclick="mycustommenu(null,null);">Show Custom Menu</div>
		<div dojoType="dijit.MenuItem" disabled="false" onclick="hideCMenu();">Hide
			Custom Menu</div>
		<div dojoType="dijit.MenuItem" disabled="false"
			onclick="showRelatedNodes();">Show Pages</div>
		<div dojoType="dijit.MenuItem" disabled="false"
			onclick="setStreamConfigDlg();">Config</div>
		<div dojoType="dijit.MenuItem" disabled="false"
			onclick="updateObject();">Update Object</div>
		<div dojoType="dijit.MenuItem" disabled="false"
			onclick="execElementFromGraph();">
			<B><font color="blue">Execute!!</font></B>
		</div>
		<div dojoType="dijit.MenuItem" disabled="false"
			onclick="setAnonConfigDlg();">
			<B><font color="blue">Config Options!!</font></B>
		</div>
		<div dojoType="dijit.MenuItem" disabled="false"
			onclick="ShowSelectedAdvancedMenu();">
			<B><font color="blue">Advanced</font></B>
		</div>




		<div dojoType="dijit.PopupMenuItem">
			<span>Events</span>
			<div dojoType="dijit.Menu" id="event_popupmenu">
				<div dojoType="dijit.MenuItem" id="event_popupmenu_addexisting"
					disabled="false" onclick="addExistingEvent();">Add Existing
					Event</div>
				<div dojoType="dijit.MenuItem" id="addeventmenuitem"
					disabled="false" onclick="showAddEventDialogAtMouse();">Add
					Event</div>
				<div dojoType="dijit.MenuItem" id="event_popupmenu_addproperty"
					disabled="false" onclick="ShowAddEventPropertyAtMouse();">Add
					Event Property</div>
				<div dojoType="dijit.MenuItem" id="event_popupmenu_save"
					disabled="false" onclick="SaveEvent();">Save Event</div>
				<div dojoType="dijit.MenuItem" id="event_popupmenu_delete"
					disabled="false" onclick="DeleteEvent();">Delete Event</div>
				<div dojoType="dijit.MenuItem" id="event_popupmenu_deletefromserver"
					disabled="false" onclick="DeleteEventFromServer();">Delete
					From Server</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuItem">
			<span>Streams</span>
			<div dojoType="dijit.Menu">
				<div dojoType="dijit.MenuItem" id="addstreammenuitem"
					disabled="false" onclick="showAddStreamDialogAtMouse();">Add
					Stream</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="ShowAddStreamPropertyDialog();">Add Stream Property</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="showUpdateStreamDialogAtMouse();">Modify Stream</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="SaveStream();">Save Stream</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="DeleteStream();">Delete Stream</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="testEventFromUser();">Test Event To Page Stream</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="testEventAutomated();">Send Test Event</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="setStreamConfigDlg();">Stream Config</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="covertToNonFreeHand();">Remove FreeHand Drawing</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="DeleteStreamFromServer();">Delete From Server</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuItem">
			<span>Nodes</span>
			<div dojoType="dijit.Menu">
				<div dojoType="dijit.MenuItem" id="addnodemenuitem" disabled="false"
					onclick="showAddNodeDialogAtMouse();">Add Node</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="SaveNode();">Save Node</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="showUpdateNodeDialogAtMouse();">Modify Node</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="setNodeConfigDlg();">Node Config</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="if(lastSelectedNode!=null)lastSelectedNode.configItems=null;">Clear
					Extra Config</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="DeleteNode();">Delete Node</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="editJavaProcess();">Edit Java Process</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="DeleteNodeFromServer();">Delete From Server</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="updateToDoStatus();">Update Completion Status</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="updateToDoAction();">Update action</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="ModifySchemaForStream();">ModifySchema</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuItem">
			<span>Routes</span>
			<div dojoType="dijit.Menu">
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="showAddRouteDialogAtMouse();">Add Route</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="SaveRoute();">Save Route</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="DeleteRoute();">Delete Route</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuItem">
			<span>Connections</span>
			<div dojoType="dijit.Menu">
				<div dojoType="dijit.MenuItem" id="addconnectionmenuitem"
					onClick="showAddConnDialogAtMouse();">Add Connection</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="SaveConnection();">Save Conn</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="DeleteConnection();">Delete Conn</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="showUpdateConnDialogAtMouse();">Modify Conn</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="editConnection();">Edit Connection</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="modifyConnectionColor();">Edit Connection Color</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="DeleteConnFromServer();">Delete From Server</div>
				<div dojoType="dijit.MenuItem" disabled="false"
					onclick="reverseSelectedConnection();">Reverse</div>

			</div>
		</div>
		<div dojoType="dijit.PopupMenuItem">
			<span>Notes</span>
			<div dojoType="dijit.Menu">
				<div dojoType="dijit.MenuItem" onClick="showNoteDlg();">Add
					Note</div>
				<div dojoType="dijit.MenuItem" onClick="removeNote();">Remove
					Note</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuItem">
			<span>Group</span>
			<div dojoType="dijit.Menu">
				<div dojoType="dijit.MenuItem" onClick="openGroup();">Open
					Group</div>
				<div dojoType="dijit.MenuItem" onClick="openGroups();">Open
					All Groups</div>
				<div dojoType="dijit.MenuItem" onClick="closeGroups();">Close
					all Groups</div>
				<div dojoType="dijit.MenuItem" onClick="renameGroup();">Rename</div>
				<div dojoType="dijit.MenuItem"
					onClick="freeHandRegion(finditemsinregion,createGroup)">Create
					Group</div>
				<div dojoType="dijit.MenuItem" onClick="deleteGroup();">Delete
					Group</div>
				<div dojoType="dijit.MenuItem" onClick="modifyGroup();">Modify
					Group</div>
			</div>
		</div>
	</div>
	<div dojoType="dijit.Dialog" id="addExistEventDlg"
		title="Choose Event To Add"
		execute="addExistingEventToPage(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="chooseExistEvent">Event</label></td>
				<td><select value="" name="chooseExistEvent"
					id="chooseExistEvent">
				</select></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Add Event</button></td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="openGraphDesignDialog"
		title="Choose GraphTo open"
		execute="getGraphDesignFromServer(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="choosegraphid2">Graph :</label></td>
				<td><select value="" name="choosegraphid2" id="choosegraphid2">
				</select></td>
			</tr>
			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Load
						Graph</button>
				</td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="deleteElementDialog"
		title="Choose Element To Delete"
		execute="deleteElement(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="chooseelem">Element :</label></td>
				<td><select value="" name="chooseelem" id="chooseelem">
				</select></td>
			</tr>
			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Delete
						Element</button>
				</td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="updateElementDialog"
		title="Choose Element To Update"
		execute="updateElement(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="chooseuelem">Element :</label></td>
				<td><select value="" name="chooseuelem" id="chooseuelem"
					onchange="updatePropName();">
				</select></td>
			</tr>
			<tr>
				<td><label for="eleprop">Property :</label></td>
				<td><select value="" name="eleprop" id="eleprop"
					onchange="updatePropValue();">
				</select></td>
			</tr>
			<tr>
				<td><label for="propval">Value :</label></td>
				<td><input type="txt" value="" name="propval" id="propval">
					</input></td>
			</tr>
			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Update
						Element</button>
				</td>
			</tr>

		</table>
	</div>
	<div dojoType="dijit.Dialog" id="addModuleNodeDialog"
		title="Choose Module to add as node"
		execute="addModuleNode(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="modnodeEle">Module :</label></td>
				<td><select value="" name="modnodeEle" id="modnodeEle">
				</select></td>
			</tr>
			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Update
						Element</button>
				</td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="animElementDialog"
		title="Choose Element To animate"
		execute="animElement(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="animelem">Element :</label></td>
				<td><select value="" name="animelem" id="animelem">
				</select></td>
			</tr>
			<tr>
				<td><label for="animAction">Action :</label></td>
				<td><select value="" name="animAction" id="animAction">
				</select></td>
			</tr>
			<tr>
				<td><label for="animObject">Animation :</label></td>
				<td><select value="" name="animObject" id="animObject">
				</select></td>
			</tr>
			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Choose
						Element</button>
				</td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="chooseEventDialog"
		title="Choose Event To Listen" execute="listenToEvent();">
		<table>
			<tr>
				<td><label for="chooseeventid">Event</label></td>
				<td><select value="" name="chooseeventid" id="chooseeventid"
					onchange="if(this.selectedIndex > 0){var a = this.options[this.selectedIndex].value; ShowEventProperty(a);}">
				</select></td>
			</tr>
			<tr>
				<td><label for="codecontent">Code To Execute</label></td>
				<td>
					<div dojoType="dijit.layout.ContentPane" title="Page Content"
						id="codecontentdiv">
						<textarea style="border-color: yellow; width: 100%; height: 100%;"
							name="codecontent" id="codecontent"></textarea>
				</td>
				</div>
				<div dojoType="dojox.layout.ResizeHandle" targetId="codecontentdiv"></div>
			</tr>
			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Listen
						To Event</button>
				</td>

			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="addStaticComponentDialog"
		title="Add static Component"
		execute="addStaticComponentFromServer(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="addstaticcomponentname">UniqueId :</label></td>
				<td><select value="" name="addstaticcomponentname"
					id="addstaticcomponentname" onchange="addStaticCompChange();">
				</select></td>
			</tr>
			<tr>
				<td><label for="addstaticcomponentid">id :</label></td>
				<td><input type="text" value="" name="addstaticcomponentid"
					id="addstaticcomponentid"></input></td>
			</tr>
			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Load
						Graph</button>
				</td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="savestaticElementDialog"
		title="Please enter element to save"
		execute="saveStaticElement(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="saveelem">Element :</label></td>
				<td><select value="" name="saveelem" id="saveelem"
					onchange="saveStaticCompChange();">
				</select></td>
			</tr>
			<tr>
				<td><label for="saveelemdoc">Documentation :</label></td>
				<td><input type="txt" value="" name="saveelemdoc"
					id="saveelemdoc"> </input></td>
			</tr>
			<tr>
				<td><label for="saveelemname">Unique id :</label></td>
				<td><input type="txt" value="" name="saveelemname"
					id="saveelemname"> </input></td>
			</tr>

			<tr>
				<td colspan="3">
					<button dojoType="dijit.form.Button" type="submit">Save
						Static Element</button>
				</td>
			</tr>
		</table>
	</div>


	<div dojoType="dijit.Dialog" id="addPropertyDialog"
		title="Add Event Property"
		execute="addEventProperty(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="aeeventid">Event: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="aeeventid" id="aeeventid" required="true" /></td>
				<td><label>Example : TestEvent</label></td>
			</tr>
		</table>
		<table>
			<tr>
				<td><label for="aename">Name: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="aename" id="aename" required="true" /></td>
			</tr>
		</table>
		<table>
			<tr>
				<td><label for="aeindexid">Index: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="aeindexid" id="aeindexid" required="true" /></td>
				<td><label>Example : 1</label></td>
			</tr>
		</table>
		<table>
			<tr>
				<td><label for="aetype">type: </label></td>
				<td><select value="" name="aetype" id="aetype">
						<option>java.lang.String</option>
						<option>java.lang.Double</option>
						<option>java.lang.Boolean</option>
						<option>java.lang.Float</option>
						<option>java.lang.Integer</option>
						<option>java.lang.Long</option>
						<option>java.util.Date</option>
						<option>boolean</option>
						<option>float</option>
						<option>double</option>
						<option>long</option>
						<option>int</option>

				</select></td>
			</tr>
		</table>
		<table>

			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Add Property</button></td>
			</tr>
		</table>

	</div>

	<div dojoType="dijit.Dialog" id="addStreamPropertyDialog"
		title="Add Stream Property"
		execute="addStreamProperty(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="streventid">Stream: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="streventid" id="streventid" required="true" /></td>
				<td><label>Example : TestEvent</label></td>
			</tr>
		</table>
		<table>
			<tr>
				<td><label for="strname">Name: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="strname" id="strname" required="true" /></td>
				<td><label>Example : name</label></td>
			</tr>
		</table>
		<table>
			<tr>
				<td><label for="strtype">Type: </label></td>
				<td><select value="" name="strtype" id="strtype">
						<option>java.lang.String</option>
						<option>java.lang.Double</option>
						<option>java.lang.Boolean</option>
						<option>java.lang.Float</option>
						<option>java.lang.Integer</option>
						<option>java.lang.Long</option>
						<option>boolean</option>

						<option>float</option>
						<option>double</option>
						<option>long</option>
						<option>int</option>

				</select></td>
			</tr>
		</table>
		<table>
			<tr>
				<td><label for="strproptype">Property Type: </label></td>
				<td><select value="property" name="strproptype"
					id="strproptype">
						<option>property</option>
						<option>propertyXmlExpr</option>
						<option>body</option>
						<option>bodyXmlExpr</option>
						<option>propertyJsonPathExpr</option>
						<option>bodyJsonPathExpr</option>
						<option>customcode</option>
						<option>propjexl</option>
						<option>bodyjexl</option>
						<option>propjxpath</option>
						<option>bodyjxpath</option>
				</select></td>
			</tr>
			<tr>
				<td><label for="strdestporp">DestinationProp: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="strdestporp" id="strdestporp" required="true" /></td>
			</tr>


			<tr>
				<td><label for="strxmlxpr">XML Expr: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="strxmlxpr" id="strxmlxpr" required="true" /></td>
			</tr>
			<tr>
				<td><label for="strpropindex">Index: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="strpropindex" id="strpropindex" required="true" /></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Add Stream Property</button></td>
			</tr>
		</table>

	</div>
	<div dojoType="dijit.Dialog" id="openGraphDialog"
		title="Choose GraphTo open"
		execute="getGraphFromServer(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="choosegraphid">From :</label></td>
				<td><select value="" name="choosegraphid" id="choosegraphid">
				</select></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Load Graph</button></td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="importGraphDialog"
		title="Choose GraphTo import"
		execute="importGraphFromServer(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="chooseimportgraphid">From :</label></td>
				<td><select value="" name="chooseimportgraphid"
					id="chooseimportgraphid">
				</select></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Import Graph</button></td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="saveConnecDialog"
		title="Save Connection Dialog"
		execute="saveConnectionItem(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="saveconnid">From :</label></td>
				<td><select value="" name="saveconnid" id="saveconnid">
				</select></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Save Connection</button></td>
			</tr>
		</table>
	</div>

	<div dojoType="dijit.Dialog" id="saveGraphDialog"
		title="Save Graph Dialog"
		execute="saveGraphData(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="savegraphid">Save Graph As</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="savegraphid" id="savegraphid" required="true" /></td>
			</tr>
			<tr>
				<td><label for="savegraphtype">Type of graph</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="savegraphtype" id="savegraphtype" required="true"
					value="graph" /></td>
			</tr>
			<tr>
				<td><label for="savegraphicon">Icon</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="savegraphicon" id="savegraphicon" required="true" value="" /></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Save Graph</button></td>
			</tr>
		</table>
	</div>
	<div dojoType="dijit.Dialog" id="addNodeDialog" title="Add Note"
		execute="addNoteItem(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="savenodeid">Note:</label></td>
				<td>
					<div dojoType="dijit.layout.ContentPane" title="Page Content"
						id="savenodeidpageid">
						<textarea
							style="border-color: yellow; width: 100%; height: 180px;"
							name="savenodeid" id="savenodeid"></textarea>
				</td>
				</div>
				<div dojoType="dojox.layout.ResizeHandle"
					targetId="savenodeidpageid"></div>
				</td>

			</tr>
			<tr>
				<td><label for="noteforid">Attach To:</label></td>
				<td><select value="" name="noteforid" id="noteforid">
				</select></td>
			</tr>

			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Save Note</button></td>
			</tr>
		</table>
	</div>

	<div dojoType="dijit.Dialog" id="createConnectionDialog"
		title="Create Connection Node"
		execute="createConnectionNode(dojo.toJson(arguments[0], true))">
		<table>
			<tr>
				<td><label for="connectionId">Id: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="connectionId" id="connectionId" required="true" /></td>
				<td><label>Example : connection1</label></td>
			</tr>
			<tr>
				<td><label for="fromNode">From :</label></td>
				<td><select value="" name="fromNode" id="fromNode">
				</select></td>
			</tr>
			<tr>
				<td><label for="toNode">To:</label></td>
				<td><select value="" name="toNode" id="toNode">
				</select></td>
			</tr>
			<tr>
				<td><label for="ctype">Connection Type:</label></td>
				<td><select value="" name="ctype" id="ctype">
						<option>org.ptg.processors.connection.ConditionalConnection</option>
						<option>org.ptg.processors.connection.FileRuleBasedConnection</option>
						<option>org.ptg.processors.connection.GlobalRuleBasedConnection</option>
						<option>org.ptg.processors.connection.KeyedRuleBasedConnection</option>
						<option>org.ptg.processors.connection.RuleBasedConnection</option>
						<option>org.ptg.processors.connection.camel</option>
						<option>arbitconnection</option>
						<option>dependency</option>
						<option>loopback</option>
				</select></td>
			</tr>
			<tr>
				<td><label for="conncond">Condition:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="conncond" id="conncond" required="true" /></td>
				<td><label>select event as evt from CacheEvent</label></td>
			</tr>
			<tr>
				<td><label for="attribCC">attrib:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="attribCC" id="attribCC" required="true" value="" /></td>
			<tr>
				<td><label for="sequenceCC">sequence:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="sequenceCC" id="sequenceCC" required="true" value="0" /></td>
			</tr>
			<input type="hidden" name="connshapeid" id="connshapeid"></input>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Create Connection</button></td>
			</tr>
		</table>
	</div>
	<!-- Modify Connection -->
	<div dojoType="dijit.Dialog" id="updateConnectionDialog"
		title="Modify Connection" execute="updateConnection(arguments[0])">
		<table>
			<tr>
				<td><label for="u_connectionId">Id: </label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="u_connectionId" id="u_connectionId" required="true" /></td>
				<td><label>Example : connection1</label></td>
			</tr>
			<tr>
				<td><label for="u_fromNode">From :</label></td>
				<td><select value="" name="u_fromNode" id="u_fromNode">
				</select></td>
			</tr>
			<tr>
				<td><label for="u_toNode">To:</label></td>
				<td><select value="" name="u_toNode" id="u_toNode">
				</select></td>
			</tr>
			<tr>
				<td><label for="u_ctype">Connection Type:</label></td>
				<td><select value="" name="u_ctype" id="u_ctype">
						<option>org.ptg.processors.connection.ConditionalConnection</option>
						<option>org.ptg.processors.connection.FileRuleBasedConnection</option>
						<option>org.ptg.processors.connection.GlobalRuleBasedConnection</option>
						<option>org.ptg.processors.connection.KeyedRuleBasedConnection</option>
						<option>org.ptg.processors.connection.RuleBasedConnection</option>
						<option>org.ptg.processors.connection.camel</option>
						<option>arbitconnection</option>
						<option>dependency</option>
						<option>loopback</option>
				</select></td>
			</tr>
			<tr>
				<td><label for="u_conncond">Condition:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="u_conncond" id="u_conncond" required="true" /></td>
				<td><label>select event as evt from Event</label></td>
			</tr>
			<tr>
				<td><label for="attribEC">attrib:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="attribEC" id="attribEC" required="true" value="" /></td>
			</tr>
			<tr>
				<td><label for="sequenceEC">sequence:</label></td>
				<td><input dojoType="dijit.form.ValidationTextBox" type="text"
					name="sequenceEC" id="sequenceEC" required="true" value="0" /></td>
			</tr>
			<tr>
				<td colspan="3"><button dojoType="dijit.form.Button"
						type="submit">Modify Connection</button></td>
			</tr>
		</table>
	</div>
	<!-- End Modify Connection -->
	<div id="menubar" dojoType="dijit.MenuBar">
		<div dojoType="dijit.PopupMenuBarItem">
			<span>File</span>
			<div dojoType="dijit.Menu" id="fileMenu">
				<div dojoType="dijit.MenuItem" id="openGraphMenuId"
					onClick="menuItemClicked(this.id);openGraph()">Open Graph</div>
				<div dojoType="dijit.MenuItem" id="saveCurrentGraphId"
					onClick="menuItemClicked(this.id);saveGraph()">Save Current Graph</div>
				<div dojoType="dijit.MenuItem" id="clearGraphMenuId"
					onClick="menuItemClicked(this.id);clearGraph()">New Graph</div>
				<div dojoType="dijit.MenuItem" id="clearGraphRedrawMenuId"
					onClick="menuItemClicked(this.id);draw()">Redraw</div>
				<div dojoType="dijit.MenuItem" id="showHistoricalMenuId" onClick="menuItemClicked(this.id);showHistorical()">Show
					Historical</div>
				<div dojoType="dijit.MenuItem" id="CompileProcessMenuId" onClick="menuItemClicked(this.id);CompileProcess()">Compile
					Graph</div>
				<div dojoType="dijit.MenuItem" id="compileGraphOnServerMenuId" onClick="menuItemClicked(this.id);compileGraphOnServer()">Compile
					Graph on server</div>
				<div dojoType="dijit.MenuItem" id="validateProcessMenuId" onClick="menuItemClicked(this.id);validateProcess()">Check
					Graph Status</div>
				<div dojoType="dijit.MenuItem" id="compileCamelProcessMenuId" onClick="menuItemClicked(this.id);compileCamelProcess();">Compile
					Camel Process</div>
				<div dojoType="dijit.MenuItem" id=moveGraphOnClickMenuId" onClick="menuItemClicked(this.id);moveGraphOnClick(false)">Move
					Graph</div>
				<div dojoType="dijit.MenuItem" id="moveGraphOnClickMenuId" onClick="menuItemClicked(this.id);moveGraphOnClick(true)">Move
					Selective Graph</div>
				<div dojoType="dijit.MenuItem" id="fixGraphMenuIdMenuId" onClick="menuItemClicked(this.id);fixGraph()">Fix Graph</div>
				<div dojoType="dijit.MenuItem" id="showGraphItemStatusMenuId" onClick="menuItemClicked(this.id);showGraphItemStatus()">Show
					Graph Elements</div>
				<div dojoType="dijit.MenuItem" id="saveWideConfigMenuId" onClick="menuItemClicked(this.id);saveWideConfig()">Save
					Wide Configuration</div>
				<div dojoType="dijit.MenuItem" id="getWideConfigMenuId" onClick="menuItemClicked(this.id);getWideConfig()">Load
					Wide Configuration</div>
				<div dojoType="dijit.MenuItem" id="modifyWideConfigurationMenuId" onClick="menuItemClicked(this.id);modifyWideConfiguration()">Configure
					Wide</div>
				<div dojoType="dijit.MenuItem" id="DeployPageMenuId" onClick="menuItemClicked(this.id);DeployPage()">
					<b>Deploy Resource</b>
				</div>
				<div dojoType="dijit.MenuItem" id="enableBamMenuId" onClick="menuItemClicked(this.id);enableBam()">Update
					BAM</div>
				<div dojoType="dijit.MenuItem" id="startSyncingMenuId" onClick="menuItemClicked(this.id);startSyncing()">Start
					Streamer</div>
				<div dojoType="dijit.MenuItem" id="stopSyncingMenuId" onClick="menuItemClicked(this.id);stopSyncing()">Stop
					Streamer</div>
				<div dojoType="dijit.MenuItem" id="showInspectorMenuId" onClick="menuItemClicked(this.id);showInspector()">Inspector</div>
				<div dojoType="dijit.MenuItem" id="undoMenuId" onClick="menuItemClicked(this.id);undo()">Undo</div>
				<div dojoType="dijit.MenuItem" id="redoMenuId" onClick="menuItemClicked(this.id);redo()">Redo</div>
				<div dojoType="dijit.MenuItem" id="checkPointMenuId" onClick="menuItemClicked(this.id);checkPoint()">Check
					Point</div>


			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Connection</span>
			<div dojoType="dijit.Menu" id="connMenu">
				<div dojoType="dijit.MenuItem" id="drawFreeFlowConnnMenuId" onClick="menuItemClicked(this.id);drawFreeFlow(true)">Free
					Hand Connection</div>
				<div dojoType="dijit.MenuItem" id="drawFreeFlowArbitConnMenuId" onClick="menuItemClicked(this.id);drawFreeFlow(false)">Free
					Hand Arbit Connection</div>
				<div dojoType="dijit.MenuItem" id="setConnColorMenuId" onClick="menuItemClicked(this.id);setConnColor();">Set
					Connection Color</div>
				<div dojoType="dijit.MenuItem" id="drawConnOnClicksMenuId" onClick="menuItemClicked(this.id);drawConnOnClicks()">Connect
					Streams</div>
				<div dojoType="dijit.MenuItem" id="drawArbitConnOnClicksMenuId" onClick="menuItemClicked(this.id);drawArbitConnOnClicks()">Arbit
					Connect Streams</div>
				<div dojoType="dijit.MenuItem" id="toggleStreamToNodeConnMenuId"
					onClick="menuItemClicked(this.id);if(showStreamToNode==false){showStreamToNode=true;}else {showStreamToNode=false;}">
					<B><font color="blue">Toggle Stream To Node Conn</font></B>
				</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Free Hand</span>
			<div dojoType="dijit.Menu" id="freeHandMenu">
				<div dojoType="dijit.MenuItem"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createLayer)" id="createLayerMenuId">Create
					Layer</div>
				<div dojoType="dijit.MenuItem"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createAvoidanceLayer)" id="createAvoidanceLayerMenuId">Create Avoidance
					Layer</div>
				<div dojoType="dijit.MenuItem"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createGroupProcNode)" id="CreateFastProcessorMenuId">Create
					Fast Processor</div>
				<div dojoType="dijit.MenuItem"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createGroup)" id="GroupSelectionMenuId">Group
					Selection</div>
				<div dojoType="dijit.MenuItem"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,yarrange)" id="AllignHorizontallyMenuId">Allign
					horizontally</div>
				<div dojoType="dijit.MenuItem"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,xarrange)"  id="AllignVerticallyMenuId">Align
					Vertically</div>
				<div dojoType="dijit.MenuItem" id="connectWithIntersectorMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinintersection,annotateComp)">
					<b>Connect With Intersector</b>
				</div>
			     <div dojoType="dijit.MenuItem" id="createPatternId"
                    onClick="menuItemClicked(this.id);freeHandRegion(finditemsinintersection,createPattern)">
                    <b>Create Pattern</b>
                </div>
				<div dojoType="dijit.MenuItem" id="selectWithFreeHandMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createSelection)">
					<b>Select With Free Hand</b>
				</div>
				<div dojoType="dijit.MenuItem" id="setModuleWigthMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,setModWeight)">
					<b>Set module weight</b>
				</div>
				<div dojoType="dijit.MenuItem" id="portifyInModuleMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,portifyModuleInput)">
					<b>Portify Module(in)</b>
				</div>
				<div dojoType="dijit.MenuItem" id="portifyOutModuleMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,portifyModuleOutput)">
					<b>Portify Module(out)</b>
				</div>
				<div dojoType="dijit.MenuItem" id="portifyAuxModuleMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,portifyModuleAux)">
					<b>Portify Module(aux)</b>
				</div>
				<div dojoType="dijit.MenuItem" id="CreateCollectionMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createSelectGroup)">
					<b>Create Collection</b>
				</div>
				<div dojoType="dijit.MenuItem"  id="pasteFreeHandMenuId" onClick="menuItemClicked(this.id);pasteFreeHand();">
					<b>Paste Selection</b>
				</div>
				<div dojoType="dijit.MenuItem"  id="testMappingMenuId" 
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,testSelection)">Test
					Mapping</div>
				<div dojoType="dijit.MenuItem"  id="creteEventFromTypeMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createEventFromTypeSel)">Create
					Event From Type</div>
				<div dojoType="dijit.MenuItem"  id="deleteFreeHandSelectionMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,deleteSelection)">Delete
					Free hand Selection</div>
				<div dojoType="dijit.MenuItem"  id="CloneFreeHandSelectionMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,cloneSelection)">Clone
					Selection</div>
				<div dojoType="dijit.MenuItem"  id="CompileSelectedItemsMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,compileSelected)">Compile
					selected items</div>
				<div dojoType="dijit.MenuItem"  id="renameSelectedItemsMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,renameSelected)">Rename
					selected</div>
				<div dojoType="dijit.MenuItem"  id="CreateRegionMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createRegion)">Create
					Region</div>
				<div dojoType="dijit.MenuItem"  id="CreateBoundingRectMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(findBoundingRect,createBoundRect)">Create
					Bounding Rect</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);setRegionType()" id="setRegionTypeMenuId">Set
					Region Type</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);setRegionOrder()"  id="setRegionOrderMenuId">Set
					Region Order</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);recompileRegions()"  id="recompileRegionsMenuId">Recompile
					regions</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);updateRegion()"  id="updateRegionsMenuId">Update
					Regions</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);deleteRegion()"  id="deleteRegionsMenuId">Delete
					Region</div>
				<div dojoType="dijit.MenuItem"  id="createArbitObjectMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,createArbitObject)">Create
					Arbit Object</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Export</span>
			<div dojoType="dijit.Menu" id="exportMenu">
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);importExistingGraph()" id="importExistingGraphMenuId">Import	Existing Graph</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);exportGraph()" id="exportExistingGraphMenuId">Export Graph</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);exportSelection()" id="exportSelectionGraphMenuId">Export	Selection</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);downloadCurrentDesign()" id="downloadCurrentDesignMenuId">Download Design</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);downloadCurrentGraph()" id="downloadCurrentGraphMenuId">Download Graph</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);downloadCurrentDesignAsPdf()" id="downloadCurrentGraphAsPdfMenuId">Download as PDF</div>
                <div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);downloadCurrentDesignAsImage()" id="downloadCurrentGraphAsImageMenuId">Download as Image</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Artist</span>
			<div dojoType="dijit.Menu" id="artistMenu">
				<div dojoType="dijit.MenuItem" id="artistsGraphMenuId"
					onClick="menuItemClicked(this.id);drawFreeFlow(false,true,100)">Artists graph</div>
				<div dojoType="dijit.MenuItem" id="setExtraMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,setGroupExtra)">Set
					Extra</div>
				<div dojoType="dijit.MenuItem" id="setProcessorMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,setGroupProcessor)">Set
					Processor</div>
				<div dojoType="dijit.MenuItem" id="setPropertyMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,setGroupProperty)">Set
					Property</div>
				<div dojoType="dijit.MenuItem" id="convertMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,convertGroup)">Convert</div>
				<div dojoType="dijit.MenuItem" id="AutoCompleteFreeHandMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,magicSpell1)">Auto
					Complete ( TODO )</div>
				<div dojoType="dijit.MenuItem" id="searchSelectedMenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,magicSpell2)">Search
					Selected ( TODO )</div>
				<div dojoType="dijit.MenuItem" id="magicSpell3MenuId"
					onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,magicSpell3)">Magic
					Spell3 ( TODO )</div>
				<div dojoType="dijit.MenuItem"  id="customMenuMenuId" onClick="menuItemClicked(this.id);mycustommenu();">Custom
					menu</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Web Page</span>
			<div dojoType="dijit.Menu" id="WebPageMenuId">
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);NewPage()">New Page</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Window</span>
			<div dojoType="dijit.Menu" id="WindowMenu">
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);hideAllWindows();">
					<B><font color="blue">Hide All Windows</font></B>
				</div>
				<div dojoType="dijit.MenuItem"  id="showAllWindowsMenuId" onClick="menuItemClicked(this.id);showAllWindows();">
					<B><font color="blue">Show All Windows</font></B>
				</div>
				<div dojoType="dijit.MenuSeparator" ></div>
				<div dojoType="dijit.MenuItem"  id="showHideCommandWindowMenuId"
					onClick="menuItemClicked(this.id);if(cw){hidediv('cmdnode');cw=false;}else{cw=true;showdiv('cmdnode');}">Command
					Window</div>
				<div dojoType="dijit.MenuItem"  id="showHideDocumentWindowMenuId"
					onClick="menuItemClicked(this.id);if(dw){hidediv('docnode');dw=false;}else{dw=true;showdiv('docnode');}">Document
					window</div>
				<div dojoType="dijit.MenuItem"  id="propertiesWindowShowHideMenuId"
					onClick="menuItemClicked(this.id);if(pw){hidediv('propnode');pw=false;}else{pw=true;showdiv('propnode');}">Properties
					window</div>
				<div dojoType="dijit.MenuItem" id="HistoryWindowShowHideMenuId"
					onClick="menuItemClicked(this.id);if(hw){hidediv('histnode');hw=false;}else{hw=true;showdiv('histnode');}">Historical
					window</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Routing Engine</span>
			<div dojoType="dijit.Menu" id="RoutingEngineMenu">
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);stopRE()" id="StopRoutingMenuId">Stop Routing
					Engine</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);startRE()" id="StartRoutingEngineMenuId">Start
					Routing Engine</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);restartRE()"  id="ReStartRoutingEngineMenuId">Re Start
					Routing Engine</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);addRoutesRE()"  id="AddRoutesToRoutingEngineMenuId">Add
					Routes To RoutingEngine</div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Mapper</span>
			<div dojoType="dijit.Menu" id="MapperMenu">
					<div dojoType="dijit.MenuItem" onClick="if(moveGroupElements){moveGroupElements=false}else{moveGroupElements=true}" id="moveGroupElementsMenuId">MoveGroupElements</div>
						
				<div dojoType="dijit.PopupMenuItem" id="codePopupMenuItem">
					<span>Java Code</span>
					<div id="javaCodeMenuColl" dojoType="dijit.Menu">
						<div dojoType="dijit.MenuItem" id="addMethodMappingMI"
							onClick="menuItemClicked(this.id);addMtdMapping()">Add Method Mapping</div>
						<div dojoType="dijit.MenuItem" id="addFieldMappingMI"
							onClick="menuItemClicked(this.id);addFldMapping()">Add Field Mapping</div>
						<div dojoType="dijit.MenuItem" id="createBeanMI"
							onClick="menuItemClicked(this.id);createBeanMappingProcessor()">Create Bean</div>
						<div dojoType="dijit.MenuItem" id="createVarMI"
							onClick="menuItemClicked(this.id);createVarOnlyMappingProcessor()">Create Var</div>
						<div dojoType="dijit.MenuItem" id="createKeyValuePairMI"
							onClick="menuItemClicked(this.id);createVarMappingProcessor()">Create KeyVal Pair</div>
						<div dojoType="dijit.MenuItem" id="createEventMappingMI"
							onClick="menuItemClicked(this.id);createEventMappingProcessor()">Create Event
							Mapping</div>
					</div>
				</div>

				<div dojoType="dijit.PopupMenuItem" id="sqlPopupMenuItem">
					<span>SQL</span>
					<div id="sqlMenuColl" dojoType="dijit.Menu">
						<div dojoType="dijit.MenuItem" id="parseSQLMI"
							onClick="menuItemClicked(this.id);parseSQL()">Parse SQL</div>
						<div dojoType="dijit.MenuItem" id="parseDynaSQLMI"
							onClick="menuItemClicked(this.id);parseDynaSQL()">Parse Dyna SQL</div>
							
						<div dojoType="dijit.MenuItem" id="compileSQLMappingOnServerMI"
							onClick="menuItemClicked(this.id);compileSQLMapOnServer()">Compile SQL Map</div>
						<div dojoType="dijit.MenuItem" id="compileAndRunSQLMapMI"
							onClick="menuItemClicked(this.id);compileSQLMapOnServer(null,null,true)">Compile and
							Run SQL Map</div>
						<div dojoType="dijit.MenuItem" id="compileCrossDBSQLMapMI"
							onClick="menuItemClicked(this.id);compileSQLDBMapOnServer()">Compile Cross DB SQL
							Map</div>
					</div>
				</div>

				<div dojoType="dijit.PopupMenuItem" id="connectPopupMenuItem">
					<span>Connect</span>
					<div id="connectMenuColl" dojoType="dijit.Menu">
						<div dojoType="dijit.MenuItem" id="addMappingMI"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,30,true)">Add Mapping</div>
						<div dojoType="dijit.MenuItem" id="addToRegionMappingMI"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,500,true,null,'region')">Add
							To Region Mapping</div>
						<div dojoType="dijit.MenuItem" id="addFromRegionMappingMI"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,500,true,'region',null)">Add
							From Region Mapping</div>
						<div dojoType="dijit.MenuItem" id="addToModuleMappingMI"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,false,null,'module')">Add
							To Module Mapping</div>
						<div dojoType="dijit.MenuItem" id="addModuleToModuleMappingMI"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,false,'module','module')">Add
							Module To Module Mapping</div>
						<div dojoType="dijit.MenuItem" id="addFromModuleMappingMI"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,true,'module',null)">Add
							From Module Mapping</div>
						<div dojoType="dijit.MenuItem" id="connectModuleToPorts"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,true,'module','Port')">
							<b>Connect Module to ports</b>
						</div>
						<div dojoType="dijit.MenuItem" id="connectPortsToModule"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,true,'Port','module')">
							<b>Connect Port to Module</b>
						</div>
						<div dojoType="dijit.MenuItem" id="connectPorts"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,true,'Port','Port')">
							<b>Connect Ports</b>
						</div>
						<div dojoType="dijit.MenuItem" id="connectWordsMenuItem"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,true,'ceword','ceword')">
							<b>Connect Words</b>
						</div>
					</div>
				</div>



				<div dojoType="dijit.PopupMenuItem" id="libPopupMenuItem">
					<span>Library</span>
					<div id="libaryMenuColl" dojoType="dijit.Menu">
						<div dojoType="dijit.MenuItem" id="addLibMI"
							onClick="menuItemClicked(this.id);openLibraryFunction()">Open Library</div>
						<div dojoType="dijit.MenuItem" id="saveLibMI"
							onClick="menuItemClicked(this.id);saveLibraryFunction()">Save Library</div>
						<div dojoType="dijit.MenuItem" id="addLibFunctionMI"
							onClick="menuItemClicked(this.id);addLibraryFunction()">Add Library</div>
					</div>
				</div>
				<div dojoType="dijit.PopupMenuItem" id="compilePopupMenuItem">
					<span>Compile</span>
					<div id="compileMenuColl" dojoType="dijit.Menu">
					<div dojoType="dijit.PopupMenuItem" id="machineExecutePopupMenuItem">
							<span>Machine</span>
								<div id="hardwareMachineMenuColl" dojoType="dijit.Menu">
							
								   <div dojoType="dijit.MenuItem" id="CompileGcodeOnServer"
			                            onClick="menuItemClicked(this.id);compileGcodeMapperOnServer()">
			                            <b>Compile Gcode</b>
			                        </div>
			                       <div dojoType="dijit.MenuItem" id="RunInGrblController"
			                            onClick="menuItemClicked(this.id);runViaGrblControllerOnServer()">
			                            <b>Execute GRBL</b>
			                        </div>
		                          <div dojoType="dijit.MenuItem" id="CompileEv3Model"
        			                    onClick="menuItemClicked(this.id);compileEv3Path()">
                    			        <b>Compile Ev3 Model</b>
                        			</div>                        
		                        
			                          <div dojoType="dijit.MenuItem" id="CompileTagModelOnServer"
			                            onClick="menuItemClicked(this.id);compileTagModelOnServer()">
			                            <b>Compile Tag Model</b>
			                        </div>
			        
			         		</div>
			         </div>                
                     <div dojoType="dijit.PopupMenuItem" id="toDelPopupMenuItem">
							<span>ToDel</span>
								<div id="toDelMenuColl" dojoType="dijit.Menu">
								</div>
					</div>
						<div dojoType="dijit.PopupMenuItem" id="DrawingPopupMenuItem">
							<span>Shape</span>
								<div id="drawingMenuColl" dojoType="dijit.Menu">
			                        <div dojoType="dijit.MenuItem" id="TriangulateOnServer"
			                            onClick="menuItemClicked(this.id);triangulateOnServer()">
			                            <b>Triangulate</b>
			                        </div>
	                                <div dojoType="dijit.MenuItem" id="ShapeMapperOnServer"
			                            onClick="menuItemClicked(this.id);shapeMapperOnServer()">
			                            <b>ShapeMapper</b>
			                        </div>
			                        <div dojoType="dijit.MenuItem" id="ShapePixalateOnServer"
			                            onClick="menuItemClicked(this.id);shapePixalateOnServer()">
			                            <b>ShapePixalate</b>
			                        </div>
			
			                        <div dojoType="dijit.MenuItem" id="ShapeOffsetOnServer"
			                            onClick="menuItemClicked(this.id);shapeOffsetOnServer()">
			                            <b>ShapeOffset</b>
			                        </div>
			                        <div dojoType="dijit.MenuItem" id="ShapeCavityOnServer"
			                            onClick="menuItemClicked(this.id);shapeCavityOnServer()">
			                            <b>Shape Cavity</b>
			                        </div>
			                        
			  						<div dojoType="dijit.MenuItem" id="shapeOffsetOnServerOutside"
			                            onClick="menuItemClicked(this.id);shapeOffsetOnServerOutside()">
			                            <b>CutOutside</b>
			                        </div>
			                          <div dojoType="dijit.MenuItem" id="shapeOffsetOnServerInside"
			                            onClick="menuItemClicked(this.id);shapeOffsetOnServerInside()">
			                            <b>CutInside</b>
			                        </div>
								</div>
						</div>
						<div dojoType="dijit.PopupMenuItem" id="findOutPopupMenuItem">
							<span>To Fix</span>
								<div id="findOutMenuColl" dojoType="dijit.Menu">
									<div dojoType="dijit.MenuItem" id="compileObjectMappingOnServer"
										onClick="menuItemClicked(this.id);compileObjectMapperOnServer()">
										<b>Compile Object Mapping</b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileSubGraphOnServer"
										onClick="menuItemClicked(this.id);compileSubGraphOnServer()">
										<b>Compile Sub Graph</b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileMappingOnServer4MI"
										onClick="menuItemClicked(this.id);compileMapperOnServer4()">
										<b>Compile Mapping</b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileMappingOnServer5MI"
										onClick="menuItemClicked(this.id);compileMapperOnServer5()">
										<b>Compile And Run Mapping </b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileMappingOnServer6MI"
										onClick="menuItemClicked(this.id);compileMapperOnServer6()">
										<b>Compile Task Plan</b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileMappingOnServerToCodeMI"
										onClick="menuItemClicked(this.id);compileMapperOnServerToCode()">
										<b>Compile To Code </b>
									</div>
			                      <div dojoType="dijit.MenuItem" id="compilePatternsOnServerToCodeMI"
			                            onClick="menuItemClicked(this.id);compilePatternOnServerToCode()">
			                            <b>Compile Patterns</b>
			                        </div>
									<div dojoType="dijit.MenuItem"
										id="compileMappingOnServerToJavaCodeMI"
										onClick="menuItemClicked(this.id);compileMapperOnServerToJavaCode()">
										<b>Compile To Java</b>
									</div>
									<div dojoType="dijit.MenuItem"
										id="compileMappingOnServerToOpenCVCodeMI"
										onClick="menuItemClicked(this.id);compileMapperOnServerToOpenCVCode()">
										<b>Compile OpenCVGraph</b>
									</div>
			                        <div dojoType="dijit.MenuItem"
			                            id="compileMappingOnServerToVizPipesCodeMI"
			                            onClick="menuItemClicked(this.id);compileMapperOnServerToVizPipeCode()">
			                            <b>Compile VizPipeline</b>
			                        </div>
									
									<div dojoType="dijit.MenuItem"
										id="compileMappingOnServerToJavaScriptCodeMI"
										onClick="menuItemClicked(this.id);compileMapperOnServerToJavaScriptCode()">
										<b>Compile To Java Script</b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileCamelPlan"
										onClick="menuItemClicked(this.id);compileCompileCamelPlan()">
										<b>Compile Camel Plan</b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileClassOnServer"
										onClick="menuItemClicked(this.id);compileClassOnServer()">
										<b>Compile Class</b>
									</div>
									<div dojoType="dijit.MenuItem" id="compileSpringConfigOnServer"
										onClick="menuItemClicked(this.id);compileSpringConfigOnServer()">
										<b>Compile Spring Config</b>
									</div>
									<!-- <div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);compileMapperOnServer()">Compile Mappper on server</div> -->
									<div dojoType="dijit.MenuItem" id="compileEventMappingOnServerMI"
										onClick="menuItemClicked(this.id);compileMapperOnServer('EventProcessor',pData.eventtype)">Compile
										Event Mappper on server</div>
									<div dojoType="dijit.MenuItem" id="compileMappingOnServer2MI"
										onClick="menuItemClicked(this.id);compileMapperOnServer2()">Compile Mappper on
										server</div>
									<div dojoType="dijit.MenuItem" id="compileMapperTemplateMI"
										onClick="menuItemClicked(this.id);compileMapperOnServer3()">Compile Mappper Template
										on server</div>
									<div dojoType="dijit.MenuItem" id="compileGraphTemplateMI"
										onClick="menuItemClicked(this.id);CompileGraphTemplate()">Compile Graph Template on
										server</div>
									<div dojoType="dijit.MenuItem" id="compileToDoMappingMI"
										onClick="menuItemClicked(this.id);compileTodoMapperOnServer()">Compile Todo Mappper
										on server</div>
									<div dojoType="dijit.MenuItem" id="runMapperOnServerMI"
										onClick="menuItemClicked(this.id);compileAndRunMapperOnServer()">Run Mappper on
										server</div>
									<div dojoType="dijit.MenuItem" id="runMapperTemplateOnServerMI"
										onClick="menuItemClicked(this.id);compileAndRunMapperOnServerUserTemplate()">Run
										Mappper Template on server</div>
									
									<div dojoType="dijit.PopupMenuItem" id="executePopupMenuItem">
										<span>Execute</span>
										<div id="executeMenuColl" dojoType="dijit.Menu">
											<div dojoType="dijit.MenuItem" id="compileDistGraphMI"
												onClick="menuItemClicked(this.id);compileFastCamelOnServer()">Compile Distributed
												Graph</div>
											<div dojoType="dijit.MenuItem" id="compileFastGraphMI"
												onClick="menuItemClicked(this.id);compileFastLocalOnServer()">Compile Fast Graph</div>
			
										</div>
									</div>
									
									<div dojoType="dijit.PopupMenuItem" id="stateMachinePopupMenuItem">
										<span>State Machine</span>
										<div id="stateMachineMenuColl" dojoType="dijit.Menu">
											<div dojoType="dijit.MenuItem" id="compileDFSMGraphMI"
												onClick="menuItemClicked(this.id);compileDFStateMachineOnServer()">Compile DF
												StateMachine on server</div>
										</div>
									</div>

								</div>
						</div>

						<div dojoType="dijit.MenuItem" id="compileWebUIFlowMI"
							onClick="menuItemClicked(this.id);compileWebUIFlow()">
							<b>Compile Web UI Flow</b>
						</div>
						<div dojoType="dijit.MenuItem" id="RunUICodeFlowMenu"
							onClick="menuItemClicked(this.id);runUICodeFlow()">
							<b>Run UI flow</b>
						</div>
						<div dojoType="dijit.MenuItem" id="runAutomation1"
							onClick="menuItemClicked(this.id);runAutomation1()">
							<b>Run Automation</b>
						</div>
						<div dojoType="dijit.MenuItem" id="runImageTestCase1"
							onClick="menuItemClicked(this.id);runImageTestCase1()">
							<b>Run Image TestCase</b>
						</div>

					</div>
				</div>




				<div dojoType="dijit.PopupMenuItem" id="utilPopupMenuItem">
					<span>Utility</span>
					<div id="utilMenuColl" dojoType="dijit.Menu">
						<div dojoType="dijit.MenuItem" id="addToArbitObjectMap"
							onClick="menuItemClicked(this.id);drawFreeFlow(false,false,100,true,null,'ArbitObject')">Add
							To ArbitObject Mapping</div>
						<div dojoType="dijit.MenuItem" id="getLayout"
							onClick="menuItemClicked(this.id);getLayout()">
							<b>Get Layout</b>
						</div>
						<div dojoType="dijit.MenuItem" id="validateMappingMI"
							onClick="menuItemClicked(this.id);validateMapper()">Validate Mapping</div>
						<div dojoType="dijit.MenuItem" id="groupOnInstanceMI"
							onClick="menuItemClicked(this.id);freeHandRegion(finditemsinregion,groupOnInstance)">Group
							on Instance</div>
						<div dojoType="dijit.MenuItem" id="compileTodoDistances"
							onClick="menuItemClicked(this.id);compileTodoDistances()">Get minimum spanning tree</div>
					</div>
				</div>

			</div>
		</div>
		<%
			String ce = request.getParameter("ce"); 
			       			if(ce!=null&&ce.equalsIgnoreCase("true")){
		%>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Text Editor</span>
			<div dojoType="dijit.Menu" id="CodeEditor">
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);compileCodeEditor()"  id="StartTextEditorMenuId">Text
					Editor</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);stopCodeEditor()" id="StopCodeEditor">Stop
					Code Editor</div>
			</div>
		</div>
		<%
			}
		%>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Design</span>
			<div dojoType="dijit.Menu" id="monitorGraphMenu">
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);openGraphDesignDialog()"  id="openGraphDesignDlgMenuId">Open
					Design</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);clearDesign();clearGraph();"  id="NewGraphDesignDlgMenuId">New
					Design</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);saveDesign()"  id="SaveDesignDlgMenuId">Save
					design</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);applyDesign()" id="ReapplyDesignMenuId">Reapply
					design</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);deleteElementsDiag();" id="DeleteItemMenuId">Delete
					Item</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);updateElementsDiag();" id="UpdateItemMenuId">Update
					Item</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);animElementsDiag();" id="AddAnimationMenuId">Add
					Animation</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);gotoEditor();"  id="RunEditorMenuId">Run
					Editor</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);saveCompStatic();" id="SaveStaticComponentMenuId">Save
					Static Component</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);addCompStatic();"  id="AddStaticComponentMenuId">Add
					Static Component</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);showModNodeDlg();"  id="AddModuleNodeMenuId">Add
					Module Node</div>
				<div dojoType="dijit.MenuItem" onClick="menuItemClicked(this.id);addAllModuleNodes();"  id="AddAllModuleNodeMenuId">Add
					All Module Node</div>
                <div dojoType="dijit.MenuItem" id="DownloadGCodeMenuItem"
                            onClick="downloadGcode();">
                            <b>Download Gcode</b>
                </div>
			</div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
			<span>Recent Actions</span>
			<div dojoType="dijit.Menu" id="RecentActionsMenu"></div>
		</div>
		<div dojoType="dijit.PopupMenuBarItem">
            <span>Plugins</span>
        <div dojoType="dijit.Menu" id="allPluginsMenu">
		<%
        IPluginManager pmgr = CommonUtil.getPluginsManager();
		pmgr.reload();
        for(IPlugin plugin : pmgr.getPlugins()){
        	out.write("<div dojoType=\"dijit.PopupMenuItem\" id=\""+plugin.getId()+"PluginMenu\">");
            out.write("<span>"+plugin.getId()+"</span>");
            out.write("<div dojoType=\"dijit.Menu\" id=\""+plugin.getId()+"PluginContMenu\">");
        	   for(IPluginMenu menu : plugin.getMenus()){
                out.write("<div dojoType=\"dijit.MenuItem\" onClick=\"menuItemClicked(this.id);"+menu.getAction()+";\" ");  
                out.write("id=\""+menu.getId()+"PluginMenuId\">"+menu.getDisplayName()+"</div> ");
        	   }
        	   out.write("</div>");
        	   out.write("</div>");
        }
        %>
        </div>
        </div>
        <div dojoType="dijit.PopupMenuBarItem">
			<span>Shape</span>
			<div dojoType="dijit.Menu" id="shapeMgmtMenu">
				<div dojoType="dijit.MenuItem" onClick="newShape(null);createFacet('top');createCurrFacetSelector('conn1');saveShape();beginShapePoints();"  >New face Shape</div>
				<div dojoType="dijit.MenuItem" onClick="newShape(null)"  >New Shape</div>
				<div dojoType="dijit.MenuItem" onClick="saveShape(null)"  >Save Shape</div>
				<div dojoType="dijit.MenuItem" onClick="selectShape(null)"  >Select Shape</div>
				<div dojoType="dijit.MenuItem" onClick="deleteShape(null)"  >Delete Shape</div>
				<div dojoType="dijit.MenuItem" onClick="createFacet(null)"  >Create facet</div>
				<div dojoType="dijit.MenuItem" onClick="createCurrFacetSelector(null)"  >Create Connector</div>	
				<div dojoType="dijit.MenuItem" onClick="stopShapePoints()"  >Stop Create Points</div>	
				<div dojoType="dijit.MenuItem" onClick="if(ptSelector==true){ptSelector=false;}else {ptSelector=true}"  >Point Selector mode</div>	
				<div dojoType="dijit.MenuItem" onClick="if(pullupmode==true){pullupmode=false;}else {pullupmode=true}"  >PullUp</div>	
				<div dojoType="dijit.MenuItem" onClick="if(clonemode==true){clonemode=false;}else {clonemode=true}"  >Clone Mode</div>
				<div dojoType="dijit.MenuItem" onClick="if(bndryMoveMode==true){bndryMoveMode=false;}else {bndryMoveMode=true}"  >Boundry Move Mode</div>
				<div dojoType="dijit.MenuItem" onClick="if(renderDesignMode==true){renderDesignMode=false;}else {renderDesignMode=true};draw();"  >renderDesignMode</div>	
				<div dojoType="dijit.MenuItem" onClick="if(showPixelVal==true){showPixelVal=false;}else {showPixelVal=true};draw();"  >ShowPixelVal</div>	
				
				
				<div dojoType="dijit.MenuItem" onClick="if(controlMode==true){controlMode=false;}else {controlMode=true};draw();"  >controlMode</div>	
				<div dojoType="dijit.MenuItem" onClick="printAllShapeAngles();"  >Show Angles</div>	
			</div>
		</div>
        <div dojoType="dijit.PopupMenuBarItem">
			<span>Animation</span>
			<div dojoType="dijit.Menu" id="animMgmtMenu">
				<div dojoType="dijit.MenuItem" onClick="var a = prompt('enter name');createAnim(a);draw();"  >Add Animation</div>
				<div dojoType="dijit.MenuItem" onClick="playAnim();"  >play anim</div>
			</div>
		</div>		
	</div>
	<div dojoType="dojo.dnd.Target" jsId="c1" class="dndContainer"
		accept="text,treeNode">
		<div id="graph"></div>
	</div>
	<div id="docnode">
		<div id="docnodeheader">Processor Documentation</div>
		<div id="docval"></div>
		<div dojoType="dojox.layout.ResizeHandle" targetId="docnode"></div>
	</div>
	<div id="cmdnode">
		<div id="cmdnodeheader" style="">Command Window</div>

		<label for="cmdname">cmd:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label> <input
			type="text" style="width: 80%" name="cmdname" id="cmdname"
			required="true" /> <br /> <label for="cmdresult">Result: </label>
		<textarea style="border-color: yellow; width: 80%; height: 70%;"
			name="cmdresult" id="cmdresult"></textarea>

		<br />
		<button dojoType="dijit.form.Button" type="button" onclick="runCmd();">Run</button>
		<button dojoType="dijit.form.Button" type="button"
			onclick="runScript(false);">Run Script</button>
		<button dojoType="dijit.form.Button" type="button"
			onclick="runServerScript();">Run Server Script</button>
		<button dojoType="dijit.form.Button" type="button"
			onclick="runCompile();">Compile/Run</button>
		<button dojoType="dijit.form.Button" type="button"
			onclick="saveCache();">SaveCache</button>
		<button dojoType="dijit.form.Button" type="button"
			onclick="getCache();">GetCache</button>
		<button dojoType="dijit.form.Button" type="button"
			onclick="applyGraph();">LoadGraph</button>
		<div dojoType="dojox.layout.ResizeHandle" targetId="cmdnode"></div>
	</div>
	<div id="histnode">
		<div id="histnodeheader">Historical Window</div>
		<div dojoType="dojox.layout.ResizeHandle" targetId="histnode"></div>
		<div id="histdata"></div>
	</div>
	<div id="propnode">
		<div id="propnodeheader">
			<div id="ndtype">Properties</div>
		</div>
		<div id="jsonval"></div>
		<div dojoType="dojox.layout.ResizeHandle" targetId="propnode"></div>
	</div>
	<div style="height: 0px; width: 0px; overflow: hidden;">
		<img src='/site/wide_5678/images/process.png' id='process.jpg' />
	</div>
	<div style="height: 0px; width: 0px; overflow: hidden;">
		<img src='/site/wide_5678/images/event.png' id='event.jpg' />
	</div>
	<div style="height: 0px; width: 0px; overflow: hidden;">
		<img src='/site/wide_5678/images/router.jpg' id='router.jpg' />
	</div>
	<div style="height: 0px; width: 0px; overflow: hidden;">
		<img src='/site/wide_5678/images/stream.jpg' id='stream.jpg' />
	</div>
	<div style="height: 0px; width: 0px; overflow: hidden;"
		id="dynamicicons"></div>
	<iframe id="estreamer" src=""
		style="width: 0px; height: 0px; border: 0px; display: none; visibility: hidden;"
		frameborder="0"> </iframe>
	<script type="text/javascript">
	
	function handleAnnotDrop(f, a,compname,isui,mx,my,configobj) {
		if(a!=null){
			console.log("Dropped annotation: "+compname +" on "+ a.id);
			if(a.tags==null){
				a.tags = new Array();
			}
			a.tags.push(compname);
		}else
			console.log("Dropped annotation: "+compname +" on "+ " Console.");
			
	}
	comps["handleAnnotDrop"] =handleAnnotDrop;
	comps["handleDesignDrop"] =handleDesignDrop;
	comps["handleTaskDrop"] =handleTaskDrop;
	comps["handleGraphDrop"] =handleGraphDrop;
	<%for(Group grp:groupMenuList){
		out.println("function handle"+grp.getId()+"Drop(f, a,compname,isui,mx,my,configobj) {");
		out.println("console.log('"+grp.getId()+"'+compname)");
		out.println("handleDefCompDrop(f, a,compname,isui,mx,my,configobj);");
		out.println("}");
		out.println("comps['handle"+grp.getId()+"Drop'] =handle"+grp.getId()+"Drop;");
		
   }%>
	
	comps["ConvertToInputPort"] =dummy;
	comps["ConvertToOutputPort"] =dummy;
	comps["ConvertToAuxPort"] =dummy;
	configs["ConvertToInputPort"] = ConvertToInputPort;
	configs["ConvertToOutputPort"] = ConvertToOutputPort;
	configs["ConvertToAuxPort"] = ConvertToAuxPort;
	function handleDefCompDrop(f, a,compname,isui,mx,my,configobj){
		if(configs[compname]!=null){
			configs[compname](f, a,compname,isui,mx,my,configobj);
			}else{
				console.log(compname+" has not way of handling..");
			}
	}
	function ConvertToInputPort(f, a,compname,isui,mx,my,configobj) {
		if(a!=null){
			var portid = "port_"+a.id+"_"+getUniqId();
		var p = createPortObj(a.normalizedx-100,a.normalizedy-100,portid,portid,"input",null,null);
		addObjectToGraph(p);
		configPort(mx, my, 8, 8, "orange", "orange",p,null);
		var c = createConnObject(p.id,a.id,getUniqId());
		addObjectToGraph(c);
		connectLinks(c);
		}
	}
	function ConvertToOutputPort(f, a,compname,isui,mx,my,configobj) {
		if(a!=null){
			var portid = "port_"+a.id+"_"+getUniqId();
		var p = createPortObj(a.normalizedx+100,a.normalizedy-100,portid,portid,"output",null,null);
		addObjectToGraph(p);
		configPort(mx, my, 8, 8, "orange", "orange",p,null);
		var c = createConnObject(p.id,a.id,getUniqId());
		addObjectToGraph(c);
		connectLinks(c);
		}
	}
	function ConvertToAuxPort(f, a,compname,isui,mx,my,configobj) {
		if(a!=null){
			var portid = "port_"+a.id+"_"+getUniqId();
		var p = createPortObj(a.normalizedx,a.normalizedy+200,portid,portid,"aux",null,null);
		addObjectToGraph(p);
		configPort(mx, my, 8, 8, "orange", "orange",p,null);
		var c = createConnObject(p.id,a.id,getUniqId());
		addObjectToGraph(c);
		connectLinks(c);
		}


	}
	function handleGraphDrop(f, a,compname,isui,mx,my,configobj) {
		getGraphFromServerByName(compname)
	}
	function handleDesignDrop(f, a,compname,isui,mx,my,configobj) {
		if(a!=null){
			console.log("Dropped Design: "+compname +" on "+ a.id);
			getGraphDesignByName(compname);
		}else{
			getGraphDesignByName(compname);
			console.log("Dropped annotation: "+compname +" on "+ " Console.");
		}			
	}
	function handleTaskDrop(f, a,compname,isui,mx,my,configobj) {
		console.log("Dropped task " + compname);
			var ts = taskSpecs[compname];
			if(ts!=null &&ts.custom==false){
				var name = prompt("Please Enter Task Name: ");
				 if(a!=null && a.type!=null && a.type=="connection"){
				    	var item = createAnonDefObj(mx,my,100,55,name,ts.inputs,ts.outputs,ts.aux,ts.anonType);
						pData.data.push(item);
						console.log(item);
				        drawAnonDef(mx,my,200,80, "orange","orange",item,null);

					 var origto = a.to;
				        a.to=item.id;
				        a.nodes[1]=item.id
				        var drawing= findDrawEleByIdEx(a.id);
				        if(drawing.item!=null)
				        drawing.item.remove();
				        if(drawing.textnode!=null)
				        drawing.textnode.remove();
				        for(var i=0;i<drawing.subs.length;i++){
				            drawing.subs[i].remove();
				        }
				        
				        createConnItem(item.id,origto,getUniqId());
				        createConnItem(a.from,item.id,getUniqId());
			      }else if(a!=null && a.type!=null && a.type=="AnonDef"){
					var xx = null;
					var yy = null;
					if(a.x<mx){
						xx = a.x+a.r+50;
					}else{
						xx = a.x-a.r-50;
					}
					/* if(a.y<my){
						yy = a.y+a.b+50;
					}else{
						yy = a.y-a.b-50;
					} 
					if((a.x+a.r/2)-mx<5)
						*/
					{
						yy = a.y;	
					}
					
			    	var item = createAnonDefObj(xx,yy,100,55,name,ts.inputs,ts.outputs,ts.aux,ts.anonType);
					pData.data.push(item);
					console.log(item);
					drawAnonDef(item.x,item.y,200,80, "orange","orange",item,null);
					createConnItem(a.id,item.id,getUniqId());
				}else{
			    	var item = createAnonDefObj(mx,my,100,55,name,ts.inputs,ts.outputs,ts.aux,ts.anonType);
					pData.data.push(item);
					console.log(item);
					drawAnonDef(mx,my,200,80, "orange","orange",item,null);
				}
			}else{
				if(configs[compname]!=null){
				configs[compname](f, a,compname,isui,mx,my,configobj);
				}else{
					console.log(compname+" has not way of handling..");
				}
			}
	}
	function addMTaskToPage(f, a,compname,isui,mx,my,configobj) {
		compname = compname.toString();
		var name = compname.substr(6,compname.length-1);
		console.log("Dropped mapper task " + name);
		if(configs[name]!=null){
			configs[name](f, a,name,isui,mx,my,configobj);
		}
	}
	function addWebUITaskToPage(f, a,compname,isui,mx,my,configobj) {
		compname = compname.toString();
		var name = compname.substr(11,compname.length-1);
		console.log("Dropped mapper task " + name);
		if(configs[name]!=null){
			configs[name](f, a,name,isui,mx,my,configobj);
		}
	}
	
      <%for(String s: mapperTaskList){
      	  out.write("configs[\"MTask:"+s+"\"] = addMTaskToPage;\n");
      	  out.write("comps[\"MTask:"+s+"\"] = dummy;\n");
      }%>
      <%for (String s : webUITaskList) {
				out.write("configs[\"WebUITask:" + s + "\"] = addWebUITaskToPage;\n");
				out.write("comps[\"WebUITask:" + s + "\"] = dummy;\n");
			}%>
      
      
     </script>
     </div>
<div id="footerbar">
<table><tr>
<td id="messages">
 <select id="messageBox" name="messageBox" >
    <option>...</option>
 </select>
</td>
<td id="bottomCanvas"><div id="bottomCanvasCont"></div></td>
<td id="rightbottombar" style="color:gray"></td>
<td id="rightbottombar2" style="color:gray">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td id="graphNameTd" style="color:orange">Name</td>
</tr></table>

<script type="text/javascript">
function addMessageToBox(msgstr){
    var sel = dojo.byId("messageBox");
    var op = dojo.create("option",{innerHTML:msgstr,"class": "greenText"},sel,"first");
    sel.value=msgstr;
    setCssPropertt(sel,"color","#00FF33");
    
}

function addWarningToBox(msgstr){
    var sel = dojo.byId("messageBox");
    var op=    dojo.create("option",{innerHTML:msgstr,"class": "orangeText"  },sel,"first");
    sel.value=msgstr;
    setCssPropertt(sel,"color","orange");
}
function addErrorToBox(msgstr){
	var sel = dojo.byId("messageBox");
   var op = dojo.create("option",{innerHTML:msgstr, "class": "redText" },sel,"first");
   sel.value=msgstr;
   setCssPropertt(sel,"color","red");
}

function addInfoToBox(msgstr){
    var sel = dojo.byId("messageBox");
   var op = dojo.create("option",{innerHTML:msgstr, "class": "greenText" },sel,"first");
   sel.value=msgstr;
   setCssPropertt(sel,"color","black");
   setCssPropertt(sel,"background-color","orange");
   setTimeout(function (){
	   setCssPropertt(sel,"color","green");
	   setCssPropertt(sel,"background-color","black");
   },2000);
}

var footerCanvas = Raphael("bottomCanvasCont", 80, 30);

</script>
</div>
<script src="/site/d3js/bullet.js" type="text/javascript" charset="utf-8"></script>
        <%
        for(IPlugin plugin : pmgr.getPlugins()){
        	for(String s : plugin.getIncludes()){
        	out.write("<script src=\"\\site\\plugins\\"+plugin.getId()+"\\"+s+"\" type=\"text/javascript\" charset=\"utf-8\"></script>");
        	}
         }
         %>
         <script type="text/javascript">
         setTimeout(function (){
         <%
         List<String> recentMenuLst = DBHelper.getInstance().getStringList("select distinct(menu) from recent_menu limit 20;");
         for(String recentMenu : recentMenuLst){
         out.write("menuItemClickedInternal('"+recentMenu+"');");
         }
         
        %>
        },4000);
        </script>
<script src="/site/threejs/three.js"></script>        
</body>
</html>

