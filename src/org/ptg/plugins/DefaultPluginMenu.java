package org.ptg.plugins;

import javax.xml.bind.annotation.XmlAttribute;


public class DefaultPluginMenu implements IPluginMenu{

	private String displayName;
	private String action;
	private String id;
	private String whereToAdd;
	
	
	public DefaultPluginMenu() {
	}

	public DefaultPluginMenu(String id, String displayName, String action, String whereToAdd) {
		this.id = id;
		this.displayName = displayName;
		this.action = action;
		this.whereToAdd = whereToAdd;
	}

	@XmlAttribute
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@XmlAttribute
	@Override
	public String getAction() {
		return action;
	}

	@XmlAttribute
	@Override
	public String getId() {
		return id;
	}

	@XmlAttribute
	@Override
	public String getWhereToAdd() {
		return whereToAdd;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setWhereToAdd(String whereToAdd) {
		this.whereToAdd = whereToAdd;
	}

	@Override
	public String toString() {
		return "DefaultPluginMenu [displayName=" + displayName + ", action=" + action + ", id=" + id + ", whereToAdd=" + whereToAdd + "]";
	}

}
