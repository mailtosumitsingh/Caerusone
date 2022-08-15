package org.ptg.util.taskfunctions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class LongRunningCommandTask extends AbstractICompileFunction {
    String opt1="Command";
    String opt2="WorkDir";
    String opt3="TempDir";
	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
			runCommand(output, graph, executionCtx);
			finished(anon.getId());
		}
		return "";
	}
	public void runCommand(List<FunctionPortObj> output, FPGraph2 graph, Map<String, Object> executionCtx) {
		String cmd  = (String) c.get(opt1);
		String wdir = (String) c.get(opt2);
		try {
			Object[] val = runCmd(cmd, wdir);
			setMyPortVal(graph, executionCtx, output.get(0),val[0]);
			setMyPortVal(graph, executionCtx, output.get(1),val[1]);
			setMyPortVal(graph, executionCtx, output.get(2),val[2]);
			
			setPortVal(graph, executionCtx, output.get(0),val[0]);
			setPortVal(graph, executionCtx, output.get(1),val[1]);
			setPortVal(graph, executionCtx, output.get(2),val[2]);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	Object[] runCmd(String s,String wdir) throws ExecuteException, IOException{
		Object[] ret = new Object[3];		
		int exitValue =   -1;
		String line = s;
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		String outfile =c.get(opt3)+"/"+"out_"+getName()+"_"+CommonUtil.getRandomString(12)+".tmp";
		String errfile =c.get(opt3)+"/"+"err_"+getName()+"_"+CommonUtil.getRandomString(12)+".tmp";
		FileOutputStream stdout = new FileOutputStream(new File(outfile));
		FileOutputStream stderr = new FileOutputStream(new File(errfile));
         PumpStreamHandler handler = new PumpStreamHandler(stdout,stderr);
		executor.setStreamHandler(handler);
		executor.setWorkingDirectory(new File(wdir));
		exitValue = executor.execute(cmdLine);
		ret[0] = exitValue;
		ret[1] = outfile;
		ret[2] = errfile;
		return ret;
	}
	public String getConfigOptions() {
		return "[\""+opt1+"\","+"\""+opt2+"\",\""+opt3+"\"]";
	}
	public static void main(String[] args) {
		LongRunningCommandTask t = new LongRunningCommandTask();
		t.setName("SomeName");
		Map<String,String> map = new HashMap<String,String>();
		 String opt1="Command";
		    String opt2="WorkDir";
		    String opt3="TempDir";
		    map.put(opt1,"cmd /c dir");
		    map.put(opt2,"c:\\temp\\");
		    map.put(opt3,"c:\\temp\\");
		t.c = map;
		 Map<String, Object> ctx = new  HashMap<String, Object> (); 
		t.runCommand(null,null,ctx);
	}
	
}
