package org.ptg.util.functions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.Constants;
import org.ptg.util.ICompileFunction;
import org.ptg.util.PropCollType;
import org.ptg.util.functions.Expression.ExpressionType;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public abstract class AbstractICompileFunction implements ICompileFunction{
	private Map<String, String> ctxMap;    
	private boolean isObjectMapper;

	@Override
	public String getInitCode(AnonDefObj anon) {
		return "";
	}

	@Override
	public String getGlobalVar(AnonDefObj anon) {
		return "";
	}

	@Override
	public String getInitVarCode(AnonDefObj anon) {
		return "";
	}
    public String handleUnk(FunctionPortObj fp,FPGraph2 o){
    	if(fp.getMyPort().getPorttype().equals("input")){
    		return StringUtils.replace(fp.getMyPort().getId(),".","_");
    	}else{
    	for(ConnDef cd : o.getForward().values()){
    		if(cd.getTo().equals(fp.getMyPort().getId())){
    			FunctionPoint fpp = o.getFunctionPoints().get(cd.getFrom());
    			if(fpp!=null)
    			return fpp.getVal();
    		}
    	}
    	return "";
    	}
    }

	@Override
	public String getRHSSetterFunction() {
		return ".set";
	}

	@Override
	public boolean isObjectMapper() {
		return this.isObjectMapper;
	}

	@Override
	public void isObjectMapper(boolean b) {
		this.isObjectMapper = b;
	}

	@Override
	public void setContext(Map<String, String> ctxMap) {
		this.ctxMap = ctxMap;
	}
	public String getInInstVar(){
		return ctxMap.get(Constants.InstanceVarName);
	}
	public String getOutInstVar(){
		return ctxMap.get(Constants.OutInstanceVarName);
	}
	public String getSafeIdentifier(String parent,String ident){
		ident = parent + ident;
		return StringUtils.replaceChars(ident, "().", "_");
	}
	public String getSafeIdentifier(String ident){
		return StringUtils.replaceChars(ident, "().", "_");
	}
	public Expression processInput(FPGraph2 graph, FunctionPortObj s) {
		Expression exp = new Expression();
		PortObj po  = s.getPo();
		ConnDef cd = getConnection(s, graph, true);
		PortObj portObj = CommonUtil.getMainPortObject(graph, po);
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
				Expression child = new Expression();
				child.setDtype(getDataType(parentPort.getDtype()));
				child.setId(getSafeIdentifier(parentPort.getGrp()));
				child.setVal(getSafeIdentifier(parentPort.getGrp() )+ retStr);
				child.setExprType(ExpressionType.READ);
				exp.getChild().add(child);
			}
			String cond = cd.getConnCond();
			if(cond!=null){
				retStr.append(cond);
			}
			exp.setDtype(getDataType(parentPort.getDtype()));
			exp.setId(getSafeIdentifier(s.getMyPort().getGrp(),s.getMyPort().getId()));
			exp.setVal(getSafeIdentifier(portObj.getGrp() )+ retStr);
			exp.setExprType(ExpressionType.READ);
			return exp;	
			//solution.append(getDataType(parentPort.getDtype())) + " " + getSafeIdentifier(s.getMyPort().getId()) + " = " + /*getInInstVar*/getSafeIdentifier(portObj.getGrp() )+ retStr);
		}
		return null;
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

	protected String getExpressionStrWithOperator(List<Expression> inputs,String operator) {
		String catStr = "";
		for (int i = 0; i < inputs.size(); i++) {
			Expression exp = inputs.get(i);
			catStr += exp.getId();
			if (i < inputs.size() - 1) {
				catStr += (operator+"\"\""+operator);
			}
		}
		return catStr;
	}
	public Expression processOutput( FPGraph2 graph, FunctionPortObj s) {
		Expression ret = null;

		ConnDef cd = getConnection(s, graph, false);
		PortObj po  =s.getPo();
		PortObj portObj = CommonUtil.getMainPortObject(graph, po);
		if (portObj != null) {
			ret = new Expression();
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
					retStr.append("( " +"{1}"  + " )");
				}
				retStr.append(")");
				ret.setDtype(getDataType(parentPort.getDtype()));
				Expression child = new Expression();
				child.setDtype(getDataType(parentPort.getDtype()));
				child.setId(getSafeIdentifier(parentPort.getGrp()));
				child.setVal(getSafeIdentifier(parentPort.getGrp() )+ retStr);
				child.setExprType(ExpressionType.READ);
				ret.getChild().add(child);

			}
			//solution.append(/*getOutInstVar*/getSafeIdentifier(portObj.getGrp()) + retStr);
			ret.setExprType(ExpressionType.WRITE);
			ret.setId(/*getSafeIdentifier*/(portObj.getGrp()));
			
			ret.setVal(getSafeIdentifier(portObj.getGrp())+retStr.toString());
			//solution.append(";\n");
			return ret;
		}else{
			return ret;
		}
	}
	public List<Expression> getInputExpressionList( List<FunctionPortObj> inputs, FPGraph2 graph){
		List<Expression> inputExpr = new LinkedList<Expression>();
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			Expression exp = processInput(graph, s);//should return a variable name, variabletype, and code to read value;
			inputExpr.add(exp);
		}
		return inputExpr;
		
	}
	public void compileInputExpr(StringBuilder solution, Expression exp ,boolean assignOnly) {
		if(!assignOnly){/*donot declare*/
		solution.append(exp.getDtype());
		solution.append(" ");
		}
		solution.append(exp.getId());//later move to variables
		solution.append("  = ");
		solution.append(exp.getVal());
		solution.append(" ; ");
		solution.append(SystemUtils.LINE_SEPARATOR);
		
	}
	public void compileOutputExpr(StringBuilder solution, String compiledExpr, Expression outExp) {
		solution.append(CommonUtil.formatMessage(outExp.getVal(),compiledExpr));
		solution.append(" ; ");
		solution.append(SystemUtils.LINE_SEPARATOR);
		
	}
}
