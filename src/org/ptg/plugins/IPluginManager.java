package org.ptg.plugins;

import java.util.Collection;

public interface IPluginManager {
	public abstract void unDeployPlugin(final IPlugin p);
	public abstract void deletePlugin(final IPlugin p);
	public abstract void deployPlugin(final IPlugin p);
	public abstract void deployPlugins(final String directory);
	public abstract Collection<IPlugin> getPlugins();
	public abstract void loadPlugins(final String directory);
	public abstract void reload();
	public IPlugin loadXMLPlugin(String string) throws Exception;
	public void installPlugin(String name) throws Exception;
	void deletePlugin(String s) throws Exception;
}
