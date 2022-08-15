package org.ptg.util.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.PropCollType;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class Remap extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
 		if (super.isObjectMapper()) {
			return compileObject(anon, inputs, output, graph);
		} else {
			StringBuilder solution = new StringBuilder();
			String ret = " (";
			for (int i = 0; i < inputs.size(); i++) {
				FunctionPortObj s = inputs.get(i);
				PortObj po = s.getPo();
				if (po == null || s.getIndex() == -1) {
					ret += ("(String)" + handleUnk(s, graph));
				} else {
					String castExpr = StringUtils.split(po.getDtype(), "/")[0];
					if (castExpr != null || castExpr.length() > 0)
						ret += ("(" + castExpr + ")");
					ret += s.getGrpName();
					ret += (".get(currIndex). get( " + s.getIndex() + " )");
				}
				if (i < inputs.size() - 1) {
					ret += "+";
				}
			}
			ret += ") ";
			StringBuilder preDefVars = new StringBuilder();

			for (int i = 0; i < output.size(); i++) {
				FunctionPortObj sLat = output.get(i);
				if (sLat.getGrpName().equals("unk")) {
					preDefVars.append("Object " + getSafeIdentifier(sLat.getPo().getId()) + " = null;\n");
					solution.append(getSafeIdentifier(sLat.getPo().getId()) + " = " + ret + "  ; \n");
				} else {
					solution.append(sLat.getGrpName() + getRHSSetterFunction() + " ( " + sLat.getIndex() + " , " + ret + " ) ; \n");
				}

			}
			preDefVars.append(solution);
			return preDefVars.toString();
		}
	}

	public Object compileObject(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph) {
		Map<String, PortObj> mainTypes = new HashMap<String, PortObj>();
		StringBuilder solution = new StringBuilder();
		// solution.append("String " + anon.getId() + "  = null;\n");
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			PortObj po = s.getPo();
			// solution.append("String " +
			// StringUtils.replace(s.getMyPort().getId(), ".",
			// "_")+" = null;\n");
			processInput(anon, inputs, output, graph, mainTypes, solution, s, po);
			solution.append("\n");
		}
		solution.append("\n");
		solution.append("\n");
		for (int i = 0; i < output.size(); i++) {
			FunctionPortObj s = output.get(i);
			PortObj po = s.getPo();
			processOutput(anon, inputs, output, graph, mainTypes, solution, s, po);
			solution.append("\n");
		}
		solution.append("\n");
		System.out.println(solution.toString());
		return solution.toString();
	}

	public void processOutput(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph, Map<String, PortObj> mainTypes, StringBuilder solution, FunctionPortObj s,
			PortObj po) {
		ConnDef cd = getConnection(s, graph, false);
		PortObj portObj = mainTypes.get(po.getGrp());
		if (portObj == null) {
			portObj = CommonUtil.getMainPortObject(graph, po);
			if (portObj != null) {
				mainTypes.put(po.getGrp(), portObj);
			}
		}
		if (portObj != null) {
			String self = StringUtils.substringAfter(po.getPortname(), portObj.getPortname());
			String[] comps = StringUtils.split(self, ".");
			String parent = portObj.getPortname();
			StringBuilder retStr = new StringBuilder();
			int count = 0;
			for (String p : comps) {
				count++;
				String fun = null;
				if (count == comps.length)
					fun = getFuncCode(s, cd);
				else
					fun = "get";
				retStr.append("." + fun + StringUtils.capitalize(p) + "(");
				parent = parent + "." + p;
				String pname = (po.getPorttype() .equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
				PortObj parentPort = graph.getPorts().get(pname);
				String[] dtype = StringUtils.split(parentPort.getDtype(), "/");
				if (dtype.length == 2) {
					PropCollType dimid = PropCollType.valueOf(dtype[1]);
					if (dimid == null) {
						throw new RuntimeException("Unknown Dimension for " + self);
					}
				}
				if (count == comps.length) {
					retStr.append("( " + getCatString(anon, inputs, graph, mainTypes) + " )");
				}
				retStr.append(")");
			}
			solution.append(/*getOutInstVar*/getSafeIdentifier(portObj.getGrp()) + retStr);
			solution.append(";\n");

		}
	}

	private String getCatString(AnonDefObj anon, List<FunctionPortObj> inputs, FPGraph2 graph, Map<String, PortObj> mainTypes) {
		String catStr = "";
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			PortObj po = s.getPo();
			catStr += getSafeIdentifier(s.getMyPort().getId());
			if (i < inputs.size() - 1) {
				catStr += "+\"\"+";
			}
		}
		return catStr;
	}

	public void processInput(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph, Map<String, PortObj> mainTypes, StringBuilder solution, FunctionPortObj s,
			PortObj po) {
		ConnDef cd = getConnection(s, graph, true);
		PortObj portObj = mainTypes.get(po.getGrp());
		if (portObj == null) {
			portObj = CommonUtil.getMainPortObject(graph, po);
			if (portObj != null) {
				mainTypes.put(po.getGrp(), portObj);
			}
		}
		PortObj parentPort = null;
		if (portObj != null) {
			String self = StringUtils.substringAfter(po.getPortname(), portObj.getPortname());
			String[] comps = StringUtils.split(self, ".");
			String parent = portObj.getPortname();
			StringBuilder retStr = new StringBuilder();
			String pname = (po.getPorttype().equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
			parentPort = graph.getPorts().get(pname);
			for (String p : comps) {

				String fun = getFuncCode(s, cd);
				retStr.append("." + fun + StringUtils.capitalize(p) + "(");
				retStr.append(")");
				parent = parent + "." + p;
				pname = (po.getPorttype().equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
				parentPort = graph.getPorts().get(pname);
				String[] dtype = StringUtils.split(parentPort.getDtype(), "/");
				if (dtype.length == 2) {
					PropCollType dimid = PropCollType.valueOf(dtype[1]);
					if (dimid == null) {
						throw new RuntimeException("Unknown Dimension for " + self);
					}
				}

			}
			String cond = cd.getConnCond();
			if(cond!=null){
				retStr.append(cond);
			}
			solution.append(getDataType(CommonUtil.getMainPortObject(graph,s.getMyPort()).getDtype()) + " " + getSafeIdentifier(s.getMyPort().getGrp()) + " = " + /*getInInstVar*/getSafeIdentifier(portObj.getGrp()) + retStr);
			solution.append(";\n");
		}
	}

	protected String getDataType(String dataType) {
		String ret = "";
		String[] dtype = StringUtils.split(dataType, "/");
		if (dtype.length == 2) {
			PropCollType dimid = PropCollType.valueOf(dtype[1]);
			if (dimid == null) {
				throw new RuntimeException("Unknown Dimension for " + dtype[1]);
			}
			ret += dimid.getDef().replace("$name", dtype[0]);
		} else {
			ret += dataType + " ";
		}
		return ret;
	}

	public String getFuncCode(FunctionPortObj s, ConnDef cd) {
		return cd.getDirection(s.getMyPort().getId()).equals("in") ? "get" : "set";
	}

	public ConnDef getConnection(FunctionPortObj s, FPGraph2 graph, Boolean inport) {
		ConnDef cdret = null;
		if (inport) {
			for (ConnDef cd : graph.getForward().values()) {
				if (cd.getFrom().equals(s.getPo().getId()) && cd.getTo().equals(s.getMyPort().getId())) {
					cdret = cd;
					break;
				}
			}
		} else {
			for (ConnDef cd : graph.getForward().values()) {
				if (cd.getTo().equals(s.getPo().getId()) && cd.getFrom().equals(s.getMyPort().getId())) {
					cdret = cd;
					break;
				}
			}
		}
		return cdret;
	}

	
}
