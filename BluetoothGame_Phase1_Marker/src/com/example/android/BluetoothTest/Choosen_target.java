package com.example.android.BluetoothTest;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Choosen_target extends Activity implements LocationListener {

	String TAG = "MainActivity";

	private GoogleMap map;
	private OnLocationChangedListener mapLocationListener = null;
	LocationManager locationManager;
	Marker m1, m2, m3, m4, m5;
	int M = 0;
	int M1 = 1;
	int M2 = 2;
	int M3 = 3;
	int M4 = 4;
	int M5 = 5;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    String address = "target_andress";
    boolean chose_continuos = false;
    
	OnMarkerClickListener listener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			if (marker.equals(m1)) {
				chose_continuos = true;
				Log.e(TAG, "Hello M1");
				showmessage(M1);
			} else if (marker.equals(m2)) {
				chose_continuos = false;
				address = "90:C1:15:26:D8:38";
				Log.e(TAG, "Hello M2");
				showmessage(M2);
			} else if (marker.equals(m3)) {
				chose_continuos = false;
				address = "90:C1:15:26:D8:38";
				Log.e(TAG, "Hello M3");
				showmessage(M4);
			} else if (marker.equals(m4)) {
				chose_continuos = false;
				address = "90:C1:15:26:D8:38";
				Log.e(TAG, "Hello M4");
				showmessage(M4);
			} else if (marker.equals(m5)) {
				chose_continuos = false;
				address = "90:C1:15:26:D8:38";
				Log.e(TAG, "Hello M5");
				showmessage(M5);
			}
			
			if(chose_continuos == false){
				star_connect();
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.example.android.BluetoothChat.R.layout.choosen_target_map);

		map = ((MapFragment) getFragmentManager().findFragmentById(
				com.example.android.BluetoothChat.R.id.map)).getMap();

		//
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);
		// -----

		Location location = null;
		location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());

		LatLng latLng1 = new LatLng(location.getLatitude() - 0.00647,
				location.getLongitude() + 0.00494);
		LatLng latLng2 = new LatLng(location.getLatitude() + 0.00422,
				location.getLongitude() + 0.00773);
		LatLng latLng3 = new LatLng(location.getLatitude() + 0.00422,
				location.getLongitude() - 0.00772);
		LatLng latLng4 = new LatLng(location.getLatitude() - 0.00864,
				location.getLongitude() - 0.00386);

		m1 = map.addMarker(new MarkerOptions()
				.position(latLng)
				.icon(BitmapDescriptorFactory
						.fromResource(com.example.android.BluetoothChat.R.drawable.marker2))
				.title("Hunter location"));
		m1.showInfoWindow();

		m2 = map.addMarker(new MarkerOptions()
				.position(latLng1)
				.icon(BitmapDescriptorFactory
						.fromResource(com.example.android.BluetoothChat.R.drawable.marker))
				.title("Target location"));

		m3 = map.addMarker(new MarkerOptions()
				.position(latLng2)
				.icon(BitmapDescriptorFactory
						.fromResource(com.example.android.BluetoothChat.R.drawable.marker))
				.title("Target location"));

		m4 = map.addMarker(new MarkerOptions()
				.position(latLng3)
				.icon(BitmapDescriptorFactory
						.fromResource(com.example.android.BluetoothChat.R.drawable.marker))
				.title("Target location"));

		m5 = map.addMarker(new MarkerOptions()
				.position(latLng4)
				.icon(BitmapDescriptorFactory
						.fromResource(com.example.android.BluetoothChat.R.drawable.marker))
				.title("Target location"));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
		map.setOnMarkerClickListener(listener);
	
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.activity_main, menu);
	// return true;
	// }

	void showmessage(int position) {
		if (position == M1) {
			Toast.makeText(this, "Please choosen your target in blue icon", Toast.LENGTH_SHORT).show();
		} else if (position == M2) {
			Toast.makeText(this, "Chossen M2", Toast.LENGTH_SHORT).show();
		} else if (position == M3) {
			Toast.makeText(this, "Chossen M3", Toast.LENGTH_SHORT).show();
		} else if (position == M4) {
			Toast.makeText(this, "Chossen M4", Toast.LENGTH_SHORT).show();
		} else if (position == M5) {
			Toast.makeText(this, "Chossen M5", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (mapLocationListener != null) {
			mapLocationListener.onLocationChanged(location);

			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());
			CameraUpdate cu = CameraUpdateFactory.newLatLng(latlng);

			map.animateCamera(cu);
		}

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
	
	void star_connect(){
		Intent intent = new Intent(this,BluetoothTest.class);
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        startActivity(intent);
	}

}
