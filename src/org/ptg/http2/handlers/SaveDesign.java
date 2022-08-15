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

public class SaveDesign extends AbstractHandler {
	boolean escape = CommonUtil.escapeJavaScriptInSql();

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {

			String ip = request.getRemoteAddr();
			String name = request.getParameter("name");
			String toSave = request.getParameter("tosave");
			String configsave = request.getParameter("configsave");
			if (escape) {
				toSave = StringEscapeUtils.escapeJavaScript(toSave);
			}
			if (configsave == null || configsave.length() <= 0) {
				configsave = "[]";
				if (DBHelper.getInstance().exists("select name from pageconfig where name='" + name + "'")) {
					String backsql = "insert into deletedpageconfig (select * from pageconfig where name='" + name + "')";
					String inssql = "update pageconfig set graph='" + "?" + "',config='" + "?" + "' ,userid=0,userip='" + "?" + "' where name='" + "?" + "'";
					DBHelper.getInstance().executeUpdate(backsql);
					DBHelper.getInstance().executePreparedUpdate(inssql, toSave, configsave, ip, name);
				} else {
					String backsql = "insert into deletedpageconfig (select * from pageconfig where name='" + name + "')";
					String inssql = "insert into pageconfig(name,graph,config,userid,userip) values ('" + "?" + "','" + "?" + "','" + "?" + "'," + "?" + ",'" + "?" + "')";
					DBHelper.getInstance().executeUpdate(backsql);
					int i = DBHelper.getInstance().executePreparedInsert(inssql, name, "", "", "0", ip);
					String sql = "update pageconfig set graph=? and config=? where id=?";
					DBHelper.getInstance().executePreparedUpdate(sql, toSave, configsave, i);

				}
				response.getOutputStream().print("Document updated");
			} else {
				if (escape) {
					configsave = StringEscapeUtils.escapeJavaScript(configsave);
				}
				String backsql = "insert into deletedpageconfig select id,graph,name,userid,userip,config from pageconfig where name='" + name + "'";
				String delsql = "delete from pageconfig where name='" + name + "'";
				String inssql = "insert into pageconfig(name,graph,config,userid,userip) values (?,?,?,?,?)";
				DBHelper.getInstance().executeUpdate(backsql);
				DBHelper.getInstance().executeUpdate(delsql);
				int i = DBHelper.getInstance().executePreparedInsert(inssql, name, toSave, configsave, "0", ip);

				response.getOutputStream().print("Document Saved");
			}

		} catch (Exception e) {
			response.getOutputStream().print("Document cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
