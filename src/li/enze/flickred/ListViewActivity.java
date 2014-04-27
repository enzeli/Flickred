package li.enze.flickred;

import li.enze.flickred.FlickrAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.Toast;
import android.view.View;

public class ListViewActivity extends Activity {
	ListView listView;
	static ListViewAdapter adapter;
	
	private Integer page = 1;
	private final Integer PERPAGE = 10;
	String encodedtag;
	
	// ensure one loading thread only
	public static Boolean isLoading = false;
	// check reaches end of photo list
	public static Boolean isListEnded = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		
		// get tag and url-encode it
		Intent intent = getIntent();
		String tag = intent.getStringExtra("tag");
		try {
			encodedtag = URLEncoder.encode(tag,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// hide spinner first
		ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar1);
		spinner.setVisibility(View.GONE);
		
		// manage list view
		listView = (ListView) findViewById(R.id.list);
		listView.setFastScrollEnabled(true);
		adapter = new ListViewAdapter(this);
        listView.setAdapter(adapter); 
		
        // ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
                  
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
                		Uri.parse(adapter.getItem(position).url_c()));
                startActivity(browserIntent);
             
              }
		}); 
		
		// ListView Scorll Listener
		listView.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// do nothing
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(view.getAdapter().getCount() > 0 
						&& !ListViewActivity.isLoading
						&& !ListViewActivity.isListEnded){
					if(view.getLastVisiblePosition() == (view.getAdapter().getCount()-2)){
						AsyncFetchPhotos fetcher = new AsyncFetchPhotos(getNextRequest());
						fetcher.execute();
					}
				}
			}
		});
        
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		AsyncFetchPhotos fetcher = new AsyncFetchPhotos(getNextRequest());
		fetcher.execute();
	}

	private String getNextRequest() {
		String url = "http://api.flickr.com/services/rest/?"
				+ "method=flickr.photos.search"
				+ "&api_key=" + FlickrAPI.getKey()
				+ "&tags=" + encodedtag
				+ "&extras=date_taken,owner_name,description"
				+ "&per_page=" + PERPAGE.toString()
				+ "&page=" + page.toString();
		
		// increment page number
		page++;
		return url;
	}
	
	private class AsyncFetchPhotos extends AsyncTask<Void, Void, String> {
		private String requestUrl;
	    public AsyncFetchPhotos(String url) {
	    	this.requestUrl = url;
		}
	    
		@Override
		protected String doInBackground(Void... uris) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(this.requestUrl);
			String xml = "";
	             
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					xml = builder.toString();
				} else {
					Log.e("Getter", "Failed to download file");
				}
			} catch (UnknownHostException e) {
				xml = "NOCONNECTION";
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return xml;
		}
		
		@Override
		protected void onPreExecute(){
			ListViewActivity.isLoading = true;
			ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar1);
			spinner.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			ListViewActivity.isLoading = false;
			ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar1);
			spinner.setVisibility(View.GONE);
			
			ArrayList<Photo> newPhotos = null;
			
			if (result.equalsIgnoreCase("NOCONNECTION")){
				Toast.makeText(getApplicationContext(),"NO NETWORK CONNECTION", Toast.LENGTH_LONG).show();
			} else {
				newPhotos= FlickrXMLParser.parse(result);
			}
		
			ListViewAdapter adapter = ListViewActivity.adapter;
			
			if (newPhotos.size() == 0){
				ListViewActivity.isListEnded = true;
			} else {
				adapter.addPhotos(newPhotos);
			}
			
			// if no photos, Toast and return to main
			if (adapter.getCount()==0){
				Toast.makeText(getApplicationContext(),"NO PHOTOS FOUND", Toast.LENGTH_LONG).show();
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					@Override
					public void run() { 
						Intent result=new Intent();
						setResult(RESULT_CANCELED, result);
						finish(); 
					} 
				}, 4000);
			}

		};
			
	}
	
}
