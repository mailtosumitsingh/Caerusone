/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.db.DBHelper;

public class DBSlayer extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("type");
		Map<String, String> sqlMap = new HashMap<String,String>();/*type to sql map*/
		sqlMap.put("WiFi Locations","SELECT * FROM nyc_wifilocs");
		sqlMap.put("Botonical Gardens","SELECT * FROM nyc_botonicalgarden");
		try {
			String query = sqlMap.get(name);
			String result = DBHelper.getInstance().getResultJson(query);
			JSONArray jj = JSONArray.fromObject(result);
			JSONObject j = (JSONObject) jj.get(0);
			for(Object o: j.keySet()){
				String s = (String) o;
				System.out.println(s);
			}
			response.getOutputStream().print(result);
		} catch (Exception e) {
			response.getOutputStream().print("{'result':'failed tp retrieve'}");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
