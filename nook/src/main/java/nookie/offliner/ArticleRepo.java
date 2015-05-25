package nookie.offliner;

import android.content.*;
import android.database.sqlite.*;
import java.net.*;
import java.util.*;
import nookie.*;
import org.json.*;

public class ArticleRepo {
	private static final boolean DEBUG = BuildConfig.DEBUG;

	private static ArticleRepo INSTANCE;

	private final Db db;
	private final JsonGetter json;
	private List<ArticleMetadata> metadata;

	private ArticleRepo(Context context) {
		db = new Db(context);

		json = new JsonGetter(BuildConfig.DEBUG);

		metadata = Arrays.asList(
			new ArticleMetadata("123", "gripper"),
			new ArticleMetadata("456", "nipper")
		);
	}

	public static void init(Context ctx) {
		if(INSTANCE == null) {
			INSTANCE = new ArticleRepo(ctx);
		}
	}

	public static ArticleRepo $() {
		return INSTANCE;
	}

	public void updateFromServer(Long lastRead) {
		String queryString = BuildConfig.COUCH_URL +
				"/_design/app/_view/articles";
		if(lastRead > 0) queryString += "?startkey=" + lastRead;
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

class Db extends SQLiteOpenHelper {
	private static final int VERSION = 1;

	Db(Context ctx) {
		super(ctx, "offliner", null, VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE articles (_id TEXT, content TEXT)");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
