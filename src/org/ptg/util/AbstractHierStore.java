package org.ptg.util;

import java.util.ArrayList;
import java.util.List;

public class AbstractHierStore< Type > implements IHierStore<Type> {
	protected List<Type> childs  = new ArrayList<Type>();
	protected String group="Default";
	protected String name;
	protected PropTypeEnum type;
	protected PropCollType collType;
	protected String propClass;
	protected String pathExpr;
	private Type parent;
	protected List<Type> parents  = new ArrayList<Type>();
	
	private Object val;
	
	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	@Override
	public void setChilds(List<Type> childs) {
		this.childs = childs;
	}

	@Override
	public List<Type> getChilds() {
		return childs;
	}

	@Override
	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public String getName() {
		
		return name;
	}
	@Override
	public PropTypeEnum getType() {
		return type;
	}
	@Override
	public void setType(PropTypeEnum type) {
		this.type = type;
	}
	@Override
	public PropCollType getCollType() {
		return collType;
	}
	@Override
	public void setCollType(PropCollType collType) {
		this.collType = collType;
	}
	@Override
	public String getPropClass() {
		return propClass;
	}
	@Override
	public void setPropClass(String propClass) {
		this.propClass = propClass;
	}

	@Override
	public String getPathExpr() {
		return pathExpr;
	}

	public void setPathExpr(String pathExpr) {
		this.pathExpr = pathExpr;
	}

	@Override
	public Type getParent() {
		return this.parent;
	}

	@Override
	public void setParent(Type t) {
		parents.remove(t);
		parents.add(t);
		this.parent = t;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collType == null) ? 0 : collType.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pathExpr == null) ? 0 : pathExpr.hashCode());
		result = prime * result + ((propClass == null) ? 0 : propClass.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractHierStore other = (AbstractHierStore) obj;
		if (collType == null) {
			if (other.collType != null)
				return false;
		} else if (!collType.equals(other.collType))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pathExpr == null) {
			if (other.pathExpr != null)
				return false;
		} else if (!pathExpr.equals(other.pathExpr))
			return false;
		if (propClass == null) {
			if (other.propClass != null)
				return false;
		} else if (!propClass.equals(other.propClass))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

public void addChild(Type t){
	
	childs.add(t);
}	
public void removeChild(Type t){
	childs.remove(t);
}	
public void addChildUniq(Type t){
	if(!childs.contains(t)){
	childs.add(t);
	}else{
		System.out.println("Child is already there");
	}
}
public void addParentUniq(Type t){
	if(!parents.contains(t)){
		parents.add(t);
		}else{
			System.out.println("Parent is already there");
		}
	
}	
public void addParent(Type t){
	parents.add(t);
}	
public void removeParent(Type t){
	parents.remove(t);
}	
public List<Type> getParents() {
	return parents;
}

public void setParents(List<Type> parents) {
	this.parents = parents;
}	

}




