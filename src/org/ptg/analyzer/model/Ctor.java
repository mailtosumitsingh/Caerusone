package org.ptg.analyzer.model;

import cern.colt.Arrays;

public class Ctor extends Setter{
	@Override
	public String toString() {
		return "Ctor [params=" + Arrays.toString(params.keySet().toArray(new String[0]))+",name= "+name + "]";
	}
}
