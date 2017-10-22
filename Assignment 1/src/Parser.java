
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {

	public List<DocumentDetails> getDocumentDetails() {
		Path path = Paths.get(System.getProperty("user.dir"), "corpus");
		File folder = new File(path.toString());
		List<DocumentDetails> docs = new ArrayList<DocumentDetails>();
		String line;
		for (File file : folder.listFiles()) {
			if (file.isFile() && !file.isHidden()) // use threading TO do
			{
				FileReader in;
				try {
					in = new FileReader(file.getAbsolutePath());
					BufferedReader br = new BufferedReader(in);
					StringBuilder message = new StringBuilder();
					// String message = br.lines().collect(Collectors.joining(" "));
					while ((line = br.readLine()) != null) {
						message.append(line);
					}
					List<String> allMatches = new ArrayList<String>();
					Matcher m = Pattern.compile("<DOC>[\\s\\S]*?<\\/DOC>").matcher(message.toString());
					while (m.find()) {
						allMatches.add(m.group().replace("<DOC>", "").replace("</DOC>", ""));
					}

					docs.addAll(Parse(allMatches));
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return docs;

	}

	public static List<DocumentDetails> Parse(List<String> allMatches) {
		List<DocumentDetails> docs = new ArrayList<DocumentDetails>();
		Pattern docid = Pattern.compile("<DOCNO>[\\s\\S]*?<\\/DOCNO>");
		Pattern head = Pattern.compile("<HEAD>[\\s\\S]*?<\\/HEAD>");
		Pattern byline = Pattern.compile("<BYLINE>[\\s\\S]*?<\\/BYLINE>");
		Pattern dateline = Pattern.compile("<DATELINE>[\\s\\S]*?<\\/DATELINE>");
		Pattern text = Pattern.compile("<TEXT>[\\s\\S]*?<\\/TEXT>");
		Matcher m;
		String temp;
		for (String tag : allMatches) {
			DocumentDetails doc = new DocumentDetails();
			m = docid.matcher(tag);
			if (m.find()) {
				doc.set_docID(m.group().replaceAll("<DOCNO>", "").replaceAll("</DOCNO>", ""));
			}
			m = head.matcher(tag);
			doc.set_head("");
			while (m.find()) {
				temp = doc.get_head().concat(m.group().replaceAll("<HEAD>", "").replaceAll("</HEAD>", ""));
				doc.set_head(temp);
			}
			m = byline.matcher(tag);
			doc.set_byLine("");
			while (m.find()) {
				temp = doc.get_byLine().concat(m.group().replaceAll("<BYLINE>", "").replaceAll("</BYLINE>", ""));
				doc.set_byLine(temp);
			}
			m = dateline.matcher(tag);
			doc.set_dateLine("");
			while (m.find()) {
				temp = doc.get_dateLine().concat(m.group().replaceAll("<DATELINE>", "").replaceAll("</DATELINE>", ""));
				doc.set_dateLine(temp);
			}
			m = text.matcher(tag);
			doc.set_text("");
			while (m.find()) {
				temp = doc.get_text().concat(m.group().replaceAll("<TEXT>", "").replaceAll("</TEXT>", ""));
				doc.set_text(temp);
			}
			docs.add(doc);
		}
		return docs;
	}

}
