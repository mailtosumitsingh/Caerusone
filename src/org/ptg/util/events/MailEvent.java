package org.ptg.util.events;

import org.ptg.events.Event;
import org.ptg.util.CommonUtil;

public class MailEvent extends Event {
	protected String subject;
	protected String from;
	protected String to;
	protected String cc;
	protected String bcc;
	protected long recvDate;
	protected long sentDate;
	protected int size;
	protected String contentType;
	protected String body;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public long getRecvDate() {
		return recvDate;
	}
	public void setRecvDate(long recvDate) {
		this.recvDate = recvDate;
	}
	public long getSentDate() {
		return sentDate;
	}
	public void setSentDate(long sentDate) {
		this.sentDate = sentDate;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
public static void main(String[] args) {
	MailEvent e = new MailEvent();
	CommonUtil.saveAndRegisterEventTransformer(e.getClass().getName(),e.getClass().getName(),e.getEventType()+"_store");
}

}
