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

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ca.cumulonimbus.pressurenetsdk.CbMapView;
import ca.cumulonimbus.pressurenetsdk.CbObservation;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;

public class APITest extends MapActivity {

	Button recentLocalData;
	SurfaceView surfaceGraph;

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
					singleObs.setObservationValue(jsonObject
							.getDouble("reading"));
					apiCbObservationResults.add(singleObs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			ArrayList<CbObservation> detailedList = CbObservation
					.addDatesAndTrends(apiCbObservationResults);

			graphDetailedMeasurements(detailedList);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void graphDetailedMeasurements(ArrayList<CbObservation> detailedList) {
		System.out.println("downloaded " + detailedList.size() + " points");
	}

	public void setMap() {
		CbMapView cbMap = (CbMapView) findViewById(R.id.mapview);
		MapController mc = cbMap.getController();
		LocationManager lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location loc = lm
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		mc.setZoom(15);
		if (loc.getLatitude() != 0) {
			// log("setting center " + loc.getLatitude() + " " +
			// loc.getLongitude());
			mc.animateTo(new GeoPoint((int) (loc.getLatitude() * 1E6),
					(int) (loc.getLongitude() * 1E6)));
		} else {

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

		surfaceGraph = (SurfaceView) findViewById(R.id.surfaceGraph);

		recentLocalData = (Button) findViewById(R.id.buttonRecentLocalData);
		recentLocalData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				APIDataDownload api = new APIDataDownload();
				api.execute("");
			}
		});

		setMap();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
