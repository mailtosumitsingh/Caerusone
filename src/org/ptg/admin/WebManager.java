/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.admin;

import org.ptg.cluster.AppContext;
import org.ptg.plugins.IPluginManager;
import org.ptg.router.RoutingEngine;
import org.ptg.util.SpringHelper;

public class WebManager {
        private static RoutingEngine camel;
        private static WebStartProcess startProcess;
        private static AppContext ctx; 
    public static void init() {
    	ctx = (AppContext) SpringHelper.get("appContext");
    	

    	startProcess =  WebStartProcess.getInstance();
        camel = startProcess.getRoutingEngine();
        startProcess.initMain();
    }
    public RoutingEngine getCamelUtil(){
        return camel;
    }
    public static void main(String[] args) throws Exception{
        init();
        while(true){
        Thread.currentThread().sleep(30000);
        }
    }

    public static RoutingEngine getCamel() {
        return camel;
    }

    public static void setCamel(RoutingEngine camel) {
        WebManager.camel = camel;
    }


 

}
