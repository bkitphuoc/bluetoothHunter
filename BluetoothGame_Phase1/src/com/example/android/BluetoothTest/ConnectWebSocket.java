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
	
	public static WebSocketClient mWebSocketClient;
	static Boolean flag_proc_mess = false;
    static String type_value="";
    static int type_state = ConnectWebSocket.socNONE;
    private Activity activity;
//
//	// TODO Auto-generated method stub
    
//    public static synchronized AppController getInstance() {
//    	  return mInstance;
//    	 }
   
    
	public void connect(Activity _activity)
	{
		activity = _activity;
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
        		BluetoothTest.isTracking = true;
            }

            @Override
            public void onMessage(String s) {
            	if(flag_proc_mess==false)
            	{
            		messageFromServer = s;
            	}
               activity.runOnUiThread(new Runnable() {
                    @SuppressLint("NewApi")
					public void run() {
                    	Log.e("all","all type"+messageFromServer);
                    	flag_proc_mess = true;
                    	int temp = 0;
                    	int temp_hunter=0; 
                    	int temp_target=0;
                    	int index_type_value = 0;
                    	int index_type = messageFromServer.indexOf("type");                    	
                    	index_type_value=messageFromServer.indexOf("\"",index_type+7);
                    	type_value = messageFromServer.substring(index_type+7,index_type_value);
                    	if(type_value.equals(TYPE_SUBSCRIBE))
                    	{
                    		Log.d("socket","Subscribe:"+messageFromServer);
                    		type_state = socLOGIN;
                    		String _id_value="";
                    		int _index = messageFromServer.indexOf("id");
                    		int _index1 = 0;
                    		if(_index!=-1)
                    		{
                    			BluetoothTest.flag_stop_game = true;
                    			_index1 = messageFromServer.indexOf("\"", _index+5);
                    			_id_value = messageFromServer.substring(_index+5, _index1);
                    			
                    			temp = Integer.parseInt(_id_value);
                    			Log.e("new user","new user:"+temp);
                    			if((temp!=LoginActivity.id)&& (temp>=BluetoothTest._startIndex) && (temp<BluetoothTest._endIndex))
                    			{
                    				Log.e("subscribe","-----------Enter---------------");
                    				String stage ="stage";
                					String address="bluetooth_address";
                					String pos = "position";
                					
                					int index = messageFromServer.indexOf(pos);
                					int index1 = 0;
                					
                					if(index!=-1)
                					{
                						BluetoothTest.PosIndex[temp]=index;
                					}
                					
//                					index = messageFromServer.indexOf(stage);
//                					if(index!=-1)
//                					{
//                						BluetoothTest.StageIndex[temp]=index;
//                					}
                					index = messageFromServer.indexOf(address);
                					if(index!=-1)
                					{
                						BluetoothTest.BTAddressIndex[temp]=index;
                					}
                					BluetoothTest._Online[temp]=true;
                					BluetoothTest._stage[temp] = BluetoothTest.FREE;
                					
                					if(!messageFromServer.substring(BluetoothTest.PosIndex[temp]+10,BluetoothTest.PosIndex[temp]+14).equals("null"))
    								{
    									
    									index=messageFromServer.indexOf("\"",BluetoothTest.PosIndex[temp]+23);
    									BluetoothTest._Lat[temp]=messageFromServer.substring(BluetoothTest.PosIndex[temp]+23,index);
    									index1 = index+15;
    									index = messageFromServer.indexOf("\"",index1);
    									BluetoothTest._Long[temp]=messageFromServer.substring(index1,index);
    									
    									Log.d("post", "Lat_player"+temp+":"+BluetoothTest._Lat[temp]);
    									Log.d("post", "Long_player"+temp+":"+BluetoothTest._Long[temp]);
    								}
    								else
    								{
    									BluetoothTest._Lat[temp]="null";
    									BluetoothTest._Long[temp]="null";
    								}
                					
//                					if(BluetoothTest.StageIndex[temp]!=-1)
//        							{
//        								Log.d("status","\n stage index"+temp+":"+messageFromServer.substring(BluetoothTest.StageIndex[temp]+8,BluetoothTest.StageIndex[temp]+12));
//        								index = messageFromServer.indexOf("\"",BluetoothTest.StageIndex[temp]+8);
//        								if(messageFromServer.substring(BluetoothTest.StageIndex[temp]+8,index).equals("free"))
//        								{
//        									BluetoothTest._detailStage[temp] = BluetoothTest._FREE;
//        									BluetoothTest._stage[temp] = BluetoothTest.FREE;
//        									
//        								}
//        								
//        								Log.d("post", "Stage_player"+temp+":"+BluetoothTest._detailStage[temp]);
//        							}
                					
                					if(BluetoothTest.BTAddressIndex[temp]!=-1)
        							{
        								index=messageFromServer.indexOf(",",BluetoothTest.BTAddressIndex[temp]+19);
        								if(!messageFromServer.substring(BluetoothTest.BTAddressIndex[temp]+19,index).equals("null"))
        								{
        									index=messageFromServer.indexOf("\"",BluetoothTest.BTAddressIndex[temp]+20);
        									Log.d("address","\n BTAddress index"+temp+":"+messageFromServer.substring(BluetoothTest.BTAddressIndex[temp]+20,index));
        									BluetoothTest._BTAddress[temp]=messageFromServer.substring(BluetoothTest.BTAddressIndex[temp]+20,index);
        								}
        								else
        								{
        									BluetoothTest._BTAddress[temp]="null";
        									Log.d("address","\n BTAddress index"+temp+":"+messageFromServer.substring(BluetoothTest.BTAddressIndex[temp]+19,index));
        								}
        								Log.d("post", "BTAddress_player"+temp+":"+BluetoothTest._BTAddress[temp]);
        							}
                    			}
                    			else
                    				{
                    					for(int index=0;index<BluetoothTest._endIndex;index++)
                    					{
                    						BluetoothTest.validMarker[index]=false;
                    					}
                    				
                    				}
                    		}
                    		else
                    		{
                    			BluetoothTest.flag_stop_game = false;
                    		}
        					
        					BluetoothTest.flag_update = true;
        					
                    	
                     	
                    	}
                    	else if(type_value.equals(TYPE_TRACKING))
                    	{

                    		Log.d("socket","Tracking:"+messageFromServer);
                    		type_state = socTRACKING;
        					String stage ="stage";
        					String address="bluetooth_address";
        					String pos = "position";
        					String onl = "is_online";

        					BluetoothTest.flag_stop_game = true;
        					int index = messageFromServer.indexOf(pos);
        					int index1 = 0;
        					int i = 1;
        					while(index!=-1)
        					{
        						Log.e("status", "++ STATUS ++");
        						BluetoothTest.PosIndex[i]=index;
        					    Log.d("status","\n position index"+i+":"+index);
        					    index = messageFromServer.indexOf(pos, index + 1);			    
        					    i++;
        					}
        					index = messageFromServer.indexOf(stage);
        					i=1;
        					while(index != -1) {
        						Log.e("status", "++ STATUS ++");
        						BluetoothTest.StageIndex[i]=index;
        					    Log.d("status","\n stage index"+i+":"+index);
        					    index = messageFromServer.indexOf(stage, index + 1);			    
        					    i++;
        					}
        					index = messageFromServer.indexOf(address);
        					i=1;
        					while(index != -1) {
        						Log.e("address", "++ Bluetooth Address ++");
        						BluetoothTest.BTAddressIndex[i]=index;
        					    Log.d("address","\n Bluetooth Address index"+i+":"+index);
        					    index = messageFromServer.indexOf(address, index + 1);			    
        					    i++;
        					}
        					index = messageFromServer.indexOf(onl);
        					i=1;
        					while(index != -1) {
        						Log.e("online", "++ Online state ++");
        						BluetoothTest.OnlineIndex[i]=index;
        					    Log.d("online","\n Online index"+i+":"+index);
        					    index = messageFromServer.indexOf(onl, index + 1);			    
        					    i++;
        					}
        					for(i=BluetoothTest._startIndex;i<BluetoothTest._endIndex;i++)
        					{
        						
        						if(i!= LoginActivity.id)
        						{
        							if(BluetoothTest.OnlineIndex[i]!=-1)
        							{
        								index=messageFromServer.indexOf("\"",BluetoothTest.OnlineIndex[i]+12);
        								Log.d("post", "online value"+i+":"+index);
        								Log.d("post", "online value"+i+":"+messageFromServer.substring(BluetoothTest.OnlineIndex[i]+12,BluetoothTest.OnlineIndex[i]+13));
//        								if(!messageFromServer.substring(BluetoothTest.BTAddressIndex[i]+12,index).equals("1"))
        								if(messageFromServer.substring(BluetoothTest.OnlineIndex[i]+12,BluetoothTest.OnlineIndex[i]+13).equals("1"))
        								{
        									BluetoothTest._Online[i]=true;
        								}
        								else
        								{
        									BluetoothTest._Online[i]=false;
        								}
        								Log.d("post", "online state"+i+":"+BluetoothTest._Online[i]);
        							}
        						}
        					}
        					for(i=BluetoothTest._startIndex;i<BluetoothTest._endIndex;i++)
        					{
        						if(i!= LoginActivity.id)
        						{
        							if(BluetoothTest.PosIndex[i+1]!=-1)
        							{
        								Log.d("status","\n Postion index"+i+":"+messageFromServer.substring(BluetoothTest.PosIndex[i+1]+10,BluetoothTest.PosIndex[i+1]+14));
        								if(!messageFromServer.substring(BluetoothTest.PosIndex[i+1]+10,BluetoothTest.PosIndex[i+1]+14).equals("null"))
        								{
        									
        									index=messageFromServer.indexOf("\"",BluetoothTest.PosIndex[i+1]+23);
        									BluetoothTest._Lat[i]=messageFromServer.substring(BluetoothTest.PosIndex[i+1]+23,index);
        									index1 = index+15;
        									index = messageFromServer.indexOf("\"",index1);
        									BluetoothTest._Long[i]=messageFromServer.substring(index1,index);
        									
        									Log.d("post", "Lat_player"+i+":"+BluetoothTest._Lat[i]);
        									Log.d("post", "Long_player"+i+":"+BluetoothTest._Long[i]);
        								}
        								else
        								{
        									BluetoothTest._Lat[i]="null";
        									BluetoothTest._Long[i]="null";
        								}
        								
        							}
        						}
        					}
        					
        				
        					for(i=BluetoothTest._startIndex;i<BluetoothTest._endIndex;i++)
        					{
//        						if(i!=LoginActivity.id)
        						{
        							if(BluetoothTest.StageIndex[i]!=-1)
        							{
        								Log.d("status","\n stage index"+i+":"+messageFromServer.substring(BluetoothTest.StageIndex[i]+8,BluetoothTest.StageIndex[i]+12));
        								index = messageFromServer.indexOf("\"",BluetoothTest.StageIndex[i]+8);
        								if(messageFromServer.substring(BluetoothTest.StageIndex[i]+8,index).equals("free"))
        								{
        									BluetoothTest._detailStage[i] = BluetoothTest._FREE;
        									BluetoothTest._stage[i] = BluetoothTest.FREE;
        									if(i==LoginActivity.id)
        									{
        										BluetoothTest.flag_vibra = false;
        										BluetoothTest.role = BluetoothTest.NO_PLAY;
        										
        										if(BluetoothTest.mWithdrawButton.getVisibility()==View.VISIBLE)
        											BluetoothTest.mWithdrawButton.setVisibility(View.INVISIBLE);
        									}
        									
        									
        								}
        								else if(messageFromServer.substring(BluetoothTest.StageIndex[i]+8,index).equals("be_targeted"))
        								{
        									BluetoothTest._stage[i]=BluetoothTest.NOT_FREE;
        									BluetoothTest._detailStage[i] = BluetoothTest._BE_TARGETED;	
        									if(i==LoginActivity.id)
        									{
        										BluetoothTest.role = BluetoothTest.TARGET;
        									}
        								}
        								else if(messageFromServer.substring(BluetoothTest.StageIndex[i]+8,index).equals("hunting"))
        								{
        									BluetoothTest._stage[i]=BluetoothTest.NOT_FREE;
        									BluetoothTest._detailStage[i] = BluetoothTest._HUNTING;
        									if(i==LoginActivity.id)
        									{
        										BluetoothTest.role = BluetoothTest.HUNTER;
        									}
        									
        								}
        								Log.d("post", "Stage_player"+i+":"+BluetoothTest._detailStage[i]);
        							}
        						}
        						
        					}
        					if((BluetoothTest._stage[LoginActivity.id]==BluetoothTest.NOT_FREE)&&(BluetoothTest.flag_play==false))
        					{
        						BluetoothTest.mWithdrawButton.setVisibility(View.VISIBLE);
        						BluetoothTest.handle_shooting.post(BluetoothTest.updateStateButton);
        						BluetoothTest.flag_play = true;
        					}
        					
        					for(i=BluetoothTest._startIndex;i<BluetoothTest._endIndex;i++)
        					{
        						
        						if(i!= LoginActivity.id)
        						{
        							if(BluetoothTest.BTAddressIndex[i]!=-1)
        							{
        								index=messageFromServer.indexOf(",",BluetoothTest.BTAddressIndex[i]+19);
        								if(!messageFromServer.substring(BluetoothTest.BTAddressIndex[i]+19,index).equals("null"))
        								{
        									index=messageFromServer.indexOf("\"",BluetoothTest.BTAddressIndex[i]+20);
        									Log.d("address","\n BTAddress index"+i+":"+messageFromServer.substring(BluetoothTest.BTAddressIndex[i]+20,index));
        									BluetoothTest._BTAddress[i]=messageFromServer.substring(BluetoothTest.BTAddressIndex[i]+20,index);
        								}
        								else
        								{
        									BluetoothTest._BTAddress[i]="null";
        									Log.d("address","\n BTAddress index"+i+":"+messageFromServer.substring(BluetoothTest.BTAddressIndex[i]+19,index));
        								}
        								Log.d("post", "BTAddress_player"+i+":"+BluetoothTest._BTAddress[i]);
        							}
        						}
        					}
        					
        					BluetoothTest.flag_update = true;
        					
        				
                    	
                    	}
                    	else if((type_value.equals(TYPE_FIGHT))||(type_value.equals(TYPE_HIT))||(type_value.equals(TYPE_WITHDRAW)))
                    	{
                    		Log.d("socket","hunter target:"+messageFromServer);
                    		
                    		type_state = socFIGHT;
                    		String _id_value="";
                    		int _index = messageFromServer.indexOf("hunter");
                    		int _index1 = 0;
                    		if(_index!=-1)
                    		{
                    			if((type_value.equals(TYPE_FIGHT))&&(BluetoothTest.choseTarget == true))
                    			{
                    				
                    				BluetoothTest.targetId = BluetoothTest.cntUserId;
									Log.d("fight","target id:"+BluetoothTest.targetId);
									BluetoothTest.device = BluetoothTest.mBluetoothAdapter.getRemoteDevice(BluetoothTest._BTAddress[BluetoothTest.cntUserId]);
//									if(BluetoothTest.instance.device.getType()==BluetoothTest.device.DEVICE_TYPE_CLASSIC)
										BluetoothTest.mChatService.connect(BluetoothTest.device, true);		
									BluetoothTest.rssi_value.setText("Out of range");
									BluetoothTest.role=BluetoothTest.HUNTER;
									BluetoothTest.mWithdrawButton.setVisibility(View.VISIBLE);
//									Toast.makeText(BluetoothTest.this,"Your target is chosen: player", Toast.LENGTH_LONG).show(); 
								
                    			}
                    			BluetoothTest.flag_stop_game = true;
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
	                    		if(temp_target>=BluetoothTest._startIndex && temp_target<BluetoothTest._endIndex)
	                    		{
	                    			String pos = "position";
		        					
		        					_index = messageFromServer.indexOf(pos);
		        					_index1 = 0;
		
		        					if(_index!=-1)
		        					{
		        						BluetoothTest.PosIndex[temp_hunter]=_index;
		        					}
		        					_index = messageFromServer.indexOf(pos, _index+1);
		        					if(_index!=-1)
		        						BluetoothTest.PosIndex[temp_target]=_index;
		        					
		        					BluetoothTest._Online[temp_hunter]=true;
		        					BluetoothTest._Online[temp_target]=true;
		        					if(type_value.equals(TYPE_FIGHT))
									{
		        						BluetoothTest._stage[temp_hunter] = BluetoothTest.NOT_FREE;
			        					BluetoothTest._stage[temp_target] = BluetoothTest.NOT_FREE;
			        					BluetoothTest._detailStage[temp_hunter]=BluetoothTest._HUNTING;
			        					BluetoothTest._detailStage[temp_target]=BluetoothTest._BE_TARGETED;
									}
		        					else if(type_value.equals(TYPE_HIT)||type_value.equals(TYPE_WITHDRAW))
		        					{
			        					BluetoothTest._stage[temp_hunter] = BluetoothTest.FREE;
			        					BluetoothTest._stage[temp_target] = BluetoothTest.FREE;
		        					}
		        					
		        					
		        					if(!messageFromServer.substring(BluetoothTest.PosIndex[temp_hunter]+10,BluetoothTest.PosIndex[temp_hunter]+14).equals("null"))
									{
										
										_index=messageFromServer.indexOf("\"",BluetoothTest.PosIndex[temp_hunter]+23);
										BluetoothTest._Lat[temp_hunter]=messageFromServer.substring(BluetoothTest.PosIndex[temp_hunter]+23,_index);
										_index1 = _index+15;
										_index = messageFromServer.indexOf("\"",_index1);
										BluetoothTest._Long[temp_hunter]=messageFromServer.substring(_index1,_index);
										
										Log.d("post", "Lat_player"+temp_hunter+":"+BluetoothTest._Lat[temp_hunter]);
										Log.d("post", "Long_player"+temp_hunter+":"+BluetoothTest._Long[temp_hunter]);
									}
									else
									{
										BluetoothTest._Lat[temp_hunter]="null";
										BluetoothTest._Long[temp_hunter]="null";
									}
		        					
		        					if(!messageFromServer.substring(BluetoothTest.PosIndex[temp_target]+10,BluetoothTest.PosIndex[temp_target]+14).equals("null"))
									{
										
										_index=messageFromServer.indexOf("\"",BluetoothTest.PosIndex[temp_target]+23);
										BluetoothTest._Lat[temp_target]=messageFromServer.substring(BluetoothTest.PosIndex[temp_target]+23,_index);
										_index1 = _index+15;
										_index = messageFromServer.indexOf("\"",_index1);
										BluetoothTest._Long[temp_target]=messageFromServer.substring(_index1,_index);
										
										Log.d("post", "Lat_player"+temp_target+":"+BluetoothTest._Lat[temp_target]);
										Log.d("post", "Long_player"+temp_target+":"+BluetoothTest._Long[temp_target]);
									}
									else
									{
										BluetoothTest._Lat[temp_target]="null";
										BluetoothTest._Long[temp_target]="null";
									}
		        					
		        					for(int index2=BluetoothTest._startIndex;index2<BluetoothTest._endIndex;index2++)
		        					{
		        						BluetoothTest.validMarker[index2]=false;
		        					}
		        					BluetoothTest.validMarker[temp_hunter]=true;
		        					BluetoothTest.validMarker[temp_target]=true;
	                    		}
	        					
                    		}
                    		else
                    		{
                    			if(BluetoothTest.targetId == BluetoothTest.cntUserId)
                    			{
                    				BluetoothTest.choseTarget = false;
                    				BluetoothTest.targetId = 0;
                    			}
                    			BluetoothTest.flag_stop_game = false;
                    		}
        					
        					
        					BluetoothTest.flag_update = true;
        					
                    	
                     	
                    	
                    	}
//                    	else if(type_value.equals(type_hit))
//                    	{
//                    		Log.d("socket","hit:"+messageFromServer);
//                    	}
//                    	else if(type_value.equals(type_withdraw))
//                    	{
//                    		Log.d("socket","withdraw:"+messageFromServer);
//                    	}
                    	
                    	//update state marker, distance
            			Location locationA = new Location("point A");
            			locationA.setLongitude(BluetoothTest.myLong);
            			locationA.setLatitude(BluetoothTest.myLat);
            			Location locationB = new Location("point B");
            			float distance[] = new float[4];
            			
            			BitmapDescriptor bitmapDesFree = BitmapDescriptorFactory.fromResource(R.drawable.pin_blue);
            			BitmapDescriptor bitmapDesNotFree = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);
//            	        BitmapDescriptor bitmapDesFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
//            	        BitmapDescriptor bitmapDesNotFree = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            	        Log.d("update", "+ Flag update:"+BluetoothTest.flag_update);
            	        int flag_hunter = 0;
            	        if(BluetoothTest.flag_update==true)
            	        {
            	        	for(int index2=BluetoothTest._startIndex;index2<BluetoothTest._endIndex;index2++)
        					{
            	        		BluetoothTest.validMarker[index2]=false;
        					}
//            	        	if(type_state == socTRACKING)
            	        	if(BluetoothTest.flag_stop_game==true)
            	        	{
            	        		BluetoothTest.mMap.clear();
            	        		BluetoothTest.myLocation = BluetoothTest.mMap.addMarker(new MarkerOptions()
	            		        .position(new LatLng(BluetoothTest.myLat,BluetoothTest.myLong))
	            		        .title("me")
	            		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_arrow_blue)));
	            	        	
	            	        	LatLng[] latLngArr = new LatLng[11];
	            	        	flag_hunter=0;
	            	        	for(int index=BluetoothTest._startIndex;index<BluetoothTest._endIndex;index++)
	            	        	{
	            	        		if(type_state==socLOGIN)
	            	        		{
	            	        			Log.e("type_state","login id:"+temp);
//	            	        			if(temp>=_startIndex && temp<_endIndex)
	            	        			{
	            	        				index = temp;
	            	        			}
//	            	        			else
//	            	        				return;
	            	        		}
	            	        		else if(type_state==socFIGHT)
	            	        		{
	            	        			Log.e("type_state","fight id:hunter:"+temp_hunter+",target:"+temp_target);
//	            	        			if(temp_hunter>=_startIndex && temp_hunter < _endIndex)
	            	        			{
		            	        			if(flag_hunter==0)
		            	        			{
		            	        				index=temp_hunter;
		            	        				flag_hunter = 1;
		            	        			}
		            	        			else
		            	        				index=temp_target;
	            	        			}
//	            	        			else
//	            	        				return;
	            	        		}
	            	        		Log.d("index","index"+index);
	            	        		if((index!=LoginActivity.id)&&(index>=BluetoothTest._startIndex)&&(index<BluetoothTest._endIndex))
	            	        		{
	            	        			try
	            	    	        	{
//	            	        				Log.e("location","index:"+index+
//	            	        							"lat:"+BluetoothTest._Lat[index]+",long:"+BluetoothTest._Long[index]);
	            	        				if(!BluetoothTest._Lat[index].equals("null"))
	            	        				{
	            	        					latLngArr[index]= new LatLng(Double.parseDouble(BluetoothTest._Lat[index]),Double.parseDouble(BluetoothTest._Long[index]));
	            	        				}
	            	        				else
	            	        					latLngArr[index]= new LatLng(0,0);
	            	    	        	}
	            	    	        	catch(NumberFormatException e)
	            	    	        	{
	            	    	        	  //not a double
	            	    	        		latLngArr[index]= new LatLng(0,0);
	            	    	        	}
	            	        			
	            	        		}
	            	        		if(type_state==socLOGIN)
	            	        		{
	            	        			index = BluetoothTest._endIndex;
	            	        		}
	            	        		if((type_state==socFIGHT)&&(flag_hunter==1))
	            	     			{
	            	     				index = BluetoothTest._endIndex;
	            	     			}
	            	        		
	            	        	}
	            	        	flag_hunter = 0;
	            	        	int j = 0;
	            				for(int index=BluetoothTest._startIndex;index<BluetoothTest._endIndex;index++)
	            				{
	            					if(type_state==socLOGIN)
	            	        		{
	            	        			Log.e("type_state","login id:"+temp);
//	            	        			if(temp>=_startIndex && temp<_endIndex)
	            	        			{
	            	        				index = temp;
	            	        			}
//	            	        			else
//	            	        				return;
	            	        		}
	            	        		else if(type_state==socFIGHT)
	            	        		{
	            	        			Log.e("type_state","fight id:hunter:"+temp_hunter+",target:"+temp_target);
//	            	        			if(temp_hunter>=_startIndex && temp_hunter < _endIndex)
	            	        			{
		            	        			if(flag_hunter==0)
		            	        			{
		            	        				index=temp_hunter;
		            	        				flag_hunter = 1;
		            	        			}
		            	        			else
		            	        				index=temp_target;
	            	        			}
//	            	        			else
//	            	        				return;
	            	        		}
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
	            					
	            	     			if((index!=LoginActivity.id)&&(index>=BluetoothTest._startIndex)&&
	            	     					(index<BluetoothTest._endIndex)&&(BluetoothTest._Online[index]==true))
	            					{
	            						if(BluetoothTest._stage[index]==BluetoothTest.FREE)
	            				        {
	            							if(!latLngArr[index].equals(new LatLng(0,0)))
	            							{
	            							
	            								if(index==BluetoothTest.targetId)
	            								{
	            									BluetoothTest.targetId = 0;
	            									BluetoothTest.choseTarget = false;
	            								}
	            								BluetoothTest.MarkerArr[index]= BluetoothTest.mMap.addMarker(new MarkerOptions()
	            						        .position(latLngArr[index])
	            						        .icon(bitmapDesFree)
	            						        .title("player"+index));
	            								BluetoothTest.MarkerArr[index].showInfoWindow();
	            						        
	            								BluetoothTest.validMarker[index] = true;
	            							}
	            							else
	            								BluetoothTest.validMarker[index] = false;
	            							
	            				        }
	            				        else
	            				        {
	            				        	if(!latLngArr[index].equals(new LatLng(0,0)))
	            				        	{
	            				        		BluetoothTest.MarkerArr[index]=BluetoothTest.mMap.addMarker(new MarkerOptions()
	            						        .position(latLngArr[index])
	            						        .icon(bitmapDesNotFree)
	            						        .title("player"+index));
	            				        		BluetoothTest.MarkerArr[index].showInfoWindow();
	            				        		BluetoothTest.validMarker[index] = true;
	            				        	}
	            				        	else
	            				        		BluetoothTest.validMarker[index] = false;
	            				        	
	            					        
	            				        }
	            						
	            						if(index==BluetoothTest.targetId)
	            						{
	            							if(!latLngArr[index].equals(new LatLng(0,0)))
	            							{
	
	            								BluetoothTest.targetText[j].setTextColor(Color.parseColor("#ff0000"));
	            								BluetoothTest.targetText[j].setText("My target:");
	            								locationB.setLatitude(latLngArr[index].latitude);
	            						        locationB.setLongitude(latLngArr[index].longitude);
	            						        Log.e("MyLocation",BluetoothTest.myLong + "    " + BluetoothTest.myLat);
	            						        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
	            						        distance[j] = locationA.distanceTo(locationB); 
	            						        BluetoothTest.distanceText[j].setTextColor(Color.parseColor("#ff0000"));
	            						        BluetoothTest.distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
	            						        
	            						        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
	            							}
	            						}
	            						else
	            						{
	            							if(!latLngArr[index].equals(new LatLng(0,0)))
	            							{
	            								if(BluetoothTest._stage[index]==BluetoothTest.FREE)
	            								{
	            									BluetoothTest.targetText[j].setTextColor(Color.parseColor("#0000ff"));
	            									BluetoothTest.targetText[j].setText("Player"+index+":");
	            									locationB.setLatitude(latLngArr[index].latitude);
	            							        locationB.setLongitude(latLngArr[index].longitude);
	            							        Log.e("MyLocation",BluetoothTest.myLong + "    " +BluetoothTest.myLat);
	            							        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
	            							        distance[j] = locationA.distanceTo(locationB);
	            							        BluetoothTest.distanceText[j].setTextColor(Color.parseColor("#0000ff"));
	            							        BluetoothTest.distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
	            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
	            							        
	            							        if(LoginActivity.id == index)
	            							        {
//	            							        	BluetoothTest.pre_LatLng = new LatLng(0,0);
	            							        }
	            								}
	            								else
	            								{
	            									BluetoothTest.targetText[j].setTextColor(Color.parseColor("#ff0000"));
	            									BluetoothTest.targetText[j].setText("Player"+index+":");
	            									locationB.setLatitude(latLngArr[index].latitude);
	            							        locationB.setLongitude(latLngArr[index].longitude);
	            							        Log.e("MyLocation",BluetoothTest.myLong + "    " + BluetoothTest.myLat);
	            							        Log.e("OtherLocation",latLngArr[index].longitude + "    " + latLngArr[index].latitude);
	            							        distance[j] = locationA.distanceTo(locationB);
	            							        BluetoothTest.distanceText[j].setTextColor(Color.parseColor("#ff0000"));
	            							        BluetoothTest.distanceText[j].setText(String.valueOf(new DecimalFormat("##.##").format(distance[j]))+" m");
	            							        Log.e("distance","distance"+j+":"+String.valueOf(distance[j]));
	            								}
	//            								pre_LatLng = new LatLng(0,0);
	            							}
	            						}
//	            				        j++;
	            					}
	            	     			else if((index!=LoginActivity.id)&&(index>=BluetoothTest._startIndex)&&
	            	     					(index<BluetoothTest._endIndex)&&(BluetoothTest._Online[index]==false))
	            	     			{
	            	     				BluetoothTest.validMarker[index] = false;
	            	     				BluetoothTest.targetText[j].setTextColor(Color.parseColor("#ff0000"));
	            	     				BluetoothTest.distanceText[j].setTextColor(Color.parseColor("#ff0000"));
	            	     				BluetoothTest.targetText[j].setText("PlayerOther:");
	            	     				BluetoothTest.distanceText[j].setText("Offline");
	            	     				
	            	     			}
	            	     			if(BluetoothTest.showInfo==false)
	            	     			{
	            	     				BluetoothTest.showInfo = true;
	            	     				BluetoothTest.handle_info.postDelayed(BluetoothTest.instance.ShowInfor, 0);
	            	     				Log.e("Info","Show infor");
	            	     			}
	            	     			
	            	     			if(type_state==socLOGIN)
	            	        		{
	            	        			index = BluetoothTest._endIndex;
	            	        		}
	            	     			if((type_state==socFIGHT)&&(flag_hunter==1))
	            	     			{
	            	     				index = BluetoothTest._endIndex;
	            	     			}
	            					
	            				}
	            				BluetoothTest.flag_stop_game = false;
            	        	}
            	        	else
            	        	{
            	        		if(type_value==TYPE_FIGHT)
            	        		{
            	        			;
            	        		}
//            	        			Toast.makeText(BluetoothTest.class,"Please wait!", Toast.LENGTH_SHORT)
//            	        				.show();
            	        	}
            
            		        BluetoothTest.flag_update = false;
            		        Log.d("update", "+ update completed");
            		        Log.d("update", "+ Flag update:"+BluetoothTest.flag_update);
            		        
            		        type_state = socNONE;
            		        
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
	
	public void socHit()
	{
		type_state = ConnectWebSocket.socHIT;
		mWebSocketClient.send("{\"type\":\"hit\"}");
	}
	public void socWithdraw()
	{
		type_state = ConnectWebSocket.socWITHDRAW;
		try {
			mWebSocketClient.send("{\"type\":\"withdraw\"}");
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("SOCKET", "ERROR DRAW");
		}
		
	}
	private void socLogin() {
	// TODO Auto-generated method stub
		if(BluetoothTest.D)
			Log.e("server","Login");
		type_state = socLOGIN;
		mWebSocketClient.send("{\"type\":\"subscribe\",\"_token\":\""+LoginActivity.token+"\"}");
	};
}
