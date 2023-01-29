package org.ptg.http2.handlers.compilers.graph;

import java.util.ArrayList;
import java.util.List;

public class VNode<T> {
	T self;
	List<T> children = new ArrayList<T>();

	public VNode(T t) {
		this.self = t;
	}
	
	public T getSelf() {
		return self;
	}

	public void setSelf(T self) {
		this.self = self;
	}

	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}

}
