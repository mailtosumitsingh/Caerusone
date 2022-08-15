/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function js_Print(s){
System.out.println(s);

}
function js_String(s){
return new java.lang.String(s);
}

function js_rnd16() {
    return scriptUtil.getRandomString(16);
}
function js_sout(str) {
    System.out.println(str);
}

function js_appendFile(name, str) {
    var w = new FileWriter(name,true);
    w.write(str);
    w.flush();
    w.close();
    w = null;
}


function js_writeFile(name, str) {
    var w = new FileWriter(name)
    w.write(str);
    w.flush();
    w.close();
    w = null;
}
 function js_loadFile(name) {
    var r = new LineNumberReader(new FileReader(name));
    var s = null ;
	 var ret = "";
    while ((s = r.readLine()) != null) {
      ret += s;
    }
	return ret;
}
function js_forEachLineInfile(name, f) {
    var r = new LineNumberReader(new FileReader(name));
    var s = null ;
    while ((s = r.readLine()) != null) {
        f(s);
    }
}
function js_forEachLine(f) {
    var str = js_getText();
    var a = StringUtils.split(str, '\n');
    for (i in a) {
        f(a[i]);
    }
}
function js_forEachWord(f) {
    var str = js_getText();
    var a = StringUtils.split(str);
    for (i in a) {
        f(a[i]);
    }
}
function js_substringBetween(str, start, end) {
    return StringUtils.substringsBetween(str, start, end);
}
function js_substringAfterLast(s, str) {
    return StringUtils.substringAfterLast(s, str);
}
function js_substringBeforeLast(s, str) {
    return StringUtils.substringBeforeLast(s, str);
}
function js_rightPad(s, i, c) {
    return StringUtils.rightPad(s, i, c);
}
function js_leftPad(s, i, c) {
    return StringUtils.leftPad(s, i, c);
}
function js_reverse(s) {
    return StringUtils.reverse(s);
}
function js_isAlphanumeric(s) {
    return StringUtils.isAlphanumeric(s);
}
function js_isAlpha(s) {
    return StringUtils.isAlpha(s);
}
function js_isWhitespace(s) {
    return StringUtils.isWhitespace(s);
}
function js_isEmpty(s) {
    return StringUtils.isBlank(s)
}
function js_except(s, start, end) {
    var temp = StringUtils.substringBefore(s, start);
    var temp2 = StringUtils.substringAfterLast(s, end);
    return temp + temp2;
}
function js_indexOf(s, s2) {
    return StringUtils.indexOf(s, s2);
}
function js_contains(s, s2) {
    return StringUtils.contains(s, s2);
}
function js_getActiveTabLine(component) {
    var caretPosition = component.getCaretPosition();
    var root = component.getDocument().getDefaultRootElement();
    return root.getElementIndex(caretPosition) + 1;
}
function js_getFileContent(name) {
    var r = new LineNumberReader(new FileReader(name));
    var str = "";
    var s = "" ;
    while ((s = r.readLine()) != null) {
        str += (s + "\n");
    }
	r.close();
    return str;
}
function js_forEachFileInDir(name, func) {
    var f = new File(name);
    if (f.isDirectory()) {
        var files = f.listFiles();
        for (fl in files) {
            if (files[ fl ].isFile()) {
                func(files[ fl ].getAbsolutePath());
            } else if (files[ fl ].isDirectory()) {
                js_forEachFileInDir(files[fl].getAbsolutePath(), func);
            }
        }
    }
}
function js_forEachDirAndFileInDir(name, func) {
    var f = new File(name);
    if (f.isDirectory()) {
        var files = f.listFiles();
        for (fl in files) {
            if (files[ fl ].isFile()) {
                func(files[ fl ].getAbsolutePath());
            } else if (files[ fl ].isDirectory()) {
                func(files[ fl ].getAbsolutePath());
                js_forEachFileInDir(files[fl].getAbsolutePath(), func);
            }
        }
    }
}


