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

public class VFD extends AbstractHandler {
	

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String dest = (String) SpringHelper.get("vfdDest");
		String action = request.getParameter("action");
		String speed = null;
		if (action != null) {
			switch (action) {
			case "stop":
				makeCall(dest + "/?pinOff0=1");
				makeCall(dest + "/?pinOff1=1");
				makeCall(dest + "/?stop0=1");
				setSpeed("0");
				break;
			case "start":
				makeCall(dest + "/?pinOn0=1");
				makeCall(dest + "/?pinOff1=1");
				speed = request.getParameter("speed");
				setSpeed(speed);
				break;
			case "reverse":
				makeCall(dest + "/?pinOn1=1");
				makeCall(dest + "/?pinOff0=1");
				speed = request.getParameter("speed");
				setSpeed(speed);
				break;
			case "up":
				makeCall(dest + "/?up0=1");
				break;
			case "down":
				makeCall(dest + "/?down0=1");
				break;
			case "setspeed":
				speed = request.getParameter("speed");
				String dir = request.getParameter("dir");
				int spd = Integer.valueOf(speed);
				if (spd > 0) {
					if (dir == null) {
						//just update speed
						setSpeed(speed);
					} else {
						//starting or changing direction
						if (dir.equalsIgnoreCase("Reverse")) {
							makeCall(dest + "/?pinOn1=1");
							makeCall(dest + "/?pinOff0=1");
							setSpeed(speed);
						} else {
							// forward
							makeCall(dest + "/?pinOn0=1");
							makeCall(dest + "/?pinOff1=1");
							setSpeed(speed);
						}
					}
				} else {// turn off
					makeCall(dest + "/?pinOff0=1");
					makeCall(dest + "/?pinOff1=1");
					makeCall(dest + "/?stop0=1");
					setSpeed(speed);
				}
				break;
			}
		}
		((Request) request).setHandled(true);
	}

	private void makeCall(String url) {
		System.out.println(url);
		Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
		HTTPClientUtil.getText(url);
	}

	private void setSpeed(String speed) {
		String dest = (String) SpringHelper.get("vfdDest");
		int spd = Integer.parseInt(speed);
		if (spd > 0 && spd < 10) {
			makeCall(dest + "/?setspeed01" + spd + "=1");
		} else if (spd > 9 && spd < 100) {
			makeCall(dest + "/?setspeed02" + spd + "=1");
		} else if (spd > 99 && spd < 256) {
			makeCall(dest + "/?setspeed03" + spd + "=1");
		}

	}

}
