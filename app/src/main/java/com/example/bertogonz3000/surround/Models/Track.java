package com.example.bertogonz3000.surround.Models;

import java.io.File;
import java.util.ArrayList;

public class Track {

    private String name;
    private ArrayList<Integer> audio;
    private int audioId;

    public Track(String name, int audioId, ArrayList<Integer> audio){
        this.name = name;
        this.audio = audio;
        this.audioId = audioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getAudio() {
        return audio;
    }

    public void setAudio(ArrayList<Integer> audio) {
        this.audio = audio;
    }

    public int getAudioId() {
        return audioId;
    }

    public void setAudioId(int audioId) {
        this.audioId = audioId;
    }
}