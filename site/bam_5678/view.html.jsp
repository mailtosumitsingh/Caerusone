<!--
/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html;  charset=windows-1250">
<meta name="apple-mobile-web-app-capable" content="yes">
<title>Viewer</title>
<script type="text/javascript" src="/site/bam_5678/resources/js/raphael-min.js"></script>
<script type="text/javascript" src="/site/bam_5678/resources/viewjs/graph2.js"></script>
<script type="text/javascript" src="/site/bam_5678/resources/viewjs/menu.js"></script>
<script type="text/javascript" src="/site/bam_5678/resources/viewjs/comps.js"></script>
<script type="text/javascript" src="/site/bam_5678/resources/viewjs/modules/custommodules.js"></script>

<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
<script type="text/javascript" src="/site/dojo/dojo/dojo.js" djConfig="parseOnLoad: true"></script>
 <style type="text/css">
            @import "/site/dojo/dojox/layout/resources/ResizeHandle.css";
			@import "/site/dojo/dijit/themes/tundra/tundra.css";
			@import "/site/dojo/dojo/resources/dojo.css"
			@import "/site/dojo/dojo/resources/dnd.css";
	  	    @import "/site/dojo/dojox/grid/enhanced/resources/tundraEnhancedGrid.css";
	  	    @import "/site/dojo/dojox/grid/enhanced/resources/EnhancedGrid_rtl.css";

#propnode {
	background: #fff;
	border: 3px solid silver;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 1000px;
	top: 100px;
	filter: alpha(opacity = 80);
	opacity: 0.8;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
}
#chartnode1 {
	background: #fff;
	border: 3px solid silver;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 600px;
	top: 100px;
	filter: alpha(opacity = 80);
	opacity: 0.8;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
}
#chartnode2 {
	background: #fff;
	border: 3px solid silver;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 100px;
	filter: alpha(opacity = 80);
	opacity: 0.8;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
}
#chartnode3 {
	background: #fff;
	border: 3px solid silver;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity = 80);
	opacity: 0.8;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
}
#chartnode3 {
	background: #fff;
	border: 3px solid silver;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 600px;
	filter: alpha(opacity = 80);
	opacity: 0.8;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
}
#ndtype,.heading {
   height:30px
	width: 100%;
	border: 1px solid #c0c0c0;
	border-bottom: 1px solid #9b9b9b;
	background: #fff
		url(http://ajax.googleapis.com/ajax/libs/dojo/1.4.1/dijit/themes/tundra/images/buttonEnabled.png)
		repeat-x bottom left;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
	 text-align:center;
}
#chart1,#chart2,#chart3 {
	width: 100%;
	height:100%;
	border: 1px solid #c0c0c0;
	border-bottom: 1px solid #9b9b9b;
	background: #fff
		url(http://ajax.googleapis.com/ajax/libs/dojo/1.4.1/dijit/themes/tundra/images/buttonEnabled.png)
		repeat-x bottom left;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
	 text-align:center;
}
#gmapsnode {
	background: #fff;
	border: 3px solid silver;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity = 80);
	opacity: .9;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);
}
.movablenode {
	background: #fff;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity = 80);
	opacity: .9;
}
.movablemodulenode {
	background: #fff;
	border: 1px solid silver;
	overflow: hide;
	position: absolute;
	width: 200px;
	height: 300px;
	left: 800px;
	top: 400px;
	filter: alpha(opacity = 80);
	opacity: .9;
	-moz-border-radius:10px;
	 -webkit-border-radius:10px;
	 behavior:url(border-radius.htc);

}

</style>
<script type="text/javascript">
		var urlMap = {};
		urlMap.Test="/GetHistoricalGraph?graphid=";
		
		function getURL(str){
			return urlMap[str];
		}
var currentGraph=null;
<%
		String graph= request.getParameter("page") ;
		if(graph==null|| graph.length()<=0) {
		out.print("currentGraph=\"MyDesign1\";");
		}
		else {
			out.print( "currentGraph=\""+graph+"\";");
		}
		%>	
dojo.require("dojox.layout.ResizeHandle");
dojo.require("dijit.MenuBar");
dojo.require("dijit.MenuBarItem");
dojo.require("dijit.PopupMenuBarItem");
dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");
dojo.require("dijit.PopupMenuItem");
dojo.require("dijit.Dialog");
dojo.require("dijit.form.Button");
    dojo.require("dijit.Menu");
    dojo.require("dojo.parser");
dojo.require("dojox.collections.ArrayList");
dojo.require("dojo.dnd.Mover");
dojo.require("dojo.dnd.Moveable");
dojo.require("dojo.dnd.move");
dojo.require("dojo.dnd.Container");
dojo.require("dojo.dnd.Manager");
dojo.require("dojo.dnd.Source");
dojo.require("dojox.charting.Chart2D");
dojo.require("dojox.grid.EnhancedGrid");
dojo.require("dojox.data.CsvStore");
dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dojox.grid.enhanced.plugins.DnD");
dojo.require("dojox.grid.enhanced.plugins.Menu");
dojo.require("dojox.grid.enhanced.plugins.NestedSorting");
dojo.require("dojox.grid.enhanced.plugins.IndirectSelection");
dojo.require("dojox.av.FLAudio");
var mySound;
  dojo.addOnLoad(documentReady);
  function documentReady(){
	  //getModulesFor("bam",prepareGraph);
	  mySound = new dojox.av.FLAudio({
        initialVolume: .5,
        autoPlay: false,
        isDebug: false,
        statusInterval: 500
    });
	  comps_graph_init();
	  prepareGraph();
  }
  dojo.subscribe("/dojo/resize/stop", function(inst){
		var comp = findComponentByName(inst.targetDomNode.id);
		if(comp!=null){
			comp.width = inst.targetDomNode.style.width;
			comp.height = inst.targetDomNode.style.height;
		}

		   var a=maps[inst.targetDomNode.id];
		   if(a!=null){
			   google.maps.event.trigger(a, "resize");
		   }
		  
		   var a=globals[inst.targetDomNode.id];
		   if(a!=null){
			   a.resize(inst.targetDomNode.offsetWidth-20,inst.targetDomNode.offsetHeight-20);
			   }
		   
		   var a=grids[inst.targetDomNode.id];
		   if(a!=null){
			   a.resize();
			   a.update();
			   }
	});


		</script>

</head>
<body class="tundra body">
<div id="graph">
</div>
<div id="dynamicnodes"></div>
<div style="height: 0px; width: 0px; overflow: hidden;"><img
	src='/bam_5678/images/process.png' id='process.jpg' /></div>
<div style="height: 0px; width: 0px; overflow: hidden;"><img
	src='/bam_5678/images/event.png' id='event.jpg' /></div>
<div style="height: 0px; width: 0px; overflow: hidden;"><img
	src='/bam_5678/images/router.jpg' id='router.jpg' /></div>
<div style="height: 0px; width: 0px; overflow: hidden;"><img
	src='/bam_5678/images/stream.jpg' id='stream.jpg' /></div>

</body>
</html>