function js_createProject(dirpath) {
    var buildDir = dirpath + "\\build"
    var p = new Project();
    p.setSaveDir(dirpath + "\\output");
    p.setSaveFile(false);
    p.addJarInDir(dirpath + "\\lib");
    p.addAllowedExtsName("*");
    p.addAllowedImplsName("*");
    p.addAllowedClassName("*");
    p.addClassFilesInDir(buildDir);
    p.setSystemPath();
    p.setSaveFile(true);
    p.setDebug(false);
    p.addAllowedMethodComp("*", "*");
    return p;
}
function js_getAntProject() {
    var p = new Project();
    p.setProperty("ant.home", ".");
    var baseDir = config.getBaseDir();
    p.setBasedir(baseDir);
    return p;
}
function js_exec(cmd) {
	js_sout("now executing: "+ cmd)    ;
    var p = js_getAntProject();
    var e = new Exec();
    e.setProject(p);
    e.setCommand(cmd);
    e.setDir(p.getBaseDir());
    e.execute();
}

function js_sleep(msecs) {
    Thread.currentThread().sleep(msecs);
}
function js_substringBefore(s,s2) {
    return StringUtils.substringBefore(s,s2);
}

function js_execParallel(cmd){
var th = new Thread(run);
function run(){
js_exec(cmd);
}
th.start();
}

function js_sendEmail(from,to,subject,desc,attachments){
		var u = new EmailUtil();
		u.SendMailWithAttachment(from,to,
				subject,
				desc,
				attachments);
}

function js_sendEmailEx(from,to,subject,desc,attachments){
		var u = new EmailUtil();
		u.SendMailWithAttachmentEx(from,to,
				subject,
				desc,
				attachments);
}
function js_calcDistance(latA, longA,  latB, longB)
{
  var theDistance = (java.lang.Math.sin(java.lang.Math.toRadians(latA)) *
                        java.lang.Math.sin(java.lang.Math.toRadians(latB)) +
                        java.lang.Math.cos(java.lang.Math.toRadians(latA)) *
                        java.lang.Math.cos(java.lang.Math.toRadians(latB)) *
                        java.lang.Math.cos(java.lang.Math.toRadians(longA - longB)));

  return  (java.lang.Math.toDegrees(java.lang.Math.acos(theDistance))) * 69.09;
}


