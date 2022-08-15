package org.ptg.plugins;

import java.util.List;


public interface IPlugin {
String getId();

List<DefaultHandlerDef> getHandlers();

List<String> getIncludes();

List<DefaultDroppable> getDropables();

List<DefaultPluginMenu> getMenus();
List<DefaultTemplate> getTemplates();
void setClassLoader(PluginClassLoader classLoader) ;
Class forName(String name);
String getActivityHandler() ;
List<DefaultDesign> getDesigns();
}
