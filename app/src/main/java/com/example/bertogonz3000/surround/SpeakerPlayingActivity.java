package com.example.bertogonz3000.surround;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.bertogonz3000.surround.ParseModels.AudioIDs;
import com.example.bertogonz3000.surround.ParseModels.PlayPause;
import com.example.bertogonz3000.surround.ParseModels.Session;
import com.example.bertogonz3000.surround.ParseModels.Throwing;
import com.example.bertogonz3000.surround.ParseModels.Time;
import com.example.bertogonz3000.surround.ParseModels.Volume;
import com.parse.GetCallback;
import com.parse.LiveQueryException;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseLiveQueryClientCallbacks;
import com.parse.ParseQuery;
import com.parse.SubscriptionHandling;

import org.parceler.Parcels;


public class SpeakerPlayingActivity extends AppCompatActivity {

    int centerID, frontRightID, frontLeftID, backRightID, backLeftID, phoneVol;
    boolean isPlaying, throwing;
    MediaPlayer centerMP, frontRightMP, frontLeftMP, backRightMP, backLeftMP;
    float position;
    AudioManager audioManager;
    int numberSeek;
    double movingNode = 0.5;
    View background;    //this will change color (flash) during throwing
    ParseLiveQueryClient parseLiveQueryClient;
    RelativeLayout lostConnection;
    RelativeLayout loaderContainer;
    RelativeLayout defaultContainer;
    boolean loaded = false;
    boolean reconnected = false;
    AudioIDs audioIDholder;
    boolean prepared = false;
    Session existingSession = null;
    boolean joining = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_playing);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2, 0);

        background = findViewById(R.id.background);
        lostConnection = findViewById(R.id.lostConnectionContainer);
        defaultContainer = findViewById(R.id.defaultContainer);

        throwing = false;
        background.setAlpha(0);

        loaderContainer = findViewById(R.id.loaderContainer);
        loaderContainer.setVisibility(View.VISIBLE);

        //this will create the loading screen for "downloading" the track
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loaderContainer.setVisibility(View.INVISIBLE);
                defaultContainer.setVisibility(View.VISIBLE);
                loaded = true;
            }
        }, 3000); // 3000 milliseconds delay


        //position selected for this phone.
        //TODO - switch from int to float from intent
        position = getIntent().getFloatExtra("position", 0);
        if(getIntent().hasExtra("session")) {
            existingSession = Parcels.unwrap(getIntent().getParcelableExtra("session"));
            joining = true;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Media Mode");

        //        // Make sure the Parse server is setup to configured for live queries
//        // URL for server is determined by Parse.initialize() call.
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        //this should respond to errors when speaker is disconnected from the server
        parseLiveQueryClient.registerListener(new ParseLiveQueryClientCallbacks() {
            @Override
            public void onLiveQueryClientConnected(ParseLiveQueryClient client) {
                Log.d("speakerplaying", "onlivequery connected");
                if(loaded) {
                    defaultContainer.setVisibility(View.VISIBLE);
                    lostConnection.setVisibility(View.INVISIBLE);
                    if(reconnected) {
                            if(centerMP != null) {
                                playAll();
                                reconnected = false;
                            }
                    }
                }
            }

            @Override
            public void onLiveQueryClientDisconnected(ParseLiveQueryClient client, boolean userInitiated) {
                disconnect();
                Log.d("speakerplaying", "onlivequerydisconnected");
            }

            @Override
            public void onLiveQueryError(ParseLiveQueryClient client, LiveQueryException reason) {
                disconnect();
                Log.d("speakerplaying", "onlivequeryerror");
            }

            @Override
            public void onSocketError(ParseLiveQueryClient client, Throwable reason) {
//                disconnect();
                Log.d("speakerplaying", "onsocketerror");
                parseLiveQueryClient.reconnect();
                parseLiveQueryClient.connectIfNeeded();
            }
        });
//          old server
//        ParseQuery<Song> query = ParseQuery.getQuery(Song.class);
        //new server
        ParseQuery<Session> session = ParseQuery.getQuery(Session.class);
        final ParseQuery<AudioIDs> audioIDs = ParseQuery.getQuery(AudioIDs.class);
        ParseQuery<PlayPause> playPause = ParseQuery.getQuery(PlayPause.class);
        ParseQuery<Throwing> throwingParseQuery = ParseQuery.getQuery(Throwing.class);
        ParseQuery<Volume> volume = ParseQuery.getQuery(Volume.class);
        ParseQuery<Time> timeQuery = ParseQuery.getQuery(Time.class);

        if(joining) { //if the speaker is joining an existing session
            session.whereEqualTo("objectId", existingSession.getObjectId());
            playPause.whereEqualTo("objectId", existingSession.getPlayPause().getPlayPauseID());
            volume.whereEqualTo("objectId", existingSession.getVolume().getVolumeID());
            timeQuery.whereEqualTo("objectId", existingSession.getTimeObject().getTimeID());
            throwingParseQuery.whereEqualTo("objectId", existingSession.getThrowingObject().getThrowingID());
            audioIDs.whereEqualTo("objectId", existingSession.getAudioIDs().getAudioID());

            //initialize everything so the speaker can catch up with the others
            audioIDs.getFirstInBackground(new GetCallback<AudioIDs>() {
                @Override
                public void done(AudioIDs object, ParseException e) {
                    audioIDholder = object;
                    prepMediaPlayers(object);
                    setToMaxVol(centerMP);
                    setToMaxVol(frontRightMP);
                    setToMaxVol(backRightMP);
                    setToMaxVol(backLeftMP);
                    setToMaxVol(frontLeftMP);
                }
            });
            playPause.getFirstInBackground(new GetCallback<PlayPause>() {
                @Override
                public void done(PlayPause object, ParseException e) {
                    isPlaying = object.getPlaying();
                    if(prepared) {
                        if(isPlaying) {
                            playAll();
                        } else {
                            pauseAll();
                        }
                    }
                }
            });
            timeQuery.getFirstInBackground(new GetCallback<Time>() {
                @Override
                public void done(Time object, ParseException e) {
                    if(!prepared)
                        prepMediaPlayers(audioIDholder);
                    changeTime(object.getTime());
                }
            });

            volume.getFirstInBackground(new GetCallback<Volume>() {
                @Override
                public void done(Volume object, ParseException e) {
                    phoneVol = (int) object.getVolume();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);
                }
            });
        }

        //subscription handling for audioIDs
        SubscriptionHandling<AudioIDs> audioIDsSubscriptionHandling = parseLiveQueryClient.subscribe(audioIDs);
        audioIDsSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<AudioIDs>() {
            @Override
            public void onEvent(ParseQuery<AudioIDs> query, AudioIDs object) {
                Log.d("SpeakerPlayingActivity", "created audioIDs subscription");
                audioIDholder = object;
                prepMediaPlayers(object);
                setToMaxVol(centerMP);
                setToMaxVol(frontRightMP);
                setToMaxVol(backRightMP);
                setToMaxVol(backLeftMP);
                setToMaxVol(frontLeftMP);
            }
        });

        //subscription handling for session
        SubscriptionHandling<Session> sessionSubscriptionHandling = parseLiveQueryClient.subscribe(session);
        sessionSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Session>() {
            @Override
            public void onEvent(ParseQuery<Session> query, Session object) {
                Log.d("SpeakerPlayingActivity", "created session subscription");
//
//                prepMediaPlayers(object.getAudioIDs());
//
//                isPlaying = object.getPlayPause().getPlaying();
//
//                changeTime(object.getTimeObject().getTime());  //if speaker initially joins late then have it match up with the others and the controller
//
//                movingNode = object.getThrowingObject().getLocation();
//                throwing = object.getThrowingObject().getThrowing();
//
//                setToMaxVol(centerMP);
//                setToMaxVol(frontRightMP);
//                setToMaxVol(backRightMP);
//                setToMaxVol(backLeftMP);
//                setToMaxVol(frontLeftMP);
//
//                if (isPlaying){
//                    playAll();
//                } else {
//                    pauseAll();
//                }
//
//                phoneVol = (int) object.getVolume().getVolume();
//                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);
//

            }
        });

        //subscription handling for playpause
        SubscriptionHandling<PlayPause> playPauseSubscriptionHandling = parseLiveQueryClient.subscribe(playPause);
        playPauseSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<PlayPause>() {

                    @Override
                    public void onEvent(ParseQuery<PlayPause> query, PlayPause object) {
                        Log.d("SpeakerPlayingActivity", "created play pause subscription");
                        isPlaying = object.getPlaying();
                        if (isPlaying){
                            playAll();
                        } else {
                            pauseAll();
                        }
                    }
                });
        playPauseSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<PlayPause>() {

            @Override
            public void onEvent(ParseQuery<PlayPause> query, PlayPause object) {
                if (isPlaying != object.getPlaying()) {
                    isPlaying = object.getPlaying();
                    if (!isPlaying){
                        Log.d("SpeakerPlayingActivity", "switching pause/play");
                        pauseAll();
//                        changeTime(object.getTime());   //change time after the media players are paused
                        return;
                    } else {
                        Log.d("SpeakerPlayingActivity", "switching pause/play");
//                        changeTime(object.getTime());   //change time before resuming
                        playAll();
                        return;
                    }
                }
            }
        } );

        playPauseSubscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<PlayPause>() {
            @Override
            public void onEvent(ParseQuery<PlayPause> query, PlayPause object) {
                Log.d("SpeakerPlayingActivity", "onEvent leave to disconnect in DELETE");
                disconnect();
            }
        });

        //if error in subscription handling, then call disconnect
        playPauseSubscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<PlayPause>() {
            @Override
            public void onError(ParseQuery<PlayPause> query, LiveQueryException exception) {
                disconnect();
                Log.d("speakerplaying", "handleerror");
            }
        });

        //subscription handling for volume
        SubscriptionHandling<Volume> volumeSubscriptionHandling = parseLiveQueryClient.subscribe(volume);
        volumeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Volume>() {

            @Override
            public void onEvent(ParseQuery<Volume> query, Volume object) {
                Log.d("SpeakerPlayingActivity", "created volume subscription");

                phoneVol = (int) object.getVolume();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);
            }
        });

        volumeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Volume>() {
            @Override
            public void onEvent(ParseQuery<Volume> query, Volume object) {
                if (phoneVol != object.getVolume() ) {
                    Log.d("SpeakerPlayingActivity", "changing volume");
                    phoneVol = (int) object.getVolume();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);
                    return;
                }
            }
        });
        volumeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<Volume>() {
            @Override
            public void onEvent(ParseQuery<Volume> query, Volume object) {
                Log.d("SpeakerPlayingActivity", "onEvent leave to disconnect in DELETE");
                disconnect();
            }
        });
        //if error in subscription handling, then call disconnect
        volumeSubscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<Volume>() {
            @Override
            public void onError(ParseQuery<Volume> query, LiveQueryException exception) {
                disconnect();
                Log.d("speakerplaying", "handleerror");
            }
        });

        SubscriptionHandling<Time> timeSubscriptionHandling = parseLiveQueryClient.subscribe(timeQuery);
        timeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Time>() {
            @Override
            public void onEvent(ParseQuery<Time> query, Time object) {
                Log.d("SpeakerPlayingActivity", "created time subscription");

                if(!prepared)
                    prepMediaPlayers(audioIDholder);
                changeTime(object.getTime());  //if speaker initially joins late then have it match up with the others and the controller
            }
        });

        timeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Time>() {
            @Override
            public void onEvent(ParseQuery<Time> query, Time object) {
                //if the time of the speaker is too different from the time of the controller
                //can continue to find "sweet spot" but somewhere between 100 and 500... 300 seems great
                if(!prepared)
                    prepMediaPlayers(audioIDholder);
                if( (centerMP.getCurrentPosition() > object.getTime() + 300) || (centerMP.getCurrentPosition() < object.getTime() - 300) ) {
                    changeTime(object.getTime());
                }
            }
        });

        timeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<Time>() {
            @Override
            public void onEvent(ParseQuery<Time> query, Time object) {
                disconnect();
                Log.d("speakerplaying", "handleerror");
            }
        });

        timeSubscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<Time>() {
            @Override
            public void onError(ParseQuery<Time> query, LiveQueryException exception) {
                disconnect();
                Log.d("speakerplaying", "handleerror");
            }
        });

        SubscriptionHandling<Throwing> throwingSubscriptionHandling = parseLiveQueryClient.subscribe(throwingParseQuery);
        throwingSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Throwing>() {
            @Override
            public void onEvent(ParseQuery<Throwing> query, Throwing object) {
                Log.d("SpeakerPlayingActivity", "created throwing subscription");

                movingNode = object.getLocation();
                throwing = object.getThrowing();
            }
        });
        throwingSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Throwing>() {
            @Override
            public void onEvent(ParseQuery<Throwing> query, Throwing object) {
                if (throwing != object.getThrowing()) {
                    Log.d("SpeakerPlayingActivity", "throwing != object.getIsThrowing");
                    // set throwing boolean to be equal to whether controller wants to be throwing sound or not
                    throwing = object.getThrowing();

                    if (throwing) {
                        Log.e("SpeakerPlayingActivity", "throwing and setting volume to " + getMaxVol(movingNode) + "" + getMaxVol(movingNode));
                        centerMP.setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
                        frontLeftMP.setVolume(0, 0);
                        backLeftMP.setVolume(0, 0);
                        frontRightMP.setVolume(0, 0);
                        backRightMP.setVolume(0, 0);
                    } else {
                        Log.e("THROWING", "Returned");
                        setToMaxVol(centerMP);
                        setToMaxVol(frontLeftMP);
                        setToMaxVol(backLeftMP);
                        setToMaxVol(frontRightMP);
                        setToMaxVol(backRightMP);
                    }

                }
                if (movingNode != object.getLocation()){

                    movingNode = object.getLocation();
                    Log.d("SpeakerPlayingActivity", "movingnode != object.getmovingnode");
                    Log.d("SpeakerPlayingActivity", "setting volume to " + getMaxVol(movingNode) + " , " + getMaxVol(movingNode));
                    centerMP.setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
                    frontLeftMP.setVolume(0, 0);
                    backLeftMP.setVolume(0, 0);
                    frontRightMP.setVolume(0, 0);
                    backRightMP.setVolume(0, 0);

                    //0 means the view is completely transparent and 1 means the view is completely opaque.
                    //sets the color to full purple if it is closest to the movingNode position
                    background.setAlpha(getMaxVol(movingNode));
                    return;
                }
            }
        });
        throwingSubscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<Throwing>() {
            @Override
            public void onEvent(ParseQuery<Throwing> query, Throwing object) {
                disconnect();
                Log.d("speakerplaying", "handleerror");
            }
        });
        throwingSubscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<Throwing>() {
            @Override
            public void onError(ParseQuery<Throwing> query, LiveQueryException exception) {
                disconnect();
                Log.d("speakerplaying", "handleerror");
            }
        });
