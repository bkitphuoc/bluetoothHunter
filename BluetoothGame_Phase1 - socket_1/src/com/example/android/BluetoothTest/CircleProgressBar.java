package com.example.android.BluetoothTest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CircleProgressBar extends Activity {
	String tokenId = "token";
	Handler handle_reponse;
	
	
	@Override
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        
		   
        setContentView(R.layout.circle_progress_bar);
        handle_reponse = new Handler();

        ProgressBar mProgress = (ProgressBar) findViewById(R.id.circlePB);
        handle_reponse.postDelayed(new readResponseLogin(), 0);
	}

	
	public void end(){
        startActivity(new Intent(CircleProgressBar.this,
				JoinGameActivity.class));
		Toast.makeText(getBaseContext(), "Login Success!",
				Toast.LENGTH_SHORT).show();
        finish();
	}
	
	public class readResponseLogin implements Runnable {
        public void run(){
             //call the service here
        	if(LoginActivity.flag_getpost == LoginActivity.HTTP_FREE)
        	{
        		LoginActivity.resultLogin = PostHttp.resultPost;
        		LoginActivity.flag_stop_readResponce++;
	        	Log.d("post", "Login result:"+LoginActivity.resultLogin);
	        	if(!LoginActivity.resultLogin.equals(""))
	        	{
		 		   int index = LoginActivity.resultLogin.indexOf("token");
		 		   if(index!=-1)
		 		   {
		 			   Log.d("post", "indexOf token:"+index);
		 			   LoginActivity.token = LoginActivity.resultLogin.substring(index+8,index+28);
		 			   Log.d("post", "token:"+LoginActivity.token);
		 			   SharedPreferences pre=getSharedPreferences(tokenId, MODE_PRIVATE);
		 			   SharedPreferences.Editor editor=pre.edit();
		 			   
		 			   editor.putString("token", LoginActivity.token);
		 			  
		 			   int indexId = LoginActivity.resultLogin.indexOf("id");
		 			   if(index!=-1)
		 			   {
		 				   Log.d("post","indexOf id:"+indexId);
		 				   int indexEnd=LoginActivity.resultLogin.indexOf("\"",indexId+5);
		 				   LoginActivity.id = Integer.parseInt(LoginActivity.resultLogin.substring(indexId+5,indexEnd));
		 				   Log.d("post", "id:"+LoginActivity.id);
		 				   editor.putString("id", ""+LoginActivity.id);
		 			   }
		 			   
		 			  editor.commit();
		 			  
		 			  LoginActivity.flag_login_succ = 1;
		 			  PostHttp.resultPost="";
		 			  LoginActivity.progressing = false;
						if (LoginActivity.progressing == false)
							Log.d("progress", "flase");
						LoginActivity.resultLogin = "";
						LoginActivity.flag_stop_readResponce = 0;
						
						handle_reponse.removeCallbacks(this);
						LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
						startActivity(new Intent(CircleProgressBar.this,
								JoinGameActivity.class));
						
						finish();
						
		 		   }
		 		   else
		 		   {
		 			  handle_reponse.postDelayed(this, 100);
		 			   if(LoginActivity.flag_stop_readResponce>10)
		 			   {
		 				   Toast.makeText(getBaseContext(), "Error Account!", Toast.LENGTH_LONG).show();
		 				  LoginActivity.flag_stop_readResponce=0;
		 				   handle_reponse.removeCallbacks(this);
		 				  LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
		 				  startActivity(new Intent(CircleProgressBar.this,
									LoginActivity.class));
		 				  
		 			   }
		 			  
		 		   }
	        	}
	        	else
	        	{
	        		handle_reponse.postDelayed(this, 100);
	        		if(LoginActivity.flag_stop_readResponce>50)
		 			   {
		 				   Toast.makeText(getBaseContext(), "Check Internet or Error Account!", Toast.LENGTH_LONG).show();
		 				  LoginActivity.flag_stop_readResponce=0;
		 				   handle_reponse.removeCallbacks(this);
		 				  LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
		 				  startActivity(new Intent(CircleProgressBar.this,
									LoginActivity.class));
		 				  
		 			   }
	        	}
	 		  if(LoginActivity.flag_login_succ==1)
	 		  {
	 			  handle_reponse.removeCallbacks(this);
	 			 LoginActivity.flag_getpost = LoginActivity.HTTP_FREE;
	 		  }
	             
	        }
        	else if((LoginActivity.flag_getpost == LoginActivity.HTTP_BUZY) && (LoginActivity.flag_login_succ!=1))
        	{
        		handle_reponse.postDelayed(this, 100);
        	}
        	else
        		handle_reponse.removeCallbacks(this);
        }
   };
   
}
