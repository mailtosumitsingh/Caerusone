/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.ptg.cluster.AppContext;
import org.ptg.events.Event;
import org.ptg.util.extern.JavaExternalizer;
import org.ptg.velocity.VelocityHelper;

public class DynaObjectHelper {
	private static String buildDir;
	static JavaExternalizer ext = new JavaExternalizer();
	static {

		AppContext ctx = (AppContext) SpringHelper.get("appContext");
		buildDir = ctx.getBuildDir();
		ext.buildSimpleExternalizer();
	}

	public static String getBuildDir() {
		return buildDir;
	}

	public static void setBuildDir(String buildDir) {
		DynaObjectHelper.buildDir = buildDir;
	}

	private static Map<String, String> inmap = new LinkedHashMap<String, String>();
	private static Map<String, String> outmap = new LinkedHashMap<String, String>();
	private static Map<String, String> postoutmap = new LinkedHashMap<String, String>();
	static {
		outmap.put("double", "$1.writeDouble(");
		outmap.put("java.lang.String", "$1.writeUTF(");
		outmap.put("java.util.Date", "$1.writeLong(");
		outmap.put("java.lang.Integer", "$1.writeInt(");
		outmap.put("java.lang.Double", "$1.writeDouble(");
		outmap.put("java.lang.Float", "$1.writeFloat(");
		outmap.put("java.lang.Boolean", "$1.writeBoolean(");
		outmap.put("boolean", "$1.writeBoolean(");
		outmap.put("int", "$1.writeInt(");
		outmap.put("long", "$1.writeLong(");
		outmap.put("float", "$1.writeFloat(");
		postoutmap.put("java.util.Date", ".getTime()");
		postoutmap.put("double", "");
		postoutmap.put("long", "");
		postoutmap.put("int", "");
		postoutmap.put("float", "");
		postoutmap.put("boolean", "");
		postoutmap.put("java.lang.Boolean", ".booleanValue()");
		postoutmap.put("java.lang.String", "");
		postoutmap.put("java.lang.Integer", ".intValue()");
		postoutmap.put("java.lang.Double", ".doubleValue()");
		postoutmap.put("java.lang.Float", ".floatValue()");
		inmap.put("java.lang.Boolean", "new java.lang.Boolean($1.readBoolean");
		inmap.put("boolean", "($1.readBoolean");

		inmap.put("int", "($1.readInt");
		inmap.put("double", "($1.readDouble");
		inmap.put("float", "($1.readFloat");
		inmap.put("java.lang.Integer", "new java.lang.Integer($1.readInt");
		inmap.put("java.lang.Double", " new java.lang.Double($1.readDouble");
		inmap.put("java.util.Float", " new java.lang.Float($1.readFloat");

		inmap.put("java.lang.String", "($1.readUTF");
		inmap.put("java.util.Date", "new java.util.Date($1.readLong");
		inmap.put("long", "($1.readLong");
	}

