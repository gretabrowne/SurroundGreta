package com.example.bertogonz3000.surround.Models;

import java.io.File;
import java.util.ArrayList;

public class Track {

    private String name;
    private ArrayList<Integer> audio;

    public Track(String name, ArrayList<Integer> audioIds){
        this.name = name;
        this.audio = audioIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<Integer> getAudioIds() {
        return audio;
    }

    public void setAudioIds(ArrayList<Integer> audio) {
        this.audio = audio;
    }
}