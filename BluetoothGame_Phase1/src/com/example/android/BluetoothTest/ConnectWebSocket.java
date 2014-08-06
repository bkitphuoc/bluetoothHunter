package com.example.android.BluetoothTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import crittercism.android.ac;


public class ConnectWebSocket {
	final String TAG = "Shooting Game";
	String messageFromServer;
	final  String TYPE_SUBSCRIBE = "subscribe";
	final  String TYPE_TRACKING = "tracking";
	final  String TYPE_FIGHT ="fight";
	final  String TYPE_HIT="hit";
	final  String TYPE_WITHDRAW = "withdraw";
//
//	// socket server
	static int socNONE = 0;
	static int socLOGIN = 1;
	static int socTRACKING = 2;
	static int socFIGHT = 3;
	static int socHIT = 4;
	static int socWITHDRAW = 5;
	static int socFAIL = 6;
	
	public  WebSocketClient mWebSocketClient;
	 Boolean flag_proc_mess = false;
     String type_value="";
     int type_state = socNONE;
    private Activity activity;
    
    int _index=0;
	int _index1=0;
	int id_login = 0;
	int temp_hunter=0; 
	int temp_target=0;
	int index_type_value = 0;
	int index_text = 0;
	String _id_value="";
	
	LatLng[] latLngArr;
	BitmapDescriptor bitmapDesFree;
	BitmapDescriptor bitmapDesNotFree;

	//update state marker, distance
	Location locationA;
	Location locationB;
	float[] distance;;
//	// TODO Auto-generated method stub

   
    
	public void connect(Activity _activity)
	{
		activity = _activity;
		flag_proc_mess = false;
	    id_login = 0;
		temp_hunter=0; 
		temp_target=0;
		latLngArr = new LatLng[11];
		locationA = new Location("point A");
		locationB = new Location("point B");
		distance = new float[4];
		index_text = 0;
		
		bitmapDesFree = BitmapDescriptorFactory.fromResource(R.drawable.pin_blue);
		bitmapDesNotFree = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);
		if (BluetoothTest.instance.D)
		{
			Log.d("connect socket", "+++ WS connect +++");
		}
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
            	
