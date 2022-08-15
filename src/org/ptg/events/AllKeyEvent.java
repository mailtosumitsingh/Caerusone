/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.events;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class AllKeyEvent extends Event {
	List<KeyEvent> list = new ArrayList<KeyEvent>();
	private String serverId;
	private String groupId;

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public AllKeyEvent() {

	}

	public AllKeyEvent(List<KeyEvent> list) {
		super();
		this.list = list;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		int length = in.readInt();
		for (int i = 0; i < length; i++) {
			KeyEvent key = new KeyEvent();
			key.readExternal(in);
			list.add(key);
		}
		serverId = in.readUTF();
		groupId = in.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(list.size());
		for (int i = 0; i < list.size(); i++) {
			list.get(i).writeExternal(out);
		}
		out.writeUTF(serverId == null ? "" : serverId);
		out.writeUTF(groupId == null ? "" : groupId);
	}

	public void add(KeyEvent v) {
		if (v != null) {
			list.add(v);
		}
	}

	public int size() {
		return list.size();
	}

	public List<KeyEvent> getItems() {
		return list;
	}
}
