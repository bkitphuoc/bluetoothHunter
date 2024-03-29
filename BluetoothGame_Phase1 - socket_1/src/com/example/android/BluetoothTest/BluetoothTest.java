/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.example.android.BluetoothTest.R;
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
GooglePlayServicesClient.OnConnectionFailedListener,OnMarkerClickListener, SensorEventListener{
//	, SensorEventListener
	// Debugging
	private static final String TAG = "Shooting Game";
	private static final boolean D = true;
	// private static final boolean B = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	
	private static final int NO_UPDATE = 0;
	private static final int COMPLETED = 1;
	private static final int UNCOMPLETED = 2;

	// flag
	public static int flag_play = 0;
	public static int flag_shoot = 0;
	public static int flag_play_update=0;
	static int flag_stop_game = 0;
	private int flag_win = 0;
	private boolean localPlayIsPressed = false;
	private boolean remotePlayIsPressed = false;
	boolean isConnected = false;
	public static boolean resetCommandIsTrue = false;
	Boolean zoomMap = false;
	Boolean showInfo = false;
	static int targetId=0;
	static int _startIndex =0;
	static int _endIndex = 0;
	int indexInfor =0;
	static int role = 0;
	static int HUNTER = 1;
	static int TARGET = 2;
	static int NO_PLAY = 3;
	static Boolean flag_vibra = false;
	static Boolean flag_resultbtn = false;
	static Boolean flag_channel = false;
	static Boolean flag_proc_mess = false;
	static Boolean flag_firt_update_map = false;
	
	// variable
	private String mConnectedDeviceName = null;
	private static BluetoothAdapter mBluetoothAdapter = null;
	private static BluetoothService mChatService = null;
	
	private static Button mTestButton,mRoleButton,mBlinkButton;
	private static Button mPlayButton;
	static Button mResultButton;

	private TextView mWarningText;	
	private StringBuffer mOutStringBuffer;	
	private BluetoothDevice device;
	static String sendMessage="";

	TextView rssi_msg;
	TextView rssi_value;
	
	GoogleMap mMap;
	MapFragment fm;
	static View myview;
	LocationManager locationManager ;
	LocationListener locationListener;
	LatLng[] latLngArr = new LatLng[11];
	static double myLong;
	static double myLat;
	Marker Target;
	private Marker[] MarkerArr = new Marker[11];
	Boolean[] validMarker = new Boolean[11];
	LatLng pre_LatLng;
	String address = "";

	
	static GetHttp fightView;
	public static Handler handle_update,handle_blinkScreen,handle_info,handle_shooting,handle_firstTracking;

	TextView distanceText[] = new TextView[4];
	TextView targetText[]= new TextView[4];
	TextView newTarget; 

	
	static Vibrator v_target;


	int socNONE = 0;
	int socLOGIN = 1;
	int socTRACKING = 2;
	int socFIGHT = 3;
	int socHIT = 4;
	int socWITHDRAW = 5;
	int socFAIL = 6;
	private WebSocketClient mWebSocketClient;
    static String message;
    int flag_type = socNONE;
    int flag_login = 0;
    int isTracking = 0;
    int isFirstTracking = 0;
    int isConnectSocket = 0;
    String type_subscribe = "subscribe";
	String type_tracking = "tracking";
	String type_fight ="fight";
	String type_hit="hit";
	String type_withdraw = "withdraw";
	String type_value="";
	
  	public void initialization(){		
		//flag
		flag_play = 0;
		flag_shoot = 0;
		flag_play_update=0;
		flag_win = 0;
		flag_vibra = false;
		flag_resultbtn = false;
		flag_channel = false;
		flag_play_update = NO_UPDATE;
		flag_login = 0;
		flag_type = socNONE;
		flag_proc_mess = false;
		flag_firt_update_map = false;
		
		//bluetooth
		localPlayIsPressed = false;
		remotePlayIsPressed = false;
		isConnected = false;
		resetCommandIsTrue = false;
		isTracking = 0;
		isFirstTracking = 0;
		//map
		zoomMap = false;
		showInfo = false;
		indexInfor =0;
		
		//player
		targetId=0;
		GetHttp.choseTarget=false;
        GetHttp.flag_update = false;
        LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;	
		_startIndex =0;
		_endIndex = 0;		
		role = NO_PLAY;
		
		isConnectSocket = 0;
		MarkerArr = new Marker[11];
		//variable
		if(mChatService != null){
			mChatService.stop();
			mChatService = null;
		}
	}  
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
 	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ACRA.init(this);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");
		// Set up the window layout
		setContentView(R.layout.main);
		
		// initialization
		initialization();
		GoogleMapOptions a = new GoogleMapOptions();
    	a.compassEnabled(true);
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
		if(LoginActivity.flag_debug==1)
		{
			Crittercism.initialize(getApplicationContext(),"53b3bb7b07229a5a86000006");
			try {
				throw new Exception("Exception Reason");
			} catch (Exception exception) {
				Crittercism.logHandledException(exception);
			}
		}
		handle_update = new Handler();
		handle_info = new Handler();
		handle_shooting = new Handler();
		handle_blinkScreen = new Handler();
		handle_firstTracking = new Handler();
		
		myview = (View) findViewById(R.id.my_view);
		String tokenId = "token";       
        
		// get infor id, token from file
		SharedPreferences pre=getSharedPreferences(tokenId,MODE_PRIVATE);
		String s_token=pre.getString("token", "");
		String s_id=pre.getString("id", "");
		
		LoginActivity.id = Integer.parseInt(s_id);
		LoginActivity.token = s_token;
		Log.e("tokenId","id:"+LoginActivity.id+", token:"+LoginActivity.token);
		
    	if(LoginActivity.id>0 && LoginActivity.id <6)
    	{
    		_startIndex = 1;
    		_endIndex = 6;
    		indexInfor = _startIndex;
    	}
    	else if(LoginActivity.id>5 && LoginActivity.id <11)
    	{
    		_startIndex = 6;
    		_endIndex = 11;
    		indexInfor = _startIndex;
    	}
    	for(int i=_startIndex;i<_endIndex;i++)
    	{
    		validMarker[i]=false;
    		GetHttp._Lat[i]="";
			GetHttp._Long[i]="";
			latLngArr[i] = new LatLng(0, 0);
    	}
		// The following line triggers the initialization of ACRA
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		 
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
 
        }
        else
        {
        	//fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapgame);
        	fm = new MapFragment();
        	fm = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.mapgame);
        	fm.newInstance(a); 
            // Getting GoogleMap object from the fragment
        	mMap = fm.getMap();        	
        	locationListener = new MyLocationListener();
        	
            // Enabling MyLocation Layer of Google Map
        	//mMap.setMyLocationEnabled(true);
        	mMap.getUiSettings().setAllGesturesEnabled(true);
        	mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
        	
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            final Criteria criteria = new Criteria();
 
            // Getting the name of the best provider



            {

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

        }
