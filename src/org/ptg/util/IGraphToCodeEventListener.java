package org.ptg.util;

import org.ptg.processors.ConnDef;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public interface IGraphToCodeEventListener {
	void setDebug(boolean isdebug);
	void init();
	/*called before calling a normal task ie task that is next sibling*/
	void handleNormalBlock(CodeBlock code, AnonDefObj curr) ;
	/*called before forking to, generally used to add open brace*/
	void handleForkBlockBegin(CodeBlock code, AnonDefObj curr);
	/*is used to add fork conditions such as if ,else, case or normal meaning executing next block*/
	void handleForkCondition(CodeBlock code, ConnDef cd,int fcount, int tcount,ForkType ftype,AnonDefObj curr, FPGraph2 o) ;
	/*called before closing the fork block, like fork block it puts close brace*/
	void handleForkFinish(CodeBlock code,String currName,boolean isLoopTarget, ForkType ftype,AnonDefObj curr) ;
	/*called before finishing the loop back target  , generally close brace is all needed*/
	void handleLoopBackTargetFinish(CodeBlock code,String currName, AnonDefObj curr) ;
	/*called before ending the child node proessing , generally a close brace is enough and is only called if calling
	 * anon has multiple childs*/
	void handleEndChild(CodeBlock code, AnonDefObj curr, PNode p) ;
	/*called to generate actual code generally it could be along jump or functionality
	if long jumpmcall continue orbreak properly other wise just do what is requried*/
	void handleGenerateCode(CodeBlock code, String currName,String breakOrContinue, boolean b, AnonDefObj curr, FPGraph2 o) ;
	/*is used to generate target code for loopbacks, while, for and explicit loops which are targeted i looopbakc only once
	direct code is generate otherwise and additional while is placed*/
	void handleLoopBackTarget(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) ;
	/*used to handle fork if target is loopback target an additional while is placed
	other wise switch is added*/
	void handleFork(CodeBlock code, String currName, boolean isloopBackTarget,ForkType ftype, AnonDefObj curr, ConnDef currCd, FPGraph2 o) ;
}
