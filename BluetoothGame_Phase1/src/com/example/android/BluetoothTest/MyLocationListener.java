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
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (BluetoothTest.instance.D)
			Log.e(TAG, "-- ON Change Location --");
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		BluetoothTest.instance.myLong = location.getLongitude();
		BluetoothTest.instance.myLat = location.getLatitude();
		if(BluetoothTest.instance.flag_firt_update_map==true)
			BluetoothTest.instance.myLocation.remove();
		
		
		BluetoothTest.instance.myLocation = BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
        .position(new LatLng(BluetoothTest.instance.myLat,BluetoothTest.instance.myLong))
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_arrow_blue)));
		BluetoothTest.instance.flag_firt_update_map = true;
		if(BluetoothTest.instance.zoomMap==false)
    	{
			Log.d("zoom","zoom 15");
		    CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
		    BluetoothTest.instance.mMap.animateCamera(cameraUpdate1, new CancelableCallback() {
				
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					BluetoothTest.instance.zoomMap = true;
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
    	}
//		if(BluetoothTest.instance.showInfo==false)
//		{
//			BluetoothTest.instance.showInfo = true;
//			BluetoothTest.instance.handle_info.postDelayed(BluetoothTest.instance.ShowInfor, 0);
//			Log.e("Info","Show infor");
//		}
		if(BluetoothTest.instance.isTracking==true)
		{
			try {
				//if(BluetoothTest.instance.myLong!=0)
					ACRAApplication.getInstance().connecWebSocket.mWebSocketClient.send("{\"type\":\"tracking\",\"longitude\":\""+BluetoothTest.instance.myLong+"\",\"latitude\":\""+BluetoothTest.instance.myLat+"\"}");
				
				Log.d("tracking","send tracking completed - long: "+BluetoothTest.instance.myLong+",lat:"+BluetoothTest.instance.myLat);
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("tracking","can send");
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
