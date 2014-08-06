package com.example.android.BluetoothTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.crittercism.app.Crittercism;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;                    
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.example.android.BluetoothTest.GetHttp.OnPost;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothTest extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,OnMarkerClickListener,SensorEventListener{

	/************ DEFINE *************/
	private static final String TAG = "Shooting Game";
	final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	
	// Intent request bluetooth codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	
	// update info from server
	private static final int NO_UPDATE = 0;
	private static final int COMPLETED = 1;
	private static final int UNCOMPLETED = 2;
	
	//role
	static int HUNTER = 1;
	static int TARGET = 2;
	static int NO_PLAY = 3;
	
	/************** FLAG ****************/
	public static Boolean flag_play = false;
	public static Boolean flag_shoot = false;
	Boolean flag_stop_game = false;
	private Boolean flag_win = false;
	Boolean flag_vibra = false;
	Boolean flag_resultbtn = false;
	Boolean flag_channel = false;
	Boolean flag_update = false;
	Boolean flag_first_time_play=false;
	int complete_login = 0;
	
	Boolean flag_firt_update_map = false;
	private boolean localPlayIsPressed = false;
	private boolean remotePlayIsPressed = false;
	boolean isConnected = false;
	public static boolean resetCommandIsTrue = false;
	Boolean zoomMap = false;
	Boolean showInfo = false;
	
    Boolean isTracking = false;
    /****************PARMETER LOGIC*******************/
    int targetId;
	int _startIndex =0;
	int _endIndex = 0;
	int indexInfor =0;
	int role = 0;
	/*****************PARAMETER GRAPHIC*****************/
	public  Button mWithdrawButton;
	public  Button mPlayButton;
	public  Button mMyLocButton;
	static TextView ResultText;
	/*****************PARAMETER DISPLAY******************/
	private TextView mWarningText;	
	private StringBuffer mOutStringBuffer;	
	public BluetoothDevice device;
	String sendMessage="";
	TextView rssi_msg;
	TextView rssi_value;
	GoogleMap mMap;
	MapFragment fm;
	 View myview;
	LocationManager locationManager ;
	LocationListener locationListener;
	double myLong;
	double myLat;
	Marker Target;
	public Marker[] MarkerArr = new Marker[11];
	Boolean[] validMarker = new Boolean[11];
	TextView distanceText[] = new TextView[4];
	TextView targetText[]= new TextView[4];
	TextView newTarget; 
	String address = "";
	public static Handler handle_update,handle_blinkScreen,handle_shooting;
	public Handler handle_info;
	 Vibrator v_target;

	
    /*************PARAMETER SENSOR*****************/
	// sensor member
 	float[] inR = new float[16];
 	float[] I = new float[16];
 	float[] gravity = new float[3];
 	float[] geomag = new float[3];
 	float[] orientVals = new float[3];
 	double azimuth = 0;
 	double pitch = 0;
 	double roll = 0; 	
 	private SensorManager sm;
 	LatLng latLng = null;
 	float degree;
 	Marker myLocation;
 	/*************PARAMETER INFOR PLAYER************/
 	 Boolean[] _stage = new Boolean[11];
	 String[] _detailStage = new String[11];
	 String[] _Long= new String[11];
	 String[] _Lat = new String[11];
	 String[] _BTAddress = new String[11];
	 Boolean[] _Online = new Boolean[11];

	 String _FREE ="free";
	 String _BE_TARGETED ="be_targeted";
	 String _HUNTING ="hunting";
	 
	 Boolean FREE = true;
	 Boolean NOT_FREE = false;
	 
	 int[] LatIndex = new int[11];
	 int[] LogIndex = new int[11];
	 int[] StageIndex = new int[12];
	 int[] BTAddressIndex = new int[11];
	 int[] PosIndex = new int[12];
	 int[] OnlineIndex = new int[11];
	
	 Boolean choseTarget=false;
	 int cntUserId;
	 int cnt_checkTarget;
	 int cnt_blink;
	 /****************PARAMETER BLUETOOTH*****************/
	 private String mConnectedDeviceName = null;
	 public static BluetoothAdapter mBluetoothAdapter = null;
	 public static BluetoothService mChatService = null;
 	
	 public static BluetoothTest instance;
//	 public ConnectWebSocket connecWebSocket;
	 
 	
 	public void initialization(){		
// 		ACRAApplication.getInstance().connecWebSocket = ACRAApplication.getInstance().connecWebSocket;
		//flag
		flag_play = false;
		flag_win = false;
		flag_vibra = false;
		flag_resultbtn = false;
		flag_first_time_play=false;
		isTracking = false;
		ACRAApplication.getInstance().connecWebSocket.flag_proc_mess = false;
		ACRAApplication.getInstance().connecWebSocket.type_state = ACRAApplication.getInstance().connecWebSocket.socNONE;
		flag_firt_update_map = false;
		complete_login = 0;
		
		//bluetooth
		localPlayIsPressed = false;
		remotePlayIsPressed = false;
		isConnected = false;
		resetCommandIsTrue = false;
		
		//map
		zoomMap = false;
		showInfo = false;
		indexInfor =0;
		myLat = new Double(0);
		myLong = new Double(0);
		
		//player
		targetId=0;
		choseTarget=false;
		_startIndex =0;
		_endIndex = 0;		
		role = NO_PLAY;
		
		distanceText[0] = (TextView) findViewById(R.id.distance_1);
		distanceText[1] = (TextView) findViewById(R.id.distance_2);
		distanceText[2] = (TextView) findViewById(R.id.distance_3);
		distanceText[3] = (TextView) findViewById(R.id.distance_4);
		targetText[0]=(TextView)findViewById(R.id.target_1);
		targetText[1]=(TextView)findViewById(R.id.target_2);
		targetText[2]=(TextView)findViewById(R.id.target_3);
		targetText[3]=(TextView)findViewById(R.id.target_4);
		targetText[0].setText("PlayerOther:");
		targetText[1].setText("PlayerOther:");
		targetText[2].setText("PlayerOther:");
		targetText[3].setText("PlayerOther:");
		//variable
		if(mChatService != null){
			mChatService.stop();
			mChatService = null;
		}
	}  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");
		setContentView(R.layout.main);
		// log crash through email
		if(LoginActivity.flag_debug==1)
		{
			Crittercism.initialize(getApplicationContext(),"53b3bb7b07229a5a86000006");
			try {
				throw new Exception("Exception Reason");
			} catch (Exception exception) {
				Crittercism.logHandledException(exception);
			}
		}
		initialization();
		instance = this;
		
		GoogleMapOptions a = new GoogleMapOptions();
    	a.compassEnabled(true);
    	// get infor id, token from file
		SharedPreferences pre=getSharedPreferences("token",MODE_PRIVATE);
		String s_token=pre.getString("token", "");
		String s_id=pre.getString("id", "");		
		LoginActivity.id = Integer.parseInt(s_id);
		LoginActivity.token = s_token;
		if(D)
			Log.e("tokenId","id:"+LoginActivity.id+", token:"+LoginActivity.token);
		
		handle_update = new Handler();
		handle_info = new Handler();
		handle_shooting = new Handler();
		handle_blinkScreen = new Handler();
		
		myview = (View) findViewById(R.id.my_view);
		if(LoginActivity.id>0 && LoginActivity.id <6)
    	{
    		_startIndex = 1;
    		_endIndex = 6;
    	}
    	else if(LoginActivity.id>5 && LoginActivity.id <11)
    	{
    		_startIndex = 6;
    		_endIndex = 11;
    	}
		indexInfor = _startIndex;
		for(int i=_startIndex;i<_endIndex;i++)
    	{
    		validMarker[i]=false;
    		_BTAddress[i]="null";
    		_Lat[i]="null";
    		_Long[i]="null";
    		_Online[i]=false;
    		_detailStage[i]= _FREE;
    		
    	}
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());		 
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show(); 
        }
        else
        {
        	fm = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.mapgame);
        	fm.newInstance(a); 
            // Getting GoogleMap object from the fragment
        	mMap = fm.getMap();        	
        	locationListener = new MyLocationListener();
        	
            // Enabling MyLocation Layer of Google Map
