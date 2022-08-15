class Ev3Action{
    constructor(x,y,x2,y2,lbl,type){
    	this.x  = x;
		this.y  = y;
        this.x2 = x2;
        this.y2 = y2;
        this.label=""+lbl;
        this.tagtype=type;
        this.group = this.label;
    }
	draw(){
	    var node= createEv3Node(this.x,this.y,this.x2,this.y2,this.label,this.label,this.tagtype,this.group); 
	    addObjectToGraph(node);
		var d = pCanvas.line(this.x,this.y,this.x2,this.y2);
		d.toFront();
		d.node.setAttribute("eleid", node.id);
		var dnode = {
				"item" : d,
				"id" : node.id
			};
		drawElements.push(dnode);
	}
}

function createEv3Node(x,y,w,h,id,name,tagtype,group,path){
    var newNode = {};
	newNode["type"]="ImageToTag";
	newNode["id"]="Ev3Node_"+group+"_"+id;
	newNode["x"]=""+x;
	newNode["normalizedx"]=""+x;
	newNode["y"]=""+y;
	newNode["normalizedy"]=""+y;
	newNode["r"]=""+w;
	newNode["b"]=""+h;
	newNode["name"] = ""+name;
	newNode["tagtype"] = ""+tagtype;
	newNode["group"] = ""+group;
	newNode["label"] = ""+name;
    return newNode
}

genericnodes["Ev3Action"] = "/images/null.png";	
nodeDrawingHandler["Ev3Action"]=drawEv3Element;

function drawEv3Element(xpos, ypos, width, height, fillColor, borderColor,node,imgsrc){
	var d = pCanvas.line(node.x,node.y,node.y,node.r,node.b);
	d.toFront();
	d.node.setAttribute("eleid", node.id);
	var dnode = {
			"item" : d,
			"id" : node.id
		};
	drawElements.push(dnode);

}