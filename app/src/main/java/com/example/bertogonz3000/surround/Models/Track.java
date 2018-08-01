package com.example.bertogonz3000.surround.Models;

import java.util.ArrayList;

public class Track {

    private String name;
    private String artist;
    private ArrayList<Integer> audio;
    private int drawable;

    public Track(String name, String artist, ArrayList<Integer> audioIds){
        this.name = name;
        this.artist = artist;
        this.audio = audioIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() { return artist; }

    public void setArtist(String artist) { this.artist = artist; }

    public ArrayList<Integer> getAudioIds() {
        return audio;
    }
    public int getDrawable() { return drawable; }

    public void setDrawable(int drawable) {this.drawable = drawable;}

    public void setAudioIds(ArrayList<Integer> audio) {
        this.audio = audio;
    }
}