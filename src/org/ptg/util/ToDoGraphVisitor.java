package org.ptg.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.SystemUtils;
import org.apache.tools.ant.util.StringUtils;
import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;
import org.mozilla.javascript.Context;
import org.ptg.processors.ConnDef;
import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.MultiGraph;

public class ToDoGraphVisitor <C extends CompilePath> implements IGraphVisitor<C > {
	FPGraph2 fpgraph2;
	
	Map<String, String> cond = new LinkedHashMap<String,String>();
	Symbols symbols = new Symbols();
	Map config;
	public Map getConfig() {
		return config;
	}

	public void setConfig(Map config) {
		this.config = config;
	}
	@Override
	public void visitNode(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code) {
		//System.out.println("visitNode: "+t);
	}

	@Override
	public void visitStart(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code) {
		//System.out.println("visitStart: "+t);		
	}

	@Override
	public void visitEnd(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code) {
		//System.out.println("visitEnd: "+t);
		
	}

	@Override
	public void visitDependentNodeResolved(String  curr, CompilePath cp, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code, Collection<String > deps, Collection<String > depsR, Collection<String > depsU) {
		//System.out.println("visitDependentNodeResolved: "+curr);
/////////////////
		System.out.println("Currently at:\t" + curr);
		String sv = code.get(cp.getStart().getName());
		String ev = code.get(cp.getEnd().getName());
		String svTotal = code.get(cp.getStart().getName()+"Total");
		String evTotal = code.get(cp.getEnd().getName()+"Total");

		if (sv == null) {
			sv = cp.getCode().get(cp.getStart().getName());
		}
//////////////////compute
		Collection<ConnDef> edges = g.getGraph().getOutEdges(cp.getStart());
		ConnDef d  = null;
		for(ConnDef dd: edges){
			if(dd.getTo().equals(cp.getEnd().getName())){
				d = dd;
				break;
			}
		}
		String connCond = d.getConnCond();
		if(connCond!=null && connCond.length()>0){
			connCond = StringUtils.replace(connCond,"%sv",sv);
			connCond = StringUtils.replace(connCond,"%from",d.getFrom());
			connCond = StringUtils.replace(connCond,"%to",d.getTo());

			
			cond.put(cp.getStart().getName(), connCond);
			try {
				sv = ""+symbols.eval(connCond);
			} catch (SyntaxException e) {
				e.printStackTrace();
			}
		}
		code.put(cp.getStart().getName(), sv);
		Double val  = Double.parseDouble(sv);
		int i = val.intValue();
		if(i>=100){
			JSONObject jobj = (JSONObject)cp.getStart().getXref();
			if(jobj!=null){
				System.out.println(cp.getStart().getName() +" finished now it is 100% <"+i+"> done.");
				if(jobj.containsKey("action")){
					String action = jobj.getString("action");
					action = getUpdatedCode(curr, action,cp);
					Context ctx = CommonUtil.beginScriptContext();
					CommonUtil.addObject("cp", cp);
					CommonUtil.addObject("curr", curr);
					CommonUtil.addObject("fpgraph", g);
					CommonUtil.addObject("config", config);
					CommonUtil.addObject("graph", graph);
					CommonUtil.executeScript(action);
					CommonUtil.endScriptContext(ctx);
				}
			}
		}
//////////////////compute
		if (ev == null) {
			ev = cp.getCode().get(cp.getEnd().getName());
			String valtoput =  ""+(Double.parseDouble(sv) + Double.parseDouble(ev));
			code.put(cp.getEnd().getName(), valtoput);
		} else {
			String valtoput =  ""+(Double.parseDouble(sv) + Double.parseDouble(ev));
			code.put(cp.getEnd().getName(), valtoput);
		}
		if (svTotal == null) {
			svTotal = "100";
			code.put(cp.getStart().getName()+"Total",svTotal);
		}
		if (evTotal == null) {
			evTotal = "100";
			String codeToPut = ""+(Double.parseDouble(svTotal) + Double.parseDouble(evTotal));
			code.put(cp.getEnd().getName()+"Total", codeToPut);
		} else {
			String codeToPut = ""+(Double.parseDouble(svTotal) + Double.parseDouble(evTotal));
			code.put(cp.getEnd().getName()+"Total", codeToPut);
		}
			val  = (Double.parseDouble(sv) + Double.parseDouble(ev));
		 i = val.intValue();
		 Double val2 = Double.parseDouble(svTotal) + Double.parseDouble(evTotal);
		 int itotal = val2.intValue();
			if(i==itotal){
				System.out.println(cp.getEnd().getName() +" finished now it is 100% <"+i+"/"+itotal+">done.");
				JSONObject jobj = (JSONObject)cp.getEnd().getXref();
				if(jobj!=null){
					if(jobj.containsKey("action")){
						String action = jobj.getString("action");
						Context ctx = CommonUtil.beginScriptContext();
						CommonUtil.addObject("cp", cp);
						CommonUtil.addObject("curr", curr);
						CommonUtil.addObject("fpgraph", g);
						CommonUtil.addObject("graph", graph);
						CommonUtil.executeScript(action);
						CommonUtil.endScriptContext(ctx);
					}
				}
			}
////////////////////////////
	}

	@Override
	public void visitDependentNodeUnResolved(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code, Collection<String > deps, Collection<String > depsR, Collection<String > depsU) {
		//System.out.println("visitDependentNodeUnResolved: "+t);		
	}

	@Override
	public void visitEndNodeDependencyResolved(String t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEndNodeDependendencyUnResolved(String t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StringBuilder getCode(Map<String, String> code) {
		StringBuilder toret = new StringBuilder();
		for (Map.Entry<String, String> cd : code.entrySet()) {
			toret.append("m.put(\""+cd.getKey() + "\",new java.lang.Double(" + cd.getValue()+" ) ) ;"+SystemUtils.LINE_SEPARATOR);
		}
		return toret;
	}

	public FPGraph2 getFpgraph2() {
		return fpgraph2;
	}

	public void setFpgraph2(FPGraph2 fpgraph2) {
		this.fpgraph2 = fpgraph2;
	}
	public String getUpdatedCode(String key,String value,CompilePath cp){
		Map<String, JSONObject> rc = fpgraph2.getOrphans();
		boolean added = false;
		Map<String, String> c = null;
		for (Map.Entry<String, JSONObject> en : rc.entrySet()) {
			String type = en.getValue().getString("type");
			String rid = en.getValue().getString("id");
			if (type != null && type.equals("region")) {
				JSONArray items = (JSONArray) en.getValue().get("items");
				for (Object o : items) {
					if (o.toString().equals(key)) {
						System.out.println("region " + rid + " contains " + o);
						Map<String, Object> p = new HashMap<String, Object>();
						p.put("cp", cp);
						p.put("c", key);
						p.put("v",value);
						p.put("r", en.getValue());
						c = CommonUtil.compileOrhpahDefinition(en.getValue(), p);
						added = c.size() > 0 ? true : false;
					}
				}
			}
		}
		if (added) {
			return (c.values().iterator().next());
		} else {
			return value;
			}
	}

	@Override
	public boolean multiFanoutAllowed() {
		return true;
	}
}
