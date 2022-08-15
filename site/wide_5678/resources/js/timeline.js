/**
 */
var tindex = {};
var mnt = [ "Jan", "Feb", "Mar", "Apr", "May", "June", "Jul", "Aug", "Sept","Oct", "Nov", "Dec" ];
var datedItems={};

function initTimeLine(){
	drawTimeLineBackGround();
	drawTimeLine(30);
	drawTimeLineCursor();
}
function clearTimeLine(){
	datedItems={};
}
function drawTimeLineBackGround() {
	var r = pCanvas.rect(0, 0, 2000, 2000);
	r.attr("fill", "black");
	r.toBack();
}
function drawTimeLine(dateOff/*days to trackback*/) {
	tindex = {};
	var dl = pCanvas.line(10, 800, 2000, 800);
	dl.attr("stroke", "gray");
	dl.attr("opacity", ".5");
	var dt = new Date();
	var curr = new Date();
	dt.setDate(dt.getDate() - dateOff);
	var cnt = 0;
	var clr = "gray";
	for ( var i = 10; i < 1500; i += 10) {
		var off = 5;
		if (cnt % 10 == 0) {
			off = 10
		}
		var l = pCanvas.line(10 + i, 800 - off, 10 + i, 800);
		l.attr("opacity", .6);
		l.attr("stroke", "gray");
		var t = null;

		if (cnt % 2 == 0) {
			t = pCanvas.text(100 + i + 5, 786, dt.getDate());
			var titem = {};
			titem.x = 100+i+5;
			titem.y = 786;
			titem.text = dt.getDate();
			titem.drawable = t;
			var m = dt.getMonth();
			m++;
			if(m<10)
				m="0"+m;
			var d = dt.getDate();
			if(d<10)
				d="0"+d;
			var indx = (1900+dt.getYear())+m+""+d;
			console.log("tindex: "+indx)
			tindex[indx] = titem;
		} else {
			t = pCanvas.text(100 + i + 5, 810, dt.getDate());
			var titem = {};
			titem.x = 100+i+5;
			titem.y = 810;
			titem.text = dt.getDate();
			titem.drawable = t;
			var m = dt.getMonth();
			m++;
			if(m<10)
				m="0"+m;
			var d = dt.getDate();
			if(d<10)
				d="0"+d;
			var indx = (1900+dt.getYear())+m+""+d;
			tindex[indx] = titem;
			console.log("tindex: "+indx)
		}
		t.attr("stroke", clr);
		t.attr({
			font : '10px Fontin-Sans, Arial'
		});
		t.attr("text-anchor", "start");
		t.rotate(-70, 45);

		if (dt.getMonth() == curr.getMonth() && dt.getDate() == curr.getDate()) {
			if (cnt % 2 == 0) {
				var cir = pCanvas.circle(100 + i + 7, 774, 4);
				pCanvas.circle(100 + i + 7, 774, 7).attr("stroke", "red").attr(
						"opacity", .7);
			} else {
				var cir = pCanvas.circle(100 + i + 7, 812, 4);
				pCanvas.circle(100 + i + 7, 812, 7).attr("stroke", "red").attr(
						"opacity", .7);
			}
			cir.attr("fill", "orange");
			cir.attr("opacity", .6);
		}
		var ld = dt.getDate();
		var lm = dt.getMonth();
		dt.setDate(dt.getDate() + 1);

		if (ld > dt.getDate()) {
			clr = Raphael.getColor();
			var t = pCanvas.text(100 + i + 40, 825, mnt[lm + 1]);
			t.attr("stroke", clr);
			t.attr({
				font : '14px Fontin-Sans, helvetica'
			});
			t.attr("text-anchor", "start");
			var l = pCanvas.line(100 + i + 20, 810, 100 + i + 20, 840);
			l.attr("stroke", clr);
		}

		cnt++;
	}
}
function drawTimeLineCursor() {
	var instid = getUniqId();
	var c = getSimpleStaticModule(instid, 'simplestaticnode2', "20px", "800px",
			"100px", "50px", "", 200);
	dojo.byId('bid_' + instid).style.opacity = .1
	c.height = 20;
	var nd = new dojo.dnd.move.boxConstrainedMoveable(instid, {
		handle : "heading_" + instid,
		box :

		{
			l : 20,
			t : 800,
			w : 2000,
			h : 0
		}
	})
}

function drawTimeLineContent(cntn,dt) {
	var invert=false;
	var item=tindex[dt];

	if(item!=null){
	var bb2 = item.drawable.getBBox();
	var instid = getUniqId();
	bb2.width = 8;
	bb2.height = 8;
	//now do the actual drwaing of drawable
	var obj = {};
	obj.dated=dt;
	obj.id=instid;
	

	obj.width=100;
	obj.height=40;
	obj.cntn=cntn;
	//now add ot list of items
	var o = datedItems[obj.dated];
	if(o==null){
		o= new Array();
	}
	if(invert){
		if((Math.floor((o.length)/19)%2)>0){
			obj.x = o[(19-(o.length%18))].x;
		}else{
			obj.x = bb2.x +(o.length/18)*100;
		}
		obj.y=30 + (o.length%36)*42;//can compute y only here
	}else{
		obj.x = bb2.x +(o.length/18)*100;
		obj.y=30 + (o.length%18)*42;//can compute y only here
		
	}
	o.push(obj);
	datedItems[obj.dated]=o;
	//do drawing
	
	var c = getSimpleStaticModule(instid, 'simplestaticnode', (obj.x+"px"), obj.y+"px",obj.width+"px", obj.height+"px", cntn, 200);
	var bbx = dojo.position('bid_' + instid);
	console.log(bbx);
	var bb1 = {};
	bb1.x = bbx.x + bbx.w / 2, bb1.y = (bbx.y + bbx.h), bb1.width = 2,		bb1.height = 2;
	//now draw connection and save the object
	var a = pCanvas.connection(bb1, bb2);
	a.attr("stroke", Raphael.getColor());
	a.attr("opacity", ".5");
	a.attr("stroke-width", 4);
	a.attr("stroke-dasharray", "-");
	
	}
}