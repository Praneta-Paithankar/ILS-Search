

public class DocumentDetails {
	private String _docID;

	private String _head;
	private String _byLine;
	private String _dateLine;
	private String _text;

	public DocumentDetails() {
	}

	public DocumentDetails(String _docID, String _fileID, String _head, String _byLine, String _dateLine,
			String _text) {
		super();
		this._docID = _docID;
		this._head = _head;
		this._byLine = _byLine;
		this._dateLine = _dateLine;
		this._text = _text;
	}

	public String get_head() {
		return _head;
	}

	public void set_head(String _head) {
		this._head = _head;
	}

	public String get_byLine() {
		return _byLine;
	}

	public void set_byLine(String _byLine) {
		this._byLine = _byLine;
	}

	public String get_dateLine() {
		return _dateLine;
	}

	public void set_dateLine(String _dateLine) {
		this._dateLine = _dateLine;
	}

	public String get_text() {
		return _text;
	}

	public void set_text(String _text) {
		this._text = _text;
	}

	public String get_docID() {
		return _docID;
	}

	public void set_docID(String _docID) {
		this._docID = _docID;
	}

}
