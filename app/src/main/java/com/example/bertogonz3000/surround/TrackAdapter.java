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
import com.example.bertogonz3000.surround.ParseModels.AudioIDs;
import com.example.bertogonz3000.surround.ParseModels.PlayPause;
import com.example.bertogonz3000.surround.ParseModels.Session;
import com.example.bertogonz3000.surround.ParseModels.Throwing;
import com.example.bertogonz3000.surround.ParseModels.Time;
import com.example.bertogonz3000.surround.ParseModels.Volume;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcel;
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTrackName;
        public TextView tvArtist;
        public ImageView ivCover;
        private final Context context = itemView.getContext();

        public ViewHolder(View itemView) {
            super(itemView);

            tvTrackName = itemView.findViewById(R.id.tvTrackName);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            ivCover = itemView.findViewById(R.id.ivCover);

            itemView.setOnClickListener(this);

        }

        //TODO - CHANGED
        public void onClick(View view) {
            final Track track = trackList.get(getAdapterPosition());
            ParseQuery<Session> query = ParseQuery.getQuery(Session.class);
            query.whereEqualTo("isConnected", true);
            query.setLimit(1);
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<Session>() {
                @Override
                public void done(List<Session> objects, ParseException e) {
                    if (objects != null && objects.isEmpty()) {
                        // if there are no other sessions, start a new one
                        //new server redesign
                        Time time = new Time();
                        time.setTime(0);
                        time.setControllerNumber(0);
                        try {
                            time.save();
                        } catch (ParseException g) {
                            g.printStackTrace();
                        }

                        AudioIDs audioIDs = new AudioIDs();
                        audioIDs.setIDs(track.getAudioIds());
                        audioIDs.setControllerNumber(0);
                        try {
                            audioIDs.save();
                            Log.d("TrackAdapter", "saved audio ID's");
                        } catch (ParseException h) {
                            h.printStackTrace();
                        }

                        PlayPause playPause = new PlayPause();
                        playPause.setPlaying(true);
                        playPause.setControllerNumber(0);
                        try {
                            playPause.save();
                        } catch (ParseException i) {
                            i.printStackTrace();
                        }

                        Throwing throwing = new Throwing();
                        throwing.setThrowing(true);
                        throwing.setControllerNumber(0);
                        try {
                            throwing.save();
                        } catch (ParseException j) {
                            j.printStackTrace();
                        }


                        Volume volume = new Volume();
                        volume.setControllerNumber(0);
                        try {
                            volume.save();
                        } catch (ParseException k) {
                            k.printStackTrace();
                        }

//            try {
//                audioIDs.save();
//                playPause.save();
//                throwing.save();
//                time.save();
//                volume.save();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
                        Session session = new Session();
                        Log.d("TrackAdapter", (String) playPause.getPlayPauseID());
                        session.setPlayPause(playPause);
                        session.setAudio(audioIDs);
                        session.setThrowing(throwing);
                        session.setTimeObject(time);
                        session.setVolume(volume);
                        session.setConnected(true);
                        session.setTrackName(track.getName());
//            Session session = new Session(playPause.getPlayPauseID(), time.getTimeID(), throwing.getThrowingID(), volume.getVolumeID(), audioIDs.getAudioID());
                        try {
                            session.save();
                        } catch (ParseException f) {
                            f.printStackTrace();
                        }


                        Intent intent = new Intent(context, ControllerPlayingActivity.class);
                        intent.putExtra("session", Parcels.wrap(session));
                        intent.putExtra("audioIDs", Parcels.wrap(audioIDs));
                        intent.putExtra("playPause", Parcels.wrap(playPause));
                        intent.putExtra("throwing", Parcels.wrap(throwing));
                        intent.putExtra("time", Parcels.wrap(time));
                        intent.putExtra("volume", Parcels.wrap(volume));
                        intent.putExtra("controllerNumber", 1);
                        intent.putExtra("track", Parcels.wrap(track));
                        context.startActivity(intent);

                    } else if (e == null) {
                        // a session already exists, but there's no error
                        // set separate audio ids (b/c separate track), playing and pausing, etc.
                        Time time = new Time();
                        time.setTime(0);
                        time.setControllerNumber(1);
                        try {
                            time.save();
                        } catch (ParseException g) {
                            g.printStackTrace();
                        }

                        AudioIDs audioIDs = new AudioIDs();
                        audioIDs.setIDs(track.getAudioIds());
                        audioIDs.setControllerNumber(1);
                        try {
                            audioIDs.save();
                            Log.d("TrackAdapter", "saved audio ID's");
                        } catch (ParseException h) {
                            h.printStackTrace();
                        }

                        PlayPause playPause = new PlayPause();
                        playPause.setPlaying(true);
                        playPause.setControllerNumber(1);
                        try {
                            playPause.save();
                        } catch (ParseException i) {
                            i.printStackTrace();
                        }

                        Throwing throwing = new Throwing();
                        throwing.setThrowing(true);
                        throwing.setControllerNumber(1);
                        try {
                            throwing.save();
                        } catch (ParseException j) {
                            j.printStackTrace();
                        }

                        Volume volume = new Volume();
                        volume.setControllerNumber(1);
                        try {
                            volume.save();
                        } catch (ParseException k) {
                            k.printStackTrace();
                        }

                        Session existingSession = objects.get(0);
                        existingSession.setPlayPause(playPause);
                        existingSession.setAudio(audioIDs);
                        existingSession.setThrowing(throwing);
                        existingSession.setTimeObject(time);
                        existingSession.setVolume(volume);
                        existingSession.setTrackName(track.getName());
                        existingSession.setConnected(true);

                        // pass over existing session
                        Intent i = new Intent(context, ControllerPlayingActivity.class);
                        i.putExtra("session", Parcels.wrap(existingSession));
                        i.putExtra("audioIDs", Parcels.wrap(audioIDs));
                        i.putExtra("playPause", Parcels.wrap(playPause));
                        i.putExtra("throwing", Parcels.wrap(throwing));
                        i.putExtra("time", Parcels.wrap(time));
                        i.putExtra("volume", Parcels.wrap(volume));
                        i.putExtra("track", Parcels.wrap(track));
                        // i.putExtra("controllerNumber", 2);
                        context.startActivity(i);
                    } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }


}
