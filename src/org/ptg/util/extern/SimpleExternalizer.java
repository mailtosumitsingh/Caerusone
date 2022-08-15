/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.extern;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleExternalizer extends Externalizer {
	private Map<String, String> inmap = new LinkedHashMap<String, String>();
	private Map<String, String> outmap = new LinkedHashMap<String, String>();
	private Map<String, String> postoutmap = new LinkedHashMap<String, String>();
	private  Map<String, Externalizer> helpers = new HashMap<String,Externalizer>();
	public SimpleExternalizer(Map<String, Externalizer> helpers ){
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
		if(helpers!=null)
		this.helpers = helpers;
	}
	public  String outStmt(String var, String type) {
		if (type.equals("java.lang.String"))
			return outmap.get(type) + postoutmap.get(type) + "org.ptg.util.CommonUtil.getNullString(" + var + "));";
		else{
			String temp = outmap.get(type) ;
			if(temp!=null){
			return outmap.get(type) + "this." + var + postoutmap.get(type) + ");";
			}else{
				Externalizer ext = helpers.get(type);
				if(ext !=null){
				return ext.outStmt(var, type);
				}else{
					System.out.println("Cannot find externalizer for " +var +" of type "+type);
					return null;
				}
			}
		}

	}

	public  String inStmt(String var, String type) {
		String temp = "this." + var + " = " + inmap.get(type) + "(" + "));";
		if(temp!=null){
		return temp;
		}else{
			Externalizer ext = helpers.get(type);
			if(ext !=null){
			return ext.inStmt(var, type);
			}else{
				System.out.println("Cannot find externalizer for " +var +" of type "+type);
				return null;
			}
		}
	}


}
