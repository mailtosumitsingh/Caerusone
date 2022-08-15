/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.admin;

import org.ptg.util.SpringHelper;


public class WebSiteConstants {
	static org.ptg.cluster.AppContext ctx  = (org.ptg.cluster.AppContext )SpringHelper.get("appContext");
    public static  String HOST = getString("HostName"); //$NON-NLS-1$
    public static  String HOST_IP  = getString("HostIp"); //$NON-NLS-1$
    public static  String HOST_IP_UDP  = getString("HostIpUDP"); //$NON-NLS-1$
    public static  String HOST_IP_SITE  = getString("HostIpSite"); //$NON-NLS-1$
    public static  String Spread_host  = getString("SpreadHost"); //$NON-NLS-1$
    public static  int Spread_port  = 4803;
    
    public static  String EVENT_QUEUE = getString("EventQueue"); //$NON-NLS-1$
    public static  String EVENT_KEY = getString("EventKey"); //$NON-NLS-1$
    public static  String CAMEL_EVENT_DESTINATION=getString("CamelEventDest"); //$NON-NLS-1$
    public static  String MY_EXCHANGE=getString("MyExchange"); //$NON-NLS-1$


    public static  String Spread_Name  = getString("SpreadName"); //$NON-NLS-1$
    public static  String Spread_Group  = getString("SpreadGroup"); //$NON-NLS-1$
    public static  String Spread_LatencyGroup  = getString("SpreadLatencyGroup"); //$NON-NLS-1$

    public static  String CAMEL_UDP_URL=getString("CamelUdpURL"); //$NON-NLS-1$ //$NON-NLS-2$
    public static  String CAMEL_HTTP_PHPDATA_URL = getString("CamelPhpDataUrl"); //$NON-NLS-1$ //$NON-NLS-2$
    public static  String CAMEL_HTTP_PHPFLUSHDATA_URL = getString("CamelPhpFlushDataUrl"); //$NON-NLS-1$ //$NON-NLS-2$
    public static  String CAMEL_HTTP_PHPREMOVEDATA_URL = getString("CamelPhpRemoveDataUrl"); //$NON-NLS-1$ //$NON-NLS-2$

    
    public static  String MEMCacheURL =  getString("MemCacheUrl"); //$NON-NLS-1$

    public static  String RABBIT_USER=getString("RabbitUser"); //$NON-NLS-1$
    public static  String RABBIT_PASSWORD=getString("RabbitPwd"); //$NON-NLS-1$
    public static  String RABBIT_VHOST = getString("RabbitVhost"); //$NON-NLS-1$
    public static  String RABBIT_HOST=getString("RabbitHost"); 
    public static  int RABBIT_PORT=Integer.parseInt(getString("RabbitPort"));
    public static  String SpreadUser=getString("SpreadUser"); 
    public static String LatencySite = getString("LatencySite");
    public static String MACROBASE = "./resources/macros/";
    public static String VELOCITY_CONFIG_PATH = "./resources/config/velocity.properties";
    public static String EMAIL_SERVER = "suns1.libnet.com";
    public static String EMAIL_PORT = "25";
   public static String getString(String pname){
	   if(ctx!=null){
		   String ret = ctx.getProperty(pname);
		   System.out.println("Read  Prop:" +pname+" : "+ ret);
		   return ret;
	   }
	   return null;
   }
   public static void main(String[] args) {
	System.out.println("done");
}
}
