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
import org.ptg.util.db.DBHelper;


public class SaveDoc extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			
			 String name = request.getParameter("name");
		     String tosave = request.getParameter("doc");
		     tosave = StringEscapeUtils.escapeSql(tosave);
		     String delsql = "delete from procdocs where name='"+name+"'";
              DBHelper.getInstance().executeUpdate(delsql);
		     String inssql = "insert into procdocs(name,doc) values ('"+name+"','"+tosave+"')";
		     DBHelper.getInstance().executeUpdate(inssql);
			
			response.getOutputStream().print("Document Saved");
		} catch (Exception e) {
			response.getOutputStream().print("Document cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
