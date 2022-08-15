package org.ptg.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.StepObj;

public class DefaultTaskExecEventListener implements ITaskExecEventListener{
	Map<Integer, Set<String>> _gByLevel;
	Map<String, StepObj> steps;
	Map<String, Set<String>> _tasksBySteps;
	Set<String> doneList= new HashSet<String>();
	List<String> frames = new LinkedList<String>();
	public void setGraphLevels(String uuid, Map<Integer, Set<PNode>> gByLevel) {
		this._gByLevel = new LinkedHashMap<Integer,Set<String>>();
		for(Map.Entry<Integer, Set<PNode>> s : gByLevel.entrySet()){
			Set<String> a = _gByLevel.get(s.getKey());
			if(a==null){
				a= new LinkedHashSet<String>();
				_gByLevel.put(s.getKey(),a);
			}
			for(PNode n : s.getValue()){
				a.add(n.getName());
			}
		}
	}
	

	public void beforeProcessStart(String uuid) {
		
	}

	public void afterProcessStart(String uuid) {
		System.out.println(frames);
																																																																											}

	public void setGraphSteps(String uuid, Map<String, StepObj> steps,Map<String, Set<AnonDefObj>> tasksBySteps) {
		this.steps = steps;
		this._tasksBySteps = new LinkedHashMap<String,Set<String>>();
		for(Map.Entry<String, Set<AnonDefObj>> s : tasksBySteps.entrySet()){
			Set<String> a = _tasksBySteps.get(s.getKey());
			if(a==null){
				a= new LinkedHashSet<String>();
				_tasksBySteps.put(s.getKey(),a);
			}
			for(AnonDefObj n : s.getValue()){
				a.add(n.getId());
			}
		}
		
	}

	public void taskExecuted(String puuid, AnonDefObj obj) {
		doneList.add(obj.getId());
		frames.add("T>>>"+obj.getId());
		fileDoneListChanged(obj.getId());
	}

	private void fileDoneListChanged(String id) {
		//fire levels
		computeLevelsDone(id);
		//fire steps
		computeStepsDone(id);
		
	}


	private void computeStepsDone(String id) {
		for(Map.Entry<String, Set<String>> a:_tasksBySteps.entrySet()){
			if(a.getValue().contains(id)){
				boolean done = true;
				for(String s: a.getValue()){
					if(!doneList.contains(s)){
						done = false;
					}
				}
				if(done){
					fireStepDone(a.getKey(),id);
				}
			}
		}
	}


	private void computeLevelsDone(String id) {
		for(Map.Entry<Integer, Set<String>> a:_gByLevel.entrySet()){
			if(a.getValue().contains(id)){
				boolean done = true;
				for(String s: a.getValue()){
					if(!doneList.contains(s)){
						done = false;
					}
				}
				if(done){
					fireLevelDone(a.getKey(),id);
				}
			}
		}
	}


	private void fireStepDone(String key, String id) {
		System.out.println("Graph Step done: "+steps.get(key).getText()+" Because of "+id);
		frames.add("Step: "+steps.get(key).getText()+">>>"+id);
	}


	private void fireLevelDone(Integer key, String id) {
		System.out.println("Graph level done: "+key +" Because of "+id);
		frames.add("Level: "+key+">>>"+key);
	}


}
