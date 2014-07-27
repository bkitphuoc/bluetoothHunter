package com.example.android.BluetoothTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.example.android.BluetoothTest.R;


public class LoginActivity extends Activity {
	
	private static final String TAG = "Login";
	private static final boolean D = true;
	
	public static int flag_debug = 1;
	public static String token="";
	public static int id=0;
	static String resultLogin = "";
	static int flag_stop_readResponce=0;
	static int flag_login_succ = 0;
	Handler handle_reponse;
	static Boolean HTTP_FREE = false;
	static Boolean HTTP_BUZY = true;
	
	static Boolean flag_getpost = HTTP_FREE;
	private BluetoothAdapter mBluetoothAdapter = null;
	private TextView localMacA;
	private static final int REQUEST_ENABLE_BT = 3;
	
	static EditText etemail;
	static EditText etpass;
	static String myMacAddress;
	
	int backButtonCount=0;
	private ProgressBar mProgress;
	public static boolean progressing = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        // setting default screen to login.xml
        setContentView(R.layout.login);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myMacAddress = mBluetoothAdapter.getAddress();
        if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
 
        if(flag_debug==1)
        {
	        Crittercism.initialize(getApplicationContext(),"53b3bb7b07229a5a86000006");
			try {
				throw new Exception("Exception Reason");
			} catch (Exception exception) {
				Crittercism.logHandledException(exception);
			}
        }
        etemail = (EditText) findViewById(R.id.UserText);
        etpass = (EditText) findViewById(R.id.PassText);
        
        
        Button registerScreen = (Button) findViewById(R.id.btnSignUp);
 
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
        
        Button login = (Button) findViewById(R.id.btnSignIn);
       
        // Listening to Login
        login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handle_reponse = new Handler();
				
				if(flag_getpost==HTTP_FREE)
				{
					flag_getpost = HTTP_BUZY;
					Log.e("http", "+ HTTP BUZY +");
					flag_login_succ = 0;
					PostHttp.casepost = PostHttp.LOGIN;
					new PostHttp().execute("");
					startActivity(new Intent(LoginActivity.this,
							CircleProgressBar.class));
					finish();
				}
                
			}
		});
    }

    @Override
	public void onBackPressed() {
    	if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }

	}

   
}