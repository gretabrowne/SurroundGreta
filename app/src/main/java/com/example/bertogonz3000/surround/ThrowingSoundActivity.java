package com.example.bertogonz3000.surround;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.bertogonz3000.surround.Models.Utilities;

import org.parceler.Parcels;

import me.angrybyte.circularslider.CircularSlider;

public class ThrowingSoundActivity extends AppCompatActivity {

    CircularSlider slider;
    Song song;
    private Utilities utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throwing_sound);
        slider = findViewById(R.id.circularSlider);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        song = Parcels.unwrap(getIntent().getParcelableExtra("song"));
        song.setIsThrowing(true);
        song.saveInBackground();

        slider.setStartAngle(0);    //double value

        slider.setOnSliderMovedListener(new CircularSlider.OnSliderMovedListener() {
            @Override
            public void onSliderMoved(double pos) {
                if (pos < 0) {
                    // if negative, make it bigger
                    pos = pos + 1;
                }
                Log.d("ThrowingSoundActivity", "in listener");
                song.setMovingNode(pos);
                song.saveInBackground();
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

}
