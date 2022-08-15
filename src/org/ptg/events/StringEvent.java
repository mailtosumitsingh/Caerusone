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

public class StringEvent extends Event {
	private String stringItem;

	public StringEvent() {
		super();
	}

	public StringEvent(String stringItem) {
		super();
		this.stringItem = stringItem;
	}

	public String getStringItem() {
		return stringItem;
	}

	public void setStringItem(String stringItem) {
		this.stringItem = stringItem;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		stringItem = in.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeUTF(stringItem == null ? "" : stringItem);
	}

	@Override
	public String toString() {
		return stringItem;
	}

}
