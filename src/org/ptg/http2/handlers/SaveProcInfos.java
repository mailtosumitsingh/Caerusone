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



public class SaveProcInfos   extends AbstractHandler {
	   boolean escape = CommonUtil.escapeJavaScriptInSql();

		public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String name = request.getParameter("name");
			String shortname = request.getParameter("shortname");
			String icon = request.getParameter("icon");
			String code = request.getParameter("code");
			if(escape){
				code = StringEscapeUtils.escapeJavaScript(code);
			}
			String delete = "delete from graphs where name='" + name + "'";
			String insert = "insert into procinfos(name,shortname,icon,code) values ('" + name + "','" + shortname + "','" + icon + "','" + code + "')";
			DBHelper.getInstance().executeUpdate(delete);
			DBHelper.getInstance().executeUpdate(insert);
			response.getOutputStream().print("saved");
			arg1.setHandled(true);
		} catch (Exception e) {
			response.getOutputStream().print("Cannot retreive processor infos");
			e.printStackTrace();
		}
	}
}