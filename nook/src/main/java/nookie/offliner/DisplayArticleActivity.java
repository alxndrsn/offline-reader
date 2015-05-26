package nookie.offliner;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;

import static nookie.offliner.BuildConfig.*;
import static nookie.Utils.*;

public class DisplayArticleActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_article);

		// Fetch article object
		Intent intent = getIntent();
		final String articleId = intent.getStringExtra("articleId");

		Article article = ArticleRepo.$().get(articleId);

		String content = article == null ?
				"Error fetching article." :
				article.content;

		// Populate content
		TextView tView = (TextView) findViewById(R.id.article_display_content);
		tView.setText(Html.fromHtml(content));
		tView.setMovementMethod(LinkMovementMethod.getInstance());

		((Button) findViewById(R.id.btnMarkDeleted))
				.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deleteArticle(articleId);
			}
		});
	}

	public void onPause() { super.onPause(); }

	private void deleteArticle(String articleId) {
		if(DEBUG) log("deleteArticle() :: articleId=%s", articleId);
		ArticleRepo.$().delete(articleId);
		// TODO send message to ArticleListActivity to refresh without contacting server
		onBackPressed();
	}

	private void log(String message, Object... args) {
		if(DEBUG) {
			System.err.println("LOG | DisplayArticleActivity." + String.format(message, args));
		}
	}
}

