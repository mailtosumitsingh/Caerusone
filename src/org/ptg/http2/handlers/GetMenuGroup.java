/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.Group;
import org.ptg.util.mapper.v2.FPGraph2;

public class GetMenuGroup extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			List<Group> grps = getGroups();
			String json = CommonUtil.jsonFromCollection(grps);
			response.getOutputStream().print(json);
		} catch (Exception e) {
			response.getOutputStream().print("Node cannot be saved");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	public List<Group> getGroups() {
		List<Group> ret = new LinkedList<Group>();
		FPGraph2 o = CommonUtil.buildDesignMappingGraph2("Groups");
		if (o != null) {
			for (Group g : o.getGroups().values()) {
				Set<String> uniq = new HashSet<String>();
				for (String s : g.getItems()) {
					if(uniq.contains(s)){
						System.out.println("Group "+g.getId()+" contains duplicate of "+s);
					}
					uniq.add(s);
				}
				g.setItems(uniq.toArray(new String[0]));
				ret.add(g);
			}
		}
		return ret;
	}

}