//
//        SubscriptionHandling<Song> subscriptionHandling = parseLiveQueryClient.subscribe(query);
////
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Song>() {
//            @Override
//            public void onEvent(ParseQuery<Song> query, Song object) {
////
////                prepMediaPlayers(object);
//
//                //TODO - check discrepancy between adapter and controller
////                isPlaying = object.getIsPlaying();
////                numberSeek = object.getNumSeek();
//
////                changeTime(object.getTime());  //if speaker initially joins late then have it match up with the others and the controller
//
////                movingNode = object.getMovingNode();
////                throwing = object.getIsThrowing();
//
////                setToMaxVol(centerMP);
////                setToMaxVol(frontRightMP);
////                setToMaxVol(backRightMP);
////                setToMaxVol(backLeftMP);
////                setToMaxVol(frontLeftMP);
//
////                if (isPlaying){
////                    playAll();
////                } else {
////                    pauseAll();
////                }
////
////                phoneVol = (int) object.getVolume();
////                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);
//
//            }
//        });
//
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Song>() {
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public void onEvent(ParseQuery<Song> query, Song object) {
//                // when volume, song, or playing status is updated
//
//                Log.d("SpeakerPlayingActivity", "in on update");
//                Log.d("SpeakerPlayingActivity", "time: " + object.getTime());
//
//                //if the scrubber was used to change the position in the song
//                if(object.getNumSeek() != numberSeek) {
//                    changeTime(object.getTime());
//                    numberSeek = object.getNumSeek();
//                    return;
//                }
//
////                if (phoneVol != object.getVolume() ) {
////                    Log.d("SpeakerPlayingActivity", "changing volume");
////                    phoneVol = (int) object.getVolume();
////                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,phoneVol, 0);
////                    return;
////                }
//
////                if (isPlaying != object.getIsPlaying()) {
////                    isPlaying = object.getIsPlaying();
////                    if (!isPlaying){
////                        Log.d("SpeakerPlayingActivity", "switching pause/play");
////                        pauseAll();
////                        changeTime(object.getTime());   //change time after the media players are paused
////                        return;
////                    } else {
////                        Log.d("SpeakerPlayingActivity", "switching pause/play");
////                        changeTime(object.getTime());   //change time before resuming
////                        playAll();
////                        return;
////                    }
////                }
//
//                // TODO - check logic of these if statements, right now it'll start as if controller wants node
//                // TODO - 0.5 to be where sound is and will re-ping server when node is moved, this seems like an extra step
////                else if (throwing != object.getIsThrowing()) {
////                    Log.d("SpeakerPlayingActivity", "throwing != object.getIsThrowing");
////                    // set throwing boolean to be equal to whether controller wants to be throwing sound or not
////                    throwing = object.getIsThrowing();
////
////                    if (throwing) {
////                        Log.e("SpeakerPlayingActivity", "throwing and setting volume to " + getMaxVol(movingNode) + "" + getMaxVol(movingNode));
////                        centerMP.setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
////                        frontLeftMP.setVolume(0, 0);
////                        backLeftMP.setVolume(0, 0);
////                        frontRightMP.setVolume(0, 0);
////                        backRightMP.setVolume(0, 0);
////                    } else {
////                        Log.e("THROWING", "Returned");
////                        setToMaxVol(centerMP);
////                        setToMaxVol(frontLeftMP);
////                        setToMaxVol(backLeftMP);
////                        setToMaxVol(frontRightMP);
////                        setToMaxVol(backRightMP);
////                    }
////
////                }
////                if (movingNode != object.getMovingNode()){
////
////                    movingNode = object.getMovingNode();
////                    Log.d("SpeakerPlayingActivity", "movingnode != object.getmovingnode");
////                    Log.d("SpeakerPlayingActivity", "setting volume to " + getMaxVol(movingNode) + " , " + getMaxVol(movingNode));
////                    centerMP.setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
////                    frontLeftMP.setVolume(0, 0);
////                    backLeftMP.setVolume(0, 0);
////                    frontRightMP.setVolume(0, 0);
////                    backRightMP.setVolume(0, 0);
////
////                    //0 means the view is completely transparent and 1 means the view is completely opaque.
////                    //sets the color to full purple if it is closest to the movingNode position
////                    background.setAlpha(getMaxVol(movingNode));
////                    return;
////                }
//
////                //if the time of the speaker is too different from the time of the controller
////                //can continue to find "sweet spot" but somewhere between 100 and 500... 300 seems great
////                if( (centerMP.getCurrentPosition() > object.getTime() + 300) || (centerMP.getCurrentPosition() < object.getTime() - 300) ) {
////                    changeTime(object.getTime());
////                }
//            }
//        });
//
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<Song>() {
//            @Override
//            public void onEvent(ParseQuery<Song> query, Song object) {
//                Log.d("SpeakerPlayingActivity", "onEvent leave to disconnect in DELETE");
//                disconnect();
//            }
//        });
//
//        //if error in subscription handling, then call disconnect
//        subscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<Song>() {
//            @Override
//            public void onError(ParseQuery<Song> query, LiveQueryException exception) {
//                disconnect();
//                Log.d("speakerplaying", "handleerror");
//            }
//        });
//
//
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
        Log.d("speakerplaying", "disconnectfunction");
        if(frontRightMP != null && backRightMP != null && frontLeftMP != null && backLeftMP != null && centerMP != null) {
            pauseAll();
            releaseAll();
            nullAll();
        }

        //Run code on the UI thread
        runOnUiThread(new Runnable() {
            @ Override
            public void run() {
                //display the view for lost connection
                defaultContainer.setVisibility(View.INVISIBLE);
                lostConnection.setVisibility(View.VISIBLE);
            }
        });
    }


    public void disconnect(View view) {
        parseLiveQueryClient.disconnect();  //only if user initiated the disconnect from the server
        disconnect();
        pauseAll();
        reconnected = false;
    }

    public void reconnect(View view) {
        Log.d("SpeakerPlaying", "in reconnect function");
        parseLiveQueryClient.reconnect();
        parseLiveQueryClient.connectIfNeeded();
        defaultContainer.setVisibility(View.VISIBLE);
        lostConnection.setVisibility(View.INVISIBLE);
        reconnected = true;
        playAll();
    }
    //Create mediaplayers based on given songIds
    private void prepMediaPlayers(AudioIDs audioIDs){

        centerID = audioIDs.getIDs().get(0);
        frontLeftID = audioIDs.getIDs().get(1);
        frontRightID = audioIDs.getIDs().get(2);
        backLeftID = audioIDs.getIDs().get(3);
        backRightID = audioIDs.getIDs().get(4);

        centerMP = MediaPlayer.create(SpeakerPlayingActivity.this, centerID);
        frontLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontLeftID);
        frontRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontRightID);
        backLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, backLeftID);
        backRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, backRightID);
        Log.d("prepMediaPlayers", "finished setting up media players");
        prepared = true;

        if(isPlaying)
            playAll();
    }
