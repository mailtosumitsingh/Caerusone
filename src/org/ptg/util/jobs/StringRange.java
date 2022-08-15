/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs;

import java.util.Map;

public class StringRange implements IRange{
    String start;
    String end;

    public void setStart(Object a) {

        start = (String) a;
    }

    public void setEnd(Object a) {
        end = (String) a;
    }

    public boolean isInRange(Map.Entry o) {
        String mid = (String) o.getValue();
        if(start.compareTo(end)!=0){
        int before = start.compareTo(mid);
        int after = end.compareTo(mid);
        return (before <= 0 && after > 0);
        }else{
         return start.equals(mid);
        }
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String toString() {
      return "{Start: "+start+", End: "+end+"}";
    }
}
