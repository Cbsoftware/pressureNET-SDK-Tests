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
import android.widget.TextView;
import ca.cumulonimbus.pressurenetsdk.CbObservation;
import ca.cumulonimbus.pressurenetsdk.CbService;

public class MainActivity extends Activity {

	CbService cbService;
	Intent serviceIntent;

	Location bestLocation;
	CbObservation bestPressure;

	Button buttonStartEverything;
	Button buttonStopEverything;
	Button buttonShowBestLocation;
	Button buttonShowBestPressure;
	TextView editLog;

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
			case CbService.MSG_BEST_PRESSURE:
				bestPressure = (CbObservation) msg.obj;
				if(bestPressure!=null) {
					log("Client Received from service " + bestPressure.getObservationValue());
					editLog.setText("best pressure: " + bestPressure.getObservationValue());
				} else {
					log("pressure null");
				}
				break;
			default:
				log("received default message");
				super.handleMessage(msg);
			}
		}
	}

	private void stopEverything(View v) {
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


	private void askForBestPressure() {
		if (mBound) {
			log("asking for best pressure");
			Message msg = Message.obtain(null, CbService.MSG_GET_BEST_PRESSURE,
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
		buttonStopEverything = (Button) findViewById(R.id.buttonStop);
		buttonStartEverything= (Button) findViewById(R.id.buttonStart);
		buttonShowBestPressure= (Button) findViewById(R.id.buttonShowBestPressure);
		editLog = (TextView) findViewById(R.id.editLog);

		buttonShowBestPressure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				log("get best pressure");
				try {
					askForBestPressure();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
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

		buttonStartEverything.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				log("start everything");
				bindService(
						new Intent(getApplicationContext(), CbService.class),
						mConnection, Context.BIND_AUTO_CREATE);
				startService(serviceIntent);
			}
		});

		buttonStopEverything.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				log("stop everything");
				stopEverything(v);
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
