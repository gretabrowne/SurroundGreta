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
import com.example.bertogonz3000.surround.ParseModels.AudioIDs;
import com.example.bertogonz3000.surround.ParseModels.PlayPause;
import com.example.bertogonz3000.surround.ParseModels.Session;
import com.example.bertogonz3000.surround.ParseModels.Throwing;
import com.example.bertogonz3000.surround.ParseModels.Time;
import com.example.bertogonz3000.surround.ParseModels.Volume;
import com.example.bertogonz3000.surround.views.VolcationSpinner;
import com.parse.ParseException;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import org.parceler.Parcels;

public class ControllerPlayingActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private AudioManager audioManager;
    VolcationSpinner spinner;
//    Song song;

    //Parse objects
    Session session;
    AudioIDs audioIDs;
    PlayPause playPause;
    Volume volume;
    Time time;
    Throwing throwing;

    TextView tvCurrent;
    TextView tvEnd;
    SeekBar seekbar;
    Button btnThrowSound;
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();   //seeking
    private Handler timerHandler = new Handler();   //universal clock/progress in song
    private Utilities utils;
    ImageButton playButton;
    Croller croller;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_playing);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

//        //new server design unwrap from intent
        session = Parcels.unwrap(getIntent().getParcelableExtra("session"));
        audioIDs = Parcels.unwrap(getIntent().getParcelableExtra("audioIDs"));
        playPause = Parcels.unwrap(getIntent().getParcelableExtra("playPause"));
        throwing = Parcels.unwrap(getIntent().getParcelableExtra("throwing"));
        time = Parcels.unwrap(getIntent().getParcelableExtra("time"));
        volume = Parcels.unwrap(getIntent().getParcelableExtra("volume"));

//        //TODO - uncomment to use old server design
//        song = Parcels.unwrap(getIntent().getParcelableExtra("song"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Media Mode");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        tvCurrent = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        seekbar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);
        btnThrowSound = findViewById(R.id.btnThrowSound);
        spinner = findViewById(R.id.spinner);

        spinner.setVisibility(View.GONE);
        spinner.setMaxVol(100);

        croller = (Croller) findViewById(R.id.croller);
        croller.setIndicatorWidth(10);
        croller.setIsContinuous(false);
        //TODO - fix this to work for all phones - create a method to scale
        croller.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2);
        croller.setLabel("");
        croller.setLabelColor(Color.BLACK);
        croller.setProgressPrimaryColor(Color.parseColor("#BCA9E6"));
        croller.setIndicatorColor(Color.parseColor("#BCA9E6"));
        croller.setProgressSecondaryCircleSize(3);
        croller.setProgressPrimaryCircleSize(5);

        croller.setMax(100);
        croller.setProgress(50);


        mp = MediaPlayer.create(ControllerPlayingActivity.this, audioIDs.getIDs().get(0));
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
            public void onStartTrackingTouch(Croller croller) {}

            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                //from new server
                volume.setVolume((float)progress/(float)100);
                volume.saveInBackground();

                //from old server
//                song.setVolume(progress);
//                song.saveInBackground();
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {}
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
                time.setTime(currentPosition);
//                song.setTime(currentPosition);    //old server

                // check for already playing, then pause
                if(mp.isPlaying()){
                    if(mp!=null){
                        // Changing button image to play button
                        playButton.setImageResource(R.drawable.ic_play_circle_filled_60dp);
                        mp.pause();
//                        //new server
                        playPause.setPlaying(false);
                        playPause.saveInBackground();
                        time.setTime(mp.getCurrentPosition());
                        time.saveInBackground();

                        //old server
//                        song.setIsPlaying(false);
//                        song.setTime(mp.getCurrentPosition());  //fix time after pausing the song
//                        song.saveInBackground();
                    }
                }else{
                    // Resume song (play)
                    if(mp!=null){
                        // Changing button image to pause button
                        playButton.setImageResource(R.drawable.ic_pause_circle_filled);

                        //new server
                        time.setTime(mp.getCurrentPosition());
                        time.saveInBackground();
                        playPause.setPlaying(true);
                        playPause.saveInBackground();
                        mp.start();

                        //old server
//                        song.setTime(mp.getCurrentPosition());  //fix time before resuming the song
//                        song.setIsPlaying(true);
//                        song.saveInBackground();
//                        mp.start();
                    }
                }
            }
        });

        btnThrowSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //new server
                if (throwing.getThrowing() == false) {
                    throwing.setThrowing(true);
                    throwing.saveInBackground();
                    spinner.setVisibility(View.VISIBLE);
                    croller.setVisibility(View.GONE);
                    btnThrowSound.setText("Surround");
                }
                else {
                    throwing.setThrowing(false);
                    throwing.saveInBackground();
                    spinner.setVisibility(View.GONE);
                    croller.setVisibility(View.VISIBLE);
                    btnThrowSound.setText("Throw");
                }

                //old server
