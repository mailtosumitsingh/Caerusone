/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.events;

import org.ptg.util.CommonUtil;

public class CacheEvent extends org.ptg.events.Event {
	private String key;
	private Event evt;
	private int action;/* 1:add 2:update 3:delete */

	public CacheEvent(String key, Event evt, int action) {
		super();
		this.key = key;
		this.evt = evt;
		this.action = action;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Event getEvt() {
		return evt;
	}

	public void setEvt(Event evt) {
		this.evt = evt;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	@Override
	public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
		super.writeExternal(out);
		byte[] bts = CommonUtil.write(evt);
		out.writeInt(bts.length);
		out.write(bts);
		out.writeInt(this.action);
		out.writeUTF(org.ptg.util.CommonUtil.getNullString(key));
	}

	@Override
	public void readExternal(java.io.ObjectInput in) throws java.io.IOException, java.lang.ClassNotFoundException {
		super.readExternal(in);
		int len = in.readInt();
		byte[] bts = new byte[len];
		in.read(bts, 0, len);
		this.evt = (Event) CommonUtil.read(bts);
		this.action = in.readInt();
		this.key = in.readUTF();
	}

}
