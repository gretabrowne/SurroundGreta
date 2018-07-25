package com.example.bertogonz3000.surround;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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
    int centerID, frontRightID, frontLeftID, backRightID, backLeftID, phoneVol;
    boolean isPlaying, throwing;
    MediaPlayer centerMP, frontRightMP, frontLeftMP, backRightMP, backLeftMP;
    float position;
    AudioManager audioManager;
    double movingNode = 0.5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_playing);
        connected = true;

        throwing = false;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //positiion selected for this phone.
        //TODO - switch from int to float from intent
        position = getIntent().getFloatExtra("position", 0);
        position = position/100;


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

                isPlaying = object.getIsPlaying();

                if (isPlaying){
                    playAll();
                } else {
                    pauseAll();
                }

                movingNode = object.getMovingNode();

                throwing = object.getIsThrowing();

                if (throwing){
                    //TODO - what do we do on create if throwing? (right now it'll only be throwing when button in ControllerPlaying is hit, so it will never be in CREATE)
                }

                setToMaxVol(centerMP);
                setToMaxVol(frontRightMP);
                setToMaxVol(backRightMP);
                setToMaxVol(backLeftMP);
                setToMaxVol(frontLeftMP);

                phoneVol = (int) object.getVolume();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);

            }
        });

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Song>() {
            @Override
            public void onEvent(ParseQuery<Song> query, Song object) {
                // when volume, song, or playing status is updated

                Log.d("SpeakerPlayingActivity", "in on update");
                Log.d("SpeakerPlayingActivity", "time: " + object.getTime());

                if(object.getTime() != backLeftMP.getCurrentPosition())
                {
                    changeTime(object.getTime());   //TODO - testing clock
                    Log.d("SpeakerPlayingActivity", "time is off");
                }

                if (isPlaying != object.getIsPlaying()) {
                    isPlaying = object.getIsPlaying();
                    if (!isPlaying){
                        Log.d("SpeakerPlayingActivity", "switching pause/play");
                        pauseAll();
                    } else {
                        Log.d("SpeakerPlayingActivity", "switching pause/play");
                        playAll();
                    }

                    //TODO - should object.getVolume be a float or an int? I think it depends on
                    //TODO - where we want to do the conversion between whatever input the croller gives
                    //TODO - us and what we need to set volume
                } else if (phoneVol != object.getVolume()) {
                    Log.d("SpeakerPlayingActivity", "changing volume");
                    phoneVol = (int) object.getVolume();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);

                }
                // TODO - check logic of these if statements, right now it'll start as if controller wants node
                // TODO - 0.5 to be where sound is and will re-ping server when node is moved, this seems like an extra step
                else if (throwing != object.getIsThrowing()) {
                    // set throwing boolean to be equal to whether controller wants to be throwing sound or not
                    throwing = object.getIsThrowing();

                    if (throwing) {
                        centerMP.setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
                        frontLeftMP.setVolume(0, 0);
                        backLeftMP.setVolume(0, 0);
                        frontRightMP.setVolume(0, 0);
                        backRightMP.setVolume(0, 0);
                    } else {
                        setToMaxVol(centerMP);
                        setToMaxVol(frontLeftMP);
                        setToMaxVol(backLeftMP);
                        setToMaxVol(frontRightMP);
                        setToMaxVol(backRightMP);
                    }

                } else if (movingNode != object.getMovingNode()){

                    movingNode = object.getMovingNode();

                } else {
                    // if nothing else has been updated, it must be the time
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
    //TODO - Why do we have 2 disconnect methods? (idk but they do the exact same thing so we should delete one)
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

    private float getMaxVol(double node){
//        float denom = (float) (Math.sqrt(2*Math.PI));
//        Log.e("MATH", "denom = " + denom);
//        float left =(float) 2.5066/denom;
//        Log.e("MATH", "left = " + left);
        float expTop = (float) -(Math.pow((position - node), 2));
        float exponent = expTop/5;
        //TODO - add LEFT* before Math.pow....if this doesn't work..got rid of cuz it was ~1
        float maxVol = (float) Math.pow(Math.E, exponent);
        Log.e("MATH", "maxVol at " + node + " = " + maxVol);
        return maxVol;
    }

    //TODO - Might have to differentiate between nodes, not letting center extend???
    //TODO - I don't like hardcoding the nodes to the mps here...should we create a dictionary or something?
    private void setToMaxVol(MediaPlayer mp){
        double node = 0.5;
        if ( mp == centerMP){
            node = 0.5;
        } else if (mp == frontRightMP){
            node = 0.625;
        } else if (mp == backRightMP){
            node = 0.875;
        } else if (mp == backLeftMP){
            node = 0.175;
        } else if (mp == frontLeftMP){
            node = 0.375;
        }


        Log.e("Adjustment", "node = " + node);
        Log.e("Adjustment", "position = " + position);



        mp.setVolume(getMaxVol(node), getMaxVol(node));

    }

}