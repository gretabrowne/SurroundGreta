package com.example.bertogonz3000.surround.ParseModels;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Session")
public class Session  extends ParseObject{

    //Session, playpause, throwing(contains location), volume, audioIDs, tracks, time

    //Getters
    public String getSessionID(){return getObjectId();}

    public String getPlayPauseID(){return getString("playPause");}

    public String getTimeID(){return getString("time");}

    public String getThrowingID(){return getString("throwing");}

    public String getVolumeID(){return getString("volume");}

    public String getAudioID(){return getString("audioIDs");}


    //Setters
    public void setPlayPauseID(String playPauseID){put("playPause", playPauseID);}

    public void setTimeID(String timeID){put("time", timeID);}

    public void setThrowingID(String throwingID){put("throwing", throwingID);}

    public void setVolumeID(String volumeID){put("volume", volumeID);}

    public void setAudioID(String audioIDs){put("audioIDs", audioIDs);}
}