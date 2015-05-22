package nookie.offliner;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;

public class DisplayArticleActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_article);

		// Fetch article object
		Intent intent = getIntent();
		String articleId = intent.getStringExtra("articleId");
		Article article = ArticleRepo.INSTANCE.get(articleId);

		// Populate content
		TextView content = (TextView) findViewById(R.id.article_display_content);
		content.setText(Html.fromHtml(article.content));
		content.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void onPause() { super.onPause(); }
}