//    //Create mediaplayers based on given songIds
//    private void prepMediaPlayers(Song object){
//
//        centerID = object.getAudioIds().get(0);
//        frontLeftID = object.getAudioIds().get(1);
//        frontRightID = object.getAudioIds().get(2);
//        backLeftID = object.getAudioIds().get(3);
//        backRightID = object.getAudioIds().get(4);
//
//        centerMP = MediaPlayer.create(SpeakerPlayingActivity.this, centerID);
//        frontLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontLeftID);
//        frontRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontRightID);
//        backLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, backLeftID);
//        backRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, backRightID);
//    }

    //pause All 5 mediaplayers
    synchronized private void pauseAll(){
        centerMP.pause();
        frontLeftMP.pause();
        frontRightMP.pause();
        backLeftMP.pause();
        backRightMP.pause();
    }

    //play all 5 media players
    synchronized private void playAll(){
        centerMP.start();
        frontLeftMP.start();
        frontRightMP.start();
        backLeftMP.start();
        backRightMP.start();
    }

    //TODO - CREATED
    private void releaseAll(){
        centerMP.release();
        frontLeftMP.release();
        frontRightMP.release();
        backLeftMP.release();
        backRightMP.release();
    }

    //TODO - CREATED
    private void nullAll(){
        centerMP = null;
        frontLeftMP = null;
        frontRightMP = null;
        backLeftMP = null;
        backRightMP = null;
    }

    //change time of all 5 media players
    synchronized private void changeTime(int time){
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
        //TODO - CHANGED 12:49 7/26
        double exponent = expTop/0.02;
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
            node = 0.125;
        } else if (mp == frontLeftMP){
            node = 0.375;
        }

        Log.e("Adjustment", "node = " + node);
        Log.e("Adjustment", "position = " + position);

        mp.setVolume(getMaxVol(node), getMaxVol(node));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(frontRightMP != null && backRightMP != null && frontLeftMP != null && backLeftMP != null && centerMP != null) {
            frontLeftMP.release();
            frontRightMP.release();
            backLeftMP.release();
            backRightMP.release();
            centerMP.release();

            frontLeftMP = null;
            frontRightMP = null;
            backLeftMP = null;
            backRightMP = null;
            centerMP = null;
        }
    }
}
