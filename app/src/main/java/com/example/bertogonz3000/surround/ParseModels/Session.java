package com.example.bertogonz3000.surround.ParseModels;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Session")
public class Session  extends ParseObject{

    //Session, playpause, throwing(contains location), volume, audioIDs, tracks, time
//    public Session(String playPauseID, String timeID, String throwingID, String volumeID, String audioID) {
//        put("playPause", playPauseID);
//        put("time", timeID);
//        put("throwing", throwingID);
//        put("volume", volumeID);
//        put("audioIDs", audioID);
//    }

    //Getters

    public AudioIDs getAudioIDs() {
        return (AudioIDs) get("audioIDs");
    }
    public PlayPause getPlayPause() {
        return (PlayPause) get("playPause");
    }
    public Volume getVolume() {
        return (Volume) get("volume");
    }
    public Time getTimeObject() {
        return (Time) getParseObject("time");
    }
    public Throwing getThrowingObject() {
        return (Throwing) get("throwing");
    }
    public boolean isConnected() {
        return (Boolean) get("isConnected");
    }

    //Setters
    public void setPlayPause(PlayPause playPause){put("playPause", playPause);}

    public void setTimeObject(Time time){put("time", time);}

    public void setThrowing(Throwing throwing){put("throwing", throwing);}

    public void setVolume(Volume volume){put("volume", volume);}

    public void setAudio(AudioIDs audio){put("audioIDs", audio);}

    public void setConnected(Boolean bool) {put("isConnected", bool); }
}