package com.example.android.BluetoothTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crittercism.app.Crittercism;

public class LoginActivity extends Activity {

	public static int flag_debug = 0;
	public static String token = "";
	public static int id = 0;
	String resultLogin = "";
	static int flag_stop_readResponce = 0;
	static int flag_login_succ = 0;
	Handler handle_reponse;
	static Boolean HTTP_FREE = false;
	static Boolean HTTP_BUZY = true;

	static Boolean flag_getpost = HTTP_FREE;

	static EditText etemail;
	static EditText etpass;

	private ProgressBar mProgress;
	public boolean progressing = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// setting default screen to login.xml
		setContentView(R.layout.login);

		if (flag_debug == 1) {
			Crittercism.initialize(getApplicationContext(),
					"53b3bb7b07229a5a86000006");
			try {
				throw new Exception("Exception Reason");
			} catch (Exception exception) {
				Crittercism.logHandledException(exception);
			}
		}
		// handle_reponse = new Handler();
		etemail = (EditText) findViewById(R.id.UserText);
		etpass = (EditText) findViewById(R.id.PassText);

		Button registerScreen = (Button) findViewById(R.id.btnSignUp);

		// Listening to register new account link
		registerScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				GetHttp test = new GetHttp();
				test.execute("http://54.255.184.201/api/v1/users?_token=IK5M5IL1MO5AhGMQmX4d");

			}
		});

		Button login = (Button) findViewById(R.id.btnSignIn);

		// Listening to Login
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// result ="";

				// TODO Auto-generated method stub
				// Intent choosenTarget_t = new Intent(getApplicationContext(),
				// JoinGameActivity.class);
				// startActivity(choosenTarget_t);

				handle_reponse = new Handler();

				if (flag_getpost == HTTP_FREE) {
					// GetHttp.client.getConnectionManager().shutdown();
					flag_getpost = HTTP_BUZY;
					Log.e("http", "+ HTTP BUZY +");
					flag_login_succ = 0;
					PostHttp.casepost = PostHttp.LOGIN;
					new PostHttp().execute("");
					handle_reponse.postDelayed(new readResponseLogin(), 0);
//					open();
				}

			}
		});
	}

	public class readResponseLogin implements Runnable {
		public void run() {
			// call the service here
			// checkResponse();
			if (flag_getpost == HTTP_FREE) {
				resultLogin = PostHttp.resultPost;
				// if(flag_stop_readResponce<6)
				flag_stop_readResponce++;
				Log.d("post", "Login result:" + resultLogin);
				// tvrespond.setText("response:"+result);
				if (!resultLogin.equals("")) {
					int index = resultLogin.indexOf("token");
					if (index != -1) {
						Log.d("post", "indexOf token:" + index);
						token = resultLogin.substring(index + 8, index + 28);
						Log.d("post", "token:" + token);

						int indexId = resultLogin.indexOf("id");
						if (index != -1) {
							Log.d("post", "indexOf id:" + indexId);
							int indexEnd = resultLogin.indexOf("\"",
									indexId + 5);
							id = Integer.parseInt(resultLogin.substring(
									indexId + 5, indexEnd));
							Log.d("post", "id:" + id);
						}
						flag_login_succ = 1;
						progressing = false;
						if (progressing == false)
							Log.d("progress", "flase");
						resultLogin = "";
						flag_stop_readResponce = 0;
						startActivity(new Intent(LoginActivity.this,
								CircleProgressBar.class));

					} else {
						if (flag_stop_readResponce > 8) {
							progressing = false;
							if (progressing == false)
								Log.d("progress", "flase");
							Toast.makeText(getBaseContext(), "Error Account!",
									Toast.LENGTH_LONG).show();
							flag_stop_readResponce = 0;
							handle_reponse.removeCallbacks(this);
							flag_getpost = HTTP_FREE;
						}

					}
				} else
					handle_reponse.postDelayed(this, 100);
				if (flag_login_succ == 1) {
					handle_reponse.removeCallbacks(this);
					flag_getpost = HTTP_FREE;
				}

			} else if ((flag_getpost == HTTP_BUZY) && (flag_login_succ != 1)) {
				handle_reponse.postDelayed(this, 100);
			} else
				handle_reponse.removeCallbacks(this);
		}
	};

	public void open(){
	   
	   new Thread(
			   
			   new Runnable() {
				   int mProgressStatus = 0;
			   @Override
	           public void run() {
				   mProgress.setVisibility(mProgress.VISIBLE);
	               while (mProgressStatus < 100) {
	            	   try {
	            		   Thread.sleep(5*10);
	                    } catch (InterruptedException e) {
	                      // TODO Auto-generated catch block
	                      e.printStackTrace();
	                    }
	                   mProgressStatus += 5;
	                   mProgress.setProgress(mProgressStatus);
	                   };
           }
       }).start();
   }
}