/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.apache.commons.lang.WordUtils;
import org.ptg.cluster.AppContext;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.events.PropertyDefinition;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamDefinition;
import org.ptg.velocity.VelocityHelper;


public class StreamTransformerHelper {
	private static int MAX_VERSION = 10000;
	private static boolean donotRecreate = true;
	private static String buildDir;
	static {
		AppContext ctx = (AppContext) SpringHelper.get("appContext");
		buildDir = ctx.getBuildDir();
	}

	public static String getBuildDir() {
		return buildDir;
	}

	public static void setBuildDir(String buildDir) {
		StreamTransformerHelper.buildDir = buildDir;
	}

	public static Class getTransformerClass(Stream s,boolean update) {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;
		CtClass cc = null;

		String objectClassName = s.getEventType();
		String transformerName = Constants.StreamTransformer + s.getName() + s.getEventType();
		try {
			System.out.println("Now transforming: " + transformerName);
			c = Class.forName(transformerName);
		} catch (ClassNotFoundException e) {
			System.out.println("Class does not exists will create now: " + transformerName);
		}
		if (c != null ) {
			if(donotRecreate ){
				return c;	
			}else if(!update){
				return c;
			}
			transformerName = transformerName + CommonUtil.getRandomString(8);
		}	
		
		try {
			cc = pool.makeClass(transformerName);
			CtMethod cmG = getTransformerGetter(objectClassName, s, cc, "org.apache.camel.Message");
			cc.addMethod(cmG);
			CtMethod cmO = getTransformerObjectGetter(objectClassName, s, cc, "org.ptg.events.Event");
			cc.addMethod(cmO);
			CtMethod cmm = getTransformerMapGetter(objectClassName, s, cc, "java.util.Map");
			cc.addMethod(cmm);
			CtMethod cmr = getTransformerResultSetGetter(objectClassName, s, cc, "java.sql.ResultSet");
			cc.addMethod(cmr);
			cmG = getTransformerGetter(objectClassName, s, cc, "org.apache.camel.impl.DefaultMessage");
			cc.addMethod(cmG);
			CtMethod cmj = getTransformerJsonGetter(objectClassName, s, cc, "org.apache.camel.Message");
			cc.addMethod(cmj);
			///added for exchange
			CtMethod cmexh = getTransformerExchange(objectClassName, s, cc, "org.apache.camel.Exchange");
			cc.addMethod(cmexh);
		
			CtMethod cmj2 = getTransformerJsonGetter(objectClassName, s, cc, "org.apache.camel.impl.DefaultMessage");
			cc.addMethod(cmj2);
			CtMethod cmh2 = getTransformerHTTPGetter(objectClassName, s, cc, "javax.servlet.http.HttpServletRequest");
			cc.addMethod(cmh2);
			CtMethod cmar = getTransformerArrayRowGetter(objectClassName, s, cc, "Object[]");
			cc.addMethod(cmar);
			CtMethod cmlr = getTransformerListRowGetter(objectClassName, s, cc, "java.util.List");
			cc.addMethod(cmlr);

			cc.addInterface(pool.get("org.ptg.util.IStreamTransformer"));
			cc.stopPruning(true);
			if (buildDir != null) {
				try {
					cc.writeFile(buildDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return cc.toClass();

		} catch (CannotCompileException e) {
			e.printStackTrace();
			return null;
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static CtMethod getTransformerListRowGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException{ 
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				if (destType == null)
					destType = srcType;
				if (prop != null)
					code += (StreamFormatter.transformExpression("rowlistprop", prop, xmlextra, destProp, srcType, destType, en.getValue().getIndex()));
			}
		}
		String compiledCode = burnTransformerGetTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		CtMethod cm = CtMethod.make("public " + "Object" + " " + "transformRow" + " " + "(" + classOfMesg + " " + " o);", decl);
		// System.out.println("public " + "Object"
		// +" "+Constants.MapTransformMethod +" "+"("+classOfMesg+" " +
		// " o);\n"+compiledCode);
		cm.setBody(compiledCode);

		return cm;
	}

	private static CtMethod getTransformerArrayRowGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException{ 
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				if (destType == null)
					destType = srcType;
				if (prop != null)
					code += (StreamFormatter.transformExpression("rowprop", prop, xmlextra, destProp, srcType, destType, en.getValue().getIndex()));
			}
		}
		String compiledCode = burnTransformerGetTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		CtMethod cm = CtMethod.make("public " + "Object" + " " + "transformRow" + " " + "(" + classOfMesg + " " + " o);", decl);
		// System.out.println("public " + "Object"
		// +" "+Constants.MapTransformMethod +" "+"("+classOfMesg+" " +
		// " o);\n"+compiledCode);
		cm.setBody(compiledCode);

		return cm;
	}

	public static CtMethod getTransformerGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				if (destType == null)
					destType = srcType;
				if (prop != null)
					code += (StreamFormatter.transformExpression(atype, prop, xmlextra, destProp, srcType, destType, 0));
			}
		}
		String compiledCode = burnTransformerGetTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		CtMethod cm = CtMethod.make("public " + "Object" + " " + Constants.StreamTranformMethod + " " + "(" + classOfMesg + " " + " o);", decl);
		// System.out.println(compiledCode);
		cm.setBody(compiledCode);
		return cm;
	}

	public static CtMethod getTransformerObjectGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				if (destType == null)
					destType = srcType;
				if (prop != null) {
					code += (StreamFormatter.transformExpression("objectprop", prop, xmlextra, destProp, srcType, destType, 0));
				}
			}
		}
		String compiledCode = burnTransformerGetTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);

		CtMethod cm = CtMethod.make("public " + "Object" + " " + Constants.ObjectTranformMethod + " " + "(" + classOfMesg + " " + " o);", decl);
		// System.out.println("public " + "Object"
		// +" "+Constants.ObjectTranformMethod+" "+"("+classOfMesg+" " +
		// " o);\n"+compiledCode);
		cm.setBody(compiledCode);

		return cm;
	}

	public static CtMethod getTransformerResultSetGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				if (destType == null)
					destType = srcType;
				if (prop != null)
					code += (StreamFormatter.transformExpression("sqlprop", prop, xmlextra, destProp, srcType, destType, 0));
			}
		}
		String compiledCode = burnTransformerGetTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		CtMethod cm = CtMethod.make("public " + "Object" + " " + Constants.ResultSetTransformMethod + " " + "(" + classOfMesg + " " + " o);", decl);
		// System.out.println("public " + "Object"
		// +" "+Constants.ResultSetTransformMethod +" "+"("+classOfMesg+" " +
		// " o);\n"+compiledCode);
		cm.setBody(compiledCode);
		return cm;
	}

	public static CtMethod getTransformerMapGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				if (destType == null)
					destType = srcType;
				if (prop != null)
					code += (StreamFormatter.transformExpression("mapprop", prop, xmlextra, destProp, srcType, destType, 0));
			}
		}
		String compiledCode = burnTransformerGetTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		CtMethod cm = CtMethod.make("public " + "Object" + " " + Constants.MapTransformMethod + " " + "(" + classOfMesg + " " + " o);", decl);
		// System.out.println("public " + "Object"
		// +" "+Constants.MapTransformMethod +" "+"("+classOfMesg+" " +
		// " o);\n"+compiledCode);
		cm.setBody(compiledCode);

		return cm;
	}
	public static CtMethod getTransformerExchange(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public " + "Object" + " " + "transformExchange" + " " + "(" + classOfMesg + " " + " o);", decl);
		cm.setBody("{return null;}");
		return cm;
	}
	public static CtMethod getTransformerJsonGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				if (destType == null)
					destType = srcType;
				if (prop != null)
					code += (StreamJsonFormatter.transformExpression(prop, xmlextra, destProp, srcType, destType));
			}
		}
		String compiledCode = burnTransformerGetJsonTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		CtMethod cm = CtMethod.make("public " + "Object" + " " + Constants.StreamTranformJsonMethod + " " + "(" + classOfMesg + " " + " o);", decl);
		cm.setBody(compiledCode);
		return cm;
	}

	public static String getDestinationType(String prop, String eventType) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(eventType);
		if (ed != null) {
			Map<String, PropertyDefinition> p = ed.getProps();
			for (PropertyDefinition pd : p.values()) {
				if (pd.getName().equals(prop)) {
					return pd.getType();
				}

			}
		}
		return null;
	}

	public static String burnTransformerGetTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "StreamTransGet.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", "somevalue");
		map.put("dynaques", "somevalue");
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		// System.out.println(s);
		return s.toString();
	}

	public static String burnTransformerHTTPTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "StreamTransHTTPGet.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", "somevalue");
		map.put("dynaques", "somevalue");
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		// System.out.println(s);
		return s.toString();
	}

	public static String burnTransformerGetJsonTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "StreamTransJsonGet.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", "somevalue");
		map.put("dynaques", "somevalue");
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		// System.out.println(s);
		return s.toString();
	}

	public static CtMethod getTransformerHTTPGetter(String name, Stream s, CtClass decl, String classOfMesg) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, StreamDefinition> en : s.getDefs().entrySet()) {
			{

				String atype = en.getValue().getAccessor();
				String prop = en.getValue().getName();
				String xmlextra = en.getValue().getXmlExpr();
				String destProp = en.getValue().getDestProp();
				String srcType = en.getValue().getType();
				String destType = getDestinationType(destProp, s.getEventType());
				
				if (destType == null)
					destType = srcType;
				if (prop != null){
					{
						code += (StreamFormatter.transformExpression("httpprop", prop, xmlextra, destProp, srcType, destType, 0));
					}
				}
			}
		}
		String compiledCode = burnTransformerHTTPTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		CtMethod cm = CtMethod.make("public " + "Object" + " " + Constants.HTTPTransformMethod + " " + "(" + classOfMesg + " " + " o);", decl);
		// System.out.println("public " + "Object"
		// +" "+Constants.HTTPTransformMethod +" "+"("+classOfMesg+" " +
		// " o);\n"+compiledCode);
		cm.setBody(compiledCode);

		return cm;
	}
}