//        	mMap.setMyLocationEnabled(true);
        	mMap.setIndoorEnabled(true);
        	
        	mMap.getUiSettings().setAllGesturesEnabled(true);
        	mMap.getUiSettings().setCompassEnabled(false);
        	
        	mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
        	
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            final Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
	    	Location loc = locationManager.getLastKnownLocation(provider);
	    	 if(loc!=null){
	             locationListener.onLocationChanged(loc);
	         }
	    	 else
	    	 {
	             Toast.makeText(getBaseContext(), "No location found", Toast.LENGTH_SHORT).show();
	    	 }
	         locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	         locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}        
		registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		
		mPlayButton = (Button) findViewById(R.id.button_play);
		mPlayButton.setEnabled(true);
		mPlayButton.setBackgroundColor(0xFFFFFFFF);
		mPlayButton.setVisibility(View.VISIBLE);
		
		mWithdrawButton = (Button)findViewById(R.id.button_withdraw);
		mWithdrawButton.setBackgroundColor(0xFFFFFFFF);
		mWithdrawButton.setVisibility(View.INVISIBLE);
		
//		mMyLocationButton = (Button) findViewById(R.id.button_myLocation);
		
		ResultText = (TextView)findViewById(R.id.textview_result);
		ResultText.setVisibility(View.INVISIBLE);
		
		mMyLocButton = (Button) findViewById(R.id.locbtn);
		mMyLocButton.setBackgroundResource(R.drawable.locbtn);
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mMyLocButton.getLayoutParams();
//		params.width = BluetoothTest.this.findViewById(R.id.locbtn).getWidth();
//        params.height = params.width;                           
//        mMyLocButton.setLayoutParams(params);
		
		
		rssi_msg = (TextView) findViewById(R.id.rssi);
		rssi_value = (TextView) findViewById(R.id.valuerssi);
		
    //compass
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
		
		mMyLocButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CameraUpdate center=
				        CameraUpdateFactory.newLatLng(new LatLng(instance.myLat,
				                                                 instance.myLong));
				mMap.moveCamera(center);
			}
		});
		
	}
	private MapFragment findFragmentById(int mapgame) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		resetCommandIsTrue = false;
		if (!mBluetoothAdapter.isEnabled()) {
	        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	    // Otherwise, setup the chat session
	    } else
		{
			if (mChatService == null){
				Log.e(TAG, "before playGame() menthod");
				playGame();
			}
			btEnsureDiscoverable();
		}

	}

	
	private void btEnsureDiscoverable() {
		// TODO Auto-generated method stub
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			startActivity(discoverableIntent);
		}
	}
	private void playGame() {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "playGame()");
		ACRAApplication.getInstance().connecWebSocket.connect(BluetoothTest.this);
		
		mPlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				{
					if(mPlayButton.getText().equals("Reset"))
					{
						mPlayButton.setText("Play");
						resetCommandIsTrue = true;
						String message = "new session";
						sendMessage = message;
						btSendMessage(message);
						ResultText.setVisibility(View.INVISIBLE);
						if(mWithdrawButton.getVisibility()==View.VISIBLE)
							mWithdrawButton.setVisibility(View.INVISIBLE);
					}
					else
					{
						if(role!=NO_PLAY)
						{
							if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
								Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT)
										.show();
								
								return;
							}
							else
							{
								if(flag_shoot==true)
								{
									
									Log.d("result","result Win");
		    						String message = "You die!";
		    						sendMessage = message;
					                btSendMessage(message);
					                flag_shoot = false;
					                flag_win = true;
					                flag_play = false;
					                targetId = 0;
					                role = NO_PLAY;
					                choseTarget=false;
					                flag_resultbtn = false;
					                
					                mWithdrawButton.setVisibility(View.INVISIBLE);
					                mPlayButton.setText("Reset");
					                mPlayButton.setBackgroundColor(0xFFFFFFFF);
					                handle_shooting.removeCallbacks(updateStateButton);
					                ACRAApplication.getInstance().connecWebSocket.socHit();
								}
								else
									Toast.makeText(getApplicationContext(), "Please wait! Not ready.", Toast.LENGTH_SHORT)
									.show();
							
							}
							
						}
						else
						{
							Toast.makeText(getApplicationContext(), R.string.not_joinGame, Toast.LENGTH_SHORT)
							.show();
						}
					}
				}
			}
		});
		
		
		mWithdrawButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				
                if(LoginActivity.flag_getpost==LoginActivity.HTTP_FREE)
                {
                	LoginActivity.flag_getpost=LoginActivity.HTTP_BUZY;
                	AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothTest.this);
					builder.setMessage("Withdraw a fighting. You will lose.")
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											
											flag_shoot = false;
							                flag_win = true;
							                flag_play = false;
							                
							                resetCommandIsTrue = true;
											String message = "new session";
											sendMessage = message;
											btSendMessage(message);
											targetId = 0;
							                choseTarget=false;
					    					mWithdrawButton.setVisibility(View.INVISIBLE);
					    					setStatus(R.string.title_no_play);
					    					ACRAApplication.getInstance().connecWebSocket.socWithdraw();
										}
									})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
					
                	
                }
			}
		});
		
		// Initialize the BluetoothChatService to perform bluetooth connections
		if(mChatService == null){
			mChatService = new BluetoothService(this, mHandler);
		}
		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
		
	}
	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		ResultText.setVisibility(View.INVISIBLE);
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
    		// Register this class as a listener for the accelerometer sensor
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
		                    SensorManager.SENSOR_DELAY_GAME);
		
