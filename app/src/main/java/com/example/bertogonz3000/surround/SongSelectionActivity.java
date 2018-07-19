package com.example.bertogonz3000.surround;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.bertogonz3000.surround.Models.Track;

import java.util.ArrayList;

public class SongSelectionActivity extends AppCompatActivity {

    ArrayList<Track> tracklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_selection);

        //Find RV
        RecyclerView rvTracks = findViewById(R.id.rvTracks);

        //init tracks
        tracklist = new ArrayList<Track>();

        //Todo - Populate the trackList

        //Create an Adapter
        TrackAdapter adapter = new TrackAdapter(tracklist);

        //attach the adapter to the RV
        rvTracks.setAdapter(adapter);

        //Set layout manager
        rvTracks.setLayoutManager(new LinearLayoutManager(this));
    }


}
