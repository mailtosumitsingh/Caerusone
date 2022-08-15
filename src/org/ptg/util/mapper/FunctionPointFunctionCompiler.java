/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.mapper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.processors.ConnDef;

public class FunctionPointFunctionCompiler {
	public static String[] compileFunctionPointFunction(Map<String, List<FunctionPoint>> grps, CompilePath cp, FunctionPoint fp,ConnDef currConn) {
		String[] lhsrhs = new String[2];
		{
			List<FunctionPoint> grp = grps.get(fp.getName());
			String pstr = "";
			if (grp != null) {
				cp.setDeps(grp);
				for (int k = 0; k < grp.size(); k++) {
					FunctionPoint gp = grp.get(k);
					if (gp != null) {
						if (gp.getVal() == null || gp.getVal().length() < 1) {
							pstr += gp.getName();
						} else {
							pstr += gp.getVal();
						}
					}
					if (k < grp.size() - 1) {
						pstr += ",";
					}

				}
			}
			String fname = fp.getFn();
			String cname = null;
			if (fp.getDataType() != null)
				cname = StringUtils.substringAfterLast(fp.getDataType(), ".");
			if (fname.equals(cname)) {// tada construtor i get you.
				fname = fp.getDataType();
				lhsrhs[0] =  (fp.getDataType() + " " + fp.getName() );
				lhsrhs[1] = ( " new " + fname + "(" + pstr + ");\n");
			} else {
				fname = fp.getFn();
				lhsrhs[0] = (fp.getDataType() + " " + fp.getName() );
				lhsrhs[1] = (  fp.getCn() + "." + fname + "(" + pstr + ");\n");
			}
		}
		return lhsrhs;
	}
}