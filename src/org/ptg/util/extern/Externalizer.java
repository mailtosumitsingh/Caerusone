/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.extern;

public abstract class Externalizer {
	public abstract String outStmt(String var, String type) ;
	public abstract String inStmt(String var, String type) ;

}
