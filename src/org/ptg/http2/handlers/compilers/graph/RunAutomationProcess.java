/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.compilers.graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.ptg.eventloop.AutomationHelper;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.awt.BBox;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

import com.google.common.collect.Multimap;

import net.sf.json.JSONObject;

/*this class applies loopback automatically*/
public class RunAutomationProcess extends AbstractHandler {
	boolean persist = false;
	boolean trace = false;

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		boolean isDataFlow = request.getParameter("dataflow")==null?false:true;
		Map<String, Object> executionCtx = new HashMap<String, Object>();
		String instidStr = getUUIDStr(null);
		int loopCount = 1;
		try {
			Map<String, Object> ctx = runApp(instidStr, name, graphjson, loopCount, executionCtx,isDataFlow);
			response.getWriter().print(CommonUtil.toJson(ctx));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	public Map<String, Object> runApp(String uidStr, String name, String graphjson, int loopCount,
			Map<String, Object> executionCtx, boolean isDataFlow) {
		init(uidStr);
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		return runApp(uidStr, name, o, loopCount, executionCtx, null,isDataFlow);
	}

	public Map<String, Object> runApp(String uidStr, String name, FPGraph2 o, int loopCount,
			Map<String, Object> executionCtx, FPGraph2 parent, boolean isDataFlow) {
		List<TypeDefObj> types = o.getTypeDefs();
		Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		CommonUtil.getAnonFromUIModels(o, types, typeMap, anonCompMap);
		CommonUtil.updateGraphicProps(anonCompMap, o.getPorts());
		return createAndRunGraph(uidStr, name, o, loopCount, executionCtx, parent,isDataFlow);
	}

	public Map<String, Object> createAndRunGraph(String uidStr, String name, FPGraph2 o, int loopCount,
			Map<String, Object> executionCtx, FPGraph2 parent, boolean isDataFlow) {
		if (persist) {
			executionCtx = (Map<String, Object>) CommonUtil.getVar("exec-" + uidStr);
		}

		if (executionCtx == null) {
			executionCtx = new LinkedHashMap<String, Object>();
		}
		// here now add all the anontypes and type defs to this graph from
		// parent
		CommonUtil.mergeParent(o, parent);
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : o.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		for (int i = 0; i < loopCount; i++) {

			// iterate graph here
			String result = "";
			try {
				result = runGraph(name, uidStr, o, executionCtx, anonCompMap,isDataFlow);
				if (persist) {
					CommonUtil.saveVar("exec-" + uidStr, executionCtx);
				}
				for (Map.Entry<String, Object> en : executionCtx.entrySet()) {
					String k = en.getKey();
					Object v = en.getValue();
					PortObj po = o.getPorts().get(k);
					if (po != null) {
						ret.put(anonCompMap.get(po.getGrp()).getName() + "." + po.getPortname(), v);
					} else {
						ret.put(en.getKey(), en.getValue());
					}
				}
				ret.put("Iter_" + i + "_Result", result);
			} catch (Exception e) {
				StringBuilder error = new StringBuilder();
				error.append(e.getMessage());
				error.append("\n");
				for (StackTraceElement stack : e.getStackTrace()) {
					error.append(stack.toString());
					error.append("\n");
				}
				ret.put("Iter_" + i + "_Exception", error.toString());
			}
		}
		return ret;
	}

	public RunAutomationProcess() {

	}

	private String getUUIDStr(Map config) {
		return CommonUtil.getUUIDStr(config);
	}

	public void init(String instid) {

	}

	private void loadDynamicFunctions(String instid) {
		/*
		 * disabled for now when not disabled will pick all functions from the dynamic
		 * code directory. try { List<ITaskFunction> funs =
		 * CommonUtil.getTaskFunctions(null, true); for (ITaskFunction f : funs) {
		 * functions.put(f.getName(), f); } } catch (Exception e) { e.printStackTrace();
		 * }
		 */
	}

	public boolean isPersist() {
		return persist;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public String runGraph(String name, String uidStr, FPGraph2 o, Map<String, Object> ctx,
			Map<String, AnonDefObj> anonCompMap, boolean isDataFlow) throws Exception {
		final DirectedMultigraph<String, DefaultEdge> g = new DirectedMultigraph<String, DefaultEdge>(
				DefaultEdge.class);
		// dependencies are dependent: dependencies
		StringBuilder sb = new StringBuilder();
		//there ar e2 methods if we use all fp graph it will not add dependent other wise will add dependents
		if(isDataFlow)
			CommonUtil.covertFPGRaphToJGrapht(o, g);
		else
			CommonUtil.covertAllFPGRaphItemsToJGrapht(o, g);
		
		System.out.println(g);
		
		// sumit this is added in the last
		// g.addEdge("9", "6");
		CommonUtil.fixMultipleStarts(g, null);
		System.out.println(g);
		List<List<String>> cycles = detectCycles(g);
		System.out.println(cycles);
		Set<String> filerOut = new HashSet<>();
		Map<String, VNode<AnonDefObj>> mapVnode = new HashMap<>();
		for (Map.Entry<String, AnonDefObj> en : anonCompMap.entrySet()) {
			mapVnode.put(en.getKey(), new VNode<AnonDefObj>(en.getValue()));
		}
		for (List<String> cycle : cycles) {
			VNode<AnonDefObj> vnode = mapVnode.get(cycle.get(0));
			vnode.setSelf(anonCompMap.get(cycle.get(0)));
			for (int i = 1; i < cycle.size(); i++) {
				vnode.getChildren().add(anonCompMap.get(cycle.get(i)));
				g.removeEdge(cycle.get(i - 1), cycle.get(i));
				g.removeVertex(cycle.get(i));
			}
			mapVnode.put(vnode.getSelf().getId(), vnode);
			// remove g cycles and vnodes
		}
		System.out.println(g);
		List<String> ports = CommonUtil.topologicallySort(g);
		System.out.println(ports);
		System.out.println("Wait");
		for (String port : ports) {
			VNode<AnonDefObj> vnodeOBj = mapVnode.get(port);
			generateCodeVnode(o, ctx, sb, port, vnodeOBj, mapVnode);

		}
		System.out.println(sb);
		try {
			Map<String, String> params = new HashMap<String, String>();
			AnonDefObj anonInit = anonCompMap.get("Init(JavaCode)");
			StringBuilder preCodeSB = new StringBuilder();
			if (anonInit != null) {
				PortObj portObj = o.getPorts()
						.get("aux_" + anonInit.getId() + "." + anonInit.getId() + "AnnotationsToLoad.in.val");
				List<BBox> bboxs = CommonUtil.getGraphToAnnotations(portObj.getPortval());
				preCodeSB.append("/*region annotations goes here*/\n");
				for (BBox b : bboxs) {
					preCodeSB.append("org.sikuli.script.Region " + b.getId() + " = new org.sikuli.script.Region(" + b.x
							+ "," + b.y + "," + b.r + "," + b.b + ");/*" + b.getTag() + "*/");
					preCodeSB.append("\n");
				}
				preCodeSB.append("/*END region annotations goes here*/\n");
			}
			params.put("precode", preCodeSB.toString());

			Object runnable = CommonUtil.compileMappingGraph2(CommonUtil.getRandomString(8)/* "TestGenTaskToCode" */,
					sb.toString(), "Automate", params);
			if (runnable instanceof AutomationHelper) {
				AutomationHelper r = (AutomationHelper) runnable;
				r.run(ctx);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}

	private void generateCodeVnode(FPGraph2 o, Map<String, Object> ctx, StringBuilder sb, String port,
			VNode<AnonDefObj> v, Map<String, VNode<AnonDefObj>> mapVnode) {
		if (v == null) {
			System.out.println("Could not find vnode: " + port);
		} else {
			System.out.println("Running vnode: " + v.getSelf().getId());
			if (v.getChildren().size() > 0) {
				String codetoAdd = getCode2(o, ctx, v.getSelf());
				sb.append(codetoAdd);

				for (AnonDefObj child : v.getChildren()) {
					VNode<AnonDefObj> vnodeChild = mapVnode.get(child.getId());
					if (vnodeChild.getChildren().size() > 0) {
						generateCodeVnode(o, ctx, sb, port, vnodeChild, mapVnode);
					} else {
						System.out.println("\tRunning child: " + child.getId());
						String codetoAddChild = getCode(o, ctx, child);
						sb.append(codetoAddChild);
					}
				}
				sb.append("\n}\n}");

			} else {
				String codetoAdd = getCode(o, ctx, v.getSelf());
				sb.append(codetoAdd);

			}

		}
	}

	private String getCode(FPGraph2 o, Map<String, Object> ctx, AnonDefObj v) {
		FunctionPoint temp = o.getFps().get(v.getId());
		JSONObject jo = (JSONObject) temp.getXref();
		String code = (String) jo.get("script");
		code = extractPatternCode(o, v, ctx, code, "aux", "\\{([a-zA-Z0-9_.-]+):([a-zA-Z0-9 _.\\\"]+)\\}");
		code = extractPatternCode(o, v, ctx, code, "out", "\\<([a-zA-Z0-9_.-]+):([a-zA-Z0-9 _.\\\"]+)\\>");

		return "{/*" + v.getId() + "*/\n" + code + "\n}";
	}

	private String getCode2(FPGraph2 o, Map<String, Object> ctx, AnonDefObj v) {
		FunctionPoint temp = o.getFps().get(v.getId());
		JSONObject jo = (JSONObject) temp.getXref();
		String code = (String) jo.get("script");
		code = extractPatternCode(o, v, ctx, code, "aux", "\\{([a-zA-Z0-9_.-]+):([a-zA-Z0-9 _.\\\"]+)\\}");
		code = extractPatternCode(o, v, ctx, code, "out", "\\<([a-zA-Z0-9_.-]+):([a-zA-Z0-9 _.\\\"]+)\\>");

		return "{/*" + v.getId() + "*/\n" + code + "{\n";
	}

	private String extractPatternCode(FPGraph2 o, AnonDefObj v, Map<String, Object> ctx, String code, String ptype,
			String patternString) {
		Pattern p = Pattern.compile(patternString);
		Matcher m = p.matcher(code);
		String codetoAdd = code;
		StringBuilder vars = new StringBuilder();
		StringBuilder sets = new StringBuilder();

		int vi = 0;
		while (m.find()) {
			String find = code.substring(m.start(), m.end());
			System.out.println(find);
			String key = m.group(1);
			String val = m.group(2);
			System.out.println(key);
			System.out.println(val);
			PortObj portObj = o.getPorts().get(ptype + "_" + v.getId() + "." + v.getId() + key);
			if (portObj != null) {
				val = portObj.getPortval();

				for (ConnDef cd : o.getForward().values()) {
					if (cd.getTo().equals(portObj.getId())) {
						if (o.getPorts().get(cd.getFrom()) != null) {
							vars.append("String var" + vi + " = \"" + cd.getFrom() + "\";\n");
							if(cd.getConnCond()==null ||cd.getConnCond().length()<1)
								val = "ctx.get(var" + vi + ")";
						}
					}
				}
				if (ptype.equals("aux"))
					for (ConnDef cd : o.getForward().values()) {
						if (cd.getFrom().equals(portObj.getId())) {
							sets.append("ctx.put(\"" + portObj.getId() + "\"," + key + ");\n");
						}
					}
				if (ptype.equals("out"))
					for (ConnDef cd : o.getForward().values()) {
						if (cd.getFrom().equals(portObj.getId())) {
							sets.append("ctx.put(\"" + portObj.getId() + "\"," + key + ");\n");
							if(cd.getConnCond()!=null) {
								sets.append(cd.getConnCond().replace('"', ' ')+"("+  key +");");
							}
						}
					}
			}
			codetoAdd = codetoAdd.replace(find, val);
			vi++;
		}
		vars.append(codetoAdd);
		vars.append(sets);
		return vars.toString();
	}

	public List<List<String>> detectCycles(DirectedMultigraph<String, DefaultEdge> g) {
		System.out.println(g);
		SzwarcfiterLauerSimpleCycles<String, DefaultEdge> cycleDetector = new SzwarcfiterLauerSimpleCycles<>(g);
		List<List<String>> cycles = cycleDetector.findSimpleCycles();
		System.out.println(cycles);
		return cycles;
	}

}
