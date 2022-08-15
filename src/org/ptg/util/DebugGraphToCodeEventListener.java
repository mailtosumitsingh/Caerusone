package org.ptg.util;

import org.ptg.processors.ConnDef;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class DebugGraphToCodeEventListener implements IGraphToCodeEventListener {
	boolean debug = false;
    //overriding should not have to do anything
	public void handleNormalBlock(CodeBlock code, AnonDefObj curr) {
		if (debug)
			code.append("/*handleNormalBlock*/",curr.getId());
	}
	//overriding should not have to do anything
	public void handleForkBlockBegin(CodeBlock code, AnonDefObj curr) {
		if (debug)
			code.append("/*handleForkBlockBegin*/",curr.getId());
		code.append("{",curr.getId());
	}

	//overriding should not have to do anything
	public void handleForkCondition(CodeBlock code, ConnDef cd, int fcount, int tcount, ForkType ftype, AnonDefObj curr,FPGraph2 o) {
		if (debug)
			code.append("/*handleForkCondition*/",curr.getId());
		if (ftype.equals(ForkType.If)) {
			handleIfFork(code, cd, fcount, tcount,curr);
		} else if (ftype.equals(ForkType.SWITCH)) {
			handleSwitchFork(code, cd, fcount, tcount,curr);
		}else if (ftype.equals(ForkType.UNK)) {
			handleUnkFork(code, cd, fcount, tcount,curr);
		}
	}
	//overriding should not have to do anything
	private void handleSwitchFork(CodeBlock code, ConnDef cd, int fcount, int tcount,AnonDefObj curr) {
		if (debug)
			code.append("/*handleSwitchFork*/",curr.getId());
		code.append(" case " + cd.getConnCond() + " : ",curr.getId());

	}
	//overriding should not have to do anything
	private void handleUnkFork(CodeBlock code, ConnDef cd, int fcount, int tcount,AnonDefObj curr) {
		if (debug)
			code.append("/*handleUnkFork*//*unknown fork type*/",curr.getId());

	}
	//overriding should not have to do anything
	private void handleIfFork(CodeBlock code, ConnDef cd, int fcount, int tcount,AnonDefObj curr) {
		if (debug)
			code.append("/*handleIfFork*/",curr.getId());
		if (fcount == 0) {
			code.append(" if(cond" + CommonUtil.convertToJavaId(cd.getFrom()) + "==" + cd.getConnCond() + ")",curr.getId());
		} else {
			if (tcount == 2) {
				code.append("else",curr.getId());
			} else {
				if (fcount == tcount - 1) {
					if (cd.getConnCond() == null || cd.getConnCond().length() < 1) {
						code.append("else ",curr.getId());
					} else {
						code.append("else if(" + cd.getConnCond() + ")",curr.getId());
					}
				} else {
					code.append("else if(" + cd.getConnCond() + ")",curr.getId());
				}
			}
		}
	}
	//overriding should not have to do anything
	public void handleForkFinish(CodeBlock code, String currName, boolean isLoopTarget, ForkType ftype, AnonDefObj curr) {
		if (debug)
			code.append("/*handleForkFinish*/",curr.getId());
		if (isLoopTarget) {
			//code.append("cond" + currName + " = false;");
		}
		code.append("",curr.getId());
		code.append("}",curr.getId());
	}
	//overriding should not have to do anything
	public void handleLoopBackTargetFinish(CodeBlock code, String currName, AnonDefObj curr) {
		if (debug)
			code.append("/*handleLoopBackTargetFinish*/",curr.getId());
		// code.append("cond"+currName+" = false;");
		code.append("",curr.getId());
		code.append("}",curr.getId());

	}
	//overriding should not have to do anything
	public void handleEndChild(CodeBlock code, AnonDefObj curr,PNode p) {
		if (debug){
			code.append("/*handleEndChild*/",curr.getId());
		}
		code.append("",curr.getId());
		code.append("}",curr.getId());
	}
	//@@override this for the handle generate code
	public void handleGenerateCode(CodeBlock code, String currName, String breakOrContinue, boolean isLongJump, AnonDefObj curr, FPGraph2 o) {
		if (debug)
			code.append("/*handleGenerateCode*/",curr.getId());
		if (isLongJump) {
			if (breakOrContinue == null)
				breakOrContinue = "continue";
			code.append(breakOrContinue + " " + CommonUtil.convertToJavaId(currName)+ ";",curr.getId());
		} else {
			if (curr != null && (curr.getAnonType() != null && curr.getAnonType().equalsIgnoreCase("switch"))) {
				// do something here???
				// when switch donot print anything
			} else {
				if(curr!=null)
				code.append("System.out.println(\"" + currName + "\");",curr.getId());
			}
		}
	}
	//@@override this since there is a specialized implementation for the loop types
	public void handleLoopBackTarget(CodeBlock code, String currName, AnonDefObj curr,FPGraph2 o) {
		if (debug){
			code.append("/*handleLoopBackTarget*/",curr.getId());
		}
		code.append("cond" + CommonUtil.convertToJavaId(currName) + " = true;",curr.getId());
		code.append(currName + ":",curr.getId());
		if (curr != null) {
			if (curr.getAnonType().equals("for") || curr.getAnonType().equals("while") || curr.getAnonType().equals("foreach") || curr.getAnonType().equals("for")) {
				// code.append(curr.getAnonType()+" (cond" + currName +
				// "==true){" + "\n");
				code.append(curr.getAnonType() + " (int i=0;i<10;i++){" ,curr.getId());
				code.append("System.out.println(\" i is now: \"+i);",curr.getId());
			} else {
				code.append("while(cond" + CommonUtil.convertToJavaId(currName) + "==true){" ,curr.getId());
			}
		} else {
			code.append("while(cond" + CommonUtil.convertToJavaId(currName) + "==true){" ,curr.getId());
		}
	}
	//overriding should not have to do anything
	public void handleFork(CodeBlock code, String currName, boolean isloopBackTarget, ForkType ftype, AnonDefObj curr, ConnDef currCd,FPGraph2 o) {
		if (debug)
			code.append("/*handleFork*/",curr.getId());
			/*sumit this is not correct
		 * 
		 * if (isloopBackTarget) {
			code.append("while(cond" + currName + "==true)");
		}*/
		if (ftype != null && ftype.equals(ForkType.SWITCH)) {
			if (currCd == null) {
				code.append("switch (" + "cond" + CommonUtil.convertToJavaId(currName) + ")",curr.getId());
			} else {
				System.out.println("This has no parent???");
				code.append("switch (" + currCd.getConnCond() + ")",curr.getId());
			}
		}else{
			code.append("cond" + CommonUtil.convertToJavaId(currName) + " = true;",curr.getId());
			code.append(CommonUtil.convertToJavaId(currName) + ":",curr.getId());
		}
		code.append("{",curr.getId());
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}

