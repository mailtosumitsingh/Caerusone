/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.TaskSpec;

public class GetTaskSpecs extends AbstractHandler {
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Map<String,TaskSpec> taskSpecs = new TreeMap<String,TaskSpec>(); 
		addTaskSpecs(taskSpecs);
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String json = CommonUtil.jsonFromCollection(taskSpecs.values());
			response.getOutputStream().print(json);
		} catch (Exception e) {
			response.getOutputStream().print("Node cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	private void addTaskSpecs(Map<String, TaskSpec> taskSpecs) {
		TaskSpec cmdTaskSpec = new TaskSpec(new String[]{"in"},new String[]{"exitval","stdout","stderr"},new String[]{"opt1"},"command",false,"CommandTask");
		TaskSpec concatTaskSpec = new TaskSpec(new String[]{"in1","in2"},new String[]{"out1","out2","out3"},new String[]{"aux"},"concat",false,"ConcatTask");
		TaskSpec constantTaskSpec = new TaskSpec(new String[0],new String[]{"const"},new String[]{"constval"},"constant",false,"ConstantTask");
		TaskSpec endTaskSpec = new TaskSpec(new String[]{"in1"},new String[]{"out1"},new String[]{"opt1"},"end",false,"EndTask");
		TaskSpec functionCallTaskSpec = new TaskSpec(new String[]{"in"},new String[]{"out"},new String[]{"class","function"},"functionCall",false,"FunctionCallTask");
		TaskSpec functionCall2TaskSpec = new TaskSpec(new String[]{"in"},new String[]{"out"},new String[]{"class","function"},"functionCall2",false,"FunctionCallTask2");
		TaskSpec ifTaskSpec = new TaskSpec(new String[]{"test"},new String[]{"if","else"},new String[]{"opt1"},"if",false,"IfTask");
		TaskSpec logTaskSpec = new TaskSpec(new String[]{"in"},new String[]{"out"},new String[]{"aux"},"log",false,"LogTask");
		TaskSpec longCmdTaskSpec = new TaskSpec(new String[]{"in"},new String[]{"exitval","stdout","stderr"},new String[]{"opt1"},"longCmd",false,"LongCommandTask");
		TaskSpec runGraphTaskSpec = new TaskSpec(new String[]{"in"},new String[]{"out"},new String[]{"aux"},"rungraph",false,"RunGraphTask");
		TaskSpec traceTaskSpec = new TaskSpec(new String[]{"in"},new String[]{"out"},new String[]{"aux"},"trace",false,"TraceTask");
		TaskSpec uiDisplayTaskSpec = new TaskSpec(new String[]{"in"},new String[]{"out1"},new String[]{"opt1"},"uidisplay",false,"DisplayTask");
		TaskSpec schemaMapperTaskSpec = new TaskSpec(new String[]{"infile"},new String[]{"out1"},new String[]{"aux"},"SchemaMapperTask",true,"SchemaMapperTask");
		
		TaskSpec forTaskSpec = new TaskSpec(new String[]{"start","test","incr"},new String[]{"out1"},new String[]{"aux"},"ForTask",false,"ForTask");
		TaskSpec whileTaskSpec = new TaskSpec(new String[]{"test"},new String[]{"out1"},new String[]{"aux"},"WhileTask",false,"WhileTask");
		taskSpecs.put(logTaskSpec.getAnonType(), logTaskSpec);
		taskSpecs.put(concatTaskSpec.getAnonType(), concatTaskSpec);
		taskSpecs.put(constantTaskSpec.getAnonType(), constantTaskSpec);
		taskSpecs.put(functionCallTaskSpec.getAnonType(), functionCallTaskSpec);
		taskSpecs.put(functionCall2TaskSpec.getAnonType(), functionCall2TaskSpec);
		taskSpecs.put(traceTaskSpec.getAnonType(), traceTaskSpec);
		taskSpecs.put(runGraphTaskSpec.getAnonType(), runGraphTaskSpec);
		
		taskSpecs.put(cmdTaskSpec.getAnonType(), cmdTaskSpec);
		taskSpecs.put(longCmdTaskSpec.getAnonType(), longCmdTaskSpec);
		taskSpecs.put(uiDisplayTaskSpec.getAnonType(), uiDisplayTaskSpec);
		
		taskSpecs.put(ifTaskSpec.getAnonType(), ifTaskSpec);
		taskSpecs.put(endTaskSpec.getAnonType(), endTaskSpec);
		taskSpecs.put(schemaMapperTaskSpec.getAnonType(), schemaMapperTaskSpec);
		taskSpecs.put(forTaskSpec.getAnonType(), forTaskSpec);
		taskSpecs.put(whileTaskSpec.getAnonType(), whileTaskSpec);
	}
	
	public List<TaskSpec> getTaskSpecs(){
		Map<String,TaskSpec> taskSpecs = new TreeMap<String,TaskSpec>();
		addTaskSpecs(taskSpecs);
		List<TaskSpec> ret = new LinkedList<TaskSpec>();
		ret.addAll(taskSpecs.values());
		Collections.sort(ret);
		return ret; 
	}

}


