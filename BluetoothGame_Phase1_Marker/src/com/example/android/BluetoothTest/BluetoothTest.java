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
import java.util.Random;

import com.crittercism.app.Crittercism;
import com.example.android.BluetoothTest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
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

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothTest extends FragmentActivity implements LocationListener,GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	// Debugging
	private static final String TAG = "BluetoothChat";
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
	public int flag_shoot = 0;
	// Layout Views
	private ListView mConversationView;
	// private EditText mOutEditText;
	private Button mSendButton, mPlayButton,mResultButton;

	private TextView mWarningText;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothService mChatService = null;
	private boolean localPlayIsPressed = false;
	private boolean remotePlayIsPressed = false;
	private int flag_win = 0;
	boolean isConnected = false;
	
	GoogleMap mMap;
	LocationManager locationManager ;
	static double myLong;
	static double myLat;
	String address = "target_address";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		setContentView(R.layout.main);
		Bundle extras = getIntent().getExtras();
		address = extras.getString("device_address");

		if(LoginActivity.flag_debug==1)
		{
			Crittercism.initialize(getApplicationContext(),"53b3bb7b07229a5a86000006");
			try {
				throw new Exception("Exception Reason");
			} catch (Exception exception) {
				Crittercism.logHandledException(exception);
			}
		}
		mMap = ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.mapgame)).getMap();
		
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
	    // Register the listener with the Location Manager to receive location updates
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);	    
		mPlayButton = (Button) findViewById(R.id.button_play);
		mPlayButton.setEnabled(true);
		mPlayButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.firewhite));
		mPlayButton.setVisibility(View.VISIBLE);
		
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
								Log.e(TAG,
										"em da o day-----------------> bang chiu ----------------->");
								
								
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

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		mPlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mConversationArrayAdapter.clear();
				if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
					Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT)
							.show();
					
					return;
				} else {
					flag_play = (flag_play+1)%2;
					if(flag_play==0)
					{

						mPlayButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.firewhite));
						mPlayButton.setText("PLAY");
						if(flag_shoot==1)
						{
							String message = "You die!";
			                sendMessage(message);
			                flag_shoot = 0;
			                flag_win = 1;
						}
						
						
						
					}
					else if(flag_play==1)
					{
						mPlayButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.firered));
						mPlayButton.setText("SHOT");
						flag_shoot = 1;
						String message = "new session";
						sendMessage(message);
						mResultButton.setVisibility(View.INVISIBLE);
					}
					
					
				}

			}
		});
		


		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
		
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, true);
	}

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
					mConversationArrayAdapter.clear();
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
				 mConversationArrayAdapter.clear();
				 if(flag_win==1)
				 {
	                mResultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.win));
	                mResultButton.setVisibility(View.VISIBLE);
	                mResultButton.setText("YOU WIN");
	                flag_win = 0;
				 }
	                break;
	            case MESSAGE_READ:
	                byte[] readBuf = (byte[]) msg.obj;
	                // construct a string from the valid bytes in the buffer
	                String readMessage = new String(readBuf, 0, msg.arg1);
	                if(readMessage.equals("You die!") )
	                {
	                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
		                mResultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.failure));
		                mResultButton.setVisibility(View.VISIBLE);
		                mResultButton.setText("YOU LOST");
	                }
	                if (readMessage.equals("new session") )
	                {
	                	Log.i("read", "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                	mResultButton.setVisibility(View.INVISIBLE);
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

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (D)
			Log.e(TAG, "-- ON Change Location --");
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//		mylocation.setLongitude(location.getLongitude());
//		mylocation.setLatitude(location.getLatitude());
		myLong = location.getLongitude();
		myLat = location.getLatitude();
		
		// (1)request get infor user
		// (2)parse to get id -> long, lat -> convert string to double
		// (3)assign long,lat to every object
		// (4)display on map 
		//=> create handler here -> parameter is function implement (1),(2),(3),(4). 
		
		LatLng latLng1 = new LatLng(location.getLatitude() - 0.00647, location.getLongitude() + 0.00494);
		LatLng latLng2 = new LatLng(location.getLatitude() + 0.00422, location.getLongitude() + 0.00773);
		LatLng latLng3 = new LatLng(location.getLatitude() + 0.00422, location.getLongitude() - 0.00772);
		LatLng latLng4 = new LatLng(location.getLatitude() - 0.00864, location.getLongitude() - 0.00386);
		
		Float acc = location.getAccuracy();
		
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        
        mMap.clear();
        
        mMap.addMarker(new MarkerOptions()
        .position(latLng));
        
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        
        mMap.addMarker(new MarkerOptions()
        .position(latLng1)
        .icon(bitmapDescriptor));
        
        mMap.addMarker(new MarkerOptions()
        .position(latLng2)
        .icon(bitmapDescriptor));
        
        mMap.addMarker(new MarkerOptions()
        .position(latLng3)
        .icon(bitmapDescriptor));
        
        mMap.addMarker(new MarkerOptions()
        .position(latLng4)
        .icon(bitmapDescriptor));
       
        mMap.animateCamera(cameraUpdate);
        
        PostHttp.casepost = PostHttp.TRACKING;
		new PostHttp().execute("");
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
