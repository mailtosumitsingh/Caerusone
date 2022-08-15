/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.events;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.ptg.events.Event;
import org.ptg.util.CommonUtil;

public class ActivityEvent extends Event {
    protected String code ;
    protected String server;
    protected String title;
    protected String icon;
    
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		code = in.readUTF();
		server = in.readUTF();
		title = in.readUTF();
		icon = in.readUTF();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		if(code==null){
			out.writeUTF("");
		}else{
			out.writeUTF(code);
		}
		if(server==null){
			out.writeUTF("");
		}else{
			out.writeUTF(server);
		}
		if(title==null){
			out.writeUTF("");
		}else{
			out.writeUTF(title);
		}
		if(icon==null){
			out.writeUTF("");
		}else{
			out.writeUTF(icon);
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	

public static void main(String[] args) {
	CommonUtil.generateEventDefinitionFromEvent(new ActivityEvent());
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getIcon() {
	return icon;
}

public void setIcon(String icon) {
	this.icon = icon;
}
}
