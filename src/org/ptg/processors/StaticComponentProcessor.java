package org.ptg.processors;

import net.sf.json.JSONObject;

import org.apache.camel.Exchange;
import org.ptg.admin.WebStartProcess;
import org.ptg.stream.StreamManager;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.GenericException;
import org.ptg.util.SpringHelper;
import org.ptg.util.db.DBHelper;

public class StaticComponentProcessor extends AbstractIProcessor{
	static String base = (String) SpringHelper.get("basedir");
	String name = null;

	@Override
	public void childAttach() throws GenericException {
		name = this.extra;
	}

	@Override
	public void childProcess(Exchange msg) throws Exception {
		String toSave = DBHelper.getInstance().getString("Select txt from staticcomponent where name='"+name+"'");
		JSONObject jo = JSONObject.fromObject(toSave);
		String s = jo.getString("txt");
		 WebStartProcess.getInstance().getScriptEngine().runString(s);

	}
	public void attach(String streamName) throws GenericException {
		try {
			stream = StreamManager.getInstance().getStream(streamName);
			extra  = stream.getExtra();
		} catch (Exception e) {
			throw new GenericException("Cannot find stream transformer", e);
		}
		childAttach();
	}
}
