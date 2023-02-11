package org.ptg.http2.handlers.compilers.graph.good;

import java.util.ArrayList;
import java.util.List;

import org.ptg.util.mapper.AnonDefObj;

public class VNode<T> extends AnonDefObj{
	T self;
	List<T> children = new ArrayList<T>();
	String condition;
	
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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