//		ACRAApplication.getInstance().connecWebSocket.connect(BluetoothTest.this);
	}
	
	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
		sm.unregisterListener(this);
		
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
		unregisterReceiver(receiver);
	}
	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed() menthod");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								
								if (mChatService.getState() == BluetoothService.STATE_CONNECTED){
									btSendMessage("I press back");
								}
								else {
									mChatService.stop();
								}
								if (mChatService.getState() != BluetoothService.STATE_NONE) {
									Log.e(TAG,
											"----------------- service STOP ------------------");
								}
//								WebSocket.socWithdraw();
								ACRAApplication.getInstance().connecWebSocket.socWithdraw();
								locationManager.removeUpdates(locationListener);;
								targetId = 0;
				                choseTarget=false;
				                flag_channel = false;
				                flag_update = false;
				                flag_vibra = false;
				                isTracking = false;
				                showInfo=false;
				                zoomMap = false;
				                role = NO_PLAY;				                
				                ACRAApplication.getInstance().connecWebSocket.flag_proc_mess=false;
				                			                
								handle_info.removeCallbacks(ShowInfor);
								handle_shooting.removeCallbacks(updateStateButton);	
								handle_blinkScreen.removeCallbacks(blinkingScreen);
								Log.i(TAG, "front finish() menthod");
