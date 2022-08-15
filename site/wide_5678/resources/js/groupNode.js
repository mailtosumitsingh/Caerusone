function drawGroupNode(xpos, ypos, width, height, fillColor, borderColor,obj,imgsrc){
	{
		var id = obj.id;
		var ret = null;
		if(obj.textual==null||obj.textual==false){
		 if(obj.title!=null){
			 ret = groupRect(xpos, ypos, width, height, fillColor, borderColor,obj.id,obj.title);
		 }else{
	     ret = groupRect(xpos, ypos, width, height, fillColor, borderColor,id);
		 }
		 ret.id=id;
	    var sh = findNodeById(id);
	    if(sh!=null){
	    	if(obj.icon!=null)
	    		imgsrc = obj.icon;
	    var mg = pCanvas.image(imgsrc, xpos-width/2, ypos-height/2, width, height);
	    	mg.toBack();
	    	ret.img= mg;
	    }        
	    drawElements.push(ret);
		}else{
			ret = drawGenericTextualNode(xpos, ypos,obj,imgsrc);
			ret.id=id;
			drawElements.push(ret);
		}
	}
}
function groupRect(xpos, ypos, width, height, fillColor, borderColor,id,title){
    var x1 = xpos - width/2;
    var y1 = ypos - height/2;
   var shape= pCanvas.rect(x1,y1,width,height,4);
   // var color = Raphael.getColor();
     shape.attr({"fill": fillColor, "stroke": borderColor,"opacity":.1, "fill-opacity": 0, "stroke-width": 2, cursor: "move"});
        shape.node.setAttribute("eleid",id);
        shape.drag(groupmove, groupDragger, groupup);
        var txt = null;
        if(title!=null){
        	txt = drawText(x1,y1,width, height,title);
        }else{
        	txt = drawText(x1,y1,width, height,id);
        }
        txt.node.setAttribute("eleid","nodetxt_"+id);
        var ret = {"item":shape,"textnode":txt,"subs":new Array()};
        return ret;
}
var groupDragger = function () {
    this.ox = this.type == "circle" ? this.attr("cx") : this.attr("x");
    this.oy = this.type == "circle" ? this.attr("cy") : this.attr("y");
    this.animate({"fill-opacity": .2}, 500);
    if(lastDragged!=null){
    	lastDragged.remove();
    	lastDragged = null;
    }
    var myid  = this.node.getAttribute("eleid");
    if(myid!=null){
    var selectedNode = findNodeById(myid);
    console.log(selectedNode);
    if(selectedNode.type!="Port"){
        lastDragged = this.clone();
        lastDragged.toBack();
        this.attr({"stroke-dasharray":"."});
    } 
    }
    },
    groupmove = function (dx, dy) {
        var att = this.type == "circle" ?{cx: this.ox + dx, cy: this.oy + dy}: {x: this.ox + dx, y: this.oy + dy}  ;
        	// this.attr(att);
            var myid  = this.node.getAttribute("eleid");
            if(myid!=null){
            var selectedNode = findNodeById(myid);
            selectedNode.normalizedx = (mouseX);
            selectedNode.normalizedy = (mouseY);
            selectedNode.x = (mouseX );
            selectedNode.y = (mouseY );
            var nditem  = findDrawEleById(myid);
            if(nditem!=null){
            var ndtext= nditem.textnode;
            var imgnode= nditem.img;
            var item = nditem.item;
            var xpos=selectedNode.normalizedx;
            var ypos = selectedNode.normalizedy;  
            if(ndtext!=null){
            ndtext.attr({"x":(xpos-selectedNode.r/2),"y":(ypos - selectedNode.b/2)});
            }
            if(imgnode!=null){
            var ix = xpos - selectedNode.r/2;
            var iy = ypos - selectedNode.b/2;
            imgnode.attr({"x":ix,"y":(iy)});
            }
            if(item!=null){
            	if(this.type=="text"){
                    item.attr({"x":(xpos),"y":(ypos )});
            	}else if(this.type=="circle"){
            		item.attr({"cx":(xpos-selectedNode.r/2),"cy":(ypos - selectedNode.b/2)});
            	}else{
                    item.attr({"x":(xpos-selectedNode.r/2),"y":(ypos - selectedNode.b/2)});
            		// item.attr({"x":(xpos),"y":(ypos)});
            	}
                }
            if(selectedNode.type=="functionobj"){
            	realignFunctionObj(selectedNode);
            }else if(selectedNode.type=="sqlobj"){
            	realignSQLObj(selectedNode);
            }
            }
            }
        pCanvas.safari();
    },
    groupup = function () {
    	  if(lastDragged!=null){
          	lastDragged.remove();
          	lastDragged = null;
          }
        this.animate({"fill-opacity": 0}, 500);
        var myid  = this.node.getAttribute("eleid");
        if(myid!=null){
        var obj = findNodeById(myid);
        if(obj!=null){
        if(obj.type=="region"){
        	lastSelectedRegion = obj;
        }else if(obj.type=="connection"){
        	lastSelectedConn = obj;
        }else{
        	lastSelectedNode = obj;
        }
        
        displaySelectedNodeProps(obj);
        
        if(obj["type"]=="node"){
            getProcDoc(obj.clz);
        }else if(obj["type"]=="group"){
        	// removed the code to rearrange will do on server side
        }else if(obj["type"]=="connection"){
        	var d = dojo.byId('docval');
            d.innerHTML = "<b>"+obj.connCond+"</b>";
        }else{
             var d = dojo.byId('docval');
             d.innerHTML = "<b>"+""+"</b>";
        }
        }
      	var myid  = this.node.getAttribute("eleid");
    	var evtdata = {};
    	evtdata.id = myid;
    	evtdata.type="up";
    	sendEvent(geq, [evtdata]) ;
    	if(Math.abs(this.ox-this.attr("x"))>5||Math.abs(this.oy-this.attr("y"))>5){
    		draw();
    	}else{
    		this.animate({"fill-opacity": 1}, 500);
    		this.click(null);
    	}
        }else{
    		this.animate({"fill-opacity": 1}, 500);
    		this.click(null);
    	}
    };
    var txtdragger = function () {
        this.animate({"fill-opacity": .2}, 500);
    },
    txtmove = function (dx, dy) {
            var att = {x: mouseX, y: mouseY} 
            this.attr(att);
            pCanvas.safari();
        },
        txtup = function () {
            this.animate({"fill-opacity":1}, 500);
            var myid  = this.node.getAttribute("eleid");
            var obj = findNodeById(myid)
            if(obj!=null){
    		obj.normalizedx = mouseX;
    		obj.normalizedy = mouseY;
            }
            var jtextarea =dojo.byId("jsonval");
            jtextarea.innerHTML = this.attr("text")
            lastSelectedNote = myid;
            pCanvas.safari();
            draw();
        };