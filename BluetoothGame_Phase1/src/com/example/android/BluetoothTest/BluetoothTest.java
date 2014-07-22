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

	public int flag_play = 0;
	public int flag_shoot = 1;
	// Layout Views
	private ListView mConversationView;
	// private EditText mOutEditText;
	private Button mTestButton, mPlayButton,mResultButton;

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
	private BluetoothService mChatService = null;
	private boolean localPlayIsPressed = false;
	private boolean remotePlayIsPressed = false;
	private int flag_win = 0;
	boolean isConnected = false;
	
	GoogleMap mMap;
	LocationManager locationManager ;
	LocationListener locationListener;
	static double myLong;
	static double myLat;
	Marker Target;
	private Marker[] MarkerArr = new Marker[6];
	String address = "";
	static int targetId=0;
	Boolean zoomMap = false;
	Boolean showInfo = false;
	
	static GetHttp fightView;
	public static Handler handle_update,handle_parse,handle_info,handle_shooting;
	Boolean flag_channel = false;

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
//        	CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(20.0f);
        	
        	
        	mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
        	
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
 
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
 
            // Getting Current Location
            Location loc = locationManager.getLastKnownLocation(provider);
        
            
            if(loc!=null){
                locationListener.onLocationChanged(loc);
            }
//            
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);	
            
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
		
		
		mResultButton = (Button)findViewById(R.id.button_result);
		mResultButton.setVisibility(View.INVISIBLE);
		
		
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

 
		if (!mBluetoothAdapter.isEnabled()) {
	        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	    // Otherwise, setup the chat session
	    } else
		{
			if (mChatService == null)
				setupChat();
			ensureDiscoverable();
		}
		
		
//		handle_update.postDelayed(updateStatus, 0);
		
//		fight.execute("http://54.255.184.201/api/v1/fight?target=2&_token="+LoginActivity.token);
//		Log.d("post","get: http://54.255.184.201/api/v1/fight?target=2&_token="+LoginActivity.token);

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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								
								if (mChatService.getState() == BluetoothService.STATE_CONNECTED){
									sendMessage("I pess back");
								}
								else {
									mChatService.stop();
								}
								Log.i(TAG, "send message I press back");

								if (mChatService.getState() != BluetoothService.STATE_NONE) {
									Log.e(TAG,
											"----------------- service STOP ------------------");
									//mChatService.stop();
								}
								Log.e(TAG,"Press back");
								
								locationManager.removeUpdates(locationListener);;
								 targetId = 0;
				                GetHttp.choseTarget=false;
				                
								handle_info.removeCallbacks(ShowInfor);
								handle_shooting.removeCallbacks(updateStateButton);
								showInfo=false;
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
	class RenderView extends View {
//        Random rand = new Random();

        public RenderView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
//            canvas.drawRGB(rand.nextInt(256), rand.nextInt(256),
//                    rand.nextInt(256));
        	canvas.drawRGB(255,0,0);
            invalidate();
        }
    }
	private void setupChat() {
		Log.d(TAG, "setupChat()");
//		handle_shooting.post(updateStateButton);
		mPlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				mConversationArrayAdapter.clear();
				if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
					Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT)
							.show();
					
					return;
				} else {
//					flag_play = (flag_play+1)%2;
					if(flag_play==0)
					{

//						mPlayButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.firewhite));
//						mPlayButton.setBackgroundColor(0xFFFFFFFF);
//						mPlayButton.setText("PLAY");
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
							                sendMessage(message);
							                flag_shoot = 0;
							                flag_win = 1;
							                targetId = 0;
							                GetHttp.choseTarget=false;
							                mPlayButton.setText("Reset");
							                handle_shooting.removeCallbacks(updateStateButton);
							                mChatService.stop();
//							                sendMessage("I pess back");
				    					}
				    				}
				    			});
			                }
			                
							
			                
//							Log.d("post","get: http://54.255.184.201/api/v1/fight/hit?_token="+LoginActivity.token);
						}
						
						
						
					}
					else if(flag_play==1)
					{
						Toast.makeText(getApplicationContext(), R.string.not_target, Toast.LENGTH_SHORT)
						.show();
//						mPlayButton.setBackgroundColor(0xFFFF0000);
						mPlayButton.setText("PLAY");
						flag_shoot = 1;
						String message = "new session";
						sendMessage(message);
						mResultButton.setVisibility(View.INVISIBLE);
					}
					
					
				}

			}
		});
		
		
		mTestButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
				                sendMessage(message);
				                flag_shoot = 0;
				                flag_win = 1;
				                targetId = 0;
				                GetHttp.choseTarget=false;
				                mPlayButton.setText("Reset");
				                
				                mChatService.stop();
	    					}
	    				}
	    			});
                }
			}
		});
		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	int flag_checkTarget = 0;
	Runnable updateStateButton = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("update", "- Update state button -");
			Log.e("update", "targetId"+ targetId);
			Log.e("update", "state bluetooth"+mChatService.getState());
			if ((mChatService.getState() != BluetoothService.STATE_CONNECTED)) {
				mPlayButton.setBackgroundColor(0xFFFFFFFF);
				flag_play = 1;
				flag_checkTarget = 0;
			}
			else if((mChatService.getState() == BluetoothService.STATE_CONNECTED))
			{
				flag_checkTarget++;	
			}
			if(flag_checkTarget>3)
			{
				mPlayButton.setBackgroundColor(0xFFFF0000);
				flag_play = 0;
				flag_shoot = 1;
				flag_checkTarget = 4;
			}
