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
    int centerID, frontRightID, frontLeftID, backRightID, backLeftID, position, adjustment;
    boolean isPlaying;
    MediaPlayer centerMP, frontRightMP, frontLeftMP, backRightMP, backLeftMP;
    float centerVol, frontRightVol, frontLeftVol, backRightVol,backLeftVol;
    int currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_playing);
        connected = true;

        //positiion selected for this phone.
        position = getIntent().getIntExtra("position", 0);
        adjustment = position;


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

                prepMediaPlayers(object);

                setToMaxVol(centerMP);
                setToMaxVol(frontRightMP);
                setToMaxVol(backRightMP);
                setToMaxVol(backLeftMP);
                setToMaxVol(frontLeftMP);

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
                changeTime(object.getTime());
                Log.d("SpeakerPlayingActivity", "in on update");
//                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                        Log.d("SpeakerPlayingActivity", "error create");
//                        return true;
//                    }
//                });
                // if paused on controller's phone, pause speaker
                if (!isPlaying) {
                    pauseAll();
                    Log.d("SpeakerPlayingActivity", "pause");
                }
                else {
                    Log.d("SpeakerPlayingActivity", "change volume");
                    //TODO - uncomment for full implementation
                    //mp.setVolume(object.getVolume(), object.getVolume());
                    playAll();
                }


//                if (isPlaying != object.getIsPlaying()) {
//
//                    if (!object.getIsPlaying()){
//                        pauseAll();
//                    } else {
//                        playAll();
//                    }
//                // when volume, song, or playing status is updated
//                isPlaying = object.getIsPlaying();
//
//                changeTime(object.getTime());
//
//                Log.d("SpeakerPlayingActivity", "in on update");
////                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
////                    @Override
////                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
////                        Log.d("SpeakerPlayingActivity", "error create");
////                        return true;
////                    }
////                });
//                if (!isPlaying) {
//                    pauseAll();
//                    Log.d("SpeakerPlayingActivity", "pause");
//                }
//                else {
//                    Log.d("SpeakerPlayingActivity", "change volume");
//                    //TODO - uncomment for full implementation
//                    //mp.setVolume(object.getVolume(), object.getVolume());
//                    playAll();
//                }


                if (isPlaying != object.getIsPlaying()) {
                    isPlaying = object.getIsPlaying();
                    if (!isPlaying){
                        pauseAll();
                    } else {
                        playAll();
                    }

                } else {
                    changeTime(object.getTime());

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

    //Create mediaplayers based on given songIds
    private void prepMediaPlayers(Song object){

        centerID = object.getAudioIds().get(0);
        frontLeftID = object.getAudioIds().get(1);
        frontRightID = object.getAudioIds().get(2);
        backLeftID = object.getAudioIds().get(3);
        backRightID = object.getAudioIds().get(4);

        centerMP = MediaPlayer.create(SpeakerPlayingActivity.this, centerID);
        frontLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontLeftID);
        frontRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontRightID);
        backLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, backLeftID);
        backRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, backRightID);

    }

    //pause All 5 mediaplayers
    private void pauseAll(){
        centerMP.pause();
        frontLeftMP.pause();
        frontRightMP.pause();
        backLeftMP.pause();
        backRightMP.pause();
    }

    //play all 5 media players
    private void playAll(){
        centerMP.start();
        frontLeftMP.start();
        frontRightMP.start();
        backLeftMP.start();
        backRightMP.start();
    }

    //change time of all 5 media players
    private void changeTime(int time){
        centerMP.seekTo(time);
        frontLeftMP.seekTo(time);
        frontRightMP.seekTo(time);
        backLeftMP.seekTo(time);
        backRightMP.seekTo(time);
    }

    private float getMaxVol(int node){
//        float denom = (float) (Math.sqrt(2*Math.PI));
//        Log.e("MATH", "denom = " + denom);
//        float left =(float) 2.5066/denom;
//        Log.e("MATH", "left = " + left);
        float expTop = (float) -(Math.pow((adjustment - node), 2));
        float exponent = expTop/5;
        //TODO - add LEFT* before Math.pow....if this doesn't work..got rid of cuz it was ~1
        float maxVol = (float) Math.pow(Math.E, exponent);
        Log.e("MATH", "maxVol at " + node + " = " + maxVol);
        return maxVol;
    }

    //TODO - Might have to differentiate between nodes, not letting center extend???
    private void setToMaxVol(MediaPlayer mp){
        int node = 0;
        if ( mp == centerMP){
            node = 0;
        } else if (mp == frontRightMP){
            node = 4;
        } else if (mp == backRightMP){
            node = 8;
        } else if (mp == backLeftMP){
            node = 12;
        } else if (mp == frontLeftMP){
            node = 16;
        }

        if (position < node - 10){
            adjustment = (node - 10) + (node - 10 - position);

        }
        else if (position > (node + 10)) {
            adjustment = (node + 10) - (position - (node + 10));
        }

        Log.e("Adjustment", "Adjustment = " + adjustment);
        Log.e("Adjustment", "node = " + node);
        Log.e("Adjustment", "position = " + position);



        mp.setVolume(getMaxVol(node), getMaxVol(node));

    }
}
