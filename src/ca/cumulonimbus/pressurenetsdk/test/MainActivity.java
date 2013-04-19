package ca.cumulonimbus.pressurenetsdk.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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

	Location bestLocation;
	
	Button buttonShowBestLocation;;
	Button buttonStartSensors;
	Button buttonStopSensors;
	EditText editLog;

	boolean mBound;
	private Messenger mMessenger = new Messenger(new IncomingHandler());
	Messenger mService = null;

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CbService.MSG_BEST_LOCATION:
				bestLocation = (Location) msg.obj;
				if(bestLocation!=null) {
					log("Client Received from service " + bestLocation.getLatitude());
					editLog.setText("best location : " + bestLocation.getLatitude() + " " + bestLocation.getLongitude());
				} else {
					log("location null");
				}
				break;
			default:
				log("received default message");
				super.handleMessage(msg);
			}
		}
	}

	private void stopCollectingData(View v) {
		if (mBound) {
			Message msg = Message.obtain(null, CbService.MSG_STOP, 0, 0);
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			log("error: not bound");
		}

	}

	private void askForBestLocation() {
		if (mBound) {
			log("asking for best location");
			Message msg = Message.obtain(null, CbService.MSG_GET_BEST_LOCATION,
					0, 0);
			try {
				msg.replyTo = mMessenger;
				mService.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			log("error: not bound");
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			log("client says : service connected");
			mService = new Messenger(service);
			mBound = true;
			Message msg = Message.obtain(null, CbService.MSG_BEST_LOCATION);
			log("client received " + msg.arg1 + " " + msg.arg2);
			
		}

		public void onServiceDisconnected(ComponentName className) {
			log("client: service disconnected");
			mMessenger = null;
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
					askForBestLocation();
				} catch (NullPointerException npe) {
					npe.printStackTrace();
				}
			}
		});

		buttonStartSensors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				log("bind to service, start sensors");
				bindService(
						new Intent(getApplicationContext(), CbService.class),
						mConnection, Context.BIND_AUTO_CREATE);
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
