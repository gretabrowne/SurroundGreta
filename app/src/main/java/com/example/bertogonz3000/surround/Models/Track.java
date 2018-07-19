package com.example.bertogonz3000.surround.Models;

import java.io.File;
import java.util.ArrayList;

public class Track {

    private String name;
    private ArrayList<File> audio;

    public Track(String name, ArrayList<File> audio){
        this.name = name;
        this.audio = audio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<File> getAudio() {
        return audio;
    }

    public void setAudio(ArrayList<File> audio) {
        this.audio = audio;
    }
}
