/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.script;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.ptg.admin.AppContext;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.ReflectionUtils;




public class ScriptEngine {
	Context cx;
	ImporterTopLevel scope;
	private Log log = LogFactory.getLog(ScriptEngine.class);
	Map<String , String>taskDefs = new HashMap<String,String>();
	private String ImportFileName = CommonUtil.getResourcePath("imports.txt");
	private String taskLetPath  = CommonUtil.getScriptPath("tasks"+File.separator);
	private String taskLetPath2  = "uploaded/extrascript/";
	public void init(){
		cx = Context.enter();
		cx.initStandardObjects();
		cx.setApplicationClassLoader( getClass().getClassLoader());
		scope = new ImporterTopLevel(cx);
		initImports();
		run(("utils.js"));
		loadTasks(taskLetPath);
		loadTasks(taskLetPath2);
	}
	private void initImports(){
		LineNumberReader r = null;
		try{
		r = new LineNumberReader(new FileReader(ImportFileName));
		String line = null;
		while((line=r.readLine())!=null){
			  cx.evaluateString(scope,"importPackage(Packages."+line+");","dummy",1,null);
			}
		}catch(Exception e){
			log.error("Cannot find the file :"+ImportFileName,e);
		}
	}
	public void addObject(String name, Object obj){
		Object wrappedOut = Context.javaToJS(obj, scope);
		ScriptableObject.putProperty(scope, name, wrappedOut);
	}
	public void removeObject(String name){
		ScriptableObject.deleteProperty(scope, name);
	}
	   public Object getObject(String refName) {
	        return ScriptableObject.getProperty(scope, refName);
	    }
	   
	    public Object runString(String name) {
	    	Context cxlocal = enter();	
	        Object ret =  cxlocal.evaluateString(scope, name, "jsexecutor.java", 0, null);
	        leave(cxlocal);
	        return ret;
	    }

	public void run(String nameOfFile){
		Context cxlocal = enter();	
		FileReader reader ;
		String utilJS = CommonUtil.getScriptPath(nameOfFile);
		try{
			reader= new FileReader(utilJS);
		
		if(reader!=null){
			cxlocal.evaluateReader(scope,reader,nameOfFile,1,null);
		}
		}catch(IOException e){
			log.error("Cannot find the file :"+nameOfFile,e);
		}
	}
	public static void main (String [] args) throws GenericException, IOException{
		ScriptEngine r = new ScriptEngine();
		r.init();
		r.addObject("worldVar","World");
		r.addObject("scripEngine", r);
		r.runTask("TestTask", new ArrayList());
		LineNumberReader r1 = new LineNumberReader(new InputStreamReader(System.in));
		while(true){
		System.out.println("loaded tasks:");
		for(String taskname:r.taskDefs.keySet()){
			System.out.println("\t\t>>"+taskname);
		}
		System.out.println("Enter task to read");
		String torun = r1.readLine();
		r.runTask(torun, null);
		}
		}
	 public void runFileWOContext(String name) {
	        Scriptable scope;
	        Context cx;
	        cx = Context.enter();
	        cx.initStandardObjects();
	        cx.setApplicationClassLoader(getClass().getClassLoader());
	        scope = new ImporterTopLevel(cx, true);

	        try {
	            cx.evaluateReader(scope, new FileReader("util.js"), "util.js", 0, null);
	            cx.evaluateReader(scope, new FileReader(CommonUtil.getScriptPath(name)), name, 0, null);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    public Object runFuntionRaw( String mtd,List arg) {
	    	Context cxlocal = enter();	    	
	    	Object result;
	    	Function f = (Function) scope.get(mtd, scope);
	        if (arg == null) {
	            result =  f.call(cxlocal, scope, scope, null);
	        } else {
	            result =  f.call(cxlocal, scope, scope, arg.toArray());
	        }
	        leave(cxlocal);
	        return result;
	    }
		public void leave(Context cxlocal) {
			cxlocal.exit();
		}
		public Context enter() {
			Context cxlocal = cx.enter();
			return cxlocal;
		}
	    public NativeJavaObject runFuntion( String mtd,List arg) {
	    	Context cxlocal = enter();	    	
	    	NativeJavaObject result;
	    	Function f = (Function) scope.get(mtd, scope);
	        if (arg == null) {
	            result = (NativeJavaObject) f.call(cxlocal, scope, scope, null);
	        } else {
	            result = (NativeJavaObject) f.call(cxlocal, scope, scope, arg.toArray());
	        }
	        return result;
	    }

	    public NativeJavaObject runFuntionInFile(String file, String mtd, List functionArgs) throws Exception {
	    	Context cxlocal = enter();	    	
	    	NativeJavaObject result;
	        	cxlocal.evaluateReader(scope, new FileReader(file), StringUtils.substringBefore(file, "."), 0, null);
	        Function f = (Function) scope.get(mtd, scope);
	        if (functionArgs == null) {
	            result = (NativeJavaObject) f.call(cxlocal, scope, scope, null);
	        } else {
	            result = (NativeJavaObject) f.call(cxlocal, scope, scope, functionArgs.toArray());
	        }
	        return result;
	    }
	     public void loadTasks(String name) {
	         File f = new File(name);
	         if (f.isDirectory()) {
	             File[] files = f.listFiles();
	             for (File fl : files) {
	                 if (fl.isFile()) {
	                     ReflectionUtils.invoke(this, "loadTask", new Object[]{fl});
	                     AppContext.getInstance().incrStat("ScriptEngineTaskCount");
	                 }
	             }
	         }
	     }

	     public void loadTask(File f) {
	         String name;
			try {
				name = (String)(runFuntionInFile(f.getAbsolutePath(), "GetName", null)).unwrap();
				taskDefs.put(name, f.getAbsolutePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         
	     }
	    public Object runTask(String name,List l) throws GenericException{
	    	String path = taskDefs.get(name);
	    	if(path==null){
	    		loadTasks(taskLetPath);
	    		loadTasks(taskLetPath2);
	    	}
	    	path = taskDefs.get(name);
	    	if(path!=null){
	    		try{
	    		NativeJavaObject object =  (NativeJavaObject) runFuntionInFile(path, "ExecuteTask", l);
	    		if(object!=null){
	    		return object.unwrap();
	    		}}catch(Exception exp){
	    			exp.printStackTrace();
	    			taskDefs.remove(name);
	    		}
	    	}else{
	    		throw new GenericException("Cannot find task: "+name);
	    	}
	    	
		    return null;
	    	}


}




 