package org.ptg.plugins;

public class PluginHelper {
public static IPluginMenu createPluginIn(String id, String displayName, String action, String whereToAdd){
	return new DefaultPluginMenu(id,displayName,action,whereToAdd);
} 
}
