package com.example.android.BluetoothTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CircleProgressBar extends Activity {
	
	@Override
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.circle_progress_bar);

        ProgressBar mProgress = (ProgressBar) findViewById(R.id.circlePB);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
		    //Do something after 100ms
			  end();
		  }
		}, 4000);

	}
//	@Override
//	public void onResume(){
//		final Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//		  @Override
//		  public void run() {
//		    //Do something after 100ms
//		  }
//		}, 5000);
//
//	}
	
	public void end(){
        startActivity(new Intent(CircleProgressBar.this,
				JoinGameActivity.class));
		Toast.makeText(getBaseContext(), "Login Success!",
				Toast.LENGTH_SHORT).show();
        finish();
	}
}