	public static Class getClass(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, Map<String, Map<String, Object>> collTypeHints, boolean updated) {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;

		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			System.out.println("Class does not exists will create now: " + name);
		}
		if (c == null || (c != null && updated)) {
			String temp = "";
			if (updated && c != null) {
				temp = CommonUtil.getRandomString(16);
			}
			CtClass cc = pool.makeClass(name + temp);
			try {
				cc.setSuperclass(pool.get("org.ptg.events.Event"));
				for (Map.Entry<String, String> en : def.entrySet()) {

					CtClass ctClass = pool.get(en.getValue());
					CtField cf = new CtField(ctClass, en.getKey(), cc);
					cf.setModifiers(Modifier.PUBLIC);
					ConstPool cp = cf.getFieldInfo().getConstPool();
					List<Annotation> annots = new LinkedList<Annotation>();
					if (collTypeHints != null) {
						Map<String, Object> hintmap = collTypeHints.get(en.getKey());
						Annotation a = getAnnotation(cf, cp, hintmap, "org.ptg.util.CollTypeAnot");
						annots.add(a);
					}
					if (hints != null) {
						Map<String, Object> hintmap = hints.get(en.getKey());
						Annotation a = getAnnotation(cf, cp, hintmap, "Property");
						annots.add(a);
					}

					AnnotationsAttribute attr = (AnnotationsAttribute) cf.getFieldInfo().getAttribute(AnnotationsAttribute.visibleTag);
					if (attr == null) {
						attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
					}
					for (Annotation a : annots) {
						attr.addAnnotation(a);
					}
					cf.getFieldInfo().addAttribute(attr);
					cc.addField(cf);
					CtMethod cm = getGetter(en.getValue(), en.getKey(), cc);
					cc.addMethod(cm);
					cm = getSetter(en.getValue(), en.getKey(), cc);
					cc.addMethod(cm);
				}
			} catch (CannotCompileException e) {
				e.printStackTrace();
				return null;
			} catch (NotFoundException e) {
				e.printStackTrace();
				return null;
			}
			try {
				CtMethod cm = getToXml(def, cc);
				cc.addMethod(cm);
				CtMethod cm2 = getToJson(cc);
				cc.addMethod(cm2);
				CtMethod cm3 = getToMap(def, cc);
				cc.addMethod(cm3);
				CtMethod cm4 = getReadExternal(def, cc);
				cc.addMethod(cm4);
				CtMethod cm5 = getWriteExternal(def, cc);
				cc.addMethod(cm5);
				cc.stopPruning(true);
				cc.getClassFile().setVersionToJava5();
				if (buildDir != null) {
					try {
						cc.debugWriteFile(buildDir);// sumit changed from
													// writeFile
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return cc.toClass();
			} catch (CannotCompileException e) {
				e.printStackTrace();
				return null;
			}
		}

		return c;
	}

	public static Class getClassV2(String name, Map<String, PropInfo<PropInfo>> def, Map<String, Map<String, Object>> hints, Map<String, Map<String, Object>> collTypeHints, boolean updated) {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;

		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			System.out.println("Class does not exists will create now: " + name);
		}
		if (c == null || (c != null && updated)) {
			String temp = "";
			if (updated && c != null) {
				temp = CommonUtil.getRandomString(16);
			}
			CtClass cc = pool.makeClass(name + temp);
			try {
				cc.setSuperclass(pool.get("org.ptg.events.Event"));
				for (Map.Entry<String, PropInfo<PropInfo>> en : def.entrySet()) {

					CtClass ctClass = pool.get(en.getValue().getPropClass());
					CtField cf = null;
					cf = CtField.make("private " + StringUtils.replace(en.getValue().getCollType().getDef(), "$name", en.getValue().getPropClass()) + " " + en.getKey() + " ; ", cc);
					cf.setModifiers(Modifier.PUBLIC);
					ConstPool cp = cf.getFieldInfo().getConstPool();
					List<Annotation> annots = new LinkedList<Annotation>();
					if (collTypeHints != null) {
						Map<String, Object> hintmap = collTypeHints.get(en.getKey());
						Annotation a = getAnnotation(cf, cp, hintmap, "org.ptg.util.CollTypeAnot");
						annots.add(a);
					}
					if (hints != null) {
						Map<String, Object> hintmap = hints.get(en.getKey());
						Annotation a = getAnnotation(cf, cp, hintmap, "Property");
						annots.add(a);
					}

					AnnotationsAttribute attr = (AnnotationsAttribute) cf.getFieldInfo().getAttribute(AnnotationsAttribute.visibleTag);
					if (attr == null) {
						attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
					}
					for (Annotation a : annots) {
						attr.addAnnotation(a);
					}
					cf.getFieldInfo().addAttribute(attr);
					cc.addField(cf);
					CtMethod cm = getGetter(StringUtils.replace(en.getValue().getCollType().getDef(), "$name", en.getValue().getPropClass()), en.getKey(), cc);
					cc.addMethod(cm);
					cm = getSetter(StringUtils.replace(en.getValue().getCollType().getDef(), "$name", en.getValue().getPropClass()), en.getKey(), cc);
					cc.addMethod(cm);
				}
			} catch (CannotCompileException e) {
				e.printStackTrace();
				return null;
			} catch (NotFoundException e) {
				e.printStackTrace();
				return null;
			}
			try {

				CtMethod cm2 = getToJson(cc);
				cc.addMethod(cm2);
				/****************************
				 * CtMethod cm = getToXml(def, cc); cc.addMethod(cm); CtMethod
				 * cm3 = getToMap(def, cc); cc.addMethod(cm3); CtMethod cm4 =
				 * getReadExternal(def, cc); cc.addMethod(cm4); CtMethod cm5 =
				 * getWriteExternal(def, cc); cc.addMethod(cm5);
				 ****************************/
				cc.stopPruning(true);
				cc.getClassFile().setVersionToJava5();
				if (buildDir != null) {
					try {
						cc.debugWriteFile(buildDir);// sumit changed from
													// writeFile
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return cc.toClass();
			} catch (CannotCompileException e) {
				e.printStackTrace();
				return null;
			}
		}

		return c;
	}

	private static Annotation getAnnotation(CtField cf, ConstPool cp, Map<String, Object> hintmap, String annotationName) {
		Annotation a = null;
		if (hintmap != null) {
			a = new Annotation(annotationName, cp);
			for (Map.Entry<String, Object> entry : hintmap.entrySet()) {
				Object ob = entry.getValue();
				if (ob instanceof Boolean) {
					a.addMemberValue(entry.getKey(), new BooleanMemberValue((Boolean) entry.getValue(), cp));
				} else if (ob instanceof String) {
					a.addMemberValue(entry.getKey(), new StringMemberValue((String) entry.getValue(), cp));
				} else if (ob.getClass().equals(Integer.class)) {
					a.addMemberValue(entry.getKey(), new IntegerMemberValue(cp, Integer.valueOf(entry.getValue().toString())));
				} else if (ob.getClass().equals(Long.class)) {
					a.addMemberValue(entry.getKey(), new LongMemberValue(Long.valueOf(entry.getValue().toString()), cp));
				} else if (ob.getClass().equals(int.class)) {
					a.addMemberValue(entry.getKey(), new IntegerMemberValue(cp, Integer.valueOf(entry.getValue().toString())));
				} else if (ob.getClass().equals(long.class)) {
					a.addMemberValue(entry.getKey(), new LongMemberValue(Long.valueOf(entry.getValue().toString()), cp));
				} else if (ob.getClass().equals(short.class)) {
					a.addMemberValue(entry.getKey(), new ShortMemberValue(Short.valueOf(entry.getValue().toString()), cp));
				} else if (ob.getClass().equals(Short.class)) {
					a.addMemberValue(entry.getKey(), new ShortMemberValue(Short.valueOf(entry.getValue().toString()), cp));
				}
				/*
				 * else if (ob.getClass().isEnum()) {
				 * a.addMemberValue(entry.getKey(), new
				 * EnumMemberValue(Short.valueOf(entry.getValue().toString()),
				 * cp)); }
				 */
				else if (ob instanceof Double) {
					a.addMemberValue(entry.getKey(), new DoubleMemberValue((Double) entry.getValue(), cp));
				}
			}
		}
		return a;
	}

	public static Class getClass(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, boolean updated) {
		return getClass(name, def, hints, null, updated);
	}

	public static CtMethod getToXml(Map<String, String> def, CtClass decl) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public String toXml();", decl);
		String body = burnTransformerToXml(def.entrySet());
		// System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	public static CtMethod getToJson(CtClass decl) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public String toJson();", decl);
		String body = burnTransformerToJson();
		// System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	public static CtMethod getToMap(Map<String, String> def, CtClass decl) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public java.util.Map toMap();", decl);
		String body = burnTransformerToMap(def.entrySet());
		// System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	public static CtMethod getGetter(String type, String prop, CtClass decl) throws CannotCompileException {
		String actual = WordUtils.capitalize(prop);
		CtMethod cm = CtMethod.make("public " + type + " get" + actual + "();", decl);
		cm.setBody("return " + prop + " ;");
		return cm;
	}

	public static CtMethod getSetter(String type, String prop, CtClass decl) throws CannotCompileException {
		String actual = WordUtils.capitalize(prop);
		String mtdStr = "public void set" + actual + "(" + type + " " + actual + ");";
		String bodyStr = "this." + prop + " = $1;";
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(bodyStr);
		return cm;
	}

	public static CtMethod getReadExternal(Map<String, String> def, CtClass decl) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public void 	readExternal(java.io.ObjectInput in)  throws java.io.IOException,java.lang.ClassNotFoundException;", decl);
		String body = burnReadObject(def.entrySet());
		// System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	public static CtMethod getWriteExternal(Map<String, String> def, CtClass decl) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public void 	writeExternal(java.io.ObjectOutput out) throws java.io.IOException;", decl);
		String body = burnWriteObject(def.entrySet());
		// System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	public static void main2(String[] args) throws Exception {
		Map<String, String> temp = new LinkedHashMap<String, String>();
		Map<String, Object> temp2 = new LinkedHashMap<String, Object>();
		temp.put("a", "java.lang.String");
		temp.put("b", "java.lang.Integer");
		temp.put("c", "java.lang.Double");
		temp.put("d", "java.lang.Boolean");

		temp2.put("a", "dada");
		temp2.put("b", 1);
		temp2.put("c", 10.2);
		temp2.put("d", true);

		HashMap<String, Map<String, Object>> m = new HashMap();
		m.put("b", temp2);
		Class c = getClass("dada", temp, m, true);
		Object o = c.newInstance();

		ReflectionUtils.invoke(o, "setA", new Object[] { "dada" });
		ReflectionUtils.invoke(o, "setB", new Object[] { 1234 });
		ReflectionUtils.invoke(o, "setC", new Object[] { 10.20d });
		ReflectionUtils.invoke(o, "setD", new Object[] { true });
		ReflectionUtils.invoke(o, "setD", new Object[] { true });

		Object k = ReflectionUtils.invoke(o, "getA", new Object[0]);
		System.out.println(k);
		k = ReflectionUtils.invoke(o, "getB", new Object[0]);
		System.out.println(k);
		k = ReflectionUtils.invoke(o, "getC", new Object[0]);
		System.out.println(k);

		Event ev = (Event) o;
		CommonUtil.dump(o);
		System.out.println(ev.toXml());
		System.out.println(ev.toJson());
		byte[] bts = CommonUtil.writeSerialized(ev);
		Event ev2 = (Event) CommonUtil.readSerialized(bts);
		System.out.println(ev2.toJson());
		System.out.println(ev2.toXml());

	}