//                if(song.getIsThrowing() == false) {
//                    song.setIsThrowing(true);
//                    song.saveInBackground();
//                    spinner.setVisibility(View.VISIBLE);
//                    croller.setVisibility(View.GONE);
//                    btnThrowSound.setText("Surround");
//                }
//                else {
//                    song.setIsThrowing(false);
//                    song.saveInBackground();
//                    spinner.setVisibility(View.GONE);
//                    croller.setVisibility(View.VISIBLE);
//                    btnThrowSound.setText("Throw");
//                }


            }
        });

        spinner.setOnThumbChangeListener(new VolcationSpinner.OnThumbChangeListener() {
            @Override
            public void onLocationChanged(float vol, float location) {
                Log.d("LOCATIONCHANGE", "newvol = " + vol + ", location  = " + location);
                throwing.setLocation(location);
                throwing.saveInBackground();

                volume.setVolume(vol);
                volume.saveInBackground();

                //old server
//                song.setMovingNode(location);
//                song.setVolume(volume);
//                song.saveInBackground();
            }
        });

//        slider.setStartAngle(0);    //double value
//
//        slider.setOnSliderMovedListener(new CircularSlider.OnSliderMovedListener() {
//            @Override
//            public void onSliderMoved(double pos) {
//                if (pos < 0) {
//                    // if negative, make it bigger
//                    pos = pos + 1;
//                }
//                Log.d("ThrowingSoundActivity", "in listener");
//                song.setMovingNode(pos);
//                song.saveInBackground();
//            }
//        });

    }

    @Override
    public void onResume(){
        super.onResume();
        //start the global clock timer when the activity appears on the screen
        //start the song
        timerHandler.postDelayed(runnableCode, 1000);
        if(mp != null) {
            mp.start();
        }
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.d("ControllerPlayingActivity", "runnable");
            //only update the current time if the media player is valid and is playing
            if(mp != null ) {
                time.setTime(mp.getCurrentPosition());
                time.saveInBackground();


                //old server
//                song.setTime(mp.getCurrentPosition());
//                song.saveInBackground();
                timerHandler.postDelayed(runnableCode, 1000); // repeat same runnable every second
            }
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
//        song.setIsConnected(false);
//        song.saveInBackground();

//        song.deleteInBackground();
        mp.pause();
        mp.release();
        mp = null;
        session.setConnected(false);
        session.saveInBackground();
    }

    @Override
    protected void onDestroy() {
        try {
            session.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
//        song.setIsConnected(false);
//        song.saveInBackground();
        return true;
    }

    //TODO - make this check the connection of the server (maybe in the onCreate)
    public void checkConnection(View view) {
        View alertView = LayoutInflater.from(ControllerPlayingActivity.this).inflate(R.layout.dialog_controller_disconnected, null);
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

            if(mp!= null) {
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

        // update timer progress again
        updateProgressBar();
        time.setTime(currentPosition);
        time.saveInBackground();

        //old server
//        // forward or backward to certain seconds
//        mp.seekTo(currentPosition);
//        song.setNumSeek(song.getNumSeek()+1);   //update the number of times used seek bar
//
//        // update timer progress again
//        updateProgressBar();
//        song.setTime(currentPosition);
//        song.saveInBackground();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mp.pause();
        mp.release();
        mp = null;
        session.setConnected(false);
        session.saveInBackground();
    }

}