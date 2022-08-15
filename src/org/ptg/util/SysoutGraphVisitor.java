package org.ptg.util;

import java.util.Map;

public class SysoutGraphVisitor extends AbstractGraphVisitor {

	public boolean multiFanoutAllowed() {
		return true;
	}

	@Override
	public Map getConfig() {
		return null;
	}

	public void setConfig(Map config) {
		
	}

}
