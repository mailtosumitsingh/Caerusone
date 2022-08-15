/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.ptg.cluster.AppContext;
import org.ptg.util.SpringHelper;



public class HTTPServer {
	static HTTPHandler handler = new HTTPHandler();
	
	static AppContext ctx = (AppContext)SpringHelper.get("appContext");
public HTTPServer() {
		super();
		
		  
	}
public static HTTPHandler getHandler() {
		return handler;
	}
	public static void setHandler(HTTPHandler handler) {
		handler = handler;
	}
	private static WebAppContext getSiteContext(){
		WebAppContext sitewebctx = new WebAppContext(ctx.getWebappdir(), ctx.getProperty("contextpath"));
		sitewebctx.setTempDirectory(new File(ctx.getWebapptempdir()));
		sitewebctx.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer","false");
		sitewebctx.setInitParameter("cacheControl","max-age=0,public"); 
		return sitewebctx;
	}
	private static WebAppContext getAxis2Context(){
		WebAppContext sitewebctx = new WebAppContext(ctx.getAxiswar(),ctx.getProperty("wscontextpath"));
		sitewebctx.setTempDirectory(new File(ctx.getAxistempdir()));
		return sitewebctx;
	}
	private static WebAppContext getActivitiContext(){
		WebAppContext activitiCtx = new WebAppContext(ctx.getActivitiwar(),ctx.getProperty("activiticontext"));
		activitiCtx.setTempDirectory(new File(ctx.getActivitiwartemp()));
		return activitiCtx;
	}
	private static WebAppContext getRootContext(){
		WebAppContext sitewebctx = new WebAppContext(ctx.getWebappdir(), ctx.getProperty("rootcontextpath"));
		sitewebctx.setTempDirectory(new File(ctx.getWebapptempdir()));
		sitewebctx.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer","false");
		return sitewebctx;
	}

public static void start(){
	Thread th = new Thread(new Runnable(){
		public void run(){
			try {
				System.out.println("Template server starting");
				InetSocketAddress add = new InetSocketAddress(ctx.getHttpServerIp(), ctx.getServerHttpPort());
				Server server = new Server(add);
				//todo should come from start up parameter
				Configuration.ClassList classlist = Configuration.ClassList
			            .setServerDefault( server );
			classlist.addBefore(
			            "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
			            "org.eclipse.jetty.annotations.AnnotationConfiguration" );
				HandlerCollection handlers= new HandlerCollection();
				List<Handler> list = new ArrayList<Handler>();
				list.add(new HTTPHandler());///>>
				list.add(getSiteContext());
				if(ctx.startAxis){
					list.add(getAxis2Context());
				}
				if(ctx.startActiviti){
					list.add(getActivitiContext());
				}

				list.add(getRootContext());
				handlers.setHandlers(list.toArray(new Handler[0]));
				server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", "10000000");
				server.setHandler(handlers);
				
				server.start();
				server.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	th.start();
}
}
