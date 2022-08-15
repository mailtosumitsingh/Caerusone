/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs.multisql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ptg.util.jobs.IStateAwareClosure;


public abstract class LoadJobClosure implements IStateAwareClosure {
    public void execute(ResultSet o) {
        String fname,task;
        int batchsize;
        try {
            fname = o.getString(1);
            task = o.getString(2);
            batchsize = o.getInt(3);
            handleJob(fname,task,batchsize);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void execute(Object object) {
        if ( object instanceof ResultSet){
            execute((ResultSet)object);
        }
    }
    ///implement any of execute of handlejob
    public abstract void handleJob(String in,String task, int size);


    public void init() {

    }

    public void finish() {
        
    }
}
