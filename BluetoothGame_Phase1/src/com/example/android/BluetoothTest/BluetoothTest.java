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
import java.text.DecimalFormat;
import java.util.Random;

import com.crittercism.app.Crittercism;
import com.example.android.BluetoothTest.GetHttp.OnPost;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.UiSettings;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;                    
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.acra.*;
import org.acra.annotation.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.*;
import org.apache.http.util.*;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothTest extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,OnMarkerClickListener{
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

	public static int flag_play = 0;
	public static int flag_shoot = 0;
	// Layout Views
	private ListView mConversationView;
	// private EditText mOutEditText;
	private static Button mTestButton;
	private static Button mPlayButton;
	static Button mResultButton;

	private TextView mWarningText;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
//	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothDevice device;
	// Member object for the chat services
	private static BluetoothService mChatService = null;
	private boolean localPlayIsPressed = false;
	private boolean remotePlayIsPressed = false;
	private int flag_win = 0;
	boolean isConnected = false;
	static String sendMessage="";
	public static boolean resetCommandIsTrue = false;
	
	GoogleMap mMap;
	LocationManager locationManager ;
	LocationListener locationListener;
	static double myLong;
	static double myLat;
	Marker Target;
	private Marker[] MarkerArr = new Marker[11];
	Boolean[] validMarker = new Boolean[11];
	String address = "";
	static int targetId=0;
	Boolean zoomMap = false;
	Boolean showInfo = false;
	
	static int _startIndex =0;
	static int _endIndex = 0;
	int indexInfor =0;
	
	static GetHttp fightView;
	public static Handler handle_update,handle_parse,handle_info,handle_shooting;
	static Boolean flag_channel = false;

	TextView distanceText[] = new TextView[4];
	TextView targetText[]= new TextView[4];
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ACRA.init(this);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		setContentView(R.layout.main);
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
		
		String tokenId = "token";
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
        	SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapgame);
        	 
            // Getting GoogleMap object from the fragment
        	mMap = fm.getMap();        	
        	locationListener = new MyLocationListener();
        	
            // Enabling MyLocation Layer of Google Map
        	mMap.setMyLocationEnabled(true);
        	mMap.getUiSettings().setAllGesturesEnabled(true);
        	mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
        	
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
 
            // Getting the name of the best provider
            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service
              .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // check if enabled and if not send user to the GSP settings
            // Better solution would be to display a dialog and suggesting to 
            // go to the settings
            if (!enabled) {
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                // Setting Dialog Title
                alertDialog.setTitle("GPS is settings");
               // Setting Dialog Message
                alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
                // On pressing Settings button
                alertDialog.setPositiveButton("Settings", new 

                           DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Intent intent = new 
                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                // on pressing cancel button
                alertDialog.setNegativeButton("Cancel", new 
                 DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
              
            } 
            else
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
//		 Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	    // Register the listener with the Location Manager to receive location updates
	        
		mPlayButton = (Button) findViewById(R.id.button_play);
		mPlayButton.setEnabled(true);
		mPlayButton.setBackgroundColor(0xFFFFFFFF);
		mPlayButton.setVisibility(View.VISIBLE);
		
		mTestButton = (Button)findViewById(R.id.button_testServer);
		mTestButton.setBackgroundColor(0xFFFFFFFF);
		mTestButton.setVisibility(View.INVISIBLE);
		
		mResultButton = (Button)findViewById(R.id.button_result);
		mResultButton.setVisibility(View.INVISIBLE);
		
		
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
	}
	
	
	@SuppressWarnings("deprecation")
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
				                LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;				                
								handle_info.removeCallbacks(ShowInfor);
								handle_shooting.removeCallbacks(updateStateButton);								
								Log.i(TAG, "front finish() menthod");
								mChatService = null;
								
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
	
	private void setupChat() {
		Log.d(TAG, "setupChat()");
		mPlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
					Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT)
							.show();
					
					return;
				} else {
					if(mPlayButton.getText().equals("Reset"))
					{
						mPlayButton.setText("Play");
						resetCommandIsTrue = true;
						String message = "new session";
						sendMessage = message;
						sendMessage(message);
						
						mResultButton.setVisibility(View.INVISIBLE);
					}
					else
					{
//						if(flag_play==0)
						{
							if(flag_shoot==1)
							{
				                GetHttp hit = new GetHttp();
				                if(LoginActivity.flag_getpost==LoginActivity.HTTP_FREE)
				                {
				                	LoginActivity.flag_getpost=LoginActivity.HTTP_BUZY;
					                hit.execute("http://54.255.184.201/api/v1/fight/hit?_token="+LoginActivity.token);
					                Log.d("fight","http://54.255.184.201/api/v1/fight/hit?_token="+LoginActivity.token);
					                GetHttp.setOnPost(new OnPost(){
					    				public void onpost(String result){
					    					Log.d("result","result hit:"+result);
					    					int index=result.indexOf("You won.");
					    					if(index!=-1)
					    					{
					    						Log.d("result","result Win");
					    						String message = "You die!";
					    						sendMessage = message;
								                sendMessage(message);
								                flag_shoot = 0;
								                flag_win = 1;
								                flag_play = 0;
								                targetId = 0;
								                GetHttp.choseTarget=false;
								                
								                mPlayButton.setText("Reset");
								                mPlayButton.setBackgroundColor(0xFFFFFFFF);
								                handle_shooting.removeCallbacks(updateStateButton);
					    					}
					    				}
					    			});
				                }
							}
							else
								Toast.makeText(getApplicationContext(), "Please wait! Not ready.", Toast.LENGTH_SHORT)
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
											GetHttp hit = new GetHttp();
											
							                hit.execute("http://54.255.184.201/api/v1/fight/withdraw?_token="+LoginActivity.token);
							                Log.d("fight","http://54.255.184.201/api/v1/fight/withdraw?_token="+LoginActivity.token);
							                GetHttp.setOnPost(new OnPost(){
							    				public void onpost(String result){
							    					Log.d("result","result withdraw:"+result);
							    					int index=result.indexOf("You lose");
							    					if(index!=-1)
							    					{
							    						Log.d("result","You lose");
							    					}
							    					if (mChatService.getState() == BluetoothService.STATE_CONNECTED){
														sendMessage("I press back");
													}
													else {
														mChatService.stop();
													}
							    					
							    					if (mChatService.getState() == BluetoothService.STATE_NONE){
														mChatService.start();
													}
							    					flag_shoot = 0;
									                flag_win = 1;
									                flag_play = 0;
									                targetId = 0;
									                GetHttp.choseTarget=false;
							    					mTestButton.setVisibility(View.INVISIBLE);
							    				}
							    			});
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

	static int flag_checkTarget = 0;
	static Runnable updateStateButton = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("update", "- Update state button -");
			Log.e("update", "targetId"+ targetId);
			Log.e("update", "state bluetooth"+mChatService.getState());
			if ((mChatService.getState() != BluetoothService.STATE_CONNECTED)) {
				mPlayButton.setBackgroundColor(0xFFFFFFFF);
//				flag_play = 1;
				flag_checkTarget = 0;
				mResultButton.setVisibility(View.INVISIBLE);
			}
			else if((mChatService.getState() == BluetoothService.STATE_CONNECTED))
			{
				flag_checkTarget++;	
				mTestButton.setVisibility(View.VISIBLE);
			}
			if(flag_checkTarget>3)
			{
				mPlayButton.setBackgroundColor(0xFFFF0000);
//				flag_play = 0;
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

	private void btNotEnalbe() {
		

		if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {

			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			mWarningText.setText(" ");
			return;
		} else {
//			flag_play = 1;
			mPlayButton.setEnabled(false);
			localPlayIsPressed = true;
			sendMessage("remote Play bt is pressed");
			mWarningText.setText(" ");
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

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendMessage(message);
			}
			if (D)
				Log.i(TAG, "END onEditorAction");
			return true;
		}
	};

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
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
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName));
//					mConversationArrayAdapter.clear();
					isConnected = true;
					break;
				case BluetoothService.STATE_CONNECTING:
					isConnected = false;
					setStatus(R.string.title_connecting);
					break;
				case BluetoothService.STATE_LISTEN:
					isConnected = false;
					setStatus(R.string.title_listen);
					break;
				case BluetoothService.STATE_NONE:
					isConnected = false;
					setStatus(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				if(sendMessage.equals("You die!") ){
//					 mConversationArrayAdapter.clear();
					 if(flag_win==1)
					 {
//		                mResultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.win));
						mResultButton.setBackgroundColor(0x00FFFF00);
						 mResultButton.setTextColor(Color.parseColor("#ff0000"));
		                mResultButton.setVisibility(View.VISIBLE);
		                mResultButton.setText("YOU WIN");
		                flag_win = 0;
//		                mChatService.reset();
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
//		                mResultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.failure));
		                mResultButton.setVisibility(View.VISIBLE);
		                mResultButton.setText("YOU LOST");
		                mResultButton.setTextColor(Color.parseColor("#0f1bb0"));
		                mResultButton.setBackgroundColor(0x0000FFFF);
		                
		                mPlayButton.setText("Reset");
//		                mPlayButton.setBackgroundColor(0xFFFFFFFF);
//						mPlayButton.setText("PLAY");
//						flag_shoot = 0;
//						flag_play= 0;
						
//						mChatService.stop();
		                
	                }
	                if (readMessage.equals("new session") )
	                {
	                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                	mResultButton.setVisibility(View.INVISIBLE);
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

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			if(isConnected == false){
				// Launch the DeviceListActivity to see devices and do scan
				serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
				return true;	
			} 
			else {
				Toast.makeText(getApplicationContext(),
						"You are already connect to another device",
						Toast.LENGTH_SHORT).show();
			}
			
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}*/


	
	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (D)
				Log.e(TAG, "-- ON Change Location --");
			
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			myLong = location.getLongitude();
			myLat = location.getLatitude();
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
        	else
        	{
        		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        		mMap.moveCamera(cameraUpdate);
        	}
			
			Location locationA = new Location("point A");
			locationA.setLongitude(myLong);
			locationA.setLatitude(myLat);
			Location locationB = new Location("point B");
			float distance[] = new float[4];
			
			// (1)request get infor user
			// (2)parse to get id -> long, lat -> convert string to double
			// (3)assign long,lat to every object
			// (4)display on map 
			//=> create handler here -> parameter is function implement (1),(2),(3),(4). 
			
			Float acc = location.getAccuracy();
	        BitmapDescriptor bitmapDesFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
	        BitmapDescriptor bitmapDesNotFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
	        
	        Log.d("update", "+ Flag update:"+GetHttp.flag_update);
	        if(GetHttp.flag_update==true)
	        {
	        	mMap.clear();
	        	LatLng[] latLngArr = new LatLng[11];
	        	for(int index=_startIndex;index<_endIndex;index++)
	        	{
	        		if(index!=LoginActivity.id)
	        		{
	        			try
	    	        	{
	        				latLngArr[index]= new LatLng(Double.parseDouble(GetHttp._Lat[index]),Double.parseDouble(GetHttp._Long[index]));
	    	        	}
	    	        	catch(NumberFormatException e)
	    	        	{
	    	        	  //not a double
	    	        		latLngArr[index]= new LatLng(0,0);
	    	        	}
	        			
	        		}
	        	}
	        	int j = 0;
				for(int index=_startIndex;index<_endIndex;index++)
				{
					
	     			if(index!=LoginActivity.id)
					{
						if(GetHttp._stage[index]==GetHttp.FREE)
				        {
							if(!latLngArr[index].equals(new LatLng(0,0)))
							{
							
								if(index==targetId)
								{
									targetId = 0;
									GetHttp.choseTarget = false;
								}
						        MarkerArr[index]= mMap.addMarker(new MarkerOptions()
						        .position(latLngArr[index])
						        .icon(bitmapDesFree)
						        .title("player"+index));
						        MarkerArr[index].showInfoWindow();
						        
						        validMarker[index] = true;
							}
							else
								validMarker[index] = false;
							
				        }
				        else
				        {
				        	if(!latLngArr[index].equals(new LatLng(0,0)))
				        	{
					        	MarkerArr[index]=mMap.addMarker(new MarkerOptions()
						        .position(latLngArr[index])
						        .icon(bitmapDesNotFree)
						        .title("player"+index));
					        	MarkerArr[index].showInfoWindow();
					        	validMarker[index] = true;
				        	}
				        	else
				        		validMarker[index] = false;
				        	
					        
				        }
						
						if(index==targetId)
						{
							if(!latLngArr[index].equals(new LatLng(0,0)))
							{
								targetText[j].setTextColor(Color.parseColor("#ff0000"));
								targetText[j].setText("My target:");
								locationB.setLatitude(latLngArr[index].latitude);
						        locationB.setLongitude(latLngArr[index].longitude);
						        Log.e("MyLocation",myLong + "    " + myLat);
						        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
						        distance[j] = locationA.distanceTo(locationB); 
						        distanceText[j].setTextColor(Color.parseColor("#ff0000"));
						        distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
						        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
							}
						}
						else
						{
							if(!latLngArr[index].equals(new LatLng(0,0)))
							{
								if(GetHttp._stage[index]==GetHttp.FREE)
								{
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
								{
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
							}
						}
				        j++;
					}
	     			if(showInfo==false)
	     			{
	     				showInfo = true;
	     				handle_info.postDelayed(ShowInfor, 0);
	     				Log.e("Info","Show infor");
	     			}
					
				}

		        GetHttp.flag_update = false;
		        Log.d("update", "+ update completed");
		        Log.d("update", "+ Flag update:"+GetHttp.flag_update);
	        }
        	
	        
	        if(flag_channel==false)
	        {
		        Log.e("update", "+ Flag getpost:"+LoginActivity.flag_getpost);
		        if(LoginActivity.flag_getpost==LoginActivity.HTTP_FREE)
		        {
		        	LoginActivity.flag_getpost=LoginActivity.HTTP_BUZY;
		        	Log.e("http", "+ HTTP POST ME BUZY +");
			        PostHttp.casepost = PostHttp.TRACKING;
					new PostHttp().execute("http://54.255.184.201/api/v1/tracking?_token="+LoginActivity.token);
					flag_channel = true;
			        Log.d("update","channel:"+flag_channel);
				}
		        
	        }
	        else
	        {
		        if(LoginActivity.flag_getpost==LoginActivity.HTTP_FREE)
	    		{
	        		Log.e("update", "+ UPDATE 2 +");
	        		Log.d("update", "+ Flag update:"+ GetHttp.flag_update);
	    			LoginActivity.flag_getpost=LoginActivity.HTTP_BUZY;
	    			UpdateStatusPlayer();
//	    			flag_channel = false;
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
					MarkerArr[indexInfor].showInfoWindow();
					Log.e("Info","Show infor player"+indexInfor);
				}
			}

			indexInfor++;
			if(indexInfor>(_endIndex-1))
				indexInfor=_startIndex;
			
			 
			if((mChatService.getState() == BluetoothService.STATE_CONNECTED)&&(flag_play==0))
			{
				handle_shooting.post(updateStateButton);
				flag_play = 1;
			}
             ////// set the interval time here
             handle_info.postDelayed(this,1000);
        }
   };

	public static void UpdateStatusPlayer()
	{		
		if(GetHttp.flag_update==false)
		{
			Log.e("http", "+ HTTP UPDATE BUZY +");			
			GetHttp.caseget = GetHttp.UPDATE;			
			GetHttp statusPlayer = new GetHttp();
			statusPlayer.execute("http://54.255.184.201/api/v1/users?_token="+LoginActivity.token);
			Log.d("post","get: http://54.255.184.201/api/v1/users?_token="+LoginActivity.token);
			GetHttp.setOnPost(new OnPost(){
				public void onpost(String result){
					String lat = "latitude";
					String log = "longitude";
					String stage ="stage";
					String address="bluetooth_address";
					String pos = "position";

					int index = result.indexOf(pos);
					int index1 = 0;
					int i = 1;
					while(index!=-1)
					{
						Log.e("status", "++ STATUS ++");
						GetHttp.PosIndex[i]=index;
					    Log.d("status","\n position index"+i+":"+index);
					    index = result.indexOf(pos, index + 1);			    
					    i++;
					}
					index = result.indexOf(stage);
					i=1;
					while(index != -1) {
						Log.e("status", "++ STATUS ++");
						GetHttp.StageIndex[i]=index;
					    Log.d("status","\n stage index"+i+":"+index);
					    index = result.indexOf(stage, index + 1);			    
					    i++;
					}
					index = result.indexOf(address);
					i=1;
					while(index != -1) {
						Log.e("address", "++ Bluetooth Address ++");
						GetHttp.BTAddressIndex[i]=index;
					    Log.d("address","\n Bluetooth Address index"+i+":"+index);
					    index = result.indexOf(address, index + 1);			    
					    i++;
					}
					
					for(i=_startIndex;i<_endIndex;i++)
					{
						if(i!= LoginActivity.id)
						{
							if(GetHttp.PosIndex[i]!=-1)
							{
								Log.d("status","\n Postion index"+i+":"+result.substring(GetHttp.PosIndex[i]+10,GetHttp.PosIndex[i]+14));
								if(!result.substring(GetHttp.PosIndex[i]+10,GetHttp.PosIndex[i]+14).equals("null"))
								{
									
									index=result.indexOf("\"",GetHttp.PosIndex[i]+23);
									GetHttp._Lat[i]=result.substring(GetHttp.PosIndex[i]+23,index);
									index1 = index+15;
									index = result.indexOf("\"",index1);
									GetHttp._Long[i]=result.substring(index1,index);
									
									Log.d("post", "Lat_player"+i+":"+GetHttp._Lat[i]);
									Log.d("post", "Long_player"+i+":"+GetHttp._Long[i]);
								}
								else
								{
									GetHttp._Lat[i]="null";
									GetHttp._Long[i]="null";
								}
								
							}
						}
					}
					
				
					for(i=_startIndex;i<_endIndex;i++)
					{
//						if(i!=LoginActivity.id)
						{
							if(GetHttp.StageIndex[i]!=-1)
							{
								Log.d("status","\n stage index"+i+":"+result.substring(GetHttp.StageIndex[i]+8,GetHttp.StageIndex[i]+12));
								index = result.indexOf("\"",GetHttp.StageIndex[i]+8);
								if(result.substring(GetHttp.StageIndex[i]+8,index).equals("free"))
								{
									GetHttp._detailStage[i] = GetHttp._FREE;
									GetHttp._stage[i] = GetHttp.FREE;
									
								}
								else if(result.substring(GetHttp.StageIndex[i]+8,index).equals("be_targeted"))
								{
									GetHttp._stage[i]=GetHttp.NOT_FREE;
									GetHttp._detailStage[i] = GetHttp._BE_TARGETED;
								}
								else if(result.substring(GetHttp.StageIndex[i]+8,index).equals("hunting"))
								{
									GetHttp._stage[i]=GetHttp.NOT_FREE;
									GetHttp._detailStage[i] = GetHttp._HUNTING;
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
//								index=result.indexOf("\"",GetHttp.BTAddressIndex[i]+20);
//								Log.d("address","\n BTAddress index"+i+":"+result.substring(GetHttp.BTAddressIndex[i]+20,index));
//								GetHttp._BTAddress[i]=result.substring(GetHttp.BTAddressIndex[i]+20,index);
//								Log.d("post", "BTAddress_player"+i+":"+GetHttp._BTAddress[i]);
								index=result.indexOf(",",GetHttp.BTAddressIndex[i]+19);
								if(!result.substring(GetHttp.BTAddressIndex[i]+19,index).equals("null"))
								{
									index=result.indexOf("\"",GetHttp.BTAddressIndex[i]+20);
									Log.d("address","\n BTAddress index"+i+":"+result.substring(GetHttp.BTAddressIndex[i]+20,index));
									GetHttp._BTAddress[i]=result.substring(GetHttp.BTAddressIndex[i]+20,index);
								}
								else
								{
									GetHttp._BTAddress[i]="null";
									Log.d("address","\n BTAddress index"+i+":"+result.substring(GetHttp.BTAddressIndex[i]+19,index));
								}
								Log.d("post", "BTAddress_player"+i+":"+GetHttp._BTAddress[i]);
							}
						}
					}
					flag_channel = false;
					GetHttp.flag_update = true;
				}
			});
			
	
		}
	}

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
				
				if(LoginActivity.flag_getpost==LoginActivity.HTTP_FREE)
				{
					Log.d("fight","Fight 3");
					LoginActivity.flag_getpost=LoginActivity.HTTP_BUZY;
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
													GetHttp fight = new GetHttp();
													fight.execute("http://54.255.184.201/api/v1/fight?target="+GetHttp.cntUserId+"&_token="+LoginActivity.token);
													Log.d("fight","http://54.255.184.201/api/v1/fight?target="+GetHttp.cntUserId+"&_token="+LoginActivity.token);
													GetHttp.setOnPost(new OnPost(){
														public void onpost(String result){
															Log.d("fight","result fight:"+result);
															int index = result.indexOf("false");
															if(index!=-1)
															{
																Toast.makeText(getBaseContext(),"Can not choose this player", Toast.LENGTH_LONG).show();
//															
															}
															index = result.indexOf("true");							
															if(index!=-1)
															{
																
																GetHttp.choseTarget = true;
																targetId = GetHttp.cntUserId;
																Log.d("fight","target id:"+targetId);
																device = mBluetoothAdapter.getRemoteDevice(GetHttp._BTAddress[GetHttp.cntUserId]);
																mChatService.connect(device, true);
																
																mTestButton.setVisibility(View.VISIBLE);
																Toast.makeText(getBaseContext(),"Your target is chosen: player"+targetId, Toast.LENGTH_LONG).show();
															}
														}
													});	

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

				}
				
				break;
			}
		}
		
	
		return true;
	}
	
}
