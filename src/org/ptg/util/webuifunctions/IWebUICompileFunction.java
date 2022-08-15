package org.ptg.util.webuifunctions;

import java.util.List;

import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public interface IWebUICompileFunction {
Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output,FPGraph2 graph);
}
