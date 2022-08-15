/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.closures;

import java.util.Date;

import org.apache.commons.collections.Closure;

public class TimeBoundClosure implements Closure {
	private int SH;
	private int EH;
	private int SM;
	private int EM;
	private int CSM;
	private int CEM;
	private Closure _wrap;

	public TimeBoundClosure(int sH, int eH, int sM, int eM, Closure wrap) {
		super();
		SH = sH;
		EH = eH;
		SM = sM;
		EM = eM;
		_wrap = wrap;
		CSM = SH*60+SM;
		CEM = EH*60+EM;
	}

	public int getSH() {
		return SH;
	}

	public void setSH(int sH) {
		SH = sH;
	}

	public int getEH() {
		return EH;
	}

	public void setEH(int eH) {
		EH = eH;
	}

	public int getSM() {
		return SM;
	}

	public void setSM(int sM) {
		SM = sM;
	}

	public int getEM() {
		return EM;
	}

	public void setEM(int eM) {
		EM = eM;
	}

	public Closure get_wrap() {
		return _wrap;
	}

	public void set_wrap(Closure wrap) {
		_wrap = wrap;
	}

	public void execute(Object arg0) {
		Date date = new Date();
		int cm = date.getHours()*60+date.getMinutes();
		if(cm>=CSM && cm<=CEM){
		if (_wrap != null) {
			_wrap.execute(arg0);
		}
		}
	}

}
