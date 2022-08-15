package org.ptg.util;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ptg.util.thread.ThreadManager;

import com.google.common.collect.ArrayListMultimap;

public class ScatterGather {
	public static final String SG_SEQ = "SG_SEQ";
	public static final String SG_GRP_ID = "SG_GRP_ID";
	protected AtomicLong seq = new AtomicLong();
	protected int sla = 5;
	ThreadManager mgr = (ThreadManager) SpringHelper.get("threadManager");
	ArrayListMultimap<String, MyRunnable> q = ArrayListMultimap.create();
	Map<String, Runnable> finishList = new LinkedHashMap<String,Runnable>();
	public void scatterGather(String grpId,Runnable[] runners,Runnable onDone) {
		finishList.put(grpId,onDone);
		for(Runnable run: runners){
		Long seqid = seq.incrementAndGet();
		MyRunnable r = new MyRunnable(grpId,seqid,run);
		synchronized (q) {
			q.put(grpId, r);
		}
		mgr.submit(r);
		}
	}
	public void allDone(String grpId){
	 Runnable r = finishList.remove(grpId);
	 if(r!=null){
		 mgr.submit(r);//async run all done notifier
	 }
	 }

	class MyRunnable implements Runnable {
		String grpId;
		long myid;
		Runnable wrapped;
		public MyRunnable(String grpId,long seqid,Runnable w) {
			this.grpId = grpId;
			myid = seqid;
			wrapped = w;
		}

		public void run() {
			boolean finished = false;
			synchronized (q) {
				if(wrapped!=null)
					wrapped.run();
				q.remove(this.grpId, this);
				List<MyRunnable> runnables = q.get(this.grpId);
				if (runnables != null) {
					if (runnables.size() == 0) {
						q.removeAll(this.grpId);
						finished = true; 
					}
				} else {
					 finished = true;
				}
			}
			if(finished){
				allDone(grpId);
			}
		}
		
	}
}
