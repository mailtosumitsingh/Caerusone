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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.DynaObjectHelper;
import org.ptg.util.ReflectionUtils;



public abstract class Event  implements java.io.Externalizable,Cloneable{
String xref;
String system;
String eventGroup;
long createdTime;
int id;
String pidStr;
String corrId;
String status ;

private long senderSeqNo = 0 ;
private long checkouttime;
private String where = "START";//activity
transient Map<String, Method> mtds = new HashMap<String,Method>();
transient List<String> stk = new java.util.ArrayList<String>();

public String getEventGroup() {
	return eventGroup;
}
public void setEventGroup(String eventGroup) {
	this.eventGroup = eventGroup;
}

public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
	xref = in.readUTF();
	system = in.readUTF();
	createdTime = in.readLong();
	eventGroup = in.readUTF();
	where = in.readUTF();
}

public void writeExternal(ObjectOutput out) throws IOException {
	out.writeUTF(xref==null?"":xref);
	out.writeUTF(system==null?"":system);
	out.writeLong(createdTime);
	out.writeUTF(eventGroup==null?"":eventGroup);
	out.writeUTF(where==null?"":where);
}

public String getPidStr() {
	return pidStr;
}
public void setPidStr(String pid) {
	this.pidStr = pid;
}
public String getCorrId() {
	return corrId;
}
public void setCorrId(String corrId) {
	this.corrId = corrId;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public Event(){
	createdTime = System.currentTimeMillis();
}
public String getXref() {
	return xref;
}
public void setXref(String xrefId) {
	this.xref = xrefId;
}
public String getSystem() {
	return system;
}
public String getEventType(){
return	this.getClass().getName();

}
public void setSystem(String system) {
	this.system = system;
}
public long getCreatedTime() {
	return createdTime;
}
public void setCreatedTime(long t) {
	createdTime = t;
}

public void setEventProperty(String prop, Object val){
	String actual= WordUtils.capitalize(prop);
	actual = "set"+actual;
	Method mtd = mtds.get(actual);
	try {
	if(mtd!=null){
			mtd.invoke(getThis(), new Object[]{val});
	}else{
		mtd = 	ReflectionUtils.getMethod(getMyClass(),actual);
		if(mtd!=null){
			mtds.put(actual, mtd);
			mtd.invoke(getThis(), new Object[]{val});
			
		}
	}} catch (Exception e) {
		System.out.println("Failed to set :" +prop +", val : "+val);
		e.printStackTrace();
	}
}
public boolean hasEventProperty(String prop){
	String actual= WordUtils.capitalize(prop);
	actual = "set"+actual;
	Method mtd = mtds.get(actual);
	try {
	if(mtd!=null){
			return true;
	}else{
		mtd = 	ReflectionUtils.getMethod(getMyClass(),actual);
		if(mtd!=null){
			mtds.put(actual, mtd);
			return true;
		}
	}} catch (IllegalArgumentException e) {
		e.printStackTrace();
	}catch (SecurityException e) {
		e.printStackTrace();
	}
	return false;
}
public String getEventStringProperty(String prop){
	return (String)getEventProperty(prop);
}
public Double getEventDoubleProperty(String prop){
	return (Double)getEventProperty(prop);
}
public Float getEventFloatProperty(String prop){
	return (Float)getEventProperty(prop);
}

public Integer getEventIntegerProperty(String prop){
	return (Integer)getEventProperty(prop);
}
public Long getEventLongProperty(String prop){
	return (Long)getEventProperty(prop);
}
public java.util.Date getDateStringProperty(String prop){
	return (java.util.Date)getEventProperty(prop);
}

public Object getEventProperty(String prop){
	String actual= WordUtils.capitalize(prop);
	actual = "get"+actual;
	Method mtd = mtds.get(actual);
	try {
	if(mtd!=null){
			return mtd.invoke(getThis(), new Object[0]);
	}else{
		mtd = getMyClass().getMethod(actual, new Class[0]);
		if(mtd!=null){
			mtds.put(actual, mtd);
		}
		return mtd.invoke(getThis(), new Object[0]);
	}} catch (IllegalArgumentException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		e.printStackTrace();
	} catch (SecurityException e) {
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		e.printStackTrace();
	}
	return null;
}
public Annotation getPropertyAnnotation(String field){
	try {
		return DynaObjectHelper.getAnnotation(this.getClass().getName(), field, "Property");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
public Object getAnnotationValue(String field,String annotation){
	try {
		Annotation an =  DynaObjectHelper.getAnnotation(this.getClass().getName(), field, "Property");
		if(an!=null){
			MemberValue temp = ((MemberValue)an.getMemberValue(annotation));
			return ReflectionUtils.invoke(temp, "getValue", new Object[0]);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
public String toXml(){
	JSONObject json = JSONObject.fromObject(this);  
	return (new XMLSerializer()).write( json );  
	}
public String toJson(){
	return CommonUtil.toJson(this);
}
public java.util.Map toMap(){return null;}
public long getSenderSeqNo() {
    return senderSeqNo;
}

public void setSenderSeqNo(long senderSeqNo) {
    this.senderSeqNo = senderSeqNo;
}
protected Class getMyClass(){
	return this.getClass();
}
protected Object getThis(){
	return this;
}
public void checkout(){
	checkouttime = System.currentTimeMillis();
}
public long stamp(){
	return checkouttime ;
}
public Event clone() throws CloneNotSupportedException {
	return (Event)super.clone();
}
public void addPath(String s){
	synchronized(stk){
	stk.add(s);
	}
}
public void removePath(String s){
	synchronized(stk){
	stk.remove(stk.size()-1);
	}
}
public void setStk(String t){
	if(t==null)return;
	String[] toadd = StringUtils.split(t,":");
	for(String s : toadd)
	{
		stk.add(s);
	}
}
public String getStk(){
	StringBuilder sb = new StringBuilder();
	int i  =0;
	for(String s:stk){
		i++;
		sb.append(s);
		if(i!=stk.size())
			sb.append(":");
	}
	return sb.toString();
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getWhere() {
	return where;
}
public void setWhere(String where) {
	this.where = where;
}
public String toString() {
	return toJson();
}
}
