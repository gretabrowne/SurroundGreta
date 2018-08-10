package com.example.bertogonz3000.surround.ParseModels;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("PlayPause")
public class PlayPause extends ParseObject {

    //Getters
    public String getPlayPauseID(){return getObjectId();}

    public boolean getPlaying(){return getBoolean("playing");}

    public int getControllerNumber () {return getInt("controllerNumber");}
    //Setters
    public void setPlaying(boolean playing){put("playing", playing);}
    public void setControllerNumber (int controllerNumber) {put("controllerNumber", controllerNumber);}
}