/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers.generators;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.CtClass;
import javassist.CtMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.analyzer.Builder;
import org.ptg.util.CommonUtil;
import org.ptg.util.db.DBHelper;
import org.ptg.velocity.VelocityHelper;

import com.google.common.collect.Sets;

public class GenerateFunctionBlocksGeneric extends AbstractHandler {
	// http://localhost:8095/site/GenerateFunctionBlocksGeneric?cls=org.ptg.util.CommonUtils&name=test&grpid=${id}TestGenGen
	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			String ip = "127.0.0.1";
			String cls = request.getParameter("cls");// "org.ptg.util.AbstractOpencvProcessor"
			String name = request.getParameter("name");// "calculateWhitePixelCount"
			String uiType = request.getParameter("uitype");// StaticModule or
															// AnonScriptModule,StaticModule
			Set<String> m = getSet(request.getParameterValues("m"));
			if (uiType == null) {
				uiType = "AnonScriptModule";
			}

			if (name == null || name.length() < 1) {
				return;
			}
			if (cls == null || cls.length() < 1) {
				return;
			}
			if (m == null || m.size() < 1) {
				Class c = Class.forName(cls);
				m = new HashSet<String>();
				for (Method mtd : c.getDeclaredMethods()) {
					m.add(mtd.getName());
				}
			}
			String ret = generateMethodInternal(ip, cls, m, name, uiType);
			response.getOutputStream().print(ret);
		} catch (Exception e) {
			response.getOutputStream().print("Document cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	private Set<String> getSet(String[] strings) {
		if (strings != null) {
			Set<String> s = Sets.newHashSet();
			for (String str : strings) {
				s.add(str);
			}
			return s;
		}
		return null;
	}

	private String generateMethodInternal(String ip, String cls, Set<String> m, String sname, String uiType) throws Exception {
		String grpId = "${id}" + sname + "(MethodCall)";
		String toret;
		Map map = new HashMap();
		map.put("items", m.toArray(new String[0]));// $formId
		map.put("uiType", uiType);
		map.put("name", sname);
		map.put("grpid", grpId);
		map.put("cls", cls);
		StringBuffer responseContent = VelocityHelper.burnTemplate(map, "modulePorts" + "ScriptGeneric" + ".vm");
		toret = responseContent.toString();
		return generateMethodBlock(ip, toret, uiType, grpId);
	}

	public static String generateMethodBlock(String ip, String code, String uiType, String name) {
		code = StringEscapeUtils.escapeJavaScript(code);
		String scriptCode = "";
		String toSave = getToSave(code, name, scriptCode);
		String doc = "";
		if (CommonUtil.escapeJavaScriptInSql()) {
			toSave = StringEscapeUtils.escapeJavaScript(toSave);
		}
		String backsql = "insert into deletedstaticcomponent (select * from staticcomponent where name='" + name + "')";
		String delsql = "delete from staticcomponent where name='" + name + "'";
		String swap = toSave;
		toSave = "";
		String inssql = "insert into staticcomponent(name,txt,userid,userip,doc) values ('" + name + "','" + toSave + "','" + ip + "','" + ip + "','" + doc + "')";

		DBHelper.getInstance().executeUpdate(backsql);
		DBHelper.getInstance().executeUpdate(delsql);
		int rid = DBHelper.getInstance().executeInsert(inssql);
		String sql = "update staticcomponent set txt=? where id=?";
		DBHelper.getInstance().executePreparedUpdate(sql, swap, rid);

		return swap;
	}

	private static String getToSave(String code, String name, String scriptCode) {

		String toSave = "{\"id\":\"" + name + "\",\"name\":\"" + "StaticModule" + "\",\"aid\":0,\"mouseX\":974,\"mouseY\":278,\"cntn\":\"" + code
				+ "\",\"left\":\"925px\",\"top\":\"372px\",\"width\":\"190px\",\"height\":\"87px\",\"zindex\":194,\"rotation\":\"0deg\",\"script\":\"" + scriptCode + "\",\"code\":\"" + code + "\"}";
		return toSave;
	}

	public String getScriptCode(String cls, String fqm) throws Exception {
		Builder b = new Builder("C:/Projects/Caerusone/bin", "org.ptg.*", "C:/Projects/Caerusone/lib/", "C:/Projects/Caerusone/upload/genclasses");
		CtClass cc = b.getClassFile(cls);
		for (CtMethod mtd : cc.getDeclaredMethods()) {
			if (mtd.getName().equals(fqm)) {
				StringBuilder sc = new StringBuilder();
				String name = mtd.getReturnType().getSimpleName();
				if (name.contains("$")) {
					name = StringUtils.substringAfterLast(name, "$");
				}
				if (!name.equals("void")) {
					sc.append(name);
					sc.append(" <" + "ret> =");
				}
				sc.append(mtd.getName());
				sc.append("(");
				String mname = mtd.getName();
				int index = 0;
				for (CtClass pcl : mtd.getParameterTypes()) {
					sc.append("{");
					sc.append("param" + index);
					sc.append("}");
					index++;
					if (index != mtd.getParameterTypes().length) {
						sc.append(",");
					}
				}
				sc.append(");");
				return sc.toString();
			}
		}
		return null;

	}

	public String getVarDefCode(String cls, String fqm) throws Exception {
		Builder b = new Builder("C:/Projects/Caerusone/bin", "org.ptg.*", "C:/Projects/Caerusone/lib/", "C:/Projects/Caerusone/upload/genclasses");
		CtClass cc = b.getClassFile(cls);

		int index = 0;
		for (CtMethod mtd : cc.getDeclaredMethods()) {
			if (mtd.getName().equals(fqm)) {
				StringBuilder sc = new StringBuilder();
				for (CtClass pcl : mtd.getParameterTypes()) {
					sc.append("{");
					sc.append("param" + index);
					sc.append(":");
					String name = pcl.getSimpleName();
					if (name.contains("$")) {
						name = StringUtils.substringAfterLast(name, "$");
					}
					sc.append(name);
					sc.append("}");
					index++;
				}
				String name = mtd.getReturnType().getSimpleName();
				if (name.contains("$")) {
					name = StringUtils.substringAfterLast(name, "$");
				}
				if (!name.equals("void")) {
					sc.append("<" + "ret" + ":" + name + "> ");
				}
				return sc.toString();
			}

		}
		return null;
	}

}
