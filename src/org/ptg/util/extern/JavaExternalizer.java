/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.extern;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ptg.util.DynaObjectHelper;

public class JavaExternalizer {
	private   Map<String, Externalizer> helpers = new HashMap<String,Externalizer>();

	public String burnWriteObject(Set<Map.Entry<String, String>> val) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("super.writeExternal($1);");
		for (Map.Entry<String, String> v : val) {
			String temp = outStmt(v.getKey(), v.getValue());
			if(temp!=null)
			sb.append("\n" + temp);
		}
		sb.append("\n}");
		return sb.toString();
	}

	public String burnReadObject(Set<Map.Entry<String, String>> val) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("super.readExternal($1);");
		for (Map.Entry<String, String> v : val) {
			sb.append("\n" + inStmt(v.getKey(), v.getValue()));
		}
		sb.append("\n}");
		return sb.toString();
	}

	public String outStmt(String var, String type) {
				Externalizer ext = helpers.get(type);
				if(ext !=null){
				return ext.outStmt(var, type);
				}else{
					System.out.println("Cannot find externalizer for " +var +" of type "+type);
					return null;
				}
			}

	public String inStmt(String var, String type) {
		Externalizer ext = helpers.get(type);
		if(ext !=null){
		return ext.inStmt(var, type);
		}else{
			System.out.println("Cannot find externalizer for " +var +" of type "+type);
			return null;
		}

	}
	public void buildSimpleExternalizer() {
		Externalizer simple = new SimpleExternalizer(helpers);
		helpers.put("double", simple);
		helpers.put("java.lang.String", simple);
		helpers.put("java.util.Date", simple);
		helpers.put("java.lang.Integer", simple);
		helpers.put("java.lang.Double", simple);
		helpers.put("java.lang.Float", simple);
		helpers.put("java.lang.Boolean", simple);
		helpers.put("boolean", simple);
		helpers.put("int", simple);
		helpers.put("long", simple);
		helpers.put("float", simple);
	}
	public void buildComplexExternalizer() {
		Externalizer simple = new SimpleExternalizer(helpers);
		helpers.put("double", simple);
		helpers.put("java.lang.String", simple);
		helpers.put("java.util.Date", simple);
		helpers.put("java.lang.Integer", simple);
		helpers.put("java.lang.Double", simple);
		helpers.put("java.lang.Float", simple);
		helpers.put("java.lang.Boolean", simple);
		helpers.put("boolean", simple);
		helpers.put("int", simple);
		helpers.put("long", simple);
		helpers.put("float", simple);
		helpers.put("java.util.Collection", new CollectionExternalizer(helpers));
		helpers.put("java.util.Map", new MapExternalizer(helpers));
		helpers.put("array", new ArrayExternalizer(helpers));
		helpers.put("Event", new BruteForceExternalizer(helpers));
		helpers.put("org.ptg.events.Event", new BruteForceExternalizer(helpers));
		
	}

public static void main(String[] args) {
	JavaExternalizer ext = new JavaExternalizer();
	ext.buildComplexExternalizer();
	Map <String,String>mp = DynaObjectHelper.externClass("cluster.common.CacheEvent",true);
	Set<Map.Entry<String, String>> val = mp.entrySet();
	for(Map.Entry<String, String> en :val){
	System.out.println(en.getKey());
	System.out.println(en.getValue());
	}
	System.out.println("\npublic void 	writeExternal(java.io.ObjectOutput $1) throws java.io.IOException");
	System.out.println(ext.burnWriteObject(val));
	System.out.println("\npublic void 	readExternal(java.io.ObjectInput $1)  throws java.io.IOException,java.lang.ClassNotFoundException");
	System.out.println(ext.burnReadObject(val));
	
}
}
