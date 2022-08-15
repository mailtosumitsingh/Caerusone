/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.events;




public class HeartBeatEvent extends org.ptg.events.Event{ 
    private long millis;

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
    
    public String getEventType() {
        return "HeartBeatEvent";
    }
}
