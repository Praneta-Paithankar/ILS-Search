

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class generateIndex {

	public static Document createDocument(DocumentDetails docInfo) {

		Document doc = new Document();
		doc.add(new StringField("DOCNO", docInfo.get_docID(), Field.Store.YES));
		doc.add(new TextField("HEAD", docInfo.get_head(), Field.Store.YES));
		doc.add(new TextField("BYLINE", docInfo.get_byLine(), Field.Store.YES));
		doc.add(new TextField("DATELINE", docInfo.get_dateLine(), Field.Store.YES));
		doc.add(new TextField("TEXT", docInfo.get_text(), Field.Store.YES));
		return doc;

	}

	public static void main(String args[]) {
		try {
			Parser parser = new Parser();
			//Directory dir = FSDirectory.open(Paths.get(System.getProperty("user.dir"), "Index"));
			Directory dir = FSDirectory.open(Paths.get("/Users/praneta/Desktop/Search/Index"));
			//KeywordAnalyzer analyzer=new KeywordAnalyzer();
			//Analyzer analyzer = new StandardAnalyzer();
			//Analyzer analyzer=new SimpleAnalyzer();
			Analyzer analyzer=new StopAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter indexwriter = new IndexWriter(dir, iwc);
			List<DocumentDetails> documents = parser.getDocumentDetails();
			for (DocumentDetails docInfo : documents) {
				indexwriter.addDocument(createDocument(docInfo));
			}
		
			indexwriter.forceMerge(1);
			indexwriter.commit();
			indexwriter.close();
			System.out.println("Index is created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
