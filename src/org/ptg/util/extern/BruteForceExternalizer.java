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

public class BruteForceExternalizer extends Externalizer {
	private Map<String, Externalizer> helpers = new HashMap<String, Externalizer>();

	public BruteForceExternalizer(Map<String, Externalizer> helpers) {
		if (helpers != null)
			this.helpers = helpers;
	}

	public String outStmt(String var, String type) {
		return "this." + var + ".writeExternal($1)";
	}

	public String inStmt(String var, String type) {
		return "this." + var + ".readExternal($1)";
	}

}
