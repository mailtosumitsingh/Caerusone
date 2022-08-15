package org.ptg.cnc;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.ptg.events.Event;
import org.ptg.util.CommonUtil;

public class InfoEvent extends Event{
	String msg = null;
    String eventType = "CNCInfoEvent";
	public String getMsg() {
		return msg;
	}
	  public String toXml()
	  {
	    StringBuilder localStringBuilder = new StringBuilder();
	    localStringBuilder.append("<Event id=\"");
	    localStringBuilder.append(getId());
	    localStringBuilder.append("\">");
	    localStringBuilder.append("<Property>\n");
	    localStringBuilder.append("<key>");
	    localStringBuilder.append("msg");
	    localStringBuilder.append("</key>\n");
	    localStringBuilder.append("<value>\n");
	    localStringBuilder.append("<![CDATA[\n");
	    localStringBuilder.append(getMsg());
	    localStringBuilder.append("\n]]>\n");
	    localStringBuilder.append("</value>\n");
	    localStringBuilder.append("</Property>\n");
	    localStringBuilder.append("</Event>");
	    return localStringBuilder.toString();
	  }

	  public String toJson()
	  {
	    JSONObject localJSONObject = JSONObject.fromObject(this);
	    return localJSONObject.toString();
	  }

	  public Map toMap()
	  {
	    HashMap localHashMap = new HashMap();
	    localHashMap.put("id", getEventProperty("id"));
	    localHashMap.put("xref", getEventProperty("xref"));
	    localHashMap.put("system", getEventProperty("system"));
	    localHashMap.put("createdTime", getEventProperty("createdTime"));
	    localHashMap.put("pidStr", getEventProperty("pidStr"));
	    localHashMap.put("corrId", getEventProperty("corrId"));
	    localHashMap.put("msg", getMsg());
	    localHashMap.put("eventType", getEventType());
	    return localHashMap;
	  }

	  public void readExternal(ObjectInput paramObjectInput)    throws IOException, ClassNotFoundException
	  {
	    super.readExternal(paramObjectInput);
	    this.msg = paramObjectInput.readUTF();
	  }

	  public void writeExternal(ObjectOutput paramObjectOutput)    throws IOException
	  {
	    super.writeExternal(paramObjectOutput);
	    paramObjectOutput.writeUTF(CommonUtil.getNullString(getMsg()));
	  }
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public InfoEvent(String msg) {
		super();
		this.msg = msg;
	}
	@Override
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	

}
