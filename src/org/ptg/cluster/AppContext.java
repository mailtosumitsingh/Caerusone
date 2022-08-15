/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.cluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.ptg.events.EventDefinitionManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.DynaObjectHelper;
import org.ptg.util.db.DBHelper;

public class AppContext {
	private String systemId;
	private String groupId;
	private String[] readGroups;
	private String[] writeGroups;
	private String brokerHost;
	private int brokerPort;
	private String memcacheIP;
	private int memcachePort;
	private String dbfile;
	private String cacheFile;
	private int maxSize;
	private int blockSize;
	private int ceilingLimit;
	private int maxItems;
	private String itemTable;
	private boolean startEmpty;
	private boolean preload;
	private boolean replicated;
	private boolean clearDBOnFlush;
	private boolean propogateQuery;
	private boolean fedarated;
	private String buildDir;
	private boolean createRabbitQueues;
	private boolean addJsonEventForEvent;
	private boolean addProcessHandlerForEvent;
	private String gmailAccount;
	private String gmailPassword;
	private String fsBaseDir;
	private String imageOutDir;

	private boolean startRabitClient;
	private String propFile;
	private boolean startFTPServer;
	private boolean startSSHD;
	private Properties prop = new Properties();
	private boolean startSpreadDaemon;
   private boolean dynaDeploy;
   private int serverHttpPort;
   public boolean connToSpread;
   public boolean startAxis;
   public boolean startActiviti;
   public boolean startUIHooks;
   public boolean startShortCut;
   public boolean sendShortCutEvents;
   
   private String webappdir;
   private String webapptempdir;
   private String axistempdir;
   private String axiswar;
   private String activitiwar;
   private String activitiwartemp;
   private String httpServerIp;
   private boolean startPlugins;
   private boolean sendDeployEvent;
   public boolean isSendShortCutEvents() {
	return sendShortCutEvents;
}

public void setSendShortCutEvents(boolean sendShortCutEvents) {
	this.sendShortCutEvents = sendShortCutEvents;
}

public boolean isStartUIHooks() {
	return startUIHooks;
}

public void setStartUIHooks(boolean startUIHooks) {
	this.startUIHooks = startUIHooks;
}

public boolean isStartShortCut() {
	return startShortCut;
}

public void setStartShortCut(boolean startShortCut) {
	this.startShortCut = startShortCut;
}

public boolean isStartActiviti() {
	return startActiviti;
}

public void setStartActiviti(boolean startActiviti) {
	this.startActiviti = startActiviti;
}


protected String sqlServer;
   
	public String getPropFile() {
		return propFile;
	}

	public void setPropFile(String propFile) {
		this.propFile = propFile;
		try {
			InputStream stream = this.getClass().getResourceAsStream(propFile);
			prop.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load properties from : " + propFile);
			System.exit(1);
		}
	}

	public String getImageOutDir() {
		return imageOutDir;
	}

	public void setImageOutDir(String imageOutDir) {
		this.imageOutDir = imageOutDir;
	}

	public String getFsBaseDir() {
		return fsBaseDir;
	}

	public void setFsBaseDir(String fsBaseDir) {
		this.fsBaseDir = fsBaseDir;
	}

	public String getGmailAccount() {
		return gmailAccount;
	}

	public void setGmailAccount(String gmailAccount) {
		this.gmailAccount = gmailAccount;
	}

	public String getGmailPassword() {
		return gmailPassword;
	}

	public void setGmailPassword(String gmailPassword) {
		this.gmailPassword = gmailPassword;
	}

	public boolean isAddJsonEventForEvent() {
		return addJsonEventForEvent;
	}

	public void setAddJsonEventForEvent(boolean addJsonEventForEvent) {
		this.addJsonEventForEvent = addJsonEventForEvent;
	}


	private Map<String, String> dynaEvents = new HashMap<String, String>();
	private Map<String, String> dynaEventTransformers = new HashMap<String, String>();
	private Map<String, String> eventStores = new HashMap<String, String>();

	public Map<String, String> getEventStores() {
		return eventStores;
	}

	public void setEventStores(Map<String, String> eventStores) {
		this.eventStores = eventStores;
	}

	public Map<String, String> getDynaEvents() {
		return dynaEvents;
	}

	public void setDynaEvents(Map<String, String> dynaEvents) {
		this.dynaEvents = dynaEvents;
	}

	public Map<String, String> getDynaEventTransformers() {
		return dynaEventTransformers;
	}

	public void setDynaEventTransformers(
			Map<String, String> dynaEventTransformers) {
		this.dynaEventTransformers = dynaEventTransformers;
	}

