import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;


public class compareAlgorithms {
	static float total_doc ;

	public static void main(String[] args) {
		try {
			IndexReader reader= DirectoryReader
					.open(FSDirectory.open(Paths.get(System.getProperty("user.dir"), "index")));
			IndexSearcher searcher = new IndexSearcher(reader);
			Parser parser1 = new Parser();
			List<QueryDetails> queries = parser1.getQueryDetails();
			
			//searcher.setSimilarity(new BM25Similarity()); 
			//searcher.setSimilarity(new LMDirichletSimilarity()); 
			//searcher.setSimilarity(new LMJelinekMercerSimilarity(0.7f)); 
			searcher.setSimilarity(new ClassicSimilarity()); 
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("TEXT", analyzer); 
			
//			String shortQueryFileName="BM25shortQuery.txt";
//			String longQueryFileName="BM25longQuery.txt";
//			String shortQueryFileName="LMDirichletshortQuery.txt";
//			String longQueryFileName="LMDirichletlongQuery.txt";
//			String shortQueryFileName="LMJelinekMercershortQuery.txt";
//			String longQueryFileName="LMJelinekMercerlongQuery.txt";
			String shortQueryFileName="DefaultshortQuery.txt";
			String longQueryFileName="DefaultlongQuery.txt";

			PrintWriter pw =new PrintWriter(shortQueryFileName);
			PrintWriter pw1 =new PrintWriter(longQueryFileName);
			String s;
			int rank;
			Query query ;
			TopDocs topDocs;
			ScoreDoc[] docs ;
			for (QueryDetails queryString : queries) {
				rank=0;
				query = parser.parse(QueryParser.escape(queryString._title));
				topDocs = searcher.search(query, 1000);
				docs = topDocs.scoreDocs;
				for (int i = 0; i < docs.length; i++) {
					rank++;
					Document doc = searcher.doc(docs[i].doc);
					s=queryString._queryID+"\tQ0\t"+doc.get("DOCNO")+"\t"+rank+"\t"+
							docs[i].score+"\t"+"run-1";
			    		pw.write(s+"\n");
				}
				rank=0;
				query = parser.parse(QueryParser.escape(queryString._desc));
				topDocs = searcher.search(query, 1000);
				docs = topDocs.scoreDocs;
				for (int i = 0; i < docs.length; i++) {
					rank++;
					Document doc = searcher.doc(docs[i].doc);
					s=queryString._queryID+"\tQ0\t"+doc.get("DOCNO")+"\t"+rank+"\t"+
							docs[i].score+"\t"+"run-1";
			    		pw1.write(s+"\n");
				}
			}
			pw1.close();
			pw.close();
			reader.close();
			System.out.println("Done");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

}
