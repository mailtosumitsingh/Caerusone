package org.ptg.util;

import java.util.concurrent.Callable;

public class CallWrapper<T> implements Callable<Result>{
	Callable<T> e;
	int index;
	
	public CallWrapper(Callable<T> e, int index) {
		this.e = e;
		this.index = index;
	}

	public Callable getE() {
		return e;
	}

	public void setE(Callable e) {
		this.e = e;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public Result<T> call() throws Exception {
		T o = e.call();
		return new Result<T>(index,o);
	}
	
	

}
