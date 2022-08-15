package org.ptg.util.mapper;


public class FunctionPortObj {
	protected PortObj po;
	protected int index;
	protected String portName;
	protected String grpName;
	protected PortObj myPort;
	
	public FunctionPortObj() {
	}
	public FunctionPortObj(PortObj po, int index, String portName) {
		this.po = po;
		this.index = index;
		this.portName = portName;
	}
	public FunctionPortObj(PortObj po, int index) {
		this.po = po;
		this.index = index;
	}
	public PortObj getPo() {
		return po;
	}
	public void setPo(PortObj po) {
		this.po = po;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getGrpName() {
		return grpName;
	}
	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}
	public PortObj getMyPort() {
		return myPort;
	}
	public void setMyPort(PortObj myPort) {
		this.myPort = myPort;
	}
}
