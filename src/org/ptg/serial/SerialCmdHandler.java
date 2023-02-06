package org.ptg.serial;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.serial.SerialClass;
import org.ptg.serial.SerialListener;

public class SerialCmdHandler extends AbstractHandler {
	String lastResult = null;
	LinkedBlockingQueue<String> cmds = new LinkedBlockingQueue<String>();
	Thread th = null;
	int cx = 0;
	int cy = 0;
	SerialClass obj;
	boolean errored = false;

	public  boolean initSerialHandler(boolean reinit, String port) {
		if (reinit == true || obj == null) {
			cmds.clear();
			errored = false;

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
				//	System.out.println("Got a result : " + rs);
					lastResult = rs;
					if (rs.contains(">")) {
						System.out.println("Position: ");
						rs = StringUtils.substringAfterLast(rs, ">");
						rs = StringUtils.substringBefore(rs, "\n");
						if (rs != null && rs.length() > 0) {
							String[] xy = rs.split(",");
							cx = Integer.parseInt(xy[0].trim());
							cy = Integer.parseInt(xy[1].trim());
							System.out.println("Current x,y : " + cx + " , " + cy);
						}
						synchronized (lock) {
							lock.notifyAll();
						}
					}
					if (rs.startsWith("Done")) {
						System.out.println("Done!!!");
						synchronized (lock) {
							lock.notifyAll();
						}
					}
					System.out.println(rs);
				}
			};
			
			obj = new SerialClass(port, 9600, listener);
			boolean ret = obj.initialize();
			return ret;
		}else {
			return false;
		}
			
	}

	 {

		th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String cmd;
						cmd = cmds.take();
						if (cmd.startsWith("waitfor")) {
							waitfor(cmd);
						} else if (cmd.startsWith("sleepcnc")) {
							Thread.currentThread().sleep(5000);
						} else if (cmd != null) {
							obj.writeData(cmd);
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}

		});
		th.start();
	
	}

	private  void waitfor(String cmd) {
		System.out.println("Waiting for :" + cmd);
		cmd = cmd.substring(8);
		String[] xy = cmd.split(",");
		int wx = Integer.parseInt(xy[0].trim());
		int wy = Integer.parseInt(xy[1].trim());
		int count = 0;
		while (cx != wx && wy != cy && count < 10) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
		}
		obj.writeData("getpos,100,100, \n ");
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("wait for :" + wx + "," + wy + " current: " + cx + "," + cy);

	}

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		String op = request.getParameter("op");
		String port = request.getParameter("port");
		if (op != null && op.equals("reinit")) {
			System.out.println("Reniniting ...: " + port);
			initSerialHandler(true, port);
			response.getOutputStream().print("{\"result\":" + "Initialized successfully" + "}");
		} else if (op == null || op.equals("cnc")) {
			initSerialHandler(false, port);
			try {
				sendCmdToCnc(cmd);
				response.getOutputStream().print("{\"result\":" + lastResult + "}");
			} catch (Exception e) {
				response.getOutputStream().print("Could not run cmd :\n" + e);
				e.printStackTrace();
			}
		}
		((Request) request).setHandled(true);
	}

	private void sendCmdToCnc(String cmd) throws InterruptedException {
		lastResult = null;
		System.out.println("Got a command adding: " + cmd + " Totals: " + cmds.size());
		cmds.put(cmd + "\n");
	}

	public static void main(String[] ag) {
		try {
			SerialCmdHandler handler = new SerialCmdHandler();
			handler.initSerialHandler(true, "COM3");
			Scanner sc = new Scanner(System.in);
			String in = sc.next();
			while (in != null) {
				System.out.println("Sending : " + in);
				handler.writeData( in,true);
				in = sc.next();
			}
			handler.close();
		} catch (Exception e) {
		}

	}

	public void close() {
		obj.close();
	}

	public void writeData(String in,boolean wait) {
		obj.writeData(in + "\n",wait);
	}

	
}