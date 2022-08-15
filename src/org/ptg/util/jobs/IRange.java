/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs;

import java.util.Map;

public interface IRange {
    public void setStart(Object  a);
    public void setEnd(Object  a);
    public boolean isInRange(Map.Entry o);
    public Object getStart();
    public Object getEnd();
}
