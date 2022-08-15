package org.ptg.proptest;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.ptg.util.CollTypeAnot;
import org.ptg.util.CommonUtil;
import org.ptg.util.IHierStore;
import org.ptg.util.PropCollType;
import org.ptg.util.PropInfo;
import org.ptg.util.PropTypeEnum;



public class PropGraph {
	static Map<String, String> baseClasses = new HashMap<String, String>();
	static Map<String, String> pc = new HashMap<String, String>();
	static {
		baseClasses.put("java.lang.String", "java.lang.String");
		baseClasses.put("java.lang.Class", "java.lang.Class");
		baseClasses.put("java.lang.annotation", "java.lang.annotation");
		baseClasses.put("int", "int");
		baseClasses.put("short", "short");
		baseClasses.put("double", "double");
		baseClasses.put("float", "float");
		baseClasses.put("boolean", "boolean");
		baseClasses.put("long", "long");
		baseClasses.put("char", "char");
		baseClasses.put("byte", "byte");
		pc.put("int", "int");
		pc.put("short", "short");
		pc.put("double", "double");
		pc.put("float", "float");
		pc.put("boolean", "boolean");
		pc.put("long", "long");
		pc.put("char", "char");
		pc.put("byte", "byte");

	}

	public static void main(String[] args) {
		String name = "org.ptg.util.awt.Point";
		PropGraph pg = new PropGraph();
		Map<String,String> stack = new LinkedHashMap<String,String>(); 
		IHierStore<PropInfo> p = pg.analyze(name, null,stack,0);
		System.out.println(CommonUtil.toJson(p));
		
	}

	public PropInfo<PropInfo> analyze(String name, PropInfo<PropInfo> parent,Map<String,String> stack,int depth) {
		if (parent == null) {
			parent = new PropInfo<PropInfo>();
			parent.setName(name);
			
		}
		try {
			Class cc = Class.forName(name);
			parent.setPropClass(cc.getName());
			analyze(cc, parent,stack,depth);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return parent;
	}

	public  IHierStore<PropInfo> analyze(Class cc, PropInfo<PropInfo> parent,Map<String,String> stack,int depth) {
		if (parent == null) {
			parent = new PropInfo<PropInfo>();
			parent.setName(cc.getName());
			parent.setPropClass(cc.getName());
		}
 
		try {
			if(stack.containsKey(cc.getName()))
			return parent;
			stack.put(cc.getName(),cc.getName());
			PropertyUtils u = new PropertyUtils();
			PropertyDescriptor[] desc = u.getPropertyDescriptors(cc);
			for (PropertyDescriptor pd : desc) {
				String pstr = "";
				Method w = null;
				Method r = null;
				r = pd.getReadMethod();
				Class p = null;
				w = pd.getWriteMethod();
				if (w != null) {
					p = w.getReturnType();
				}
				if (r != null) {
					p = r.getReturnType();
				} 
				if (p != null) {
					PropInfo<PropInfo> child = new PropInfo();
					String name = p.getName();
					if(name.startsWith("[")){
						if(name.startsWith("[[[[[")){
						child.setCollType(PropCollType.Array5D);
						}else if(name.startsWith("[[[[")){
						child.setCollType(PropCollType.Array4D);
						}else if(name.startsWith("[[[")){
						child.setCollType(PropCollType.Array3D);
						}else if(name.startsWith("[[")){
						child.setCollType(PropCollType.Array2D);
						}else if(name.startsWith("[")){
						child.setCollType(PropCollType.Array);
						} 
						Class mdArray = p.getComponentType();
						name = (mdArray.getName());
						while(name.contains("[")){
							mdArray = mdArray.getComponentType();
							name = (mdArray.getName());
						}
					}
					child.setPropClass(name);
					setSignatures(w, r, child);
					child.setName(pd.getName());
					child.setParent(parent);
					Annotation[] anon = r.getAnnotations();
					for(Annotation a: anon){
						if(a.annotationType().equals(CollTypeAnot.class)){
							child.setCollType(PropCollType.valueOf(r.getAnnotation(CollTypeAnot.class).collType()));
							child.setPropClass(r.getAnnotation(CollTypeAnot.class).collValType());
							break;
						}
					}
					if (!baseClasses.containsKey(name)) {
						analyze(Class.forName(name), child,stack,depth+1);
					}
					PropInfo<PropInfo> tempChild = child;
					PropInfo<PropInfo> tempParent = tempChild;
					String pathExpr=null;
					while(tempParent!=null){
						if(pathExpr==null)
							pathExpr = tempParent.getName();
						else
							pathExpr = tempParent.getName()+"/"+pathExpr;
						tempParent = tempParent.getParent();
					}
					System.out.println(pathExpr);
					child.setPathExpr(pathExpr);
					if(!(child.getReadSig().equals("getClass")&&(child.getWriteSig()==null))){
						parent.getChilds().add(child);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parent;
	}

	public  void setSignatures(Method w, Method r, PropInfo child) {
		if(r!=null){
			child.setReadSig(r.getName());
			child.setType(PropTypeEnum.READ);
		}
		if(w!=null){
			child.setWriteSig(w.getName());
			if(r!=null){
				child.setType(PropTypeEnum.READWRITE);
			}else{
				child.setType(PropTypeEnum.WRITE);
			}
		}
	}
}
