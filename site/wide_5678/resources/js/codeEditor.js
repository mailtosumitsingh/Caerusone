var wordGap = 30;
var lineGap = 20;	
var jkeyWords = {};
jkeyWords["abstract"] = "abstract";
jkeyWords["continue"] = "continue";
jkeyWords["for"] = "for";
jkeyWords["new"] = "new";
jkeyWords["switch"] = "switch";
jkeyWords["assert"] = "assert";
jkeyWords["default"] = "default";
jkeyWords["goto"] = "goto";
jkeyWords["package"] = "package";
jkeyWords["synchronized"] = "synchronized";
jkeyWords["boolean"] = "boolean";
jkeyWords["do"] = "do";
jkeyWords["if"] = "if";
jkeyWords["this"] = "this";
jkeyWords["private"] = "private";
jkeyWords["break"] = "break";
jkeyWords["double"] = "double";
jkeyWords["implements"] = "implements";
jkeyWords["protected"] = "protected";
jkeyWords["throw"] = "throw";
jkeyWords["byte"] = "byte";
jkeyWords["double"] = "double";
jkeyWords["else"] = "else";
jkeyWords["import"] = "import";
jkeyWords["public"] = "public";
jkeyWords["throws"] = "throws";
jkeyWords["case"] = "case";
jkeyWords["enum"] = "enum";
jkeyWords["instanceof"] = "instanceof";
jkeyWords["return"] = "return";
jkeyWords["transient"] = "transient";
jkeyWords["catch"] = "catch";
jkeyWords["extends"] = "extends";
jkeyWords["int"] = "int";
jkeyWords["short"] = "short";
jkeyWords["try"] = "try";
jkeyWords["char"] = "char";
jkeyWords["final"] = "final";
jkeyWords["interface"] = "interface";
jkeyWords["static"] = "static";
jkeyWords["void"] = "void";
jkeyWords["class"] = "class";
jkeyWords["finally"] = "finally";
jkeyWords["long"] = "long";
jkeyWords["strictfp"] = "strictfp";
jkeyWords["volatile"] = "volatile";
jkeyWords["const"] = "const";
jkeyWords["float"] = "float";
jkeyWords["native"] = "native";
jkeyWords["super"] = "super";
jkeyWords["while"] = "while";

var jspecial = {};
jspecial["{"] = "{";
jspecial["}"] = "}";
jspecial["["] = "]";
jspecial["]"] = "]";
jspecial["("] = "(";
jspecial[")"] = ")";
jspecial["<"] = "<";
jspecial[">"] = ">";

var jpunct = {};
jpunct[";"] = "";
jpunct[","] = "";
jpunct["."] = "";
jpunct["@"] = "";
jpunct["!"] = "";
jpunct["~"] = "";
jpunct["#"] = "";
jpunct["$"] = "";
jpunct["%"] = "";
jpunct["^"] = "";

jpunct["&"] = "";
jpunct["*"] = "";
jpunct["-"] = "";
jpunct["_"] = "";
jpunct["+"] = "";

jpunct["="] = "";
jpunct[":"] = "";
jpunct["/"] = "";
jpunct["?"] = "";
jpunct["\\"] = "";
jpunct["|"] = "";

var ceid = 0;
var lastword = "";
var defx = 100;
var defy = 50;

var curx = defx;
var cury = defy;
var lines = new Array();
var line = null;
var lw = null;
var mtcount = 0;
var ceHandle = null;
var enabledUndo = false;
var addWordFunc = null;
if(enabledUndo){
	addWordFunc =addObjectToGraph;
}else{
	addWordFunc = addObjectToGraphNoUndo;
}
function enableUndo(){
	enabledUndo = true;
	if(enabledUndo){
		addWordFunc =addObjectToGraph;
	}else{
		addWordFunc = addObjectToGraphNoUndo;
	}
}
function disableUndo(){
	enabledUndo = false;
	if(enabledUndo){
		addWordFunc =addObjectToGraph;
	}else{
		addWordFunc = addObjectToGraphNoUndo;
	}
}

function stopCodeEditor() {
	dojo.disconnect(ceHandle);
	ceHandle = null;
	window.onkeydown = null;
}

