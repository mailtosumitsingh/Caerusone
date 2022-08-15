package org.ptg.util;

import java.util.Set;

import org.ptg.util.mapper.v2.FPGraph2;

public class GraphNodeProcessor<NodeType> {
	StringBuilder sb  = new StringBuilder();
	public void handleStart(NodeType ad, FPGraph2 g) {
		System.out.println("GraphNodeProcessor.handleStart()");
		sb = new StringBuilder();
		sb.append("{");
	}

	public void handleEnd(NodeType ad, FPGraph2 g, int ins) {
		System.out.println("GraphNodeProcessor.handleEnd()");
		System.out.println(sb);
	}

	public void handleSimple(NodeType ad, FPGraph2 g) {
		System.out.println("GraphNodeProcessor.handleSimple()");
		sb.append("System.out.println(\""+ad+"\");\n");
	}

	public void handleSimpleJumpSource(NodeType ad, FPGraph2 g) {
		System.out.println("GraphNodeProcessor.handleSimpleJumpSource()");
		sb.append("break  label_"+ad+";\n");
	}

	public void handleFork(NodeType ad, FPGraph2 g) {
		System.out.println("GraphNodeProcessor.handleFork()");
		sb.append("{\n");
	}

	public void handleMerge(NodeType ad, Set<String> inputs, FPGraph2 g,int ins) {
		System.out.println("GraphNodeProcessor.handleMerge()");
		for(int i=0;i<ins;i++){
			sb.append("}\n");
		}
	}

	public void handleSimpleJumpTarget(NodeType ad, FPGraph2 g) {
		System.out.println("GraphNodeProcessor.handleSimpleJumpTarget()");
		sb.append("label_"+ad+":");		
	}

	public void handleBranchChange(NodeType oldAd, NodeType newAd, FPGraph2 g) {
		System.out.println("GraphNodeProcessor.handleBranchChange()");
		sb.append("}");
	}
	public void handleJump(NodeType ad, FPGraph2 g) {
		System.out.println("GraphNodeProcessor.handleJump()");
	}

	public void handleForkStart(String port, FPGraph2 dummy) {
		System.out.println("GraphNodeProcessor.handleForkStart()");
		sb.append("if("+port+")");
	}

}
