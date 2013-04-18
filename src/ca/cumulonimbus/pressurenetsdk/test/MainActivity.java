package ca.cumulonimbus.pressurenetsdk.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import ca.cumulonimbus.pressurenetsdk.CbService;

public class MainActivity extends Activity {

	CbService cbService;
	Intent serviceIntent;
	
	Button buttonShowBestLocation;;
	Button buttonStartSensors;
	Button buttonStopSensors;
	EditText editLog;
	
	Messenger mService = null;
	boolean mBound;

	public void stopCollectingData(View v) {
		if (!mBound)
			return;
		Message msg = Message
				.obtain(null, CbService.MSG_STOP, 0, 0);
		try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		serviceIntent = new Intent(this, CbService.class);
		serviceIntent.putExtra("serverURL", "http://localhost:8000/");
		
		buttonShowBestLocation = (Button) findViewById(R.id.buttonShowBestLocation);
		buttonStopSensors = (Button) findViewById(R.id.buttonStopSensors);
		buttonStartSensors = (Button) findViewById(R.id.buttonStartSensors);

		editLog = (EditText) findViewById(R.id.editLog);
		
		buttonShowBestLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				log("get location");
				try {
					// Bind to CbService
					bindService(new Intent(getApplicationContext(), CbService.class), mConnection,
				            Context.BIND_AUTO_CREATE);
				} catch(NullPointerException npe) {
					npe.printStackTrace();
				}
			}
		});

		buttonStartSensors.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				log("start sensors");
				startService(serviceIntent);
			}
		});
		
		buttonStopSensors.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				log("stop sensors");
				stopCollectingData(v);
				
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
		if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
		super.onPause();
	}	
}
