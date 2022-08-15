package org.ptg.util.compiletocodefunctions;

import org.ptg.http2.handlers.compilers.graph.CompileObjectMapping;
import org.ptg.processors.ConnDef;
import org.ptg.util.CodeBlock;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class Subgraph  implements CodeGenHandler{

	@Override
	public void generateCode(CodeBlock code, String currName, AnonDefObj curr, FPGraph2 o) {
		String gname = null;
		if(curr.getConfigItems()!=null){
			for(String s:curr.getConfigItems().split(";")){
				s = s.trim();
				if(s.startsWith("graph:")){
					gname = s.substring(s.indexOf("graph:")+"graph:".length());
				}
			}
		}
		if(gname!=null){
		CompileObjectMapping m = new CompileObjectMapping();
		FPGraph2 g = CommonUtil.buildDesignMappingGraph2(gname);
		for(ConnDef cd : o.getForward().values()){
			String frm = cd.getFrom();
			String to = cd.getTo();
			for(String s: curr.getOutputs()){ 
				String portName = "out_"+currName+"."+s;
				if(portName.equals(frm)){
					ConnDef cdv = new ConnDef();
					cdv.setFrom(s);
					cdv.setTo(cd.getTo());
					cdv.setId(cd.getId());
					g.getForward().put(cd.getId(), cdv);
				}
			}
			
			for(String s: curr.getInputs()){
				String portName = "inp_"+currName+"."+s;
				if(portName.equals(to)){
					ConnDef cdv = new ConnDef();
					cdv.setTo(s);
					cdv.setFrom(cd.getFrom());
					cdv.setId(cd.getId());
					g.getForward().put(cd.getId(), cdv);
				}
			}
			for(String s: curr.getAux()){
				String portName = "aux_"+currName+"."+s;
				if(portName.equals(to)||portName.equals(frm)){
					ConnDef cdv = new ConnDef();
					cdv.setTo(s);
					cdv.setFrom(cd.getFrom());
					cdv.setId(cd.getId());
					g.getForward().put(cd.getId(), cdv);
				}
			}
		}
		CodeBlock sol = null;
		try {
			sol  = m.getSolution(gname, g, o,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		code.addNested(sol,currName);
		}		
	}

}
