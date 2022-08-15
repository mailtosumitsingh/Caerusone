package org.ptg.plugins;

import javax.xml.bind.annotation.XmlAttribute;

public class DefaultHandlerDef implements IPageHandlerDef{
String path;
String handlerClass;
@XmlAttribute
public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
@XmlAttribute
@Override
public String getHandlerClass() {
	return handlerClass;
}
public void setHandlerClass(String handlerClass) {
	this.handlerClass = handlerClass;
}
@Override
public String toString() {
	return "DefaultHandlerDef [path=" + path + ", handlerClass=" + handlerClass + "]";
}

}
