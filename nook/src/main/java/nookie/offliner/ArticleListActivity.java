package nookie.offliner;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;

public class ArticleListActivity extends Activity {
	private ListView list;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		list = (ListView) findViewById(R.id.lstArticles);
		refreshArticlesList();
	}

	private void refreshArticlesList() {
	}
}
