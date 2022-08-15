package org.ptg.util.npath;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PTGSearch {

    private static final String START = "B";
    private static final String END = "E";

    public static void main(String[] args) {
        // this graph is directional
        PTGGraph graph = new PTGGraph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "A");
        graph.addEdge("B", "D");
        graph.addEdge("B", "E"); // this is the only one-way connection
        graph.addEdge("B", "F");
        graph.addEdge("C", "A");
        graph.addEdge("C", "E");
        graph.addEdge("C", "F");
        graph.addEdge("D", "B");
        graph.addEdge("E", "C");
        graph.addEdge("E", "F");
        graph.addEdge("F", "B");
        graph.addEdge("F", "C");
        graph.addEdge("F", "E");
        LinkedList<String> visited = new LinkedList();
        visited.add(START);
        List<List<String>> npaths = new LinkedList<List<String>>();
        PTGSearch ptg = new PTGSearch();
        ptg.deapthFirst(graph, visited,npaths);
        System.out.println("done");
    }

    private void deapthFirst(PTGGraph graph, LinkedList<String> visited,List<List<String>> npaths) {
        LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        // examine adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(END)) {
                visited.add(node);
                printPath(visited,npaths);
                visited.removeLast();
                break;
            }
        }
        // in breadth-first, recursion needs to come after visiting adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(END)) {
                continue;
            }
            visited.addLast(node);
            deapthFirst(graph, visited,npaths);
            visited.removeLast();
        }
    }

    private void printPath(List<String> visited,List<List<String>> npaths) {
    	List<String> t = new ArrayList<String>(visited.size());
    	t.addAll(visited);
    	npaths.add(t);
        for (String node : visited) {
            System.out.print(node);
            System.out.print(" ");
        }
        System.out.println();
    }
}