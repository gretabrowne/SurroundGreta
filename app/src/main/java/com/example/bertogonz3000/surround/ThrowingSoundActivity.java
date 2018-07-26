package com.example.bertogonz3000.surround;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.angrybyte.circularslider.CircularSlider;

import com.example.bertogonz3000.surround.Models.Utilities;

import org.parceler.Parcels;

public class ThrowingSoundActivity extends AppCompatActivity {

    CircularSlider slider;


    private AudioManager audioManager;
    int volume;
    Song song;
    private MediaPlayer mp;
    private Utilities utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throwing_sound);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        slider.setStartAngle(0);    //double value

        slider.setOnSliderMovedListener(new CircularSlider.OnSliderMovedListener() {
            @Override
            public void onSliderMoved(double pos) {
                /**
                 * This method is invoked when slider moves, providing position of the slider thumb.
                 *
                 * @param pos Value between 0 and 1 representing the current angle.<br>
                 *            {@code pos = (Angle - StartingAngle) / (2 * Pi)}
                 */

            }
        });
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        song = Parcels.unwrap(getIntent().getParcelableExtra("song"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
