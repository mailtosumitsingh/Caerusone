/*
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.mozilla.javascript.NativeJavaObject;
import org.ptg.admin.WebManager;
import org.ptg.admin.WebStartProcess;
import org.ptg.cluster.AppContext;
import org.ptg.common.CaerusCLIConstants;
import org.ptg.events.Event;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.events.PropertyDefinition;
import org.ptg.events.QueryEvent;
import org.ptg.events.TraceEvent;
import org.ptg.http2.handlers.compilers.graph.CompileTaskPlanV2;
import org.ptg.models.Connector;
import org.ptg.models.Facet;
import org.ptg.models.Point;
import org.ptg.models.Shape;
import org.ptg.plugins.IPluginManager;
import org.ptg.processors.ConnDef;
import org.ptg.processors.ProcessorDef;
import org.ptg.processors.ProcessorManager;
import org.ptg.processors.ProxyProcessor;
import org.ptg.script.ScriptEngine;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamDefinition;
import org.ptg.stream.StreamManager;
import org.ptg.util.awt.BBox;
import org.ptg.util.closures.IStateAwareClosure;
import org.ptg.util.closures.MethodClosure;
import org.ptg.util.db.ColDef;
import org.ptg.util.db.DBHelper;
import org.ptg.util.db.SQLObj;
import org.ptg.util.db.TableDef;
import org.ptg.util.events.DeployEvent;
import org.ptg.util.expr.IInputExprProcessor;
import org.ptg.util.expr.IOutputExprProcessor;
import org.ptg.util.expr.jawa.JavaExprInputProcessor;
import org.ptg.util.expr.jawa.JavaExprOutputProcessor;
import org.ptg.util.functions.Expression.ExpressionType;
import org.ptg.util.graph.PNode;
import org.ptg.util.graph.WaitStruct;
import org.ptg.util.index.ILuceneEventDocWriter;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.CompilePath;
import org.ptg.util.mapper.FPGraph;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.LayerObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.SimpleMapperCompiler;
import org.ptg.util.mapper.SimpleTodoCompiler;
import org.ptg.util.mapper.StepObj;
import org.ptg.util.mapper.StepObj.StepType;
import org.ptg.util.mapper.TypeDefObj;
import org.ptg.util.mapper.v2.FPGraph2;
import org.ptg.util.regioncomp.CastRegionCompiler;
import org.ptg.util.regioncomp.ExeceptionRegionCompiler;
import org.ptg.util.regioncomp.IfRegionCompiler;
import org.ptg.util.regioncomp.NotBeforeCompiler;
import org.ptg.util.regioncomp.WhileRegionCompiler;
import org.ptg.util.thread.ThreadManager;
import org.ptg.util.titan.ITitanClass;
import org.ptg.util.titan.TitanCompiler;
import org.ptg.velocity.VelocityHelper;
import org.springframework.context.support.GenericApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistry;
import com.sun.tools.xjc.XJC2Task;

import Zql.ZQuery;
import Zql.ZSelectItem;
import Zql.ZqlParser;
import au.com.bytecode.opencsv.CSVReader;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.StreamGobbler;
import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.annotation.Annotation;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.TextExtractor;
import net.sf.ezmorph.Morpher;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

public class CommonUtil {
	static SystemLogger s;
	static ExtensionRegistry extensionRegistry;
	static Map<String, String> monthMap = new HashMap<String, String>();
	static JexlEngine jexl = new JexlEngine();
	static Map<String, Expression> expressions = new HashMap<String, Expression>();
	public static String base = (String) SpringHelper.get("basedir");
	public static AppContext ctx = (AppContext) SpringHelper.get("appContext");
	static JavaRuleManager javaRuleManager = (JavaRuleManager) SpringHelper.get("javaRuleManager");
	static String buildDir = null;
	static boolean windows = SystemUtils.IS_OS_WINDOWS;
	static Map<String, GraphObjectCompiler> graphObjectCompilers = new HashMap<String, GraphObjectCompiler>();
	static Map<String, String> dataTypeTempl = new LinkedHashMap<String, String>();
	static final GenericApplicationContext dynaSpringCtx = new GenericApplicationContext(SpringHelper.getInstance().getXmlBeanFactory());

	static Map<String, IInputExprProcessor> inputExprProcessor = new LinkedHashMap<String, IInputExprProcessor>();
	static Map<String, IOutputExprProcessor> outputExprProcessor = new LinkedHashMap<String, IOutputExprProcessor>();
	static {
		outputExprProcessor.put("java", new JavaExprOutputProcessor());
		inputExprProcessor.put("java", new JavaExprInputProcessor());
	}
	static {

		monthMap.put("Jan", "01");
		monthMap.put("Feb", "02");
		monthMap.put("Mar", "03");
		monthMap.put("Apr", "04");
		monthMap.put("May", "05");
		monthMap.put("Jun", "06");
		monthMap.put("Jul", "07");
		monthMap.put("Aug", "08");
		monthMap.put("Sep", "09");
		monthMap.put("Oct", "10");
		monthMap.put("Nov", "11");
		monthMap.put("Dec", "12");
		monthMap.put("jan", "01");
		monthMap.put("feb", "02");
		monthMap.put("mar", "03");
		monthMap.put("apr", "04");
		monthMap.put("may", "05");
		monthMap.put("jun", "06");
		monthMap.put("jul", "07");
		monthMap.put("aug", "08");
		monthMap.put("sep", "09");
		monthMap.put("oct", "10");
		monthMap.put("nov", "11");
		monthMap.put("dec", "12");
		dataTypeTempl.put("java.lang.Runnable", "java.lang.Runnable ${name} = new java.lang.Runnable(){\n" + "public void run(){\n" + "${code}" + "}\n" + "\n};\n");
		dataTypeTempl.put("java.lang.String", "java.lang.String ${name} = ${code};");
		dataTypeTempl.put("org.apache.commons.collections.Closure", "org.apache.commons.collections.Closure ${name} = new org.apache.commons.collections.Closure(){\n"
				+ "public void execute(Object obj){\n" + "${code}" + "}\n" + "\n};\n");

		dataTypeTempl.put("org.ptg.util.Code", "/*{*/\n" + "${code}" + "\n/*}*/\n" + " org.ptg.util.Code ${name} = new org.ptg.util.Code(); \n");

	}
	static {
		AppContext ctx = (AppContext) SpringHelper.get("appContext");
		buildDir = ctx.getBuildDir();

	}
	static {
		graphObjectCompilers.put("excp", new ExeceptionRegionCompiler());
		graphObjectCompilers.put("cast", new CastRegionCompiler());
		graphObjectCompilers.put("while", new WhileRegionCompiler());
		graphObjectCompilers.put("if", new IfRegionCompiler());
		graphObjectCompilers.put("timed", new NotBeforeCompiler());

	}
	private static Map<Integer, String> strNum = new HashMap<Integer, String>();
	static {
		extensionRegistry = ExtensionRegistry.newInstance();
		strNum.put(0, "zero");
		strNum.put(1, "one");
		strNum.put(2, "two");
		strNum.put(3, "three");
		strNum.put(4, "four");
		strNum.put(5, "five");
		strNum.put(6, "six");
		strNum.put(7, "seven");
		strNum.put(8, "eight");
		strNum.put(9, "nine");
		strNum.put(10, "ten");
		strNum.put(11, "eleven");
		strNum.put(12, "twelve");
	}
	static {
		s = (SystemLogger) SpringHelper.getInstance().getBean("systemLogger");
		System.setProperty("org.codehaus.janino.source_debugging.enable", "true");
	}

	public static String getDateString() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");
		return format.format(date);
	}

	public static void loadProperties(String from, Map to) {
		Properties prop = new Properties();
		try {
			if (new File(from).exists()) {
				prop.load(new FileInputStream(from));
			}
			for (Map.Entry<Object, Object> e : prop.entrySet()) {
				to.put(e.getKey().toString(), e.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void dump(Object o) {
		if (o == null) {
			// System.out.println("[ Object is null ]" + " : " + o);
			return;
		}
		Field[] flds = o.getClass().getDeclaredFields();
		for (Field f : flds) {
			if (!f.getDeclaringClass().equals(o.getClass())) {
				continue;
			}
			f.setAccessible(true);
			Object obj = null;
			try {
				obj = f.get(o);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (obj != null) {
				if (obj instanceof Map) {
					dumpMap((Map) obj);
				} else if (obj instanceof Collection) {
					dumpCollection((Collection) obj);
				} else {
					// System.out.println("[" + f.getName() + "]" + " : " +
					// obj);
					// System.out.println("\tAnnotations:");
					Annotation an;
					try {
						an = DynaObjectHelper.getAnnotation(o.getClass().getCanonicalName(), f.getName(), "Property");
						// System.out.println(an);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	}

	public static void dumpMap(Map o) {
		if (o == null) {
			return;
		}
		// System.out.println("Dumping map..");
		for (Object e : o.keySet()) {
			// System.out.println("Key: " + e + ", Value:" + o.get(e));
			// dump(o.get(e));
		}
	}

	public static void dumpCollection(Collection o) {
		if (o == null) {
			return;
		}

		// System.out.println("Dumping collection..");
		for (Object e : o) {
			// System.out.println("Value:" + e);
		}
	}

	public static String getRandomString(int len) {
		return RandomStringUtils.randomAlphanumeric(len);
	}

	public static int getRandomInt(int around) {
		return RandomUtils.nextInt(around);
	}

	public static double getRandomDuble() {
		return RandomUtils.nextDouble();
	}

	public static String getClassNameFromFileName(String str) {
		if (str == null || str.length() < 1) {
			return str;
		}
		str = str.substring(0, str.indexOf(".class"));
		str = str.replace('/', File.separatorChar);
		str = str.replace('\\', File.separatorChar);
		str = str.replace(File.separator, ".");
		return str;
	}

	public static String[] getClassesFromDir(String dir) {
		org.apache.tools.ant.Project p = new org.apache.tools.ant.Project();
		p.setBasedir(dir);
		FileSet fs = new FileSet();
		fs.setIncludes("**/*.class");
		fs.setDir(new File(dir));
		fs.setProject(p);
		String[] files = fs.getDirectoryScanner().getIncludedFiles();
		for (int i = 0; i < files.length; i++) {
			String temp = files[i];
			temp = getClassNameFromFileName(temp);
			files[i] = temp;
		}
		return files;
	}

	public static String[] getClassesFromJar(String name) {
		ArrayList<String> l = new ArrayList<String>();
		try {
			JarFile f = new JarFile(name);
			Enumeration e = f.entries();
			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry) e.nextElement();
				if (!je.isDirectory()) {
					String temp = je.getName();
					if (!temp.contains(".class")) {
						continue;
					} else {
						temp = temp.substring(0, temp.indexOf(".class"));
						temp = temp.replace("/", ".");
						temp = temp.replace("\\", ".");
						temp = temp.replace("\\\\", ".");
						temp = temp.replace("//", ".");
						l.add(temp);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l.toArray(new String[0]);
	}

	public static String conditionFileName(String file) {
		String temp = file.replace("\\\\", "\\");
		temp = temp.replace('\\', '/');
		return temp;
	}

	public static String conditionWindowsClassName(String file) {
		String temp = file.replace(".", "\\");
		temp = temp.replace('/', '\\');
		temp = StringUtils.substringBefore(temp, "$");
		return temp + ".java";
	}

	public static int getInt(String s) {
		int ret = Integer.MIN_VALUE;
		try {
			ret = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static long getLong(String s) {
		long ret = Long.MIN_VALUE;
		try {
			ret = Long.parseLong(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void forEachMultiMapItem(Object obj, String forEachMapItem, MultiMap m) {
		for (Object k : m.keySet()) {
			Collection c = (Collection) m.get(k);
			ReflectionUtils.invoke(obj, forEachMapItem, new Object[] { k, c });
		}
	}

	public static void forEachMultiMapCollItem(Object obj, String forEachMapColItem, MultiMap m) {
		for (Object k : m.keySet()) {
			Collection c = (Collection) m.get(k);
			for (Object o : c) {
				ReflectionUtils.invoke(obj, forEachMapColItem, new Object[] { k, o });
			}
		}
	}

	public static void forEachMapItem(Object obj, String forEachMapItem, Method forEachMapColItem, Map m) {
		for (Object k : m.keySet()) {
			Object o = m.get(k);
			ReflectionUtils.invoke(obj, forEachMapItem, new Object[] { k, o });
		}
	}

	public static void forEachCollectionItemInvoke(Collection c, String forEachMapItem, Collection m) {
		if (c != null) {
			for (Object obj : c) {
				for (Object o : m) {
					ReflectionUtils.invoke(obj, forEachMapItem, new Object[] { o });
				}
			}
		}
	}

	public static void forEachCollectionItemInvoke(Collection c, String forEachMapItem, Object o) {
		if (c != null) {
			for (Object obj : c) {
				ReflectionUtils.invoke(obj, forEachMapItem, new Object[] { o });
			}
		}
	}

	public static void forEachMapItem(Closure c, Map m) {
		for (Object o : m.entrySet()) {
			c.execute(o);
		}
	}

	public static void forEachCollectionItem(Closure c, Collection m) {
		for (Object o : m) {
			c.execute(o);
		}
	}

	public static void forEachCollectionItem(Object obj, String forEachMapItem, Collection m) {
		for (Object o : m) {
			ReflectionUtils.invoke(obj, forEachMapItem, new Object[] { o });
		}
	}

	public static void safeSleep(long mSec) {
		try {
			Thread.sleep(mSec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void debugGC() {
		System.gc();
		CommonUtil.safeSleep(1000);
		System.gc();
	}

	public static String escapeXml(String str) {
		return StringEscapeUtils.escapeXml(str);
	}

	public static String escapeJavaScript(String str) {
		return StringEscapeUtils.escapeJavaScript(str);
	}

	public static String escapeHTML(String str) {
		return StringEscapeUtils.escapeHtml(str);
	}

	public static String getStackTrace(Throwable th) {
		return ExceptionUtils.getFullStackTrace(th);
	}

	public static Date getWithCurrentDate(Date time) throws ParseException {
		Calendar orig = Calendar.getInstance();
		orig.setTime(time);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, orig.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, orig.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, orig.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Calendar now() {
		return Calendar.getInstance();
	}

	public static String[] validExpr(String expr) {
		String[] ret = null;
		if ((ret = matchRange(expr)) != null) {// range
		}
		return ret;
	}

	public static String[] matchRange(String str) {
		Pattern p = Pattern.compile("([1-9]{1}[0-2]{0,1})|(([1-9]{0,1}[0-2]{0,1}){1}[\\-]{1}([1-9]{0,1}[0-2]{0,1})){1}");
		Matcher m = p.matcher(str);
		boolean res = m.matches();
		if (res) {
			String[] rets = new String[m.groupCount() + 1];
			for (int i = 0; i <= m.groupCount(); i++) {
				rets[i] = m.group(i);
			}
			return rets;
		} else {
			return null;
		}

	}

	public static boolean onOrBefore(Date left, Date right) {
		return left.getTime() - right.getTime() <= 0;
	}

	public static boolean onOrAfter(Date left, Date right) {
		return left.getTime() - right.getTime() >= 0;
	}

	public static String readTrim(ResultSet rs, String column) {
		String ret = null;
		try {
			ret = rs.getString(column);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return ret == null ? ret : ret.trim();
	}

	public static boolean isLastDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		int currDate = calendar.get(Calendar.DAY_OF_MONTH);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		return currDate == lastDate;
	}

	public static int[] getMinuteAndSecondOfDay(Date dt) {
		Calendar time = Calendar.getInstance();
		if (dt != null) {
			time.setTime(dt);
		}
		int[] data = new int[2];
		int min = time.get(Calendar.MINUTE);
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int sec = time.get(Calendar.SECOND);
		data[0] = hour * 60 + min;
		data[1] = sec;
		return data;
	}

	public static boolean isBoolean(String s) {
		if (s.equalsIgnoreCase("boolean") || s.equalsIgnoreCase("java.lang.Boolean")) {
			return true;
		}

		return false;
	}

	public static boolean isDate(String s) {
		if (s.equalsIgnoreCase("Date") || s.equalsIgnoreCase("java.util.Date")) {
			return true;
		}

		return false;
	}

	public static boolean isDouble(String s) {
		if (s.equalsIgnoreCase("double") || s.equalsIgnoreCase("java.lang.Double")) {
			return true;
		}

		return false;
	}

	public static boolean isLong(String s) {
		if (s.equalsIgnoreCase("long") || s.equalsIgnoreCase("java.lang.Long")) {
			return true;
		}

		return false;
	}

	public static boolean isFloat(String s) {
		if (s.equalsIgnoreCase("float") || s.equalsIgnoreCase("java.lang.Float")) {
			return true;
		}

		return false;
	}

	public static boolean isString(String s) {
		if (s.equalsIgnoreCase("String") || s.equalsIgnoreCase("java.lang.String")) {
			return true;
		}

		return false;
	}

	public static boolean isInt(String s) {
		if (s.equalsIgnoreCase("int") || s.equalsIgnoreCase("java.lang.Integer")) {
			return true;
		}

		return false;
	}

	public static boolean isShort(String s) {
		if (s.equalsIgnoreCase("short") || s.equalsIgnoreCase("java.lang.Short")) {
			return true;
		}

		return false;
	}

	public static boolean isNativeBoolean(String s) {
		if (s.equalsIgnoreCase("boolean")) {
			return true;
		}

		return false;
	}

	public static boolean isNativeDouble(String s) {
		if (s.equalsIgnoreCase("double")) {
			return true;
		}

		return false;
	}

	public static boolean isNativeLong(String s) {
		if (s.equalsIgnoreCase("long")) {
			return true;
		}

		return false;
	}

	public static boolean isNativeFloat(String s) {
		if (s.equalsIgnoreCase("float")) {
			return true;
		}

		return false;
	}

	public static boolean isNativeInt(String s) {
		if (s.equalsIgnoreCase("int")) {
			return true;
		}

		return false;
	}

	public static Object getXpathValue(String s, String xpath) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath1 = factory.newXPath();

		XPathExpression expr;
		try {
			expr = xpath1.compile(xpath);
			DocumentBuilderFactory docf = DocumentBuilderFactory.newInstance();
			docf.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = docf.newDocumentBuilder();
			Document doc = builder.parse(s);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				// System.out.println(nodes.item(i).getNodeValue());
			}
			return nodes.item(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isPrimitive(String type) {
		if (isNative(type) || type.equals("java.util.Calendar") || type.equals("java.util.Date") || type.equals("java.sql.Date")) {
			return true;
		}
		return false;

	}

	public static String getNonPrimitiveDestType(String s) {
		if (CommonUtil.isNativeBoolean(s)) {
			return "java.lang.Boolean";

		} else if (CommonUtil.isNativeInt(s)) {
			return "java.lang.Integer";

		} else if (CommonUtil.isNativeLong(s)) {
			return "java.lang.Long";

		} else if (CommonUtil.isNativeDouble(s)) {
			return "java.lang.Double";

		} else if (CommonUtil.isNativeFloat(s)) {
			return "java.lang.Float";

		} else {
			return s;
		}
	}

	public static boolean isNative(String s) {
		return isNativeBoolean(s) || isNativeDouble(s) || isNativeFloat(s) || isNativeFloat(s) || isNativeInt(s) || isNativeLong(s);
	}

	public static String getScriptPath(String filename) {
		return Constants.Resources + File.separator + Constants.ScriptPath + File.separator + filename;
	}

	public static String getResourcePath(String filename) {
		return Constants.Resources + File.separator + filename;
	}

	public static String getUUID() {
		return RandomStringUtils.randomAlphabetic(128) + "_" + RandomStringUtils.randomAlphabetic(64);

	}

	public static String toJson(Object obj) {
		net.sf.json.JSONObject o = net.sf.json.JSONObject.fromObject(obj);
		return o.toString();
	}
	public static String toJsonObjectMapper(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	public static String jsonFromCollection(Collection obj) {
		JSONArray o = net.sf.json.JSONArray.fromObject(obj.toArray());
		return o.toString();
	}

	public static String jsonFromArray(Object[] obj) {
		JSONArray o = net.sf.json.JSONArray.fromObject(obj);
		return o.toString();
	}

	public static String jsonFromArray(Object obj) {
		JSONArray o = net.sf.json.JSONArray.fromObject(obj);
		return o.toString();
	}

	public static String getNullString(String s) {
		return s == null ? "" : s;
	}

	public static int getBooleanInt(boolean s) {
		return s == true ? 1 : 0;
	}

	public static int getBooleanInt(java.lang.Boolean s) {
		return s == true ? 1 : 0;
	}

	public static byte[] write(Object o) {
		if (o instanceof com.google.protobuf.GeneratedMessage) {
			return writeSerializedGPB((com.google.protobuf.GeneratedMessage) o);
		} else if (o instanceof Externalizable) {
			return writeSerialized((Externalizable) o);
		} else {
			// System.out.println("Unknown type of object to serialize from");
			return null;
		}
	}

	public static boolean isGoogledSerialized(byte[] s) {
		boolean googled = false;
		ByteArrayInputStream b = new ByteArrayInputStream(s);
		try {
			CodedInputStream str = CodedInputStream.newInstance(b);
			String className = str.readString();
			googled = str.readBool();
			b.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return googled;
	}

	public static boolean isJavaSerialized(byte[] s) {
		boolean javaSerialized = false;
		ByteArrayInputStream b = new ByteArrayInputStream(s);
		try {
			ObjectInputStream str = new ObjectInputStream(b);
			String className = str.readUTF();
			javaSerialized = str.readBoolean();
			str.close();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return javaSerialized;
	}

	public static Object read(byte[] s) {
		Object obj = null;
		ByteArrayInputStream b = new ByteArrayInputStream(s);
		try {
			CodedInputStream str = CodedInputStream.newInstance(b);
			String className = str.readString();
			boolean googled = str.readBool();
			if (googled) {
				// //System.out.println("Got a googled object");
				obj = ReflectionUtils.invokeStatic(className, "parseFrom", new Object[] { str });
			} else {
				// reconstruct bytearray coded input stream screws everything.
				b = new ByteArrayInputStream(s);
				// //System.out.println("Skipping : " +
				// str.getTotalBytesRead());
				b.skip(str.getTotalBytesRead());
				// //System.out.println("Got a normal object");
				ObjectInputStream str2 = new ObjectInputStream(b);
				obj = ReflectionUtils.createInstance(className);
				((Externalizable) obj).readExternal(str2);

			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return obj;
	}

	public static byte[] writeSerialized(Externalizable s) {
		if (s == null) {
			return null;
		}
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			CodedOutputStream str2 = CodedOutputStream.newInstance(b);
			str2.writeStringNoTag(s.getClass().getName());
			str2.writeBoolNoTag(false);// it is not google serialized
			str2.flush();

			ObjectOutputStream str = new ObjectOutputStream(b);
			s.writeExternal(str);
			str.flush();
			b.flush();
			bytes = b.toByteArray();
			str.close();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// //System.out.println("Written : " + bytes.length + " bytes.");
		org.ptg.admin.AppContext.getInstance().addStat(CaerusCLIConstants.BytesOut, bytes.length);
		return bytes;
	}

	public static Externalizable readSerialized(byte[] s) {
		// //System.out.println("Read : " + s.length + " bytes.");
		org.ptg.admin.AppContext.getInstance().addStat(CaerusCLIConstants.BytesIn, s.length);
		Externalizable ex = null;
		if (s == null) {
			return null;
		}
		ByteArrayInputStream b = new ByteArrayInputStream(s);
		try {
			CodedInputStream str2 = CodedInputStream.newInstance(b);
			String className = str2.readString();
			boolean googled = str2.readBool();
			b = new ByteArrayInputStream(s);
			b.skip(str2.getTotalBytesRead());
			ObjectInputStream str = new ObjectInputStream(b);
			ex = (Externalizable) ReflectionUtils.createInstance(className);
			ex.readExternal(str);
			str.close();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ex;
	}

	public static byte[] writeSerializedGPB(com.google.protobuf.GeneratedMessage s) {
		if (s == null) {
			return null;
		}
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			CodedOutputStream str = CodedOutputStream.newInstance(b);
			str.writeStringNoTag(s.getClass().getName());// .replace('$', '.'));
			str.writeBoolNoTag(true);// it is indeed google serialized
			s.writeTo(str);
			str.flush();
			b.flush();
			bytes = b.toByteArray();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Written : " + bytes.length + " bytes.");
		org.ptg.admin.AppContext.getInstance().addStat(CaerusCLIConstants.BytesOut, bytes.length);
		return bytes;
	}

	public static com.google.protobuf.GeneratedMessage readSerializedGPB(byte[] s) {
		// System.out.println("Read : " + s.length + " bytes.");
		org.ptg.admin.AppContext.getInstance().addStat(CaerusCLIConstants.BytesIn, s.length);
		com.google.protobuf.GeneratedMessage ex = null;
		if (s == null) {
			return null;
		}
		ByteArrayInputStream b = new ByteArrayInputStream(s);
		try {
			CodedInputStream str = CodedInputStream.newInstance(b);
			String className = str.readString();
			boolean googled = str.readBool();
			ex = (com.google.protobuf.GeneratedMessage) ReflectionUtils.invokeStatic(className, "parseFrom", new Object[] { str });
			b.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return ex;
	}

	public static StreamDefinition getStreamPropertyDefinition(String destProp, String type, int index) {
		StreamDefinition def = new StreamDefinition();
		def.setAccessor(Constants.Property);
		def.setDestProp(destProp);
		def.setExtra(destProp);
		def.setName(destProp);
		def.setType(type);
		def.setXmlExpr(null);
		return def;
	}

	public static PropertyDefinition getPropertyDefinition(String name, String type, int index, int searchable) {
		PropertyDefinition p1 = new PropertyDefinition();
		p1.setName(name);
		p1.setType(type);
		p1.setIndex(index);
		p1.setSearchable(searchable);
		return p1;
	}

	public static byte[] writeRawSerializedGPB(com.google.protobuf.GeneratedMessage s) {
		if (s == null) {
			return null;
		}
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			CodedOutputStream str = CodedOutputStream.newInstance(b);
			s.writeTo(str);
			str.flush();
			b.flush();
			bytes = b.toByteArray();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Written : " + bytes.length + " bytes.");
		org.ptg.admin.AppContext.getInstance().addStat(CaerusCLIConstants.BytesOut, bytes.length);
		return bytes;
	}

	public static com.google.protobuf.GeneratedMessage readRawSerializedGPB(String className, byte[] s) {
		// System.out.println("Read : " + s.length + " bytes.");
		org.ptg.admin.AppContext.getInstance().addStat(CaerusCLIConstants.BytesIn, s.length);
		com.google.protobuf.GeneratedMessage ex = null;
		if (s == null) {
			return null;
		}
		ByteArrayInputStream b = new ByteArrayInputStream(s);
		try {
			CodedInputStream str = CodedInputStream.newInstance(b);
			ex = (com.google.protobuf.GeneratedMessage) ReflectionUtils.invokeStatic(className, "parseFrom", new Object[] { str });
			b.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return ex;
	}

	public static boolean checkRecursive(String systemid, QueryEvent qe, int omitLast) {
		if (qe != null) {
			String eles[] = qe.getPathEle();
			if (eles != null) {
				for (int i = 0; i < eles.length - omitLast; i++) {
					String s = eles[i];
					if (s.equals(systemid)) {
						// System.out.println("Recursive execution found for " +
						// qe.getPath());
						return true;
					}
				}
			}
		}
		return false;
	}

	public static EventDefinition buildEventDefinition(String name, String type, String store) {
		EventDefinition e = new EventDefinition();
		List<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
		e.setType(type);
		e.setEventStore(store);
		Map<String, String> mp = DynaObjectHelper.externClass(name, true);
		Set<Map.Entry<String, String>> val = mp.entrySet();
		int i = 1;
		for (Map.Entry<String, String> en : val) {
			PropertyDefinition p = getPropertyDefinition(en.getKey(), en.getValue(), i, 1);
			props.add(p);
			i = i + 1;
		}

		for (PropertyDefinition p : props) {
			e.getProps().put("" + p.getIndex(), p);
		}
		return e;
	}

	public static Stream buildStreamDefinition(String name, String type, String store) {
		Stream e = new Stream();
		e.setName(name);
		e.setEventType(type);
		List<StreamDefinition> props = new ArrayList<StreamDefinition>();
		Map<String, String> mp = DynaObjectHelper.externClass(type, true);
		Set<Map.Entry<String, String>> val = mp.entrySet();
		int i = 1;
		for (Map.Entry<String, String> en : val) {
			StreamDefinition p = getStreamPropertyDefinition(en.getKey(), en.getValue(), i);
			props.add(p);
			i = i + 1;
		}

		for (StreamDefinition p : props) {
			e.getDefs().put(p.getName(), p);
		}
		return e;
	}

	public static EventDefinition saveAndRegisterEventTransformer(String name, String type, String store) {
		EventDefinition e = buildEventDefinition(name, type, store);
		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
		EventDefinitionManager.getInstance().buildDBTransformerDefinition(e, false);
		return e;
	}

	public static QueryEvent getQueryFromJsonData(byte[] data) {
		String s = new String(data);
		return getQueryFromJsonData(s);
	}

	public static QueryEvent getQueryFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		QueryEvent bean = (QueryEvent) JSONObject.toBean(jsonObject, QueryEvent.class);
		return bean;
	}

	public static Event getEventFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		String type = jsonObject.getString("eventType");
		Object obj = ReflectionUtils.createInstance(type);
		Event bean = (Event) JSONObject.toBean(jsonObject, obj.getClass());

		return bean;
	}

	public static Event getEventFromJsonObject(JSONObject jsonObject) {
		String type = jsonObject.getString("eventType");
		Object obj = ReflectionUtils.createInstance(type);
		Event bean = (Event) JSONObject.toBean(jsonObject, obj.getClass());

		return bean;
	}

	public static Event getEventFromJsonData(byte[] data) {
		String s = new String(data);
		return getEventFromJsonData(s);
	}

	public static EventDefinition getEventDefinitionFromJsonData(byte[] data) {
		String s = new String(data);
		return getEventDefinitionFromJsonData(s);
	}

	public static EventDefinition getEventDefinitionFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		return getEventDefinitionFromJsonObject(jsonObject);
	}

	public static EventDefinition getEventDefinitionFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		Morpher dynaMorpher = new BeanMorpher(PropertyDefinition.class, JSONUtils.getMorpherRegistry());
		JSONUtils.getMorpherRegistry().registerMorpher(dynaMorpher);
		EventDefinition bean = (EventDefinition) JSONObject.toBean(jsonObject, EventDefinition.class, classMap);
		String nameid = jsonObject.getString("id");
		if (nameid != null) {
			bean.setType(nameid);
		}
		Object jsonObject2 = jsonObject.get("props");
		Map<String, PropertyDefinition> output = new HashMap<String, PropertyDefinition>();
		for (Iterator i = bean.getProps().values().iterator(); i.hasNext();) {
			Object obj = i.next();
			PropertyDefinition prop = (PropertyDefinition) JSONUtils.getMorpherRegistry().morph(PropertyDefinition.class, obj);
			output.put("" + prop.getIndex(), prop);
		}
		bean.setProps(output);
		if (bean.getEventStore() == null) {
			bean.setEventStore("current_events");
		}
		return bean;
	}

	public static boolean saveConnection(ConnDef cdef) {
		/*
		 * using from to create a processor then create a streamdef save both
		 */
		try {
			ProcessorDef def = new ProcessorDef();
			String pname = "CondProc" + cdef.getId();
			final Stream s = new Stream();
			String cls = cdef.getCtype();
			if (cls == null || cls.trim().length() == 0) {
				cls = "org.ptg.processors.connection.ConditionalConnection";
			}
			def.setClz(cls);
			def.setName(pname);
			def.setQuery(cdef.getConnCond());
			ProcessorManager.getInstance().deleteProcessorDef(def);
			ProcessorManager.getInstance().saveProcessorDef(def);
			ProcessorManager.getInstance().registerProcessorType(def);
			Stream start = StreamManager.getInstance().getStream(cdef.getFrom());
			s.setEventType(start.getEventType());
			s.setExtra(cdef.getFrom() + ">" + cdef.getTo());
			s.setName("CondStream" + cdef.getId());
			s.setProcessor(pname);
			s.setSeda(0);
			StreamManager.getInstance().deleteStream(s);
			StreamManager.getInstance().saveStream(s);
			final IProcessor iprocessor = ProcessorManager.getInstance().attach(s.getName(), s.getProcessor());
			final String connstr = s.getExtra();
			final String directfrm = "direct:" + s.getName();
			final String frmOut = "direct:" + s.getName() + "-out";

			RouteBuilder streamRouteBuilder = new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					if (s != null) {
						String name = "";

						if (iprocessor instanceof ProxyProcessor) {
							name = ((ProxyProcessor) iprocessor).getInnerName();
						} else {
							name = iprocessor.getName();
						}
						from(directfrm).process(iprocessor).to(frmOut).setId(name);
					}
				}
			};
			WebStartProcess.getInstance().getRoutingEngine().addRoutes(streamRouteBuilder);
			RouteBuilder processorRouteBuilder = new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					if (s != null) {
						String[] conns = StringUtils.split(connstr, ">");
						from("direct:" + conns[0] + "-out").to(directfrm);
						from(frmOut).to("direct:" + conns[1]);
					}
				}
			};
			WebStartProcess.getInstance().getRoutingEngine().addRoutes(processorRouteBuilder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	public static ConnDef getConnDefinitionFromJsonData(byte[] data) {
		String s = new String(data);
		return getConnDefinitionFromJsonData(s);
	}

	public static ProcessorDef getProcDefinitionFromJsonData(byte[] data) {
		String s = new String(data);
		return getProcDefinitionFromJsonData(s);
	}

	public static Stream getStreamDefinitionFromJsonData(byte[] data) {
		String s = new String(data);
		return getStreamDefinitionFromJsonData(s);
	}

	public static Group getGroupDefinitionFromJsonData(byte[] data) {
		String s = new String(data);
		return getGroupDefinitionFromJsonData(s);
	}

	public static Group getGroupDefinitionFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		return getGroupDefinitionFromJsonObject(jsonObject);
	}

	public static Group getGroupDefinitionFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.remove("x");// remove points
		jsonObject.remove("y");// remove points
		jsonObject.remove("r");// remove points
		jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		jsonObject.remove("shape");// remove points
		Group bean = (Group) JSONObject.toBean(jsonObject, Group.class, classMap);
		return bean;
	}

	public static ColDef getColDefinitionFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.remove("x");// remove points
		jsonObject.remove("y");// remove points
		jsonObject.remove("r");// remove points
		jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		jsonObject.remove("shape");// remove points
		jsonObject.remove("textual");// remove points

		ColDef bean = (ColDef) JSONObject.toBean(jsonObject, ColDef.class, classMap);
		return bean;
	}

	public static SQLObj getSqlDefinitionFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("c");// from stream
		jsonObject.remove("x");// remove points
		jsonObject.remove("y");// remove points
		jsonObject.remove("r");// remove points
		jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		jsonObject.remove("shape");// remove points
		jsonObject.remove("textual");// remove points
		SQLObj bean = (SQLObj) JSONObject.toBean(jsonObject, SQLObj.class, classMap);
		return bean;
	}

	public static TableDef getTableDefDefinitionFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.remove("x");// remove points
		jsonObject.remove("y");// remove points
		jsonObject.remove("r");// remove points
		jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		jsonObject.remove("shape");// remove points
		jsonObject.remove("textual");// remove points
		TableDef bean = (TableDef) JSONObject.toBean(jsonObject, TableDef.class, classMap);
		return bean;
	}

	public static ConnDef getConnDefinitionFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		return getConnDefinitionFromJsonObject(jsonObject);
	}

	public static FunctionPoint getFunctionPointFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		// jsonObject.remove("x");// remove points
		// jsonObject.remove("y");// remove points
		// jsonObject.remove("r");// remove points
		// jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		FunctionPoint bean = (FunctionPoint) JSONObject.toBean(jsonObject, FunctionPoint.class, classMap);
		return bean;
	}

	public static StepObj getStepObjFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		// jsonObject.remove("x");// remove points
		// jsonObject.remove("y");// remove points
		// jsonObject.remove("r");// remove points
		// jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		StepObj bean = (StepObj) JSONObject.toBean(jsonObject, StepObj.class, classMap);
		return bean;
	}
	public static Shape getShapeObjFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		// jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		Shape bean = (Shape) JSONObject.toBean(jsonObject, Shape.class, classMap);
		bean.getFacets().clear();
		bean.getPoints().clear();
		JSONArray pts= (JSONArray) jsonObject.get("pts");
		for(Object jo: pts){
			JSONObject o = (JSONObject) jo;
			Point f = getPointObjFromJsonObject(o);
			bean.getPoints().add(f);
		}
		JSONArray oo= (JSONArray) jsonObject.get("facets");
		for(Object jo: oo){
			JSONObject o = (JSONObject) jo;
			Facet f = getFacetObjFromJsonObject(o);
			bean.getFacets().add(f);
		}
		return bean;
	}
	public static Facet getFacetObjFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		Facet bean = (Facet) JSONObject.toBean(jsonObject, Facet.class, classMap);
		bean.getConnectors().clear();
		JSONArray oo= (JSONArray) jsonObject.get("connectors");
		for(Object jo: oo){
			JSONObject o = (JSONObject) jo;
			Connector f = getConnectorObjFromJsonObject(o);
			bean.getConnectors().add(f);
		}
		return bean;
	}
	public static Connector getConnectorObjFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		Connector bean = (Connector) JSONObject.toBean(jsonObject, Connector.class, classMap);
		return bean;
	}
	public static Point getPointObjFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		Point bean = (Point) JSONObject.toBean(jsonObject, Point.class, classMap);
		return bean;
	}
	public static FunctionPoint getPortDefFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.remove("x");// remove points
		jsonObject.remove("y");// remove points
		jsonObject.remove("r");// remove points
		jsonObject.remove("b");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		FunctionPoint bean = (FunctionPoint) JSONObject.toBean(jsonObject, FunctionPoint.class, classMap);
		return bean;
	}

	public static ConnDef getConnDefinitionFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("defs");// from node
		jsonObject.remove("props");// from stream
		jsonObject.remove("shape");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		String connCond = jsonObject.getString("connCond");
		// connCond = escapeLang(connCond);
		jsonObject.put("connCond", "");
		ConnDef bean = (ConnDef) JSONObject.toBean(jsonObject, ConnDef.class, classMap);
		bean.setConnCond(connCond);
		return bean;
	}

	public static String unescapeLang(String connCond) {
		connCond = StringUtils.replace(connCond, "\\[", "[");
		connCond = StringUtils.replace(connCond, "\\]", "]");
		return connCond;
	}

	public static String escapeLang(String connCond) {
		StringBuilder sb = new StringBuilder();
		if (connCond != null) {
			for (char ch : connCond.toCharArray()) {
				if (ch == '[') {
					sb.append("\\[");
				} else if (ch == ']') {
					sb.append("\\]");
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	public static ProcessorDef getProcDefinitionFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		jsonObject.remove("defs");// from node

		return getProcDefinitionFromJsonObject(jsonObject);
	}

	public static ProcessorDef getProcDefinitionFromJsonObject(JSONObject jsonObject) {
		// System.out.println("Name of procesor parsing: " +
		// jsonObject.getString("name"));
		Map classMap = new HashMap();
		ProcessorDef bean = (ProcessorDef) JSONObject.toBean(jsonObject, ProcessorDef.class, classMap);
		return bean;
	}

	public static Stream getStreamDefinitionFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		return getStreamDefinitionFromJsonObject(jsonObject);
	}

	public static Stream getStreamDefinitionFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("props");// from stream

		Stream bean = (Stream) JSONObject.toBean(jsonObject, Stream.class, classMap);

		Morpher dynaMorpher = new BeanMorpher(StreamDefinition.class, JSONUtils.getMorpherRegistry());
		JSONUtils.getMorpherRegistry().registerMorpher(dynaMorpher);
		Map<String, StreamDefinition> output = new HashMap<String, StreamDefinition>();
		for (Iterator i = bean.getDefs().values().iterator(); i.hasNext();) {
			StreamDefinition prop = (StreamDefinition) JSONUtils.getMorpherRegistry().morph(StreamDefinition.class, i.next());
			output.put("" + prop.getName(), prop);
		}
		bean.setDefs(output);
		if (bean.getEventType() == null) {
			bean.setEventType("TestEvent");
		}
		return bean;
	}

	public static Event[] getParamArray(Event e, List<String> depends, Map<String, String> map) {
		Event[] ret = new Event[depends.size()];
		int i = 0;
		String realEventType = null;
		if (map != null) {
			realEventType = map.get(e.getEventType());
		}
		if (realEventType == null) {
			realEventType = e.getEventType();
		}
		for (String s : depends) {
			if (e.getEventType().equals(s) || s.equals("org.ptg.events.Event")) {
				ret[i] = e;
			} else {
				ret[i] = null;
			}
			i++;
		}
		return ret;
	}

	public static String getNumStr(int i) {
		return strNum.get(i);
	}

	// ...............................................
	public static void print(String s) {
		// System.out.println(s);
	}

	public static void appendFile(String name, String str) {
		try {
			FileWriter w = new FileWriter(name, true);
			w.write(str);
			w.flush();
			w.close();
			w = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(String name, String str) {
		try {
			FileWriter w = new FileWriter(name);
			w.write(str);
			w.flush();
			w.close();
			w = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Collection<String> loadFile(String name) {
		try {
			LineNumberReader r = new LineNumberReader(new FileReader(name));
			Collection<String> list = new ArrayList<String>();
			String s = null;
			String ret = "";
			while ((s = r.readLine()) != null) {
				list.add(s);
			}
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void forEachLineInfile(String name, Closure f) {
		LineNumberReader r = null;
		try {
			r = new LineNumberReader(new FileReader(name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String s = null;
		if (r != null) {
			try {
				while ((s = r.readLine()) != null) {
					f.execute(s);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void forEachWordInFile(String name, Closure f) {
		String str = loadFileContent(name);
		String[] a = StringUtils.split(str);
		for (String i : a) {
			f.execute(i);
		}
	}

	public static String[] substringBetween(String str, String start, String end) {
		return StringUtils.substringsBetween(str, start, end);
	}

	public static String substringAfterLast(String s, String str) {
		return StringUtils.substringAfterLast(s, str);
	}

	public static String substringBeforeLast(String s, String str) {
		return StringUtils.substringBeforeLast(s, str);
	}

	public static String rightPad(String s, int i, String c) {
		return StringUtils.rightPad(s, i, c);
	}

	public static String leftPad(String s, int i, String c) {
		return StringUtils.leftPad(s, i, c);
	}

	public static String reverse(String s) {
		return StringUtils.reverse(s);
	}

	public static boolean isAlphanumeric(String s) {
		return StringUtils.isAlphanumeric(s);
	}

	public static boolean isAlpha(String s) {
		return StringUtils.isAlpha(s);
	}

	public static boolean isWhitespace(String s) {
		return StringUtils.isWhitespace(s);
	}

	public static boolean isEmpty(String s) {
		return StringUtils.isBlank(s);
	}

	public static String except(String s, String start, String end) {
		String temp = StringUtils.substringBefore(s, start);
		String temp2 = StringUtils.substringAfterLast(s, end);
		return temp + temp2;
	}

	public static int indexOf(String s, String s2) {
		return StringUtils.indexOf(s, s2);
	}

	public static boolean contains(String s, String s2) {
		return StringUtils.contains(s, s2);
	}

	public static String loadFileContent(String name) {
		try {
			LineNumberReader r = new LineNumberReader(new FileReader(name));
			String str = "";
			String s = "";
			while ((s = r.readLine()) != null) {
				str += s + "\n";
			}
			r.close();
			return str;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void forEachFileInDir(String name, Closure func, boolean withdir) {
		File f = new File(name);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File fl : files) {
				if (fl.isFile()) {
					func.execute(fl.getAbsolutePath());
				} else if (f.isDirectory()) {
					if (withdir) {
						func.execute(fl.getAbsolutePath());
					}
					forEachFileInDir(fl.getAbsolutePath(), func, withdir);
				}
			}
		}
	}

	public static void forEachFileInDirFile(String name, Closure func, boolean withdir) {
		File f = new File(name);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File fl : files) {
				if (fl.isFile()) {
					func.execute(fl);
				} else if (f.isDirectory()) {
					if (withdir) {
						func.execute(fl);
					}
					forEachFileInDirFile(fl.getAbsolutePath(), func, withdir);
				}
			}
		}
	}

	public static String substringBefore(String s, String s2) {
		return StringUtils.substringBefore(s, s2);
	}

	public static void sendEmail(String from, String to, String subject, String desc, Map<String, String> attachments) {
		EmailUtil u = new EmailUtil();
		u.SendMailWithAttachment(from, to, subject, desc, attachments);
	}

	public static void sendEmailEx(String from, String to, String subject, String desc, Map<String, String> attachments) {
		EmailUtil u = new EmailUtil();
		u.SendMailWithAttachmentEx(from, to, subject, desc, attachments);
	}

	public static Exchange sendAndWait(String where, Object e) {
		DefaultMessage msgtemp = new DefaultMessage();
		msgtemp.setBody(e);
		Exchange ex = WebStartProcess.getInstance().getRoutingEngine().sendMessage("direct:" + where, msgtemp);
		return ex;
	}

	public static void sendAsync(final String where, final Object e) {
		ThreadManager mgr = (ThreadManager) SpringHelper.get("threadManager");
		mgr.submit(new Runnable() {
			final String w = where;
			final Object m = e;

			@Override
			public void run() {
				DefaultMessage msgtemp = new DefaultMessage();
				msgtemp.setBody(m);
				WebStartProcess.getInstance().getRoutingEngine().sendMessage("direct:" + w, msgtemp);
			}
		});
	}

	public static Exchange sendAndWait(String where, Exchange e) {
		Exchange ex = WebStartProcess.getInstance().getRoutingEngine().sendMessage("direct:" + where, e);
		return ex;
	}

	public static void sendAsync(final String where, final Exchange e) {
		ThreadManager mgr = (ThreadManager) SpringHelper.get("threadManager");
		mgr.submit(new Runnable() {
			final String w = where;
			final Object m = e;

			@Override
			public void run() {
				WebStartProcess.getInstance().getRoutingEngine().sendMessage("direct:" + w, e);
			}
		});
	}

	public static void runAsync(final Runnable runner) {
		ThreadManager mgr = (ThreadManager) SpringHelper.get("threadManager");
		mgr.submit(runner);
	}

	public static <TYPE> Collection<Collection<TYPE>> cartesianProduct(Collection<TYPE>... sets) {
		if (sets.length < 2) {
			throw new IllegalArgumentException("Can't have a product of fewer than two sets (got " + sets.length + ")");
		}

		return _cartesianProduct(0, sets);
	}

	private static <TYPE> Collection<Collection<TYPE>> _cartesianProduct(int index, Collection<TYPE>... sets) {
		Collection<Collection<TYPE>> ret = new ArrayList<Collection<TYPE>>();
		if (index == sets.length) {
			ret.add(new ArrayList<TYPE>());
		} else {
			for (TYPE obj : sets[index]) {
				for (Collection<TYPE> set : _cartesianProduct(index + 1, sets)) {
					set.add(obj);
					ret.add(set);
					if (set.size() == sets.length) {
						// System.out.println("Done:" + set);
					}
				}
			}
		}
		return ret;
	}









	public static void compactList(List l, int size) {
		while (l.size() > size) {
			l.remove(0);
		}
	}

	public static String getJsonPathVal(String doc, String q) {
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		List l = new ArrayList();
		l.add(doc);
		l.add(q);
		Object ret = s.runFuntionRaw("js_jsonPath", l);
		return (String) ret;
	}

	public static void sendTrace(String stmt, String origin) {
		Event evt = new TraceEvent(stmt, origin, "T");
		publishEvent(evt);
	}

	public static void sendException(String stmt, String origin) {
		Event evt = new TraceEvent(stmt, origin, "E");
		publishEvent(evt);
	}

	public static void sendLog(String stmt, String origin) {
		// System.out.println("{\n" + stmt + "\n}");
		Event evt = new TraceEvent(stmt, origin, "L");
		publishEvent(evt);
	}

	public static String getZip(String location, String state) {
		String q = "select zipcode from zipinfo where state='" + state + "' and city='" + location + "' limit 1";
		String ret = DBHelper.getInstance().getString(q);
		if (ret == null) {
			q = "select zipcode from zipinfo where state='" + state + "'  limit 1";
			ret = DBHelper.getInstance().getString(q);
		}
		if (ret == null) {
			return "11111";
		} else {
			return ret;
		}
	}

	public static String[] extractNumbers(String location) {
		return new String[] { "" };
	}

	public static String[] extractPhone(String body) {
		return new String[] { "" };
	}

	public static String[] extractEmail(String body) {
		return new String[] { "" };
	}

	public static int[] extractMoneyValue(String body) {
		return new int[] { 0 };
	}

	public static String[] extractStringValues(String body) {
		return new String[] { "" };
	}

	public static String getResultCSV(String query) {
		return DBHelper.getInstance().getResultCSV(query);
	}

	public static String getResultJson(String query) {
		return DBHelper.getInstance().getResultJson(query);
	}



	public static boolean chmod(String f, int mode, boolean recurse) throws InterruptedException {
		File file = new File(f);
		// TODO: Refactor this to FileSystemUtils
		List<String> args = new ArrayList<String>();
		args.add("chmod");

		if (recurse) {
			args.add("-R");
		}

		args.add(Integer.toString(mode));
		args.add(file.getAbsolutePath());

		Process proc;

		try {
			proc = Runtime.getRuntime().exec(args.toArray(new String[args.size()]));
		} catch (IOException e) {
			return false;
		}
		int result = proc.waitFor();
		return result == 0;
	}

	public static boolean chown(String f, String userWithGroup, boolean recurse) throws InterruptedException {
		File file = new File(f);
		List<String> args = new ArrayList<String>();
		args.add("chown");

		if (recurse) {
			args.add("-R");
		}

		args.add(userWithGroup);
		args.add(file.getAbsolutePath());

		Process proc;

		try {
			proc = Runtime.getRuntime().exec(args.toArray(new String[args.size()]));
		} catch (IOException e) {
			return false;
		}
		int result = proc.waitFor();
		return result == 0;
	}

	public static void generateGetProcessJson() {
		Map<String, String> procs = new TreeMap<String, String>();
		File[] fls = (File[]) FileUtils.listFiles(new File(base + "bin" + File.separator + "org" + File.separator + "ptg" + File.separator + "processors"), null, true).toArray(new File[0]);
		for (File f : fls) {
			String temp = StringUtils.substringAfterLast(f.getPath(), base + "bin" + File.separator);
			temp = getClassNameFromFileName(temp);
			try {
				Class c = Class.forName(temp);
				if (IProcessor.class.isAssignableFrom(c)) {
					procs.put(StringUtils.substringAfterLast(temp, "."), "{\"name\": \"" + StringEscapeUtils.escapeJavaScript(temp) + "\"}");
					IProcessor p = (IProcessor) c.newInstance();
					String sql = "insert into procdocs(name,doc,configoptions) values( \'" + c.getName() + "\'," + "\'" + StringEscapeUtils.escapeSql(p.getDoc()) + "\',\'"
							+ StringEscapeUtils.escapeSql(p.getConfigOptions()) + "\') ";
					// System.out.println(p.getDoc());
					// System.out.println("now executing : " + sql);
					DBHelper.getInstance().execute(sql);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		try {
			insertAnonDocs();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		int i = 0;
		for (Map.Entry<String, String> s : procs.entrySet()) {
			sb.append(s.getValue());
			i++;
			if (i < procs.size()) {
				sb.append(",\n");
			}
		}
		sb.append("\n]");
		// System.out.println(sb.toString());
		try {
			String fname = base + File.separator + "site" + File.separator + "getprocess.json";
			FileUtils.writeStringToFile(new File(fname), sb.toString());
			chmod(fname, 755, false);
			chown(fname, "www-data:www-data", false);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void insertAnonDocs() throws Exception {
		String[] names = { "ConcatTask", "LogTask", "IfTask", "ConstantTask", "RunGraphTask", "CommandTask", "LongRunningCommandTask", "SchemaMapperTask" };
		for (String c : names) {
			ITaskFunction p = (ITaskFunction) Class.forName("org.ptg.util.taskfunctions." + c).newInstance();
			String sql = "insert into procdocs(name,doc,configoptions) values( \'" + c + "\'," + "\'" + StringEscapeUtils.escapeSql(p.getDoc()) + "\',\'"
					+ StringEscapeUtils.escapeSql(p.getConfigOptions()) + "\') ";
			// System.out.println("now executing : " + sql);
			DBHelper.getInstance().execute(sql);
		}
	}

	
	public static String generateEventLayout(String s) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(s);
		StringBuilder sb = new StringBuilder();
		sb.append("  [" + "\n");
		if (ed != null) {
			int i = 0;
			sb.append("  {" + "\n");
			sb.append("field:'" + "id" + "',\n");
			sb.append("name:'" + "id" + "',\n");
			sb.append("width: '60px'" + "\n");
			sb.append("}" + ",\n");

			for (PropertyDefinition pd : ed.getProps().values()) {
				sb.append("  {" + "\n");
				sb.append("field:'" + WordUtils.uncapitalize(pd.getName()) + "',\n");
				sb.append("name:'" + WordUtils.uncapitalize(pd.getName()) + "',\n");
				sb.append("width: '60px'" + "\n");
				sb.append("}" + "\n");
				i++;
				if (i != ed.getProps().values().size()) {
					sb.append("\n,\n");
				}
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static String generateSQLLayout(String s) {
		StringBuilder sb = new StringBuilder();
		sb.append("  [" + "\n");
		{
			int i = 0;
			sb.append("  {" + "\n");
			sb.append("field:'" + "id" + "',\n");
			sb.append("name:'" + "id" + "',\n");
			sb.append("width: '60px'" + "\n");
			sb.append("}" + ",\n");
			try {
				ZqlParser p = new ZqlParser();
				p.initParser(new ByteArrayInputStream(s.getBytes()));
				ZQuery st = (ZQuery) p.readStatement();
				Vector<ZSelectItem> sels = st.getSelect();
				for (ZSelectItem col : sels) {
					String res = col.getAlias() == null ? col.getColumn() : col.getAlias();
					// System.out.println("Selecting:" + res);
					sb.append("  {" + "\n");
					sb.append("field:'" + WordUtils.uncapitalize(res) + "',\n");
					sb.append("name:'" + WordUtils.uncapitalize(res) + "',\n");
					sb.append("width: '60px'" + "\n");
					sb.append("}" + "\n");
					i++;
					if (i != sels.size()) {
						sb.append("\n,\n");
					}
				}
			} catch (Zql.ParseException e) {
				e.printStackTrace();
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static String generatePropLayout(String s) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(s);
		StringBuilder sb = new StringBuilder();
		sb.append("  [" + "\n");
		if (ed != null) {
			int i = 0;
			sb.append("  {" + "\n");
			sb.append("field:'" + "id" + "',\n");
			sb.append("name:'" + "id" + "',\n");
			sb.append("width: '60px'" + "\n");
			sb.append("}" + ",\n");
			String[] splits = StringUtils.split(s, ":");

			for (String sp : splits) {
				sb.append("  {" + "\n");
				sb.append("field:'" + WordUtils.uncapitalize(sp) + "',\n");
				sb.append("name:'" + WordUtils.uncapitalize(sp) + "',\n");
				sb.append("width: '60px'" + "\n");
				sb.append("}" + "\n");
				i++;
				if (i != splits.length) {
					sb.append("\n,\n");
				}
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static org.mozilla.javascript.Context beginScriptContext() {
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		return s.enter();
	}

	public static void endScriptContext(org.mozilla.javascript.Context cxlocal) {
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		s.leave(cxlocal);
	}

	public static Object executeScript(String code) {
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		return s.runString(code);
	}

	public static void removeObject(String name) {
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		s.removeObject(name);
	}

	public static void addObject(String name, Object data) {
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		s.addObject(name, data);
	}

	public static Object getObject(String name) {
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		Object o = s.getObject(name);
		if (o instanceof NativeJavaObject) {
			NativeJavaObject j = (NativeJavaObject) o;
			o = j.unwrap();
		}
		return o;
	}

	public static Class compileClosure(String name, String code, boolean update) throws Exception {
		return DynaObjectHelper.getClosureImplWithException(name, code, update);
	}

	public static boolean checkClassExists(String name) {
		Class c = null;
		try {
			// System.out.println("Now Looking: " + name);
			c = Class.forName(name);
			return true;
		} catch (ClassNotFoundException e) {
			// System.out.println("Class Not Found" + name);
		}
		return false;
	}

	public static Stream getStream(String streamName) {
		Stream stream = null;
		try {
			Class c = StreamManager.getInstance().getStreamTransformer(streamName);
			stream = StreamManager.getInstance().getStream(streamName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stream;
	}

	public static void executeQuery(String s) {
		DBHelper.getInstance().execute(s);

	}

	public static List getMethods(String className) {
		return DynaObjectHelper.getMethodsWithSig(className);
	}

	public static Map getFields(String className) {
		return DynaObjectHelper.getFields(className);
	}

	public static Date getDateFromTimeStamp(String tStamp) {
		Date dt = new Date();
		String timeParts[] = StringUtils.split(tStamp, ":");
		dt.setHours(Integer.parseInt(timeParts[0]));
		dt.setMinutes(Integer.parseInt(timeParts[1]));
		dt.setSeconds(Integer.parseInt(timeParts[2]));
		return dt;
	}

	public static String toDatePeriod(String in) {
		String out = in;
		String parts[] = StringUtils.split(in, "-");
		out = "20" + parts[2] + toMonth(parts[1]) + toDays(parts[0]);
		return out;
	}

	public static String toDays(String in) {
		if (in.length() == 1) {
			return "0" + in;
		}
		return in;

	}

	public static String toMonth(String in) {
		String ret = monthMap.get(in);

		return ret;
	}

	public static void testDate() {
		// System.out.println(toDatePeriod("24-Sep-10"));
		// System.out.println(toDatePeriod("9-Jul-10"));
		// System.out.println(toDatePeriod("14-Jun-10"));
		// System.out.println(toDatePeriod("1-jun-10"));
	}

	public static String eventForm(String type) {
		EventDefinition def = EventDefinitionManager.getInstance().getEventDefinition(type);
		for (PropertyDefinition pdef : def.getProps().values()) {
			// System.out.println(pdef.getName());
		}
		Map m = new HashMap();
		m.put("formId", type + "NewForm");// $formId
		m.put("propList", def.getProps());// propList
		m.put("formStream", "/" + type + "CreateStream");// $formStream
		StringBuffer responseContent = VelocityHelper.burnTemplate(m, "NewEventForm.vm");
		return responseContent.toString();
	}

	public static String eventUpdateForm(String type, String url) {
		EventDefinition def = EventDefinitionManager.getInstance().getEventDefinition(type);
		for (PropertyDefinition pdef : def.getProps().values()) {
			// System.out.println(pdef.getName());
		}
		Map m = new HashMap();
		m.put("formId", type + "UpdateForm");// $formId
		m.put("propList", def.getProps());// propList
		m.put("formStream", "/" + type + "UpdateStream");// $formStream
		StringBuffer responseContent = VelocityHelper.burnTemplate(m, "UpdateEventForm.vm");
		return responseContent.toString();
	}

	/*
	 * todo: fix list event form.vm need to lis the events with two buttons to
	 * update and new
	 */
	public static String listEventPage(String eventType, String sqlstr) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(eventType);
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(eventType);
		IEventDBTransformer dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
		dbtransformer.setStore(ed.getEventStore());
		DBHelper db = DBHelper.getInstance();
		List<Event> events = db.getEventsFromTable(sqlstr, dbtransformer);
		Map m = new HashMap();
		m.put("formId", eventType + "ListForm");// $formId
		m.put("propList", ed.getProps());// propList
		m.put("newformStream", "/myhtml/" + eventType + "NewStream");// $formStream
		m.put("updateformStream", "/myhtml/" + eventType + "UpdateStream");// $formStream
		StringBuffer responseContent = VelocityHelper.burnTemplate(m, "ListEventForm.vm");
		return responseContent.toString();
	}

	public static boolean saveEventToDB(Event e) {
		boolean result = false;
		try {
			EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(e.getEventType());
			Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(e.getEventType());
			IEventDBTransformer dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
			dbtransformer.setStore(ed.getEventStore());
			dbtransformer.saveToDb(e);
			result = true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public static void updateEventToDB(Event e) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(e.getEventType());
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(e.getEventType());
		IEventDBTransformer dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
		dbtransformer.setStore(ed.getEventStore());
		dbtransformer.update(e);
	}

	public static void updateEventByXrefToDB(Event e) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(e.getEventType());
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(e.getEventType());
		IEventDBTransformer dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
		dbtransformer.setStore(ed.getEventStore());
		dbtransformer.updateByXref(e);
	}

	public static void deleteEventFromDB(Event e) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(e.getEventType());
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(e.getEventType());
		IEventDBTransformer dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
		dbtransformer.setStore(ed.getEventStore());
		dbtransformer.deleteFromDb(e);
	}

	public static boolean saveEventToDBNative(Event e) {
		boolean result = false;
		try {
			EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(e.getEventType());
			Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(e.getEventType());
			IEventDBTransformer dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
			dbtransformer.setStore(ed.getEventStore());
			dbtransformer.saveToDbNative(e);
			result = true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public static void updateEventToDBNative(String whereClause, Event e) {
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(e.getEventType());
		Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(e.getEventType());
		IEventDBTransformer dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
		dbtransformer.setStore(ed.getEventStore());
		dbtransformer.updateNative(whereClause, e);
	}

	/*
	 * todo : implement clone
	 */
	public static Event cloneEvent(Event evt) {
		try {
			return evt.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void publishEvent(Event evt) {
		ContPublisherWriter.getInstance().loadEvent(evt);
	}

	public static void publishSystemEvent(Event evt) {
		ContPublisherWriter.getInstance().loadEvent(evt);
	}

	public static void saveLog(String stmt, String origin) {
		s.saveLog(stmt, origin);
	}

	public static void saveException(String stmt, String origin) {
		s.saveException(stmt, origin);
	}

	public static void saveLog(String stmt) {
		s.saveLog(stmt);
	}

	public static void saveException(String stmt) {
		s.saveException(stmt);
	}

	public static Object evalJexl(Object item, String jexlExp) {
		Expression e = expressions.get(jexlExp);
		if (e == null) {
			e = jexl.createExpression(jexlExp);
			expressions.put(jexlExp, e);
		}
		JexlContext jc = new MapContext();
		jc.set("in", item);
		Object o = e.evaluate(jc);
		return o;

	}

	public static void addJarToClassPath(String path) {
		try {
			ClassPathHacker.addFile(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map getConfigFromJsonData(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		Map bean = new HashMap();
		for (Object o : jsonObject.keySet()) {
			bean.put(o, jsonObject.get(o));
		}
		return bean;
	}

	public static Object fromYamlString(String code) {
		Yaml yaml = new Yaml();
		return yaml.load(code);

	}

	public static String toYamlString(Object code) {
		Yaml yaml = new Yaml();
		return yaml.dump(code);
	}

	public static String jaxbMarshall(Object o, String pack) {
		JAXBContext context = null;
		Marshaller marshaller = null;
		String ret = null;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			context = JAXBContext.newInstance(pack);
			marshaller = context.createMarshaller();
			marshaller.marshal(o, bs);
			ret = new String(bs.toByteArray());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Object jaxbUnMarshall(InputStream data, String pack) {
		JAXBContext context = null;
		Unmarshaller unmarshaller = null;
		Object ret = null;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			context = JAXBContext.newInstance(pack);
			unmarshaller = context.createUnmarshaller();
			ret = unmarshaller.unmarshal(data);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return ret;
	}

	// Loads dynamicall and every time a new class is created since
	// fqn of class includes classloaded and this instantiates a new
	// class loaded every time
	public static Class compileJavaToClass(String path, String dest) {
		Class o = null;
		try {
			CommonCompiler cl = new CommonCompiler(CommonUtil.class.getClassLoader());
			cl.setSourcePath(new File[] { new File(path) });
			byte[] ret = cl.getClassByte(dest);
			ClassPool pool = getClassPool();
			CtClass cc = pool.makeClass(new ByteArrayInputStream(ret), true);
			o = cc.toClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public static ClassPool getClassPool() {
		ClassPool pool = ClassPool.getDefault();
		pool.appendSystemPath();
		String extraBin = (String) SpringHelper.get("basedir") + SystemUtils.FILE_SEPARATOR + "extrabin";
		String extraClass = (String) SpringHelper.get("basedir") + SystemUtils.FILE_SEPARATOR + "uploaded" + SystemUtils.FILE_SEPARATOR + "extraclass";
		try {
			pool.appendClassPath(extraBin);
			pool.appendClassPath(extraClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pool;
	}

	public static Class compileJavaToRule(String path, String dest) {
		Class o = null;
		try {
			CommonCompiler cl = new CommonCompiler(CommonUtil.class.getClassLoader());
			cl.setSourcePath(new File[] { new File(path) });
			byte[] ret = cl.getClassByte(dest);
			o = cl.findClass(dest);
			ClassPool pool = getClassPool();
			CtClass cc = pool.makeClass(new ByteArrayInputStream(ret), false);
			Method[] m = o.getMethods();
			for (Method mtd : m) {
				if (mtd.getName().equals("transform")) {
					cc.addInterface(pool.getCtClass("org.ptg.util.ITransformationRule"));
					break;
				} else if (mtd.getName().equals("runrule")) {
					cc.addInterface(pool.getCtClass("org.ptg.util.IBusinessRule"));
					break;
				} else if (mtd.getName().equals("validate")) {
					cc.addInterface(pool.getCtClass("org.ptg.util.IValidationRule"));
					break;
				} else {
					return null;
				}
			}
			cc.setName(dest + getRandomString(8));
			o = cc.toClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public static Class compileJavaToClassFresh(String path, String dest) throws Exception {
		return compileJavaToClassFresh(path, dest, false);
	}

	/* always reload class */
	public static Class compileJavaToClassFresh(String path, String dest, boolean saveBytes) throws Exception {
		Class o = null;
		File[] sourcePath;
		if (path == null) {
			path = base + "uploaded/extrajava/";
			sourcePath = new File[] { new File(base + "uploaded/extrajava/"), new File(base + "uploaded/extraprocess/"), new File(base + "uploaded/extrajob/"), new File(base + "uploaded/titan/"),
					new File(base + "uploaded/in/"), new File(base + "uploaded/extraindex/"), new File(base + "uploaded/javarule/"), new File(base + "uploaded/out/") };
		} else {
			sourcePath = new File[] { new File(path) };

		}
		CommonCompiler cl = new CommonCompiler(CommonUtil.class.getClassLoader());
		cl.setSourcePath(sourcePath);

		// cl.setDebuggingInfo(true,true,true);
		byte[] ret = cl.getClassByte(dest);

		// CtClass cc = pool.makeClass(new ByteArrayInputStream(ret), false);
		// Class retCl = cc.toClass();
		Class retCl = cl.findClass(dest);

		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(base + "uploaded/extraclass/" + dest + ".class"), ret);
		return retCl;
	}

	public static byte[] compileToByteCode(String basePath, String className) throws Exception {
		// System.out.println("Now compiling: " + className);
		if (basePath == null) {
			basePath = base + "uploaded/extrajava/";
		}
		byte[] ret = null;
		CommonCompiler cl = new CommonCompiler(CommonUtil.class.getClassLoader());
		cl.setSourcePath(new File[] { new File(basePath) });

		ret = cl.getClassByte(className);
		return ret;
	}

	// ////////////////converts bytecode to class
	// always use through classpool becuase that is
	// out default factory!!!
	public static Class byteCodeToClass(byte[] data, String name) {
		Class cls = null;
		try {
			ClassPool pool = getClassPool();
			CtClass cc = pool.makeClass(new ByteArrayInputStream(data));
			cc.setName(name);
			cls = cc.toClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cls;
	}

	// //////////////
	public static Class getUploadedProcessClass(String basePath, String cname, String cnameOut) throws Exception {
		if (basePath == null) {
			basePath = base + "uploaded/extraprocess/";
		}
		ClassPool pool = getClassPool();
		byte[] cls = compileToByteCode(basePath, cname);
		CtClass cc = pool.makeClass(new ByteArrayInputStream(cls), true);
		if (cc.getSuperclass().equals(pool.get("java.lang.Object"))) {
			cc.setSuperclass(pool.get("org.ptg.util.AbstractIProcessor"));
		}
		cc.setName(cnameOut + "_" + getRandomString(10));
		cc.addInterface(pool.get("org.ptg.util.IProcessor"));
		return cc.toClass();
	}

	public static Class getUploadedJobClass(String basePath, String cname, String cnameOut) throws Exception {
		if (basePath == null) {
			basePath = base + "uploaded/extrajob/";
		}
		ClassPool pool = getClassPool();
		byte[] cls = compileToByteCode(basePath, cname);
		CtClass cc = pool.makeClass(new ByteArrayInputStream(cls), true);
		if (cc.getSuperclass().equals(pool.get("java.lang.Object"))) {
			cc.setSuperclass(pool.get("org.ptg.timer.AbstractJob"));
		}
		cc.setName(cnameOut + "_" + getRandomString(10));
		// cc.addInterface(pool.get("org.quartz.Job"));
		return cc.toClass();
	}

	public static void precompileTitan(String basePath, String cname) {
		String f = basePath + "/" + cname + ".titan";
		try {
			String data = org.apache.commons.io.FileUtils.readFileToString(new File(f));
			// compile data in between
			data = CommonUtil.extractTextFromHtmlTitan(data);
			data = TitanCompiler.compile(data);
			org.apache.commons.io.FileUtils.writeStringToFile(new File(basePath + "/" + cname + ".java"), data);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* always reload class */
	public static Class compileTitanToClassFresh(String basePath, String cname) throws Exception {
		if (basePath == null) {
			basePath = base + "uploaded/titan/";
		}
		ClassPool pool = getClassPool();
		precompileTitan(basePath, cname);
		byte[] cls = compileToByteCode(basePath, cname);
		CtClass cc = pool.makeClass(new ByteArrayInputStream(cls), false);
		cc.setSuperclass(pool.get("org.ptg.util.titan.TitanBase"));
		cc.setName(cname + "_" + getRandomString(10));
		cc.addInterface(pool.get("org.ptg.util.titan.ITitanClass"));
		return cc.toClass();
	}

	/* always reload class */
	public static ITitanClass compileTitanToObjectFresh(String basePath, String cname) throws Exception {
		Class c = compileTitanToClassFresh(basePath, cname);
		return (ITitanClass) c.newInstance();
	}

	// ///////////////event specific
	public static Class getUploadedEventClass(String basePath, String cname, String cnameOut) throws Exception {
		if (basePath == null) {
			basePath = base + "uploaded/extrajava/";
		}
		ClassPool pool = getClassPool();
		byte[] cls = compileToByteCode(basePath, cname);
		CtClass cc = pool.makeClass(new ByteArrayInputStream(cls));
		cc.setName(cnameOut);
		cc.setSuperclass(pool.get("org.ptg.events.Event"));
		return cc.toClass();
	}

	// ///////////////event Transformer specific
	public static Class getUploadedEventTransformerClass(String basePath, String cname, String cnameOut) throws Exception {
		if (basePath == null) {
			basePath = base + "uploaded/extrajava/";
		}

		ClassPool pool = getClassPool();
		byte[] cls = compileToByteCode(basePath, cname);
		CtClass cc = pool.makeClass(new ByteArrayInputStream(cls));
		cc.setName(cnameOut);
		cc.addInterface(pool.get("org.ptg.events.Event.IEventDBTransformer"));
		return cc.toClass();
	}

	// compile xsd to java classes
	public static void xjc(String xsd, String pkg, String out) {
		Project p = new Project();
		p.setProperty("ant.home", (String) SpringHelper.get("basedir"));
		p.setBaseDir(new File((String) SpringHelper.get("basedir")));
		// xsd = StringUtils.replace(xsd, "\\", "/");
		Ant ant = new Ant();

		XJC2Task task = new XJC2Task();
		task.setProject(p);
		task.init();
		task.setSchema(xsd);
		task.setPackage(pkg);
		// task.setTarget("2.2");
		task.options.debugMode = true;
		task.options.verbose = true;
		task.setDestdir(new File(out));

		task.execute();

	}

	public static List<Class> getXSDEventClasses(String basePath, String destDir, String xsd, final String pkg, boolean xsdupdated) {
		final List<Class> ret = new ArrayList<Class>();
		if (basePath == null) {
			basePath = base + "uploaded\\extrajaxb\\";
		}
		if (destDir == null) {
			destDir = basePath;
		}
		if (xsdupdated) {
			xjc(/* "file://" + */basePath + xsd, pkg, destDir);
		}

		final String searchPath = basePath;

		forEachFileInDir(destDir + pkg, new Closure() {

			@Override
			public void execute(Object arg0) {
				String s = (String) arg0;
				String fname = StringUtils.substringAfterLast(s, "\\");
				fname = StringUtils.substringBefore(fname, ".java");
				try {
					Class c = getUploadedEventClass(searchPath, pkg + "." + fname, pkg + "." + fname);
					ret.add(c);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, false);
		return ret;
	}

	public static Class forName(String name) {
		Class cls = null;

		try {
			try {
				cls = Class.forName(name);
			} catch (Exception exp) {
				// System.out.println("Failed to load class from common gene pool");
			}
			if (cls == null) {
				ClassPool p = getClassPool();
				CtClass ct = p.get(name);
				if (ct != null) {
					cls = ct.toClass();
				}
			}
			if (cls == null) {// if everything fails see if we can create a new.
				cls = CommonUtil.compileJavaToClassFresh(null, name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cls;
	}

	public static Class forName(String name, String onpath) {
		Class cls = null;

		try {
			try {
				cls = Class.forName(name);
			} catch (Exception exp) {
				// System.out.println("Failed to load class from common gene pool");
			}
			if (cls == null) {
				ClassPool p = getClassPool();
				try {
					CtClass ct = p.get(name);
					if (ct != null) {
						cls = ct.toClass();
					} else {
						// System.out.println("Failed to load class from our gene pool");
					}
				} catch (Exception e) {
					// System.out.println("Failed to load class from our gene pool");
				}
			}
			if (cls == null) {// if everything fails see if we can create a new.
				cls = CommonUtil.compileJavaToClassFresh(onpath, name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cls;
	}

	public static FileObject getVFSObject(String path) {
		FileObject jarFile = null;
		try {
			FileSystemManager fsManager = VFS.getManager();
			jarFile = fsManager.resolveFile(path);

		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		return jarFile;

	}

	public static void main(String[] args) throws Exception {
		DBHelper.getInstance().execute("truncate table procdocs");
		generateGetProcessJson();

	}

	public static String translateProperty(String s) {
		return s.substring(1, s.length());
	}

	public static String translatePropertyName(String s) {
		return s.substring(1, s.length());
	}

	public static void doDeployement(String base, Map m, String deployName, String deployType, FileItem item) throws IOException {
		String dest;
		byte[] data = item.get();
		try {
			if ("java".equalsIgnoreCase(deployType)) {
				final String path = base + "uploaded/extrajava/";
				dest = item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), data);
				m.put("dest", path + dest);
				String cpath = dest.substring(0, dest.length() - 5);
				CommonUtil.compileJavaToClassFresh(path, cpath);
			}
			if ("jar".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extrajar/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
				CommonUtil.addJarToClassPath(dest);
			}
			if ("script".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extrascript/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("class".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extraclass/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("template".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extratemplate/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("jsp".equalsIgnoreCase(deployType)) {
				dest = base + "site/jsp/uploaded/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("ClassService".equalsIgnoreCase(deployType)) {
				dest = base + "tmp/axis2/webapp/WEB-INF/services/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("ServiceArchive".equalsIgnoreCase(deployType)) {
				dest = base + "tmp/axis2/webapp/WEB-INF/services/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("ProtocolBuf".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extraprotocolbuf/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("xml".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extraxml/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("yaml".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extrayaml/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("process".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded/extraprocess/";
				dest = item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), data);
				m.put("dest", path + dest);
				String cpath = dest.substring(0, dest.length() - 5);
				Class c = CommonUtil.getUploadedProcessClass(path, cpath, deployName);
				IProcessor p = (IProcessor) c.newInstance();
				// System.out.println(p.getName());

			}
			if ("in".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/in/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("out".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/out/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("rule".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extrarule/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
			}
			if ("jaxb".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extrajaxb/" + item.getName();
				String basePath = base + "uploaded/extrajaxb/";
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
				CommonUtil.xjc(/* "file://" + */dest, deployName, basePath);
			}
			if ("jaxbEvents".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extrajaxb/" + item.getName();
				String basePath = base + "uploaded\\extrajaxb\\";
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
				CommonUtil.getXSDEventClasses(basePath, basePath, item.getName(), deployName, true);
			}
			if ("javarule".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded" + File.separator + "javarule" + File.separator;
				dest = item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), data);
				m.put("dest", path + dest);
				String cpath = dest.substring(0, dest.length() - 5);
				Class c = CommonUtil.compileJavaToRule(path, cpath);
				registerJavaRule(deployName, c);
			}
			if ("cacheitem".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded" + File.separator + "cacheitems" + File.separator;
				dest = item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), data);
				m.put("dest", path + dest);
				setItemToCache(deployName, data);
			}
			if ("titan".equalsIgnoreCase(deployType)) {
				final String path = base + "uploaded/titan/";
				dest = item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest + ".titan"), data);
				m.put("dest", path + dest);
				String cpath = dest.substring(0, dest.length() - 5);
				CommonUtil.compileTitanToClassFresh(path, cpath);
			}
			if ("image".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded" + File.separator + "extraimages" + File.separator;
				dest = deployName;
				if (dest == null) {
					dest = item.getFieldName();
				}
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), data);
				m.put("dest", path + dest);
				// copy to site
				path = base + "site/images/uploaded" + File.separator;
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), data);

			}
			if ("plugin".equalsIgnoreCase(deployType)) {
				dest = base + "uploaded/extraplugins/" + item.getName();
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(dest), data);
				m.put("dest", dest);
				IPluginManager pluginsManager = (IPluginManager) SpringHelper.get("pluginsManager");
				pluginsManager.installPlugin(item.getName());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doDeployementOnly(String base, String deployName, String deployType, String dest) throws IOException {
		try {
			if ("java".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded/extrajava/";
				String cpath = dest.substring(0, dest.length() - 5);
				CommonUtil.compileJavaToClassFresh(path, cpath);
			}
			if ("jar".equalsIgnoreCase(deployType)) {
				CommonUtil.addJarToClassPath(dest);
			}
			if ("script".equalsIgnoreCase(deployType)) {
			}
			if ("class".equalsIgnoreCase(deployType)) {
			}
			if ("template".equalsIgnoreCase(deployType)) {
			}
			if ("jsp".equalsIgnoreCase(deployType)) {
			}
			if ("ClassService".equalsIgnoreCase(deployType)) {
			}
			if ("ServiceArchive".equalsIgnoreCase(deployType)) {
			}
			if ("ProtocolBuf".equalsIgnoreCase(deployType)) {
			}
			if ("xml".equalsIgnoreCase(deployType)) {
			}
			if ("yaml".equalsIgnoreCase(deployType)) {
			}
			if ("process".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded/extraprocess/";
				String cpath = dest.substring(0, dest.length() - 5);
				Class c = CommonUtil.getUploadedProcessClass(path, cpath, deployName);
				IProcessor p = (IProcessor) c.newInstance();
				// System.out.println(p.getName());
			}
			if ("in".equalsIgnoreCase(deployType)) {
			}
			if ("out".equalsIgnoreCase(deployType)) {
			}
			if ("rule".equalsIgnoreCase(deployType)) {
			}
			if ("jaxb".equalsIgnoreCase(deployType)) {
				String basePath = base + "uploaded/extrajaxb/";
				CommonUtil.xjc(/* "file://" + */dest, deployName, basePath);
			}
			if ("jaxbEvents".equalsIgnoreCase(deployType)) {
				String fname = StringUtils.substringAfterLast(dest, "/");
				dest = base + "uploaded/extrajaxb/" + fname;
				String basePath = base + "uploaded/extrajaxb/";
				CommonUtil.getXSDEventClasses(basePath, basePath, fname, deployName, true);
			}
			if ("javarule".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded/javarule/";
				String cpath = dest.substring(0, dest.length() - 5);
				Class c = CommonUtil.compileJavaToRule(path, cpath);
				registerJavaRule(deployName, c);
			}
			if ("cacheitem".equalsIgnoreCase(deployType)) {
				setItemToCacheFromFile(deployName, dest);
			}
			if ("titan".equalsIgnoreCase(deployType)) {
				String path = base + "uploaded/titan/";
				CommonUtil.compileTitanToClassFresh(path, deployName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setItemToCacheFromFile(String deployName, String dest) throws IOException {
		byte[] data = org.apache.commons.io.FileUtils.readFileToByteArray(new File(dest));

	}

	private static void setItemToCache(String deployName, byte[] data) {
	}

	public static List<Event> uniqWithHighestId(List<Event> events, String onprop) {
		List<Event> ret = new ArrayList<Event>();
		Map<String, Event> u = new HashMap<String, Event>();
		for (Event e : events) {
			{
				Event te = u.get(e.getEventStringProperty(onprop));
				if (te == null) {
					u.put(e.getEventStringProperty(onprop), e);
				} else {
					if (te.getId() <= e.getId()) {
						u.put(e.getEventStringProperty(onprop), e);
					}
				}
			}
		}
		for (Event evt : u.values()) {
			ret.add(evt);
		}
		return ret;
	}

	public static void loadDeployements() {
		DeployEvent de = new DeployEvent();
		EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(de.getEventType());
		List<Event> events = DBHelper.getInstance().getEventsFromTableWhere(ed.getEventStore(), " order by id desc");
		events = uniqWithHighestId(events, "dest");
		for (Event e : events) {
			de = (DeployEvent) e;
			try {
				doDeployementOnly(base, de.getName(), de.getType(), de.getDest());
			} catch (Exception e1) {
				// System.out.println("Deployement failed for: " +
				// de.getDest());
			}
		}
	}

	public static void registerTransformationRule(String name, ITransformationRule rule) {
		javaRuleManager.registerTransformationRule(name, rule);
	}

	public static ITransformationRule getTransformationRule(String name) {
		return javaRuleManager.getTransformationRule(name);
	}

	public static void registerValidationRule(String name, IValidationRule rule) {
		javaRuleManager.registerValidationRule(name, rule);
	}

	public static IValidationRule getValidationRule(String name) {
		return javaRuleManager.getValidationRule(name);

	}

	public static void registerBusinessRule(String name, IBusinessRule rule) {
		javaRuleManager.registerBusinessRule(name, rule);
	}

	public static IBusinessRule getBusinessRule(String name) {
		return javaRuleManager.getBusinessRule(name);
	}

	public static void registerJavaRule(String name, Class c) throws InstantiationException, IllegalAccessException {
		if (c.equals(IBusinessRule.class)) {
			registerBusinessRule(name, (IBusinessRule) c.newInstance());
		} else if (c.equals(ITransformationRule.class)) {
			registerTransformationRule(name, (ITransformationRule) c.newInstance());
		} else if (c.equals(IValidationRule.class)) {
			registerValidationRule(name, (IValidationRule) c.newInstance());
		}
	}

	public static Object getJXPath(Object obj, String path) {
		JXPathContext context = JXPathContext.newContext(obj);
		return context.getValue(path);
	}

	public static byte[] writeSerializable(Serializable s) {
		if (s == null) {
			return null;
		}
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			ObjectOutputStream str = new ObjectOutputStream(b);
			str.writeObject(s);
			str.flush();
			bytes = b.toByteArray();
			str.close();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		org.ptg.admin.AppContext.getInstance().addStat(CaerusCLIConstants.BytesOut, bytes.length);
		return bytes;
	}

	public static Serializable readSerializable(byte[] s) {
		if (s == null) {
			return null;
		}
		ByteArrayInputStream b = new ByteArrayInputStream(s);
		Serializable ex = null;
		try {
			ObjectInputStream str = new ObjectInputStream(b);
			ex = (Serializable) str.readObject();
			str.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ex;
	}

	public static void saveVar(String name, Object obj) {
		try {
			if (obj instanceof Serializable) {
				byte[] b = writeSerializable((Serializable) obj);
				DBHelper.getInstance().execute("delete from vars where varname='" + name + "'");
				String sql = " insert into vars (varname,varvalue) values (?,?) ";
				Connection conn = null;
				try {
					conn = DBHelper.getInstance().createConnection();
					PreparedStatement ret = conn.prepareStatement(sql);
					ret.setString(1, name);
					ret.setObject(2, b);
					ret.execute();
				} catch (SQLException e) {
					e.printStackTrace(); // To change body of catch statement
					// use File | Settings | File
					// Templates.
				} finally {
					DBHelper.getInstance().closeConnection(conn);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Serializable getVar(String name) {
		Serializable obj = null;
		String sql = " select varvalue from vars where varname=? ";
		Connection conn = null;
		try {
			conn = DBHelper.getInstance().createConnection();
			PreparedStatement ret = conn.prepareStatement(sql);
			ret.setString(1, name);
			ResultSet rs = ret.executeQuery();
			if (rs.next()) {
				byte[] data = rs.getBytes(1);
				obj = readSerializable(data);
			}
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		} finally {
			DBHelper.getInstance().closeConnection(conn);
		}
		return obj;
	}

	public static Map<String, Serializable> getAllVars() {
		Map<String, Serializable> objret = new HashMap<String, Serializable>();

		String sql = " select varname,varvalue from vars ";
		Connection conn = null;
		try {
			conn = DBHelper.getInstance().createConnection();
			PreparedStatement ret = conn.prepareStatement(sql);
			ResultSet rs = ret.executeQuery();
			if (rs.next()) {
				Serializable obj = null;
				String name = rs.getString(1);
				byte[] data = rs.getBytes(2);
				obj = readSerializable(data);
				objret.put(name, obj);
			}
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		} finally {
			DBHelper.getInstance().closeConnection(conn);
		}
		return objret;
	}

	/*
	 * ~ -> is sytem var which is saved in db $->is a variable in appContext " "
	 * is for escaping % is for parameter loaded from a properties file.
	 */
	public static Object resolveParam(String s) {
		if (s != null && s.startsWith("~")) {
			String v = s.substring(1);
			Object o = org.ptg.admin.AppContext.getInstance().getVar(v);
			return o;
		} else if (s != null && s.startsWith("$")) {
			String v = s.substring(1);
			Object o = ReflectionUtils.getFieldValue(SpringHelper.get("appContext"), v);
			return o;
		} else if (s != null && s.startsWith(" ")) {
			return s.trim();
		} else if (s != null && s.startsWith("%")) {
			String v = s.substring(1);
			Object o = ((AppContext) SpringHelper.get("appContext")).getProperty(v);
			return o;
		}
		return s;
	}

	public static String compileProcessOnServer(String process) {
		// ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		ScriptEngine s = new ScriptEngine();
		s.init();
		List l = new ArrayList();
		l.add(process);
		Object ret = s.runFuntionRaw("compileGraphOnServer", l);
		return (String) ((org.mozilla.javascript.NativeJavaObject) ret).unwrap();
	}

	public static List getStreamsFromProcess(String process) {
		// ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		ScriptEngine s = new ScriptEngine();
		s.init();
		List l = new ArrayList();
		l.add(process);
		List ret = (List) ((org.mozilla.javascript.NativeJavaObject) s.runFuntionRaw("getGraphElements", l)).unwrap();
		return ret;
	}

	public static List getIdsFromProcess(String process) {
		// ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		ScriptEngine s = new ScriptEngine();
		s.init();
		List l = new ArrayList();
		l.add(process);
		List ret = (List) ((org.mozilla.javascript.NativeJavaObject) s.runFuntionRaw("getGraphElementIds", l)).unwrap();
		return ret;
	}

	public static String extractTextFromHtml(String htmlCode) {
		Source c = new Source(htmlCode);
		TextExtractor textExtractor = new TextExtractor(c) {
			@Override
			public boolean excludeElement(StartTag startTag) {
				return false;
			}

		};

		return textExtractor.toString();
	}

	public static String extractCodeFromHtml(String htmlCode) {
		htmlCode = htmlCode.replace("\n", "<br/>");
		Source c = new Source(htmlCode);
		Renderer r = c.getRenderer();
		String code = r.toString();
		if (!windows) {
			code = StringUtils.replace(code, "\\n", SystemUtils.LINE_SEPARATOR);
			code = StringUtils.replace(code, "\\t", "\t");
		}
		code = StringEscapeUtils.unescapeHtml(code);
		return code;
	}

	public static String extractTextFromHtmlTitan(String htmlCode) {
		Source c = new Source(htmlCode);
		Renderer r = c.getRenderer();
		r.setConvertNonBreakingSpaces(false);
		String code = r.toString();
		// replace breaking white space commming from
		// the html code.
		code = StringUtils.replace(code, String.valueOf((char) 160), " ");
		if (!windows) {
			code = StringUtils.replace(code, "\\n", SystemUtils.LINE_SEPARATOR);
			code = StringUtils.replace(code, "\\t", "\t");
		}
		code = StringEscapeUtils.unescapeHtml(code);
		return code;
	}



	public static Object[] getProcessItems(String stream) {
		Exchange exh = CommonUtil.sendAndWait(stream, new String("DummyStartEvent"));
		Object[] items = getExchangeItems(exh);
		return items;
	}


	public static Object[] getExchangeItems(Exchange msg) {
		Object doc = msg.getIn().getBody();
		if (doc == null) {
			return null;
		}
		Object[] items = null;
		if (doc instanceof Object[]) {
			items = (Object[]) doc;
		} else if (doc instanceof Event[]) {
			items = (Event[]) doc;
		} else if (doc instanceof List) {
			items = ((List) doc).toArray();
		} else if (doc instanceof Collection) {
			items = ((Collection) doc).toArray();
		} else if (doc instanceof Map) {
			items = ((Map) doc).values().toArray();
		} else if (doc instanceof Event) {
			items = new Object[] { doc };
		} else if (doc instanceof double[]) {
			items = ArrayUtils.toObject((double[]) doc);
		} else if (doc instanceof int[]) {
			items = ArrayUtils.toObject((int[]) doc);
		} else if (doc instanceof long[]) {
			items = ArrayUtils.toObject((long[]) doc);
		} else if (doc instanceof float[]) {
			items = ArrayUtils.toObject((float[]) doc);
		} else if (doc instanceof short[]) {
			items = ArrayUtils.toObject((short[]) doc);
		} else if (doc instanceof char[]) {
			items = ArrayUtils.toObject((char[]) doc);
		} else if (doc instanceof Object) {
			items = new Object[] { doc };
		}
		return items;
	}





	public static Exchange sendAndWaitLocal(String where, Exchange ex) {
		IProcessor p = org.ptg.processors.ProcessorManager.getInstance().getProcessorFromRoutingTable(where);
		try {
			p.process(ex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ex;
	}

	public static void sendAsyncLocal(final String where, final Exchange ex) {
		ThreadManager mgr = (ThreadManager) SpringHelper.get("threadManager");
		mgr.submit(new Runnable() {
			final String w = where;
			final Exchange m = ex;

			@Override
			public void run() {
				IProcessor p = org.ptg.processors.ProcessorManager.getInstance().getProcessorFromRoutingTable(where);
				try {
					p.process(ex);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static <T> Collection<T> getCollection(T[] objs) {
		int length = objs == null ? 0 : objs.length;
		Collection<T> col = new ArrayList<T>(length);
		if (objs != null) {
			for (T t : objs) {
				col.add(t);
			}
		}
		return col;
	}

	public static <T> List<T> getList(T[] objs) {
		int length = objs == null ? 0 : objs.length;
		List<T> col = new ArrayList<T>(length);
		if (objs != null) {
			for (T t : objs) {
				col.add(t);
			}
		}
		return col;
	}

	public static <T> LinkedList<T> getLinkedList(T[] objs) {
		LinkedList<T> col = new LinkedList<T>();
		for (T t : objs) {
			col.add(t);
		}
		return col;
	}

	public static <T> ArrayList<T> getArrayList(T[] objs) {
		int length = objs == null ? 0 : objs.length;
		ArrayList<T> col = new ArrayList<T>(length);
		if (objs != null) {
			for (T t : objs) {
				col.add(t);
			}
		}
		return col;
	}

	public static <T> Set<T> getSet(T[] objs) {
		int length = objs == null ? 0 : objs.length;
		Set<T> col = new HashSet<T>(length);
		if (objs != null) {
			for (T t : objs) {
				col.add(t);
			}
		}
		return col;
	}

	public static <T> TreeSet<T> getTreeSet(T[] objs) {
		TreeSet<T> col = new TreeSet<T>();
		if (objs != null) {
			for (T t : objs) {
				col.add(t);
			}
		}
		return col;
	}

	public static <T> HashSet<T> getHashSet(T[] objs) {
		int length = objs == null ? 0 : objs.length;
		HashSet<T> col = new HashSet<T>(length);
		if (objs != null) {
			for (T t : objs) {
				col.add(t);
			}
		}
		return col;
	}

	public static DelegateForest<Stream, ConnDef> getGraphFromJson(String name, String graphjson) {
		Graph g = getGraphRepresentationFromJson(name, graphjson);
		return g.getGraph();
	}

	public static Graph getGraphRepresentationFromJson(String name, String graphjson) {
		Graph g = new Graph();
		g.fromJson(name, graphjson);
		return g;
	}

	public static void cleanGraphCompiledCode(String name) {
		String json = getGraphJson(name);
		JSONObject obj = JSONObject.fromObject(json);
		final List<String> l = new ArrayList<String>();
		JSONArray obj2 = (JSONArray) obj.get("data");
		for (Object o : obj2) {
			String id = ((JSONObject) o).get("id").toString();
			String type = ((JSONObject) o).get("type").toString();
			if (!type.equals("textnode")) {
				if (id.contains(".")) {
					id = StringUtils.substringAfterLast(id, ".");
				}
				l.add(id);
			} else {
				// System.out.println();
			}

		}
		forEachFileInDirFile(base + File.separator + "extrabin", new Closure() {
			@Override
			public void execute(Object arg0) {
				File f = (File) arg0;
				String fname = f.getName();
				for (String s : l) {
					if (StringUtils.contains(fname, s)) {
						// System.out.println("Now deleting: " + f.getName());
						f.delete();
						break;
					}
				}

			}
		}, false);
	}

	public static List<Graph> forEachGraph(final Predicate p) {
		final List<Graph> graphs = new ArrayList<Graph>();
		final List<Graph> ret = new ArrayList<Graph>();
		DBHelper.getInstance().forEach("select name,graph from graphs", new IStateAwareClosure() {
			@Override
			public void init() {
			}

			@Override
			public void finish() {
			}

			@Override
			public void execute(ResultSet rs) throws SQLException {
				String name = rs.getString("name");
				String json = rs.getString("graph");
				final Map<String, Object> l = getGraphObjectsFromJson(name, json);
				Graph g = new Graph();
				g.setName(name);
				g.fromObjectMap(l, null);
				if (p.apply(g)) {
					ret.add(g);
				}
			}
		});
		return ret;
	}

	public static List<Graph> forEachGraphDo(final List<Graph> graphs, final Predicate p) {
		final List<Graph> ret = new ArrayList<Graph>();
		for (Graph g : graphs) {
			if (p.apply(g)) {
				ret.add(g);
			}
		}
		return ret;
	}

	public static List<Graph> buildGraphs() {
		final List<Graph> ret = new ArrayList<Graph>();
		DBHelper.getInstance().forEach("select name,graph from graphs  ", new IStateAwareClosure() {
			@Override
			public void init() {
				// TODO Auto-generated method stub

			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub

			}

			@Override
			public void execute(ResultSet rs) throws SQLException {
				String name = rs.getString("name");
				String json = rs.getString("graph");
				final Map<String, Object> l = getGraphObjectsFromJson(name, json);
				Graph g = new Graph();
				g.setName(name);
				g.fromObjectMap(l, null);

				ret.add(g);
			}
		});
		return ret;
	}

	public static Graph buildGraph(String name) {
		final List<Graph> ret = new ArrayList<Graph>();
		DBHelper.getInstance().forEach("select name,graph from graphs  where name=\'" + name + "\'", new IStateAwareClosure() {
			@Override
			public void init() {
			}

			@Override
			public void finish() {
			}

			@Override
			public void execute(ResultSet rs) throws SQLException {
				String name = rs.getString("name");
				String json = rs.getString("graph");
				final Map<String, Object> l = getGraphObjectsFromJson(name, json);
				Graph g = new Graph();
				g.setName(name);
				g.fromObjectMap(l, null);

				ret.add(g);
			}

		});
		if (ret.size() == 1) {
			return ret.get(0);
		} else if (ret.size() > 1) {
			return ret.get(0);
		} else {
			return null;
		}
	}

	public static Map<String, Object> getGraphObjectsFromJson(String name, String json) {
		// System.out.println("Now processing Graph: " + name);
		final Map<String, Object> l = new HashMap<String, Object>();
		JSONObject obj = null;
		JSONArray obj2 = null;
		try {
			json = json.replace("NaN", "\"0\"");
			json = json.replace("Infinity", "\"0\"");
			obj = JSONObject.fromObject(json);
			obj2 = (JSONArray) obj.get("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getGraphObjectsFromJsonArray(l, obj2);
	}

	public static Map<String, Object> getGraphObjectsFromJsonData(String name, String json) {
		// System.out.println("Now processing Graph: " + name);
		final Map<String, Object> l = new HashMap<String, Object>();
		json = StringUtils.replace(json, "NaN", "null");
		json = json.replace("Infinity", "\"0\"");
		JSONArray obj = JSONArray.fromObject(json);
		return getGraphObjectsFromJsonArray(l, obj);
	}

	public static Map<String, Object> getRawGraphObjectsFromJsonData(String name, String json) {
		// System.out.println("Now processing Graph: " + name);
		final Map<String, Object> l = new HashMap<String, Object>();
		json = StringUtils.replace(json, "NaN", "null");
		json = json.replace("Infinity", "\"0\"");
		JSONArray obj = JSONArray.fromObject(json);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Object o : obj.toArray(new JSONObject[0])) {
			JSONObject j = (JSONObject) o;
			map.put(j.getString("id"), j);
		}
		return map;
	}

	public static Map<String, Object> getGraphObjectsFromJsonArray(final Map<String, Object> l, JSONArray obj2) {
		for (Object bo : obj2) {
			JSONObject o = (JSONObject) bo;
			String idstr = (String) o.get("id");
			if (idstr == null) {
				System.out.println("oid is null");
				continue;
			}
			String id = idstr.toString();
			String type = o.get("type").toString();
			if (type.equals("stream")) {
				Stream s = CommonUtil.getStreamDefinitionFromJsonObject(o);
				l.put(s.getName(), s);
			} else if (type.equals("node")) {
				ProcessorDef proc = CommonUtil.getProcDefinitionFromJsonObject(o);
				l.put(proc.getName(), proc);
			} else if (type.equals("event")) {
				EventDefinition ed = CommonUtil.getEventDefinitionFromJsonObject(o);
				l.put(ed.getType(), ed);
			} else if (type.equals("connection")) {
				ConnDef def = CommonUtil.getConnDefinitionFromJsonObject(o);
				l.put(def.getId(), def);
			} else if (type.equals("group")) {
				Group ed = CommonUtil.getGroupDefinitionFromJsonObject(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("classobj")) {
				FunctionPoint ed = CommonUtil.getFunctionPointFromJsonObject(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("paramobj")) {
				FunctionPoint ed = CommonUtil.getFunctionPointFromJsonObject(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("functionobj")) {
				FunctionPoint ed = CommonUtil.getFunctionPointFromJsonObject(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("ColDef")) {
				ColDef ed = CommonUtil.getColDefinitionFromJsonObject(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("sqlobj")) {
				SQLObj ed = CommonUtil.getSqlDefinitionFromJsonObject(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("TableDef")) {
				TableDef ed = CommonUtil.getTableDefDefinitionFromJsonObject(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("module")) {
				FunctionPoint ed = CommonUtil.getFunctionPointFromJsonObject(o);
				ed.setType("moduleobj");
				System.out.println(ed.getId());
				ed.setXref(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("AnonDef")) {// work here for rest of the
				// items sumit
				AnonDefObj ed = CommonUtil.getAnonDefObjFromJsonObject(o);
				ed.setType("AnonDef");
				ed.setXref(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("Port")) {
				PortObj ed = CommonUtil.getPortFromJsonObject(o);
				ed.setType("Port");
				ed.setXref(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("Layer")) {
				LayerObj ed = CommonUtil.getLayerFromJsonObject(o);
				ed.setType("Layer");
				ed.setXref(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("TypeDef")) {
				TypeDefObj ed = CommonUtil.getTypeDefObjFromJsonObject(o);
				ed.setType("TypeDef");
				ed.setXref(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("Step")) {
				StepObj ed = CommonUtil.getStepObjFromJsonObject(o);
				ed.setType("Step");
				ed.setXref(o);
				l.put(ed.getId(), ed);
			} else if (type.equals("ShapeShape")) {
				Shape ed = CommonUtil.getShapeObjFromJsonObject(o);
				ed.setType("Shape");
				ed.setXref(o);
				l.put(ed.getId(), ed);
			}
			else {
				String oid = o.getString("id");
				if (o.containsKey("type")) {
					if (o.getString("type").equals("ToDoEvent")) {
						FunctionPoint ed = new FunctionPoint();
						ed.setId(oid);
						ed.setType("ToDoObj");
						ed.setXref(o);
						l.put(oid, ed);
					} else if (o.getString("type").equals("org.ptg.util.events.ActivityEvent")) {
						FunctionPoint ed = new FunctionPoint();
						ed.setId(oid);
						ed.setType("ActivityObj");
						ed.setXref(o);
						l.put(oid, ed);
					} else {
						l.put(oid, o);
					}
				} else {
					l.put(oid, o);
				}
			}
		}
		return l;
	}

	private static TypeDefObj getTypeDefObjFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.put("x", jsonObject.get("normalizedx"));
		jsonObject.put("y", jsonObject.get("normalizedy"));

		/*
		 * jsonObject.remove("x");// remove points jsonObject.remove("y");//
		 * remove points jsonObject.remove("r");// remove points
		 * jsonObject.remove("b");// remove points
		 */jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		TypeDefObj bean = (TypeDefObj) JSONObject.toBean(jsonObject, TypeDefObj.class, classMap);
		return bean;
	}

	private static PortObj getPortFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.put("x", jsonObject.get("normalizedx"));
		jsonObject.put("y", jsonObject.get("normalizedy"));

		/*
		 * jsonObject.remove("x");// remove points jsonObject.remove("y");//
		 * remove points jsonObject.remove("r");// remove points
		 * jsonObject.remove("b");// remove points
		 */jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		if (jsonObject.containsKey("portval")) {
			if (jsonObject.get("portval") instanceof JSONArray) {
				JSONArray ar = (JSONArray) jsonObject.get("portval");
				Object[] rr = ar.toArray();
				String s = Arrays.toString(rr);
				jsonObject.put("portval", s);

			}
		}
		PortObj bean = (PortObj) JSONObject.toBean(jsonObject, PortObj.class, classMap);
		return bean;
	}

	private static LayerObj getLayerFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.remove("x");// remove points
		jsonObject.remove("y");// remove points
		jsonObject.remove("r");// remove points
		jsonObject.remove("b");// remove points
		jsonObject.remove("shape");// remove points
		jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		LayerObj bean = (LayerObj) JSONObject.toBean(jsonObject, LayerObj.class, classMap);
		return bean;
	}

	private static AnonDefObj getAnonDefObjFromJsonObject(JSONObject jsonObject) {
		Map classMap = new HashMap();
		jsonObject.remove("textual");// from node
		jsonObject.remove("c");// from stream
		jsonObject.put("x", jsonObject.get("normalizedx"));
		jsonObject.put("y", jsonObject.get("normalizedy"));
		/*
		 * jsonObject.remove("x");// remove points jsonObject.remove("y");//
		 * remove points jsonObject.remove("r");// remove points
		 * jsonObject.remove("b");// remove points
		 */jsonObject.remove("icon");// remove points
		jsonObject.remove("normalizedx");// remove points
		jsonObject.remove("normalizedy");// remove points
		// System.out.println("Now processing: " + jsonObject.getString("id"));
		AnonDefObj bean = (AnonDefObj) JSONObject.toBean(jsonObject, AnonDefObj.class, classMap);
		if (jsonObject.has("configItems")) {
			bean.setConfigItems(jsonObject.getString("configItems"));
		}
		return bean;
	}

	public static boolean allTrue(Collection<Boolean> truthTable) {
		boolean ret = truthTable.size() > 0;
		for (Boolean b : truthTable) {
			ret &= b.booleanValue();
		}
		return ret;
	}

	public static ExecuteResultHandler asyncExec(String c, int time) throws ExecuteException, IOException {
		CommandLine cmdLine = CommandLine.parse(c);
		DefaultExecutor executor = new DefaultExecutor();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(time);
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		executor.execute(cmdLine, resultHandler);
		return resultHandler;
	}

	public static int syncExec(String c, int time) throws ExecuteException, IOException {
		CommandLine cmdLine = CommandLine.parse(c);
		DefaultExecutor executor = new DefaultExecutor();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(time);
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		return executor.execute(cmdLine);
	}

	public static boolean compileProcessDef(String data) {
		try {
			ProcessorDef ed = CommonUtil.getProcDefinitionFromJsonData(data);
			compileProcessorDefinition(ed);
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean compileProcessorDefinition(ProcessorDef ed) {
		if (ed != null) {
			try {
				ProcessorManager.getInstance().deleteProcessorDef(ed);
				ProcessorManager.getInstance().saveProcessorDef(ed);
				ProcessorManager.getInstance().registerProcessorType(ed);
				org.ptg.admin.AppContext.getInstance().incrStat("ProcDef");
				return true;
			} catch (GenericException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;

	}

	public static boolean setJsonStreamDefinition(String data) {
		try {
			final Stream ed = CommonUtil.getStreamDefinitionFromJsonData(data);
			compileStreamDefinition(ed);
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean compileStreamDefinition(final Stream ed) {
		return compileStreamDefinition(ed, true);
	}

	public static boolean compileStreamDefinition(final Stream ed, final boolean save) {
		if (ed != null) {
			try {
				if (save) {
					StreamManager.getInstance().deleteStream(ed);
					StreamManager.getInstance().saveStream(ed);
				}
				StreamManager.getInstance().getStreamTransformer(ed, true);
				// ProcessorManager.getInstance().attach(ed.getName(),
				// ed.getProcessor());
				/* now create */
				final String directfrm = "direct:" + ed.getName();
				final String frmOut = "direct:" + ed.getName() + "-out";
				ProcessorManager p = ProcessorManager.getInstance();
				String name = ed.getName();
				String processor = ed.getProcessor();
				final IProcessor iprocessor = p.attach(name, processor);
				RouteBuilder processorRouteBuilder = new RouteBuilder() {
					@Override
					public void configure() throws Exception {
						if (ed != null) {
							String name = "";

							if (iprocessor instanceof ProxyProcessor) {
								name = ((ProxyProcessor) iprocessor).getInnerName();
							} else {
								name = iprocessor.getName();
							}
							/* sumit debug this commented now uncomment later */
							// WebStartProcess.getInstance().getRoutingEngine().register(name,
							// iprocessor);
							from(directfrm).process(iprocessor).to(frmOut).setId(name);
						}
					}
				};
				WebStartProcess.getInstance().getRoutingEngine().addRoutes(processorRouteBuilder);
				org.ptg.admin.AppContext.getInstance().incrStat("JsonStreamDef");
				return true;
			} catch (GenericException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public static boolean setJsonConnDefinition(String data) {
		try {
			ConnDef ed = CommonUtil.getConnDefinitionFromJsonData(data);
			compileConnDefinition(ed);
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean compileConnDefinition(ConnDef ed) {
		try {
			if (CommonUtil.saveConnection(ed) == true) {
				org.ptg.admin.AppContext.getInstance().incrStat("ConnDef");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}




	public static boolean compileGroupDefinition(Group ed) {
		if (ed != null) {

		}
		return true;
	}

	public static Map<String, String> compileOrhpahDefinition(JSONObject ed, Map param) {

		Map<String, String> toRet = new HashMap<String, String>();
		if (ed != null) {
			for (Map.Entry<String, GraphObjectCompiler> c : graphObjectCompilers.entrySet()) {
				if (ed.containsKey("gtype")) {
					String type = ed.getString("gtype");
					if (type != null && type.equals(c.getKey())) {
						String code = c.getValue().compile(param);
						String name = c.getValue().getName();
						toRet.put(name, code);

					}
				}
			}
		}
		return toRet;
	}

	public static ProcessorDef getDefaultProcessor(String name) {
		ProcessorDef def = new ProcessorDef();
		def.setClz("org.ptg.processors.NullProcessor");
		def.setName(name);
		return def;
	}

	public static void empty() {

	}

	public static Object instantiateMappingGraph(String name, String mappingtype, Map<String, String> params) {
		FPGraph g = buildMappingGraph(name);
		if (mappingtype == null) {
			mappingtype = "FreeSpring";
		}
		Object cl = compileMappingGraph(name, g, mappingtype, params);
		return cl;
	}

	public static Object compileMappingGraph2(String name, String codein, String mappingtype, Map<String, String> params) throws Exception {
		return compileMappingGraph2("extrajava", name, codein, mappingtype, params);
	}

	public static Object compileMappingGraph3(String folder, String name, String codein, String codein2, String mappingtype, Map<String, String> params) throws Exception {
		String path = base + File.separator + "uploaded" + File.separator + folder + File.separator;
		String temp = codein;
		String temp2 = codein2;
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "Mapping_" + name);
		m.put("code1", temp);
		m.put("code2", temp2);
		if (params != null) {
			for (Map.Entry<String, String> p : params.entrySet()) {
				m.put(p.getKey(), p.getValue());
			}
		}
		String code = VelocityHelper.burnTemplate(m, "mapping_" + mappingtype + ".vm").toString();
		String dest = "Mapping_" + name + ".java";
		code = TitanCompiler.compile(code);
		Object cl = null;
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), code.getBytes());
		Class cls = CommonUtil.compileJavaToClassFresh(path, "Mapping_" + name);
		cl = cls.newInstance();
		return cl;
	}

	public static Object compileMappingGraph2(String folder, String name, String codein, String mappingtype, Map<String, String> params) throws Exception {
		String path = base + File.separator + "uploaded" + File.separator + folder + File.separator;
		String temp = codein;
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "Mapping_" + name);
		m.put("code", temp);
		if (params != null) {
			for (Map.Entry<String, String> p : params.entrySet()) {
				m.put(p.getKey(), p.getValue());
			}
		}
		String code = VelocityHelper.burnTemplate(m, "mapping_" + mappingtype + ".vm").toString();
		String dest = "Mapping_" + name + ".java";
		code = TitanCompiler.compile(code);
		System.out.println("Code: "+code);
		Object cl = null;
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), code.getBytes());
		Class cls = CommonUtil.compileJavaToClassFresh(path, "Mapping_" + name);
		cl = cls.newInstance();
		return cl;
	}

	public static String getMappingGraphCode(String name, String codein, String mappingtype, Map<String, String> params) {
		return getMappingGraphCode("extrajava", name, codein, mappingtype, params);
	}

	public static String getMappingGraphCode(String folder, String name, String codein, String mappingtype, Map<String, String> params) {
		String path = base + File.separator + "uploaded" + File.separator + folder + File.separator;
		String temp = codein;
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "Mapping_" + name);
		m.put("code", temp);
		if (params != null) {
			for (Map.Entry<String, String> p : params.entrySet()) {
				m.put(p.getKey(), p.getValue());
			}
		}
		String code = VelocityHelper.burnTemplate(m, "mapping_" + mappingtype + ".vm").toString();
		return code;
	}

	public static Object compileMappingGraph(String name, FPGraph g, String mappingtype, Map<String, String> params) {
		String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;
		SimpleMapperCompiler c = new SimpleMapperCompiler();
		String temp = c.compile(name, g, null);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "Mapping_" + name);
		m.put("code", temp);
		if (params != null) {
			for (Map.Entry<String, String> p : params.entrySet()) {
				m.put(p.getKey(), p.getValue());
			}
		}
		String code = VelocityHelper.burnTemplate(m, "mapping_" + mappingtype + ".vm").toString();
		String dest = "Mapping_" + name + ".java";
		code = TitanCompiler.compile(code);
		Object cl = null;
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), code.getBytes());
			Class cls = CommonUtil.compileJavaToClassFresh(path, "Mapping_" + name);
			cl = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cl;
	}

	public static Object compileDFStateMachineGraph(String name, DFStateMachineCompiler c, String mappingtype, Map<String, String> params) {
		String path = base + File.separator + "uploaded" + File.separator + "extrajava" + File.separator;
		String temp = c.compile();
		String vars = c.getVars();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "DFStateMachine_" + name);
		m.put("code", temp);
		m.put("states", vars);
		if (params != null) {
			for (Map.Entry<String, String> p : params.entrySet()) {
				m.put(p.getKey(), p.getValue());
			}
		}
		String code = VelocityHelper.burnTemplate(m, "mapping_" + mappingtype + ".vm").toString();
		String dest = "DFStateMachine_" + name + ".java";
		code = TitanCompiler.compile(code);
		Object cl = null;
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), code.getBytes());
			Class cls = CommonUtil.compileJavaToClassFresh(path, "DFStateMachine_" + name);
			cl = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cl;
	}

	public static Object instantiateMappingGraph2(String name, String mappingtype, Map<String, String> params) throws Exception {
		FPGraph2 g = buildMappingGraph2(name);
		return instantiateMappingGraph2(g, mappingtype, params);
	}

	public static FPGraph2 getMappingGraph(String name, String graphjson) {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return g;
	}

	public static Object instantiateMappingGraph2(String name, String graphjson, String mappingtype, Map<String, String> params) throws Exception {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return instantiateMappingGraph2(g, mappingtype, params);
	}

	public static Object instantiateMappingGraph2(FPGraph2 g, String mappingtype, Map<String, String> params) throws Exception {
		Map<String, FPGraph> gs = g.getSubgraphs();
		// System.out.println(gs.size());
		StringBuilder temp = new StringBuilder();
		SimpleMapperCompiler c = null;
		List<FPGraph> vals = new ArrayList<FPGraph>();
		vals.addAll(gs.values());
		Collections.sort(vals, new Comparator<FPGraph>() {
			@Override
			public int compare(FPGraph arg0, FPGraph arg1) {
				return arg0.getOrder() - arg1.getOrder();
			}
		});

		/********* MAIN ******/
		FPGraph fmain = g.getMainGraph();
		c = new SimpleMapperCompiler();
		temp.append(SystemUtils.LINE_SEPARATOR + "/****" + fmain.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
		temp.append(c.compile(fmain.getName(), fmain, g));
		temp.append(SystemUtils.LINE_SEPARATOR + "/**** END " + fmain.getName() + " END ****/" + SystemUtils.LINE_SEPARATOR);
		/************* MAIN END *******/
		for (FPGraph gg : vals/* vals is sorted collection */) {
			if (gg.getGtype() != null && gg.getGtype().equals("values")) {
				c = new SimpleMapperCompiler();
				temp.append(c.compile(gg.getName(), gg, g));
				String stemp = "{";
				Collection<FunctionPoint> fpp = gg.getFunctionPoints().values();
				Iterator<FunctionPoint> fi = fpp.iterator();
				for (int k = 0; k < fpp.size(); k++) {
					FunctionPoint fp = fi.next();
					stemp += fp.getId();
					if (k < fpp.size() - 1) {
						stemp += ",";
					}
				}
				stemp += "};";
				if (gg.getOut() != null) {
					FunctionPoint fo = g.getFunctionPoints().get(gg.getOut().getTo());
					String objtype = "Object  []";
					if (fo != null) {
						objtype = fo.getDataType();
					}
					stemp = gg.getOut().getTo() + " = new " + objtype + "  " + stemp;
				} else {
					stemp = "obj" + getRandomString(4) + " = new Object [ ] " + stemp;
				}
				temp.append(stemp);
			} else {
				c = new SimpleMapperCompiler();
				ConnDef in = gg.getIn();
				if (in != null) {
					String cond = in.getConnCond();
					String ccond = null;
					if (cond != null && cond.length() > 1) {
						ccond = cond;
					} else {
						String s = in.getFrom();
						FunctionPoint fp = g.getFunctionPoints().get(s);
						if (fp != null) {
							String ss = fp.getDataType();
							try {
								Class cls = Class.forName(ss);
								if (cls != null) {
									if (cls.isAssignableFrom(java.util.Map.class) && !cls.equals(Object.class)) {
										ccond = "Map.Entry item: " + s + ".getEntrySet()";
									} else if (cls.isAssignableFrom(java.util.Collection.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else if (cls.isAssignableFrom(java.util.List.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else {
										ccond = " " + ss + " item : org.ptg.util.CommonUtil.getCollection(new " + ss + "[]{" + s + "}) ";
									}
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					temp.append(gg.getGtype() + " ( " + ccond + " ) { " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*" + gg.getGtype() + " ( " + "Null Condition" + " ){*/  " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
				temp.append(c.compile(g.getName(), gg, g));
				if (in != null) {
					temp.append("} " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*}*/ " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
			}
		}
		if (mappingtype == null) {
			mappingtype = "FreeSpring";
		}
		return compileMappingGraph2(g.getName(), temp.toString(), mappingtype, params);
	}

	public static Object instantiateMappingGraph(String name, String graphjson, String mappingtype, Map<String, String> params) {
		FPGraph g = new FPGraph();
		g.fromGraphJson(name, graphjson);
		if (mappingtype == null) {
			mappingtype = "FreeSpring";
		}
		Object cl = compileMappingGraph(name, g, mappingtype, params);
		return cl;
	}

	public static Object instantiateDFStateMachineGraph(String name, String mappingtype, Map<String, String> params) {
		String graphjson = DBHelper.getInstance().getString("select graph from graphs  where name=\'" + name + "\'");
		DFStateMachineCompiler g = new DFStateMachineCompiler();
		g.fromGraphJson(name, graphjson);
		if (mappingtype == null) {
			mappingtype = "DFStateMachine";
		}
		Object cl = compileDFStateMachineGraph(name, g, mappingtype, params);
		return cl;
	}

	public static Object instantiateDFStateMachineGraph(String name, String graphjson, String mappingtype, Map<String, String> params) {
		DFStateMachineCompiler g = new DFStateMachineCompiler();
		g.fromGraphJson(name, graphjson);
		if (mappingtype == null) {
			mappingtype = "DFStateMachine";
		}
		Object cl = compileDFStateMachineGraph(name, g, mappingtype, params);
		return cl;
	}

	public static FPGraph2 buildDesignMappingGraph2(String name) {
		return buildMappingGraph2(name, null, "pageconfig", "name", "graph");
	}

	public static FPGraph2 buildMappingGraph2(String name) {
		return buildMappingGraph2(name, null, "graphs", "name", "graph");
	}

	public static FPGraph2 buildMappingGraph2(String name, final Map<String, String> replacement) {
		return buildMappingGraph2(name, replacement, "graphs", "name", "graph");
	}

	public static FPGraph2 buildMappingGraph2(String name, final Map<String, String> replacement, final String table, final String nameCol, final String graphCol) {
		final List<FPGraph2> ret = new ArrayList<FPGraph2>();
		DBHelper.getInstance().forEach("select " + nameCol + "," + graphCol + " from " + table + "  where " + nameCol + "=\'" + name + "\'", new IStateAwareClosure() {
			@Override
			public void init() {
				// TODO Auto-generated method stub

			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub

			}

			@Override
			public void execute(ResultSet rs) throws SQLException {
				String name = rs.getString(nameCol);
				String json = rs.getString(graphCol);
				if (replacement != null) {
					for (Map.Entry<String, String> en : replacement.entrySet()) {
						json = StringUtils.replace(json, en.getKey(), en.getValue());
					}
				}
				final Map<String, Object> l = getGraphObjectsFromJson(name, json);
				FPGraph2 g = new FPGraph2();
				g.setName(name);
				g.fromObjectMap(l, null);
				ret.add(g);
			}

		});
		if (ret.size() == 1) {
			return ret.get(0);
		} else if (ret.size() > 1) {
			return ret.get(0);
		} else {
			return null;
		}
	}

	public static <T> T getObj(T s) {
		return s;
	}

	public static FPGraph buildMappingGraph(String name) {
		final List<FPGraph> ret = new ArrayList<FPGraph>();
		DBHelper.getInstance().forEach("select name,graph from graphs  where name=\'" + name + "\'", new IStateAwareClosure() {
			@Override
			public void init() {
				// TODO Auto-generated method stub

			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub

			}

			@Override
			public void execute(ResultSet rs) throws SQLException {
				String name = rs.getString("name");
				String json = rs.getString("graph");
				final Map<String, Object> l = getGraphObjectsFromJson(name, json);
				FPGraph g = new FPGraph();
				g.setName(name);
				g.fromObjectMap(l, null);
				ret.add(g);
			}

		});
		if (ret.size() == 1) {
			return ret.get(0);
		} else if (ret.size() > 1) {
			return ret.get(0);
		} else {
			return null;
		}
	}

	public static void println(Object o) {
		// System.out.println(o);
	}

	public static List<Element> getHtmlElements(String s) {
		Source source = new Source(s);
		List<Element> elementList = source.getAllElements();
		return elementList;
	}

	public static void exit(int code) {
		System.exit(code);
	}

	public static void deleteGraphItem(String name, String type) {
		if ("graph".equalsIgnoreCase(type)) {
			String delete = "delete from graphs where name='" + name + "'";
			DBHelper.getInstance().executeUpdate(delete);
		} else if ("stream".equalsIgnoreCase(type)) {
			StreamManager.getInstance().deleteStream(name);
		} else if ("event".equalsIgnoreCase(type)) {
			EventDefinition ed = EventDefinitionManager.getInstance().getEventDefinition(name);
			EventDefinitionManager.getInstance().deleteEventDefinition(ed);
		} else if ("processor".equalsIgnoreCase(type)) {
			ProcessorManager.getInstance().deleteProcessorDef(name);
		} else if ("connection".equalsIgnoreCase(type)) {
			ProcessorManager.getInstance().deleteProcessorDef("CondProc" + name);
			StreamManager.getInstance().deleteStream("CondStream" + name);

		}
	}

	public static void unzip(String file, String where) {
		Expand ex = new Expand();
		ex.setSrc(new File(file));
		ex.setDest(new File(where));
		ex.execute();
	}

	public static void unJar(String file, String where) {
		unzip(file, where);
	}

	public static void mkdir(String dir) {
		Mkdir mk = new Mkdir();
		mk.setDir(new File(dir));
		mk.execute();
	}

	public static void jar(String baseDir, String dir, String tofile) {
		Project p = new Project();
		p.setProperty("ant.home", baseDir);
		p.setBaseDir(new File(baseDir));
		Zip mk = new Zip();
		mk.setProject(p);
		mk.setBasedir(new File(dir));
		mk.setDestFile(new File(tofile));
		mk.setCompress(false);
		mk.execute();
	}

	public static void zip(String baseDir, String dir, String tofile) {
		Project p = new Project();
		p.setProperty("ant.home", baseDir);
		p.setBaseDir(new File(baseDir));
		Zip mk = new Zip();
		mk.setProject(p);
		mk.setBasedir(new File(dir));
		mk.setDestFile(new File(tofile));
		mk.setCompress(true);
		mk.execute();
	}

	public static void copyDir(String fromdir, String todir) {
		try {
			File fl = new File(todir);
			FileUtils.copyDirectory(new File(fromdir), fl);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void deleteDir(String cleandir) {
		try {
			File fl = new File(cleandir);
			if (fl.exists()) {
				FileUtils.cleanDirectory(fl);
				FileUtils.forceDelete(fl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void moveDir(String fromdir, String todir) {
		try {
			File fl = new File(todir);
			if (fl.exists()) {
				FileUtils.cleanDirectory(fl);
				FileUtils.forceDelete(fl);
			}
			FileUtils.moveDirectory(new File(fromdir), fl);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String getBaseUrl(String in) {
		try {
			URL a = new URL(in);
			return a.getProtocol() + "://" + a.getHost() + (a.getPort() == 80 ? "" : ":" + a.getPort()) + "/";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static EventDefinition generateEventDefinitionFromEvent(Event evt) {
		EventDefinition e = new EventDefinition();
		e.setType(evt.getClass().getName());
		e.setEventStore(evt.getClass().getSimpleName() + "_store");
		Field[] flds = evt.getClass().getDeclaredFields();
		int index = 1;
		for (Field fld : flds) {
			String name = fld.getName();
			String type = fld.getType().getName();
			PropertyDefinition p1 = CommonUtil.getPropertyDefinition(name, type, index, 1);
			e.getProps().put("" + p1.getIndex(), p1);
			index++;
		}
		EventDefinitionManager.getInstance().deleteEventDefinition(e);
		EventDefinitionManager.getInstance().saveEvent(e);
		return e;
	}

	public static String capitalize(String s) {
		return WordUtils.capitalize(s);
	}

	public static String uncapitalize(String s) {
		return WordUtils.uncapitalize(s);
	}

	public static void centerFrame(JFrame frame) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		// frame.setSize(screenWidth / 2, screenHeight / 2);
		frame.setLocation(screenWidth / 4, screenHeight / 4);
	}

	public static void centerDlg(JDialog frame) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		// frame.setSize(screenWidth / 2, screenHeight / 2);
		frame.setLocation(screenWidth / 4, screenHeight / 4);
	}

	public static void copyFile(String fromdir, String todir) {
		try {
			File fl = new File(todir);
			if (fl.exists()) {
				fl.delete();
			}
			FileUtils.copyFile(new File(fromdir), fl);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static boolean escapeJavaScriptInSql() {
		if (ctx.getSqlServer().equals("sqllite") || ctx.getSqlServer().equals("h2")) {
			return false;
		}
		return true;

	}

	public static Object instantiateSQLMappingGraph(String name, String graphjson, String mappingtype, Map<String, String> params) throws Exception {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return instantiateSQLMappingGraph(g, mappingtype, params);
	}

	public static Object instantiateSQLMappingGraph(FPGraph2 g, String mappingtype, Map<String, String> params) throws Exception {
		Map<String, FPGraph> gs = g.getSubgraphs();
		// System.out.println(gs.size());
		StringBuilder temp = new StringBuilder();
		SimpleMapperCompiler c = null;
		List<FPGraph> vals = new ArrayList<FPGraph>();
		vals.addAll(gs.values());
		Collections.sort(vals, new Comparator<FPGraph>() {
			@Override
			public int compare(FPGraph arg0, FPGraph arg1) {
				return arg0.getOrder() - arg1.getOrder();
			}
		});

		/********* MAIN ******/
		FPGraph fmain = g.getMainGraph();
		c = new SimpleMapperCompiler();
		temp.append(SystemUtils.LINE_SEPARATOR + "/****" + fmain.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
		temp.append(c.compile(fmain.getName(), fmain, g));
		temp.append(SystemUtils.LINE_SEPARATOR + "/**** END " + fmain.getName() + " END ****/" + SystemUtils.LINE_SEPARATOR);
		/************* MAIN END *******/
		for (FPGraph gg : vals/* vals is sorted collection */) {
			if (gg.getGtype() != null && gg.getGtype().equals("values")) {
				c = new SimpleMapperCompiler();
				temp.append(c.compile(gg.getName(), gg, g));
				String stemp = "{";
				Collection<FunctionPoint> fpp = gg.getFunctionPoints().values();
				Iterator<FunctionPoint> fi = fpp.iterator();
				for (int k = 0; k < fpp.size(); k++) {
					FunctionPoint fp = fi.next();
					stemp += fp.getId();
					if (k < fpp.size() - 1) {
						stemp += ",";
					}
				}
				stemp += "};";
				if (gg.getOut() != null) {
					FunctionPoint fo = g.getFunctionPoints().get(gg.getOut().getTo());
					String objtype = "Object [] ";
					if (fo != null) {
						objtype = fo.getDataType();
					}
					stemp = gg.getOut().getTo() + " = new " + objtype + "  " + stemp;
				} else {
					stemp = "Object [ ] obj" + getRandomString(4) + " = new Object [ ] " + stemp;
				}
				temp.append(stemp);
			} else {
				c = new SimpleMapperCompiler();
				ConnDef in = gg.getIn();
				if (in != null) {
					String cond = in.getConnCond();
					String ccond = null;
					if (cond != null && cond.length() > 1) {
						ccond = cond;
					} else {
						String s = in.getFrom();
						FunctionPoint fp = g.getFunctionPoints().get(s);
						if (fp != null) {
							String ss = fp.getDataType();
							try {
								Class cls = Class.forName(ss);
								if (cls != null) {
									if (cls.isAssignableFrom(java.util.Map.class) && !cls.equals(Object.class)) {
										ccond = "Map.Entry item: " + s + ".getEntrySet()";
									} else if (cls.isAssignableFrom(java.util.Collection.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else if (cls.isAssignableFrom(java.util.List.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else {
										ccond = " " + ss + " item : org.ptg.util.CommonUtil.getCollection(new " + ss + "[]{" + s + "}) ";
									}
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					temp.append("for ( " + ccond + " ) { " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*for ( " + "Null Condition" + " ){*/  " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
				temp.append(c.compile(g.getName(), gg, g));
				if (in != null) {
					temp.append("} " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*}*/ " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
			}
		}
		if (mappingtype == null) {
			mappingtype = "FreeSpring";
		}
		return compileMappingGraph2(g.getName(), temp.toString(), mappingtype, params);
	}

	public static Object instantiateJobMappingGraph(String name, String graphjson, String mappingtype, Map<String, String> params) throws Exception {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return instantiateJobMappingGraph(g, mappingtype, params);
	}

	public static Object instantiateJobMappingGraph(FPGraph2 g, String mappingtype, Map<String, String> params) throws Exception {
		Map<String, FPGraph> gs = g.getSubgraphs();
		// System.out.println(gs.size());
		StringBuilder temp = new StringBuilder();
		SimpleTodoCompiler c = null;
		List<FPGraph> vals = new ArrayList<FPGraph>();
		vals.addAll(gs.values());
		Collections.sort(vals, new Comparator<FPGraph>() {
			@Override
			public int compare(FPGraph arg0, FPGraph arg1) {
				return arg0.getOrder() - arg1.getOrder();
			}
		});

		/********* MAIN ******/
		FPGraph fmain = g.getMainGraph();
		c = new SimpleTodoCompiler();

		JobGraphVisitor<CompilePath> visitor = new JobGraphVisitor<CompilePath>();
		String query = "select graphconfig from " + "graphs" + " where name=\"" + g.getName() + "\"";
		String config = DBHelper.getInstance().getString(query);
		Map configMap = CommonUtil.getConfigFromJsonData(config);
		visitor.setConfig(configMap);
		c.setVisitor(visitor);
		temp.append(SystemUtils.LINE_SEPARATOR + "/****" + fmain.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
		temp.append(c.compile(fmain.getName(), fmain, g));
		temp.append(SystemUtils.LINE_SEPARATOR + "/**** END " + fmain.getName() + " END ****/" + SystemUtils.LINE_SEPARATOR);
		/************* MAIN END *******/
		for (FPGraph gg : vals/* vals is sorted collection */) {
			if (gg.getGtype() != null && gg.getGtype().equals("values")) {
				c = new SimpleTodoCompiler();
				c.setVisitor(visitor);
				temp.append(c.compile(gg.getName(), gg, g));
				String stemp = "{";
				Collection<FunctionPoint> fpp = gg.getFunctionPoints().values();
				Iterator<FunctionPoint> fi = fpp.iterator();
				for (int k = 0; k < fpp.size(); k++) {
					FunctionPoint fp = fi.next();
					stemp += fp.getId();
					if (k < fpp.size() - 1) {
						stemp += ",";
					}
				}
				stemp += "};";
				if (gg.getOut() != null) {
					FunctionPoint fo = g.getFunctionPoints().get(gg.getOut().getTo());
					String objtype = "Object  []";
					if (fo != null) {
						objtype = fo.getDataType();
					}
					stemp = gg.getOut().getTo() + " = new " + objtype + "  " + stemp;
				} else {
					stemp = "obj" + getRandomString(4) + " = new Object [ ] " + stemp;
				}
				temp.append(stemp);
			} else {
				c = new SimpleTodoCompiler();
				c.setVisitor(visitor);
				ConnDef in = gg.getIn();
				if (in != null) {
					String cond = in.getConnCond();
					String ccond = null;
					if (cond != null && cond.length() > 1) {
						ccond = cond;
					} else {
						String s = in.getFrom();
						FunctionPoint fp = g.getFunctionPoints().get(s);
						if (fp != null) {
							String ss = fp.getDataType();
							try {
								Class cls = Class.forName(ss);
								if (cls != null) {
									if (cls.isAssignableFrom(java.util.Map.class) && !cls.equals(Object.class)) {
										ccond = "Map.Entry item: " + s + ".getEntrySet()";
									} else if (cls.isAssignableFrom(java.util.Collection.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else if (cls.isAssignableFrom(java.util.List.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else {
										ccond = " " + ss + " item : org.ptg.util.CommonUtil.getCollection(new " + ss + "[]{" + s + "}) ";
									}
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					temp.append(gg.getGtype() + " ( " + ccond + " ) { " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*" + gg.getGtype() + " ( " + "Null Condition" + " ){*/  " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
				temp.append(c.compile(g.getName(), gg, g));
				if (in != null) {
					temp.append("} " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*}*/ " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
			}
		}
		if (mappingtype == null) {
			mappingtype = "FreeSpring";
		}
		return compileMappingGraph2(g.getName(), temp.toString(), mappingtype, params);
	}

	public static Object instantiateTodoMappingGraph2(String name, String graphjson, String mappingtype, Map<String, String> params) throws Exception {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return instantiateTodoMappingGraph2(g, mappingtype, params);
	}

	public static Object instantiateTodoMappingGraph2(FPGraph2 g, String mappingtype, Map<String, String> params) throws Exception {
		Map<String, FPGraph> gs = g.getSubgraphs();
		// System.out.println(gs.size());
		StringBuilder temp = new StringBuilder();
		SimpleTodoCompiler c = null;
		List<FPGraph> vals = new ArrayList<FPGraph>();
		vals.addAll(gs.values());
		Collections.sort(vals, new Comparator<FPGraph>() {
			@Override
			public int compare(FPGraph arg0, FPGraph arg1) {
				return arg0.getOrder() - arg1.getOrder();
			}
		});

		/********* MAIN ******/
		FPGraph fmain = g.getMainGraph();
		c = new SimpleTodoCompiler();
		temp.append(SystemUtils.LINE_SEPARATOR + "/****" + fmain.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
		temp.append(c.compile(fmain.getName(), fmain, g));
		temp.append(SystemUtils.LINE_SEPARATOR + "/**** END " + fmain.getName() + " END ****/" + SystemUtils.LINE_SEPARATOR);
		/************* MAIN END *******/
		for (FPGraph gg : vals/* vals is sorted collection */) {
			if (gg.getGtype() != null && gg.getGtype().equals("values")) {
				c = new SimpleTodoCompiler();
				temp.append(c.compile(gg.getName(), gg, g));
				String stemp = "{";
				Collection<FunctionPoint> fpp = gg.getFunctionPoints().values();
				Iterator<FunctionPoint> fi = fpp.iterator();
				for (int k = 0; k < fpp.size(); k++) {
					FunctionPoint fp = fi.next();
					stemp += fp.getId();
					if (k < fpp.size() - 1) {
						stemp += ",";
					}
				}
				stemp += "};";
				if (gg.getOut() != null) {
					FunctionPoint fo = g.getFunctionPoints().get(gg.getOut().getTo());
					String objtype = "Object  []";
					if (fo != null) {
						objtype = fo.getDataType();
					}
					stemp = gg.getOut().getTo() + " = new " + objtype + "  " + stemp;
				} else {
					stemp = "obj" + getRandomString(4) + " = new Object [ ] " + stemp;
				}
				temp.append(stemp);
			} else {
				c = new SimpleTodoCompiler();
				ConnDef in = gg.getIn();
				if (in != null) {
					String cond = in.getConnCond();
					String ccond = null;
					if (cond != null && cond.length() > 1) {
						ccond = cond;
					} else {
						String s = in.getFrom();
						FunctionPoint fp = g.getFunctionPoints().get(s);
						if (fp != null) {
							String ss = fp.getDataType();
							try {
								Class cls = Class.forName(ss);
								if (cls != null) {
									if (cls.isAssignableFrom(java.util.Map.class) && !cls.equals(Object.class)) {
										ccond = "Map.Entry item: " + s + ".getEntrySet()";
									} else if (cls.isAssignableFrom(java.util.Collection.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else if (cls.isAssignableFrom(java.util.List.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else {
										ccond = " " + ss + " item : org.ptg.util.CommonUtil.getCollection(new " + ss + "[]{" + s + "}) ";
									}
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					temp.append(gg.getGtype() + " ( " + ccond + " ) { " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*" + gg.getGtype() + " ( " + "Null Condition" + " ){*/  " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
				temp.append(c.compile(g.getName(), gg, g));
				if (in != null) {
					temp.append("} " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*}*/ " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
			}
		}
		if (mappingtype == null) {
			mappingtype = "FreeSpring";
		}
		return compileMappingGraph2(g.getName(), temp.toString(), mappingtype, params);
	}

	public static List<Event> readCSV(String fileName, EventDefinition ed, int startLine) {
		Map<Integer, String> indexMap = ed.getIndexMap();
		List<Event> evts = new ArrayList<Event>();
		try {

			CSVReader reader = new CSVReader(new FileReader(new File(fileName)));
			String[] cols = null;
			int linecount = 0;
			while ((cols = reader.readNext()) != null) {
				if (linecount >= startLine) {
					Event e = (Event) ReflectionUtils.createInstance(ed.getType());
					for (int i = 0; i < cols.length; i++) {
						if (ed != null) {
							String propName = indexMap.get(i + 1);
							PropertyDefinition prop = ed.getProps().get("" + (i + 1));
							Object val = NativeFormatter.fromString(prop.getType(), cols[i]);
							e.setEventProperty(propName, val);
						}
					}
					evts.add(e);
				}
				linecount++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return evts;
	}

	public static List<Event> readCSV(String file, String objectName) {
		EventDefinition ed = CommonUtil.buildEventDefinition(objectName, objectName, objectName + "_store");
		List<Event> evts = CommonUtil.readCSV(file, ed, 1);
		return evts;
	}

	public static List<Event> csvToDB(String file, String objectName, String store) throws Exception {
		EventDefinition ed = CommonUtil.buildEventDefinition(objectName, objectName, store);
		List<Event> evts = CommonUtil.readCSV(file, ed, 1);
		Class c = EventDefinitionManager.getInstance().buildDBTransformerDefinition(ed);
		IEventDBTransformer dbxfer = (IEventDBTransformer) c.newInstance();
		dbxfer.setStore(store);
		for (Event e : evts) {
			dbxfer.saveToDbNative(e);
		}
		return evts;
	}

	public static IStreamTransformer getStreamTransformer(String objectName, String store) throws Exception {
		EventDefinition ed = CommonUtil.buildEventDefinition(objectName, objectName, store);
		Stream sd = CommonUtil.buildStreamDefinition(objectName + "Stream", objectName, store);
		Class c = StreamManager.getInstance().getStreamTransformer(sd);
		IStreamTransformer tx = (IStreamTransformer) c.newInstance();
		return tx;

	}

	public static IEventDBTransformer getDBTransformer(String objectName, String store) throws Exception {
		EventDefinition ed = CommonUtil.buildEventDefinition(objectName, objectName, store);
		Class c = EventDefinitionManager.getInstance().buildDBTransformerDefinition(ed);
		IEventDBTransformer dbxfer = (IEventDBTransformer) c.newInstance();
		dbxfer.setStore(store);
		return dbxfer;
	}

	public static Map<String, Integer> updateAndRunTodoGraph(String name, String uid, String prop, String value) {
		return CommonUtil.updateAndRunTodoGraph(name, new String[] { uid }, new String[] { prop }, new String[] { value });
	}

	public static Map<String, Integer> updateAndRunTodoGraph(String name, String[] uid, String[] prop, String[] value) {
		updateGraphItemsOffline(name, uid, prop, value);
		String json = getGraphJson(name);
		return runTodoGraphJson(name, "ToDo", json, new HashMap());
	}

	public static Map<String, Integer> updateAndRunJobGraph(String name, String uid, String prop, String value) {
		return CommonUtil.updateAndRunJobGraph(name, new String[] { uid }, new String[] { prop }, new String[] { value });
	}

	public static Map<String, Integer> updateAndRunJobGraph(String name, String[] uid, String[] prop, String[] value) {
		updateGraphItemsOffline(name, uid, prop, value);
		String json = getGraphJson(name);
		return runJobGraphJson(name, "JobProcess", json, new HashMap());
	}

	public static String getGraphJson(String name) {
		String json = DBHelper.getInstance().getString("select graph from graphs where name='" + name + "'");
		return json;
	}

	public static String getGraphJsonData(String name) {
		String json = DBHelper.getInstance().getString("select graph from graphs where name='" + name + "'");
		return json;
	}

	public static Map<String, Integer> runJobGraphJson(String name, String mappingType, String json, Map params) {
		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		try {
			Closure c = getProcessJobGraphCompiled(name, mappingType, json, params);
			if (c != null) {
				c.execute(m);
				// System.out.println(m);
			} else {
				// System.out.println("Failed to compile the job");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	public static Map<String, Integer> runTodoGraphJson(String name, String mappingType, String json, Map params) {
		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		try {
			Closure c = getJobGraphCompiled(name, mappingType, json, params);
			if (c != null) {
				c.execute(m);
				// System.out.println(m);
			} else {
				// System.out.println("Failed to compile the job");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	public static Closure getProcessJobGraphCompiled(String name, String mappingType, String json, Map params) {
		final Map<String, Object> l = getGraphObjectsFromJson(name, json);
		FPGraph2 g = new FPGraph2();
		g.setName(name);
		g.fromObjectMap(l, null);
		Closure c = null;
		try {
			Object o = instantiateJobMappingGraph(g, mappingType, params);
			if (o != null) {
				if (o instanceof Closure) {
					c = (Closure) o;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public static Closure getJobGraphCompiled(String name, String mappingType, String json, Map params) {
		final Map<String, Object> l = getGraphObjectsFromJson(name, json);
		FPGraph2 g = new FPGraph2();
		g.setName(name);
		g.fromObjectMap(l, null);
		Closure c = null;
		try {
			Object o = instantiateTodoMappingGraph2(g, mappingType, params);
			if (o != null) {
				if (o instanceof Closure) {
					c = (Closure) o;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public static void updateGraphItemsOffline(String name, String uid, String prop, String value) {
		updateGraphItemsOffline(name, new String[] { uid }, new String[] { prop }, new String[] { value });
	}

	public static void updateGraphItemsOffline(String name/*
														 * graph name to be
														 * updated
														 */, String[] uid, String[] prop, String[] value) {
		String graphType = DBHelper.getInstance().getString("select graphtype from graphs where name='" + name + "'");
		String json = getGraphJson(name);
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		List l = new ArrayList();
		l.add(json);
		l.add(uid);
		l.add(prop);
		l.add(value);
		String graphStr = (String) s.runFuntionRaw("updateGraphItemsProp", l);
		graphStr = StringEscapeUtils.escapeJavaScript(graphStr);
		String move = "insert into deletedgraphs select * from graphs where name='" + name + "'";
		String delete = "delete from graphs where name='" + name + "'";
		String insert = "insert into graphs(name,graph,userid,userip,graphconfig,graphtype) values ('" + name + "','" + graphStr + "','" + 0 + "','" + "127.0.0.1" + "','" + "" + "','" + graphType
				+ "')";
		DBHelper.getInstance().executeUpdate(move);
		DBHelper.getInstance().executeUpdate(delete);
		DBHelper.getInstance().executeUpdate(insert);

		// System.out.println("Done updating graph " + uid + " offline .");
	}

	public static String getGraphItemValue(String name, String uid, String prop) {
		String graphType = DBHelper.getInstance().getString("select graphtype from graphs where name='" + name + "'");
		String json = getGraphJson(name);
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		List l = new ArrayList();
		l.add(json);
		l.add(uid);
		l.add(prop);
		NativeJavaObject graphStr = (NativeJavaObject) s.runFuntionRaw("getGraphItemsProp", l);
		// System.out.println("Got a value : " + graphStr + " offline .");
		return (String) graphStr.unwrap();
	}

	public static String packageJob(String name, String classname, String mappingType) {
		if (mappingType == null) {
			mappingType = "ToDo";
		}
		Map m = new HashMap();
		m.put("name", name);
		m.put("mappingtype", mappingType);
		m.put("classname", classname);
		StringBuffer responseContent = VelocityHelper.burnTemplate(m, "packageJob.vm");
		return responseContent.toString();

	}

	public static String packageMapperJob(String name, String classname, String mappingType, String eventType) {
		if (mappingType == null) {
			mappingType = "FreeSpring";
		}
		Map m = new HashMap();
		m.put("name", name);
		m.put("mappingtype", mappingType);
		m.put("classname", classname);
		m.put("eventtype", eventType);
		StringBuffer responseContent = VelocityHelper.burnTemplate(m, "packageMapperJob.vm");
		return responseContent.toString();

	}

	public static String getGraphItemsJson(String name) {
		String json = getGraphJson(name);
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		List l = new ArrayList();
		l.add(json);
		String graphStr = (String) s.runFuntionRaw("getGraphItemsJson", l);
		return graphStr;
	}

	public static String packageSQLJob(String name, String classname, String mappingType, String eventType) {
		if (mappingType == null) {
			mappingType = "FreeSpring";
		}
		Map m = new HashMap();
		m.put("name", name);
		m.put("mappingtype", mappingType);
		m.put("classname", classname);
		m.put("eventtype", eventType);
		StringBuffer responseContent = VelocityHelper.burnTemplate(m, "packageSQLJob.vm");
		return responseContent.toString();
	}

	public static void compileMapperJobPackage(String path, String jobfile, String name, String eventType) throws Exception {
		String s = CommonUtil.packageMapperJob(name, jobfile, "FreeSpring", eventType);
		String json = CommonUtil.getGraphItemsJson(name);
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".process"), json.getBytes());
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".java"), s.getBytes());
	}

	public static void compileSQLJobPackage(String path, String jobfile, String name, String eventType) throws IOException {
		String s = CommonUtil.packageSQLJob(name, jobfile, "FreeSpring", eventType);
		String json = CommonUtil.getGraphItemsJson(name);
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".process"), json.getBytes());
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".java"), s.getBytes());
	}

	public static void compileToDoJobPackage(String path, String jobfile, String name) throws IOException {
		String s = CommonUtil.packageJob(name, jobfile, null);
		String json = CommonUtil.getGraphJson(name);
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".process"), json.getBytes());
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".java"), s.getBytes());
	}

	public static void compileProcessJobPackage(String path, String jobfile, String name) throws IOException {
		String s = CommonUtil.packageJob(name, jobfile, null);
		String json = CommonUtil.getGraphJson(name);
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".process"), json.getBytes());
		org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + jobfile + ".java"), s.getBytes());
	}

	public static String compileDataTypeTempl(String dataType, String name, String code) {
		StringBuilder str = new StringBuilder();
		if (dataType.equals("java.util.Collection") || dataType.equals("java.util.Collection") || dataType.equals("java.util.Collection") || dataType.equals("java.util.Collection")) {
			str.append("java.util.Collection ${name} = new java.util.ArrayList();");
			String[] strs = StringUtils.split(code, SystemUtils.LINE_SEPARATOR);
			for (String strsi : strs) {
				str.append("${name}.add(");
				str.append(strsi);
				str.append(");");
			}
			dataTypeTempl.put("java.util.Collection", str.toString());
			dataTypeTempl.put("java.util.List", str.toString());
			dataTypeTempl.put("java.util.Set", str.toString());
		}
		if (dataType.equals("java.util.Map") || dataType.equals("java.util.HashMap") || dataType.equals("java.util.TreeMap") || dataType.equals("java.util.LinkedMap")) {
			str = new StringBuilder();
			str.append("java.util.Map ${name} = new java.util.LinkedHashMap();");
			String[] strs = StringUtils.split(code, SystemUtils.LINE_SEPARATOR);
			for (String strsi : strs) {
				String[] splt = StringUtils.split(strsi, ":");
				str.append("${name}.put(");
				str.append(splt[0]);
				str.append(",");
				str.append(splt[1]);
				str.append(");");
			}
			dataTypeTempl.put("java.util.Map", str.toString());
			dataTypeTempl.put("java.util.HashMap", str.toString());
			dataTypeTempl.put("java.util.TreeMap", str.toString());
			dataTypeTempl.put("java.util.LinkedMap", str.toString());
		}
		/**/
		String temp = dataTypeTempl.get(dataType);
		StringBuffer ret = null;
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put("name", name);
		contextMap.put("code", code);
		if (temp != null) {
			ret = VelocityHelper.burnStringTemplate(contextMap, temp);
		}
		return ret == null ? null : ret.toString();

	}



	public static int testRunnable(String s, Runnable r) {
		return 0;
	}

	public static void runMainClass(String name) {
		try {
			Class c = Class.forName(name);
			Object o = c.newInstance();
			Method mtd = c.getMethod("main", new Class[] { java.lang.String[].class });
			mtd.invoke(o, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void runMainClass(Object o) {
		try {
			Class c = o.getClass();
			Method mtd = c.getMethod("main", new Class[] { java.lang.String[].class });
			mtd.invoke(o, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* dummy function to be used for synchrnonization */
	public static int start(int i) {
		return i;
	}

	public static Object instantiateLocalGraph(String name, String graphjson, String mappingtype, Map<String, String> params) throws Exception {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return instantiateLocalGraph(g, mappingtype, params);
	}

	public static Object instantiateLocalGraph(FPGraph2 g, String mappingtype, Map<String, String> params) throws Exception {
		Map<String, FPGraph> gs = g.getSubgraphs();
		// System.out.println(gs.size());
		StringBuilder temp = new StringBuilder();
		SimpleTodoCompiler c = null;
		List<FPGraph> vals = new ArrayList<FPGraph>();
		vals.addAll(gs.values());
		Collections.sort(vals, new Comparator<FPGraph>() {
			@Override
			public int compare(FPGraph arg0, FPGraph arg1) {
				return arg0.getOrder() - arg1.getOrder();
			}
		});
		LocalCamelVisitor<CompilePath> visitor = (LocalCamelVisitor<CompilePath>) SpringHelper.get("localCamelVisitor");
		/********* MAIN ******/
		FPGraph fmain = g.getMainGraph();
		c = new SimpleTodoCompiler();
		c.setVisitor(visitor);
		temp.append(SystemUtils.LINE_SEPARATOR + "/****" + fmain.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
		temp.append(c.compile(fmain.getName(), fmain, g));
		temp.append(SystemUtils.LINE_SEPARATOR + "/**** END " + fmain.getName() + " END ****/" + SystemUtils.LINE_SEPARATOR);
		/************* MAIN END *******/
		for (FPGraph gg : vals/* vals is sorted collection */) {
			if (gg.getGtype() != null && gg.getGtype().equals("values")) {
				c = new SimpleTodoCompiler();
				c.setVisitor(visitor);
				temp.append(c.compile(gg.getName(), gg, g));
				String stemp = "{";
				Collection<FunctionPoint> fpp = gg.getFunctionPoints().values();
				Iterator<FunctionPoint> fi = fpp.iterator();
				for (int k = 0; k < fpp.size(); k++) {
					FunctionPoint fp = fi.next();
					stemp += fp.getId();
					if (k < fpp.size() - 1) {
						stemp += ",";
					}
				}
				stemp += "};";
				if (gg.getOut() != null) {
					FunctionPoint fo = g.getFunctionPoints().get(gg.getOut().getTo());
					String objtype = "Object  []";
					if (fo != null) {
						objtype = fo.getDataType();
					}
					stemp = gg.getOut().getTo() + " = new " + objtype + "  " + stemp;
				} else {
					stemp = "obj" + getRandomString(4) + " = new Object [ ] " + stemp;
				}
				temp.append(stemp);
			} else {
				c = new SimpleTodoCompiler();
				c.setVisitor(visitor);
				ConnDef in = gg.getIn();
				if (in != null) {
					String cond = in.getConnCond();
					String ccond = null;
					if (cond != null && cond.length() > 1) {
						ccond = cond;
					} else {
						String s = in.getFrom();
						FunctionPoint fp = g.getFunctionPoints().get(s);
						if (fp != null) {
							String ss = fp.getDataType();
							try {
								Class cls = Class.forName(ss);
								if (cls != null) {
									if (cls.isAssignableFrom(java.util.Map.class) && !cls.equals(Object.class)) {
										ccond = "Map.Entry item: " + s + ".getEntrySet()";
									} else if (cls.isAssignableFrom(java.util.Collection.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else if (cls.isAssignableFrom(java.util.List.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else {
										ccond = " " + ss + " item : org.ptg.util.CommonUtil.getCollection(new " + ss + "[]{" + s + "}) ";
									}
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					temp.append(gg.getGtype() + " ( " + ccond + " ) { " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*" + gg.getGtype() + " ( " + "Null Condition" + " ){*/  " + SystemUtils.LINE_SEPARATOR + "/****" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
				temp.append(c.compile(g.getName(), gg, g));
				if (in != null) {
					temp.append("} " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				} else {
					temp.append("/*}*/ " + SystemUtils.LINE_SEPARATOR + "/**** END" + gg.getName() + "****/" + SystemUtils.LINE_SEPARATOR);
				}
			}
		}
		if (mappingtype == null) {
			mappingtype = "FastCamel";
		}
		return compileMappingGraph2(g.getName(), temp.toString(), mappingtype, params);
	}

	

	public static String graphToList(String name) {
		String graphjson = DBHelper.getInstance().getString("select graph from " + "graphs" + " where name='" + name + "'");
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return graphToList(g);
	}

	public static String graphToList(String name, String graphjson) {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		return graphToList(g);
	}

	public static String graphToList(FPGraph2 g) {
		Map<String, FPGraph> gs = g.getSubgraphs();
		// System.out.println(gs.size());
		StringBuilder temp = new StringBuilder();
		SimpleTodoCompiler c = null;
		List<FPGraph> vals = new ArrayList<FPGraph>();
		vals.addAll(gs.values());
		Collections.sort(vals, new Comparator<FPGraph>() {
			@Override
			public int compare(FPGraph arg0, FPGraph arg1) {
				return arg0.getOrder() - arg1.getOrder();
			}
		});
		GraphOrderingVisitor<CompilePath> visitor = new GraphOrderingVisitor<CompilePath>();
		/********* MAIN ******/
		FPGraph fmain = g.getMainGraph();
		c = new SimpleTodoCompiler();
		c.setVisitor(visitor);
		temp.append(c.compile(fmain.getName(), fmain, g));
		/************* MAIN END *******/
		for (FPGraph gg : vals/* vals is sorted collection */) {
			if (gg.getGtype() != null && gg.getGtype().equals("values")) {
				c = new SimpleTodoCompiler();
				c.setVisitor(visitor);
				temp.append(c.compile(gg.getName(), gg, g));
				String stemp = "{";
				Collection<FunctionPoint> fpp = gg.getFunctionPoints().values();
				Iterator<FunctionPoint> fi = fpp.iterator();
				for (int k = 0; k < fpp.size(); k++) {
					FunctionPoint fp = fi.next();
					stemp += fp.getId();
					if (k < fpp.size() - 1) {
						stemp += ",";
					}
				}
				stemp += "};";
				if (gg.getOut() != null) {
					FunctionPoint fo = g.getFunctionPoints().get(gg.getOut().getTo());
					String objtype = "Object  []";
					if (fo != null) {
						objtype = fo.getDataType();
					}
					stemp = gg.getOut().getTo() + " = new " + objtype + "  " + stemp;
				} else {
					stemp = "obj" + getRandomString(4) + " = new Object [ ] " + stemp;
				}
				temp.append(stemp);
			} else {
				c = new SimpleTodoCompiler();
				c.setVisitor(visitor);
				ConnDef in = gg.getIn();
				if (in != null) {
					String cond = in.getConnCond();
					String ccond = null;
					if (cond != null && cond.length() > 1) {
						ccond = cond;
					} else {
						String s = in.getFrom();
						FunctionPoint fp = g.getFunctionPoints().get(s);
						if (fp != null) {
							String ss = fp.getDataType();
							try {
								Class cls = Class.forName(ss);
								if (cls != null) {
									if (cls.isAssignableFrom(java.util.Map.class) && !cls.equals(Object.class)) {
										ccond = "Map.Entry item: " + s + ".getEntrySet()";
									} else if (cls.isAssignableFrom(java.util.Collection.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else if (cls.isAssignableFrom(java.util.List.class) && !cls.equals(Object.class)) {
										ccond = "Object item: " + s;
									} else {
										ccond = " " + ss + " item : org.ptg.util.CommonUtil.getCollection(new " + ss + "[]{" + s + "}) ";
									}
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}

				} else {

				}
				temp.append(c.compile(g.getName(), gg, g));
			}
		}
		return temp.toString();
	}

	public static void updateGraphItemsOfflineInTable(String id/*
																 * graph name to
																 * be updated
																 */, String[] uid, String[] prop, String[] value, String table) {
		String json = DBHelper.getInstance().getString("select graph from " + table + " where instid='" + id + "'");
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		List l = new ArrayList();
		l.add(json);
		l.add(uid);
		l.add(prop);
		l.add(value);
		String graphStr = (String) s.runFuntionRaw("updateGraphItemsProp", l);
		graphStr = StringEscapeUtils.escapeJavaScript(graphStr);
		String insert = "update " + table + " set graph=\'" + graphStr + "\' where instid=\'" + id + "\'";
		DBHelper.getInstance().executeUpdate(insert);

		// System.out.println("Done updating graph " + uid + " offline .");
	}

	public static String getGraphItemValueFromTable(String name, String uid, String prop, String table) {
		String graphType = DBHelper.getInstance().getString("select graphtype from " + table + " where name='" + name + "'");
		String json = getGraphJson(name);
		ScriptEngine s = WebStartProcess.getInstance().getScriptEngine();
		if (s == null) {
			s = new ScriptEngine();
			s.init();
		}
		List l = new ArrayList();
		l.add(json);
		l.add(uid);
		l.add(prop);
		NativeJavaObject graphStr = (NativeJavaObject) s.runFuntionRaw("getGraphItemsProp", l);
		// System.out.println("Got a value : " + graphStr + " offline .");
		return (String) graphStr.unwrap();
	}

	public static void updateExecStatus(String nameid, String instid, String execStatus, String exp) {
		String s = "insert into graphexecstatus (name,instid,execstatus,exp) values(\'" + nameid + "\',\'" + instid + "\',\'" + execStatus + "\',\'" + exp + "\')";
		String s2 = "delete from " + " graphexecstatus " + " where instid=\"" + instid + "\" and name=\"" + nameid + "\"";
		String s3 = "insert into graphexeclog (name,instid,execstatus,exp) values(\'" + nameid + "\',\'" + instid + "\',\'" + execStatus + "\',\'" + exp + "\')";

		try {
			DBHelper.getInstance().executeInTrans(new String[] { s3, s2, s });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getExecStatus(String nameid, String instid) {
		String s = "Select execStatus from graphexecstatus where name='" + nameid + "' and instid='" + instid + "'";
		String status = DBHelper.getInstance().getString(s);
		return status;
	}

	public static Map<String, String> getExecStatus(String instid) {
		Map<String, String> sts = new HashMap<String, String>();
		String s = "Select name,execStatus from graphexecstatus where instid='" + instid + "'";
		sts = DBHelper.getInstance().getStringMap(s);
		return sts;
	}

	public static String getUUIDStr(Map config) {
		if (config != null) {
			// compute from config the best possible uuid for the process
		}
		return CommonUtil.getRandomString(16) + "-" + CommonUtil.getRandomString(16);
	}

	public static String getUUIDStr() {
		return CommonUtil.getRandomString(16) + "-" + CommonUtil.getRandomString(16);
	}

	public static String createGraphInstance(String name, String toSave, String graphconfig, String graphtype, String ip, String instidStr) {
		Object[] rets = DBHelper.getInstance().getObjects("select id,graph,graphconfig,graphtype from graphs where name='" + name + "'", 4);
		int id = (Integer) rets[0];
		if (ip == null) {
			ip = "127.0.0.1";
		}

		if (toSave == null) {
			toSave = (String) rets[1];
		}
		if (graphconfig == null) {
			graphconfig = (String) rets[2];
		}
		if (graphtype == null) {
			graphtype = (String) rets[3];
		}

		String configStr = graphconfig;
		if (instidStr == null) {
			Map config = CommonUtil.getConfigFromJsonData(configStr);
			instidStr = getUUIDStr(config);
		}
		boolean exists = DBHelper.getInstance().exists("select instid from graphinstances where instid='" + instidStr + "'");
		if (!exists) {
			String insert = "insert into graphinstances(id,name,graph,userid,userip,graphconfig,graphtype,instid) values (" + id + ",'" + name + "','" + toSave + "','" + 0 + "','" + ip + "','"
					+ graphconfig + "','" + graphtype + "','" + instidStr + "')";
			DBHelper.getInstance().executeUpdate(insert);
		}
		return instidStr;
	}

	public static String createGraphInstance(String name, String instanceIdStr) {
		return createGraphInstance(name, null, null, null, null, instanceIdStr);
	}

	public static String createGraphInstance(String name) {
		return createGraphInstance(name, null, null, null, null, null);
	}

	public static DirectedGraph<FunctionPoint, ConnDef> analyzeSpanningTree(String name, String graphjson, String mappingtype, Map<String, String> params) {
		FPGraph2 g = new FPGraph2();
		g.fromGraphJson(name, graphjson);
		DirectedSparseMultigraph<FunctionPoint, ConnDef> f = g.getMainGraph().getGraph();
		Map m = new HashMap();
		org.apache.commons.collections15.Factory<DelegateForest<FunctionPoint, ConnDef>> fac = new org.apache.commons.collections15.Factory<DelegateForest<FunctionPoint, ConnDef>>() {
			@Override
			public DelegateForest<FunctionPoint, ConnDef> create() {
				return new DelegateForest<FunctionPoint, ConnDef>();
			}

		};
		PrimMinimumSpanningTree<FunctionPoint, ConnDef> dist = new PrimMinimumSpanningTree<FunctionPoint, ConnDef>(fac);
		return (DirectedGraph<FunctionPoint, ConnDef>) dist.transform(f);
	}

	public Object lookup(String type, String key) {
		return null;
	}

	public static String getLuceneIndexFuncCode(EventDefinition edef) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();\n");
		sb.append(edef.getType() + " event  =    " + " (" + edef.getType() + ") " + "evt" + " ;\n");
		for (PropertyDefinition d : edef.getProps().values()) {
			if (isString(d.getType())) {
				sb.append("doc.add(new Field(\"" + d.getName() + "\", " + "event.get" + WordUtils.capitalize(d.getName()) + "()" + "," + "Field.Store.YES, Field.Index.ANALYZED));\n");
			} else if (isFloat(d.getType())) {
				sb.append("doc.add(new NumericField(\"" + d.getName() + "\", Field.Store.YES, true).setFloatValue(" + "event.get" + WordUtils.capitalize(d.getName()) + "()));\n");
			} else if (isDouble(d.getType())) {
				sb.append("doc.add(new NumericField(\"" + d.getName() + "\", Field.Store.YES, true).setDoubleValue(" + "event.get" + WordUtils.capitalize(d.getName()) + "()));\n");
			} else if (isLong(d.getType())) {
				sb.append("doc.add(new NumericField(\"" + d.getName() + "\", Field.Store.YES, true).setLongValue(" + "event.get" + WordUtils.capitalize(d.getName()) + "()));\n");
			} else if (isShort(d.getType())) {
				sb.append("doc.add(new NumericField(\"" + d.getName() + "\", Field.Store.YES, true).setIntValue(" + "event.get" + WordUtils.capitalize(d.getName()) + "()));\n");
			} else if (isInt(d.getType())) {
				sb.append("doc.add(new NumericField(\"" + d.getName() + "\", Field.Store.YES, true).setIntValue(" + "event.get" + WordUtils.capitalize(d.getName()) + "()));\n");
			} else if (isDate(d.getType())) {
				sb.append("doc.add(new NumericField(\"" + d.getName() + "\", Field.Store.YES, true).setLongValue(" + "event.get" + WordUtils.capitalize(d.getName()) + "()==null?0:" + "event.get"
						+ WordUtils.capitalize(d.getName()) + "().getTime()));\n");
			} else {
				throw new Exception("Failed to parse the event definition type");
			}

		}
		sb.append("doc.add(new Field(\"" + "EventType" + "\", \"" + edef.getType() + "\"," + "Field.Store.YES, Field.Index.ANALYZED));\n");
		sb.append("doc.add(new Field(\"" + "EventStore" + "\", \"" + edef.getEventStore() + "\"," + "Field.Store.YES, Field.Index.ANALYZED));\n");
		sb.append("doc.add(new NumericField(\"" + "EventId" + "\", Field.Store.YES, true).setFloatValue(" + "event.getId" + "()));\n");

		sb.append("w.addDocument(doc);\n");
		return sb.toString();
	}

	public static String getLuceneDocToEventCode(EventDefinition edef) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(edef.getType() + " event  =    new " + " " + edef.getType() + "()" + " ;\n");
		for (PropertyDefinition d : edef.getProps().values()) {
			if (d.getType().contains("String")) {
				sb.append("event.set" + WordUtils.capitalize(d.getName()) + "((" + d.getType() + ") doc.get(\"" + d.getName() + "\"));\n");
			} else {
				if ("java.util.Date".equals(d.getType())) {
					sb.append("event.set" + WordUtils.capitalize(d.getName()) + "((" + d.getType() + ")new java.util.Date(Long.parseLong( doc.get(\"" + d.getName() + "\"))));\n");
				} else if (isLong(d.getType())) {
					sb.append("event.set" + WordUtils.capitalize(d.getName()) + "((" + d.getType() + ")new " + d.getType() + "(Long.parseLong( doc.get(\"" + d.getName() + "\"))));\n");
				} else if (isInt(d.getType())) {
					sb.append("event.set" + WordUtils.capitalize(d.getName()) + "((" + d.getType() + ")new " + d.getType() + "(Integer.parseInt( doc.get(\"" + d.getName() + "\"))));\n");
				} else if (isFloat(d.getType())) {
					sb.append("event.set" + WordUtils.capitalize(d.getName()) + "((" + d.getType() + ")new " + d.getType() + "(Float.parseFloat( doc.get(\"" + d.getName() + "\"))));\n");
				} else if (isDouble(d.getType())) {
					sb.append("event.set" + WordUtils.capitalize(d.getName()) + "((" + d.getType() + ")new " + d.getType() + "(Double.parseDouble( doc.get(\"" + d.getName() + "\"))));\n");
				}

			}
		}
		sb.append("return event; \n");
		return sb.toString();
	}

	public static ILuceneEventDocWriter getLuceneIndexFunc(EventDefinition edef) throws Exception {
		ILuceneEventDocWriter writer = null;
		Map<String, String> params = new HashMap<String, String>();
		String codein = getLuceneIndexFuncCode(edef);
		String codein2 = getLuceneDocToEventCode(edef);
		String str = StringUtils.replace(edef.getType(), ".", "_");
		Object obj = compileMappingGraph3("extraindex", str + "LuceneIndexer", codein, codein2, "ILuceneDocWriter", params);
		writer = (ILuceneEventDocWriter) obj;
		return writer;
	}

	public static List<AnonDefObj> topologicalSortAnonTypes(FPGraph2 g) {
		final DefaultDirectedGraph<PortObj, DefaultEdge> graph = new DefaultDirectedGraph<PortObj, DefaultEdge>(DefaultEdge.class);
		Map<String, PortObj> filter = new HashMap<String, PortObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : g.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		for (ConnDef cd : g.getForward().values()) {
			PortObj from = g.getPorts().get(cd.getFrom());
			if (from != null) {
				if (!filter.containsKey(from.getId())) {
					graph.addVertex(from);
					filter.put(from.getId(), from);
				}
			}
			PortObj to = g.getPorts().get(cd.getTo());
			if (to != null) {
				if (!filter.containsKey(to.getId())) {
					graph.addVertex(to);
					filter.put(to.getId(), to);
				}
			}
			if (from != null && to != null) {
				// System.out.println("Adding edge: " + from.getId() + "->" +
				// to.getId());
				graph.addEdge(from, to);
			} else {
				AnonDefObj a1 = anonCompMap.get(cd.getFrom());
				AnonDefObj a2 = anonCompMap.get(cd.getTo());
				if (a1 != null && a2 != null) {
					for (String s : a1.getOutputs()) {
						String outkey = "out_" + a1.getId() + "." + s;
						for (String s2 : a2.getInputs()) {
							PortObj fromp = null;
							if (!filter.containsKey(outkey)) {
								fromp = g.getPorts().get(outkey);
								if (fromp != null) {
									graph.addVertex(fromp);
									filter.put(fromp.getId(), fromp);
								}
							}
							String inkey = "inp_" + a2.getId() + "." + s2;
							PortObj top = null;
							if (!filter.containsKey(inkey)) {
								top = g.getPorts().get(inkey);
								if (top != null) {
									graph.addVertex(top);
									filter.put(top.getId(), top);
								}
							}
							if (fromp != null && top != null) {
								// System.out.println("Adding edge: " +
								// fromp.getId() + "->" + top.getId());
								graph.addEdge(fromp, top);
							}
						}
					}
				}
			}

		}
		Map<String, PortObj> ports = g.getPorts();
		for (AnonDefObj def : g.getAnonDefs()) {
			for (String sout : def.getOutputs()) {
				PortObj outp = ports.get("out_" + def.getId() + "." + sout);
				if (outp != null) {
					for (String sin : def.getInputs()) {
						PortObj inp = ports.get("inp_" + def.getId() + "." + sin);
						if (inp != null) {
							if (!filter.containsKey(inp.getId())) {
								graph.addVertex(inp);
								filter.put(inp.getId(), inp);
							}
							if (!filter.containsKey(outp.getId())) {
								graph.addVertex(outp);
								filter.put(outp.getId(), outp);
							}
							// System.out.println("Adding edge: " + inp.getId()
							// + "->" + outp.getId());
							graph.addEdge(inp, outp);
						}
					}
				}
			}
		}
		final List<PortObj> portObjects = new LinkedList<PortObj>();
		TopologicalOrderIterator<PortObj, DefaultEdge> iter = new TopologicalOrderIterator<PortObj, DefaultEdge>(graph);
		iter.addTraversalListener(new TraversalListenerAdapter<PortObj, DefaultEdge>() {
			@Override
			public void vertexTraversed(VertexTraversalEvent<PortObj> a) {
				PortObj v = a.getVertex();
				boolean added = false;
				Collection<DefaultEdge> vertexes = graph.incomingEdgesOf(v);
				Iterator<DefaultEdge> viter = vertexes.iterator();
				int currCount = 0;
				int maxParent = -1;
				for (int k = 0; k < vertexes.size(); k++) {
					DefaultEdge ed = viter.next();
					PortObj from = graph.getEdgeSource(ed);
					int lastParent = portObjects.indexOf(from);
					if (maxParent < lastParent) {
						maxParent = lastParent;
					}
					if (lastParent > -1) {
						currCount++;
						if (currCount == vertexes.size()) {
							if (maxParent == portObjects.size()) {
								added = true;
								portObjects.add(v);
							} else {
								portObjects.add(maxParent + 1, v);
								added = true;
								break;
							}
						}
					} else {
						// System.out.println("This should not come here topological sort failed");
					}
				}
				if (!added) {
					portObjects.add(v);
				}
			}
		});
		while (iter.hasNext()) {
			iter.next();
		}

		int i = 0;
		List<AnonDefObj> toRem = new LinkedList<AnonDefObj>();
		Map<String, String> done = new LinkedHashMap<String, String>();

		Iterator<PortObj> iter2 = portObjects.iterator();
		while (iter2.hasNext()) {
			PortObj port = iter2.next();
			String id = port.getId();
			// System.out.println(id);
			done.put(id, id);
			for (AnonDefObj def : anonCompMap.values()) {
				int found = 0;
				for (String s : def.getInputs()) {
					if (!done.containsKey("inp_" + def.getId() + "." + s)) {
						break;
					} else {
						found++;
					}
				}
				if (found == def.getInputs().size()) {
					toRem.add(def);
				}
			}
			for (AnonDefObj obj : toRem) {
				anonCompMap.remove(obj.getId());
			}
			++i;
		}

		for (AnonDefObj def : g.getAnonDefs()) {
			/* add all missing */
			if (toRem.contains(def) == false) {
				toRem.add(def);
			}
		}
		return toRem;
	}

	public static Collection<String> topologicalSortPorts(FPGraph2 g) {
		final DefaultDirectedGraph<PortObj, DefaultEdge> graph = new DefaultDirectedGraph<PortObj, DefaultEdge>(DefaultEdge.class);
		Map<String, PortObj> filter = new HashMap<String, PortObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : g.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		for (ConnDef cd : g.getForward().values()) {
			PortObj from = g.getPorts().get(cd.getFrom());
			if (from != null) {
				if (!filter.containsKey(from.getId())) {
					graph.addVertex(from);
					filter.put(from.getId(), from);
				}
			}
			PortObj to = g.getPorts().get(cd.getTo());
			if (to != null) {
				if (!filter.containsKey(to.getId())) {
					graph.addVertex(to);
					filter.put(to.getId(), to);
				}
			}
			if (from != null && to != null) {
				// System.out.println("Adding edge: " + from.getId() + "->" +
				// to.getId());
				graph.addEdge(from, to);
			} else {
				AnonDefObj a1 = anonCompMap.get(cd.getFrom());
				AnonDefObj a2 = anonCompMap.get(cd.getTo());
				if (a1 != null && a2 != null) {
					for (String s : a1.getOutputs()) {
						String outkey = "out_" + a1.getId() + "." + s;
						for (String s2 : a2.getInputs()) {
							PortObj fromp = null;
							if (!filter.containsKey(outkey)) {
								fromp = g.getPorts().get(outkey);
								if (fromp != null) {
									graph.addVertex(fromp);
									filter.put(fromp.getId(), fromp);
								}
							}
							String inkey = "inp_" + a2.getId() + "." + s2;
							PortObj top = null;
							if (!filter.containsKey(inkey)) {
								top = g.getPorts().get(inkey);
								if (top != null) {
									graph.addVertex(top);
									filter.put(top.getId(), top);
								}
							}
							if (fromp != null && top != null) {
								// System.out.println("Adding edge: " +
								// fromp.getId() + "->" + top.getId());
								graph.addEdge(fromp, top);
							}
						}
					}
				}
			}

		}
		Map<String, PortObj> ports = g.getPorts();
		for (AnonDefObj def : g.getAnonDefs()) {
			for (String sout : def.getOutputs()) {
				PortObj outp = ports.get("out_" + def.getId() + "." + sout);
				if (outp != null) {
					for (String sin : def.getInputs()) {
						PortObj inp = ports.get("inp_" + def.getId() + "." + sin);
						if (inp != null) {
							if (!filter.containsKey(inp.getId())) {
								graph.addVertex(inp);
								filter.put(inp.getId(), inp);
							}
							if (!filter.containsKey(outp.getId())) {
								graph.addVertex(outp);
								filter.put(outp.getId(), outp);
							}
							// System.out.println("Adding edge: " + inp.getId()
							// + "->" + outp.getId());
							graph.addEdge(inp, outp);
						}
					}
				}
			}
		}
		final List<PortObj> portObjects = new LinkedList<PortObj>();
		TopologicalOrderIterator<PortObj, DefaultEdge> iter = new TopologicalOrderIterator<PortObj, DefaultEdge>(graph);
		iter.addTraversalListener(new TraversalListenerAdapter<PortObj, DefaultEdge>() {
			@Override
			public void vertexTraversed(VertexTraversalEvent<PortObj> a) {
				PortObj v = a.getVertex();
				boolean added = false;
				Collection<DefaultEdge> vertexes = graph.incomingEdgesOf(v);
				Iterator<DefaultEdge> viter = vertexes.iterator();
				int currCount = 0;
				int maxParent = -1;
				for (int k = 0; k < vertexes.size(); k++) {
					DefaultEdge ed = viter.next();
					PortObj from = graph.getEdgeSource(ed);
					int lastParent = portObjects.indexOf(from);
					if (maxParent < lastParent) {
						maxParent = lastParent;
					}
					if (lastParent > -1) {
						currCount++;
						if (currCount == vertexes.size()) {
							if (maxParent == portObjects.size()) {
								added = true;
								portObjects.add(v);
							} else {
								portObjects.add(maxParent + 1, v);
								added = true;
								break;
							}
						}
					} else {
						// System.out.println("This should not come here topological sort failed");
					}
				}
				if (!added) {
					portObjects.add(v);
				}
			}
		});
		while (iter.hasNext()) {
			iter.next();
		}
		Map<String, String> done = new LinkedHashMap<String, String>();
		Iterator<PortObj> iter2 = portObjects.iterator();
		while (iter2.hasNext()) {
			PortObj port = iter2.next();
			String id = port.getId();
			// System.out.println(id);
			if (done.get(id) == null) {
				done.put(id, id);
			} else {
				// System.out.println("Already processed : " + id);
			}
		}

		return done.values();
	}

	public static void downloadImage(String url, String dir) throws FileNotFoundException, IOException {
		String fileName = org.apache.commons.lang.StringUtils.substringAfterLast(url, "/");
		byte[] data = HTTPClientUtil.getBinary(url);
		IOUtils.write(data, new FileOutputStream(dir + "/" + fileName));
	}

	public static void escapeSQL(List l) {
		for (int i = 0; i < l.size(); i++) {
			Object o = l.get(i);
			if (o instanceof String) {
				o = StringEscapeUtils.escapeSql(o.toString());
				l.set(i, o);
			}
		}
	}

	/* always reload class */
	public static List<ITaskFunction> getTaskFunctions(String basePath, boolean recompile) throws Exception {
		List<ITaskFunction> functions = new LinkedList<ITaskFunction>();
		if (basePath == null) {
			basePath = base + "uploaded/titan/";
		}
		ClassPool pool = getClassPool();
		File fileBase = new File(basePath);
		File[] files = fileBase.listFiles();
		for (File f : files) {
			String cname = f.getName();
			if (cname.endsWith(".titan")) {
				cname = cname.substring(0, cname.indexOf(".titan"));
				if (recompile) {
					precompileTitan(basePath, cname);
				}
				byte[] cls = compileToByteCode(basePath, cname);
				CtClass ccold = pool.getCtClass(cname);
				CtClass cc = pool.makeClass(new ByteArrayInputStream(cls), false);
				if (ccold != null) {
					cc.setName(cname + getRandomString(8));
				}
				for (CtClass c : cc.getInterfaces()) {
					if (c.getName().equals("org.ptg.util.ITaskFunction")) {
						ITaskFunction task = (ITaskFunction) cc.toClass().newInstance();
						task.setName(cname);
						functions.add(task);
					}
				}

			}
		}
		return functions;
	}

	public static Map<String, Object> execTaskPlan(String name) {
		CompileTaskPlanV2 ctp = new CompileTaskPlanV2();
		FPGraph2 g = CommonUtil.buildMappingGraph2(name);
		Map<String, Object> map = ctp.runApp(CommonUtil.getRandomString(8), name, g, 1, null, null);
		return map;
	}

	public static Map<String, Object> execTaskPlanFromJson(String name, String json) {
		CompileTaskPlanV2 ctp = new CompileTaskPlanV2();
		final Map<String, Object> l = getGraphObjectsFromJson(name, json);
		FPGraph2 g = new FPGraph2();
		g.setName(name);
		g.fromObjectMap(l, null);
		Map<String, Object> map = ctp.runApp(CommonUtil.getRandomString(8), name, g, 1, null, null);
		return map;
	}

	public static byte[] itoba(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i >> 24);
		b[1] = (byte) (i >> 16);
		b[2] = (byte) (i >> 8);
		b[3] = (byte) i;
		return b;
	}

	public static byte[] itoba(int... i) {
		byte[] b = new byte[i.length * 4];
		for (int k = 0; k < i.length; k++) {
			int item = i[k];
			b[k * 4] = (byte) (item >> 24);
			b[k * 4 + 1] = (byte) (item >> 16);
			b[k * 4 + 2] = (byte) (item >> 8);
			b[k * 4 + 3] = (byte) item;
		}
		return b;
	}

	public static byte[] itobaViaList(int... array) {
		List<Byte> bytes = new ArrayList<Byte>();
		for (int i = 0; i < array.length; i++) {
			bytes.add((byte) (array[i] >> 24));
			bytes.add((byte) (array[i] >> 16));
			bytes.add((byte) (array[i] >> 8));
			bytes.add((byte) (array[i] >> 0));
		}
		byte[] ret = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			ret[i] = bytes.get(i);
		}
		return ret;
	}

	public static List<String> topologicalSort(FPGraph2 g) {
		final DirectedMultigraph<String, DefaultEdge> graph = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
		List<String> ret = new ArrayList<String>(g.getForward().values().size() * 2);
		for (ConnDef cd : g.getForward().values()) {
			if (!graph.containsVertex(cd.getFrom())) {
				graph.addVertex(cd.getFrom());
			}
			if (!graph.containsVertex(cd.getTo())) {
				graph.addVertex(cd.getTo());
			}
			if (!graph.containsEdge(cd.getFrom(), cd.getTo())) {
				graph.addEdge(cd.getFrom(), cd.getTo());
			}
		}
		final List<String> portObjects = new LinkedList<String>();
		TopologicalOrderIterator<String, DefaultEdge> iter = new TopologicalOrderIterator<String, DefaultEdge>(graph);
		iter.addTraversalListener(new TraversalListenerAdapter<String, DefaultEdge>() {
			@Override
			public void vertexTraversed(VertexTraversalEvent<String> a) {
				String v = a.getVertex();
				boolean added = false;
				Collection<DefaultEdge> vertexes = graph.incomingEdgesOf(v);
				Iterator<DefaultEdge> viter = vertexes.iterator();
				int currCount = 0;
				int maxParent = -1;
				for (int k = 0; k < vertexes.size(); k++) {
					DefaultEdge ed = viter.next();
					String from = graph.getEdgeSource(ed);
					int lastParent = portObjects.indexOf(from);
					if (maxParent < lastParent) {
						maxParent = lastParent;
					}
					if (lastParent > -1) {
						currCount++;
						if (currCount == vertexes.size()) {
							if (maxParent == portObjects.size()) {
								added = true;
								portObjects.add(v);
							} else {
								portObjects.add(maxParent + 1, v);
								added = true;
								break;
							}
						}
					} else {
						// System.out.println("This should not come here topological sort failed");
					}
				}
				if (!added) {
					portObjects.add(v);
				}
			}
		});
		while (iter.hasNext()) {
			iter.next();
		}
		Iterator<String> iter2 = portObjects.iterator();
		while (iter2.hasNext()) {
			String port = iter2.next();
			// System.out.println(port);
			ret.add(port);
		}
		/*
		 * Calculate strands List<List<String>> strands = new
		 * LinkedList<List<String>>(); String lastV = null;
		 * List<String>strandColl = new LinkedList<String>();
		 * strands.add(strandColl); for(String s: ret){ if(lastV==null){ lastV =
		 * s; strandColl.add(s); }else{ boolean strand = false;
		 * Set<DefaultEdge>edges = graph.edgesOf(s); for(DefaultEdge ed:edges){
		 * String source =graph.getEdgeSource (ed); if(!source.equals(s)){
		 * if(source.equals(lastV)){ strand = true; } } } if(strand==true){
		 * strandColl.add(s); }else{ strandColl = new LinkedList<String>();
		 * strandColl.add(s); strands.add(strandColl); } lastV = s; } }
		 */
		return ret;
	}

	public static String runRemoteCommand(String url, int port, String user, String password, String cmd) throws IOException {
		ch.ethz.ssh2.Connection conn = null;
		ch.ethz.ssh2.Session sess = null;
		conn = new ch.ethz.ssh2.Connection(url, port);
		conn.connect();
		String[] mtds = conn.getRemainingAuthMethods(user);
		boolean isAuthenticated = conn.authenticateWithPassword(user, password);
		if (isAuthenticated == false) {
			throw new IOException("Authentication failed.");
		}
		sess = conn.openSession();
		sess.execCommand(cmd);
		InputStream stdout = new StreamGobbler(sess.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		StringBuilder sb = new StringBuilder();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
		}
		return sb.toString();
	}

	public static String runRemoteCommandInteractive(String url, int port, String user, final String password, String cmd) throws IOException {
		ch.ethz.ssh2.Connection conn = null;
		ch.ethz.ssh2.Session sess = null;
		conn = new ch.ethz.ssh2.Connection(url, port);
		conn.connect();
		boolean isAuthenticated = conn.authenticateWithKeyboardInteractive(user, new InteractiveCallback() {

			@Override
			public String[] replyToChallenge(String paramString1, String paramString2, int numPrompts, String[] paramArrayOfString, boolean[] paramArrayOfBoolean) throws Exception {
				String[] responses = new String[numPrompts];
				for (int i = 0; i < numPrompts; i++) {
					responses[i] = password;
				}
				return responses;
			}

		});
		if (isAuthenticated == false) {
			throw new IOException("Authentication failed.");
		}
		sess = conn.openSession();
		sess.execCommand(cmd);
		InputStream stdout = new StreamGobbler(sess.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		StringBuilder sb = new StringBuilder();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
		}
		return sb.toString();
	}

	public static Set<String> getNextPossibleStrings(Set<String> keys, String search) {
		Set<String> c = new TreeSet<String>();
		String temp = search;
		int index = 0;
		while (index != search.length()) {
			index = index + 1;
			for (String t : keys) {
				if (t.startsWith(temp)) {
					c.add(t);
				}
			}
			temp = search.substring(index, search.length());
		}
		return c;
	}

	public static CmdOutput runCmd(String s, String wdir) throws ExecuteException, IOException {
		int exitValue = -1;
		String line = s;
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		PumpStreamHandler handler = new PumpStreamHandler(stdout, stderr);
		executor.setStreamHandler(handler);
		executor.setWorkingDirectory(new File(wdir));
		exitValue = executor.execute(cmdLine);
		CmdOutput res = new CmdOutput();
		res.setExitValue(exitValue);
		res.setOut(stdout.toString());
		res.setErr(stderr.toString());
		return res;
	}

	public CmdOutput runCmd(String s, String wdir, String tempDir, String name) throws ExecuteException, IOException {
		int exitValue = -1;
		String line = s;
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		String outfile = tempDir + "/" + "out_" + name + "_" + CommonUtil.getRandomString(12) + ".tmp";
		String errfile = tempDir + "/" + "err_" + name + "_" + CommonUtil.getRandomString(12) + ".tmp";
		FileOutputStream stdout = new FileOutputStream(new File(outfile));
		FileOutputStream stderr = new FileOutputStream(new File(errfile));
		PumpStreamHandler handler = new PumpStreamHandler(stdout, stderr);
		executor.setStreamHandler(handler);
		executor.setWorkingDirectory(new File(wdir));
		exitValue = executor.execute(cmdLine);
		CmdOutput res = new CmdOutput();
		res.setExitValue(exitValue);
		res.setOut(stdout.toString());
		res.setErr(stderr.toString());
		return res;
	}

	/********************************************************
 * 
 ********************************************************/


	public static PortObj getMainPortObject(FPGraph2 graph, PortObj po) {
		PortObj portObj = null;
		for (AnonDefObj d : graph.getAnonDefs()) {
			if (d.getId().equals(po.getGrp())) {
				if (d.getMainPort() != null) {
					return d.getMainPort();
				}
				return getMainPortObject(graph, d);
			}
			// System.out.println(d.getId());
		}
		return null;
	}

	public static PortObj getMainPortObject(FPGraph2 graph, AnonDefObj d) {
		PortObj portObj;
		if (d.getMainPort() != null) {
			return d.getMainPort();
		}
		List<String> ports = new ArrayList<String>();
		String ptype = "input";
		{
			ports.addAll(d.getInputs());
			ports.addAll(d.getOutputs());
		}

		if (ports != null) {
			for (String b : ports) {
				String pname = "inp_" + d.getId() + "." + b;
				portObj = graph.getPorts().get(pname);
				if (portObj == null) {
					pname = "out_" + d.getId() + "." + b;
					portObj = graph.getPorts().get(pname);
				}
				if (portObj == null) {
					pname = "aux_" + d.getId() + "." + b;
					portObj = graph.getPorts().get(pname);
				}
				/* is top port in hier */
				if (portObj != null && portObj.getDtype() != null) {
					if ((portObj.getGrp() + "_" + portObj.getDtype()).equals(portObj.getPortname())) {
						// System.out.println(b + ":" + portObj.getPortname());
						d.setMainPort(portObj);// we cache it with the anon
						return portObj;
					}
				}
			}
		}
		return null;
	}

	public static SubGraph subGraphFromGraph(FPGraph2 g) {
		SubGraph subGraph = new SubGraph();

		DirectedSparseMultigraph<AnonDefObj, ConnDef> graph = new DirectedSparseMultigraph<AnonDefObj, ConnDef>();

		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : g.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
			graph.addVertex(def);
		}
		for (ConnDef cd : g.getForward().values()) {
			PortObj from = g.getPorts().get(cd.getFrom());
			PortObj to = g.getPorts().get(cd.getTo());
			if (from != null && to != null) {
				// System.out.println("Adding edge: " + from.getId() + "->" +
				// to.getId());
				ConnDef conndef = new ConnDef();
				conndef.setFrom(from.getGrp());
				conndef.setTo(to.getGrp());
				graph.addEdge(conndef, anonCompMap.get(from.getGrp()), anonCompMap.get(to.getGrp()));
			} else {
				AnonDefObj a1 = anonCompMap.get(cd.getFrom());
				AnonDefObj a2 = anonCompMap.get(cd.getTo());
				if (a1 != null && a2 != null) {
					ConnDef conndef = new ConnDef();
					conndef.setFrom(cd.getFrom());
					conndef.setTo(cd.getTo());
					graph.addEdge(conndef, a1, a2);
				}
			}

		}
		Map<String, AnonDefObj> s = new HashMap<String, AnonDefObj>();
		Map<String, AnonDefObj> d = new HashMap<String, AnonDefObj>();

		for (AnonDefObj o : graph.getVertices()) {
			if (graph.getInEdges(o).size() == 0) {
				s.put(o.getId(), o);
			}
			if (graph.getOutEdges(o).size() == 0) {
				d.put(o.getId(), o);
			}
		}
		List<String> inputPorts = new ArrayList<String>();
		List<String> outputPorts = new ArrayList<String>();
		List<String> auxPorts = new ArrayList<String>();
		for (AnonDefObj ad : s.values()) {
			for (String str : ad.getInputs()) {
				String pid = "inp_" + ad.getId() + "." + str;
				PortObj po = g.getPorts().get(pid);
				if (po != null) {
					inputPorts.add(po.getId());
				}
			}
		}
		for (AnonDefObj ad : s.values()) {
			for (String str : ad.getAux()) {
				String pid = "aux_" + ad.getId() + "." + str;
				PortObj po = g.getPorts().get(pid);
				if (po != null) {
					auxPorts.add(po.getId());
				}
			}
		}

		for (AnonDefObj ad : d.values()) {
			for (String str : ad.getOutputs()) {
				String pid = "out_" + ad.getId() + "." + str;
				PortObj po = g.getPorts().get(pid);
				if (po != null) {
					outputPorts.add(po.getId());
				}
			}
		}
		subGraph.setInputPorts(inputPorts);
		subGraph.setOutputPorts(outputPorts);
		subGraph.setAuxPorts(auxPorts);
		return subGraph;
	}

	public static String formatMessage(String... msg) {
		MessageFormat mf = new MessageFormat(msg[0]);
		return mf.format(msg);
	}

	public static void listToHierarchial(Map<String, PropInfo> props, Transformer parentExtractor, Transformer parentExtractor2) {
		for (Map.Entry<String, PropInfo> en : props.entrySet()) {
			if (!en.getValue().getName().equals(en.getValue().getGroup() + "_" + en.getValue().getPropClass())) {
				PropInfo p = props.get(parentExtractor.transform(en));
				if (p != null) {
					en.getValue().setParent(p);
					p.getChilds().add(en.getValue());
				} else {
					p = props.get(parentExtractor2.transform(en));
					if (p != null) {
						en.getValue().setParent(p);
						p.getChilds().add(en.getValue());
					} else {
						// System.out.println("Parent is null!!!");
					}
				}
			}
		}
	}

	public static void getPropInfos(FPGraph2 o, Map<String, PropInfo> props, List<PropInfo> roots, List<AnonDefObj> types) {
		for (AnonDefObj def : types) {
			PortObj pmain = CommonUtil.getMainPortObject(o, def);
			if (pmain != null) {
				for (String s : def.getOutputs()) {
					PortObj po = o.getPorts().get("out_" + def.getId() + "." + s);
					PropInfo<PropInfo> p = new PropInfo<PropInfo>();
					if (po.getId().equals(pmain.getId())) {
						p.setName(po.getPortname());
						p.setGroup(po.getGrp());
						roots.add(p);
					} else {
						p.setName(StringUtils.substringAfterLast(po.getPortname(), "."));
						p.setGroup(po.getGrp());
					}
					String dtype = po.getDtype();
					String[] parts = StringUtils.split(dtype, "/");
					if (parts.length > 1) {
						String type = parts[0];
						PropCollType colltype = PropCollType.valueOf(parts[1]);
						p.setPropClass(type);
						p.setCollType(colltype);
					} else {
						p.setPropClass(dtype);
						p.setCollType(PropCollType.Single);
					}
					List<Object> vals = new ArrayList<Object>();
					int count = 0;
					for (ConnDef cd : o.getForward().values()) {
						if (cd.getTo().equals(po.getId())) {
							count++;
							Object portObj = o.getPorts().get(cd.getFrom());
							if (portObj == null) {
								portObj = o.getFunctionPoints().get(cd.getFrom());
							}
							vals.add(portObj);
							// vals.add(o.getPorts().get(cd.getFrom()));
						}
					}
					for (ConnDef cd : o.getForward().values()) {
						if (cd.getTo().equals(po.getId())) {
							int seqId = Integer.valueOf(cd.getSequence());
							if (seqId < count) {
								Object portObj = o.getPorts().get(cd.getFrom());
								if (portObj == null) {
									portObj = o.getFunctionPoints().get(cd.getFrom());
								}
								vals.set(seqId, portObj);
							} else {
								// System.out.println("Sequence id of " +
								// cd.getId() + " is greater then count: " +
								// count);
							}

						}
					}
					if (vals.size() > 0) {
						p.setVal(vals);
					} else if (po.getPortval() != null) {
						p.setVal(po.getPortval());
					}

					props.put(po.getPortname(), p);
				}
			}
		}
	}

	public static void childFirstDepthFirstIterate(PropInfo<PropInfo> p, org.apache.commons.collections15.Closure<PropInfo<PropInfo>> procClosure) {
		// System.out.println("generateClass:" + p.getName());
		if (p.getChilds().size() == 0) {
			procClosure.execute(p);
		} else {
			for (PropInfo c : p.getChilds()) {
				childFirstDepthFirstIterate(c, procClosure);
			}
			procClosure.execute(p);
		}
	}

	public static org.apache.commons.collections15.Closure<PropInfo<PropInfo>> getMethodClosure(String mtdName, Object thisObject) {
		org.apache.commons.collections15.Closure<PropInfo<PropInfo>> procClosure = null;
		try {
			Method[] mtds = thisObject.getClass().getMethods();
			for (Method mtd : mtds) {
				if (mtd.getName().equals(mtdName)) {
					procClosure = new MethodClosure<PropInfo<PropInfo>>(thisObject, mtd);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (procClosure == null) {
			// System.out.println("Could not find a suitable method");
			System.exit(-1);
		}
		return procClosure;
	}

	public static GenericApplicationContext getDynamicSpringConfig() {
		return dynaSpringCtx;
	}

	public static List<AnonDefObj> topologicallySortAnonDefs(FPGraph2 o) {
		final DirectedMultigraph<String, DefaultEdge> graph = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
		final Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : o.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
			graph.addVertex(def.getId());
		}
		for (ConnDef cd : o.getForward().values()) {
			PortObj from = o.getPorts().get(cd.getFrom());
			PortObj to = o.getPorts().get(cd.getTo());
			if (from != null && to != null) {
				// System.out.println("Adding edge: " + from.getId() + "->" +
				// to.getId());
				graph.addEdge(from.getGrp(), to.getGrp());
			} else {
				AnonDefObj a1 = anonCompMap.get(cd.getFrom());
				AnonDefObj a2 = anonCompMap.get(cd.getTo());
				if (a1 != null && a2 != null) {
					graph.addEdge(a1.getId(), a2.getId());
				}
			}

		}
		final List<AnonDefObj> retList = new LinkedList<AnonDefObj>();
		TopologicalOrderIterator<String, DefaultEdge> iter = new TopologicalOrderIterator<String, DefaultEdge>(graph);
		iter.addTraversalListener(new TraversalListenerAdapter<String, DefaultEdge>() {
			@Override
			public void vertexTraversed(VertexTraversalEvent<String> a) {
				retList.add(anonCompMap.get(a.getVertex()));
			}
		});
		while (iter.hasNext()) {
			iter.next();
		}
		return retList;
	}

	public static List<Integer> findGraphPartitions(DirectedMultigraph<String, DefaultEdge> g, List<String> ports) {
		List<Integer> pnodes = new ArrayList<Integer>(60);
		for (int i = ports.size() - 1; i > -1; i--) {
			boolean allBefore = true;
			for (int j = i - 1; j > -1; j--) {
				String v = ports.get(j);
				Set<DefaultEdge> edges = g.outgoingEdgesOf(v);
				for (DefaultEdge d : edges) {
					String t = g.getEdgeTarget(d);
					if (ports.indexOf(t) > i) {
						allBefore = false;
					}
				}
			}
			if (allBefore) {
				pnodes.add(0, i);
			}
		}
		// System.out.println("-----------------------");
		for (Integer i : pnodes) {
			// System.out.println(i + " : " + ports.get(i));
		}
		List<Integer> breaks = new ArrayList<Integer>(pnodes.size());
		int lastBreak = pnodes.get(0);
		for (int i = 1; i < pnodes.size(); i++) {
			int j = pnodes.get(i);
			if (j - lastBreak == 1)/* is consecutive */{
				// lastBreak = j;
			} else {
				breaks.add(lastBreak);
				lastBreak = j;
			}
		}
		if (!breaks.contains(lastBreak)) {
			breaks.add(lastBreak);
		}
		breaks.remove(breaks.indexOf(0));
		breaks.remove(breaks.indexOf(ports.size() - 1));
		return breaks;
	}

	public static void fixMultipleStarts(final DirectedMultigraph<String, DefaultEdge> g, org.apache.commons.collections15.Predicate<Object> p) {
		List<String> sources = new ArrayList<String>();
		for (String s : g.vertexSet()) {
			if (g.inDegreeOf(s) == 0 && (p == null || p != null && p.evaluate(s))) {
				sources.add(s);
			}
		}
		/* if(sources.size()>1) */
		{
			String startNodeName = "STARTNODE";
			g.addVertex(startNodeName);
			for (String s : sources) {
				g.addEdge(startNodeName, s);
			}
		}
	}

	public static void fixMultipleEnds(final DirectedMultigraph<String, DefaultEdge> g, org.apache.commons.collections15.Predicate<Object> p) {
		List<String> ends = new ArrayList<String>();
		for (String s : g.vertexSet()) {
			if (g.outDegreeOf(s) == 0 && (p == null || p != null && p.evaluate(s))) {
				ends.add(s);
			}
		}
		/* if(ends.size()>1) */
		{
			String startNodeName = "ENDNODE";
			g.addVertex(startNodeName);
			for (String s : ends) {
				g.addEdge(s, startNodeName);
			}
		}
	}

	public static void removeNonRoot(Map<String, PNode> pnodes, DirectedMultigraph<String, DefaultEdge> g) {
		List<String> toRem = new ArrayList<String>(pnodes.size());
		for (Map.Entry<String, PNode> en : pnodes.entrySet()) {
			if (g.inDegreeOf(en.getKey()) != 0) {
				toRem.add(en.getKey());
			}
		}
		for (String s : toRem) {
			pnodes.remove(s);
		}
	}

	/*
	 * coverts a graph to hierarchial set of nodes. A nodes outgoing paths are
	 * considered childs not siblings in the pnodes.
	 */
	public static int graphToHierPNodes(final DirectedMultigraph<String, DefaultEdge> g, List<String> topo, int i, Map<String, PNode> pnodes) {
		List<String> toProcess = new LinkedList<String>();
		Stack<String> forks = new Stack<String>();
		forks.push(topo.get(i));// >>
		// System.out.println("Current: " + forks);
		while (true) {
			String s = topo.get(i);
			final PNode pnode = pnodes.get(s) == null ? new PNode() : pnodes.get(s);
			pnode.setName(s);
			pnodes.put(s, pnode);
			// System.out.println("Simple: " + s);
			Set<DefaultEdge> obj = g.outgoingEdgesOf(s);
			if (obj.size() == 1) {
				Iterator<DefaultEdge> di = obj.iterator();
				DefaultEdge de = di.next();
				String target = g.getEdgeTarget(de);
				i = topo.indexOf(target);
				// System.out.println("Jumping to: " + target);
				PNode childNode = pnodes.get(target);
				if (childNode == null) {
					childNode = new PNode();
					childNode.setName(target);
					pnodes.put(target, childNode);
				}
				pnode.addChildUniq(childNode);
				childNode.addParentUniq(pnode);
			} else if (obj.size() == 0) {
				// System.out.println("Last item: " + s);
				// System.out.println("Finished fork: " + forks.pop());
				if (toProcess.size() > 0) {
					int l = toProcess.size() - 1;
					String item = toProcess.get(l);
					// System.out.println("Will jump to stack: " + item);
					forks.push(item);// >>>
					// System.out.println("Current: " + forks);// >>>
					// System.out.println("Remaining: " + toProcess);// >>>
					i = topo.indexOf(item);
					toProcess.remove(l);
				} else {
					break;
				}
			} else if (obj.size() > 1) {
				// System.out.println("Fork at: " + s);
				Iterator<DefaultEdge> di = obj.iterator();
				while (di.hasNext()) {
					DefaultEdge de = di.next();
					String target = g.getEdgeTarget(de);
					i = topo.indexOf(target);
					// System.out.println("Adding to stack: " + target);
					toProcess.add(target);
					PNode childNode = pnodes.get(target);
					if (childNode == null) {
						childNode = new PNode();
						childNode.setName(target);
						pnodes.put(target, childNode);
					}
					pnode.addChildUniq(childNode);
					childNode.addParentUniq(pnode);
				}
				if (toProcess.size() > 0) {
					int l = toProcess.size() - 1;
					String item = toProcess.get(l);
					// System.out.println("Will jump to stack: " + item);
					forks.push(item);// >>>
					// System.out.println("Current: " + forks);// >>
					// System.out.println("Remaining: " + toProcess);// >>>
					i = topo.indexOf(item);
					toProcess.remove(l);
				}
			}

		}
		return i;
	}

	/*
	 * return list of connections that are part of : 1) Loopbacks 2)Hyperjumps :
	 * jump form one if activity to another if activity like a long jump
	 * 3)Forward jumps: Jump to activity that is part of another if, while or
	 * any other kind of block.
	 */
	public static List<ConnDef> getLoops(final DirectedMultigraph<String, DefaultEdge> g) {
		final DirectedMultigraph<String, DefaultEdge> orig = (DirectedMultigraph<String, DefaultEdge>) g.clone();
		final List<ConnDef> torem = new ArrayList<ConnDef>();
		final Set<String> filter = new HashSet<String>();
		final Set<String> done = new HashSet<String>();
		final DepthFirstIterator<String, DefaultEdge> iter = new DepthFirstIterator<String, DefaultEdge>(g, "STARTNODE");
		final LinkedHashSet<String> stack = new LinkedHashSet<String>();
		iter.addTraversalListener(new TraversalListener<String, DefaultEdge>() {
			@Override
			public void vertexTraversed(VertexTraversalEvent<String> arg0) {
				// System.out.println("Traveresed: " + arg0.getVertex());
				filter.add(arg0.getVertex());
				String v = arg0.getVertex();
				Set<DefaultEdge> outs = g.outgoingEdgesOf(v);
				for (DefaultEdge d : outs) {
					String out = g.getEdgeTarget(d);
					boolean isLast = g.outgoingEdgesOf(out).size() == 0;
					if (done.contains(out)) {
						if (isLast) {
							// System.out.println("Need to add forward virtual for: "
							// + out);
							ConnDef cdef = new ConnDef();
							cdef.setFrom(v);
							cdef.setTo(out);
							cdef.setConnCond("forward");
							// torem.add(cdef);
						} else {
							// System.out.println("Need to add hyperjump for: "
							// + out);
							ConnDef cdef = new ConnDef();
							cdef.setFrom(v);
							cdef.setTo(out);
							cdef.setConnCond("hyperjump");
							// torem.add(cdef);
						}
					}
				}
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<String> arg0) {
				String v = arg0.getVertex();
				// System.out.println("Finished: " + v);
				stack.remove(v);
				filter.remove(v);

				Set<DefaultEdge> outs = g.outgoingEdgesOf(v);
				for (DefaultEdge d : outs) {
					String out = g.getEdgeTarget(d);
					if (filter.contains(out)) {
						// System.out.println("Need to add loopback virtual for: "
						// + out);
						ConnDef cdef = new ConnDef();
						cdef.setFrom(v);
						cdef.setTo(out);
						cdef.setConnCond("loopback");
						torem.add(cdef);
					}
				}
				done.add(arg0.getVertex());
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> arg0) {
				stack.add(g.getEdgeSource(arg0.getEdge()));
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
			}

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {

			}
		});

		while (iter.hasNext()) {
			String df = iter.next();
			// System.out.println("-------------------------");
			// System.out.println(df);
			// System.out.println(stack);
		}
		return torem;
	}

	public static List<ConnDef> getFeedBackLoops(final DirectedMultigraph<String, DefaultEdge> g) {
		final DirectedMultigraph<String, DefaultEdge> orig = (DirectedMultigraph<String, DefaultEdge>) g.clone();
		final List<ConnDef> torem = new ArrayList<ConnDef>();
		final Set<String> filter = new HashSet<String>();
		final Set<String> done = new HashSet<String>();
		final DepthFirstIterator<String, DefaultEdge> iter = new DepthFirstIterator<String, DefaultEdge>(g, "STARTNODE");
		final LinkedHashSet<String> stack = new LinkedHashSet<String>();
		iter.addTraversalListener(new TraversalListener<String, DefaultEdge>() {
			@Override
			public void vertexTraversed(VertexTraversalEvent<String> arg0) {
				 System.out.println("Traveresed: " + arg0.getVertex());
				filter.add(arg0.getVertex());
				String v = arg0.getVertex();
				Set<DefaultEdge> outs = g.outgoingEdgesOf(v);
				for (DefaultEdge d : outs) {
					String out = g.getEdgeTarget(d);
					boolean isLast = g.outgoingEdgesOf(out).size() == 0;
					if (done.contains(out)) {
						if (isLast) {
							System.out.println("Need to add forward virtual for: " + out);
							ConnDef cdef = new ConnDef();
							cdef.setFrom(v);
							cdef.setTo(out);
							cdef.setConnCond("forward");
							// torem.add(cdef);
						} else {
							System.out.println("Need to add hyperjump for: " + out);
							ConnDef cdef = new ConnDef();
							cdef.setFrom(v);
							cdef.setTo(out);
							cdef.setConnCond("hyperjump");
							// torem.add(cdef);
						}
					}
				}
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<String> arg0) {
				String v = arg0.getVertex();
			    System.out.println("Finished: " + v);
				stack.remove(v);
				filter.remove(v);

				Set<DefaultEdge> outs = g.outgoingEdgesOf(v);
				for (DefaultEdge d : outs) {
					String out = g.getEdgeTarget(d);
					if (filter.contains(out)) {
						System.out.println("Need to add loopback virtual for: " + out);
						ConnDef cdef = new ConnDef();
						cdef.setFrom(v);
						cdef.setTo(out);
						cdef.setConnCond("loopback");
						torem.add(cdef);
					}
				}
				done.add(arg0.getVertex());
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
			}

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {

			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> arg0) {
				stack.add(g.getEdgeSource(arg0.getEdge()));
			}
		});

		while (iter.hasNext()) {
			String df = iter.next();
			// System.out.println("-------------------------");
			// System.out.println(df);
			// System.out.println(stack);
		}
		return torem;
	}

	public static void convertLoopsToVirtualActivity(final DirectedMultigraph<String, DefaultEdge> g, List<ConnDef> torem) {
		int index = 0; // to make sure we have unique jumps
		for (ConnDef cdef : torem) {
			// System.out.println("Going to fix: " + cdef.getFrom() + "->" +
			// cdef.getTo());
			g.removeEdge(cdef.getFrom(), cdef.getTo());
			String virtualTo = null;
			if (cdef.getConnCond().equals("loopback") || cdef.getConnCond().equals("hyperjump")) {
				virtualTo = "$" + cdef.getTo() + "$" + index;
			} else {
				virtualTo = "#" + cdef.getTo() + "#" + index;
			}
			g.addVertex(virtualTo);
			g.addEdge(cdef.getFrom(), virtualTo);

			index++;
		}
	}

	public static List<String> topologicallySort(final DirectedMultigraph<String, DefaultEdge> g) {
		TopologicalOrderIterator<String, DefaultEdge> titer = new TopologicalOrderIterator<String, DefaultEdge>(g);
		TopologicalDepthFirstIterator<String, DefaultEdge> trav = new TopologicalDepthFirstIterator<String, DefaultEdge>(g);
		titer.addTraversalListener(trav);
		while (titer.hasNext()) {
			titer.next();
		}
		List<String> ports = trav.getPortObjects();
		for (int i = 0; i < ports.size(); i++) {
			String port = ports.get(i);
			if (port.startsWith("$")) {
				String[] part = StringUtils.split(port, "$");
				// ports.set(i, part[0]);
				// System.out.println(part[0] + "*");
			} else {
				// System.out.println(port);
			}
		}
		return ports;
	}

	public static Map<String, AnonDefObj> prepareAnonMap(List<AnonDefObj> types) {
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : types) {
			anonCompMap.put(def.getId(), def);
		}
		return anonCompMap;
	}

	public static void childFirstDepthFirstIterateAll(List<PropInfo> roots, org.apache.commons.collections15.Closure<PropInfo<PropInfo>> nodeProcessor) {
		for (PropInfo p : roots) {
			CommonUtil.childFirstDepthFirstIterate(p, nodeProcessor);
		}
	}

	public static void childFirstDepthFirstIterateInOrder(List<AnonDefObj> sorted, List<PropInfo> roots, org.apache.commons.collections15.Closure<PropInfo<PropInfo>> nodeProcessor) {
		for (AnonDefObj d : sorted) {
			for (PropInfo p : roots) {
				if (p.getGroup().equals(d.getId())) {
					CommonUtil.childFirstDepthFirstIterate(p, nodeProcessor);
				}
			}
		}
	}

	public static Map<Integer, Set<PNode>> setPnodeLevels(Map<String, PNode> pnodes, DirectedMultigraph<String, DefaultEdge> g) {
		Map<Integer, Set<PNode>> gByLevel = new LinkedHashMap<Integer, Set<PNode>>();
		BreadthFirstIterator<String, DefaultEdge> bfgi = new BreadthFirstIterator<String, DefaultEdge>(g);
		while (bfgi.hasNext()) {
			String nxt = bfgi.next();
			PNode p = pnodes.get(nxt);
			if (p == null) {
				// System.out.println("Could not find : " + nxt);
			} else {
				p.setLevel(getMaxLevel(p.getParents()) + 1);
				Set<PNode> pg = gByLevel.get(p.getLevel());
				if (pg == null) {
					pg = new HashSet<PNode>();
					gByLevel.put(p.getLevel(), pg);
				}
				pg.add(p);
				//
				for (PNode c : p.getChilds()) {
					if (c.getParents().size() > 0) {
						int old = c.getLevel();
						c.setLevel(getMaxLevel(c.getParents()) + 1);
					}
				}
			}

		}
		return gByLevel;
	}

	public static int getMaxLevel(List<PNode> parents) {
		int max = 0;
		if (parents != null) {
			for (PNode p : parents) {
				if (max <= p.getLevel()) {
					max = p.getLevel();
				}
			}
		}
		return max;
	}

	public static Multimap<String, String> covertAllFPGRaphItemsToJGrapht(FPGraph2 g, final DirectedMultigraph<String, DefaultEdge> graph, org.apache.commons.collections15.Predicate<Object> p) {
		com.google.common.collect.Multimap<String, String> ret = LinkedHashMultimap.create();
		for (ConnDef cd : g.getForward().values()) {
			if (p == null || p != null && p.evaluate(cd.getFrom())) {
				if (!graph.containsVertex(cd.getFrom())) {
					graph.addVertex(cd.getFrom());
				}
			}
			if (p == null || p != null && p.evaluate(cd.getTo())) {
				if (!graph.containsVertex(cd.getTo())) {
					graph.addVertex(cd.getTo());
				}
			}
			if (p == null || p != null && p.evaluate(cd.getFrom()) && p != null && p.evaluate(cd.getTo())) {
				if (!graph.containsEdge(cd.getFrom(), cd.getTo())) {
					graph.addEdge(cd.getFrom(), cd.getTo());
				}
			}
			if (p == null || p != null && p.evaluate(cd.getFrom()) && p != null && p.evaluate(cd.getTo())) {
				if (cd.getCtype().equalsIgnoreCase("dependency")) {
					ret.put(cd.getFrom(), cd.getTo());
				}
			}
		}
		return ret;
	}

	public static Multimap<String, String> covertAllFPGRaphItemsToJGrapht(FPGraph2 g, final DirectedMultigraph<String, DefaultEdge> graph) {
		return covertAllFPGRaphItemsToJGrapht(g, graph, null);
	}

	
	public static Multimap<String, String> covertFPGRaphToJGrapht(FPGraph2 g, final DirectedMultigraph<String, DefaultEdge> graph) {
		com.google.common.collect.Multimap<String, String> ret = LinkedHashMultimap.create();
		Map<String, AnonDefObj> anonCompMap = CommonUtil.prepareAnonMap(g.getAnonDefs());
		Map<String, String> filter = new HashMap<String, String>();

		
		
		
		for (ConnDef cd : g.getForward().values()) {
			PortObj from = g.getPorts().get(cd.getFrom());
			PortObj to = g.getPorts().get(cd.getTo());
			if (from != null && to != null ) {
				String f = from.getGrp();
				AnonDefObj fa = anonCompMap.get(f);
				String t = to.getGrp();
				AnonDefObj ta = anonCompMap.get(t);
				String filterKey = f + "->" + t;
				if (!filter.containsKey(filterKey)) {
					if (!fa.equals(ta)) {
						if (cd.getCtype().equalsIgnoreCase("dependency")) {
							ret.put(ta.getId(), fa.getId());
						}
						{
							graph.addVertex(fa.getId());
							graph.addVertex(ta.getId());
							graph.addEdge(fa.getId(), ta.getId());
						}
					}
					filter.put(filterKey, filterKey);
				}
			} else if (from == null || to == null) {/* add from anon def */
				AnonDefObj ta = null;
				AnonDefObj fa = null;
				if (from != null) {
					String f = from.getGrp();
					fa = anonCompMap.get(f);
				} else {
					fa = anonCompMap.get(cd.getFrom());
				}
				if (to != null) {
					String t = to.getGrp();
					ta = anonCompMap.get(t);
				} else {
					ta = anonCompMap.get(cd.getTo());
				}
				if (fa != null & ta != null) {
					String filterKey = fa.getId() + "->" + ta.getId();
					if (!filter.containsKey(filterKey)) {
						if (cd.getCtype().equalsIgnoreCase("dependency")) {
							ret.put(ta.getId(), fa.getId());
						}
						{
							graph.addVertex(fa.getId());
							graph.addVertex(ta.getId());
							graph.addEdge(fa.getId(), ta.getId());
						}
						filter.put(filterKey, filterKey);
					}
				} else {
					if (fa == null && ta != null) {
						String fromItem = cd.getFrom();
						StepObj st = g.getSteps().get(fromItem);
						if (st != null) {
							Map<String, Set<AnonDefObj>> steps = getTasksByStepsImplicit(g, g.getSteps());
							for (AnonDefObj s : steps.get(fromItem)) {
								ret.put(ta.getId(), s.getId());
							}
						} else {
							JSONObject sub = g.getOrphans().get(fromItem);
							if (sub != null) {
								if (sub != null && sub.getString("type").equalsIgnoreCase("Region")) {
									JSONArray ele = sub.getJSONArray("items");
									for (Object o : ele) {
										String str = (String) o;
										AnonDefObj ao = anonCompMap.get(str);
										if (ao != null) {
											ret.put(ta.getId(), ao.getId());
										}
									}
								}
							} else {
								Group grp = g.getGroups().get(fromItem);
								if (grp != null) {
									for (String str : grp.getItems()) {
										AnonDefObj ao = anonCompMap.get(str);
										if (ao != null) {
											ret.put(ta.getId(), ao.getId());
										}
									}

								}
							}
						}
					}
				}
			}
		}
		for (AnonDefObj o : anonCompMap.values()) {
			if (!graph.containsVertex(o.getId())) {
				graph.addVertex(o.getId());
			}
		}

		return ret;
	}

	public static Map<String, Set<AnonDefObj>> getTasksByStepsImplicit(FPGraph2 o, Map<String, StepObj> steps) {
		return getTaskBySteps(o, steps, false);
	}

	public static Map<String, Set<AnonDefObj>> getTasksByStepsExplicit(FPGraph2 o, Map<String, StepObj> steps) {
		return getTaskBySteps(o, steps, true);
	}

	private static Map<String, Set<AnonDefObj>> getTaskBySteps(FPGraph2 o, Map<String, StepObj> steps, boolean explicit) {
		List<StepObj> hSteps = new ArrayList<StepObj>(20);
		List<StepObj> vSteps = new ArrayList<StepObj>(20);
		for (Map.Entry<String, StepObj> stepEn : steps.entrySet()) {
			StepObj step = stepEn.getValue();
			if (step.getSteptype().equals(StepObj.StepType.horiz.name())) {
				hSteps.add(stepEn.getValue());
			}
			if (step.getSteptype().equals(StepObj.StepType.vert.name())) {
				vSteps.add(stepEn.getValue());
			}
		}
		java.util.Collections.sort(hSteps, new Comparator<StepObj>() {
			@Override
			public int compare(StepObj a, StepObj b) {
				return a.getY() - b.getY();
			}
		});
		java.util.Collections.sort(vSteps, new Comparator<StepObj>() {
			@Override
			public int compare(StepObj a, StepObj b) {
				return a.getX() - b.getX();
			}
		});
		Map<String, Set<AnonDefObj>> tasksBySteps = new LinkedHashMap<String, Set<AnonDefObj>>();
		for (AnonDefObj a : o.getAnonDefs()) {
			List<StepObj> vsteps = getStep(vSteps, a.getX(), a.getY(), StepType.vert, explicit);
			List<StepObj> hsteps = getStep(hSteps, a.getX(), a.getY(), StepType.horiz, explicit);
			for (StepObj vstep : vsteps) {
				if (vstep != null) {
					Set<AnonDefObj> vtasks = tasksBySteps.get(vstep.getId());
					if (vtasks == null) {
						vtasks = new LinkedHashSet<AnonDefObj>();
						tasksBySteps.put(vstep.getId(), vtasks);
					}
					vtasks.add(a);
				}
			}
			for (StepObj hstep : hsteps) {
				if (hstep != null) {
					Set<AnonDefObj> htasks = tasksBySteps.get(hstep.getId());
					if (htasks == null) {
						htasks = new LinkedHashSet<AnonDefObj>();
						tasksBySteps.put(hstep.getId(), htasks);
					}
					htasks.add(a);
				}
			}
		}
		return tasksBySteps;
	}

	public static List<StepObj> getStep(List<StepObj> steps, double ox, double oy, StepObj.StepType type, boolean explicit) {
		List<StepObj> ret = new LinkedList<StepObj>();
		StepObj last = null;
		for (StepObj s : steps) {
			if (type.equals(StepType.vert)) {
				if (ox < s.getX()) {
					if (last != null) {
						if (explicit == true) {
							if (ox > last.getX()) {
								last = s;
							}
						} else {
							ret.add(last);
							last = s;
						}
					} else {
						last = s;
					}
				}
			}
			if (type.equals(StepType.horiz)) {
				if (oy < s.getY()) {
					if (last != null) {
						if (explicit == true) {
							if (oy > last.getY()) {
								last = s;
							}
						} else {
							ret.add(last);
							last = s;
						}
					} else {
						last = s;
					}
				} else {

				}
			}
		}
		ret.add(last);
		return ret;
	}

	public static void extractAnonFromOrphans(FPGraph2 o) {
		List<TypeDefObj> types = o.getTypeDefs();
		Map<String, TypeDefObj> typeMap = new LinkedHashMap<String, TypeDefObj>();
		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		getAnonFromUIModels(o, types, typeMap, anonCompMap);
	}

	public static void getAnonFromUIModels(FPGraph2 o, List<TypeDefObj> types, Map<String, TypeDefObj> typeMap, Map<String, AnonDefObj> anonCompMap) {

		for (TypeDefObj obj : types) {
			typeMap.put(obj.getId(), obj);
		}
		for (AnonDefObj def : o.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		fixArbitConnections(o, anonCompMap);

		Pattern anonPat = Pattern.compile("([a-z_A-Z0-9 ]+)\\(([ a-z_A-Z0-9]+)\\)");

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
					while (tobj.getDtypes().size() < indx + 1) {
						tobj.getDtypes().add(null);
					}
					while (tobj.getInputs().size() < indx + 1) {
						tobj.getInputs().add(null);
					}
					tobj.getDtypes().set(indx, jo.getString("dtype"));
					String tid = null;
					tid = jo.getString("id");
					tobj.getInputs().set(indx, tid);
					// System.out.println(jo);
					typeMap.put(tobj.getId(), tobj);
				} else { /* anondef */
					String id = jo.getString("grpid");
					Matcher mat = anonPat.matcher(id);
					String uid = null;
					String ttype = null;
					if (mat.find()) {
						uid = mat.group(1);
						ttype = mat.group(2).trim();
					} else {/*
							 * sumit added on 13 aug for supporting non types
							 * obj
							 */
						uid = id;
						ttype = "";
					}
					if (uid == null || ttype == null) {
						continue;
					}
					AnonDefObj tobj = anonCompMap.get(id);
					if (tobj == null) {
						tobj = new AnonDefObj();
						tobj.setId(id);
						/* tobj.setId(uid); */
						tobj.setAnonType(ttype);
						tobj.setName(uid);
					}
					int indx = jo.getInt("index");
					String portId = null;
					portId = jo.getString("id");

					if ("input".equals(portType)) {
						while (tobj.getInputs().size() < indx + 1) {
							tobj.getInputs().add(null);
						}
						if (indx >= 0 && tobj.getInputs().get(indx) == null) {
							tobj.getInputs().set(indx, portId);
						} else {
							tobj.getInputs().add(portId);
						}
					} else if ("output".equals(portType)) {
						while (tobj.getOutputs().size() < indx + 1) {
							tobj.getOutputs().add(null);
						}
						if (indx >= 0 && tobj.getOutputs().get(indx) == null) {
							tobj.getOutputs().set(indx, portId);
						} else {
							tobj.getOutputs().add(portId);
						}
					} else if ("aux".equals(portType)) {
						while (tobj.getAux().size() < indx + 1) {
							tobj.getAux().add(null);
						}
						if (indx >= 0 && tobj.getAux().get(indx) == null) {
							tobj.getAux().set(indx, portId);
						} else {
							tobj.getAux().add(portId);
						}
					}
					/*
					 * o.getAnonDefs().add(tobj);sumit this was causing multiple
					 * instance of anondef anonCompMap.put(tobj.getId(), tobj);
					 */
					boolean found = false;
					for (AnonDefObj d : o.getAnonDefs()) {
						if (d.getId().equals(id)) {
							found = true;
							break;
						}

					}
					if (!found) {
						o.getAnonDefs().add(tobj);
					}
					anonCompMap.put(tobj.getId(), tobj);
				}

			}
			
		}
		types = new ArrayList<TypeDefObj>();
		for (TypeDefObj t : typeMap.values()) {
			types.add(t);
		}
		o.setTypeDefs(types);
		

		for (AnonDefObj aod : o.getAnonDefs()) {
			fixBadIndexedAnonDef(aod);
		}
	}

	private static void fixArbitConnections(FPGraph2 o, Map<String, AnonDefObj> anonCompMap) {
		/*
		 * get the conns that target arbit port in from or to clause
		 */
		for (Map.Entry<String, ConnDef> en : o.getForward().entrySet()) {
			ConnDef cc = en.getValue();
			PortObj t = o.getPorts().get(cc.getTo());
			PortObj f = o.getPorts().get(cc.getFrom());
			if (t != null) {
				if (t.getPorttype().equals("arbitport")) {
					cc.setTo(t.getGrp());
					if (cc.getNodes() != null && cc.getNodes().length >= 2) {
						cc.getNodes()[1] = t.getGrp();
					}
				}
			}
			if (f != null) {
				if (f.getPorttype().equals("arbitport")) {
					cc.setFrom(f.getGrp());
					if (cc.getNodes() != null && cc.getNodes().length >= 1) {
						cc.getNodes()[0] = f.getGrp();
					}

				}
			}
		}
	}

	private static void fixBadIndexedAnonDef(AnonDefObj tobj) {
		List<String> temp = new LinkedList<String>();
		for (String s : tobj.getInputs()) {
			if (s != null) {
				temp.add(s);
			}
		}
		tobj.setInputs(temp);

		temp = new LinkedList<String>();
		for (String s : tobj.getOutputs()) {
			if (s != null) {
				temp.add(s);
			}
		}
		tobj.setOutputs(temp);

		temp = new LinkedList<String>();
		for (String s : tobj.getAux()) {
			if (s != null) {
				temp.add(s);
			}
		}
		tobj.setAux(temp);
	}

	public static void mergeParent(FPGraph2 o, FPGraph2 parent) {
		if (parent != null) {
			for (AnonDefObj apar : parent.getAnonDefs()) {
				o.getAnonDefs().add(apar);
				for (String str : apar.getInputs()) {
					String pname = "inp_" + apar.getId() + "." + str;
					PortObj po = parent.getPorts().get(pname);
					if (pname != null) {
						o.getPorts().put(po.getId(), po);
					}
				}
				for (String str : apar.getOutputs()) {
					String pname = "out_" + apar.getId() + "." + str;
					PortObj po = parent.getPorts().get(pname);
					if (pname != null) {
						o.getPorts().put(po.getId(), po);
					}
				}
			}
			for (TypeDefObj apar : parent.getTypeDefs()) {
				o.getTypeDefs().add(apar);
				for (String str : apar.getInputs()) {
					String pname = "r_" + apar.getId() + "." + str;
					PortObj po = parent.getPorts().get(pname);
					if (pname != null) {
						o.getPorts().put(po.getId(), po);
					}
				}
				for (String str : apar.getInputs()) {
					String pname = "l_" + apar.getId() + "." + str;
					PortObj po = parent.getPorts().get(pname);
					if (pname != null) {
						o.getPorts().put(po.getId(), po);
					}
				}
			}
		}
	}

	public static void updateGraphicProps(Map<String, AnonDefObj> anonCompMap, Map<String, PortObj> map) {
		for (Map.Entry<String, AnonDefObj> en : anonCompMap.entrySet()) {
			AnonDefObj ad = en.getValue();
			if (ad.getX() < 1 && ad.getY() < 1 && ad.getR() < 1 && ad.getB() < 1) {
				double avgx = 0;
				double avgy = 0;
				int count = 0;
				for (String s : ad.getInputs()) {
					PortObj p = map.get("inp_" + ad.getId() + "." + s);
					avgx += p.getX();
					avgy += p.getY();
					count++;
				}
				for (String s : ad.getOutputs()) {
					PortObj p = map.get("out_" + ad.getId() + "." + s);
					avgx += p.getX();
					avgy += p.getY();
					count++;
				}
				for (String s : ad.getAux()) {
					PortObj p = map.get("aux_" + ad.getId() + "." + s);
					avgx += p.getX();
					avgy += p.getY();
					count++;
				}
				avgx = avgx / count;
				avgy = avgy / count;
				ad.setX(avgx);
				ad.setY(avgy);
			}
		}
	}

	public static void processOutputs(FPGraph2 o, Map<String, PortObj> ports, AnonDefObj anon, List<FunctionPortObj> outputs, String str) {
		PortObj po = ports.get("out_" + anon.getId() + "." + str);
		// System.out.println("Found: " + po.getId());
		for (ConnDef cd : o.getForward().values()) {
			if (cd.getFrom().equals(po.getId())) {
				// System.out.println("Found output conn: " + cd.getId());
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
						// System.out.println("Opposite to: " + inp);
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
						// System.out.println("Opposite to: " + inp);
					}
				}
				outputs.add(inp);
			}
		}
	}

	public static void processInputs(FPGraph2 o, Map<String, PortObj> ports, AnonDefObj anon, List<FunctionPortObj> inputs, String s) {
		// System.out.println("ProcessInputs: " + "inp_" + anon.getId() + "." +
		// s);
		PortObj po = ports.get("inp_" + anon.getId() + "." + s);
		// System.out.println("Found: " + po.getId());
		for (ConnDef cd : o.getForward().values()) {
			if (cd.getTo().equals(po.getId())) {
				// System.out.println("Found input conn: " + cd.getId());
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
						// System.out.println("Opposite From: " + inp);
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
										// System.out.println("Bad mapping mapping from input to output ");
									}
								}
							}
						}

						// System.out.println("Opposite From: " + inp);
					}
				}
				inputs.add(inp);
			}
		}
	}

	public static void processAux(FPGraph2 o, Map<String, PortObj> ports, AnonDefObj anon, List<FunctionPortObj> inputs, String s) {
		// System.out.println("ProcessInputs: " + "inp_" + anon.getId() + "." +
		// s);
		PortObj po = ports.get("aux_" + anon.getId() + "." + s);
		// System.out.println("Found: " + po.getId());
		for (ConnDef cd : o.getForward().values()) {
			if (cd.getTo().equals(po.getId())) {
				// System.out.println("Found input conn: " + cd.getId());
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
						// System.out.println("Opposite From: " + inp);
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
										// System.out.println("Bad mapping mapping from input to output ");
									}
								}
							}
						}

						// System.out.println("Opposite From: " + inp);
					}
				}
				inputs.add(inp);
			}
		}
	}

	public static void updateWaitListWithDone(Map<String, WaitStruct> waits, String done) {
		for (Map.Entry<String, WaitStruct> en : waits.entrySet()) {
			WaitStruct w = en.getValue();
			if (w.isWaitingOn(done)) {
				w.done(done);
			}
		}
	}

	public static Map<String, WaitStruct> getWaitListFromDependencies(com.google.common.collect.Multimap<String, String> deps) {
		Map<String, WaitStruct> waits = new HashMap<String, WaitStruct>();
		for (String k : deps.keySet()) {
			WaitStruct w = waits.get(k);
			if (w == null) {
				w = new WaitStruct();
				waits.put(k, w);
			}
			Collection<String> d = deps.get(k);
			for (String witem : d) {
				w.addWait(witem);
			}
		}
		return waits;
	}

	public static String convertToJavaId(String name) {
		return StringUtils.replaceEach(name, new String[] { "(", ")" }, new String[] { "_", "_" });
	}

	public static byte[] getMurmurHash(byte[] data) {
		HashFunction h = Hashing.murmur3_128();
		HashCode hc = h.newHasher().putBytes(data).hash();
		return hc.asBytes();
	}

	public static org.ptg.util.functions.Expression processOutput(FPGraph2 graph, FunctionPortObj s) {
		org.ptg.util.functions.Expression ret = null;

		ConnDef cd = getConnection(s, graph, false);
		PortObj po = s.getPo();
		PortObj portObj = CommonUtil.getMainPortObject(graph, po);
		if (portObj != null) {
			ret = new org.ptg.util.functions.Expression();
			String self = StringUtils.substringAfter(po.getPortname(), portObj.getPortname());
			String[] comps = StringUtils.split(self, ".");
			String parent = portObj.getPortname();
			StringBuilder retStr = new StringBuilder();
			int count = 0;
			for (String p : comps) {
				count++;
				String fun = null;
				if (count == comps.length) {
					fun = getFuncCode(s, cd);
				} else {
					fun = "get";
				}
				retStr.append("." + fun + StringUtils.capitalize(p) + "(");
				parent = parent + "." + p;
				String pname = (po.getPorttype().equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
				PortObj parentPort = graph.getPorts().get(pname);
				String[] dtype = StringUtils.split(parentPort.getDtype(), "/");
				if (dtype.length == 2) {
					PropCollType dimid = PropCollType.valueOf(dtype[1]);
					if (dimid == null) {
						throw new RuntimeException("Unknown Dimension for " + self);
					}
				}
				if (count == comps.length) {
					retStr.append("( " + "{1}" + " )");
				}
				retStr.append(")");
				ret.setDtype(getDataType(parentPort.getDtype()));
				org.ptg.util.functions.Expression child = new org.ptg.util.functions.Expression();
				child.setDtype(getDataType(parentPort.getDtype()));
				child.setId(getSafeIdentifier(parentPort.getGrp()));
				child.setVal(getSafeIdentifier(parentPort.getGrp()) + retStr);
				child.setExprType(ExpressionType.READ);
				ret.getChild().add(child);

			}
			// solution.append(/*getOutInstVar*/getSafeIdentifier(portObj.getGrp())
			// + retStr);
			ret.setExprType(ExpressionType.WRITE);
			ret.setId(/* getSafeIdentifier */portObj.getGrp());

			ret.setVal(getSafeIdentifier(portObj.getGrp()) + retStr.toString());
			// solution.append(";\n");
			return ret;
		} else {
			return ret;
		}
	}

	public static List<org.ptg.util.functions.Expression> getInputExpressionList(List<FunctionPortObj> inputs, FPGraph2 graph) {
		List<org.ptg.util.functions.Expression> inputExpr = new LinkedList<org.ptg.util.functions.Expression>();
		for (int i = 0; i < inputs.size(); i++) {
			FunctionPortObj s = inputs.get(i);
			org.ptg.util.functions.Expression exp = processInput(graph, s);
			inputExpr.add(exp);
		}
		return inputExpr;
	}

	public static org.ptg.util.functions.Expression processInput(FPGraph2 graph, FunctionPortObj s) {
		org.ptg.util.functions.Expression exp = new org.ptg.util.functions.Expression();
		PortObj po = s.getPo();
		ConnDef cd = getConnection(s, graph, true);
		PortObj portObj = CommonUtil.getMainPortObject(graph, po);
		PortObj parentPort = null;
		if (portObj != null) {
			String self = StringUtils.substringAfter(po.getPortname(), portObj.getPortname());
			String[] comps = StringUtils.split(self, ".");
			String parent = portObj.getPortname();
			StringBuilder retStr = new StringBuilder();
			String pname = (po.getPorttype().equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
			parentPort = graph.getPorts().get(pname);
			for (String p : comps) {

				String fun = getFuncCode(s, cd);
				retStr.append("." + fun + StringUtils.capitalize(p) + "(");
				retStr.append(")");
				parent = parent + "." + p;
				pname = (po.getPorttype().equals("output") ? "out_" : "inp_") + portObj.getGrp() + "." + parent;
				parentPort = graph.getPorts().get(pname);
				String[] dtype = StringUtils.split(parentPort.getDtype(), "/");
				if (dtype.length == 2) {
					PropCollType dimid = PropCollType.valueOf(dtype[1]);
					if (dimid == null) {
						throw new RuntimeException("Unknown Dimension for " + self);
					}
				}
				org.ptg.util.functions.Expression child = new org.ptg.util.functions.Expression();
				child.setDtype(getDataType(parentPort.getDtype()));
				child.setId(getSafeIdentifier(parentPort.getGrp()));
				child.setVal(getSafeIdentifier(parentPort.getGrp()) + retStr);
				child.setExprType(ExpressionType.READ);
				exp.getChild().add(child);
			}
			String cond = cd.getConnCond();
			if (cond != null) {
				retStr.append(cond);
			}
			exp.setDtype(getDataType(parentPort.getDtype()));
			exp.setId(getSafeIdentifier(s.getMyPort().getGrp(), s.getMyPort().getId()));
			exp.setVal(getSafeIdentifier(portObj.getGrp()) + retStr);
			exp.setExprType(ExpressionType.READ);
			return exp;
			// solution.append(getDataType(parentPort.getDtype())) + " " +
			// getSafeIdentifier(s.getMyPort().getId()) + " = " +
			// /*getInInstVar*/getSafeIdentifier(portObj.getGrp() )+ retStr);
		}
		return null;
	}

	public static String getFuncCode(FunctionPortObj s, ConnDef cd) {
		return cd.getDirection(s.getMyPort().getId()).equals("in") ? "get" : "set";
	}

	public static ConnDef getConnection(FunctionPortObj s, FPGraph2 graph, Boolean inport) {
		ConnDef cdret = null;
		if (inport) {
			for (ConnDef cd : graph.getForward().values()) {
				if (cd.getFrom().equals(s.getPo().getId()) && cd.getTo().equals(s.getMyPort().getId())) {
					cdret = cd;
					break;
				}
			}
		} else {
			for (ConnDef cd : graph.getForward().values()) {
				if (cd.getTo().equals(s.getPo().getId()) && cd.getFrom().equals(s.getMyPort().getId())) {
					cdret = cd;
					break;
				}
			}
		}
		return cdret;
	}

	public static String getDataType(String dataType) {
		String ret = "";
		String[] dtype = StringUtils.split(dataType, "/");
		if (dtype.length == 2) {
			PropCollType dimid = PropCollType.valueOf(dtype[1]);
			if (dimid == null) {
				throw new RuntimeException("Unknown Dimension for " + dtype[1]);
			}
			ret += dimid.getDef().replace("$name", dtype[0]);
		} else {
			ret += dataType + " ";
		}
		return ret;
	}

	public static String getSafeIdentifier(String ident) {
		return StringUtils.replaceChars(ident, "().", "_");
	}

	public static String getSafeIdentifier(String parent, String ident) {
		ident = parent + ident;
		return StringUtils.replaceChars(ident, "().", "_");
	}

	/*
	 * my port id
	 */
	public static org.ptg.util.functions.Expression getMyInExpression(FPGraph2 o, AnonDefObj anon, String str, String exprType) {
		Map<String, PortObj> ports = o.getPorts();
		List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
		processAux(o, ports, anon, inputs, str);
		if (inputs.size() > 0 && inputs.get(0).getPo() != null) {
			IInputExprProcessor proc = inputExprProcessor.get(exprType);
			org.ptg.util.functions.Expression exp = proc.process(o, inputs.get(0));
			return exp;
		} else {
			return null;
		}
	}

	/*
	 * my port id
	 */
	public static org.ptg.util.functions.Expression getMyOutExpression(FPGraph2 o, AnonDefObj anon, String str, String exprType) {
		Map<String, PortObj> ports = o.getPorts();
		List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
		processOutputs(o, ports, anon, inputs, str);
		org.ptg.util.functions.Expression exp = null;
		if (inputs.size() > 0) {
			for (FunctionPortObj obj : inputs) {
				if (inputs.get(0).getPo() != null) {
					IOutputExprProcessor proc = outputExprProcessor.get(exprType);
					exp = proc.process(o, obj);
				}
			}
			return exp;
		} else {
			return null;
		}
	}

	public static List<org.ptg.util.functions.Expression> getMyOutExpressions(FPGraph2 o, AnonDefObj anon, String str, String exprType) {
		List<org.ptg.util.functions.Expression> ret = new LinkedList<org.ptg.util.functions.Expression>();
		Map<String, PortObj> ports = o.getPorts();
		List<FunctionPortObj> inputs = new ArrayList<FunctionPortObj>();
		processOutputs(o, ports, anon, inputs, str);
		if (inputs.size() > 0) {
			for (FunctionPortObj obj : inputs) {
				org.ptg.util.functions.Expression exp = null;
				if (inputs.get(0).getPo() != null) {
					IOutputExprProcessor proc = outputExprProcessor.get(exprType);
					exp = proc.process(o, obj);
					ret.add(exp);
				}
			}
			return ret;
		} else {
			return null;
		}

	}

	public static int getVarInt(String name, int def) {
		Object v = getVar(name);
		if (v == null) {
			return def;
		}
		if (v instanceof Integer) {
			return (Integer) v;
		} else {
			return def;
		}
	}

	public static double getVarDouble(String name, double def) {
		Object v = getVar(name);
		if (v == null) {
			return def;
		}
		if (v instanceof Double) {
			return (Double) v;
		} else {
			return def;
		}
	}

	public static float getVarFloat(String name, float def) {
		Object v = getVar(name);
		if (v == null) {
			return def;
		}
		if (v instanceof Float) {
			return (Float) v;
		} else {
			return def;
		}
	}

	public static String getVarString(String name, String def) {
		Object v = getVar(name);
		if (v == null) {
			return def;
		}
		if (v instanceof String) {
			return (String) v;
		} else {
			return def;
		}
	}

	public static long getVarLong(String name, long def) {
		Object v = getVar(name);
		if (v == null) {
			return def;
		}
		if (v instanceof Long) {
			return (Long) v;
		} else {
			return def;
		}
	}

	public static boolean getVarBoolean(String name, boolean def) {
		Object v = getVar(name);
		if (v == null) {
			return def;
		}
		if (v instanceof Boolean) {
			return (Boolean) v;
		} else {
			return def;
		}
	}

	public static Object getVarBoolean(String name, Object def) {
		Object v = getVar(name);
		if (v == null) {
			return def;
		} else {
			return v;
		}

	}

	public static String getDataTypePrefix(String varType) {
		if (isDouble(varType)) {
			return "Double";
		} else if (isFloat(varType)) {
			return "Float";
		} else if (isLong(varType)) {
			return "Long";
		} else if (isBoolean(varType)) {
			return "Boolean";
		} else if (isString(varType)) {
			return "String";
		} else if (isInt(varType)) {
			return "Int";
		} else {
			return "Object";
		}

	}

	public static void captureScreen(String fileName) throws Exception {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		String format = StringUtils.substringAfterLast(fileName, ".");
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		ImageIO.write(image, format, new File(fileName));

	}

	public static String transformXml(String xmlData, String tranfData) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xmlStream = new StreamSource(xmlData);
		StreamSource xslt = new StreamSource(tranfData);
		javax.xml.transform.Transformer transformer = factory.newTransformer(xslt);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamResult s = new StreamResult(bos);
		transformer.transform(xmlStream, s);
		return new String(bos.toByteArray());
	}

	public static void createMapReduceJar(byte[] mapperClassCode, byte[] reducerClassCode, String mapperClassName, String reducerClassName, String jarName) {

		// /CompressorOutputStream gzippedOut = new
		// CompressorStreamFactory().createCompressorOutputStream("jar",
	}

	public static HttpHost extractHost(URI uri) {
		if (uri == null) {
			return null;
		}
		HttpHost target = null;
		if (uri.isAbsolute()) {
			int port = uri.getPort();
			String host = uri.getHost();
			if (host == null) {
				host = uri.getAuthority();
				if (host != null) {
					int at = host.indexOf('@');
					if (at >= 0) {
						if (host.length() > at + 1) {
							host = host.substring(at + 1);
						} else {
							host = null;
						}
					}

					if (host != null) {
						int colon = host.indexOf(':');
						if (colon >= 0) {
							int pos = colon + 1;
							int len = 0;
							for (int i = pos; i < host.length() && Character.isDigit(host.charAt(i)); i++) {
								len++;
							}

							if (len > 0) {
								try {
									port = Integer.parseInt(host.substring(pos, pos + len));
								} catch (NumberFormatException ex) {
								}
							}
							host = host.substring(0, colon);
						}
					}
				}
			}
			String scheme = uri.getScheme();
			if (host != null) {
				target = new HttpHost(host, port, Protocol.getProtocol(scheme));

			}
		}
		return target;
	}

	public static IPluginManager getPluginsManager() {
		IPluginManager pluginsManager = (IPluginManager) SpringHelper.get("pluginsManager");
		return pluginsManager;
	}

	public static void saveStaticComponent(String ip, String name, String codeToSave, String doc) {
		if (doc == null) {
			doc = "";
		}
		codeToSave = StringEscapeUtils.escapeJavaScript(codeToSave);
		String backsql = "insert into deletedstaticcomponent (select * from staticcomponent where name='" + name + "')";
		String delsql = "delete from staticcomponent where name='" + name + "'";
		String inssql = "insert into staticcomponent(name,txt,userid,userip,doc) values ('" + name + "','" + codeToSave + "','" + ip + "','" + ip + "','" + doc + "')";

		DBHelper.getInstance().executeUpdate(backsql);
		DBHelper.getInstance().executeUpdate(delsql);
		DBHelper.getInstance().executeUpdate(inssql);
	}

	public static void removeStaticComponent(String ip, String name) {
		String backsql = "insert into deletedstaticcomponent (select * from staticcomponent where name='" + name + "')";
		String delsql = "delete from staticcomponent where name='" + name + "'";
		DBHelper.getInstance().executeUpdate(backsql);
		DBHelper.getInstance().executeUpdate(delsql);
	}

	public static String getStaticComponent(String graphid) {
		return DBHelper.getInstance().getString("select txt from staticcomponent where name='" + graphid + "'");
	}

	public static void saveDesign(String ip, String name, String toSave, String configsave) {
		if (configsave == null || configsave.length() <= 0) {
			configsave = "[]";
		}

		if (DBHelper.getInstance().exists("select name from pageconfig where name='" + name + "'")) {
			String backsql = "insert into deletedpageconfig (select * from pageconfig where name='" + name + "')";
			String inssql = "update pageconfig set graph='" + toSave + "',config='" + configsave + "' ,userid=0,userip='" + ip + "' where name='" + name + "'";
			DBHelper.getInstance().executeUpdate(backsql);
			DBHelper.getInstance().execute(inssql);
		} else {
			String backsql = "insert into deletedpageconfig (select * from pageconfig where name='" + name + "')";
			String inssql = "insert into pageconfig(name,graph,config,userid,userip) values ('" + name + "','" + toSave + "','" + configsave + "'," + 0 + ",'" + ip + "')";
			DBHelper.getInstance().executeUpdate(backsql);
			DBHelper.getInstance().execute(inssql);

		}
	}

	public static void ping(String ipAddress) throws UnknownHostException, IOException {
		InetAddress inet = InetAddress.getByName(ipAddress);

		System.out.println("Sending Ping Request to " + ipAddress);
		System.out.println(inet.isReachable(5000) ? "Host is reachable" : "Host is NOT reachable");

	}
	public static List<BBox> getGraphToAnnotations(String name){
		FPGraph2 fp2 = CommonUtil.buildDesignMappingGraph2(name);
		Map<String, Shape> shapes = fp2.getShapes();
			List<BBox> rectangles = new ArrayList<>();
			for(Shape sh : shapes.values()) {
				BBox r = new BBox();
				r.x = sh.getX();
				r.y = sh.getY();
				r.r= sh.getR();
				r.b = sh.getB();
				r.id  = sh.getId();
				r.setTag(sh.getData().get("tag"));
				rectangles.add(r);
				}
			return rectangles;
		}
}