//								
								ACRAApplication.getInstance().connecWebSocket.mWebSocketClient.close();
								 mBluetoothAdapter.disable();
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}
	private void btSendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);
			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);

		}
	}
	ActionBar actionBar;
	private void setStatus(int resId) {
		actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}
	
	private CharSequence getStatus()
	{
		return actionBar.getSubtitle();
	}

	private void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				playGame();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}
	
	private final BroadcastReceiver receiver = new BroadcastReceiver(){
		   @SuppressWarnings("static-access")
		@Override
		   public void onReceive(Context context, Intent intent) {

		    String action = intent.getAction();
		    if(device.ACTION_FOUND.equals(action)&& role!=NO_PLAY) {
		        int  rssi = intent.getShortExtra(device.EXTRA_RSSI,Short.MIN_VALUE);
			        rssi_value.setText(""+rssi+"dBm");
			        Log.e("rssi","rssi value"+rssi+"dBm");
		    }
		  }
	};
	
	/*****Handler******/
	static Runnable updateStateButton = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("update", "- Update state button -");
			Log.d("update", "targetId"+ instance.targetId);
			if(mChatService!=null)
			{
				Log.d("update", "state bluetooth"+mChatService.getState());
				if ((mChatService.getState() != BluetoothService.STATE_CONNECTED)) {
					instance.mPlayButton.setBackgroundColor(0xFFFFFFFF);
					instance.cnt_checkTarget = 0;
					ResultText.setVisibility(View.INVISIBLE);
				}
				else if((mChatService.getState() == BluetoothService.STATE_CONNECTED))
				{
					Log.e("rssi","rssi discovery");
					mBluetoothAdapter.startDiscovery();
					instance.cnt_checkTarget++;	
					instance.mWithdrawButton.setVisibility(View.VISIBLE);
				}
			}
			if(instance.cnt_checkTarget>3)
			{
				instance.mPlayButton.setBackgroundColor(0xFFFF0000);
				flag_shoot = true;
				instance.cnt_checkTarget = 4;
			}
			handle_shooting.postDelayed(this, 1000);
		}
		
		
	};
	
	// The Handler that gets information back from the BluetoothChatService
		private final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				
				case MESSAGE_STATE_CHANGE:
					if (D)
						Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
					case BluetoothService.STATE_CONNECTED:
