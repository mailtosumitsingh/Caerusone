package org.ptg.util.taskfunctions;

import java.util.List;
import java.util.Map;

import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.ITaskFunction;
import org.ptg.util.graph.PNode;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public abstract class AbstractICompileFunction implements ITaskFunction{
	protected String cstatus = null;
    protected  String uid;
    protected String name ;
    protected Map c;/*configuration as a map*/
    protected String cfg;
	private boolean persist = false;
	
	public AbstractICompileFunction() {
		name = this.getClass().getSimpleName();
	}
	public String getAuxPortVal(AnonDefObj anon,FPGraph2 graph,Map<String, Object> executionCtx,String auxPort){
    	PortObj f = graph.getPorts().get("aux_"+anon.getId()+"."+auxPort);
		return f.getPortval();
    }
    public void setPortVal(FPGraph2 graph, Map<String, Object> executionCtx, FunctionPortObj fobj,Object val) {
    	
		PortObj po = fobj.getPo();
		if(po==null){
			for(ConnDef cd : graph.getForward().values()){
				if(cd.getFrom().equals(fobj.getMyPort().getId())){
					FunctionPoint fpp = graph.getFunctionPoints().get(cd.getTo());
					if(fpp!=null){
						fpp.setVal(val.toString());
					}
				}
			}
		}else{
			String idOut = po.getId();
			executionCtx.put(idOut,val);
		}
	}
    public Object getPortVal(FPGraph2 graph, Map<String, Object> executionCtx, FunctionPortObj fobj) {
    	String id = fobj.getMyPort().getId();
		PortObj po = fobj.getPo();
		Object val = null;
		if(po==null){
			for(ConnDef cd : graph.getForward().values()){
				if(cd.getTo().equals(fobj.getMyPort().getId())){
					FunctionPoint fpp = graph.getFunctionPoints().get(cd.getFrom());
					if(fpp!=null){
						val =  fpp.getVal();
					}
					if(val==null){
						if(fpp!=null)
							val = CommonUtil.getVar(fpp.getCn());
					}
				}
			}
		}else{
			if(po.getPortval()!=null){
				val  = po.getPortval();
			}else{
				val = executionCtx.get(id);
				if(val==null){
					val= executionCtx.get(po.getId());
				}
			}
		
		}
		return val;
	}
    public void setMyPortVal(FPGraph2 graph, Map<String, Object> executionCtx, FunctionPortObj fobj, Object val) {
    	PortObj po = fobj.getMyPort();
		executionCtx.put(po.getId(),val);
	}
    public Object getMyPortVal(FPGraph2 graph, Map<String, Object> executionCtx, FunctionPortObj fobj) {
    	PortObj po = fobj.getMyPort();
		Object val = null;
				if(po.getPortval()!=null){
				val  = po.getPortval();
			}else{
				val = executionCtx.get(po.getId());
			}
		return val;
	}
    public ITaskFunction setInstId(String id){
    	this.uid = id;
    	return this;
    }
    public String getInstId(){
    	return uid;
    }
	@Override
	public boolean canExec(String eleid ) {
		if(persist){
		cstatus = CommonUtil.getExecStatus(eleid, uid);
		return (cstatus == null ||(cstatus != null && cstatus.equalsIgnoreCase("Failed")) );
		}
		return true;
	}
	public void started(String eleid){
		if(persist){
		CommonUtil.updateExecStatus(eleid, uid, "Started", "");
		}
		cstatus = "Started";
		
	}
	public void finished(String eleid){
		if(persist){
		CommonUtil.updateExecStatus(eleid, uid, "Finished", "");
		}
		cstatus = "Finished";
	}
	public void failed(String eleid){
		if(persist){
		CommonUtil.updateExecStatus(eleid, uid, "Failed", "");
		}
		cstatus  = "Failed";
	}
	
	@Override
	public boolean wasSuccess() {
		return cstatus!=null  && cstatus.equalsIgnoreCase("Finished");
	}
	public boolean wasFailure() {
		return cstatus!=null  && cstatus.equalsIgnoreCase("Failed");
	}
	public boolean isRunning() {
		return cstatus!=null  && cstatus.equalsIgnoreCase("Started");
	}

	public void setCstatus(String cstatus) {
		this.cstatus = cstatus;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	@Override
	public Object execute(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output, FPGraph2 graph,Map<String, Object> executionCtx,Map<String, String> skipList ,PNode p, PNode parent) {
		if(canExec(anon.getId())){
			started(anon.getId());
		for (FunctionPortObj fobj :inputs){
			Object val = getPortVal(graph, executionCtx, fobj);
			for (FunctionPortObj out :output){
				 setPortVal(graph, executionCtx, out,val);  
			}
		}
		finished(anon.getId());
		}
		return "";
	}

	public void setConfigItems(String s) {
		if (s == null || s.length() <= 0)
			return;
		c = CommonUtil.getConfigFromJsonData(s);
		cfg = s;
	}
	public String getConfigItems() {
		return cfg;
	}
	public String getConfigOptions() {
		return "[\"p1\",\"p2\",\"p3\"]";
	}
	
	public String getDoc(){
	return "";
	}
	 public ITaskFunction setPersistable(boolean p){
		 	this.persist  = p;
	    	return this;
	    }
}    


