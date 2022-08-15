package org.ptg.util.model;

import java.util.Date;

public class CalEvent {
		int id;
		String summary;
		String task;
		String cal;
		int startYear;
		int startMon;
		int startDay;
		int startHour;
		int startMin;
		int startSec;
		
		int endYear;
		int endMon;
		int endDay;
		int endHour;
		int endMin;
		int endSec;
		
		public void setEnd(Date dt){
			endYear = dt.getYear()+1900;
			endMon = dt.getMonth()+1;
			endDay = dt.getDate();
			endHour = dt.getHours();
			endMin = dt.getMinutes();
			endSec = dt.getSeconds();
		}		
		public void setStart(Date dt){
			startYear = dt.getYear()+1900;
			startMon = dt.getMonth()+1;
			startDay = dt.getDate();
			startHour = dt.getHours();
			startMin = dt.getMinutes();
			startSec = dt.getSeconds();
		}
		public int getStartYear() {
			return startYear;
		}
		public void setStartYear(int startYear) {
			this.startYear = startYear;
		}
		public int getStartMon() {
			return startMon;
		}
		public void setStartMon(int startMon) {
			this.startMon = startMon;
		}
		public int getStartDay() {
			return startDay;
		}
		public void setStartDay(int startDay) {
			this.startDay = startDay;
		}
		public int getStartHour() {
			return startHour;
		}
		public void setStartHour(int startHour) {
			this.startHour = startHour;
		}
		public int getStartMin() {
			return startMin;
		}
		public void setStartMin(int startMin) {
			this.startMin = startMin;
		}
		public int getStartSec() {
			return startSec;
		}
		public void setStartSec(int startSec) {
			this.startSec = startSec;
		}
		public int getEndYear() {
			return endYear;
		}
		public void setEndYear(int endYear) {
			this.endYear = endYear;
		}
		public int getEndMon() {
			return endMon;
		}
		public void setEndMon(int endMon) {
			this.endMon = endMon;
		}
		public int getEndDay() {
			return endDay;
		}
		public void setEndDay(int endDay) {
			this.endDay = endDay;
		}
		public int getEndHour() {
			return endHour;
		}
		public void setEndHour(int endHour) {
			this.endHour = endHour;
		}
		public int getEndMin() {
			return endMin;
		}
		public void setEndMin(int endMin) {
			this.endMin = endMin;
		}
		public int getEndSec() {
			return endSec;
		}
		public void setEndSec(int endSec) {
			this.endSec = endSec;
		}
		public String getSummary() {
			return summary;
		}
		public void setSummary(String summary) {
			this.summary = summary;
		}
		public String getTask() {
			return task;
		}
		public void setTask(String task) {
			this.task = task;
		}
		public String getCal() {
			return cal;
		}
		public void setCal(String cal) {
			this.cal = cal;
		}
		public int getId() { 
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		
}