/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.AppContext;
import org.ptg.admin.WebStartProcess;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.processors.ConnDef;
import org.ptg.processors.ProcessorDef;
import org.ptg.processors.ProcessorManager;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.IProcessor;
import org.ptg.util.db.DBHelper;

public class ValidateProcess extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String proc = request.getParameter("process");
		Map <String, Object> statusMap = new HashMap<String,Object>(); 
		try {
			List ret = CommonUtil.getStreamsFromProcess(proc);
			for (Object obj : ret) {
				if (obj instanceof ProcessorDef) {
					ProcessorDef def = (ProcessorDef) obj;
					boolean b = WebStartProcess.getInstance().getRoutingEngine().checkProcessorRoute(def);
					System.out.println("Found: " + b + " " + def.getName());
					statusMap.put("Processor.Route."+def.getName(), b);
					ProcessorManager p = ProcessorManager.getInstance();
					IProcessor processor = p.getProcessorFromRoutingTable(def.getName());
					if (processor != null) {
						System.out.println("found processor: " + def.getName());
					}else{
						System.out.println("Could not found processor: " + def.getName());
					}
					statusMap.put("Processor.Instance."+def.getName(), processor!=null);
					String namecalls = "Processor_"+def.getName()+"_Calls";
					String namecallsExp = "Processor_"+def.getName()+"_CallsExp";
					
					long val = AppContext.getInstance().getStat(namecalls);
					statusMap.put("Processor.Calls."+def.getName(), val);
					val = AppContext.getInstance().getStat(namecallsExp);
					statusMap.put("Processor.ExpCalls."+def.getName(), val);
					boolean dbStat = DBHelper.getInstance().exists("select name from processors where name='"+def.getName()+"'");
					if(dbStat){
						statusMap.put("Processor.DBRecord."+def.getName(), true);
					}else{
						statusMap.put("Processor.DBRecord."+def.getName(), false);
					}
				}
				if (obj instanceof ConnDef) {
					ConnDef def = (ConnDef) obj;
					boolean b = WebStartProcess.getInstance().getRoutingEngine().checkConnectionRoutes(def);
					System.out.println("Found: " + b + " " + def.getId());
					statusMap.put("Connection.Route."+def.getId(), b);
					StreamManager m = StreamManager.getInstance();
					Stream def2= m.getStreams().get("CondStream"+def.getId());
					if(def2!=null){
						System.out.println("Stream Definition is found: "+def.getId());
					}else{
						System.out.println("Stream Definition is missing : "+def.getId());
					}
					statusMap.put("Connection.Instance."+def.getId(), def2!=null);
					{
						ProcessorManager p = ProcessorManager.getInstance();
						IProcessor processor = p.getProcessorFromRoutingTable("CondProc"+def.getId());
						if (processor != null) {
							System.out.println("found processor: " + def.getId());
						}else{
							System.out.println("Could not found processor: " + def.getId());
						}
						statusMap.put("Connection.Processor."+def.getId(), processor!=null);
					}
					boolean dbStat = DBHelper.getInstance().exists("select name from processors where name='"+"CondProc"+def.getId()+"'");
					if(dbStat){
						statusMap.put("Connection.ProcDBRecord."+def.getId(), true);
					}else{
						statusMap.put("Connection.ProcDBRecord."+def.getId(), false);
					}
					 dbStat = DBHelper.getInstance().exists("select name from stream where name='"+"CondStream"+def.getId()+"'");
					if(dbStat){
						statusMap.put("Connection.StreamDBRecord."+def.getId(), true);
					}else{
						statusMap.put("Connection.StreamDBRecord."+def.getId(), false);
					}
				}
				if (obj instanceof Stream) {
					Stream def = (Stream) obj;
					boolean b = WebStartProcess.getInstance().getRoutingEngine().checkStreamRoutes(def);
					System.out.println("Found: " + b + " " + def.getName());
					statusMap.put("Stream.Route."+def.getName(), b);
					
					StreamManager m = StreamManager.getInstance();
					Stream def2= m.getStreams().get(def.getName());
					if(def2!=null){
						System.out.println("Stream Definition is found: "+def.getName());
					}else{
						System.out.println("Stream Definition is missing : "+def.getName());
					}
					statusMap.put("Stream.Instance."+def.getName(), def2!=null);
					boolean dbStat = DBHelper.getInstance().exists("select name from stream where name='"+""+def.getName()+"'");
					if(dbStat){
						statusMap.put("Stream.DBRecord."+def.getName(), true);
					}else{
						statusMap.put("Stream.DBRecord."+def.getName(), false);
					}
					String pageQ = "select count(a1) from page_events where a3='"+def.getName()+"'";
					String pagesQ = "select a1 from page_events where a3='"+def.getName()+"'";
					String []pagenames = DBHelper.getInstance().getStringList(pagesQ).toArray(new String[0]);
					int pageCount = DBHelper.getInstance().getInt(pageQ);
					statusMap.put("Stream.PageCount."+def.getName(), pageCount);
					statusMap.put("Stream.PageNames."+def.getName(), pagenames);
				}
				if (obj instanceof EventDefinition) {
					EventDefinition def = (EventDefinition) obj;
					EventDefinition def2 = EventDefinitionManager.getInstance().getEventDefinition(def.getType());
					if(def2!=null){
						System.out.println("Event Definition is missing: "+def.getType());
					}else{
						System.out.println("Stream Definition is found : "+def.getType());
					}
					statusMap.put("EventDefinition.Instance."+def.getType(), def2!=null);
				}
			}
			WebStartProcess.getInstance().getRoutingEngine().printRoutes();
			response.getOutputStream().print(CommonUtil.toJson(statusMap));
		} catch (Exception e) {
			response.getOutputStream().print("Could not validate process:\n" + e);
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
