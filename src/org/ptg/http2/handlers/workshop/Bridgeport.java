package org.ptg.http2.handlers.workshop;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.serial.SerialCmdHandler;

import scala.Math;

public class Bridgeport extends AbstractHandler {
	SerialCmdHandler handler = new SerialCmdHandler();
	boolean connected = false;
	public Bridgeport() {
		new Thread(()->{connected = handler.initSerialHandler(true, "COM31");}).start();
	}

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String action = request.getParameter("action");
		String speed =  request.getParameter("speed");
		int spd = Integer.parseInt(speed);
		int wait = 100;//meaningless for now
		if(connected) {
		if(action!=null){
			switch (action){
				case "stop":
						handler.writeData("stop"+",50,50\n",true);
					   break;
				case "mf":
					handler.writeData("mf"+","+spd+","+wait+"\n",true);
						break;
				case "mb":
					handler.writeData("mf"+","+spd+","+wait+"\n",true);
					   break;

			}
		}
			response.getOutputStream().print("Done");
		}
		else {
			response.getOutputStream().print("Not connected");
		}
		
		((Request) request).setHandled(true);
	}

	
}
