package org.ptg.util;

import java.util.Map;
import java.util.Set;

import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.StepObj;

public interface ITaskExecEventListener {
	void setGraphLevels(String uuid, Map<Integer, Set<PNode>> gByLevel) ;
	void beforeProcessStart(String uuid) ;
	void afterProcessStart(String uuid) ;
	void setGraphSteps(String uuid, Map<String, StepObj> steps, Map<String, Set<AnonDefObj>> tasksBySteps) ;
	void taskExecuted(String puuid, AnonDefObj obj) ;

}
