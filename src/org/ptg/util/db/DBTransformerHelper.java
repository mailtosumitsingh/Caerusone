/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.db;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.ptg.cluster.AppContext;
import org.ptg.util.CommonUtil;
import org.ptg.util.Constants;
import org.ptg.util.DynaObjectHelper;
import org.ptg.util.NativeFormatter;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.SpringHelper;
import org.ptg.velocity.VelocityHelper;

public class DBTransformerHelper {
	static public Map<Object, String> types = new HashMap<Object, String>();
	static public Map<String, String> fromDbMtdMap = new HashMap<String, String>();
	static public Map<String, String> toDbMtdMap = new HashMap<String, String>();
	static public Map<String, String> DbToJavaMtdMap = new HashMap<String, String>();
	static public Map<String, String> dataTypeMap = new HashMap<String, String>();
    public DBTransformerHelper(){
    }
	private static String buildDir;
	static {
		AppContext ctx = (AppContext) SpringHelper.get("appContext");
		buildDir = ctx.getBuildDir();
    	init();
	}

	public static String getBuildDir() {
		return buildDir;
	}

	public static void setBuildDir(String buildDir) {
		DBTransformerHelper.buildDir = buildDir;
	}

	public static Class getTransformerClass(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, String store, boolean useRealName) {
		ClassPool pool = CommonUtil.getClassPool();
		Class c = null;
		CtClass cc = null;

		String objectClassName = name;
		String transformerName = name + Constants.Transformer + (useRealName ? "Real" : "");
		try {
			c = Class.forName(transformerName);
		} catch (ClassNotFoundException e) {
			System.out.println("Class does not exists will create now: " + transformerName);
		}
		if (c == null) {
			try {
				cc = pool.makeClass(transformerName);
				CtField cf = new CtField(pool.get("java.lang.String"), "Table", cc);
				cf.setModifiers(Modifier.PRIVATE);
			//	cf.setModifiers(Modifier.STATIC);
				cc.addField(cf);
				cf = new CtField(pool.get("org.ptg.util.db.DBHelper"), "dataSource", cc);
				cf.setModifiers(Modifier.PRIVATE);
			//	cf.setModifiers(Modifier.STATIC);
				cc.addField(cf);
				CtMethod cmG = getTransformerGetter(objectClassName, def, hints, cc, useRealName);
				cc.addMethod(cmG);
				CtMethod cmS = getTransformerSetter(objectClassName, def, hints, cc, useRealName);
				cc.addMethod(cmS);
				CtMethod cmu = getTransformerUpdate(objectClassName, def, hints, cc, useRealName);
				cc.addMethod(cmu);
				CtMethod cmux = getTransformerUpdateXref(objectClassName, def, hints, cc, useRealName);
				cc.addMethod(cmux);

				CtMethod cmd = getTransformerDelete(cc, useRealName);
				cc.addMethod(cmd);
				CtMethod cmdxRef = getTransformerDeleteByXref(cc, useRealName);
				cc.addMethod(cmdxRef);
				CtMethod cmdTableSet = getTransformerSetTable(cc, useRealName);
				cc.addMethod(cmdTableSet);
				CtMethod cmdTableGet = getTransformerGetTable(cc, useRealName);
				cc.addMethod(cmdTableGet);
				CtMethod cmGN = getTransformerGetterNative(objectClassName, def, hints, cc, useRealName);
				cc.addMethod(cmGN);
				CtMethod cmSN = getTransformerSetterNative(objectClassName, def, hints, cc, useRealName);
				cc.addMethod(cmSN);
				CtMethod cmuN = getTransformerUpdateNative(objectClassName, def, hints, cc, useRealName);
				cc.addMethod(cmuN);
				CtMethod cmdDataSourceSet = getTransformerSetDataSource(cc, useRealName);
				cc.addMethod(cmdDataSourceSet);
				CtMethod cmdDataSourceGet = getTransformerGetDataSource(cc, useRealName);
				cc.addMethod(cmdDataSourceGet);
				
				try {
					cc.addInterface(pool.get("org.ptg.util.IEventDBTransformer"));
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
				cc.stopPruning(true);
				Class cls = cc.toClass();
				if (store != null) {
					ReflectionUtils.setStaticFieldValue(ReflectionUtils.createInstance(cls.getName()), "Table", store);
				} else {
					ReflectionUtils.setStaticFieldValue(ReflectionUtils.createInstance(cls.getName()), "Table", Constants.DefaultEventTable);
				}
				if (buildDir != null) {
					try {
						cc.writeFile(buildDir);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return cls;

			} catch (CannotCompileException e) {
				e.printStackTrace();
				return null;
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return c;
		}
		return null;
	}

	public static CtMethod getTransformerSetTable(CtClass decl, boolean useRealName) throws CannotCompileException {
		String s = "{this.Table = $1;}";
		String mtdStr = "public void setStore(String " + " o);";
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(s);
		return cm;
	}

	public static CtMethod getTransformerGetTable(CtClass decl, boolean useRealName) throws CannotCompileException {
		String s = "{return this.Table ;}";
		String mtdStr = "public String getStore();";
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(s);
		return cm;

	}
	public static CtMethod getTransformerSetDataSource(CtClass decl, boolean useRealName) throws CannotCompileException {
		String s = "{\nthis.dataSource = 		org.ptg.util.db.DBHelper.getInstance($1);\n}";
		String mtdStr = "public void setDataSource(String source);";
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(s);
		return cm;
	}

	public static CtMethod getTransformerGetDataSource(CtClass decl, boolean useRealName) throws CannotCompileException {
		String s = "{\nreturn this.dataSource ;\n}";
		String mtdStr = "public org.ptg.util.db.DBHelper getDataSource();";
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(s);
		return cm;

	}
	public static CtMethod getTransformerDelete(CtClass decl, boolean useRealName) throws CannotCompileException {
		String s = burnTransformerDeleteTemplate();
		String mtdStr = "public void deleteFromDb(org.ptg.events.Event " + " o);";
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(s);
		return cm;

	}

	public static CtMethod getTransformerDeleteByXref(CtClass decl, boolean useRealName) throws CannotCompileException {
		String s = burnTransformerDeleteByXrefTemplate();
		String mtdStr = "public void deleteFromDbByXref(org.ptg.events.Event " + " o);";
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(s);
		return cm;

	}

	public static CtMethod getTransformerGetter(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, CtClass decl, boolean useRealName) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		String dynaques = "";
		String dynaprop = "";
		int count = 1;
		for (Map.Entry<String, String> en : def.entrySet()) {
			Map<String, Object> hintmap = hints.get(en.getKey());
			{
				Boolean serial = (Boolean) hintmap.get(Constants.Searchable);
				Integer idx = (Integer) hintmap.get(Constants.Index);
				if (serial != null && idx != null && serial.booleanValue() == true) {
					String actual = WordUtils.capitalize(en.getKey());
					String idxString = "tempvar+" + idx;
					code += NativeFormatter.getParseGetString(idxString, en.getValue(), en.getKey(), true);
					if (useRealName) {
						dynaprop += "," + en.getKey();
					} else {
						dynaprop += "," + "a" + count;
					}
					dynaques += ",?";
					count++;
				}
			}
		}
		String compiledCode = burnTransformerGetTemplate(name, dynaprop, dynaques, code);
		String actual = WordUtils.capitalize(name);
		String mtdStr = "public void saveToDb(org.ptg.events.Event " + " o);";
		// System.out.println(mtdStr);
		// System.out.println(compiledCode);
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(compiledCode);

		return cm;

	}

	public static CtMethod getTransformerUpdate(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, CtClass decl, boolean useRealName) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		String dynaques = "";
		String dynaprop = "";
		int count = 1;
		for (Map.Entry<String, String> en : def.entrySet()) {
			Map<String, Object> hintmap = hints.get(en.getKey());
			{
				Boolean serial = (Boolean) hintmap.get(Constants.Searchable);
				Integer idx = (Integer) hintmap.get(Constants.Index);
				if (serial != null && idx != null && serial.booleanValue() == true) {
					String actual = WordUtils.capitalize(en.getKey());
					String idxString = "tempvar+" + idx;
					code += NativeFormatter.getParseGetString(idxString, en.getValue(), en.getKey(), true);
					if (useRealName) {
						dynaprop += (" , " + en.getKey() + " = ?");
					} else {
						dynaprop += (" , " + "a" + count + " = ?");
					}
					dynaques += ",?";
					count++;
				}
			}
		}
		String compiledCode = burnTransformerUpdateTemplate(name, dynaprop, dynaques, code);
		String actual = WordUtils.capitalize(name);
		String mtdStr = "public void update(org.ptg.events.Event " + " o);";
		// System.out.println(mtdStr);
		// System.out.println(compiledCode);
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(compiledCode);
		return cm;
	}

	public static CtMethod getTransformerUpdateXref(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, CtClass decl, boolean useRealName) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		String dynaques = "";
		String dynaprop = "";
		int count = 1;
		for (Map.Entry<String, String> en : def.entrySet()) {
			Map<String, Object> hintmap = hints.get(en.getKey());
			{
				Boolean serial = (Boolean) hintmap.get(Constants.Searchable);
				Integer idx = (Integer) hintmap.get(Constants.Index);
				if (serial != null && idx != null && serial.booleanValue() == true) {
					String actual = WordUtils.capitalize(en.getKey());
					String idxString = "tempvar+" + idx;
					code += NativeFormatter.getParseGetString(idxString, en.getValue(), en.getKey(), true);
					if (useRealName) {
						dynaprop += (" , " + en.getKey() + " = ?");
					} else {
						dynaprop += (" , " + "a" + count + " = ?");
					}
					dynaques += ",?";
					count++;
				}
			}
		}
		String compiledCode = burnTransformerUpdateXrefTemplate(name, dynaprop, dynaques, code);
		String actual = WordUtils.capitalize(name);
		String mtdStr = "public void updateByXref(org.ptg.events.Event " + " o);";
		// System.out.println(mtdStr);
		// System.out.println(compiledCode);
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(compiledCode);
		return cm;
	}

	public static CtMethod getTransformerSetter(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, CtClass decl, boolean useRealName) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, String> en : def.entrySet()) {
			Map<String, Object> hintmap = hints.get(en.getKey());
			{
				Boolean serial = (Boolean) hintmap.get(Constants.Searchable);
				Integer idx = (Integer) hintmap.get(Constants.Index);
				if (serial != null && idx != null && serial.booleanValue() == true) {
					String actual = WordUtils.capitalize(en.getKey());
					code += (NativeFormatter.getParseSetString(en.getValue(), en.getKey(), idx, true) + ";\n");
				}

			}
		}
		String compiledCode = burnTransformerSetTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		String mtdStr = "public " + "org.ptg.events.Event" + " loadFromDb" + "(java.sql.ResultSet rs);";
		// System.out.println(mtdStr);
		// System.out.println(compiledCode);
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(compiledCode);
		return cm;
	}

	public static String burnTransformerGetTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "trans_get.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", dynaprop);
		map.put("dynaques", dynaques);
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnTransformerSetTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "trans_set.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", dynaprop);
		map.put("dynaques", dynaques);
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnTransformerDeleteTemplate() {
		String inFile = "deleteevent.vm";
		Map map = new HashMap();
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnTransformerDeleteByXrefTemplate() {
		String inFile = "deletebyxref.vm";
		Map map = new HashMap();
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnTransformerUpdateTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "trans_update.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", dynaprop);
		map.put("dynaques", dynaques);
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();
	}

	public static String burnTransformerUpdateXrefTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "trans_updateXref.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", dynaprop);
		map.put("dynaques", dynaques);
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();
	}

	public void printTable(LineNumberReader reader, String name, String cname) throws IOException {
		System.out.println("Code To Read>>>>>>>>>>>>");
		DBHelper instance = DBHelper.getInstance();
		List<String[]> definitions = instance.getColumnDefinitions("event", "event", name);
		String cv = "_" + StringUtils.substringAfterLast(cname, ".");
		System.out.println(cname + " " + cv + "= new " + cname + "();");
		{
			for (String[] cols : definitions) {
				String var = "_" + WordUtils.capitalize(cols[0]);
				String type = types.get(Integer.valueOf(cols[1]));
				System.out.print("\n\t" + dataTypeMap.get(type) + " ");
				System.out.print(var);
				System.out.print(" = rs." + fromDbMtdMap.get(type) + "(\"" + cols[0] + "\");");
				System.out.println();
				printSetter(reader, cname, cols[0], var, cv);
			}
		}
		System.out.println("\nEND Code To Read>>>>>>>>>>>>");
	}

	public void printObject(LineNumberReader reader, String name, String cname) throws IOException {
		System.out.println("Code To Write>>>>>>>>>>>>");
		DBHelper instance = DBHelper.getInstance();
		List<String[]> definitions = instance.getColumnDefinitions("event", "event", name);
		String columStr = "";
		String valueStr = "";
		String cv = "_" + StringUtils.substringAfterLast(cname, ".");
		for (String[] cols : definitions) {
			String var = "_" + WordUtils.capitalize(cols[0]);
			String type = types.get(Integer.valueOf(cols[1]));
			String dataType = dataTypeMap.get(type);
			if (cols.length > 1) {
				columStr += (cols[0] + ",");
				if (dataType.equals("String")) {
					valueStr += ("\"\\\'\"+" + var + "+\"\\\'\"" + ",");
				} else {
					valueStr += (var + ",");
				}
				printGetter(reader, cname, cols[0], dataType, var, cv);
			} else {
				columStr += (cols[0]);
				if (dataType.equals("String")) {
					valueStr += ("\"\\\'\"+" + var + "+\"\\\'\"" + ",");
				} else {
					valueStr += (var + ",");
				}
				printGetter(reader, cname, cols[0], dataType, var, cv);
			}

		}
		String toPrint = "\ninsert into + name(" + columStr + ") values (" + valueStr + ")";
		System.out.println(toPrint);
		System.out.println("\nEND Code To Write>>>>>>>>>>>>");
	}

	public void printSetter(LineNumberReader reader, String className, String name, String real, String cv) throws IOException {

		String[] alt = new String[16];
		alt[0] = "set" + WordUtils.capitalize(name);
		alt[1] = "set" + "_" + WordUtils.capitalize(name);
		alt[2] = "set" + name;
		alt[3] = "set" + "_" + name;
		alt[4] = "_set" + WordUtils.capitalize(name);
		alt[5] = "_set" + "_" + WordUtils.capitalize(name);
		alt[6] = "_set" + name;
		alt[7] = "_set" + "_" + name;

		alt[8] = "Set" + WordUtils.capitalize(name);
		alt[9] = "Set" + "_" + WordUtils.capitalize(name);
		alt[10] = "Set" + name;
		alt[11] = "Set" + "_" + name;
		alt[12] = "_Set" + WordUtils.capitalize(name);
		alt[13] = "_Set" + "_" + WordUtils.capitalize(name);
		alt[14] = "_Set" + name;
		alt[15] = "_Set" + "_" + name;

		String Mtd = null;
		for (String tempMtd : alt) {
			tempMtd = DynaObjectHelper.hasMethod(className, tempMtd, true);
			if (tempMtd != null) {
				Mtd = tempMtd;
				break;
			}
		}
		if (Mtd != null)
			System.out.print("\t" + cv + "." + Mtd + "(" + real + ");");
		else {
			System.out.println("\nPlease enter the method to set the value:");
			Mtd = reader.readLine();
			System.out.println("\t" + cv + "." + Mtd + "(" + real + ");");
		}

	}

	public void printGetter(LineNumberReader reader, String className, String name, String realType, String real, String cv) throws IOException {

		String[] alt = new String[16];
		alt[0] = "get" + WordUtils.capitalize(name);
		alt[1] = "get" + "_" + WordUtils.capitalize(name);
		alt[2] = "get" + name;
		alt[3] = "get" + "_" + name;
		alt[4] = "_get" + WordUtils.capitalize(name);
		alt[5] = "_get" + "_" + WordUtils.capitalize(name);
		alt[6] = "_get" + name;
		alt[7] = "_get" + "_" + name;

		alt[8] = "Get" + WordUtils.capitalize(name);
		alt[9] = "Get" + "_" + WordUtils.capitalize(name);
		alt[10] = "Get" + name;
		alt[11] = "Get" + "_" + name;
		alt[12] = "_Get" + WordUtils.capitalize(name);
		alt[13] = "_Get" + "_" + WordUtils.capitalize(name);
		alt[14] = "_Get" + name;
		alt[15] = "_Get" + "_" + name;

		String Mtd = null;
		for (String tempMtd : alt) {
			tempMtd = DynaObjectHelper.hasMethod(className, tempMtd, true);
			if (tempMtd != null) {
				Mtd = tempMtd;
				break;
			}
		}
		if (Mtd != null)
			System.out.println("\t" + realType + " " + real + " = " + cv + "." + Mtd + "();");
		else {
			System.out.println("\nPlease enter the method to set the value:");
			Mtd = reader.readLine();
			System.out.println("\t" + realType + " " + real + " = " + cv + "." + Mtd + "();");
		}

	}

	public static void buildTypes(String name) {
		Map<String, Object> m = DynaObjectHelper.getFieldValues("java.sql.Types");
		for (Map.Entry<String, Object> e : m.entrySet()) {
			types.put(e.getValue(), e.getKey());
		}
	}

	public static void buildFromMaps() {
		fromDbMtdMap.put("BIGINT", "getLong");
		fromDbMtdMap.put("CHAR", "getString");
		fromDbMtdMap.put("DATE", "getDate");
		fromDbMtdMap.put("DECIMAL", "getInt");
		fromDbMtdMap.put("DOUBLE", "getDouble");
		fromDbMtdMap.put("FLOAT", "getFloat");
		fromDbMtdMap.put("INTEGER", "getInt");
		fromDbMtdMap.put("LONG", "getLong");
		fromDbMtdMap.put("NUMERIC", "getInt");
		fromDbMtdMap.put("TINYINT", "getShort");
		fromDbMtdMap.put("REAL", "getDouble");
		fromDbMtdMap.put("SMALLINT", "getInt");
		fromDbMtdMap.put("TIMESTAMP", "getTimestamp");
		fromDbMtdMap.put("TIME", "getTime");
		fromDbMtdMap.put("VARCHAR", "getString");
		fromDbMtdMap.put("LONGVARCHAR", "getString");
	}

	public static void buildDbToJavaMap() {
		toDbMtdMap.put("int", "setInt");
		toDbMtdMap.put("char", "setString");
		toDbMtdMap.put("char[]", "setString");
		toDbMtdMap.put("string", "setString");
		toDbMtdMap.put("java.lang.String", "setString");
		toDbMtdMap.put("double", "setDouble");
		toDbMtdMap.put("float", "setFloat");
		toDbMtdMap.put("date", "setLong");
		toDbMtdMap.put("time", "setTime");
		toDbMtdMap.put("java.lang.Integer", "setInt");
		toDbMtdMap.put("java.lang.Character", "setString");
		toDbMtdMap.put("java.lang.Double", "setDouble");
		toDbMtdMap.put("java.lang.Float", "setFloat");
		toDbMtdMap.put("java.util.Date", "setDate");
		toDbMtdMap.put("java.lang.Long", "setLong");
		toDbMtdMap.put("boolean", "setInt");
		toDbMtdMap.put("java.lang.Boolean", "setInt");
		toDbMtdMap.put("java.lang.Long", "setLong");
		toDbMtdMap.put("Long", "setLong");
		toDbMtdMap.put("long", "setLong");
		
		
		
		
		
	}

	public static void buildJavaToDBMap() {
		DbToJavaMtdMap.put("int", "getInt");
		DbToJavaMtdMap.put("char", "getString");
		DbToJavaMtdMap.put("char[]", "getString");
		DbToJavaMtdMap.put("string", "getString");
		DbToJavaMtdMap.put("String", "getString");
		DbToJavaMtdMap.put("java.lang.String", "getString");
		DbToJavaMtdMap.put("double", "getDouble");
		DbToJavaMtdMap.put("float", "getFloat");
		DbToJavaMtdMap.put("date", "getLong");
		DbToJavaMtdMap.put("time", "getTime");
		DbToJavaMtdMap.put("java.lang.Integer", "getInt");
		DbToJavaMtdMap.put("java.lang.Character", "getString");
		DbToJavaMtdMap.put("java.lang.Double", "getDouble");
		DbToJavaMtdMap.put("java.lang.Float", "getFloat");
		DbToJavaMtdMap.put("java.util.Date", "getDate");
		DbToJavaMtdMap.put("java.lang.Long", "getLong");
		DbToJavaMtdMap.put("boolean", "getInt");
		DbToJavaMtdMap.put("java.lang.Boolean", "getInt");
		DbToJavaMtdMap.put("java.lang.Long", "getLong");
		DbToJavaMtdMap.put("long", "getLong");
			
		
		toDbMtdMap.put("BIT", "getBoolean");
		toDbMtdMap.put("TINYINT", "getInt");

		toDbMtdMap.put("SMALLINT","getInt");
		toDbMtdMap.put("INTEGER","getInt");
		toDbMtdMap.put("BIGINT","getInt");
		toDbMtdMap.put("FLOAT","getFloat");
		toDbMtdMap.put("REAL","getDouble");
		toDbMtdMap.put("DOUBLE","getDouble");
		toDbMtdMap.put("NUMERIC","getDouble");
		toDbMtdMap.put("DECIMAL","getDouble");
		toDbMtdMap.put("CHAR","getString");
		toDbMtdMap.put("VARCHAR","getString");
		toDbMtdMap.put("LONGVARCHAR","getString");
		toDbMtdMap.put("DATE","getDate");
		toDbMtdMap.put("TIME","getTime");
		toDbMtdMap.put("TIMESTAMP","getTimestamp");
		toDbMtdMap.put("BINARY","getBlob");
		toDbMtdMap.put("VARBINARY","getBlob");
		toDbMtdMap.put("LONGVARBINARY","getBlob");
		toDbMtdMap.put("BLOB","getBlob");
		toDbMtdMap.put("CBLOB","getClob");
		toDbMtdMap.put("NCBLOB","getClob");
		DbToJavaMtdMap.put("NCHAR", "getString");
		DbToJavaMtdMap.put("NVARCHAR", "getString");
		DbToJavaMtdMap.put("LONGNVARCHAR", "getString");
		
	}

	public String getJavaToDbMtd(String type) {
		return toDbMtdMap.get(type);
	}

	public String getDbToJavaMtd(String type) {
		return DbToJavaMtdMap.get(type);
	}

	public static void buildDataTypeMaps() {
		dataTypeMap.put("BIGINT", "long");
		dataTypeMap.put("CHAR", "String");
		dataTypeMap.put("DATE", "Date");
		dataTypeMap.put("DECIMAL", "int");
		dataTypeMap.put("DOUBLE", "double");
		dataTypeMap.put("FLOAT", "float");
		dataTypeMap.put("INTEGER", "int");
		dataTypeMap.put("INT", "int");
		dataTypeMap.put("LONG", "long");
		dataTypeMap.put("NUMERIC", "int");
		dataTypeMap.put("TINYINT", "short");
		dataTypeMap.put("REAL", "double");
		dataTypeMap.put("SMALLINT", "int");
		dataTypeMap.put("TIMESTAMP", "Date");
		dataTypeMap.put("TIME", "Date");
		dataTypeMap.put("VARCHAR", "String");
		dataTypeMap.put("LONGVARCHAR", "String");
	}

	private static void init() {
		buildTypes("java.sql.Types");
		buildFromMaps();
		buildDbToJavaMap();
		buildJavaToDBMap();
		buildDataTypeMaps();
	}
	public static CtMethod getTransformerGetterNative(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, CtClass decl, boolean useRealName) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		String dynaques = "";
		String dynaprop = "";
		int count = 1;
		for (Map.Entry<String, String> en : def.entrySet()) {
			Map<String, Object> hintmap = hints.get(en.getKey());
			{
				Boolean serial = (Boolean) hintmap.get(Constants.Searchable);
				Integer idx = (Integer) hintmap.get(Constants.Index);
				if (serial != null && idx != null && serial.booleanValue() == true) {
					String actual = WordUtils.capitalize(en.getKey());
					String idxString = "tempvar+" + idx;
					String type = def.get(en.getKey());
					String mtd = toDbMtdMap.get(type);
					System.out.println("Got "+mtd +" for type "+type);
					String fparam = "obj";
					String dest = en.getKey();
					if(type.equals("java.util.Date")){
						code +="stmt." + mtd + "("+ idx+",(java.sql.Date)new java.sql.Date("+ fparam + "." + "get"+actual+ "(" + ").getTime())" + ");\n";
					}else if(CommonUtil.isNative(type)){
						code +="stmt." + mtd + "("+ idx+","+ fparam + "." + "get"+actual+ "(" + ")" + ");\n";						
					}else{
						code +="stmt." + "setObject" + "("+ idx+","+ fparam + "." + "get"+actual+ "(" + ")" + ");\n";						
					}
					if(count>1){
					dynaprop += "," + en.getKey();
					dynaques += ",?";
					}else{
						dynaprop += en.getKey();
						dynaques += "?";
						
					}
					count++;
				}
			}
		}
		String compiledCode = burnTransformerGetNativeTemplate(name, dynaprop, dynaques, code);
		String actual = WordUtils.capitalize(name);
		String mtdStr = "public int saveToDbNative(org.ptg.events.Event " + " o);";
		//System.out.println(mtdStr);
		//System.out.println(compiledCode);
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(compiledCode);

		return cm;

	}
	public static CtMethod getTransformerSetterNative(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, CtClass decl, boolean useRealName) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		for (Map.Entry<String, String> en : def.entrySet()) {
			Map<String, Object> hintmap = hints.get(en.getKey());
			{
				Boolean serial = (Boolean) hintmap.get(Constants.Searchable);
				Integer idx = (Integer) hintmap.get(Constants.Index);
				if (serial != null && idx != null && serial.booleanValue() == true) {
					String actual = WordUtils.capitalize(en.getKey());
					String type = def.get(en.getKey());
					String mtd = DbToJavaMtdMap.get(type);
					System.out.println("For type: "+type +" got " +mtd);
					String fparam = "$1";
					String dest = en.getKey();
					if (CommonUtil.isNative(type)) {
						code +="obj.set" + actual + "(" + fparam + "." + mtd + "(" + "\"" + dest + "\"" + ")" + ");\n";
					} else {
						code +="obj.set" + actual + "(("+type+")($w)" + fparam + "." + mtd + "(" + "\"" + dest + "\"" + ")" + ");\n";

					}
				}

			}
		}
		String compiledCode = burnTransformerSetNativeTemplate(name, "", "", code);
		String actual = WordUtils.capitalize(name);
		String mtdStr = "public " + "org.ptg.events.Event" + " loadFromDbNative" + "(java.sql.ResultSet rs);";
		// System.out.println(mtdStr);
		// System.out.println(compiledCode);
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(compiledCode);
		return cm;
	}
	public static String burnTransformerGetNativeTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "trans_getNative.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", dynaprop);
		map.put("dynaques", dynaques);
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}

	public static String burnTransformerSetNativeTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "trans_setNative.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", dynaprop);
		map.put("dynaques", dynaques);
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();

	}
	public static CtMethod getTransformerUpdateNative(String name, Map<String, String> def, Map<String, Map<String, Object>> hints, CtClass decl, boolean useRealName) throws CannotCompileException {
		ClassPool pool = CommonUtil.getClassPool();
		String code = "";
		String dynaques = "";
		String dynaprop = "";
		int count = 1;
		for (Map.Entry<String, String> en : def.entrySet()) {
			Map<String, Object> hintmap = hints.get(en.getKey());
			{
				Boolean serial = (Boolean) hintmap.get(Constants.Searchable);
				Integer idx = (Integer) hintmap.get(Constants.Index);
				if (serial != null && idx != null && serial.booleanValue() == true) {
					String actual = WordUtils.capitalize(en.getKey());
					String type = def.get(en.getKey());
					String mtd = toDbMtdMap.get(type);
					String fparam = "obj";
					String dest = en.getKey();
					if(type.equals("java.util.Date")){
						code +="stmt." + mtd + "("+ idx+",(java.sql.Date)new java.sql.Date("+ fparam + "." + "get"+actual+ "(" + ").getTime())" + ");\n";
					}else if(CommonUtil.isNative(type)){
						code +="stmt." + mtd + "("+ idx+","+ fparam + "." + "get"+actual+ "(" + ")" + ");\n";						
					}else{
						code +="stmt." + "setObject" + "("+ idx+","+ fparam + "." + "get"+actual+ "(" + ")" + ");\n";						
					}
					if (count==1) {
						dynaprop += ("" + en.getKey() + " = ?");
						dynaques += "?";
					}else{
						dynaprop += ("," + en.getKey() + " = ?");
						dynaques += ",?";
					}
					
					count++;
				}
			}
		}
		String compiledCode = burnTransformerUpdateNativeTemplate(name, dynaprop, dynaques, code);
		String actual = WordUtils.capitalize(name);
		String mtdStr = "public void updateNative(String whereClause,org.ptg.events.Event " + " o);";
		 //System.out.println(mtdStr);
		//System.out.println(compiledCode);
		CtMethod cm = CtMethod.make(mtdStr, decl);
		cm.setBody(compiledCode);
		return cm;
	}
	public static String burnTransformerUpdateNativeTemplate(String type, String dynaprop, String dynaques, String codestr) {
		String inFile = "trans_updateNative.vm";
		Map map = new HashMap();
		map.put("objtype", type);
		map.put("dynaprop", dynaprop);
		map.put("dynaques", dynaques);
		map.put("codestr", codestr);
		StringBuffer s = VelocityHelper.burnTemplate(map, inFile);
		return s.toString();
	}
}
