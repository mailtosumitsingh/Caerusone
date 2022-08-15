<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="org.ptg.plugins.IPluginMenu"%>
<%@page import="org.ptg.plugins.IPlugin"%>
<%@page import="org.ptg.plugins.IPluginManager"%>
<%@ page import="org.ptg.util.*,org.ptg.util.db.*,java.util.*,org.ptg.http2.handlers.*,org.ptg.util.TaskSpec,java.util.*	,org.ptg.http2.handlers.GetMenuGroup"%>
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
<meta http-equiv="Content-Type" content="text/html;  charset=windows-1250">
<meta name="apple-mobile-web-app-capable" content="yes">
<title>WIDE - Web IDE</title>
<script src="/site/d3js/d3.v3.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/d3.csv.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/contour.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/hexbin.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/sankey.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/d3box.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/gauge.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/d3js/bullet.js" type="text/javascript" charset="utf-8"></script>
<script src="/site/threejs/threejs.js"></script>

<script type="text/javascript" src="/site/dojo/dojo/dojo.js" djConfig="parseOnLoad: true"></script>
<script type="text/javascript" src="/site/wide_5678/resources/js/raphael-min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript" src="/site/wide_5678/resources/js/graph2.js"></script>
<!-- <script type="text/javascript" src="/site/wide_5678/resources/js/menu.js"></script>
<script type="text/javascript" src="/site/wide_5678/resources/js/plugin.js"></script>
 -->
<style type="text/css">
path.chord {
	fill-opacity: .67;
}
.link {
  fill: none;
  stroke: #000;
  stroke-opacity: .2;
}


