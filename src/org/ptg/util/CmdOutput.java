package org.ptg.util;

public class CmdOutput {
private String err;
private String out;
private int exitValue;
public String getErr() {
	return err;
}
public void setErr(String err) {
	this.err = err;
}
public String getOut() {
	return out;
}
public void setOut(String out) {
	this.out = out;
}
public int getExitValue() {
	return exitValue;
}
public void setExitValue(int exitValue) {
	this.exitValue = exitValue;
}
public CmdOutput(String err, String out, int exitValue) {
	this.err = err;
	this.out = out;
	this.exitValue = exitValue;
}
public CmdOutput() {
}

}
