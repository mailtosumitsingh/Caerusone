package org.ptg.plugins;

public class InstallTest {
	
	public static void main(String[] args) throws Exception {
		IPluginManager d = new DefaultPluginManager();
		IPlugin p = d.loadXMLPlugin("plugins/opencv/cplugin.xml");
		//d.deployPlugin(p);
		//d.unDeployPlugin(p);
		//d.deletePlugin(p);
		d.installPlugin("test.zip");
	}
}
