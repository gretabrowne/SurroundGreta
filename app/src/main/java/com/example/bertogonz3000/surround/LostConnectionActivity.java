package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LostConnectionActivity extends AppCompatActivity {

    boolean connected;  //TODO - update this?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_connection);

        //TODO - check connection here as well
    }

    public void checkConnection(View view) {
        //TODO - check if the connection to the server is valid
        Intent intent = new Intent(LostConnectionActivity.this, SpeakerPlayingActivity.class);
        startActivity(intent);
    }

}
