package com.example.android.BluetoothTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PostHttp extends AsyncTask<String, Void, String> {
	static int LOGIN=1;
	static int TRACKING=2;
	static int casepost;
	static String resultPost = "";
	static String result1 = "";
	static int flag_stop_readResponce=0;
	static int flag_login_succ = 0;
	static Boolean flag_resp = false;
	    @Override
	    protected String doInBackground(String... urls) {
	    	result1="";
	    	if(casepost==LOGIN)
	    		urls[0]="http://54.255.184.201/api/v1/auth/login";
	    	else if(casepost==TRACKING)
	    		urls[0]="http://54.255.184.201/api/v1/tracking?_token="+LoginActivity.token;
	
	        return POST(urls[0]);
	    }
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(String result) {
	//        Toast.makeText(getBaseContext(), "Login Server!", Toast.LENGTH_LONG).show();
	    	
	    	result1 = result;
	    	LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
	    	Log.e("http", "+ HTTP POST FREE +");
	    	Log.d("post", "longitude="+Double.toString(BluetoothTest.myLong) +
	        		",latitude="+Double.toString(BluetoothTest.myLat) +",limit:5");
	    	
	   }
	
	
	public static String POST(String url){
	    InputStream inputStream = null;
	    
	    try {
	    	Log.d("post", "process post");
	        // 1. create HttpClient
	        HttpClient httpclient = new DefaultHttpClient();
	        // 2. make POST request to the given URL
	        HttpPost httpPost = new HttpPost(url);
	
	        String json = "";
	
	        // 3. build jsonObject
	        JSONObject jsonObject = new JSONObject();
	        if(casepost==LOGIN)
	        {
		        jsonObject.put("email",LoginActivity.etemail.getText().toString()+"@domain.com");
//		        jsonObject.put("password",LoginActivity.etpass.getText().toString());
//	        	jsonObject.put("email","user_1@domain.com");
		        jsonObject.put("password","abc123");
		        Log.d("post", "email="+LoginActivity.etemail.getText().toString() + ", pass=" + LoginActivity.etpass.getText().toString());
	        }
	        else if(casepost==TRACKING)
	        {
	        	jsonObject.put("longitude", Double.toString(BluetoothTest.myLong) );
		        jsonObject.put("latitude",Double.toString(BluetoothTest.myLat) );
		        jsonObject.put("limit","5");
		        Log.d("post", "longitude="+Double.toString(BluetoothTest.myLong) +
		        		",latitude="+Double.toString(BluetoothTest.myLat) +",limit:5");
	        }
	        // 4. convert JSONObject to JSON to String
	        json = jsonObject.toString();
	
	        // 5. set json to StringEntity
	        StringEntity se = new StringEntity(json);
	
	        // 6. set httpPost Entity
	        httpPost.setEntity(se);
	
	        // 7. Set some headers to inform server about the type of the content   
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setHeader("Content-type", "application/json");
	
	        // 8. Execute POST request to the given URL
	        HttpResponse httpResponse = httpclient.execute(httpPost);
	
	        HttpEntity entity = httpResponse.getEntity();
	
	        // 9. convert inputstream to string
	        if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}
	
				// convert content stream to a String
				 resultPost= convertInputStreamToString(instream);
				instream.close();
				resultPost = resultPost.substring(1,resultPost.length()-1); // remove wrapping "[" and "]"
			} 
	        else
	        	resultPost = "Did not work!";
	        
	//        handle_reponse.postDelayed(new readResponse(), 0);  
	//        Log.d("post", "result:"+result);
	    } catch (Exception e) {
	        Log.d("post", e.getLocalizedMessage());
	        Log.d("post", "cannot post");
	    }
	    LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
	    // 10. return result
	    return resultPost;
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	    String line = "";
	    String result = "";
	    while((line = bufferedReader.readLine()) != null)
	        result += line;
	
	    inputStream.close();
	    return result;
	
	}  
	

}