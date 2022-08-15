package org.ptg.util.events;

public class VizDataEvent extends org.ptg.events.Event{
	protected String targetId;
	protected Object[]dataPoints;
	public VizDataEvent (String targetId,Object ...points){
		this.targetId = targetId;
		this.dataPoints = points;
		
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	@Override
	public String getEventType() {
		return "VizDataEvent";
	}
	public Object[] getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(Object[] dataPoints) {
		this.dataPoints = dataPoints;
	}
}
