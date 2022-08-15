package org.ptg.imgs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.ClassReader;
import org.ptg.analyzer.asm.ConstExtractor;
import org.ptg.analyzer.model.Ctor;
import org.ptg.analyzer.model.Setter;
import org.ptg.analyzer.model.SimpleMethod;

public class FilterGenerator {
	public static void main(String[] args) throws Exception {
		
		java.util.jar.JarFile jar = new java.util.jar.JarFile("C:/projects/CaerusonePlugins/ImageFilters/lib/ImageFiltersPlugin.jar");
		java.util.Enumeration enumE = jar.entries();
		Map<String,List<Setter>> creach  = new  LinkedHashMap<String,List<Setter>> (); 
		while (enumE.hasMoreElements()) {
			java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumE.nextElement();
			String className = file.getName();
			if (className.startsWith("com/jhlabs/image") && className.endsWith("Filter.class")) {
				className = StringUtils.substringBefore(className, ".class");
				className = className.replace('/', '.');
				String origClassName = className;
				String filterName = StringUtils.substringAfter(className, "com.jhlabs.image.");
				System.out.println("Got a filter: " + filterName);
				//visitReflectionCode(className);
				List<Setter> reaches = visitClassCode(jar,file);
				if(reaches!=null && reaches.size()>0)
				creach.put(origClassName,reaches);
			}
		}
		System.out.println("Done reach analysis");
		Map<String,List<Setter>> creachFiltered  = new  LinkedHashMap<String,List<Setter>> ();
		for(Map.Entry<String, List<Setter>>en : creach.entrySet()){
			for(Setter s: en.getValue()){
				if(!( s instanceof SimpleMethod )){
					creachFiltered.put(en.getKey(),en.getValue());
				}else{
					System.out.println("Method: "+en.getKey()+">"+s);
				}
			}
		}
		generateCode(creachFiltered);
	}

	private static void generateCode(Map<String, List<Setter>> creach) {
		System.out.println("Starting generating" );
		for(Map.Entry<String, List<Setter>>en : creach.entrySet()){
			StringBuilder code = new StringBuilder();
			//System.out.println("generating: " +en.getKey());
			boolean hasNZeroParamCtor = false;
			Ctor nzCtor = null;
			int setters = 0;
			for(Setter s: en.getValue()){
				if(s instanceof Ctor){
					Ctor c = (Ctor) s;
				  if(c.getParams().size()>0){
					hasNZeroParamCtor= true;
					nzCtor = c;
				  }
				  }else {
					  setters++;
				  }
			}
			if(hasNZeroParamCtor){
				StringBuilder bodyCode = new StringBuilder();
				code.append("public static BufferedImage apply"+StringUtils.substringAfterLast(en.getKey(),".")+"(");
				code.append("BufferedImage in");
				bodyCode.append(en.getKey() +" " +"f = new "+en.getKey()+"(");
				int size = nzCtor.getParams().size();
				int i = 0;
				for(Entry<String, String> entry: nzCtor.getParams().entrySet()){
					code.append(",");
					code.append(entry.getValue()+" " +entry.getKey());
					bodyCode.append(entry.getKey());
					if(i<size-1){
						code.append(",");
						bodyCode.append(",");
					}
					
					i++;
					
				}
				bodyCode.append(");" );
				code.append("){\n");
				bodyCode.append("\n	\tBufferedImage o2 = cloneImage(in);");
				bodyCode.append("\n \tf.filter(in,o2);");
				bodyCode.append("\n \treturn o2;");
				bodyCode.append("\n}\n");
				code.append("\n"+bodyCode);
			}else{
				StringBuilder bodyCode = new StringBuilder();
				code.append("public static BufferedImage apply"+StringUtils.substringAfterLast(en.getKey(),".")+"(");
				code.append("BufferedImage in");
				if(setters>0)code.append(",");
				bodyCode.append(en.getKey() +" " +"f = new "+en.getKey()+"();");
				int scount = 0;
				for(Setter s: en.getValue()){
					if(! (s instanceof Ctor)){
						
						int size = s.getParams().size();
						int i = 0;
						bodyCode.append("\n f."+s.getName()+"(");
						for(Entry<String, String> entry: s.getParams().entrySet()){
							code.append(entry.getValue()+" " +entry.getKey());
							bodyCode.append(entry.getKey());
							if(i<size-1){
								code.append(",");
								bodyCode.append(",");
							}
							
							i++;
						}
						bodyCode.append(");\n");
						if(scount<setters-1){
							code.append(",");
						}
						scount++;
					}
					
				}
				code.append("){\n");
				bodyCode.append("\n	\tBufferedImage o2 = cloneImage(in);");
				bodyCode.append("\n \tf.filter(in,o2);");
				bodyCode.append("\n \treturn o2;");
				code.append(bodyCode.toString());
				code.append("\n}\n");
			}
			System.out.println(code);
		}
		
	}

	public static void visitReflectionCode(String className) throws ClassNotFoundException {
		Class cc = Class.forName(className);
		if (cc.getConstructors() != null && cc.getConstructors().length > 0) {
			for(Constructor c : cc.getConstructors()){
			if (c.getParameterTypes() != null && c.getParameterTypes().length > 0)
				for (Class ptype : c.getParameterTypes()) {
					System.out.println("\t" + ptype.getName());
				}
			System.out.println("\t-----------------");
			}
			
		}
	}

	public static List<Setter> visitClassCode(java.util.jar.JarFile jar ,java.util.jar.JarEntry file ) throws Exception{
		java.io.InputStream is = jar.getInputStream(file); 
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		while (is.available() > 0) {  
			fos.write(is.read());
		}
		fos.flush();
		fos.close();
		byte[]data = fos.toByteArray();
		List<Setter> v = analyzeBytecode(data);
		System.out.println(v);
		return v;
	}

	private static List<Setter> analyzeBytecode(byte[] data) throws IOException {
		ClassReader  cr = new ClassReader(new ByteArrayInputStream(data));
		ConstExtractor v = new ConstExtractor(262144);
		cr.accept(v,0);
		return v.getReach();
	}
}
