package org.ptg.util;

import java.util.List;

public interface IHierStore<Type> {
    Type getParent();
    void setParent(Type t);
	void setChilds(List<Type> childs);

	List<Type> getChilds();

	void setGroup(String group);

	String getGroup();

	void setName(String name);

	String getName();

	void setPropClass(String propClass);

	String getPropClass();

	void setCollType(PropCollType collType);

	PropCollType getCollType();

	void setType(PropTypeEnum type);

	PropTypeEnum getType();
	
	String getPathExpr();
	
	void setPathExpr(String s);
}
