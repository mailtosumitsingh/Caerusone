package org.ptg.util.taskfunctions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.ExecuteException;
import org.ptg.util.CmdOutput;
import org.ptg.util.CommonUtil;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class CommandTask extends AbstractICompileFunction {
	static String opt1 = "Command";
	static String opt2 = "WorkDir";

	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph, Map<String, Object> executionCtx, Map<String, String> skipList, PNode p,
			PNode parent) {
		if (canExec(anon.getId())) {
			started(anon.getId());
			runCommand(output, graph, executionCtx);
			finished(anon.getId());
		}
		return "";
	}

	public void runCommand(List<FunctionPortObj> output, FPGraph2 graph, Map<String, Object> executionCtx) {
		String cmd = (String) c.get(opt1);
		String wdir = (String) c.get(opt2);
		try {
			CmdOutput val = runCmd(cmd, wdir);
			setMyPortVal(graph, executionCtx, output.get(0), val.getExitValue());
			setMyPortVal(graph, executionCtx, output.get(1), val.getOut());
			setMyPortVal(graph, executionCtx, output.get(2), val.getErr());

			setPortVal(graph, executionCtx, output.get(0), val.getExitValue());
			setPortVal(graph, executionCtx, output.get(1), val.getOut());
			setPortVal(graph, executionCtx, output.get(2), val.getErr());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static CmdOutput runCmd(String s, String wdir) throws ExecuteException, IOException {
		return CommonUtil.runCmd(s, wdir);
	}

	@Override
	public String getConfigOptions() {
		return "[\"" + opt1 + "\"," + "\"" + opt2 + "\"]";
	}

	public static void main(String[] args) {
		CommandTask t = new CommandTask();
		Map<String, String> map = new HashMap<String, String>();
		map.put(opt1, "cmd /c dir");
		map.put(opt2, "c:\\temp\\");
		t.c = map;
		Map<String, Object> ctx = new HashMap<String, Object>();
		t.runCommand(null, null, ctx);
	}

}
