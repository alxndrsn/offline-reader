package nookie.offliner;

import android.content.*;
import android.database.sqlite.*;
import android.database.*;
import java.net.*;
import java.util.*;
import nookie.*;
import org.json.*;

import static nookie.offliner.BuildConfig.DEBUG;
import static nookie.Utils.*;

public class ArticleRepo {
	private static ArticleRepo INSTANCE;

	private final Db db;
	private final JsonGetter json;

	private ArticleRepo(Context context) {
		db = new Db(context);
		db.init();

		json = new JsonGetter(DEBUG);
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
			db.store(metadata);
		} catch(Exception _) { if(DEBUG) _.printStackTrace(); }
	}

	public List<ArticleMetadata> getList() {
		return db.list();
	}

	public Article get(String articleId) {
		if(DEBUG) log("get() :: requested: %s", articleId);
		Article a = db.get(articleId);
		if(a == null || a.content == null) {
			if(DEBUG) log("get() :: Article not found in DB (%s).  Fetching from server...", a);
			a = fetch(articleId);
			db.store(a);
		}
		return a;
	}

	private Article fetch(String _id) {
		if(DEBUG) log("fetch() :: Fetching article with ID: %s", _id);
		String queryString = BuildConfig.COUCH_URL + "/" + _id;
		Article a = null;
		try {
			JSONObject json = this.json.get(queryString);
			a = new Article(_id, json.getString("content"));
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

	private void log(String message, Object... args) {
		if(DEBUG) {
			System.err.println("LOG | ArticleRepo." + String.format(message, args));
		}
	}
}

class Db extends SQLiteOpenHelper {
	private static final boolean DEBUG = BuildConfig.DEBUG;

	private static final int VERSION = 1;
	private static final String tblARTICLES = "articles";
	private static final String clmTITLE = "title";
	private static final String clmCONTENT = "content";
	private static final String clmID = "_id";
	private static final String[] NO_ARGS = new String[0];

	private SQLiteDatabase db;

	Db(Context ctx) {
		super(ctx, "offliner", null, VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL(String.format(
				"CREATE TABLE %s (%s TEXT, %s TEXT, %s TEXT)",
				tblARTICLES, clmID, clmTITLE, clmCONTENT));
	}

	public void init() {
		if(db == null) db = getWritableDatabase();
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	Article get(String _id) {
		if(DEBUG) log("get() :: article requested: %s", _id);
		String q = String.format("SELECT %s FROM %s WHERE %s=?",
				clmCONTENT, tblARTICLES, clmID);
		Cursor c = null;
		try {
			c = db.rawQuery(q, A(_id));
			if(DEBUG) log("get() :: got %s rows", c.getCount());
			if(c.getCount() == 0) return null;
			c.moveToFirst();
			return new Article(_id, c.getString(0));
		} finally {
			if(c != null) c.close();
		}
	}

	void store(Article a) {
		if(DEBUG) log("store() :: storing article with ID %s", a._id);
		store(a._id, a.title, a.content);
	}

	List<ArticleMetadata> list() {
		String q = String.format("SELECT %s,%s FROM %s",
				clmID, clmTITLE, tblARTICLES);
		Cursor c = null;
		try {
			c = db.rawQuery(q, NO_ARGS);

			int count = c.getCount();
			if(DEBUG) log("list() :: item fetch count: %s", count);
			ArrayList<ArticleMetadata> list = new ArrayList(count);
			c.moveToFirst();
			while(count-- > 0) {
				list.add(new ArticleMetadata(
						c.getString(0), c.getString(1)));
				c.moveToNext();
			}
			if(DEBUG) log("list() :: list size: %s", list.size());
			return list;
		} finally {
			if(c != null) c.close();
		}
	}

	void store(List<ArticleMetadata> metadata) {
		for(ArticleMetadata md : metadata) {
			String _id = md._id;
			if(!inDb(_id)) {
				store(_id, md.title, null);
			}
		}
	}

	private boolean inDb(String _id) {
		String q = String.format("SELECT %s FROM %s WHERE %s=?",
				clmID, tblARTICLES, clmID);
		Cursor c = null;
		try {
			c = db.rawQuery(q, A(_id));
			return c.getCount() > 0;
		} finally {
			if(c != null) c.close();
		}
	}

	private void store(String _id, String title, String content) {
		if(DEBUG) log("store() :: _id:%s, title:%s, contentLength:%s",
				_id, title,
				content == null? null: content.length());
		ContentValues v = new ContentValues();
		v.put(clmTITLE, title);
		v.put(clmCONTENT, content);

		int rowsUpdated = db.update(tblARTICLES, v, "_id=?", A(_id));
		if(rowsUpdated > 0) {
			if(DEBUG) log("store() :: Updated %s in database.", _id);
		} else {
			v.put(clmID, _id);
			db.insert(tblARTICLES, null, v);
			if(DEBUG) log("store() :: inserted %s into database.", _id);
		}
	}

	private void log(String message, Object... args) {
		if(DEBUG) {
			System.err.println("LOG | Db." + String.format(message, args));
		}
	}
}