            	socLogin();
            	Log.e("socket","open");
        		BluetoothTest.instance.isTracking = true;
//        		if(BluetoothTest.instance.complete_login==0)
//        			BluetoothTest.instance.complete_login=1;
//            
        		}

            @Override
            public void onMessage(String s) {
            	Log.e("socket","onMessage");
            	if(flag_proc_mess==false)
            	{
            		messageFromServer = s;
            	}
            	Log.e("socket","start received");
               activity.runOnUiThread(new Runnable() {
                    @SuppressLint("NewApi")
					public void run() {
//                    	if (BluetoothTest.instance.D)
                		Log.d("socket","message:"+messageFromServer);
                    	flag_proc_mess = true;
                    	
                    	int index_type = messageFromServer.indexOf("type");                    	
                    	index_type_value=messageFromServer.indexOf("\"",index_type+7);
                    	type_value = messageFromServer.substring(index_type+7,index_type_value);
                    	
                    	if(type_value.equals(TYPE_SUBSCRIBE))
                    	{
                    		procSubscribe();
                    	}
                    	else if(type_value.equals(TYPE_TRACKING))
                    	{
                    		procTracking();
                    	}
                    	
                    	else if(type_value.equals(TYPE_FIGHT))
                    	{
                    		procFight();
                    	}
                    	
                    	else if((type_value.equals(TYPE_WITHDRAW))||(type_value.equals(TYPE_HIT)))
                    	{
                    		procEndGame();
                    	}
                    	
                    	
                    	procUpdate();
                    	
//                    	if(BluetoothTest.instance.complete_login==1)
//                    		BluetoothTest.instance.complete_login=2;
                    	flag_proc_mess = false;
                    	temp_hunter=0; 
                		temp_target=0;
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
//        if(BluetoothTest.instance.complete_login==0)
//			BluetoothTest.instance.complete_login=1;
	}
	
	public void procSubscribe()
	{

		if (BluetoothTest.instance.D)
			Log.d("socket","Subscribe:"+messageFromServer);
		type_state = socLOGIN;
		
		_index = messageFromServer.indexOf("id");
		_index1 = 0;
		if(_index!=-1)
		{
			BluetoothTest.instance.flag_stop_game = true;
			_index1 = messageFromServer.indexOf("\"", _index+5);
			_id_value = messageFromServer.substring(_index+5, _index1);
			
			id_login = Integer.parseInt(_id_value);
			if (BluetoothTest.instance.D)
				Log.e("socket","new user:"+id_login);
			if((id_login!=LoginActivity.id)&& (id_login>=BluetoothTest.instance._startIndex) && (id_login<BluetoothTest.instance._endIndex))
			{
				if (BluetoothTest.instance.D)
					Log.e("subscribe","-----------Enter---------------");
				String stage ="stage";
				String address="bluetooth_address";
				String pos = "position";
				
				int index = messageFromServer.indexOf(pos);
				int index1 = 0;
				
				if(index!=-1)
				{
					BluetoothTest.instance.PosIndex[id_login]=index;
				}
				
				index = messageFromServer.indexOf(stage);
				if(index!=-1)
				{
					BluetoothTest.instance.StageIndex[id_login]=index;
				}
				index = messageFromServer.indexOf(address);
				if(index!=-1)
				{
					BluetoothTest.instance.BTAddressIndex[id_login]=index;
				}
				BluetoothTest.instance._Online[id_login]=true;
				
				if(!messageFromServer.substring(BluetoothTest.instance.PosIndex[id_login]+10,BluetoothTest.instance.PosIndex[id_login]+14).equals("null"))
				{
					
					index=messageFromServer.indexOf("\"",BluetoothTest.instance.PosIndex[id_login]+23);
					BluetoothTest.instance._Lat[id_login]=messageFromServer.substring(BluetoothTest.instance.PosIndex[id_login]+23,index);
					index1 = index+15;
					index = messageFromServer.indexOf("\"",index1);
					BluetoothTest.instance._Long[id_login]=messageFromServer.substring(index1,index);
					if (BluetoothTest.instance.D)
					{
						Log.d("post", "Lat_player"+id_login+":"+BluetoothTest.instance._Lat[id_login]);
						Log.d("post", "Long_player"+id_login+":"+BluetoothTest.instance._Long[id_login]);
					}
				}
				else
				{
					BluetoothTest.instance._Lat[id_login]="null";
					BluetoothTest.instance._Long[id_login]="null";
				}
				
				if(BluetoothTest.instance.StageIndex[id_login]!=-1)
				{
					if (BluetoothTest.instance.D)
					{
						Log.d("status","\n stage index"+id_login+":"+messageFromServer.substring(BluetoothTest.instance.StageIndex[id_login]+8,BluetoothTest.instance.StageIndex[id_login]+12));
					}
					index = messageFromServer.indexOf("\"",BluetoothTest.instance.StageIndex[id_login]+8);
					String stage_temp=messageFromServer.substring(BluetoothTest.instance.StageIndex[id_login]+8,index);
					if(stage_temp.equals("free"))
					{
						BluetoothTest.instance._detailStage[id_login] = BluetoothTest.instance._FREE;
					}
					else if(stage_temp.equals("hunting"))
					{
						BluetoothTest.instance._detailStage[id_login] = BluetoothTest.instance._HUNTING;
					}
					else if(stage_temp.equals("be_targeted"))
					{
						BluetoothTest.instance._detailStage[id_login] = BluetoothTest.instance._BE_TARGETED;
					}
					if (BluetoothTest.instance.D)
					{
						Log.d("post", "Stage_player"+id_login+":"+BluetoothTest.instance._detailStage[id_login]);
					}
				}
				
				if(BluetoothTest.instance.BTAddressIndex[id_login]!=-1)
				{
					index=messageFromServer.indexOf(",",BluetoothTest.instance.BTAddressIndex[id_login]+19);
					String btAddress_temp =messageFromServer.substring(BluetoothTest.instance.BTAddressIndex[id_login]+19,index); 
					if(!btAddress_temp.equals("null"))
					{
						index=messageFromServer.indexOf("\"",BluetoothTest.instance.BTAddressIndex[id_login]+20);
						if (BluetoothTest.instance.D)
						{
							Log.d("address","\n BTAddress index"+id_login+":"+messageFromServer.substring(BluetoothTest.instance.BTAddressIndex[id_login]+20,index));
						}
						btAddress_temp = messageFromServer.substring(BluetoothTest.instance.BTAddressIndex[id_login]+20,index);
						if(btAddress_temp.length()<17)
						{
							BluetoothTest.instance._BTAddress[id_login]="null";
						}
						else
						{
							BluetoothTest.instance._BTAddress[id_login]= btAddress_temp;
						}
					}
					else
					{
						BluetoothTest.instance._BTAddress[id_login]="null";
					}
					if (BluetoothTest.instance.D)
					{
						Log.d("post", "BTAddress_player"+id_login+":"+BluetoothTest.instance._BTAddress[id_login]);
					}
				}
			}
			else
				{
					;
				}
		}
		else
		{
			BluetoothTest.instance.flag_stop_game = false;
		}
		
		BluetoothTest.instance.flag_update = true;
	
	}
	public void procTracking()
	{

		if (BluetoothTest.instance.D)
		{
			Log.d("socket","Tracking:"+messageFromServer);
		}
		type_state = socTRACKING;
		String stage ="stage";
		String address="bluetooth_address";
		String pos = "position";
		String onl = "is_online";
		
		BluetoothTest.instance.flag_stop_game = true;
		int index = messageFromServer.indexOf(pos);
		int index1 = 0;
		int i = 1;
		if (BluetoothTest.instance.D)
		{
			Log.e("status", "++ STATUS ++");
		}
		while(index!=-1)
		{
			
			BluetoothTest.instance.PosIndex[i]=index;
			if (BluetoothTest.instance.D)
			{
				Log.d("status","\n position index"+i+":"+index);
			}
		    index = messageFromServer.indexOf(pos, index + 1);			    
		    i++;
		}
		index = messageFromServer.indexOf(stage);
		i=1;
		while(index != -1) {
			BluetoothTest.instance.StageIndex[i]=index;
			if (BluetoothTest.instance.D)
			{
				Log.d("status","\n stage index"+i+":"+index);
			}
		    index = messageFromServer.indexOf(stage, index + 1);			    
		    i++;
		}
		index = messageFromServer.indexOf(address);
		i=1;
		while(index != -1) {
			Log.e("address", "++ Bluetooth Address ++");
			BluetoothTest.instance.BTAddressIndex[i]=index;
		    Log.d("address","\n Bluetooth Address index"+i+":"+index);
		    index = messageFromServer.indexOf(address, index + 1);			    
		    i++;
		}
		if (BluetoothTest.instance.D)
		{
			Log.e("online", "++ Online state ++");
		}
		index = messageFromServer.indexOf(onl);
		i=1;
		while(index != -1) {
		
			BluetoothTest.instance.OnlineIndex[i]=index;
			if (BluetoothTest.instance.D)
			{
				Log.d("online","\n Online index"+i+":"+index);
			}
		    index = messageFromServer.indexOf(onl, index + 1);			    
		    i++;
		}
		String online_temp;
		for(i=BluetoothTest.instance._startIndex;i<BluetoothTest.instance._endIndex;i++)
		{
			
			if(i!= LoginActivity.id)
			{
				if(BluetoothTest.instance.OnlineIndex[i]!=-1)
				{
					index=messageFromServer.indexOf("\"",BluetoothTest.instance.OnlineIndex[i]+12);
					online_temp =messageFromServer.substring(BluetoothTest.instance.OnlineIndex[i]+12,BluetoothTest.instance.OnlineIndex[i]+13);
					if (BluetoothTest.instance.D)
					{
						Log.d("post", "online value"+i+":"+online_temp);
					}
					if(online_temp.equals("1"))
					{
						BluetoothTest.instance._Online[i]=true;
					}
					else
					{
						BluetoothTest.instance._Online[i]=false;
					}
					if (BluetoothTest.instance.D)
					{
						Log.d("post", "online state"+i+":"+BluetoothTest.instance._Online[i]);
					}
				}
			}
		}
		String pos_temp;
		for(i=BluetoothTest.instance._startIndex;i<BluetoothTest.instance._endIndex;i++)
		{
			if(i!= LoginActivity.id)
			{
				if(BluetoothTest.instance.PosIndex[i+1]!=-1)
				{
					pos_temp = messageFromServer.substring(BluetoothTest.instance.PosIndex[i+1]+10,BluetoothTest.instance.PosIndex[i+1]+14);
					if (BluetoothTest.instance.D)
					{
						Log.d("status","\n Postion index"+i+":"+pos_temp);
					}
					if(!pos_temp.equals("null"))
					{
						index=messageFromServer.indexOf("\"",BluetoothTest.instance.PosIndex[i+1]+23);
						BluetoothTest.instance._Lat[i]=messageFromServer.substring(BluetoothTest.instance.PosIndex[i+1]+23,index);
						index1 = index+15;
						index = messageFromServer.indexOf("\"",index1);
						BluetoothTest.instance._Long[i]=messageFromServer.substring(index1,index);
						if (BluetoothTest.instance.D)
						{
							Log.d("post", "Lat_player"+i+":"+BluetoothTest.instance._Lat[i]);
							Log.d("post", "Long_player"+i+":"+BluetoothTest.instance._Long[i]);
						}
					}
					else
					{
						BluetoothTest.instance._Lat[i]="null";
						BluetoothTest.instance._Long[i]="null";
					}
					
				}
			}
		}
		
		String stage_temp;
		for(i=BluetoothTest.instance._startIndex;i<BluetoothTest.instance._endIndex;i++)
		{
//			if(i!=LoginActivity.id)
			{
				if(BluetoothTest.instance.StageIndex[i]!=-1)
				{
					stage_temp = messageFromServer.substring(BluetoothTest.instance.StageIndex[i]+8,BluetoothTest.instance.StageIndex[i]+12);
					if(BluetoothTest.instance.D)
					{
						Log.d("status","\n stage index"+i+":"+stage_temp);
					}
					index = messageFromServer.indexOf("\"",BluetoothTest.instance.StageIndex[i]+8);
					stage_temp=messageFromServer.substring(BluetoothTest.instance.StageIndex[i]+8,index);
					if(stage_temp.equals("free"))
					{
						BluetoothTest.instance._detailStage[i] = BluetoothTest.instance._FREE;
						if(i==LoginActivity.id)
						{
							BluetoothTest.instance.flag_vibra = false;
							BluetoothTest.instance.role = BluetoothTest.NO_PLAY;
							if(BluetoothTest.instance.mWithdrawButton.getVisibility()==View.VISIBLE)
								BluetoothTest.instance.mWithdrawButton.setVisibility(View.INVISIBLE);
						}
						
					}
					else if(stage_temp.equals("be_targeted"))
					{
						BluetoothTest.instance._detailStage[i] = BluetoothTest.instance._BE_TARGETED;	
						if(i==LoginActivity.id)
						{
							BluetoothTest.instance.role = BluetoothTest.TARGET;
						}
					}
					else if(stage_temp.equals("hunting"))
					{
						BluetoothTest.instance._detailStage[i] = BluetoothTest.instance._HUNTING;
						if(i==LoginActivity.id)
						{
							BluetoothTest.instance.role = BluetoothTest.HUNTER;
						}
						
					}
					if (BluetoothTest.instance.D)
					{
						Log.d("post", "Stage_player"+i+":"+BluetoothTest.instance._detailStage[i]);
					}
				}
			}
			
		}
		if((!BluetoothTest.instance._detailStage[LoginActivity.id].equals(BluetoothTest.instance._FREE))&&(BluetoothTest.flag_play==false))
		{
			BluetoothTest.instance.mWithdrawButton.setVisibility(View.VISIBLE);
			BluetoothTest.handle_shooting.post(BluetoothTest.updateStateButton);
			BluetoothTest.flag_play = true;
		}
		
		for(i=BluetoothTest.instance._startIndex;i<BluetoothTest.instance._endIndex;i++)
		{
			
			if(i!= LoginActivity.id)
			{
				if(BluetoothTest.instance.BTAddressIndex[i]!=-1)
				{
					index=messageFromServer.indexOf(",",BluetoothTest.instance.BTAddressIndex[i]+19);
					if(!messageFromServer.substring(BluetoothTest.instance.BTAddressIndex[i]+19,index).equals("null"))
					{
						index=messageFromServer.indexOf("\"",BluetoothTest.instance.BTAddressIndex[i]+20);
						Log.d("address","\n BTAddress index"+i+":"+messageFromServer.substring(BluetoothTest.instance.BTAddressIndex[i]+20,index));
						BluetoothTest.instance._BTAddress[i]=messageFromServer.substring(BluetoothTest.instance.BTAddressIndex[i]+20,index);
					}
					else
					{
						BluetoothTest.instance._BTAddress[i]="null";
						Log.d("address","\n BTAddress index"+i+":"+messageFromServer.substring(BluetoothTest.instance.BTAddressIndex[i]+19,index));
					}
					Log.d("post", "BTAddress_player"+i+":"+BluetoothTest.instance._BTAddress[i]);
				}
			}
		}
		
		BluetoothTest.instance.flag_update = true;
	
	}
	public void procFight()
	{

		Log.e("socket","Fight:"+messageFromServer);
		type_state = socFIGHT;
		_index = messageFromServer.indexOf("success");
		_index1 = messageFromServer.indexOf(":",_index+1);
		_index =  messageFromServer.indexOf(",",_index1+1);
		String result_temp;
		result_temp = messageFromServer.substring(_index1,_index);
		if(result_temp.equals("false"))
		{
			_index1 = messageFromServer.indexOf("\"",_index+12);
			result_temp = messageFromServer.substring(_index+12,_index1);
			Toast.makeText(activity, result_temp, Toast.LENGTH_LONG).show();
		}
		else
		{
			_index = messageFromServer.indexOf("hunter");
			if(BluetoothTest.instance.choseTarget == true)
			{
				BluetoothTest.instance.targetId = BluetoothTest.instance.cntUserId;
				Log.d("fight","target id:"+BluetoothTest.instance.targetId);
				BluetoothTest.instance.device = BluetoothTest.mBluetoothAdapter.getRemoteDevice(BluetoothTest.instance._BTAddress[BluetoothTest.instance.cntUserId]);
//				if(BluetoothTest.instance.device.getType()==BluetoothTest.instance.device.DEVICE_TYPE_CLASSIC)
					BluetoothTest.mChatService.connect(BluetoothTest.instance.device, true);		
				BluetoothTest.instance.rssi_value.setText("Out of range");
				BluetoothTest.instance.role=BluetoothTest.HUNTER;
				BluetoothTest.instance.mWithdrawButton.setVisibility(View.VISIBLE);
				Toast.makeText(activity, "Your target is chosen: player"+BluetoothTest.instance.targetId, Toast.LENGTH_LONG).show();
			
				_index = messageFromServer.indexOf("hunter");
				if(_index!=-1)
        		{
        			BluetoothTest.instance.flag_stop_game = true;
        			_index1 = messageFromServer.indexOf("\"", _index+15);
        			Log.d("hunter","id:"+messageFromServer.substring(_index+15, _index1));
        			_id_value = messageFromServer.substring(_index+15, _index1);
        			try{
        				temp_hunter = Integer.parseInt(_id_value);
        			}
        			catch(NumberFormatException e)
        			{
        				Log.d("target","id:"+messageFromServer.substring(_index+15, _index1));
        			}
        			
        			_index = messageFromServer.indexOf("target");
            		if(_index!=-1)
            		{
            			_index1 = messageFromServer.indexOf("\"", _index+15);
            			Log.d("target","id:"+messageFromServer.substring(_index+15, _index1));
            			_id_value = messageFromServer.substring(_index+15, _index1);
            			try{
            				temp_target = Integer.parseInt(_id_value);
            			}
            			catch(NumberFormatException e)
            			{
            				Log.d("target","id:"+messageFromServer.substring(_index+15, _index1));
            			}
            		}
            		if(temp_target>=BluetoothTest.instance._startIndex && temp_target<BluetoothTest.instance._endIndex)
            		{
            			String pos = "position";
    					
    					_index = messageFromServer.indexOf(pos);
    					_index1 = 0;

    					if(_index!=-1)
    					{
    						BluetoothTest.instance.PosIndex[temp_hunter]=_index;
    					}
    					_index = messageFromServer.indexOf(pos, _index+1);
    					if(_index!=-1)
    						BluetoothTest.instance.PosIndex[temp_target]=_index;
    					
    					BluetoothTest.instance._Online[temp_hunter]=true;
    					BluetoothTest.instance._Online[temp_target]=true;
    					BluetoothTest.instance._detailStage[temp_hunter]=BluetoothTest.instance._HUNTING;
    					BluetoothTest.instance._detailStage[temp_target]=BluetoothTest.instance._BE_TARGETED;
    					
    					
    					if(!messageFromServer.substring(BluetoothTest.instance.PosIndex[temp_hunter]+10,BluetoothTest.instance.PosIndex[temp_hunter]+14).equals("null"))
						{
							
							_index=messageFromServer.indexOf("\"",BluetoothTest.instance.PosIndex[temp_hunter]+23);
							BluetoothTest.instance._Lat[temp_hunter]=messageFromServer.substring(BluetoothTest.instance.PosIndex[temp_hunter]+23,_index);
							_index1 = _index+15;
							_index = messageFromServer.indexOf("\"",_index1);
							BluetoothTest.instance._Long[temp_hunter]=messageFromServer.substring(_index1,_index);
							
							Log.d("post", "Lat_player"+temp_hunter+":"+BluetoothTest.instance._Lat[temp_hunter]);
							Log.d("post", "Long_player"+temp_hunter+":"+BluetoothTest.instance._Long[temp_hunter]);
						}
						else
						{
							BluetoothTest.instance._Lat[temp_hunter]="null";
							BluetoothTest.instance._Long[temp_hunter]="null";
						}
    					
    					if(!messageFromServer.substring(BluetoothTest.instance.PosIndex[temp_target]+10,BluetoothTest.instance.PosIndex[temp_target]+14).equals("null"))
						{
							
							_index=messageFromServer.indexOf("\"",BluetoothTest.instance.PosIndex[temp_target]+23);
							BluetoothTest.instance._Lat[temp_target]=messageFromServer.substring(BluetoothTest.instance.PosIndex[temp_target]+23,_index);
							_index1 = _index+15;
							_index = messageFromServer.indexOf("\"",_index1);
							BluetoothTest.instance._Long[temp_target]=messageFromServer.substring(_index1,_index);
							
							Log.d("post", "Lat_player"+temp_target+":"+BluetoothTest.instance._Lat[temp_target]);
							Log.d("post", "Long_player"+temp_target+":"+BluetoothTest.instance._Long[temp_target]);
						}
						else
						{
							BluetoothTest.instance._Lat[temp_target]="null";
							BluetoothTest.instance._Long[temp_target]="null";
						}

    					BluetoothTest.instance.validMarker[temp_hunter]=true;
    					BluetoothTest.instance.validMarker[temp_target]=true;
            		}
        		}
				

			}
		}
		BluetoothTest.instance.flag_update = true;
	
	}
	public void procEndGame()
	{

		Log.e("socket","hit or withdraw:"+messageFromServer);
		
		type_state = socWITHDRAW;
		
		_index = messageFromServer.indexOf("success");
		_index1 = messageFromServer.indexOf(":",_index+1);
		_index =  messageFromServer.indexOf(",",_index1+1);
		String result_temp;
		result_temp = messageFromServer.substring(_index1,_index);
		if(result_temp.equals("false"))
		{
			_index1 = messageFromServer.indexOf("\"",_index+12);
			result_temp = messageFromServer.substring(_index+12,_index1);
			Toast.makeText(activity, result_temp, Toast.LENGTH_LONG).show();
		}
		else
		{
    		_index = messageFromServer.indexOf("hunter");
    		_index1 = 0;
    		if(_index!=-1)
    		{                    			
    			BluetoothTest.instance.flag_stop_game = true;
    			_index1 = messageFromServer.indexOf("\"", _index+15);
    			Log.d("hunter","id:"+messageFromServer.substring(_index+15, _index1));
    			_id_value = messageFromServer.substring(_index+15, _index1);
    			try{
    				temp_hunter = Integer.parseInt(_id_value);
    			}
    			catch(NumberFormatException e)
    			{
    				Log.d("target","id:"+messageFromServer.substring(_index+15, _index1));
    			}
    			
    			_index = messageFromServer.indexOf("target");
        		if(_index!=-1)
        		{
        			_index1 = messageFromServer.indexOf("\"", _index+15);
        			Log.d("target","id:"+messageFromServer.substring(_index+15, _index1));
        			_id_value = messageFromServer.substring(_index+15, _index1);
        			try{
        				temp_target = Integer.parseInt(_id_value);
        			}
        			catch(NumberFormatException e)
        			{
        				Log.d("target","id:"+messageFromServer.substring(_index+15, _index1));
        			}
        		}
        		if(temp_target>=BluetoothTest.instance._startIndex && temp_target<BluetoothTest.instance._endIndex)
        		{
        			String pos = "position";
					
					_index = messageFromServer.indexOf(pos);
					_index1 = 0;

					if(_index!=-1)
					{
						BluetoothTest.instance.PosIndex[temp_hunter]=_index;
					}
					_index = messageFromServer.indexOf(pos, _index+1);
					if(_index!=-1)
						BluetoothTest.instance.PosIndex[temp_target]=_index;
					
					BluetoothTest.instance._Online[temp_hunter]=true;
					BluetoothTest.instance._Online[temp_target]=true;
					BluetoothTest.instance._detailStage[temp_hunter] = BluetoothTest.instance._FREE;
    				BluetoothTest.instance._detailStage[temp_target] = BluetoothTest.instance._FREE;
					

					BluetoothTest.instance.validMarker[temp_hunter]=true;
					BluetoothTest.instance.validMarker[temp_target]=true;
        		}
				
    		}
    		else
    		{
    			if(BluetoothTest.instance.targetId == BluetoothTest.instance.cntUserId)
    			{
    				BluetoothTest.instance.choseTarget = false;
    				BluetoothTest.instance.targetId = 0;
    			}
    			BluetoothTest.instance.flag_stop_game = false;
    		}
		}
		BluetoothTest.instance.flag_update = true;
		
	
	}
	public void procUpdate()
	{
		Log.e("update", "+ Flag update:"+BluetoothTest.instance.flag_update);

        if(BluetoothTest.instance.flag_update==true)
        {

        	if(BluetoothTest.instance.flag_stop_game==true)
        	{
//        		BluetoothTest.instance.mMap.clear();
        		if(BluetoothTest.instance.myLocation!=null)
        		{
        			BluetoothTest.instance.myLocation.remove();
        		}
        		if(BluetoothTest.instance._detailStage[LoginActivity.id]==
        				BluetoothTest.instance._FREE)
        		{
	        		BluetoothTest.instance.myLocation = BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
			        .position(new LatLng(BluetoothTest.instance.myLat,BluetoothTest.instance.myLong))
			        .title("me")
			        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_arrow_blue)));
        		}
        		else
        		{
        			BluetoothTest.instance.myLocation = BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
			        .position(new LatLng(BluetoothTest.instance.myLat,BluetoothTest.instance.myLong))
			        .title("me")
			        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_arrow_red)));
        		}
	        	procUpdateLocation();
	        	procUpdateMarker();
	        	procUpdateDistance();        	
	        	
				if(BluetoothTest.instance.showInfo==false)
     			{
     				BluetoothTest.instance.showInfo = true;
     				BluetoothTest.instance.handle_info.postDelayed(BluetoothTest.instance.ShowInfor, 0);
     				Log.e("Info","Show infor");
     			}
				BluetoothTest.instance.flag_stop_game = false;
        	}
        	else
        	{
        		
        	}

	        BluetoothTest.instance.flag_update = false;
	        Log.d("update", "+ update completed");
	        Log.d("update", "+ Flag update:"+BluetoothTest.instance.flag_update);
	        
	        type_state = socNONE;
	        temp_hunter = 0;
	        temp_target = 0;
	        
        }
	}
	public void procUpdateLocation()
	{
		if(type_state==socLOGIN)
    	{
    		if((id_login!=LoginActivity.id)&&(id_login>=BluetoothTest.instance._startIndex)&&(id_login<BluetoothTest.instance._endIndex))
    		{
    			try
	        	{
//    				Log.e("location","index:"+index+
//    							"lat:"+BluetoothTest._Lat[index]+",long:"+BluetoothTest._Long[index]);
    				if(!BluetoothTest.instance._Lat[id_login].equals("null"))
    				{
    					latLngArr[id_login]= new LatLng(Double.parseDouble(BluetoothTest.instance._Lat[id_login]),Double.parseDouble(BluetoothTest.instance._Long[id_login]));
    				}
    				else
    					latLngArr[id_login]= new LatLng(0,0);
	        	}
	        	catch(NumberFormatException e)
	        	{
	        	  //not a double
	        		latLngArr[id_login]= new LatLng(0,0);
	        	}
    			
    		}
    	}
		else if(type_state==socTRACKING)
		{
			for(int index = BluetoothTest.instance._startIndex;index<BluetoothTest.instance._endIndex;index++)
			{
				if(!BluetoothTest.instance._Lat[index].equals("null"))
				{
					latLngArr[index]= new LatLng(Double.parseDouble(BluetoothTest.instance._Lat[index]),Double.parseDouble(BluetoothTest.instance._Long[index]));
				}
				else
					latLngArr[index]=new LatLng(0,0);
			}
		}
    	else if(type_state==socFIGHT)
    	{
    		if((temp_hunter!=LoginActivity.id)&&(temp_hunter>=BluetoothTest.instance._startIndex)&&(temp_hunter<BluetoothTest.instance._endIndex))
    		{
    			try
	        	{
    				if(!BluetoothTest.instance._Lat[temp_hunter].equals("null"))
    				{
    					latLngArr[temp_hunter]= new LatLng(Double.parseDouble(BluetoothTest.instance._Lat[temp_hunter]),Double.parseDouble(BluetoothTest.instance._Long[temp_hunter]));
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
    		if((temp_target!=LoginActivity.id)&&(temp_target>=BluetoothTest.instance._startIndex)&&(temp_target<BluetoothTest.instance._endIndex))
    		{
    			try
	        	{
    				if(!BluetoothTest.instance._Lat[temp_target].equals("null"))
    				{
    					latLngArr[temp_target]= new LatLng(Double.parseDouble(BluetoothTest.instance._Lat[temp_target]),Double.parseDouble(BluetoothTest.instance._Long[temp_target]));
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
    	}
	}

	public void procUpdateMarker()
	{
		if(type_state==socLOGIN)
		{
			if((id_login!=LoginActivity.id)&&(id_login>=BluetoothTest.instance._startIndex)&&
					(id_login<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[id_login]==true))
			{
					// color of marker
				if(BluetoothTest.instance._detailStage[id_login]==BluetoothTest.instance._FREE)
		        {
					if(!latLngArr[id_login].equals(new LatLng(0,0)))
					{
					
						if(id_login==BluetoothTest.instance.targetId)
						{
							BluetoothTest.instance.targetId = 0;
							BluetoothTest.instance.choseTarget = false;
						}
						if(BluetoothTest.instance.MarkerArr[temp_target]!=null)
							BluetoothTest.instance.MarkerArr[temp_target].remove();
						BluetoothTest.instance.MarkerArr[id_login]= BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
				        .position(latLngArr[id_login])
				        .icon(bitmapDesFree)
				        .title("player"+id_login));
						BluetoothTest.instance.MarkerArr[id_login].showInfoWindow();
				        
						BluetoothTest.instance.validMarker[id_login] = true;
					}
					else
						BluetoothTest.instance.validMarker[id_login] = false;
					
		        }
		        else
		        {
		        	if(!latLngArr[id_login].equals(new LatLng(0,0)))
		        	{
		        		if(BluetoothTest.instance.MarkerArr[id_login]!=null)
		        			BluetoothTest.instance.MarkerArr[id_login].remove();
		        		BluetoothTest.instance.MarkerArr[id_login]=BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
				        .position(latLngArr[id_login])
				        .icon(bitmapDesNotFree)
				        .title("player"+id_login));
		        		BluetoothTest.instance.MarkerArr[id_login].showInfoWindow();
		        		BluetoothTest.instance.validMarker[id_login] = true;
		        	}
		        	else
		        		BluetoothTest.instance.validMarker[id_login] = false;
		          
		        }
			}
		}
		else if(type_state==socTRACKING)
		{
			for(int index=BluetoothTest.instance._startIndex;
					index<BluetoothTest.instance._endIndex;index++)
			{
				
				if((index!=LoginActivity.id)&&(index>=BluetoothTest.instance._startIndex)&&
						(index<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[index]==true))
				{
						// color of marker
					if(BluetoothTest.instance._detailStage[index]==BluetoothTest.instance._FREE)
			        {
						if(!latLngArr[index].equals(new LatLng(0,0)))
						{
						
							if(index==BluetoothTest.instance.targetId)
							{
								BluetoothTest.instance.targetId = 0;
								BluetoothTest.instance.choseTarget = false;
							}
							if(BluetoothTest.instance.MarkerArr[index]!=null)
								BluetoothTest.instance.MarkerArr[index].remove();
							
							BluetoothTest.instance.MarkerArr[index]= BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
					        .position(latLngArr[index])
					        .icon(bitmapDesFree)
					        .title("player"+index));
							BluetoothTest.instance.MarkerArr[index].showInfoWindow();
					        
							BluetoothTest.instance.validMarker[index] = true;
						}
						else
							BluetoothTest.instance.validMarker[index] = false;
						
			        }
			        else
			        {
			        	if(!latLngArr[index].equals(new LatLng(0,0)))
			        	{
			        		if(BluetoothTest.instance.MarkerArr[index]!=null)
			        			BluetoothTest.instance.MarkerArr[index].remove();
			        		BluetoothTest.instance.MarkerArr[index]=BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
					        .position(latLngArr[index])
					        .icon(bitmapDesNotFree)
					        .title("player"+index));
			        		BluetoothTest.instance.MarkerArr[index].showInfoWindow();
			        		BluetoothTest.instance.validMarker[index] = true;
			        	}
			        	else
			        		BluetoothTest.instance.validMarker[index] = false;
			          
			        }
				}
			}
		
		}
		else if(type_state==socFIGHT)
		{
			if((temp_hunter!=LoginActivity.id)&&(temp_hunter>=BluetoothTest.instance._startIndex)&&
					(temp_hunter<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[temp_hunter]==true))
			{
				// color of marker
	        	if(!latLngArr[temp_hunter].equals(new LatLng(0,0)))
	        	{
	        		if(BluetoothTest.instance.MarkerArr[temp_hunter]!=null)
	        			BluetoothTest.instance.MarkerArr[temp_hunter].remove();
	        		BluetoothTest.instance.MarkerArr[temp_hunter]=BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
			        .position(latLngArr[temp_hunter])
			        .icon(bitmapDesNotFree)
			        .title("player"+temp_hunter));
	        		BluetoothTest.instance.MarkerArr[temp_hunter].showInfoWindow();
	        		BluetoothTest.instance.validMarker[temp_hunter] = true;
	        	}
	        	else
	        		BluetoothTest.instance.validMarker[temp_hunter] = false;
		          
			}
			if((temp_target!=LoginActivity.id)&&(temp_target>=BluetoothTest.instance._startIndex)&&
					(temp_target<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[temp_target]==true))
			{
				// color of marker
	        	if(!latLngArr[temp_target].equals(new LatLng(0,0)))
	        	{
	        		if(BluetoothTest.instance.MarkerArr[temp_target]!=null)
	        			BluetoothTest.instance.MarkerArr[temp_target].remove();
	        		BluetoothTest.instance.MarkerArr[temp_target]=BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
			        .position(latLngArr[temp_target])
			        .icon(bitmapDesNotFree)
			        .title("player"+temp_target));
	        		BluetoothTest.instance.MarkerArr[temp_target].showInfoWindow();
	        		BluetoothTest.instance.validMarker[temp_target] = true;
	        	}
	        	else
	        		BluetoothTest.instance.validMarker[temp_target] = false;
		          
			}
			else
				BluetoothTest.instance.validMarker[temp_target] = false;
		}
		else if(type_state==socWITHDRAW)
		{
			if((temp_hunter!=LoginActivity.id)&&(temp_hunter>=BluetoothTest.instance._startIndex)&&
					(temp_hunter<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[temp_hunter]==true))
			{
				// color of marker
	        	if(!latLngArr[temp_hunter].equals(new LatLng(0,0)))
	        	{
	        		if(BluetoothTest.instance.MarkerArr[temp_hunter]!=null)
	        			BluetoothTest.instance.MarkerArr[temp_hunter].remove();
	        		BluetoothTest.instance.MarkerArr[temp_hunter]=BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
			        .position(latLngArr[temp_hunter])
			        .icon(bitmapDesFree)
			        .title("player"+temp_hunter));
	        		BluetoothTest.instance.MarkerArr[temp_hunter].showInfoWindow();
	        		BluetoothTest.instance.validMarker[temp_hunter] = true;
	        	}
	        	else
	        		BluetoothTest.instance.validMarker[temp_hunter] = false;
		          
			}
			if((temp_target!=LoginActivity.id)&&(temp_target>=BluetoothTest.instance._startIndex)&&
					(temp_target<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[temp_target]==true))
			{
				// color of marker
	        	if(!latLngArr[temp_target].equals(new LatLng(0,0)))
	        	{
	        		if(BluetoothTest.instance.MarkerArr[temp_target]!=null)
	        			BluetoothTest.instance.MarkerArr[temp_target].remove();
	        		BluetoothTest.instance.MarkerArr[temp_target]=BluetoothTest.instance.mMap.addMarker(new MarkerOptions()
			        .position(latLngArr[temp_target])
			        .icon(bitmapDesFree)
			        .title("player"+temp_target));
	        		BluetoothTest.instance.MarkerArr[temp_target].showInfoWindow();
	        		BluetoothTest.instance.validMarker[temp_target] = true;
	        	}
	        	else
	        		BluetoothTest.instance.validMarker[temp_target] = false;
		          
			}
		}
	
		
	}
	
	public void procUpdateDistance()
	{
		locationA.setLongitude(BluetoothTest.instance.myLong);
		locationA.setLatitude(BluetoothTest.instance.myLat);
		
		for(int index=BluetoothTest.instance._startIndex;
				index<BluetoothTest.instance._endIndex;index++)
		{
			if(LoginActivity.id>5)
			{
				if(index<LoginActivity.id)
				{
					index_text=index%6;
				}
				else
					index_text=index%6-1;
			}
			else
			{
				if(index<LoginActivity.id)
				{
					index_text=index-1;
				}
				else
					index_text=index-2;
			}
			if((index!=LoginActivity.id)&&(index>=BluetoothTest.instance._startIndex)&&
 					(index<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[index]==true))
			{
				if(temp_target == LoginActivity.id)
				{
					if(index==temp_hunter)
					{
						if(!latLngArr[index].equals(new LatLng(0,0)))
						{
		
							BluetoothTest.instance.targetText[index_text].setTextColor(Color.parseColor("#ff0000"));
							BluetoothTest.instance.targetText[index_text].setText("My target:");
							locationB.setLatitude(latLngArr[index].latitude);
					        locationB.setLongitude(latLngArr[index].longitude);
					        Log.e("MyLocation",BluetoothTest.instance.myLong + "    " + BluetoothTest.instance.myLat);
					        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
					        distance[index_text] = locationA.distanceTo(locationB); 
					        BluetoothTest.instance.distanceText[index_text].setTextColor(Color.parseColor("#ff0000"));
					        BluetoothTest.instance.distanceText[index_text].setText(String.valueOf(new DecimalFormat("##.##").format(distance[index_text]))+" m");
					        
					        Log.e("distance","distance"+index_text+":"+String.valueOf(distance[index_text]));
						}
					}
				}
				if(index==BluetoothTest.instance.targetId)
				{
					if(!latLngArr[index].equals(new LatLng(0,0)))
					{
	
						BluetoothTest.instance.targetText[index_text].setTextColor(Color.parseColor("#ff0000"));
						BluetoothTest.instance.targetText[index_text].setText("My target:");
						locationB.setLatitude(latLngArr[index].latitude);
				        locationB.setLongitude(latLngArr[index].longitude);
				        Log.e("MyLocation",BluetoothTest.instance.myLong + "    " + BluetoothTest.instance.myLat);
				        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
				        distance[index_text] = locationA.distanceTo(locationB); 
				        BluetoothTest.instance.distanceText[index_text].setTextColor(Color.parseColor("#ff0000"));
				        BluetoothTest.instance.distanceText[index_text].setText(String.valueOf(new DecimalFormat("##.##").format(distance[index_text]))+" m");
				        
				        Log.e("distance","distance"+index_text+":"+String.valueOf(distance[index_text]));
					}
				}
				else if((index!=BluetoothTest.instance.targetId))
				{
					if(!latLngArr[index].equals(new LatLng(0,0)))
					{
						if(BluetoothTest.instance._detailStage[index]==BluetoothTest.instance._FREE)
						{
							BluetoothTest.instance.targetText[index_text].setTextColor(Color.parseColor("#0000ff"));
							BluetoothTest.instance.targetText[index_text].setText("Player"+index+":");
							locationB.setLatitude(latLngArr[index].latitude);
					        locationB.setLongitude(latLngArr[index].longitude);
					        Log.e("MyLocation",BluetoothTest.instance.myLong + "    " +BluetoothTest.instance.myLat);
					        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
					        distance[index_text] = locationA.distanceTo(locationB);
					        BluetoothTest.instance.distanceText[index_text].setTextColor(Color.parseColor("#0000ff"));
					        BluetoothTest.instance.distanceText[index_text].setText(String.valueOf(new DecimalFormat("##.##").format(distance[index_text]))+" m");
					        Log.e("distance","distance"+index_text+":"+String.valueOf(distance[index_text]));
					        
					        if(LoginActivity.id == index)
					        {
	//				        	BluetoothTest.pre_LatLng = new LatLng(0,0);
					        }
						}
						else
						{
							BluetoothTest.instance.targetText[index_text].setTextColor(Color.parseColor("#ff0000"));
							BluetoothTest.instance.targetText[index_text].setText("Player"+index+":");
							locationB.setLatitude(latLngArr[index].latitude);
					        locationB.setLongitude(latLngArr[index].longitude);
					        Log.e("MyLocation",BluetoothTest.instance.myLong + "    " + BluetoothTest.instance.myLat);
					        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
					        distance[index_text] = locationA.distanceTo(locationB);
					        BluetoothTest.instance.distanceText[index_text].setTextColor(Color.parseColor("#ff0000"));
					        BluetoothTest.instance.distanceText[index_text].setText(String.valueOf(new DecimalFormat("##.##").format(distance[index_text]))+" m");
					        Log.e("distance","distance"+index_text+":"+String.valueOf(distance[index_text]));
						}
					}
				}
			}
			else if((index!=LoginActivity.id)&&(index>=BluetoothTest.instance._startIndex)&&
 					(index<BluetoothTest.instance._endIndex)&&(BluetoothTest.instance._Online[index]==false))
 			{
 				BluetoothTest.instance.validMarker[index] = false;
 				BluetoothTest.instance.targetText[index_text].setTextColor(Color.parseColor("#ff0000"));
 				BluetoothTest.instance.distanceText[index_text].setTextColor(Color.parseColor("#ff0000"));
 				BluetoothTest.instance.targetText[index_text].setText("PlayerOther:");
 				BluetoothTest.instance.distanceText[index_text].setText("Offline");
 				
 			}
			
			
			
		}
		
	}
	public void socHit()
	{
		type_state = socHIT;
		mWebSocketClient.send("{\"type\":\"hit\"}");
	}
	public void socWithdraw()
	{
		type_state = socWITHDRAW;
		try {
			mWebSocketClient.send("{\"type\":\"withdraw\"}");
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("SOCKET", "ERROR DRAW");
		}
		
	}
	private void socLogin() {
	// TODO Auto-generated method stub
		if(BluetoothTest.instance.D)
			Log.e("server","Login");
		type_state = socLOGIN;
		mWebSocketClient.send("{\"type\":\"subscribe\",\"_token\":\""+LoginActivity.token+"\"}");
	};
}
