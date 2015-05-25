package nookie.offliner;

import java.util.*;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;

public class ArticleListActivity extends Activity {
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
				refreshArticlesList();
			}
		});

		list = (ListView) findViewById(R.id.lstArticles);
		refreshArticlesList();
	}

	private void refreshArticlesList() {
		long now = System.currentTimeMillis(); // TODO this should be read from last-fetched

		ArticleRepo.$().updateFromServer(
				prefs.getLong("last-update", 0));

		List<ArticleMetadata> articles = ArticleRepo.$().getList();
		list.setAdapter(new ArrayAdapter(this,
				R.layout.article_list_item,
				articles));
		list.setOnItemClickListener(new ArticleClickListener(this, articles));

		SharedPreferences.Editor ed = prefs.edit();
		ed.putLong("last-update", now);
		ed.commit();
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
		intent.putExtra("articleId", articles.get(position).id);
		context.startActivity(intent);
	}
}
