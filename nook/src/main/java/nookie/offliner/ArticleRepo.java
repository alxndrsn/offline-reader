package nookie.offliner;

import java.util.*;

public class ArticleRepo {
	public static final ArticleRepo INSTANCE = new ArticleRepo();

	private final List<ArticleMetadata> metadata;
	private final HashMap<String, Article> articles;

	private ArticleRepo() {
		metadata = Arrays.asList(
			new ArticleMetadata("123", "gripper"),
			new ArticleMetadata("456", "nipper")
		);
		articles = new HashMap();
		add("123", "A boring article about nothing.");
		add("456", "Another boring article about nothing.");
	}

	public List<ArticleMetadata> getList() {
		return metadata;
	}

	public Article get(String articleId) {
		return articles.get(articleId);
	}

	private void add(String id, String content) {
		articles.put(id, new Article(id, content));
	}
}
