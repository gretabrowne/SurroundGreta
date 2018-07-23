package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.SubscriptionHandling;


public class SpeakerPlayingActivity extends AppCompatActivity {

    boolean connected;  //TODO - update this?
    int songId;
    boolean isPlaying;
    MediaPlayer mp;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_playing);
        connected = true;

        position = getIntent().getIntExtra("position", 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

                // use value of "position"
//                Log.d("SpeakerPlayingActivity", "onEvent create");
//                if (zone.equals("center")) {
//                    // play center
//                    songId = object.getAudioIds().get(0);
//                }
//                else if (zone.equals("frontLeft")) {
//                    // play front left
//                    songId = object.getAudioIds().get(1);
//                }
//                else if (zone.equals("frontRight")){
//                    // play front right
//                    songId = object.getAudioIds().get(2);
//                }
//                else if(zone.equals("backLeft")) {
//                    // play back left
//                    songId = object.getAudioIds().get(3);
//                }
//                else if (zone.equals("backRight")) {
//                    // play back right
//                    songId = object.getAudioIds().get(4);
//                }
//                mp = MediaPlayer.create(SpeakerPlayingActivity.this, songId);
//                mp.setVolume(object.getVolume(), object.getVolume());
//                mp.start();
            }
        });

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Song>() {
            @Override
            public void onEvent(ParseQuery<Song> query, Song object) {
                // when volume, song, or playing status is updated
                isPlaying = object.getIsPlaying();
                Log.d("SpeakerPlayingActivity", "in on update");
//                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                        Log.d("SpeakerPlayingActivity", "error create");
//                        return true;
//                    }
//                });
                if (!isPlaying) {
                    mp.pause();
                    Log.d("SpeakerPlayingActivity", "pause");
                }
                else {
                    Log.d("SpeakerPlayingActivity", "change volume");
                    mp.setVolume(object.getVolume(), object.getVolume());
                    mp.start();
                }
            }
        });

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.LEAVE, new SubscriptionHandling.HandleEventCallback<Song>() {
            @Override
            public void onEvent(ParseQuery<Song> query, Song object) {
                Log.d("SpeakerPlayingActivity", "onEvent leave to disconnect");
                disconnect();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //TODO - later make sure the speaker is connected to the master device and server
    public void disconnect() {
        Intent intent = new Intent(SpeakerPlayingActivity.this, LostConnectionActivity.class);
        startActivity(intent);
        finish();
    }

    public void disconnect(View view) {
        Intent intent = new Intent(SpeakerPlayingActivity.this, LostConnectionActivity.class);
        startActivity(intent);
        finish();
    }
}
