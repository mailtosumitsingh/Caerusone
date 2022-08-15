/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.HashMap;
import java.util.Map;

public class JavaRuleManager {
	Map<String,ITransformationRule> trules = new HashMap<String,ITransformationRule>();
	Map<String,IValidationRule> vrules = new HashMap<String,IValidationRule>();
	Map<String,IBusinessRule> brules = new HashMap<String,IBusinessRule>();
	
	public void registerTransformationRule(String name,ITransformationRule rule){
		   trules.put(name,rule);
	}
	public ITransformationRule getTransformationRule(String name){
		return trules.get(name);
	}
	public void registerValidationRule(String name,IValidationRule rule){
		 vrules.put(name,rule);
	}
	public IValidationRule getValidationRule(String name){
		  return vrules.get(name);
	}
public void registerBusinessRule(String name,IBusinessRule rule){
		brules.put(name,rule);
	}
	public IBusinessRule getBusinessRule(String name){
		return brules.get(name);
	}
}
