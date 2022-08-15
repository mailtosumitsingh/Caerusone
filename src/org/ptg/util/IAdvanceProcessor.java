/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import org.ptg.events.Event;

public interface IAdvanceProcessor extends IProcessor {
	public void processCtrlZ(String where,Event e) ;
	public void processForward(String where,Event e);
	public void processError(String where,Event e) ;
	public void processCopy(String where,Event e) ;
	public void processCtrlZAsync(String where,Event e) ;
	public void processForwardAsync(String where,Event e);
	public void processErrorAsync(String where,Event e) ;
	public void processCopyAsync(String where,Event e) ;
	public void deleteAll(String where,Event e);
	public void delete(String where,Event e);

}
