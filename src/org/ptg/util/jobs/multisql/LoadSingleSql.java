/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs.multisql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.ptg.util.jobs.IStateAwareClosure;
import org.ptg.util.jobs.IntRange;

public class LoadSingleSql implements Runnable {
	static String rngs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	File fname;
	int batchsize;
	String sql;
	boolean parallel = true;
	IStateAwareClosure c;

	public LoadSingleSql(File fname, int batchsize, String sql, IStateAwareClosure c) {
		System.out.println("Splitter created: " + sql);
		System.out.println("Loading split keys from: " + fname);
		System.out.println("Will process keys in batch size: " + batchsize);
		this.fname = fname;
		this.batchsize = batchsize;
		this.sql = sql;
		this.c = c;

	}

	public void run() {

		List<Thread> ths = new ArrayList<Thread>();
		Map m = new TreeMap();
		try {
			Iterator iter = FileUtils.lineIterator(fname);
			int i = 0;
			while (iter.hasNext()) {
				String line = (String) iter.next();
				m.put(i, line);
				++i;

			}
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}

		int start = 0;
		int end = start + batchsize;

		do {
			Thread th = createJobThread(start, end, m, sql);
			ths.add(th);
			start = start + batchsize;
			end = end + batchsize;

			if (end == rngs.length()) {
				ths.add(createJobThread(start, start, m, sql));
			}
		} while (end < m.keySet().size());

		Iterator<Thread> iter = ths.iterator();
		while (iter.hasNext()) {
			Thread th2 = iter.next();
			if (parallel)
				th2.start();
			else
				th2.run();
		}
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}

	}

	private Thread createJobThread(int start, int end, Map m, String sql) {
		LoadSingleSqlJob job = new LoadSingleSqlJob(sql, c);
		IntRange r = new IntRange();
		r.setStart(start);
		r.setEnd(end);
		job.setRange(r);
		job.setMap(m);
		return new Thread(job);
	}
}
