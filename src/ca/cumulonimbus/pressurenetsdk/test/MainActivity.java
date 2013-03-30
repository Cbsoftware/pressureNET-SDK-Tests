package ca.cumulonimbus.pressurenetsdk.test;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bin.classes.ca.cumulonimbus.pressurenetsdk.CbLocationManager;

public class MainActivity extends Activity {

	CbLocationManager cbLocation;
	Button buttonGo;
	Button buttonStop;
	EditText editLog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		cbLocation = new CbLocationManager(getApplicationContext());
		
		buttonGo = (Button) findViewById(R.id.button1);
		buttonStop = (Button) findViewById(R.id.button2);

		editLog = (EditText) findViewById(R.id.editLog);
		buttonGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Location currentBest = cbLocation.getCurrentBestLocation();
					String newBestInfo = currentBest.getProvider() + 
							" " + currentBest.getLatitude() + 
							" " + currentBest.getLongitude() + 
							" " + currentBest.getAccuracy();
					editLog.setText(editLog.getText() + newBestInfo + "\n");
					//Toast.makeText(getApplicationContext(), newBestInfo, Toast.LENGTH_LONG).show();
				} catch(NullPointerException npe) {

					
				}
			}
		});
		
		buttonStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cbLocation.stopGettingLocations();
			}
		});
	}

	
	
	@Override
	protected void onStart() {
		cbLocation.startGettingLocations();
		super.onResume();
	}

	@Override
	protected void onStop() {
		cbLocation.stopGettingLocations();
		super.onPause();
	}	
}
