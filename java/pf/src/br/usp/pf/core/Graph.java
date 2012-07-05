package br.usp.pf.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import br.usp.pf.jung.mst.MyLink;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * 
 * @author Francisco Morgani Fatore
 */
public class Graph {

	private String fileName;
	private UndirectedSparseGraph<Integer, MyLink> usg;
	private Node root;
	private int numVertices;
	private int verticesMinEnergy;
	private int verticesMaxEnergy;
	private float edgesMinEnergy;
	private float edgesMaxEnergy;

	private void loadGraph() throws Exception {

		usg = new UndirectedSparseGraph<Integer, MyLink>();

		BufferedReader br = new BufferedReader(new FileReader(fileName));

		StringTokenizer token;

		// count number of vertices
		String line = br.readLine();

		if (line == null) {
			throw new Exception("Not a gph File!");
		}

		token = new StringTokenizer(line.trim());

		// skip first word *Vertices
		token.nextToken();

		numVertices = Integer.parseInt(token.nextToken());

		// store native state
		line = br.readLine();
		token = new StringTokenizer(line.trim());
		root = new Node();
		root.setKey(Integer.parseInt(token.nextToken()));
		root.setEnergy(Integer.parseInt(token.nextToken()));

		verticesMaxEnergy = root.getEnergy();
		verticesMinEnergy = root.getEnergy();

		// skip vertices
		while (!line.startsWith("*Edges")) {
			token = new StringTokenizer(line.trim());
			token.nextToken();
			int energy = Integer.parseInt(token.nextToken());
			if (energy < verticesMinEnergy)
				throw new Exception("Node energy is lower than native state!");
			if (energy > verticesMaxEnergy)
				verticesMaxEnergy = energy;
			line = br.readLine();
		}

		edgesMinEnergy = Float.POSITIVE_INFINITY;
		edgesMaxEnergy = Float.NEGATIVE_INFINITY;

		int edgeCounter = 0;
		// read edges
		while ((line = br.readLine()) != null) {
			token = new StringTokenizer(line.trim());

			int source = Integer.parseInt(token.nextToken());
			int target = Integer.parseInt(token.nextToken());
			int weight = Integer.parseInt(token.nextToken());

			if (weight < edgesMinEnergy)
				edgesMinEnergy = weight;
			if (weight > edgesMaxEnergy)
				edgesMaxEnergy = weight;

			usg.addEdge(new MyLink(edgeCounter++, weight), source, target);

		}
	}
	
	public Graph(String inputFile) throws Exception {
		this.fileName = inputFile;
		loadGraph();
	}

	public UndirectedSparseGraph getUndirectedSparseGraph() {
		return usg;
	}

	public Node getRoot() {
		return root;
	}

	public int getNumVertices() {
		return numVertices;
	}

	public int getVerticesMaxEnergy() {
		return verticesMaxEnergy;
	}

	public int getVerticesMinEnergy() {
		return verticesMinEnergy;
	}

	public float getEdgesMinEnergy() {
		return edgesMinEnergy;
	}

	public float getEdgesMaxEnergy() {
		return edgesMaxEnergy;
	}

	class Node {

		/**
		 */
		private int key;
		/**
		 */
		private int energy;

		/**
		 * @return
		 */
		public int getEnergy() {
			return energy;
		}

		/**
		 * @param energy
		 */
		public void setEnergy(int energy) {
			this.energy = energy;
		}

		/**
		 * @return
		 */
		public int getKey() {
			return key;
		}

		/**
		 * @param key
		 */
		public void setKey(int key) {
			this.key = key;
		}
	}
}
