/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.titan;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.titan.handlers.im;
import org.ptg.util.titan.handlers.loop;
import org.ptg.util.titan.handlers.oloop;
import org.ptg.util.titan.handlers.sloop;

public class TitanCompiler {
	static TitanCompiler comp = new TitanCompiler();
	private static Map<String, String> expensions = new HashMap<String, String>();
	private static Map<String, IMacroHandler> operators = new HashMap<String, IMacroHandler>();
	static {
		operators.put("im", new im());
		operators.put("loop", new loop());
		operators.put("oloop", new oloop());
		operators.put("sloop", new sloop());
		expensions.put("[ITaskFunction]", " implements org.ptg.util.ITaskFunction ");
		expensions.put("[AbstractICompileFunction]", " extends org.ptg.util.taskfunctions.AbstractICompileFunction ");

	}

	public static String compile(String all) {
		for (Map.Entry<String, String> exp : expensions.entrySet()) {
			all = StringUtils.replace(all, exp.getKey(), exp.getValue());
		}
		return comp.compileintern(all);
	}

	public String compileintern(String all) {
		boolean found = true;
		StringBuilder sbinner = new StringBuilder();
		while (found == true) {
			found = false;
			sbinner = new StringBuilder();
			String[] s = StringUtils.split(all, "\n");
			String mode = "Start";
			String para = "";
			String currMacro = null;
			String currLine = null;
			int nestcount = 0;
			int depth = 0;
			for (String line : s) {
				String line2 = StringUtils.trim(line);
				if (!mode.equals("in") && line.startsWith("//#")) {
					found = true;
					String macro = StringUtils.substringAfter(line, "//#");
					macro = StringUtils.trim(macro);

					String[] temp = macro.split(" ");
					if (temp != null && temp.length != 0) {
						macro = temp[0];
					}
					line = expandMacro(macro, para, line2, all, depth, comp);
				}
				if (line.startsWith("/*{")) {
					found = true;
					depth++;
					if ("in".equals(mode)) {
						nestcount++;
					} else {
						mode = "in";
						currMacro = StringUtils.substringAfter(line, "/*{");
						currMacro = StringUtils.trim(currMacro);

						String[] temp = currMacro.split(" ");
						if (temp != null && temp.length != 0) {
							currMacro = temp[0];
						}
						currLine = line;
					}
				}
				if (line.startsWith("}*/")) {
					found = true;
					if (nestcount == 0) {
						para += (line + "\n");
						mode = "Start";
						line = expandMacro(currMacro, para, currLine, all, depth, this);
						depth--;
						currMacro = null;
						currLine = null;
					} else {
						nestcount--;
					}
				}
				if (mode.equals("in")) {
					para += (line + "\n");
				} else {
					sbinner.append(line + "\n");
				}
			}
			all = sbinner.toString();
		}
		return sbinner.toString();
	}

	public static String expandMacro(String macro, String para, String line, String all, int nestcount, TitanCompiler comp) {
		String m = macro.split(" ")[0];
		if (m != null) {
			IMacroHandler h = operators.get(m);
			if (h != null) {
				return h.handle(macro, para, line, all, nestcount, comp);
			}
		}
		return ""/* eat it */;
	}

	public static void addMacro(String name, IMacroHandler exp) {
		operators.put(name, exp);
	}
}
