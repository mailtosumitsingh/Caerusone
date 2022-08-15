/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

public class GenericException extends Exception {

	public GenericException(String string) {
		super(string);
	}

	public GenericException(String string, Exception e) {
		super(string,e);
	}

}
