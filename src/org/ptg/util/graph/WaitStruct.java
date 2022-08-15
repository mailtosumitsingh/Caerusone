package org.ptg.util.graph;

import java.util.HashSet;
import java.util.Set;

public class WaitStruct {
	//sumit: todo make threadsafe;
	Set<String> wait = new HashSet<String>();
	Set<String> done = new HashSet<String>();
	public Set<String> getWait() {
		return wait;
	}
	public void setWait(Set<String> wait) {
		this.wait = wait;
	}
	public Set<String> getDone() {
		return done;
	}
	public void setDone(Set<String> done) {
		this.done = done;
	}
	public void done(String s){
		done.add(s);
		wait.remove(s);
	}
	public void addWait(String s){
		done.remove(s);
		wait.add(s);
	}
	public boolean isWaitingOn(String mayBe){
		return wait.contains(mayBe);
	}
	public boolean isNotWaitingOn(String mayBe){
		return done.contains(mayBe);
	}
	public boolean isReady(){
		return wait.size()==0;
	}
}
