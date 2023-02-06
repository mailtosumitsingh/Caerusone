package org.ptg.http2.handlers.workshop;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.handlers.cnc.GcodeGenerator;
import org.ptg.serial.SerialClass;
import org.ptg.serial.SerialListener;

import com.google.common.base.CharMatcher;
import com.google.common.util.concurrent.Uninterruptibles;

public class GrblController extends AbstractHandler {
	SerialClass obj;
	boolean connected = false;
	GcodeGenerator generator = new GcodeGenerator();

	public GrblController() {
		if (obj != null) {
			try {
				obj.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		SerialListener listener = new SerialListener() {
			@Override
			public void onResult(String rs) {
				System.out.println("Raw: "+rs);
				synchronized (lock) {
					
					System.out.println("Checking data!");
					rs = rs.trim();
					rs =  CharMatcher.JAVA_ISO_CONTROL.removeFrom(rs);
					System.out.println("rs: "+rs);
					if (rs.equalsIgnoreCase("ok")) {
						System.out.println("Notifying locks");
						lock.notifyAll();
					}
				}

			}
		};
		new Thread(() -> {
			obj = new SerialClass("COM32", 115200, listener);
			obj.initialize();
		}).start();
	}

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String graphjson = request.getParameter("process");
		String design = request.getParameter("design");

		if (design != null) {
			try {
				String gcodes = generator.generateGcode(name, graphjson, design);
				System.out.println("Going to run : ");
				System.out.println(gcodes);
				String[] lines = StringUtils.split(gcodes, "\n");
				for (String line : lines) {
					line  = CharMatcher.JAVA_ISO_CONTROL.removeFrom(line);
					line = line.trim();
					if(StringUtils.isNotBlank(line)) {
							
							if(!StringUtils.startsWith(line, "G04"))
							{
								
								System.out.println("Now running: "+line);
								handleInternal(line);
							}else {
								System.out.println("Will sleep for a second");
								Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
							}
					}
				}
				System.out.println("Done running gcodes.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.getOutputStream().print("Done");
		} else {
			String action = request.getParameter("action");
			handleInternal(action);
			response.getOutputStream().print("Done");
		}
		((Request) request).setHandled(true);
	}

	private void handleInternal(String action) {
		writeData(action, true);
	}

	private void writeData(String in, boolean wait) {
		obj.writeData(in + "\n", wait);
	}

	public static void main(String[] args) {
		GrblController controller = new GrblController();
		Uninterruptibles.sleepUninterruptibly(10000, TimeUnit.MILLISECONDS);
		while (true) {
			Scanner sc = new Scanner(System.in);
			String in = sc.nextLine();
			while (in != null) {
				System.out.println("Sending : " + in);
				controller.writeData(in, true);
				in = sc.nextLine();
			}
		}
	}

	// todo
	private void moveTo(double x, double y, double z, int feedRate) {

	}

	private void lower(double z) {

	}

	private void higher(double z) {

	}

	private void goHome() {

	}

	private void circle(double x, double y, double radius, double z) {

	}

	private void moveAll(double x, double y, double z, int angles, int feedRate) {

	}

}
