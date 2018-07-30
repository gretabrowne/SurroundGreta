package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity {

    //declare Buttons
    Button speakerButton, controllerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //init Buttons
        speakerButton = findViewById(R.id.speakerButton);
        controllerButton = findViewById(R.id.controllerButton);

    }


    //Become a speaker
    public void selectSpeaker(View view){
        //New intent between this and Zone Choice
        Intent i = new Intent(this, SelectZone.class);
        startActivity(i);
    }

    //Become the controller
    public void selectController(View view){
        Intent i = new Intent(this, SongSelectionActivity.class);
        startActivity(i);
    }

}
