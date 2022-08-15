/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs.multisql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.ptg.util.db.DBHelper;
import org.ptg.util.jobs.ICJobFast;
import org.ptg.util.jobs.IStateAwareClosure;

public class LoadSingleSqlJob extends ICJobFast {
        static AtomicInteger a = new AtomicInteger();
        String mysql ;
        IStateAwareClosure c;
    public LoadSingleSqlJob(String sql, IStateAwareClosure closure) {
        int val = a.incrementAndGet();
        mysql = sql;
        System.out.println("Created one instance of load locality job: "+val);
        System.out.println("My Sql is: "+mysql);
        c = closure;

    }

    public boolean handle(Object o) {
        System.out.println(a.get()+" handling : "+o.toString());
        PreparedStatement stmt = DBHelper.getInstance().createPreparedStatement(mysql);
        try {
            stmt.setString(1, o.toString());
            ResultSet res = stmt.executeQuery();
            c.init();
            while(res.next()) {
            System.out.println("stat is : "+res.getString(1));
            c.execute(res);
            }
            c.finish();
            } catch (SQLException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally{
            try {
                if(stmt!=null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();  
            }
        }
        return false;
    }
}
