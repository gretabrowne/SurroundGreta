package com.example.bertogonz3000.surround;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

public class ControllerPlayingActivity extends AppCompatActivity {
    private AudioManager audioManager = null;
    MediaPlayer song;
    float rightVol, leftVol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_playing);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


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
        croller.setProgressSecondaryColor(Color.parseColor("#EEEEEE"));

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        croller.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        croller.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        rightVol = 1;
        leftVol = 1;

        //TODO - Copy a soundfile into a new directory under "res" and place it here
        //TODO - as the second argument
      //  song = MediaPlayer.create(ControllerPlayingActivity.this, R.raw.heyjude);

        MediaPlayer.TrackInfo[] trackInfo = song.getTrackInfo();

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        song.setAudioAttributes(attributes);


        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
                song.setVolume(leftVol,rightVol);
                song.start();
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
            }
        });

    }


    public void pauseSong(View view){
        song.pause();
    }

    public void playSong(View view) {
        song.start();
    }

}
