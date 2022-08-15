/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.db.DBHelper;

public class SaveStaticComponent extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {

			String ip = request.getRemoteAddr();
			String name = request.getParameter("name");
			String toSave = request.getParameter("tosave");
			String doc = request.getParameter("doc");

			saveStaticComponent(ip, name, toSave, doc);

			response.getOutputStream().print("Document Saved");
		} catch (Exception e) {
			response.getOutputStream().print("Document cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	private void saveStaticComponent(String ip, String name, String toSave, String doc) {
		if (CommonUtil.escapeJavaScriptInSql()) {
			toSave = StringEscapeUtils.escapeJavaScript(toSave);
		}
		String backsql = "insert into deletedstaticcomponent (select * from staticcomponent where name='" + name + "')";
		String delsql = "delete from staticcomponent where name='" + name + "'";
		String inssql = "insert into staticcomponent(name,txt,userid,userip,doc) values (?,?,?,?,?)";
		DBHelper.getInstance().executeUpdate("delete from staticcomponent where name=null");
		DBHelper.getInstance().executeUpdate(backsql);
		DBHelper.getInstance().executeUpdate(delsql);
		DBHelper.getInstance().executePreparedInsert(inssql, name, toSave, ip, ip, doc);
	}

}
