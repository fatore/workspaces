package br.usp.pf.preprocess;

import java.io.PrintWriter;
import java.util.Map;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class ConformationsShortestPath implements Runnable {
	
	private UnweightedShortestPath<Integer, Integer> sp;
	private UndirectedSparseGraph<Integer, Integer> graph;
	private PrintWriter out;
	private int v1;
	private int v2;
	
	public ConformationsShortestPath(UnweightedShortestPath<Integer, Integer> sp,
			UndirectedSparseGraph<Integer, Integer> graph, int v1, int v2, PrintWriter out) {
		this.sp = sp;
		this.graph = graph;
		this.v1 = v1;
		this.v2 = v2;
		this.out = out;
	}

	public void run() {
		try {
			String output = v1 + " " + v2 + getShortestPath(v1, v2);
			out.println(output);
		} catch (Exception e) {
			System.err.println("error");
		}
	}

	private String getShortestPath(int v1, int v2) throws Exception {
    	Number dist = sp.getDistance(v1, v2);
    	Map<Integer, Integer> incomEdges = sp.getIncomingEdgeMap(v1);
    	Pair<Integer> ends = graph.getEndpoints(incomEdges.get(v2));
    	if (dist.intValue() == 2) {
    		if (ends.getFirst() != v2) {
    			return " " + ends.getFirst();
    		}
    		if (ends.getSecond() != v2) {
    			return " " + ends.getSecond();
    		}
    	}
    	else {
    		if (ends.getFirst() != v2) {
    			return " " + ends.getFirst() + getShortestPath(v1, ends.getFirst());
    		}
    		if (ends.getSecond() != v2) {
    			return " " + ends.getSecond() + getShortestPath(v1, ends.getSecond());
    		}
    	}
    	throw new Exception();
    }
	
}
