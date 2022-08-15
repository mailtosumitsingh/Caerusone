package org.ptg.plugins;

import javax.xml.bind.annotation.XmlAttribute;

public class DefaultDesign implements IDroppable {
	String id;
	String code;

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@XmlAttribute
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "<design id=\""+id+ "\" code=\""+code+"\" />";
	}

}
