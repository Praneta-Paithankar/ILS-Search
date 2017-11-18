import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class AuthorRank {
	public static void main(String[] args) {

		DirectedSparseGraph<String, String> graph = createGraph();
		// Execute page rank algorithm
		double alpha = 0.15;
		PageRank<String, String> ranker = new PageRank<String, String>(graph, alpha);
		ranker.evaluate();
		// get rank of each vertex
		HashMap<String, Double> verticesMap = new HashMap<String, Double>();
		for (String v : graph.getVertices()) {
			verticesMap.put(v, ranker.getVertexScore(v));
		}
		verticesMap = sortByValue(verticesMap);
		Iterator<Entry<String, Double>> iterator=verticesMap.entrySet().iterator();
		for (int j=0;j<10;j++) {
			Map.Entry<String, Double> element = (Map.Entry<String, Double>) iterator.next();
			System.out.println(element.getKey()+"\t\t"+element.getValue());
		}
	}
	public static DirectedSparseGraph<String, String> createGraph() {
		String fileName = Paths.get(System.getProperty("user.dir"), "author.net.txt").toString();
		DirectedSparseGraph<String, String> graph = new DirectedSparseGraph<String, String>();
		HashMap<String, String> verticesMap = new HashMap<String, String>();
		try (Scanner sc = new Scanner(new File(fileName))) {
			// add vertex to graph
			String[] verticesLine = sc.nextLine().split("\\s+");
			int totalVertices = Integer.parseInt(verticesLine[1]);
			for (int i = 0; i < totalVertices; i++) {
				String[] s = sc.nextLine().split("\\s+");
				s[1] = s[1].replace("\"", "");
				verticesMap.put(s[0], s[1]);
				graph.addVertex(s[1]);
			}
			// add edges to the graph
			String[] edgesLine = sc.nextLine().split("\\s+");
			int totalEdges = Integer.parseInt(edgesLine[1]);
			for (int i = 0; i < totalEdges; i++) {
				String[] s = sc.nextLine().split("\\s+");
				Pair<String> p = new Pair<String>(verticesMap.get(s[0]), verticesMap.get(s[1]));
				graph.addEdge(Integer.toString(i), p, EdgeType.DIRECTED);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return graph;
	}
	

	private static HashMap<String, Double> sortByValue(HashMap<String, Double> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).collect(Collectors.toMap(
		          Map.Entry::getKey, 
		          Map.Entry::getValue, 
		          (e1, e2) -> e1, 
		          LinkedHashMap::new
		        ));
	
	}

	
}
