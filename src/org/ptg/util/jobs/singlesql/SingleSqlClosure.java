/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs.singlesql;


import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.ptg.util.jobs.multisql.LoadJobClosure;

public abstract class SingleSqlClosure extends LoadJobClosure {
    static AtomicInteger a = new AtomicInteger();

    public void execute(ResultSet task) {
        int val = a.incrementAndGet();
        System.out.println("Created one instance of load locality job: " + val);
        handle(task);

    }
    public void handleJob(String in,String task, int size){}

    public abstract boolean handle(ResultSet o);

}
