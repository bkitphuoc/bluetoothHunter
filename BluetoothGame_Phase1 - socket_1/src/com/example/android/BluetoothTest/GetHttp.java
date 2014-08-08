package com.example.android.BluetoothTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.*;
import org.apache.http.util.*;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class GetHttp extends AsyncTask<String, Void, String>
{
	
//	Player Player1,Player2,Player3,Player4,Player5;
//	 Player[] PlayerArr = new Player[5];
	 static Boolean[] _stage = new Boolean[11];
	 static String[] _detailStage = new String[11];
	 static String[] _Long= new String[11];
	 static String[] _Lat = new String[11];
	 static String[] _BTAddress = new String[11];
	 static Boolean[] _Online = new Boolean[11];
	 static int HIT=1;
	 static int FIGHT=2;
	 static int FIGHT_VIEW=3;
	 static int UPDATE = 4;
	 static int LOGOUT = 5;
	 static String _FREE ="free";
	 static String _BE_TARGETED ="be_targeted";
	 static String _HUNTING ="hunting";
	 
	 static Boolean FREE = true;
	 static Boolean NOT_FREE = false;
	 
	 static Boolean flag_update = false;
	
	 static int caseget = 0;
	 static int[] LatIndex = new int[11];
	 static int[] LogIndex = new int[11];
	 static int[] StageIndex = new int[12];
	 static int[] BTAddressIndex = new int[11];
	 static int[] PosIndex = new int[12];
	 static int[] OnlineIndex = new int[11];
	
	 static Boolean choseTarget=false;
	 static int cntUserId;
	
	interface OnPost{
		void onpost(String result);
	}
	Context mContext;
	private static OnPost ltn;
	static String resultGet="";
	static HttpClient client = new DefaultHttpClient();
//	static HttpClient client;
	private HttpGet httpget;
	
	static public void setOnPost(OnPost mltn){
		ltn = mltn;
	}
	private String result = null;
	@Override
	protected void onPreExecute()
	{
		// TODO: Implement this method
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result)
	{
		// TODO: Implement this method
		//resultGet = result;
//		Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
		if(result!=null && ltn!=null){
			ltn.onpost(result);
		}
//		Log.d("post","get result:"+result);
		LoginActivity.flag_getpost=LoginActivity.HTTP_FREE;
		Log.e("http", "+ HTTP FREE +");
		
	}
	
	@Override
	protected String doInBackground(String[] p1)
	{
		// TODO: Implement this method
		httpget = new HttpGet(p1[0]);
		HttpResponse response;
		try{
			response = client.execute(httpget);
			result = EntityUtils.toString( response.getEntity());
		}catch(Exception ex){

			return null;
		}



		
		return result;
	}

	
	
}