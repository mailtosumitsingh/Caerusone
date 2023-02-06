package org.ptg.tests.opencv.dro.simpledro;

import java.util.ArrayList;
import java.util.List;

public class DROValue {
	List<Integer> values = new ArrayList<Integer>();

	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "DROValue [values=" + values + "]";
	}

}
