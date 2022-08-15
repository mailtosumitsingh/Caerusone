/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.events;

import java.lang.reflect.Method;

import org.apache.commons.lang.WordUtils;
import org.ptg.util.IMethodLocatorStrategy;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.SpringHelper;

public class ObjectWrapperEvent extends Event {
	
	protected Class getMyClass() {
		return _wrapped.getClass();
	}

	protected Object getThis() {
		return _wrapped;
	}

	private Object _wrapped;

	public ObjectWrapperEvent(Object wrapped) {
		_wrapped = wrapped;
	}

	public void setEventProperty(String prop, Object val) {
		String actual = WordUtils.capitalize(prop);
		actual = "set" + actual;
		Method mtd = mtds.get(actual);
		try {
			if (mtd != null) {
				mtd.invoke(getThis(), new Object[] { val });
			} else {
				mtd = ReflectionUtils.getMethod(getMyClass(), actual);
				if (mtd != null) {
					mtds.put(actual, mtd);
					mtd.invoke(getThis(), new Object[] { val });
				}else{
					System.out.println("Failed to get method will try bruteforce:"+actual);
					mtd = getBruteForceSetMethod(getMyClass(),prop);
					if (mtd != null) {
						mtds.put(actual, mtd);
						mtd.invoke(getThis(), new Object[] { val });
					}	else{
						System.out.println("Brute Force Failed:"+actual);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getEventProperty(String prop) {
		Object ret = null;
		String actual = WordUtils.capitalize(prop);
		actual = "get" + actual;
		Method mtd = mtds.get(actual);
		try {
			if (mtd != null) {
				return mtd.invoke(getThis(), new Object[0]);
			} else {
				mtd = getMyClass().getMethod(actual, new Class[0]);
				if (mtd != null) {
					mtds.put(actual, mtd);
					ret = mtd.invoke(getThis(), new Object[0]);
				}else{
					System.out.println("Failed to get method will try bruteforce:"+actual);
					mtd = getBruteForceGetMethod(getMyClass(),prop);
					if (mtd != null) {
						mtds.put(actual, mtd);
						ret = mtd.invoke(getThis(), new Object[0]);
					}	else{
						System.out.println("Brute Force Failed:"+actual);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	public Method getBruteForceSetMethod(Class cc,String prop){
		IMethodLocatorStrategy strategy  = (IMethodLocatorStrategy)SpringHelper.get("methodLocatorHelper");
		return strategy.getBruteForceSetMethod(cc, prop);
	}
	public Method getBruteForceGetMethod(Class cc,String prop){
		IMethodLocatorStrategy strategy  = (IMethodLocatorStrategy)SpringHelper.get("methodLocatorHelper");
		return strategy.getBruteForceGetMethod(cc, prop);
	}

}
