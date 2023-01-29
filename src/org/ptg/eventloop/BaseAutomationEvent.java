package org.ptg.eventloop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseAutomationEvent implements IAutomationEvent {
	String id = UUID.randomUUID().toString();
	Map<String, Object> data = new HashMap<>();
	String msg;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "BaseAutomationEvent [id=" + id + ", data=" + data + ", msg=" + msg + "]";
	}
	public BaseAutomationEvent(String msg) {
		super();
		this.msg = msg;
	}
	
	
}
