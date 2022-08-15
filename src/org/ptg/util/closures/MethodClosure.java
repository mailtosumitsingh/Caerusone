/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.closures;

import java.lang.reflect.Method;

import org.apache.commons.collections15.Closure;

public class MethodClosure<DataType> implements Closure<DataType>{
    private Method mtd;
	private Object obj;
	public MethodClosure(Object onObject, Method mtd) {
		obj = onObject;
		this.mtd = mtd;
	}

	public void execute(DataType arg0) {
		 try {
			mtd.invoke(obj,new Object[]{ arg0});
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
