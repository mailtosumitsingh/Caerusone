/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.lang.reflect.Method;

public interface IMethodLocatorStrategy {
	public Method getBruteForceSetMethod(Class cc,String prop);
	public Method getBruteForceGetMethod(Class cc,String prop);

}
