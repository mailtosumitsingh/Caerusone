/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.titan.handlers;

import java.util.HashMap;

import org.ptg.util.titan.IMacroHandler;
import org.ptg.util.titan.TitanCompiler;
import org.ptg.velocity.VelocityHelper;

public class im implements IMacroHandler{

	@Override
	public String handle(String m,String para, String line, String all,int nestcount,TitanCompiler comp) {
		return VelocityHelper.burnTemplate(new HashMap(),"javaimports.vm").toString();
	}

}
