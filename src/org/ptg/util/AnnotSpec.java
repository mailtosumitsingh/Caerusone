package org.ptg.util;

public class AnnotSpec {
String name;
String displayName;
String icon;

public AnnotSpec(String name, String displayName, String icon) {
	this.name = name;
	this.displayName = displayName;
	this.icon = icon;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDisplayName() {
	return displayName;
}
public void setDisplayName(String displayName) {
	this.displayName = displayName;
}
public String getIcon() {
	return icon;
}
public void setIcon(String icon) {
	this.icon = icon;
}

}
