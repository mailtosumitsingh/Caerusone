/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.events;

import org.ptg.admin.WebStartProcess;





public class EventManager {
public void pumpEvent(Event e){
}
private  static class SingletonHolder {
	private static final EventManager INSTANCE = new EventManager();
	static{
		
	}
}

public  static EventManager getInstance() {
	return SingletonHolder.INSTANCE;
}	
}
