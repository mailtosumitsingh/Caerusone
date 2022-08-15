/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs;

import java.util.Map;

public class IntRange implements IRange{
    int start;
    int end;
    public void setStart(Object a) {
            if( a instanceof Integer){
            start = (Integer)a;
            }
    }

    public void setEnd(Object a) {
             if( a instanceof Integer){
            end = (Integer)a;
            }
    }

    public boolean isInRange(Map.Entry  o) {
        if( o.getValue() instanceof Integer){
            int mid  = (Integer)o.getValue();
            return (mid >= start && mid<end);
            }
        return false;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }
     public String toString() {
      return "{Start: "+start+", End: "+end+"}";
    }
}
