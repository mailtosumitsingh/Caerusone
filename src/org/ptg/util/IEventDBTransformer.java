/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import org.ptg.util.db.DBHelper;



public interface IEventDBTransformer {
	public org.ptg.events.Event loadFromDb(java.sql.ResultSet rs);
	public void saveToDb(org.ptg.events.Event o);
	public void deleteFromDb(org.ptg.events.Event  o);
	public void deleteFromDbByXref(org.ptg.events.Event  o);
	public void setStore(String store);
	public String getStore();
	public void setDataSource(String source);
	public DBHelper getDataSource();

	public void update(org.ptg.events.Event o);
	public void updateByXref(org.ptg.events.Event o);

	public void updateNative(String whereClause,org.ptg.events.Event o);
	public org.ptg.events.Event loadFromDbNative(java.sql.ResultSet rs);
	public int saveToDbNative(org.ptg.events.Event o);

}
