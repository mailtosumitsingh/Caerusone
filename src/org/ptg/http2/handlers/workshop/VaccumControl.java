package org.ptg.http2.handlers.workshop;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.HTTPClientUtil;
import org.ptg.util.SpringHelper;

import com.google.common.util.concurrent.Uninterruptibles;
//toggles only
public class VaccumControl extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String dest = (String) SpringHelper.get("vfdDest");
		String action = request.getParameter("action");
		String url1 = dest + "/?pinOn2=1";
		String url2 = dest + "/?pinOff2=1";

		String url3 = dest + "/?pinOn4=1";
		String url4 = dest + "/?pinOff4=1";
		boolean result = false;

		if (action != null) {
			switch (action) {
			case "stop":
				System.out.println("Stop vaccum");
				System.out.println(url1);
				HTTPClientUtil.getText(url1);
				Uninterruptibles.sleepUninterruptibly(1200, TimeUnit.MILLISECONDS);
				HTTPClientUtil.getText(url2);
				result = true;
				break;
			case "start":
				System.out.println("start vaccum");
				System.out.println(url2);
				HTTPClientUtil.getText(url1);
				Uninterruptibles.sleepUninterruptibly(1200, TimeUnit.MILLISECONDS);
				HTTPClientUtil.getText(url2);
				result = true;
				break;
			case "spindlestop":
				System.out.println("Stop spindle");
				System.out.println(url4);
				HTTPClientUtil.getText(url3);
				Uninterruptibles.sleepUninterruptibly(1200, TimeUnit.MILLISECONDS);
				HTTPClientUtil.getText(url4);
				result = true;
				break;
			case "spindlestart":
				System.out.println("start spindle");
				System.out.println(url4);
				HTTPClientUtil.getText(url3);
				Uninterruptibles.sleepUninterruptibly(1200, TimeUnit.MILLISECONDS);
				HTTPClientUtil.getText(url4);
				result = true;
				break;
			default:
				result = false;
				break;
			}
		}
		if(result)
			response.getOutputStream().print("Done");
		else
			response.getOutputStream().print("Failed no action provided");

		((Request) request).setHandled(true);
	}

}
