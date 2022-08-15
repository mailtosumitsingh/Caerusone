/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs;

import java.util.Map;

public abstract class ICJob implements IJob{
    IRange ran;
    Map vals;
    static final boolean pmatch = true;
    static final boolean pmmatch = false;
    public void setRange(IRange ran){
        System.out.println("My Range is "+ran.toString());
        this.ran  = ran;
    }
    public void setVals(Map vals){
        this.vals = vals;
    }
    public void run() {
        for (Object e : vals.entrySet()){
            if(e!=null){
                if (ran.isInRange((Map.Entry) e)){
                    if (pmatch)System.out.println(((Map.Entry) e).getValue() +" is in range "+ran.toString());
                    handle((Map.Entry)e);
                }else{
                    if (pmmatch)System.out.println(((Map.Entry) e).getValue() +" is not in range "+ran.toString());
                }
            }
        }

    }
    public abstract boolean handle(Map.Entry e);

}
