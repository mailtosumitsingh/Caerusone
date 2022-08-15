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

public class CollectionExternalizer extends Externalizer {
	private  Map<String, Externalizer> helpers = new HashMap<String,Externalizer>();
	public CollectionExternalizer(Map<String, Externalizer> helpers ){
		if(helpers!=null)
     		this.helpers = helpers;
	}
	public  String outStmt(String var, String type) {
		String s= "if("+var +"!=null){\n"+
			"out.writeInt("+var+".size());\n"+
			"for(int i=0;i<"+var+".size();i++){\n"+
			"Externalizable ex = "+var+".get(i);\n"+
			"out.writeUTF(ex.getClass().getName());\n"+
			"ex.writeExternal(out);\n}\n}else {\n"+
			"out.writeInt(0);\n";
		return s;
	}

	public  String inStmt(String var, String type) {
		String str = "int "+var+"Length = in.readInt();\n"+
		"for(int i=0;i<"+var+"Length;i++){\n"+
			"String className = in.readUTF();\n"+
			"Externalizable ex = (Externalizable) ReflectionUtils.createInstance(className);\n"+
            "ex.readExternal(in);\n"+
            var+".add(ex);\n}\n";
			return str;	
	}
}
