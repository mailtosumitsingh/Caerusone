/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs;

import java.util.Map;

public abstract class ICJobFast  implements IJob{
    protected IntRange ran;
    protected Map map;
    static protected final boolean pmatch = true;
    static protected final boolean pmmatch = false;
    public void setRange(IntRange ran){
        System.out.println("My Range is "+ran.toString());
        this.ran  = ran;
    }
    public void setMap(Map vals){
        this.map = vals;
    }
    public void run() {
        for (Integer i =ran.getStart();i<ran.getEnd();i++){
            {
                Object o = map.get(i);
                if (o!=null){
                    System.out.println("No handling object at "+i +" in "+ran.getStart()+ " end "+ran.getEnd());
                    handle(o);
                }
            }
        }

    }
    public abstract boolean handle(Object o);

}

