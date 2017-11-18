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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;
public class AuthorRankwithQuery {
	public static Map<String, Double> authorMap;

	public static void main(String[] args) {
		String queryString = "Information Retrieval";
		calculatePriorProbability(queryString);
		DirectedSparseGraph<String, String> graph = createGraph();
		double alpha=0.15;
		Transformer<String,Double>authorTransformer=MapTransformer.getInstance(authorMap);
		PageRankWithPriors<String,String> pagerank=new PageRankWithPriors<String, String>(graph,authorTransformer, alpha);
		pagerank.evaluate();
		HashMap<String, Double> authorResultMap = new HashMap<String, Double>();
		for (String v : graph.getVertices()) {
			authorResultMap.put(v, pagerank.getVertexScore(v));
		}
		authorResultMap = sortByValue(authorResultMap);
		Iterator<Entry<String, Double>> iterator=authorResultMap.entrySet().iterator();
		for (int j=0;j<10;j++) {
			Map.Entry<String, Double> element = (Map.Entry<String, Double>) iterator.next();
			System.out.println(element.getKey()+"\t\t"+element.getValue());
		}
	}

	private static void calculatePriorProbability(String queryString) {
		
		try (IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("author_index")));){
			double priorSum = 0.0;
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new BM25Similarity());
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("content", analyzer);
			Query query = parser.parse(queryString);
			TopDocs results = searcher.search(query, 300);
			ScoreDoc[] scoreDocs = results.scoreDocs;
			authorMap = new HashMap<String, Double>();
			Document doc = null;
			double value;
			String authorid;
			for (int i = 0; i < 300; i++) {
				doc = searcher.doc(scoreDocs[i].doc);
				authorid=doc.get("authorid");
				if (authorMap.containsKey(authorid)) {
					value=authorMap.get(authorid).doubleValue()+scoreDocs[i].score;
					authorMap.put(authorid, value);
				}
				else {
					authorMap.put(authorid, (double) scoreDocs[i].score);
				}
				priorSum+=scoreDocs[i].score;
			}
			Iterator<Entry<String, Double>> iterator=authorMap.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, Double> element = (Map.Entry<String, Double>) iterator.next();
				value=element.getValue().doubleValue()/priorSum;
				authorMap.put(element.getKey(), value);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
				if(!authorMap.containsKey(s[1])) {
					authorMap.put(s[1], 0.0);
				}
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
