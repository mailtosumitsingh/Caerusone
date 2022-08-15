/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.SpringHelper;
import org.ptg.util.mapper.FunctionPoint;

public class GetBeanDefinition extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = (String) arg1.getParameter("classname");
		String mtdname = (String) arg1.getParameter("mtdname");
		String fldname = (String) arg1.getParameter("fldname");
		String bean = (String) arg1.getParameter("bean");
		String[] ptypes = null;
		String paramtype = null;
		if(mtdname!=null &&mtdname.contains("(")){
			paramtype = StringUtils.substringBetween(mtdname, "(",")");
			mtdname  = StringUtils.substringBefore(mtdname,"(");
			mtdname  = StringUtils.trim(mtdname);
			if(paramtype!=null){
				if(paramtype.contains(","))
					ptypes = StringUtils.split(paramtype,",");
				else
					ptypes= new String[]{paramtype};
				for(int i=0;i<ptypes.length;i++){
					ptypes[i]=StringUtils.trim(ptypes[i]);
				}
			}
	
		}
		
		List<FunctionPoint> fps = new ArrayList<FunctionPoint>();
		if(name!=null){
			ClassPool c = CommonUtil.getClassPool();
			CtClass cl;
			String extraBin =  ((String) SpringHelper.get("basedir"))+SystemUtils.FILE_SEPARATOR+"extrabin";
			try {
				c.appendClassPath(extraBin);
				cl = c.get(name);
				CtField[] cfs = cl.getDeclaredFields();
				int i = 0;
				if(mtdname==null)
				for (CtField cf : cfs) {
					if(fldname!=null)
						if(fldname.equals("*")||cf.getName().contains(fldname)){
					FunctionPoint fp = new FunctionPoint();
				    fp.setCn(bean==null?cl.getName():bean);
				    fp.setFn(null);
				    fp.setIndex(i++);
				    fp.setPn(cf.getName());
				    fp.setType("paramobj");
				    fp.setClassType(cl.getName());
				    fp.setDataType(cf.getType().getName());
				    fps.add(fp);
						}else {
							continue;
						}
				}
				CtMethod[] cms = cl.getDeclaredMethods();
				
				int j = 0;
				if(fldname==null)
				for (CtMethod cm : cms) {
					if(mtdname!=null)if(!cm.getName().matches(mtdname))continue;
					CtClass []pcs =cm.getParameterTypes();
					if(ptypes!=null){
						if(ptypes.length!=pcs.length&&!(ptypes.length==1&&ptypes[0].length()==0&&pcs.length==0))
							continue;
						boolean matches = true;
						for(int k=0;k<pcs.length;k++){
							if(!ptypes[k].equals(pcs[k].getName())){
								matches = false;
								break;
							}
							
						}	
						if(!matches)continue;
					}
					{
					FunctionPoint fp = new FunctionPoint();
				    fp.setCn(bean==null?cl.getName():bean);
				    fp.setFn(cm.getName());
				    fp.setIndex(j++);
				    fp.setPn(null);
				    fp.setType("functionobj");
				    fp.setClassType(cl.getName());
				    fp.setDataType(cm.getReturnType().getName());
				    fps.add(fp);
				    int k = 0;
				    for(CtClass pc: pcs){
						FunctionPoint fp2 = new FunctionPoint();
					    fp2.setCn(bean==null?cl.getName():bean);
						fp2.setFn(cm.getName());
						fp2.setIndex(k++);
						fp2.setPn(pc.getName());
						fp2.setDataType(pc.getName());
						fp2.setType("paramobj");
					    fps.add(fp2);				    	
				    }
				}
				}
				{
					CtConstructor[] cts = cl.getConstructors();				
					int l = 0;
					
					
					if(fldname==null){
					for (CtConstructor ct : cts) {
						CtClass []ps = ct.getParameterTypes();
						if(mtdname!=null)if(!ct.getName().equals(mtdname))continue;
						if(ptypes!=null){
							if(ptypes.length!=ps.length&&!(ptypes.length==1&&ptypes[0].length()==0&&ps.length==0))
								continue;
							boolean matches = true;
							for(int k=0;k<ps.length;k++){
								if(!ptypes[k].equals(ps[k].getName())){
									matches = false;
									break;
								}
								
							}	
							if(!matches)continue;
						}
						FunctionPoint fp = new FunctionPoint();
					    fp.setCn(bean==null?cl.getName():bean);
					    fp.setFn(ct.getName());
					    fp.setIndex(l++);
					    fp.setPn(null);
					    fp.setType("functionobj");
					    fp.setClassType(cl.getName());
					    fp.setDataType(cl.getName());
					    fps.add(fp);
					    int k = 0;
					    for(CtClass pc: ct.getParameterTypes()){
							FunctionPoint fp2 = new FunctionPoint();
						    fp2.setCn(bean==null?cl.getName():bean);
							fp2.setFn(ct.getName());
							fp2.setIndex(k++);
							fp2.setPn(pc.getName());
							fp2.setDataType(pc.getName());
							fp2.setType("paramobj");
						    fps.add(fp2);				    	
					    }
					}	
					}
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}
		String ret =  CommonUtil.jsonFromArray(fps);
		response.getWriter().write(ret);
		((Request) request).setHandled(true);
	}
}
