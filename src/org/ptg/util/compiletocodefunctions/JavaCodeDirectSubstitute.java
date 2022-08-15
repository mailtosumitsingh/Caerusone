package org.ptg.util.compiletocodefunctions;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.mapper.AnonDefObj;

public class JavaCodeDirectSubstitute extends JavaMethodCall implements CodeGenHandler{
    Pattern exprPart = Pattern.compile("[a-zA-Z0-9_]+\\.expr(\\(([a-zA-Z0-9_\\,\\[\\]\\(\\)\\.]+)\\))");
    
	public String cleanOuts(AnonDefObj curr, boolean useVarSubstitution, String codeStr, Map<String, String> portNames) {
		/*
		 * Clean codestr replace with the variable substitution.
		 * */
		for(Map.Entry<String, String>en:portNames.entrySet()){
			String portName = en.getKey();
				codeStr = StringUtils.replace(codeStr,"<"+ portName+">","" );
		}
		return codeStr;
	}

}
