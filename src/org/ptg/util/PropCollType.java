package org.ptg.util;

public enum PropCollType {
Single("Zero","$name "),Array("One Dimentional Array ","$name []"),Array2D ("Two Dimentional Array ","$name [][]"),
Array3D("Three Dimentional Array ","$name [][][]"),
Array4D("Four Dimentional Array ","$name [][][][]"),Array5D("Five Dimentional Array ","$name [][][][][]"),
Array6d("Six Dimentional Array ","$name [][][][][][]"),Coll("Collection","java.util.Collection $name "),List("Index","java.util.List $name "),
Map("Key","java.util.Map "),Tree("Key","java.util.Map "),Graph("Graph","java.util.Map ");
private String dimString;
private String def;

public String getDef() {
	return def;
}

public void setDef(String def) {
	this.def = def;
}

private PropCollType(String dim,String def){
	this.dimString = dim;
	this.def = def;
}

public String getDimString() {
	return dimString;
}

public void setDimString(String dimString) {
	this.dimString = dimString;
}

}
