package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.SubscriptionHandling;


public class SpeakerPlayingActivity extends AppCompatActivity {

    boolean connected;  //TODO - update this?
    int songId;
    boolean isPlaying;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_playing);
        connected = true;


        //TODO - later make sure the speaker is connected to the master device and server
        if(!connected) {
            Intent intent = new Intent(SpeakerPlayingActivity.this, LostConnectionActivity.class);
            startActivity(intent);
        }

        //        // Make sure the Parse server is setup to configured for live queries
//        // URL for server is determined by Parse.initialize() call.
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
//
        ParseQuery<Song> query = ParseQuery.getQuery(Song.class);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
//
//        // Connect to Parse server]
        SubscriptionHandling<Song> subscriptionHandling = parseLiveQueryClient.subscribe(query);
//
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Song>() {
            @Override
            public void onEvent(ParseQuery<Song> query, Song object) {
                // when a new song is "created"
                Log.d("SpeakerPlayingActivity", "onEvent");
                songId = object.getFileId();
                mp = MediaPlayer.create(SpeakerPlayingActivity.this, songId);
                mp.setVolume(object.getVolume(), object.getVolume());
                mp.start();
            }
        });

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Song>() {
            @Override
            public void onEvent(ParseQuery<Song> query, Song object) {
                // when volume, song, or playing status is updated
                isPlaying = object.getIsPlaying();
                if (!isPlaying) {
                    mp.pause();
                }
                else {
                    mp.setVolume(object.getVolume(), object.getVolume());
                    mp.start();
                }
            }
        });

    }

    //TODO - later make sure the speaker is connected to the master device and server
    public void disconnect(View view) {
        Intent intent = new Intent(SpeakerPlayingActivity.this, LostConnectionActivity.class);
        startActivity(intent);
    }
}
