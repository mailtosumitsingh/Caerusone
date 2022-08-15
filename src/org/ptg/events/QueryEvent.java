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

import org.apache.commons.lang.StringUtils;

public class QueryEvent extends Event {
	@Override
	public String toString() {
		return "QueryEvent [cascade=" + cascade + ", hint=" + hint + ", levels=" + levels + ", name=" + name + ", parallel=" + parallel + ", path=" + path + ", query=" + query + ", resType="
				+ resType + ", type=" + type + "]";
	}

	private String name;// key to whom result is to be mapped
	private String query; // query
	private String resType;// type of result
	private boolean parallel;
	private boolean cascade;
	private int levels;
	private String path;
	private String hint;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}

	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	public boolean isParallel() {
		return parallel;
	}

	public void setParallel(boolean parallel) {
		this.parallel = parallel;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	private int type = 1;/* 1:normal,2:unique,3:list */

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		name = in.readUTF();
		query = in.readUTF();
		type = in.readInt();
		resType = in.readUTF();
		parallel = in.readBoolean();
		cascade = in.readBoolean();
		levels = in.readInt();
		path = in.readUTF();
		hint = in.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeUTF(name == null ? "" : name);
		out.writeUTF(query == null ? "" : query);
		out.writeInt(type);
		out.writeUTF(resType == null ? "" : resType);
		out.writeBoolean(parallel);
		out.writeBoolean(cascade);
		out.writeInt(levels);
		out.writeUTF(path == null ? "" : path);
		out.writeUTF(hint == null ? "" : hint);
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String[] getPathEle() {
		if (path != null) {
			return StringUtils.split(path, "->");
		}
		return null;
	}

	public String addPathEle(String str) {
		if (path == null || path.length() == 0) {
			path = str;
		} else {
			path = path + "->" + str;
		}
		return path;
	}
}
