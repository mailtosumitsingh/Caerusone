package org.ptg.util.compiletocodefunctions;

import org.ptg.util.CodeBlock;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public interface CodeGenHandler {
	void generateCode(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) ;
}
