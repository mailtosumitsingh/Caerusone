/*
Licensed under gpl.

Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */
var editors = {};

function createActionButton(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var url = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		url = prompt('Enter URL', 'http://someurl');
		uid = prompt('Enter Uid', 'Button1');
		txt = prompt('Enter Label', 'Button1');
		configobj = {
			"id" : uid,
			"url" : url,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : dojo.toJson(code),
			"label" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		code = dojo.fromJson(configobj.code);
		url = configobj.url;
		uid = configobj.id;
		txt = configobj.label;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("input", {
			"type" : "button",
			"value" : txt,
			"style" : "width:100%;height:100%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "button_" + uid);
		var bcode = "doGetHtml(\"" + url + "\"," + "function fun(evt){\n"
				+ code + "\n}" + ");"
		console.log("Code for action button: " + bcode);
		var fun = (new Function(bcode));
		dojo.connect(mnode, "ondblclick", fun);
		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}

function createButton(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		uid = prompt('Enter Uid', 'Button1');
		txt = prompt('Enter Label', 'Button1');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : dojo.toJson(code),
			"label" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		code = dojo.fromJson(configobj.code);
		uid = configobj.id;
		txt = configobj.label;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("input", {
			"type" : "button",
			"value" : txt,
			"style" : "width:100%;height:100%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "button_" + uid);
		var fun = (new Function(code));
		dojo.connect(mnode, "ondblclick", fun);
		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createLink(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var txt = null;
	var href = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		uid = prompt('Enter uid', 'Link1');
		href = prompt('Enter href', '/bam_5678/wide.html');
		txt = prompt('Enter LinkText', 'Link1');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : dojo.toJson(code),
			"label" : txt,
			"href" : href,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		code = dojo.fromJson(configobj.code);
		uid = configobj.id;
		txt = configobj.label;
		href = configobj.href;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("a", {
			"innerHTML" : txt,
			"href" : href
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "link_" + uid);
		var fun = (new Function(code));
		dojo.connect(mnode, "onclick", fun);
		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createNewClassPropMapping(f, a, compname, isui, mx, my, configobj) {
	createClassMapping(f, a, compname, isui, mx, my, configobj, "prop", true);
}

function createNewClassOutputMapping(f, a, compname, isui, mx, my, configobj) {
	createClassMapping(f, a, compname, isui, mx, my, configobj, "output", true);
}
function createNewClassInputMapping(f, a, compname, isui, mx, my, configobj) {
	createClassMapping(f, a, compname, isui, mx, my, configobj, "input", true);
}
function createClassPropMapping(f, a, compname, isui, mx, my, configobj) {
	createClassMapping(f, a, compname, isui, mx, my, configobj, "prop", false);
}

function createRemapMapping(f, a, compname, isui, mx, my, configobj) {
	createClassReMapping(f, a, compname, isui, mx, my, configobj, "output", false);
}
function createClassOutputMapping(f, a, compname, isui, mx, my, configobj) {
	createClassMapping(f, a, compname, isui, mx, my, configobj, "output", false);
}
function createSQLOutputMapping(f, a, compname, isui, mx, my, configobj) {
	createSQLMapping(f, a, compname, isui, mx, my, configobj, "output", false);
}
function createSQLInputMapping(f, a, compname, isui, mx, my, configobj) {
	createSQLMapping(f, a, compname, isui, mx, my, configobj, "input", false);
}
function createClassInputMapping(f, a, compname, isui, mx, my, configobj) {
	createClassMapping(f, a, compname, isui, mx, my, configobj, "input", false);
}
function createClassMapping(f, a, compname, isui, mx, my, configobj,
		mappingtype, newname) {
	var uid = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	var cls = null;
	var nname = null;
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		cntn = "";
		if (newname) {
			cls = prompt('Enter New Name', '');
			nname = "org.ptg.proptest.Empty";
		} else {
			cls = prompt('Enter Class Name', '');
		}
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"cls" : cls,
			"zindex" : (getZIndex())
		};

		var req = {};
		if (newname) {
			req.classname = nname;
			req.newclassname = cls;

		} else {
			req.classname = cls;
		}
		req.id = uid;
		req.type = mappingtype;
		postFormWithContent("/site/GetClassLayout", req, function(res) {
			var a = getStaticModule(uid, l, t, w, h, res, zi);
			//dojo.byId("bid_" + uid).innerHTML = res;
			ddtreemenu.createTree("t_" + uid);
			// prepare portable again
			configobj.cntn = res;
			compDiag.push(configobj);
			preparePortable();
			convertNestedItemsToContainers();

		});

	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		cls = configobj.cls;
		try {
			var a = getStaticModule(uid, l, t, w, h, cntn, zi);
/*			var target = new dojo.dnd.Source("bid_" + uid, {
				copyOnly : true,
				accept : [ "text", "treeNode" ],
				creator : widgetSourceCreator
			});
			var target = dojo.byId("bid_" + uid);
			dojo.connect(target, "onDrop", function(source, nodes, copy) {
				if (lastDropModuleGrp != null) {
					var mod = lastDropModuleGrp;
					findComponentByName(mod).cntn = dojo.byId("bid_"+ lastDropModuleGrp).innerHTML;
				}
			});
			*/
			htmlcomps[uid] = a;
			ddtreemenu.createTree("t_" + uid);
			preparePortable();
			convertNestedItemsToContainers()
			
		} catch (e) {
			console.log(e);
			alert(e.message || e);
		}
	}
}
function createSQLMapping(f, a, compname, isui, mx, my, configobj,
		mappingtype, newname) {
	var uid = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	var cls = null;
	var nname = null;
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		cntn = "";
		if (newname) {
			cls = prompt('Enter New Name', '');
			nname = "org.ptg.proptest.Empty";
		} else {
			cls = prompt('Enter Class Name', '');
		}
		uid = uid +"(" +"db"+ ")"
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"cls" : cls,
			"zindex" : (getZIndex())
		};

		var req = {};
		if (newname) {
			req.classname = nname;
			req.newclassname = cls;

		} else {
			req.classname = cls;
		}
		req.id = uid;
		req.type = mappingtype;
		postFormWithContent(getURL("GetSQLLayout"), req, function(res) {
			var a = getStaticModule(uid, l, t, w, h, res, zi);
			//dojo.byId("bid_" + uid).innerHTML = res;
			ddtreemenu.createTree("t_" + uid);
			// prepare portable again
			configobj.cntn = res;
			compDiag.push(configobj);
			preparePortable();
			convertNestedItemsToContainers();

		});

	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		cls = configobj.cls;
		try {
			var a = getStaticModule(uid, l, t, w, h, cntn, zi);
			htmlcomps[uid] = a;
			ddtreemenu.createTree("t_" + uid);
			preparePortable();
			convertNestedItemsToContainers()
			
		} catch (e) {
			console.log(e);
			alert(e.message || e);
		}
	}
}
function createProtoMapping(f, a, compname, isui, mx, my, configobj,
		mappingtype) {
	var uid = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	var cls = null;
	var nname = null;
	var pname = null
	if (isui == true) {
		cntn = "";
		uid = prompt('Enter uid', 'Module1');
		cls = prompt('Enter file Name', '');
		pname = prompt('Enter proto Name', '');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"filename" : cls,
			"pname":pname,
			"zindex" : (getZIndex())
		};

		var req = {};
		req.filename = cls;
		req.id = uid;
		req.type = mappingtype;
		req.name = pname; 
		postFormWithContent(getURL("GetProtoLayout"), req, function(res) {
			var a = getStaticModule(uid, l, t, w, h, res, zi);
			//dojo.byId("bid_" + uid).innerHTML = res;
			ddtreemenu.createTree("t_" + uid);
			// prepare portable again
			configobj.cntn = res;
			compDiag.push(configobj);
			preparePortable();
			convertNestedItemsToContainers();

		});

	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		cls = configobj.filename;
		pname = configobj.pname
		try {
			var a = getStaticModule(uid, l, t, w, h, cntn, zi);
/*			var target = new dojo.dnd.Source("bid_" + uid, {
				copyOnly : true,
				accept : [ "text", "treeNode" ],
				creator : widgetSourceCreator
			});
			var target = dojo.byId("bid_" + uid);
			dojo.connect(target, "onDrop", function(source, nodes, copy) {
				if (lastDropModuleGrp != null) {
					var mod = lastDropModuleGrp;
					findComponentByName(mod).cntn = dojo.byId("bid_"+ lastDropModuleGrp).innerHTML;
				}
			});
			*/
			htmlcomps[uid] = a;
			ddtreemenu.createTree("t_" + uid);
			preparePortable();
			convertNestedItemsToContainers()
			
		} catch (e) {
			console.log(e);
			alert(e.message || e);
		}
	}
}

function createSimpleStaticModule(f, a, compname, isui, mx, my, configobj) {
	var uid = null;
	var cls = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		cntn = prompt('Enter Content', '');
		cls = prompt('Enter Class', '');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"cls" : cls,
			"zindex" : (getZIndex())
		};
		compDiag.push(configobj);

	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		cls = configobj.cls;
	}
	try {
		if(cls==null||cls=="")
			cls = "staticnode";
		var a = getSimpleStaticModule(uid, cls, l, t, w, h, cntn, zi)
		var target = new dojo.dnd.Source("bid_" + uid, {
			copyOnly : true,
			accept : [ "text", "treeNode" ],
			creator : widgetSourceCreator
		});
		dojo.connect(target, "onDrop",
				function(source, nodes, copy) {
					if (lastDropModule != null) {
						var mod = lastDropModule.substring(4);
						findComponentByName(mod).cntn = dojo
								.byId(lastDropModule).innerHTML;
					}
				});
	} catch (e) {
		alert(e.message || e);
	}
}
function createModule(f, a, compname, isui, mx, my, configobj) {
	var path = null;
	var uid = null;
	var modulename = null;
	var jscomp = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		path = prompt('Enter Path', '/bam_5678/resources/extra/test.html');
		modulename = prompt('Enter Module Name', 'TestModule');
		jscomp = prompt('Enter JS Path',
				'/bam_5678/resources/extra/testmodule.js');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"path" : path,
			"modulename" : modulename,
			"jscomp" : jscomp,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : (getZIndex())
		};
		compDiag.push(configobj);

	} else {
		path = configobj.path;
		uid = configobj.id;
		modulename = configobj.modulename;
		jscomp = configobj.jscomp;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = getDynaModule(uid, path, modulename, jscomp, l, t, w, h, zi)
		htmlcomps[uid] = a;
	} catch (e) {
		alert(e.message || e);
	}
}
function createHeaderModule(f, a, compname, isui, mx, my, configobj) {
	var path = null;
	var uid = null;
	var modulename = null;
	var jscomp = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	if (isui == true) {
		uid = prompt('Enter uid', getUniqId());
		path = prompt('Enter Path',
				'/wide_5678/resources/extra/img/imgmod.html');
		modulename = prompt('Enter Module Name', 'imgmod');
		jscomp = prompt('Enter JS Path',
				'/wide_5678/resources/extra/img/imgmod.js');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"path" : path,
			"modulename" : modulename,
			"jscomp" : jscomp,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : (getZIndex())
		};
		compDiag.push(configobj);

	} else {
		path = configobj.path;
		uid = configobj.id;
		modulename = configobj.modulename;
		jscomp = configobj.jscomp;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = getDynaHeaderModule(uid, path, modulename, jscomp, l, t, w, h,
				zi)
		htmlcomps[uid] = a;
	} catch (e) {
		alert(e.message || e);
	}
}
function createImage(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		uid = prompt('Enter Uid', 'img1');
		txt = prompt('Enter image src', '/images/cal.gif');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : dojo.toJson(code),
			"src" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		code = dojo.fromJson(configobj.code);
		uid = configobj.id;
		txt = configobj.src;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("img", {
			"src" : txt,
			"style" : "width:90%;height:90%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "img_" + uid);
		var fun = (new Function(code));
		dojo.connect(mnode, "ondblclick", fun);
		htmlcomps[uid] = mnode;

	} catch (e) {
		alert(e.message || e);
	}
}
function createRawImage(f, a, compname, isui, mx, my, configobj) {
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	var anim = null;
	if (isui == true) {
		uid = prompt('Enter Uid', 'img1');
		anim = prompt('Enter Animation', '');
		txt = prompt('Enter image src', '/images/cal.gif');
		configobj = {
			"id" : uid,
			"anim" : anim,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"src" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		uid = configobj.id;
		txt = configobj.src;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		anim = configobj.anim;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("img", {
			"src" : txt,
			"style" : "width:100%;height:100%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "img_" + uid);
		htmlcomps[uid] = mnode;
		playAnim(anim);
	} catch (e) {
		alert(e.message || e);
	}
}
function createTextArea(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		uid = prompt('Enter Uid', 'TextArea1');
		txt = prompt('Enter Text', 'Some Text');
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : dojo.toJson(code),
			"txt" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		code = dojo.fromJson(configobj.code);
		uid = configobj.id;
		txt = configobj.txt;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("textarea", {
			"value" : txt,
			"style" : "width:100%;height:70%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "textarea_" + uid);

		var mnodebs = dojo.create("input", {
			"type" : "button",
			"value" : "Apply",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebs, "id", "savebutton_" + uid);
		var fun = (new Function("saveTAValue(\"" + uid + "\")"));
		dojo.connect(mnodebs, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Reset",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "resetbutton_" + uid);
		var fun = (new Function("reloadTaValue(\"" + uid + "\")"));
		dojo.connect(mnodebr, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Run",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "runbutton_" + uid);
		var fun = (new Function(code));
		dojo.connect(mnodebr, "onclick", fun);

		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}

function saveTAValue(id) {
	var a = findComponentByName(id)
	if (a != null) {
		var b = editors[id].getSession().getDocument().getValue();
		if (a.origtxt == null)
			a.origtxt = a.txt;// do only one time
		a.txt = b;
	}
}

function reloadTaValue(id) {
	var a = findComponentByName(id)
	if (a != null) {
		var b = a.origtxt == null ? a.txt : a.origtxt;
		editors[id].getSession().getDocument().setValue(b);
	}
}

comps_graph_init = function() {
	configs["FetchUrlButton"] = createActionButton;
	comps["FetchUrlButton"] = createActionButton;
	configs["Button"] = createButton;
	comps["Button"] = dummy;
	configs["Link"] = createLink;
	comps["Link"] = dummy;
	configs["Module"] = createModule;
	comps["Module"] = dummy;
	configs["ClassInputMapping"] = createClassInputMapping;
	comps["ClassInputMapping"] = dummy;
	configs["Remap"] = createRemapMapping;
	comps["Remap"] = dummy;
	configs["ClassOutputMapping"] = createClassOutputMapping;
	comps["ClassOutputMapping"] = dummy;
	configs["ClassPropMapping"] = createClassPropMapping;
	comps["ClassPropMapping"] = dummy;

	configs["NewClassInputMapping"] = createNewClassInputMapping;
	comps["NewClassInputMapping"] = dummy;
	configs["NewClassOutputMapping"] = createNewClassOutputMapping;
	comps["NewClassOutputMapping"] = dummy;
	configs["NewClassPropMapping"] = createNewClassPropMapping;
	comps["NewClassPropMapping"] = dummy;

	configs["ProtoBuff"] = createProtoMapping;
	comps["ProtoBuff"] = dummy;

	
	configs["AnonScriptModule"] = createAnonScriptModule;
	comps["AnonScriptModule"] = dummy;

	configs["CanvasModule"] = createCanvasModule;
	comps["CanvasModule"] = dummy;

	configs["StaticModule"] = createStaticModule;
	comps["StaticModule"] = dummy;
	configs["SimpleStaticModule"] = createSimpleStaticModule;
	comps["SimpleStaticModule"] = dummy;
	configs["Image"] = createImage;
	comps["Image"] = dummy;
	configs["TextArea"] = createTextArea;
	comps["TextArea"] = dummy;
	configs["Graphics"] = createGraphicsEditor;
	comps["Graphics"] = dummy;
	configs["Label"] = createLable;
	comps["Label"] = dummy;
	configs["Animation"] = createAnimation;
	comps["Animation"] = dummy;
	configs["Input"] = createInput;
	comps["Input"] = dummy;
	configs["Action"] = createAction;
	comps["Action"] = dummy;
	configs["EventAnimation"] = createEventAnimation;
	comps["EventAnimation"] = dummy;
	configs["RawImage"] = createRawImage;
	comps["RawImage"] = dummy;
	configs["Code"] = createCode;
	comps["Code"] = dummy;
	configs["HeaderModule"] = createHeaderModule;
	comps["HeaderModule"] = dummy;
	configs["HeaderLabel"] = createHeaderLable;
	comps["HeaderLabel"] = dummy;
	configs["Last"] = createLast;
	comps["Last"] = dummy;
	configs["Shape"] = createShape;
	comps["Shape"] = dummy;
	configs["CanvasImage"] = createImage;
	comps["CanvasImage"] = dummy;
	configs["SQLInputMapping"] = createSQLInputMapping;
	comps["SQLInputMapping"] = dummy;
	configs["SQLOutputMapping"] = createSQLOutputMapping;
	comps["SQLOutputMapping"] = dummy;

	
	new dojo.dnd.Source("customwdgts", {
		copyOnly : true
	});
}

function createGraphicsEditor(f, a, compname, isui, mx, my, configobj) {
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "60px";
	var zi = null;
	if (isui == true) {
		uid = prompt('Enter Uid', 'Graphics');
		txt = prompt('Enter Text', 'pCanvas.line(10,10,100,100);');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"txt" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		uid = configobj.id;
		txt = configobj.txt;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("div", {
			"value" : txt,
			"style" : "width:80%;height:90%;position:relative;"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "textarea_" + uid);
		var editor = ace.edit("textarea_" + uid);
		editor.setTheme("ace/theme/eclipse");
		var JavaMode = require("ace/mode/javascript").Mode;
		editor.getSession().setMode(new JavaMode());
		editor.getSession().getDocument().setValue(txt);
		editors[uid] = editor;

		var mnodebs = dojo.create("input", {
			"type" : "button",
			"value" : "Apply",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebs, "id", "savebutton_" + uid);
		var fun = (new Function("saveTAValue(\"" + uid + "\")"));
		dojo.connect(mnodebs, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Reset",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "resetbutton_" + uid);
		var fun = (new Function("reloadTaValue(\"" + uid + "\")"));
		dojo.connect(mnodebr, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Run",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "runbutton_" + uid);
		// var cd = "dojo.eval(dojo.byId(\""+"textarea_"+uid+"\").value);";
		var cd = "dojo.eval(editors[\"" + uid
				+ "\"].getSession().getDocument().getValue());";
		var fun = (new Function(cd));
		dojo.connect(mnodebr, "onclick", fun);

		var msavebr = dojo.create("input", {
			"type" : "button",
			"value" : "Save",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(msavebr, "id", "savebutton_" + uid);
		var cd = "saveTAValue(\"" + uid + "\");"+"saveStaticComp('" + uid + "','" + uid + "','" + uid + "');";
		var fun = (new Function(cd));
		dojo.connect(msavebr, "onclick", fun);

		var msaveasbr = dojo.create("input", {
			"type" : "button",
			"value" : "SaveAs",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(msaveasbr, "id", "saveasbutton_" + uid);
		var cd = "saveTAValue(\"" + uid + "\"); var saveas = prompt('save as');saveStaticComp('" + uid+"',saveas,saveas);";
		var fun = (new Function(cd));
		dojo.connect(msaveasbr, "onclick", fun);

		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createLable(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		uid = prompt('Enter uid', 'Lable1');
		txt = prompt('Enter Label Text', 'Some Label');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : dojo.toJson(code),
			"label" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		try {
			code = dojo.fromJson(configobj.code);
		} catch (e) {
			alert(e.message || e);
		}
		uid = configobj.id;
		txt = configobj.label;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("div", {
			"innerHTML" : txt
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "label_" + uid);
		/* if(isui==true) */{
			var mynode = dojo.byId("heading_" + uid);
			dojo.connect(mynode, "ondblclick", function(e) {
				var nd = e.target;
				e.preventDefault(); // stop the event
				var uid = dojo.attr(nd, "id");
				// alert("uid:"+uid);
				uid = uid.substring(8);
				// alert("uid:"+uid);
				var cd = findComponentByName(uid).label;
				showEditor(cd, uid,"createLable");
			});
		}/*
			 * this file is for design mode only so comming is ui part we should
			 * in deisgn mode be always be able to change the ocntent of the
			 * labele else{ var fun = (new Function(code));
			 * dojo.connect(mnode,"onclick",fun); }
			 */
		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createHeaderLable(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		code = prompt('Enter Code', 'alert(\"OnTimer\");');
		uid = prompt('Enter uid', 'Lable1');
		txt = prompt('Enter Label Text', 'Some Label');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : dojo.toJson(code),
			"label" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		code = dojo.fromJson(configobj.code);
		uid = configobj.id;
		txt = configobj.label;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("div", {
			"innerHTML" : txt
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "label_" + uid);
		/* if(isui==true) */{
			var mynode = dojo.byId("heading_" + uid);
			dojo.connect(mynode, "ondblclick", function(e) {
				var nd = e.target;
				e.preventDefault(); // stop the event
				var uid = dojo.attr(nd, "id");
				// alert("uid:"+uid);
				uid = uid.substring(8);
				// alert("uid:"+uid);
				var cd = findComponentByName(uid).label;
				showEditor(cd, uid,"createHeaderLable");
			});
		}/*
			 * this file is for design mode only so comming is ui part we should
			 * in deisgn mode be always be able to change the ocntent of the
			 * labele else{ var fun = (new Function(code));
			 * dojo.connect(mnode,"onclick",fun); }
			 */
		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createAnimation(f, a, compname, isui, mx, my, configobj) {
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		uid = prompt('Enter Uid', 'Animation1');
		txt = prompt('Enter Text', 'pCanvas.line(10,10,100,100);');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"txt" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		uid = configobj.id;
		txt = configobj.txt;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("textarea", {
			"value" : txt,
			"style" : "width:100%;height:70%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "textarea_" + uid);

		var mnodebs = dojo.create("input", {
			"type" : "button",
			"value" : "Apply",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebs, "id", "savebutton_" + uid);
		var fun = (new Function("saveTAValue(\"" + uid + "\")"));
		dojo.connect(mnodebs, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Reset",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "resetbutton_" + uid);
		var fun = (new Function("reloadTaValue(\"" + uid + "\")"));
		dojo.connect(mnodebr, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Run",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "runbutton_" + uid);
		var cd = "dojo.eval(dojo.byId(\"" + "textarea_" + uid + "\").value);";

		var fun = (new Function(cd));
		dojo.connect(mnodebr, "onclick", fun);

		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createCode(f, a, compname, isui, mx, my, configobj) {
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	if (isui == true) {
		uid = prompt('Enter Uid', 'Code1');
		txt = prompt('Enter Text', 'pCanvas.line(10,10,100,100);');
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"txt" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex
		};
		compDiag.push(configobj);
	} else {
		uid = configobj.id;
		txt = configobj.txt;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("textarea", {
			"value" : txt,
			"style" : "width:100%;height:70%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "textarea_" + uid);

		var mnodebs = dojo.create("input", {
			"type" : "button",
			"value" : "Apply",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebs, "id", "savebutton_" + uid);
		var fun = (new Function("saveTAValue(\"" + uid + "\")"));
		dojo.connect(mnodebs, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Reset",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "resetbutton_" + uid);
		var fun = (new Function("reloadTaValue(\"" + uid + "\")"));
		dojo.connect(mnodebr, "onclick", fun);

		var mnodebr = dojo.create("input", {
			"type" : "button",
			"value" : "Run",
			"style" : "width:60px;height:40px"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnodebr, "id", "runbutton_" + uid);
		var cd = "dojo.eval(dojo.byId(\"" + "textarea_" + uid + "\").value);";

		var fun = (new Function(cd));
		dojo.connect(mnodebr, "onclick", fun);

		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createInput(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var uid = null;
	var txt = null;
	var l = mx, t = my, w = "60px", h = "40px";
	var zi = null;
	var type = "text";
	if (isui == true) {
		uid = prompt('Enter Uid', 'input1');
		txt = prompt('Enter txt', 'some random text');
		type = prompt('Enter type', 'text');
		txt = dojo.toJson(txt);
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"txt" : txt,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : zindex,
			"inputtype" : type
		};
		compDiag.push(configobj);
	} else {
		type = configobj.inputtype;
		uid = configobj.id;
		txt = configobj.txt;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
	}
	try {
		var a = createHeadlessDynamicModuleHolder(uid);
		a.style.left = configobj.left;
		a.style.top = configobj.top;
		a.style.width = configobj.width;
		a.style.height = configobj.height;
		a.style.zIndex = configobj.zindex;
		var mnode = dojo.create("input", {
			"type" : type,
			"value" : txt,
			"style" : "width:100%;height:100%"
		}, dojo.byId("bid_" + uid));
		dojo.attr(mnode, "id", "input_" + uid);
		var fun = (new Function(code));
		dojo.connect(mnode, "ondblclick", fun);
		htmlcomps[uid] = mnode;
	} catch (e) {
		alert(e.message || e);
	}
}
function createLast(f, a, compname, isui, mx, my, configobj) {
	var a = null;
	if (isui == true) {
		var a = prompt('Enter Code', 'pCanvas.line(0,0,1000,1000);');
		var configobj = {
			"id" : "LASTComponent",
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : 0,
			"mouseY" : 0,
			"t" : 0,
			"left" : 0,
			"top" : 0,
			"width" : 0,
			"height" : 0,
			"zindex" : 0,
			"action" : a
		};
		compDiag.push(configobj);
	} else {
		a = configobj.action;
	}
	var func = (new Function(a));
	func();
}
function createShape(f, a, compname, isui, mx, my, configobj) {
	console.log("Got a shape");
	var a = null;
	if (isui == true) {
		var a = prompt('Enter Code', 'var shape = pCanvas.line(0,0,1000,1000);');
		var uid = prompt('Enter uid', 'shape1');
		var configobj = {
			"id" : uid,
			"name" : compname,
			"design" : a
		};
		compDiag.push(configobj);
	} else {
		a = configobj.design;
	}
	var torun = a;
	torun += "shape.node.id=\"" + configobj.id + "\";";
	torun += "globals[\"" + configobj.id + "\"]=shape;\n";
	torun += "shape.drag(mymove, mydrag, myup);\n";
	if(configobj.tr!=null){
		torun += "shape.translate("+configobj.tr.x+","+configobj.tr.y+");\n";
		console.log("Shape has transation: "+configobj.tr);
	}
	var func = (new Function(torun));
	func();
	var eleid = configobj.id;
	if (configobj.pts) {
		var pts = dojo.fromJson(configobj.pts);
		var count = 0;
		for ( var j in pts) {
			var c = pCanvas.circle(pts[j].x, pts[j].y, 3, 3);
			c.node.id = pts[j].id;
			c.node.eleid = eleid;
			c.attr("fill", "blue");
			globals[pts[j].id] = c;
			c.drag(ptmove, ptdrag, ptup);
			c.mouseover(function(e) {
				if(eraseMode){
					removePoint(this);
				}else{
					this.attr("fill", "red");
				}

			});
			c.mouseout(function(e) {
				this.attr("fill", "blue");
			});
			c.dblclick(function(e) {
					removePoint(this);
			});
			c.toFront();
		}
	}

}
function getPointTxt(pt){
	return "( "+(pt.x/20).toFixed(2)+" mm"+" , "+(pt.y/20).toFixed(2)+" mm"+")";
}
function createAction(f, a, compname, isui, mx, my, configobj) {
	// alert("TargetElement" +configobj.onelem );
	// alert("Event" +configobj.onevent );
	// alert("ActionType" +configobj.action);

	var targetEle = findComponentByName(configobj.onelem)
	var actionToTake = findComponentByName(configobj.action)
	var action = configobj.onevent
	// alert(getPrepend(targetEle.name)+targetEle.id);
	// alert(actionToTake.txt);
	// alert(action);
	if (action == "onRowClick") {
		var nd = grids[targetEle.id]
		var fun = (new Function("cevent", actionToTake.txt));
		nd.onRowClick = fun;

	} else {
		var nd = dojo.byId(getPrepend(targetEle.name) + targetEle.id)
		var fun = (new Function("cevent", actionToTake.txt));
		dojo.connect(nd, action, fun);
	}

}
function getPrepend(type) {
	if (type == "Button")
		return "button_";
	if (type == "Link")
		return "link_";
	if (type == "Module")
		return "";// module is blank.
	if (type == "Image")
		return "img_";
	if (type == "TextArea")
		return "textarea_";
	if (type == "Graphics")
		return "textarea_";
	if (type == "Label")
		return "label_";
	if (type == "Animation")
		return "textarea_";
	if (type == "Input")
		return "";
	if (type == "Action")
		return "input_";
	if (type == "RawImage")
		return "img_";
	if (type == "CanvasImage")
		return "img_";

	if (type == "EventAnimation")
		return "";// event animation is pure animation

	return "";
}

function createEventAnimation(f, a, compname, isui, mx, my, configobj) {
	var code = null;
	var event = null;
	if (isui == true) {
		code = prompt('Enter Animation', '');
		event = prompt('Enter Event', 'TestEvent');
		var uid = prompt('Enter Uid', 'unique1');
		compDiag.push({
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"t" : 0,
			"code" : code,
			"event" : event
		});
	} else {
		code = configobj.code;
		event = configobj.event;
	}
	try {
		var fun = (getAnim(code));
		dojo.subscribe(event, null, fun);
	} catch (e) {
		alert(e.message || e);
	}
}
function removeCompFromGui(name) {
	var a = dojo.byId(name);
	if (a != null)
		dojo.destroy(a);
}

var ptdrag = function() {
	this.animate({
		"fill-opacity" : .2
	}, 500);
	this.ox = this.attr("cx");
	this.oy = this.attr("cy");
}, ptmove = function(dx, dy) {
	var ox = this.ox + dx;
	var oy = this.oy + dy;
	this.attr("cx", ox);
	this.attr("cy", oy);
}, ptup = function() {
	if (Math.abs(this.ox - this.attr("cx")) > 5
			|| Math.abs(this.oy - this.attr("cy")) > 5) {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var par = findComponentByName(this.node.eleid);
	var pts = dojo.fromJson(par.pts);
	var x = this.attr("cx");
	var y = this.attr("cy");
	pts[this.node.id] = {
		"x" : x,
		"y" : y,
		"id" : this.node.id
	};
	var str = dojo.toJson(pts);
	par.pts = str;
	var pathstr = getPathFromPoints(str);
	var path = pCanvas.path(pathstr);
	path.attr("stroke", "yellow");
	path.attr("fill", "orange");
	path.tx = 0;
	path.ty = 0;
	path.node.id = this.node.eleid
	path.drag(mymove, mydrag, myup);
	var shapestr = "var shape=pCanvas.path(\"" + pathstr + "\");\n";
	shapestr += "shape.attr(\"fill\",\"green\");";
	shapestr += "shape.attr(\"stroke\",\"red\");";
	par.design = shapestr;
	globals[this.node.eleid].remove();
	globals[this.node.eleid] = path;
	var shape = findComponentByName(this.node.eleid);
	var pts = dojo.fromJson(shape.pts);
	for ( var k in pts) {
		globals[pts[k].id].toFront();
	}
	} else {
		this.dblclick();
	}
}

/*******************************************************************************
 * 
 ******************************************************************************/
function removePoint(nd) {
	var name = nd.node.eleid;
	var ptname = nd.node.id;
	var pout = {};
	var shape = findComponentByName(name);
	if (shape) {

		if (shape.pts) {
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {
				if ([ pts[k].id ] == ptname) {
				} else {
					pout[pts[k].id] = pts[k];
				}
			}

			shape.pts = dojo.toJson(pout);
			var shapestr = "var shape=pCanvas.path(\""
					+ getPathFromPoints(shape.pts) + "\");\n";
			shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shape.design = shapestr;
			var pathstr = getPathFromPoints(shape.pts);
			var path = pCanvas.path(pathstr);
			path.attr("stroke", "pink");
			path.attr("fill", "silver");
			path.tx = 0;
			path.ty = 0;
			globals[name].remove();
			globals[name] = path;
			path.node.id = name;
			path.drag(mymove, mydrag, myup);
			globals[ptname].remove();
			globals[ptname] = null;
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {
				globals[pts[k].id].toFront();
			}
		}
	}
}
/*******************************************************************************
 * 
 ******************************************************************************/
function txShape(name, x, y) {
	var shape = findComponentByName(name);
	if (shape) {

		if (shape.pts) {
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {

				pts[k].x += x;
				pts[k].y += y;
				globals[pts[k].id].attr("cx", pts[k].x);
				globals[pts[k].id].attr("cy", pts[k].y);
				globals[pts[k].id].toFront();

			}
			shape.pts = dojo.toJson(pts);
			var shapestr = "var shape=pCanvas.path(\""
					+ getPathFromPoints(shape.pts) + "\");\n";
			shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shape.design = shapestr;
			var path = pCanvas.path(getPathFromPoints(shape.pts));
			path.attr("stroke", "yellow");
			path.attr("fill", "orange");
			path.node.id = name;
			path.tx = 0;
			path.ty = 0;
			path.drag(mymove, mydrag, myup);
			globals[name].remove();
			globals[name] = path;
		}
	}
}

/*******************************************************************************
 * create path from points
 ******************************************************************************/
function getPathFromPoints(ptsstr) {
	var pstr = "";
	var pts = dojo.fromJson(ptsstr);
	var started = false;
	for ( var i in pts) {
		var pt = pts[i];
		var ox = pt.x;
		var oy = pt.y;
		if (!started) {
			pstr += "M " + ox + " " + oy + " ";
			started = true;
		} else {
			pstr += " L " + ox + " " + oy;
		}
	}
	return pstr;
}
/*******************************************************************************
 * 
 ******************************************************************************/

function txShape(name, x, y) {
	var shape = findComponentByName(name);
	if (shape) {

		if (shape.pts) {
			var pts = dojo.fromJson(shape.pts);
			for ( var k in pts) {

				pts[k].x += x;
				pts[k].y += y;
				globals[pts[k].id].attr("cx", pts[k].x);
				globals[pts[k].id].attr("cy", pts[k].y);
				globals[pts[k].id].toFront();

			}
			shape.pts = dojo.toJson(pts);
			var shapestr = "var shape=pCanvas.path(\""
					+ getPathFromPoints(shape.pts) + "\");\n";
			shapestr += "shape.attr(\"fill\",\"green\");";
			shapestr += "shape.attr(\"stroke\",\"red\");";
			shape.design = shapestr;
			var path = pCanvas.path(getPathFromPoints(shape.pts));
			path.attr("stroke", "yellow");
			path.attr("fill", "orange");
			path.node.id = name;
			path.tx = 0;
			path.ty = 0;
			path.drag(mymove, mydrag, myup);
			globals[name].remove();
			globals[name] = path;
		}
	}
}

/*******************************************************************************
 * 
 ******************************************************************************/
function dist(x1, y1, x2, y2) {
	return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}

/*******************************************************************************
 * 
 ******************************************************************************/
var mydrag = function() {
	this.animate({
		"fill-opacity" : .2
	}, 500);
}, mymove = function(dx, dy) {

	var pt = this.getPointAtLength(1);
	var xamt = mouseX - pt.x;
	var yamt = mouseY - pt.y;
	if (this.tx) {
		this.tx += xamt;
	} else {
		this.tx = xamt;
	}

	if (this.ty) {
		this.ty += yamt;
	} else {
		this.ty = yamt;
	}

	this.translate(xamt, yamt);
	pCanvas.safari();
	var tr = this.attr("translation");
	setHintMessage(Math.ceil(xamt) + " : " + Math.ceil(yamt));
	var myid = this.node.id;
	var shape = findComponentByName(this.node.id);
	if (shape.pts) {
		var pts = dojo.fromJson(shape.pts);
		for ( var j in pts) {
			globals[pts[j].id].translate(xamt, yamt);
		}
	}
}, myup = function() {
	this.animate({
		"fill-opacity" : 1
	}, 500);
	var tr = this.attr("translation");
	var shape = findComponentByName(this.node.id);
	if (shape) {
		{
			if (shape.pts) {
				var pts = dojo.fromJson(shape.pts);
				for ( var k in pts) {
					pts[k].x += this.tx;
					pts[k].y += this.ty;
					globals[pts[k].id].attr("cx", pts[k].x);
					globals[pts[k].id].attr("cy", pts[k].y);
					globals[pts[k].id].toFront();
				}
				shape.pts = dojo.toJson(pts);
				this.tx = 0;
				this.ty = 0;
				var shapestr = "var shape=pCanvas.path(\""
						+ getPathFromPoints(shape.pts) + "\");\n";
				shapestr += "shape.attr(\"fill\",\"green\");";
				shapestr += "shape.attr(\"stroke\",\"red\");";
				shape.design = shapestr;
			}else{
				//translation is only used for non point pure svg shapes
				shape.tr = tr;
			}
		}

	}
};
/* 
 * anon def script
 */
function createAnonScriptModule(f, droppedon, compname, isui, mx, my, configobj) {
	var uid = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	var rotation = "0deg"; 
	var uidchanged = false;
	if(droppedon!=null && droppedon!="0"){
	console.log("Dropped on : "+droppedon.id);
	}
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		uid=uid+"(JavaCode)";
		cntn = prompt('Enter Content', '');
		rotation = prompt('Enter rotation', '0');
		rotation= rotation+"deg";
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : droppedon == null ? 0 : droppedon.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : (getZIndex()),
			"rotation":rotation,
			"script":""
		};
		compDiag.push(configobj);

	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		if(uid.indexOf("${id}")>=0){
			var tuid = prompt("Please enter a name");
			if(tuid!=null && tuid.length>0){
			uid = uid.replace(/\$\{id\}/g,tuid);
			cntn = cntn.replace(/\$\{id\}/g,tuid);
			cntn = cntn.replace(/\$\{id\}/g,tuid);
			configobj.id = uid;
			configobj.cntn = cntn;
			uidchanged = true;
			}
		}
		
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		if(configobj.rotation==null || configobj.rotation.length<1)
			rotation = "0deg";
		else
			rotation = configobj.rotation;
	}
	try {
		var a = getAnonScriptStaticModule(uid, l, t, w, h, cntn, zi,configobj.script)
		var target = new dojo.dnd.Source("bid_" + uid, {
			copyOnly : true,
			accept : [ "text", "treeNode" ],
			creator : widgetSourceCreator
		});
		setCSSRotation(uid,rotation);
		dojo.connect(target, "onDrop",
				function(source, nodes, copy) {
					if (lastDropModule != null) {
						var mod = lastDropModule.substring(4);
						findComponentByName(mod).cntn = dojo
								.byId(lastDropModule).innerHTML;
					}
				});
		htmlcomps[uid] = a;
		if(uidchanged){
		removeNodeById(uid);
		var obj = addModuleNodeObj(uid);
		obj.x = l-topWidth;
		obj.y = t;
		obj.r = w;
		obj.b =h
		obj.normalizedx = obj.x;
		obj.normalizedy = obj.y;
		obj.script = configobj.script;
		obj.val = configobj.cntn;
	    var pos = dojo.position(uid);
		var evtdata = {};
		evtdata.id = uid;
		evtdata.type="ssmmovestop";
		evtdata.pos = pos;
		sendEvent(geq, [evtdata]) ;
		preparePortable();
		syncModuleCode(uid);
		if(droppedon!=null && droppedon.type!=null && droppedon.type=="connection"){
			var origto = droppedon.to;
			droppedon.to=obj.id;
			droppedon.nodes[1]=obj.id
	        var drawing= findDrawEleByIdEx(droppedon.id);
	        if(drawing.item!=null)
	        drawing.item.remove();
	        if(drawing.textnode!=null)
	        drawing.textnode.remove();
	        for(var i=0;i<drawing.subs.length;i++){
	            drawing.subs[i].remove();
	        }
	        
	        createConnItem(obj.id,origto,getUniqId());
	        createConnItem(droppedon.from,obj.id,getUniqId());
      }else if(droppedon!=null && droppedon.type!=null && droppedon.type=="AnonDef"){
			createConnItem(droppedon.id,obj.id,getUniqId());
		}
		}else{
			removeNodeById(uid);
			var obj = addModuleNodeObj(uid);
			obj.script = configobj.script;
			draw();
		}
	} catch (e) {
		alert(e.message || e);
	}
}
/* 
 * anon def script
 */
function createCanvasModule(f, a, compname, isui, mx, my, configobj) {
	var uid = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	var rotation = "0deg"; 
		
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		rotation = prompt('Enter rotation', '0');
		rotation= rotation+"deg";
		var content = { } ;
		content.uid = uid;
		var html = doGetHtmlSyncWithContent(getURL("GetCanvas"),content);
		cntn=html;
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"zindex" : (getZIndex()),
			"rotation":rotation
		};
		compDiag.push(configobj);
	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		if(uid.indexOf("${id}")>=0){
			var tuid = prompt("Please enter a name");
			if(tuid!=null && tuid.length>0){
			uid = uid.replace(/\$\{id\}/g,tuid);
			cntn = cntn.replace(/\$\{id\}/g,tuid);
			configobj.id = uid;
			configobj.cntn = cntn;
			}
		}
		
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		if(configobj.rotation==null || configobj.rotation.length<1){
			rotation = "0deg";
		}else{
			rotation = configobj.rotation;
		}
	}
	try {
		var a = getStaticModule(uid, l, t, w, h, cntn, zi)
		var target = new dojo.dnd.Source("bid_" + uid, {
			copyOnly : true,
			accept : [ "text", "treeNode" ],
			creator : widgetSourceCreator
		});
		setCSSRotation(uid,rotation);
		dojo.connect(target, "onDrop",
				function(source, nodes, copy) {
					if (lastDropModule != null) {
						var mod = lastDropModule.substring(4);
						findComponentByName(mod).cntn = dojo.byId(lastDropModule).innerHTML;
					}
				});
		htmlcomps[uid] = a;		
		var cid = "canvas_"+uid;
		w= parseInt(w);
		h= parseInt(h);
		var canvas = Raphael(cid, w-10, h-20);
		canvas.rect(0,0,w-12,h-30,4);
		canvasModules[uid] = canvas;
	} catch (e) {
		alert(e.message || e);
	}
}
function createClassReMapping(f, a, compname, isui, mx, my, configobj,
		mappingtype, newname) {
	var uid = null;
	var cntn = null;
	var l = mx, t = my, w = "200px", h = "200px";
	var zi = null;
	var cls = null;
	var nname = null;
	if (isui == true) {
		uid = prompt('Enter uid', 'Module1');
		uid = uid+"(Remap)";
		cntn = "";
		if (newname) {
			cls = prompt('Enter New Name', '');
			nname = "org.ptg.proptest.Empty";
		} else {
			cls = prompt('Enter Class Name', '');
		}
		configobj = {
			"id" : uid,
			"name" : compname,
			"aid" : a == null ? 0 : a.id,
			"mouseX" : mx,
			"mouseY" : my,
			"cntn" : cntn,
			"left" : l,
			"top" : t,
			"width" : w,
			"height" : h,
			"cls" : cls,
			"zindex" : (getZIndex())
		};

		var req = {};
		if (newname) {
			req.classname = nname;
			req.newclassname = cls;

		} else {
			req.classname = cls;
		}
		req.id = uid;
		req.type = mappingtype;
		req.isremap="true";
		postFormWithContent("/site/GetClassLayout", req, function(res) {
			var a = getStaticModule(uid, l, t, w, h, res, zi);
			//dojo.byId("bid_" + uid).innerHTML = res;
			ddtreemenu.createTree("t_" + uid);
			// prepare portable again
			configobj.cntn = res;
			compDiag.push(configobj);
			preparePortable();
			convertNestedItemsToContainers();

		});

	} else {
		uid = configobj.id;
		cntn = configobj.cntn;
		l = configobj.left;
		t = configobj.top;
		w = configobj.width;
		h = configobj.height;
		zi = configobj.zindex;
		cls = configobj.cls;
		try {
			var a = getStaticModule(uid, l, t, w, h, cntn, zi);
/*			var target = new dojo.dnd.Source("bid_" + uid, {
				copyOnly : true,
				accept : [ "text", "treeNode" ],
				creator : widgetSourceCreator
			});
			var target = dojo.byId("bid_" + uid);
			dojo.connect(target, "onDrop", function(source, nodes, copy) {
				if (lastDropModuleGrp != null) {
					var mod = lastDropModuleGrp;
					findComponentByName(mod).cntn = dojo.byId("bid_"+ lastDropModuleGrp).innerHTML;
				}
			});
			*/
			htmlcomps[uid] = a;
			ddtreemenu.createTree("t_" + uid);
			preparePortable();
			convertNestedItemsToContainers()
			
		} catch (e) {
			console.log(e);
			alert(e.message || e);
		}
	}
}