//						setStatus(getString(R.string.title_connected_to,
//								mConnectedDeviceName));
						isConnected = true;
						if(role==HUNTER)
						{
							setStatus(R.string.title_target_in);
						}
						else if(role==TARGET)
						{
							setStatus(R.string.title_hunter_in);
						}
						break;
					case BluetoothService.STATE_CONNECTING:
						isConnected = false;
//						setStatus(R.string.title_connecting);
						if(role==HUNTER)
						{
							setStatus(R.string.title_target_out);
						}
						else if(role==TARGET)
						{
							setStatus(R.string.title_hunter_out);
						}

						break;
					case BluetoothService.STATE_LISTEN:
						isConnected = false;
						mWithdrawButton.setVisibility(View.INVISIBLE);
						if(role==HUNTER)
						{
							setStatus(R.string.title_target_out);
						}
						else if(role==TARGET)
						{
							setStatus(R.string.title_hunter_out);
						}
						else
						{
							setStatus(R.string.title_no_play);
						}
						break;
					case BluetoothService.STATE_NONE:
						isConnected = false;
						setStatus(R.string.title_no_play);
						break;
					}
					break;
				case MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					if(sendMessage.equals("You die!") ){
//						 mConversationArrayAdapter.clear();
						 if(flag_win==true)
						 {
//			                mResultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.win));
							ResultText.setBackgroundColor(0x00FFFF00);
							ResultText.setTextColor(Color.parseColor("#ff0000"));
							ResultText.setVisibility(View.VISIBLE);
							ResultText.setText("YOU WIN");
			                flag_win = false;
//			                mChatService.reset();
						 }
					}
					else if(sendMessage.equals("new session")){
						mPlayButton.setText("PLAY");
						mChatService.reset();
						resetCommandIsTrue = false;
					}
		                break;
		            case MESSAGE_READ:
		                byte[] readBuf = (byte[]) msg.obj;
		                // construct a string from the valid bytes in the buffer
		                String readMessage = new String(readBuf, 0, msg.arg1);
		                if(readMessage.equals("You die!") )
		                {
		                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
		                	ResultText.setVisibility(View.VISIBLE);
		                	ResultText.setText("YOU LOST");
		                	ResultText.setTextColor(Color.parseColor("#0f1bb0"));
		                	ResultText.setBackgroundColor(0x0000FFFF);
			                
			                handle_blinkScreen.postDelayed(blinkingScreen, 0);
			                mWithdrawButton.setVisibility(View.INVISIBLE);
			                mPlayButton.setText("Reset");
		                }
		                if (readMessage.equals("new session") )
		                {
		                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
		                	ResultText.setVisibility(View.INVISIBLE);
		                	mWithdrawButton.setVisibility(View.INVISIBLE);
		                	mChatService.reset();
		                	resetCommandIsTrue = false;
		                	mPlayButton.setText("PLAY");
		                	
		                }

		                break;
				case MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(),
							"Connected to " + mConnectedDeviceName,
							Toast.LENGTH_SHORT).show();
					mPlayButton.setText("PLAY");
					mPlayButton.setEnabled(true);
					break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(),
							msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
							.show();
					break;
				}
			}
		};
		
		static Runnable blinkingScreen = new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("update", "- Update state button -");
				Log.e("update", "targetId"+ instance.targetId);
				Log.e("update", "state bluetooth"+mChatService.getState());
				ColorDrawable[] colorBlink = {new ColorDrawable(Color.parseColor("#000000")), new ColorDrawable(Color.parseColor("#FF0000"))};
	            TransitionDrawable trans = new TransitionDrawable(colorBlink);
	            instance.myview.setBackground(trans);
	            trans.startTransition(200);
	            instance.cnt_blink++;
	            if(instance.cnt_blink<5)
	            {
	            	handle_blinkScreen.postDelayed(this, 500);
	            }
	            else 
	            {
	            	instance.cnt_blink=0;
	            	instance.myview.setBackground(null);;
	            	handle_blinkScreen.removeCallbacks(this);
	            }
	            
			}
			
			
		};
		 Runnable ShowInfor = new Runnable(){
	        public void run(){
	        	Log.e("ShowInfor","ShowInfor");
	             //call the service here

				if(indexInfor!=LoginActivity.id)
				{
					if(validMarker[indexInfor])
					{
						MarkerArr[indexInfor].showInfoWindow();
						Log.e("Info","Show infor player"+indexInfor);
					}
				}
				indexInfor++;
				if(indexInfor>(_endIndex-1))
					indexInfor=_startIndex;
				if(mChatService!=null)
				{
					if((mChatService.getState() == BluetoothService.STATE_CONNECTED)&&(flag_play==false))
					{
						handle_shooting.post(updateStateButton);
						rssi_value.setText("updating...");
						flag_play = true;
					}
				}
				if(role != NO_PLAY)
				{
					if((mChatService.getState() != BluetoothService.STATE_CONNECTED))
					{
						rssi_value.setText("Out of range");
					}
				}
				else
				{
					rssi_value.setText("No measure");
					setStatus(R.string.title_no_play);
				}
				
				
				if(flag_resultbtn)
				{
					ResultText.setVisibility(View.INVISIBLE);
				}
				if(role==TARGET && flag_vibra==false)
				{
					Log.e("vibrator","vibrator");
					v_target = (Vibrator) BluetoothTest.this.getSystemService(Context.VIBRATOR_SERVICE);
					 // Vibrate for 1000 milliseconds
					v_target.vibrate(1000);
					flag_vibra = true;
					
					ResultText.setVisibility(View.VISIBLE);
					ResultText.setText("YOU ARE TARGETED");
					ResultText.setTextColor(Color.parseColor("#0f1bb0"));
	                if(BluetoothTest.this.getStatus() == "no play")
	                {
	                	BluetoothTest.this.setStatus(R.string.title_hunter_out);
	                }
	                
	                flag_resultbtn = true;

				}
			
	             ////// set the interval time here
	             handle_info.postDelayed(this,1000);
	        }
	   };
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		degree = Math.round(event.values[0]);
        if(myLocation!=null)
        	myLocation.setRotation(degree);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		if(D)
			Log.d("fight","Fight");
		
		for( cntUserId=_startIndex; cntUserId<_endIndex; cntUserId++)
		{
			Log.d("fight","Fight 1");
			if(arg0.equals(MarkerArr[ cntUserId])){
				Log.d("fight","Fight 2");
				if( choseTarget==false)
				{
					Log.d("fight","BTAddress"+ _BTAddress[ cntUserId]);
					if(!_BTAddress[cntUserId].equals("null"))
					{
						
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("Are you sure you choose player"+ cntUserId+" ?")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												ACRAApplication.getInstance().connecWebSocket.mWebSocketClient.send("{\"type\":\"fight\",\"target\":\""+ cntUserId+"\"}");
												Log.e("socket","send request fight");
												instance.choseTarget = true;
												instance.targetId =  cntUserId;
											}
										})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;
										dialog.cancel();
									}
								});
						AlertDialog alert = builder.create();
						alert.show();				
					
					}
					else
					{
						
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("Format address bluetooth of player"+ cntUserId+"is false! Please choose other!")
								.setCancelable(false)
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;
											}
										});
						AlertDialog alert = builder.create();
						alert.show();	
											
						
					}
					
				}
				else 
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Can't choose other, your target is player"+targetId)
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
					
				}

				break;
			}
		}
		
	
		return true;
		
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	


	

}
 	
	
