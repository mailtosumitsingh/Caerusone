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

public class TraceEvent extends Event {
	private String stmt;
	private String origin;
	private String tracetype;

	public TraceEvent(String stmt, String origin, String type) {
		super();
		this.stmt = stmt;
		this.origin = origin;
		this.tracetype = type;
	}

	public TraceEvent() {
		super();
	}

	public String getStmt() {
		return stmt;
	}

	public void setStmt(String stmt) {
		this.stmt = stmt;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getTracetype() {
		return tracetype;
	}

	public void setTracetype(String tracetype) {
		this.tracetype = tracetype;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		stmt = in.readUTF();
		origin = in.readUTF();
		tracetype = in.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		if (stmt == null) {
			out.writeUTF("");
		} else {
			out.writeUTF(stmt);
		}
		if (origin == null) {
			out.writeUTF("");
		} else {
			out.writeUTF(origin);
		}
		if (tracetype == null) {
			out.writeUTF("");
		} else {
			out.writeUTF(tracetype);
		}

	}

}
