/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ptg.util.CommonUtil;

public class AppContext {
	Map<String, Object> vars = new ConcurrentHashMap<String, Object>();
	Map<String, AtomicLong> stats = new HashMap<String, AtomicLong>();

	public  void incrStat(String name) {

		AtomicLong i = stats.get(name);
		if (i == null) {
			i = new AtomicLong(0);
			i.incrementAndGet();
			stats.put(name, i);
		} else {
			i.incrementAndGet();
		}

	}
	public  void addStat(String name,long val) {

		AtomicLong i = stats.get(name);
		if (i == null) {
			i = new AtomicLong(0);
			i.set(val);
			stats.put(name, i);
		} else {
			i.addAndGet(val);
		}

	}
	public void setStat(String name, int val) {
		AtomicLong i = stats.get(name);
		if (i == null) {
			i = new AtomicLong(val);
			stats.put(name, i);
		} else {
			i.set(val);
		}

	}

	public boolean getBooleanVar(String name) {
		return (Boolean) getVar(name);
	}

	public String getStringVar(String name) {
		return (String) getVar(name);
	}

	public Long getLongVar(String name) {
		return (Long) getVar(name);
	}

	public void setVar(String name, Object obj) {
		vars.put(name, obj);
		if (obj != null) {
			CommonUtil.saveVar(name, obj);
		}
	}

	public void setBooleanVar(String name, Boolean obj) {
		setVar(name, obj);
	}

	public boolean checkLock(String name) {
		ReentrantReadWriteLock b = (ReentrantReadWriteLock) vars.get(name);
		if (b != null) {
			return b.isWriteLocked();
		} else
			return false;
	}

	public boolean freeLock(String name, boolean remove) {
		ReentrantReadWriteLock b = (ReentrantReadWriteLock) vars.get(name);
		if (b != null) {
			b.writeLock().unlock();
			if (remove) {
				vars.remove(name);
				b = null;
			}
			return true;
		} else {
			return false;
		}

	}

	public boolean setLock(String name) {
		ReentrantReadWriteLock b = (ReentrantReadWriteLock) vars.get(name);
		if (b != null) {
			if (b.isWriteLocked())
				return false;
			else {
				b.writeLock().lock();
				return true;
			}
		} else {
			b = new ReentrantReadWriteLock();
			b.writeLock().lock();
			vars.put(name, b);
			return true;
		}

	}

	public Object getVar(String name) {
		Object v = vars.get(name);
		if (v == null) {
			v = CommonUtil.getVar(name);
		}
		return v;
	}

	private static class SingletonHolder {
		private static final AppContext INSTANCE = new AppContext();
		static {
			INSTANCE.init();
		}
	}

	public Map<String, AtomicLong> getStats() {
		return stats;
	}

	public static AppContext getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void init() {
		Map<String, Serializable> vars = CommonUtil.getAllVars();
		if (vars != null) {
			for (Map.Entry<String, Serializable> en : vars.entrySet()) {
				System.out.print("Setting var: " + en.getKey() + " to " + en.getValue());
				setVar(en.getKey(), en.getValue());
			}
		}
	}
	public long getStat(String name) {

		AtomicLong i = stats.get(name);
		if (i == null) {
			return 0;
		} else {
			return i.get();
		}
	}
}
