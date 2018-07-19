package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class SpeakerPlayingActivity extends AppCompatActivity {

    boolean connected;  //TODO - update this?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_playing);

        //TODO - later make sure the speaker is connected to the master device and server
        if(!connected) {
            Intent intent = new Intent(SpeakerPlayingActivity.this, LostConnectionActivity.class);
            startActivity(intent);
        }
    }

    //TODO - later make sure the speaker is connected to the master device and server
    public void disconnect(View view) {
        Intent intent = new Intent(SpeakerPlayingActivity.this, LostConnectionActivity.class);
        startActivity(intent);
    }
}
