/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.events;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.ptg.util.CommonUtil;

public class PageDumpEvent extends Event
{

  public String filename;

  public String foundat;

  public String getFilename()
  {
    return this.filename;
  }

  public void setFilename(String paramString)
  {
    this.filename = paramString;
  }

  public String getFoundat()
  {
    return this.foundat;
  }

  public void setFoundat(String paramString)
  {
    this.foundat = paramString;
  }

  public String toXml()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<Event id=\"");
    localStringBuilder.append(getId());
    localStringBuilder.append("\">");
    localStringBuilder.append("<Property>\n");
    localStringBuilder.append("<key>");
    localStringBuilder.append("filename");
    localStringBuilder.append("</key>\n");
    localStringBuilder.append("<value>\n");
    localStringBuilder.append("<![CDATA[\n");
    localStringBuilder.append(getFilename());
    localStringBuilder.append("\n]]>\n");
    localStringBuilder.append("</value>\n");
    localStringBuilder.append("</Property>\n");
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
    localHashMap.put("filename", getEventProperty("filename"));
    localHashMap.put("foundat", getEventProperty("foundat"));
    return localHashMap;
  }

  public void readExternal(ObjectInput paramObjectInput)    throws IOException, ClassNotFoundException
  {
    super.readExternal(paramObjectInput);
    this.filename = paramObjectInput.readUTF();
    this.foundat = paramObjectInput.readUTF();
  }

  public void writeExternal(ObjectOutput paramObjectOutput)    throws IOException
  {
    super.writeExternal(paramObjectOutput);
    paramObjectOutput.writeUTF(CommonUtil.getNullString(this.filename));
    paramObjectOutput.writeUTF(CommonUtil.getNullString(this.foundat));
  }

  public PageDumpEvent()
  {
    super();
  }
}