//=================================================
/* JSONPath 0.8.0 - XPath for JSON
*
* Copyright (c) 2007 Stefan Goessner (goessner.net)
* Licensed under the MIT (MIT-LICENSE.txt) licence.
*/
function jsonPath(obj, expr, arg) {
  var P = {
     resultType: arg && arg.resultType || "VALUE",
     result: [],
     normalize: function(expr) {
        var subx = [];
        return expr.replace(/[\['](\??\(.*?\))[\]']/g, function($0,$1){return "[#"+(subx.push($1)-1)+"]";})
                   .replace(/'?\.'?|\['?/g, ";")
                   .replace(/;;;|;;/g, ";..;")
                   .replace(/;$|'?\]|'$/g, "")
                   .replace(/#([0-9]+)/g, function($0,$1){return subx[$1];});
     },
     asPath: function(path) {
        var x = path.split(";"), p = "$";
        for (var i=1,n=x.length; i<n; i++)
           p += /^[0-9*]+$/.test(x[i]) ? ("["+x[i]+"]") : ("['"+x[i]+"']");
        return p;
     },
     store: function(p, v) {
        if (p) P.result[P.result.length] = P.resultType == "PATH" ? P.asPath(p) : v;
        return !!p;
     },
     trace: function(expr, val, path) {
        if (expr) {
           var x = expr.split(";"), loc = x.shift();
           x = x.join(";");
           if (val && val.hasOwnProperty(loc))
              P.trace(x, val[loc], path + ";" + loc);
           else if (loc === "*")
              P.walk(loc, x, val, path, function(m,l,x,v,p) { P.trace(m+";"+x,v,p); });
           else if (loc === "..") {
              P.trace(x, val, path);
              P.walk(loc, x, val, path, function(m,l,x,v,p) { typeof v[m] === "object" && P.trace("..;"+x,v[m],p+";"+m); });
           }
           else if (/,/.test(loc)) { // [name1,name2,...]
              for (var s=loc.split(/'?,'?/),i=0,n=s.length; i<n; i++)
                 P.trace(s[i]+";"+x, val, path);
           }
           else if (/^\(.*?\)$/.test(loc)) // [(expr)]
              P.trace(P.eval(loc, val, path.substr(path.lastIndexOf(";")+1))+";"+x, val, path);
           else if (/^\?\(.*?\)$/.test(loc)) // [?(expr)]
              P.walk(loc, x, val, path, function(m,l,x,v,p) { if (P.eval(l.replace(/^\?\((.*?)\)$/,"$1"),v[m],m)) P.trace(m+";"+x,v,p); });
           else if (/^(-?[0-9]*):(-?[0-9]*):?([0-9]*)$/.test(loc)) // [start:end:step]  phyton slice syntax
              P.slice(loc, x, val, path);
        }
        else
           P.store(path, val);
     },
     walk: function(loc, expr, val, path, f) {
        if (val instanceof Array) {
           for (var i=0,n=val.length; i<n; i++)
              if (i in val)
                 f(i,loc,expr,val,path);
        }
        else if (typeof val === "object") {
           for (var m in val)
              if (val.hasOwnProperty(m))
                 f(m,loc,expr,val,path);
        }
     },
     slice: function(loc, expr, val, path) {
        if (val instanceof Array) {
           var len=val.length, start=0, end=len, step=1;
           loc.replace(/^(-?[0-9]*):(-?[0-9]*):?(-?[0-9]*)$/g, function($0,$1,$2,$3){start=parseInt($1||start);end=parseInt($2||end);step=parseInt($3||step);});
           start = (start < 0) ? Math.max(0,start+len) : Math.min(len,start);
           end   = (end < 0)   ? Math.max(0,end+len)   : Math.min(len,end);
           for (var i=start; i<end; i+=step)
              P.trace(i+";"+expr, val, path);
        }
     },
     eval: function(x, _v, _vname) {
        try { return $ && _v && eval(x.replace(/@/g, "_v")); }
        catch(e) { throw new SyntaxError("jsonPath: " + e.message + ": " + x.replace(/@/g, "_v").replace(/\^/g, "_a")); }
     }
  };

  var $ = obj;
  if (expr && obj && (P.resultType == "VALUE" || P.resultType == "PATH")) {
     P.trace(P.normalize(expr).replace(/^\$;/,""), obj, "$");
     return P.result.length ? P.result : false;
  }
} 
var JSON = JSON || {};

(function () {

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return this.getUTCFullYear()   + '-' +
                 f(this.getUTCMonth() + 1) + '-' +
                 f(this.getUTCDate())      + 'T' +
                 f(this.getUTCHours())     + ':' +
                 f(this.getUTCMinutes())   + ':' +
                 f(this.getUTCSeconds())   + 'Z';
        };

        String.prototype.toJSON =
        Number.prototype.toJSON =
        Boolean.prototype.toJSON = function (key) {
            return this.valueOf();
        };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ?
            '"' + string.replace(escapable, function (a) {
                var c = meta[a];
                return typeof c === 'string' ? c :
                    '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
            }) + '"' :
            '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0 ? '[]' :
                    gap ? '[\n' + gap +
                            partial.join(',\n' + gap) + '\n' +
                                mind + ']' :
                          '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    k = rep[i];
                    if (typeof k === 'string') {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0 ? '{}' :
                gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' +
                        mind + '}' : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                     typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }
            text = String(text);

// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/.
test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function' ?
                    walk({'': j}, '') : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());


// Augment the basic prototypes if they have not already been augmented.
// These forms are obsolete. It is recommended that JSON.stringify and
// JSON.parse be used instead.

if (!Object.prototype.toJSONString) {
    Object.prototype.toJSONString = function (filter) {
        return JSON.stringify(this, filter);
    };
    Object.prototype.parseJSON = function (filter) {
        return JSON.parse(this, filter);
    };
}

function js_jsonPath(a,b){
	js_Print("Evaluating: "+a);
	eval("var jsonvar = "+a+";");
	
	return jsonPath(jsonvar, b).toJSONString() 
}


function CallWebServiceSilent(a,b,s,f){
	//var url  = SpringHelper.get("appContext").getProperty("ProcessWebService")+a+"?tosave="+b;
	var url  = "http://127.0.0.1:8095/"+a+"?tosave="+b;
	var rs = HTTPClientUtil.getText(url);
    js_Print(rs);
     //b is the string that contains the json data to save
    //which represent the stream node or the connection

}
/*data is pData.data from ui*/
function compileGraphOnServer(pdata){
	js_Print(pdata);
	var data = eval(pdata);
	var str = "";
	for (i in data){
	if( data[i].type=="node"){
	}else if( data[i].type=="stream"){
	str +=data[i].id+"\n";
	}
	saveGraphEle(data[i]);
	return js_String("Graph compiled.")
	}

	function saveGraphEle(obj){
	var objstr = obj.toJSONString();
	objstr=URLEncoder.encode(objstr);/*do that before calling web service other wise urls get messed up*/
	var id = obj.id;
	if(obj.type=="stream"){
	CallWebServiceSilent("SaveStream",objstr);
	}

	if(obj.type=="node"){
	CallWebServiceSilent("SaveNode",objstr);
	//MemcacheManager.getUtil().set("jsonpDef_" + id, objstr);
	}

	if(obj.type=="event"){
	CallWebServiceSilent("SaveEvent",objstr);
	}

	if(obj.type=="connection"){
	CallWebServiceSilent("SaveConnection",objstr);
	}

	if(obj.type=="route"){
	CallWebServiceSilent("SaveRoute",objstr);
	}
	}
}
/*data is pData.data from ui*/
function getGraphElementIds(pdata){
	var a  = new ArrayList();
	js_Print(pdata);
	var data = eval(pdata);
	js_Print("dada");
	for (i in data){
		js_Print(data[i]);
		var obj  = data[i];
		var id = obj.id;
		a.add(id);
	}
	return a;
}
/*data is pData.data from ui*/
function getGraphElements(pdata){
	var a  = new ArrayList();
	js_Print(pdata);
	var data = eval(pdata);
	for (i in data){
	var obj  = data[i];
	var id = obj.id;
	if(obj.type=="stream"){
		var objstr = obj.toJSONString();
		var stream = CommonUtil.getStreamDefinitionFromJsonData(objstr)
		a.add(stream);
	}
	if(obj.type=="node"){
		var objstr = obj.toJSONString();
		var stream = CommonUtil.getProcDefinitionFromJsonData(objstr)
		a.add(stream);
	}
	if(obj.type=="event"){
		var objstr = obj.toJSONString();
		var stream = CommonUtil.getEventDefinitionFromJsonData(objstr)
		a.add(stream);
	}
	if(obj.type=="connection"){
		obj.shape=null;
		js_Print(obj.shape==null?"NULL":obj.shape);
		obj.x=null;
		obj.y=null;
		var objstr = obj.toJSONString();
		var stream = CommonUtil.getConnDefinitionFromJsonData(objstr)
		a.add(stream);
	}if(obj.type=="group"){
		var objstr = obj.toJSONString();
		js_Print(objstr);
		var stream = CommonUtil.getGroupDefinitionFromJsonData(objstr)
		a.add(stream);
	}
	}
	return a;
}

/*data is pData.data from ui*/
function findGraphItemsInRegion(pdata,region){
	js_Print(pdata);
	var data = eval(pdata);
	/*build region*/
	js_Print(region);
	var poly = new Polygon();
	js_Print("Using class: in function findGraphItemsInRegion:"+poly.getClass().getName());
	eval("var rg ="+region+";");
	for(var pt=0;pt<rg.length; pt++){
		js_Print(rg[pt].toJSONString());
		var cp = rg[pt];
		if(cp.x!=null && cp.y!=null){
			poly.addPoint(cp.x, cp.y);
		}
	}
	/*now polygon is ready to varify*/
	var toret = new Array();
	for (i in data){
		var cp = (data[i]);
		if(cp.normalizedx!=null && cp.normalizedy!=null){
			js_Print(cp.normalizedx+":"+cp.normalizedy);
			
			if(poly.contains(parseFloat(cp.normalizedx), parseFloat(cp.normalizedy))){
				js_Print(cp.id +" is contained");
				toret.push(cp.id);
			}else{
				js_Print(cp.id +" is not contained");
			}
		}
		}

	return toret.toJSONString();
	
}

function updateGraphItemsProp(jsonStr,ids,props,vals){
	 eval("var obj ="+ jsonStr+";");
	 for(var j =0;j<ids.length;j++){
		 var id = ids[j];
		 var prop = props[j];
		 var val = vals[j];
		 for(var i in obj.data){
			 if(obj.data[i].id==id){
				js_Print("Now updating: <"+id+"> prop : <"+prop+ "> val : <"+val+">");
				obj.data[i][""+prop] = ""+val;
			}
		 }
	 }
	return obj.toJSONString();
}
function getGraphItemsProp(jsonStr,id,prop){
	var ret = null; 
	eval("var obj ="+ jsonStr+";");
		 for(var i in obj.data){
			 if(obj.data[i].id==id){
				ret = js_String(obj.data[i][""+prop]) ;
			}
		 }
	return ret;
}

function getGraphItemsJson(jsonStr){
	 eval("var obj ="+ jsonStr+";");
	 return obj.data.toJSONString();
}


function isPointInPoly(poly, pt){
    for(var c = false, i = -1, l = poly.length, j = l - 1; ++i < l; j = i)
        ((poly[i].y <= pt.y && pt.y < poly[j].y) || (poly[j].y <= pt.y && pt.y < poly[i].y))
        && (pt.x < (poly[j].x - poly[i].x) * (pt.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x)
        && (c = !c);
    return c;
}


function findBoundingRect(pdata,region){
	js_Print(pdata);
	var data = eval(pdata);
	/*build region*/
	js_Print(region);
	var poly = new Polygon();
	js_Print("Using class: in function findGraphItemsInRegion:"+poly.getClass().getName());
	eval("var rg ="+region+";");
	for(var pt=0;pt<rg.length; pt++){
		js_Print(rg[pt].toJSONString());
		var cp = rg[pt];
		if(cp.x!=null && cp.y!=null){
			poly.addPoint(cp.x, cp.y);
		}
	}
	/*now polygon is ready to varify*/
	var poly2 = new Polygon();
	for (i in data){
		var cp = (data[i]);
		if(cp.normalizedx!=null && cp.normalizedy!=null){
			js_Print(cp.normalizedx+":"+cp.normalizedy);
			
			if(poly.contains(parseFloat(cp.normalizedx), parseFloat(cp.normalizedy))){
				js_Print(cp.id +" is contained");
				poly2.addPoint(cp.normalizedx, cp.normalizedy);
			}else{
				js_Print(cp.id +" is not contained");
			}
		}
		}
	var r = poly2.getBounds();
	var toRet = {};
	toRet.x = r.getX();
	toRet.y = r.getY();
	toRet.w = r.getWidth();
	toRet.h = r.getHeight();

	toRet.x = toRet.x - 5;
	toRet.y = toRet.y - 5;
	toRet.w = toRet.w + 10; 
	toRet.h = toRet.h + 10;
	return toRet.toJSONString();
	
}

function getPoly(pdata){
		js_Print(pdata);
		eval("var rg ="+pdata+";");
		var poly = new Polygon();
		js_Print("Using class: in function getPoly:"+poly.getClass().getName());
		for(var pt=0;pt<rg.length; pt++){
				var cp = (rg[pt]);
				poly.addPoint(cp.x,cp.y);
		}
		return poly;
}