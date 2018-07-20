package com.example.bertogonz3000.surround;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import org.parceler.Parcels;

public class ControllerPlayingActivity extends AppCompatActivity {
    //private AudioManager audioManager = null;
    //MediaPlayer song;
    float rightVol, leftVol;
    Song track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_playing);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        track = Parcels.unwrap(getIntent().getParcelableExtra("song"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Croller croller = (Croller) findViewById(R.id.croller);
        croller.setIndicatorWidth(10);
        croller.setBackCircleColor(Color.parseColor("#EDEDED"));
        croller.setMainCircleColor(Color.WHITE);
        //croller.setMax(50);
        croller.setStartOffset(45);
        croller.setIsContinuous(false);
        croller.setLabel("Surround");
        croller.setLabelColor(Color.BLACK);
        croller.setProgressPrimaryColor(Color.parseColor("#BCA9E6"));
        croller.setIndicatorColor(Color.parseColor("#BCA9E6"));
        croller.setProgressSecondaryColor(Color.parseColor("#ffffff"));
        croller.setProgressPrimaryCircleSize(5);

        //audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //croller.setMax(audioManager
        //        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        //croller.setProgress(audioManager
        //        .getStreamVolume(AudioManager.STREAM_MUSIC));

        rightVol = 1;
        leftVol = 1;

        //TODO - Copy a soundfile into a new directory under "res" and place it here
        //TODO - as the second argument
       // song = MediaPlayer.create(ControllerPlayingActivity.this, R.raw.heyjude);
        //MediaPlayer.TrackInfo[] trackInfo = song.getTrackInfo();

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        //song.setAudioAttributes(attributes);


        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
               // song.setVolume(leftVol,rightVol);
              //  song.start();
                track.setVolume(leftVol);
                track.saveInBackground();
            }

            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                //   audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                //   progress, 0);

                float prog = (float) progress/100;
                track.setVolume(prog);
                track.saveInBackground();
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // song.release();
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


    public void pauseSong(View view){
        track.setIsPlaying(false);
        track.saveInBackground();
        //song.pause();
    }

    public void playSong(View view) {
        track.setIsPlaying(true);
        track.saveInBackground();
        //song.start();
    }

    //TODO - make this check the connection of the server (maybe in the onCreate)
    public void checkConnection(View view) {
        View alertView = LayoutInflater.from(ControllerPlayingActivity.this).inflate(R.layout.dialog_speaker_disconnected, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ControllerPlayingActivity.this);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(alertView);

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();


        // Configure dialog button (Refresh)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Refresh",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO - check again if there is a connection to the server
                        //if connected, then dismiss and return to the ControllerPlayingActivity
                        dialog.dismiss();
                    }
                });


        // Configure dialog button (Restart)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "End Session",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //if the restart button is hit, just return to the home screen
                        Intent intent = new Intent(ControllerPlayingActivity.this, LandingActivity.class);
                        startActivity(intent);
                    }
                });


        // Display the dialog
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.alertDialogBackground);
    }

}
