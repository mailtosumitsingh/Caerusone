package org.ptg.http2.handlers.webui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.db.DBHelper;

public class SaveRecentMenu extends AbstractHandler {

	@Override
	public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException, ServletException {
		String menu = httpRequest.getParameter("m");
		System.out.println("SaveRecentMenu.handle(): "+menu);
		if(menu!=null){
		DBHelper.getInstance().executeUpdate("insert into recent_menu(menu) values('"+menu+"')");
		}
		((Request) request).setHandled(true);		
	}

}
