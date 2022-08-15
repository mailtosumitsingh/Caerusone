package org.ptg.util.expr;

import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public interface IInputExprProcessor {
	public org.ptg.util.functions.Expression process(FPGraph2 graph, FunctionPortObj s) ;
}
