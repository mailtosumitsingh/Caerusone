package org.ptg.util.expr.jawa;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.PropCollType;
import org.ptg.util.db.DBTransformerHelper;
import org.ptg.util.expr.IInputExprProcessor;
import org.ptg.util.functions.Expression.ExpressionType;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class JavaExprInputProcessor implements IInputExprProcessor {

	@Override
	public org.ptg.util.functions.Expression process(FPGraph2 graph, FunctionPortObj s) {
		org.ptg.util.functions.Expression exp = new org.ptg.util.functions.Expression();
		PortObj po = s.getPo();
		ConnDef cd = CommonUtil.getConnection(s, graph, true);
		PortObj portObj = CommonUtil.getMainPortObject(graph, po);
		PortObj parentPort = null;
		if (portObj != null) {
			AnonDefObj anonDef = null;
			for (AnonDefObj d : graph.getAnonDefs()) {
				if (d.getId().equals(portObj.getGrp())) {
					anonDef = d;
				}
			}
			String self = StringUtils.substringAfter(po.getPortname(), portObj.getPortname());
			String[] comps = StringUtils.split(self, ".");
			String parent = portObj.getPortname();
			StringBuilder retStr = new StringBuilder();
			String pname = (po.getPorttype().equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
			parentPort = graph.getPorts().get(pname);
			for (String p : comps) {

				String fun = CommonUtil.getFuncCode(s, cd);
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
				org.ptg.util.functions.Expression child = new org.ptg.util.functions.Expression();
				child.setDtype(CommonUtil.getDataType(parentPort.getDtype()));
				child.setId(CommonUtil.getSafeIdentifier(parentPort.getGrp()));
				child.setVal(CommonUtil.getSafeIdentifier(parentPort.getGrp()) + retStr);
				child.setExprType(ExpressionType.READ);
				exp.getChild().add(child);
			}
			String cond = cd.getConnCond();
			if (cond != null) {
				retStr.append(cond);
			}
			if (anonDef.getAnonType().equalsIgnoreCase("db")) {
				getExprForDBType(s, exp, po,anonDef);
			} else {
				getExprForObjType(s, exp, portObj, parentPort, retStr);
			}
			return exp;
			// solution.append(getDataType(parentPort.getDtype())) + " " +
			// getSafeIdentifier(s.getMyPort().getId()) + " = " +
			// /*getInInstVar*/getSafeIdentifier(portObj.getGrp() )+ retStr);
		}
		return null;
	}

	private void getExprForObjType(FunctionPortObj s, org.ptg.util.functions.Expression exp, PortObj portObj, PortObj parentPort, StringBuilder retStr) {
		exp.setDtype(CommonUtil.getDataType(parentPort.getDtype()));
		exp.setId(CommonUtil.getSafeIdentifier(s.getMyPort().getGrp(), s.getMyPort().getId()));
		exp.setVal(CommonUtil.getSafeIdentifier(portObj.getGrp()) + retStr);
		exp.setExprType(ExpressionType.READ);
	}

	private void getExprForDBType(FunctionPortObj s, org.ptg.util.functions.Expression exp, PortObj po, AnonDefObj anonDef) {
		exp.setDtype(CommonUtil.getDataType(po.getDtype()));
		exp.setId(CommonUtil.getSafeIdentifier(s.getMyPort().getGrp(), s.getMyPort().getId()));
		String portNameToUse = po.getName();
		if(portNameToUse.contains(po.getGrp()+"_")){
		portNameToUse = StringUtils.substringAfterLast(portNameToUse, po.getGrp()+"_");
		}
		if(portNameToUse.contains(".")){
			portNameToUse = StringUtils.substringAfterLast(portNameToUse, ".");
		}
		String inElement  = CommonUtil.getSafeIdentifier(po.getGrp(), "");
		String dbStr = (inElement+"." + DBTransformerHelper.DbToJavaMtdMap.get(po.getDtype()) + "(\"" + portNameToUse + "\")");
		exp.setVal(/* "in"+po.getName()+ */dbStr);
		exp.setExprType(ExpressionType.READ);
	}

}
