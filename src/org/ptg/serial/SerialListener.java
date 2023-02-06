package org.ptg.serial;

import org.ptg.serial.ISerialListener;

public class SerialListener implements ISerialListener {
	protected Object lock ;
	
	
	
	@Override
	public void onResult(String rs) {

	}

	@Override
	public void setLock(Object lock) {
		this.lock = lock;

	}

}
