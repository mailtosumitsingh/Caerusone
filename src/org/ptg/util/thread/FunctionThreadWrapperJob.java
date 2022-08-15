/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.thread;

import org.ptg.util.ReflectionUtils;

public class FunctionThreadWrapperJob implements Runnable{
    private String className;
    private String function;
    private String hint;
    
	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public FunctionThreadWrapperJob(String className, String function, String hint) {
		super();
		this.className = className;
		this.function = function;
		this.hint = hint;
	}

	public FunctionThreadWrapperJob(String className, String function) {
		super();
		this.className = className;
		this.function = function;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public void run() {
		if(hint!=null)
			ReflectionUtils.invokeStatic(className,function,new Object[]{ hint});
		else
			ReflectionUtils.invokeStatic(className,function,new Object[0]);
	}

}
