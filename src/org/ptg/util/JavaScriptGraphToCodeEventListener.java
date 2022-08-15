package org.ptg.util;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class JavaScriptGraphToCodeEventListener implements IGraphToCodeEventListener {
	boolean debug = false;

	// overriding should not have to do anything
	@Override
	public void handleNormalBlock(CodeBlock code, AnonDefObj curr) {
		if (debug) {
			if (curr != null) {
				code.append("/*handleNormalBlock*/", curr.getId());
			} else {
				code.append("/*handleNormalBlock*/", "");
			}
		}
	}

	// overriding should not have to do anything
	@Override
	public void handleForkBlockBegin(CodeBlock code, AnonDefObj curr) {
		if (debug) {
			code.append("/*handleForkBlockBegin*/", curr.getId());
		}
		code.append("{", curr.getId());
	}

	// overriding should not have to do anything
	@Override
	public void handleForkCondition(CodeBlock code, ConnDef cd, int fcount, int tcount, ForkType ftype, AnonDefObj curr, FPGraph2 o) {
		if (debug) {
			code.append("/*handleForkCondition*/", curr.getId());
		}
		if (ftype.equals(ForkType.If)) {
			handleIfFork(code, cd, fcount, tcount, curr,o);
		} else if (ftype.equals(ForkType.SWITCH)) {
			handleSwitchFork(code, cd, fcount, tcount, curr);
		} else if (ftype.equals(ForkType.UNK)) {
			handleUnkFork(code, cd, fcount, tcount, curr);
		}
	}

	// overriding should not have to do anything
	private void handleSwitchFork(CodeBlock code, ConnDef cd, int fcount, int tcount, AnonDefObj curr) {
		if (debug) {
			code.append("/*handleSwitchFork*/", curr.getId());
		}
		code.append(" case " + cd.getConnCond() + " : ", curr.getId());

	}

	// overriding should not have to do anything
	private void handleUnkFork(CodeBlock code, ConnDef cd, int fcount, int tcount, AnonDefObj curr) {
		if (debug) {
			code.append("/*handleUnkFork*//*unknown fork type*/", curr.getId());
		}

	}

	// overriding should not have to do anything
	private void handleIfFork(CodeBlock code, ConnDef cd, int fcount, int tcount, AnonDefObj curr, FPGraph2 o) {
		String id = getCurrId(curr);
		if (debug) {
			code.append("/*handleIfFork*/", curr.getId());
		}
		if (fcount == 0) {
			String initVal = getIfConditionValue(cd, curr, o);
			// code.addCodeLine(" if(cond" +
			// CommonUtil.convertToJavaId(cd.getFrom()) + "==" + initVal +
			// ")",id);
			code.addCodeLine(" if(" + initVal + ")", id);
		} else {
			if (tcount == 2) {
				code.append("else", curr.getId());
			} else {
				if (fcount == tcount - 1) {
					if (cd.getConnCond() == null || cd.getConnCond().length() < 1) {
						code.append("else ", curr.getId());
					} else {
						code.append("else if(" + cd.getConnCond() + ")", curr.getId());
					}
				} else {
					code.append("else if(" + cd.getConnCond() + ")", curr.getId());
				}
			}
		}
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
		if (debug) {
			code.append("/*handleForkFinish*/", curr.getId());
		}
		if (isLoopTarget) {
			// code.append("cond" + currName + " = false;");
		}
		code.append("", curr.getId());
		code.append("}", curr.getId());
	}

	// overriding should not have to do anything
	@Override
	public void handleLoopBackTargetFinish(CodeBlock code, String currName, AnonDefObj curr) {
		if (debug) {
			code.append("/*handleLoopBackTargetFinish*/", curr.getId());
		}
		// code.append("cond"+currName+" = false;");
		code.append("", curr.getId());
		code.append("}", curr.getId());
	}

	// overriding should not have to do anything
	@Override
	public void handleEndChild(CodeBlock code, AnonDefObj curr, PNode p) {
		if (debug) {
			code.append("/*handleEndChild*/", curr.getId());
		}
		code.append("", curr.getId());
		code.append("}", curr.getId());

	}

	// @@override this for the handle generate code
	@Override
	public void handleGenerateCode(CodeBlock code, String currName, String breakOrContinue, boolean isLongJump, AnonDefObj curr, FPGraph2 o) {
		if (debug) {
			code.append("/*handleGenerateCode*/", curr.getId());
		}
		if (isLongJump) {
			if (breakOrContinue == null) {
				breakOrContinue = "continue";
			}
			if(curr!=null)
			code.append(breakOrContinue + " " + CommonUtil.convertToJavaId(currName) + ";", curr.getId());
			else
				code.append(breakOrContinue + " " + CommonUtil.convertToJavaId(currName) + ";", "");
				
		} else {
			if (curr != null && curr.getAnonType() != null && curr.getAnonType().equalsIgnoreCase("switch")) {
				// do something here??? when switch donot print anything
			} else if (curr != null && curr.getAnonType() != null && curr.getAnonType().equalsIgnoreCase("var")) {
				genVarCommand(code, curr, o);
			} else if (curr != null && curr.getAnonType() != null && curr.getAnonType().equalsIgnoreCase("JavaCode")) {
				genJavaCodeCommand(code, curr, o);
			} else {
				if (curr != null) {
					code.append("console.log(\"" + currName + "\");", curr.getId());
				} else {
					code.append("console.log(\"" + currName + "\");", currName);
				}
			}
		}
	}

	private void genJavaCodeCommand(CodeBlock codeBldr, AnonDefObj curr, FPGraph2 o) {
		JSONObject jo = (JSONObject) o.getFps().get(curr.getId()).getXref();
		String codeStr = jo.getString("script");
		Map<String, String> portNames = new LinkedHashMap<String, String>();
		for (String s : curr.getAux()) {
			portNames.put(StringUtils.substringAfterLast(s, curr.getId()), s);
		}
		for (Map.Entry<String, String> en : portNames.entrySet()) {
			PortObj po = o.getPorts().get("aux_" + curr.getId() + "." + en.getValue());
			String portName = en.getKey();
			String portVal = po.getPortval();
			if (portName.endsWith(".in.value")) {
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getTo().equals(po.getId())) {
						PortObj from = o.getPorts().get(cd.getFrom());
						if (from == null) {
							FunctionPoint obj = o.getFunctionPoints().get(from);
							if (obj != null) {
								portVal= obj.getVal();
							} else {
								portVal = po.getPortval();
							}
						} else {
							portVal = from.getPortval();
						}
						break;
					}
				}
			} else if (portName.endsWith(".in.name")) {
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getTo().equals(po.getId())) {
						PortObj from = o.getPorts().get(cd.getFrom());
						if (from == null) {
							FunctionPoint obj = o.getFunctionPoints().get(cd.getFrom());
							if (obj != null) {
								portVal = ( obj.getId());
							} else {
								portVal = (po.getPortval());
							}
						} else {
							portVal = (from.getPortname());
						}
						break;
					}
				}
			} else {
				portVal = po.getPortval();
			}
			codeStr = codeStr.replaceAll("\\{"+portName+":([a-zA-Z0-9 _\\.\\[\\]\"\']*)\\}", portVal);
			codeStr = StringUtils.replace(codeStr, "{" + portName + "}", po.getPortval());
		}

		codeBldr.append(codeStr, curr.getId());
	}

	private void genVarCommand(CodeBlock code, AnonDefObj curr, FPGraph2 o) {
		String varName = "i";
		String varType = "var";
		String varInitVal = "0";
		{
			String vtypePort = "aux_" + curr.getId() + "." + curr.getAux().get(0);
			String vnamePort = "aux_" + curr.getId() + "." + curr.getAux().get(1);
			String initValPort = "aux_" + curr.getId() + "." + curr.getAux().get(2);
			varType = o.getPorts().get(vtypePort).getPortval();
			varName = o.getPorts().get(vnamePort).getPortval();
			varInitVal = o.getPorts().get(initValPort).getPortval();
		}
		code.append(varType + " " + varName + " = " + varInitVal + ";", curr.getId());
	}

	// @@override this since there is a specialized implementation for the loop
	// types
	@Override
	public void handleLoopBackTarget(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		if (debug) {
			code.append("/*handleLoopBackTarget*/", curr.getId());
		}
		code.append("cond" + CommonUtil.convertToJavaId(currName) + " = true;", curr.getId());
		code.append(CommonUtil.convertToJavaId(currName) + ":", curr.getId());
		if (curr != null) {
			if (curr.getAnonType().equals("for")) {
				handleFor(code, currName, curr, o);
				code.append("{", curr.getId());
			} else if (curr.getAnonType().equals("while")) {
				handleWhile(code, currName, curr, o);
				code.append("{", curr.getId());
			} else if (curr.getAnonType().equals("foreach")) {
				handleForEach(code, currName, curr, o);
				code.append("{", curr.getId());
			} else {
				code.append("while(cond" + CommonUtil.convertToJavaId(currName) + "==true){", curr.getId());
			}
		} else {
			code.append("while(cond" + CommonUtil.convertToJavaId(currName) + "==true){", curr.getId());
		}
	}

	public void handleFor(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		code.append(" for ", curr.getId());
		String varName = "i";
		String varType = "var";
		String varInitVal = "0";
		String breakCond = null;
		String incr = null;
		{
			String vnamePort = "aux_" + curr.getId() + "." + curr.getAux().get(1);
			String initValPort = "aux_" + curr.getId() + "." + curr.getAux().get(2);
			String nextValPort = "aux_" + curr.getId() + "." + curr.getAux().get(3);
			String testValPort = "aux_" + curr.getId() + "." + curr.getAux().get(4);
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
		String varType = "var";
		String varInitVal = "0";
		{
			String vtypePort = "aux_" + curr.getId() + "." + curr.getAux().get(0);
			String vnamePort = "aux_" + curr.getId() + "." + curr.getAux().get(1);
			String initValPort = "aux_" + curr.getId() + "." + curr.getAux().get(2);
			varType = o.getPorts().get(vtypePort).getPortval();
			varName = o.getPorts().get(vnamePort).getPortval();
			varInitVal = o.getPorts().get(initValPort).getPortval();
		}
		code.addCodeLineSameLine("(", curr.getId());
		code.addCodeLineSameLine(varType + " " + varName + " in " + varInitVal, curr.getId());
		code.addCodeLineSameLine(")", curr.getId());
	}

	// overriding should not have to do anything
	@Override
	public void handleFork(CodeBlock code, String currName, boolean isloopBackTarget, ForkType ftype, AnonDefObj curr, ConnDef currCd, FPGraph2 o) {
		if (debug) {
			code.append("/*handleFork*/", curr.getId());
		}
		/*
		 * sumit this is not correct
		 * 
		 * if (isloopBackTarget) { code.append("while(cond" + currName +
		 * "==true)"); }
		 */
		if (ftype != null && ftype.equals(ForkType.SWITCH)) {
			if (currCd == null) {
				code.append("switch (" + "cond" + CommonUtil.convertToJavaId(currName) + ")", curr.getId());
			} else {
				System.out.println("This has no parent???");
				code.append("switch (" + currCd.getConnCond() + ")", curr.getId());
			}
		} else {
			code.append("cond" + CommonUtil.convertToJavaId(currName) + " = true;", curr.getId());
			code.append(CommonUtil.convertToJavaId(currName) + ":", curr.getId());
		}
		code.append("{", curr.getId());

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
