/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

function enableDropBox() {
	var dropbox =  dojo.byId("dropbox");
	dojo.connect(dropbox , "dragenter", dragenter);
	dojo.connect(dropbox , "dragover", dragover);
	dojo.connect(dropbox , "drop", drop);
}
function dragenter(e) {
	e.stopPropagation();
	e.preventDefault();
}

function dragover(e) {
	e.stopPropagation();
	e.preventDefault();
}
function drop(e) {
	e.stopPropagation();
	e.preventDefault();

	var dt = e.dataTransfer;
	var files = dt.files;
    var len  = files.length;
    if(len>0)postFiles(files);
}

function handleFiles(files){
	var file = files[0];
	var lbl = dojo.byId("dropLabel");
	if(lbl!=null){
		lbl.innerHTML = "Processing: "+file.name;
	}
	var reader = new FileReader();
	reader.onload = handleReaderLoad;
	reader.readAsDataURL(file);
}
function handleReaderLoad(evt) {
	  var img = document.getElementById("preview");
	  if(img!=null){
	  img.src = evt.target.result;
	  }
	}

function postFiles(files){
	var formData = new FormData();
	for( var i=0; i<files.length; i++ ){
		formData.append('File',files[i]);
		formData.append("deployName",files[i].name);
		formData.append("deployType","in");
		var lbl = dojo.byId("dropLabel");
		if(lbl!=null){
			lbl.innerHTML = "Processing: "+files[i].name;
		}
	}
	var xhr = new XMLHttpRequest();
	xhr.open("POST",'/SaveDeploy');
	xhr.onload = function (){
		if(xhr.status==200){
			console.log(xhr.responseText);
		}else{
			console.log("Error while sending file to server.");
		}
	};
    xhr.send(formData);
	
	
}
function dropBox_init(){
	enableDropBox();
}