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

public class BinaryEvent extends Event {
	private byte[] dataBytes;

	public BinaryEvent() {
		super();
	}

	public BinaryEvent(byte[] stringItem) {
		super();
		this.dataBytes = stringItem;
	}

	public byte[] getBytes() {
		return dataBytes;
	}

	public void setBytes(byte[] bytes) {
		this.dataBytes = bytes;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		int len = in.readInt();
		dataBytes = new byte[len];
		in.read(dataBytes);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(dataBytes.length);
		out.write(dataBytes);

	}

	@Override
	public String toString() {
		return "ByteEvent [bytelength=" + dataBytes.length + "]";
	}

}
