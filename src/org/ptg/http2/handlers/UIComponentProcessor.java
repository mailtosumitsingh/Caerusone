package org.ptg.http2.handlers;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.ptg.admin.WebStartProcess;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.db.DBHelper;

public class UIComponentProcessor extends AbstractIProcessor{

	
	@Override
	public void childProcess(Exchange msg) throws Exception {
		String name = this.extra;
		String toSave = DBHelper.getInstance().getString("Select txt from staticcomponent where name='"+name+"'");
		JSONObject jo = JSONObject.fromObject(toSave);
		String s = jo.getString("txt");
		WebStartProcess.getInstance().getScriptEngine().runString(s);
	}

}
