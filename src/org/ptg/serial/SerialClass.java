package org.ptg.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class SerialClass implements SerialPortEventListener {

	private SerialPort serialPort;
	private String portName;
	
	private InputStream input;
	private OutputStream output;
	private int  TIME_OUT = 2000;
	private int dataRate = 9600;
	private StringBuilder result  = new StringBuilder();
	ISerialListener listener;
	private Object lock = new Object();
	
	public SerialClass(String portName, int dataRate,ISerialListener  listener) {
		this.portName = portName;
		this.dataRate = dataRate;
		this.listener = listener;
		listener.setLock(lock);
	}

	public boolean initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println("Is port: "+currPortId.getName() +" same as "+portName);
				if (currPortId.getName().equalsIgnoreCase(portName)) {
					portId = currPortId;
					break;
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return false;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(dataRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return true;
	}

	public void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
	
	
	private void readSerial() {
		byte[] readBuffer = new byte[400];
	    try {
	        int availableBytes = input.available();
	        if (availableBytes > 0) {
	            int readlen  = input.read(readBuffer, 0, availableBytes);
	            String str = new String(readBuffer, 0, readlen);
	            result.append(str);
	            System.out.println(str);
	            if(str.endsWith("\n")){
	            	listener.onResult(result.toString());
	            	result = new StringBuilder();
	            }
	        }
	    } catch (IOException e) {
	    	
	    }
	}
	public void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				readSerial();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void writeData(String data,boolean sync) {
		try {
			if(sync) {
				synchronized (lock) {
					output.write(data.getBytes());
					System.out.println("Sent: " + data);
					System.out.println("Waiting for lock!");	
					lock.wait();
					System.out.println("Got the lock!");	
				}
			}else {
				output.write(data.getBytes());
				System.out.println("Sent: " + data);
			}
		} catch (Exception e) {
			System.out.println("could not write to port");
		}
	}
	
	public void writeData(String data) {
		writeData(data,false);
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getDataRate() {
		return dataRate;
	}

	public void setDataRate(int dataRate) {
		this.dataRate = dataRate;
	}
	
}