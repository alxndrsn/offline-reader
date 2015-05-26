package nookie.offliner;

public class ArticleMetadata {
	private static final boolean DEBUG = BuildConfig.DEBUG;

	public final String _id;
	public final long dateAdded;
	public final String title;

	ArticleMetadata(String _id, String title) {
		this(_id, 0, title);
	}

	ArticleMetadata(String _id, long dateAdded, String title) {
		if(DEBUG) log("ArticleMetadata() :: _id:%s, title:%s", _id, title);
		this._id = _id;
		this.dateAdded = dateAdded;
		this.title = title;
	}

	public String toString() {
		return title == null ? "(untitled)" : title;
	}

	private void log(String message, Object... args) {
		if(DEBUG) {
			System.err.println("LOG | " + String.format(message, args));
		}
	}
}

