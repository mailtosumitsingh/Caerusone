/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.closures;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IStateAwareClosure {
	public void init();
	public void finish();
	public void execute(ResultSet rs)throws SQLException;

}
