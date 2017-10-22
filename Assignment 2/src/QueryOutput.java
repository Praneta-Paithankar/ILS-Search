
public class QueryOutput {

	private String _queryID;
	private String _docID;
	private int _rank;
	private double _score;
	private String _runID;
	private String _q0;
	

	public QueryOutput(String _queryID, String _docID, int _rank, double _score, String _runID, String _q0) {
		super();
		this._queryID = _queryID;
		this._docID = _docID;
		this._rank = _rank;
		this._score = _score;
		this._runID = _runID;
		this._q0 = _q0;
	}

	public String get_docID() {
		return _docID;
	}

	public int get_rank() {
		return _rank;
	}

	public double get_score() {
		return _score;
	}

	public String get_runID() {
		return _runID;
	}

	public String get_q0() {
		return _q0;
	}

	public void set_docID(String _docID) {
		this._docID = _docID;
	}

	public void set_rank(int _rank) {
		this._rank = _rank;
	}

	public void set_score(double _score) {
		this._score = _score;
	}

	public void set_runID(String _runID) {
		this._runID = _runID;
	}

	public void set_q0(String _q0) {
		this._q0 = _q0;
	}

	public String get_queryID() {
		return _queryID;
	}

	public void set_queryID(String _queryID) {
		this._queryID = _queryID;
	}
	
}
