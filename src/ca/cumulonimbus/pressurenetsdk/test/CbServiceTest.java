package ca.cumulonimbus.pressurenetsdk.test;

import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import static org.mockito.Mockito.doReturn;
import junit.framework.TestSuite;
import junit.framework.Assert.*;
import android.net.Uri;
import android.os.Handler;
import android.os.Messenger;
import android.os.PowerManager;
import static org.mockito.Mockito.mock;
import android.content.pm.ApplicationInfo;
import static org.mockito.Mockito.spy;
import android.content.pm.PackageManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.mock.MockContext;
import ca.cumulonimbus.pressurenetsdk.CbService;
import ca.cumulonimbus.pressurenetsdk.CbService.CbSensorStreamer;

/**
 * 
 */

/**
 * @author natalie
 *
 */
public class CbServiceTest extends ServiceTestCase<CbService> {
 
	
	class CbServiceTestContext extends MockContext{

		private String packageName = "ca.cumulonimbus.pressurenetsdk";
		
		/* (non-Javadoc)
		 * @see android.test.mock.MockContext#checkPermission(java.lang.String, int, int)
		 */
		@Override
		public int checkPermission(String permission, int pid, int uid) {
			return PackageManager.PERMISSION_GRANTED;
		}

		/* (non-Javadoc)
		 * @see android.test.mock.MockContext#getApplicationInfo()
		 */
		@Override
		public ApplicationInfo getApplicationInfo() {
			System.out.println("in getAppInfo");
			return mock(ApplicationInfo.class);
		}

		/* (non-Javadoc)
		 * @see android.test.mock.MockContext#getApplicationContext()
		 */
		@Override
		public Context getApplicationContext() {
			return this;
		}

		/* (non-Javadoc)
		 * @see android.test.mock.MockContext#getPackageName()
		 */
		@Override
		public String getPackageName() {
			return this.packageName;
		}

		/* (non-Javadoc)
		 * @see android.test.mock.MockContext#registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter)
		 */
		@Override
		public Intent registerReceiver(BroadcastReceiver receiver,
				IntentFilter filter) {
		    return mock(Intent.class);
		}

		/* (non-Javadoc)
		 * @see android.test.mock.MockContext#getPackageManager()
		 */
		@Override
		public PackageManager getPackageManager() {
			return mock(PackageManager.class);
		}

		/* (non-Javadoc)
		 * @see android.test.mock.MockContext#getSystemService(java.lang.String)
		 */
		@Override
		public Object getSystemService(String name) {
			return mock(PowerManager.class);
		}
		
		
	}

	public CbServiceTest() {
		super(CbService.class);
		// TODO Auto-generated constructor stub
	}
	
	public CbServiceTest(Class<CbService> serviceClass) {
		super(serviceClass);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.test.ServiceTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("in setup");
		//System.setProperty("dexmaker.dexcache", "/home/natalie/dexcache");
		System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
        //CbServiceTestContext mcontext = new CbServiceTestContext();
        ContextWrapper mcontext = new ContextWrapper(getSystemContext()) {

			/* (non-Javadoc)
			 * @see android.content.ContextWrapper#checkPermission(java.lang.String, int, int)
			 */
			@Override
			public int checkPermission(String permission, int pid, int uid) {
				System.out.println("checkpermission");
				if(permission == "android.permission.ACCESS_FINE_LOCATION"){
					return PackageManager.PERMISSION_GRANTED;
				}else if(permission == "android.permission.INTERNET"){
					return PackageManager.PERMISSION_GRANTED;
				}else if(permission == "android.permission.ACCESS_NETWORK_STATE"){
					return PackageManager.PERMISSION_GRANTED;
		    	}else if(permission == "android.permission.WAKE_LOCK"){
					return PackageManager.PERMISSION_GRANTED;
		    	}else{
					return PackageManager.PERMISSION_DENIED;				
			    }
			}

			/* (non-Javadoc)
			 * @see android.content.ContextWrapper#checkCallingOrSelfPermission(java.lang.String)
			 */
			@Override
			public int checkCallingOrSelfPermission(String permission) {
				System.out.println("checkcallingorselfpermission");
				// TODO Auto-generated method stub
				return super.checkCallingOrSelfPermission(permission);
			}

			/* (non-Javadoc)
			 * @see android.content.ContextWrapper#checkCallingOrSelfUriPermission(android.net.Uri, int)
			 */
			@Override
			public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
				System.out.println("checkcallingorselfuripermission");
				return super.checkCallingOrSelfUriPermission(uri, modeFlags);
			}

			/* (non-Javadoc)
			 * @see android.content.ContextWrapper#checkCallingPermission(java.lang.String)
			 */
			@Override
			public int checkCallingPermission(String permission) {
				System.out.println("checkcallingpermission");
				return super.checkCallingPermission(permission);
			}

			/* (non-Javadoc)
			 * @see android.content.ContextWrapper#checkCallingUriPermission(android.net.Uri, int)
			 */
			@Override
			public int checkCallingUriPermission(Uri uri, int modeFlags) {
				System.out.println("checkcallinguripermission");
				return super.checkCallingUriPermission(uri, modeFlags);
			}

			/* (non-Javadoc)
			 * @see android.content.ContextWrapper#checkUriPermission(android.net.Uri, int, int, int)
			 */
			@Override
			public int checkUriPermission(Uri uri, int pid, int uid,
					int modeFlags) {
				System.out.println("checkuripermission");
				return super.checkUriPermission(uri, pid, uid, modeFlags);
			}

			/* (non-Javadoc)
			 * @see android.content.ContextWrapper#checkUriPermission(android.net.Uri, java.lang.String, java.lang.String, int, int, int)
			 */
			@Override
			public int checkUriPermission(Uri uri, String readPermission,
					String writePermission, int pid, int uid, int modeFlags) {
				System.out.println("checkuripermission2");
				return super.checkUriPermission(uri, readPermission, writePermission, pid, uid,
						modeFlags);
			}
			
			
        };
        
