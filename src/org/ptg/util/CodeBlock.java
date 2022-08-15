package org.ptg.util;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

public class CodeBlock {
	String name;
	StringBuilder code;
	long lineNo;
	Map<Long, String> lineToComp;
	Map<Long, CodeBlock > nested;
	public void addCodeLineSameLine(String ln, String comp) {
		if (ln != null) {
			code.append(ln);
		}
	}

	public void append(String ln, String comp) {
		addCodeLine(ln, comp);
	}

	public void addCodeLine(String ln, String comp) {
		if (ln != null) {
			ln = ("\n/*" + comp + "*/\n")+ln;
			for(String lninter : StringUtils.split(ln,"\n")){
			code.append(lninter);
			code.append("\n");
			lineToComp.put(lineNo, comp);
			lineNo++;
			}
		}
	}

	public void init(String name) {
		this.name = name;
		code = new StringBuilder();
		lineNo = 1;
		lineToComp = new TreeMap<Long, String>();
		nested = new TreeMap<Long, CodeBlock>();
	}

	public String getName() {
		return name;
	}

	public String build() {
		return code.toString();
	}

	public long getLineNo() {
		return lineNo;
	}

	public Map<Long, String> getLineToComp() {
		return lineToComp;
	}
  public void addNested(CodeBlock n,String name){
	  nested.put(lineNo, n);
	  String s = n.build();
	  if(s!=null && s.length()>0){
		  String[] t = StringUtils.split(s,"\n");
		  for(String a:t){
			  addCodeLine("\t\t"+a, name);
		  }
	  }
  }
}
