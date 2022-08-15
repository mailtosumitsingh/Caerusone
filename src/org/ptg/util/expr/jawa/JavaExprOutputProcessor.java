package org.ptg.util.expr.jawa;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.PropCollType;
import org.ptg.util.expr.IOutputExprProcessor;
import org.ptg.util.functions.Expression.ExpressionType;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class JavaExprOutputProcessor implements IOutputExprProcessor {

	@Override
	public org.ptg.util.functions.Expression process(FPGraph2 graph, FunctionPortObj s) {
		org.ptg.util.functions.Expression ret = null;

		ConnDef cd = CommonUtil.getConnection(s, graph, false);
		PortObj po = s.getPo();
		PortObj portObj = CommonUtil.getMainPortObject(graph, po);
		boolean isRemap = false;

		if (portObj != null) {
			for (AnonDefObj d : graph.getAnonDefs()) {
				if (d.getId().equals(portObj.getGrp())) {
					if (d.getAnonType().equals("Remap")) {
						isRemap = true;
					}
				}
			}
			if (!isRemap) {
				ret = new org.ptg.util.functions.Expression();
				String self = StringUtils.substringAfter(po.getPortname(), portObj.getPortname());
				String[] comps = StringUtils.split(self, ".");
				String parent = portObj.getPortname();
				StringBuilder retStr = new StringBuilder();
				int count = 0;
				for (String p : comps) {
					count++;
					String fun = null;
					if (count == comps.length)
						fun = CommonUtil.getFuncCode(s, cd);
					else
						fun = "get";
					retStr.append("." + fun + StringUtils.capitalize(p) + "(");
					parent = parent + "." + p;
					String pname = (po.getPorttype().equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
					PortObj parentPort = graph.getPorts().get(pname);
					String dataType = parentPort.getDtype();
					if (dataType != null) {
						String[] dtype = StringUtils.split(dataType, "/");
						if (dtype.length == 2) {
							PropCollType dimid = PropCollType.valueOf(dtype[1]);
							if (dimid == null) {
								throw new RuntimeException("Unknown Dimension for " + self);
							}
						}
						if (count == comps.length) {
							retStr.append("( " + "{1}" + " )");
						}
						retStr.append(")");
						ret.setDtype(CommonUtil.getDataType(dataType));
						org.ptg.util.functions.Expression child = new org.ptg.util.functions.Expression();
						child.setDtype(CommonUtil.getDataType(dataType));
						child.setId(CommonUtil.getSafeIdentifier(parentPort.getGrp()));
						child.setVal(CommonUtil.getSafeIdentifier(parentPort.getGrp()) + retStr);
						child.setExprType(ExpressionType.READ);
						ret.getChild().add(child);
					} else {

					}

				}
				// solution.append(/*getOutInstVar*/getSafeIdentifier(portObj.getGrp())
				// + retStr);
				ret.setExprType(ExpressionType.WRITE);
				ret.setId(/* getSafeIdentifier */(portObj.getGrp()));

				ret.setVal(CommonUtil.getSafeIdentifier(portObj.getGrp()) + retStr.toString());
				// solution.append(";\n");
			}
			return ret;
		} else {
			return ret;
		}
	}

}
