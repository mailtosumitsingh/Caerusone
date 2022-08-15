

package org.ptg.components;

import org.ptg.admin.WebStartProcess;
import org.ptg.util.Constants;
import org.ptg.util.db.DBHelper;



public class ComponentsManager {
	private  static class SingletonHolder {
		private static final ComponentsManager INSTANCE = new ComponentsManager();
	}
	public  static ComponentsManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	public void init(){
		String sql = "select * from "+ Constants.ComponentsStore;
		ComponentsLoader l = new ComponentsLoader(WebStartProcess.getInstance().getRoutingEngine());
		DBHelper.getInstance().forEach(sql, l);
		
	}
}
