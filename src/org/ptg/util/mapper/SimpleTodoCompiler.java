/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.mapper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.IGraphVisitor;
import org.ptg.util.ToDoGraphVisitor;
import org.ptg.util.TodoPathMachineCompiler;
import org.ptg.util.db.DBHelper;
import org.ptg.util.mapper.v2.FPGraph2;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class SimpleTodoCompiler {
	FPGraph graph = null;
	Map<String, List<FunctionPoint>> grps = null;
	IGraphVisitor<CompilePath> visitor ;
	 
	public String compile(String name, String graphjson) {
		if(visitor==null){
			visitor = new ToDoGraphVisitor<CompilePath>();
		}
		String query = ("select graphconfig from "+"graphs"+" where name=\"" + name + "\"");
		String config = DBHelper.getInstance().getString(query);
		Map configMap = CommonUtil.getConfigFromJsonData(config);
		visitor.setConfig(configMap);
	
		FPGraph g = new FPGraph();
		try {
			g.fromGraphJson(name, graphjson);
			return compile(name, g, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String compile(String name, FPGraph g, FPGraph2 pg) {
		if(visitor==null)
			visitor = new ToDoGraphVisitor<CompilePath>();
		TodoPathMachineCompiler<String, CompilePath> comp = new TodoPathMachineCompiler<String, CompilePath>();
		DirectedSparseMultigraph<FunctionPoint, ConnDef> gr = g.getGraph();
		comp.setFpgraph(g);
		comp.setFpgraph2(pg);
		comp.init();

		Collection<ConnDef> cpaths = gr.getEdges();
		List<ConnDef> torem = new LinkedList<ConnDef>();
		for (ConnDef d : cpaths) {
			FunctionPoint start = g.getFunctionPoints().get(d.getFrom());
			FunctionPoint end = g.getFunctionPoints().get(d.getTo());
			String code = start.getName();
			long count =0;
			long count2 =0;
			String title = "",title2 = "";
			if (start.getType().equals("ToDoObj")) {
				JSONObject ob = (JSONObject) start.getXref();
				code = ob.getString("compStatus");
				title = ob.getString("title");
				if(code!=null&&code.length()>0)
				count = Long.parseLong(code);
				else
					count = 0;
			}
			if (end.getType().equals("ToDoObj")) {
				JSONObject ob = (JSONObject) end.getXref();
				code = ob.getString("compStatus");
				if(code!=null&&code.length()>0)
				count2 = Long.parseLong(code);
				else
					count = 0;
				title2 = ob.getString("title");
			}

			CompilePath cp = new CompilePath();
			Map<String, String> codes = new LinkedHashMap<String, String>();
			codes.put(start.getName(), ""+count);
			codes.put(end.getName(), ""+count2);

			cp.setStart(start);
			cp.setEnd(end);
			cp.setCode(codes);
			comp.addCompilePath(cp);
			
		}
		comp.prepare();
		visitor.setFpgraph2(pg);
		String s = comp.compile(visitor);
		return s;
	}

	public IGraphVisitor<CompilePath> getVisitor() {
		return visitor;
	}

	public void setVisitor(IGraphVisitor<CompilePath> visitor) {
		this.visitor = visitor;
	}

}