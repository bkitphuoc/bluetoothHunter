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