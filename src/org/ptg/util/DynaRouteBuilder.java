/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.apache.camel.builder.RouteBuilder;

public class DynaRouteBuilder {
	 public static Class getClass(String name,String builderCode) {
	        ClassPool pool = CommonUtil.getClassPool();
	        Class c = null;
	        try {
	            c = Class.forName(name);
	        } catch (ClassNotFoundException e) {
	            System.out.println("Class does not exists will create now: "+ name);
	        }
	        if (c == null) {
	            try{
	            		CtClass cc = pool.makeClass(name);
	            		cc.setSuperclass(pool.get("org.apache.camel.builder.RouteBuilder"));
	            	    CtMethod cm  = getConfigure(builderCode,cc);
	                    cc.addMethod(cm);
	                    cc.stopPruning(true);
		            	
		                c =  cc.toClass();
	                } catch (CannotCompileException e) {
	                    e.printStackTrace();
	                    return null;
	                } catch (NotFoundException e) {
	                    e.printStackTrace();
	                    return null;
	                }
	        }
	        return c;
	    }
	 public static CtMethod getConfigure(String builderBody , CtClass decl) throws CannotCompileException{
		 CtMethod cm = CtMethod.make(" public void configure();", decl);
		 System.out.println(builderBody);
		 cm.setBody(builderBody);
		 return cm;
	 }
	 public static org.apache.camel.builder.RouteBuilder getRouteBuilder(String name,String code){
		 Class c = getClass(name,code);
		 return (RouteBuilder) ReflectionUtils.createInstance(c.getName(),new Object[0]);
		 
	 }
}
