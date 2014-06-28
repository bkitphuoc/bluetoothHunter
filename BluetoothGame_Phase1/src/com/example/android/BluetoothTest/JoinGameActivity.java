package com.example.android.BluetoothTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.BluetoothChat.R;

public class JoinGameActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.joingame);
 
        Button joinGame = (Button) findViewById(R.id.btnJoinGame);
 
        // Listening to register new account link
        joinGame.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), BluetoothTest.class);
                startActivity(i);
            }
        });

    }
}