//        pre_LatLng = new LatLng(0,0);
//		 Get local Bluetooth adapter
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
		
		mTestButton = (Button)findViewById(R.id.button_withdraw);
		mTestButton.setBackgroundColor(0xFFFFFFFF);
		mTestButton.setVisibility(View.INVISIBLE);
		
		mResultButton = (Button)findViewById(R.id.button_result);
		mResultButton.setVisibility(View.INVISIBLE);
		
		rssi_msg = (TextView) findViewById(R.id.rssi);
		rssi_value = (TextView) findViewById(R.id.valuerssi);
		
    //compass
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
		

	}
  private MapFragment findFragmentById(int mapgame) {
		// TODO Auto-generated method stub
		return null;
	}
	private void connectWebSocket() {
		// TODO Auto-generated method stub
		Log.e("socket","start");
        URI uri;
        try {
            uri = new URI("ws://54.255.184.201:7778/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
//            	flag_type = socLOGIN;
//        		mWebSocketClient.send("{\"type\":\"subscribe\",\"_token\":\""+LoginActivity.token+"\"}");
            	socLogin();
        		isTracking = 1;
            }

            @Override
            public void onMessage(String s) {
            	if(flag_proc_mess==false)
            	{
            		message = s;
            	}
                runOnUiThread(new Runnable() {
                    @SuppressLint("NewApi")
					public void run() {
                    	Log.e("all","all type"+message);
                    	flag_proc_mess = true;
                    	int temp = 0;
                    	int temp_hunter=0;
                    	int temp_target=0;
                    	int index_type_value = 0;
                    	int index_log_out = message.indexOf("logged out");
                    	if(index_log_out!=-1)
                    	{
                    		int _index = message.indexOf("user_");
                    		int _index1 = message.indexOf("@", _index);
                    		String index_out = message.substring(_index+5, _index1);
                    		Log.e("socket","user_out:"+index_out);
                    		
                    		int _index_out = Integer.parseInt(index_out);
                    		
                    		int j;
                    		if(LoginActivity.id>5)
        					{
            					if(_index_out<LoginActivity.id)
            					{
            						j=_index_out%6;
            					}
            					else
            						j=_index_out%6-1;
        					}
        					else
        					{
        						if(_index_out<LoginActivity.id)
            					{
            						j=_index_out-1;
            					}
            					else
            						j=_index_out-2;
        					}
                    		
                    		validMarker[_index_out] = false;
    	     				targetText[j].setTextColor(Color.parseColor("#ff0000"));
    	     				distanceText[j].setTextColor(Color.parseColor("#ff0000"));
	     					targetText[j].setText("PlayerOther:");
        	     			distanceText[j].setText("Offline");
                    		
                    	}
                    	             	
                    	
                    	int index_type = message.indexOf("type");                    	
                    	index_type_value=message.indexOf("\"",index_type+7);
                    	type_value = message.substring(index_type+7,index_type_value);
                    	if(type_value.equals(type_subscribe))
                    	{
                    		Log.d("socket","Subscribe:"+message);
                    		flag_type = socLOGIN;
                    		String _id_value="";
                    		int _index = message.indexOf("id");
                    		int _index1 = 0;
                    		if(_index!=-1)
                    		{
                    			flag_stop_game = 1;
                    			_index1 = message.indexOf("\"", _index+5);
                    			_id_value = message.substring(_index+5, _index1);
                    			
                    			temp = Integer.parseInt(_id_value);
                    			Log.e("new user","new user:"+temp);
                    			if((temp!=LoginActivity.id)&& (temp>=_startIndex) && (temp<_endIndex))
                    			{
                    				Log.e("subscribe","-----------Enter---------------");
                    				String stage ="stage";
                					String address="bluetooth_address";
                					String pos = "position";
                					
                					int index = message.indexOf(pos);
                					int index1 = 0;
                					
                					if(index!=-1)
                					{
                						GetHttp.PosIndex[temp]=index;
                					}
                					
//                					index = message.indexOf(stage);
//                					if(index!=-1)
//                					{
//                						GetHttp.StageIndex[temp]=index;
//                					}
                					index = message.indexOf(address);
                					if(index!=-1)
                					{
                						GetHttp.BTAddressIndex[temp]=index;
                					}
                					GetHttp._Online[temp]=true;
                					GetHttp._stage[temp] = GetHttp.FREE;
                					
                					if(!message.substring(GetHttp.PosIndex[temp]+10,GetHttp.PosIndex[temp]+14).equals("null"))
    								{
    									
    									index=message.indexOf("\"",GetHttp.PosIndex[temp]+23);
    									GetHttp._Lat[temp]=message.substring(GetHttp.PosIndex[temp]+23,index);
    									index1 = index+15;
    									index = message.indexOf("\"",index1);
    									GetHttp._Long[temp]=message.substring(index1,index);
    									
    									Log.d("post", "Lat_player"+temp+":"+GetHttp._Lat[temp]);
    									Log.d("post", "Long_player"+temp+":"+GetHttp._Long[temp]);
    								}
    								else
    								{
    									GetHttp._Lat[temp]="";
    									GetHttp._Long[temp]="";
    								}
                					
               					
                					if(GetHttp.BTAddressIndex[temp]!=-1)
        							{
        								index=message.indexOf(",",GetHttp.BTAddressIndex[temp]+19);
        								if(!message.substring(GetHttp.BTAddressIndex[temp]+19,index).equals("null"))
        								{
        									index=message.indexOf("\"",GetHttp.BTAddressIndex[temp]+20);
        									Log.d("address","\n BTAddress index"+temp+":"+message.substring(GetHttp.BTAddressIndex[temp]+20,index));
        									GetHttp._BTAddress[temp]=message.substring(GetHttp.BTAddressIndex[temp]+20,index);
        								}
        								else
        								{
        									GetHttp._BTAddress[temp]="null";
        									Log.d("address","\n BTAddress index"+temp+":"+message.substring(GetHttp.BTAddressIndex[temp]+19,index));
        								}
        								Log.d("post", "BTAddress_player"+temp+":"+GetHttp._BTAddress[temp]);
        							}
                    			}
                    			else
                    				{
                    					for(int index=0;index<_endIndex;index++)
                    					{
                    						validMarker[index]=false;
                    					}
                    				
                    				}
                    		}
                    		else
                    		{
                    			flag_stop_game = 0;
                    		}
        					flag_channel = false;
        					GetHttp.flag_update = true;
        					flag_play_update = COMPLETED;
                    	
                     	
                    	}
                    	else if(type_value.equals(type_tracking))
                    	{

                    		Log.d("socket","Tracking:"+message);
                    		flag_type = socTRACKING;
        					String stage ="stage";
        					String address="bluetooth_address";
        					String pos = "position";
        					String onl = "is_online";

        					flag_stop_game = 1;
        					int index = message.indexOf(pos);
        					int index1 = 0;
        					int i = 1;
        					while(index!=-1)
        					{
        						Log.e("status", "++ POSITON ++");
        						GetHttp.PosIndex[i]=index;
        					    Log.d("status","\n position index"+i+":"+index);
        					    index = message.indexOf(pos, index + 1);			    
        					    i++;
        					}
        					index = message.indexOf(stage);
        					i=1;
        					while(index != -1) {
        						Log.e("status", "++ STATUS ++");
        						GetHttp.StageIndex[i]=index;
        					    Log.d("status","\n stage index"+i+":"+index);
        					    index = message.indexOf(stage, index + 1);			    
        					    i++;
        					}
        					index = message.indexOf(address);
        					i=1;
        					while(index != -1) {
        						Log.e("address", "++ Bluetooth Address ++");
        						GetHttp.BTAddressIndex[i]=index;
        					    Log.d("address","\n Bluetooth Address index"+i+":"+index);
        					    index = message.indexOf(address, index + 1);			    
        					    i++;
        					}
        					index = message.indexOf(onl);
        					i=1;
        					while(index != -1) {
        						Log.e("online", "++ Online state ++");
        						GetHttp.OnlineIndex[i]=index;
        					    Log.d("online","\n Online index"+i+":"+index);
        					    index = message.indexOf(onl, index + 1);			    
        					    i++;
        					}
        					for(i=_startIndex;i<_endIndex;i++)
        					{
        						
        						if(i!= LoginActivity.id)
        						{
        							if(GetHttp.OnlineIndex[i]!=-1)
        							{
        								index=message.indexOf("\"",GetHttp.OnlineIndex[i]+12);
        								Log.d("post", "online value"+i+":"+index);
        								Log.d("post", "online value"+i+":"+message.substring(GetHttp.OnlineIndex[i]+12,GetHttp.OnlineIndex[i]+13));
//        								if(!message.substring(GetHttp.BTAddressIndex[i]+12,index).equals("1"))
        								if(message.substring(GetHttp.OnlineIndex[i]+12,GetHttp.OnlineIndex[i]+13).equals("1"))
        								{
        									GetHttp._Online[i]=true;
        								}
        								else
        								{
        									GetHttp._Online[i]=false;
        								}
        								Log.d("post", "online state"+i+":"+GetHttp._Online[i]);
        							}
        						}
        					}
        					for(i=_startIndex;i<_endIndex;i++)
        					{
        						if(i!= LoginActivity.id)
        						{
        							if(GetHttp.PosIndex[i+1]!=-1)
        							{
        								Log.d("status","\n Postion index"+i+":"+message.substring(GetHttp.PosIndex[i+1]+10,GetHttp.PosIndex[i+1]+14));
        								if(!message.substring(GetHttp.PosIndex[i+1]+10,GetHttp.PosIndex[i+1]+14).equals("null"))
        								{
        									
        									index=message.indexOf("\"",GetHttp.PosIndex[i+1]+23);
        									GetHttp._Lat[i]=message.substring(GetHttp.PosIndex[i+1]+23,index);
        									index1 = index+15;
        									index = message.indexOf("\"",index1);
        									GetHttp._Long[i]=message.substring(index1,index);
        									
        									Log.d("post", "Lat_player"+i+":"+GetHttp._Lat[i]);
        									Log.d("post", "Long_player"+i+":"+GetHttp._Long[i]);
        								}
        								else
        								{
        									GetHttp._Lat[i]="";
        									GetHttp._Long[i]="";
        									Log.d("post", "Lat_player"+i+":"+GetHttp._Lat[i]);
        									Log.d("post", "Long_player"+i+":"+GetHttp._Long[i]);
        								}
        								
        							}
        						}
        					}
        					
        				
        					for(i=_startIndex;i<_endIndex;i++)
        					{
//        						if(i!=LoginActivity.id)
        						{
        							if(GetHttp.StageIndex[i]!=-1)
        							{
        								Log.d("status","\n stage index"+i+":"+message.substring(GetHttp.StageIndex[i]+8,GetHttp.StageIndex[i]+12));
        								index = message.indexOf("\"",GetHttp.StageIndex[i]+8);
        								if(message.substring(GetHttp.StageIndex[i]+8,index).equals("free"))
        								{
        									GetHttp._detailStage[i] = GetHttp._FREE;
        									GetHttp._stage[i] = GetHttp.FREE;
        									if(i==LoginActivity.id)
        									{
        										flag_vibra = false;
        										role = NO_PLAY;
        										
        										if(mTestButton.getVisibility()==View.VISIBLE)
        											mTestButton.setVisibility(View.INVISIBLE);
        									}
        									
        									
        								}
        								else if(message.substring(GetHttp.StageIndex[i]+8,index).equals("be_targeted"))
        								{
        									GetHttp._stage[i]=GetHttp.NOT_FREE;
        									GetHttp._detailStage[i] = GetHttp._BE_TARGETED;	
        									if(i==LoginActivity.id)
        									{
        										role = TARGET;
        									}
        								}
        								else if(message.substring(GetHttp.StageIndex[i]+8,index).equals("hunting"))
        								{
        									GetHttp._stage[i]=GetHttp.NOT_FREE;
        									GetHttp._detailStage[i] = GetHttp._HUNTING;
        									if(i==LoginActivity.id)
        									{
        										role = HUNTER;
        									}
        									
        								}
        								Log.d("post", "Stage_player"+i+":"+GetHttp._detailStage[i]);
        							}
        						}
        						
        					}
        					if((GetHttp._stage[LoginActivity.id]==GetHttp.NOT_FREE)&&(flag_play==0))
        					{
        						mTestButton.setVisibility(View.VISIBLE);
        						handle_shooting.post(updateStateButton);
        						flag_play = 1;
        					}
        					
        					for(i=_startIndex;i<_endIndex;i++)
        					{
        						
        						if(i!= LoginActivity.id)
        						{
        							if(GetHttp.BTAddressIndex[i]!=-1)
        							{
        								index=message.indexOf(",",GetHttp.BTAddressIndex[i]+19);
        								if(!message.substring(GetHttp.BTAddressIndex[i]+19,index).equals("null"))
        								{
        									index=message.indexOf("\"",GetHttp.BTAddressIndex[i]+20);
        									Log.d("address","\n BTAddress index"+i+":"+message.substring(GetHttp.BTAddressIndex[i]+20,index));
        									GetHttp._BTAddress[i]=message.substring(GetHttp.BTAddressIndex[i]+20,index);
        								}
        								else
        								{
        									GetHttp._BTAddress[i]="null";
        									Log.d("address","\n BTAddress index"+i+":"+message.substring(GetHttp.BTAddressIndex[i]+19,index));
        								}
        								Log.d("post", "BTAddress_player"+i+":"+GetHttp._BTAddress[i]);
        							}
        						}
        					}
        					flag_channel = false;
        					GetHttp.flag_update = true;
        					flag_play_update = COMPLETED;
        				
                    	
                    	}
                    	else if((type_value.equals(type_fight)))
                    	{
                    		Log.d("socket","hunter target:"+message);
                    		flag_type = socFIGHT;
                    		String _id_value="";
                    		int _index = message.indexOf("hunter");
                    		int _index1 = 0;
                    		if(_index!=-1)
                    		{

                    			if((GetHttp.choseTarget == true))
                    			{
                    				
									targetId = GetHttp.cntUserId;
									Log.d("fight","target id:"+targetId);
									device = mBluetoothAdapter.getRemoteDevice(GetHttp._BTAddress[GetHttp.cntUserId]);
//									if(device.getType()==device.DEVICE_TYPE_CLASSIC)
									mChatService.connect(device, true);		
									rssi_value.setText("Out of range");
									role=HUNTER;
									mTestButton.setVisibility(View.VISIBLE);
									Toast.makeText(getBaseContext(),"Your target is chosen: player"+targetId, Toast.LENGTH_LONG).show(); 
									sm.registerListener(BluetoothTest.this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
						                    SensorManager.SENSOR_DELAY_GAME);
                    			}
                    			flag_stop_game = 1;
                    			_index1 = message.indexOf("\"", _index+15);
                    			Log.d("hunter","id:"+message.substring(_index+15, _index1));
                    			_id_value = message.substring(_index+15, _index1);
                    			try{
                    				temp_hunter = Integer.parseInt(_id_value);
                    			}
                    			catch(NumberFormatException e)
                    			{
                    				Log.d("target","id:"+message.substring(_index+15, _index1));
                    			}
                    			
                    			_index = message.indexOf("target");
	                    		if(_index!=-1)
	                    		{
	                    			_index1 = message.indexOf("\"", _index+15);
	                    			Log.d("target","id:"+message.substring(_index+15, _index1));
	                    			_id_value = message.substring(_index+15, _index1);
	                    			try{
	                    				temp_target = Integer.parseInt(_id_value);
	                    			}
	                    			catch(NumberFormatException e)
	                    			{
	                    				Log.d("target","id:"+message.substring(_index+15, _index1));
	                    			}
	                    		}
	                    		if(temp_target>=_startIndex && temp_target<_endIndex)
	                    		{
	                    			String pos = "position";
		        					
		        					_index = message.indexOf(pos);
		        					_index1 = 0;
		
		        					if(_index!=-1)
		        					{
		        						GetHttp.PosIndex[temp_hunter]=_index;
		        					}
		        					_index = message.indexOf(pos, _index+1);
		        					if(_index!=-1)
		        						GetHttp.PosIndex[temp_target]=_index;
		        					
		        					GetHttp._Online[temp_hunter]=true;
		        					GetHttp._Online[temp_target]=true;
		        					
	        						GetHttp._stage[temp_hunter] = GetHttp.NOT_FREE;
		        					GetHttp._stage[temp_target] = GetHttp.NOT_FREE;
		        					GetHttp._detailStage[temp_hunter]=GetHttp._HUNTING;
		        					GetHttp._detailStage[temp_target]=GetHttp._BE_TARGETED;
									
		        					if(!message.substring(GetHttp.PosIndex[temp_hunter]+10,GetHttp.PosIndex[temp_hunter]+14).equals("null"))
									{
										
										_index=message.indexOf("\"",GetHttp.PosIndex[temp_hunter]+23);
										GetHttp._Lat[temp_hunter]=message.substring(GetHttp.PosIndex[temp_hunter]+23,_index);
										_index1 = _index+15;
										_index = message.indexOf("\"",_index1);
										GetHttp._Long[temp_hunter]=message.substring(_index1,_index);
										
										Log.d("post", "Lat_player"+temp_hunter+":"+GetHttp._Lat[temp_hunter]);
										Log.d("post", "Long_player"+temp_hunter+":"+GetHttp._Long[temp_hunter]);
									}
									else
									{
										GetHttp._Lat[temp_hunter]="";
										GetHttp._Long[temp_hunter]="";
									}
		        					
		        					if(!message.substring(GetHttp.PosIndex[temp_target]+10,GetHttp.PosIndex[temp_target]+14).equals("null"))
									{
										
										_index=message.indexOf("\"",GetHttp.PosIndex[temp_target]+23);
										GetHttp._Lat[temp_target]=message.substring(GetHttp.PosIndex[temp_target]+23,_index);
										_index1 = _index+15;
										_index = message.indexOf("\"",_index1);
										GetHttp._Long[temp_target]=message.substring(_index1,_index);
										
										Log.d("post", "Lat_player"+temp_target+":"+GetHttp._Lat[temp_target]);
										Log.d("post", "Long_player"+temp_target+":"+GetHttp._Long[temp_target]);
									}
									else
									{
										GetHttp._Lat[temp_target]="";
										GetHttp._Long[temp_target]="";
									}
		        					
		        					
		        					validMarker[temp_hunter]=true;
		        					validMarker[temp_target]=true;
	                    		}
	        					
                    		}
                    		else
                    		{
                    			if(targetId == GetHttp.cntUserId)
                    			{
                    				GetHttp.choseTarget = false;
                    				targetId = 0;
                    			}
                    			flag_stop_game = 0;
                    		}
        					
        					flag_channel = false;
        					GetHttp.flag_update = true;
        					flag_play_update = COMPLETED;
                    		
                    		
                    	}
                    	else if((type_value.equals(type_hit))||(type_value.equals(type_withdraw)))
                    	{
                    		Log.d("socket","hunter target:"+message);
                    		
                    		flag_type = socWITHDRAW;
                    		String _id_value="";
                    		int _index = message.indexOf("hunter");
                    		int _index1 = 0;
                    		if(_index!=-1)
                    		{
                    			
                    			flag_stop_game = 1;
                    			_index1 = message.indexOf("\"", _index+15);
                    			Log.d("hunter","id:"+message.substring(_index+15, _index1));
                    			_id_value = message.substring(_index+15, _index1);
                    			try{
                    				temp_hunter = Integer.parseInt(_id_value);
                    			}
                    			catch(NumberFormatException e)
                    			{
                    				Log.d("target","id:"+message.substring(_index+15, _index1));
                    			}
                    			
                    			_index = message.indexOf("target");
	                    		if(_index!=-1)
	                    		{
	                    			_index1 = message.indexOf("\"", _index+15);
	                    			Log.d("target","id:"+message.substring(_index+15, _index1));
	                    			_id_value = message.substring(_index+15, _index1);
	                    			try{
	                    				temp_target = Integer.parseInt(_id_value);
	                    			}
	                    			catch(NumberFormatException e)
	                    			{
	                    				Log.d("target","id:"+message.substring(_index+15, _index1));
	                    			}
	                    		}
	                    		if(temp_target>=_startIndex && temp_target<_endIndex)
	                    		{
	                    			String pos = "position";
		        					
		        					_index = message.indexOf(pos);
		        					_index1 = 0;
		
		        					if(_index!=-1)
		        					{
		        						GetHttp.PosIndex[temp_hunter]=_index;
		        					}
		        					_index = message.indexOf(pos, _index+1);
		        					if(_index!=-1)
		        						GetHttp.PosIndex[temp_target]=_index;
		        					
		        					GetHttp._Online[temp_hunter]=true;
		        					GetHttp._Online[temp_target]=true;
		        					
			        					GetHttp._stage[temp_hunter] = GetHttp.FREE;
			        					GetHttp._stage[temp_target] = GetHttp.FREE;
		        					
		        					
		        					if(!message.substring(GetHttp.PosIndex[temp_hunter]+10,GetHttp.PosIndex[temp_hunter]+14).equals("null"))
									{
										
										_index=message.indexOf("\"",GetHttp.PosIndex[temp_hunter]+23);
										GetHttp._Lat[temp_hunter]=message.substring(GetHttp.PosIndex[temp_hunter]+23,_index);
										_index1 = _index+15;
										_index = message.indexOf("\"",_index1);
										GetHttp._Long[temp_hunter]=message.substring(_index1,_index);
										
										Log.d("post", "Lat_player"+temp_hunter+":"+GetHttp._Lat[temp_hunter]);
										Log.d("post", "Long_player"+temp_hunter+":"+GetHttp._Long[temp_hunter]);
									}
									else
									{
										GetHttp._Lat[temp_hunter]="";
										GetHttp._Long[temp_hunter]="";
									}
		        					
		        					if(!message.substring(GetHttp.PosIndex[temp_target]+10,GetHttp.PosIndex[temp_target]+14).equals("null"))
									{
										
										_index=message.indexOf("\"",GetHttp.PosIndex[temp_target]+23);
										GetHttp._Lat[temp_target]=message.substring(GetHttp.PosIndex[temp_target]+23,_index);
										_index1 = _index+15;
										_index = message.indexOf("\"",_index1);
										GetHttp._Long[temp_target]=message.substring(_index1,_index);
										
										Log.d("post", "Lat_player"+temp_target+":"+GetHttp._Lat[temp_target]);
										Log.d("post", "Long_player"+temp_target+":"+GetHttp._Long[temp_target]);
									}
									else
									{
										GetHttp._Lat[temp_target]="";
										GetHttp._Long[temp_target]="";
									}
		        					
		        					
		        					validMarker[temp_hunter]=true;
		        					validMarker[temp_target]=true;
	                    		}
	        					
                    		}
                    		else
                    		{
                    			if(targetId == GetHttp.cntUserId)
                    			{
                    				GetHttp.choseTarget = false;
                    				targetId = 0;
                    			}
                    			flag_stop_game = 0;
                    		}
        					
        					flag_channel = false;
        					GetHttp.flag_update = true;
        					flag_play_update = COMPLETED;
                    	
                     	
                    	
                    	}
                    	
                    	//update state marker, distance
            			Location locationA = new Location("point A");
            			locationA.setLongitude(myLong);
            			locationA.setLatitude(myLat);
            			Location locationB = new Location("point B");
            			float distance[] = new float[4];
            			
            			BitmapDescriptor bitmapDesFree = BitmapDescriptorFactory.fromResource(R.drawable.pin_blue_bg_big);
            			BitmapDescriptor bitmapDesNotFree = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);
//            	        BitmapDescriptor bitmapDesFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
//            	        BitmapDescriptor bitmapDesNotFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            	        Log.d("update", "+ Flag update:"+GetHttp.flag_update);
            	        
            	        if(GetHttp.flag_update==true)
            	        {

            	        	if(flag_stop_game==1)
            	        	{
	            	        	mMap.clear();
	            	        	myLocation = mMap.addMarker(new MarkerOptions()
	            		        .position(new LatLng(myLat,myLong))
	            		        .title("me")
	            		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_arrow_blue)));
	            	        	
	            	        	
	            	        	
	            	        	if(flag_type==socLOGIN)
	            	        	{
	            	        		Log.d("index","index"+temp);
	            	        		if((temp!=LoginActivity.id)&&(temp>=_startIndex)&&(temp<_endIndex))
	            	        		{
	            	        			try
	            	    	        	{
	            	        				
	            	        				//if(((!GetHttp._Lat[temp].equals("null"))&&(GetHttp._Lat[temp]!=null)))
	            	        				if(GetHttp._Lat[temp].length()>0)
	            	        				{
	            	        					latLngArr[temp]= new LatLng(Double.parseDouble(GetHttp._Lat[temp]),Double.parseDouble(GetHttp._Long[temp]));
	            	        				}
	            	        				else
	            	        					latLngArr[temp]= new LatLng(0,0);
	            	    	        	}
	            	    	        	catch(NumberFormatException e)
	            	    	        	{
	            	    	        		latLngArr[temp]= new LatLng(0,0);
	            	    	        	}
	            	        			
	            	        		}

	            	        		int j = 0;
	            	        		if(temp!=LoginActivity.id)
	            	        		{
	            						if(LoginActivity.id>5)
		            					{
			            					if(temp<LoginActivity.id)
			            					{
			            						j=temp%6;
			            					}
			            					else
			            						j=temp%6-1;
		            					}
		            					else
		            					{
		            						if(temp<LoginActivity.id)
			            					{
			            						j=temp-1;
			            					}
			            					else
			            						j=temp-2;
		            					}
	            	        		}
	            	        		// update marker
	            	        		if((temp!=LoginActivity.id)&&(temp>=_startIndex)&&
	            	     					(temp<_endIndex)&&(GetHttp._Online[temp]==true))
	            					{
	            						if(GetHttp._stage[temp]==GetHttp.FREE)
	            				        {
	            							if(!latLngArr[temp].equals(new LatLng(0,0)))
	            							{
	            							
	            								if(temp==targetId)
	            								{
	            									targetId = 0;
	            									GetHttp.choseTarget = false;
	            								}
	            						        MarkerArr[temp]= mMap.addMarker(new MarkerOptions()
	            						        .position(latLngArr[temp])
	            						        .icon(bitmapDesFree)
	            						        .title("player"+temp));
	            						        MarkerArr[temp].showInfoWindow();
	            						        
	            						        validMarker[temp] = true;
	            							}
	            							else
	            								validMarker[temp] = false;
	            							
	            							targetText[j].setTextColor(Color.parseColor("#0000ff"));
        									targetText[j].setText("Player"+temp+":");
        									locationB.setLatitude(latLngArr[temp].latitude);
        							        locationB.setLongitude(latLngArr[temp].longitude);
        							        Log.e("MyLocation",myLong + "    " + myLat);
        							        Log.e("OtherLocation",latLngArr[temp].longitude + "    " + latLngArr[temp].latitude);
        							        distance[j] = locationA.distanceTo(locationB);
        							        distanceText[j].setTextColor(Color.parseColor("#0000ff"));
        							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
        							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
        							        
        							        if(LoginActivity.id == temp)
        							        {
        							        	pre_LatLng = new LatLng(0,0);
        							        }
	            							
	            				        }
	            				        else
	            				        {
	            				        	if(!latLngArr[temp].equals(new LatLng(0,0)))
	            				        	{
	            					        	MarkerArr[temp]=mMap.addMarker(new MarkerOptions()
	            						        .position(latLngArr[temp])
	            						        .icon(bitmapDesNotFree)
	            						        .title("player"+temp));
	            					        	MarkerArr[temp].showInfoWindow();
	            					        	validMarker[temp] = true;
	            				        	}
	            				        	else
	            				        		validMarker[temp] = false;
	            				        	
	            				        	targetText[j].setTextColor(Color.parseColor("#ff0000"));
        									targetText[j].setText("Player"+temp+":");
        									locationB.setLatitude(latLngArr[temp].latitude);
        							        locationB.setLongitude(latLngArr[temp].longitude);
        							        Log.e("MyLocation",myLong + "    " + myLat);
        							        Log.e("OtherLocation",latLngArr[temp].longitude + "    " + latLngArr[temp].latitude);
        							        distance[j] = locationA.distanceTo(locationB);
        							        distanceText[j].setTextColor(Color.parseColor("#ff0000"));
        							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
        							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
	            				        	
	            					        
	            				        }

	            						
	            					}
	            	     			if(showInfo==false)
	            	     			{
	            	     				showInfo = true;
	            	     				handle_info.postDelayed(ShowInfor, 0);
	            	     				Log.e("Info","Show infor");
	            	     			}
	            	        	
	            	        	}
	            	        	else if((flag_type==socFIGHT) || (flag_type==socHIT) || (flag_type==socWITHDRAW))
	            	        	{
	            	        		if((temp_hunter!=LoginActivity.id)&&(temp_hunter>=_startIndex)&&(temp_hunter<_endIndex))
	            	        		{
	            	        			try
	            	    	        	{
//	            	        				
//	            	        				if(((!GetHttp._Lat[temp_hunter].equals("null"))&&(GetHttp._Lat[temp_hunter]!=null)))
	            	        				if(GetHttp._Lat[temp_hunter].length()>0)
	            	        				{
	            	        					latLngArr[temp_hunter]= new LatLng(Double.parseDouble(GetHttp._Lat[temp_hunter]),Double.parseDouble(GetHttp._Long[temp_hunter]));
	            	        				}
	            	        				else
	            	        					latLngArr[temp_hunter]= new LatLng(0,0);
	            	    	        	}
	            	    	        	catch(NumberFormatException e)
	            	    	        	{
	            	    	        	  //not a double
	            	    	        		latLngArr[temp_hunter]= new LatLng(0,0);
	            	    	        	}
	            	        			
	            	        		}
	            	        		if((temp_target!=LoginActivity.id)&&(temp_target>=_startIndex)&&(temp_target<_endIndex))
	            	        		{
	            	        			try
	            	    	        	{
//	            	        				
	            	        				//if(((!GetHttp._Lat[temp_target].equals("null"))&&(GetHttp._Lat[temp_target]!=null)))
	            	        				if(GetHttp._Lat[temp_target].length()>0)
	            	        				{
	            	        					latLngArr[temp_target]= new LatLng(Double.parseDouble(GetHttp._Lat[temp_target]),Double.parseDouble(GetHttp._Long[temp_target]));
	            	        				}
	            	        				else
	            	        					latLngArr[temp_target]= new LatLng(0,0);
	            	    	        	}
	            	    	        	catch(NumberFormatException e)
	            	    	        	{
	            	    	        	  //not a double
	            	    	        		latLngArr[temp_target]= new LatLng(0,0);
	            	    	        	}
	            	        			
	            	        		}
	            	        		if(flag_type==socFIGHT)
	            	        		{

		            	        		int j=0;
		            	        		if(LoginActivity.id>5)
		            					{
			            					if(temp_hunter<LoginActivity.id)
			            					{
			            						j=temp_hunter%6;
			            					}
			            					else
			            						j=temp_hunter%6-1;
		            					}
		            					else
		            					{
		            						if(temp_hunter<LoginActivity.id)
			            					{
			            						j=temp_hunter-1;
			            					}
			            					else
			            						j=temp_hunter-2;
		            					}
		            	        		
		            	        		if((temp_hunter!=LoginActivity.id)&&(temp_hunter>=_startIndex)&&
		            	     					(temp_hunter<_endIndex)&&(GetHttp._Online[temp_hunter]==true))
		            					{
		            						
		            				        {
		            				        	if(!latLngArr[temp_hunter].equals(new LatLng(0,0)))
		            				        	{
		            					        	MarkerArr[temp_hunter]=mMap.addMarker(new MarkerOptions()
		            						        .position(latLngArr[temp_hunter])
		            						        .icon(bitmapDesNotFree)
		            						        .title("player"+temp_hunter));
		            					        	MarkerArr[temp_hunter].showInfoWindow();
		            					        	validMarker[temp_hunter] = true;
		            				        	}
		            				        	else
		            				        		validMarker[temp_hunter] = false;
		            				        	
		            					        
		            				        }
		            						
		            						
		            						{
		            							if(!latLngArr[temp_hunter].equals(new LatLng(0,0)))
		            							{
		            								
		            								{
		            									targetText[j].setTextColor(Color.parseColor("#ff0000"));
		            									targetText[j].setText("Player"+temp_hunter+":");
		            									locationB.setLatitude(latLngArr[temp_hunter].latitude);
		            							        locationB.setLongitude(latLngArr[temp_hunter].longitude);
		            							        Log.e("MyLocation",myLong + "    " + myLat);
		            							        Log.e("OtherLocation",latLngArr[temp_hunter].longitude + "    " + latLngArr[temp_hunter].latitude);
		            							        distance[j] = locationA.distanceTo(locationB);
		            							        distanceText[j].setTextColor(Color.parseColor("#ff0000"));
		            							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
		            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
		            								}
		            							}
		            						}
		            					}
		            	        	

		            	        		if(LoginActivity.id>5)
		            					{
			            					if(temp_target<LoginActivity.id)
			            					{
			            						j=temp_target%6;
			            					}
			            					else
			            						j=temp_target%6-1;
		            					}
		            					else
		            					{
		            						if(temp_target<LoginActivity.id)
			            					{
			            						j=temp_target-1;
			            					}
			            					else
			            						j=temp_target-2;
		            					}
		            	        		
		            	        		if((temp_target!=LoginActivity.id)&&(temp_target>=_startIndex)&&
		            	     					(temp_target<_endIndex)&&(GetHttp._Online[temp_target]==true))
		            					{
		            						
		            				        {
		            				        	if(!latLngArr[temp_target].equals(new LatLng(0,0)))
		            				        	{
		            					        	MarkerArr[temp_target]=mMap.addMarker(new MarkerOptions()
		            						        .position(latLngArr[temp_target])
		            						        .icon(bitmapDesNotFree)
		            						        .title("player"+temp_target));
		            					        	MarkerArr[temp_target].showInfoWindow();
		            					        	validMarker[temp_target] = true;
		            				        	}
		            				        	else
		            				        		validMarker[temp_target] = false;
		            				        	
		            					        
		            				        }
		            						
		            						
		            						
		            							if(!latLngArr[temp_target].equals(new LatLng(0,0)))
		            							{
		            								
		            								{
		            									targetText[j].setTextColor(Color.parseColor("#ff0000"));
		            									targetText[j].setText("Player"+temp_target+":");
		            									locationB.setLatitude(latLngArr[temp_target].latitude);
		            							        locationB.setLongitude(latLngArr[temp_target].longitude);
		            							        Log.e("MyLocation",myLong + "    " + myLat);
		            							        Log.e("OtherLocation",latLngArr[temp_target].longitude + "    " + latLngArr[temp_target].latitude);
		            							        distance[j] = locationA.distanceTo(locationB);
		            							        distanceText[j].setTextColor(Color.parseColor("#ff0000"));
		            							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
		            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
		            								}
		            							}
		            						
		            					}
		            	     			if(showInfo==false)
		            	     			{
		            	     				showInfo = true;
		            	     				handle_info.postDelayed(ShowInfor, 0);
		            	     				Log.e("Info","Show infor");
		            	     			}
		            	     			flag_proc_mess = true;
	            	        		}
	            	        		else if((flag_type==socHIT) || (flag_type==socWITHDRAW))
	            	        		{


		            	        		int j=0;
		            	        		if(LoginActivity.id>5)
		            					{
			            					if(temp_hunter<LoginActivity.id)
			            					{
			            						j=temp_hunter%6;
			            					}
			            					else
			            						j=temp_hunter%6-1;
		            					}
		            					else
		            					{
		            						if(temp_hunter<LoginActivity.id)
			            					{
			            						j=temp_hunter-1;
			            					}
			            					else
			            						j=temp_hunter-2;
		            					}
		            	        		
		            	        		if((temp_hunter!=LoginActivity.id)&&(temp_hunter>=_startIndex)&&
		            	     					(temp_hunter<_endIndex)&&(GetHttp._Online[temp_hunter]==true))
		            					{
		            						
		            				        {
		            				        	if(!latLngArr[temp_hunter].equals(new LatLng(0,0)))
		            				        	{
		            					        	MarkerArr[temp_hunter]=mMap.addMarker(new MarkerOptions()
		            						        .position(latLngArr[temp_hunter])
		            						        .icon(bitmapDesFree)
		            						        .title("player"+temp_hunter));
		            					        	MarkerArr[temp_hunter].showInfoWindow();
		            					        	validMarker[temp_hunter] = true;
		            					        	
		            					        	targetText[j].setTextColor(Color.parseColor("#0000ff"));
	            									targetText[j].setText("Player"+temp_hunter+":");
	            									locationB.setLatitude(latLngArr[temp_hunter].latitude);
	            							        locationB.setLongitude(latLngArr[temp_hunter].longitude);
	            							        Log.e("MyLocation",myLong + "    " + myLat);
	            							        Log.e("OtherLocation",latLngArr[temp_hunter].longitude + "    " + latLngArr[temp_hunter].latitude);
	            							        distance[j] = locationA.distanceTo(locationB);
	            							        distanceText[j].setTextColor(Color.parseColor("#0000ff"));
	            							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
	            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
		            				        	}
		            				        	else
		            				        		validMarker[temp_hunter] = false;
		            				        	
		            					        
		            				        }            						
		            						
		            						
		            					}
		            	        	

		            	        		if(LoginActivity.id>5)
		            					{
			            					if(temp_target<LoginActivity.id)
			            					{
			            						j=temp_target%6;
			            					}
			            					else
			            						j=temp_target%6-1;
		            					}
		            					else
		            					{
		            						if(temp_target<LoginActivity.id)
			            					{
			            						j=temp_target-1;
			            					}
			            					else
			            						j=temp_target-2;
		            					}
		            	        		
		            	        		if((temp_target!=LoginActivity.id)&&(temp_target>=_startIndex)&&
		            	     					(temp_target<_endIndex)&&(GetHttp._Online[temp_target]==true))
		            					{
		            						
		            				        {
		            				        	if(!latLngArr[temp_target].equals(new LatLng(0,0)))
		            				        	{
		            					        	MarkerArr[temp_target]=mMap.addMarker(new MarkerOptions()
		            						        .position(latLngArr[temp_target])
		            						        .icon(bitmapDesFree)
		            						        .title("player"+temp_target));
		            					        	MarkerArr[temp_target].showInfoWindow();
		            					        	validMarker[temp_target] = true;
		            					        	
		            					        	targetText[j].setTextColor(Color.parseColor("#0000ff"));
	            									targetText[j].setText("Player"+temp_target+":");
	            									locationB.setLatitude(latLngArr[temp_target].latitude);
	            							        locationB.setLongitude(latLngArr[temp_target].longitude);
	            							        Log.e("MyLocation",myLong + "    " + myLat);
	            							        Log.e("OtherLocation",latLngArr[temp_target].longitude + "    " + latLngArr[temp_target].latitude);
	            							        distance[j] = locationA.distanceTo(locationB);
	            							        distanceText[j].setTextColor(Color.parseColor("#0000ff"));
	            							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
	            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
		            				        	}
		            				        	else
		            				        		validMarker[temp_target] = false;
		            				        }
		            					}
		            	     			if(showInfo==false)
		            	     			{
		            	     				showInfo = true;
		            	     				handle_info.postDelayed(ShowInfor, 0);
		            	     				Log.e("Info","Show infor");
		            	     			}
		            	     			flag_proc_mess = true;
	            	        		
	            	        		}
	            	        	}
	            	        	else if(flag_type==socTRACKING)
	            	        	{
	            	        		int j=0;
	            	        		for(int index=_startIndex;index<_endIndex;index++)
	            	        		{
	            	        			if(LoginActivity.id>5)
		            					{
			            					if(index<LoginActivity.id)
			            					{
			            						j=index%6;
			            					}
			            					else
			            						j=index%6-1;
		            					}
		            					else
		            					{
		            						if(index<LoginActivity.id)
			            					{
			            						j=index-1;
			            					}
			            					else
			            						j=index-2;
		            					}
	            	        			
            	        				
		            	        		if((index!=LoginActivity.id)&&(index>=_startIndex)&&
		            	     					(index<_endIndex)&&(GetHttp._Online[index]==true))
		            					{
		            	        			
		            	        			if(GetHttp._Lat[index].length()>0)
	            	        				{
		            	        				
		            	        				Log.e("location","index:"+index+
	            	        							"lat:"+GetHttp._Lat[index]+",long:"+GetHttp._Long[index]);
	            	        					latLngArr[index]= new LatLng(Double.parseDouble(GetHttp._Lat[index]),Double.parseDouble(GetHttp._Long[index]));
		            	        				
	            	        				}
	            	        				else
	            	        				{
	            	        					Log.e("location"," not value index:"+index+
	            	        							"lat:"+GetHttp._Lat[index]+",long:"+GetHttp._Long[index]);
	            	        					latLngArr[index]= new LatLng(0,0);
	            	        					
	            	        				}
		            	        			
		            						if(GetHttp._stage[index]==GetHttp.FREE)
		            				        {
		            							Log.e("bug","index"+index);
		            							Log.e("bug","Lat"+latLngArr[index].latitude+"Log"+latLngArr[index].longitude);
		            				        	if(latLngArr[index].latitude!=0.0f ||latLngArr[index]!=null)
		            				        	{
		            					        	MarkerArr[index]=mMap.addMarker(new MarkerOptions()
		            						        .position(latLngArr[index])
		            						        .icon(bitmapDesFree)
		            						        .title("player"+index));
		            					        	MarkerArr[index].showInfoWindow();
		            					        	validMarker[index] = true;
		            					        	
		            					        	targetText[j].setTextColor(Color.parseColor("#0000ff"));
	            									targetText[j].setText("Player"+index+":");
	            									locationB.setLatitude(latLngArr[index].latitude);
	            							        locationB.setLongitude(latLngArr[index].longitude);
	            							        Log.e("MyLocation",myLong + "    " + myLat);
	            							        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
	            							        distance[j] = locationA.distanceTo(locationB);
	            							        distanceText[j].setTextColor(Color.parseColor("#0000ff"));
	            							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
	            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
		            				        	}
		            				        	else
		            				        		validMarker[index] = false;
		            				        	
		            				        	
		            					        
		            				        }
		            						else
		            				        {
		            							Log.e("bug","index"+index);
		            							Log.e("bug","Lat"+latLngArr[index].latitude+"Log"+latLngArr[index].longitude);
		            				        	if(!latLngArr[index].equals(new LatLng(0,0))||latLngArr[index]!=null)
		            				        	{
		            					        	MarkerArr[index]=mMap.addMarker(new MarkerOptions()
		            						        .position(latLngArr[index])
		            						        .icon(bitmapDesNotFree)
		            						        .title("player"+index));
		            					        	MarkerArr[index].showInfoWindow();
		            					        	validMarker[index] = true;
		            					        	
		            					        	targetText[j].setTextColor(Color.parseColor("#ff0000"));
	            									targetText[j].setText("Player"+index+":");
	            									locationB.setLatitude(latLngArr[index].latitude);
	            							        locationB.setLongitude(latLngArr[index].longitude);
	            							        Log.e("MyLocation",myLong + "    " + myLat);
	            							        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
	            							        distance[j] = locationA.distanceTo(locationB);
	            							        distanceText[j].setTextColor(Color.parseColor("#ff0000"));
	            							        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
	            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
		            				        	}
		            				        	else
		            				        		validMarker[index] = false;
		            				        	
		            				        	
		            					        
		            				        }
	            							
		            						
		            					}
		            	        		else if((index!=LoginActivity.id)&&(index>=_startIndex)&&
		            	     					(index<_endIndex)&&(GetHttp._Online[index]==false))
		            	        		{
		            	        			validMarker[index] = false;
		            	     				targetText[j].setTextColor(Color.parseColor("#ff0000"));
		            	     				distanceText[j].setTextColor(Color.parseColor("#ff0000"));
	            	     					targetText[j].setText("PlayerOther:");
			            	     			distanceText[j].setText("Offline");
		            	        		}
	            	        		}
	            	        		if(showInfo==false)
	            	     			{
	            	     				showInfo = true;
	            	     				handle_info.postDelayed(ShowInfor, 0);
	            	     				Log.e("Info","Show infor");
	            	     			}
	            	        		flag_proc_mess = true;
	            	        	
	            	        	}
            	        	}
            	        	else
            	        	{
            	        		if(type_value==type_fight)
            	        			Toast.makeText(getApplicationContext(),"Please wait!", Toast.LENGTH_SHORT)
            	        				.show();
            	        	}
            
            		        GetHttp.flag_update = false;
            		        Log.d("update", "+ update completed");
            		        Log.d("update", "+ Flag update:"+GetHttp.flag_update);
            		        
            		        flag_type = socNONE;
            		        
            	        }
                    flag_proc_mess = false;
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    
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
				Log.e(TAG, "before setupChat() menthod");
				setupChat();
			}
			ensureDiscoverable();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		mResultButton.setVisibility(View.INVISIBLE);
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
//		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//		                    SensorManager.SENSOR_DELAY_GAME);
		// ...and the orientation sensor
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
		                    SensorManager.SENSOR_DELAY_GAME);
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
									sendMessage("I press back");
								}
								else {
									mChatService.stop();
								}
								if (mChatService.getState() != BluetoothService.STATE_NONE) {
									Log.e(TAG,
											"----------------- service STOP ------------------");
								}
								
								locationManager.removeUpdates(locationListener);;
								targetId = 0;
				                GetHttp.choseTarget=false;
				                GetHttp.flag_update = false;
				                flag_channel = false;
				                showInfo=false;
				                zoomMap = false;
				                role = NO_PLAY;
				                flag_vibra = false;
				                flag_proc_mess = false;
				                
				                LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;				                
								handle_info.removeCallbacks(ShowInfor);
								handle_shooting.removeCallbacks(updateStateButton);	
								handle_blinkScreen.removeCallbacks(blinkingScreen);
								Log.i(TAG, "front finish() menthod");
								if(isTracking==1)
								{
									 mWebSocketClient.close();
									 Log.e("socket","close");
								}
								isTracking = 0;
								 sm.unregisterListener(BluetoothTest.this);
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
	
	private void socLogin()
	{
//		flag_type = socLOGIN;
		Log.e("socket","Login success");
		mWebSocketClient.send("{\"type\":\"subscribe\",\"_token\":\""+LoginActivity.token+"\"}");
	}
	
	
	private void socHit()
	{
//		flag_type = socHIT;
		mWebSocketClient.send("{\"type\":\"hit\"}");
	}
	private void socWithdraw()
	{
//		flag_type = socWITHDRAW;
		mWebSocketClient.send("{\"type\":\"withdraw\"}");
	}
	
	private void setupChat() {
		Log.e("socket","connect");
		connectWebSocket();
		Log.d(TAG, "setupChat()");
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
						sendMessage(message);
						flag_play_update = UNCOMPLETED;
						mResultButton.setVisibility(View.INVISIBLE);
						if(mTestButton.getVisibility()==View.VISIBLE)
							mTestButton.setVisibility(View.INVISIBLE);
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
								if(flag_shoot==1)
								{
									
									Log.d("result","result Win");
		    						String message = "You die!";
		    						sendMessage = message;
					                sendMessage(message);
					                flag_shoot = 0;
					                flag_win = 1;
					                flag_play = 0;
					                targetId = 0;
					                role = NO_PLAY;
					                GetHttp.choseTarget=false;
					                flag_resultbtn = false;
					                
					                mTestButton.setVisibility(View.INVISIBLE);
					                mPlayButton.setText("Reset");
					                mPlayButton.setBackgroundColor(0xFFFFFFFF);
					                handle_shooting.removeCallbacks(updateStateButton);
					                
					                socHit();

								
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
		
		
		mTestButton.setOnClickListener(new OnClickListener() {
			
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
											
											flag_shoot = 0;
							                flag_win = 1;
							                flag_play = 0;
							                
							                resetCommandIsTrue = true;
											String message = "new session";
											sendMessage = message;
											sendMessage(message);
							                //
											targetId = 0;
							                GetHttp.choseTarget=false;
					    					mTestButton.setVisibility(View.INVISIBLE);
					    					setStatus(R.string.title_no_play);
					    					socWithdraw();
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

	static int cnt_blink = 0;
	static Runnable blinkingScreen = new Runnable() {
		
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("update", "- Update state button -");
			Log.e("update", "targetId"+ targetId);
			Log.e("update", "state bluetooth"+mChatService.getState());
			ColorDrawable[] colorBlink = {new ColorDrawable(Color.parseColor("#000000")), new ColorDrawable(Color.parseColor("#FF0000"))};
            TransitionDrawable trans = new TransitionDrawable(colorBlink);
            myview.setBackground(trans);
            trans.startTransition(200);
            cnt_blink++;
            if(cnt_blink<5)
            {
            	handle_blinkScreen.postDelayed(this, 500);
            }
            else 
            {
            	cnt_blink=0;
            	myview.setBackground(null);;
            	handle_blinkScreen.removeCallbacks(this);
            }
            
		}
		
		
	};
	static int flag_checkTarget = 0;
	static Runnable updateStateButton = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("update", "- Update state button -");
			Log.d("update", "targetId"+ targetId);
			Log.d("update", "state bluetooth"+mChatService.getState());
			if(mChatService!=null)
			{
				if ((mChatService.getState() != BluetoothService.STATE_CONNECTED)) {
					mPlayButton.setBackgroundColor(0xFFFFFFFF);
					flag_checkTarget = 0;
					mResultButton.setVisibility(View.INVISIBLE);
				}
				else if((mChatService.getState() == BluetoothService.STATE_CONNECTED)&&(flag_play==1))
				{
					Log.e("rssi","rssi discovery");
					mBluetoothAdapter.startDiscovery();
					flag_checkTarget++;	
					mTestButton.setVisibility(View.VISIBLE);
				}
			}
			if(flag_checkTarget>3)
			{
				mPlayButton.setBackgroundColor(0xFFFF0000);
				flag_shoot = 1;
				flag_checkTarget = 4;
			}
			handle_shooting.postDelayed(this, 1000);
		}
		
		
	};
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
		// Stop the Bluetooth chat services
		// if (mChatService != null) mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
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


	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
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
	private final void setStatus(int resId) {
		actionBar = getActionBar();
		actionBar.setSubtitle(resId);
		
		
	}
	
	private final CharSequence getStatus()
	{
		return actionBar.getSubtitle();
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

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
					mTestButton.setVisibility(View.INVISIBLE);
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
					 if(flag_win==1)
					 {
						mResultButton.setBackgroundColor(0x00FFFF00);
						mResultButton.setTextColor(Color.parseColor("#ff0000"));
		                mResultButton.setVisibility(View.VISIBLE);
		                mResultButton.setText("YOU WIN");
		                flag_win = 0;
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
	                String readMessage = new String(readBuf, 0, msg.arg1);
	                if(readMessage.equals("You die!") )
	                {
	                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
		                mResultButton.setVisibility(View.VISIBLE);
		                mResultButton.setText("YOU LOST");
		                mResultButton.setTextColor(Color.parseColor("#0f1bb0"));
		                mResultButton.setBackgroundColor(0x0000FFFF);
		                
		                handle_blinkScreen.postDelayed(blinkingScreen, 0);
		                mTestButton.setVisibility(View.INVISIBLE);
		                mPlayButton.setText("Reset");
	                }
	                if (readMessage.equals("new session") )
	                {
	                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                	mResultButton.setVisibility(View.INVISIBLE);
	                	mTestButton.setVisibility(View.INVISIBLE);
	                	mChatService.reset();
	                	resetCommandIsTrue = false;
	                	mPlayButton.setText("PLAY");
	                	
	                }

	                break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to other player",
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
				setupChat();
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

	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (D)
				Log.e(TAG, "-- ON Change Location --");
			
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			myLong = location.getLongitude();
			myLat = location.getLatitude();
			if(flag_firt_update_map==true)
			{
				myLocation.remove();
				
			}
			
			
			myLocation = mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(myLat,myLong))
	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_arrow_blue)));
			flag_firt_update_map = true;
			if(zoomMap==false)
        	{
				Log.d("zoom","zoom 15");
    		    CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
        		mMap.animateCamera(cameraUpdate1, new CancelableCallback() {
					
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						zoomMap = true;
					}
					
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						
					}
				});
        	}
