package org.ptg.plugins;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ptg.util.CommonUtil;
@XmlRootElement
public class DefaultPlugin implements IPlugin {

	private String id;
	private List<DefaultPluginMenu >menus = new LinkedList<DefaultPluginMenu>();
	private List<DefaultDroppable >dropables = new LinkedList<DefaultDroppable>();
	private List<DefaultDesign>designs= new LinkedList<DefaultDesign>();
	
	private List<String >includes = new LinkedList<String>();
	private List<DefaultTemplate >templates = new LinkedList<DefaultTemplate>();
	private List<DefaultHandlerDef >handlers = new LinkedList<DefaultHandlerDef>();
	private String activityHandler ;
	PluginClassLoader classLoader;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	@XmlElement(name="menu")
	public List<DefaultPluginMenu> getMenus() {
		return menus;
	}
	public void setMenus(List<DefaultPluginMenu> menus) {
		this.menus = menus;
	}
	@Override
	@XmlElement(name="dropable")
	public List<DefaultDroppable> getDropables() {
		return dropables;
	}
	public void setDropables(List<DefaultDroppable> dropables) {
		this.dropables = dropables;
	}
	@Override
	@XmlElement(name="include")
	public List<String> getIncludes() {
		return includes;
	}
	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}
	@Override
	@XmlElement(name="handler")
	public List<DefaultHandlerDef> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<DefaultHandlerDef> handlers) {
		this.handlers = handlers;
	}
	@Override
	public String toString() {
		return "DefaultPlugin [id=" + id + ", menus=" + menus + ", dropables=" + dropables + ", includes=" + includes + ", handlers=" + handlers + "]";
	}
	@Override
	@XmlElement(name="template")
	public List<DefaultTemplate> getTemplates() {
		return templates;
	}
	public void setTemplates(List<DefaultTemplate> templates) {
		this.templates = templates;
	}
	public PluginClassLoader getClassLoader() {
		return classLoader;
	}
	public void setClassLoader(PluginClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	@Override
	public Class forName(String name) {
		Class c = null;
		try {
			c  = CommonUtil.forName(name);
		} catch (Exception e) {
			System.out.println(name +" is not installed will try from plugin jars.");
		}
		if(c==null){
				try {
					c = classLoader.loadClass(name);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return c;
	}
	@XmlElement(name="pluginActivity")
	public String getActivityHandler() {
		return activityHandler;
	}
	public void setActivityHandler(String activityHandler) {
		this.activityHandler = activityHandler;
	}
	@Override
	@XmlElement(name="design")
	public List<DefaultDesign> getDesigns() {
		return designs;
	}
	public void setDesigns(List<DefaultDesign> Designs) {
		this.designs= designs;
	}	

}
