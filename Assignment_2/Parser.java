
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {

	//get the query from document
	public List<QueryDetails> getQueryDetails() {
		Path path = Paths.get(System.getProperty("user.dir"), "topics.51-100");
		File file = new File(path.toString());
		List<QueryDetails> queries = new ArrayList<QueryDetails>();
		String line;
		FileReader in;
		try {
			in = new FileReader(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(in);
			StringBuilder message = new StringBuilder();
			while ((line = br.readLine()) != null) {
				message.append(line);
			}
			List<String> allMatches = new ArrayList<String>();
			Matcher m = Pattern.compile("<top>[\\s\\S]*?<\\/top>").matcher(message.toString());
			while (m.find()) {
				allMatches.add(m.group().replace("<top>", "").replace("</top>", ""));
			}

			queries.addAll(Parse(allMatches));
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return queries;

	}

	public static List<QueryDetails> Parse(List<String> allMatches) {
		List<QueryDetails> queries = new ArrayList<QueryDetails>();
		Pattern docid = Pattern.compile("<num>[\\s\\S]*?<dom>");
		Pattern title = Pattern.compile("<title>[\\s\\S]*?<desc>");
		Pattern desc = Pattern.compile("<desc>[\\s\\S]*?<smry>");
		Matcher m;
		for (String tag : allMatches) {
			QueryDetails doc = new QueryDetails();
			m = docid.matcher(tag);
			if (m.find()) {
				doc.set_queryID(m.group().replaceAll("<num>", "").replaceAll("<dom>", "").replace("Number: ", "").trim());
			}
			m = title.matcher(tag);
			if (m.find()) {
				doc.set_title(m.group().replaceAll("<title>", "").replaceAll("<desc>", "").replace("Topic:", "").trim());
			}
			m = desc.matcher(tag);
			if (m.find()) {
				doc.set_desc(m.group().replaceAll("<desc>", "").replaceAll("<smry>", "").replace("Description:", "").trim());
			}
			queries.add(doc);
		}
		return queries;
	}

}
