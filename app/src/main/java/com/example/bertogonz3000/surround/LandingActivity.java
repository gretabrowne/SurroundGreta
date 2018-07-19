package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.SubscriptionHandling;

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


        ParseLiveQueryClient liveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Song> parseQuery = ParseQuery.getQuery(Song.class);

        SubscriptionHandling<Song> subscriptionHandling = liveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Song>() {
            @Override
            public void onEvent(ParseQuery<Song> query, Song object) {
                Log.e("LANDINGACTIVITY", "WITHIN");
            }
        });

        Log.e("LANDINGACTIVITY", "WITHOUT");
    }


    //Become a speaker
    public void selectSpeaker(View view){
        //New intent between this and Zone Choice
        Intent i = new Intent(this, ZoneChoiceActivity.class);
        startActivity(i);
    }

    //Become the controller
    public void selectController(View view){
        Intent i = new Intent(this, SongSelectionActivity.class);
        startActivity(i);
    }
}
