package com.example.android.BluetoothTest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetHttp extends AsyncTask<String, Void, String>
{
	interface OnPost{
		void onpost(String result);
	}
	Context mContext;
	private OnPost ltn;
	static String resultGet="";
	private HttpClient client = new DefaultHttpClient();
	
	
	public void setOnPost(OnPost mltn){
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
		resultGet = result;

		Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
		if(result!=null && ltn!=null){
			ltn.onpost(result);
		}
		
	}

	@Override
	protected String doInBackground(String[] p1)
	{
		// TODO: Implement this method
		HttpGet http = new HttpGet(p1[0]);
		HttpResponse response;
		try{
			response = client.execute(http);
			result = EntityUtils.toString( response.getEntity());
		}catch(Exception ex){
			Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
			return null;
		}
		return result;
	}

	
	
}