function compileCodeEditor() {
	if (mtcount == 0) {
		showLineStart();
	}
	mtcount++;
	window.onkeydown = function(e) {
		if (e.keyCode == 32 || e.keyCode == 8) {
			if(e.keyCode==32){
		        lastword = "";

			if (lw != null) {
				updateCursorWithWord();
			}
			if (line == null) {
				line = new Array();
			}
			addWordToLine(lw, line);
			lw = null;
		    }
			return false;
		}
	};
	if (ceHandle != null) {
		dojo.disconnect(ceHandle);
		ceHandle = null;
	}
	ceHandle = dojo.connect(window, "onkeypress", function(e) {
		var lcl = mtcount;
		switch (e.charOrCode) {
		case dojo.keys.ENTER: {
			lastword = "";
			if (line == null) {
				line = new Array();
			}
			addWordToLine(lw, line);
			lw = null;
			line = addNewLine(line);
			dojo.stopEvent(e);

			break;
		}

		case dojo.keys.BACKSPACE: {
			var word = null;
			if (lw != null && lw.attr("text").length > 0) {
				word = lw.attr("text");
				word = word.substring(0, word.length - 1);
				lastword = word;
				lw.attr("text", word);
			} else {
				lastword = "";
				if (line != null && line.length > 0) {
					lw = line.pop();
					if (lw != null) {
						lastword = lw.attr("text");
					}
				} else {
					lastword = "";
					if (lines != null) {
						line = lines.pop();
						if (line != null) {
							lw = line.pop();
							if (lw != null) {
								lastword = lw.attr("text");
							}
						}
					}
				}
			}
			syntaxHighliteWord(lw);
			dojo.stopEvent(e);
			break;
		}
		case " ": {
			lastword = "";

			if (lw != null) {
				updateCursorWithWord();
			}
			if (line == null) {
				line = new Array();
			}
			addWordToLine(lw, line);
			lw = null;
			dojo.stopEvent(e);
			break;
		}
		default: {
			console.log('CodeEditor: ' + lcl + " >" + e.keyChar + "< ");
			if(e.keyChar!=""){
			lastword += e.keyChar;
			if (lw != null)
				lw.attr("text", lastword);
			else {
				lw = getDispWord(lastword);
			}
			syntaxHighliteWord(lw);
			dojo.stopEvent(e);
			break;
			}
			break;
		}
		}
	});
}
function addNewLine(line) {
	lines.push(line);
	line = new Array();
	setCursorToStart();
	showLineStart();
	return line;
}
function updateCursorWithWord() {
	if(lw!=null){
	curx = lw.getBBox().x + lw.getBBox().width + wordGap;
	}
}
function setCursorToStart() {
	curx = defx;
}
function showLineStart() {
/*	var ret = pCanvas.text(curx - 40, cury + lines.length * 20, ""+ lines.length);
	ret.attr("font-size", 20);
	ret.attr("text-anchor", "start");*/

}
function addWordToLine(lw, line) {

	if (lw != null) {
		line.push(lw);
		lw.dblclick(function(evt) {
			var mid = this.node.getAttribute("eleid");
			var lid = lw.node.getAttribute("lineid");
			var wid = lw.node.getAttribute("wordid");
			if (lid > lines.length)
				console.log("clicked: " + mid + " " + lid + " " + wid + ">>"
						+ lines[lid][wid].attr("text"));
			else
				console.log("clicked: " + mid + " " + lid + " " + wid);
			var inp = prompt("Enter", this.attr("text"));
			this.attr("text", inp);
			var nd = findNodeById(mid)
			var bbox = this.getBBox();
			nd.r = bbox.width;
			nd.b = bbox.height;
			nd.text = inp;

		});

		var txt = lw.attr("text");
		var bb = lw.getBBox();
		var myid = ceid++;
		lw.node.setAttribute("eleid", "word_" + myid);
		lw.node.setAttribute("lineid", lines.length);
		lw.node.setAttribute("wordid", line.length - 1);
		var itm = {
			"item" : lw,
			"textnode" : null,
			"subs" : null,
			"id" : (myid)
		};
		drawElements.push(itm);
		addWordFunc({
			"type" : "ceword",
			"text" : txt,
			"x" : bb.x,
			"y" : bb.y,
			"r" : bb.width,
			"b" : bb.height,
			"normalizedx" : bb.x,
			"normalizedy" : bb.y,
			"id" : "word_" + myid,
			"line" : lines.length,
			"word" : line.length - 1
		});
	}
}
var codeEditDragger = function() {
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, codeEditMove = function(dx, dy) {
	var att = {
		x : mouseX,
		y : mouseY
	}
	this.attr(att);
	pCanvas.safari();
}, codeEditUp = function() {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var myid = this.node.getAttribute("eleid");
	var obj = findNodeById(myid)
	if (obj != null) {
		var bbox = this.getBBox();
		obj.normalizedx = bbox.x;
		obj.normalizedy = bbox.y;
		obj.x = bbox.x;
		obj.y = bbox.y;
        obj.r = bbox.width;
        obj.b = bbox.height;
		console.log("updated ceword");
	}
	if (Math.abs(this.ox - this.attr("x")) > 5
			|| Math.abs(this.oy - this.attr("y")) > 5) {
		// this is moved
	} else {
		// this is clicked
	}
	var id = findnearItem(this.attr("x"),this.attr("y"),"50","group");
	var group = null;
	if(id!=null){
		group = findNodeById(id);
	}
	if(group!=null){
		group.items.push(myid);
		this.animate( {
			"fill-opacity" : 0,
			"opacity" :0
		}, 800);
		setTimeout(function(){
			draw();
		},1000);
		
		
	}
	if(obj!=null)
	displaySelectedNodeProps(obj);
	pCanvas.safari();
};

function syntaxHighliteWord(lw, word) {
	if (word == null)
		word = lastword
	if (lw != null) {
		if (jkeyWords[word] != null) {
			lw.attr("stroke", "blue");
			lw.attr("fill", "blue");
		} else if (jspecial[word] != null) {
			lw.attr("stroke", "green");
			lw.attr("fill", "green");
		} else if (jpunct[word] != null) {
			lw.attr("stroke", "blue");
			lw.attr("fill", "blue");
		} else if ((word.charAt(0) == "\"" && word.charAt(word.length - 1) == "\"")
				|| (word.charAt(0) == "'" && word.charAt(word.length - 1) == "'")
				|| (word.charAt(0) == "`" && word.charAt(word.length - 1) == "`")) {
			lw.attr("stroke", "red");
			lw.attr("fill", "red");
		} else {
			lw.attr("stroke", "orange");
			lw.attr("fill", "orange");
		}
	}
}

postUpdateFun["codeeditor"] = function() {
	console.log("Now clearing editor");
	if (lines != null) {
		for ( var i = 0; i < lines.length; i++) {
			var ln = lines[i];
			for ( var j = 0; j < ln.length; j++) {
				var w = ln[j];
				if (w != null) {
					w.remove();
				}
			}
		}
	}
	if (line != null) {
		for ( var j = 0; j < line.length; j++) {
			var w = line[j];
			if (w != null) {
				w.remove();
			}
		}
	}
	line = new Array();
	lines = new Array();

}
function getDispWord(lastword, x, y) {
	if (x == null)
		x = curx;
	if (y == null)
		y = cury + lines.length * 20;
	var ret = pCanvas.text(x, y, lastword);
	ret.attr({
		font : '20px Fontin-Sans, Arial'
	});
	ret.attr("font-size", 20);
	ret.attr("text-anchor", "start");
	ret.drag(codeEditMove, codeEditDragger, codeEditUp);
	return ret;
}
genericnodes["ceword"] = "test.icon";
nodeDrawingHandler["ceword"] = function(xpos, ypos, w, h, fillColor,
		borderColor, obj, extra) {
	var lw = getDispWord(obj.text, xpos, ypos+obj.b/2);

	lw.node.setAttribute("eleid", obj.id);
	lw.node.setAttribute("lineid", obj.line);
	lw.node.setAttribute("wordid", obj.word);
	syntaxHighliteWord(lw, obj.text);
	lw.dblclick(function(evt) {
		var mid = this.node.getAttribute("eleid");
		var lid = lw.node.getAttribute("lineid");
		var wid = lw.node.getAttribute("wordid");
		if (lid > lines.length)
			console.log("clicked: " + mid + " " + lid + " "+ this.attr("text"));
		else
			console.log("clicked: " + mid + " " + lid + " " + wid);
		var inp = prompt("Enter", this.attr("text"));
		this.attr("text", inp);
		var nd = findNodeById(mid)
		var bbox = this.getBBox();
		nd.x = bbox.x;
		nd.y = bbox.y;
		nd.normalizedx = bbox.x;
		nd.normalizedy = bbox.y;
		nd.r = bbox.width;
		nd.b = bbox.height;
		nd.text = inp;
	});
	var item = {
		"item" : lw,
		"textnode" : null,
		"subs" : null,
		"id" : (obj.id)
	};
	drawElements.push(item);

}

function loadLines(a){
	ceid = 0;      	 	 	
	lastword="";
	defx = 100;
	defy = 100;
	curx = defx;
	cury = defy;
	lines = new Array();
	line = null;
	lw = null;
	mtcount = 0;
	showLineStart();
	appendLines(a);
}
function appendLines(a){

for(var i = 0;i<a.length;i++){
var ch = a.charAt(i);
if(ch=='|'||ch==','){
   loadChar(' ')
   loadChar(ch)
   loadChar(' ')
}else{
loadChar(ch)
}
}
loadChar(' ');
loadChar('\n');
}

function loadChar(ch){
    	var lcl  = mtcount;
	    switch(ch){
	       case  '\n' :{
	    	   lw = getDispWord(lastword);
	    	   syntaxHighliteWord(lw);
	    	   lastword="";
	    	   if(line==null){
	    		   line = new Array();
	    	   }
	    	   addWordToLine(lw,line);
	    	   lw = null;
	    	   line  = addNewLine(line);
	    	   break;
	       }
	       case  ' ' :{
	    	   if(lastword.length>0){
	    	   lw = getDispWord(lastword);
	    	   syntaxHighliteWord(lw);
	    	   }
	    	   lastword="";
	       		if(lw!=null){
	       			updateCursorWithWord();
	       		}
	     	   if(line==null){
	    		   line = new Array();
	    	   }
	     	   addWordToLine(lw,line);
	     	   lw = null;
	    	   break;
	       }
	       default: {
	       	console.log('CodeEditor: '+lcl+" >" +ch+"< ");
	       	lastword+=ch;
/*	       	if(lw!=null)
	       		lw.attr("text",lastword);
	       	else{
	       		lw = getDispWord(lastword);
	       	}
	       	   			syntaxHighliteWord(lw);
*/    
				break;
	       }
	    }
   

}
var maxcolumns = 0;
function getTempLines(){
	maxcolumns = 0;
var dlines = new Array();
for(var i=0;i<pData.data.length;i++){
    var c = pData.data[i];
    if(c.type=="ceword"){
        var ln =c.line;
        var w = c.word;
        if(dlines.length<ln+1){
        for(var k=dlines.length;k<ln+1;k++)    
         var line = new Array();
         dlines.push(line)
        }
        var line = dlines[ln];
        if(line.length<w+1){
        for(var k=line.length;k<w+1;k++)    
         var word = {};
         line.push(word)
        }
        dlines[ln][w]=c;
        if(dlines[ln].length>maxcolumns)
        	maxcolumns=dlines[ln].length;
    }
}
return dlines;
}

function pretyPrintData(){
	var dlines = getTempLines();
	console.log(dlines[0]);
	for(var k=0;k<maxcolumns;k++){
	var maxx = 0;
	for(var i=0;i<dlines.length;i++){
	    var line = dlines[i];
	    if(line!=null){
	    if(line.length>k){
	    	if(line[k]!=null && line[k].x!=null){
	        if(maxx<line[k].x+line[k].r){
	            maxx = line[k].x+line[k].r+wordGap
	        }
	    	}
	    }
	    }
	}
	for(var i=0;i<dlines.length;i++){
	    var line = dlines[i];
	    if(line!=null){
	    if(line.length>k+1){
	    	if(line[k+1]!=null && line[k+1].x!=null && line[k]!=null && line[k].x!=null){
		        line[k+1].x=maxx;
		        line[k+1].normalizedx=maxx;
		        line[k+1].y=line[k].y;
		        line[k+1].normalizedy=line[k].y;
	    	}
	        }
	    }
	    }
	}

}