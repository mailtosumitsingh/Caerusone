package org.ptg.util.events;

import java.util.Map;

public class ExecCtxEvent extends org.ptg.events.Event{
	Map<String, Object> ctx;
	String targetId;
	

	public ExecCtxEvent(Map<String, Object> ctx, String targetId) {
		this.ctx = ctx;
		this.targetId = targetId;
	}

	public ExecCtxEvent(Map<String, Object> ctx2) {
		this.ctx = ctx2;
	}

	public Map<String, Object> getCtx() {
		return ctx;
	}

	public void setCtx(Map<String, Object> ctx) {
		this.ctx = ctx;
	}

	@Override
	public String getEventType() {
		return "ExecCtxEvent";
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	

}
