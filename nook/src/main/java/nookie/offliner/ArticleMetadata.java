package nookie.offliner;

public class ArticleMetadata {
	public final String id;
	public final String title;

	ArticleMetadata(String id, String title) {
		this.id = id;
		this.title = title;
	}

	public String toString() {
		return title;
	}
}

