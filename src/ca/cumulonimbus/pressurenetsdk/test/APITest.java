package ca.cumulonimbus.pressurenetsdk.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class APITest extends Activity {

	Button recentLocalData;
	
	String serverURL = "https://pressurenet.cumulonimbus.ca/live/";
	
    private class APIDataDownload extends AsyncTask<String, String, String> {
    
    	@Override
		protected String doInBackground(String... arg0) {
	    	String responseText = "";	    	
	    	try {
	    		DefaultHttpClient client = new DefaultHttpClient();
	    		HttpPost post = new HttpPost(serverURL);
	    		
	    		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	    		nvps.add(new BasicNameValuePair("min_lat", ""));
	    		nvps.add(new BasicNameValuePair("max_lat", ""));
	    		nvps.add(new BasicNameValuePair("min_lon", ""));
	    		nvps.add(new BasicNameValuePair("max_lon", ""));
	    		nvps.add(new BasicNameValuePair("start_time", ""));
	    		nvps.add(new BasicNameValuePair("end_time", ""));
	    		nvps.add(new BasicNameValuePair("api_key", ""));
	    		nvps.add(new BasicNameValuePair("format", "json"));
	    		
	    		post.setEntity(new UrlEncodedFormEntity(nvps));
	    		
	    		
	    		// Execute the GET call and obtain the response
	    		HttpResponse getResponse = client.execute(post);
	    		HttpEntity responseEntity = getResponse.getEntity();
	    		
	    		
	    		BufferedReader r = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
	    		
	    		StringBuilder total = new StringBuilder();
	    		String line;
	    		if(r!=null) {
		    		while((line = r.readLine()) != null) {
		    			total.append(line);
		    		}
		    		responseText = total.toString();
	    		}
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	return responseText;
		}
		protected void onPostExecute(String result) {
			System.out.println("datadownload post execute " + result);
		}
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.api_test);
		
		recentLocalData = (Button) findViewById(R.id.buttonRecentLocalData);
		
		recentLocalData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
	}
}
