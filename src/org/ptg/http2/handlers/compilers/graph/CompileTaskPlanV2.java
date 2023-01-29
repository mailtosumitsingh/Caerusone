/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.compilers.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.ContPublisherWriter;
import org.ptg.util.DefaultTaskExecEventListener;
import org.ptg.util.ITaskExecEventListener;
import org.ptg.util.ITaskFunction;
import org.ptg.util.events.TaskTraceEvent;
import org.ptg.util.graph.ComplexNodeHandler;
import org.ptg.util.graph.PNode;
import org.ptg.util.graph.ParallelComplexNodeHandler;
import org.ptg.util.graph.SimpleNodeHandler;
import org.ptg.util.graph.WaitStruct;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
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

import com.google.common.collect.Multimap;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

/*this class applies loopback automatically*/
public class CompileTaskPlanV2 extends AbstractHandler {
	Map<String, ITaskExecEventListener> listners = new HashMap<String, ITaskExecEventListener>();
	Map<String, ITaskFunction> functions = new LinkedHashMap<String, ITaskFunction>();
	boolean persist = false;
	boolean trace = false;

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String trace = request.getParameter("trace");
		this.trace = trace != null ? true : false;
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String loop = request.getParameter("loopcount");
		String graphconfig = request.getParameter("graphconfig");
		if (graphconfig == null) {
			graphconfig = "{}";
		}
		Map<String, Object> executionCtx = null;
		Map config = CommonUtil.getConfigFromJsonData(graphconfig);
		String instidStr = getUUIDStr(config);
		int loopCount = 1;
		if (loop != null) {
			loopCount = Integer.parseInt(loop);
		}
		try {
			Map<String, Object> ctx = runApp(instidStr, name, graphjson, loopCount, executionCtx);
			response.getWriter().print(CommonUtil.toJson(ctx));
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n" + e);
			e.printStackTrace();
		}

		((Request) request).setHandled(true);
	}

	public Map<String, Object> runApp(String uidStr, String name, String graphjson, int loopCount, Map<String, Object> executionCtx) {
		init(uidStr);
		FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
		return runApp(uidStr, name, o, loopCount, executionCtx, null);
	}

	public Map<String, Object> runApp(String uidStr, String name, FPGraph2 o, int loopCount, Map<String, Object> executionCtx, FPGraph2 parent) {
		List<TypeDefObj> types = o.getTypeDefs();
		Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		CommonUtil.getAnonFromUIModels(o, types, typeMap, anonCompMap);
		CommonUtil.updateGraphicProps(anonCompMap, o.getPorts());
		return createAndRunGraph(uidStr, name, o, loopCount, executionCtx, parent);
	}

	public Map<String, Object> createAndRunGraph(String uidStr, String name, FPGraph2 o, int loopCount, Map<String, Object> executionCtx, FPGraph2 parent) {
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
			runGraph(uidStr, o, executionCtx, anonCompMap);
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
		}
		return ret;
	}

	public CompileTaskPlanV2() {

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
		/*
		 * disabled for now when not disabled will pick all functions from the
		 * dynamic code directory. try { List<ITaskFunction> funs =
		 * CommonUtil.getTaskFunctions(null, true); for (ITaskFunction f : funs)
		 * { functions.put(f.getName(), f); } } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}

	public boolean isPersist() {
		return persist;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public void runGraph(String uidStr, FPGraph2 o, Map<String, Object> ctx, Map<String, AnonDefObj> anonCompMap) {
		final DirectedMultigraph<String, DefaultEdge> g = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
		listners.put("default", new DefaultTaskExecEventListener());
		beforeProcessStart(uidStr);
		// dependencies are dependent: dependencies
		Multimap<String, String> dependencies = CommonUtil.covertFPGRaphToJGrapht(o, g);
		// sumit this is added in the last
		// g.addEdge("9", "6");
		CommonUtil.fixMultipleStarts(g, null);
		List<ConnDef> torem = CommonUtil.getLoops(g);
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
		ComplexNodeHandler ch = new ComplexNodeHandler(functions);
		SimpleNodeHandler sh = new SimpleNodeHandler(functions);
		ParallelComplexNodeHandler psh = new ParallelComplexNodeHandler(functions);
		Map<String, WaitStruct> waitList = CommonUtil.getWaitListFromDependencies(dependencies);
		// start execution

		for (PNode pnode : pnodes.values()) {
			analyze(uidStr, sh, ch, psh, ctx, pnode, null, waitList, o, anonCompMap, allPnodes, this);
		}

		System.out.println("Done Execution");
		afterProcessFinished(uidStr);
		listners.remove("default");
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

	public void analyze(String uidStr, SimpleNodeHandler sh, ComplexNodeHandler ch, ParallelComplexNodeHandler psh, Map<String, Object> ctx, PNode pnode, PNode parent,
			Map<String, WaitStruct> waitList, FPGraph2 o, Map<String, AnonDefObj> anonCompMap, Map<String, PNode> allPnodes, CompileTaskPlanV2 v2) {
		List<PNode> childs = null;
		List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
		List<FunctionPortObj> outputs = new ArrayList<FunctionPortObj>();
		String currName = pnode.getName();
		if (currName.startsWith("$") || currName.startsWith("#")) {
			String[] part = StringUtils.split(currName, "$");
			currName = part[0];
		}

		AnonDefObj curr = anonCompMap.get(currName);
		if (curr != null) {
			for (String str : curr.getInputs()) {
				CommonUtil.processInputs(o, o.getPorts(), curr, inputs, str);
			}
			for (String str : curr.getOutputs()) {
				CommonUtil.processOutputs(o, o.getPorts(), curr, outputs, str);
			}
		}

		if (pnode.getChilds().size() > 1) {
			if (curr != null && curr.getTags() != null && curr.getTags().contains("AllAtOnce")) {
				childs = psh.handle(curr, inputs, outputs, uidStr, sh, ch, psh, ctx, pnode, parent, waitList, o, anonCompMap, allPnodes, this);
				if (curr != null) {
					taskExecuted(uidStr, curr);
				}
			} else {
				childs = ch.handle(pnode, parent, waitList, curr, ctx, inputs, outputs, o, anonCompMap, this);
				if (curr != null) {
					taskExecuted(uidStr, curr);
				}
			}
		} else {
			childs = sh.handle(pnode, parent, waitList, curr, ctx, inputs, outputs, o, anonCompMap, this);
			if (curr != null) {
				taskExecuted(uidStr, curr);
			}
		}
		CommonUtil.saveVar("exec-" + uidStr, ctx);
		if (trace) {
			ContPublisherWriter.getInstance().loadEvent(new TaskTraceEvent(ctx, pnode.getName()));
		}

		if (childs != null) {
			for (PNode p : childs) {
				analyze(uidStr, sh, ch, psh, ctx, p, pnode, waitList, o, anonCompMap, allPnodes, this);
			}
		}
		if (curr != null) {
			CommonUtil.updateWaitListWithDone(waitList, curr.getId());
		}
		/* before executing the child nodes execute any ready task */
		List<String> runFirstList = new LinkedList<String>();
		for (Map.Entry<String, WaitStruct> wen : waitList.entrySet()) {
			if (wen.getValue().isReady()) {
				runFirstList.add(wen.getKey());
			}
		}
		for (String s : runFirstList) {
			PNode p = allPnodes.get(s);
			analyze(uidStr, sh, ch, psh, ctx, p, pnode, waitList, o, anonCompMap, allPnodes, this);
		}
	}

	public void addListener(String token, ITaskExecEventListener list) {
		listners.put(token, list);
	}

	public void removeListener(String token) {
		listners.remove(token);
	}
}
