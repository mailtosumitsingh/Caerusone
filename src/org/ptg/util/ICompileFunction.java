package org.ptg.util;

import java.util.List;
import java.util.Map;

import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public interface ICompileFunction {
	Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph);
	String getInitCode(AnonDefObj anon);
	String getGlobalVar(AnonDefObj anon);
	String getInitVarCode(AnonDefObj anon);
	String getRHSSetterFunction();
	boolean isObjectMapper();
	void isObjectMapper(boolean b);
	void setContext(Map<String,String> ctxMap);
}
