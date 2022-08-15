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

public class MapExternalizer extends Externalizer {
	private  Map<String, Externalizer> helpers = new HashMap<String,Externalizer>();
	public MapExternalizer(Map<String, Externalizer> helpers ){
		if(helpers!=null)
     		this.helpers = helpers;
	}
	//does not handles map == null;
	public  String outStmt(String var, String type) {
		String s= "if("+var +"!=null){\n"+
			"out.writeInt("+var+".size());\n"+
			"out.writeUTF(Externalizable);\n"+
			"out.writeUTF(Externalizable);\n"+
			"for(Map.Entry<Externalizable, Externalizable>en: "+var+".entrySet()){\n"+
				"en.getKey().writeExternal(out);\n"+
				"en.getValue().writeExternal(out);\n}\n";
		return s;
	}

	public  String inStmt(String var, String type) {
		String str = "int "+var+"Length = in.readInt();\n"+
		"String className = in.readUTF();\n"+
		"String valueClassName = in.readUTF();\n"+
		"for(int i = 0 ; i< "+var+"Length; i++){\n"+
			"Externalizable ex = (Externalizable) ReflectionUtils.createInstance(className);\n"+
			"ex.readExternal(in);\n"+
			"Externalizable ex2 = (Externalizable) ReflectionUtils.createInstance(valueClassName);\n"+
			"ex2.readExternal(in);\n"+
			var+".put(ex, ex2);\n"+
			"}\n";
		return str;	
	}
}
