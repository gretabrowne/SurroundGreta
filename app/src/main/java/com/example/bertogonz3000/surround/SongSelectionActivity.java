package com.example.bertogonz3000.surround;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.bertogonz3000.surround.Models.Track;

import java.io.File;
import java.util.ArrayList;

public class SongSelectionActivity extends AppCompatActivity {

    ArrayList<Track> tracklist;
    TrackAdapter adapter;

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
        adapter = new TrackAdapter(tracklist);

        //attach the adapter to the RV
        rvTracks.setAdapter(adapter);

        //Set layout manager
        rvTracks.setLayoutManager(new LinearLayoutManager(this));

        createTracks();
    }

    //This method should only be called in onCreate
    public void createTracks(){
        ArrayList<Integer> heyJudeList = new ArrayList<Integer>();

        heyJudeList.add(R.raw.heyjude);

        Track heyJude = new Track("Hey Jude", heyJudeList);

        tracklist.add(heyJude);

        adapter.notifyItemInserted(0);
    }


}
