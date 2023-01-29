package org.ptg.admin;

import org.ptg.eventloop.BaseAutomationEvent;
import org.ptg.eventloop.EventSender;
import org.ptg.eventloop.IAutomationEvent;
import org.ptg.eventloop.ZeroMQEventServer;

public class AutomationServer {
	ZeroMQEventServer eventServer = new ZeroMQEventServer();
	EventSender eventSender = new EventSender();
	public void start() {
		Runnable r = ()->{
			try {
				eventServer.startEventServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		new Thread(r).start();
		eventSender.sendEvent(new BaseAutomationEvent("Test"));
	}
	
	public void sendEvent(IAutomationEvent event) {
		eventSender.sendEvent(event);
	}

}
