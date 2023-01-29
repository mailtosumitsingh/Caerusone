/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.handlers.CodeToPortJava;
import org.ptg.http2.handlers.GetAnnotSpecs;
import org.ptg.http2.handlers.GetAnnotations;
import org.ptg.http2.handlers.GetCanvas;
import org.ptg.http2.handlers.GetMenuGroup;
import org.ptg.http2.handlers.GetSQLLayout;
import org.ptg.http2.handlers.GetTaskSpecs;
import org.ptg.http2.handlers.ReprocessRegions;
import org.ptg.http2.handlers.compilers.graph.CompileTaskPlanToCode;
import org.ptg.http2.handlers.compilers.graph.RunUICodeFlow;
import org.ptg.http2.handlers.executors.ExecuteGraph;
import org.ptg.http2.handlers.generators.GenerateFunctionBlocks;
import org.ptg.http2.handlers.generators.GenerateFunctionBlocks4;
import org.ptg.http2.handlers.generators.GenerateFunctionBlocksGeneric;
import org.ptg.http2.handlers.geom.PathToPoints;
import org.ptg.http2.handlers.geom.SVGExtracterToShape;
import org.ptg.http2.handlers.geom.ShapeMapper;
import org.ptg.http2.handlers.geom.ShapeOffset;
import org.ptg.http2.handlers.geom.ShapePixalate;
import org.ptg.http2.handlers.geom.TransformHandler;
import org.ptg.http2.handlers.geom.algo.AStartPathFinder;
import org.ptg.http2.handlers.geom.algo.FindItemsViaIntersection;
import org.ptg.http2.handlers.geom.algo.Triangulate;
import org.ptg.http2.handlers.webui.SaveRecentMenu;
import org.ptg.util.procintr.ProcessingPlan;