        setContext(mcontext);
        
       
	}
	
	private CbService setUpService(){
		 Intent intent  = new Intent(getSystemContext(), CbService.class);
	     startService(intent);
	     
	     CbService service = getService();
		 CbService spyService = spy(service);
		 return spyService;
	}

	/* (non-Javadoc)
	 * @see android.test.ServiceTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#onCreate()}.
	 */
	public void testOnCreate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#onDestroy()}.
	 */
	public void testOnDestroy() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#startSensorStream(int, android.os.Messenger)}.
	 */
	public void testStartSensorStreamSensorStreamingFalse() {
		CbService service = setUpService();
		
		int sensorId = 1;
		//Messenger messenger = PowerMockito.mock(Messenger.class);
		//Handler mhandler = mock(Handler.class);
		//mhandler.when(getIMessenger)
		Handler mhandler = new Handler();
		Messenger messenger = new Messenger(mhandler);
		//Messenger spyMessenger = spy(messenger);
		ArrayList<CbSensorStreamer> streams = spy(new ArrayList<CbSensorStreamer>());
		
		service.setActiveStreams(streams);		
		
		CbSensorStreamer stream = mock(CbSensorStreamer.class);
		doReturn(stream).doCallRealMethod().when(service).createCbSensorStreamer(sensorId, messenger);

		service.startSensorStream(sensorId, messenger);
		
		verify(service).log("CbService starting live sensor streaming " + sensorId);     
		verify(service).createCbSensorStreamer(sensorId, messenger);
		verify(streams).add(stream);
		verify(stream).startSendingData();
	}
	
	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#startSensorStream(int, android.os.Messenger)}.
	 */
	public void testStartSensorStreamSensorStreamingTrue() {
		CbService service = setUpService();
		
		int sensorId = 1;
		Handler mhandler = new Handler();
		Messenger messenger = new Messenger(mhandler);
		//Messenger spyMessenger = spy(messenger);
		ArrayList<CbSensorStreamer> alist = new ArrayList<CbSensorStreamer>();
		CbSensorStreamer alreadyStreaming = service.new CbSensorStreamer(1, messenger);
		alist.add(alreadyStreaming);
        ArrayList<CbSensorStreamer> streams = spy(alist);
		
		service.setActiveStreams(streams);				
		service.startSensorStream(sensorId, messenger);
		verify(service).log("CbService not starting live sensor streaming 1, already streaming");     
		
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#stopSensorStream(int)}.
	 */
	public void testStopSensorStream() {
		CbService service = setUpService();
		int sensorId1 = 1;
		Handler mhandler1 = new Handler();
		Messenger messenger1 = new Messenger(mhandler1);
		int sensorId2 = 2;
		Handler mhandler2 = new Handler();
		Messenger messenger2 = new Messenger(mhandler2);
		ArrayList<CbSensorStreamer> alist = new ArrayList<CbSensorStreamer>();
		CbSensorStreamer alreadyStreaming1 = service.new CbSensorStreamer(sensorId1, messenger1);
		CbSensorStreamer alreadyStreaming2 = service.new CbSensorStreamer(sensorId2, messenger2);
		
		
		
		ArrayList<CbSensorStreamer> postList = new ArrayList<CbSensorStreamer>();
		postList.add(alreadyStreaming2);
        ArrayList<CbSensorStreamer> streams = spy(alist);
        CbSensorStreamer sensorStreamSpy = spy(alreadyStreaming1);
		
        alist.add(sensorStreamSpy);
		alist.add(alreadyStreaming2);
        
		service.setActiveStreams(alist);	
		
		service.stopSensorStream(sensorId1);
		
		verify(service).log("CbService stopping live sensor streaming " + sensorId1);

		verify(sensorStreamSpy).stopSendingData();
		

		assertEquals(postList, alist);
	}
	
	public void testStopSensorStreamNotStreaming() {
		CbService service = setUpService();
		int sensorId = 1;
		service.stopSensorStream(sensorId);
		verify(service).log("CbService not stopping live sensor streaming 1 sensor not running");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#collectNewObservation()}.
	 */
	public void testCollectNewObservation() {
		CbService service = setUpService();
		verify(service).log("cb collecting new observation");

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#buildPressureObservation()}.
	 */
	public void testBuildPressureObservation() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#getSDKVersion()}.
	 */
	public void testGetSDKVersion() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#buildLocalConditionsApiCall()}.
	 */
	public void testBuildLocalConditionsApiCall() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#isNetworkAvailable()}.
	 */
	public void testIsNetworkAvailable() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#stopAutoSubmit()}.
	 */
	public void testStopAutoSubmit() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#sendCbObservation(ca.cumulonimbus.pressurenetsdk.CbObservation)}.
	 */
	public void testSendCbObservation() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#sendCbAccount(ca.cumulonimbus.pressurenetsdk.CbAccount)}.
	 */
	public void testSendCbAccount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#sendCbCurrentCondition(ca.cumulonimbus.pressurenetsdk.CbCurrentCondition)}.
	 */
	public void testSendCbCurrentCondition() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#startSubmit()}.
	 */
	public void testStartSubmit() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#isCharging()}.
	 */
	public void testIsCharging() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#onStartCommand(android.content.Intent, int, int)}.
	 */
	public void testOnStartCommandIntentIntInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#removeAllUninstalledApps()}.
	 */
	public void testRemoveAllUninstalledApps() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#stringTimeToLongHack(java.lang.String)}.
	 */
	public void testStringTimeToLongHack() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#loadSetttingsFromPreferences()}.
	 */
	public void testLoadSetttingsFromPreferences() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#startWithIntent(android.content.Intent, boolean)}.
	 */
	public void testStartWithIntent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#startWithDatabase()}.
	 */
	public void testStartWithDatabase() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#sendSingleObs()}.
	 */
	public void testSendSingleObs() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#deleteOldData()}.
	 */
	public void testDeleteOldData() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#notifyAPIResult(android.os.Messenger, int)}.
	 */
	public void testNotifyAPIResult() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#notifyAPIStats(android.os.Messenger, java.util.ArrayList)}.
	 */
	public void testNotifyAPIStats() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#recentPressureFromDatabase()}.
	 */
	public void testRecentPressureFromDatabase() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#getID()}.
	 */
	public void testGetID() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#setUpFiles()}.
	 */
	public void testSetUpFiles() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#logToFile(java.lang.String)}.
	 */
	public void testLogToFile() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#onBind(android.content.Intent)}.
	 */
	public void testOnBindIntent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#onRebind(android.content.Intent)}.
	 */
	public void testOnRebindIntent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#log(java.lang.String)}.
	 */
	
	public void testLogDebugModeOn() {
		CbService spyService = setUpService();
	
		String msg = "test log message";
		spyService.log(msg);
		verify(spyService).logToFile(msg);   
		//verify(spysysout).println(msg);    
	}
	

	public void testLogDebugModeOff() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#getDataCollector()}.
	 */
	public void testGetDataCollector() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#setDataCollector(ca.cumulonimbus.pressurenetsdk.CbService.CbDataCollector)}.
	 */
	public void testSetDataCollector() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#getLocationManager()}.
	 */
	public void testGetLocationManager() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link ca.cumulonimbus.pressurenetsdk.CbService#setLocationManager(ca.cumulonimbus.pressurenetsdk.CbLocationManager)}.
	 */
	public void testSetLocationManager() {
		fail("Not yet implemented");
	}

}
