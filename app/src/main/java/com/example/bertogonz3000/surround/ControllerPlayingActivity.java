package com.example.bertogonz3000.surround;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bertogonz3000.surround.Models.Utilities;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import org.parceler.Parcels;

import java.util.Timer;

import me.angrybyte.circularslider.CircularSlider;

public class ControllerPlayingActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private AudioManager audioManager;
    CircularSlider slider;
    int volume;
    Song song;
    TextView tvCurrent;
    TextView tvEnd;
    SeekBar seekbar;
    Button btnThrowSound;
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private Utilities utils;
    ImageButton playButton;
    // MyTimerTask myTask;
    Timer myTimer;
    Croller croller;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_playing);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        song = Parcels.unwrap(getIntent().getParcelableExtra("song"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        tvCurrent = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        seekbar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);
        btnThrowSound = findViewById(R.id.btnThrowSound);
        slider = findViewById(R.id.circularSlider);

        slider.setVisibility(View.GONE);
        croller = (Croller) findViewById(R.id.croller);
        croller.setIndicatorWidth(10);
        croller.setBackCircleColor(Color.parseColor("#EDEDED"));
        croller.setMainCircleColor(Color.parseColor("#212121"));
        croller.setIsContinuous(false);
        //TODO - fix this to work for all phones - create a method to scale
        croller.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2);
        croller.setLabel("");
        croller.setLabelColor(Color.BLACK);
        croller.setProgressPrimaryColor(Color.parseColor("#BCA9E6"));
        croller.setIndicatorColor(Color.parseColor("#BCA9E6"));
        croller.setProgressSecondaryCircleSize(3);
        croller.setProgressSecondaryColor(Color.parseColor("#ffffff"));
        croller.setProgressPrimaryCircleSize(5);

        croller.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        //croller.setProgress(audioManager
        //        .getStreamVolume(AudioManager.STREAM_MUSIC));


        mp = MediaPlayer.create(ControllerPlayingActivity.this, song.getAudioIds().get(0));
        mp.setVolume(0,0);
        //MediaPlayer.TrackInfo[] trackInfo = song.getTrackInfo();
        // Changing button image to play button
        playButton.setImageResource(R.drawable.ic_pause_circle_filled);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mp.setAudioAttributes(attributes);


        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {

            @Override
            public void onStartTrackingTouch(Croller croller) {

//                // tracking started
//                mp.setVolume(10,10);
//                mp.start(); //TODO- comment out when using timertask

                // Changing button image to pause button
                // playButton.setImageResource(R.drawable.ic_pause_circle_filled);
//                Log.d("SpeakerPlayingActivity", "tracking touch");
//                song.setVolume(volume);
//                song.saveInBackground();
            }

            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                //   audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                //   progress, 0);

                song.setVolume(progress);
                //  song.setTestString("test string");
                song.saveInBackground();
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
            }
        });

        utils = new Utilities();

        //listeners
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setProgress(0);
        seekbar.setMax(100);

        updateProgressBar();

        //pausing and playing the song
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekbar.getProgress(), totalDuration);
                song.setTime(currentPosition);

                // check for already playing, then pause
                if(mp.isPlaying()){
                    if(mp!=null){
                        // Changing button image to play button
                        playButton.setImageResource(R.drawable.ic_play_circle_filled_60dp);
                        mp.pause();
                        song.setIsPlaying(false);
                        song.setTime(mp.getCurrentPosition());
                        song.saveInBackground();
                    }
                }else{
                    // Resume song (play)
                    if(mp!=null){
                        // Changing button image to pause button
                        playButton.setImageResource(R.drawable.ic_pause_circle_filled);
                        song.setIsPlaying(true);
                        song.setTime(mp.getCurrentPosition());
                        song.saveInBackground();
                        mp.start();
                    }
                }
            }
        });

        btnThrowSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(ControllerPlayingActivity.this, ThrowingSoundActivity.class);
//                // pass song object over
//                song.setTime(mp.getCurrentPosition());
//                i.putExtra("song", Parcels.wrap(song));
//                startActivity(i);
                if(song.getIsThrowing() == false) {
                    song.setIsThrowing(true);
                    song.saveInBackground();
                    slider.setVisibility(View.VISIBLE);
                    croller.setVisibility(View.GONE);
                    btnThrowSound.setText("Surround");
                }
                else {
                    song.setIsThrowing(false);
                    song.saveInBackground();
                    slider.setVisibility(View.GONE);
                    croller.setVisibility(View.VISIBLE);
                    btnThrowSound.setText("Throw");
                }


            }
        });

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
    //TODO- fix timer task
    //start the global clock timer when the activity appears on the screen
    //start the song
    Handler timerHandler = new Handler();

    //TODO - Commented out
    @Override
    public void onResume(){
        super.onResume();
        // myTask = new MyTimerTask();
//        myTimer = new Timer();
//        // myTimer.schedule(myTask, 0, 10000); //check every 10 sec instead
//        timerHandler.post(runnableCode);
        // myTimer.schedule(myTask, 0, 1000); //check every 10 sec instead
        mp.start();
    }
//
//    class MyTimerTask extends TimerTask {
//        //update the current position every 10 seconds in the parse song class
//        public void run() {
//            Log.d("SpeakerPlayingActivity", "mytimertask");
//            int currentPosition = mp.getCurrentPosition();
//            song.setTime(currentPosition);
//            song.saveInBackground();
//        }
//    }


    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.d("ControllerPlayingActivity", "runnable");
            int currentPosition = mp.getCurrentPosition();
            song.setTime(currentPosition);
            song.saveInBackground();
            timerHandler.postDelayed(runnableCode, 10000); // repeat same runnable in 10 seconds
            // TODO-- clear handler?
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        song.deleteInBackground();
        mp.release();
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
                        finish();
                    }
                });

        // Display the dialog
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.background);
    }

    //Update timer on seekbar
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    //Background Runnable thread
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();


            // Displaying Total Duration time
            tvEnd.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            tvCurrent.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekbar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    //seekbar override methods
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);
        song.setNumSeek(song.getNumSeek()+1);   //update the number of times used seek bar

        // update timer progress again
        updateProgressBar();
        song.setTime(currentPosition);
        song.saveInBackground();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mp.release();
    }


}