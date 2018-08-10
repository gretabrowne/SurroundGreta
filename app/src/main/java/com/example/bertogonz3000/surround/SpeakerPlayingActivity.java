package com.example.bertogonz3000.surround;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.ArrayList;
import java.util.List;


public class SpeakerPlayingActivity extends AppCompatActivity {

    int centerID, frontRightID, frontLeftID, backRightID, backLeftID, controllerNumber, numControllers, phoneVol;;
    boolean isPlaying0, isPlaying1, throwing, loaded, reconnected, prepared0, prepared1, joining;
    MediaPlayer centerMP, frontRightMP, frontLeftMP, backRightMP, backLeftMP;
    float position, phoneVolPercentage;
    AudioManager audioManager;
    double movingNode = 0.5;
    View background;    //this will change color (flash) during throwing
    ParseLiveQueryClient parseLiveQueryClient;
    RelativeLayout lostConnection;
    RelativeLayout loaderContainer;
    RelativeLayout defaultContainer;
    boolean userInitiatedDisconnect = false;
    Session existingSession = null;
    int savedTime = -1;
    List<AudioIDs> allAudioTracks;
    List<MediaPlayer> allMPs;
    Handler recreateHandler = new Handler();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_playing);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2, 0);

        background = findViewById(R.id.background);
        lostConnection = findViewById(R.id.lostConnectionContainer);
        defaultContainer = findViewById(R.id.defaultContainer);
        allAudioTracks = new ArrayList<AudioIDs>();
        allMPs = new ArrayList<MediaPlayer>();
        throwing = false;
        loaded = false;
        joining = false;
        isPlaying0 = false;
        isPlaying1 = false;
        reconnected = false;
        prepared0 = false;
        prepared1 = false;
        background.setAlpha(0);
        numControllers = 0;
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

            if(existingSession.isConnected()) {
                Log.d("SpeakerPlayingActivity", "existing session connected");
                joining = true;
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Media Mode");

        recreateHandler = new Handler(Looper.getMainLooper());

        //        // Make sure the Parse server is setup to configured for live queries
//        // URL for server is determined by Parse.initialize() call.
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        //this should respond to errors when speaker is disconnected from the server
        parseLiveQueryClient.registerListener(new ParseLiveQueryClientCallbacks() {
            @Override
            public void onLiveQueryClientConnected(ParseLiveQueryClient client) {
                Log.d("speakerplaying", "onlivequery connected");
                if(loaded) {
                    runOnUiThread(reconnectViews);
                    if(reconnected) {
                        if(allMPs != null) {
                            if (isPlaying0) {
                                playMPs(0);
                            }
                            if (controllerNumber > 1 && isPlaying1) {
                                playMPs(1);
                            }
                            reconnected = false;
                        }
                        userInitiatedDisconnect = false;
                    }
                }
            }

            @Override
            public void onLiveQueryClientDisconnected(ParseLiveQueryClient client, boolean userInitiated) {
                disconnect();
                Log.d("speakerplaying", "onlivequerydisconnected");
//                if(!userInitiatedDisconnect) {
//                    client.reconnect();
//                    reconnected = true;
//                    userInitiatedDisconnect = false;
//                    runOnUiThread(reconnectViews);
//                    if(centerMP != null && isPlaying) {
//                        playAll();
//                    }
//                }
            }

            @Override
            public void onLiveQueryError(ParseLiveQueryClient client, LiveQueryException reason) {
                disconnect();
                Log.d("speakerplaying", "onlivequeryerror");
            }

            @Override
            public void onSocketError(ParseLiveQueryClient client, Throwable reason) {
                //this happens when there is lost connection from the
//                disconnect();
                Log.d("speakerplaying", "onsocketerror");

                if(!userInitiatedDisconnect) {
                    client.reconnect();
                    reconnected = true;
                    runOnUiThread(reconnectViews);
                    if(allMPs != null) {
                        if (isPlaying0) {
                            playMPs(0);
                        }
                        if (controllerNumber > 1 && isPlaying1) {
                            playMPs(1);
                        }
                        reconnected = false;
                    }
                }
            }
        });
//          old server
//        ParseQuery<Song> query = ParseQuery.getQuery(Song.class);
        //new server
        ParseQuery<Session> session = ParseQuery.getQuery(Session.class);
        final ParseQuery<AudioIDs> audioIDs = ParseQuery.getQuery(AudioIDs.class);
        ParseQuery<Throwing> throwingParseQuery = ParseQuery.getQuery(Throwing.class);
        ParseQuery<Volume> volume = ParseQuery.getQuery(Volume.class);
        ParseQuery<Time> timeQuery = ParseQuery.getQuery(Time.class);
        ParseQuery<PlayPause> playPause = ParseQuery.getQuery(PlayPause.class);

        if(joining) { //if the speaker is joining an existing session
            session.whereEqualTo("objectId", existingSession.getObjectId());
            audioIDs.whereEqualTo("objectId", existingSession.getAudioIDs().getAudioID());
            throwingParseQuery.whereEqualTo("objectId", existingSession.getThrowingObject().getThrowingID());
            volume.whereEqualTo("objectId", existingSession.getVolume().getVolumeID());
            timeQuery.whereEqualTo("objectId", existingSession.getTimeObject().getTimeID());
            playPause.whereEqualTo("objectId", existingSession.getPlayPause().getPlayPauseID());

            //initialize everything so the speaker can catch up with the others
            audioIDs.getFirstInBackground(new GetCallback<AudioIDs>() {
                @Override
                public void done(AudioIDs object, ParseException e) {
                    numControllers++;
                    allAudioTracks.add(object);
                    prepMediaPlayers(object, numControllers - 1);
                }
            });
            playPause.getFirstInBackground(new GetCallback<PlayPause>() {
                @Override
                public void done(PlayPause object, ParseException e) {
                    isPlaying0 = object.getPlaying();
                    if(prepared0) {
                        if (!isPlaying0){
                            // pause media players that match this object's controller creator
                            // todo-- uncomment when support for joining a session with multiple controllers is added
                            // if (object.getControllerNumber() == 0) {
                            // if first controller starting session
                            pauseMPs(0);
//                            }
//                            // playAll();
//                            else {
//                                pauseMPs(1);
//                            }
                        } else {
                            // pause media players that match this object's controller creator
                            // if (object.getControllerNumber() == 0) {
                            // if first controller starting session
                            playMPs(0);
//                            }
//                            // playAll();
//                            else {
//                                playMPs(1);
//                            }
                        }
                    }
                }
            });
            timeQuery.getFirstInBackground(new GetCallback<Time>() {
                @Override
                public void done(Time object, ParseException e) {
                    if (!prepared0){
                        // prepare media players for specified controller
                        // if (object.getControllerNumber() == 0) {
                        if (!allMPs.isEmpty()) {
                            prepMediaPlayers(allAudioTracks.get(0), 0);
                        }
                        // }
//                        else {
//                            if (!allMPs.isEmpty()) {
//                                prepMediaPlayers(allAudioTracks.get(1), 1);
//                            }
//                        }
                    }

                    if (object.getControllerNumber() == 0) {
                        // if first controller starting session
                        if (allMPs.get(0).getCurrentPosition() > object.getTime() + 200) {
                            Log.d("SpeakerPlayingActivity", "times are off so calling changeTime");
                            changeTime(object.getTime(), 0);
                        } else if (allMPs.get(0).getCurrentPosition() < object.getTime() - 200) {
                            changeTime(object.getTime() + 100, 0);
                        }
                    } else {
                        // if first controller starting session
                        if (allMPs.get(1).getCurrentPosition() > object.getTime() + 200) {
                            Log.d("SpeakerPlayingActivity", "times are off so calling changeTime");
                            changeTime(object.getTime(), 1);
                        } else if (allMPs.get(5).getCurrentPosition() < object.getTime() - 200) {
                            changeTime(object.getTime() + 100, 1);
                        }

                    }
                }
            });

            volume.getFirstInBackground(new GetCallback<Volume>() {
                @Override
                public void done(Volume object, ParseException e) {
                    phoneVolPercentage = object.getVolume();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*phoneVolPercentage), 0);
                }
            });
        }

        //subscription handling for audioIDs
        SubscriptionHandling<AudioIDs> audioIDsSubscriptionHandling = parseLiveQueryClient.subscribe(audioIDs);
        audioIDsSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<AudioIDs>() {
            @Override
            public void onEvent(ParseQuery<AudioIDs> query, AudioIDs object) {
                Log.d("SpeakerPlayingActivity", "created audioIDs subscription");
                numControllers++;
                allAudioTracks.add(object);
                prepMediaPlayers(object, numControllers - 1);
            }
        });

        //subscription handling for session
        final SubscriptionHandling<Session> sessionSubscriptionHandling = parseLiveQueryClient.subscribe(session);
        sessionSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Session>() {
            @Override
            public void onEvent(ParseQuery<Session> query, Session object) {
                Log.d("SpeakerPlayingActivity", "created session subscription");
            }
        });
        //do need to keep both update and delete
        sessionSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Session>() {
            @Override
            public void onEvent(ParseQuery<Session> query, Session object) {
                if(object.isConnected() == false) {
                    pauseMPs(0);
                    if (numControllers > 1) {
                        pauseMPs(1);
                    }
                    releaseAll();
                    nullAll();
                    joining = false;
                }
            }
        });

        sessionSubscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<Session>() {
            @Override
            public void onEvent(ParseQuery<Session> query, Session object) {
                pauseMPs(0);
                if (numControllers > 1) {
                    pauseMPs(1);
                }
                releaseAll();
                nullAll();

                if(joining) {
                    joining = false;
                    recreateHandler.post(recreateRunnable);
                }
            }
        });

        //subscription handling for playpause
        SubscriptionHandling<PlayPause> playPauseSubscriptionHandling = parseLiveQueryClient.subscribe(playPause);
        playPauseSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<PlayPause>() {

            @Override
            public void onEvent(ParseQuery<PlayPause> query, PlayPause object) {
                Log.d("SpeakerPlayingActivity", "created play pause subscription");
                if (object.getControllerNumber() == 0) {
                    isPlaying0 = object.getPlaying();
                    if (!isPlaying0) {
                        Log.d("SpeakerPlayingActivity", "first controller just told phones to pause for the first time");
                        pauseMPs(0);
                    } else {
                        Log.d("SpeakerPlayingActivity", "first controller just told phones to play for the first time");
                        playMPs(0);
                    }
                } else {
                    isPlaying1 = object.getPlaying();
                    if (!isPlaying1) {
                        Log.d("SpeakerPlayingActivity", "second controller just told phones to pause for the first time");
                        pauseMPs(1);
                    } else {
                        Log.d("SpeakerPlayingActivity", "second controller just told phones to play for the first time");
                        playMPs(1);
                    }
                }
            }
        });
        playPauseSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<PlayPause>() {

            @Override
            public void onEvent(ParseQuery<PlayPause> query, PlayPause object) {
                if (object.getControllerNumber() == 0) {
                    if (isPlaying0 != object.getPlaying()) {
                        isPlaying0 = object.getPlaying();
                        if (!isPlaying0) {
                            Log.d("SpeakerPlayingActivity", "controller 1 told phones to pause");
                            pauseMPs(0);
                        } else {
                            Log.d("SpeakerPlayingActivity", "controller 1 told phones to play");
                            playMPs(0);
                        }
                    }
                } else {
                    if (isPlaying1 != object.getPlaying()) {
                        isPlaying1 = object.getPlaying();
                        if (!isPlaying1) {
                            Log.d("SpeakerPlayingActivity", "controller 2 told phones to pause");
                            pauseMPs(1);
                        } else {
                            Log.d("SpeakerPlayingActivity", "controller 2 told phones to play");
                            playMPs(1);
                        }
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

                phoneVolPercentage = object.getVolume();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*phoneVolPercentage), 0);
            }
        });

        volumeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Volume>() {
            @Override
            public void onEvent(ParseQuery<Volume> query, Volume object) {
                if (phoneVolPercentage != object.getVolume() ) {
                    Log.d("SpeakerPlayingActivity", "changing volume");
                    phoneVolPercentage = object.getVolume();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*phoneVolPercentage), 0);
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

                if (object.getControllerNumber() == 0) {
                    if (!prepared0 && !allMPs.isEmpty()) {
                        prepMediaPlayers(allAudioTracks.get(0), 0);
                    }
                } else {
                    if (!prepared1 && !allMPs.isEmpty()) {
                        prepMediaPlayers(allAudioTracks.get(1), 1);
                    }
                }
                if (object.getControllerNumber() == 0) {
                    // if first controller starting session
                    changeTime(object.getTime(), 0);
                }
                else {
                    changeTime(object.getTime(), 1);
                }  //if speaker initially joins late then have it match up with the others and the controller
            }
        });

        timeSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Time>() {
            @Override
            public void onEvent(ParseQuery<Time> query, Time object) {
                //if the time of the speaker is too different from the time of the controller
                //can continue to find "sweet spot" but somewhere between 100 and 500... 300 seems great
                if (object.getControllerNumber() == 0) {
                    if (!prepared0 && !allMPs.isEmpty()) {
                        prepMediaPlayers(allAudioTracks.get(0), 0);
                    }
                } else {
                    if (!prepared1 && !allMPs.isEmpty()) {
                        prepMediaPlayers(allAudioTracks.get(1), 1);
                    }
                }

                //if the controller app crashed
                //the playback time is the same, then pause all the speaker media players
//                if (object.getControllerNumber() == 0 && isPlaying0 && savedTime == object.getTime()) {
//                    pauseMPs(0);
//                    return;
//                }
//                else if (object.getControllerNumber() == 1 && isPlaying0 && savedTime == object.getTime()) {
//                    pauseMPs(1);
//                    return;
//                }


                if (object.getControllerNumber() == 0) {
                    // if first controller starting session
                    if (!allMPs.isEmpty() && allMPs.get(0).getCurrentPosition() > object.getTime() + 200) {
                        Log.d("SpeakerPlayingActivity", "times are off so calling changeTime");
                        changeTime(object.getTime(), 0);
                    } else if (!allMPs.isEmpty() && allMPs.get(0).getCurrentPosition() < object.getTime() - 200) {
                        changeTime(object.getTime() + 100, 0);
                    }
                } else {
                    // if first controller starting session
                    if (allMPs.get(1).getCurrentPosition() > object.getTime() + 200) {
                        Log.d("SpeakerPlayingActivity", "times are off so calling changeTime");
                        changeTime(object.getTime(), 1);
                    } else if (allMPs.get(5).getCurrentPosition() < object.getTime() - 200) {
                        changeTime(object.getTime() + 100, 1);
                    }

                }
                savedTime = object.getTime();
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
                        if (object.getControllerNumber() == 0) {
                            allMPs.get(0).setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
                            for (int i = 1; i < 5; i++) {
                                allMPs.get(i).setVolume(0, 0);
                            }
                        }
                        else if (object.getControllerNumber() == 1) {
                            allMPs.get(5).setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
                            for (int i = 6; i < 10; i++) {
                                allMPs.get(i).setVolume(0, 0);
                            }
                        }

                    } else {
                        Log.e("THROWING", "Returned");
                        if (object.getControllerNumber() == 0) {
                            for (int i = 0; i < 5; i++) {
                                setToMaxVol(allMPs.get(i), i);
                            }
                        }
                        else if (object.getControllerNumber() == 1) {
                            for (int i = 5; i < 10; i++) {
                                setToMaxVol(allMPs.get(i), i - 5);
                            }
                        }
                    }

                }
                if (movingNode != object.getLocation()){

                    movingNode = object.getLocation();
                    Log.d("SpeakerPlayingActivity", "movingnode != object.getmovingnode");
                    Log.d("SpeakerPlayingActivity", "setting volume to " + getMaxVol(movingNode) + " , " + getMaxVol(movingNode));
                    if (object.getControllerNumber() == 0) {
                        allMPs.get(0).setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
                        for (int i = 1; i < 5; i++) {
                            allMPs.get(i).setVolume(0, 0);
                        }
                    }
                    else if (object.getControllerNumber() == 1) {
                        allMPs.get(5).setVolume(getMaxVol(movingNode), getMaxVol(movingNode));
                        for (int i = 6; i < 10; i++) {
                            allMPs.get(i).setVolume(0, 0);
                        }
                    }

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
    }

    @Override
    protected void onRestart() {
        Log.d("test", "recreated");
        super.onRestart();
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

    public void disconnect() {
        Log.d("speakerplaying", "disconnectfunction");
//        if(frontRightMP != null && backRightMP != null && frontLeftMP != null && backLeftMP != null && centerMP != null) {
//            pauseAll();
//        }

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
        pauseMPs(0);
        pauseMPs(1);
        reconnected = false;
        userInitiatedDisconnect = true;
    }

    public void reconnect(View view) {
        Log.d("SpeakerPlaying", "in reconnect function");
        parseLiveQueryClient.reconnect();
        parseLiveQueryClient.connectIfNeeded();
        defaultContainer.setVisibility(View.VISIBLE);
        lostConnection.setVisibility(View.INVISIBLE);
        reconnected = true;

        if(isPlaying0) {
            playMPs(0);
        } else if (isPlaying1) {
            playMPs(1);
        }
    }

    //runnable to restart the activity
    private Runnable recreateRunnable = new Runnable() {
        @Override
        public void run() {
//            recreate();
            Intent restartIntent = new Intent(SpeakerPlayingActivity.this, SelectZone.class);
            restartIntent.putExtra("source", "SpeakerPlaying");
            startActivity(restartIntent);
            finish();
        }
    };

    //Background Runnable thread
    private Runnable reconnectViews = new Runnable() {
        public void run() {
            defaultContainer.setVisibility(View.VISIBLE);
            lostConnection.setVisibility(View.INVISIBLE);
        }
    };

    //TODO- should these be synchronized?
    //Create mediaplayers based on given songIds
    private void prepMediaPlayers(AudioIDs audioIDs, int controller){

        centerID = audioIDs.getIDs().get(0);
        frontLeftID = audioIDs.getIDs().get(1);
        frontRightID = audioIDs.getIDs().get(2);
        backLeftID = audioIDs.getIDs().get(3);
        backRightID = audioIDs.getIDs().get(4);

        MediaPlayer centerMP = MediaPlayer.create(SpeakerPlayingActivity.this, centerID);
        MediaPlayer frontLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontLeftID);
        MediaPlayer frontRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, frontRightID);
        MediaPlayer backLeftMP = MediaPlayer.create(SpeakerPlayingActivity.this, backLeftID);
        MediaPlayer backRightMP = MediaPlayer.create(SpeakerPlayingActivity.this, backRightID);
        allMPs.add(centerMP);
        allMPs.add(frontLeftMP);
        allMPs.add(frontRightMP);
        allMPs.add(backLeftMP);
        allMPs.add(backRightMP);
        Log.d("SpeakerPlayingActivity", "finished setting up media players");

        setToMaxVol(centerMP, 0);
        setToMaxVol(frontRightMP, 1);
        setToMaxVol(backRightMP, 2);
        setToMaxVol(backLeftMP, 3);
        setToMaxVol(frontLeftMP, 4);
        if(isPlaying0 && controller == 0) {
            Log.d("SpeakerPlayingActivity", "isPlaying for controller 1, so playing media players within prep");
            prepared0 = true;
            playMPs(controller);
        } else if (isPlaying1 && controller == 1) {
            Log.d("SpeakerPlayingActivity", "isPlaying for controller 2, so playing media players within prep");
            prepared1 = true;
            playMPs(controller);
        }
    }

    //pause All 5 mediaplayers
    synchronized private void pauseMPs(int controller){
        // pause the media players for specified controller
        if (controller == 0) {
            // pause mps for first controller
            for (int i = 0; i < 5; i++) {
                allMPs.get(i).pause();
            }

        } else if (numControllers > 1){
            // pause mps for second controller
            for (int i = 5; i < 10; i++) {
                allMPs.get(i).pause();
            }
        }
    }

    //play all 5 mediaplayers
    synchronized private void playMPs(int controller){
        // pause the media players for specified controller
        if (controller == 0) {
            // pause mps for first controller
            for (int i = 0; i < 5; i++) {
                if (!allMPs.isEmpty()) {
                    Log.d("SpeakerPlayingActivity", "playing media player " + i);
                    allMPs.get(i).start();
                }
            }

        } else if (numControllers > 1){
            // pause mps for second controller
            Log.d("SpeakerPlayingActivity", "more than one controller");
            Log.d("SpeakerPlayingActivity", String.format("allMPs size: " + allMPs.size()));
            for (int i = 5; i < 10; i++) {
                if (allMPs.size() > 5) {
                    allMPs.get(i).start();
                }
            }
        }
    }

    //TODO - CREATED
    private void releaseAll(){
        for (int i = 0; i < allMPs.size(); i++) {
            if (!allMPs.isEmpty()) {
                allMPs.get(i).release();
            }
        }
    }

    //TODO - CREATED
    private void nullAll(){
        MediaPlayer m;
        for (int i = 0; i < allMPs.size(); i++) {
            if (!allMPs.isEmpty()) {
                m = allMPs.get(i);
                m = null;
            }

        }
    }

    //change time of media players with specified controller
    synchronized private void changeTime(int time, int controller){
        if (controller == 0) {
            Log.d("SpeakerPlayingActivity", "changing time for controller 1");
            for (int i = 0; i < 5; i++) {
                if (!allMPs.isEmpty()) {
                    Log.d("SpeakerPlayingActivity", "changing time of media player " + i + "to " + time);
                    allMPs.get(i).seekTo(time);
                }
            }

        } else if (numControllers > 1){
            // pause mps for second controller
            for (int i = 5; i < 10; i++) {
                if (!allMPs.isEmpty()) {
                    allMPs.get(i).seekTo(time);
                }
            }
        }
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

    private void setToMaxVol(MediaPlayer mp, int num){
        double node = 0.5;
        if ( num == 0){
            node = 0.5;
        } else if (num == 1){
            node = 0.625;
        } else if (num == 2){
            node = 0.875;
        } else if (num == 3){
            node = 0.125;
        } else if (num == 4){
            node = 0.375;
        }

        mp.setVolume(getMaxVol(node), getMaxVol(node));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(allMPs != null) {
            releaseAll();
            nullAll();
        }
    }

//    @Override
//    protected void onStop() {
//        Log.d("test", "ondtop");
//        super.onStop();
//        if(frontRightMP != null && backRightMP != null && frontLeftMP != null && backLeftMP != null && centerMP != null) {
//            frontLeftMP.release();
//            frontRightMP.release();
//            backLeftMP.release();
//            backRightMP.release();
//            centerMP.release();
//
//            frontLeftMP = null;
//            frontRightMP = null;
//            backLeftMP = null;
//            backRightMP = null;
//            centerMP = null;
//        }
//
//    }

}