	public static List getMethods(String className) {
		List m = new ArrayList();
		ClassPool c = CommonUtil.getClassPool();
		CtClass cl;
		try {
			cl = c.get(className);
			CtMethod[] cfs = cl.getDeclaredMethods();
			for (CtMethod cf : cfs) {
				m.add(cf.getName());
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	public static List getMethodsWithSig(String className) {
		List m = new ArrayList();
		ClassPool c = CommonUtil.getClassPool();
		CtClass cl;
		try {
			cl = c.get(className);
			CtMethod[] cfs = cl.getDeclaredMethods();
			for (CtMethod cf : cfs) {
				m.add(cf.getName() + cf.getSignature());
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	public static Map getFields(String className) {
		Map m = new TreeMap();
		ClassPool c = CommonUtil.getClassPool();
		CtClass cl;
		try {
			cl = c.get(className);
			CtField[] cfs = cl.getDeclaredFields();
			for (CtField cf : cfs) {
				m.put(cf.getName(), cf.getType().getName());
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	public static Map getFieldValues(String className) {
		Map m = new TreeMap();
		ClassPool c = CommonUtil.getClassPool();
		CtClass cl;
		try {
			cl = c.get(className);
			CtField[] cfs = cl.getDeclaredFields();
			for (CtField cf : cfs) {
				m.put(cf.getName(), cf.getConstantValue());
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;

	}

	public static String hasMethod(String className, String name, boolean silent) {
		ClassPool c = CommonUtil.getClassPool();
		CtClass cl;
		try {
			cl = c.get(className);
			CtMethod[] method = cl.getMethods();
			for (CtMethod m : method) {
				if (m.getName().equals(name))
					return name;
			}
			return name;
		} catch (NotFoundException e) {
			if (!silent)
				System.out.println("name not found");
		}
		return null;
	}

	public static Annotation getAnnotation(String cls, String field, String annotation) throws Exception {
		CtClass ct = CommonUtil.getClassPool().get(cls);
		Annotation an = null;
		ct.defrost();
		FieldInfo minfo = ct.getField(field).getFieldInfo();
		AnnotationsAttribute attr = (AnnotationsAttribute) minfo.getAttribute(annotation);// sumit
		// changed
		if (attr != null) {
			an = attr.getAnnotation(annotation);
		}
		return an;
	}

	public static String burnTransformerToXml(Set<Map.Entry<String, String>> val) {
		Map<String, String> values = new HashMap<String, String>();
		String inFile = "toxml.vm";
		for (Map.Entry<String, String> v : val) {
			values.put(v.getKey(), "get" + WordUtils.capitalize(v.getKey()) + "()");
		}
		Map map = new HashMap();
		map.put("properties", values.entrySet());

		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnTransformerToJson() {
		String inFile = "tojson.vm";
		Map map = new HashMap();
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnTransformerToMap(Set<Map.Entry<String, String>> val) {
		Map<String, String> values = new HashMap<String, String>();
		String inFile = "tomap.vm";
		for (Map.Entry<String, String> v : val) {
			values.put(v.getKey(), "get" + WordUtils.capitalize(v.getKey()) + "()");
		}
		Map map = new HashMap();
		map.put("properties", values.entrySet());

		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnWriteObject(Set<Map.Entry<String, String>> val) {
		// void writeExternal(ObjectOutput out) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("super.writeExternal($1);");
		for (Map.Entry<String, String> v : val) {
			sb.append("\n" + outStmt(v.getKey(), v.getValue()));
		}
		sb.append("\n}");

		return sb.toString();
	}

	public static String burnReadObject(Set<Map.Entry<String, String>> val) {
		// void readExternal(ObjectInput in) throws
		// IOException,ClassNotFoundException
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("super.readExternal($1);");
		for (Map.Entry<String, String> v : val) {
			sb.append("\n" + inStmt(v.getKey(), v.getValue()));
		}
		sb.append("\n}");
		return sb.toString();
	}

	public static String outStmt(String var, String type) {
		if (type.equals("java.lang.String"))
			return outmap.get(type) + postoutmap.get(type) + "org.ptg.util.CommonUtil.getNullString(" + var + "));";
		else {
			String mapped = outmap.get(type);
			if (mapped == null)
				return "";
			return mapped + "this." + var + postoutmap.get(type) + ");";
		}
	}

	public static String inStmt(String var, String type) {
		String mapped = inmap.get(type);
		if (mapped == null)
			return "";
		else
			return "this." + var + " = " + mapped + "(" + "));";
	}

	public static Class getClass(String name, byte[] bytes) {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;
		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			System.out.println("Class does not exists will create now: " + name);
		}
		if (c == null && bytes != null) {
			CtClass cc = null;
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			try {

				cc = pool.makeClass(b, false);
				// cc = pool.makeClass(b);
				cc.setName(name);
				c = cc.toClass();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return c;
	}

	public static byte[] getClassBytes(String name) {
		ClassPool pool = CommonUtil.getClassPool();
		CtClass cc = null;
		byte[] data = null;
		try {
			cc = pool.get(name);
			data = cc.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public static Class extendClass(String cname, byte[] data, String extensionClass, Map<String, String> def) {
		try {
			ClassPool pool = CommonUtil.getClassPool();
			CtClass cc = pool.makeClass(new ByteArrayInputStream(data));
			cc.setName(cname);
			if (extensionClass != null) {
				cc.setSuperclass(pool.get(extensionClass));
			}
			if (def != null) {
				CtMethod cm = getToXml(def, cc);
				cc.addMethod(cm);
				CtMethod cm2 = getToJson(cc);
				cc.addMethod(cm2);
				CtMethod cm3 = getToMap(def, cc);
				cc.addMethod(cm3);
				CtMethod cm4 = getReadExternal(def, cc);
				cc.addMethod(cm4);
				CtMethod cm5 = getWriteExternal(def, cc);
				cc.addMethod(cm5);
			}
			cc.stopPruning(true);
			return cc.toClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CtClass getClassType(String name, byte[] bytes) {
		ClassPool pool = CommonUtil.getClassPool();
		CtClass cc = null;
		try {
			cc = pool.get(name);
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}
		if (cc == null && bytes != null) {
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			try {
				cc = pool.makeClass(b);
				cc.setName(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cc;
	}

	public static CtClass getClassType(String name, String extensionClass, String origClass) {
		byte[] bytes = getClassBytes(origClass);
		if (bytes != null) {
			return getClassType(name, extensionClass, bytes);
		}
		return null;
	}

	public static CtClass getClassType(String name, String extensionClass, byte[] bytes) {
		ClassPool pool = CommonUtil.getClassPool();
		CtClass cc = null;
		try {
			cc = pool.get(name);
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}
		if (cc == null && bytes != null) {
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			try {
				cc.setSuperclass(pool.get(extensionClass));
				cc = pool.makeClass(b);
				cc.setName(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cc;
	}

	public static Map<String, String> externClass(String name, boolean declaredOnly) {
		Map<String, String> m = new LinkedHashMap<String, String>();
		CtClass c = DynaObjectHelper.getClassType(name, null);
		CtMethod[] mtds = (declaredOnly == true ? c.getDeclaredMethods() : c.getMethods());
		for (CtMethod mt : mtds) {
			if (mt.getName().startsWith("get")) {
				String prop = WordUtils.uncapitalize(StringUtils.substringAfter(mt.getName(), "get"));
				// System.out.println(prop);
				try {
					m.put(prop, mt.getReturnType().getName());
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return m;
	}

	public static CtMethod getReadExternalRuntime(Map<String, String> def, CtClass decl) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public void 	readExternal(java.io.ObjectInput in)  throws java.io.IOException,java.lang.ClassNotFoundException;", decl);
		String body = ext.burnReadObject(def.entrySet());
		System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	public static CtMethod getWriteExternalRuntime(Map<String, String> def, CtClass decl) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public void 	writeExternal(java.io.ObjectOutput out) throws java.io.IOException;", decl);
		String body = ext.burnWriteObject(def.entrySet());
		System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	public static Class externalizeClass(String name, String type, boolean runtime) {
		ClassPool pool = CommonUtil.getClassPool();
		Map<String, String> mp = DynaObjectHelper.externClass(name, true);
		Set<Map.Entry<String, String>> val = mp.entrySet();
		/*
		 * for(Map.Entry<String, String> en :val){
		 * System.out.println(en.getKey()); System.out.println(en.getValue()); }
		 */Class c = pool.getClass();
		try {
			CtClass cc = pool.getCtClass(name);
			if (cc == null) {
				cc = pool.makeClass(name);
				CtMethod cm = getReadExternalRuntime(mp, cc);
				cc.addMethod(cm);
				CtMethod cm2 = getWriteExternalRuntime(mp, cc);
				cc.addMethod(cm2);
				cc.stopPruning(true);
				cc.setName(type);
				if (name != null && runtime == false) {
					if (!(name.equals(type)) && buildDir != null) {
						try {
							cc.debugWriteFile(buildDir);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				return cc.toClass();
			} else {
				return cc.getClass();
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * public static void reload(String name,byte[] bytes ){ CtClass clazz = ...
	 * byte[] classFile = clazz.toBytecode(); HotSwapper hs = new
	 * HostSwapper(8000); // 8000 is a port number. hs.reload("Test",
	 * classFile);
	 * 
	 * }
	 */
	public static Class getClosureImpl(String name, String code) {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;
		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			System.out.println("Class does not exists will create now: " + name);
		}
		if (c == null && code != null) {
			CtClass cc = null;
			try {
				cc = pool.makeClass(name);
				CtClass intr = pool.getCtClass("org.apache.commons.collections.Closure");
				cc.addInterface(intr);
				CtMethod cm = CtMethod.make("public void 	execute(Object input) ;", cc);
				cm.setBody(code);
				cc.addMethod(cm);
				c = cc.toClass();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return c;
	}

	public static Class getClosureImplWithException(String name, String code, boolean updateVersion) throws Exception {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;
		try {
			c = Class.forName(name);
			c = null;
			name += CommonUtil.getRandomString(8);
		} catch (ClassNotFoundException e) {
			System.out.println("Class does not exists will create now: " + name);
		}
		if (c == null && code != null) {
			CtClass cc = null;
			cc = pool.makeClass(name);
			CtClass intr = pool.getCtClass("org.apache.commons.collections.Closure");
			cc.addInterface(intr);
			CtMethod cm = CtMethod.make("public void 	execute(Object input) ;", cc);
			cm.setBody(code);
			cc.addMethod(cm);
			c = cc.toClass();
		}
		return c;
	}

	public static void main(String[] args) throws Exception {
		Class c = getClosureImpl("dada", "System.out.println(\"dada dum dum\");");
		Closure cc = (Closure) c.newInstance();
		cc.execute(null);
	}

	public static CtMethod getServiceMethod(CtClass decl, String in, String out, String code) throws CannotCompileException {
		CtMethod cm = CtMethod.make("public " + out + " 	Service(" + in + " in) throws java.io.IOException;", decl);
		System.out.println(code);
		cm.setBody(code);
		return cm;
	}

	public static Class getServiceClass(String name, String code, String in, String out, String savePath) throws Exception {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;
		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			System.out.println("Class does not exists will create now: " + name);
		}
		if (c == null) {
			CtClass cc = null;
			cc = pool.makeClass(name);
			CtMethod cm = getServiceMethod(cc, in, out, code);
			cc.addMethod(cm);
			c = cc.toClass();
			if (savePath != null)
				cc.writeFile(savePath);
		}
		return c;
	}

	public static Class generateClassFromPropInfo(PropInfo<PropInfo> p) {
		// generate the class now really
		Map<String, PropInfo<PropInfo>> def = new HashMap<String, PropInfo<PropInfo>>();
		Map<String, Map<String, Object>> hints = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> collTypeHints = new HashMap<String, Map<String, Object>>();

		for (PropInfo child : p.getChilds()) {
			String name = child.getName();
			String propClass = child.getPropClass();
			Map<String, Object> acth = new HashMap<String, Object>();
			acth.put("collType", child.getCollType().name());
			acth.put("collValType", propClass);
			Map<String, Object> ah = new HashMap<String, Object>();
			ah.put("index", 0);
			ah.put("searchable", false);
			def.put(name, child);
			hints.put(name, ah);
			collTypeHints.put(name, acth);
		}
		Class cls = DynaObjectHelper.getClassV2(p.getPropClass(), def, hints, collTypeHints, true);
		return cls;
	}

	public static void testGeneratedPropInfoClass(Class cls, PropInfo<PropInfo> p) throws Exception {
		for (PropInfo child : p.getChilds()) {
			String name = child.getName();
			java.lang.annotation.Annotation[] annots = cls.getField(name).getDeclaredAnnotations();
			for (java.lang.annotation.Annotation a : annots) {
				System.out.println(a.toString());
			}
		}
	}
}
