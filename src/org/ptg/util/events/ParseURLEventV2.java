/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.events;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.ptg.events.Event;
import org.ptg.util.CommonUtil;

public class ParseURLEventV2 extends Event
{

  public String parser;

  public String foundat;

  public String url;

 

  public String toXml()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<Event id=\"");
    localStringBuilder.append(getId());
    localStringBuilder.append("\">");
    localStringBuilder.append("<Property>\n");
    localStringBuilder.append("<key>");
    localStringBuilder.append("foundat");
    localStringBuilder.append("</key>\n");
    localStringBuilder.append("<value>\n");
    localStringBuilder.append("<![CDATA[\n");
    localStringBuilder.append(getFoundat());
    localStringBuilder.append("\n]]>\n");
    localStringBuilder.append("</value>\n");
    localStringBuilder.append("</Property>\n");
    localStringBuilder.append("<Property>\n");
    localStringBuilder.append("<key>");
    localStringBuilder.append("parser");
    localStringBuilder.append("</key>\n");
    localStringBuilder.append("<value>\n");
    localStringBuilder.append("<![CDATA[\n");
    localStringBuilder.append(getParser());
    localStringBuilder.append("\n]]>\n");
    localStringBuilder.append("</value>\n");
    localStringBuilder.append("</Property>\n");
    localStringBuilder.append("<Property>\n");
    localStringBuilder.append("<key>");
    localStringBuilder.append("url");
    localStringBuilder.append("</key>\n");
    localStringBuilder.append("<value>\n");
    localStringBuilder.append("<![CDATA[\n");
    localStringBuilder.append(getUrl());
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
    localHashMap.put("foundat", getEventProperty("foundat"));
    localHashMap.put("parser", getEventProperty("parser"));
    localHashMap.put("url", getEventProperty("url"));
    return localHashMap;
  }

  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    super.readExternal(paramObjectInput);
    parser = paramObjectInput.readUTF();
    foundat = paramObjectInput.readUTF();
    url = paramObjectInput.readUTF();
  }

  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    super.writeExternal(paramObjectOutput);
    paramObjectOutput.writeUTF(CommonUtil.getNullString(parser));
    paramObjectOutput.writeUTF(CommonUtil.getNullString(foundat));
    paramObjectOutput.writeUTF(CommonUtil.getNullString(url));
  }

  public ParseURLEventV2()
  {
    super();
  }

public String getParser() {
	return parser;
}

public void setParser(String parser) {
	this.parser = parser;
}

public String getFoundat() {
	return foundat;
}

public void setFoundat(String foundat) {
	this.foundat = foundat;
}

public String getUrl() {
	return url;
}

public void setUrl(String url) {
	this.url = url;
}
}