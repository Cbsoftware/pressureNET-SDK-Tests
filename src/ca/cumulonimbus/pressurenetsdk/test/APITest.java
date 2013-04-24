package ca.cumulonimbus.pressurenetsdk.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import ca.cumulonimbus.pressurenetsdk.CbObservation;

public class APITest extends Activity {

	Button recentLocalData;

	private double latitude = 0.0;
	private double longitude = 0.0;
	private long startTime = 0;
	private long endTime = 0;
	private String format = "json";

	// hold raw results from the API in CbObservation objects
	private ArrayList<CbObservation> apiCbObservationResults = new ArrayList<CbObservation>();

	String serverURL = "https://pressurenet.cumulonimbus.ca/live/?";

	private class APIDataDownload extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String responseText = "";
			try {
				DefaultHttpClient client = new DefaultHttpClient();

				System.out.println("contacting api...");
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("min_lat", (latitude - 0.05)
						+ ""));
				nvps.add(new BasicNameValuePair("max_lat", (latitude + 0.05)
						+ ""));
				nvps.add(new BasicNameValuePair("min_lon", (longitude - 0.05)
						+ ""));
				nvps.add(new BasicNameValuePair("max_lon", (longitude + 0.05)
						+ ""));
				nvps.add(new BasicNameValuePair("start_time", startTime + ""));
				nvps.add(new BasicNameValuePair("end_time", endTime + ""));
				nvps.add(new BasicNameValuePair("api_key",
						SDKTestSettings.API_KEY));
				nvps.add(new BasicNameValuePair("format", format));

				String paramString = URLEncodedUtils.format(nvps, "utf-8");

				serverURL = serverURL + paramString;
				System.out.println(serverURL);
				HttpGet get = new HttpGet(serverURL);

				// Execute the GET call and obtain the response
				HttpResponse getResponse = client.execute(get);
				HttpEntity responseEntity = getResponse.getEntity();

				BufferedReader r = new BufferedReader(new InputStreamReader(
						responseEntity.getContent()));

				StringBuilder total = new StringBuilder();
				String line;
				if (r != null) {
					while ((line = r.readLine()) != null) {
						total.append(line);
					}
					responseText = total.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return responseText;
		}

		protected void onPostExecute(String result) {
			processJSONResult(result);
		}
	}

	/**
	 * Take a JSON string and return the data in a useful structure
	 * 
	 * @param resultJSON
	 */
	void processJSONResult(String resultJSON) {
		try {
			JSONArray jsonArray = new JSONArray(resultJSON);
			apiCbObservationResults.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				CbObservation singleObs = new CbObservation();
				try {
					Location location = new Location("network");
					location.setLatitude(jsonObject.getDouble("latitude"));
					location.setLongitude(jsonObject.getDouble("longitude"));
					location.setAccuracy((float) jsonObject
							.getDouble("location_accuracy"));
					singleObs.setLocation(location);
					singleObs.setTime(jsonObject.getLong("daterecorded"));
					singleObs.setTimeZoneOffset(jsonObject
							.getDouble("tzoffset"));
					singleObs.setSharing(jsonObject.getString("sharing"));
					singleObs.setUser_id(jsonObject.getString("user_id"));
					apiCbObservationResults.add(singleObs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			System.out.println(apiCbObservationResults.size() + " nearby pressure readings");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.api_test);

		Intent intent = getIntent();
		latitude = intent.getDoubleExtra("latitude", 0.0);
		longitude = intent.getDoubleExtra("longitude", 0.0);
		startTime = intent.getLongExtra("start_time", 0);
		endTime = intent.getLongExtra("end_time", 0);
		format = intent.getStringExtra("format");

		System.out.println("latitude " + latitude + ", longitude " + longitude
				+ ", start time " + startTime + ", end time " + endTime);

		recentLocalData = (Button) findViewById(R.id.buttonRecentLocalData);
		recentLocalData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				APIDataDownload api = new APIDataDownload();
				api.execute("");
			}
		});

	}
}
