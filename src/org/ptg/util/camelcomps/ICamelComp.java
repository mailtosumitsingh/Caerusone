package org.ptg.util.camelcomps;

import java.util.List;
import java.util.Map;

import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public interface ICamelComp {
	Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output,FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList );
	ICamelComp setInstId(String id);
	boolean canExec(String eleid);
	boolean wasSuccess();
	boolean wasFailure();
	boolean isRunning();
	String getName();
	void setName(String name);
	public String getConfigItems();
	public void setConfigItems(String s);
	public String getConfigOptions();
	public String getDoc();
	public ICamelComp setPersistable(boolean p);
	}
