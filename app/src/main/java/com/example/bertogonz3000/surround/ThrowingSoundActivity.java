package com.example.bertogonz3000.surround;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bertogonz3000.surround.Models.Utilities;

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
    }
}
