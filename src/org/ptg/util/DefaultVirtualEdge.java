package org.ptg.util;

import org.jgrapht.graph.DefaultEdge;

public class DefaultVirtualEdge<VType> extends DefaultEdge{
	boolean isVirtual;
	VType virtualFor;
	public boolean isVirtual() {
		return isVirtual;
	}
	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}
	public VType getVirtualFor() {
		return virtualFor;
	}
	public void setVirtualFor(VType virtualFor) {
		this.virtualFor = virtualFor;
	}
	

}
