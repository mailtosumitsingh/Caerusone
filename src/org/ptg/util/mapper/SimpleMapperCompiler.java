/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.ptg.processors.ConnDef;
import org.ptg.util.CodePathMachineCompiler;
import org.ptg.util.CommonUtil;
import org.ptg.util.db.ColDef;
import org.ptg.util.mapper.v2.FPGraph2;

public class SimpleMapperCompiler {
	FPGraph graph = null;
	Map<String, List<FunctionPoint>> grps = null;
	public String compile(String name, String graphjson) {
		FPGraph  g = new FPGraph();
		try {
			g.fromGraphJson(name, graphjson);
			return compile(name, g,null);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public String compile(String name, FPGraph g,FPGraph2 pg) {
		graph = g;
		Map<String, String> results = new HashMap<String, String>();
		Map<FunctionPoint, CompilePath> codes = new HashMap<FunctionPoint, CompilePath>();
		grps = new HashMap<String, List<FunctionPoint>>();
		try {
			System.out.println(graph.getEnds());
			System.out.println(graph.getStarts());
			List<Collection<FunctionPoint>> g1 = graph.getAllPaths();
			List<List<FunctionPoint>> g2 = graph.getAllSubPaths();
			for (List<FunctionPoint> gi : g2) {
				FunctionPoint fp = gi.get(gi.size() - 1);
				if (fp.getGrp() != null && fp.getGrp().length() > 0) {
					List<FunctionPoint> grp = grps.get(fp.getGrp());
					if (grp == null) {
						grp = new ArrayList<FunctionPoint>();
					}
					while (grp.size() <= fp.getIndex()) {
						grp.add(null);
					}
					grp.set(fp.getIndex(), fp);
					grps.put(fp.getGrp(), grp);
				}
			}
			// joinby group
			for (List<FunctionPoint> gi : g2) {
				// compile code
				// first parse only isolated
				Map<String, String> sb = new LinkedHashMap<String, String>();
				CompilePath cp = new CompilePath();
				cp.setStart(gi.get(0));
				cp.setEnd(gi.get(gi.size() - 1));
				FunctionPoint lp = null;
				FunctionPoint fp = null;
				for (int i = 0; i < gi.size(); i++) {
					fp = gi.get(i);
					if(lp==null){
						//find lp from parent
						for(ConnDef find : pg.getForward().values()){
							if(find.getTo().equals(fp.getId())){
								lp = pg.getFunctionPoints().get(find.getFrom());
								break;
							}
						}
					}
					ConnDef currConn = 		getCurrentConn(lp, fp);

					// i==0 is read
					if(fp.getType().equals("sqlobj")){
						/*String c  = null;
						c = "${stmt}"+" = "+" ${conn} "+".getStatement();\n";
						c += fp.getFn();
						if(c!=null)
							sb.put(fp.getId(),c);*/
					}else if(fp.getType().equals("colgetobj")){
						compileColGetObj(pg, sb, cp, fp,currConn);
					}else if(fp.getType().equals("colsetobj")){
						compileColSetObj(pg, sb, cp, fp,currConn);
					}else if(fp.getType().equals("classobj")){
						compileClassObj(sb, lp, fp,currConn);
					}else if(fp.getType().equals("moduleobj")){
						String c  = fp.getVal();
						if(c!=null){
							String extCode = CommonUtil.extractCodeFromHtml(c);
							sb.put(fp.getName(),extCode);
						}
					}else if(fp.getType().equals("ToDoObj")){
							//to do nothing
					}else if(fp.getType().equals("ActivityObj")){
							//to do nothing
					}else{
						if ((fp.getPn() == null || fp.getPn().length() < 1) && fp.getFn() != null) {
						// this is functionif (lp == null) should have separate
						// process later
						String c  = compileFunctionPointFunction(cp, fp,currConn);
						if(c!=null)
						sb.put(fp.getName(),c);
					} else if (fp.getPn() != null && (fp.getFn() == null || fp.getFn().length() < 1)) {
						// this is variable
						String c = compileFunctionPointVariable(lp, fp,currConn);
						if(c!=null){
							if(lp!=null && lp.getType().equals("moduleobj")){
								String code =  CommonUtil.compileDataTypeTempl(fp.getDataType(),lp.getName(),sb.get(lp.getName()));
								if(code!=null){
								sb.put(lp.getName(),code);
								}
								sb.put(fp.getName(),c);
							}else{
								sb.put(fp.getName(),c);
							}
						}
					} else if ((fp.getPn() != null || fp.getPn().length() > 0) && (fp.getFn() != null || fp.getFn().length() > 0)) {
						// is param definition
						String c = compileFunctionPointFunctionParam( lp, fp,currConn);
						if(c!=null){
							if(lp!=null && lp.getType().equals("moduleobj")){
								String code =  CommonUtil.compileDataTypeTempl(fp.getDataType(),lp.getName(),sb.get(lp.getName()));
								if(code!=null){
								sb.put(lp.getName(),code);
								}
								sb.put(fp.getName(),c);
							}else{
								sb.put(fp.getName(),c);
							}
						}
					}
					}
					lp = fp;
				}
				//merge here properly
				cp.setCode(sb);
				CompilePath tempcp = codes.get(fp);
				if(tempcp!=null){
					for(Map.Entry<String,String>entry:tempcp.getCode().entrySet()){
					cp.getCode().put(entry.getKey(), entry.getValue());
					}
					codes.put(fp, cp);
				}else{
					codes.put(fp, cp);
				}
				System.out.println(sb.toString());
			}
			// attach discrete code elements
			Map<FunctionPoint, Integer> visits = new HashMap<FunctionPoint, Integer>();
			for (FunctionPoint gp : graph.getFunctionPoints().values()) {
				int t = graph.getGraph().getSuccessorCount(gp);
				visits.put(gp, t + 1);
			}
			StringBuilder toRet = new StringBuilder();
			Set<String> vars = new HashSet<String>();
			//resolve dyna dependencies
			System.out.println("=========================================");
			CodePathMachineCompiler cpcomp = new CodePathMachineCompiler();
			cpcomp.setFpgraph(graph);
			cpcomp.setFpgraph2(pg);
			cpcomp.setGrps(grps);
			cpcomp.init();
			for (CompilePath cpp : codes.values()) {
				//toRet.append(resolve(cpp, codes, visits, vars));
				System.out.println("-----------------------");
				System.out.print(cpp.getStart().getId()+"---->"+cpp.getEnd().getId()+"[");
				if(cpp.getDeps()!=null){
					for(FunctionPoint dp : cpp.getDeps()){
						System.out.print(dp.getId());
						System.out.print(",");
					}
				}
				cpcomp.addCompilePath(cpp);
				System.out.println("]");
				System.out.println(cpp.getCode().toString());
				System.out.println("-----------------------");
			}
			System.out.println("=========================================");
			String s = cpcomp.compile();
			System.out.println(".........................................");
			System.out.println(s);
			System.out.println(".........................................");
			return s; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public ConnDef getCurrentConn(FunctionPoint lp, FunctionPoint fp) {
		ConnDef curr = null;
		if(lp!=null){
		Collection<ConnDef>  o = graph.getGraph().getOutEdges(lp);
		if(o!=null){
			for(ConnDef iter : o){
			 if(iter.getTo().equals(fp.getName())){
				 curr = iter; 
			 }
			}
		}
		}
		return curr;
	}
	private void compileClassObj(Map<String, String> sb, FunctionPoint lp, FunctionPoint fp,ConnDef curr) {
		String c  = null;
		if(fp.cn.equals("if")){
			c=" if ("+fp.val+"){\n";
		}else if(fp.cn.equals("end")){
			c=" }\n";
		}else if (fp.getVal() == null || fp.getVal().length() < 1) {
			if(lp!=null&&(lp.cn==null||lp.cn.equals("if"))){
				c = (fp.getDataType() + " " + fp.getName() + " = ("+fp.getDataType()+")" + lp.getName() + ";\n");
			}else
				c = fp.getDataType() +" "+fp.getCn() +" = "+ " null "+";\n";
		} else{
			c =  (fp.getDataType() + " " + fp.getName() + " = ("+fp.getDataType()+")" + fp.getVal() + ";\n");
		}
		if(c!=null){
			if(lp!=null && lp.getType().equals("moduleobj")){
				String code =  CommonUtil.compileDataTypeTempl(fp.getDataType(),lp.getName(),sb.get(lp.getName()));
				if(code!=null){
				sb.put(lp.getName(),code);
				}
				sb.put(fp.getName(),c);
			}else{
				sb.put(fp.getName(),c);
			}
		}
	}
	private void compileColSetObj(FPGraph2 pg, Map<String, String> sb, CompilePath cp, FunctionPoint fp,ConnDef currConn) {
		FunctionPoint iou = pg.getFunctionPoints().get(((ColDef)fp.getXref()).getGrp());
		String c  = compileFunctionPointFunction(cp, fp,currConn);
		if(c!=null)
		sb.put(fp.getName(),c);
	}
	private void compileColGetObj(FPGraph2 pg, Map<String, String> sb, CompilePath cp, FunctionPoint fp,ConnDef currConn) {
		if(fp.getSymbolTable().get("_processed")==null) {
		String c  = compileFunctionPointFunction(cp, fp,currConn);
		FunctionPoint query = pg.getAux().get(((ColDef)fp.getXref()).getGrp());
		
		if(query!=null && query.getSymbolTable().get("_processed")==null){
			String c2  = null;
			c2 = query.getFn()+"\n";
			c2 += fp.getFn();
			c = c2 + c ;
			query.getSymbolTable().put("_processed","done_"+fp.getId());
		}
		if(c!=null){
		sb.put(fp.getName(),c);
		fp.getSymbolTable().put("_processed","done_"+fp.getId());
		}}
	}

	private String compileFunctionPointFunctionParam( FunctionPoint lp, FunctionPoint fp,ConnDef curr) {
		return FunctionPointFunctionParamCompiler.compileFunctionPointFunctionParam(lp, fp,curr);
	}

	private String compileFunctionPointVariable(FunctionPoint lp, FunctionPoint fp,ConnDef curr) {
			return FunctionPointVariableCompiler.compileFunctionPointVariable(lp, fp,curr);
	}

	private String compileFunctionPointFunction(  CompilePath cp, FunctionPoint fp,ConnDef curr) {
		String []lhsrhs =  FunctionPointFunctionCompiler.compileFunctionPointFunction(grps, cp, fp,curr);
		String temp = null;
		if(lhsrhs!=null&&lhsrhs.length==2){
			if(fp.dataType.equals("void")){
				temp = lhsrhs[1];
			}else{
			temp = lhsrhs[0] +" = "+"("+fp.getDataType()+") "+lhsrhs[1];
			}
		}
		return temp;
	}

	public String resolve(CompilePath cp, Map<FunctionPoint, CompilePath> codes, Map<FunctionPoint, Integer> visits, Set<String> vars) {
		StringBuilder toret = new StringBuilder();
		int v = visits.get(cp.getStart());
		if (v == 0)
			return toret.toString();
		else {
			visits.put(cp.getStart(), v - 1);
			if (cp.getDeps() != null && cp.getDeps().size() > 0) {
				for (FunctionPoint fp : cp.getDeps()) {
					CompilePath temp = codes.get(fp);
					String r = resolve(temp, codes, visits, vars);
					toret.append(r);
				}
			}
			toret.append("/*START*****" + cp.getStart().getName() + "*/\n");
			for (Map.Entry<String, String> s : cp.getCode().entrySet()) {
				if (!vars.contains(s.getKey())) {
					Map<String, JSONObject> rc = graph.getOrphans();
					boolean added = false;
					Map<String, String> c = null;
					///add here copied code #$#@%@#$@#$@#$65456
					if (added) {
						// for now append first one later we might want to pick
						// and choose
						toret.append(c.values().iterator().next());
					} else {
						toret.append(s.getValue());// here is code being
													// appended
					}
					vars.add(s.getKey());
				}
			}
			toret.append("/*END*****" + cp.getStart().getName() + "*/\n\n");

		}
		return toret.toString();
	}
}

/*
 * 
 * for (Map.Entry<String, JSONObject> en : rc.entrySet()) {
						String type = en.getValue().getString("type");
						String rid = en.getValue().getString("id");
						if (type != null && type.equals("region")) {
							JSONArray items = (JSONArray) en.getValue().get("items");
							for (Object o : items) {
								if (o.toString().equals(s.getKey())) {
									System.out.println("region " + rid + " contains " + o);
									Map<String, Object> p = new HashMap<String, Object>();
									p.put("cp", cp);
									p.put("c", s);
									Collection<FunctionPoint> lps  = graph.getGraph().getPredecessors(graph.getFunctionPoints().get(s.getKey()));
									FunctionPoint lp = null;
									if(lps.size()>0){
										lp = lps.iterator().next();
									}
									p.put("lp",lp);
									p.put("fp",graph.getFunctionPoints().get(s.getKey()));
									p.put("r", en.getValue());
									p.put("paths", codes);
									p.put("visits", visits);
									p.put("vars", vars);
									p.put("grps",grps);
									c = CommonUtil.compileOrhpahDefinition(en.getValue(), p);
									added = c.size() > 0 ? true : false;
								}
							}
						}
					}
 * */
 