#chartnode1 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden; /* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 600px;
	top: 100px;
	filter: alpha(opacity =             80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#chartnode2 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden; /* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 100px;
	filter: alpha(opacity =             80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#chartnode3 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden; /* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =             80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

#chartnode3 {
	background: #fff;
	border: 3px solid silver;
	overflow: hidden; /* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 600px;
	filter: alpha(opacity =             80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

.fixmoduleheading {
	height: 20px;
	width: 100%;
	background: orange;
	text-align: center;
	background: #c5deea; /* Old browsers */
	background: -moz-linear-gradient(left, #969696 0%, #9696f0 31%, #9696f0 100%);
	/* FF3.6+ */
	background: -webkit-gradient(linear, left top, right top, color-stop(0%, #969696),
		color-stop(31%, #9696f0), color-stop(100%, #9696f0));
	/* Chrome,Safari4+ */
	background: -webkit-linear-gradient(left, #969696 0%, #9696f0 31%, #9696f0 100%);
	/* Chrome10+,Safari5.1+ */
	background: -o-linear-gradient(left, #969696 0%, #9696f0 31%, #9696f0 100%);
	/* Opera 11.10+ */
	background: -ms-linear-gradient(left, #969696 0%, #9696f0 31%, #9696f0 100%);
	/* IE10+ */
	background: linear-gradient(to right, #969696 0%, #9696f0 31%, #9696f0 100%);
	/* W3C */
	filter: progid:DXImageTransform.Microsoft.gradient(     startColorstr='#969696',
		endColorstr='#9696f0', GradientType=1); /* IE6-9 */
	border-top-left-radius: 10px;
	border-top-right-radius: 10px;
	-webkit-border-top-left-radius: 10px;
	-webkit-border-top-right-radius: 10px;
}
 
.anondefheading,#ndtype,.heading,#ndtype2 {
	height: 20px;
	width: 100%;
	background: orange;
	text-align: center;
	background: #c5deea; /* Old browsers */
	background: -moz-linear-gradient(left, #c5deea 0%, #8abbd7 31%, #066dab 100%);
	/* FF3.6+ */
	background: -webkit-gradient(linear, left top, right top, color-stop(0%, #c5deea),
		color-stop(31%, #8abbd7), color-stop(100%, #066dab));
	/* Chrome,Safari4+ */
	background: -webkit-linear-gradient(left, #c5deea 0%, #8abbd7 31%, #066dab 100%);
	/* Chrome10+,Safari5.1+ */
	background: -o-linear-gradient(left, #c5deea 0%, #8abbd7 31%, #066dab 100%);
	/* Opera 11.10+ */
	background: -ms-linear-gradient(left, #c5deea 0%, #8abbd7 31%, #066dab 100%);
	/* IE10+ */
	background: linear-gradient(to right, #c5deea 0%, #8abbd7 31%, #066dab 100%);
	/* W3C */
	filter: progid:DXImageTransform.Microsoft.gradient(     startColorstr='#c5deea',
		endColorstr='#066dab', GradientType=1); /* IE6-9 */
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
	overflow: hidden; /* hide; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =             80);
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
	overflow: hidden; /* auto; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =             80);
	opacity: 0.8;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}

.anondefstaticnode,.staticnode,.movablenode {
	background: #fff;
	overflow: hidden; /* auto; */
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity =             80);
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
	filter: alpha(opacity =             100);
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
	filter: alpha(opacity =             90);
	opacity: 0.9;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	behavior: url(border-radius.htc);
}
.buttonOn {background-color: #4CAF50;} /* Green */
.buttonOff {background-color: #f44336;} /* Blue */


</style>
</head>
<%
	String s = DBHelper.getInstance().getString("select config from pageconfig where name='" + request.getParameter("graphid") + "'");
	String comp = request.getParameter("compId")==null?"#@#@#@#@#":request.getParameter("compId");
	String singleDraw = request.getParameter("sd");
	String bodyStyle   = "" ;
	s = s.replace("NaN", "\"0\"");
	s = s.replace("Infinity", "\"0\"");
	s = s.replace("undefined", "\"\"");
	JSONArray ja = JSONArray.fromObject(s);
	for (int i = 0; (i < ja.size() ); i++) {
		JSONObject item = (JSONObject) ja.get(i);
		String id = ("" + item.get("id"));
		String type = "" + item.get("name");
		if (id == null)
			continue;
		if (!id.equals(comp))
				continue;
		if (type.equals("StaticModule") || type.equals("CanvasModule")) {
			String x = item.getString("left");
			String y = item.getString("top");
			String r = item.getString("width");
			String b = item.getString("height");
			String rotation = "0";
			if (item.has("rotation"))
				rotation = item.getString("rotation");
			String stylestr = "width: " + r + "; height: " + b + ";";
			bodyStyle=(  stylestr );
		}

	}
	
%>
<body class="tundra body" id="bodyelem" style="background-color: white;<%=bodyStyle%>">
	   <div id="graph"></div>
	<script>
		var canvasModules = {};
		
		setupRaphael();
		var screenWidth = 3000;
		var screenHeight = 3000;
		var pCanvas = Raphael("graph", screenWidth, screenHeight);
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
		urlMap.CompileCamelPlan="/CompileCamelPlan";
		urlMap.GetLayout="/GetLayout";
		urlMap.CompileSubGraph="/CompileSubGraph";
		urlMap.CompileObjectMapping="/site/CompileObjectMapping";
		urlMap.ProcessingPlan = "/ProcessingPlan";
		urlMap.CompileTaskPlanV2="/CompileTaskPlanV2";
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

		
		function getURL(str){
			return urlMap[str];
		}
		function doGetHtmlSync(url) {
			var ret = null;
			dojo.xhrGet({
				sync : true,
				url : url,
				load : function(data) {
					ret = data;
				}
			});
			return ret;
		}
		function doGetHtmlSyncWithContent(url, content) {
			var ret = null;
			dojo.xhrGet({
				sync : true,
				url : url,
				content : content,
				load : function(data) {
					console.log("sync get:" + data);
					ret = data;
				}
			});
			return ret;
		}
		function doGetHtml(url, fun1) {
			dojo.xhrGet({
				url : url,
				load : function(response, ioArgs) {
					if (fun1 != null) {
						fun1(response);
					}
					return response;
				},
				error : function(response, ioArgs) {
					if (fun1 != null) {
						fun1(response);
					}
					return response;
				}
			});
		}
		function doPostHtml(url, cnt, fun1) {
			dojo.xhrPost({
				url : url,
				content : {
					"tosave" : cnt
				},
				load : function(response, ioArgs) {
					alert(response);
					return response;
				},
				error : function(response, ioArgs) {
					var a = dojo.fromJson(response);
					alert("Failed to Save" + a.result);
					return response;
				}
			});
		}

	</script>
	<%
		for (int i = 0; (i < ja.size()); i++) {
			JSONObject item = (JSONObject) ja.get(i);
			String id = ("" + item.get("id"));
			String type = "" + item.get("name");
			if(id==null)continue;
			if(singleDraw!=null && !id.equals(comp))continue;
			if (type.equals("StaticModule") || type.equals("CanvasModule")) {
				String x = item.getString("left");
				String y = item.getString("top");
				String r = item.getString("width");
				String b = item.getString("height");
				String rotation = "0";
				if (item.has("rotation"))
					rotation = item.getString("rotation");
				String stylestr = "";
				if(id.equals(comp)){
					stylestr = "width: " + r+ "; height: " + b + "; z-index: 10;  position: absolute; left: " + 0 + "; top: " + 0+ ";overflow:hidden;";
				}else{
					stylestr = "width: " + r + "; height: " + b + "; z-index: 10; -webkit-transform: rotate(" + rotation + "); position: absolute; left: " + x + "; top: " + y
						+ ";position:absolute;overflow:hidden;";
				}
				out.write("<div id=" + id + " style='" + stylestr + "'>");
				if (item.has("cntn")) {
					String cntn = "" + item.get("cntn");
					out.write(cntn);
				}
				if (type.equals("CanvasModule")) {
					String w = r.substring(0, r.length() - 2);
					String h = r.substring(0, b.length() - 2);
					out.write("<script> var canvas = Raphael('canvas_" + id + "'," + w + "," + h + " );");
					out.write("canvas.rect(0,0," + w + "," + h + ",4);");
					out.write("canvasModules['" + id + "'] = canvas;");
					out.write("</script>");
				}
				out.write("</div>\n");
				out.write("<br/>");
			}
		}
		for (int i = 0;(i < ja.size()); i++) {
			JSONObject item = (JSONObject) ja.get(i);
			String id = ("" + item.get("id"));
			String type = "" + item.get("name");
			if(id==null)continue;
			//if(singleDraw!=null && !id.equals(comp))continue;
			if (type.equals("Graphics")) {
				String x = item.getString("left");
				String y = item.getString("top");
				String r = item.getString("width");
				String b = item.getString("height");
				String rotation = "0";
				if (item.has("rotation"))
					rotation = item.getString("rotation");
				String stylestr = "";
				if(id.equals(comp)){
                    stylestr = "width: " + r+ "; height: " + b + "; z-index: 10;  position: absolute; left: " + 0 + "; top: " + 0+ ";";
                }else{
                    stylestr = "width: " + r + "; height: " + b + "; z-index: 10; -webkit-transform: rotate(" + rotation + "); position: absolute; left: " + x + "; top: " + y
                        + ";position:absolute;";
                }				out.write("<div id=" + id + " style='" + stylestr + "'>");
				if (type.equals("Graphics")) {
					if (item.has("txt")) {
						out.write("<script>");
						String cntn = "" + item.get("txt");
						out.write(cntn);
						out.write("</script>");
					}

				}
				out.write("</div>\n");
				out.write("<br/>");
			}
			if (type.equals("Shape")) {
			 out.write("<script>");
			 out.write(item.getString("design"));
			 out.write("</script>");
			}
		}
	%>

</body>
</html>

