package com.example.bertogonz3000.surround;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bertogonz3000.surround.Models.Utilities;

import org.parceler.Parcels;

public class ThrowingSoundActivity extends AppCompatActivity {

    private AudioManager audioManager;
    int volume;
    Song song;
    private MediaPlayer mp;
    private Utilities utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throwing_sound);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        song = Parcels.unwrap(getIntent().getParcelableExtra("song"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }
}
