package org.ptg.plugins;

public class PluginLoadTest {
	public static void main(String[] args) throws Exception {
		IPluginManager d = new DefaultPluginManager();
		IPlugin p = d.loadXMLPlugin("plugins/opencv/cplugin.xml");
		for (DefaultDroppable dd : p.getDropables())
			System.out.println(dd);
	}
}
