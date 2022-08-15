/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.procintr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Closure;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.GraphProcessorSupport;
import org.ptg.util.ITaskFunction;
import org.ptg.util.PropInfo;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;
import org.ptg.util.procintr.closure.CompileObjectClasses;
import org.ptg.util.procintr.closure.CompileSpringConfig;
import org.ptg.util.taskfunctions.sping.LogTask;

public class ProcessingPlan extends GraphProcessorSupport {
	Map<String, ITaskFunction> functions = new LinkedHashMap<String, ITaskFunction>();

	public ProcessingPlan() {
		functions.put("log", new LogTask());
		Closure<PropInfo<PropInfo>> procClosure = CommonUtil.getMethodClosure("realGenerateClass", new CompileObjectClasses());
		closureMap.put("CompileObjectClasses", procClosure);
		procClosure = CommonUtil.getMethodClosure("realCompileConfig", new CompileSpringConfig());
		closureMap.put("CompileSpringConfig", procClosure);

	}

	public void createAndRunGraph(String name, FPGraph2 o, String processingPlan) {
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		Map<String, PropInfo> props = new LinkedHashMap<String, PropInfo>();
		List<PropInfo> roots = new ArrayList<PropInfo>();
		// get topologically sorted types
		List<AnonDefObj> types = CommonUtil.topologicallySortAnonDefs(o);
		// get anon def map
		anonCompMap = CommonUtil.prepareAnonMap(types);
		// build prop info maps and the root tree
		CommonUtil.getPropInfos(o, props, roots, types);
		// list to hierarchial
		CommonUtil.listToHierarchial(props, new PropInfoTransformer(), new PropInfoTransformer2());
		Closure<PropInfo<PropInfo>> procClosure = closureMap.get(processingPlan);
		CommonUtil.childFirstDepthFirstIterateAll(roots, procClosure);
		Map<String, Object> executionCtx = new HashMap<String, Object>();
		Map<String, String> skipList = new HashMap<String, String>();

		// now process any left over anondefobj that is actually not a object
		// definition
		for (AnonDefObj oo : types) {
			if (oo.getMainPort() == null) {// it is a processing node
				List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
				List<FunctionPortObj> outputs = new ArrayList<FunctionPortObj>();
				Map<String, PortObj> ports = o.getPorts();
				for (String str : oo.getInputs()) {
					processInputs(o, ports, oo, inputs, str);
				}
				for (String str : oo.getOutputs()) {
					processOutputs(o, ports, oo, outputs, str);
				}
				ITaskFunction f = functions.get(oo.getAnonType());
				if (f != null) {
					f.execute(oo, inputs, outputs, o, executionCtx, skipList, null, null);
				}
			}
		}
	}

	public void processOutputs(FPGraph2 o, Map<String, PortObj> ports, AnonDefObj anon, List<FunctionPortObj> outputs, String s) {
		PortObj po = ports.get("out_" + anon.getId() + "." + s);
		//System.out.println("Found: " + po.getId());
		for (ConnDef cd : o.getForward().values()) {

			if (cd.getFrom().equals(po.getId())) {
				//System.out.println("Found output conn: " + cd.getId());
				PortObj opp = ports.get(cd.getTo());
				PortObj myPort = ports.get(cd.getFrom());
				FunctionPortObj inp = null;
				inp = new FunctionPortObj(opp, -1);
				inp.setGrpName("unk");
				inp.setMyPort(myPort);
				if (opp != null) {
					if (opp.getPorttype().equals("pinput")) {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {// this is
																// it
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										inp = new FunctionPortObj(opp, i);
										String mvar = opp.getGrp();
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(opp.getGrp());
										inp.setMyPort(myPort);
									}
								}
							}
						}
						//System.out.println("Opposite to: " + inp);
					} else {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {// this is
																// it
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										String mvar = opp.getGrp();
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(mvar);
										inp.setMyPort(myPort);
									}
								}
							}
						}
						//System.out.println("Opposite to: " + inp);
					}
				}
				outputs.add(inp);
			}
		}
	}

	public void processInputs(FPGraph2 o, Map<String, PortObj> ports, AnonDefObj anon, List<FunctionPortObj> inputs, String s) {
		//System.out.println("ProcessInputs: " + "inp_" + anon.getId() + "." + s);
		PortObj po = ports.get("inp_" + anon.getId() + "." + s);
		//System.out.println("Found: " + po.getId());
		for (ConnDef cd : o.getForward().values()) {
			if (cd.getTo().equals(po.getId())) {
				//System.out.println("Found input conn: " + cd.getId());
				PortObj opp = ports.get(cd.getFrom());
				PortObj myPort = ports.get(cd.getTo());
				FunctionPortObj inp = null;
				inp = new FunctionPortObj(opp, -1);
				inp.setGrpName("unk");
				inp.setMyPort(myPort);
				if (opp != null) {
					if (opp.getPorttype().equals("poutput")) {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {// this is
																// it
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(opp.getGrp());
										inp.setMyPort(myPort);
									}
								}
							}
						}
						//System.out.println("Opposite From: " + inp);
					} else {
						String grp = opp.getGrp();
						for (TypeDefObj typeObj : o.getTypeDefs()) {
							if (typeObj.getId().equals(grp)) {
								for (int i = 0; i < typeObj.getInputs().size(); i++) {
									if (opp.getPortname().equals(typeObj.getInputs().get(i))) {
										String mvar = opp.getGrp();
										inp.setPo(opp);
										inp.setIndex(i);
										inp.setGrpName(mvar);
										inp.setMyPort(myPort);
										//System.out.println("Bad mapping mapping from input to output ");
									}
								}
							}
						}

						//System.out.println("Opposite From: " + inp);
					}
				}
				inputs.add(inp);
			}
		}
	}
}