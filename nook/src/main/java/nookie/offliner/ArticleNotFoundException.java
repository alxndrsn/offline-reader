package nookie.offliner;

class ArticleNotFoundException extends Exception {
	public ArticleNotFoundException(String _id) {
		super(String.format("id: %s", _id));
	}
}
