package org.ptg.plugins;

public class DeleteTest {
	
	public static void main(String[] args) throws Exception {
		IPluginManager d = new DefaultPluginManager();
		IPlugin p = null;
		try {
			p = d.loadXMLPlugin("plugins/opencv/cplugin.xml");
			d.deletePlugin(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			p = d.loadXMLPlugin("plugins/test/cplugin.xml");
			d.deletePlugin(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			p = d.loadXMLPlugin("plugins/ShortCut/cplugin.xml");
			d.deletePlugin(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
