/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.events;
//org.ptg.util.events.ExcelReadEvent 
public class ExcelReadEvent extends org.ptg.events.Event{
	String url;
	String query;
	String excelEventype  ;
	String workbook ;
	int start;
	int end;
	int colstart;
	String doc;
	String docprop;
	public String getDocprop() {
		return docprop;
	}
	public void setDocprop(String docprop) {
		this.docprop = docprop;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getColstart() {
		return colstart;
	}
	public void setColstart(int colstart) {
		this.colstart = colstart;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

	public String getWorkbook() {
		return workbook;
	}
	public void setWorkbook(String workbook) {
		this.workbook = workbook;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public String getExcelEventype() {
		return excelEventype;
	}
	public void setExcelEventype(String excelEventype) {
		this.excelEventype = excelEventype;
	}

}
