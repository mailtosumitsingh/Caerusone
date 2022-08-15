/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.db.SqlHelper;

import Zql.ParseException;

public class GetSQLDefinition extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = (String) arg1.getParameter("sql");
		String type = (String) arg1.getParameter("type");
		SqlHelper h = new SqlHelper();
		// it could be sql,dynasql , sp
		/* is dyna when it has one of the selects as * expression */
		List c = null;
		try {
			if (type == null || type.equals("sql")) {
				type = "sql";
				c = h.parseSql(name);
			} else if (type.equals("sp")) {
				c = h.parseSP(name);
			} else if (type.equals("dynasql")) {
				c = h.parseDynaSql(name);
			}
			response.getOutputStream().print(CommonUtil.jsonFromCollection(c));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}
}
