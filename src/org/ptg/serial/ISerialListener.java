package org.ptg.serial;

public interface ISerialListener {
void onResult(String rs);
void setLock(Object lock);
}