public class HTTPHandler extends AbstractHandler {
	private static Map<String, AbstractHandler> handlers = new HashMap<String, AbstractHandler>();
	static {
		handlers.put("/stream", new org.ptg.http2.handlers.stream.StreamHandler());
		handlers.put("/cmd", new org.ptg.http2.handlers.executors.CommandHandler());
		handlers.put("/scriptcmd", new org.ptg.http2.handlers.executors.SriptCommandHandler());
		handlers.put("/newpage", new org.ptg.http2.handlers.PageHandler());
		handlers.put("/SaveGraph", new org.ptg.http2.handlers.SaveGraph());
		handlers.put("/SaveConnection", new org.ptg.http2.handlers.SaveConnection());
		handlers.put("/CompileClosure", new org.ptg.http2.handlers.compilers.code.CompileClosure());
		handlers.put("/DeleteFromServer", new org.ptg.http2.handlers.DeleteFromServer());
		handlers.put("/SaveEventObject", new org.ptg.http2.handlers.EventHandler());
		handlers.put("/GetDesign", new org.ptg.http2.handlers.GetDesign());
		handlers.put("/GetDesigns", new org.ptg.http2.handlers.GetDesigns());
		handlers.put("/GetGraph", new org.ptg.http2.handlers.GetGraph());
		handlers.put("/GetGraphRaw", new org.ptg.http2.handlers.GetGraphRaw());
		handlers.put("/GetGraphNamesWithIcon", new org.ptg.http2.handlers.GetGraphNamesWithIcon());

		handlers.put("/GetGraphs", new org.ptg.http2.handlers.GetGraphs());
		handlers.put("/GetParser", new org.ptg.http2.handlers.GetParser());
		handlers.put("/GetProcDoc", new org.ptg.http2.handlers.GetProcDoc());

		handlers.put("/GetStaticComponent", new org.ptg.http2.handlers.GetStaticComponent());
		handlers.put("/GetStaticComponents", new org.ptg.http2.handlers.GetStaticComponents());
		handlers.put("/SaveDesign", new org.ptg.http2.handlers.SaveDesign());
		handlers.put("/SaveDoc", new org.ptg.http2.handlers.SaveDoc());
		handlers.put("/SaveForm", new org.ptg.http2.handlers.SaveForm());
		handlers.put("/SaveStaticComponent", new org.ptg.http2.handlers.SaveStaticComponent());
		handlers.put("/SaveDeploy", new org.ptg.http2.handlers.SaveDeploy());
		handlers.put("/GetHistoricalGraph", new org.ptg.http2.handlers.GetHistoricalGraph());
		handlers.put("/GetHistoricalGraphs", new org.ptg.http2.handlers.GetHistoricalGraphs());
		handlers.put("/GetStreamDefinition", new org.ptg.http2.handlers.GetStreamDefinition());
		handlers.put("/GetStreams", new org.ptg.http2.handlers.GetStreams());
		handlers.put("/GetEvents", new org.ptg.http2.handlers.GetEvents());
		handlers.put("/GetEventDefinition", new org.ptg.http2.handlers.GetEventDefinition());
		handlers.put("/CompileGraphOnServer", new org.ptg.http2.handlers.compilers.graph.CompileProcessOnServer());
		handlers.put("/CompileJava", new org.ptg.http2.handlers.compilers.code.CompileJava());
		handlers.put("/SetVar", new org.ptg.http2.handlers.SetVar());
		handlers.put("/contevents", new org.ptg.http2.handlers.stream.Streamer());
		handlers.put("/CompileProcess", new org.ptg.http2.handlers.compilers.code.CompileProcess());
		handlers.put("/SiteUpTest", new org.ptg.http2.handlers.SiteUpTest());
		handlers.put("/ValidateProcess", new org.ptg.http2.handlers.ValidateProcess());
		handlers.put("/AddRoutesToEngine", new org.ptg.http2.handlers.routers.AddRoutesToEngine());
		handlers.put("/RestartRoutingEngine", new org.ptg.http2.handlers.routers.RestartRoutingEngine());
		handlers.put("/StartRoutingEngine", new org.ptg.http2.handlers.routers.StartRoutingEngine());
		handlers.put("/StopRoutingEngine", new org.ptg.http2.handlers.routers.StopRoutingEngine());
		handlers.put("/CompileJob", new org.ptg.http2.handlers.compilers.code.CompileJob());
		handlers.put("/FindItemsInRegion", new org.ptg.http2.handlers.geom.algo.FindItemsInRegion());
		handlers.put("/TitanCompile", new org.ptg.http2.handlers.compilers.code.TitanCompile());
		handlers.put("/DumpRequest", new org.ptg.http2.handlers.DumpRequest());
		handlers.put("/CompileCamelGraph", new org.ptg.http2.handlers.compilers.graph.CompileCamelGraph());
		handlers.put("/ReprocessRegions", new ReprocessRegions());
		handlers.put("/MagicSpell1", new org.ptg.http2.handlers.spells.MagicSpell1());
		handlers.put("/MagicSpell2", new org.ptg.http2.handlers.spells.MagicSpell2());
		handlers.put("/MagicSpell3", new org.ptg.http2.handlers.spells.MagicSpell3());
		handlers.put("/CompileMapper", new org.ptg.http2.handlers.compilers.graph.CompileMapper());
		handlers.put("/CompileTodoMapper", new org.ptg.http2.handlers.compilers.graph.CompileTodoMapper());
		handlers.put("/CompileMapper2", new org.ptg.http2.handlers.compilers.graph.CompileMapper2());
		handlers.put("/GetBeanDefinition", new org.ptg.http2.handlers.GetBeanDefinition());
		handlers.put("/GetMethods", new org.ptg.http2.handlers.GetMethods());
		handlers.put("/CompileDFStateMachine", new org.ptg.http2.handlers.compilers.graph.CompileDFStateMachine());
		handlers.put("/SaveConfig", new org.ptg.http2.handlers.SaveConfig());
		handlers.put("/GetConfig", new org.ptg.http2.handlers.GetConfig());
		handlers.put("/ParseSQL", new org.ptg.http2.handlers.GetSQLDefinition());
		handlers.put("/CompileSQLMapper", new org.ptg.http2.handlers.compilers.graph.CompileSQLMapper());
		handlers.put("/CompileTemplate", new org.ptg.http2.handlers.compilers.code.CompileTemplate());
		handlers.put("/CompileLocalGraph", new org.ptg.http2.handlers.compilers.graph.CompileLocalGraph());
		handlers.put("/GraphToList", new org.ptg.http2.handlers.steveexecutor.GraphToList());
		handlers.put("/GetExecTran", new org.ptg.http2.handlers.steveexecutor.GetUniqExecId());
		handlers.put("/ExecuteGraphElement", new org.ptg.http2.handlers.steveexecutor.ExecuteGraphElement());
		handlers.put("/GetGraphExecutionStatus", new org.ptg.http2.handlers.steveexecutor.GetGraphExecutionStatus());
		handlers.put("/GetProcInfos", new org.ptg.http2.handlers.GetProcInfos());
		handlers.put("/SaveProcInfos", new org.ptg.http2.handlers.SaveProcInfos());
		handlers.put("/GetTodoDistances", new org.ptg.http2.handlers.graphalgo.GraphDistMatrix());
		handlers.put("/FindBoundryRect", new org.ptg.http2.handlers.geom.algo.FindBoundryRect());
		handlers.put("/ExecuteStaticComponent", new org.ptg.http2.handlers.executors.ExecuteStaticComponent());
		handlers.put("/GraphToList2", new org.ptg.http2.handlers.steveexecutor.GraphToList2());
		handlers.put("/CompileWebUIFlow", new org.ptg.http2.handlers.compilers.graph.CompileWebUIFlow());
		// with site config
		handlers.put("/site/stream", new org.ptg.http2.handlers.stream.StreamHandler());
		handlers.put("/site/cmd", new org.ptg.http2.handlers.executors.CommandHandler());
		handlers.put("/site/scriptcmd", new org.ptg.http2.handlers.executors.SriptCommandHandler());
		handlers.put("/site/newpage", new org.ptg.http2.handlers.PageHandler());
		handlers.put("/site/SaveGraph", new org.ptg.http2.handlers.SaveGraph());
		handlers.put("/site/SaveConnection", new org.ptg.http2.handlers.SaveConnection());
		handlers.put("/site/CompileClosure", new org.ptg.http2.handlers.compilers.code.CompileClosure());
		handlers.put("/site/DeleteFromServer", new org.ptg.http2.handlers.DeleteFromServer());
		handlers.put("/site/SaveEventObject", new org.ptg.http2.handlers.EventHandler());
		handlers.put("/site/GetDesign", new org.ptg.http2.handlers.GetDesign());
		handlers.put("/site/GetDesigns", new org.ptg.http2.handlers.GetDesigns());
		handlers.put("/site/GetGraph", new org.ptg.http2.handlers.GetGraph());
		handlers.put("/site/GetGraphs", new org.ptg.http2.handlers.GetGraphs());
		handlers.put("/site/GetParser", new org.ptg.http2.handlers.GetParser());
		handlers.put("/site/GetProcDoc", new org.ptg.http2.handlers.GetProcDoc());
		handlers.put("/site/GetStaticComponent", new org.ptg.http2.handlers.GetStaticComponent());
		handlers.put("/site/GetStaticComponents", new org.ptg.http2.handlers.GetStaticComponents());
		handlers.put("/site/SaveDesign", new org.ptg.http2.handlers.SaveDesign());
		handlers.put("/site/SaveDoc", new org.ptg.http2.handlers.SaveDoc());
		handlers.put("/site/SaveForm", new org.ptg.http2.handlers.SaveForm());
		handlers.put("/site/SaveStaticComponent", new org.ptg.http2.handlers.SaveStaticComponent());
		handlers.put("/site/SaveDeploy", new org.ptg.http2.handlers.SaveDeploy());
		handlers.put("/site/GetHistoricalGraph", new org.ptg.http2.handlers.GetHistoricalGraph());
		handlers.put("/site/GetHistoricalGraphs", new org.ptg.http2.handlers.GetHistoricalGraphs());
		handlers.put("/site/GetStreamDefinition", new org.ptg.http2.handlers.GetStreamDefinition());
		handlers.put("/site/GetStreams", new org.ptg.http2.handlers.GetStreams());
		handlers.put("/site/GetEvents", new org.ptg.http2.handlers.GetEvents());
		handlers.put("/site/GetEventDefinition", new org.ptg.http2.handlers.GetEventDefinition());
		handlers.put("/site/CompileGraphOnServer", new org.ptg.http2.handlers.compilers.graph.CompileProcessOnServer());
		handlers.put("/site/CompileJava", new org.ptg.http2.handlers.compilers.code.CompileJava());
		handlers.put("/site/SetVar", new org.ptg.http2.handlers.SetVar());
		handlers.put("/site/contevents", new org.ptg.http2.handlers.stream.Streamer());
		handlers.put("/site/CompileProcess", new org.ptg.http2.handlers.compilers.code.CompileProcess());
		handlers.put("/site/SiteUpTest", new org.ptg.http2.handlers.SiteUpTest());
		handlers.put("/site/ValidateProcess", new org.ptg.http2.handlers.ValidateProcess());
		handlers.put("/site/AddRoutesToEngine", new org.ptg.http2.handlers.routers.AddRoutesToEngine());
		handlers.put("/site/RestartRoutingEngine", new org.ptg.http2.handlers.routers.RestartRoutingEngine());
		handlers.put("/site/StartRoutingEngine", new org.ptg.http2.handlers.routers.StartRoutingEngine());
		handlers.put("/site/StopRoutingEngine", new org.ptg.http2.handlers.routers.StopRoutingEngine());
		handlers.put("/site/CompileJob", new org.ptg.http2.handlers.compilers.code.CompileJob());
		handlers.put("/site/FindItemsInRegion", new org.ptg.http2.handlers.geom.algo.FindItemsInRegion());
		handlers.put("/site/TitanCompile", new org.ptg.http2.handlers.compilers.code.TitanCompile());
		handlers.put("/site/DumpRequest", new org.ptg.http2.handlers.DumpRequest());
		handlers.put("/site/CompileCamelGraph", new org.ptg.http2.handlers.compilers.graph.CompileCamelGraph());
		handlers.put("/site/ReprocessRegions", new ReprocessRegions());
		handlers.put("/site/MagicSpell1", new org.ptg.http2.handlers.spells.MagicSpell1());
		handlers.put("/site/MagicSpell2", new org.ptg.http2.handlers.spells.MagicSpell2());
		handlers.put("/site/MagicSpell3", new org.ptg.http2.handlers.spells.MagicSpell3());
		handlers.put("/site/CompileMapper", new org.ptg.http2.handlers.compilers.graph.CompileMapper());
		handlers.put("/site/CompileTodoMapper", new org.ptg.http2.handlers.compilers.graph.CompileTodoMapper());
		handlers.put("/site/CompileMapper2", new org.ptg.http2.handlers.compilers.graph.CompileMapper2());
		handlers.put("/site/GetBeanDefinition", new org.ptg.http2.handlers.GetBeanDefinition());
		handlers.put("/site/GetMethods", new org.ptg.http2.handlers.GetMethods());
		handlers.put("/site/CompileDFStateMachine", new org.ptg.http2.handlers.compilers.graph.CompileDFStateMachine());
		handlers.put("/site/SaveConfig", new org.ptg.http2.handlers.SaveConfig());
		handlers.put("/site/GetConfig", new org.ptg.http2.handlers.GetConfig());
		handlers.put("/site/ParseSQL", new org.ptg.http2.handlers.GetSQLDefinition());
		handlers.put("/site/CompileSQLMapper", new org.ptg.http2.handlers.compilers.graph.CompileSQLMapper());
		handlers.put("/site/CompileTemplate", new org.ptg.http2.handlers.compilers.code.CompileTemplate());
		handlers.put("/site/CompileLocalGraph", new org.ptg.http2.handlers.compilers.graph.CompileLocalGraph());
		handlers.put("/site/GraphToList", new org.ptg.http2.handlers.steveexecutor.GraphToList());
		handlers.put("/site/GetExecTran", new org.ptg.http2.handlers.steveexecutor.GetUniqExecId());
		handlers.put("/site/ExecuteGraphElement", new org.ptg.http2.handlers.steveexecutor.ExecuteGraphElement());
		handlers.put("/site/GetGraphExecutionStatus", new org.ptg.http2.handlers.steveexecutor.GetGraphExecutionStatus());
		handlers.put("/site/GetProcInfos", new org.ptg.http2.handlers.GetProcInfos());
		handlers.put("/site/SaveProcInfos", new org.ptg.http2.handlers.SaveProcInfos());
		handlers.put("/site/GetTodoDistances", new org.ptg.http2.handlers.graphalgo.GraphDistMatrix());
		handlers.put("/DevidePolygon", new org.ptg.http2.handlers.geom.algo.DevidePolygon());
		handlers.put("/site/GetGraphRaw", new org.ptg.http2.handlers.GetGraphRaw());
		handlers.put("/site/DBSlayer", new org.ptg.http2.handlers.DBSlayer());
		handlers.put("/site/GetAnonConfig", new org.ptg.http2.handlers.GetAnonDoc());
		handlers.put("/site/CompileCamelPlan", new org.ptg.http2.handlers.compilers.graph.CompileCamelPlan());
		handlers.put("/CompileCamelPlan", new org.ptg.http2.handlers.compilers.graph.CompileCamelPlan());
		handlers.put("/SimpleGraphToList", new org.ptg.http2.handlers.steveexecutor.SimpleGraphToList());
		handlers.put("/site/SimpleGraphToList", new org.ptg.http2.handlers.steveexecutor.SimpleGraphToList());
		handlers.put("/site/PolyIntersect", new org.ptg.http2.handlers.geom.algo.FindPolyIntersections());
		handlers.put("/site/GetClassLayout", new org.ptg.http2.handlers.GetClassLayout());
		handlers.put("/site/GetProtoLayout", new org.ptg.http2.handlers.GetProtoLayout());
		handlers.put("/CompileSubGraph", new org.ptg.http2.handlers.compilers.graph.CompileSubGraph());
		handlers.put("/site/FindConvexHull", new org.ptg.http2.handlers.geom.algo.FindConvexHull());
		handlers.put("/site/CompileObjectMapping", new org.ptg.http2.handlers.compilers.graph.CompileObjectMapping());
		handlers.put("/ProcessingPlan", new ProcessingPlan());
		handlers.put("/CompileTaskPlanV2", new org.ptg.http2.handlers.compilers.graph.CompileTaskPlanV2());
		handlers.put("/RunAutomationProcess", new org.ptg.http2.handlers.compilers.graph.RunAutomationProcess());
		
		handlers.put("/site/FindItemsViaIntersection", new FindItemsViaIntersection());
		handlers.put("/site/SaveRecentMenu", new SaveRecentMenu());
		handlers.put("/site/runGraph", new ExecuteGraph());
		handlers.put("/site/Transform", new TransformHandler());
		//cnc
		handlers.put("/site/UploadGcode", new org.ptg.http2.handlers.cnc.CncHandler());
		handlers.put("/site/DownloadGcode", new org.ptg.http2.handlers.cnc.CncHandler());
		handlers.put("/site/imgtosvg", new org.ptg.http2.handlers.geom.SVGExtracter());
		handlers.put("/site/compileGcode", new org.ptg.http2.handlers.cnc.GcodeGenerator());
		//geommore
		handlers.put("/site/triangulate1", new Triangulate());

		
		handlers.put("/site/shapeMapper", new ShapeMapper());
		handlers.put("/site/ShapeOffset", new ShapeOffset());

		
		
		handlers.put("/site/ShapePixalate", new ShapePixalate());
		handlers.put("/site/RunUICodeFlow", new RunUICodeFlow());
		handlers.put("/site/PathToPoints", new PathToPoints());
		handlers.put("/site/svgToShapeObj", new SVGExtracterToShape());

		handlers.put("/GetAnnotations", new GetAnnotations());
	}

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		renewHandlers();
		String handlerType = arg0;
		System.out.println("url path: " + handlerType);
		try {
			if (handlerType != null) {
				AbstractHandler handler = handlers.get(handlerType);
				if (handler != null) {
					System.out.println("using handler: " + handler.getClass().getSimpleName());
					handler.handle(arg0, arg1, request, response);
				} else {
					String[] paths = StringUtils.split(handlerType, "/");
					if (paths.length > 1) {
						String ctx = "/" + paths[0] + "/";
						handler = handlers.get(ctx + "*");
						String path = StringUtils.substringAfter(handlerType, ctx);
						if (handler != null) {
							System.out.println("using handler: " + handler.getClass().getSimpleName());
							handler.handle(path, arg1, request, response);
						} else {
							System.out.println("Could not find a handler.");
						}
					}
				}
			}
		} catch (Throwable exp) {
			exp.printStackTrace();
		}
	}

	public static void renewHandlers() {
		handlers.put("/site/GetTaskSpecs", new GetTaskSpecs());
		handlers.put("/site/CompileTaskPlanToCode", new CompileTaskPlanToCode());
		handlers.put("/site/GetAnnotSpecs", new GetAnnotSpecs());
		handlers.put("/site/CodeToPortJava", new CodeToPortJava());
		handlers.put("/site/GetCanvas", new GetCanvas());
		handlers.put("/site/RoutingHelper", new AStartPathFinder());
		handlers.put("/site/GetMenuGroup", new GetMenuGroup());
		handlers.put("/site/GetSQLLayout", new GetSQLLayout());
		handlers.put("/site/GenerateFunctionBlocks", new GenerateFunctionBlocks());
		handlers.put("/site/GenerateFunctionBlocksDyna", new GenerateFunctionBlocks4());
		handlers.put("/site/GenerateFunctionBlocksGeneric", new GenerateFunctionBlocksGeneric());

	}

	public static void addHandler(String path, AbstractHandler hndlr) {
		handlers.put(path, hndlr);
	}

	public static void removeHandler(String path) {
		handlers.remove(path);
	}
}
