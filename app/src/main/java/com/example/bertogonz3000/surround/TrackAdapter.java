package com.example.bertogonz3000.surround;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bertogonz3000.surround.Models.Track;
import com.parse.ParseException;

import org.parceler.Parcels;

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

        holder.tvTrackName.setText(track.getName());
        holder.tvArtist.setText(track.getArtist());

        Context context = holder.itemView.getContext();
        Glide.with(context)
                .load(track.getDrawable())
                .into(holder.ivCover);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }




    //ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvTrackName;
        public TextView tvArtist;
        public ImageView ivCover;
        private final Context context = itemView.getContext();

        public ViewHolder(View itemView){
            super(itemView);

            tvTrackName = itemView.findViewById(R.id.tvTrackName);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            ivCover = itemView.findViewById(R.id.ivCover);

            itemView.setOnClickListener(this);

        }

        //TODO - CHANGED
        public void onClick(View view){
            Track track = trackList.get(getAdapterPosition());
            Song song = new Song();
            song.setAudioIds(track.getAudioIds());
            song.setIsPlaying(true);
            song.setIsThrowing(false);
            //song.setVolume(0);
            song.setTime(0);
            song.setNumSeek(0);
            try {
                song.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Intent i = new Intent(context, ControllerPlayingActivity.class);
            i.putExtra("song", Parcels.wrap(song));
            Log.d("TrackAdapter", "volume" + song.getVolume());
            context.startActivity(i);
        }

    }
}
