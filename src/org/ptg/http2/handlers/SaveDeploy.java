/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.admin.WebStartProcess;
import org.ptg.cluster.AppContext;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.events.DeployEvent;


public class SaveDeploy extends AbstractHandler {
	DiskFileItemFactory factory = new DiskFileItemFactory();
	ServletFileUpload upload = new ServletFileUpload(factory);
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	boolean sendEvent = false;
    public SaveDeploy(){
		factory.setSizeThreshold(20000);
		factory.setRepository(new File(temp));
		upload.setSizeMax(100000000);
		AppContext ctx = (AppContext) SpringHelper.get("appContext");
		sendEvent = ctx.isSendDeployEvent();
	
    }
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			Map m = new java.util.HashMap();
	    	DeployEvent de = new DeployEvent();
			List  items = upload.parseRequest(request);
			for(Object o : items){
				    FileItem item = (FileItem) o;
				    	if (item.isFormField()) {
				    	    String name = item.getFieldName();
				    	    String value = item.getString();
				    	    m.put(name, value);
				    	    System.out.println("Form field: "+name +" value : "+value);
				    } 
			}
			String deployName = (String) m .get("deployName");
			String deployType= (String) m.get("deployType");
			for(Object o : items){
				FileItem item = (FileItem) o;
		    		//String dest = temp+"/"+"upload"+"/"+item.getName()+"_"+CommonUtil.getRandomString(8);
			    	if (!item.isFormField()) {
			    	if(deployName==null || deployName.trim().length()==0){
			    		deployName = item.getName();
			    	}
			    	CommonUtil.doDeployement(base,m, deployName, deployType, item);
		    }
		}
			if(sendEvent){
			String dest = (String) m.get("dest");
	    	de.setName(deployName);
	    	de.setType(deployType);
	    	de.setDest(dest);
	    	CommonUtil.saveEventToDB(de);
			}
			response.getOutputStream().print("<html><title>response</title><body><textarea>Successfully deployed "+deployName+"</textarea></body></html>");
			response.getOutputStream().flush();
		} catch (Exception e) {
			response.getOutputStream().print("Deployement could not be saved.");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}
	


}
