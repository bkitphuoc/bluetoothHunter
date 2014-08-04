package com.example.android.BluetoothTest;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {
	private static final String TAG = "My location Listener";
	public ConnectWebSocket connectWebSocket;

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (BluetoothTest.D)
			Log.e(TAG, "-- ON Change Location --");
		
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		BluetoothTest.myLong = location.getLongitude();
		BluetoothTest.myLat = location.getLatitude();
		if(BluetoothTest.flag_firt_update_map==true)
			BluetoothTest.myLocation.remove();
		
		BluetoothTest.myLocation = BluetoothTest.mMap.addMarker(new MarkerOptions()
        .position(new LatLng(BluetoothTest.myLat,BluetoothTest.myLong))
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_arrow_blue)));
		BluetoothTest.flag_firt_update_map = true;
		if(BluetoothTest.zoomMap==false)
    	{
			Log.d("zoom","zoom 15");
		    CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
		    BluetoothTest.mMap.animateCamera(cameraUpdate1, new CancelableCallback() {
				
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					BluetoothTest.zoomMap = true;
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
    	}
		if(BluetoothTest.isTracking==true)
		{
			try {
				Log.d("tracking","send tracking");
//				WebSocket.mWebSocketClient.send("{\"type\":\"tracking\",\"longitude\":\""+BluetoothTest.myLong+"\",\"latitude\":\""+BluetoothTest.myLat+"\"}");
				connectWebSocket.mWebSocketClient.send("{\"type\":\"tracking\",\"longitude\":\""+BluetoothTest.myLong+"\",\"latitude\":\""+BluetoothTest.myLat+"\"}");
				Log.d("tracking","send tracking completed");
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
