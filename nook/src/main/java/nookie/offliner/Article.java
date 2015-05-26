package nookie.offliner;

import static nookie.offliner.BuildConfig.*;
import static nookie.Utils.*;

public class Article {
	final String _id;
	final String content;
	final String title;

	public Article(String _id, String content) {
		this._id = _id;
		this.content = content;

		title = getTitle();

		if(DEBUG) log("Article() :: _id:%s, title:%s", _id, getTitle());
	}

	private String getTitle() {
		if(content == null) {
			return "(no content)";
		}
		String stripped = stripHtmlTags(content)
				.replaceAll("\\s*\n(\\s*\n\\s*)+", " | ")
				.replaceAll("\\s+", " ")
				.trim();
		if(stripped.length() <= 100) {
			return stripped;
		}
		return stripped.substring(0, 99) + "…";
	}

	private void log(String message, Object... args) {
		if(DEBUG) {
			System.err.println("LOG | " + String.format(message, args));
		}
	}
}
