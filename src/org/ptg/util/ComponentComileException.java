package org.ptg.util;

public class ComponentComileException extends Exception {
	String compName;

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public ComponentComileException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ComponentComileException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ComponentComileException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ComponentComileException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	

}
