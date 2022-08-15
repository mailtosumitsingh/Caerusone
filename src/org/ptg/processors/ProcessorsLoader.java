/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ptg.util.GenericException;
import org.ptg.util.closures.IStateAwareClosure;




public class ProcessorsLoader implements IStateAwareClosure{
	private ProcessorManager mgr;
	public ProcessorsLoader(ProcessorManager mgr){
		this.mgr = mgr;
	}
	public void execute(ResultSet rs) throws SQLException {
		String name = rs.getString("name");
		String clz = rs.getString("clz");
		String query = rs.getString("query");
		String configItems = rs.getString("configItems");
		try {
			mgr.registerProcessorType(name,clz,query,configItems);
		} catch (GenericException e) {
			e.printStackTrace();
		}
		}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
}
