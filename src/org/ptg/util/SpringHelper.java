/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringHelper {
	ClassPathXmlApplicationContext  xmlBeanFactory ;
	static private Map<String,Object>locals= new HashMap<String,Object>();
	private static class SingletonHolder { 
	     private static final SpringHelper INSTANCE = new SpringHelper();
	     static{
	    	 INSTANCE.init();
	     }
	   }
	   public static SpringHelper getInstance() {
	     return SingletonHolder.INSTANCE;
	   }
	   
	   public void init(){
		   try{
		   String s = System.getProperty("spring.file");
		   if(s==null) s = "springConf.xml";
		   xmlBeanFactory = new ClassPathXmlApplicationContext(s);
		   }catch (Throwable th){
			   th.printStackTrace();
		   }
	   }	   
	   public Object getBean(String name){
		   Object o= SingletonHolder.INSTANCE.xmlBeanFactory.getBean(name);
		   if(o==null){
			   return locals.get(name);
		   }
		   return o;

	   }
	   public static Object get(String name){
		   Object o= SingletonHolder.INSTANCE.xmlBeanFactory.getBean(name);
		   if(o==null){
			   return locals.get(name);
		   }
		   return o;
	   }
	   public static void setBean(String s, Object o){
		   locals.put(s,o);
	   }

	   public static void set(String s, Object o){
		   locals.put(s,o);
	   }
	   public static Object get(Class clazz,String name){
		   Object obj =  SingletonHolder.INSTANCE.getBean(name);
		   if(obj!=null){
			   if(clazz!=null && (clazz.isAssignableFrom(obj.getClass()))){
				   return obj;
			   }
		   }
		   return obj;
	   }

	public ClassPathXmlApplicationContext getXmlBeanFactory() {
		return xmlBeanFactory;
	}

	public void setXmlBeanFactory(ClassPathXmlApplicationContext xmlBeanFactory) {
		this.xmlBeanFactory = xmlBeanFactory;
	}

}
