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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.model.UIExtractedPorts;
import org.ptg.velocity.VelocityHelper;

public class CodeToPortJava extends AbstractHandler {
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String grpid = request.getParameter("grpid");
		String code = request.getParameter("code");
		String template = request.getParameter("template");
		String toret =  null;
		try{
			toret =  generateTemplateCode(grpid, code, template,true) ;
			
		} catch (Exception e) {
			response.getOutputStream().print("Could not compile:\n"+e.toString());
			e.printStackTrace();
		}
		if(toret!=null){
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.getOutputStream().print(toret);
		}
		((Request) request).setHandled(true);
	}

	public String generateTemplateCodeWithDtype(String grpid, String code, String template) {
		return generateTemplateCode(grpid, code, template,false) ;
	}
	private String generateTemplateCode(String grpid, String code, String template,boolean isUI) {
		String toret;
		if(template==null)
			template ="";
		Pattern p = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9 _\\.]*):([a-zA-Z0-9 _\\.\\[\\]\"\']*)\\}");
		Matcher matcher = p.matcher(code);
		List<String> inputPortsId = new LinkedList<String>();
		List<String> inputPortDtype = new LinkedList<String>();
		while(matcher.find()){
			String expression = matcher.group(0);
			String portId = matcher.group(1);
			String dtype = matcher.group(2);
			if(!inputPortsId.contains(portId)){
				inputPortsId.add(portId);
				inputPortDtype.add(dtype);
			}
		}
		Pattern p2 = Pattern.compile("\\<([a-zA-Z][a-zA-Z0-9 _\\.\\[\\]\\(\\)]*):([a-zA-Z0-9 _\\.\\[\\]\"\']*)\\>");
		Matcher matcher2 = p2.matcher(code);
		List<String> outputPortsId = new LinkedList<String>();
		List<String> outputPortsDtype = new LinkedList<String>();
		while(matcher2.find()){
			String expression = matcher2.group(0);
			String port = matcher2.group(1);
			String dType = matcher2.group(2);
			outputPortsId.add(port);
			outputPortsDtype.add(dType);
		}
		
		
		
		List<UIExtractedPorts> inputPorts = new LinkedList<UIExtractedPorts>();
		List<UIExtractedPorts> outputPorts = new LinkedList<UIExtractedPorts>();
		int index = 0;
		for(String id: inputPortsId){
			UIExtractedPorts inputPort = new UIExtractedPorts(grpid, id, index);
			String dtype = inputPortDtype.get(index);
			if(isUI)
				inputPort.setDefaultVal(dtype);
			else
				inputPort.setDtype(dtype);
			String idLabel = id;
			if(id.contains("."))
				idLabel  = StringUtils.substringBefore(id, ".");
			inputPort.setLabel(idLabel);
			inputPorts.add(inputPort);
			index++;
		}
		index = 0;
		for(String id: outputPortsId){
			UIExtractedPorts outputPort = new UIExtractedPorts(grpid, id, index);
			String dtype = outputPortsDtype.get(index);
			if(isUI)
				outputPort.setDefaultVal(dtype);
			else
				outputPort.setDtype(dtype);
			String idLabel = id;
			if(id.contains("."))
				idLabel  = StringUtils.substringBefore(id, ".");
			
			outputPort.setLabel(idLabel);
			outputPorts.add(outputPort);
			index++;
		}
		toret = generateTemplate(template, inputPorts, outputPorts);
		return toret;
	}
	public String generateTemplate(String template, List<UIExtractedPorts> list, List<UIExtractedPorts> list2) {
		String toret;
		Map m = new HashMap();
		m.put("items", list);// $formId
		m.put("outputitems", list2);// $formId
		StringBuffer responseContent = VelocityHelper.burnTemplate(m, "modulePorts"+template+".vm");
		 toret = responseContent.toString();
		return toret;
	}

}
