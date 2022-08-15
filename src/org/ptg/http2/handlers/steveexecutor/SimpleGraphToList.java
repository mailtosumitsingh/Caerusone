/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers.steveexecutor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.processors.ConnDef;
import org.ptg.util.CommonUtil;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;


public class SimpleGraphToList extends AbstractHandler{

    		public void handle(String arg0, Request arg1, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    			String name = request.getParameter("name");
    			String graphjson = request.getParameter("process");
    			try {
    				FPGraph2 o = runApp(name,graphjson);
    				List<String> ret = CommonUtil.topologicalSort(o);
    				response.getWriter().print(CommonUtil.jsonFromCollection(ret));
    			} catch (Exception e) {
    				response.getOutputStream().print("Could not compile:\n" + e);
    				e.printStackTrace();
    			}

    			((Request) request).setHandled(true);
    		}
    		public FPGraph2 runApp( String name, String graphjson) {
    			FPGraph2 o = CommonUtil.getMappingGraph(name, graphjson);
    			return prepare(name, o);
    		}

    		public FPGraph2 prepare(String name, FPGraph2 o) {
    			List<TypeDefObj> types = o.getTypeDefs();
    			Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
    			Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
    			for (TypeDefObj obj : types) {
    				typeMap.put(obj.getId(), obj);
    			}
    			for (AnonDefObj def : o.getAnonDefs()) {
    				anonCompMap.put(def.getId(), def);
    			}

    			Pattern anonPat = Pattern.compile("([a-z_A-Z0-9]+)\\(([a-z_A-Z0-9]+)\\)");

    			Map<String, JSONObject> orphans = o.getOrphans();
    			for (Map.Entry<String, JSONObject> en : orphans.entrySet()) {
    				JSONObject jo = en.getValue();
    				if (jo.getString("type").equals("portable")) {
    					String portType = jo.getString("portType");
    					if ("prop".equals(portType)) {
    						String id = jo.getString("grpid");
    						TypeDefObj tobj = typeMap.get(id);
    						if (tobj == null) {
    							tobj = new TypeDefObj();
    							tobj.setId(id);
    						}
    						int indx = jo.getInt("index");
    						while (tobj.getDtypes().size() < indx + 1)
    							tobj.getDtypes().add(null);
    						while (tobj.getInputs().size() < indx + 1)
    							tobj.getInputs().add(null);
    						tobj.getDtypes().set(indx, jo.getString("dtype"));
    						tobj.getInputs().set(indx, jo.getString("id"));
    						System.out.println(jo);
    						typeMap.put(tobj.getId(), tobj);
    					} else { /* anondef */
    						String id = jo.getString("grpid");
    						Matcher mat = anonPat.matcher(id);
    						String uid = null;
    						String ttype = null;
    						if (mat.find()) {
    							uid = mat.group(1);
    							ttype = mat.group(2);
    						}
    						if (uid == null || ttype == null)
    							continue;
    						AnonDefObj tobj = anonCompMap.get(id);
    						if (tobj == null) {
    							tobj = new AnonDefObj();
    							tobj.setId(id);
    							/* tobj.setId(uid); */
    							tobj.setAnonType(ttype);
    							tobj.setName(uid);
    						}
    						String portId = jo.getString("id");
    						if ("input".equals(portType))
    							tobj.getInputs().add(portId);
    						else if ("output".equals(portType))
    							tobj.getOutputs().add(portId);
    						else if ("aux".equals(portType))
    							tobj.getAux().add(portId);

    						o.getAnonDefs().add(tobj);
    						anonCompMap.put(tobj.getId(), tobj);
    					}

    				}
    			}
    			types = new ArrayList<TypeDefObj>();
    			for (TypeDefObj t : typeMap.values()) {
    				types.add(t);
    			}
    			o.setTypeDefs(types);
    			// fix loopback
    			o = applyLoopBacks(o, typeMap, anonCompMap);
    			return o;

    		}
    		protected FPGraph2 applyLoopBacks(FPGraph2 o, Map<String, TypeDefObj> typeMap, Map<String, AnonDefObj> anonCompMap) {
    			Map<String, ConnDef> m = o.getForward();
    			List<String> toRem = new ArrayList<String>();
    			for (Map.Entry<String, ConnDef> en : m.entrySet()) {
    				ConnDef cd = en.getValue();
    				String id = en.getKey();
    				if (cd.getCtype() != null && cd.getCtype().equalsIgnoreCase("loopback")) {
    					System.out.println("There is a loopback: " + cd.getId());
    					toRem.add(id);
    				}
    			}
    			Map<String, PortObj> ports = o.getPorts();
    			for (String s : toRem) {
    				ConnDef cd = o.getForward().get(s);
    				PortObj f = ports.get(cd.getFrom());
    				PortObj t = ports.get(cd.getTo());
    				o.getForward().remove(s);
    				o.getMainGraph().getForward().remove(s);
    				o.getMainGraph().getGraph().removeEdge(cd);
    				// add anon
    				AnonDefObj tobj = new AnonDefObj();
    				tobj.setId(cd.getId() + "loopback");
    				tobj.setAnonType("loopback");
    				tobj.setName(CommonUtil.getRandomString(4) + "_loopback");
    				tobj.getInputs().add("inp1");
    				tobj.getOutputs().add("out1");
    				tobj.getAux().add("aux1");
    				// input
    				PortObj pi = new PortObj();
    				pi.setId("inp_" + tobj.getId() + "." + "inp1");
    				pi.setGrp(tobj.getId());
    				pi.setPorttype("input");
    				pi.setPortname("inp1");
    				// output
    				PortObj po = new PortObj();
    				po.setId("out_" + tobj.getId() + "." + "out1");
    				po.setGrp(tobj.getId());
    				po.setPorttype("output");
    				po.setPortname("out1");
    				// output
    				PortObj pa = new PortObj();
    				pa.setId("aux_" + tobj.getId() + "." + "aux1");
    				pa.setGrp(tobj.getId());
    				pa.setPorttype("aux");
    				pa.setPortname("aux1");

    				ConnDef ci = new ConnDef();
    				ci.setId(pi.getId() + "conn");
    				ci.setFrom(cd.getFrom());
    				ci.setTo(pi.getId());

    				ConnDef co = new ConnDef();
    				co.setId(po.getId() + "conn");
    				co.setFrom(po.getId());
    				co.setTo(cd.getTo());

    				AnonDefObj a1 = anonCompMap.get(f.getGrp());
    				String aux1 = a1.getAux().get(0);
    				AnonDefObj a2 = anonCompMap.get(t.getGrp());
    				String aux2 = a2.getAux().get(0);

    				ConnDef ca1 = new ConnDef();
    				ca1.setId(pa.getId() + "conn1");
    				ca1.setFrom("aux_" + a1.getId() + "." + aux1);
    				ca1.setTo(pa.getId());
    				ca1.setNodes(new String[] { "aux_" + a1.getId() + "." + aux1, pa.getId() });

    				/*
    				 * We will not finish the short circuit but keep it open loop so
    				 * what we are essentially doing is we take a loopback and then
    				 * convert into anon def but that is not setting values anywhre.
    				 * ConnDef ca2 = new ConnDef(); ca2.setId(pa.getId()+"conn2");
    				 * ca2.setFrom("aux_"+tobj.getId()+"."+"aux1");
    				 * ca2.setTo("aux_"+a2.getId()+"."+aux2);
    				 * o.getForward().put(ca2.getId(), ca2);
    				 */

    				anonCompMap.put(tobj.getId(), tobj);
    				o.getAnonDefs().add(tobj);
    				o.getPorts().put(pi.getId(), pi);
    				o.getPorts().put(po.getId(), po);
    				o.getPorts().put(pa.getId(), pa);
    				o.getForward().put(ci.getId(), ci);
    				o.getForward().put(co.getId(), co);
    				o.getForward().put(ca1.getId(), ca1);

    			}
    			return o;
    		}
}
