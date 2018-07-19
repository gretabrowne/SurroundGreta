package com.example.bertogonz3000.surround;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bertogonz3000.surround.Models.Track;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder>{

    private List<Track> trackList;

    public TrackAdapter(List<Track> trackList){
        this.trackList = trackList;
    }

    //inflate the XML layout and return the holder
    @NonNull
    @Override
    public TrackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View trackView = inflater.inflate(R.layout.item_track, parent, false);

        ViewHolder holder = new ViewHolder(trackView);
        return holder;
    }

    //Populate data
    @Override
    public void onBindViewHolder(@NonNull TrackAdapter.ViewHolder holder, int position) {
        //Get the track
        Track track = trackList.get(position);

        //Set views based on model

        holder.trackButton.setText(track.getName());
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }




    //ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public Button trackButton;
        private final Context context = itemView.getContext();

        public ViewHolder(View itemView){
            super(itemView);

            trackButton = itemView.findViewById(R.id.trackButton);

            trackButton.setOnClickListener(this);

        }

        public void onClick(View view){
            //Todo - create some server logic that tells other phones what to play and when
            Intent i = new Intent(context, ControllerPlayingActivity.class);
            context.startActivity(i);
        }

    }
}
