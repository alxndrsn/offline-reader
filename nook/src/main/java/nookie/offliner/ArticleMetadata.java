package nookie.offliner;

public class ArticleMetadata {
	private static final boolean DEBUG = BuildConfig.DEBUG;

	public final String _id;
	public final long dateAdded;
	public final String title;
	public final boolean isDownloaded;

	ArticleMetadata(String _id, String title, boolean isDownloaded) {
		// From the database, we don't care about date-added, but we do
		// want to know if the article has been downloaded before
		this(_id, 0, title, isDownloaded);
	}

	ArticleMetadata(String _id, long dateAdded, String title) {
		// If we've just fetched this article info form the server, then
		// there's no possibility that it's already downloaded.
		this(_id, dateAdded, title, false);
	}

	ArticleMetadata(String _id, long dateAdded, String title, boolean isDownloaded) {
		if(DEBUG) log("ArticleMetadata() :: _id:%s, title:%s", _id, title);
		this._id = _id;
		this.dateAdded = dateAdded;
		this.title = title;
		this.isDownloaded = isDownloaded;
	}

	public String toString() {
		return (isDownloaded ? "[Y] " : "[N] ") +
				(title == null ? "(untitled)" : title);
	}

	private void log(String message, Object... args) {
		if(DEBUG) {
			System.err.println("LOG | " + String.format(message, args));
		}
	}
}

