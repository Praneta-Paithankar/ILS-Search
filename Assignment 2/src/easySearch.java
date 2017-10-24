import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class easySearch {
	private static Scanner scanner;
	private static IndexReader reader;
	private static IndexSearcher searcher;
	private static Analyzer analyzer;

	// Get document frequency k(t)
	public static Map<String, Integer> getDocumentFrequency(Query query) {
		Set<Term> queryTerms = new LinkedHashSet<Term>();
		Map<String, Integer> docFreqMap = new HashMap<String, Integer>();
		try {
			searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
			for (Term t : queryTerms) {
				int df = reader.docFreq(new Term("TEXT", t.text()));
				docFreqMap.put(t.text(), df);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docFreqMap;

	}

	// Get the preprocessed query terms
	public static Query getPreprocessedQuery() {
		Query query = null;
		try {
			analyzer = new StandardAnalyzer();
			System.out.println("Enter query:");
			scanner = new Scanner(System.in);
			String queryString = scanner.nextLine();
			QueryParser parser = new QueryParser("TEXT", analyzer);
			query = parser.parse(queryString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}

	// Get document length and term frequency
	public static HashMap<String, Double> calculateScore(Map<String, Integer> docFreqMap) throws Exception {
		float total_doc = reader.maxDoc();
		
		// Use DefaultSimilarity.decodeNormValue(…) to decode normalized document length
		ClassicSimilarity dSimi = new ClassicSimilarity();
		// Get the segments of the index
		List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();// Processing each segment
		Map<String, Float> docLength = new HashMap<String, Float>();
		HashMap<String, Double> tfValue = new HashMap<String, Double>();
		
		for (int i = 0; i < leafContexts.size(); i++) {
			// Get document length
			LeafReaderContext leafContext = leafContexts.get(i);
			int startDocNo = leafContext.docBase;
			int numberOfDoc = leafContext.reader().maxDoc();
			for (int docId = 0; docId < numberOfDoc; docId++) {
				// Get normalized length (1/sqrt(numOfTokens)) of the document
				float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
				// Get length of the document
				float docLeng = 1 / (normDocLeng * normDocLeng);
				docLength.put(searcher.doc(docId + startDocNo).get("DOCNO"), docLeng);
			}

			for (String t : docFreqMap.keySet()) {
				PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(), "TEXT", new BytesRef(t));
				if (de != null) {
					double result = 0.0;
					while ((de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
						String doc1 = searcher.doc(de.docID() + startDocNo).get("DOCNO");
						result = de.freq() / docLength.get(doc1);
						result = result * Math.log(1 + (total_doc / docFreqMap.get(t)));
						if (tfValue.containsKey(doc1)) {
							result += tfValue.get(doc1);
						}
						tfValue.put(doc1, result);
					}

				}
			}

		}
		return tfValue;
	}

	public static void main(String[] args) {
		try {
			reader = DirectoryReader
					.open(FSDirectory.open(Paths.get(System.getProperty("user.dir"), "index"));
			searcher = new IndexSearcher(reader);
			Query query = getPreprocessedQuery();
			Map<String, Integer> docFreqMap = getDocumentFrequency(query);
			HashMap<String, Double> tfValue=calculateScore(docFreqMap);
			writeResult(tfValue);
			System.out.println("Query is processed and results are written in firstop.txt.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writeResult(HashMap<String, Double> tfValue) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("firstop.txt", "UTF-8");
			for (String t : tfValue.keySet()) {
				writer.write("\n" + t + " " + tfValue.get(t));

			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
