package nookie.offliner;

import java.util.*;
import nookie.*;
import org.json.*;

public class ArticleRepo {
	private static final boolean DEBUG = BuildConfig.DEBUG;

	public static final ArticleRepo I = new ArticleRepo();

	private List<ArticleMetadata> metadata;
	private final JsonGetter json;

	private ArticleRepo() {
		json = new JsonGetter(BuildConfig.DEBUG);

		metadata = Arrays.asList(
			new ArticleMetadata("123", "gripper"),
			new ArticleMetadata("456", "nipper")
		);
	}

	public void updateFromServer() {
		String queryString = BuildConfig.COUCH_URL + "/_design/app/_view/articles";
		try {
			JSONObject json = this.json.get(queryString);
			List<ArticleMetadata> metadata = asMetadata(
					json.getJSONArray("rows"));
			synchronized(this) {
				this.metadata = metadata;
			}
		} catch(Exception _) { if(DEBUG) _.printStackTrace(); }
	}

	public List<ArticleMetadata> getList() {
		synchronized(this) {
			return metadata;
		}
	}

	public Article get(String articleId) {
		String queryString = BuildConfig.COUCH_URL + "/" + articleId;
		Article a = null;
		try {
			JSONObject json = this.json.get(queryString);
			a = new Article(articleId,
					json.getString("content"));
		} catch(Exception _) { if(DEBUG) _.printStackTrace(); }
		return a;
	}

	private List<ArticleMetadata> asMetadata(JSONArray rows) throws JSONException {
		int len = rows.length();
		ArrayList<ArticleMetadata> md = new ArrayList(len);
		for(int i=0; i<len; ++i) {
			md.add(asMetadata(rows.getJSONObject(i)));
		}
		return md;
	}

	private ArticleMetadata asMetadata(JSONObject raw) throws JSONException {
		return new ArticleMetadata(
				raw.getString("id"),
				raw.getString("value"));
	}
}
