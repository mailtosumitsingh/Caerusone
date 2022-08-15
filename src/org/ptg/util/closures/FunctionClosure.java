/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.closures;

import org.apache.commons.collections15.Closure;
import org.ptg.util.ReflectionUtils;

public class FunctionClosure<DataType> implements Closure<DataType>{
    private String mtd ;
    private String className;
	public FunctionClosure(String className,String mtd) {
		super();
		this.mtd = mtd;
		this.className  = className;
	}

	public void execute(DataType arg0) {
		ReflectionUtils.invokeStatic(className,mtd,new Object[]{ arg0});
    }

}
