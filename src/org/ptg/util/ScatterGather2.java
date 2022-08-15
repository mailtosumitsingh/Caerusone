package org.ptg.util;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScatterGather2 {
	private static BlockingQueue<Runnable> jobs = new LinkedBlockingQueue<Runnable>();
	private static ThreadPoolExecutor exec = new ThreadPoolExecutor(10,100, 60*1000, TimeUnit.MILLISECONDS, jobs);
	public Object [] scatterGather(Callable[] runners) {
		ExecutorCompletionService ecs  = new ExecutorCompletionService(exec);
        Object [] result = new Object[runners.length];
        int index = 0;
        for(Callable s: runners){
        	result[index] = null;
        	CallWrapper c = new CallWrapper(runners[index], index);
			ecs.submit(c);
			++index;
        }
    int n = runners.length;
    for (int i = 0; i < n; ++i) {
        Object r;
		try {
			r = ecs.take().get();
	        Result res = (Result) r;
	        if (r != null) {
	            result[res.getIndex()] = res.getRes();
	        }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    return result;
	}
}
