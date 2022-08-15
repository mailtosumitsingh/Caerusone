package org.ptg.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.compiletocodefunctions.CodeGenHandler;
import org.ptg.util.compiletocodefunctions.JavaCodeDirectSubstitute;
import org.ptg.util.compiletocodefunctions.JavaMethodCall;
import org.ptg.util.compiletocodefunctions.JavaVizCall;
import org.ptg.util.compiletocodefunctions.Remap;
import org.ptg.util.compiletocodefunctions.SVar;
import org.ptg.util.compiletocodefunctions.Subgraph;
import org.ptg.util.compiletocodefunctions.Var;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class JavaGraphToCodeEventListener implements IGraphToCodeEventListener {
	boolean debug = true;

	Map<String, Integer> labels = new HashMap<String, Integer>();
	Pattern exprPart = Pattern.compile("[a-zA-Z0-9_]+\\.expr(\\(([a-zA-Z0-9_\\,\\[\\]\\(\\)\\.]+)\\))");
	Map<String, CodeGenHandler> handlers = new HashMap<String, CodeGenHandler>();

	public JavaGraphToCodeEventListener() {
		handlers.put("svar", new SVar());
		handlers.put("var", new Var());
		handlers.put("MethodCall", new JavaMethodCall());
		handlers.put("JavaCode", new JavaCodeDirectSubstitute());
		handlers.put("Remap", new Remap());
		handlers.put("SubGraph", new Subgraph());
		handlers.put("Viz", new JavaVizCall());

	}

	// overriding should not have to do anything
	@Override
	public void handleNormalBlock(CodeBlock code, AnonDefObj curr) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleNormalBlock*/", id);
		}
		code.addCodeLine("", id);
	}

	// overriding should not have to do anything
	@Override
	public void handleForkBlockBegin(CodeBlock code, AnonDefObj curr) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleForkBlockBegin*/", id);
		}
		code.addCodeLine("{", id);
	}

	private String getCurrId(AnonDefObj curr) {
		String id = null;
		if (curr != null) {
			id = curr.getId();
		}
		if (id == null) {
			id = "";
		}
		return id;
	}

	// overriding should not have to do anything
	@Override
	public void handleForkCondition(CodeBlock code, ConnDef cd, int fcount, int tcount, ForkType ftype, AnonDefObj curr, FPGraph2 o) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleForkCondition*/", id);
		}
		if (ftype.equals(ForkType.If)) {
			handleIfFork(code, cd, fcount, tcount, curr, o);
		} else if (ftype.equals(ForkType.SWITCH)) {
			handleSwitchFork(code, cd, fcount, tcount, curr);
		} else if (ftype.equals(ForkType.UNK)) {
			handleUnkFork(code, cd, fcount, tcount, curr, o);
		}
	}

	// overriding should not have to do anything
	private void handleSwitchFork(CodeBlock code, ConnDef cd, int fcount, int tcount, AnonDefObj curr) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleSwitchFork*/", id);
		}
		code.addCodeLine(" case " + cd.getConnCond() + " : ", id);

	}

	// overriding should not have to do anything
	private void handleUnkFork(CodeBlock code, ConnDef cd, int fcount, int tcount, AnonDefObj curr, FPGraph2 o) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleUnkFork*//*unknown fork type*/", id);
		}
	}

	// overriding should not have to do anything
	private void handleIfFork(CodeBlock code, ConnDef cd, int fcount, int tcount, AnonDefObj curr, FPGraph2 o) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleIfFork*/", id);
		}
		if (fcount == 0) {
			String initVal = getIfConditionValue(cd, curr, o);
			// code.addCodeLine(" if(cond" +
			// CommonUtil.convertToJavaId(cd.getFrom()) + "==" + initVal +
			// ")",id);
			code.addCodeLine(" if(" + initVal + ")", id);
		} else {
			if (tcount == 2) {
				code.addCodeLine("else", id);
			} else {
				if (fcount == tcount - 1) {
					if (cd.getConnCond() == null || cd.getConnCond().length() < 1) {
						code.addCodeLine("else ", id);
					} else {
						code.addCodeLine("else if(" + cd.getConnCond() + ")", id);
					}
				} else {
					code.addCodeLine("else if(" + cd.getConnCond() + ")", id);
				}
			}
		}
	}

	/* returns the value that should go in the if codition by default */
	public String getIfConditionValue(ConnDef cd, AnonDefObj curr, FPGraph2 o) {
		if (cd == null) {
			return "true";
		}
		String initVal = cd.getConnCond() == null || cd.getConnCond().length() < 1 ? "true" : cd.getConnCond();
		if (curr.getAux() != null && curr.getAux().size() > 0) {
			String ifCondPort = "aux_" + curr.getId() + "." + curr.getAux().get(0);
			PortObj ifcondPort = o.getPorts().get(ifCondPort);
			if (ifcondPort != null) {
				String temp = ifcondPort.getPortval();
				if (temp != null && temp.length() > 0) {
					initVal = temp;
				} else {
					Map<String, ConnDef> obj = o.getForward();
					ConnDef cdPort = null;
					for (Map.Entry<String, ConnDef> en : obj.entrySet()) {
						if (en.getValue().getTo().equals(ifcondPort.getId())) {
							cdPort = en.getValue();
							break;
						}
					}
					if (cdPort != null) {
						String portFrom = cdPort.getFrom();
						if (portFrom != null && portFrom.length() > 0) {
							PortObj fromPort = o.getPorts().get(portFrom);
							if (fromPort != null) {
								if (!fromPort.getPorttype().equalsIgnoreCase("arbitport")) {
									String portVal = fromPort.getPortval();
									if (portVal != null && portVal.length() > 0) {
										initVal = portVal;
									}
								} else {

								}
							} else {
								FunctionPoint fo = o.getFunctionPoints().get(portFrom);
								if (fo != null) {
									if (fo.getVal() != null && fo.getVal().length() > 0) {
										initVal = fo.getVal();
									} else {
										System.out.println("Error in mapping switch");
									}
								} else {
									System.out.println("Error in mapping switch");
								}
							}
						} else {
							System.out.println("Error in mapping switch");
						}
					}
				}
			}
		}
		return initVal;
	}

	// overriding should not have to do anything
	@Override
	public void handleForkFinish(CodeBlock code, String currName, boolean isLoopTarget, ForkType ftype, AnonDefObj curr) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleForkFinish*/", id);
		}
		if (isLoopTarget) {
			// code.addCodeLine("cond" + currName + " = false;");
		}
		code.addCodeLine("", id);
		code.addCodeLine("}", id);
		code.addCodeLine("", id);
	}

	// overriding should not have to do anything
	@Override
	public void handleLoopBackTargetFinish(CodeBlock code, String currName, AnonDefObj curr) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleLoopBackTargetFinish*/", id);
		}
		// code.addCodeLine("cond"+currName+" = false;");
		code.addCodeLine("", id);
		code.addCodeLine("}", id);
		code.addCodeLine("", id);
	}

	// overriding should not have to do anything
	@Override
	public void handleEndChild(CodeBlock code, AnonDefObj curr, PNode p) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleEndChild*/", id);
		}
		code.addCodeLine("", id);
		if (curr != null && curr.getAnonType().equalsIgnoreCase("switch")) {
			code.addCodeLine("break;", id);
		}
		code.addCodeLine("}", id);
		code.addCodeLine("", id);
	}

	// @@override this for the handle generate code
	@Override
	public void handleGenerateCode(CodeBlock code, String currName, String breakOrContinue, boolean isLongJump, AnonDefObj curr, FPGraph2 o) {
		String id = getCurrId(curr);
		if (debug) {
			code.addCodeLine("/*handleGenerateCode*/", id);
		}
		if (isLongJump) {
			if (breakOrContinue == null) {
				breakOrContinue = "continue";
			}
			code.addCodeLine(breakOrContinue + " " + getBreakLabel(currName) + ";", id);
		} else {
			if (curr != null && curr.getAnonType() != null && curr.getAnonType().equalsIgnoreCase("switch")) {
				// do something here??? when switch donot print anything
			} else if (curr != null && curr.getAnonType() != null && handlers.get(curr.getAnonType()) != null) {
				CodeGenHandler obj = handlers.get(curr.getAnonType());
				obj.generateCode(code, currName, curr, o);
			} else {
				org.ptg.util.functions.Expression exp = null;
				if (curr != null && curr.getAux() != null && curr.getAux().size() > 0) {
					exp = CommonUtil.getMyInExpression(o, curr, curr.getAux().get(0), "java");
				}
				if (exp != null) {
					code.addCodeLine("System.out.println(\"" + exp.getVal() + "\");", currName);
				} else {
					// code.addCodeLine("System.out.println(\"" + currName +
					// "\");",currName);
				}

				if (curr != null && curr.getOutputs() != null && curr.getOutputs().size() > 0) {
					for (String s : curr.getOutputs()) {
						exp = CommonUtil.getMyOutExpression(o, curr, s, "java");
						if (exp != null) {
							String temp = exp.getVal();
							temp = StringUtils.replace(temp, "{1}", currName);
							PortObj po = o.getPorts().get("out_" + curr.getId() + "." + s);
							if (po != null) {
								if (curr.getAnonType().equals("db")) {
									String pid = StringUtils.substringAfterLast(po.getName(), curr.getId() + "_");
									String tname = "rs" + ".get(\"" + pid + "\")";
									temp = exp.getVal();
									temp = StringUtils.replace(temp, "{1}", tname);
									temp += ";";
									code.addCodeLine(temp, temp);
								}
							} else {
								code.addCodeLine(temp, currName);
							}
						}
					}
				}

			}
		}
	}

	// @@override this since there is a specialized implementation for the loop
	// types
	@Override
	public void handleLoopBackTarget(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		if (debug) {
			code.addCodeLine("/*handleLoopBackTarget*/", curr.getId());
		}
		code.addCodeLine("cond" + CommonUtil.convertToJavaId(currName) + " = true;", curr.getId());
		code.addCodeLine(getStmtLabel(currName) + ":", curr.getId());
		if (curr != null) {
			if (curr.getAnonType().equals("for")) {
				handleFor(code, currName, curr, o);
				code.addCodeLine("{", curr.getId());
			} else if (curr.getAnonType().equals("while")) {
				handleWhile(code, currName, curr, o);
				code.addCodeLine("{", curr.getId());
			} else if (curr.getAnonType().equals("foreach")) {
				handleForEach(code, currName, curr, o);
				code.addCodeLine("{", curr.getId());
			} else {
				code.addCodeLine("while(cond" + CommonUtil.convertToJavaId(currName) + "==true){", curr.getId());
			}
		} else {
			code.addCodeLine("while(cond" + CommonUtil.convertToJavaId(currName) + "==true){", curr.getId());
		}
	}

	public void handleFor(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		code.addCodeLineSameLine(" for ", curr.getId());
		String varName = "i";
		String varType = "int";
		String varInitVal = "0";
		String breakCond = null;
		String incr = null;
		{
			String vtypePort = "aux_" + curr.getId() + "." + curr.getAux().get(0);
			String vnamePort = "aux_" + curr.getId() + "." + curr.getAux().get(1);
			String initValPort = "aux_" + curr.getId() + "." + curr.getAux().get(2);
			String nextValPort = "aux_" + curr.getId() + "." + curr.getAux().get(3);
			String testValPort = "aux_" + curr.getId() + "." + curr.getAux().get(4);
			varType = o.getPorts().get(vtypePort).getPortval();
			varName = o.getPorts().get(vnamePort).getPortval();
			varInitVal = o.getPorts().get(initValPort).getPortval();
			incr = o.getPorts().get(nextValPort).getPortval();
			breakCond = o.getPorts().get(testValPort).getPortval();
		}
		if (breakCond == null) {
			breakCond = varName + "<1";
		}
		if (incr == null) {
			incr = varName + "++";
		}
		code.addCodeLineSameLine("(", curr.getId());
		code.addCodeLineSameLine(varType + " " + varName + " = " + varInitVal, curr.getId());
		code.addCodeLineSameLine(";", curr.getId());
		code.addCodeLineSameLine(breakCond, curr.getId());
		code.addCodeLineSameLine(";", curr.getId());
		code.addCodeLineSameLine(incr, curr.getId());
		code.addCodeLineSameLine(")", curr.getId());
	}

	public void handleWhile(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		code.addCodeLineSameLine(" while ", curr.getId());
		String breakCond = null;
		String incr = null;
		{
			String testValPort = "aux_" + curr.getId() + "." + curr.getAux().get(0);
			breakCond = o.getPorts().get(testValPort).getPortval();
		}
		if (breakCond == null) {
			breakCond = "var1" + "<1";
		}
		code.addCodeLineSameLine("(", curr.getId());
		code.addCodeLineSameLine(breakCond, curr.getId());
		code.addCodeLineSameLine(")", curr.getId());
	}

	public void handleForEach(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		code.addCodeLineSameLine(" for ", curr.getId());
		String varName = "i";
		String varType = "int";
		String varInitVal = "0";
		{
			String vtypePort = "aux_" + curr.getId() + "." + curr.getAux().get(0);
			String vnamePort = "aux_" + curr.getId() + "." + curr.getAux().get(1);
			org.ptg.util.functions.Expression exp = CommonUtil.getMyInExpression(o, curr, curr.getAux().get(2), "java");
			String initValPort = "aux_" + curr.getId() + "." + curr.getAux().get(2);
			varType = o.getPorts().get(vtypePort).getPortval();
			varName = o.getPorts().get(vnamePort).getPortval();
			varInitVal = o.getPorts().get(initValPort).getPortval();
			if (exp != null) {
				if (exp.getVal() != null && exp.getVal().length() > 0) {
					varInitVal = exp.getVal();
				}
			} else {
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getTo().equals(initValPort)) {
						FunctionPoint fpp = o.getFunctionPoints().get(cd.getFrom());
						if (fpp != null) {
							varInitVal = fpp.getVal();
							break;
						}
					}
				}
			}

		}
		code.addCodeLineSameLine("(", curr.getId());
		code.addCodeLineSameLine(varType + " " + varName + " : " + varInitVal, curr.getId());
		code.addCodeLineSameLine(")", curr.getId());
	}

	// overriding should not have to do anything
	@Override
	public void handleFork(CodeBlock code, String currName, boolean isloopBackTarget, ForkType ftype, AnonDefObj curr, ConnDef currCd, FPGraph2 o) {
		if (debug) {
			code.addCodeLine("/*handleFork*/", curr.getId());
		}
		/*
		 * sumit this is not correct
		 * 
		 * if (isloopBackTarget) { code.addCodeLine("while(cond" + currName +
		 * "==true)"); }
		 */
		if (ftype != null && ftype.equals(ForkType.SWITCH)) {
			if (currCd != null && currCd.getConnCond() != null && currCd.getConnCond().length() > 0) {
				System.out.println("This has no parent???");
				code.addCodeLine("switch (" + currCd.getConnCond() + ")", curr.getId());
			} else {
				String initVal = getIfConditionValue(currCd, curr, o);
				code.addCodeLine("switch (" + initVal + ")", curr.getId());

			}
		} else if (ftype != null && ftype.equals(ForkType.If)) {
			String initVal = getIfConditionValue(currCd, curr, o);
			code.addCodeLine("cond" + CommonUtil.convertToJavaId(currName) + " = " + initVal + ";", curr == null ? "UNK" : curr.getId());

			code.addCodeLine(getStmtLabel(currName) + ":", curr == null ? "UNK" : curr.getId());
		} else {
			String initVal = "true";
			code.addCodeLine("cond" + CommonUtil.convertToJavaId(currName) + " = " + initVal + ";", curr == null ? "UNK" : curr.getId());
			code.addCodeLine(getStmtLabel(currName) + ":", curr == null ? "UNK" : curr.getId());
		}
		code.addCodeLine("{", curr == null ? "UNK" : curr.getId());
	}

	@Override
	public void init() {
		labels.clear();

	}

	private String getStmtLabel(String currName) {
		String label = CommonUtil.convertToJavaId(currName);
		Integer depth = labels.get(label);
		if (depth == null) {
			depth = 0;
		} else {
			depth++;
			label = label + "_" + depth;
		}
		labels.put(label, depth);
		return label;
	}

	private String getBreakLabel(String currName) {
		String label = CommonUtil.convertToJavaId(currName);
		Integer depth = labels.get(label);
		if (depth != null && depth > 0) {
			label = label + "_" + depth;
		}
		return label;
	}

	public boolean isDebug() {
		return debug;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
