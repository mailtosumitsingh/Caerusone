package org.ptg.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.Closure;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.taskdefs.Expand;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.http2.HTTPHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.CommonUtils;
import org.ptg.util.SpringHelper;
import org.ptg.util.db.DBHelper;

public class DefaultPluginManager implements IPluginManager {
	String pluginDir = "plugins/";
	private Map<String, IPlugin> plugins = new LinkedHashMap<String, IPlugin>();
	private final String base;

	public DefaultPluginManager() {
		this.base = (String) SpringHelper.get("basedir");
	}

	@Override
	public IPlugin loadXMLPlugin(String string) throws Exception {
		System.out.println("Loading xml plugin: " + string);
		JAXBContext context = JAXBContext.newInstance(DefaultPlugin.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();
		IPlugin d = (IPlugin) unMarshaller.unmarshal(new FileInputStream(new File(base + string)));
		System.out.println("loaded:----------------");
		System.out.println(d);
		System.out.println("-------------done unmarshalling");
		return d;
	}

	private void postInstall(final IPlugin d){
		String handlerClass = d.getActivityHandler(); 
		if(handlerClass!=null && handlerClass.length()>0)
		{
			System.out.println("Now running activity handler : " + handlerClass +".");
			Class c = null;
			IPluginActivity t = null;
			try {
				c = d.forName(handlerClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (c != null) {
				try {
					t = (IPluginActivity) c.newInstance();
					t.postInstall(d);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void initHandlers(final IPlugin d) {
		d.setClassLoader(getPluginClassloader(d));
		for (IPageHandlerDef def : d.getHandlers()) {
			String handlerClass = def.getHandlerClass();
			System.out.println("Now installing handler : " + handlerClass + " at " + def.getPath());
			Class c = null;
			AbstractHandler t = null;
			try {
				c = d.forName(handlerClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (c != null) {
				try {
					t = (AbstractHandler) c.newInstance();
					HTTPHandler.addHandler(def.getPath(), t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void reload() {
		plugins.clear();
		loadPlugins(pluginDir);
	}

	@Override
	public void loadPlugins(final String directory) {
		pluginDir = directory;
		final List<String> enabledPlugins = DBHelper.getInstance().getStringList("select name from plugins_tbl where state='ACTIVE'");
		CommonUtils.forEachFileInDirFile(directory, new Closure() {
			@Override
			public void execute(Object arg0) {
				File name = (File) arg0;
				System.out.println("DPM handling resource: " + name);
				try {
					IPlugin p = loadXMLPlugin(directory + name.getName() + "/cplugin.xml");
					if (enabledPlugins.contains(p.getId())) {
						initHandlers(p);
						plugins.put(p.getId(), p);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, true, false);
	}

	@Override
	public Collection<IPlugin> getPlugins() {
		return plugins.values();
	}

	@Override
	public void deployPlugins(final String directory) {
		pluginDir = directory;
		CommonUtils.forEachFileInDirFile(directory, new Closure() {
			@Override
			public void execute(Object arg0) {
				File name = (File) arg0;
				System.out.println("DPM handling resource: " + name);
				try {
					IPlugin p = loadXMLPlugin(directory + name.getName() + "/cplugin.xml");
					deployPlugin(p);
					plugins.put(p.getId(), p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, true, false);
	}

	private PluginClassLoader getPluginClassloader(final IPlugin p) {
		final PluginClassLoader loader = new PluginClassLoader();
		CommonUtils.forEachFileInDirFile("plugins/" + p.getId() + "/lib/", new Closure() {
			public void execute(Object arg0) {
				File name = (File) arg0;
				try {
					loader.add(name.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, false, false);
		return loader;
	}

	@Override
	public void deployPlugin(final IPlugin p) {
		System.out.println("Now deployin : " + p.getId() + " plugin.");
		installPluginInDB(p);

		CommonUtils.forEachFileInDirFile("plugins/" + p.getId() + "/lib/", new Closure() {

			public void execute(Object arg0) {
				File name = (File) arg0;
				try {
					System.out.println("Now copying lib : " + name + " library.");
					File pluginsFile = new File("pluginlibs\\");
					FileUtils.copyFileToDirectory(name, pluginsFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, false, false);
		// now setup the class loader

		CommonUtils.forEachFileInDirFile("plugins/" + p.getId() + "/includes/", new Closure() {

			public void execute(Object arg0) {
				File name = (File) arg0;
				try {
					System.out.println("Now copying include : " + name + " includes.");
					FileUtils.copyFileToDirectory(name, new File("site\\plugins\\" + p.getId() + "\\includes\\"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}, false, false);
		installDroppables(p);
		installDesigns(p);
		installTemplates(p);
		initHandlers(p);
		postInstall(p);
	}

	private void installPluginInDB(IPlugin p) {
		System.out.println("Now installing in db : " + p.getId() + " plugin.");
		String deleteOldSql = " delete from plugins_tbl where name = '" + p.getId() + "'";
		String sql = " insert into plugins_tbl(name, state) values('" + p.getId() + "','ACTIVE')";
		DBHelper.getInstance().executeUpdate(deleteOldSql);
		DBHelper.getInstance().executeUpdate(sql);
	}

	private void unInstallPluginInDB(IPlugin p) {
		String sql = " update plugins_tbl set state ='INACTIVE' where name='" + p.getId() + "'";
		DBHelper.getInstance().executeUpdate(sql);
	}

	private void deletePluginInDB(IPlugin p) {
		String sql = " update plugins_tbl set state ='DELETED' where name='" + p.getId() + "'";
		DBHelper.getInstance().executeUpdate(sql);
	}

	private void installTemplates(IPlugin p) {
		for (DefaultTemplate d : p.getTemplates()) {
			try {
				System.out.println("Now installing template : " + d.getId());
				String codeFile = "plugins/" + p.getId() + "/templates/" + d.getCode();
				FileUtils.copyFileToDirectory(new File(codeFile), new File("extra"), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void installDroppables(IPlugin p) {
		for (DefaultDroppable d : p.getDropables()) {
			try {
				System.out.println("Now installing droppable : " + d.getId());
				String codeFile = "plugins/" + p.getId() + "/drops/" + d.getCode();
				String stringCode = FileUtils.readFileToString(new File(codeFile));
				CommonUtil.saveStaticComponent("server-" + p.getId(), d.getId(), stringCode, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	//sumit fill in here
	private void installDesigns(IPlugin p) {
		for (DefaultDesign d : p.getDesigns()) {
			try {
				System.out.println("Now installing design : " + d.getId());
				String codeFile = "plugins/" + p.getId() + "/drops/" + d.getCode();
				String stringCode = FileUtils.readFileToString(new File(codeFile));
				stringCode = StringEscapeUtils.escapeJavaScript(stringCode);
				CommonUtil.saveDesign("server-" + p.getId(), d.getId(), stringCode, "[]");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void deletePlugin(final String s) throws Exception {
		IPlugin p = loadXMLPlugin(pluginDir + s + "/cplugin.xml");
		deletePlugin(p);
	}

	@Override
	public void deletePlugin(final IPlugin p) {
		unDeployPlugin(p);
		deletePluginInDB(p);
		try {
			deletePluginDir(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deletePluginDir(IPlugin p) throws IOException {
		File src = new File("plugins/" + p.getId());
		File destDir = new File("deletedplugins/");
		String d = "deletedplugins/" + p.getId();
		File ff = new File(d);
		if (ff.exists()) {
			try {
				FileUtils.forceDelete(ff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		FileUtils.moveDirectoryToDirectory(src, destDir, false);

	}

	private void unInstallTemplates(IPlugin p) {
		for (DefaultTemplate d : p.getTemplates()) {
			try {
				FileUtils.forceDelete(new File("extra/" + d.getCode()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void unDeployPlugin(final IPlugin p) {
		unInstallPluginInDB(p);
		CommonUtils.forEachFileInDirFile("plugins/" + p.getId() + "/lib/", new Closure() {

			public void execute(Object arg0) {
				File name = (File) arg0;
				try {
					FileUtils.forceDelete(new File("pluginlibs\\" + name.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}, false, false);
		CommonUtils.forEachFileInDirFile("plugins/" + p.getId() + "/includes/", new Closure() {

			public void execute(Object arg0) {
				File name = (File) arg0;
				try {
					FileUtils.forceDelete(new File("site\\plugins\\" + p.getId() + "\\includes\\" + name.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}, false, false);
		unInstallDroppables(p);
		unInstallDesigns(p);
		undeployHandlers(p);
		unInstallTemplates(p);
	}

	private void undeployHandlers(IPlugin p) {
		for (IPageHandlerDef def : p.getHandlers()) {
			try {
				HTTPHandler.removeHandler(def.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void unInstallDroppables(IPlugin p) {
		for (DefaultDroppable d : p.getDropables()) {
			CommonUtil.removeStaticComponent("server", d.getId());
		}
	}
	//sumit fill in here
	private void unInstallDesigns(IPlugin p) {
		for (DefaultDesign d : p.getDesigns()) {
			 System.out.println("moving design "+d.getId()+"to deleted...");
			 String backsql = "insert into deletedpageconfig (select * from pageconfig where name='"+d.getId()+"')";
			 DBHelper.getInstance().executeUpdate(backsql);
		}
	}

	public void installPlugin(String name) throws Exception {
		String id = StringUtils.substringBeforeLast(name, ".");
		checkAndRemoveExisting(id);
		System.out.println("Now installing : " + name + " plugin.");

		String which = "uploaded/extraplugins/" + name;
		Expand u = new Expand();
		u.setSrc(new File(which));
		u.setDest(new File("plugins/" + id + "/"));
		u.execute();
		IPlugin t = loadXMLPlugin(pluginDir + id + "/cplugin.xml");
		deployPlugin(t);
		plugins.put(t.getId(), t);
	}

	private void checkAndRemoveExisting(String name) {
		boolean exists = DBHelper.getInstance().exists("select * from plugins_tbl where name='" + name + "'");
		if (exists) {
			try {
				deletePlugin(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
