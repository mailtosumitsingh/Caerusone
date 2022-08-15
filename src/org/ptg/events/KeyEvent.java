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

public class KeyEvent extends Event {
	String key;
	int version;

	public KeyEvent(String key, int version) {
		super();
		this.key = key;
		this.version = version;
	}

	public KeyEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		key = in.readUTF();
		version = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		super.writeExternal(out);
		out.writeUTF(key == null ? "" : key);
		out.writeInt(version);
	}

}
