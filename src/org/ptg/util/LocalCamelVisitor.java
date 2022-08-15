package org.ptg.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.javia.arity.Symbols;
import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.MultiGraph;

public class LocalCamelVisitor<C extends CompilePath> implements IGraphVisitor<C> {
	FPGraph2 fpgraph2;
	Symbols symbols = new Symbols();

	public void init() {
	}

	@Override
	public void visitNode(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code) {
		System.out.println("visitNode: " + t);
	}

	@Override
	public void visitStart(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code) {
		System.out.println("visitStart: " + t);
	}

	@Override
	public void visitEnd(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code) {
		System.out.println("visitEnd: " + t);

	}

	@Override
	public void visitDependentNodeResolved(String curr, CompilePath cp, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR,
			Collection<String> depsU) {
		depsResolvedLocal(curr, cp, g, graph, code, deps);
	}

	public void depsResolvedLocal(String curr, CompilePath cp, FPGraph g, MultiGraph<String, String> graph, Map<String, String> code, Collection<String> deps) {
		System.out.println(curr);
		System.out.println(cp.getStart().getName() + ":" + cp.getEnd().getName());
		System.out.println(deps);
		FunctionPoint node = cp.getStart().getName().equals(curr) ? cp.getStart() : cp.getEnd();
		String actualCurr = node.getName();
		System.out.println("ActualNode: " + actualCurr);
		JSONObject jobj = (JSONObject) node.getXref();
		if (jobj != null) {
			try {
				{
					if(jobj.containsKey("code")){
					String action = jobj.getString("code");
					action = getUpdatedCode(actualCurr, action, cp);
					code.put(actualCurr, action);
					}else{
						code.put(actualCurr, actualCurr);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void visitDependentNodeUnResolved(String t, CompilePath c, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR,
			Collection<String> depsU) {
		System.out.println("visitDependentNodeUnResolved: " + t);
	}

	@Override
	public void visitEndNodeDependencyResolved(String curr, CompilePath cp, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR,
			Collection<String> depsU) {
		depsResolvedLocal(curr, cp, g, graph, code, deps);
	}

	@Override
	public void visitEndNodeDependendencyUnResolved(String curr, CompilePath cp, int depth, FPGraph g, MultiGraph<String, String> graph, Map<String, String> visited, Map<String, String> code, Collection<String> deps, Collection<String> depsR,
			Collection<String> depsU) {
		System.out.println("visitDependentNodeUnResolved: " + curr);
	}

	@Override
	public StringBuilder getCode(Map<String, String> code) {
		StringBuilder toret = new StringBuilder();
		for (Map.Entry<String, String> cd : code.entrySet()) {
			toret.append(cd.getValue());
		}
		return toret;
	}

	public FPGraph2 getFpgraph2() {
		return fpgraph2;
	}

	public void setFpgraph2(FPGraph2 fpgraph2) {
		this.fpgraph2 = fpgraph2;
	}

	public String getUpdatedCode(String key, String value, CompilePath cp) {
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
						p.put("v", value);
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
		return false;
	}
	public Map getConfig() {
		return null;
	}

	public void setConfig(Map config) {
	}
}
