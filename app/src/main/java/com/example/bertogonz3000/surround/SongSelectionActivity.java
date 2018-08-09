package com.example.bertogonz3000.surround;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.bertogonz3000.surround.Models.Track;

import java.util.ArrayList;

public class SongSelectionActivity extends AppCompatActivity {

    ArrayList<Track> tracklist;
    TrackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_selection);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Song Selection");

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
//        ArrayList<Integer> heyJudeList = new ArrayList<Integer>();
//
//        heyJudeList.add(R.raw.heyjude);
//
//        heyJudeList.add(R.raw.heyjudeleft);
//
//        heyJudeList.add(R.raw.heyjuderight);
//
//        heyJudeList.add(R.raw.heyjudeleft);
//
//        heyJudeList.add(R.raw.heyjuderight);
//
//        Track heyJude = new Track("Hey Jude", "The Beatles", heyJudeList);
//        heyJude.setDrawable(R.drawable.heyjude);
//
//        tracklist.add(heyJude);
//
//        adapter.notifyItemInserted(0);
//
        ArrayList<Integer> yesterdayList = new ArrayList<Integer>();

        yesterdayList.add(R.raw.yesterdayfull);

        yesterdayList.add(R.raw.yesterdayleft);

        yesterdayList.add(R.raw.yesterdayright);

        yesterdayList.add(R.raw.yesterdayleft);

        yesterdayList.add(R.raw.yesterdayright);

        Track yesterday = new Track("Yesterday", "The Beatles", yesterdayList);
        yesterday.setDrawable(R.drawable.yesterday);

        tracklist.add(yesterday);

        adapter.notifyItemInserted(0);
//
//        ArrayList<Integer> testList = new ArrayList<Integer>();
//
//        testList.add(R.raw.frontcentertest);
//
//        testList.add(R.raw.frontlefttest);
//
//        testList.add(R.raw.frontrighttest);
//
//        testList.add(R.raw.leftsurroundtest);
//
//        testList.add(R.raw.rightsurroundtest);
//
//        Track testTrack = new Track("Test Track", "Dolby Atmos", testList);
//        testTrack.setDrawable(R.drawable.dolby);
//
//        tracklist.add(testTrack);
//
//        adapter.notifyItemInserted(2);

        ArrayList<Integer> heliList = new ArrayList<Integer>();

        heliList.add(R.raw.helicopter);
        heliList.add(R.raw.helicopter);
        heliList.add(R.raw.helicopter);
        heliList.add(R.raw.helicopter);
        heliList.add(R.raw.helicopter);

        Track heliTrack = new Track("Helicopter", "Single Sound", heliList);
        heliTrack.setDrawable(R.drawable.helicopter);

        tracklist.add(heliTrack);

        adapter.notifyItemInserted(1);

        ArrayList<Integer> starwarsList = new ArrayList<Integer>();

        starwarsList.add(R.raw.starwarsfull);
        starwarsList.add(R.raw.stawarsleft);
        starwarsList.add(R.raw.starwarsright);
        starwarsList.add(R.raw.stawarsleft);
        starwarsList.add(R.raw.starwarsright);

        Track starwarsTrack = new Track("Star Wars: the Force Awakens", "Lucasfilm", starwarsList);
        starwarsTrack.setDrawable(R.drawable.starwars7);

        tracklist.add(starwarsTrack);

        adapter.notifyItemInserted(2);

        ArrayList<Integer> houseList = new ArrayList<Integer>();

        houseList.add(R.raw.housefull);
        houseList.add(R.raw.houseleft);
        houseList.add(R.raw.houseright);
        houseList.add(R.raw.houseleft);
        houseList.add(R.raw.houseright);


        Track houseTrack = new Track("House of the Flying Daggers", "Edko Films", houseList);
        houseTrack.setDrawable(R.drawable.house);

        tracklist.add(houseTrack);

        adapter.notifyItemInserted(3);
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

}
