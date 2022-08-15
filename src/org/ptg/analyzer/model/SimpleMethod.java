package org.ptg.analyzer.model;

import cern.colt.Arrays;

public class SimpleMethod extends Setter{
	@Override
	public String toString() {
		return "Method[params=" + Arrays.toString(params.keySet().toArray(new String[0]))+",name= "+name+",isStatic="+isStatic + "]";
	}
}
