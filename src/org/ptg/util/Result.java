package org.ptg.util;

public class Result<T> {
int index;
public Result(int index, T res) {
	super();
	this.index = index;
	this.res = res;
}
T res;
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
public T getRes() {
	return res;
}
public void setRes(T res) {
	this.res = res;
}

}
