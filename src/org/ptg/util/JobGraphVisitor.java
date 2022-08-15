package org.ptg.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.SystemUtils;
import org.mozilla.javascript.Context;
import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.MultiGraph;

public class JobGraphVisitor <C extends CompilePath> implements IGraphVisitor<C > {
	FPGraph2 fpgraph2;
	String prop = "compStatus";
	Map config;
	public Map getConfig() {
		return config;
	}

	public void setConfig(Map config) {
		this.config = config;
	}

	boolean ignorePrevStatus=true;
	Map<String, String> cond = new LinkedHashMap<String,String>();
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
		depsResolvedLocal(curr, cp, g, graph, code,deps);
	}

	public void depsResolvedLocal(String curr, CompilePath cp, FPGraph g, MultiGraph<String, String> graph, Map<String, String> code,Collection<String > deps) {
		System.out.println("Currently at:\t" + curr);
		String sv = null;
		if(ignorePrevStatus==true){
			sv = "100";
		}else{
			sv = CommonUtil.getGraphItemValue(fpgraph2.getName(),curr,prop);
		}
		if(deps.size()>0){
		String [] depvals = new String[deps.size()];	
		int j=0;
		for(String d: deps){
			String depval = CommonUtil.getGraphItemValue(fpgraph2.getName(),d,prop);
			depvals[j] = depval;
			if(!depval.equals("100")){
				sv = "0";
				System.out.println(curr + " depends on "+d + " which is only complete "+depval);
			}
			j++;
		}
		}else{
		sv = "100";
		}
		code.put(curr, sv);
		Double val  = Double.parseDouble(sv);
		int i = val.intValue();
		if(i==100){
			JSONObject jobj = (JSONObject)cp.getStart().getXref();
			if(jobj!=null){
				System.out.println(curr +" finished now it is 100% <"+i+"> done.");
					try {
						if(jobj.containsKey("action")){
						String action = jobj.getString("action");
						action = getUpdatedCode(curr, action,cp);
						Context ctx = CommonUtil.beginScriptContext();
						CommonUtil.addObject("cp", cp);
						CommonUtil.addObject("curr", curr);
						CommonUtil.addObject("fpgraph", g);
						CommonUtil.addObject("config", config);
						CommonUtil.addObject("graph", graph);
						CommonUtil.addObject("sv", sv);
						System.out.println("now executing : "+action);
						CommonUtil.executeScript(action);
						sv = (String) CommonUtil.getObject("sv");
						CommonUtil.endScriptContext(ctx);
						}
						CommonUtil.updateGraphItemsOffline(fpgraph2.getName(),curr,prop,sv);
					} catch (Exception e) {
						CommonUtil.updateGraphItemsOffline(fpgraph2.getName(),curr,prop,"0");
						e.printStackTrace();
					}
				}
			}else{
				CommonUtil.updateGraphItemsOffline(fpgraph2.getName(),curr,prop,"0");
			}
		}

	@Override
	public void visitDependentNodeUnResolved(String  t, CompilePath c, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String , String > visited, Map<String , String > code, Collection<String > deps, Collection<String > depsR, Collection<String > depsU) {
		//System.out.println("visitDependentNodeUnResolved: "+t);		
	}

	@Override
	public void visitEndNodeDependencyResolved(String curr, CompilePath cp, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU) {
		depsResolvedLocal(curr, cp, g, graph, code,deps);
	}

	@Override
	public void visitEndNodeDependendencyUnResolved(String curr, CompilePath cp, int depth, FPGraph g,MultiGraph<String,String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR, Collection<String> depsU) {
				
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

	public boolean isIgnorePrevStatus() {
		return ignorePrevStatus;
	}

	public void setIgnorePrevStatus(boolean ignorePrevStatus) {
		this.ignorePrevStatus = ignorePrevStatus;
	}

	@Override
	public boolean multiFanoutAllowed() {
		return false;
	}
}
