/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.velocity;

import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ptg.util.Constants;



public class VelocityHelper {
    public static void main(String[] args) throws Exception {
        /*  first, get and initialize an engine  */
        String inFile = "trans_get.vm";
        String outFile = "c:\\out.txt";
        Map map = new HashMap();
        map.put("dada", "somevalue");
        burnTemplate(map, inFile, outFile);
    }

    public static void burnTemplate(Map<String, Object> contextMap, String inFile, String outFile) {
        VelocityEngine ve = new VelocityEngine();
        try {
            ve.init(Constants.VELOCITY_CONFIG_PATH);
            Template t = ve.getTemplate(inFile);
            VelocityContext context = new VelocityContext();
            if(contextMap!=null){
    			
            for (String key : contextMap.keySet()) {
                Object val = contextMap.get(key);
                context.put(key, val);
            }}
            FileWriter writer = new FileWriter(outFile);
            t.merge(context, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static StringBuffer burnTemplate(Map<String, Object> contextMap, String inFile) {
		StringWriter sw = new StringWriter(100000);
		VelocityEngine ve = new VelocityEngine();
		try {
			ve.init(Constants.VELOCITY_CONFIG_PATH);
			Template t = ve.getTemplate(inFile);
			
			VelocityContext context = new VelocityContext();
			if(contextMap!=null){
			for (String key : contextMap.keySet()) {
				Object val = contextMap.get(key);
				context.put(key, val);
			}
			}
			t.merge(context, sw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.getBuffer();
	}
	public static StringBuffer burnStringTemplate(Map<String, Object> contextMap, String in) {
		StringReader s = new StringReader(in);
		StringWriter out = new StringWriter();
		VelocityEngine ve = new VelocityEngine();
		try {
			ve.init(Constants.VELOCITY_CONFIG_PATH);
			VelocityContext context = new VelocityContext();
			if(contextMap!=null){
			for (String key : contextMap.keySet()) {
				Object val = contextMap.get(key);
				context.put(key, val);
			}
			}
			ve.evaluate(context, out, "OntheflyTemplate", s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.getBuffer();
	}

}

