package com.example.android.BluetoothTest;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.example.android.BluetoothTest.R;
import com.example.android.BluetoothTest.GetHttp.OnPost;
import com.google.android.gms.plus.model.people.Person;


public class JoinGameActivity extends Activity {
	
	public class Person {
		 
	    private String email;
	    private String password;
	    
	 
	//getters & setters....
	 
	}
	Handler handle_reponse;
	static TextView tvrespond;
	private HttpClient client = new DefaultHttpClient();
	
	
	private static final int LOCATION =0;
	private static final int  TOKEN = 1;
	static String result = "";
	static int flag_stop_readResponce=0;
	static int flag_login_succ = 0;
	private Thread ThreadsLogin;
	Handler handlers;
	GetHttp logout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.joingame);
 
        if(LoginActivity.flag_debug==1)
        {
	        Crittercism.initialize(getApplicationContext(),"53b3bb7b07229a5a86000006");
			try {
				throw new Exception("Exception Reason");
			} catch (Exception exception) {
				Crittercism.logHandledException(exception);
			}
        }
        
        Button btnjoinGame = (Button) findViewById(R.id.btnJoinGame);
        handle_reponse = new Handler();
        Button btnsignout = (Button) findViewById(R.id.btnReq);
//        tvrespond = (TextView)findViewById(R.id.tvRespond);
//        tvrespond.setText("response:");
        
        logout = new GetHttp();
        logout.mContext = this;
        
        // Listening to register new account link
        btnjoinGame.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), BluetoothTest.class);
                startActivity(i);
            }
        });
        
        
        
        btnsignout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
           // call AsynTask to perform network operation on separate thread
				AlertDialog.Builder builder = new AlertDialog.Builder(JoinGameActivity.this);
				builder.setMessage("Are you sure you want to Sign Out?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										LoginActivity.flag_getpost = LoginActivity.HTTP_BUZY;
										GetHttp.resultGet = "";
										Log.e("http", "+ HTTP BUZY LOGOUT +");
										GetHttp.caseget = GetHttp.LOGOUT;
										logout.execute("http://54.255.184.201/api/v1/auth/logout?_token="+LoginActivity.token);
										Log.d("post","get: http://54.255.184.201/api/v1/auth/logout?_token="+LoginActivity.token);
										GetHttp.setOnPost(new OnPost(){
											public void onpost(String result){
												Log.d("post", "Logout result:"+result);
												
										        if(!result.equals(""))
										        {
										 		   int index = result.indexOf("true");
										 		   if(index!=-1)
										 		   {
										 			   Log.d("post", "indexOf:"+index);
											 			startActivity(new Intent(JoinGameActivity.this,LoginActivity.class));								 			  
										 		   }
										 		   else
										 		   {
										 			  Log.e("post", "Logout Error");
										 		   }
										        }
										        else
										        	Log.e("post", "Logout Response Empty");
										        LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
										        Log.e("http", "+ HTTP FREE LOGOUT+");
											}
										});
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
		});
        
        
        

    }
    @Override
	public void onBackPressed() {
//    	logout.execute("http://54.255.184.201/api/v1/auth/logout?_token="+LoginActivity.token);
//		Log.d("post","get: http://54.255.184.201/api/v1/auth/logout?_token="+LoginActivity.token);
//		
//		handle_reponse.postDelayed(new readResponse(), 0); 
		
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to Sign Out?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Log.e("http", "+ HTTP BUZY LOGOUT +");
								LoginActivity.flag_getpost = LoginActivity.HTTP_BUZY;
								GetHttp.resultGet = "";
								logout.execute("http://54.255.184.201/api/v1/auth/logout?_token="+LoginActivity.token);
								Log.d("post","get: http://54.255.184.201/api/v1/auth/logout?_token="+LoginActivity.token);
								GetHttp.setOnPost(new OnPost(){
									public void onpost(String result){
										Log.d("post", "Logout result:"+result);
										
								        if(!result.equals(""))
								        {
								 		   int index = result.indexOf("true");
								 		   if(index!=-1)
								 		   {
								 			   Log.d("post", "indexOf:"+index);
									 			startActivity(new Intent(JoinGameActivity.this,LoginActivity.class));								 			  
								 		   }
								 		   else
								 		   {
								 			  Log.e("post", "Logout Error");
								 		   }
								        }
								        else
								        	Log.e("post", "Logout Response Empty");
								        LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
								        Log.e("http", "+ HTTP BUZY FREE +");
									}
								});
								
//								startActivity(new Intent(JoinGameActivity.this,
//										LoginActivity.class));
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

   
	
}