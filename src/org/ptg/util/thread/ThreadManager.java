/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.Closure;


public class ThreadManager {
private BlockingQueue<Runnable> jobs = new LinkedBlockingQueue<Runnable>();
private ThreadPoolExecutor exec = new ThreadPoolExecutor(10,100, 60*1000, TimeUnit.MILLISECONDS, jobs);
private Map<String, Closure> executors = new HashMap<String,Closure>();

public void submit(Runnable run){
	exec.submit(run);
}

private class ThreadWrapper implements Runnable{
	private Runnable r;
	
	public ThreadWrapper(Runnable r) {
		super();
		this.r = r;
	}

	public void run() {
		 r.run();
	}
	
}
}
