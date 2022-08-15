package org.ptg.util.functions;

import java.util.ArrayList;
import java.util.List;

public class Expression {
String dtype;
String id;
String val;
List<Expression> child  = new ArrayList<Expression>(); 
public List<Expression> getChild() {
	return child;
}
public void setChild(List<Expression> child) {
	this.child = child;
}
public enum ExpressionType{
	READ,WRITE
} ;
ExpressionType exprType;
public String getDtype() {
	return dtype;
}
public void setDtype(String dtype) {
	this.dtype = dtype;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getVal() {
	return val;
}
public void setVal(String val) {
	this.val = val;
}
public ExpressionType getExprType() {
	return exprType;
}
public void setExprType(ExpressionType exprType) {
	this.exprType = exprType;
}

}
