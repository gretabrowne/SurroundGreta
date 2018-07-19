package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ZoneChoiceActivity extends AppCompatActivity {

    //Declare buttons
    Button centerButton, frontLeftButton, frontRightButton, backLeftButton, backRightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_choice);

        //init buttons
        centerButton = findViewById(R.id.centerButton);
        frontLeftButton = findViewById(R.id.frontLeftButton);
        frontRightButton = findViewById(R.id.frontRightButton);
        backLeftButton = findViewById(R.id.backLeftButton);
        backRightButton = findViewById(R.id.backRightButton);

    }

    public void onSelectZone(View view){
        Intent i = new Intent(this, SpeakerPlayingActivity.class);
        startActivity(i);
    }
}
