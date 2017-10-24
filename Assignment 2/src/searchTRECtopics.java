import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class searchTRECtopics {
	static IndexReader reader ;
	static IndexSearcher searcher;
	static Analyzer analyzer ;
	static QueryParser parser;
	static float total_doc ;

	//Calculate the score
	public static HashMap<String, Double> CalculateScore(String queryString) throws ParseException, IOException {

		Query query = parser.parse(QueryParser.escape(queryString));
		Set<Term> queryTerms = new LinkedHashSet<Term>();
		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
		// Get document frequency k(t)
		Map<String, Integer> docFreqMap = new HashMap<String, Integer>();
		for (Term t : queryTerms) {
			int df = reader.docFreq(new Term("TEXT", t.text()));
			docFreqMap.put(t.text(), df);
		}
		/**
		 * Get document length and term frequency
		 */
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
			//Calculate the final value
			for (String t : docFreqMap.keySet()) {
				PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(), "TEXT", new BytesRef(t));
				int doc;
				if (de != null) {
					double result = 0.0;
					while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
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
	//Write result in a file
    public static void WriteQuery(HashMap<String, Double> score,String queryID, PrintWriter pw ) {
    		int rank=0;
    		Object []a=score.entrySet().toArray();
    		String s;
    		extracted(a);
    		for(Object o:a) {
    			rank++;
    			@SuppressWarnings("unchecked")
				Entry<String, Double> entry = (Map.Entry<String, Double>)o;
				s=queryID+"\tQ0\t"+entry.getKey()+"\t"+rank+"\t"+
    			  entry.getValue()+"\t"+"run-1";
    			pw.write(s+"\n");
    			if(rank==1000) {
    				break;
    			}
    		}
    }
	@SuppressWarnings("unchecked")
	private static void extracted(Object[] a) {
		Arrays.sort(a,(o1, o2) -> {
			Entry<String, Double> entry = (Map.Entry<String, Double>)o1;
			return ((Map.Entry<String, Double>)o2).getValue()
					.compareTo(entry.getValue());
		});
	}
	public static void main(String[] args) {
		try {
			reader= DirectoryReader
					.open(FSDirectory.open(Paths.get(System.getProperty("user.dir"), "index")));
			searcher = new IndexSearcher(reader);
			analyzer = new StandardAnalyzer();
			total_doc = reader.maxDoc();
			parser = new QueryParser("TEXT", analyzer);
			Parser parser1 = new Parser();
			List<QueryDetails> queries = parser1.getQueryDetails();
			HashMap<String, Double> tfvalue ;
			PrintWriter pw =new PrintWriter("shortQuery.txt");
			PrintWriter pw1 =new PrintWriter("longQuery.txt");
			for (QueryDetails queryString : queries) {
				tfvalue = CalculateScore(queryString._title);
				WriteQuery(tfvalue,queryString._queryID,pw);
				tfvalue = CalculateScore(queryString._desc);
				WriteQuery(tfvalue,queryString._queryID,pw1);
			}
			pw1.close();
			pw.close();
			System.out.println("Done");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
}
