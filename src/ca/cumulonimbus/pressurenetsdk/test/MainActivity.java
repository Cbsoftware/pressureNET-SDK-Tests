package ca.cumulonimbus.pressurenetsdk.test;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import ca.cumulonimbus.pressurenetsdk.CbService;

public class MainActivity extends Activity {

	CbService cbService;
	Intent serviceIntent;
	
	Button buttonGo;
	Button buttonStart;
	Button buttonStop;
	EditText editLog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		serviceIntent = new Intent(this, CbService.class);
		
		buttonGo = (Button) findViewById(R.id.buttonShowBest);
		buttonStop = (Button) findViewById(R.id.buttonStop);
		buttonStart = (Button) findViewById(R.id.buttonStart);

		editLog = (EditText) findViewById(R.id.editLog);
		buttonGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Location currentBest = cbService.getLocationManager().getCurrentBestLocation();
					String newBestInfo = currentBest.getProvider() + 
							" " + currentBest.getLatitude() + 
							" " + currentBest.getLongitude() + 
							" " + currentBest.getAccuracy();
					editLog.setText(editLog.getText() + newBestInfo + "\n");
					log(newBestInfo);
				} catch(NullPointerException npe) {
					
				}
			}
		});

		buttonStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startService(serviceIntent);
			}
		});
		
		buttonStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopService(serviceIntent);
			}
		});
	}
	
	public void log(String message) {
		System.out.println(message);
	}
	
	@Override
	protected void onStart() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		stopService(serviceIntent);
		super.onPause();
	}	
}
