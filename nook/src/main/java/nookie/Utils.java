package nookie;

import android.text.Html;

public class Utils {
	public static String[] A(String... args) {
		return args;
	}

	public static String stripHtmlTags(String html) {
		return Html.fromHtml(html).toString();
	}
}
