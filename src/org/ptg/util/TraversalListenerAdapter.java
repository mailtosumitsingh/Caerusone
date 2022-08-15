package org.ptg.util;

import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;

public class TraversalListenerAdapter<V,E> implements TraversalListener<V, E>{

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edgeTraversed(EdgeTraversalEvent< E> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<V> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<V> arg0) {
		// TODO Auto-generated method stub
		
	}

}
