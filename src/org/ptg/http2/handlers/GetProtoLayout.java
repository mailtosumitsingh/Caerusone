/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.proptest.Generate;
import org.ptg.proptest.PropGraph;
import org.ptg.util.PropCollType;
import org.ptg.util.PropInfo;

import com.dyuproject.protostuff.parser.Field.Modifier;
import com.dyuproject.protostuff.parser.Message;
import com.dyuproject.protostuff.parser.Proto;
import com.dyuproject.protostuff.parser.ProtoUtil;

public class GetProtoLayout extends AbstractHandler {
	
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Proto p = new Proto();
		String protoCode = (String) arg1.getParameter("filename");
		String name = (String) arg1.getParameter("name");
		String force = (String) arg1.getParameter("type");
		String id = (String) arg1.getParameter("id");
		try {
			ProtoUtil.loadFrom(new StringBufferInputStream(protoCode), p);
			//ProtoUtil.loadFrom(Thread.currentThread().getContextClassLoader().getResourceAsStream(protoCode ), p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Message> m = p.getMessageMap();
		if(m!=null){
		Map<String,String> stack = new LinkedHashMap<String,String>();
		PropGraph pg = new PropGraph();
		PropInfo<PropInfo> prop = protoToPropInfo(m,name);
		Generate g = new Generate();
		String ret = null;
		ret = g.getHtml(id,name, prop,force,false);
		response.getWriter().write(ret);
		((Request) request).setHandled(true);
		return;
		}
		response.getWriter().write("failed");
		((Request) request).setHandled(true);
	}

	private PropInfo<PropInfo> protoToPropInfo(Map<String, Message> m, String name) {
		PropInfo<PropInfo> ret = new PropInfo<PropInfo>();
		ret.setName(name);
		ret.setPropClass(name);
		Message mm = m.get(name);
		for(com.dyuproject.protostuff.parser.Field<?> f: mm.getFields()){
			PropInfo<PropInfo> child = new PropInfo<PropInfo>();
			child.setName(f.getName());
			child.setPropClass(f.getJavaType());
			if(f.getModifier().equals(Modifier.REPEATED)){
			child.setCollType(PropCollType.List);
			}
			ret.addChild(child);
		}
		return ret;
	}


}