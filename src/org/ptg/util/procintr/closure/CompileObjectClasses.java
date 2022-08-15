/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.procintr.closure;

import org.ptg.util.CommonUtil;
import org.ptg.util.DynaObjectHelper;
import org.ptg.util.PropInfo;

public class CompileObjectClasses {

	public void realGenerateClass(PropInfo<PropInfo> p) {
		System.out.println("RealgenerateClass:"+p.getName());
		if (!CommonUtil.isPrimitive(p.getPropClass())) {
			Class c = null;
			try {
				c = Class.forName(p.getPropClass());
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found "+p.getPropClass()+" going to create a  new.");
			}
			if (c == null) {
				try {
					Class cc = DynaObjectHelper.generateClassFromPropInfo(p);
					DynaObjectHelper.testGeneratedPropInfoClass(cc, p);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("done");
			}
		} else {
			System.out.println(p.getPropClass()+" is primitive not generating class.");
		}
	}
	
}