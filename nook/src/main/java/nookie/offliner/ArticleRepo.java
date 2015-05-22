package nookie.offliner;

public class ArticleRepo {
	public static final ArticleRepo INSTANCE = new ArticleRepo();

	public Article get(String articleId) {
		// TODO if article is not available locally then attempt to
		// fetch it
		// TODO if the article is now available, return it
		return null;
	}
}
