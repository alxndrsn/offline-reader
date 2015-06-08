package nookie.offliner;

import java.util.*;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;

import static android.view.View.*;

public class ArticleListActivity extends Activity {
	private static final boolean DEBUG = BuildConfig.DEBUG;

	private ListView list;
	private SharedPreferences prefs;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		prefs = getPreferences(Context.MODE_PRIVATE);
		ArticleRepo.init(this);

		((Button) findViewById(R.id.btnRefreshArticles))
				.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateArticleListFromServer();
			}
		});

		((Button) findViewById(R.id.btnDownloadAllArticles))
				.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArticleRepo.$().fetchAllArticles();
				refreshArticlesList();
			}
		});

		list = (ListView) findViewById(R.id.lstArticles);
		refreshArticlesList();
	}

	public void onResume() {
		super.onResume();
		refreshArticlesList();
	}

	private void updateArticleListFromServer() {
		long latest = ArticleRepo.$().updateFromServer(
				prefs.getLong("last-update", 0));

		if(latest == 0) {
			toast(R.string.lstArticles_update_fail);
		} else {
			toast(R.string.lstArticles_update_success);

			refreshArticlesList();

			SharedPreferences.Editor ed = prefs.edit();
			ed.putLong("last-update", latest);
			ed.commit();
		}
	}

	private void refreshArticlesList() {
		List<ArticleMetadata> articles = ArticleRepo.$().getList();

		boolean allDownloaded = true;
		for(ArticleMetadata md : articles)
			allDownloaded = allDownloaded && md.isDownloaded;

		final int visibility = allDownloaded ? GONE : VISIBLE;
		findViewById(R.id.btnDownloadAllArticles)
				.setVisibility(visibility);

		list.setAdapter(new ArrayAdapter(this,
				R.layout.article_list_item,
				articles));
		list.setOnItemClickListener(new ArticleClickListener(this, articles));
	}

	private void toast(int messageId) {
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}

	private void log(String message, Object... args) {
		if(DEBUG) {
			System.err.println("LOG | ArticleListActivity." + String.format(message, args));
		}
	}
}

class ArticleClickListener implements OnItemClickListener {
	private final Context context;
	private final List<ArticleMetadata> articles;

	public ArticleClickListener(Context context, List<ArticleMetadata> articles) {
		this.context = context;
		this.articles = articles;
	}

	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		Intent intent = new Intent(context, DisplayArticleActivity.class);
		intent.putExtra("articleId", articles.get(position)._id);
		context.startActivity(intent);
	}
}