//			if(isConnectSocket==0)
//			{
//			Log.e("bluetooth","connect");
//				connectWebSocket();
//				isConnectSocket = 1;
//			}
			if(isFirstTracking==0)
				handle_info.postDelayed(FirstTracking, 0);
			if(isTracking==1)
			{
				try {
					isFirstTracking = 1;
					Log.d("tracking","send tracking");
					mWebSocketClient.send("{\"type\":\"tracking\",\"longitude\":\""+myLong+"\",\"latitude\":\""+myLat+"\"}");
					Log.d("tracking","send tracking completed");
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}

		}
		
	   		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
	Runnable ShowInfor = new Runnable(){
        public void run(){
             //call the service here
			if(indexInfor!=LoginActivity.id)
			{
				if(validMarker[indexInfor])
				{
					if(MarkerArr[indexInfor]!=null)
					{					
						MarkerArr[indexInfor].showInfoWindow();
						Log.e("Info","Show infor player"+indexInfor);
					}
				}
			}
			indexInfor++;
			if(indexInfor>(_endIndex-1))
				indexInfor=_startIndex;
			if(mChatService!=null)
			{
				if((mChatService.getState() == BluetoothService.STATE_CONNECTED)&&(flag_play==0))
				{
					handle_shooting.post(updateStateButton);
					rssi_value.setText("updating...");
					flag_play = 1;
				}
				if(role != NO_PLAY)
				{
					if((mChatService.getState() != BluetoothService.STATE_CONNECTED))
					{
						rssi_value.setText("Out of range");
					}
				}
			}
			else
			{
				rssi_value.setText("No measure");
				setStatus(R.string.title_no_play);
			}
			
			
			if(flag_resultbtn)
			{
				mResultButton.setVisibility(View.INVISIBLE);
			}
			if(role==TARGET && flag_vibra==false)
			{
				Log.e("vibrator","vibrator");
				v_target = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				 // Vibrate for 1000 milliseconds
				v_target.vibrate(1000);
				flag_vibra = true;
				
				mResultButton.setVisibility(View.VISIBLE);
                mResultButton.setText("YOU ARE TARGETED");
                mResultButton.setTextColor(Color.parseColor("#0f1bb0"));
                if(getStatus() == "no play")
                {
                	setStatus(R.string.title_hunter_out);
                }
                
                flag_resultbtn = true;

			}
		
             ////// set the interval time here
             handle_info.postDelayed(this,2000);
        }
   };
   Runnable FirstTracking = new Runnable(){
       public void run(){
            //call the service here
    	   if((isTracking==1) && (isFirstTracking==0))
			{
				try {
					Log.d("socket","First send tracking");
					mWebSocketClient.send("{\"type\":\"tracking\",\"longitude\":\""+myLong+"\",\"latitude\":\""+myLat+"\"}");
					Log.d("socket","First send tracking completed");
					isFirstTracking = 1;
					handle_firstTracking.removeCallbacks(FirstTracking);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
    	   else
    		   handle_firstTracking.postDelayed(this,1000);
		
            ////// set the interval time here
            
       }
  };

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
//		Toast.makeText(this, "Please connect to Goolge Play Service", Toast.LENGTH_SHORT).show();
	}
	
	

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		Log.d("fight","Fight");
		
		for(GetHttp.cntUserId=_startIndex;GetHttp.cntUserId<_endIndex;GetHttp.cntUserId++)
		{
			Log.d("fight","Fight 1");
			if(arg0.equals(MarkerArr[GetHttp.cntUserId])){
				Log.d("fight","Fight 2");
				if(GetHttp.choseTarget==false)
				{
					Log.d("fight","BTAddress"+GetHttp._BTAddress[GetHttp.cntUserId]);
					if(!GetHttp._BTAddress[GetHttp.cntUserId].equals("null"))
					{
						
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("Are you sure you choose player"+GetHttp.cntUserId+" ?")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												mWebSocketClient.send("{\"type\":\"fight\",\"target\":\""+GetHttp.cntUserId+"\"}");
												GetHttp.choseTarget = true;
												targetId = GetHttp.cntUserId;
//												Log.d("fight","target id:"+targetId);
//												device = mBluetoothAdapter.getRemoteDevice(GetHttp._BTAddress[GetHttp.cntUserId]);
//												mChatService.connect(device, true);		
//												rssi_value.setText("Out of range");
//												role=HUNTER;
//												mTestButton.setVisibility(View.VISIBLE);
//												Toast.makeText(getBaseContext(),"Your target is chosen: player"+targetId, Toast.LENGTH_LONG).show(); 
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
						builder.setMessage("Format address bluetooth of player"+GetHttp.cntUserId+"is false! Please choose other!")
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
	private final BroadcastReceiver receiver = new BroadcastReceiver(){
		   @SuppressWarnings("static-access")
		@Override
		   public void onReceive(Context context, Intent intent) {

		    String action = intent.getAction();
		    if(device.ACTION_FOUND.equals(action)&& role!=NO_PLAY) {
		        int  rssi = intent.getShortExtra(device.EXTRA_RSSI,Short.MIN_VALUE);
			        rssi_value.setText(""+rssi+"dBm");
			        Log.e("rssi","rssi value"+rssi+"dBm");
//		        Toast.makeText(getApplicationContext(),"  RSSI: " + rssi + "dBm",Toast.LENGTH_SHORT).show();
		    }
		   }
	};
	
    
	//sensor listener
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
    	// get the angle around the z-axis rotated
		int type = sensorEvent.sensor.getType();
        degree = Math.round(sensorEvent.values[0]);
        if(myLocation!=null)
        	myLocation.setRotation(degree);


	}
	//

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	

}