//			else
//			{
//				mPlayButton.setBackgroundColor(0xFFFFFFFF);
//				flag_play = 1;
//			}
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
//				 mConversationArrayAdapter.clear();
				 if(flag_win==1)
				 {
//	                mResultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.win));
					mResultButton.setBackgroundColor(0x00FFFF00);
					 mResultButton.setTextColor(Color.parseColor("#ff0000"));
	                mResultButton.setVisibility(View.VISIBLE);
	                mResultButton.setText("YOU WIN");
	                flag_win = 0;
	                mChatService.reset();
//	                mChatService.stop();
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
		                mChatService.reset();
	                }
	                if (readMessage.equals("new session") )
	                {
	                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                	mResultButton.setVisibility(View.INVISIBLE);
//	                	mPlayButton.setBackgroundColor(0x0000ff);
//						mPlayButton.setText("SHOT");
//						flag_shoot = 1;
//						flag_play= 1;
	                }

	                break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
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

	@Override
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
			if(zoomMap==false)
        	{
    		    CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        		mMap.animateCamera(cameraUpdate1);
    			
        		zoomMap = true;
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
//		    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 25);
//			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
		    
	        BitmapDescriptor bitmapDesFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
	        BitmapDescriptor bitmapDesNotFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
	        
	        Log.d("update", "+ Flag update:"+GetHttp.flag_update);
	        if(GetHttp.flag_update==true)
	        {
	        	mMap.clear();
	        	LatLng[] latLngArr = new LatLng[6];
	        	for(int index=1;index<6;index++)
	        	{
	        		if(index!=LoginActivity.id)
	        			latLngArr[index]= new LatLng(Double.parseDouble(GetHttp._Lat[index]),Double.parseDouble(GetHttp._Long[index]));
	        	}

		        // true: free
		        // false: not free
	        	int j = 0;
				for(int index=1;index<6;index++)
				{
					
	     			if(index!=LoginActivity.id)
					{
						if(GetHttp._stage[index]==GetHttp.FREE)
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
							
				        }
				        else
				        {
				        	MarkerArr[index]=mMap.addMarker(new MarkerOptions()
					        .position(latLngArr[index])
					        .icon(bitmapDesNotFree)
					        .title("player"+index));
				        	MarkerArr[index].showInfoWindow();
				        	
					        
				        }
						if(index==targetId)
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
						else
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
	    			flag_channel = false;
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
	int indexInfor =1;
	Runnable ShowInfor = new Runnable(){
        public void run(){
             //call the service here
	
			if(indexInfor!=LoginActivity.id)
			{
				MarkerArr[indexInfor].showInfoWindow();
				Log.e("Info","Show infor player"+indexInfor);
			}

			indexInfor++;
			if(indexInfor>5)
				indexInfor=1;
             ////// set the interval time here
             handle_info.postDelayed(this,1000);
        }
   };
	Runnable updateStatus= new Runnable(){
        public void run(){
             //call the service here
        	Log.e("update", "+ UPDATE +");
        	Log.e("update", "+ Flag getpost:"+LoginActivity.flag_getpost);
        	if(LoginActivity.flag_getpost==LoginActivity.HTTP_FREE)
    		{
        		LoginActivity.flag_getpost=LoginActivity.HTTP_BUZY;
        		Log.e("update", "+ UPDATE 2 +");
        		Log.d("update", "+ Flag update:"+ GetHttp.flag_update);
    			
    			
    			UpdateStatusPlayer();
    		}
        		handle_update.postDelayed(this,10000);
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
					int index = result.indexOf(lat);
					int i=1;
					
					while(index != -1) {
						Log.e("status", "++ STATUS ++");
						GetHttp.LatIndex[i]=index;
					    Log.d("status","\n Lat index"+i+":"+index);
					    index = result.indexOf(lat, index + 1);
					    
					    i++;
					}
					index = result.indexOf(log);
					i=1;
					while(index != -1) {
						Log.e("status", "++ STATUS ++");
						GetHttp.LogIndex[i]=index;
					    Log.d("status","\n Log index"+i+":"+index);
					    index = result.indexOf(log, index + 1);
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
					
					for(i=1;i<6;i++)
					{
						if(i!= LoginActivity.id)
						{
							if(GetHttp.LatIndex[i]!=-1)
							{
								index=result.indexOf("\"",GetHttp.LatIndex[i]+11);
								Log.d("status","\n Lat index"+i+":"+result.substring(GetHttp.LatIndex[i]+11,index));
								GetHttp._Lat[i]=result.substring(GetHttp.LatIndex[i]+11,index);
								Log.d("post", "Lat: player"+i+":"+GetHttp._Lat[i]);
							}
						}
					}
					
					for(i=1;i<6;i++)
					{
						if(i!=LoginActivity.id)
						{
							if(GetHttp.LogIndex[i]!=-1)
							{
								index=result.indexOf("\"",GetHttp.LogIndex[i]+12);
								Log.d("status","\n Lat index"+i+":"+result.substring(GetHttp.LogIndex[i]+12,index));
								GetHttp._Long[i]=result.substring(GetHttp.LogIndex[i]+12,index);
								Log.d("post", "Lat: player"+i+":"+GetHttp._Long[i]);
							}
						}
					}
					
					for(i=1;i<6;i++)
					{
						if(i!=LoginActivity.id)
						{
							if(GetHttp.StageIndex[i]!=-1)
							{
								Log.d("status","\n stage index"+i+":"+result.substring(GetHttp.StageIndex[i]+8,GetHttp.StageIndex[i]+12));
								if(result.substring(GetHttp.StageIndex[i]+8,GetHttp.StageIndex[i]+12).equals("free"))
								{
									GetHttp._stage[i] = GetHttp.FREE;
									
								}
								else
								{
									GetHttp._stage[i]=GetHttp.NOT_FREE;
								}
								Log.d("post", "Stage: player"+i+":"+GetHttp._stage[i]);
							}
						}
					}
					
					for(i=1;i<6;i++)
					{
						
						if(i!= LoginActivity.id)
						{
							if(GetHttp.BTAddressIndex[i]!=-1)
							{
								index=result.indexOf("\"",GetHttp.BTAddressIndex[i]+20);
								Log.d("address","\n BTAddress index"+i+":"+result.substring(GetHttp.BTAddressIndex[i]+20,index));
								GetHttp._BTAddress[i]=result.substring(GetHttp.BTAddressIndex[i]+20,index);
								Log.d("post", "BTAddress: player"+i+":"+GetHttp._BTAddress[i]);
							}
						}
					}
				}
			});
			GetHttp.flag_update = true;
	
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
		
		for(GetHttp.cntUserId=1;GetHttp.cntUserId<6;GetHttp.cntUserId++)
		{
			Log.d("fight","Fight 1");
			if(arg0.equals(MarkerArr[GetHttp.cntUserId])){
				Log.d("fight","Fight 2");
				GetHttp fight = new GetHttp();
				if(LoginActivity.flag_getpost==LoginActivity.HTTP_FREE)
				{
					Log.d("fight","Fight 3");
					LoginActivity.flag_getpost=LoginActivity.HTTP_BUZY;
					if(GetHttp.choseTarget==false)
					{
						Log.d("fight","BTAddress"+GetHttp._BTAddress[GetHttp.cntUserId]);
						if(!GetHttp._BTAddress[GetHttp.cntUserId].equals("ull,"))
						{
							fight.execute("http://54.255.184.201/api/v1/fight?target="+GetHttp.cntUserId+"&_token="+LoginActivity.token);
							Log.d("fight","http://54.255.184.201/api/v1/fight?target="+GetHttp.cntUserId+"&_token="+LoginActivity.token);
							GetHttp.setOnPost(new OnPost(){
								public void onpost(String result){
									Log.d("fight","result fight:"+result);
									int index = result.indexOf("false");
									if(index!=-1)
									{
										Toast.makeText(getBaseContext(),"Please chose others", Toast.LENGTH_LONG).show();
									}
									index = result.indexOf("true");							
									if(index!=-1)
									{
										
										GetHttp.choseTarget = true;
										targetId = GetHttp.cntUserId;
										Log.d("fight","target id:"+targetId);
										device = mBluetoothAdapter.getRemoteDevice(GetHttp._BTAddress[GetHttp.cntUserId]);
										mChatService.connect(device, true);
										Toast.makeText(getBaseContext(),"Your target is chosen: player"+targetId, Toast.LENGTH_LONG).show();
										handle_shooting.post(updateStateButton);
									}
								}
							});
						}
						else
						{
							Toast.makeText(getBaseContext(),"Format of address bluetooth is false", Toast.LENGTH_LONG).show();
							LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;
						}
						
					}
					else
					{
						Toast.makeText(getBaseContext(),"Can't choose other, your target is player"+targetId, Toast.LENGTH_LONG).show();
						LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;
					}

				}
				
				break;
			}
		}
		
	
		return true;
	}
	
}
