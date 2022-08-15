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

public class VersionEvent extends Event {
	private String systemId;
	private int version;
	private String groupId;
	private long cascount;

	public VersionEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getCascount() {
		return cascount;
	}

	public void setCascount(long cascount) {
		this.cascount = cascount;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		systemId = in.readUTF();
		version = in.readInt();
		groupId = in.readUTF();
		cascount = in.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeUTF(systemId == null ? "" : systemId);
		out.writeInt(version);
		out.writeUTF(groupId == null ? "" : groupId);
		out.writeLong(cascount);

	}

}
