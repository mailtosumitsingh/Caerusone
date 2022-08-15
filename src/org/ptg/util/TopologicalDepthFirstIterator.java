package org.ptg.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DirectedMultigraph;


public class TopologicalDepthFirstIterator<String, DefaultEdge> extends TraversalListenerAdapter<String, DefaultEdge>{
	private List<String> portObjects = new LinkedList<String>();
	private DirectedMultigraph<String, DefaultEdge> graph ;
	
	public TopologicalDepthFirstIterator() {
		super();
	}
	public TopologicalDepthFirstIterator(List<String> portObjects, DirectedMultigraph<String, DefaultEdge> graph) {
		super();
		this.portObjects = portObjects;
		this.graph = graph;
	}
	public TopologicalDepthFirstIterator(DirectedMultigraph<String, DefaultEdge> graph) {
		super();
		this.graph = graph;
	}
	public void vertexTraversed(VertexTraversalEvent<String> a) {
		String v = a.getVertex();
		boolean added = false;
		Collection<DefaultEdge> vertexes = graph.incomingEdgesOf(v);
		Iterator<DefaultEdge> viter = vertexes.iterator();
		int currCount = 0;
		int maxParent = -1;
		for (int k = 0; k < vertexes.size(); k++) {
			DefaultEdge ed = viter.next();
			String from = graph.getEdgeSource(ed);
			int lastParent = portObjects.indexOf(from);
			if (maxParent < lastParent) {
				maxParent = lastParent;
			}
			if (lastParent > -1) {
				currCount++;
				if (currCount == vertexes.size()) {
					if (maxParent == portObjects.size()) {
						added = true;
						portObjects.add(v);
					} else {
						portObjects.add(maxParent + 1, v);
						added = true;
						break;
					}
				}
			} else {
				System.out.println("This should not come here topological sort failed");
			}
		}
		if (!added) {
			portObjects.add(v);
		}
	}
	public List<String> getPortObjects() {
		return portObjects;
	}
	public void setPortObjects(List<String> portObjects) {
		this.portObjects = portObjects;
	}
	public DirectedMultigraph<String, DefaultEdge> getGraph() {
		return graph;
	}
	public void setGraph(DirectedMultigraph<String, DefaultEdge> graph) {
		this.graph = graph;
	}
	
}
