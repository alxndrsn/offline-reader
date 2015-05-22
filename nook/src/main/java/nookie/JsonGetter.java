package nookie;

import java.io.*;
import java.util.regex.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;

import org.json.*;

public class JsonGetter {
	private final boolean trace;
	private static final Pattern BASIC_AUTH = Pattern.compile(
			"(http[s]?://)(\\w*:\\w*)@(.*)");

	public JsonGetter(boolean trace) {
		this.trace = trace;
	}

	public JSONObject get(String url) throws Exception {
		if(trace) traceMethod("get", "url", url);
		DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpGet getter = createGetter(httpclient, url);

		InputStream inputStream = null;
		try {
			HttpResponse response = httpclient.execute(getter);
			HttpEntity entity = response.getEntity();
			if(trace) trace("get", "Got HTTP response: " + entity);

			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder bob = new StringBuilder();

			String line = null;
			while((line = reader.readLine()) != null) {
				bob.append(line + "\n");
			}
			String jsonString = bob.toString();
			if(trace) trace("get", "Retrieved JSON: " + jsonString);
			return new JSONObject(jsonString);
		} catch (Exception ex) {
			throw ex;
		} finally {
			try { inputStream.close(); } catch(Exception ex) {}
		}
	}

	private HttpGet createGetter(DefaultHttpClient httpclient, String url) {
		String authn = null;

		Matcher m = BASIC_AUTH.matcher(url);
		if(m.matches()) {
			url = m.group(1) + m.group(3);
			authn = "Basic " + Base64.encodeToString(
					m.group(2).getBytes(), false);
		}

		HttpGet getter = new HttpGet(url);
		getter.setHeader("Content-type", "application/json");

		if(authn != null) {
			getter.setHeader("Authorization", authn);
		}

		return getter;
	}

	private static void trace(String methodName, String message) {
		System.err.println("TRACE :: JsonGetter." +
				methodName + "()" +
				message);
	}

	private static void traceMethod(String methodName, String...args) {
		StringBuilder bob = new StringBuilder();
		for(int i=0; i<args.length; i+=2) {
			bob.append(args[i]);
			bob.append("=");
			bob.append(args[i+1]);
			bob.append(";");
		}
		trace(methodName, bob.toString());
	}
}

