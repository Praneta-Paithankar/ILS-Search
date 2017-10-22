public class QueryDetails {
	public String _queryID;
	public  String _title;
	public  String _desc;


	public QueryDetails() {
	}

	public QueryDetails(String _queryID, String _title, String _desc) {
		super();
		this._queryID = _queryID;
		this._title = _title;
		this._desc = _desc;
		
	}
	public String get_queryID() {
		return _queryID;
	}

	public void set_queryID(String _queryID) {
		this._queryID = _queryID;
	}
	public String get_title() {
		return _title;
	}

	public void set_title(String _title) {
		this._title =_title;
	}
	public String get_desc() {
		return _desc;
	}

	public void set_desc(String _desc) {
		this._desc = _desc;
	}
}