	private boolean createIndexForClustered;

	public boolean isCreateIndexForClustered() {
		return createIndexForClustered;
	}

	public void setCreateIndexForClustered(boolean createIndexForClustered) {
		this.createIndexForClustered = createIndexForClustered;
	}

	

	private java.util.Map<String, String> gProtocolMap = new java.util.HashMap<String, String>();

	public java.util.Map<String, String> getgProtocolMap() {
		return gProtocolMap;
	}

	public void setgProtocolMap(java.util.Map<String, String> gProtocolMap) {
		this.gProtocolMap = gProtocolMap;
	}

	public String getBuildDir() {
		return buildDir;
	}

	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}

	public boolean isFedarated() {
		return fedarated;
	}

	public void setFedarated(boolean fedarated) {
		this.fedarated = fedarated;
	}

	public boolean isStartEmpty() {
		return startEmpty;
	}

	public void setStartEmpty(boolean startEmpty) {
		this.startEmpty = startEmpty;
	}

	public boolean isPreload() {
		return preload;
	}

	public void setPreload(boolean preload) {
		this.preload = preload;
	}

	public boolean isReplicated() {
		return replicated;
	}

	public void setReplicated(boolean replicated) {
		this.replicated = replicated;
	}

	public boolean isClearDBOnFlush() {
		return clearDBOnFlush;
	}

	public void setClearDBOnFlush(boolean clearDBOnFlush) {
		this.clearDBOnFlush = clearDBOnFlush;
	}

	public boolean isPropogateQuery() {
		return propogateQuery;
	}

	public void setPropogateQuery(boolean propogateQuery) {
		this.propogateQuery = propogateQuery;
	}

	public String getItemTable() {
		return itemTable;
	}

	public void setItemTable(String itemTable) {
		this.itemTable = itemTable;
	}

	public String getDbfile() {
		return dbfile;
	}

	public void setDbfile(String dbfile) {
		this.dbfile = dbfile;
	}

	public String getCacheFile() {
		return cacheFile;
	}

	public void setCacheFile(String cacheFile) {
		this.cacheFile = cacheFile;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public int getCeilingLimit() {
		return ceilingLimit;
	}

	public void setCeilingLimit(int ceilingLimit) {
		this.ceilingLimit = ceilingLimit;
	}

	public int getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(int maxItems) {
		this.maxItems = maxItems;
	}

	public String getMemcacheIP() {
		return memcacheIP;
	}

	public void setMemcacheIP(String memcacheIP) {
		this.memcacheIP = memcacheIP;
	}

	public int getMemcachePort() {
		return memcachePort;
	}

	public void setMemcachePort(int memcachePort) {
		this.memcachePort = memcachePort;
	}

	public String getBrokerHost() {
		return brokerHost;
	}

	public void setBrokerHost(String brokerHost) {
		this.brokerHost = brokerHost;
	}

	public int getBrokerPort() {
		return brokerPort;
	}

	public void setBrokerPort(int brokerPort) {
		this.brokerPort = brokerPort;
	}

	public String[] getReadGroups() {
		return readGroups;
	}

	public void setReadGroups(String[] readGroups) {
		this.readGroups = readGroups;
	}

	public String[] getWriteGroups() {
		return writeGroups;
	}

	public void setWriteGroups(String[] writeGroups) {
		this.writeGroups = writeGroups;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void init() {
		for (Map.Entry<String, String> en : dynaEvents.entrySet()) {
			Class c = DynaObjectHelper.externalizeClass(en.getValue(),
					en.getKey(), true);
			if (c != null)
				System.out.println("Success fully registered: " + en.getValue()
						+ " as " + en.getKey());
			else
				System.out.println("Could not register: " + en.getValue()
						+ " as " + en.getKey());
		}
		for (Map.Entry<String, String> en : dynaEventTransformers.entrySet()) {
			Class c = DynaObjectHelper.externalizeClass(en.getValue(),
					en.getKey(), true);
			if (c != null) {
				System.out.println("Success fully externalized: "
						+ en.getValue() + " as " + en.getKey());
				CommonUtil.saveAndRegisterEventTransformer(en.getValue(), en
						.getKey(),
						eventStores.get(en.getKey() == null ? "dummy_events"
								: en.getKey()));
				EventDefinitionManager.getInstance()
						.buildDBTransformerDefinition(c.getClass().getName());
			} else {
				System.out.println("Could not register: " + en.getValue()
						+ " as " + en.getKey());
			}

		}
		if (startEmpty) {
			java.sql.Connection conn = null;
			java.sql.Statement stmt = null;
			try {
				conn = DBHelper.getInstance().createConnection();
				conn.setAutoCommit(true);
				for (String s : eventStores.values()) {
					stmt = conn.createStatement();
					stmt.execute("truncate " + s + ";");
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (conn != null) {
					DBHelper.getInstance().rollback(conn);
					DBHelper.getInstance().closeStmt(stmt);
					DBHelper.getInstance().closeConnection(conn);
				}
			}
		}
	}


	public boolean isCreateRabbitQueues() {
		return createRabbitQueues;
	}

	public void setCreateRabbitQueues(boolean createRabbitQueues) {
		this.createRabbitQueues = createRabbitQueues;
	}

	public boolean isStartRabitClient() {
		return startRabitClient;
	}

	public void setStartRabitClient(boolean startRabitClient) {
		this.startRabitClient = startRabitClient;
	}
	/*
	 * get property will return loaded property 
	 * if the prop name starts with @ it si a system property and then we will just return
	 * the system property
	 * if the name starts with ~ then it is a pseudo or proxy name then we get the real name and 
	 * return it.
	 * */
	public String getProperty(String name) {
		if (name.startsWith("@")) {
			String prop = CommonUtil.translateProperty(name);
			return prop;
		} else if (name.startsWith("~")) {
			String val = CommonUtil.translatePropertyName(name);
			return prop.getProperty(val);
		}else {
			return prop.getProperty(name);
		}
	}

	public boolean isDynaDeploy() {
		return dynaDeploy;
	}

	public void setDynaDeploy(boolean dynaDeploy) {
		this.dynaDeploy = dynaDeploy;
	}

	public boolean isStartFTPServer() {
		return startFTPServer;
	}

	public void setStartFTPServer(boolean startFTPServer) {
		this.startFTPServer = startFTPServer;
	}

	public boolean isStartSSHD() {
		return startSSHD;
	}

	public void setStartSSHD(boolean startSSHD) {
		this.startSSHD = startSSHD;
	}

	public boolean isStartSpreadDaemon() {
		return startSpreadDaemon;
	}

	public void setStartSpreadDaemon(boolean startSpreadDaemon) {
		this.startSpreadDaemon = startSpreadDaemon;
	}

	public int getServerHttpPort() {
		return serverHttpPort;
	}

	public void setServerHttpPort(int serverHttpPort) {
		this.serverHttpPort = serverHttpPort;
	}



	public boolean isConnToSpread() {
		return connToSpread;
	}

	public void setConnToSpread(boolean connToSpread) {
		this.connToSpread = connToSpread;
	}

	public boolean isStartAxis() {
		return startAxis;
	}

	public void setStartAxis(boolean startAxis) {
		this.startAxis = startAxis;
	}

	public String getSqlServer() {
		return sqlServer;
	}

	public void setSqlServer(String sqlServer) {
		this.sqlServer = sqlServer;
	}

	public String getWebappdir() {
		return webappdir;
	}

	public void setWebappdir(String webappdir) {
		this.webappdir = webappdir;
	}

	public String getWebapptempdir() {
		return webapptempdir;
	}

	public void setWebapptempdir(String webapptempdir) {
		this.webapptempdir = webapptempdir;
	}

	public String getAxistempdir() {
		return axistempdir;
	}

	public void setAxistempdir(String axistempdir) {
		this.axistempdir = axistempdir;
	}

	public String getAxiswar() {
		return axiswar;
	}

	public void setAxiswar(String axiswar) {
		this.axiswar = axiswar;
	}

	public String getActivitiwar() {
		return activitiwar;
	}

	public void setActivitiwar(String activitiwar) {
		this.activitiwar = activitiwar;
	}

	public String getActivitiwartemp() {
		return activitiwartemp;
	}

	public void setActivitiwartemp(String activitiwartemp) {
		this.activitiwartemp = activitiwartemp;
	}

	public String getHttpServerIp() {
		return httpServerIp;
	}

	public void setHttpServerIp(String httpServerIp) {
		this.httpServerIp = httpServerIp;
	}

	public boolean isStartPlugins() {
		return startPlugins;
	}

	public void setStartPlugins(boolean startPlugins) {
		this.startPlugins = startPlugins;
	}

	public boolean isSendDeployEvent() {
		return sendDeployEvent;
	}

	public void setSendDeployEvent(boolean sendDeployEvent) {
		this.sendDeployEvent = sendDeployEvent;
	}

}
