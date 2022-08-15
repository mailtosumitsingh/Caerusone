/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs.multisql;



import java.io.File;

import org.ptg.util.jobs.IStateAwareClosure;


public class LoadSqlJobClosure extends LoadJobClosure{
    IStateAwareClosure c  ;

    public LoadSqlJobClosure(IStateAwareClosure c) {
        this.c = c;
    }

    public void handleJob(String in, String task, int size) {
        LoadSingleSql s = new LoadSingleSql(new File(in),size,task,c);
         s.run();

    }
}
