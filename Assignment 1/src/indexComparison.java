


import java.nio.file.Paths;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class indexComparison {
	public static void main(String args[]) {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("/Users/praneta/Desktop/Search/Index")));
			
			// Print the total number of documents in the corpus
			System.out.println("Total number of documents in the corpus: " + reader.maxDoc());
			// Print the size of the vocabulary for <field>TEXT</field>, applicable when the
			// index has only one segment.
			Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
			System.out.println("Size of the vocabulary for this field: " + vocabulary.size());
			
			System.out.println("Number of tokens for this field: "
					+ vocabulary.getSumTotalTermFreq());
			
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
