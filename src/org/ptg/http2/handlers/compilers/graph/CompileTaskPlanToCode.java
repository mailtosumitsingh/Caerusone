/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.compilers.graph;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.ptg.processors.ConnDef;
import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.DebugGraphToCodeEventListener;
import org.ptg.util.DefaultTaskExecEventListener;
import org.ptg.util.DiagnosticException;
import org.ptg.util.ForkType;
import org.ptg.util.IGraphToCodeEventListener;
import org.ptg.util.IOpencvProcessor;
import org.ptg.util.IRunnable;
import org.ptg.util.ITaskExecEventListener;
import org.ptg.util.ITaskFunction;
import org.ptg.util.JavaGraphToCodeEventListener;
import org.ptg.util.JavaScriptGraphToCodeEventListener;
import org.ptg.util.filters.AnonDefObjPredicateFilter;
import org.ptg.util.graph.PNode;
import org.ptg.util.graph.WaitStruct;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.StepObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;
import org.ptg.util.taskfunctions.CommandTask;
import org.ptg.util.taskfunctions.ConcatTask;
import org.ptg.util.taskfunctions.ConstantTask;
import org.ptg.util.taskfunctions.DisplayTask;
import org.ptg.util.taskfunctions.EndTask;
import org.ptg.util.taskfunctions.FunctionTask;
import org.ptg.util.taskfunctions.IfTask;
import org.ptg.util.taskfunctions.LogTask;
import org.ptg.util.taskfunctions.LongRunningCommandTask;
import org.ptg.util.taskfunctions.LoopBackTask;
import org.ptg.util.taskfunctions.MethodCallTask;
import org.ptg.util.taskfunctions.RunGraphTask;
import org.ptg.util.taskfunctions.TraceTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/*this class applies loopback automatically*/
public class CompileTaskPlanToCode extends AbstractHandler {
	public static final String DEBUG_PRINTF = "debugPrintf";
	public static final String JAVA_SRC = "javasrc";
	public static final String JAVA_SCRIPT_SRC = "javascriptsrc";
	Map<String, ITaskExecEventListener> listners = new HashMap<String, ITaskExecEventListener>();
	Map<String, ITaskFunction> functions = new LinkedHashMap<String, ITaskFunction>();
	boolean persist = false;
	boolean trace = false;
	Map<String, IGraphToCodeEventListener> codeEventListeners = new HashMap<String, IGraphToCodeEventListener>();

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String trace = request.getParameter("trace");
		this.trace = trace != null ? true : false;
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String graphconfig = request.getParameter("graphconfig");
		String handlerKey = request.getParameter("handler");
		String mappingType = "TaskToCode";
		if (request.getParameter("mappingtype") != null) {
			mappingType = request.getParameter("mappingtype");
		}
		if (handlerKey == null) {
			handlerKey = DEBUG_PRINTF;
		}
		if (graphconfig == null) {
			graphconfig = "{}";
		}
		Map config = CommonUtil.getConfigFromJsonData(graphconfig);
		String instidStr = getUUIDStr(config);
		CodeBlock ret = runApp(instidStr, name, graphjson, handlerKey);
		try {
			Object o = CommonUtil.compileMappingGraph2(name/* "TestGenTaskToCode" */, ret.build(), mappingType, null);
			if (o instanceof IRunnable) {
				IRunnable r = (IRunnable) o;
				r.run();
			} else if (o instanceof org.apache.commons.collections.Closure) {
				org.apache.commons.collections.Closure c = (Closure) o;
				c.execute(null);
			} else if (o instanceof IOpencvProcessor) {
				final IOpencvProcessor p = (IOpencvProcessor) o;
				new Thread(new Runnable() {
					@Override
					public void run() {
						p.restart();
						p.process();
					}
				}).start();
			}
			response.getWriter().print(ret.build());
		} catch (RuntimeException e) {
			e.printStackTrace();
			if (e.getCause() instanceof DiagnosticException) {
			} else if (e.getCause() instanceof DiagnosticException) {
				DiagnosticException d = (DiagnosticException) e.getCause();
				long line = d.getDiagnostic().getLineNumber();
				String comp = ret.getLineToComp().get(line - 57/*
																 * base line
																 * code that is
																 * used by the
																 * template
																 */);
				String ln = ret.build().split("\n")[(int) (line - 57 - 1)];
				ln = StringEscapeUtils.escapeJavaScript(ln);
				if (comp != null) {
					System.out.println("Problem with Component: " + comp);
					if (comp != null && comp.length() > 0) {
						response.getOutputStream().print(
								"{\"status\":\"fail\",\"msg\":\"Couldn not Compile " + StringEscapeUtils.escapeJavaScript(e.toString()) + "[generatedCode: " + ln + "]" + "\",\"component\":\"" + comp
										+ "\"}");
					} else {
						response.getOutputStream()
								.print("{\"status\":\"fail\",\"msg\":\"Couldn not Compile" + StringEscapeUtils.escapeJavaScript(e.toString()) + "[generatedCode: " + ln + "]" + "\"}");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getOutputStream().print("{\"status\":\"fail\",\"msg\":\"Couldn not Compile" + e + "\"}");
		}
		((Request) request).setHandled(true);
	}

	public CodeBlock runApp(String uidStr, String name, String graphjson, String handlerKey) {
		init(uidStr);
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		return runApp(uidStr, name, o, null, handlerKey, true);
	}

	public CodeBlock runApp(String uidStr, String name, FPGraph2 o, FPGraph2 parent, String handlerKey, boolean addCondVars) {
		CommonUtil.mergeParent(o, parent);
		List<TypeDefObj> types = o.getTypeDefs();
		Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		CommonUtil.getAnonFromUIModels(o, types, typeMap, anonCompMap);
		CommonUtil.updateGraphicProps(anonCompMap, o.getPorts());
		return createAndRunGraph(uidStr, name, o, parent, handlerKey, addCondVars);
	}

	public CodeBlock createAndRunGraph(String uidStr, String name, FPGraph2 o, FPGraph2 parent, String handlerKey, boolean addCondVars) {
		// here now add all the anontypes and type defs to this graph from
		// parent
		CommonUtil.mergeParent(o, parent);
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : o.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		return runGraph(uidStr, o, anonCompMap, handlerKey, addCondVars);
	}

	public CompileTaskPlanToCode() {
		codeEventListeners.put(DEBUG_PRINTF, new DebugGraphToCodeEventListener());
		codeEventListeners.put(JAVA_SRC, new JavaGraphToCodeEventListener());
		codeEventListeners.put(JAVA_SCRIPT_SRC, new JavaScriptGraphToCodeEventListener());

	}

	private String getUUIDStr(Map config) {
		return CommonUtil.getUUIDStr(config);
	}

	public void init(String instid) {
		functions.put("concat", new ConcatTask().setInstId(instid));
		functions.put("constant", new ConstantTask().setInstId(instid));
		functions.put("log", new LogTask().setInstId(instid));
		functions.put("functionCall", new FunctionTask().setInstId(instid));
		functions.put("functionCall2", new MethodCallTask().setInstId(instid));
		functions.put("trace", new TraceTask().setInstId(instid));

		functions.put("if", new IfTask().setInstId(instid));
		functions.put("end", new EndTask().setInstId(instid));
		functions.put("uidisplay", new DisplayTask().setInstId(instid));
		functions.put("loopback", new LoopBackTask().setInstId(instid));
		functions.put("rungraph", new RunGraphTask().setInstId(instid));
		functions.put("command", new CommandTask().setInstId(instid));
		functions.put("longCmd", new LongRunningCommandTask().setInstId(instid));
		functions.put("MethodCall", new MethodCallTask().setInstId(instid));

		loadDynamicFunctions(instid);
		for (ITaskFunction tf : functions.values()) {
			tf.setPersistable(persist);
		}
	}

	private void loadDynamicFunctions(String instid) {
	}

	public boolean isPersist() {
		return persist;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public CodeBlock runGraph(String uidStr, FPGraph2 o, Map<String, AnonDefObj> anonCompMap, String handlerKey, boolean addCondVars) {
		final DirectedMultigraph<String, DefaultEdge> g = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
		IGraphToCodeEventListener handler = codeEventListeners.get(handlerKey);
		handler.init();
		listners.put("default", new DefaultTaskExecEventListener());
		beforeProcessStart(uidStr);
		// dependencies are dependent: dependencies
		AnonDefObjPredicateFilter anonObjFilter = new AnonDefObjPredicateFilter(anonCompMap);
		Multimap<String, String> dependencies = CommonUtil.covertAllFPGRaphItemsToJGrapht(o, g, anonObjFilter);
		// sumit this is added in the last
		// g.addEdge("9", "6");
		CommonUtil.fixMultipleStarts(g, anonObjFilter);
		List<ConnDef> torem = CommonUtil.getFeedBackLoops(g);
		Set<String> loopBackTargets = new HashSet<String>();
		if (torem != null) {
			for (ConnDef cd : torem) {
				loopBackTargets.add(cd.getTo());
			}
		}
		CommonUtil.convertLoopsToVirtualActivity(g, torem);
		// done.clear(); is useless operation
		List<String> ports = CommonUtil.topologicallySort(g);
		int i = 0;
		Map<String, PNode> pnodes = new LinkedHashMap<String, PNode>();
		i = CommonUtil.graphToHierPNodes(g, ports, i, pnodes);
		// levelize graph
		Map<Integer, Set<PNode>> gByLevel = CommonUtil.setPnodeLevels(pnodes, g);
		// step wise graph
		Map<String, StepObj> steps = o.getSteps();
		// //////////////////////////////////////////////////////
		// /////////////////////////////////////////////////////
		setGraphLevels(uidStr, gByLevel);
		Map<String, Set<AnonDefObj>> tasksBySteps = CommonUtil.getTasksByStepsImplicit(o, steps);
		setGraphSteps(uidStr, steps, tasksBySteps);

		Map<String, PNode> allPnodes = new LinkedHashMap<String, PNode>();
		allPnodes.putAll(pnodes);
		CommonUtil.removeNonRoot(pnodes, g);
		Map<String, WaitStruct> waitList = CommonUtil.getWaitListFromDependencies(dependencies);
		// start execution
		CodeBlock code = new CodeBlock();
		code.init(o.getName());
		if (addCondVars) {
			code.addCodeLine("boolean cond = false;", "STARTBLOCK");

			for (String ss : allPnodes.keySet()) {
				code.addCodeLine("boolean cond" + CommonUtil.convertToJavaId(ss) + " = true;", "STARTBLOCK");
			}
		}
		for (PNode pnode : pnodes.values()) {
			analyze(uidStr, pnode, null, waitList, o, anonCompMap, allPnodes, this, code, loopBackTargets, handler);
		}

		System.out.println("Done Execution");
		afterProcessFinished(uidStr);
		listners.remove("default");
		return code;
	}

	public void analyze(String uidStr, final PNode pnode, final PNode parent, Map<String, WaitStruct> waitList, final FPGraph2 o, final Map<String, AnonDefObj> anonCompMap,
			Map<String, PNode> allPnodes, CompileTaskPlanToCode v2, CodeBlock code, Set<String> loopBackTargets, IGraphToCodeEventListener handler) {
		List<PNode> childs = null;
		ForkType ftype = null;
		String currName = pnode.getName();
		if (currName.startsWith("$") || currName.startsWith("#")) {
			String[] part = StringUtils.split(currName, "$");
			currName = part[0];
		}

		AnonDefObj curr = anonCompMap.get(currName);
		if (curr != null) {
			System.out.println("Now analyzing : " + curr.getName());
		} else {
			curr = new AnonDefObj();
			curr.setId("DUMMY_VIRTUAL_NODE");
			curr.setAnonType("VIRTUAL");
		}

		childs = pnode.getChilds();
		if (childs.size() > 1) {
			if (getForkType(curr).equals(ForkType.If)) {
				sortIfCollection(pnode, o, childs);
			} else if (getForkType(curr).equals(ForkType.SWITCH)) {
				sortSwitchCollection(pnode, o, childs);
			} else {
				sortUnkCollection(pnode, o, childs);
			}
		}

		if (loopBackTargets.contains(currName)) {
			handler.handleLoopBackTarget(code, currName, curr, o);
		}
		if (childs != null && childs.size() > 1) {
			ConnDef currCd = null;
			if (parent != null && pnode != null) {
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getFrom().equals(parent.getName()) && cd.getTo().equals(pnode.getName())) {
						currCd = cd;
						break;
					}
				}
			}
			handler.handleFork(code, currName, loopBackTargets.contains(currName), getForkType(curr), curr, currCd, o);
		}

		if (pnode.getName().startsWith("$")) {
			String breakOrContinue = "continue";
			String child = pnode.getName();
			if (child.startsWith("$") || child.startsWith("#")) {
				String[] part = StringUtils.split(child, "$");
				child = part[0];
			}

			for (ConnDef cd : o.getForward().values()) {
				if (cd.getFrom().equals(parent.getName()) && cd.getTo().equals(child)) {
					if (cd.getTags() != null && cd.getTags().contains("break")) {
						breakOrContinue = "break";
						break;
					}
				}
			}
			handler.handleGenerateCode(code, currName, breakOrContinue, true, curr, o);
		} else {
			handler.handleGenerateCode(code, currName, null, false, curr, o);
		}

		int fcount = 0;
		int tcount = childs.size();
		ConnDef cdChild = null;
		ftype = getForkType(pnode, anonCompMap);
		for (PNode p : childs) {
			String child = p.getName();
			if (child.startsWith("$") || child.startsWith("#")) {
				String[] part = StringUtils.split(child, "$");
				child = part[0];
			}
			if (tcount > 1) {
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getFrom().equals(pnode.getName()) && cd.getTo().equals(child)) {

						handler.handleForkCondition(code, cd, fcount, tcount, ftype, curr, o);
						break;
					}
				}
				handler.handleForkBlockBegin(code, curr);
			} else {
				handler.handleNormalBlock(code, curr);
			}
			if (p.getName().startsWith("$")) {
				String[] part = StringUtils.split(p.getName(), "$");
				String breakOrContinue = "continue";
				String child2 = p.getName();
				if (child2.startsWith("$") || child2.startsWith("#")) {
					String[] child2parts = StringUtils.split(child2, "$");
					child2 = child2parts[0];
				}
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getFrom().equals(pnode.getName()) && cd.getTo().equals(child2)) {
						if (cd.getTags() != null && cd.getTags().contains("break")) {
							breakOrContinue = "break";
							break;
						}
					}
				}
				AnonDefObj childAnon = anonCompMap.get(child2);
				handler.handleGenerateCode(code, part[0], breakOrContinue, true, childAnon, o);
			} else {
				analyze(uidStr, p, pnode, waitList, o, anonCompMap, allPnodes, v2, code, loopBackTargets, handler);
			}
			if (childs != null && childs.size() > 1) {
				handler.handleEndChild(code, curr, p);
			}
			fcount++;

		}
		if (childs != null && childs.size() > 1) {
			handler.handleForkFinish(code, currName, loopBackTargets.contains(currName), ftype, curr);
		}
		if (loopBackTargets.contains(currName)) {
			handler.handleLoopBackTargetFinish(code, currName, curr);
		}

	}

	private ForkType getForkType(PNode p, Map<String, AnonDefObj> anonCompMap) {
		AnonDefObj ao = anonCompMap.get(p.getName());
		return getForkType(ao);
	}

	private ForkType getForkType(AnonDefObj ao) {
		if (ao != null) {
			if (ao.getAnonType() != null && ao.getAnonType().equalsIgnoreCase("switch")) {
				return ForkType.SWITCH;
			} else if (ao.getAnonType() != null && ao.getAnonType().equalsIgnoreCase("if")) {
				return ForkType.If;
			}
		}
		return ForkType.UNK;
	}

	public void addListener(String token, ITaskExecEventListener list) {
		listners.put(token, list);
	}

	public void removeListener(String token) {
		listners.remove(token);
	}

	public void setGraphLevels(String uuid, Map<Integer, Set<PNode>> gByLevel) {
		for (ITaskExecEventListener l : listners.values()) {
			l.setGraphLevels(uuid, gByLevel);
		}
	}

	public void beforeProcessStart(String uuid) {
		for (ITaskExecEventListener l : listners.values()) {
			l.beforeProcessStart(uuid);
		}
	}

	public void afterProcessFinished(String uuid) {
		for (ITaskExecEventListener l : listners.values()) {
			l.afterProcessStart(uuid);
		}
	}

	public void setGraphSteps(String uuid, Map<String, StepObj> steps, Map<String, Set<AnonDefObj>> tasksBySteps) {
		for (ITaskExecEventListener l : listners.values()) {
			l.setGraphSteps(uuid, steps, tasksBySteps);
		}
	}

	public void taskExecuted(String uuid, AnonDefObj obj) {
		for (ITaskExecEventListener l : listners.values()) {
			l.taskExecuted(uuid, obj);
		}
	}

	private void sortSwitchCollection(final PNode pnode, final FPGraph2 o, List<PNode> childs) {
		Collections.sort(childs, new Comparator<PNode>() {
			@Override
			public int compare(PNode o1, PNode o2) {
				ConnDef cd1 = null, cd2 = null;
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getFrom().equals(pnode.getName())) {
						if (cd.getTo().equals(o1.getName())) {
							cd1 = cd;
						} else if (cd.getTo().equals(o2.getName())) {
							cd2 = cd;
						}
					}
				}
				if (cd1.getSequence() != null && !cd1.getSequence().equals("0") && cd2.getSequence() != null && !cd2.getSequence().equals("0")) {
					try {
						int i1 = Integer.parseInt(cd1.getSequence());
						int i2 = Integer.parseInt(cd2.getSequence());
						return i1 - i2;
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				return -1;
			}
		});
	}

	private void sortIfCollection(final PNode pnode, final FPGraph2 o, List<PNode> childs) {
		Collections.sort(childs, new Comparator<PNode>() {
			@Override
			public int compare(PNode o1, PNode o2) {
				String cd1 = null, cd2 = null;
				for (ConnDef cd : o.getForward().values()) {
					if (cd.getFrom().equals(pnode.getName())) {
						if (cd.getTo().equals(o1.getName())) {
							cd1 = cd.getConnCond();
						} else if (cd.getTo().equals(o2.getName())) {
							cd2 = cd.getConnCond();
						}
					}
				}
				if (cd1 != null && cd2 != null) {
					if (cd1.equals("true")) {
						return -1;
					} else if (cd1.equals("false") && !cd2.equals("true") || cd2.equals("false") && !cd1.equals("true")) {
						if (cd1.equals("false")) {
							return -1;
						} else {
							return 1;
						}
					} else {
						return 1;
					}
				}
				return -1;
			}
		});
	}

	private void sortUnkCollection(final PNode pnode, final FPGraph2 graph, List<PNode> childs) {
		Map<Integer, ConnDef> sorted = new TreeMap<Integer, ConnDef>();
		Map<String, PNode> pnodes = new HashMap<String, PNode>();
		for (PNode p : childs) {
			pnodes.put(p.getName(), p);
		}
		int i = 100;
		List<PNode> nodes = Lists.newLinkedList();
		for (PNode pn : childs) {
			i++;
			for (ConnDef cd : graph.getForward().values()) {
				if (cd.getFrom().equals(pnode.getName()) && cd.getTo().equals(pn.getName())) {
					if (cd.getSequence() == null || cd.getSequence().length() < 1 || cd.getSequence().equals("0")) {
						cd.setSequence("" + i);
					}
					sorted.put(Integer.parseInt(cd.getSequence()), cd);
					nodes.add(pn);
					break;
				}
			}
		}
		for (PNode pn : nodes) {
			childs.remove(pn);
		}
		for (Integer id : sorted.keySet()) {
			childs.add(pnodes.get(sorted.get(id).getTo()));
		}
	}
}
