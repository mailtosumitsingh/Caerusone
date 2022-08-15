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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CtClass;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.analyzer.Builder;
import org.ptg.analyzer.model.Setter;
import org.ptg.http2.handlers.CodeToPortJava;
import org.ptg.util.CommonUtil;
import org.ptg.util.CommonUtils;
import org.ptg.util.db.DBHelper;

public class GenerateFunctionBlocks extends AbstractHandler {
	// http://localhost:8095/site/GenerateFunctionBlocks?cls=org.ptg.util.AbstractOpencvProcessor&mtd=calculateWhitePixelCount&name=calcWhitePixel
	// http://localhost:8095/site/GenerateFunctionBlocks?cls=org.ptg.util.CommonUtils&mtd=jaxbMarshall&name=calcWhitePixel
	// scc=java.lang.String <ret> =jaxbMarshall({o},{pack});
	// vdc={o:java.lang.Object}{pack:java.lang.String}<ret:java.lang.String>
	// &scc=java.lang.String <ret>
	// =jaxbMarshall({o},{pack},{packo});&vdc={o:java.lang.String}{pack:java.lang.String}{packo:java.lang.String}<ret:java.lang.String>
	// http://localhost:8095/site/GenerateFunctionBlocks?cls=org.ptg.util.CommonUtils&mtd=jaxbMarshall&name=calcWhitePixel&scc=java.lang.String
	// <ret>
	// =jaxbMarshall({o},{pack},{packo});&vdc={o:java.lang.String}{pack:java.lang.String}{packo:java.lang.String}<ret:java.lang.String>
	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			List<String> ret = new LinkedList<String>();
			String ip = "127.0.0.1";

			String cls = request.getParameter("cls");// "org.ptg.util.AbstractOpencvProcessor"
			String fqm = request.getParameter("mtd");// "calculateWhitePixelCount"
			String sname = request.getParameter("name");// "calculateWhitePixelCount"
			String uiType = request.getParameter("uitype");// StaticModule or
															// AnonScriptModule
			String grpId = request.getParameter("grpid");// if grpid specified
			// use it
			String nosave = request.getParameter("nosave");
			String scc = request.getParameter("scc");
			String vdc = request.getParameter("vdc");
			if (uiType == null) {
				uiType = "StaticModule";
			}

			if (sname == null || sname.length() < 1) {
				return;
			}
			if (cls == null || cls.length() < 1) {
				return;
			}
			if (scc != null && vdc != null) {
				String name = "${id}" + sname + "(MethodCall)";
				if (grpId != null) {
					name = grpId;
				}
				String[] retinternal = generateMethodBlock(ip, uiType, name, scc, vdc, nosave);

				ret.add(retinternal[0]);
				ret.add(retinternal[1]);

			} else {
				if (fqm.equals("*")) {
					Class c = Class.forName(cls);
					Set<String> m = new HashSet<String>();
					for (Method mtd : c.getDeclaredMethods()) {
						m.add(mtd.getName());
					}
					for (String s : m) {
						if (s.startsWith("main") || s.equals("init") || s.equals("test")) {
							continue;
						}
						String[] retinternal = generateMethodInternal(ip, cls, s, s, uiType, grpId, nosave);
						ret.add(retinternal[0]);
						ret.add(retinternal[1]);

					}
				} else {
					if (fqm == null || fqm.length() < 1) {
						fqm = sname;
					}
					String[] retinternal = generateMethodInternal(ip, cls, fqm, sname, uiType, grpId, nosave);
					ret.add(retinternal[0]);
					ret.add(retinternal[1]);

				}
			}
			response.getOutputStream().print(CommonUtil.jsonFromCollection(ret));
		} catch (Exception e) {
			response.getOutputStream().print("Document cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	private String[] generateMethodInternal(String ip, String cls, String fqm, String sname, String uiType, String grpId, String nosave) throws Exception {
		String name = "${id}" + sname + "(MethodCall)";
		if (grpId != null) {
			name = grpId;
		}
		String scc = getScriptCode(cls, fqm);
		String vdc = getVarDefCode(cls, fqm);
		return generateMethodBlock(ip, uiType, name, scc, vdc, nosave);
	}

	public static String[] generateMethodBlock(String ip, String uiType, String name, String scc, String vdc, String nosave) {
		String scriptCode = scc;// "IplImage <out> = IplImage.create({val1}, {val2}, IPL_DEPTH_8U, 1);";
		String vardef = vdc;// "{val1:inttype}{val2:inttype}<out:IplImage>";
		CodeToPortJava cpj = new CodeToPortJava();
		String code = cpj.generateTemplateCodeWithDtype(name, vardef, "Script2");
		String retCode = code;
		if (nosave == null) {
			code = StringEscapeUtils.escapeJavaScript(code);
			String toSave = "{\"id\":\"" + name + "\",\"name\":\"" + uiType + "\",\"aid\":0,\"mouseX\":974,\"mouseY\":278,\"cntn\":\"" + code
					+ "\",\"left\":\"925px\",\"top\":\"372px\",\"width\":\"190px\",\"height\":\"87px\",\"zindex\":194,\"rotation\":\"0deg\",\"script\":\"" + scriptCode + "\",\"code\":\"" + code
					+ "\"}";
			String doc = "";

			// toSave = StringEscapeUtils.escapeJavaScript(toSave);

			String backsql = "insert into deletedstaticcomponent (select * from staticcomponent where name='" + name + "')";
			String delsql = "delete from staticcomponent where name='" + name + "'";
			String inssql = "insert into staticcomponent(name,txt,userid,userip,doc) values (?,?,?,?,?)";
			DBHelper.getInstance().executeUpdate(backsql);
			DBHelper.getInstance().executeUpdate(delsql);
			DBHelper.getInstance().executePreparedInsert(inssql, name, toSave, ip, ip, doc);
		}
		return new String[] { retCode, scriptCode };
	}

	public String getScriptCode(String cls, String fqm) throws Exception {
		Builder b = new Builder("C:/Projects/Caerusone/bin", "org.ptg.*", "C:/Projects/Caerusone/lib/", "C:/Projects/Caerusone/upload/genclasses");
		CtClass cc = b.getClassFile(cls);
		List<Setter> setters = CommonUtils.analyzeBytecode(cc.toBytecode());
		for (Setter mtd : setters) {
			if (mtd.getName().equals(fqm)) {
				StringBuilder sc = new StringBuilder();
				String name = mtd.getReturnType();
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
				for (Map.Entry<String, String> pcl : mtd.getParams().entrySet()) {
					sc.append("{");
					sc.append(pcl.getKey());
					sc.append("}");
					index++;
					if (index != mtd.getParams().size()) {
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
		List<Setter> setters = CommonUtils.analyzeBytecode(cc.toBytecode());
		int index = 0;
		for (Setter mtd : setters) {
			if (mtd.getName().equals(fqm)) {
				StringBuilder sc = new StringBuilder();
				for (Map.Entry<String, String> pcl : mtd.getParams().entrySet()) {
					sc.append("{");
					sc.append(pcl.getKey());
					sc.append(":");
					String name = pcl.getValue();
					if (name.contains("$")) {
						name = StringUtils.substringAfterLast(name, "$");
					}
					sc.append(name);
					sc.append("}");
					index++;
				}
				String name = mtd.getReturnType();
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
