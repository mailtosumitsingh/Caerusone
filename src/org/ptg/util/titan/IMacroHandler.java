/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.titan;

public interface IMacroHandler {
 String handle(String m , String para,String line, String all,int nestcount,TitanCompiler comp);
}
